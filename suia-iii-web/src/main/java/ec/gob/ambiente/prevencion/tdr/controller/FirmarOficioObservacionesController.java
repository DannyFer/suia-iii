package ec.gob.ambiente.prevencion.tdr.controller;

import java.io.Serializable;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.TdrEiaLicencia;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.tdr.facade.TdrFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.DigitalSign;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class FirmarOficioObservacionesController implements Serializable {
	private static final Logger LOGGER = Logger
			.getLogger(RevisarDocumentacionGeneralController.class);
	private static final long serialVersionUID = -3526371393838393L;

	@EJB
	private ProcesoFacade procesoFacade;

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

	@Setter
	@Getter
	TdrEiaLicencia tdrEia;

	@EJB
	private TdrFacade tdrFacade;
	String documentOffice = "";

	@Getter
	@Setter
	private String area = "";

	@PostConstruct
	public void init() {

		// seleccionar el proyecto del proceso activo
		Map<String, Object> variables;
		try {
			Map<String, String> params = FacesContext.getCurrentInstance()
					.getExternalContext().getRequestParameterMap();
			String area_tmp = params.get("area");

			if (area_tmp != null && !area_tmp.isEmpty()) {
				area = area_tmp;

			} else {
				area = "Consolidado";
			}

			variables = procesoFacade.recuperarVariablesProceso(loginBean.getUsuario(),
					bandejaTareasBean.getTarea().getProcessInstanceId());
			Integer idProyecto = Integer.parseInt((String) variables
					.get(Constantes.ID_PROYECTO));
			if (idProyecto != null) {
				tdrEia = tdrFacade.getTdrEiaLicenciaPorIdProyecto(idProyecto);
				try {
					documentOffice = tdrFacade
							.recuperarInformeTdrAreaDocumento(tdrEia.getId(),
									area);

				} catch (Exception e) {
					LOGGER.error("Error al cargar los datos.", e);
					JsfUtil.addMessageError("Error al cargar los datos.");
				}
			}
		} catch (JbpmException e) {
			LOGGER.error("Error al cargar los datos.", e);
			JsfUtil.addMessageError("Error al cargar los datos.");
		}

	}

	public String firmarDocumento() {
		System.out.println(documentOffice);
		// String documentOffice =
		// "http://desa-alfresco.ambiente.gob.ec:8101/alfresco/service/api/node/workspace/SpacesStore/2613bcae-3896-4afa-ad19-9420265f02ae/content?alf_ticket=TICKET_173a241a156c9a573e07ee84554e4439045e0a63";
		try {
			DigitalSign firmaE = new DigitalSign();
			return firmaE.sign(documentOffice, "1721076097"); // loginBean.getUsuario()
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
	}

	public String completarTarea() {
		try {
			taskBeanFacade.approveTask(loginBean.getNombreUsuario(),
					bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null,
					loginBean.getPassword(),
					Constantes.getUrlBusinessCentral(), Constantes.getRemoteApiTimeout(), Constantes.getNotificationService());

			JsfUtil.addMessageInfo("Se realizó correctamente la operación.");
			return JsfUtil.actionNavigateTo("/bandeja/bandejaTareas.jsf");
		} catch (JbpmException e) {
			LOGGER.error(e);
			JsfUtil.addMessageError("Error al realizar la operación.");
		}
		return "";
	}
}