package ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.controllers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.facade.DocumentoPquaFacade;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.facade.ObservacionesActualizacionPquaFacade;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.DocumentoPqua;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.InformeTecnicoPqua;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.ObservacionesActualizacionPqua;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.DigitalSign;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class ElaborarInformeOficioPquaController implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ManagedProperty(value = "#{bandejaTareasBean}")
    @Getter
    @Setter
    private BandejaTareasBean bandejaTareasBean;
	
	@ManagedProperty(value = "#{informeOficioPquaBean}")
	@Getter
	@Setter
	private InformeOficioPquaBean informeOficioPquaBean;
	
	@EJB
	private DocumentoPquaFacade documentosFacade;
	@EJB
    private UsuarioFacade usuarioFacade;
	@EJB
    private ProcesoFacade procesoFacade;
	@EJB
    private ObservacionesActualizacionPquaFacade observacionesActualizacionPquaFacade;
	
	@Getter
	@Setter
	private DocumentoPqua documentoInformeAlfresco, informeFirmaManual;
	
	@Getter
	@Setter
	private Boolean informeGuardado, token, documentoDescargado, subido;
	
	@Getter
	@Setter
	private Boolean esPronunciamientoFavorable, esProduccion, habilitarFirma;

	@Getter
	@Setter
	private String nombreTipoInforme, nombreTipoOficio, nombreDocumentoFirmado, seccionObservaciones, claseObservaciones;

	@Getter
	@Setter
	private String urlAlfresco;
	
	@Setter
    @Getter
    private int activeIndex = 0;

	@PostConstruct
	private void iniciar() {
		try {

		esProduccion = Constantes.getPropertyAsBoolean("ambiente.produccion") ;
		
		subido = false;
		informeGuardado = false;
		documentoDescargado = false;
		habilitarFirma = false;
		verificaToken();
		
		esPronunciamientoFavorable = !validarObservaciones();
		
		informeOficioPquaBean.setEsPronunciamientoFavorable(esPronunciamientoFavorable);
		
		informeOficioPquaBean.generarInforme(true);
		
		informeOficioPquaBean.generarOficio(true);
		
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos.");
		}
	}
	
	public void validarTareaBpm() {
		JsfUtil.validarPaginasUrlTareasBpm(JsfUtil.getCurrentTask(),
				"/pages/rcoa/plaguicidasQuimicosUsoAgricola/elaborarInformeTecnicoOficio.jsf");
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
	
	public void siguiente() {
		try {
	        setActiveIndex(1);
	        habilitarFirma = false;
	
	        informeOficioPquaBean.generarOficio(true);
		} catch (Exception e) {
			habilitarFirma = false;
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}
	
	public void atras() {
        setActiveIndex(0);
        habilitarFirma = false;
	}
	
	public void onTabChange(TabChangeEvent event) {
		try {
			if(event.getTab().getId().equals("tabOficio")) {
				informeOficioPquaBean.generarOficio(true);
			}
		} catch (Exception e) {
			habilitarFirma = false;
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}
	
	public Boolean validarObservaciones() {
		Boolean existeObservacionesEtiquetas = false;
		try {
			List<ObservacionesActualizacionPqua> observacionesInforme = observacionesActualizacionPquaFacade.listarPorIdClaseNombreClaseNoCorregidas(
							informeOficioPquaBean.getProyectoPlaguicidas().getId(), "ActualizacionEtiquetaPqua");
			
			if(observacionesInforme.size() > 0) {
				existeObservacionesEtiquetas = true;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return existeObservacionesEtiquetas;
	}
	
	public StreamedContent getStream(Integer tipo) throws Exception {
		DefaultStreamedContent content = new DefaultStreamedContent();
		
		switch (tipo) {
		case 1:
			if (informeOficioPquaBean.getInformeTecnico().getArchivoInforme() != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(
						informeOficioPquaBean.getInformeTecnico().getArchivoInforme()), "application/octet-stream");
				content.setName(informeOficioPquaBean.getInformeTecnico().getNombreFichero());
			}
			break;
		case 2:
			if (informeOficioPquaBean.getOficioRevision().getArchivoOficio() != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(
						informeOficioPquaBean.getOficioRevision().getArchivoOficio()), "application/octet-stream");
				content.setName(informeOficioPquaBean.getOficioRevision().getNombreFichero());
			}
			break;
		default:
			break;
		}
		
		return content;
	}
	
	public void guardarInforme() {
		try {
			informeOficioPquaBean.setEsPronunciamientoFavorable(esPronunciamientoFavorable);
			informeOficioPquaBean.guardarInforme();
			informeOficioPquaBean.generarInforme(true);
			
			informeGuardado = true;
			
			RequestContext.getCurrentInstance().update("form:tab:pnlInforme");
			
			JsfUtil.addMessageInfo(JsfUtil.REGISTRO_GUARDADO);
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}
	
	public void guardarOficio() {
		try {
			informeOficioPquaBean.guardarOficio();
			informeOficioPquaBean.generarOficio(true);
			
			habilitarFirma = true;
			
			RequestContext.getCurrentInstance().update("form:tab:pnlDocumentoOficio");
			RequestContext.getCurrentInstance().update("form:tab:pnlButtons");
			
			JsfUtil.addMessageInfo(JsfUtil.REGISTRO_GUARDADO);
		} catch (Exception e) {
			habilitarFirma = false;
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
			
			RequestContext.getCurrentInstance().update("form:tab:pnlDocumentoOficio");
			RequestContext.getCurrentInstance().update("form:tab:pnlButtons");
		}
	}

	public void guardarDocumentosFirma() {
		try {
			
			informeOficioPquaBean.guardarInforme();
			informeOficioPquaBean.generarInforme(false);
			
			informeOficioPquaBean.guardarOficio();
			
			Boolean guardarDocumento = false;
			if(documentoInformeAlfresco != null && documentoInformeAlfresco.getId() != null) {
				if(!documentoInformeAlfresco.getUsuarioCreacion().equals(JsfUtil.getLoggedUser().getNombre()) 
						|| !JsfUtil.getSimpleDateFormat(documentoInformeAlfresco.getFechaCreacion()).equals(JsfUtil.getSimpleDateFormat(new Date()))) {
					documentoInformeAlfresco.setEstado(false);
					documentosFacade.guardar(documentoInformeAlfresco);
					
					guardarDocumento = true;
				}
			} else {
				guardarDocumento = true;
			}
			
			if(guardarDocumento) {
				documentoInformeAlfresco = new DocumentoPqua();
				documentoInformeAlfresco.setNombre(informeOficioPquaBean.getInformeTecnico().getNombreFichero());
				documentoInformeAlfresco.setContenidoDocumento(informeOficioPquaBean.getInformeTecnico().getArchivoInforme());
				documentoInformeAlfresco.setMime("application/pdf");
				documentoInformeAlfresco.setIdTabla(informeOficioPquaBean.getInformeTecnico().getId());
				documentoInformeAlfresco.setNombreTabla(InformeTecnicoPqua.class.getSimpleName());
				documentoInformeAlfresco.setIdTipoDocumento(informeOficioPquaBean.getTipoDocInforme().getIdTipoDocumento());
				
				
				documentoInformeAlfresco = documentosFacade.guardarDocumentoAlfresco(informeOficioPquaBean.getProyectoPlaguicidas().getCodigoProyecto(), 
						"PQUA_INFORME_TECNICO", 0L, documentoInformeAlfresco, informeOficioPquaBean.getTipoDocInforme());
			}
			
			if(documentoInformeAlfresco.getId() != null) {
				String documentOffice = documentosFacade.direccionDescarga(documentoInformeAlfresco);
				urlAlfresco = DigitalSign.sign(documentOffice, JsfUtil.getLoggedUser().getNombre());
			} else {
				JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
				return;
			}
			
			RequestContext.getCurrentInstance().update("formDialogs:");
	        RequestContext context = RequestContext.getCurrentInstance();
	        context.execute("PF('signDialog').show();");
	        
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
			return;
		}
	}
	
	public StreamedContent descargar() throws IOException {
		DefaultStreamedContent content = null;
		try {
			
			byte[] documentoContent = informeOficioPquaBean.getInformeTecnico().getArchivoInforme();
			
			if (documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(informeOficioPquaBean.getInformeTecnico().getNombreFichero());
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
	        
	        nombreDocumentoFirmado = event.getFile().getFileName();
	        
	        informeFirmaManual = new DocumentoPqua();
	        informeFirmaManual.setId(null);
	        informeFirmaManual.setNombre(event.getFile().getFileName());
	        informeFirmaManual.setContenidoDocumento(contenidoDocumento);
	        informeFirmaManual.setMime("application/pdf");
	        informeFirmaManual.setIdTabla(informeOficioPquaBean.getInformeTecnico().getId());
	        informeFirmaManual.setNombreTabla(InformeTecnicoPqua.class.getSimpleName());
	        informeFirmaManual.setIdTipoDocumento(informeOficioPquaBean.getTipoDocInforme().getIdTipoDocumento());
	        
	        subido = true;
		} else{
			JsfUtil.addMessageError("No ha realizado la descarga del documento");
		}
    }

	public void finalizar() {
		try {
			if (token) {
				String idAlfresco = documentoInformeAlfresco.getIdAlfresco();

				if (!documentosFacade.verificarFirmaAlfresco(idAlfresco)) {
					JsfUtil.addMessageError("El informe no está firmado electrónicamente.");
					return;
				}
			} else {
				if(subido){
					documentoInformeAlfresco = documentosFacade.guardarDocumentoAlfresco(informeOficioPquaBean.getProyectoPlaguicidas().getCodigoProyecto(), 
							"PQUA_INFORME_TECNICO", 0L, informeFirmaManual, informeOficioPquaBean.getTipoDocInforme());
				} else {
					JsfUtil.addMessageError("Debe adjuntar el informe firmado.");
					return;
				}
			}
			
			if( informeOficioPquaBean.getUsuarioAutoridad() == null 
					||  informeOficioPquaBean.getUsuarioAutoridad().getId() == null){
				System.out.println("No se encontro usuario autoridad para enviar el proyecto " + informeOficioPquaBean.getProyectoPlaguicidas().getCodigoProyecto());
				return;
			}
			
			Map<String, Object> parametros = new HashMap<>();
			
			parametros.put("esPronunciamientoAprobacion", esPronunciamientoFavorable);
			if(esPronunciamientoFavorable) {
				parametros.put("autoridadAmbiental", informeOficioPquaBean.getUsuarioAutoridad().getNombre());
			} else {
				parametros.put("director", informeOficioPquaBean.getUsuarioAutoridad().getNombre());
			}
			
			procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros); 
			
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
			
			documentoInformeAlfresco.setIdProceso(bandejaTareasBean.getProcessId());
			documentosFacade.guardar(documentoInformeAlfresco);

			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
			JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Error al realizar la operación.");
		}
	}
	
}
