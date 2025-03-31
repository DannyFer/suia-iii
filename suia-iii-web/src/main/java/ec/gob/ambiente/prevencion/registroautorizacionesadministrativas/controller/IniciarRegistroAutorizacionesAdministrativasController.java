package ec.gob.ambiente.prevencion.registroautorizacionesadministrativas.controller;

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
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;

@RequestScoped
@ManagedBean
public class IniciarRegistroAutorizacionesAdministrativasController {

	@EJB
	private ProcesoFacade procesoFacade;

	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;

	private static final Logger LOG = Logger
			.getLogger(IniciarRegistroAutorizacionesAdministrativasController.class);

	public void iniciarProceso() {
		try {

			String sujetoControl = loginBean.getNombreUsuario();
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("u_ente_acreditado_gads", sujetoControl);
			//
			procesoFacade.iniciarProceso(loginBean.getUsuario(),
					"registroAutorizacionesAdministrativas",
					"Registro de autorizaciones Administrativas-Tramite",
					data);

			JsfUtil.addMessageInfo("Se inició correctamente el programa de remediación.");
			JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");

		} catch (JbpmException e) {
			LOG.error(
					"Error al iniciar la tarea (Registro de autorizaciones Administrativas)",
					e);
			JsfUtil.addMessageError("Error al iniciar la tarea (Registro de autorizaciones Administrativas)");
		}

	}

}
