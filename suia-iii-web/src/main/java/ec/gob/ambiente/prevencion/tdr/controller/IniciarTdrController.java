package ec.gob.ambiente.prevencion.tdr.controller;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.prevencion.tdr.bean.IngresarTDRBean;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.comun.bean.ReasignarTareaComunBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.entidadResponsable.facade.EntidadResponsableFacade;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.tdr.facade.TdrFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.reportes.DocumentoPDFPlantillaHtml;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@RequestScoped
@ManagedBean
public class IniciarTdrController implements Serializable {
	private static final long serialVersionUID = -352637123838338665L;

	private static final Logger LOGGER = Logger
			.getLogger(IniciarTdrController.class);
	@EJB
	private ProcesoFacade procesoFacade;

	@EJB
	private AreaFacade areaFacade;

	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;

	@EJB(lookup = Constantes.JBPM_EJB_TASK_BEAN)
	private TaskBeanFacade taskBeanFacade;

	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{ingresarTDRBean}")
	private IngresarTDRBean ingresarTDRBean;
	@Getter
	@Setter
	@ManagedProperty(value = "#{ingresarTDRController}")
	private IngresarTDRController ingresarTDRController;

	@EJB
	private TdrFacade tdrFacade;

	@EJB
	EntidadResponsableFacade entidadResponsableFacade;
	@Getter
	@Setter
	@ManagedProperty(value = "#{reasignarTareaComunBean}")
	private ReasignarTareaComunBean reasignarTareaComunBean;

	public void iniciarProceso() {

		if (validarInicioTdr(ingresarTDRBean.getProyectoActivo().getId())) {
			try {
				Map<String, Object> data = new HashMap<String, Object>();

				if (ingresarTDRBean.isCorregir()) {// en el caso de correción
					taskBeanFacade.approveTask(loginBean.getNombreUsuario(),
							bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), data,
							loginBean.getPassword(),
							Constantes.getUrlBusinessCentral(), Constantes.getRemoteApiTimeout(), Constantes.getNotificationService());
					JsfUtil.addMessageInfo("Se realizó correctamente la operacións.");
					JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
				} else {

					try {
						Area areaProyecto = null;
						try {
							areaProyecto = entidadResponsableFacade
									.obtenerEntidadResponsable(ingresarTDRBean
											.getProyectoActivo());
							areaProyecto = areaFacade.getAreaFull(areaProyecto
									.getId());
						} catch (Exception e) {
							LOGGER.error("No seleccionó el área del proyecto.");
						}

						if (areaProyecto == null
								|| areaProyecto
										.getTipoArea()
										.getSiglas()
										.equalsIgnoreCase(
												Constantes.SIGLAS_TIPO_AREA_PC)) {
							Usuario coordinador = areaFacade
									.getCoordinadorControlAmbientalPlantaCentral();

							data.put("u_Controlador", coordinador.getNombre());
							data.put("tipoAreaProyecto",
									Constantes.SIGLAS_TIPO_AREA_PC);
						} else {
							Usuario coordinadorArea = areaFacade
									.getCoordinadorPorArea(areaProyecto);
							data.put("u_Controlador",
									coordinadorArea.getNombre());
							data.put("tipoAreaProyecto", areaProyecto
									.getTipoArea().getSiglas());
							data.put("areaProyectoId", areaProyecto.getId());
						}
						Usuario subsecretario = areaFacade
								.getSubsecretariaCalidadAmbiental();
						data.put("u_SubsecretariaCA", subsecretario.getNombre());

						data.put("idProyecto", ingresarTDRBean
								.getProyectoActivo().getId());

						data.put("proyectoCodigo", ingresarTDRBean
								.getProyectoActivo().getCodigo());

						data.put("u_Promotor", loginBean.getNombreUsuario());

						long idProceso = procesoFacade.iniciarProceso(loginBean.getUsuario(),
								Constantes.NOMBRE_PROCESO_TDR, "TDR-"
										+ ingresarTDRBean.getProyectoActivo()
												.getCodigo(), data);

						File archivoTemporal = DocumentoPDFPlantillaHtml
								.crearPDFConCss("tdr", ingresarTDRController
										.cargarTDRProponente(),
										"tdr_hidrocarburos", true,
										Constantes.ALFRESCO_TDR_FOLDER_NAME);

						tdrFacade.ingresarInformeTdr(archivoTemporal,
								ingresarTDRBean.getTdrEia().getId(), idProceso,
								0);

						JsfUtil.addMessageInfo("Se inició correctamente el proceso de TDR.");
						JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");

					} catch (Exception e) {
						LOGGER.error("Error al iniciar la tarea (TDR)", e);
						JsfUtil.addMessageError("Error al iniciar la tarea (TDR)");
					}
				}

			} catch (JbpmException e) {
				LOGGER.error("Error al iniciar la tarea (TDR)", e);
				JsfUtil.addMessageError("Error al iniciar la tarea (TDR)");
			}
		} else {
			JsfUtil.addMessageError("Proceso de (TDR) iniciado previamente.");
		}
	}

	public boolean validarInicioTdr(Integer idProyecto) {
		return true;

	}

}
