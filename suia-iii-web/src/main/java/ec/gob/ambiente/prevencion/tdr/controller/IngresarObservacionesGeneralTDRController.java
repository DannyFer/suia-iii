/*
 * Copyright 2014 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.prevencion.tdr.controller;

import java.io.File;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.prevencion.tdr.bean.ElaborarInformeTecnicoAreaBean;
import ec.gob.ambiente.prevencion.tdr.bean.IngresarTDRBean;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.ObservacionTdrEiaLiciencia;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.TdrEiaLicencia;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.tdr.facade.TdrFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.reportes.DocumentoPDFPlantillaHtml;
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
public class IngresarObservacionesGeneralTDRController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2455721599348572978L;

	private static final Logger LOGGER = Logger
			.getLogger(IngresarObservacionesGeneralTDRController.class);

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

	@Getter
	@Setter
	@ManagedProperty(value = "#{elaborarInformeTecnicoAreaBean}")
	private ElaborarInformeTecnicoAreaBean elaborarInformeTecnicoAreaBean;

	@EJB
	private TdrFacade tdrFacade;

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

		try {
			tdrFacade.saveOrUpdate(ingresarTDRBean
					.getObservacionTdrEiaLiciencia());
			if (this.validarObservaciones()) {
				RequestContext context = RequestContext.getCurrentInstance();
				context.execute("PF('enviarObservaciones').show();");
			}
			JsfUtil.addMessageInfo("Se guardó correctamenta la información.");
		} catch (Exception e) {
			LOGGER.info("Error al guardarObservacion", e);
		}
	}

	public void guardarObservacionesGeneral() {
		try {			
			tdrFacade.saveOrUpdate(elaborarInformeTecnicoAreaBean.getTdrEia());
			elaborarInformeTecnicoAreaBean.guardarInformeTecnico(elaborarInformeTecnicoAreaBean.getObservacionTdrEiaLiciencia());
			JsfUtil.addMessageInfo("Se guardó correctamenta la información.");
		} catch (Exception e) {
			LOGGER.error("Error al guardar Observaciones Generales", e);
		}
	}

	public Boolean validarObservacionesGeneral() {
		try {
			if (elaborarInformeTecnicoAreaBean.getObservacionTdrEiaLiciencia()
					.getConclusionesRecomendaciones() != null
					&& elaborarInformeTecnicoAreaBean
							.getObservacionTdrEiaLiciencia().getObservacion() != null
					&& elaborarInformeTecnicoAreaBean
							.getObservacionTdrEiaLiciencia()
							.getEvaluacionTecnicaGeneral() != null
					&& elaborarInformeTecnicoAreaBean
							.getObservacionTdrEiaLiciencia()
							.getCaracteristicasImportantesProyecto() != null
					&& elaborarInformeTecnicoAreaBean
							.getObservacionTdrEiaLiciencia().getObjetivos() != null

					&& !elaborarInformeTecnicoAreaBean
							.getObservacionTdrEiaLiciencia()
							.getConclusionesRecomendaciones().isEmpty()
					&& !elaborarInformeTecnicoAreaBean
							.getObservacionTdrEiaLiciencia().getObservacion()
							.isEmpty()
					&& !elaborarInformeTecnicoAreaBean
							.getObservacionTdrEiaLiciencia()
							.getEvaluacionTecnicaGeneral().isEmpty()
					&& !elaborarInformeTecnicoAreaBean
							.getObservacionTdrEiaLiciencia()
							.getCaracteristicasImportantesProyecto().isEmpty()
					&& !elaborarInformeTecnicoAreaBean
							.getObservacionTdrEiaLiciencia().getObjetivos()
							.isEmpty()) {
				return true;
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}

	public String enviarObservacionesGeneral() {
		Map<String, Object> data = new ConcurrentHashMap<String, Object>();

		try {
			if (elaborarInformeTecnicoAreaBean.getConsolidado()) {
				Map<String, Object> params = new ConcurrentHashMap<String, Object>();
				params.put("cumpleCriterios",
						elaborarInformeTecnicoAreaBean.getCumpleCriterios());
				procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean
						.getTarea().getProcessInstanceId(), params);
				String codigo = "000";
				File archivoTemporal = DocumentoPDFPlantillaHtml
						.crearPDFConCss(
								"tdr",
								cargarTDRArea(
										elaborarInformeTecnicoAreaBean
												.getTdrEia().getProyecto()
												.getId(),
										elaborarInformeTecnicoAreaBean
												.getArea(),
										elaborarInformeTecnicoAreaBean
												.getObservacionTdrEiaLiciencia(),
										codigo), "tdr_informe_tecnico_areas",
								true, "TDR");
				try {
					tdrFacade.ingresarInformeTdrArea(archivoTemporal,
							elaborarInformeTecnicoAreaBean.getTdrEia()
									.getProyecto().getCodigo(),
							elaborarInformeTecnicoAreaBean.getTdrEia().getId(),
							elaborarInformeTecnicoAreaBean.getArea(),
							bandejaTareasBean.getProcessId(), bandejaTareasBean
									.getTarea().getTaskId());

				} catch (Exception e) {
				}
				// return "";
			} else {
				String codigo = tdrFacade.obtenerSecuencial();
				File archivoTemporal = DocumentoPDFPlantillaHtml
						.crearPDFConCss(
								"tdr",
								cargarTDRArea(
										elaborarInformeTecnicoAreaBean
												.getTdrEia().getProyecto()
												.getId(),
										elaborarInformeTecnicoAreaBean
												.getArea(),
										elaborarInformeTecnicoAreaBean
												.getObservacionTdrEiaLiciencia(),
										codigo), "tdr_informe_tecnico_areas",
								true, "TDR");
				try {
					tdrFacade.ingresarInformeTdrArea(archivoTemporal,
							elaborarInformeTecnicoAreaBean.getTdrEia()
									.getProyecto().getCodigo(),
							elaborarInformeTecnicoAreaBean.getTdrEia().getId(),
							elaborarInformeTecnicoAreaBean.getArea(),
							bandejaTareasBean.getProcessId(), bandejaTareasBean
									.getTarea().getTaskId());

				} catch (Exception e) {
				}

			}

			taskBeanFacade.approveTask(loginBean.getNombreUsuario(),
					bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), data,
					loginBean.getPassword(),
					Constantes.getUrlBusinessCentral(), Constantes.getRemoteApiTimeout(), Constantes.getNotificationService());
			// JsfUtil.addMessageInfo("Pendiente");
			JsfUtil.addMessageInfo("Se realizó correctamente la operación.");
			return JsfUtil.actionNavigateTo("/bandeja/bandejaTareas.jsf");

		} catch (JbpmException e) {
			LOGGER.error(e);
		}

		return "";
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

	public String[] cargarTDRArea(Integer tdrId, String area,
			ObservacionTdrEiaLiciencia observacionTdrEiaLiciencia,
			String numeroInforme) {

		Calendar calendario = new GregorianCalendar();
		calendario.setTime(new Date());
		String year = Integer.toString(calendario.get(Calendar.YEAR));

		String iniciales = "";
		List<String> nombreTecnico = Arrays.asList(loginBean.getUsuario()
				.getPersona().getNombre().split("\\s+"));
		for (String nombre : nombreTecnico) {
			if (!nombre.isEmpty())
				iniciales += nombre.toUpperCase().charAt(0);
		}
		String direccion = "DNPCA";
		String codigo = "No. 000-" + numeroInforme + "-" + iniciales + "-"
				+ area + "-" + direccion + "-" + year;

		TdrEiaLicencia tdrFull = tdrFacade
				.getTdrEiaLicenciaPorIdProyectoFull(tdrId);
		String[] parametros = new String[29];
		Integer i = 0;
		parametros[i++] = codigo;
		parametros[i++] = tdrFull.getProyecto().getCodigo();
		parametros[i++] = tdrFull.getProyecto().getNombre();
		parametros[i++] = tdrFull.getProyecto().getCatalogoCategoria().getFase() == null ? ""
				: ingresarTDRBean.getTdrEia().getProyecto().getCatalogoCategoria()
						.getFase().getNombre();
		parametros[i++] = new Double(tdrFull.getProyecto().getArea())
				.toString() + " " + tdrFull.getProyecto().getUnidad();
		parametros[i++] = tdrFull.getProyecto().getUsuario().getPersona()
				.getOrganizaciones().size() > 0 ? tdrFull.getProyecto()
				.getUsuario().getPersona().getOrganizaciones().get(0)
				.getNombre() : "";
		parametros[i++] = tdrFull.getProyecto().getUsuario().getPersona()
				.getOrganizaciones().size() > 0 ? tdrFull.getProyecto()
				.getUsuario().getPersona().getOrganizaciones().get(0)
				.getPersona().getNombre() : "";
		parametros[i++] = tdrFull.getTdelExecutionTime().toString();
		parametros[i++] = tdrFull.getProyecto()
				.getProyectoUbicacionesGeograficas().size() > 0 ? DocumentoPDFPlantillaHtml
				.crearTablaUbicacion(tdrFull.getProyecto()
						.getProyectoUbicacionesGeograficas()) : "";
		parametros[i++] = tdrFull.getProyecto().getProyectoBloques().size() > 0 ? DocumentoPDFPlantillaHtml
				.crearTablaBloques(tdrFull.getProyecto().getProyectoBloques())
				: "";
		Organizacion organizacion = tdrFull.getProyecto().getUsuario()
				.getPersona().getOrganizaciones().get(0);
		// Ubicacion
		if (tdrFull.getProyecto().getUsuario().getPersona()
				.getUbicacionesGeografica() != null) {
			parametros[i++] = tdrFull.getProyecto().getUsuario().getPersona()
					.getUbicacionesGeografica().getNombre();
			parametros[i++] = tdrFull.getProyecto().getUsuario().getPersona()
					.getUbicacionesGeografica().getUbicacionesGeografica()
					.getNombre();
			parametros[i++] = tdrFull.getProyecto().getUsuario().getPersona()
					.getUbicacionesGeografica().getUbicacionesGeografica()
					.getUbicacionesGeografica().getNombre();
		} else {
			i += 3;
		}
		for (Contacto c : organizacion.getContactos()) {
			if (c.getFormasContacto().getId() == 4) {// Direccion
				parametros[i] = c.getValor();
			} else if (c.getFormasContacto().getId() == 1) {// telefono
				parametros[i + 1] = c.getValor();
			} else if (c.getFormasContacto().getId() == 6) {// Email
				parametros[i + 2] = c.getValor();
			}
		}
		i += 3;
		// organizacion.getIdUbicacionGeografica()

		parametros[i++] = tdrFull.getEquipoTecnico().size() > 0 ? DocumentoPDFPlantillaHtml
				.crearTabaEquipoTecnico(tdrFull.getEquipoTecnico()) : "";

		//
		parametros[i++] = observacionTdrEiaLiciencia.getAntecedentes();

		parametros[i++] = observacionTdrEiaLiciencia.getObjetivos(); // Objetivos
		parametros[i++] = observacionTdrEiaLiciencia
				.getCaracteristicasImportantesProyecto();
		parametros[i++] = observacionTdrEiaLiciencia
				.getEvaluacionTecnicaGeneral();
		parametros[i++] = observacionTdrEiaLiciencia.getObservacion();
		parametros[i++] = observacionTdrEiaLiciencia
				.getConclusionesRecomendaciones();

		parametros[i++] = loginBean.getUsuario().getPersona().getNombre();// equipo
		// técnico
		return parametros;

	}
}
