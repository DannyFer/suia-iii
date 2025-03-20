package ec.gob.ambiente.control.inspeccionescontrolambiental.controller;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;

import ec.gob.ambiente.control.inspeccionescontrolambiental.bean.DocumentoICABean;
import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;

@ManagedBean
@ViewScoped
public class RecibirPronunciamientoICAController implements Serializable {

	private static final long serialVersionUID = 373943877747256120L;

	private final Logger LOG = Logger.getLogger(RecibirPronunciamientoICAController.class);

	@EJB
	private ProcesoFacade procesoFacade;

	@EJB
	private DocumentosFacade documentosFacade;

	@ManagedProperty(value = "#{documentoICABean}")
	@Getter
	@Setter
	private DocumentoICABean documentoICABean;

	@PostConstruct
	public void init() {
		try {
			Documento documentoOficioAprovacion = documentoICABean.inicializarOficioPronunciamientoAsociado()
					.getDocumento();

			if (documentoOficioAprovacion != null)
				documentoOficioAprovacion
						.setContenidoDocumento(documentosFacade.descargar(documentoOficioAprovacion.getIdAlfresco()));
		} catch (Exception e) {
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_INICIAR_TAREA);
			LOG.error("Error cargando datos de inspeccion de control ambiental", e);
		}
	}

	public String aceptar() throws ServiceException, CmisAlfrescoException {
		try {
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), JsfUtil.getCurrentTask().getTaskId(),
					JsfUtil.getCurrentProcessInstanceId(), null);
		} catch (Exception e) {
			LOG.error("Error al completar la tarea de inspeccion de control ambiental", e);
		}
		JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
		return JsfUtil.actionNavigateToBandeja();
	}

	public void validarTareaBpm() {
		JsfUtil.validarPaginasUrlTareasBpm(JsfUtil.getCurrentTask(),
				"/control/inspeccionescontrolambiental/sujetoControlRecibirPronunciamiento.jsf");
	}
}
