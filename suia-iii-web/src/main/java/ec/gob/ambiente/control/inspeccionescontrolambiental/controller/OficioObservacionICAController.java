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
public class OficioObservacionICAController implements Serializable {

	private static final long serialVersionUID = 2205338056926550957L;

	private final Logger LOG = Logger.getLogger(OficioObservacionICAController.class);

	@EJB
	protected ProcesoFacade procesoFacade;

	@ManagedProperty(value = "#{documentoICABean}")
	@Getter
	@Setter
	private DocumentoICABean documentoICABean;

	@PostConstruct
	public void init() {
		documentoICABean.inicializarInformeTecnicoAsociado();
		updateOficio();
	}
	
	public String getDiscriminador() {
		return "tecnico";
	}

	public void updateOficio() {
		try {
			documentoICABean.visualizarOficioObservaciones(true, true);
		} catch (Exception e) {
			LOG.error("Error al cargar el oficio de observación de inspeccion de control ambiental", e);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}

	public String aceptar() throws ServiceException, CmisAlfrescoException {
		List<String> errors = new ArrayList<>();
		if (documentoICABean.getOficioObservaciones().getCumplimiento() != null
				&& documentoICABean.getOficioObservaciones().getCumplimiento().trim().isEmpty())
			errors.add("El campo 'CUMPLIMIENTO' es requerido.");
		if (documentoICABean.getOficioObservaciones().getEstablecido() != null
				&& documentoICABean.getOficioObservaciones().getEstablecido().trim().isEmpty())
			errors.add("El campo 'ESTABLECIDO' es requerido.");
		if (documentoICABean.getOficioObservaciones().getObservaciones() != null
				&& documentoICABean.getOficioObservaciones().getObservaciones().trim().isEmpty())
			errors.add("El campo 'OBSERVACIONES' es requerido.");

		if (!validarPuedeContinuarExistenObservacionesSinCorregir())
			errors.add("El campo 'ES NECESARIO REALIZAR CORRECCIONES SOBRE EL INFORME TÉCNICO O AL OFICIO DE PRONUNCIAMIENTO' debe tener el valor 'Sí', porque existen observaciones sin corregir.");

		if (!validarPuedeContinuarNoExistenObservacionesSinCorregir())
			errors.add("El campo 'ES NECESARIO REALIZAR CORRECCIONES SOBRE EL INFORME TÉCNICO O AL OFICIO DE PRONUNCIAMIENTO' debe tener el valor 'No', porque no existen observaciones sin corregir.");

		if (!errors.isEmpty()) {
			JsfUtil.addMessageError(errors);
			return "";
		}

		documentoICABean.guardarOficioObservaciones();
		String url = JsfUtil.getRequest().getRequestURI();
		if (url.equals("/suia-iii/prevencion/registrogeneradordesechos/directorRevisarInformeOficioObservaciones.jsf")) {
			documentoICABean.getOficioObservaciones().setDocumento(documentoICABean.guardarOficioObservacionesDocumento());
			documentoICABean.guardarOficioObservaciones();
		 }
		
		ejecutarLogicaAdicional();

		updateOficio();

		JsfUtil.getBean(SendCopyBean.class).sendFilesCopies(
				documentoICABean.getOficioObservaciones().getClass().getSimpleName(),
				documentoICABean.getOficioObservaciones().getId(), getDiscriminador(),
				new String[] { documentoICABean.getOficioObservaciones().getOficioRealPath() });

		try {
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), JsfUtil.getCurrentTask().getTaskId(),
					JsfUtil.getCurrentProcessInstanceId(), null);
		} catch (JbpmException e) {
			e.printStackTrace();
		}
		JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
		return JsfUtil.actionNavigateToBandeja();
	}

	public void validarTareaBpm() {
		JsfUtil.validarPaginasUrlTareasBpm(JsfUtil.getCurrentTask(),
				"/control/inspeccionescontrolambiental/tecnicoResponsableInformeTecnico.jsf");
	}

	public void guardar() {
		documentoICABean.guardarOficioObservaciones();
		JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
		updateOficio();
		
		JsfUtil.getBean(SendCopyBean.class).guardarFilesCopies(documentoICABean.getOficioObservaciones().getClass().getSimpleName(),
				documentoICABean.getOficioObservaciones().getId(), getDiscriminador());
	}

	public String cancelar() {
		return JsfUtil.actionNavigateToBandeja();
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
