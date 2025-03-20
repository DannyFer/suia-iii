package ec.gob.ambiente.suia.eia.pma.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.DetalleEvaluacionAspectoAmbiental;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.domain.EvaluacionAspectoAmbiental;
import ec.gob.ambiente.suia.domain.IdentificacionEvaluacionImpactoAmbiental;
import ec.gob.ambiente.suia.eia.facade.EstudioImpactoAmbientalFacade;
import ec.gob.ambiente.suia.eia.impactoAmbiental.facade.ImpactoAmbientalFacade;
import ec.gob.ambiente.suia.eia.pma.bean.PlanManejoAmbientalBean;
import ec.gob.ambiente.suia.eia.pma.facade.PlanManejoAmbientalFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class PlanManejoAmbientalController implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -971306035001252479L;
	
	@EJB
	private ImpactoAmbientalFacade impactoAmbientalFacade;
	
	@EJB
	private PlanManejoAmbientalFacade planManejoAmbientalFacade;
	
	@EJB
	private EstudioImpactoAmbientalFacade estudioImpactoAmbientalFacade;

	@Getter
	@Setter
	private PlanManejoAmbientalBean planManejoAmbientalBean;
	
	public void init(){
		EstudioImpactoAmbiental estudio = estudioImpactoAmbientalFacade.obtenerEIAPorId(((EstudioImpactoAmbiental) JsfUtil.devolverObjetoSession(Constantes.SESSION_EIA_OBJECT)).getId());
		getPlanManejoAmbientalBean().setEstudioImpactoAmbiental(estudio);
		getPlanManejoAmbientalBean().setDetalleEvaluacionAspectoAmbientals(cargarDetalleEvaluacionImpactosAmbientalesXEIA(estudio));
		
	}
	
	/**
	 * Método para consultar los detalles de evaluación de impactos que fueron registrados en Identificación y Evaluación de impactos ambientales
	 * @param estudio
	 */
	public List<DetalleEvaluacionAspectoAmbiental> cargarDetalleEvaluacionImpactosAmbientalesXEIA(EstudioImpactoAmbiental estudio){
		IdentificacionEvaluacionImpactoAmbiental identificacionEvaluacionImpactoAmbiental = new IdentificacionEvaluacionImpactoAmbiental();
		identificacionEvaluacionImpactoAmbiental = impactoAmbientalFacade.getIdentificacionEvaluacionImpactoAmbientale(estudio);
		
		List<EvaluacionAspectoAmbiental> evaluacionAspectosAmbientales = identificacionEvaluacionImpactoAmbiental.getEvaluacionAspectoAmbientals();
		List<DetalleEvaluacionAspectoAmbiental> detalleAspectosAmbientales = new ArrayList<DetalleEvaluacionAspectoAmbiental>();
		
		for (EvaluacionAspectoAmbiental evaluacionAspectoAmbiental : evaluacionAspectosAmbientales) {
			detalleAspectosAmbientales.addAll(evaluacionAspectoAmbiental.getDetalleEvaluacionLista());
		}
		return detalleAspectosAmbientales;
	}
	
	
}
