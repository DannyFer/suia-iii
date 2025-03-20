package ec.gob.ambiente.control.aprobacionrequisitostecnicos.controllers;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import ec.gob.ambiente.control.aprobacionrequisitostecnicos.bean.AprobacionRequisitosTecnicosBean;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.AprobacionRequisitosTecnicosFacade;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.AprobacionRequisitosTecnicos;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class RecibirTramiteTecnicoController implements Serializable {

	private static final long serialVersionUID = 1496583714884078545L;

	private static final Logger LOG = Logger.getLogger(RecibirTramiteTecnicoController.class);

	@Setter
	@Getter
	private AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos;

	@Setter
	@Getter
	@ManagedProperty(value = "#{aprobacionRequisitosTecnicosBean}")
	private AprobacionRequisitosTecnicosBean aprobacionRequisitosTecnicosBean;

	@EJB
	private AprobacionRequisitosTecnicosFacade aprobacionRequisitosTecnicosFacade;

	@EJB
	private ProcesoFacade procesoFacade;

	@PostConstruct
	public void init() {

		try {
                    aprobacionRequisitosTecnicos =aprobacionRequisitosTecnicosFacade
					.recuperarCrearAprobacionRequisitosTecnicos(JsfUtil.getBean(BandejaTareasBean.class).getProcessId(),
							JsfUtil.getLoggedUser());                    			
		} catch (Exception e) {
			LOG.error("Error al cargar el requisito tecnico", e);
			JsfUtil.addMessageError("No se pudo obtener los datos iniciales.");
		}
	}

	public String aceptar() {
		try {			
                        aprobacionRequisitosTecnicosFacade.enviarAprobacionRequisitosTecnicos(JsfUtil.getBean(BandejaTareasBean.class).getTarea()
							.getProcessInstanceId(), JsfUtil.getLoggedUser(), JsfUtil.getBean(BandejaTareasBean.class).getTarea().getTaskId());
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
			return JsfUtil.actionNavigateTo(JsfUtil.NAVIGATION_TO_BANDEJA);
		} catch (JbpmException e) {
			LOG.debug("Error completando tarea actual de requisitos tecnicos", e);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
			return "";
		}
	}
}
