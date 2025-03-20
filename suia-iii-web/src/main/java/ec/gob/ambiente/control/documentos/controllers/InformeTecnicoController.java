package ec.gob.ambiente.control.documentos.controllers;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import ec.gob.ambiente.control.documentos.bean.ComentariosBean;
import ec.gob.ambiente.control.documentos.bean.InformeTecnicoBean;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.bandeja.controllers.BandejaTareasController;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.control.documentos.service.InformeTecnicoService;
import ec.gob.ambiente.suia.domain.InformeTecnico;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ViewScoped
@ManagedBean
public class InformeTecnicoController  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8044500024648229674L;

	@Getter
	@Setter
	@ManagedProperty(value = "#{informeTecnicoBean}")
	private InformeTecnicoBean informeTecnicoBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;

	@EJB
	private InformeTecnicoService informeTecnicoService;

	@EJB
	private DocumentosFacade documentosFacade;

	@Getter
	@Setter
	@ManagedProperty(value = "#{comentariosBean}")
	private ComentariosBean comentariosBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;

	private static final Logger LOG = Logger
			.getLogger(BandejaTareasController.class);

	public InformeTecnicoController() {
		System.out.println("Crea controller");
	}

	@PostConstruct
	private void init() {
		try {
			System.out.println("Entra al postconstruct");
			String esEditable = informeTecnicoService.esEditable(
					bandejaTareasBean.getTarea().getTaskId(),
					loginBean.getNombreUsuario(), loginBean.getPassword());
			LOG.error("Booleano obtenido: " + esEditable);
			if (esEditable != null
					&& esEditable.equals(Boolean.toString(false).toUpperCase())) {
				informeTecnicoBean.setHabilitarComentarios(true);
				informeTecnicoBean.setHabilitarEdicion(true);
				informeTecnicoBean.setInformeTecnico(documentosFacade
						.getInformeTecnico(
								bandejaTareasBean.getTarea().getTaskId(),
								loginBean.getUsuario()));
			} else {
				informeTecnicoBean.setHabilitarComentarios(false);
				informeTecnicoBean.setHabilitarEdicion(false);
			}
			comentariosBean.getComentarioTarea().setTipoDocumento(
					InformeTecnico.class.toString());
			comentariosBean.getComentarioTarea().setIdInstanciaProceso(
					bandejaTareasBean.getTarea().getTaskId());
			comentariosBean.getComentarioTarea().setIdDocumento(
					informeTecnicoBean.getInformeTecnico()
							.getId());
		} catch (JbpmException e) {
			LOG.error(e.getMessage());
		}
	}

	public void navegar() {

		try {
			String esEditable = informeTecnicoService.esEditable(
					bandejaTareasBean.getTarea().getTaskId(),
					loginBean.getNombreUsuario(), loginBean.getPassword());
			FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_INFO,
					esEditable, esEditable);
			FacesContext.getCurrentInstance().addMessage("", fm);
		} catch (JbpmException e) {
			LOG.error("Error al iniciar el proceso", e);
		}
	}

	public String completarTarea() {
		try {
			Map<String, Object> parametrosMap = new HashMap<String, Object>();
			parametrosMap.put("tipoActividad_", "estrategico");
			System.out.println("CONTROLLER*******Antecedentes: "
					+ informeTecnicoBean.getInformeTecnico().getAntecedente()
					+ " Observaciones: "
					+ informeTecnicoBean.getInformeTecnico().getObservacion());
			documentosFacade.completarTareaEnviarInformeTecnico(
					informeTecnicoBean.getInformeTecnico(), parametrosMap,
					bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(),
					loginBean.getUsuario());
			JsfUtil.addMessageInfo("Tarea completada");
			return JsfUtil.actionNavigateTo("../../bandeja/bandejaTareas.jsf");
		} catch (JbpmException e) {
			LOG.error(e.getMessage());
			return "#";
		}
	}

	public String completarRevision() {
		try {
			Map<String, Object> parametrosMap = new HashMap<String, Object>();
			documentosFacade.completarRevisionInformeTecnico(comentariosBean
					.getComentarioTarea(), parametrosMap, bandejaTareasBean
					.getTarea().getProcessInstanceId(), informeTecnicoBean
					.getInformeTecnico().getId(), bandejaTareasBean.getTarea().getTaskId(),
					loginBean
							.getUsuario());
			JsfUtil.addMessageInfo("Tarea completada");
			return JsfUtil.actionNavigateTo("../../bandeja/bandejaTareas.jsf");
		} catch (JbpmException e) {
			LOG.error(e.getMessage());
			return "#";
		}
	}
}