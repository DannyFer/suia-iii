package ec.gob.ambiente.control.denuncias.controllers;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import ec.gob.ambiente.control.denuncias.bean.DenunciaIncludeBean;
import ec.gob.ambiente.control.denuncias.bean.SolicitarAcompaniamientoBean;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.denuncia.facade.DenunciaFacade;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.utils.JsfUtil;
import java.io.Serializable;

@ManagedBean
@ViewScoped
public class SolicitarAcompaniamientoController implements Serializable{
    
    private static final long serialVersionUID = -3526371287123619686L;
	@Getter
	@Setter
	@ManagedProperty(value = "#{solicitarAcompaniamientoBean}")
	private SolicitarAcompaniamientoBean solicitarAcompaniamientoBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{denunciaIncludeBean}")
	private DenunciaIncludeBean denunciaIncludeBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;

	@EJB
	private DenunciaFacade denunciaFacade;

	private static final Logger LOG = Logger
			.getLogger(SolicitarAcompaniamientoController.class);

	@PostConstruct
	public void init() {

		try {
			solicitarAcompaniamientoBean.setDenuncia(denunciaFacade
					.getDenuncia(bandejaTareasBean.getTarea().getTaskId(),
							loginBean.getUsuario()));
			denunciaIncludeBean.setDenuncia(solicitarAcompaniamientoBean
					.getDenuncia());

		} catch (JbpmException e) {
			LOG.error("Error al recuperar la denuncia a revisar");
			JsfUtil.addMessageError("Ocurrio un error al recuperar la denuncia");
		}

	}

	public String aceptar() {
		try {
			Map<String, Object> parametros = new HashMap<String, Object>();
			parametros.put("requiereAcompaniamiento_",
					solicitarAcompaniamientoBean.getRequiereApoyo());

			denunciaFacade.completarTarea(parametros, bandejaTareasBean
					.getTarea().getTaskId(), bandejaTareasBean
					.getProcessId(), loginBean.getUsuario());
			JsfUtil.addMessageInfo("Tarea completada");
			return JsfUtil.actionNavigateTo("../../bandeja/bandejaTareas.jsf");
		} catch (JbpmException e) {
			LOG.error("Error al completar la tarea donde se puede solicitar acommaniamiento");
			JsfUtil.addMessageError("Ocurrio un error al recuperar la denuncia");
			return "#";
		}

	}
}
