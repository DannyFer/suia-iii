package ec.gob.ambiente.control.retce.controllers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.retce.model.GeneradorDesechosPeligrososRetce;
import ec.gob.ambiente.retce.services.GeneradorDesechosPeligrososFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class DescargarOficioGeneradorController implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private final Logger LOG = Logger.getLogger(DescargarOficioGeneradorController.class);
	
	@ManagedProperty(value = "#{bandejaTareasBean}")
    @Getter
    @Setter
    private BandejaTareasBean bandejaTareasBean;
	
	@EJB
	private ProcesoFacade procesoFacade;
	
	@EJB
	private DocumentosFacade documentosFacade;
	
	@EJB
	private GeneradorDesechosPeligrososFacade generadorDesechosPeligrososFacade;
	
	@Getter
	@Setter
	private GeneradorDesechosPeligrososRetce generadorDesechosRetce;
	
	@Getter
	@Setter
	private Documento oficioPronunciamiento;
	
	@Getter
	@Setter
	private String tipoOficio;
	
	@Getter
	@Setter
	private Boolean documentoDescargado, pronunciamiento_aprobado;

	private Map<String, Object> variables;	
	
	@PostConstruct
	public void init() {
		try {
			variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
			String codigoGenerador = (String) variables.get(Constantes.VARIABLE_PROCESO_TRAMITE);
			pronunciamiento_aprobado = Boolean.valueOf(variables.get("pronunciamiento_aprobado").toString());
			
			generadorDesechosRetce = generadorDesechosPeligrososFacade.getRgdRetcePorCodigo(codigoGenerador);
			
			TipoDocumentoSistema tipoDocumento = (pronunciamiento_aprobado) ? TipoDocumentoSistema.OFICIO_APROBACION_GENERADOR : TipoDocumentoSistema.OFICIO_OBSERVACION_GENERADOR;
			tipoOficio = (pronunciamiento_aprobado) ? "Aprobación" : "Observaciones";
			
			documentoDescargado = false;
			
			List<Documento> oficios = documentosFacade.documentoXTablaIdXIdDoc(
					generadorDesechosRetce.getId(), "OficioPronunciamientoRetceGenerador",
					tipoDocumento);
			if (oficios.size() > 0)
				oficioPronunciamiento = oficios.get(0);
			
		} catch (Exception e) {
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar el OficioRetce.");
		}
	}
	
	public StreamedContent descargar() throws IOException {
		DefaultStreamedContent content = null;
		try {
			byte[] documentoContent = null;
			
			if (oficioPronunciamiento != null && oficioPronunciamiento.getIdAlfresco() != null) {
				documentoContent = documentosFacade.descargar(oficioPronunciamiento.getIdAlfresco());
			} else if (oficioPronunciamiento.getContenidoDocumento() != null) {
				documentoContent = oficioPronunciamiento.getContenidoDocumento();
			}
			
			if (oficioPronunciamiento != null && oficioPronunciamiento.getNombre() != null
					&& documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(oficioPronunciamiento.getNombre());
			} else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);

		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		
		documentoDescargado = true;
		return content;
	}
	
	public void enviar() {
		
		if(documentoDescargado) {
			try {
				procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
				
				if(pronunciamiento_aprobado) {
					generadorDesechosRetce.setTramiteFinalizado(true);
					generadorDesechosPeligrososFacade.guardarRgdRetce(generadorDesechosRetce);
				}
				
				JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
				JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
			} catch (JbpmException e) {
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
				e.printStackTrace();
			}
		} else
			JsfUtil.addMessageError("Antes de enviar debe descargar el documento.");
	}
}
