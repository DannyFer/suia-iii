package ec.gob.ambiente.control.programasremediacion.controllers;

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
public class IniciarProgramaRemediacionController {

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
			.getLogger(IdentificarProyectoController.class);

	public void iniciarProceso() {
		try {

			String sujetoControl = loginBean.getNombreUsuario();
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("u_sujetoControl", sujetoControl);
			//
			Long processInstanceID = procesoFacade.iniciarProceso(loginBean.getUsuario(),
					"programaRemediacion", "PR-Tramite", data);

			Map<String, Object> params = new HashMap<String, Object>();
			Map<String, Object> params1 = new HashMap<String, Object>();
			params1.put("valor", "valor1");

			params.put("prueba", params1);
			procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), processInstanceID, params);

			JsfUtil.addMessageInfo("Se inició correctamente el programa de remediación.");
			JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");

		} catch (JbpmException e) {
			LOG.error(
					"Error al iniciar la tarea de identificar proyecto del programa de remediación",
					e);
			JsfUtil.addMessageError("Ocurrio un error al enviar la información, intente mas tarde.");
		}

	}

}
