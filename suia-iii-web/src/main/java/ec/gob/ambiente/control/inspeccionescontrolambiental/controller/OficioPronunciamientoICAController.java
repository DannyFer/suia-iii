package ec.gob.ambiente.control.inspeccionescontrolambiental.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;

import ec.gob.ambiente.control.inspeccionescontrolambiental.bean.DocumentoICABean;
import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.suia.comun.bean.SendCopyBean;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;

@ManagedBean
@ViewScoped
public class OficioPronunciamientoICAController implements Serializable {

	private static final long serialVersionUID = 7329420844087609082L;

	private final Logger LOG = Logger.getLogger(OficioPronunciamientoICAController.class);

	@EJB
	protected ProcesoFacade procesoFacade;

	@ManagedProperty(value = "#{documentoICABean}")
	@Getter
	@Setter
	private DocumentoICABean documentoICABean;

	@PostConstruct
	public void init() throws Exception {
		documentoICABean.inicializarInformeTecnicoAsociado();
		updateOficio();
	}

	public String getDiscriminador() {
		return "tecnico";
	}

	public void updateOficio() {
		try {
			documentoICABean.visualizarOficio(true, true);
		} catch (Exception e) {
			LOG.error("Error al cargar el oficio de pronunciamiento de inspeccion de control ambiental", e);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}

	public String aceptar() throws ServiceException, CmisAlfrescoException {
		List<String> errors = new ArrayList<>();
		if (documentoICABean.getOficio().getCumplimiento() != null
				&& documentoICABean.getOficio().getCumplimiento().trim().isEmpty())
			errors.add("El campo 'CUMPLIMIENTO' es requerido.");
		if (documentoICABean.getOficio().getEstablecido() != null
				&& documentoICABean.getOficio().getEstablecido().trim().isEmpty())
			errors.add("El campo 'ESTABLECIDO' es requerido.");

		if (!validarPuedeContinuarExistenObservacionesSinCorregir())
			errors.add(
					"El campo 'ES NECESARIO REALIZAR CORRECCIONES SOBRE EL INFORME TÉCNICO O AL OFICIO DE PRONUNCIAMIENTO' debe tener el valor sí, porque existen observaciones sin corregir.");

		if (!validarPuedeContinuarNoExistenObservacionesSinCorregir())
			errors.add(
					"El campo 'ES NECESARIO REALIZAR CORRECCIONES SOBRE EL INFORME TÉCNICO O AL OFICIO DE PRONUNCIAMIENTO' debe tener el valor 'No', porque no existen observaciones sin corregir.");

		if (!errors.isEmpty()) {
			JsfUtil.addMessageError(errors);
			return "";
		}

		documentoICABean.guardarOficio();
		documentoICABean.getOficio().setDocumento(documentoICABean.guardarOficioDocumento());
		documentoICABean.guardarOficio();

		ejecutarLogicaAdicional();

		updateOficio();

		JsfUtil.getBean(SendCopyBean.class).sendFilesCopies(documentoICABean.getOficio().getClass().getSimpleName(),
				documentoICABean.getOficio().getId(), getDiscriminador(),
				new String[] { documentoICABean.getOficio().getOficioRealPath() });

		try {
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), JsfUtil.getCurrentTask().getTaskId(),
					JsfUtil.getCurrentProcessInstanceId(), null);
		} catch (JbpmException e) {
			e.printStackTrace();
		}
		JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
		return JsfUtil.actionNavigateToBandeja();
	}

	public void guardar() {
		documentoICABean.guardarOficio();
		JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
		updateOficio();

		JsfUtil.getBean(SendCopyBean.class).guardarFilesCopies(documentoICABean.getOficio().getClass().getSimpleName(),
				documentoICABean.getOficio().getId(), getDiscriminador());
	}

	public String cancelar() {
		return JsfUtil.actionNavigateToBandeja();
	}

	public void validarTareaBpm() {
		JsfUtil.validarPaginasUrlTareasBpm(JsfUtil.getCurrentTask(),
				"/control/inspeccionescontrolambiental/tecnicoResponsableInformeTecnico.jsf");
	}

	public void ejecutarLogicaAdicional() {

	}

	public boolean validarPuedeContinuarExistenObservacionesSinCorregir() {
		return true;
	}

	public boolean validarPuedeContinuarNoExistenObservacionesSinCorregir() {
		return true;
	}

	public String guardarRegresar() {
		guardar();
		return JsfUtil.actionNavigateTo("/control/inspeccionescontrolambiental/tecnicoResponsableInformeTecnico.jsf");
	}
}
