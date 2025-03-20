package ec.gob.ambiente.rcoa.sustanciasQuimicasImportacion.controllers;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
import org.jfree.util.Log;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.rcoa.dto.EntityOficioAutorizacionAnulacionImportacionRSQ;
import ec.gob.ambiente.rcoa.enums.TipoInformeOficioEnum;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.DetalleSolicitudImportacionRSQFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.DocumentosRSQFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.DocumentosSustanciasQuimicasRcoaFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.InformesOficiosRSQFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.SolicitudImportacionRSQFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.DetalleSolicitudImportacionRSQ;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.DocumentosSustanciasQuimicasRcoa;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.InformeOficioRSQ;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.SolicitudImportacionRSQ;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.PlantillaReporteFacade;
import ec.gob.ambiente.suia.administracion.facade.ContactoFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.TipoDocumento;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.dto.TaskSummaryCustom;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.DigitalSign;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilGenerarInforme;

@ManagedBean
@ViewScoped
public class FirmarAutorizacionAnulacionController {
	
	private final Logger LOG = Logger.getLogger(FirmarAutorizacionAnulacionController.class);
	
	@EJB
	private ProcesoFacade procesoFacade;
	@EJB
	private UsuarioFacade usuarioFacade;
	@EJB
	private SolicitudImportacionRSQFacade solicitudImportacionRSQFacade;
	@EJB
	private DetalleSolicitudImportacionRSQFacade detalleSolicitudImportacionRSQFacade;
	@EJB
	private ContactoFacade contactoFacade;
	@EJB
	private OrganizacionFacade organizacionFacade;
	@EJB
	private PlantillaReporteFacade plantillaReporteFacade;
	@EJB
	private DocumentosSustanciasQuimicasRcoaFacade   documentoSustanciasQuimicasFacade;
	@EJB
	private SecuenciasFacade secuenciasFacade;
	@EJB
	private InformesOficiosRSQFacade informesOficiosRSQFacade;
	@EJB
	private DocumentosRSQFacade documentosRSQFacade;
	
	@ManagedProperty(value = "#{bandejaTareasBean}")
    @Getter
    @Setter
    private BandejaTareasBean bandejaTareasBean;
	
	@Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;
    
    @EJB(lookup = Constantes.JBPM_EJB_TASK_BEAN)
    private TaskBeanFacade taskBeanFacade;
    
    @ManagedProperty(value = "#{proyectosBean}")
    @Getter
    @Setter
    private ProyectosBean proyectosBean;
    
    private Map<String, Object> variables;	
   
    @Getter
    @Setter
	private String tramite;
	
    @Getter
    @Setter
	private boolean autorizacion;
	
	@Getter
	@Setter
	private SolicitudImportacionRSQ solicitud;
	
	@Getter
	@Setter
	private DetalleSolicitudImportacionRSQ detalle;
	
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyecto;
	
	@Getter
	@Setter
	private byte[] archivoInforme;
	
	@Getter
    @Setter
    private DocumentosSustanciasQuimicasRcoa documento, documentoManual, documentoOficio;
	
	@Getter
	@Setter
	private boolean token, documentoDescargado = false, ambienteProduccion, documentoSubido = false;
	
	@Getter
	@Setter
	private String nombreFormulario;
	
	@Getter
    @Setter
    public static String tipoAmbiente = Constantes.getPropertyAsString("ambiente.produccion");
	
	@Getter
	@Setter
	private String codigo, codigoRSQ;
	
	@Getter
    @Setter
    private InformeOficioRSQ oficio;
	
	@Getter
	@Setter
	private Boolean firmaMasiva = false;
	
	@Getter
	@Setter
	private long idProceso;
	
    @PostConstruct
	public void init(){
		try {
			
			TaskSummaryCustom tarea =(TaskSummaryCustom)(JsfUtil.devolverObjetoSession("tarea"));
			
			if(tarea == null) {
				variables=procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());			
			}else {
				variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), tarea.getProcessInstanceId());
				firmaMasiva = true;
				idProceso = tarea.getProcessInstanceId();
			}
						
			tramite=(String)variables.get(Constantes.VARIABLE_PROCESO_TRAMITE);	
			
			String autorizacionAux = (String)variables.get("emision");
			autorizacion= Boolean.valueOf(autorizacionAux);
						
			solicitud = solicitudImportacionRSQFacade.buscarPorTramite(tramite);
			
			codigoRSQ = solicitud.getRegistroSustanciaQuimica().getNumeroAplicacion();
			codigo = tramite;
			
			ambienteProduccion = Boolean.parseBoolean(tipoAmbiente);
			token = true;
			if(!ambienteProduccion){
				verificaToken();
			}
			
			if(solicitud != null){
				if(solicitud.getRegistroSustanciaQuimica().getProyectoLicenciaCoa() != null){
					proyecto = solicitud.getRegistroSustanciaQuimica().getProyectoLicenciaCoa();
				}				
				detalle = detalleSolicitudImportacionRSQFacade.buscarPorSolicitud(solicitud);
			}			
			
			if(autorizacion){
				List<DocumentosSustanciasQuimicasRcoa> documentoList = documentoSustanciasQuimicasFacade.documentoXIdTablaIdTipoDoc(solicitud.getId(), SolicitudImportacionRSQ.class.getName(), TipoDocumentoSistema.RCOA_RSQ_AUTORIZACION_IMPORTACION);
				if(documentoList != null && !documentoList.isEmpty()){
					documento = documentoList.get(0);
				}
				
				nombreFormulario = "Firmar y Enviar la Autorización de Importación Emitido";
			}else{
				List<DocumentosSustanciasQuimicasRcoa> documentoList = documentoSustanciasQuimicasFacade.documentoXIdTablaIdTipoDoc(solicitud.getId(), SolicitudImportacionRSQ.class.getName(), TipoDocumentoSistema.RCOA_RSQ_ANULACION_IMPORTACION);
				if(documentoList != null && !documentoList.isEmpty()){
					documento = documentoList.get(0);
				}
				nombreFormulario = "Firmar Anulación de la Autorización de Importación";
			}
			
			List<InformeOficioRSQ> oficios=informesOficiosRSQFacade.obtenerPorRSQListaPorTipo(solicitud.getRegistroSustanciaQuimica(),TipoInformeOficioEnum.OFICIO_PRONUNCIAMIENTO);
			if(oficios != null && !oficios.isEmpty()){
				oficio = oficios.get(0);
				documentoOficio=documentosRSQFacade.obtenerDocumentoPorTipo(solicitud.getRegistroSustanciaQuimica().pronunciamientoAprobado()?TipoDocumentoSistema.RCOA_RSQ_OFICIO_APROBADO:TipoDocumentoSistema.RCOA_RSQ_OFICIO_NEGADO,"InformeOficioRSQ",oficio.getId());
			}	
			
			
		} catch (Exception e) {
			Log.error(e.getMessage());
			e.printStackTrace();
		}			
	}
    
    private void generadorDocumento(){
    	try {
    		    		
    		String identificacionUsuario = "";
			String cargo = "";
			String nombreEmpresa = "";
			String representante = "";
			String trato = "";
			
			if(proyecto == null || proyecto.getId() == null){
				identificacionUsuario = solicitud.getUsuarioCreacion();
				
				if(identificacionUsuario.length() == 10){
					Usuario usuarioOperador = usuarioFacade.buscarUsuario(identificacionUsuario);
					cargo = usuarioOperador.getPersona().getTitulo();				
    				trato = usuarioOperador.getPersona().getTipoTratos().getNombre();
    				nombreEmpresa = trato + " " + usuarioOperador.getPersona().getNombre();
				}else{
					
					Organizacion organizacion = organizacionFacade.buscarPorRuc(identificacionUsuario);
    				
    				if(organizacion != null){
    					cargo = organizacion.getCargoRepresentante();
    					
    					nombreEmpresa = organizacion.getNombre();
    					trato = organizacion.getPersona().getTipoTratos().getNombre();
    					representante = trato + " " + organizacion.getPersona().getNombre();
    					
    				}else{
    					Usuario usuarioOperador = usuarioFacade.buscarUsuario(identificacionUsuario);
    					cargo = usuarioOperador.getPersona().getTitulo();				
        				trato = usuarioOperador.getPersona().getTipoTratos().getNombre();
        				nombreEmpresa = trato + " " + usuarioOperador.getPersona().getNombre();					
    				}	
				}			
    		}else{
    			identificacionUsuario = proyecto.getUsuario().getNombre();
    			
    			if(identificacionUsuario.length() == 10){
    				cargo = proyecto.getUsuario().getPersona().getTitulo();				
    				trato = proyecto.getUsuario().getPersona().getTipoTratos().getNombre();
    				nombreEmpresa = trato + " " + proyecto.getUsuario().getPersona().getNombre();	
    				
    			}else{
    				Organizacion organizacion = organizacionFacade.buscarPorRuc(identificacionUsuario);
    				
    				if(organizacion != null){
    					cargo = organizacion.getCargoRepresentante();
    					
    					nombreEmpresa = organizacion.getNombre();
    					trato = organizacion.getPersona().getTipoTratos().getNombre();
    					representante = trato + " " + organizacion.getPersona().getNombre();
    					
    				}else{
    					trato = proyecto.getUsuario().getPersona().getTipoTratos().getNombre();
    					cargo = proyecto.getUsuario().getPersona().getTitulo();
    					nombreEmpresa = trato + " " + proyecto.getUsuario().getPersona().getNombre();					
    				}				
    			}		
    		}
			
			
			
    		EntityOficioAutorizacionAnulacionImportacionRSQ entity = new EntityOficioAutorizacionAnulacionImportacionRSQ();  		  		
    		    		
    		entity.setFecha(JsfUtil.devuelveFechaEnLetrasSinHora(new Date()));
    		
    		entity.setNombreOperador(nombreEmpresa);
    		if(representante != null && !representante.isEmpty())
    			entity.setRepresentanteLegal(representante);
    		else
    			entity.setRepresentanteLegal(" ");
    		
    		entity.setNombreEmpresa(nombreEmpresa);
    		entity.setCodigoRegistroSustancias(solicitud.getRegistroSustanciaQuimica().getNumeroAplicacion());
    		entity.setNumeroTramite(solicitud.getTramite());    		
			
			entity.setSubsecretario(JsfUtil.getLoggedUser().getPersona().getNombre());
			
			if(!autorizacion){
				if(solicitud.getNumeroDocumentoAnulacion() == null){
					entity.setCodigoOficio(generarCodigoRSQ());
				}else{
					entity.setCodigoOficio(solicitud.getNumeroDocumentoAnulacion());
				}
				
				entity.setNumeroOficioAprobacion(solicitud.getSolicitudAnulada().getNumeroDocumentoAutorizacion());
				entity.setFechaAprobacion(JsfUtil.devuelveFechaEnLetrasSinHora(solicitud.getSolicitudAnulada().getFechaFinAutorizacion()));
	    		entity.setFechaActual(JsfUtil.devuelveFechaEnLetrasSinHora(solicitud.getFechaAnulacion()));
	    		entity.setNumeroAnulacion(solicitud.getSolicitudAnulada().getTramite());
	    		
	    		
	    		DetalleSolicitudImportacionRSQ detalleAnulado = detalleSolicitudImportacionRSQFacade.buscarPorSolicitud(solicitud.getSolicitudAnulada());
	    		
	    		String pesoBruto = "";
	    		
	    		if(detalleAnulado.getPesoBruto() != null){
	    			pesoBruto = detalleAnulado.getPesoBruto().toString();
	    		}
	    		
	    		String datos = "<table border=\"1\" cellpadding=\"7\" cellspacing=\"0\" style=\"width: 700px;font-size: 11px !important;border:1;\">";

				datos += "<tr><td style=\"width:20%\">Número de Oficio</td><td style=\"width:20%\">Fecha de Emisión</td><td style=\"width:20%\">Sustancia</td><td style=\"width:20%\">Subpartida Arancelaria</td><td style=\"width:20%\">Peso Neto Kg</td><td style=\"width:20%\">Peso Bruto Kg</td></tr>";
				datos += "<tr><td style=\"width:20%\">"
						+ solicitud.getSolicitudAnulada().getNumeroDocumentoAutorizacion()
						+ "</td><td style=\"width:20%\">"
						+ JsfUtil.devuelveFechaEnLetrasSinHora(solicitud.getSolicitudAnulada().getFechaFinAutorizacion())
						+ "</td><td style=\"width:20%\">"
						+ solicitud.getSolicitudAnulada().getGestionarProductosQuimicosProyectoAmbiental().getSustanciaquimica().getDescripcion()
						+ "</td><td style=\"width:20%\">"
						+ solicitud.getSolicitudAnulada().getGestionarProductosQuimicosProyectoAmbiental().getSustanciaquimica().getPartidaArancelaria()
						+ "</td><td style=\"width:20%\">"
						+ detalleAnulado.getPesoNeto()
						+ "</td><td style=\"width:20%\">"
						+ pesoBruto +"</td></tr>";

				datos += "</tbody></table>";
	    		
				entity.setTablaSustancias(datos);
				
			}else{
				if(solicitud.getNumeroDocumentoAutorizacion() == null){
					entity.setCodigoOficio(generarCodigoRSQ());
				}else{
					entity.setCodigoOficio(solicitud.getNumeroDocumentoAutorizacion());
				}
				
				entity.setNombreSustancia(solicitud.getGestionarProductosQuimicosProyectoAmbiental().getSustanciaquimica().getDescripcion());
	    		entity.setSubPartida(solicitud.getGestionarProductosQuimicosProyectoAmbiental().getSustanciaquimica().getPartidaArancelaria());
	    		entity.setFechaActual(JsfUtil.devuelveFechaEnLetrasSinHora(solicitud.getFechaInicioAutorizacion()));
				entity.setPesoNeto(detalle.getPesoNeto() == null ? "" : detalle.getPesoNeto().toString());
				
				String datos = "<table border=\"1\" cellpadding=\"7\" cellspacing=\"0\" style=\"width: 700px;font-size: 11px !important;border:1;\">";

				datos += "<tr><td style=\"width:20%\">Sustancia</td><td style=\"width:20%\">Subpartida Arancelaria</td><td style=\"width:20%\">País de Origen</td><td style=\"width:20%\">Peso Neto Kg</td><td style=\"width:20%\">Peso Bruto Kg</td></tr>";
				datos += "<tr><td style=\"width:20%\">"
						+ entity.getNombreSustancia()
						+ "</td><td style=\"width:20%\">"
						+ entity.getSubPartida()
						+ "</td><td style=\"width:20%\">"
						+ detalle.getUbicacionGeografica().getNombre()
						+ "</td><td style=\"width:20%\">"+detalle.getPesoNeto()+"</td><td style=\"width:20%\">"+detalle.getPesoBruto()+"</td></tr>";

				datos += "</tbody></table>";
	    		
				entity.setTablaSustancias(datos);
			}
			
			PlantillaReporte plantillaReporte = plantillaReporteFacade.getPlantillaReporte(TipoDocumentoSistema.RCOA_RSQ_AUTORIZACION_IMPORTACION);		
			
			if(!autorizacion)
			{
				plantillaReporte = plantillaReporteFacade.getPlantillaReporte(TipoDocumentoSistema.RCOA_RSQ_ANULACION_IMPORTACION);
			}
			
			String nombreFichero = "ImportacionSustanciasQuimicas" + solicitud.getTramite()+".pdf";
			String nombreReporte = "ImportacionSustanciasQuimicas.pdf";			
			
			File informePdf = UtilGenerarInforme.generarFichero(
					plantillaReporte.getHtmlPlantilla(),
					nombreReporte, true, entity);
					

			Path path = Paths.get(informePdf.getAbsolutePath());
			String reporteHtmlfinal = nombreReporte;
			archivoInforme = Files.readAllBytes(path);
			File archivoFinal = new File(JsfUtil.devolverPathReportesHtml(reporteHtmlfinal));
			FileOutputStream file = new FileOutputStream(archivoFinal);
			file.write(Files.readAllBytes(path));
			file.close();
						
			TipoDocumento tipoDoc = new TipoDocumento();
			if(autorizacion){
				tipoDoc.setId(TipoDocumentoSistema.RCOA_RSQ_AUTORIZACION_IMPORTACION.getIdTipoDocumento());
			}else{
				tipoDoc.setId(TipoDocumentoSistema.RCOA_RSQ_ANULACION_IMPORTACION.getIdTipoDocumento());
			}			
			
			DocumentosSustanciasQuimicasRcoa documento = new DocumentosSustanciasQuimicasRcoa();
			documento.setNombre(nombreReporte);
			documento.setExtesion(".pdf");
			documento.setMime("application/pdf");					
			documento.setContenidoDocumento(archivoInforme);
			documento.setNombreTabla(SolicitudImportacionRSQ.class.getSimpleName());
			documento.setTipoDocumento(tipoDoc);
			documento.setIdTable(solicitud.getId());		
			
			int procesoId = 0;
			long proceso = 0;
			if(firmaMasiva){
				procesoId = (int)idProceso;
				proceso = idProceso;
			}else{
				procesoId = (int)bandejaTareasBean.getProcessId();
				proceso = bandejaTareasBean.getProcessId();
			}
			
			documento.setProcessinstanceid(procesoId);
			documento.setGestionarProductosQuimicosProyectoAmbiental(solicitud.getGestionarProductosQuimicosProyectoAmbiental());
			
			if(autorizacion){
				documento = documentoSustanciasQuimicasFacade.guardarDocumentoAlfrescoImportacion(solicitud.getTramite(),"REGISTRO SUSTANCIAS QUIMICAS", proceso, documento, TipoDocumentoSistema.RCOA_RSQ_AUTORIZACION_IMPORTACION);
			}else{
				documento = documentoSustanciasQuimicasFacade.guardarDocumentoAlfrescoImportacion(solicitud.getTramite(),"REGISTRO SUSTANCIAS QUIMICAS", proceso, documento, TipoDocumentoSistema.RCOA_RSQ_ANULACION_IMPORTACION);
			}	
			
			setDocumento(documento);
			
			solicitud.setUsuarioAutorizaSolicitud(JsfUtil.getLoggedUser());
			solicitud.setFechaFinAutorizacion(new Date());	
			if(autorizacion){
				solicitud.setNumeroDocumentoAutorizacion(entity.getCodigoOficio());
			}else{
				solicitud.setNumeroDocumentoAnulacion(entity.getCodigoOficio());
			}
			
			solicitudImportacionRSQFacade.save(solicitud, JsfUtil.getLoggedUser());					
    		
		} catch (Exception e) {
			Log.error(e.getMessage());
			e.printStackTrace();
		} 	
    	
    }
    
    private String generarCodigoRSQ() {
		String anioActual=secuenciasFacade.getCurrentYear();		
		String nombreSecuencia="SUSTANCIAS_QUIMICAS_IMPORTACION"+anioActual;
		
		try {
			return Constantes.SIGLAS_INSTITUCION + "-RSQ-IMP"
					+ "-"
					+ anioActual				
					+ "-"
					+ secuenciasFacade.getNextValueDedicateSequence(nombreSecuencia,4);		
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
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
    
    public boolean verificaToken() {
		token = false;
		if (JsfUtil.getLoggedUser().getToken() != null
				&& JsfUtil.getLoggedUser().getToken())
			token = true;
		return token;
	}
    
    public void crearDocumento(){
    	if(documento != null){
    		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
    	    String fechaDocumento = sdf.format(documento.getFechaCreacion());
    	    
    	    String fechaActual = sdf.format(new Date());		
    	   
    		if(!fechaActual.equals(fechaDocumento)){
    			documento = null;
    		}
        	
        	if(documento != null && !documento.getUsuarioCreacion().equals(JsfUtil.getLoggedUser().getNombre())){
        		documento = null;
        	}
    	}
    	
    	
    	if(documento == null || documento.getId() == null){
    		generadorDocumento();
    	}
    }
	
	public String firmarDocumento() {
		try {
			crearDocumento();
			
			String documentOffice = documentoSustanciasQuimicasFacade.direccionDescarga(documento);
			return DigitalSign.sign(documentOffice, JsfUtil.getLoggedUser().getNombre()); 			
			
		} catch (Exception exception) {
			LOG.error("Ocurrió un error durante la firma del certificado", exception);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
		return "";
	}
	
	public StreamedContent descargarDocumento() throws Exception {
		
		crearDocumento();
		
		DefaultStreamedContent content = new DefaultStreamedContent();
		if (documento != null) {
			if (documento.getContenidoDocumento() == null) {
				documento.setContenidoDocumento(documentoSustanciasQuimicasFacade
						.descargar(documento.getIdAlfresco()));
			}
			content = new DefaultStreamedContent(new ByteArrayInputStream(
					documento.getContenidoDocumento()), documento.getExtesion());
			content.setName(documento.getNombre());
		}
		
		documentoDescargado = true;
		return content;
	}
	
	public void validarTareaBpm() {
		
	}
	
	public void uploadListenerDocumentos(FileUploadEvent event) {
		if (documentoDescargado == true) {
			
			TipoDocumento tipoDoc = new TipoDocumento();
			if(autorizacion){
				tipoDoc.setId(TipoDocumentoSistema.RCOA_RSQ_AUTORIZACION_IMPORTACION.getIdTipoDocumento());
			}else{
				tipoDoc.setId(TipoDocumentoSistema.RCOA_RSQ_ANULACION_IMPORTACION.getIdTipoDocumento());
			}
			
			documentoManual=new DocumentosSustanciasQuimicasRcoa();
			byte[] contenidoDocumento = event.getFile().getContents();
			documentoManual.setContenidoDocumento(contenidoDocumento);
			documentoManual.setNombre(event.getFile().getFileName());
			documentoManual.setExtesion(".pdf");		
			documentoManual.setMime("application/pdf");
			documentoManual.setNombreTabla(SolicitudImportacionRSQ.class.getSimpleName());
			documentoManual.setIdTable(solicitud.getId());
			documentoManual.setTipoDocumento(tipoDoc);
			documentoSubido = true;
			JsfUtil.addMessageInfo("Documento subido exitosamente");
		} else{
			JsfUtil.addMessageError("No ha descargado el documento para la firma");
		}

	}
	
	public void completarTarea() {
		try {
			if (token) {
				String idAlfrescoInforme = documento.getIdAlfresco();

				if (!documentoSustanciasQuimicasFacade.verificarFirmaVersion(idAlfrescoInforme)) {
					JsfUtil.addMessageError("El informe no está firmado electrónicamente.");
					return;
				}	
				
				if(documento.getProcessinstanceid() == null) {
					Long idProceso = JsfUtil.getCurrentProcessInstanceId();
					documento.setProcessinstanceid(idProceso.intValue());
					documentoSustanciasQuimicasFacade.guardar(documento);
				}
				
			} else {
				if (!documentoSubido) {
					JsfUtil.addMessageError("Debe adjuntar el documento firmado.");
					return;
				} else  {
					
					if(autorizacion){
						documento = documentoSustanciasQuimicasFacade
								.guardarDocumentoAlfrescoImportacion(solicitud.getTramite(),"REGISTRO SUSTANCIAS QUIMICAS", bandejaTareasBean.getProcessId(), documentoManual, TipoDocumentoSistema.RCOA_RSQ_AUTORIZACION_IMPORTACION);
					}else{
						documento = documentoSustanciasQuimicasFacade
								.guardarDocumentoAlfrescoImportacion(solicitud.getTramite(),"REGISTRO SUSTANCIAS QUIMICAS", bandejaTareasBean.getProcessId(), documentoManual, TipoDocumentoSistema.RCOA_RSQ_ANULACION_IMPORTACION);						
					}					
				}
			}			
			
//			solicitud.setDocumentosSustanciasQuimicasRcoa(documento);
			if(autorizacion){
				solicitud.setUsuarioAutorizaSolicitud(loginBean.getUsuario());
			}else{
				solicitud.setUsuarioAutorizaAnulacion(loginBean.getUsuario());
			}
			
			solicitudImportacionRSQFacade.save(solicitud, loginBean.getUsuario());
			
			Map<String, Object> parametros = new HashMap<>();
			
			parametros.put("usuario_operador", solicitud.getUsuarioCreacion());
			
			procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);
			
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getTarea().getProcessInstanceId(), null);
							
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
			JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
		} catch (Exception e) {
			LOG.error(e);
			JsfUtil.addMessageError("Error al realizar la operación.");
		}
	}
	
	public StreamedContent descargarDocumentoRsq() throws Exception {		
		
		DefaultStreamedContent content = new DefaultStreamedContent();
		if (documentoOficio != null) {
			if (documentoOficio.getContenidoDocumento() == null) {
				documentoOficio.setContenidoDocumento(documentoSustanciasQuimicasFacade
						.descargar(documentoOficio.getIdAlfresco()));
			}
			content = new DefaultStreamedContent(new ByteArrayInputStream(
					documentoOficio.getContenidoDocumento()), documentoOficio.getExtesion());
			content.setName(documentoOficio.getNombre());
		}
		
		return content;
	}	

}
