package ec.gob.ambiente.rcoa.firmaMasiva.controllers;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

import org.kie.api.task.model.TaskSummary;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.control.retce.beans.InformeTecnicoOficioGeneradorBean;
import ec.gob.ambiente.control.retce.controllers.RevisionInformeOficioEmisionesController;
import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.prevencion.certificado.ambiental.controllers.CertificadoAmbientalController;
import ec.gob.ambiente.prevencion.registrogeneradordesechos.controller.FirmarDocumentosRGController;
import ec.gob.ambiente.rcoa.certificado.interseccion.facade.CertificadoInterseccionCoaFacade;
import ec.gob.ambiente.rcoa.dto.DocumentoTareaDTO;
import ec.gob.ambiente.rcoa.facade.DocumentosCertificadoAmbientalFacade;
import ec.gob.ambiente.rcoa.facade.DocumentosCoaFacade;
import ec.gob.ambiente.rcoa.facade.DocumentosProcesoRcoaFacade;
import ec.gob.ambiente.rcoa.facade.DocumentosRegistroAmbientalFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.facade.RegistroAmbientalCoaFacade;
import ec.gob.ambiente.rcoa.facade.TareaFirmaMasivaFacade;
import ec.gob.ambiente.rcoa.model.DocumentoCertificadoAmbiental;
import ec.gob.ambiente.rcoa.model.DocumentoRegistroAmbiental;
import ec.gob.ambiente.rcoa.model.DocumentosCOA;
import ec.gob.ambiente.rcoa.model.ProyectoCertificadoAmbiental;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.RegistroAmbientalRcoa;
import ec.gob.ambiente.rcoa.model.TareaFirmaMasiva;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.controllers.InformeOficioPquaBean;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.facade.DocumentoPquaFacade;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.facade.OficioPronunciamientoPquaFacade;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.facade.ProyectoPlaguicidasFacade;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.DocumentoPqua;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.OficioPronunciamientoPqua;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.ProyectoPlaguicidas;
import ec.gob.ambiente.rcoa.preliminar.contoller.GeneracionDocumentosController;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.controllers.FirmarPronunciamientoPermisoRGDController;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.DocumentosRgdRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.DocumentosRgdRcoa;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.DocumentosRSQFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.SolicitudImportacionRSQFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.DocumentosSustanciasQuimicasRcoa;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.SolicitudImportacionRSQ;
import ec.gob.ambiente.rcoa.sustanciasQuimicasImportacion.controllers.FirmarAutorizacionAnulacionController;
import ec.gob.ambiente.rcoa.util.NotificacionInternaUtil;
import ec.gob.ambiente.suia.administracion.facade.FlujosCategoriaFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.BandejaFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.Flujo;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.dto.TaskSummaryCustom;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.BeanLocator;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.DigitalSign;
import ec.gob.ambiente.suia.utils.Email;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class FirmaElectronicaMasivaController implements Serializable{

	private static final long serialVersionUID = -875087443147320594L;

	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;
	
	@Getter
    @Setter
    @ManagedProperty(value = "#{certificadoAmbientalController}")
    private CertificadoAmbientalController certificadoAmbientalController;
	
	@ManagedProperty(value = "#{informeOficioPquaBean}")
	@Getter
	@Setter
	private InformeOficioPquaBean informeOficioPquaBean;

	@EJB
	private ProcesoFacade procesoFacade;
	@EJB
	private DocumentosCoaFacade documentosFacade;
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	@EJB
	private FlujosCategoriaFacade flujosCategoriaFacade;
	@EJB
	private DocumentosProcesoRcoaFacade documentosProcesoRcoaFacade;
	@EJB
	private TareaFirmaMasivaFacade tareaFirmaMasivaFacade;
	@EJB
	private CertificadoInterseccionCoaFacade certificadoInterseccionCoaFacade;
	@EJB
	private UsuarioFacade usuarioFacade;
	@EJB
	private DocumentosRegistroAmbientalFacade documentosRegistroFacade;
	@EJB
	private DocumentosCertificadoAmbientalFacade documentosCertificadoFacade;
	@EJB
	private RegistroAmbientalCoaFacade registroAmbientalCoaFacade;
	@EJB
	private BandejaFacade bandejaFacade;
	@EJB
	private OrganizacionFacade organizacionFacade;
	@EJB
	private MensajeNotificacionFacade mensajeNotificacionFacade;
	@EJB
	private DocumentosRgdRcoaFacade documentosRgdRcoaFacade;
	@EJB
    private ProyectoPlaguicidasFacade proyectoPlaguicidasFacade;
    @EJB
	private OficioPronunciamientoPquaFacade oficioPronunciamientoPquaFacade;
	@EJB
	private DocumentoPquaFacade documentoPquaFacade;
	@EJB
	private DocumentosRSQFacade documentoRSQFacade;
	@EJB
	private SolicitudImportacionRSQFacade solicitudImportacionRSQFacade;


	@Getter
	@Setter
	private DocumentosCOA documento;
	
	@Getter
	@Setter
	private String idsDocumentosAlfresco;
	
	@Getter
	@Setter
	private List<DocumentoTareaDTO> listaDocumentoTarea; 
	
	@Getter
	@Setter
	private Boolean habilitarForm;

	private RevisionInformeOficioEmisionesController oficioEAController = JsfUtil.getBean(RevisionInformeOficioEmisionesController.class);
	private InformeTecnicoOficioGeneradorBean informeTecnicoOficioGeneradorBean = JsfUtil.getBean(InformeTecnicoOficioGeneradorBean.class);
	private FirmarPronunciamientoPermisoRGDController oficioController = JsfUtil.getBean(FirmarPronunciamientoPermisoRGDController.class);
	private FirmarDocumentosRGController firmarDocumentoRgdSuiaController = JsfUtil.getBean(FirmarDocumentosRGController.class);
	
	@PostConstruct
	public void iniciar() throws JbpmException, NumberFormatException, CmisAlfrescoException
	{
		if(bandejaTareasBean.getTareasFirmaMasiva() == null || bandejaTareasBean.getTareasFirmaMasiva().size() == 0) {
			JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
			return;
		}
		
		listaDocumentoTarea = new ArrayList<>();
		idsDocumentosAlfresco = "";
		habilitarForm = true;
		
		for(TaskSummaryCustom tarea : bandejaTareasBean.getTareasFirmaMasiva()) {
			Flujo flujo = flujosCategoriaFacade.getFlujoPorIdProceso(tarea.getProcessId());
			if(flujo != null) {
				TareaFirmaMasiva tareaFirma = tareaFirmaMasivaFacade.obtenerPorFlujoTarea(flujo.getId(), tarea.getTaskName());
				
				if(tareaFirma != null) {
					String clave = tareaFirma.getCodigoTarea();
					switch (clave) {
					case "firma.oficio.archivacion":
						generarOficioArchivacion(tarea);
						break;
					case "firma.registro.ambiental":
						generarResolucionRegistroAmbiental(tarea);
						break;
					case "firma.certificado.ambiental":
						generarCertificadoAmbiental(tarea);
						break;
					case "firma.pronunciamiento.retce":
					case "firma.pronunciamiento|.retce":
						generarPronunciamientoRtece(tarea);
						break;
					case "firma.pronunciamiento.rgd.rep.aaa":
						generarPronunciamientoRgdRcoa(tarea);
						break;
					case "firma.pronunciamiento.rgd.suia":
						generarPronunciamientoRgdSuia(tarea);
						break;
					case "firma.pqua.oficio.observacion":
						generarDocumentosPlaguicidas(tarea, false);
						break;
					case "firma.pqua.oficio.aprobacion":
						generarDocumentosPlaguicidas(tarea, true);
						break;
					case "firma.importacion.autorizacion":
						generarAutorizacionAnulacionImportacion(tarea);
						break;
					case "firma.importacion.anulacion":
						generarAutorizacionAnulacionImportacion(tarea);
						break;

					default:
						
					}
				}
			}
		}
		
		if(idsDocumentosAlfresco.equals("")) {
			JsfUtil.addMessageError("Error al recuperar los documentos a firmar.");
			habilitarForm = false;
		}
		
		bandejaTareasBean.setTareasFirmaMasiva(null);
	}
	
	public void validarTarea() {
		if(idsDocumentosAlfresco.equals(""))
			JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
	}
	
	public void generarOficioArchivacion(TaskSummaryCustom tarea) {
		try {
			Map<String, Object> variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), tarea.getProcessInstanceId());
			String tramite = (String) variables.get("u_tramite");
			Boolean esZonificacionBiodiversidad =  Boolean.parseBoolean(variables.get("zonificacionBiodiversidad").toString()) ;
			
			ProyectoLicenciaCoa proyecto = proyectoLicenciaCoaFacade.buscarProyecto(tramite);
			
			GeneracionDocumentosController oficioController = (GeneracionDocumentosController) BeanLocator.getInstance(GeneracionDocumentosController.class);
			
			DocumentosCOA documento = oficioController.generarOficioArchivacion(proyecto, esZonificacionBiodiversidad);
			
			if(documento != null) {
				String idAlfrescoWks = (documento.getIdAlfresco().split(";"))[0];
				String documentUrl = getUrlAlfresco(documento.getIdAlfresco());
				
				idsDocumentosAlfresco += (idsDocumentosAlfresco.equals("")) ? documentUrl : ";" + documentUrl;
				
				DocumentoTareaDTO docTarea = new DocumentoTareaDTO();
				docTarea.setIdAlfresco(idAlfrescoWks);
				docTarea.setNombreDocumento(documento.getNombreDocumento());
				docTarea.setTarea(tarea);
				docTarea.setDocumentoCoa(documento);
				
				listaDocumentoTarea.add(docTarea);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void generarResolucionRegistroAmbiental(TaskSummaryCustom tarea) {
		try {
			Map<String, Object> variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), tarea.getProcessInstanceId());
			String tramite = (String) variables.get("tramite");
			
			ProyectoLicenciaCoa proyecto = proyectoLicenciaCoaFacade.buscarProyecto(tramite);
			
			Boolean generarNuevo = false;
			DocumentoRegistroAmbiental documento = documentosRegistroFacade.documentoXTablaIdXIdDoc(proyecto.getId(), ProyectoLicenciaCoa.class.getSimpleName(), TipoDocumentoSistema.RESOLUCION_AMBIENTAL_RCOA);
			if(documento != null && !documento.getUsuarioCreacion().equals(loginBean.getNombreUsuario()))
				generarNuevo = true;
			else if (documento == null)
				generarNuevo = true;
			
			if(generarNuevo){
				GeneracionDocumentosController oficioController = (GeneracionDocumentosController) BeanLocator.getInstance(GeneracionDocumentosController.class);
				
				documento = oficioController.generarResolucionRegistroAmbiental(proyecto);
			}
			
			if(documento != null) {
				String idAlfrescoWks = (documento.getAlfrescoId().split(";"))[0];
				String documentUrl = getUrlAlfresco(documento.getAlfrescoId());
				
				idsDocumentosAlfresco += (idsDocumentosAlfresco.equals("")) ? documentUrl : ";" + documentUrl;
				
				DocumentoTareaDTO docTarea = new DocumentoTareaDTO();
				docTarea.setIdAlfresco(idAlfrescoWks);
				docTarea.setNombreDocumento(documento.getNombre());
				docTarea.setTarea(tarea);
				docTarea.setDocumentoRegistroAmbiental(documento);
				docTarea.setNumeroTramite(tramite);
				
				listaDocumentoTarea.add(docTarea);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void generarCertificadoAmbiental(TaskSummaryCustom tarea) {
		try {
			
			Map<String, Object> variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), tarea.getProcessInstanceId());
			String tramite = (String) variables.get("tramite");
			
			ProyectoLicenciaCoa proyecto = proyectoLicenciaCoaFacade.buscarProyecto(tramite);
			
			DocumentoCertificadoAmbiental documentoCertificado = null;
			List<DocumentoCertificadoAmbiental> listaDocumentos = new ArrayList<>();
			
			listaDocumentos = documentosCertificadoFacade.documentoXTablaIdXIdDoc(proyecto.getId(), ProyectoCertificadoAmbiental.class.getSimpleName(), TipoDocumentoSistema.TIPO_CERTIFICADO_AMBIENTAL_MAE);					
			
			if(listaDocumentos != null && !listaDocumentos.isEmpty()){
				documentoCertificado = listaDocumentos.get(0);
			}else{
				listaDocumentos = documentosCertificadoFacade.documentoXTablaIdXIdDoc(proyecto.getId(), ProyectoCertificadoAmbiental.class.getSimpleName(), TipoDocumentoSistema.TIPO_CERTIFICADO_AMBIENTAL_GAD);
				if(listaDocumentos != null && !listaDocumentos.isEmpty())
					documentoCertificado = listaDocumentos.get(0);
			}
			
			Boolean generarNuevo = false;
			if (documentoCertificado == null)
				generarNuevo = true;
			else if(documentoCertificado != null && (!documentoCertificado.getUsuarioCreacion().equals(loginBean.getNombreUsuario()))
					|| !JsfUtil.getSimpleDateFormat(documentoCertificado.getFechaCreacion()).equals(JsfUtil.getSimpleDateFormat(new Date())))
				generarNuevo = true;
			
			if(generarNuevo){
				certificadoAmbientalController.enviarFicha(proyecto.getId(), 0L);
				
				listaDocumentos = documentosCertificadoFacade.documentoXTablaIdXIdDoc(proyecto.getId(), ProyectoCertificadoAmbiental.class.getSimpleName(), TipoDocumentoSistema.TIPO_CERTIFICADO_AMBIENTAL_MAE);					
				
				if(listaDocumentos != null && !listaDocumentos.isEmpty()){
					documentoCertificado = listaDocumentos.get(0);
				}else{
					listaDocumentos = documentosCertificadoFacade.documentoXTablaIdXIdDoc(proyecto.getId(), ProyectoCertificadoAmbiental.class.getSimpleName(), TipoDocumentoSistema.TIPO_CERTIFICADO_AMBIENTAL_GAD);
					documentoCertificado = listaDocumentos.get(0);
				}
			}
			
			if(documentoCertificado != null) {
				String idAlfrescoWks = (documentoCertificado.getAlfrescoId().split(";"))[0];
				String documentUrl = getUrlAlfresco(documentoCertificado.getAlfrescoId());
				
				idsDocumentosAlfresco += (idsDocumentosAlfresco.equals("")) ? documentUrl : ";" + documentUrl;
				
				DocumentoTareaDTO docTarea = new DocumentoTareaDTO();
				docTarea.setIdAlfresco(idAlfrescoWks);
				docTarea.setNombreDocumento(documentoCertificado.getNombre());
				docTarea.setTarea(tarea);
				docTarea.setDocumentoCertificadoAmbiental(documentoCertificado);
				docTarea.setNumeroTramite(tramite);
				
				listaDocumentoTarea.add(docTarea);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String getUrlAlfresco(String documento) {
		String urlAlfresco = null;
		try {
			urlAlfresco = documentosFacade.direccionDescarga(documento);
			String tiketStr = "alf_ticket=";
			if (!urlAlfresco.endsWith(tiketStr)) {
				int pos = urlAlfresco.lastIndexOf(tiketStr) + tiketStr.length();
				urlAlfresco = urlAlfresco.substring(0, pos);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return urlAlfresco;
	}

	public void completarTarea() {
		try {
			String tramitesNoFinalizados = "";
			for (DocumentoTareaDTO item : listaDocumentoTarea) {
				TaskSummaryCustom tarea = item.getTarea();
				if (!documentosFacade.verificarFirmaVersion(item.getIdAlfresco())) {
					if(!tramitesNoFinalizados.contains(tarea.getProcedure()))
						tramitesNoFinalizados += "<br />" + tarea.getProcedure();
				} else {
					DocumentosCOA documento = item.getDocumentoCoa();
					if(documento != null) {							
						documento.setIdProceso(tarea.getProcessInstanceId());
						documentosFacade.guardar(documento);
					}
					
					DocumentoRegistroAmbiental documentoRA = item.getDocumentoRegistroAmbiental();
					if(documentoRA != null) {							
						documentoRA.setIdProceso(tarea.getProcessInstanceId());
						documentosRegistroFacade.guardar(documentoRA);
					}
					
					DocumentoCertificadoAmbiental documentoCA = item.getDocumentoCertificadoAmbiental();
					if(documentoCA != null) {							
						documentoCA.setIdProceso(tarea.getProcessInstanceId());
						documentosCertificadoFacade.guardar(documentoCA);
					}
					
					Documento documentoRetce = item.getDocumentoPronunciamientoRetce();
					if(documentoRetce != null) {	
						//guardar en documento_tareas_procesos
					}
					
					DocumentosRgdRcoa documentoRgdRcoa = item.getDocumentoRgdRcoa();
					boolean completado=true;
					if(documentoRgdRcoa != null) {
						item.setProcesado(true);
						documentoRgdRcoa.setIdProceso(Long.valueOf(tarea.getProcessInstanceId()).intValue());
						documentosRgdRcoaFacade.save(documentoRgdRcoa, JsfUtil.getLoggedUser());
						//valido si existe algun documento del mismo proceso por guardar
						for (DocumentoTareaDTO itemAux : listaDocumentoTarea) {
							if(tarea.getProcessInstanceId() == itemAux.getTarea().getProcessInstanceId()){
								if(itemAux.getProcesado() == null || !itemAux.getProcesado()){
									completado=false;	
								}
							}
						}
					}
					
					DocumentosSustanciasQuimicasRcoa documentoImportacion = item.getDocumentoImportacion();
					if(documentoImportacion != null){
						
						documentoImportacion.setProcessinstanceid(Long.valueOf(tarea.getProcessInstanceId()).intValue());
						documentoRSQFacade.guardar(documentoImportacion);
					}
					
					// si falta guardar documentos porque en el proyecto tiene que firmar mas de un documento
					if(!completado){
						continue;
					}
					Map<String, Object> parametrosE = new HashMap<>();
					parametrosE.put("firmadoMedianteFirmaMasiva", true);
					if(tarea.getProcessId().equals("mae-procesos.RegistroEmisionesTransferenciaContaminantesEcuador")){
						bandejaTareasBean.setProcessId(tarea.getProcessInstanceId());
						informeTecnicoOficioGeneradorBean = JsfUtil.getBean(InformeTecnicoOficioGeneradorBean.class);
						informeTecnicoOficioGeneradorBean.init();
						informeTecnicoOficioGeneradorBean.getOficio();
						parametrosE.put("pronunciamiento_aprobado", informeTecnicoOficioGeneradorBean.getInforme().getEsReporteAprobacion());
					}
					
					procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), tarea.getProcessInstanceId(), parametrosE);
					procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), tarea.getTaskId(), tarea.getProcessInstanceId(), null);
					
					String estadoFirma = tarea.getProcessId();
					
					switch (estadoFirma) {
					case "rcoa.RegistroAmbiental":
						ejecutarAdicionalesRegistro(item.getNumeroTramite(), tarea);
						break;
					case "rcoa.CertificadoAmbiental":
						ejecutarAdicionalesCertificado(item.getNumeroTramite(), tarea);
						break;
					case "mae-procesos.RegistroEmisionesTransferenciaContaminantesEcuador":
						ejecutarAdicionalesRetce(item.getNumeroTramite(), tarea, informeTecnicoOficioGeneradorBean);
						break;
					case "rcoa.RegistrodeGeneradordeDesechosPeligrososyEspeciales":
						ejecutarAdicionalesRgdRcoa(item.getNumeroTramite(), tarea);
						break;
					case "rcoa.ActualizacionEtiquetadoPlaguicidasUsoAgricola":
						ejecutarAdicionalesPlaguicidas(item, tarea);
						break;
					case "rcoa.RegistroSustanciasQuimicasImportacion":
						ejecutarAdicionalesImportacion(item.getNumeroTramite(), item.getAutorizacion(), tarea);
						break;
					default:
						break;
					}
				}
			}
			
			if(tramitesNoFinalizados.equals(""))
				JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
			else
				JsfUtil.addMessageError("Los siguientes trámites no finalizaron: " + tramitesNoFinalizados);
			
			JsfUtil.redirectTo(JsfUtil.NAVIGATION_TO_BANDEJA);

		} catch (Exception e) {
			JsfUtil.addMessageError("Error al realizar la operación.");
		}
	}
	
	public String firmarDocumento() {
		try {
			if(!idsDocumentosAlfresco.equals("")) {
				return DigitalSign.sign(idsDocumentosAlfresco, loginBean.getUsuario().getNombre()); 
			}
		} catch (Throwable e) {
			JsfUtil.addMessageError("Error al realizar la operación.");
		}
		
		return "";
	}
	
	public StreamedContent descargarDocumento(DocumentoTareaDTO itemFirma) {
		DefaultStreamedContent content = null;
		try {
			byte[] documentoContent = null;
			String nombreDocumento = "";
			
			documentoContent = documentosFacade.descargar(itemFirma.getIdAlfresco());
			nombreDocumento = itemFirma.getNombreDocumento();
			
			if (documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(nombreDocumento);
			} 
			else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
	
	public void ejecutarAdicionalesRegistro(String tramite, TaskSummaryCustom infoTarea)  {
		try{
			ProyectoLicenciaCoa proyecto = proyectoLicenciaCoaFacade.buscarProyecto(tramite);
			
			// finalizo el proyecto
			proyecto.setProyectoFinalizado(true);
			proyecto.setProyectoFechaFinalizado(new Date());
			proyectoLicenciaCoaFacade.guardar(proyecto);
			
			RegistroAmbientalRcoa registroAmbientalRcoa = registroAmbientalCoaFacade.obtenerRegistroAmbientalPorProyecto(proyecto);
			registroAmbientalRcoa.setFechaFinalizacion(new Date());
			registroAmbientalRcoa.setFinalizado(true);
			registroAmbientalCoaFacade.guardar(registroAmbientalRcoa);
			
			//notificacion usuarios internos
			NotificacionInternaUtil.enviarNotificacionRegistro(proyecto, registroAmbientalRcoa.getNumeroResolucion());
			
			//para finalizar la tarea de encuesta
			List<TaskSummary> listaTareas = procesoFacade.getTaskReservedInProgress(JsfUtil.getLoggedUser(), infoTarea.getProcessInstanceId());
			if(listaTareas != null && listaTareas.size() > 0) {				
				for (TaskSummary tarea : listaTareas) {
					String nombretarea = bandejaFacade.getNombreTarea(tarea.getId());
					if(nombretarea.equals("encuestaYDescargaDocumentos")) {
						Usuario usuarioTarea = usuarioFacade.buscarUsuario(tarea.getActualOwner().getId());
	
						String fechaAvance = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss:SS").format(new Date());
	
						Map<String, Object> parametros = new HashMap<>();
				
						parametros.put("finalizadoAutomaticoSisEncuestaRA", fechaAvance);
						
						procesoFacade.modificarVariablesProceso(usuarioTarea, infoTarea.getProcessInstanceId(), parametros);
	
						procesoFacade.aprobarTarea(usuarioTarea, tarea.getId(), infoTarea.getProcessInstanceId(), null);
	
						registroAmbientalRcoa.setFinalizado(true);
						registroAmbientalCoaFacade.guardar(registroAmbientalRcoa);
								
						break;
					} 
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void ejecutarAdicionalesCertificado(String tramite, TaskSummaryCustom infoTarea) {
		try {
			ProyectoLicenciaCoa proyecto = proyectoLicenciaCoaFacade.buscarProyecto(tramite);
			
			proyecto.setProyectoFinalizado(true);
			proyecto.setProyectoFechaFinalizado(new Date());			
			proyectoLicenciaCoaFacade.guardar(proyecto);
			
			//para finalizar la tarea de encuesta
			List<TaskSummary> listaTareas = procesoFacade.getTaskReservedInProgress(JsfUtil.getLoggedUser(), infoTarea.getProcessInstanceId());
			if(listaTareas != null && listaTareas.size() > 0) {
				for (TaskSummary tarea : listaTareas) {
					String nombretarea = bandejaFacade.getNombreTarea(tarea.getId());
					if(nombretarea.equals("descargarCertificadoAmbiental")) {
						Usuario usuarioTarea = usuarioFacade.buscarUsuario(tarea.getActualOwner().getId());

						String fechaAvance = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss:SS").format(new Date());

						Map<String, Object> parametrosE = new HashMap<>();
				
						parametrosE.put("finalizadoAutomaticoSisEncuestaCA", fechaAvance);
						
						procesoFacade.modificarVariablesProceso(usuarioTarea, infoTarea.getProcessInstanceId(), parametrosE);

						procesoFacade.aprobarTarea(usuarioTarea, tarea.getId(), infoTarea.getProcessInstanceId(), null);
								
						break;
					} 
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void generarPronunciamientoRtece(TaskSummaryCustom tarea) {
		try {
			bandejaTareasBean.setProcessId(tarea.getProcessInstanceId());
			Documento documentoOficioPronunciamiento = null;
			Map<String, Object> variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), tarea.getProcessInstanceId());
			String TipoTramite = (String) variables.get("tipo_tramite");
			switch (TipoTramite) {
			case "emisionesAtmosfericas":
				oficioEAController.init();
				oficioEAController.descargar();
				documentoOficioPronunciamiento = oficioEAController.getDocumentoOficio();
				break;
			case "generadorDesechos":
				informeTecnicoOficioGeneradorBean.init();;
				informeTecnicoOficioGeneradorBean.getOficio();
				informeTecnicoOficioGeneradorBean.generarOficio(false);
				informeTecnicoOficioGeneradorBean.guardarOficio();
				informeTecnicoOficioGeneradorBean.guardarDocumentoOficio();
				informeTecnicoOficioGeneradorBean.subirInforme();
				documentoOficioPronunciamiento = informeTecnicoOficioGeneradorBean.getDocumentoOficioPronunciamiento();
				break;
			default:
				break;
			}
			
			if(documentoOficioPronunciamiento != null) {
				String idAlfrescoWks = (documentoOficioPronunciamiento.getIdAlfresco().split(";"))[0];
				String documentUrl = getUrlAlfresco(documentoOficioPronunciamiento.getIdAlfresco());
				idsDocumentosAlfresco += (idsDocumentosAlfresco.equals("")) ? documentUrl : ";" + documentUrl;
				DocumentoTareaDTO docTarea = new DocumentoTareaDTO();
				docTarea.setIdAlfresco(idAlfrescoWks);
				docTarea.setNombreDocumento(documentoOficioPronunciamiento.getNombre());
				docTarea.setTarea(tarea);
				docTarea.setDocumentoPronunciamientoRetce(documentoOficioPronunciamiento);
				listaDocumentoTarea.add(docTarea);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void ejecutarAdicionalesRetce(String tramite, TaskSummaryCustom infoTarea, InformeTecnicoOficioGeneradorBean informeTecnicoOficioGeneradorBean)  {
		try{
			bandejaTareasBean.setProcessId(infoTarea.getProcessInstanceId());
			Map<String, Object> variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), infoTarea.getProcessInstanceId());
			String usuario = (String) variables.get("usuario_operador");
			Usuario usuarioOperador=usuarioFacade.buscarUsuario(usuario);
			Organizacion organizacion = organizacionFacade.buscarPorRuc(usuario);
			String nombreOperador = JsfUtil.getNombreOperador(usuarioOperador, organizacion);
			Object[] parametrosCorreo = new Object[] {nombreOperador, informeTecnicoOficioGeneradorBean.getGeneradorDesechosRetce().getCodigoGenerador() };
			String notificacion = mensajeNotificacionFacade.recuperarValorMensajeNotificacion("bodyNotificacionPronunciamientoEmitido", parametrosCorreo);
			
			Email.sendEmail(usuarioOperador, "Pronunciamiento RETCE emitido", notificacion, informeTecnicoOficioGeneradorBean.getInformacionProyecto().getCodigo(), JsfUtil.getLoggedUser());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void generarPronunciamientoRgdRcoa(TaskSummaryCustom tarea) {
		try {
			bandejaTareasBean.setProcessId(tarea.getProcessInstanceId());
			oficioController.init();
			oficioController.crearDocumentos();

			DocumentosRgdRcoa documentoOficioPronunciamiento = oficioController.getDocumentoPermiso();
			if(documentoOficioPronunciamiento != null) {
				String idAlfrescoWks = (documentoOficioPronunciamiento.getIdAlfresco().split(";"))[0];
				String documentUrl = getUrlAlfresco(documentoOficioPronunciamiento.getIdAlfresco());
				idsDocumentosAlfresco += (idsDocumentosAlfresco.equals("")) ? documentUrl : ";" + documentUrl;
				DocumentoTareaDTO docTarea = new DocumentoTareaDTO();
				docTarea.setIdAlfresco(idAlfrescoWks);
				docTarea.setNombreDocumento(documentoOficioPronunciamiento.getNombre());
				docTarea.setTarea(tarea);
				docTarea.setDocumentoRgdRcoa(documentoOficioPronunciamiento);
				listaDocumentoTarea.add(docTarea);
			}
			documentoOficioPronunciamiento = oficioController.getDocumentoOficio();
			if(documentoOficioPronunciamiento != null) {
				String idAlfrescoWks = (documentoOficioPronunciamiento.getIdAlfresco().split(";"))[0];
				String documentUrl = getUrlAlfresco(documentoOficioPronunciamiento.getIdAlfresco());
				idsDocumentosAlfresco += (idsDocumentosAlfresco.equals("")) ? documentUrl : ";" + documentUrl;
				DocumentoTareaDTO docTarea = new DocumentoTareaDTO();
				docTarea.setIdAlfresco(idAlfrescoWks);
				docTarea.setNombreDocumento(documentoOficioPronunciamiento.getNombre());
				docTarea.setTarea(tarea);
				docTarea.setDocumentoRgdRcoa(documentoOficioPronunciamiento);
				listaDocumentoTarea.add(docTarea);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void ejecutarAdicionalesRgdRcoa(String tramite, TaskSummaryCustom infoTarea){
		try{
			bandejaTareasBean.setProcessId(infoTarea.getProcessInstanceId());
			FirmarPronunciamientoPermisoRGDController oficioController = JsfUtil.getBean(FirmarPronunciamientoPermisoRGDController.class);
			if(oficioController.getTipoPermisoRGD() != null && oficioController.getTipoPermisoRGD().equals(Constantes.TIPO_RGD_REP))
				oficioController.notificacionREP();
			else
				oficioController.notificacion();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void generarPronunciamientoRgdSuia(TaskSummaryCustom tarea) {
		try {
			tarea.setProcessVariables(bandejaFacade.getProcessVariables(tarea.getProcessInstanceId(), JsfUtil.getLoggedUser()));
			bandejaTareasBean.setProcessId(tarea.getProcessInstanceId());
			bandejaTareasBean.setTarea(tarea);
			firmarDocumentoRgdSuiaController.init();

			Documento documentoOficioPronunciamiento = firmarDocumentoRgdSuiaController.getArchivoAprobacion();
			if(documentoOficioPronunciamiento != null) {
				String idAlfrescoWks = (documentoOficioPronunciamiento.getIdAlfresco().split(";"))[0];
				String documentUrl = getUrlAlfresco(documentoOficioPronunciamiento.getIdAlfresco());
				idsDocumentosAlfresco += (idsDocumentosAlfresco.equals("")) ? documentUrl : ";" + documentUrl;
				DocumentoTareaDTO docTarea = new DocumentoTareaDTO();
				docTarea.setIdAlfresco(idAlfrescoWks);
				docTarea.setNombreDocumento(documentoOficioPronunciamiento.getNombre());
				docTarea.setTarea(tarea);
				docTarea.setDocumentoPronunciamientoRetce(documentoOficioPronunciamiento);
				listaDocumentoTarea.add(docTarea);
			}
			documentoOficioPronunciamiento = firmarDocumentoRgdSuiaController.getArchivoRegistro();
			if(documentoOficioPronunciamiento != null) {
				String idAlfrescoWks = (documentoOficioPronunciamiento.getIdAlfresco().split(";"))[0];
				String documentUrl = getUrlAlfresco(documentoOficioPronunciamiento.getIdAlfresco());
				idsDocumentosAlfresco += (idsDocumentosAlfresco.equals("")) ? documentUrl : ";" + documentUrl;
				DocumentoTareaDTO docTarea = new DocumentoTareaDTO();
				docTarea.setIdAlfresco(idAlfrescoWks);
				docTarea.setNombreDocumento(documentoOficioPronunciamiento.getNombre());
				docTarea.setTarea(tarea);
				docTarea.setDocumentoPronunciamientoRetce(documentoOficioPronunciamiento);
				listaDocumentoTarea.add(docTarea);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void generarDocumentosPlaguicidas(TaskSummaryCustom tarea, Boolean esPronunciamientoFavorable) {
		try {
			Map<String, Object> variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), tarea.getProcessInstanceId());
			Integer numeroRevision = Integer.valueOf((String) variables.get("numeroRevision"));
			String tramite = (String) variables.get("tramite");
			
			ProyectoPlaguicidas proyectoPlaguicidas = proyectoPlaguicidasFacade.getPorCodigoProyecto(tramite);

			OficioPronunciamientoPqua oficioRevision = oficioPronunciamientoPquaFacade.getPorProyectoRevision(proyectoPlaguicidas.getId(), numeroRevision);

			List<DocumentoPqua> documentos = new ArrayList<>();
			if(oficioRevision.getEsAprobacion()) {
				documentos = documentoPquaFacade.documentoPorTablaIdPorIdDoc(oficioRevision.getId(), TipoDocumentoSistema.PQUA_OFICIO_APROBADO,
							OficioPronunciamientoPqua.class.getSimpleName());
			} else {
				documentos = documentoPquaFacade.documentoPorTablaIdPorIdDoc(oficioRevision.getId(), TipoDocumentoSistema.PQUA_OFICIO_OBSERVADO,
							OficioPronunciamientoPqua.class.getSimpleName());
			}

			DocumentoPqua documentoOficioAlfresco = null;
			Boolean guardarDocumento = false;
			if (documentos.size() > 0) {
				documentoOficioAlfresco = documentos.get(0);
				if(documentoOficioAlfresco != null && documentoOficioAlfresco.getId() != null) {
					if(!documentoOficioAlfresco.getUsuarioCreacion().equals(JsfUtil.getLoggedUser().getNombre()) 
							|| !JsfUtil.getSimpleDateFormat(documentoOficioAlfresco.getFechaCreacion()).equals(JsfUtil.getSimpleDateFormat(new Date()))) {
						documentoOficioAlfresco.setEstado(false);
						documentoPquaFacade.guardar(documentoOficioAlfresco);
						
						guardarDocumento = true;
					}
				} else {
					guardarDocumento = true;
				}
			} else {
				guardarDocumento = true;
			}
			
			if(guardarDocumento) {
				informeOficioPquaBean.setEsPronunciamientoFavorable(esPronunciamientoFavorable);
				informeOficioPquaBean.setNumeroRevision(numeroRevision);
				
				informeOficioPquaBean.cargarDatosProyecto(proyectoPlaguicidas);
				informeOficioPquaBean.generarOficio(false);
				
				documentoOficioAlfresco = informeOficioPquaBean.guardarDocumentoOficio();
				
				informeOficioPquaBean.getOficioRevision().setIdTarea(Long.valueOf(tarea.getTaskId()).intValue());
				oficioPronunciamientoPquaFacade.guardar(informeOficioPquaBean.getOficioRevision()); //para guardar la fecha de elaboracion del informe
			}
			
			if(documentoOficioAlfresco != null) {
				String idAlfrescoWks = (documentoOficioAlfresco.getIdAlfresco().split(";"))[0];
				String documentUrl = getUrlAlfresco(documentoOficioAlfresco.getIdAlfresco());
				
				idsDocumentosAlfresco += (idsDocumentosAlfresco.equals("")) ? documentUrl : ";" + documentUrl;
				
				DocumentoTareaDTO docTarea = new DocumentoTareaDTO();
				docTarea.setIdAlfresco(idAlfrescoWks);
				docTarea.setNombreDocumento(documentoOficioAlfresco.getNombre());
				docTarea.setTarea(tarea);
				docTarea.setDocumentoPlaguicidas(documentoOficioAlfresco);
				docTarea.setNumeroTramite(tramite);
				docTarea.setIdOficio(oficioRevision.getId());
				
				listaDocumentoTarea.add(docTarea);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void ejecutarAdicionalesPlaguicidas(DocumentoTareaDTO item, TaskSummaryCustom infoTarea)  {
		try{
			ProyectoPlaguicidas proyectoPlaguicidas = proyectoPlaguicidasFacade.getPorCodigoProyecto(item.getNumeroTramite());
			OficioPronunciamientoPqua oficioRevision = oficioPronunciamientoPquaFacade.getPorIdOficio(item.getIdOficio());
			
			informeOficioPquaBean.guardarFechaOficio(oficioRevision);  //para guardar la fecha de firma del informe
			
			DocumentoPqua documentoOficioAlfresco = item.getDocumentoPlaguicidas();
			documentoOficioAlfresco.setIdProceso(infoTarea.getProcessInstanceId());
			documentoPquaFacade.guardar(documentoOficioAlfresco);
			
			informeOficioPquaBean.cargarDatosProyecto(proyectoPlaguicidas);
			informeOficioPquaBean.notificarOperador(documentoOficioAlfresco);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void generarAutorizacionAnulacionImportacion(TaskSummaryCustom tarea){
		try {
			
			JsfUtil.cargarObjetoSession("tarea", tarea);
			
			FirmarAutorizacionAnulacionController firmarController = (FirmarAutorizacionAnulacionController) BeanLocator.getInstance(FirmarAutorizacionAnulacionController.class);
			
			firmarController.crearDocumento();
			String tramite = firmarController.getTramite();
			Boolean autorizacion = firmarController.isAutorizacion();
			
			DocumentosSustanciasQuimicasRcoa documento = firmarController.getDocumento();			
			
			if(documento != null) {
				String idAlfrescoWks = (documento.getIdAlfresco().split(";"))[0];
				String documentUrl = getUrlAlfresco(documento.getIdAlfresco());
				
				idsDocumentosAlfresco += (idsDocumentosAlfresco.equals("")) ? documentUrl : ";" + documentUrl;
				
				DocumentoTareaDTO docTarea = new DocumentoTareaDTO();
				docTarea.setIdAlfresco(idAlfrescoWks);
				docTarea.setNombreDocumento(documento.getNombre());
				docTarea.setTarea(tarea);
				docTarea.setDocumentoImportacion(documento);
				docTarea.setNumeroTramite(tramite);
				docTarea.setAutorizacion(autorizacion);
				
				listaDocumentoTarea.add(docTarea);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public void ejecutarAdicionalesImportacion(String tramite, Boolean autorizacion, TaskSummaryCustom tarea){
		try {
			
			SolicitudImportacionRSQ solicitud = solicitudImportacionRSQFacade.buscarPorTramite(tramite);
			
			if(autorizacion){
				solicitud.setUsuarioAutorizaSolicitud(JsfUtil.getLoggedUser());
			}else{
				solicitud.setUsuarioAutorizaAnulacion(JsfUtil.getLoggedUser());
			}
			
			solicitudImportacionRSQFacade.save(solicitud, JsfUtil.getLoggedUser());
			
			Map<String, Object> parametros = new HashMap<>();
			
			parametros.put("usuario_operador", solicitud.getUsuarioCreacion());
			
			procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), tarea.getProcessInstanceId(), parametros);
			
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), tarea.getTaskId(), tarea.getProcessInstanceId(), null);							
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

}
