package ec.gob.ambiente.control.denuncias.controllers;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import ec.gob.ambiente.core.interfaces.CompleteOperation;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.bandeja.controllers.BandejaTareasController;
import ec.gob.ambiente.suia.comun.bean.ReasignarTareaComunBean;
import ec.gob.ambiente.suia.control.denuncia.facade.DenunciaFacade;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.utils.JsfUtil;
import java.io.Serializable;

@RequestScoped
@ManagedBean
public class AsignarTecnicoAcompaniamientoInspeccionController implements Serializable{
    
    private static final long serialVersionUID = -3526171287113629686L;

	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{reasignarTareaComunBean}")
	private ReasignarTareaComunBean reasignarTareaComunBean;

	@EJB
	private DenunciaFacade denunciaFacade;

	@Getter
	@Setter
	private String a;

	private static final Logger LOG = Logger
			.getLogger(BandejaTareasController.class);

	@PostConstruct
	private void init() {
		a = "aaa";
		reasignarTareaComunBean.initFunctionOnNotStatartedTask(
				bandejaTareasBean.getTarea().getProcessInstanceId(),
				"Acompaniamiento a inspeccion", "tecnicoAcompaniamientoInspeccion",
				"tecnicoProvincial", null,
				"/bandeja/bandejaTareas.jsf",false ,new CompleteOperation() {

					public Object endOperation(Object object) {

						try {
							denunciaFacade.completarTarea(null,
									bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean
									.getProcessId(),
									loginBean.getUsuario());
							JsfUtil.addMessageInfo("Tarea completada");

						} catch (JbpmException e) {
							LOG.error("Error al completar la tarea de asignar técnico");							
							JsfUtil.addMessageError("Ocurrio un error al completar la tarea, intente mas tarde");

						}

						return null;
					}
				});
	}
}
