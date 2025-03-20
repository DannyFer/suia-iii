package ec.gob.ambiente.pronunciamiento.controller;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.pronunciamiento.bean.ElaborarPronunciamientoAreaBean;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.pronunciamiento.facade.PronunciamientoFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;

@RequestScoped
@ManagedBean
public class ElaborarPronunciamientoAreaController implements Serializable {

	private static final long serialVersionUID = -3524836879863L;
	private static final Logger LOGGER = Logger.getLogger(ElaborarPronunciamientoAreaController.class);
	@EJB
	private ProcesoFacade procesoFacade;

	@EJB
	private AreaFacade areaFacade;

	@EJB
	private PronunciamientoFacade pronunciamientoFacade;

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
	@ManagedProperty(value = "#{elaborarPronunciamientoAreaBean}")
	private ElaborarPronunciamientoAreaBean elaborarPronunciamientoAreaBean;

	public String culminarTarea() {

		try {
			elaborarPronunciamientoAreaBean.getPronunciamiento().setFecha(new Date());
			pronunciamientoFacade.saveOrUpdate(elaborarPronunciamientoAreaBean.getPronunciamiento());

			Map<String, Object> data = new ConcurrentHashMap<String, Object>();
			procesoFacade.aprobarTarea(JsfUtil.getBean(LoginBean.class).getUsuario(), bandejaTareasBean.getTarea()
					.getTaskId(), bandejaTareasBean.getProcessId(), data);

			JsfUtil.addMessageInfo("Se realizó correctamente la operación.");
			return JsfUtil.actionNavigateTo("/bandeja/bandejaTareas.jsf");
		} catch (JbpmException e) {
			LOGGER.error(e);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
		return "";
	}

	public void guardar() {

		try {
			pronunciamientoFacade.saveOrUpdate(elaborarPronunciamientoAreaBean.getPronunciamiento());
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);

		} catch (Exception exception) {
			LOGGER.error(exception);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}
}
