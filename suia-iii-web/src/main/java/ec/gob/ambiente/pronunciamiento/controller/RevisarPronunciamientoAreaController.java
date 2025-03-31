package ec.gob.ambiente.pronunciamiento.controller;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import lombok.Getter;
import lombok.Setter;
import observaciones.ObservacionesController;

import org.apache.log4j.Logger;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.pronunciamiento.bean.RevisarPronunciamientoAreaBean;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.ObservacionesFormularios;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;

@RequestScoped
@ManagedBean
public class RevisarPronunciamientoAreaController implements Serializable {

	private static final long serialVersionUID = 2249941164991744159L;

	private static final Logger LOGGER = Logger.getLogger(RevisarPronunciamientoAreaController.class);

	@EJB
	private ProcesoFacade procesoFacade;

	@Getter
	@Setter
	@ManagedProperty(value = "#{observacionesController}")
	private ObservacionesController observacionesController;

	@Getter
	@Setter
	@ManagedProperty(value = "#{revisarPronunciamientoAreaBean}")
	private RevisarPronunciamientoAreaBean revisarPronunciamientoAreaBean;

	public String iniciarTarea() {

		try {
			Boolean correcto = revisarPronunciamientoAreaBean.getCorrecto();
			if (validarObservaciones(correcto)) {
				Map<String, Object> params = new ConcurrentHashMap<String, Object>();
				params.put("existenObservaciones", !revisarPronunciamientoAreaBean.getCorrecto());
				procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(),
						JsfUtil.getBean(BandejaTareasBean.class).getTarea().getProcessInstanceId(), params);

				Map<String, Object> data = new ConcurrentHashMap<String, Object>();

				procesoFacade.aprobarTarea(JsfUtil.getBean(LoginBean.class).getUsuario(),
						JsfUtil.getBean(BandejaTareasBean.class).getTarea().getTaskId(),
						JsfUtil.getBean(BandejaTareasBean.class).getProcessId(), data);
				JsfUtil.addMessageInfo("Se realizó correctamente la operación.");
				return JsfUtil.actionNavigateTo("/bandeja/bandejaTareas.jsf");
			} else {
				return "";
			}
		} catch (JbpmException e) {
			LOGGER.error(e);
			JsfUtil.addMessageError("Ocurrio un error al enviar la información.");
		}

		return "";
	}

	public void validarTareaBpm() {
		JsfUtil.validarPaginasUrlTareasBpm(JsfUtil.getBean(BandejaTareasBean.class).getTarea(),
				"/pronunciamiento/revisarPronunciamientoArea.jsf?area=" + revisarPronunciamientoAreaBean.getArea());
	}

	public Boolean validarObservaciones(Boolean estado) {
		List<ObservacionesFormularios> observaciones = observacionesController.getObservacionesBB().getMapaSecciones()
				.get(revisarPronunciamientoAreaBean.getArea());

		if (estado) {
			for (ObservacionesFormularios observacion : observaciones) {
				if (!observacion.isObservacionCorregida()) {

					JsfUtil.addMessageError("Existen observaciones sin corregir. Por favor rectifique los datos.");
					return false;
				}
			}
		} else {
			int posicion = 0;
			int cantidad = observaciones.size();
			Boolean encontrado = false;
			while (!encontrado && posicion < cantidad) {
				if (!observaciones.get(posicion++).isObservacionCorregida()) {
					encontrado = true;
				}
			}
			if (!encontrado) {
				JsfUtil.addMessageError("No existen observaciones sin corregir. Por favor rectifique los datos.");
				return false;
			}
		}
		return true;
	}
}
