package ec.gob.ambiente.rcoa.viabilidadAmbientalForestal.controllers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.DocumentosViabilidadFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.DocumentoViabilidad;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.DigitalSign;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class ElaborarDocumentosApoyoViabilidadPfnController implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private final Logger LOG = Logger.getLogger(ElaborarDocumentosApoyoViabilidadPfnController.class);
	
	@ManagedProperty(value = "#{bandejaTareasBean}")
    @Getter
    @Setter
    private BandejaTareasBean bandejaTareasBean;
	
	@ManagedProperty(value = "#{informeOficioApoyoViabilidadPfnBean}")
	@Getter
	@Setter
	private InformeOficioApoyoViabilidadPfnBean informeOficioApoyoViabilidadPfnBean;
	
	@EJB
	private ProcesoFacade procesoFacade;
	
	@EJB
	private DocumentosViabilidadFacade documentosFacade;
	
	@EJB
	private UsuarioFacade usuarioFacade;
	
	@Getter
	@Setter
	private DocumentoViabilidad documentoInformeAlfresco, informeFirmaManual;

	@Getter
	@Setter
	private Boolean token, documentoDescargado, subido, informeGuardado, esProduccion, habilitarFirma;
	
	@Getter
	@Setter
	private String urlReporte, nombreReporte, urlAlfresco;

	@Getter
	@Setter
	private Integer numeroRevision;
	
	@Setter
    @Getter
    private int activeIndex = 0;
	
	@Getter
	@Setter
	private byte[] archivoReporte;
	
	@PostConstruct
	private void iniciar() {
		esProduccion = Constantes.getPropertyAsBoolean("ambiente.produccion") ;
		
		subido = false;
		documentoDescargado = false;
		informeGuardado = false;
		habilitarFirma = false;
		verificaToken();

		numeroRevision = Integer.valueOf((String) informeOficioApoyoViabilidadPfnBean.getVariables().get("numeroRevisionInformacion"));
		
		informeOficioApoyoViabilidadPfnBean.generarInforme(true);
		
		urlReporte = informeOficioApoyoViabilidadPfnBean.getInformeApoyo().getInformePath();
		nombreReporte = informeOficioApoyoViabilidadPfnBean.getInformeApoyo().getNombreReporte();
		archivoReporte = informeOficioApoyoViabilidadPfnBean.getInformeApoyo().getArchivoInforme();
		
		informeOficioApoyoViabilidadPfnBean.generarOficio(true);
	}
	
	public void validarTareaBpm() {
		JsfUtil.validarPaginasUrlTareasBpm(JsfUtil.getCurrentTask(),
				"/pages/rcoa/viabilidadAmbientalForestal/elaborarDocumentosApoyo.jsf");
	}
	
	public boolean verificaToken() {
		if(esProduccion) {
			token = true;
			return token;
		}
		
		token = false;
		if (JsfUtil.getLoggedUser().getToken() != null
				&& JsfUtil.getLoggedUser().getToken())
			token = true;
		return token;
	}

	public void guardarToken() {
		Usuario usuario = JsfUtil.getLoggedUser();
		usuario.setToken(token);
		try {
			usuarioFacade.guardar(usuario);
			verificaToken();
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}
	
	public StreamedContent getStream(Integer tipo) throws Exception {
		DefaultStreamedContent content = new DefaultStreamedContent();
		
		switch (tipo) {
		case 1:
			if (archivoReporte != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(
						archivoReporte), "application/octet-stream");
				content.setName(informeOficioApoyoViabilidadPfnBean.getInformeApoyo().getNombreFichero());
			}
			break;
		case 2:
			if (informeOficioApoyoViabilidadPfnBean.getOficioPronunciamiento().getArchivoOficio() != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(
						informeOficioApoyoViabilidadPfnBean.getOficioPronunciamiento().getArchivoOficio()), "application/octet-stream");
				content.setName(informeOficioApoyoViabilidadPfnBean.getOficioPronunciamiento().getNombreFichero());
			}
			break;
		default:
			break;
		}
		
		return content;
	}
	
	public void siguiente() {
        setActiveIndex(1);
        habilitarFirma = false;
	}
	
	public void atras() {
        setActiveIndex(0);
        habilitarFirma = false;
	}
	
	public void validateDatosIngresoInforme(FacesContext context, UIComponent validate, Object value) {
		List<FacesMessage> errorMessages = new ArrayList<>();
		
		if(informeOficioApoyoViabilidadPfnBean.getInformeApoyo().getAntecedentes() == null || 
				informeOficioApoyoViabilidadPfnBean.getInformeApoyo().getAntecedentes().isEmpty())
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"El campo 'Antecedente' es requerido", null));
		
		if(informeOficioApoyoViabilidadPfnBean.getInformeApoyo().getMarcoLegal()== null || 
				informeOficioApoyoViabilidadPfnBean.getInformeApoyo().getMarcoLegal().isEmpty())
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"El campo 'Marco Legal' es requerido", null));
		
		if(informeOficioApoyoViabilidadPfnBean.getInformeApoyo().getConclusiones() == null || 
				informeOficioApoyoViabilidadPfnBean.getInformeApoyo().getConclusiones().isEmpty())
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"El campo 'Conclusiones/Observaciones' es requerido", null));
		
		if(informeOficioApoyoViabilidadPfnBean.getInformeApoyo().getRecomendaciones()== null || 
				informeOficioApoyoViabilidadPfnBean.getInformeApoyo().getRecomendaciones().isEmpty())
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"El campo 'Recomendaciones' es requerido", null));

		if(context.getMessageList().size() > 0 || errorMessages.size() > 0)
			informeGuardado = false;
		
		if (!errorMessages.isEmpty())
			throw new ValidatorException(errorMessages);
	}
	
	public void validateDatosIngresoOficio(FacesContext context, UIComponent validate, Object value) {
		List<FacesMessage> errorMessages = new ArrayList<>();
		
		if(informeOficioApoyoViabilidadPfnBean.getOficioPronunciamiento().getAntecedentes() == null || 
				informeOficioApoyoViabilidadPfnBean.getOficioPronunciamiento().getAntecedentes().isEmpty())
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"El campo 'Antecedente' es requerido", null));
		
		if(informeOficioApoyoViabilidadPfnBean.getOficioPronunciamiento().getMarcoLegal()== null || 
				informeOficioApoyoViabilidadPfnBean.getOficioPronunciamiento().getMarcoLegal().isEmpty())
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"El campo 'Marco Legal' es requerido", null));
		
		if(informeOficioApoyoViabilidadPfnBean.getOficioPronunciamiento().getConclusiones() == null || 
				informeOficioApoyoViabilidadPfnBean.getOficioPronunciamiento().getConclusiones().isEmpty())
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"El campo 'Conclusiones/Observaciones' es requerido", null));
		
		if(informeOficioApoyoViabilidadPfnBean.getOficioPronunciamiento().getRecomendaciones()== null || 
				informeOficioApoyoViabilidadPfnBean.getOficioPronunciamiento().getRecomendaciones().isEmpty())
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"El campo 'Recomendaciones' es requerido", null));

		if(context.getMessageList().size() > 0 || errorMessages.size() > 0)
			habilitarFirma = false;
		
		if (!errorMessages.isEmpty())
			throw new ValidatorException(errorMessages);
	}
	
	public void guardarInforme() {
		try {
			informeOficioApoyoViabilidadPfnBean.guardarInforme();
					
			informeOficioApoyoViabilidadPfnBean.generarInforme(true);
			
			urlReporte = informeOficioApoyoViabilidadPfnBean.getInformeApoyo().getInformePath();
			nombreReporte = informeOficioApoyoViabilidadPfnBean.getInformeApoyo().getNombreReporte();
			archivoReporte = informeOficioApoyoViabilidadPfnBean.getInformeApoyo().getArchivoInforme();
			
			informeGuardado = true;
			
			RequestContext.getCurrentInstance().update("form:tab:pnlDocumentoInforme");
			
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}
	
	public void guardarOficio() {
		try {
			informeOficioApoyoViabilidadPfnBean.guardarInforme();
			
			informeOficioApoyoViabilidadPfnBean.guardarOficio();
	
			informeOficioApoyoViabilidadPfnBean.generarOficio(true);
			
			habilitarFirma = true;
			
			RequestContext.getCurrentInstance().update("form:tab:pnlDocumentoOficio");
			
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
		} catch (Exception e) {
			habilitarFirma = false;
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}
	
	public void guardarDocumentosFirma() {
		try {
			
			informeOficioApoyoViabilidadPfnBean.guardarInforme();
			
			informeOficioApoyoViabilidadPfnBean.generarInforme(false);
			
			urlReporte = informeOficioApoyoViabilidadPfnBean.getInformeApoyo().getInformePath();
			nombreReporte = informeOficioApoyoViabilidadPfnBean.getInformeApoyo().getNombreReporte();
			archivoReporte = informeOficioApoyoViabilidadPfnBean.getInformeApoyo().getArchivoInforme();
			
			informeOficioApoyoViabilidadPfnBean.guardarOficio();
			
			informeOficioApoyoViabilidadPfnBean.generarOficio(true);

			documentoInformeAlfresco = informeOficioApoyoViabilidadPfnBean.guardarDocumentoInformeAlfresco();
			
			if(documentoInformeAlfresco != null && documentoInformeAlfresco.getId() != null) {
				String documentOffice = documentosFacade.direccionDescarga(documentoInformeAlfresco);
				urlAlfresco = DigitalSign.sign(documentOffice, JsfUtil.getLoggedUser().getNombre());
			} else {
				LOG.error("No se encontro el informe forestal");
				JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
			return;
		}
		
		RequestContext.getCurrentInstance().update("formDialogs:");
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('signDialog').show();");

	}

	public StreamedContent descargar() throws IOException {
		DefaultStreamedContent content = null;
		try {
			byte[] documentoContent = informeOficioApoyoViabilidadPfnBean.getInformeApoyo().getArchivoInforme();
			
			if (documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(informeOficioApoyoViabilidadPfnBean.getInformeApoyo().getNombreFichero());
			} else
				JsfUtil.addMessageError("Error al generar el archivo");
			
			documentoDescargado = true;
		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}

	public void uploadListenerDocumentos(FileUploadEvent event) {
		if(documentoDescargado) {
	        byte[] contenidoDocumento = event.getFile().getContents();
	        
	        informeFirmaManual = new DocumentoViabilidad();
	        informeFirmaManual.setId(null);
	        informeFirmaManual.setContenidoDocumento(contenidoDocumento);
	        informeFirmaManual.setNombre(event.getFile().getFileName());
	        informeFirmaManual.setMime("application/pdf");
	        informeFirmaManual.setIdViabilidad(informeOficioApoyoViabilidadPfnBean.getViabilidadProyecto().getId());
	        informeFirmaManual.setIdTipoDocumento(TipoDocumentoSistema.RCOA_VIABILIDAD_PFN_II_INFORME_APOYO.getIdTipoDocumento());
	        
	        subido = true;
		} else{
			JsfUtil.addMessageError("No ha realizado la descarga del documento");
		}
    }

	public void finalizar() {
		try {
			if (token) {
				String idAlfrescoOficio = documentoInformeAlfresco.getIdAlfresco();

				if (!documentosFacade.verificarFirmaVersion(idAlfrescoOficio)) {
					JsfUtil.addMessageError("El informe no está firmado electrónicamente.");
					return;
				}
			} else {
				if(!subido) {
					JsfUtil.addMessageError("Debe adjuntar el informe firmado.");
					return;
				}
				
				documentoInformeAlfresco = documentosFacade.guardarDocumentoProceso(
						informeOficioApoyoViabilidadPfnBean.getProyectoLicenciaCoa().getCodigoUnicoAmbiental(),
						"VIABILIDAD_AMBIENTAL", informeFirmaManual, 2, JsfUtil.getCurrentProcessInstanceId());
				
			}
			
			Map<String, Object> variables = informeOficioApoyoViabilidadPfnBean.getVariables();
			
			Map<String, Object> parametros = new HashMap<>();
			
			Usuario usuarioAutoridad = informeOficioApoyoViabilidadPfnBean.getUsuarioAutoridad();
			String usuarioAutoridadBpm = (String) variables.get("autoridadApoyo");
			if (usuarioAutoridadBpm == null || !usuarioAutoridadBpm.equals(usuarioAutoridad.getNombre())) {
				parametros.put("autoridadApoyo", usuarioAutoridad.getNombre());
			}
			
			procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
			
			documentoInformeAlfresco.setIdProceso(JsfUtil.getCurrentProcessInstanceId());
			documentosFacade.guardar(documentoInformeAlfresco);

			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
			JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
			
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al enviar la tarea.");
		}
	}
	
}
