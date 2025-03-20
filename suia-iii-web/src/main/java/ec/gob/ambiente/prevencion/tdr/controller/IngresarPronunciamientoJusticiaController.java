package ec.gob.ambiente.prevencion.tdr.controller;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.prevencion.tdr.bean.IngresarPronunciamientoJusticiaBean;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.TdrEiaLicencia;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.tdr.facade.TdrFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@RequestScoped
@ManagedBean
public class IngresarPronunciamientoJusticiaController {

	private static final Logger LOGGER = Logger
			.getLogger(IngresarPronunciamientoJusticiaController.class);

	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;

	@EJB(lookup = Constantes.JBPM_EJB_TASK_BEAN)
	private TaskBeanFacade taskBeanFacade;

	@EJB
	private ProcesoFacade procesoFacade;

	@Setter
	@Getter
	private ProyectoLicenciamientoAmbiental proyectoActivo;

	@Getter
	@Setter
	@ManagedProperty(value = "#{ingresarPronunciamientoJusticiaBean}")
	private IngresarPronunciamientoJusticiaBean ingresarPronunciamientoJusticiaBean;

	@EJB
	private TdrFacade tdrFacade;

	public void uploadListener(FileUploadEvent event) {

		ingresarPronunciamientoJusticiaBean.setPronunciamiento(JsfUtil
				.upload(event));
		ingresarPronunciamientoJusticiaBean
				.setPronunciamientoNombre(ingresarPronunciamientoJusticiaBean
						.getPronunciamiento().getName());

	}

	public String enviarDatos() {

		try {
			// seleccionar el proyecto del proceso activo
			Map<String, Object> variables = procesoFacade
					.recuperarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getTarea()
							.getProcessInstanceId());

			Integer idProyecto = Integer.parseInt((String) variables
					.get(Constantes.ID_PROYECTO));
			if (idProyecto != null) {
				TdrEiaLicencia tdrEia = tdrFacade
						.getTdrEiaLicenciaPorIdProyecto(idProyecto);
				if (tdrEia != null) {
					String folderFileName = "PronunciamientoMJ";
					tdrFacade.eliminarDocumentoTdr(folderFileName,
							tdrEia.getId(), 1);

					// insertar adjunto
					tdrFacade.ingresarTdrAdjunto(
							ingresarPronunciamientoJusticiaBean
									.getPronunciamiento(), tdrEia.getId(),
							folderFileName, bandejaTareasBean.getProcessId(),
							bandejaTareasBean.getTarea().getTaskId());

					Map<String, Object> params = new ConcurrentHashMap<String, Object>();
					try {
						procesoFacade.modificarVariablesProceso(loginBean.getUsuario(),
								bandejaTareasBean.getTarea()
										.getProcessInstanceId(), params);
						Map<String, Object> data = new ConcurrentHashMap<String, Object>();
						taskBeanFacade.approveTask(
								loginBean.getNombreUsuario(), bandejaTareasBean
										.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), data, loginBean
										.getPassword(), Constantes
										.getUrlBusinessCentral(), Constantes.getRemoteApiTimeout(), Constantes.getNotificationService());

						JsfUtil.addMessageInfo("Se realizó correctamente la operación.");
						return JsfUtil
								.actionNavigateTo("/bandeja/bandejaTareas.jsf");
					} catch (JbpmException e) {
						LOGGER.error(e);
						JsfUtil.addMessageError("Error al realizar la operación.");
					}
				}
			}

		} catch (Exception e) {
			LOGGER.error("Error al subir el pronunciamiento", e);
			JsfUtil.addMessageError("Error al subir el pronunciamiento al servidor.");
		}

		return "";
	}
}
