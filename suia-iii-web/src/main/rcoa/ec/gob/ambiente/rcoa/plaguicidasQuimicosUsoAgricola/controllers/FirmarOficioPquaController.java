package ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.controllers;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

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
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.DocumentoPqua;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.InformeTecnicoPqua;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.OficioPronunciamientoPqua;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.ProyectoPlaguicidas;
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
public class FirmarOficioPquaController implements Serializable {

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
	
	@Getter
	@Setter
	private DocumentoPqua documentoOficioAlfresco, oficioFirmaManual;
	
	@Getter
	@Setter
	private TipoDocumentoSistema tipoDocInforme;
	
	@Getter
	@Setter
	private Boolean token, documentoDescargado, subido;
	
	@Getter
	@Setter
	private Boolean esPronunciamientoFavorable, esProduccion, habilitarFirma;

	@Getter
	@Setter
	private String nombreTipoInforme, nombreTipoOficio, nombreDocumentoFirmado, seccionObservaciones, claseObservaciones;

	@Getter
	@Setter
	private String urlAlfresco, urlInforme, nombreInforme;
	
	@Getter
	@Setter
	private byte[] archivoInforme;
	
	@Setter
    @Getter
    private int activeIndex = 0;

	@PostConstruct
	private void iniciar() {
		try {

			esProduccion = Constantes.getPropertyAsBoolean("ambiente.produccion");

			subido = false;
			documentoDescargado = false;
			habilitarFirma = false;
			verificaToken();

			esPronunciamientoFavorable = informeOficioPquaBean.getVariables().containsKey("esPronunciamientoAprobacion") 
					? (Boolean.valueOf((String) informeOficioPquaBean.getVariables().get("esPronunciamientoAprobacion"))) : false;

			informeOficioPquaBean.setEsPronunciamientoFavorable(esPronunciamientoFavorable);
			
			informeOficioPquaBean.generarOficio(true);

			if (esPronunciamientoFavorable) {
				tipoDocInforme = TipoDocumentoSistema.PQUA_INFORME_APROBADO;
			} else {
				tipoDocInforme = TipoDocumentoSistema.PQUA_INFORME_OBSERVADO;
			}

			List<DocumentoPqua> documentos = documentosFacade.documentoPorTablaIdPorIdDoc(informeOficioPquaBean.getInformeTecnico().getId(), tipoDocInforme,
							InformeTecnicoPqua.class.getSimpleName());
			if (documentos.size() > 0) {
				DocumentoPqua documentoInforme = documentos.get(0);

				File fileDoc = documentosFacade.descargarFile(documentoInforme);

				Path path = Paths.get(fileDoc.getAbsolutePath());
				byte[] contenido = Files.readAllBytes(path);
				String reporteHtmlfinal = documentoInforme.getNombre().replace("/", "-");
				File fileInforme = new File(JsfUtil.devolverPathReportesHtml(reporteHtmlfinal));
				FileOutputStream file = new FileOutputStream(fileInforme);
				file.write(contenido);
				file.close();

				urlInforme = JsfUtil.devolverContexto("/reportesHtml/"+ documentoInforme.getNombre());
				nombreInforme = documentoInforme.getNombre();
				archivoInforme = contenido;
			}

		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos.");
		}
	}
	
	public void validarTareaBpm() {
		JsfUtil.validarPaginasUrlTareasBpm(JsfUtil.getCurrentTask(),
				"/pages/rcoa/plaguicidasQuimicosUsoAgricola/firmarOficio.jsf");
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
	        
	        habilitarFirma = true;
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
				
				habilitarFirma = true;
			}
		} catch (Exception e) {
			habilitarFirma = false;
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}
	
	public StreamedContent getStream(Integer tipo) throws Exception {
		DefaultStreamedContent content = new DefaultStreamedContent();
		
		switch (tipo) {
		case 1:
			if (archivoInforme != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(archivoInforme), "application/octet-stream");
				content.setName(nombreInforme);
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

	public void guardarDocumentosFirma() {
		try {
			
			informeOficioPquaBean.guardarOficio();
			informeOficioPquaBean.generarOficio(false);
			
			Boolean guardarDocumento = false;
			if(documentoOficioAlfresco != null && documentoOficioAlfresco.getId() != null) {
				if(!documentoOficioAlfresco.getUsuarioCreacion().equals(JsfUtil.getLoggedUser().getNombre()) 
						|| !JsfUtil.getSimpleDateFormat(documentoOficioAlfresco.getFechaCreacion()).equals(JsfUtil.getSimpleDateFormat(new Date()))) {
					documentoOficioAlfresco.setEstado(false);
					documentosFacade.guardar(documentoOficioAlfresco);
					
					guardarDocumento = true;
				}
			} else {
				guardarDocumento = true;
			}
			
			if(guardarDocumento) {
				documentoOficioAlfresco = informeOficioPquaBean.guardarDocumentoOficio();
			}
			
			if(documentoOficioAlfresco.getId() != null) {
				String documentOffice = documentosFacade.direccionDescarga(documentoOficioAlfresco);
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
			
			byte[] documentoContent = informeOficioPquaBean.getOficioRevision().getArchivoOficio();
			
			if (documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(informeOficioPquaBean.getOficioRevision().getNombreFichero());
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
	        
	        oficioFirmaManual = new DocumentoPqua();
	        oficioFirmaManual.setId(null);
	        oficioFirmaManual.setNombre(event.getFile().getFileName());
	        oficioFirmaManual.setContenidoDocumento(contenidoDocumento);
	        oficioFirmaManual.setMime("application/pdf");
	        oficioFirmaManual.setIdTabla(informeOficioPquaBean.getOficioRevision().getId());
	        oficioFirmaManual.setNombreTabla(OficioPronunciamientoPqua.class.getSimpleName());
	        oficioFirmaManual.setIdTipoDocumento(informeOficioPquaBean.getTipoDocOficio().getIdTipoDocumento());
	        
	        subido = true;
		} else{
			JsfUtil.addMessageError("No ha realizado la descarga del documento");
		}
    }

	public void finalizar() {
		try {
			if (token) {
				String idAlfresco = documentoOficioAlfresco.getIdAlfresco();

				if (!documentosFacade.verificarFirmaAlfresco(idAlfresco)) {
					JsfUtil.addMessageError("El oficio no está firmado electrónicamente.");
					return;
				}
			} else {
				if(subido){
					documentoOficioAlfresco = documentosFacade.guardarDocumentoAlfresco(informeOficioPquaBean.getProyectoPlaguicidas().getCodigoProyecto(), 
							"PQUA_OFICIO_PRONUNCIAMIENTO", 0L, oficioFirmaManual, informeOficioPquaBean.getTipoDocOficio());
				} else {
					JsfUtil.addMessageError("Debe adjuntar el oficio firmado.");
					return;
				}
			}
			
			informeOficioPquaBean.guardarFechaOficio(informeOficioPquaBean.getOficioRevision());
			
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
			
			documentoOficioAlfresco.setIdProceso(bandejaTareasBean.getProcessId());
			documentosFacade.guardar(documentoOficioAlfresco);
			
			if(esPronunciamientoFavorable) {
				List<DocumentoPqua> documentos = documentosFacade.documentoPorTablaIdPorIdDoc(informeOficioPquaBean.getProyectoPlaguicidas().getId(), 
						TipoDocumentoSistema.PQUA_DOCUMENTO_ETIQUETA, "RespaldoEtiquetaProyectoPlaguicidas");
				if(documentos != null && documentos.size() > 0) {
					DocumentoPqua documentoEtiqueta = documentos.get(0);
					documentoEtiqueta.setIdProceso(bandejaTareasBean.getProcessId());
					documentosFacade.guardar(documentoEtiqueta);
				}
			}
			
			Integer pronunciamiento = (esPronunciamientoFavorable) ? ProyectoPlaguicidas.TRAMITE_APROBADO : ProyectoPlaguicidas.TRAMITE_OBSERVADO;
			
			informeOficioPquaBean.getProyectoPlaguicidas().setResultadoRevision(pronunciamiento);
			informeOficioPquaBean.getProyectoPlaguicidas().setFechaResultado(new Date());
			
			informeOficioPquaBean.guardarProyecto();
			
			informeOficioPquaBean.notificarOperador(documentoOficioAlfresco);

			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
			JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Error al realizar la operación.");
		}
	}
	
	
	
}
