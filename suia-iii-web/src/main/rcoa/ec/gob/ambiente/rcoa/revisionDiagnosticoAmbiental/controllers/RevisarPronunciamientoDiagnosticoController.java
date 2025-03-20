package ec.gob.ambiente.rcoa.revisionDiagnosticoAmbiental.controllers;

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

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.rcoa.facade.CatalogoCoaFacade;
import ec.gob.ambiente.rcoa.facade.DocumentosCoaFacade;
import ec.gob.ambiente.rcoa.facade.ObservacionesPreliminarFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.model.CatalogoGeneralCoa;
import ec.gob.ambiente.rcoa.model.DocumentosCOA;
import ec.gob.ambiente.rcoa.model.ObservacionesPreliminar;
import ec.gob.ambiente.rcoa.utils.DiasHabilesUtil;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.DigitalSign;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;

@ManagedBean
@ViewScoped
public class RevisarPronunciamientoDiagnosticoController implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private final Logger LOG = Logger.getLogger(RevisarPronunciamientoDiagnosticoController.class);
	
	@ManagedProperty(value = "#{bandejaTareasBean}")
    @Getter
    @Setter
    private BandejaTareasBean bandejaTareasBean;
	
	@Getter
    @Setter
    @ManagedProperty(value = "#{informeOficioDiagnosticoAmbientalBean}")
    private InformeOficioDiagnosticoAmbientalBean informeOficioDiagnosticoAmbientalBean;
	
	@EJB
	private ProcesoFacade procesoFacade;
	
	@EJB
    private UsuarioFacade usuarioFacade;
	
	@EJB
    private ObservacionesPreliminarFacade observacionesPreliminarFacade;
	
	@EJB
	private DocumentosCoaFacade documentoFacade;
	
	@EJB
	private CatalogoCoaFacade catalogoCoaFacade;
	
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	
	@EJB
	private DiasHabilesUtil diasHabilesUtil;
	
	@Getter
	@Setter
	private DocumentosCOA documentoManual;

	@Getter
	@Setter
	private Boolean requiereCorrecciones;
	
	@Getter
	@Setter
	private String urlReporte, nombreReporte, tipoOficio, nombreDocumentoFirmado;
	
	@Getter
	@Setter
	private byte[] archivoReporte;
	
	@Getter
	@Setter
	private boolean token, subido, firmaSoloToken;
	
	@Getter
	@Setter
	private Boolean descargaroficio = false;
	
	@Getter
	@Setter
	private Boolean oficioGuardado;
	
	@Getter
	@Setter
	private String urlAlfresco;

	@Setter
    @Getter
    private int activeIndex = 0;
	
	@PostConstruct
	private void iniciar() {
		try {
			
			urlAlfresco = "";
			oficioGuardado = false;
			
			firmaSoloToken = Constantes.getPropertyAsBoolean("ambiente.produccion") ;
			
			verificaToken();
			
			informeOficioDiagnosticoAmbientalBean.recuperarInformeTecnico();
			
			informeOficioDiagnosticoAmbientalBean.generarOficio(true);
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos.");
		}
	}
	
	public boolean verificaToken() {
		token = false;
		if (JsfUtil.getLoggedUser().getToken() != null
				&& JsfUtil.getLoggedUser().getToken())
			token = true;
		
		if(firmaSoloToken)
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
	
	public StreamedContent getStream(String name, byte[] fileContent) throws Exception {
		DefaultStreamedContent content = new DefaultStreamedContent();
		if (fileContent != null) {
			content = new DefaultStreamedContent(new ByteArrayInputStream(fileContent), "application/octet-stream");
			content.setName(name);
		}
		return content;
	}
	
	public void validarExisteObservacionesOficio() {
		try {
			requiereCorrecciones = false;
			List<ObservacionesPreliminar> observacionesOficio = observacionesPreliminarFacade.listarPorIdClaseNombreClaseNoCorregidas(
					informeOficioDiagnosticoAmbientalBean.getIdInformePrincipal(), "pronunciamientoDiagnosticoAmbiental");
			
			if(observacionesOficio.size() > 0) {
				requiereCorrecciones = true;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar las observaciones.");
		}
	}
	
	public void guardar() {
		informeOficioDiagnosticoAmbientalBean.guardarOficio();
		oficioGuardado = true;
		
		JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);

	}
	
	public void subirDocumento() {
		try {
			informeOficioDiagnosticoAmbientalBean.generarOficio(false);
			informeOficioDiagnosticoAmbientalBean.guardarDocumentoOficioAlfresco();

			if (informeOficioDiagnosticoAmbientalBean.getDocumentoOficioPronunciamiento() != null) {
				String documentOffice = documentoFacade.direccionDescarga(informeOficioDiagnosticoAmbientalBean.getDocumentoOficioPronunciamiento());
				urlAlfresco = DigitalSign.sign(documentOffice, JsfUtil.getLoggedUser().getNombre());
			}
		} catch (Exception exception) {
			LOG.error("Ocurrió un error al guardar el oficio", exception);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
		
		RequestContext.getCurrentInstance().update("formDialogs:");
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('signDialog').show();");
	}
	
	public StreamedContent descargar() throws IOException {
		DefaultStreamedContent content = null;
		try {
			
			byte[] documentoContent = informeOficioDiagnosticoAmbientalBean.getOficioPronunciamiento().getArchivoReporte();
			
			if (documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(informeOficioDiagnosticoAmbientalBean.getOficioPronunciamiento().getNombreReporte());
			} else {
				JsfUtil.addMessageError("Error al generar el archivo");
			}
			
			descargaroficio = true;
		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
	
	public void uploadListenerDocumentos(FileUploadEvent event) {
		if (descargaroficio == true) {
			documentoManual=new DocumentosCOA();
			byte[] contenidoDocumento = event.getFile().getContents();
			documentoManual.setContenidoDocumento(contenidoDocumento);
			documentoManual.setNombreDocumento(event.getFile().getFileName());
			documentoManual.setExtencionDocumento(".pdf");		
			documentoManual.setTipo("application/pdf");
			documentoManual.setNombreTabla("OficioDiagnosticoAmbiental");
			documentoManual.setIdTabla(informeOficioDiagnosticoAmbientalBean.getOficioPronunciamiento().getId());
			documentoManual.setProyectoLicenciaCoa(informeOficioDiagnosticoAmbientalBean.getProyecto());
			subido = true;
			JsfUtil.addMessageInfo("Documento subido exitosamente");
		} else{
			JsfUtil.addMessageError("No ha descargado el documento para la firma");
		}
	}
	
	public String firmarDocumento() {
		try {
			if(informeOficioDiagnosticoAmbientalBean.getDocumentoOficioPronunciamiento() != null) {
				String documentOffice = documentoFacade.direccionDescarga(informeOficioDiagnosticoAmbientalBean.getDocumentoOficioPronunciamiento());
				return DigitalSign.sign(documentOffice, JsfUtil.getLoggedUser().getNombre());
			} 
		} catch (Exception exception) {
			LOG.error("Ocurrió un error durante la firma del oficio", exception);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
		JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		return "";
	}
	
	public void enviar() {
		try {			
			Map<String, Object> parametros = new HashMap<>();
			
			try {
				
				parametros.put("observacionesPronunciamiento", requiereCorrecciones);
				
				procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);
				procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
				
				JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
				JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
			} catch (JbpmException e) {
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al enviar la tarea.");
		}
	}
	
	public void finalizar() {
		try {
			DocumentosCOA documentoFinal = null;
			
			if (token) {
				documentoFinal = informeOficioDiagnosticoAmbientalBean.getDocumentoOficioPronunciamiento();
				String idAlfresco = informeOficioDiagnosticoAmbientalBean.getDocumentoOficioPronunciamiento().getIdAlfresco();

				if (!documentoFacade.verificarFirmaVersion(idAlfresco)) {
					JsfUtil.addMessageError("El oficio de pronunciamiento no está firmado electrónicamente.");
					return;
				}
			} else {
				if(subido) {
					documentoFinal = documentoFacade.guardarDocumentoAlfresco(informeOficioDiagnosticoAmbientalBean.getProyecto().getCodigoUnicoAmbiental(), 
							"DIAGNOSTICO_AMBIENTAL", 1L, documentoManual, TipoDocumentoSistema.RCOA_OFICIO_DIAGNOSTICO_AMBIENTAL);
				} else {
					JsfUtil.addMessageError("Debe adjuntar el oficio de pronunciamiento firmado.");
					return;
				}				
			}
			
			Boolean esAprobacion = informeOficioDiagnosticoAmbientalBean.getOficioPronunciamiento().getTipoPronunciamiento().equals(2) ? true: false;
			if(esAprobacion) {
				CatalogoGeneralCoa catalogo = catalogoCoaFacade.obtenerCatalogoPorCodigo("dias.habiles.inicio.regularizacion.ambiental");
				
				Integer dias = Integer.valueOf(catalogo.getValor());
				
				Date fechaInicioRegularizacion = diasHabilesUtil.recuperarFechaFinal(new Date(), dias);
				
				informeOficioDiagnosticoAmbientalBean.getProyecto().setFechaInicioRegularizacionAmbiental(fechaInicioRegularizacion);
				
				proyectoLicenciaCoaFacade.guardar(informeOficioDiagnosticoAmbientalBean.getProyecto());
			}
			
			Map<String, Object> parametros = new HashMap<>();
			
			try {
				
				parametros.put("observacionesPronunciamiento", requiereCorrecciones);
				
				procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);
				
				procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
				
				if(!requiereCorrecciones) {
					documentoFinal.setIdProceso(bandejaTareasBean.getProcessId());
					documentoFacade.guardar(documentoFinal);
				}
				
				JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
				JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
			} catch (JbpmException e) {
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al enviar la tarea.");
		}
	}
}
