package ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.rcoa.dto.EntityPmaEsIA;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.DetallePlanManejoEsIA;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.DocumentoEstudioImpacto;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.ObservacionesEsIA;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.PlanManejoAmbientalEsIA;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.PlanManejoEsIAObservaciones;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.ProgramaPlanManejoEsIA;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.SubplanesManejoAmbientalEsIA;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;

@Stateless
public class PlanManejoAmbientalEsIAFacade {
	
	@EJB
    private CrudServiceBean crudServiceBean;
    @EJB
    private DocumentosImpactoEstudioFacade documentosFacade;
    @EJB
    private ObservacionesEsIAFacade observacionesEsIAFacade;
    @EJB
    private PlanManejoEsIAObservacionesFacade planManejoEsIAObservacionesFacade;

    public PlanManejoAmbientalEsIA guardarPlan(PlanManejoAmbientalEsIA plan) {
		return crudServiceBean.saveOrUpdate(plan);
	}

	public DetallePlanManejoEsIA guardarPlanDetalle(DetallePlanManejoEsIA detalle) {
		return crudServiceBean.saveOrUpdate(detalle);
	}

	public ProgramaPlanManejoEsIA guardarPrograma(ProgramaPlanManejoEsIA programa) {
		return crudServiceBean.saveOrUpdate(programa);
	}
	
	public ProgramaPlanManejoEsIA eliminarPrograma(ProgramaPlanManejoEsIA programa) {
		ProgramaPlanManejoEsIA programaA = crudServiceBean.saveOrUpdate(programa);

		String sql = " update coa_environmental_impact_study.management_plan_study_detail set mpsd_status = false, "
				+ " mpsd_date_update = now(), mpsd_user_update =  '" + programaA.getUsuarioCreacion() + "' "
				+ " where mpsp_id = " + programaA.getId() + " ;";
		crudServiceBean.insertUpdateByNativeQuery(sql.toString(), null);
		
		return programaA;
	}

	@SuppressWarnings("unchecked")
	public List<PlanManejoAmbientalEsIA> obtenerPlanManejoEsia(Integer idEstudio) {
		Map<String, Object> paramPlan = new HashMap<String, Object>();
		paramPlan.put("idEstudio", idEstudio);
		List<PlanManejoAmbientalEsIA> listaPma = (List<PlanManejoAmbientalEsIA>) crudServiceBean.findByNamedQuery(PlanManejoAmbientalEsIA.GET_POR_ESTUDIO, paramPlan);
		if(listaPma.size() > 0 ){
			return listaPma;
		}

		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<PlanManejoAmbientalEsIA> obtenerPlanManejoEsiaPrincipales(Integer idEstudio) {
		Map<String, Object> paramPlan = new HashMap<String, Object>();
		paramPlan.put("idEstudio", idEstudio);
		List<PlanManejoAmbientalEsIA> listaPma = (List<PlanManejoAmbientalEsIA>) crudServiceBean.findByNamedQuery(PlanManejoAmbientalEsIA.GET_POR_ESTUDIO_PRINCIPALES, paramPlan);
		if(listaPma.size() > 0 ){
			return listaPma;
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	public List<EntityPmaEsIA> obtenerSubplanes(Integer idEstudio, Boolean edicionHabilitada) throws Exception
	{	
		List<EntityPmaEsIA> resultado = new ArrayList<EntityPmaEsIA>();

		PlanManejoAmbientalEsIA infoPlan = null;

		List<SubplanesManejoAmbientalEsIA> listaSubplanes = (List<SubplanesManejoAmbientalEsIA>) crudServiceBean
					.findByNamedQuery(SubplanesManejoAmbientalEsIA.GET_PLANES_ACTIVOS, null);
		if(listaSubplanes.size() > 0 ){
			for(SubplanesManejoAmbientalEsIA subplan : listaSubplanes) {
				
				EntityPmaEsIA itemPlan = new EntityPmaEsIA();
				
				Map<String, Object> paramPlan = new HashMap<String, Object>();
				paramPlan.put("idSubplan", subplan.getId());
				paramPlan.put("idEstudio", idEstudio);
				List<PlanManejoAmbientalEsIA> listaPmaPorSubplan = (List<PlanManejoAmbientalEsIA>) crudServiceBean.findByNamedQuery(PlanManejoAmbientalEsIA.GET_POR_ESTUDIO_SUBPLAN, paramPlan);

				if(listaPmaPorSubplan.size() > 0 ){
					itemPlan = getInfoPlan(subplan, listaPmaPorSubplan.get(0));

					if (itemPlan.getPlanManejo().getTieneObservaciones() != null
							&& itemPlan.getPlanManejo().getTieneObservaciones()) {

						List<PlanManejoAmbientalEsIA> listaSubsanacionesSubPlan = obtenerSubPlanesHijos(itemPlan.getPlanManejo().getId());

						if(edicionHabilitada) {
							List<ObservacionesEsIA> observacionesPendientes = observacionesEsIAFacade.listarPorIdClaseSeccionNoCorregidas(idEstudio, subplan.getCodigo());
							//validar si existen observaciones pendientes de corregir para agregar un nuevo ítem del plan de manejo para la subsanación
							if(observacionesPendientes != null && observacionesPendientes.size() > 0) {
								if (listaSubsanacionesSubPlan.size() > 0) {
									PlanManejoAmbientalEsIA ultimaSubsanacion = listaSubsanacionesSubPlan.get(listaSubsanacionesSubPlan.size() - 1);
									if (ultimaSubsanacion.getRegistroRevisado()) { //si la ultima subsnacion está revisada se agrega un nuevo ítem del plan de manejo para la subsanación
										PlanManejoAmbientalEsIA nuevaSubsanacion = new PlanManejoAmbientalEsIA();
										nuevaSubsanacion.setNumeroObservacion(ultimaSubsanacion.getNumeroObservacion() + 1);
										nuevaSubsanacion.setRegistroRevisado(false);
										listaSubsanacionesSubPlan.add(nuevaSubsanacion);
										
									}
								} else {
									PlanManejoAmbientalEsIA nuevaSubsanacion = new PlanManejoAmbientalEsIA();
									nuevaSubsanacion.setNumeroObservacion(1);
									nuevaSubsanacion.setRegistroRevisado(false);
									listaSubsanacionesSubPlan.add(nuevaSubsanacion);
								}
							}
						}

						List<EntityPmaEsIA> resultadoSubsanaciones = new ArrayList<EntityPmaEsIA>();
						for (PlanManejoAmbientalEsIA planSubsanado : listaSubsanacionesSubPlan) {
							EntityPmaEsIA itemPlanSub = new EntityPmaEsIA();
							if(planSubsanado.getId() != null) {
								itemPlanSub = getInfoPlan(subplan, planSubsanado);
							} else {
								itemPlanSub.setSubplan(subplan);
								itemPlanSub.setPlanManejo(planSubsanado);
							}
							resultadoSubsanaciones.add(itemPlanSub);
						}

						itemPlan.getPlanManejo().setListaSubsanacionesSubPlan(resultadoSubsanaciones);
					}
					
				} else {
					infoPlan = new PlanManejoAmbientalEsIA();
					infoPlan.setNumeroObservacion(0);
					infoPlan.setPlanManejoObservacion(new PlanManejoEsIAObservaciones());

					itemPlan.setSubplan(subplan);
					itemPlan.setPlanManejo(infoPlan);
				}

				resultado.add(itemPlan);
			}
		}
		
		return  resultado;
	}

	@SuppressWarnings("unchecked")
	public EntityPmaEsIA getInfoPlan(SubplanesManejoAmbientalEsIA subplan, PlanManejoAmbientalEsIA infoPlan) throws Exception {
		List<ProgramaPlanManejoEsIA> listaProgramaPlan = null;
		DocumentoEstudioImpacto plantillaPma = null;
		List<DocumentoEstudioImpacto> listaAnexosSubPlan = null;
		
		Map<String, Object> paramPrograma = new HashMap<String, Object>();
		paramPrograma.put("idPlan", infoPlan.getId());
		listaProgramaPlan = (List<ProgramaPlanManejoEsIA>) crudServiceBean.findByNamedQuery(ProgramaPlanManejoEsIA.GET_PRINCIPALES_POR_PLAN, paramPrograma);

		if (listaProgramaPlan.size() > 0) {
			for (ProgramaPlanManejoEsIA programa : listaProgramaPlan) {
//				programa = getInfoPrograma(programa);
			}

			String nombreTablaDoc = "PMA_" + subplan.getNombre();

			List<DocumentoEstudioImpacto> listaPlantillaPma = documentosFacade.documentoXTablaIdXIdDocLista(infoPlan.getId(), nombreTablaDoc, TipoDocumentoSistema.EIA_PLANTILLA_DETALLE_PMA);
			if (listaPlantillaPma.size() > 0) {
				plantillaPma = listaPlantillaPma.get(0);
			}
		} else {
			listaProgramaPlan = null;
		}

		listaAnexosSubPlan = documentosFacade.documentoXTablaIdXIdDocLista(infoPlan.getId(), "PMA_AnexoSubPlan", TipoDocumentoSistema.EIA_ANEXO_SUB_PLAN_PMA);
		
		EntityPmaEsIA itemPlan = new EntityPmaEsIA();
		itemPlan.setSubplan(subplan);
		itemPlan.setPlanManejo(infoPlan);
		itemPlan.setListaProgramas(listaProgramaPlan);
		itemPlan.getPlanManejo().setPlantillaSubPlan(plantillaPma);
		if(listaAnexosSubPlan != null && listaAnexosSubPlan.size() > 0 ){
			itemPlan.getPlanManejo().setListaAnexosSubPlan(listaAnexosSubPlan);
		}

		return itemPlan;
	}	

	@SuppressWarnings("unchecked")
	private ProgramaPlanManejoEsIA getInfoPrograma(ProgramaPlanManejoEsIA programa) throws Exception {

		Map<String, Object> paramDetalle = new HashMap<String, Object>();
		paramDetalle.put("idPrograma", programa.getId());
		List<DetallePlanManejoEsIA> detalle = (List<DetallePlanManejoEsIA>) crudServiceBean.findByNamedQuery(DetallePlanManejoEsIA.GET_POR_PROGRAMA, paramDetalle);

		programa.setListaDetallePlan(detalle);

		return programa;
	}

	@SuppressWarnings("unchecked")
	public List<PlanManejoAmbientalEsIA> obtenerSubPlanesHijos(Integer idPadre) {
		Map<String, Object> paramPlan = new HashMap<String, Object>();
		paramPlan.put("idPadre", idPadre);
		List<PlanManejoAmbientalEsIA> listaPma = (List<PlanManejoAmbientalEsIA>) crudServiceBean.findByNamedQuery(PlanManejoAmbientalEsIA.GET_POR_PADRE, paramPlan);
		if(listaPma.size() > 0 ){
			return listaPma;
		}

		return new ArrayList<PlanManejoAmbientalEsIA>();
	}

	private SubplanesManejoAmbientalEsIA obtenerSubPlanPma(Integer idSubplan)
			throws Exception {
		try {
			SubplanesManejoAmbientalEsIA subPlan = (SubplanesManejoAmbientalEsIA) crudServiceBean
					.getEntityManager()
					.createQuery(
							"SELECT s FROM SubplanesManejoAmbientalEsIA s where s.id = :id")
					.setParameter("id", idSubplan).getSingleResult();
			return subPlan;

		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String validarIngresoObservacionesPma(Integer idEstudio, String nombreClaseObservaciones) throws Exception {
		List<PlanManejoAmbientalEsIA> listaPma = obtenerPlanManejoEsiaPrincipales(idEstudio);
		for (PlanManejoAmbientalEsIA itemPlan : listaPma) {
			PlanManejoEsIAObservaciones planObservacion = planManejoEsIAObservacionesFacade.getPorSubPlanNombreObservacion(itemPlan.getId(), nombreClaseObservaciones);

			if(planObservacion != null && planObservacion.getId() != null) {
				if (planObservacion.getTieneObservaciones() != null
					&& planObservacion.getTieneObservaciones()) {
					SubplanesManejoAmbientalEsIA subPlan = obtenerSubPlanPma(itemPlan.getIdSubplan());
					if (subPlan != null) {
						List<String> listaNombreClases = Arrays.asList(nombreClaseObservaciones.split(";"));
						List<ObservacionesEsIA> observaciones = observacionesEsIAFacade.listarPorIdClaseNombreClase(idEstudio, listaNombreClases, subPlan.getCodigo());
						if (observaciones == null || observaciones.size() == 0) {
							return "Debe ingresar al menos una observación en los Sub Planes observados"; //no hay observaciones ingresadas para el subplan observado
						}
					}
				}
			} else {
				return "Debe registrar la respuesta a la pregunta ¿Existen observaciones? en todos los Sub Planes"; //no hay observaciones ingresadas para el subplan observado
			}
			
		}

		return null; //existe observaciones ingresadas para el subplan observado
	}

	public void actualizarEstadoRevisionPma(Integer idEstudio, Boolean estado) throws Exception {
		List<PlanManejoAmbientalEsIA> listaPma = obtenerPlanManejoEsia(idEstudio);
		for (PlanManejoAmbientalEsIA itemPlan : listaPma) {
			
			List<PlanManejoEsIAObservaciones> listaPlanesObservados = planManejoEsIAObservacionesFacade.getListaPorSubPlanEstado(itemPlan.getId(), true);

			Boolean subPlanObs = (listaPlanesObservados.size() >0 ) ? true : false;
			
			itemPlan.setRegistroRevisado(estado);
			itemPlan.setTieneObservaciones(subPlanObs);
			crudServiceBean.saveOrUpdate(itemPlan);
		}
	}

}