package ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.controllers;

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

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.facade.DocumentoPquaFacade;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.facade.OficioPronunciamientoPquaFacade;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.facade.ProyectoPlaguicidasFacade;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.DocumentoPqua;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.OficioPronunciamientoPqua;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.ProyectoPlaguicidas;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class RecibirPronunciamientoPquaController implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ManagedProperty(value = "#{bandejaTareasBean}")
    @Getter
    @Setter
    private BandejaTareasBean bandejaTareasBean;
	
	@EJB
	private DocumentoPquaFacade documentosFacade;
	@EJB
    private ProcesoFacade procesoFacade;
	@EJB
    private ProyectoPlaguicidasFacade proyectoPlaguicidasFacade;
	@EJB
	private OficioPronunciamientoPquaFacade oficioPronunciamientoPquaFacade;
	
	@Getter
	@Setter
	private ProyectoPlaguicidas proyectoPlaguicidas;
	
	@Getter
	@Setter
	private DocumentoPqua documentoOficioAlfresco;
	
	@Getter
	@Setter
	private TipoDocumentoSistema tipoDocOficio;
	
	@Getter
	@Setter
	private Boolean documentoDescargado, esPronunciamientoFavorable;
	
	@Getter
	@Setter
	private Integer numeroRevision;
	
	@Getter
	@Setter
	private byte[] archivoInforme;
	
	@Setter
    @Getter
    private int activeIndex = 0;
	
	@Getter
	@Setter
	private Map<String, Object> variables;

	@PostConstruct
	private void iniciar() {
		try {
			documentoDescargado = false;
			
			variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
			
			String tramite = (String) variables.get("tramite");

			numeroRevision = Integer.valueOf((String) variables.get("numeroRevision"));
			
			proyectoPlaguicidas = proyectoPlaguicidasFacade.getPorCodigoProyecto(tramite);

			esPronunciamientoFavorable = variables.containsKey("esPronunciamientoAprobacion") ? (Boolean.valueOf((String) variables.get("esPronunciamientoAprobacion"))) : false;

			if(esPronunciamientoFavorable) {
				tipoDocOficio = TipoDocumentoSistema.PQUA_OFICIO_APROBADO;
			} else {
				tipoDocOficio = TipoDocumentoSistema.PQUA_OFICIO_OBSERVADO;
			}
			
			OficioPronunciamientoPqua oficioRevision = oficioPronunciamientoPquaFacade.getPorProyectoRevision(proyectoPlaguicidas.getId(), numeroRevision);

			List<DocumentoPqua> documentos = documentosFacade.documentoPorTablaIdPorIdDoc(oficioRevision.getId(), tipoDocOficio,
					OficioPronunciamientoPqua.class.getSimpleName());
			if (documentos.size() > 0) {
				documentoOficioAlfresco = documentos.get(0);
			}

		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos.");
		}
	}
	
	public void validarTareaBpm() {
		JsfUtil.validarPaginasUrlTareasBpm(JsfUtil.getCurrentTask(),
				"/pages/rcoa/plaguicidasQuimicosUsoAgricola/recibirPronunciamiento.jsf");
	}
	
	public StreamedContent descargar() throws IOException {
		DefaultStreamedContent content = null;
		try {
			byte[] documentoContent = null;
			
			if (documentoOficioAlfresco != null && documentoOficioAlfresco.getIdAlfresco() != null) {
				documentoContent = documentosFacade.descargar(documentoOficioAlfresco.getIdAlfresco());
			} else if (documentoOficioAlfresco.getContenidoDocumento() != null) {
				documentoContent = documentoOficioAlfresco.getContenidoDocumento();
			}
			
			if (documentoOficioAlfresco != null && documentoOficioAlfresco.getNombre() != null
					&& documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(documentoOficioAlfresco.getNombre());
				
				documentoDescargado = true;
				return content;
			} else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);

		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}

	public void finalizar() {
		try {
			if (!documentoDescargado) {
				JsfUtil.addMessageError("Debe descargar el Oficio de pronunciamiento");
				return;
			}
			
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
			
			documentoOficioAlfresco.setIdProceso(bandejaTareasBean.getProcessId());
			documentosFacade.guardar(documentoOficioAlfresco);

			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
			JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Error al realizar la operación.");
		}
	}
	
}
