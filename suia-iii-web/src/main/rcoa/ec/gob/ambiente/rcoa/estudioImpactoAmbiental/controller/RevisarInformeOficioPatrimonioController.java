package ec.gob.ambiente.rcoa.estudioImpactoAmbiental.controller;
import java.io.ByteArrayInputStream;
import java.io.Serializable;
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
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.DocumentosImpactoEstudioFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.ObservacionesEsIAFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.DocumentoEstudioImpacto;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.InformeTecnicoEsIA;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.ObservacionesEsIA;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.OficioPronunciamientoEsIA;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.bandeja.BandejaFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.DigitalSign;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class RevisarInformeOficioPatrimonioController implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ManagedProperty(value = "#{bandejaTareasBean}")
    @Getter
    @Setter
    private BandejaTareasBean bandejaTareasBean;
	
	@ManagedProperty(value = "#{memoOficioPatrimonioBean}")
	@Getter
	@Setter
	private MemoOficioPatrimonioBean memoOficioPatrimonioBean;
	
	@ManagedProperty(value = "#{informeTecnicoEsIABean}")
	@Getter
	@Setter
	private InformeTecnicoEsIABean informeTecnicoEsIABean;
	
	@EJB
	private ProcesoFacade procesoFacade;
	
	@EJB
	private AreaFacade areaFacade;
	
	@EJB
	private UsuarioFacade usuarioFacade;
	
	@EJB
	private DocumentosFacade documentosGeneralFacade;
	
	@EJB
	private DocumentosImpactoEstudioFacade documentosFacade;

	@EJB
    private ObservacionesEsIAFacade observacionesEsIAFacade;
	
	@EJB
	private BandejaFacade bandejaFacade;
	
	@Getter
	@Setter
	private Boolean mostrarInforme, esReporteAprobacion, informeGuardado, oficioGuardado;
	
	@Getter
	@Setter
	private DocumentoEstudioImpacto documentoManual;
	
	@Getter
	@Setter
	private String urlAlfresco;
	
	@Getter
	@Setter
	private String urlReporte, nombreReporte, tipoOficio;
	
	@Getter
	@Setter
	private byte[] archivoReporte;
	
	@Getter
	@Setter
	private boolean token, descargado, subido, documentosAceptados, firmaSoloToken;

	@Getter
	@Setter
	private Boolean existeObservacionesEstudio, visualizarMemoOficio, requiereCorrecciones, esCoordinador, deshabilitarToken, esGad;
	
	@Getter
	@Setter
	private Integer idInformePrincipal;
	
	@PostConstruct
	private void iniciar() {
		JsfUtil.getLoggedUser().getNombre();
		try {
			
			firmaSoloToken = Constantes.getPropertyAsBoolean("ambiente.produccion");
			
			verificaToken();
			
			informeGuardado = false;
			oficioGuardado = false;
			documentosAceptados = false;
			deshabilitarToken = false;
		
			String tarea = bandejaFacade.getNombreTarea(JsfUtil.getCurrentTask().getTaskId());
			
			if(tarea.contains("Coord"))
				esCoordinador = true;
			else
				esCoordinador = false;
			
			
			visualizarMemoOficio = false;
			esGad = false;
			if(informeTecnicoEsIABean.getProyecto().getAreaResponsable().getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_PC)){
				//si es planta central se genera un memo 
				memoOficioPatrimonioBean.setTipoOficio(OficioPronunciamientoEsIA.memorando);
				visualizarMemoOficio = true;
			} else if (!informeTecnicoEsIABean.getProyecto().getAreaResponsable().getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_OT)) {
				//si es GAD se genera un oficio de respuesta por inventario
				memoOficioPatrimonioBean.setTipoOficio(OficioPronunciamientoEsIA.oficio);
				visualizarMemoOficio = true;
				esGad = true;
			}		
			
			informeTecnicoEsIABean.recuperarInformeTecnico();
			
			if(informeTecnicoEsIABean.getInformeTecnico() != null) {
				memoOficioPatrimonioBean.setIdInforme(informeTecnicoEsIABean.getInformeTecnico().getId());
				memoOficioPatrimonioBean.setInformeTecnico(informeTecnicoEsIABean.getInformeTecnico());
				
				idInformePrincipal = informeTecnicoEsIABean.getInformeTecnico().getIdInformePrincipal();
				if(idInformePrincipal == null)
					idInformePrincipal = informeTecnicoEsIABean.getInformeTecnico().getId();
			}
			
			if(visualizarMemoOficio) {
				memoOficioPatrimonioBean.cargarDatos();
				memoOficioPatrimonioBean.generarOficio(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al recuperar la información.");
		}
	}
	
	public void validarTareaBpm() {
		JsfUtil.validarPaginasUrlTareasBpm(JsfUtil.getCurrentTask(),
				"/pages/rcoa/estudioImpactoAmbiental/revisarInformeOficioPatrimonio.jsf");
	}
	
	public boolean verificaToken() {
		if(firmaSoloToken) {
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void validarExisteObservacionesInformeOficio() {
		try {
			requiereCorrecciones = false;
			List<ObservacionesEsIA> observaciones= observacionesEsIAFacade.listarPorIdClaseNombreClaseNoCorregidas(
					idInformePrincipal, "InfomeOficioPatrimonio");
			
			if(observaciones.size() > 0) {
				requiereCorrecciones = true;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al recuperar las observaciones.");
		}
	}
	
	public void guardarInformeOficio() {
		try {
			
			documentosAceptados = true;
			
			JsfUtil.addMessageInfo(JsfUtil.REGISTRO_GUARDADO);
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
		}
	}
	
	public void enviar() {
		try {			
			Map<String, Object> parametros = new HashMap<>();
			
			if(informeTecnicoEsIABean.getInformeTecnico().getTipoInforme().equals(InformeTecnicoEsIA.snap))
				parametros.put("observacionesPronunciamientoSnap", requiereCorrecciones);
			else if(informeTecnicoEsIABean.getInformeTecnico().getTipoInforme().equals(InformeTecnicoEsIA.forestal))
				parametros.put("observacionesBosquesInterseccion", requiereCorrecciones);
			else
				parametros.put("observacionesPronunciamientoInventario", requiereCorrecciones);
			
				
			procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
				
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
			JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
		}
	}
	
	public void guardarDocumentos() {
		try {
			
			if(esCoordinador) {
				if(informeTecnicoEsIABean.getDocumentoInforme() != null) {
					if (documentosFacade.verificarFirmaVersion(informeTecnicoEsIABean.getDocumentoInforme().getAlfrescoId())) {
						deshabilitarToken = true;
						token = true;
					}
					
					String documentOffice = documentosFacade.direccionDescarga(informeTecnicoEsIABean.getDocumentoInforme());
					urlAlfresco = DigitalSign.sign(documentOffice, JsfUtil.getLoggedUser().getNombre());
				}
			} else {
				//es autoridad
				memoOficioPatrimonioBean.guardarDocumentosAlfresco();
				
				if (memoOficioPatrimonioBean.getDocumentoOficio() != null) {
					String documentOffice = documentosFacade.direccionDescarga(memoOficioPatrimonioBean.getDocumentoOficio());
					urlAlfresco = DigitalSign.sign(documentOffice, JsfUtil.getLoggedUser().getNombre());
				}
			}
			
		} catch (Exception exception) {
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
		
		RequestContext.getCurrentInstance().update("formDialogFirma:");
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('signDialog').show();");
	}
	
	public StreamedContent descargar(Integer tipoDocumento) {
		DefaultStreamedContent content = null;
		try {
			byte[] documentoContent = null;
			String nombre = "";
			if(tipoDocumento.equals(1)) {
				documentoContent = informeTecnicoEsIABean.getArchivoReporte();
				nombre = informeTecnicoEsIABean.getNombreReporte();
			} else {
				documentoContent = memoOficioPatrimonioBean.getArchivoReporte();
				nombre = memoOficioPatrimonioBean.getNombreReporte();
			}
			
			if (documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(nombre);
			} else
				JsfUtil.addMessageError("Error al generar el archivo");

			descargado = true;
		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
	
	public void uploadListenerDocumentos(FileUploadEvent event) {
		if (descargado== true) {
			byte[] contenidoDocumento = event.getFile().getContents();

			documentoManual=new DocumentoEstudioImpacto();
			documentoManual.setContenidoDocumento(contenidoDocumento);
			documentoManual.setNombre(event.getFile().getFileName());
			documentoManual.setExtesion(".pdf");		
			documentoManual.setMime("application/pdf");

			subido = true;
			JsfUtil.addMessageInfo("Documento subido exitosamente");
		} else{
			JsfUtil.addMessageError("No ha descargado el documento para la firma");
		}
	}
	
	public void finalizar() {		
		try {
			String tipoDocumento = "informe técnico";
			if(!esCoordinador)
				tipoDocumento = (memoOficioPatrimonioBean.getTipoOficio().equals(OficioPronunciamientoEsIA.memorando)) ? "memorando" : "oficio de pronunciamiento";
			
			if (token) {
				String idAlfresco = informeTecnicoEsIABean.getDocumentoInforme().getAlfrescoId();
				if(!esCoordinador) {
					idAlfresco = memoOficioPatrimonioBean.getDocumentoOficio().getAlfrescoId();
				}
				
				if (!documentosFacade.verificarFirmaVersion(idAlfresco)) {
					JsfUtil.addMessageError("El " + tipoDocumento + " no está firmado electrónicamente.");
					return;
				}
			} else {
				if(subido) {
					if(esCoordinador)
						informeTecnicoEsIABean.guardarDocumentoFirmaManual(documentoManual);
					else
						memoOficioPatrimonioBean.guardarDocumentoFirmaManual(documentoManual);
				} else {
					JsfUtil.addMessageError("Debe adjuntar el " + tipoDocumento + " firmado.");
					return;
				}				
			}
			
			Map<String, Object> parametros = new HashMap<>();
			
			if(informeTecnicoEsIABean.getInformeTecnico().getTipoInforme().equals(InformeTecnicoEsIA.snap))
				parametros.put("observacionesPronunciamientoSnap", requiereCorrecciones);
			else if(informeTecnicoEsIABean.getInformeTecnico().getTipoInforme().equals(InformeTecnicoEsIA.forestal))
				parametros.put("observacionesBosquesInterseccion", requiereCorrecciones);
			else
				parametros.put("observacionesPronunciamientoInventario", requiereCorrecciones);
			
			if(esCoordinador) {
				if(informeTecnicoEsIABean.getInformeTecnico().getTipoInforme().equals(InformeTecnicoEsIA.snap))
					parametros.put("directorConservacion", memoOficioPatrimonioBean.getUsuarioAutoridad().getNombre());
				else {
					if(!esGad)
						parametros.put("directorBosques", memoOficioPatrimonioBean.getUsuarioAutoridad().getNombre());
					else
						parametros.put("usuarioAutoridadInventario", memoOficioPatrimonioBean.getUsuarioAutoridad().getNombre());
				}
			}
			
			procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);
			
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
			
			if(!esCoordinador) {
				//si firma la autoridad establezco los ids de procesos para visualización en resumen de tareas
				informeTecnicoEsIABean.getDocumentoInforme().setIdProceso(bandejaTareasBean.getProcessId());
				documentosFacade.guardar(informeTecnicoEsIABean.getDocumentoInforme());
				
				memoOficioPatrimonioBean.getDocumentoOficio().setIdProceso(bandejaTareasBean.getProcessId());
				documentosFacade.guardar(memoOficioPatrimonioBean.getDocumentoOficio());
			}
			
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
			JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
		}
	}
	
}
