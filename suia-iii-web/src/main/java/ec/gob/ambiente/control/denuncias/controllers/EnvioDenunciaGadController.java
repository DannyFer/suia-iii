package ec.gob.ambiente.control.denuncias.controllers;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import ec.gob.ambiente.control.denuncias.bean.DenunciaIncludeBean;
import ec.gob.ambiente.control.denuncias.bean.EnviarDenunciaGadBean;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.denuncia.facade.DenunciaFacade;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.utils.JsfUtil;
import java.io.Serializable;

@ManagedBean
@ViewScoped
public class EnvioDenunciaGadController implements Serializable{
    
    private static final long serialVersionUID = -3526671287113629686L;

	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{denunciaIncludeBean}")
	private DenunciaIncludeBean denunciaIncludeBean;

	@EJB
	private DenunciaFacade denunciaFacade;

	@Getter
	@Setter
	@ManagedProperty(value = "#{enviarDenunciaGadBean}")
	private EnviarDenunciaGadBean enviarDenunciaGadBean;

	private static final Logger LOG = Logger
			.getLogger(EnvioDenunciaGadController.class);

	@PostConstruct
	public void init() {

		try {

			denunciaIncludeBean.setDenuncia(denunciaFacade.getDenuncia(
					bandejaTareasBean.getTarea().getTaskId(),
					loginBean.getUsuario()));

			enviarDenunciaGadBean.setEnteAcreditado(denunciaFacade
					.getVariableEnteAcreditado(bandejaTareasBean.getTarea().getTaskId(),
							loginBean.getUsuario()));

		} catch (JbpmException e) {
			LOG.error("Error al recuperar la denuncia a revisar");
			JsfUtil.addMessageError("Ocurrio un error al recuperar la denuncia");
		}

	}

	public String completarTarea() {
		try {
			denunciaFacade.completarTarea(null, bandejaTareasBean.getTarea()
					.getTaskId(), bandejaTareasBean
					.getProcessId(), loginBean.getUsuario());
			JsfUtil.addMessageInfo("Tarea completada");
			return JsfUtil.actionNavigateTo("../../bandeja/bandejaTareas.jsf");
		} catch (JbpmException e) {
			LOG.error("Error al finalizar la tarea de envio de denuncia");
			JsfUtil.addMessageError("Ocurrio un error al recuperar la denuncia");
			return "#";
		}

	}

}
