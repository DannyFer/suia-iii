package ec.gob.ambiente.rcoa.facade;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.dto.EntidadPma;
import ec.gob.ambiente.rcoa.model.AspectoAmbientalPma;
import ec.gob.ambiente.rcoa.model.MedidaVerificacionPma;
import ec.gob.ambiente.rcoa.model.PlanManejoAmbientalPma;
import ec.gob.ambiente.rcoa.model.PmaAceptadoRegistroAmbiental;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.RegistroAmbientalRcoa;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class PlanManejoAmbientalCoaFacade {
	
	@EJB
    private CrudServiceBean crudServiceBean;
	
	public PmaAceptadoRegistroAmbiental guardar(PmaAceptadoRegistroAmbiental pmaRegistroAmbiental) {        	
    	return crudServiceBean.saveOrUpdate(pmaRegistroAmbiental);        
	}

	public PlanManejoAmbientalPma obtenerPlanPorId(Integer planId) {
		PlanManejoAmbientalPma obj = new PlanManejoAmbientalPma();
		Query sql =crudServiceBean.getEntityManager().createQuery("select p from PlanManejoAmbientalPma p where p.id=:planId and p.estado=true");
		sql.setParameter("planId", planId);
		if(sql.getResultList().size()>0)
			obj=(PlanManejoAmbientalPma) sql.getResultList().get(0);
		return obj;     
	}
	
	public void eliminarPlanesAgregados(String listaId, Integer registroAmbientalId){	
		try{
		String sql =" update coa_environmental_record.accept_management_plan  set acmp_status = false, acmp_date_update=now(), acmp_user_update = acmp_creator_user "
				+ " where enmp_id in ("+listaId+") and enre_id = "+registroAmbientalId+" ;";
		crudServiceBean.insertUpdateByNativeQuery(sql.toString(), null);
		}catch(Exception e){
			System.out.println("Error al eliminar los planes");;
		}
	}
	
	public void eliminarPlanesAgregadosNuevos(String listaId, Integer registroAmbientalId){	
		try{
		String sql =" update coa_environmental_record.accept_management_plan  set acmp_status = false, acmp_date_update=now(), acmp_user_update = acmp_creator_user "
				+ " where acmp_id in ("+listaId+") and enre_id = "+registroAmbientalId+" ;";
		crudServiceBean.insertUpdateByNativeQuery(sql.toString(), null);
		}catch(Exception e){
			System.out.println("Error al eliminar los planes");;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<AspectoAmbientalPma> obtenerAspectoAmbientalPorPlan(Integer planId)
	{
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("planId", planId);
		
		List<AspectoAmbientalPma> lista = (List<AspectoAmbientalPma>) crudServiceBean
					.findByNamedQuery(
							AspectoAmbientalPma.GET_ASPECTO_POR_PLAN,
							parameters);
		if(lista.size() > 0 ){
			return lista;
		}
		return  new ArrayList<AspectoAmbientalPma>();
	}
	
	@SuppressWarnings("unchecked")
	public List<EntidadPma> obtenerPmaPorRegistroAmbiental(RegistroAmbientalRcoa registroAmbiental, Integer tipoPlan, boolean todos )
	{
		List<EntidadPma> listaPMA = new ArrayList<EntidadPma>();
		List<PlanManejoAmbientalPma> listaPma;
		if(tipoPlan == 0){
			listaPma = crudServiceBean
					.getEntityManager()
					.createQuery(
							" SELECT m FROM PlanManejoAmbientalPma m "
							+ "where m.estado = true and m.id in ("
							+ " select distinct p.medidaVerificacionPma.aspectoAmbientalPma.planManejoAmbientalPma.id from PmaAceptadoRegistroAmbiental p where p.estado = true  and p.registroAmbientalId = :registroId  "	
							+ ")"
							+ "  order by m.orden "
							)
					.setParameter("registroId", registroAmbiental.getId())
					.getResultList();
		}else{
			listaPma = crudServiceBean
					.getEntityManager()
					.createQuery(
							" SELECT m FROM PlanManejoAmbientalPma m "
							+ "where m.estado = true and m.id in ("
							+ " select distinct p.medidaVerificacionPma.aspectoAmbientalPma.planManejoAmbientalPma.id from PmaAceptadoRegistroAmbiental p "
							+ "where p.estado = true  and p.registroAmbientalId = :registroId and p.medidaVerificacionPma.aspectoAmbientalPma.planManejoAmbientalPma.faseTipoPlan.id = :tipoPlanId "	
							+ ")"
							+ "  order by m.orden "
							)
					.setParameter("registroId", registroAmbiental.getId())
					.setParameter("tipoPlanId", tipoPlan)
					.getResultList();
		}
		for (PlanManejoAmbientalPma objPlanManejoAmbientalCoa : listaPma) {
			List<PmaAceptadoRegistroAmbiental> listaMedidas = buscarMedidaaAmbientalesPMAPorCiuPorRegistroAmbiental(objPlanManejoAmbientalCoa.getId(), registroAmbiental.getId(), todos);
			if(listaMedidas != null && listaMedidas.size() > 0){
				// inicializo los valores como guardados
				EntidadPma entidadPlanPma = new EntidadPma();
				entidadPlanPma.setPlanNombre(objPlanManejoAmbientalCoa.getDescripcion());
				entidadPlanPma.setPlanId(objPlanManejoAmbientalCoa.getId());
				entidadPlanPma.setMedidasProyecto(listaMedidas);
				listaPMA.add(entidadPlanPma);
				
			}
		}
		return  listaPMA;
	}

	public List<PmaAceptadoRegistroAmbiental> buscarMedidaaAmbientalesPMAPorCiuPorRegistroAmbiental(Integer planPadreId, Integer registroId, boolean todos ) {
		try {
			List<PmaAceptadoRegistroAmbiental> planManejoAmbiental = crudServiceBean
					.getEntityManager()
					.createQuery(
							" SELECT m FROM PmaAceptadoRegistroAmbiental m"
							+ " where m.estado = true "+(todos ? "": "and m.aceptado=true")
							+ " and m.aspectoAmbientalPma.id is null "
							+ " and m.medidaVerificacionPma.aspectoAmbientalPma.planManejoAmbientalPma.id  = :planId  "
							+ " and m.registroAmbientalId = :registroId "
							+ " order by m.medidaVerificacionPma.orden ")
					.setParameter("registroId", registroId)
					.setParameter("planId", planPadreId).getResultList();
			
			List<PmaAceptadoRegistroAmbiental> planManejoAmbiental1 = crudServiceBean
					.getEntityManager()
					.createQuery(
							" SELECT m FROM PmaAceptadoRegistroAmbiental m"
							+ " where m.estado = true and m.aceptado=true and m.medidaVerificacionPma.id is null and "
							+ " m.aspectoAmbientalPma.planManejoAmbientalPma.id  = :planId  "
							+ " and m.registroAmbientalId = :registroId ")
					.setParameter("registroId", registroId)
					.setParameter("planId", planPadreId).getResultList();
			if(planManejoAmbiental1 != null && planManejoAmbiental1.size() > 0){
				planManejoAmbiental.addAll(planManejoAmbiental1);
			}
			
			return planManejoAmbiental;
		} catch (Exception e) {
			return null;
			// TODO: handle exception
		}
	}

	public List<PmaAceptadoRegistroAmbiental> buscarMedidaaAmbientalesIngresadasPorRegistroAmbiental(Integer planPadreId, Integer registroId ) {
		try {
			List<PmaAceptadoRegistroAmbiental> planManejoAmbiental = crudServiceBean
					.getEntityManager()
					.createQuery(
							" SELECT m FROM PmaAceptadoRegistroAmbiental m"
							+ " where m.estado = true and m.aceptado=true and m.medidaVerificacionPma.id is null and "
							+ " m.aspectoAmbientalPma.planManejoAmbientalPma.id  = :planId  "
							+ " and m.registroAmbientalId = :registroId order by m.id ")
					.setParameter("registroId", registroId)
					.setParameter("planId", planPadreId).getResultList();
		
			return planManejoAmbiental;
		} catch (Exception e) {
			return null;
			// TODO: handle exception
		}
	}

	@SuppressWarnings("unchecked")
	public List<EntidadPma> obtenerPmaActividad(String codActividad, Integer tipoPlan, boolean paraAdicionar, Integer registroId, ProyectoLicenciaCoa proyectoLicenciaCoa, Integer idConsideracion)
	{
		List<EntidadPma> listaPMA = new ArrayList<EntidadPma>();
		Integer catalogoId, nivelActividad=5;
		boolean existeDatos = false;

		Query sql=crudServiceBean.getEntityManager().createNativeQuery("WITH RECURSIVE menu1( caci_id, caci_code, caci_name, caci_status, caci_level, caci_environmental_authority  ) AS ("
				+ "	 SELECT * FROM coa_mae.catalog_ciuu where caci_status = true and caci_code = '"+codActividad+"'"
				+ "	 UNION"
				+ " SELECT 	m.* "
				+ " FROM coa_mae.catalog_ciuu m, menu1 as b "
				+ "	WHERE m.caci_status = true and b.caci_code like  m.caci_code|| '%'"
				+ " and  case when b.caci_pma is true then false else true end "
				+ "  ) "
				+ "	 SELECT caci_id, caci_level, caci_pma FROM menu1"
				+ "  order by caci_level desc; ; ");
		List<Object> result = (List<Object>) sql.getResultList();
				
		boolean esPmaActividad=false; 
		
		for (Object object : result) {
			Object[] obj=(Object[])object;
			//catalogoId =  (((Integer) object).intValue());
			catalogoId =  (((Integer) obj[0]).intValue());
			nivelActividad=  (((Integer) obj[1]).intValue());
			esPmaActividad = Boolean.valueOf((boolean) obj[2]);
			List<MedidaVerificacionPma> listaMedidas2 = buscarMedidaaAmbientalesPMAPorCiu(catalogoId, tipoPlan, paraAdicionar, true, proyectoLicenciaCoa, idConsideracion);
			//List<MedidaVerificacionPma> listaMedidas1 = buscarMedidaaAmbientalesPMAPorCiu(catalogoId, tipoPlan, paraAdicionar, false, proyectoLicenciaCoa );
			// si es pma exclusivo para la actividad pero no tiene pma registrado para la consideracion busco si tiene pma a nivel 5 de la actividad y el pma generico para todos
			if(esPmaActividad && listaMedidas2.size() == 0){
				esPmaActividad=false;//para buscar pma generico
				Query sqlNivel5 =crudServiceBean.getEntityManager().createNativeQuery("WITH RECURSIVE menu1( caci_id, caci_code, caci_name, caci_status, caci_level, caci_environmental_authority  ) AS ("
						+ "	 SELECT * FROM coa_mae.catalog_ciuu where caci_status = true and caci_code = '"+codActividad+"'"
						+ "	 UNION"
						+ " SELECT 	m.* "
						+ " FROM coa_mae.catalog_ciuu m, menu1 as b "
						+ "	WHERE m.caci_status = true and b.caci_code like  m.caci_code|| '%'"
						+ "  ) "
						+ "	 SELECT caci_id, caci_level, caci_pma FROM menu1 "
						+ " where caci_level = 5 "
						+ "  order by caci_level desc; ; ");
				List<Object> resultNivel5 = (List<Object>) sqlNivel5.getResultList();
				for (Object objectNivel5 : resultNivel5) {
					Object[] objNivel5=(Object[])objectNivel5;
					catalogoId =  (((Integer) objNivel5[0]).intValue());
				}
				idConsideracion=0;
				listaMedidas2 = buscarMedidaaAmbientalesPMAPorCiu(catalogoId, tipoPlan, paraAdicionar, true, proyectoLicenciaCoa, idConsideracion);
			}
			
			List<MedidaVerificacionPma> listaMedidas = new ArrayList<MedidaVerificacionPma>();
			if(listaMedidas2 != null && listaMedidas2.size() > 0){
				listaMedidas.addAll(listaMedidas2);
			}
	
			if(listaMedidas != null && listaMedidas.size() > 0){
				String plan = "";
				Integer planId = 0;
				EntidadPma entidadPlanPma = new EntidadPma();
				List<MedidaVerificacionPma> entidadMedida = new ArrayList<MedidaVerificacionPma>();
				List<PmaAceptadoRegistroAmbiental> entidadMedidaProyecto = new ArrayList<PmaAceptadoRegistroAmbiental>();
				for (MedidaVerificacionPma objMedidads : listaMedidas) {
					if(objMedidads.isTipoEstandar()){
						existeDatos = true;
					}
					if(!plan.equals(objMedidads.getAspectoAmbientalPma().getPlanManejoAmbientalPma().getDescripcion())){
						if(!plan.isEmpty()){
							entidadPlanPma.setPlanNombre(plan);
							entidadPlanPma.setPlanId(planId);
							entidadPlanPma.setMedidas(entidadMedida);
							entidadPlanPma.setMedidasProyecto(entidadMedidaProyecto);
							listaPMA.add(entidadPlanPma);
							entidadPlanPma = new EntidadPma();
							entidadMedida = new ArrayList<MedidaVerificacionPma>();
							entidadMedidaProyecto = new ArrayList<PmaAceptadoRegistroAmbiental>();
							plan = objMedidads.getAspectoAmbientalPma().getPlanManejoAmbientalPma().getDescripcion();
							planId = objMedidads.getAspectoAmbientalPma().getPlanManejoAmbientalPma().getId();
							entidadMedida.add(objMedidads);
							PmaAceptadoRegistroAmbiental objPmaAceptado =crearPlan(objMedidads, registroId);
							if(objPmaAceptado != null){
								entidadMedidaProyecto.add(objPmaAceptado);
							}
						}else{
							plan = objMedidads.getAspectoAmbientalPma().getPlanManejoAmbientalPma().getDescripcion();
							planId = objMedidads.getAspectoAmbientalPma().getPlanManejoAmbientalPma().getId();
							entidadMedida.add(objMedidads);
							PmaAceptadoRegistroAmbiental objPmaAceptado =crearPlan(objMedidads, registroId);
							if(objPmaAceptado != null){
								entidadMedidaProyecto.add(objPmaAceptado);
							}
						}
					}else{
						entidadMedida.add(objMedidads);
						PmaAceptadoRegistroAmbiental objPmaAceptado =crearPlan(objMedidads, registroId);
						if(objPmaAceptado != null){
							entidadMedidaProyecto.add(objPmaAceptado);
						}
					}
				}
				entidadPlanPma.setPlanNombre(plan);
				entidadPlanPma.setPlanId(planId);
				entidadPlanPma.setMedidas(entidadMedida);
				entidadPlanPma.setMedidasProyecto(entidadMedidaProyecto);
				listaPMA.add(entidadPlanPma);
				if (existeDatos){
					break;
				}
			}
		}
		//si no es pma esclusivo de la actividad busco el resto del pma
		if(!esPmaActividad){
			for (Object object : result) {
				Object[] obj=(Object[])object;
				//catalogoId =  (((Integer) object).intValue());
				catalogoId =  (((Integer) obj[0]).intValue());
				nivelActividad=  (((Integer) obj[1]).intValue());
				idConsideracion=0;
				List<MedidaVerificacionPma> listaMedidas1 = buscarMedidaaAmbientalesPMAPorCiu(catalogoId, tipoPlan, paraAdicionar, false, proyectoLicenciaCoa, idConsideracion);
				
				List<MedidaVerificacionPma> listaMedidas = new ArrayList<MedidaVerificacionPma>();

				if(listaMedidas.size() == 0 && listaMedidas1 != null && listaMedidas1.size() > 0){
					listaMedidas.addAll(listaMedidas1);
				}
				if(listaMedidas != null && listaMedidas.size() > 0){
					String plan = "";
					Integer planId = 0;
					EntidadPma entidadPlanPma = new EntidadPma();
					List<MedidaVerificacionPma> entidadMedida = new ArrayList<MedidaVerificacionPma>();
					List<PmaAceptadoRegistroAmbiental> entidadMedidaProyecto = new ArrayList<PmaAceptadoRegistroAmbiental>();
					for (MedidaVerificacionPma objMedidads : listaMedidas) {
						if(objMedidads.isTipoEstandar()){
							existeDatos = true;
						}
						if(!plan.equals(objMedidads.getAspectoAmbientalPma().getPlanManejoAmbientalPma().getDescripcion())){
							if(!plan.isEmpty()){
								entidadPlanPma.setPlanNombre(plan);
								entidadPlanPma.setPlanId(planId);
								entidadPlanPma.setMedidas(entidadMedida);
								entidadPlanPma.setMedidasProyecto(entidadMedidaProyecto);
								listaPMA.add(entidadPlanPma);
								entidadPlanPma = new EntidadPma();
								entidadMedida = new ArrayList<MedidaVerificacionPma>();
								entidadMedidaProyecto = new ArrayList<PmaAceptadoRegistroAmbiental>();
								plan = objMedidads.getAspectoAmbientalPma().getPlanManejoAmbientalPma().getDescripcion();
								planId = objMedidads.getAspectoAmbientalPma().getPlanManejoAmbientalPma().getId();
								entidadMedida.add(objMedidads);
								PmaAceptadoRegistroAmbiental objPmaAceptado =crearPlan(objMedidads, registroId);
								if(objPmaAceptado != null){
									entidadMedidaProyecto.add(objPmaAceptado);
								}
							}else{
								plan = objMedidads.getAspectoAmbientalPma().getPlanManejoAmbientalPma().getDescripcion();
								planId = objMedidads.getAspectoAmbientalPma().getPlanManejoAmbientalPma().getId();
								entidadMedida.add(objMedidads);
								PmaAceptadoRegistroAmbiental objPmaAceptado =crearPlan(objMedidads, registroId);
								if(objPmaAceptado != null){
									entidadMedidaProyecto.add(objPmaAceptado);
								}
							}
						}else{
							entidadMedida.add(objMedidads);
							PmaAceptadoRegistroAmbiental objPmaAceptado =crearPlan(objMedidads, registroId);
							if(objPmaAceptado != null){
								entidadMedidaProyecto.add(objPmaAceptado);
							}
						}
						
					}
					entidadPlanPma.setPlanNombre(plan);
					entidadPlanPma.setPlanId(planId);
					entidadPlanPma.setMedidas(entidadMedida);
					entidadPlanPma.setMedidasProyecto(entidadMedidaProyecto);
					listaPMA.add(entidadPlanPma);
					if (existeDatos){
						break;
					}
				}
			}
		}
		return  listaPMA;
	}

	private PmaAceptadoRegistroAmbiental crearPlan(MedidaVerificacionPma medida, Integer registroId){
		PmaAceptadoRegistroAmbiental aux = obtenerPlanAceptado(medida, registroId);
		if(aux == null){
			return aux;
		}
		if(aux.getId() == null){
			aux.setMedidaVerificacionPma(medida);
			aux.setEstado(true);
		}
		aux.setOrden(1);
		return aux;
	}

	private PmaAceptadoRegistroAmbiental obtenerPlanAceptado(MedidaVerificacionPma medida, Integer registroId){
		PmaAceptadoRegistroAmbiental obj = new PmaAceptadoRegistroAmbiental();
		Query sql =crudServiceBean.getEntityManager().createQuery("select p from PmaAceptadoRegistroAmbiental p where p.registroAmbientalId=:registroId and p.medidaVerificacionPma.id = :medioVerificacionId and p.estado=true");
		sql.setParameter("registroId", registroId).setParameter("medioVerificacionId", medida.getId());
		if(sql.getResultList().size()>0){
			obj=(PmaAceptadoRegistroAmbiental) sql.getResultList().get(0);
			if(!obj.isAceptado()){
				return null;
			}
		}
		return obj;   
	}
	
	@SuppressWarnings("unchecked")
	public List<MedidaVerificacionPma> buscarMedidaaAmbientalesPMAPorCiu( Integer codActividad, Integer tipoPlan, 
			boolean paraAdicionar, boolean conActividad, ProyectoLicenciaCoa proyectoLicenciaCoa, Integer idConsideracion) {
		try {
			StringBuilder sql = new StringBuilder();
			List<Integer> result = new ArrayList<Integer>();
			List<MedidaVerificacionPma> planManejoAmbiental020s = new ArrayList<MedidaVerificacionPma>();
			String sqlWhere = "";
			String sqlMineria = "";
			Integer total = 0;
			if(conActividad){
				//busco si existe pma para la consideracion
				if(idConsideracion > 0){
					Query sqlPma=crudServiceBean.getEntityManager().createNativeQuery(" select count(*) "
							+ "  FROM coa_environmental_record.management_plan_ciuu "
							+ "	 where mapc_status is true and cosu_id = "+idConsideracion);
					total = ((BigInteger) sqlPma.getSingleResult()).intValue();
					// si existe pma para la consideracion pongo el idconsideracion para no realizar la busqueda 
					if(total > 0){
						sqlWhere=" and cosu_id = "+idConsideracion;
					}else{
						sqlWhere=" and cosu_id is null ";
					}
				}else{
					sqlWhere=" and cosu_id is null ";
				}
				
				Query sqlSelect=crudServiceBean.getEntityManager().createNativeQuery(" SELECT  enmp_id"
								+ "  FROM coa_environmental_record.management_plan_ciuu "
								+ " where mapc_status = true and caci_id = "+codActividad+sqlWhere+sqlMineria);
				result = (List<Integer>) sqlSelect.getResultList();
			}
			if(paraAdicionar ){
				sql.append( "SELECT  m FROM MedidaVerificacionPma m where m.estado = true "
						+ " and m.tipoEstandar = true and m.estadoCatalogoCiu = "+(conActividad?"true":"false")
						+ " and m.aspectoAmbientalPma.estado = true and m.aspectoAmbientalPma.planManejoAmbientalPma.estado = true "
						+ " and m.aspectoAmbientalPma.planManejoAmbientalPma.faseTipoPlan.id  = :tipoPlanId ");
				if(conActividad){
					// valido si no hay pma con esa actividad retorno vacio
					if(result.size() == 0)
						return planManejoAmbiental020s;
					sql.append(" and m.id in( :codActividad ) ");
				}
				//si se sellecciono que no realiza gestion de desechos no se muestra el subplan de desechos
				if(!proyectoLicenciaCoa.getGeneraDesechos()){
					sql.append(" and m.aspectoAmbientalPma.descripcion not like '%Gestión de residuos / desechos peligrosos y especiales%' ");
				}
				// si no tiene un pma especifico entra a la validacion de interseccion
				if(total == 0){
					//si no interseca con Áreas Protegidas, Áreas Especiales del Conservación y Patrimonio Forestal Nacionalno se muestra el subplan de desechos
					if(!proyectoLicenciaCoa.getInterecaBosqueProtector() && !proyectoLicenciaCoa.getInterecaSnap() && !proyectoLicenciaCoa.getInterecaPatrimonioForestal()){
						sql.append(" and m.aspectoAmbientalPma.planManejoAmbientalPma.descripcion not like '%vida%' ");
					}
				}
				sql.append( "order by m.aspectoAmbientalPma.planManejoAmbientalPma.orden, m.orden ");
				if(result.size() > 0)
					planManejoAmbiental020s = crudServiceBean
						.getEntityManager()
						.createQuery(sql.toString()).setParameter("tipoPlanId", tipoPlan).setParameter("codActividad", result)
						.getResultList();
				else
					planManejoAmbiental020s = crudServiceBean
						.getEntityManager()
						.createQuery(sql.toString()).setParameter("tipoPlanId", tipoPlan)
						.getResultList();
					
			}else{
				sql.append( "SELECT  m FROM MedidaVerificacionPma m where m.estado = true "
						+ " and m.tipoEstandar = true and m.estadoCatalogoCiu = false " 
						+ "and m.id "+(conActividad?"":" not ")+"  in( select p.medioVerificacionId from PlanManejoAmbientalActividad p where p.estado = true and p.catalogoCIUU.id in ("+codActividad+")) "
						+ "and m.aspectoAmbientalPma.estado = true and m.aspectoAmbientalPma.planManejoAmbientalPma.estado = true "
						+ " and m.aspectoAmbientalPma.planManejoAmbientalPma.faseTipoPlan.id  = :tipoPlanId ");
				//si se sellecciono que no realiza gestion de desechos no se muestra el subplan de desechos
				if(!proyectoLicenciaCoa.getGeneraDesechos()){
					sql.append(" and m.aspectoAmbientalPma.planManejoAmbientalPma.descripcion not like '%Desechos%' ");
				}
				//si se no interseca con Áreas Protegidas, Áreas Especiales del Conservación y Patrimonio Forestal Nacionalno se muestra el subplan de desechos
				if(!proyectoLicenciaCoa.getInterecaBosqueProtector() && !proyectoLicenciaCoa.getInterecaSnap() && !proyectoLicenciaCoa.getInterecaPatrimonioForestal()){
					sql.append(" and m.aspectoAmbientalPma.planManejoAmbientalPma.descripcion not like '%vida%' ");
				}
						sql.append( "order by m.aspectoAmbientalPma.planManejoAmbientalPma.orden, m.orden ");
				planManejoAmbiental020s = crudServiceBean
						.getEntityManager()
						.createQuery(sql.toString()).setParameter("tipoPlanId", tipoPlan)
						.getResultList();
			}
			
			return planManejoAmbiental020s;
		} catch (Exception e) {
			return null;
			// TODO: handle exception
		}
	}


	public List<MedidaVerificacionPma> buscarMedidaaAmbientalesPMAGenerales(Integer tipoPlan) {
		try {
			List<MedidaVerificacionPma> planManejoAmbiental020s = crudServiceBean
					.getEntityManager()
					.createQuery(
							" SELECT m FROM MedidaVerificacionPma m"
							+ " where m.estado = true "
							+ " and m.aspectoAmbientalPma.planManejoAmbientalPma.faseTipoPlan.id  = :tipoPlanId "
							+ " order by m.aspectoAmbientalPma.planManejoAmbientalPma.orden, m.orden ")
							.setParameter("tipoPlanId", tipoPlan).getResultList();
			return planManejoAmbiental020s;
		} catch (Exception e) {
			return null;
		}
	}
	
	public void deshabilitarPlanesPMAFasesEliminadass(Integer registroAmbientalId){
			String sql =" update coa_environmental_record.accept_management_plan "
				+ " set acmp_status = false , acmp_date_update = now() "
				+ "where enre_id = "+registroAmbientalId+" and acmp_status = true "
						+ " and acmp_id in ("
						+ "SELECT acmp_id"
						+ "  FROM coa_environmental_record.accept_management_plan a inner join coa_environmental_record.environmental_measure_pma b on a.enmp_id = b.enmp_id "
						+ "  inner join coa_environmental_record.environmental_aspect_pma c on b.enas_id = c.enas_id "
						+ " inner join coa_environmental_record.management_plan_record_pma d on c.mapr_id = d.mapr_id "
						+ " inner join coa_environmental_record.environmental_record_phases e on a.enre_id = e.enre_id and d.phas_id = e.phas_id "
						+ " where a.enre_id = "+registroAmbientalId+"  and a.acmp_status = true  and e.enph_status = false"
						+ ") ;";
				crudServiceBean.insertUpdateByNativeQuery(sql.toString(), null);
	}
}
