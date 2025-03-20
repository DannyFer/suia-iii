
package ec.gob.ambiente.rcoa.viabilidadTecnica.facade;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.dto.EntidadPma;
import ec.gob.ambiente.rcoa.dto.EntidadPmaViabilidad;
import ec.gob.ambiente.rcoa.model.PlanManejoAmbientalPma;
import ec.gob.ambiente.rcoa.model.PmaAceptadoRegistroAmbiental;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.viabilidadTecnica.model.FaseViabilidadTecnicaRcoa;
import ec.gob.ambiente.rcoa.viabilidadTecnica.model.MedioFrecuenciaMedida;
import ec.gob.ambiente.rcoa.viabilidadTecnica.model.PmaViabilidadTecnica;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Usuario;

@Stateless
public class PmaViabilidadTecnicaFacade {
	
	@EJB
    private CrudServiceBean crudServiceBean;
	
	public void guardar(PmaViabilidadTecnica obj, Usuario usuario){
		if(obj.getId()==null){
			obj.setUsuarioCreacion(usuario.getNombre());
			obj.setFechaCreacion(new Date());			
		}
		else{
			obj.setUsuarioModificacion(usuario.getNombre());
			obj.setFechaModificacion(new Date());			
		}
		crudServiceBean.saveOrUpdate(obj);
	}
	
	public PmaViabilidadTecnica buscarPorId(Integer id){
		try {
			return (PmaViabilidadTecnica)crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM PmaViabilidadTecnica o where o.id = :id")
					.setParameter("id", id).getSingleResult();	
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}	
	
	public void deshabilitarPlanesPMAFasesEliminadas(Integer faseid){
		try {
			
			//buscamos los registros para según el id de base colocarlos en false
			String sql = "update coa_viability_technical.technical_viability_pma set tvip_status = false, tvip_status_date = now()  "
					+ " where tvph_id = " + faseid + " and tvip_status = true;";
			
			crudServiceBean.insertUpdateByNativeQuery(sql.toString(), null);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public List<EntidadPmaViabilidad> obtenerPma(Boolean manejaDesechos, Integer tipoPlan, Integer idViabilidadTecnicaProyecto, FaseViabilidadTecnicaRcoa fase){
		List<EntidadPmaViabilidad> listaPMA = new ArrayList<EntidadPmaViabilidad>();
		
		Integer catalogoId;
		boolean existeDatos = false;
		
		List<MedioFrecuenciaMedida> listaMedidas = obtenerPMaViabilidad(manejaDesechos, tipoPlan);
		
				
		if(listaMedidas != null && listaMedidas.size() > 0){
			String plan = "";
			Integer planId = 0;
			EntidadPmaViabilidad entidadPlanPma = new EntidadPmaViabilidad();
			List<MedioFrecuenciaMedida> entidadMedida = new ArrayList<MedioFrecuenciaMedida>();
			List<PmaViabilidadTecnica> entidadMedidaProyecto = new ArrayList<PmaViabilidadTecnica>();
			
			
			for(MedioFrecuenciaMedida medida : listaMedidas){
				if(!plan.equals(medida.getAspectoViabilidad().getPlanManejoAmbientalPma().getDescripcion())){
					if(!plan.isEmpty()){
						entidadPlanPma.setPlanNombre(plan);
						entidadPlanPma.setPlanId(planId);
						entidadPlanPma.setMedidas(entidadMedida);
						entidadPlanPma.setMedidasProyecto(entidadMedidaProyecto);
						listaPMA.add(entidadPlanPma);
						entidadPlanPma = new EntidadPmaViabilidad();
						entidadMedida = new ArrayList<MedioFrecuenciaMedida>();
						entidadMedidaProyecto = new ArrayList<PmaViabilidadTecnica>();
						plan = medida.getAspectoViabilidad().getPlanManejoAmbientalPma().getDescripcion();
						planId = medida.getAspectoViabilidad().getPlanManejoAmbientalPma().getId();
						entidadMedida.add(medida);
						PmaViabilidadTecnica objPmaAceptado = crearObjeto(medida, idViabilidadTecnicaProyecto, fase);
						if(objPmaAceptado != null){
							entidadMedidaProyecto.add(objPmaAceptado);
						}
					}else{
						plan = medida.getAspectoViabilidad().getPlanManejoAmbientalPma().getDescripcion();
						planId = medida.getAspectoViabilidad().getPlanManejoAmbientalPma().getId();
						entidadMedida.add(medida);
						PmaViabilidadTecnica objPmaAceptado = crearObjeto(medida, idViabilidadTecnicaProyecto, fase);
						if(objPmaAceptado != null){
							entidadMedidaProyecto.add(objPmaAceptado);
						}
						
					}
				}else{
					entidadMedida.add(medida);
					PmaViabilidadTecnica objPmaAceptado = crearObjeto(medida, idViabilidadTecnicaProyecto, fase);
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
			
		}		
		
		return listaPMA;
		
	}
	
	@SuppressWarnings("unchecked")
	public List<MedioFrecuenciaMedida> obtenerPMaViabilidad(boolean manejaDesechos, int tipoPlan){
		
		List<MedioFrecuenciaMedida> lista = new ArrayList<MedioFrecuenciaMedida>();
		try {
			
			Query sql = crudServiceBean.getEntityManager().createQuery("SELECT m FROM MedioFrecuenciaMedida m where "
					+ "m.aspectoViabilidad.planManejoAmbientalPma.faseTipoPlan.id = :tipoPlan and m.estado = true "
					+ "and m.aspectoViabilidad.manejaDesechos = :manejaDesechos order by m.id asc");
			
			sql.setParameter("tipoPlan", tipoPlan);
			sql.setParameter("manejaDesechos", manejaDesechos);
			
			lista = (List<MedioFrecuenciaMedida>)sql.getResultList();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;
	}	
	
	private PmaViabilidadTecnica crearObjeto(MedioFrecuenciaMedida medida, Integer idViabilidad, FaseViabilidadTecnicaRcoa fase){
		PmaViabilidadTecnica aux = obtenerPma(medida, idViabilidad);
		if(aux == null){
			return aux;
		}
		if(aux.getId() == null){
			aux.setMedioFrecuenciaMedida(medida);
			aux.setEstado(true);
			aux.setFaseViabilidadTecnica(fase);			
		}
		return aux;
	}
	
	private PmaViabilidadTecnica obtenerPma(MedioFrecuenciaMedida medida, Integer idViabilidad){
		PmaViabilidadTecnica obj = new PmaViabilidadTecnica();
		
		Query sql = crudServiceBean.getEntityManager().createQuery("Select p from PmaViabilidadTecnica p where "
				+ "p.faseViabilidadTecnica.viabilidadTecnicaProyecto.id = :idViabilidad and p.estado = true "
				+ "and p.medioFrecuenciaMedida.id = :idMedida");
		sql.setParameter("idMedida", medida.getId());
		sql.setParameter("idViabilidad", idViabilidad);
		
		if(sql.getResultList().size()>0){
			obj = (PmaViabilidadTecnica)sql.getResultList().get(0);	
			if(obj.getPlazo() != null){
				try {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					Date fecha = sdf.parse(obj.getPlazo());
					obj.setPlazoFecha(fecha);
				} catch (Exception e) {
					e.printStackTrace();
				}				
			}			
		}
		
		return obj;
	}
	
	@SuppressWarnings("unchecked")
	public List<EntidadPmaViabilidad> obtenerPmaPorViabilidaTecnica(Integer tipoPlan, Integer idViabilidadTecnicaProyecto){
		List<EntidadPmaViabilidad> listaPMA = new ArrayList<EntidadPmaViabilidad>();
		List<PlanManejoAmbientalPma> listaPma;
		
		listaPma = crudServiceBean
				.getEntityManager()
				.createQuery(
						" SELECT m FROM PlanManejoAmbientalPma m "
						+ "where m.estado = true and m.id in ("
						+ " select distinct p.medioFrecuenciaMedida.aspectoViabilidad.planManejoAmbientalPma.id from PmaViabilidadTecnica p "
						+ "where p.estado = true  and p.faseViabilidadTecnica.viabilidadTecnicaProyecto.id = :idViabilidadTecnicaProyecto "
						+ "and p.medioFrecuenciaMedida.aspectoViabilidad.planManejoAmbientalPma.faseTipoPlan.id = :tipoPlanId "	
						+ ")"
						+ "  order by m.orden "
						)
				.setParameter("idViabilidadTecnicaProyecto", idViabilidadTecnicaProyecto)
				.setParameter("tipoPlanId", tipoPlan)
				.getResultList();
		
		for (PlanManejoAmbientalPma objPlanManejoAmbientalCoa : listaPma) {
			List<PmaViabilidadTecnica> listaMedidas = buscarMedidasAmbientalesPMAPorPlanViabilidadl(objPlanManejoAmbientalCoa.getId(), idViabilidadTecnicaProyecto);
			if(listaMedidas != null && listaMedidas.size() > 0){
				// inicializo los valores como guardados
				EntidadPmaViabilidad entidadPlanPma = new EntidadPmaViabilidad();
				entidadPlanPma.setPlanNombre(objPlanManejoAmbientalCoa.getDescripcion());
				entidadPlanPma.setPlanId(objPlanManejoAmbientalCoa.getId());
				entidadPlanPma.setMedidasProyecto(listaMedidas);
				listaPMA.add(entidadPlanPma);			
			}
		}
		return  listaPMA;		
	}
	
	@SuppressWarnings("unchecked")
	public List<PmaViabilidadTecnica> buscarMedidasAmbientalesPMAPorPlanViabilidadl(Integer planId, Integer idViabilidad) {
		try {
			//registros propios de pma
			List<PmaViabilidadTecnica> planManejoAmbiental = crudServiceBean
					.getEntityManager()
					.createQuery(
							" SELECT m FROM PmaViabilidadTecnica m"
							+ " where m.estado = true and m.aspectoViabilidadTecnica is null "
							+ "and m.medioFrecuenciaMedida.aspectoViabilidad.planManejoAmbientalPma.id  = :planId "						
							+ " and m.faseViabilidadTecnica.viabilidadTecnicaProyecto.id = :idViabilidad ")
					.setParameter("idViabilidad", idViabilidad).setParameter("planId", planId).getResultList();
			
			
			return planManejoAmbiental;
		} catch (Exception e) {
			e.printStackTrace();
			return null;			
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<PmaViabilidadTecnica> buscarMedidasAmbientalesIngresadasPorViabilidad(Integer planPadreId, Integer idViabilidad) {
		try {
			List<PmaViabilidadTecnica> planManejoAmbiental = crudServiceBean
					.getEntityManager()
					.createQuery(
							" SELECT m FROM PmaViabilidadTecnica m"
							+ " where m.estado = true and m.aceptado=true and m.medioFrecuenciaMedida.id is null and "
							+ " m.aspectoViabilidadTecnica.planManejoAmbientalPma.id  = :planId  "
							+ " and m.faseViabilidadTecnica.viabilidadTecnicaProyecto.id = :idViabilidad order by m.id ")
					.setParameter("idViabilidad", idViabilidad)
					.setParameter("planId", planPadreId).getResultList();
		
			return planManejoAmbiental;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void eliminarPlanesAgregadosNuevos(String listaId){
		try{
			String sql =" update coa_viability_technical.technical_viability_pma  set tvip_status = false, tvip_status_date=now(), tvip_user_update = tvip_creator_user "
					+ " where tvip_id in ("+listaId+") ;";
			crudServiceBean.insertUpdateByNativeQuery(sql.toString(), null);
			}catch(Exception e){
				System.out.println("Error al eliminar los planes");;
			}
	}
	
	


}
