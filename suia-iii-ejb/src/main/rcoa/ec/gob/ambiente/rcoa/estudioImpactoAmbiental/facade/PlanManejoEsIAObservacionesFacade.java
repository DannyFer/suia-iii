package ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.PlanManejoEsIAObservaciones;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class PlanManejoEsIAObservacionesFacade {
	
	@EJB
    private CrudServiceBean crudServiceBean;
    @EJB
    private DocumentosImpactoEstudioFacade documentosFacade;
    @EJB
    private ObservacionesEsIAFacade observacionesEsIAFacade;

    public PlanManejoEsIAObservaciones guardar(PlanManejoEsIAObservaciones plan) {
		return crudServiceBean.saveOrUpdate(plan);
	}

	@SuppressWarnings("unchecked")
	public PlanManejoEsIAObservaciones getPorSubPlanNombreObservacion(Integer idSubplan, String nombreObservacion) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("idPlan", idSubplan);
		params.put("observacion", nombreObservacion);
		List<PlanManejoEsIAObservaciones> listaPma = (List<PlanManejoEsIAObservaciones>) crudServiceBean.findByNamedQuery(PlanManejoEsIAObservaciones.GET_POR_PLAN_OBSERVACION, params);
		if(listaPma.size() > 0 ){
			return listaPma.get(0);
		}

		return new PlanManejoEsIAObservaciones();
	}


	@SuppressWarnings("unchecked")
	public List<PlanManejoEsIAObservaciones> getListaPorSubPlanEstado(Integer idSubplan, Boolean estado) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("idPlan", idSubplan);
		params.put("estado", estado);

		List<PlanManejoEsIAObservaciones> listaPma = (List<PlanManejoEsIAObservaciones>) crudServiceBean.findByNamedQuery(PlanManejoEsIAObservaciones.GET_POR_PLAN_ESTADO, params);
		if(listaPma.size() > 0 ){
			return listaPma;
		}

		return new ArrayList<>();
	}

}