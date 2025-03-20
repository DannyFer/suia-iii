package ec.gob.ambiente.prevencion.categoria2.controllers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;

import ec.gob.ambiente.prevencion.categoria2.bean.DetallePlanManejoAmbientalBean;
import ec.gob.ambiente.suia.detallefichaplan.facade.DetalleFichaPlanFacade;
import ec.gob.ambiente.suia.domain.DetalleFichaPlan;
import ec.gob.ambiente.suia.domain.FichaAmbientalPma;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.FichaAmbientalPmaFacade;

public class DetallePmaController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5760447123089377015L;

	@EJB
	private DetalleFichaPlanFacade detalleFichaPlanFacade;
	@EJB
	private FichaAmbientalPmaFacade fichaAmbientalPmaFacade;

	public List<String> guardarDetalleFichaPlan(
			List<DetallePlanManejoAmbientalBean> listaDetallePlan)
			throws ServiceException {
		List<String> errores = new ArrayList<String>();
		List<DetalleFichaPlan> listaDetalles = new ArrayList<DetalleFichaPlan>();
		FichaAmbientalPma fichaAmbientalPma = null;
		for (DetallePlanManejoAmbientalBean detallePlanManejoAmbientalBean : listaDetallePlan) {
			if (detallePlanManejoAmbientalBean.getFechaInicio() != null && detallePlanManejoAmbientalBean.getFechaInicio().compareTo(
					detallePlanManejoAmbientalBean.getFechaFin()) > 0) {
				errores.add("La fecha de fin no puede ser menor a la fecha de inicio para el "
						+ detallePlanManejoAmbientalBean.getPlanSector()
								.getTipoPlanManejoAmbiental().getTipo());
			}
			DetalleFichaPlan detalle = new DetalleFichaPlan();
			detalle.setId(detallePlanManejoAmbientalBean.getId());
			detalle.setFechaFin(detallePlanManejoAmbientalBean.getFechaFin());
			detalle.setFechaInicio(detallePlanManejoAmbientalBean
					.getFechaInicio());
			detalle.setPlanSector(detallePlanManejoAmbientalBean
					.getPlanSector());
			detalle.setFichaAmbientalPma(detallePlanManejoAmbientalBean
					.getFichaAmbientalPma());
			fichaAmbientalPma = detallePlanManejoAmbientalBean
					.getFichaAmbientalPma();
			listaDetalles.add(detalle);
		}
		
		if(errores.isEmpty()){
			detalleFichaPlanFacade.guadar(listaDetalles);
			fichaAmbientalPma.setValidarPlanManejoAmbiental(true);
			fichaAmbientalPmaFacade.guardarSoloFicha(fichaAmbientalPma);
		}
		return errores;
	}
}
