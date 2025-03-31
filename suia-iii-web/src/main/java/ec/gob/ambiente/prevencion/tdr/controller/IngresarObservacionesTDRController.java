/*
 * Copyright 2014 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.prevencion.tdr.controller;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import lombok.Getter;
import lombok.Setter;

import org.primefaces.context.RequestContext;

import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.prevencion.tdr.bean.IngresarTDRBean;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author Frank Torres
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Frank Torres, Fecha: 22/01/2015]
 *          </p>
 */
@RequestScoped
@ManagedBean
public class IngresarObservacionesTDRController implements Serializable {

	private static final long serialVersionUID = -352637137373737399L;

	@ManagedProperty(value = "#{loginBean}")
	@Setter
	private LoginBean loginBean;

	@EJB
	private ProcesoFacade procesoFacade;

	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{ingresarTDRBean}")
	private IngresarTDRBean ingresarTDRBean;

	
	@EJB
	CrudServiceBean crudServiceBean;

	@EJB(lookup = Constantes.JBPM_EJB_TASK_BEAN)
	private TaskBeanFacade taskBeanFacade;

	public void guardarObservacion(Integer formulario) {

		switch (formulario) {
		case 1:

			if (!ingresarTDRBean.getObservacionTdrEiaLiciencia()
					.getObs_fichaTecnica()) {
				ingresarTDRBean.getObservacionTdrEiaLiciencia()
						.setFichaTecnica("");
			}
			break;

		case 2:

			if (!ingresarTDRBean.getObservacionTdrEiaLiciencia()
					.getObs_introduccion()) {
				ingresarTDRBean.getObservacionTdrEiaLiciencia()
						.setIntroduccion("");
			}
			break;
		case 3:

			if (!ingresarTDRBean.getObservacionTdrEiaLiciencia()
					.getObs_diagnosticoAmbientalLineaBase()) {
				ingresarTDRBean.getObservacionTdrEiaLiciencia()
						.setDiagnosticoAmbientalLineaBase("");
			}
			break;
		case 4:

			if (!ingresarTDRBean.getObservacionTdrEiaLiciencia()
					.getObs_descripcionProyecto()) {
				ingresarTDRBean.getObservacionTdrEiaLiciencia()
						.setDescripcionProyecto("");
			}
			break;
		case 5:

			if (!ingresarTDRBean.getObservacionTdrEiaLiciencia()
					.getObs_determinacionAreaInfluencia()) {
				ingresarTDRBean.getObservacionTdrEiaLiciencia()
						.setDeterminacionAreaInfluencia("");
			}
			break;
		case 6:

			if (!ingresarTDRBean.getObservacionTdrEiaLiciencia()
					.getObs_identificacionEvaluacionValoracion()) {
				ingresarTDRBean.getObservacionTdrEiaLiciencia()
						.setIdentificacionEvaluacionValoracion("");
			}
			break;

		case 7:

			if (!ingresarTDRBean.getObservacionTdrEiaLiciencia()
					.getObs_identificacionSitiosContaminados()) {
				ingresarTDRBean.getObservacionTdrEiaLiciencia()
						.setIdentificacionSitiosContaminados("");
			}
			break;

		case 8:

			if (!ingresarTDRBean.getObservacionTdrEiaLiciencia()
					.getObs_planManejoAmbiental()) {
				ingresarTDRBean.getObservacionTdrEiaLiciencia()
						.setPlanManejoAmbiental("");
			}
			break;

		case 9:

			if (!ingresarTDRBean.getObservacionTdrEiaLiciencia()
					.getObs_planMonitoreo()) {
				ingresarTDRBean.getObservacionTdrEiaLiciencia()
						.setPlanMonitoreo("");
			}
			break;

		case 10:

			if (!ingresarTDRBean.getObservacionTdrEiaLiciencia()
					.getObs_inventarioForestal()) {
				ingresarTDRBean.getObservacionTdrEiaLiciencia()
						.setInventarioForestal("");
			}
			break;

		case 11:

			if (!ingresarTDRBean.getObservacionTdrEiaLiciencia()
					.getObs_anexos()) {
				ingresarTDRBean.getObservacionTdrEiaLiciencia().setAnexos("");
			}
			break;

		default:
			break;
		}

		crudServiceBean.saveOrUpdate(ingresarTDRBean
				.getObservacionTdrEiaLiciencia());
		if (this.validarObservaciones()) {
			RequestContext context = RequestContext.getCurrentInstance();
			context.execute("PF('enviarObservaciones').show();");
		}

		JsfUtil.addMessageInfo("Se guardó correctamenta la información.");
	}

	
	

	public Boolean validarObservaciones() {
		if (!ingresarTDRBean.isProcesoIniciado()) {
			return false;
		}
		if (ingresarTDRBean.getObservacionTdrEiaLiciencia()
				.getObs_fichaTecnica() == null
				|| ingresarTDRBean.getObservacionTdrEiaLiciencia()
						.getObs_introduccion() == null
				|| ingresarTDRBean.getObservacionTdrEiaLiciencia()
						.getObs_diagnosticoAmbientalLineaBase() == null
				|| ingresarTDRBean.getObservacionTdrEiaLiciencia()
						.getObs_descripcionProyecto() == null
				|| ingresarTDRBean.getObservacionTdrEiaLiciencia()
						.getObs_determinacionAreaInfluencia() == null
				|| ingresarTDRBean.getObservacionTdrEiaLiciencia()
						.getObs_identificacionEvaluacionValoracion() == null
				|| ingresarTDRBean.getObservacionTdrEiaLiciencia()
						.getObs_identificacionSitiosContaminados() == null
				|| ingresarTDRBean.getObservacionTdrEiaLiciencia()
						.getObs_planManejoAmbiental() == null
				|| ingresarTDRBean.getObservacionTdrEiaLiciencia()
						.getObs_planMonitoreo() == null
				|| ingresarTDRBean.getObservacionTdrEiaLiciencia()
						.getObs_inventarioForestal() == null
				|| ingresarTDRBean.getObservacionTdrEiaLiciencia()
						.getObs_anexos() == null
		/*
		 * || ingresarTDRBean.getObservacionTdrEiaLiciencia()
		 * .getObs_caracteristicasImportantesProyecto() == null ||
		 * ingresarTDRBean.getObservacionTdrEiaLiciencia()
		 * .getObs_evaluacionTecnica() == null ||
		 * ingresarTDRBean.getObservacionTdrEiaLiciencia()
		 * .getObs_antecedentes() == null
		 */) {
			return false;
		}

		return true;
	}

	public void enviarObservaciones() {
		if (this.validarObservaciones()) {
			// <f:viewParam name="area" value="#{ingresarTDRBean.area}" />
			JsfUtil.redirectTo("/prevencion/tdr/elaborarInformeTecnicoArea.jsf?area="
					+ ingresarTDRBean.getArea().trim());
			// Map<String, Object> data = new ConcurrentHashMap<String,
			// Object>();
			//
			// try {
			// taskBeanFacade.approveTask(loginBean.getNombreUsuario(),
			// bandejaTareasBean.getTarea().getTaskId(), data,
			// Constantes.getDeploymentId(), loginBean.getPassword(),
			// Constantes.getUrlBusinessCentral());
			// JsfUtil.addMessageInfo("Se realizó correctamente la operación.");
			// return JsfUtil.actionNavigateTo("/bandeja/bandejaTareas.jsf");
			//
			// } catch (JbpmException e) {
			// LOGGER.error(e);
			// }
		}

	}

}
