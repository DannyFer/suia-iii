package ec.gob.ambiente.rcoa.registroAmbiental.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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

import org.apache.log4j.Logger;
import org.kie.api.task.model.TaskSummary;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.rcoa.facade.CoordenadasProyectoCoaFacade;
import ec.gob.ambiente.rcoa.facade.DocumentosRegistroAmbientalFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaUbicacionFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCuaCiuuFacade;
import ec.gob.ambiente.rcoa.facade.RegistroAmbientalCoaFacade;
import ec.gob.ambiente.rcoa.model.DocumentoRegistroAmbiental;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.RegistroAmbientalRcoa;
import ec.gob.ambiente.rcoa.preliminar.contoller.GeneracionDocumentosController;
import ec.gob.ambiente.rcoa.util.NotificacionInternaUtil;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.PlantillaReporteFacade;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.administracion.facade.ContactoFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.BandejaFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.dto.EntityResolucionAmbientalRCOA;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.notificacionautoridades.controllers.NotificacionAutoridadesController;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.CategoriaIIFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.BeanLocator;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.DigitalSign;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class FirmarElectronicamenteResolucionAmbientalRcoaController {
	
	private final Logger LOG = Logger.getLogger(FirmarElectronicamenteResolucionAmbientalRcoaController.class);

	@EJB
	private PlantillaReporteFacade plantillaReporteFacade;
	@EJB
	private ProcesoFacade procesoFacade;
	@EJB
	private UsuarioFacade usuarioFacade;
    @EJB
    private ContactoFacade contactoFacade;
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	@EJB
	private ProyectoLicenciaCuaCiuuFacade proyectoLicenciaCuaCiuuFacade;
	@EJB
	private ProyectoLicenciaCoaUbicacionFacade proyectoLicenciaCoaUbicacionFacade;
	@EJB
	private CoordenadasProyectoCoaFacade coordenadasProyectoCoaFacade;
	@EJB
	private OrganizacionFacade organizacionFacade;
	@EJB
	private BandejaFacade bandejaFacade;

	@EJB
	private DocumentosRegistroAmbientalFacade documentosFacade;
	@EJB
	private CategoriaIIFacade categoriaIIFacade;
	@EJB
	private AreaFacade areaFacade;

	@EJB
	private RegistroAmbientalCoaFacade registroAmbientalCoaFacade;
	@EJB
	private MensajeNotificacionFacade mensajeNotificacionFacade;
	
    @Getter
    @Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;
	
	@Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;
    
	@Getter
    @Setter
	private String pdf;
    
	@Getter
	@Setter
	private File resolucionTmpPdf;
		
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyecto;
	
	@Getter
	@Setter
	private DocumentoRegistroAmbiental documentoResolucionAmbiental, documentoManual;
    
	@Getter
	@Setter
	private Area areaResponsable;
    
	@Getter
	@Setter
	private EntityResolucionAmbientalRCOA entityInforme;
    
    private Map<String, Object> variables;
    
	@Getter
	@Setter
	private String tramite;

	@Getter
	@Setter
	private boolean token, descargarRegistroAmbiental = false, subido = false, ambienteProduccion;
	
	private String nombreReporteRegistro;
	private RegistroAmbientalRcoa registroAmbientalRcoa;
	
	@Getter
    @Setter
    public static String tipoAmbiente = Constantes.getPropertyAsString("ambiente.produccion");
	
	@PostConstruct
	public void init(){
		try {
			
			ambienteProduccion = Boolean.parseBoolean(tipoAmbiente);
			
			if(ambienteProduccion){
				token = true;
			}
				
			variables=procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
			tramite=(String)variables.get(Constantes.VARIABLE_PROCESO_TRAMITE);
			proyecto = proyectoLicenciaCoaFacade.buscarProyecto(tramite);
			areaResponsable = proyecto.getAreaResponsable();
			
			nombreReporteRegistro = "Resolucion_AmbientalRcoa_"+proyecto.getCodigoUnicoAmbiental()+".pdf";
			switch (areaResponsable.getTipoArea().getSiglas()) {
			case "DP":
				areaResponsable = proyecto.getAreaResponsable();
				break;
			case "ZONALES":
				UbicacionesGeografica provincia = new UbicacionesGeografica();
				provincia = proyecto.getAreaResponsable().getUbicacionesGeografica();	
				areaResponsable = areaFacade.getAreaCoordinacionZonal(provincia);
				break;
			case "OT":
				areaResponsable = proyecto.getAreaResponsable().getArea();
				break;
			case "PC":
				nombreReporteRegistro= "Resolucion_GADs_Municipales_gestión_ambiental_"+proyecto.getCodigoUnicoAmbiental()+".pdf";
				break;
			case "EA":
				if(areaResponsable.getAreaName().contains("PROVINCIA")){
				}else{
					if(areaResponsable.getAreaName().contains("QUITO")
							|| areaResponsable.getAreaName().contains("GUAYAQUIL")
							|| areaResponsable.getAreaName().contains("CUENCA")){
						nombreReporteRegistro= "Resolucion_GADs_Municipales_gestión_ambiental_"+proyecto.getCodigoUnicoAmbiental()+".pdf";
					}
				}
				break;
			default:
				break;
			}
			cargarDatosResplucionCoa() ;
			documentoResolucionAmbiental = documentosFacade.documentoXTablaIdXIdDoc(proyecto.getId(), ProyectoLicenciaCoa.class.getSimpleName(), TipoDocumentoSistema.RESOLUCION_AMBIENTAL_RCOA);
			registroAmbientalRcoa = registroAmbientalCoaFacade.obtenerRegistroAmbientalPorProyecto(proyecto);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	
	public String firmarDocumento() {
		try {
			if(documentoResolucionAmbiental != null) {
				String documentOffice = documentosFacade.direccionDescarga(documentoResolucionAmbiental);
				return DigitalSign.sign(documentOffice, JsfUtil.getLoggedUser().getNombre());
			}
		} catch (Exception exception) {
			LOG.error("Ocurrió un error durante la firma del certificado", exception);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
		return "";
	}	
	
	public void completarTarea() {
		try {			
			
			if (documentoResolucionAmbiental != null && documentoResolucionAmbiental.getId() != null) {
				if (token) {
					String idAlfrescoInforme = documentoResolucionAmbiental.getAlfrescoId();

					if (!documentosFacade.verificarFirmaVersion(idAlfrescoInforme)) {
						JsfUtil.addMessageError("La resolución no está firmado electrónicamente.");
						return;
					}			

				} else {
					if (!subido) {
						JsfUtil.addMessageError("Debe adjuntar la resolución firmada.");
						return;
					}				
				}
				// finalizo el proyecto
				proyecto.setProyectoFinalizado(true);
				proyecto.setProyectoFechaFinalizado(new Date());
				proyectoLicenciaCoaFacade.guardar(proyecto);
				
				registroAmbientalRcoa.setFechaFinalizacion(new Date());
				registroAmbientalRcoa.setFinalizado(true);
				registroAmbientalCoaFacade.guardar(registroAmbientalRcoa);
				
				try{
					procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getTarea().getProcessInstanceId(), null);
					
					if (subido && descargarRegistroAmbiental){
						documentoResolucionAmbiental= documentosFacade.ingresarDocumento(documentoManual, proyecto.getId(), proyecto.getCodigoUnicoAmbiental(), TipoDocumentoSistema.RESOLUCION_AMBIENTAL_RCOA, nombreReporteRegistro, ProyectoLicenciaCoa.class.getSimpleName(), bandejaTareasBean.getProcessId());		
					}
					
					documentoResolucionAmbiental.setIdProceso(bandejaTareasBean.getProcessId());
					documentoResolucionAmbiental = documentosFacade.guardar(documentoResolucionAmbiental);
					
					notificacion();
					
					//notificacion usuarios internos
					NotificacionInternaUtil.enviarNotificacionRegistro(proyecto, registroAmbientalRcoa.getNumeroResolucion());

					//para finalizar la tarea de encuesta
					List<TaskSummary> listaTareas = procesoFacade.getTaskReservedInProgress(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
					if(listaTareas != null && listaTareas.size() > 0) {				
						for (TaskSummary tarea : listaTareas) {
							String nombretarea = bandejaFacade.getNombreTarea(tarea.getId());
							if(nombretarea.equals("encuestaYDescargaDocumentos")) {
								Usuario usuarioTarea = usuarioFacade.buscarUsuario(tarea.getActualOwner().getId());

								String fechaAvance = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss:SS").format(new Date());

								Map<String, Object> parametros = new HashMap<>();
						
								parametros.put("finalizadoAutomaticoSisEncuestaRA", fechaAvance);
								
								procesoFacade.modificarVariablesProceso(usuarioTarea, bandejaTareasBean.getTarea().getProcessInstanceId(), parametros);

								procesoFacade.aprobarTarea(usuarioTarea, tarea.getId(), bandejaTareasBean.getTarea().getProcessInstanceId(), null);

								registroAmbientalRcoa.setFinalizado(true);
								registroAmbientalCoaFacade.guardar(registroAmbientalRcoa);
										
								break;
							} 
						}
					}

					JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
					JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
				} catch (JbpmException e) {
					e.printStackTrace();
					JsfUtil.addMessageError("Error al realizar la operación.");
				}
			} else {
				JsfUtil.addMessageError("Error al realizar la operación el oficio no está firmado.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Error al realizar la operación.");
		}
	}
	
    /**
     * metodo para cargar datos en la plantilla
     * @throws Exception
     */
    public void cargarDatosResplucionCoa() throws Exception{
    	boolean generarNuevo = false;
    	documentoResolucionAmbiental = documentosFacade.documentoXTablaIdXIdDoc(proyecto.getId(), ProyectoLicenciaCoa.class.getSimpleName(), TipoDocumentoSistema.RESOLUCION_AMBIENTAL_RCOA);
    	
    	if(documentoResolucionAmbiental == null)
    		generarNuevo = true;
		else if(documentoResolucionAmbiental != null && !documentoResolucionAmbiental.getUsuarioCreacion().equals(loginBean.getNombreUsuario()))
			generarNuevo = true;
		else if(documentoResolucionAmbiental != null){
			DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			Date todayDate = formatter.parse(formatter.format(new Date()));
			Date fechaDocumento = formatter.parse(formatter.format(documentoResolucionAmbiental.getFechaCreacion()));
				if(todayDate.compareTo(fechaDocumento) != 0)
					generarNuevo = true;
		}
    	
    	//generar documento resolucion ambiental primera vez 
    	if(generarNuevo) { 
    		if(documentoResolucionAmbiental != null) {
    			documentoResolucionAmbiental.setEstado(false);
    			documentosFacade.guardar(documentoResolucionAmbiental);
    		}
    			
	    	GeneracionDocumentosController oficioController = (GeneracionDocumentosController) BeanLocator.getInstance(GeneracionDocumentosController.class);
			
	    	documentoResolucionAmbiental = oficioController.generarResolucionRegistroAmbiental(proyecto);
    	}
		
		byte[] contenido = documentosFacade.descargar(documentoResolucionAmbiental.getAlfrescoId());
		String reporteHtmlfinal = documentoResolucionAmbiental.getNombre().replace("/", "-");
		
		File archivoFinal = new File(JsfUtil.devolverPathReportesHtml(reporteHtmlfinal));
			FileOutputStream file = new FileOutputStream(archivoFinal);
			file.write(contenido);
			file.close();
		
		setPdf(JsfUtil.devolverContexto("/reportesHtml/" + documentoResolucionAmbiental.getNombre()));
    }
    
	public boolean verificaToken() {
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
	
	public StreamedContent getDocumentoRegistroA(){
		DescargarDocumentosCoaController descargarDocumentosCoaController = JsfUtil.getBean(DescargarDocumentosCoaController.class);
		StreamedContent file = descargarDocumentosCoaController.descargarDocumento(documentoResolucionAmbiental); 
		if(file != null){
			descargarRegistroAmbiental = true;
			return file;
		}else{
			try{
				cargarDatosResplucionCoa();
				file = descargarDocumentosCoaController.descargarDocumento(documentoResolucionAmbiental);
				if(file != null){
					descargarRegistroAmbiental = true;
					return file;
				}
			}catch(Exception e){
				JsfUtil.addMessageError("Error al generar el documento");
			}	
		}
		return file; 
	}

	public void uploadListenerDocumentos(FileUploadEvent event) {
		if (descargarRegistroAmbiental) {
			documentoManual=new DocumentoRegistroAmbiental();
			byte[] contenidoDocumento = event.getFile().getContents();
			documentoManual.setContenidoDocumento(contenidoDocumento);
			documentoManual.setNombre(event.getFile().getFileName());
			documentoManual.setExtesion(".pdf");		
			documentoManual.setMime("application/pdf");
			documentoManual.setNombreTabla(ProyectoLicenciaCoa.class.getSimpleName());
			documentoManual.setIdTable(proyecto.getId());
			subido = true;
			JsfUtil.addMessageInfo("Documento subido exitosamente");
		} else{
			JsfUtil.addMessageError("No ha descargado el documento para la firma");
		}

	}

	private void notificacion(){
		try {
			String correoOperador = "";			
			String ubicacion = "";
			String nombreOperador = "";
			
			if(proyecto.getUsuario().getNombre().length() == 10){			
				
				List<Contacto> contacto = contactoFacade.buscarPorPersona(proyecto.getUsuario().getPersona());				
				for (Contacto con : contacto) {	                
	                if (con.getFormasContacto().getId() == 5 && con.getEstado().equals(true)) {
	                	correoOperador = con.getValor();
	                	break;
	                }	               
	            }				
				nombreOperador = proyecto.getUsuario().getPersona().getNombre();				
			}else{
				Organizacion organizacion = organizacionFacade.buscarPorRuc(proyecto.getUsuario().getNombre());
				
				if(organizacion != null){					
					List<Contacto> contacto = contactoFacade.buscarPorOrganizacion(organizacion);					
					for (Contacto con : contacto) {		               
		                if (con.getFormasContacto().getId() == 5 && con.getEstado().equals(true)) {
		                    correoOperador = con.getValor();
		                    break;
		                }
		            }					
					nombreOperador = organizacion.getNombre();
				}else{					
					List<Contacto> contacto = contactoFacade.buscarPorPersona(proyecto.getUsuario().getPersona());					
					for (Contacto con : contacto) {		                
		                if (con.getFormasContacto().getId() == 5 && con.getEstado().equals(true)) {
		                	correoOperador = con.getValor();
		                	break;
		                }	               
		            }					
					nombreOperador = proyecto.getUsuario().getPersona().getNombre();					
				}				
			}	
			
			List<UbicacionesGeografica> ubicaciones = proyectoLicenciaCoaUbicacionFacade.buscarPorProyecto(proyecto);
			 if(ubicaciones != null && ubicaciones.size() == 1){
				 ubicacion = "la provincia de " + ubicaciones.get(0).getUbicacionesGeografica().getUbicacionesGeografica().getNombre() 
							+ ", cantón " + ubicaciones.get(0).getUbicacionesGeografica().getNombre() 
							+ ", parroquia " + ubicaciones.get(0).getNombre() + ".";
			 }
			 if(ubicaciones != null && ubicaciones.size() > 1){
				 ubicacion = "<br/><table border=\"1\" cellpadding=\"10\" cellspacing=\"0\" class=\"w600Table\" style=\"font-family:arial;font-size:12px;\">";

					ubicacion += "<tr><td><b>PROVINCIA</b></td><td><b>CANTÓN</b></td><td><b>PARROQUIA</b></td></tr>";
					for (UbicacionesGeografica ubicacionActual : ubicaciones) {
						
						ubicacion += "<tr><td>" + ubicacionActual.getUbicacionesGeografica().getUbicacionesGeografica().getNombre() + "</td><td>"
								+ ubicacionActual.getUbicacionesGeografica().getNombre() + "</td><td>"
								+ ubicacionActual.getNombre() + "</td></tr>";
					}
					ubicacion += "</table>";
			 }			
			
			String mensaje = mensajeNotificacionFacade.recuperarValorMensajeNotificacion("bodyNotificacionOperadorRcoaRegistroAmbiental", new Object[]{});
			
			mensaje = mensaje.replace("nombre_operador", nombreOperador);
			mensaje = mensaje.replace("numero_resolucion", registroAmbientalRcoa.getNumeroResolucion());
			mensaje = mensaje.replace("nombre_proyecto", proyecto.getNombreProyecto());
			mensaje = mensaje.replace("fecha_resolucion", JsfUtil.devuelveFechaEnLetrasSinHora1(registroAmbientalRcoa.getFechaFinalizacion()));
			mensaje = mensaje.replace("ubicacion_tabla", ubicacion);
			
			NotificacionAutoridadesController mail = new NotificacionAutoridadesController();
 			mail.sendEmailInformacionProponente(correoOperador, "", mensaje, "Notificación", proyecto.getCodigoUnicoAmbiental(), proyecto.getUsuario(), loginBean.getUsuario()); 
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
