package ec.gob.ambiente.prevencion.categoria2.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.prevencion.categoria2.controllers.DetallePmaController;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.detallefichaplan.facade.DetalleFichaPlanFacade;
import ec.gob.ambiente.suia.domain.DetalleFichaPlan;
import ec.gob.ambiente.suia.domain.FichaAmbientalPma;
import ec.gob.ambiente.suia.domain.PlanSector;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.FichaAmbientalPmaFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class DetallePmaBean implements Serializable {

	Logger LOG = Logger.getLogger(DetallePmaBean.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 253606719848118271L;
	@EJB
	private FichaAmbientalPmaFacade fichaAmbientalPmaFacade;
	@EJB
	private DetalleFichaPlanFacade detalleFichaPlanFacade;

	@Inject
	DetallePmaController detallePmaController;

	@Getter
	@Setter
	@ManagedProperty(value = "#{proyectosBean}")
	private ProyectosBean proyectosBean;
	@Getter
	@Setter
	private FichaAmbientalPma ficha;
	@Getter
	@Setter
	private ProyectoLicenciamientoAmbiental proyecto;
	@Getter
	@Setter
	private PlanSector planSector;
	@Getter
	@Setter
	private List<PlanSector> listaPlanSector;
	@Getter
	@Setter
	private List<DetallePlanManejoAmbientalBean> listaDetallePlan;

	@PostConstruct
	public void init() {
		try {
			if (proyectosBean.getProyecto() != null
					&& proyectosBean.getProyecto().getId() != null) {
				this.proyecto = proyectosBean.getProyecto();
				this.ficha = fichaAmbientalPmaFacade
						.getFichaAmbientalPorIdProyecto(proyectosBean
								.getProyecto().getId());
			} else {
				ficha = new FichaAmbientalPma();
				ficha.setProyectoLicenciamientoAmbiental(proyecto);
				ficha = fichaAmbientalPmaFacade.guardarSoloFicha(ficha);
			}

			if (listaDetallePlan == null) {
				listaDetallePlan = new ArrayList<DetallePlanManejoAmbientalBean>();
			}

			List<DetalleFichaPlan> listaDetalleFichaPlan = detalleFichaPlanFacade
					.buscarDetallePorFicha(ficha.getId());
			if (listaDetalleFichaPlan != null
					&& !listaDetalleFichaPlan.isEmpty()) {
				for (DetalleFichaPlan detallePlan : listaDetalleFichaPlan) {
					DetallePlanManejoAmbientalBean detalle = new DetallePlanManejoAmbientalBean();
					detalle.setId(detallePlan.getId());
					detalle.setPlanSector(detallePlan.getPlanSector());
					detalle.setFichaAmbientalPma(ficha);
					detalle.setFechaFin(detallePlan.getFechaFin());
					detalle.setFechaInicio(detallePlan.getFechaInicio());

					listaDetallePlan.add(detalle);
				}
			} else {
				listaPlanSector = detalleFichaPlanFacade
						.obtenerListaPlanesPorSector(proyecto
								.getCatalogoCategoria().getTipoSubsector()
								.getCodigo());
				for (PlanSector plan : listaPlanSector) {
					DetallePlanManejoAmbientalBean detalle = new DetallePlanManejoAmbientalBean();
					detalle.setPlanSector(plan);
					detalle.setFichaAmbientalPma(ficha);
					listaDetallePlan.add(detalle);
				}
			}
		} catch (Exception e) {
			LOG.error("Error cargando datos", e);
			JsfUtil.addMessageError("No se logró cargar los datos iniciales.");
		}
	}

	public void guardarDetalleFichaPlan() {
		try {
			List<String> errores = detallePmaController
					.guardarDetalleFichaPlan(listaDetallePlan);
			if (errores.isEmpty()) {
				JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
				JsfUtil.redirectTo("/prevencion/categoria2/fichaAmbiental/planManejoAmbiental.jsf");
			}else{
				JsfUtil.addMessageError(errores);
			}
		} catch (Exception e) {
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
			LOG.error(
					":::::::::ERROR AL GUARDAR PLAN MANEJO AMBIENTAL::::::::::",
					e);
		}
	}

	public StreamedContent descargarPlan(PlanSector planSector) {
//		InputStream is = null;
//		try {
//			byte[] plan = detalleFichaPlanFacade.descargarPlanPorSector(
//					planSector.getTipoPlanManejoAmbiental().getCodigo(),
//					planSector.getTipoSubsector().getCodigo());
//
//			if (plan != null) {
//				is = new ByteArrayInputStream(plan);
//				return new DefaultStreamedContent(is, "pdf", planSector
//						.getTipoPlanManejoAmbiental().getCodigo()
//						+ "_"
//						+ planSector.getTipoSubsector().getCodigo() + ".pdf");
//			} else {
//				JsfUtil.addMessageError("No se logró recuperar el plan para este sector.");
//				return null;
//			}
//		} catch (Exception e) {
//			LOG.error("No se logró recuperar el plan para este sector.", e);
//			JsfUtil.addMessageError("No se logró recuperar el plan para este sector.");
			return null;
//		}
	}

}
