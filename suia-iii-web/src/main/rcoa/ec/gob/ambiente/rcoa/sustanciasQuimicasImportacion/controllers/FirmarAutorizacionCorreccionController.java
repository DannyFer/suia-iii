package ec.gob.ambiente.rcoa.sustanciasQuimicasImportacion.controllers;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;
import org.jfree.util.Log;
import org.primefaces.context.DefaultRequestContext;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.BaseColor;

import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.rcoa.dto.EntityFormularioImportacionVueRSQDTO;
import ec.gob.ambiente.rcoa.dto.EntityOficioAutorizacionCorrecionImportacionVueRSQ;
import ec.gob.ambiente.rcoa.dto.EntityTablaFormularioImportacionVueRSQDTO;
import ec.gob.ambiente.rcoa.dto.EntityTablaOficioAutorizacionCorrecionImportacionVueRSQ;
import ec.gob.ambiente.rcoa.enums.TipoInformeOficioEnum;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.ActividadSustanciaQuimicaFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.DetalleSolicitudImportacionRSQFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.DocumentosRSQFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.DocumentosSustanciasQuimicasRcoaFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.InformesOficiosRSQFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.SolicitudImportacionRSQExtVueFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.SolicitudImportacionRSQFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.ActividadSustancia;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.DetalleSolicitudImportacionRSQ;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.DocumentosSustanciasQuimicasRcoa;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.InformeOficioRSQ;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.SolicitudImportacionRSQ;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.SolicitudImportacionRSQExtVue;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.controllers.UtilViabilidad;
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
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.DigitalSign;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilGenerarInforme;
import lombok.Getter;
import lombok.Setter;

@ManagedBean
@ViewScoped
public class FirmarAutorizacionCorreccionController {
	
	private final Logger LOG = Logger.getLogger(FirmarAutorizacionCorreccionController.class);
	
	@EJB
	private ProcesoFacade procesoFacade;
	@EJB
	private UsuarioFacade usuarioFacade;
	@EJB
	private SolicitudImportacionRSQFacade solicitudImportacionRSQFacade;
	@EJB
	private SolicitudImportacionRSQExtVueFacade solicitudImportacionRSQExtVueFacade;
	@EJB
	private ActividadSustanciaQuimicaFacade actividadSustanciaQuimicaFacade;
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
    
    @ManagedProperty(value = "#{ingresoParamsReporteImportacSustanciasQuimicasController}")
    @Getter
    @Setter
    private IngresoParamsReporteImportacSustanciasQuimicasController ingresoParamsReporteImportacSustanciasQuimicasController;
    
    @Getter
	@Setter
	private byte[] archivoFormulario;
	
	@Getter
	@Setter
	private String urlFormulario, urlAlfresco;
    
    private Map<String, Object> variables;	
   
	private String tramite;
	
	@Getter
	@Setter
	private boolean autorizacion;
	
	@Getter
	@Setter
	private SolicitudImportacionRSQ solicitud;
	
	@Getter
	@Setter
	private SolicitudImportacionRSQExtVue solicitudExtVue;
	
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
	private boolean token, documentoDescargado = false, ambienteProduccion, documentoSubido = false, correccionNoAprobada = false;
	
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
	private String nroSolicitudVue;
	
	@Getter
	@Setter
	private List<DetalleSolicitudImportacionRSQ> lDetalleSolicitudImportacionRSQ;
	
	@Getter
	@Setter
	private List<EntityTablaFormularioImportacionVueRSQDTO> lEntityTablaFormularioImportacionVueRSQDTOVentana;
	
	private String justificacionNoAprobacion, accion;
	
    @PostConstruct
	public void init(){
		try {
			variables=procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
			tramite=(String)variables.get(Constantes.VARIABLE_PROCESO_TRAMITE);	
			
			String autorizacionAux = (String)variables.get("emision");
			autorizacion= Boolean.valueOf(autorizacionAux);
			accion = null;
			
			//RG 20230225:: Inicio segmento de código para eliminar trámites basura de la bandeja de tarea cuando la solicitud ha sido eliminada por bdd
			//Comentar para si no se requiere
			/* Map<String, Object> parametros = new HashMap<>();
			parametros.put("usuario_operador", "0190137643001");
			procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getTarea().getProcessInstanceId(), null);
			*/
			//RG 20230225:: Fin segmento de código para eliminar trámites basura de la bandeja de tarea cuando la solicitud ha sido eliminada por bdd
			solicitud = solicitudImportacionRSQFacade.buscarPorTramiteVue(tramite);
			solicitudExtVue = solicitudImportacionRSQExtVueFacade.buscarPorId(solicitud.getId());
			solicitud.setSolicitudImportacionRSQExtVue(solicitudExtVue);
			
			codigoRSQ = solicitud.getRegistroSustanciaQuimica().getNumeroAplicacion();
			codigo = tramite;
			nroSolicitudVue = solicitudExtVue.getReqNo();
			
			ambienteProduccion = Boolean.parseBoolean(tipoAmbiente);
			token = true;
			if(!ambienteProduccion){
				verificaToken();
			}
			
			if(solicitud != null){
				if(solicitud.getRegistroSustanciaQuimica().getProyectoLicenciaCoa() != null){
					proyecto = solicitud.getRegistroSustanciaQuimica().getProyectoLicenciaCoa();
				}	
				
				//detalle = detalleSolicitudImportacionRSQFacade.buscarPorSolicitud(solicitud);
				
				//RG 20220815:: Para más de un detalle de sustancia
				lDetalleSolicitudImportacionRSQ = detalleSolicitudImportacionRSQFacade.buscarPorIdSolicitud(solicitud);
				
				if(lDetalleSolicitudImportacionRSQ!=null && !lDetalleSolicitudImportacionRSQ.isEmpty())
					detalle = lDetalleSolicitudImportacionRSQ.get(0);
			}			
			
			if(autorizacion){
				List<DocumentosSustanciasQuimicasRcoa> documentoList = documentoSustanciasQuimicasFacade.documentoXIdTablaIdTipoDoc(solicitud.getId(), SolicitudImportacionRSQ.class.getName(), TipoDocumentoSistema.RCOA_RSQ_AUTORIZACION_IMPORTACION_VUE);
				if(documentoList != null && !documentoList.isEmpty()){
					documento = documentoList.get(0);
				}
				
				nombreFormulario = "Firmar y Enviar la Autorización de Importación Emitido";
			}else{
				List<DocumentosSustanciasQuimicasRcoa> documentoList = documentoSustanciasQuimicasFacade.documentoXIdTablaIdTipoDoc(solicitud.getId(), SolicitudImportacionRSQ.class.getName(), TipoDocumentoSistema.RCOA_RSQ_CORRECCION_IMPORTACION_VUE);
				if(documentoList != null && !documentoList.isEmpty()){
					documento = documentoList.get(0);
				}
				nombreFormulario = "Firmar y Enviar Aprobación de Corrección de Importación"; // o Enviar No Aprobación
			}
			
			List<InformeOficioRSQ> oficios = informesOficiosRSQFacade.obtenerPorRSQListaPorTipo(solicitud.getRegistroSustanciaQuimica(),TipoInformeOficioEnum.OFICIO_PRONUNCIAMIENTO);
			if(oficios != null && !oficios.isEmpty()){
				oficio = oficios.get(0);
				documentoOficio = documentosRSQFacade.obtenerDocumentoPorTipo(solicitud.getRegistroSustanciaQuimica().pronunciamientoAprobado()?TipoDocumentoSistema.RCOA_RSQ_OFICIO_APROBADO:TipoDocumentoSistema.RCOA_RSQ_OFICIO_NEGADO,"InformeOficioRSQ",oficio.getId());
			}	
		} catch (Exception e) {
			Log.error(e.getMessage());
			e.printStackTrace();
		}			
	}
    
    private void generadorDocumentoVue(){
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
			
    		EntityOficioAutorizacionCorrecionImportacionVueRSQ entity = new EntityOficioAutorizacionCorrecionImportacionVueRSQ();  		  		
    		entity.setFecha(JsfUtil.devuelveFechaEnLetrasSinHora(new Date()));
    		entity.setNombreOperador(nombreEmpresa);
    		
    		if(representante != null && !representante.isEmpty())
    			entity.setRepresentanteLegal(representante);
    		else
    			entity.setRepresentanteLegal(" ");
    		
    		entity.setNombreEmpresa(nombreEmpresa);
    		entity.setCodigoRegistroSustancias(solicitud.getRegistroSustanciaQuimica().getNumeroAplicacion());
    		entity.setNumeroTramite(solicitud.getTramite());    		
			entity.setSubsecretario(loginBean.getUsuario().getPersona().getNombre());
			entity.setRolFirmante(loginBean.getUsuario().getPersona().getPosicion());
			
    		List<EntityTablaOficioAutorizacionCorrecionImportacionVueRSQ> lEntityTablaOficioAutorizacionCorrecionImportacionVueRSQ = new ArrayList<>();
    		for (Iterator iterator = lDetalleSolicitudImportacionRSQ.iterator(); iterator.hasNext();) {
				DetalleSolicitudImportacionRSQ detalleSolicitudImportacionRSQ = (DetalleSolicitudImportacionRSQ) iterator.next();
				
				EntityTablaOficioAutorizacionCorrecionImportacionVueRSQ etoacivr = new EntityTablaOficioAutorizacionCorrecionImportacionVueRSQ();
				etoacivr.setNombreSustancia(detalleSolicitudImportacionRSQ.getActividadSustancia().getGestionarProductosQuimicosProyectoAmbiental().getSustanciaquimica().getDescripcion());
				etoacivr.setSubPartida(detalleSolicitudImportacionRSQ.getSubPartidaArancelaria());
				etoacivr.setCupoDisponible(detalleSolicitudImportacionRSQ.getCupoCantidad());
				etoacivr.setPesoNeto(detalleSolicitudImportacionRSQ.getPesoNeto()); //CantidadNeta
				etoacivr.setPesoBruto(detalleSolicitudImportacionRSQ.getPesoBruto());
				etoacivr.setTipoRecipiente(detalleSolicitudImportacionRSQ.getTipoRecipiente());
				
				
				lEntityTablaOficioAutorizacionCorrecionImportacionVueRSQ.add(etoacivr);
			}
    		
    		String datos = "<table border=\"1\" cellpadding=\"7\" cellspacing=\"0\" style=\"width: 700px;font-size: 11px !important;border:1;\">";
    		datos += "<tr><td style=\"width:5%\">No</td><td style=\"width:20%\">Sustancia Química</td><td style=\"width:20%\">Subpartida Arancelaria</td><td style=\"width:15%\">Cupo Disponible</td><td style=\"width:10%\">Cantidad Neto</td><td style=\"width:10%\">Peso Bruto</td><td style=\"width:20%\">Tipo Recipiente</td></tr>";
    		
    		int con = 0;
			for (Iterator iterator2 = lEntityTablaOficioAutorizacionCorrecionImportacionVueRSQ.iterator(); iterator2.hasNext();) {
				EntityTablaOficioAutorizacionCorrecionImportacionVueRSQ entityTablaImportacionVueRSQ = (EntityTablaOficioAutorizacionCorrecionImportacionVueRSQ) iterator2.next();
				
				datos += "<tr><td style=\"width:5%\">"
					+ (con+1)
					+ "</td><td style=\"width:20%\">"
					+ entityTablaImportacionVueRSQ.getNombreSustancia()
					+ "</td><td style=\"width:20%\">"
					+ entityTablaImportacionVueRSQ.getSubPartida()
					+ "</td><td style=\"width:15%\">"
					+ entityTablaImportacionVueRSQ.getCupoDisponible()
					+ "</td><td style=\"width:10%\">"
					+ entityTablaImportacionVueRSQ.getPesoNeto()
					+ "</td><td style=\"width:10%\">"
					+ entityTablaImportacionVueRSQ.getPesoBruto()
					+ "</td><td style=\"width:20%\">"
					+ entityTablaImportacionVueRSQ.getTipoRecipiente()
					+ "</td></tr>";
			}
			
			datos += "</tbody></table>";
			entity.setTablaSustancias(datos);
			
			if(!autorizacion){
				if(solicitud.getNumeroDocumentoCorreccion() == null){
					entity.setCodigoOficio(generarCodigoRSQ());
				}else{
					entity.setCodigoOficio(solicitud.getNumeroDocumentoCorreccion());
				}
				
				entity.setNumeroOficioAprobacion(solicitud.getNumeroDocumentoAutorizacion());
				
				Date fechaAproba = (solicitud.getFechaFinAutorizacion() != null ? solicitud.getFechaFinAutorizacion(): (solicitud.getFechaFinCorreccion() != null ? solicitud.getFechaFinCorreccion(): new Date()));
				entity.setFechaAprobacion(JsfUtil.devuelveFechaEnLetrasSinHora(fechaAproba));
	    		entity.setFechaActual(JsfUtil.devuelveFechaEnLetrasSinHora(solicitud.getFechaInicioCorreccion()));
	    		entity.setNumeroCorreccion(solicitud.getNumeroDocumentoCorreccion());
			}else {
				if(solicitud.getNumeroDocumentoAutorizacion() == null){
					entity.setCodigoOficio(generarCodigoRSQ());
				}else{
					entity.setCodigoOficio(solicitud.getNumeroDocumentoAutorizacion());
				}
				
	    		entity.setFechaActual(JsfUtil.devuelveFechaEnLetrasSinHora(solicitud.getFechaInicioAutorizacion()));
			}
			
			PlantillaReporte plantillaReporte = plantillaReporteFacade.getPlantillaReporte(TipoDocumentoSistema.RCOA_RSQ_AUTORIZACION_IMPORTACION_VUE);		
			
			if(!autorizacion){
				plantillaReporte = plantillaReporteFacade.getPlantillaReporte(TipoDocumentoSistema.RCOA_RSQ_CORRECCION_IMPORTACION_VUE);
			}
			
			String nombreReporte = "ImportacionSustanciasQuimicas" + solicitud.getTramite()+".pdf";			
			
			File informePdf = UtilGenerarInforme.generarFichero(plantillaReporte.getHtmlPlantilla(), nombreReporte, true, entity);

			Path path = Paths.get(informePdf.getAbsolutePath());
			String reporteHtmlfinal = nombreReporte;
			archivoInforme = Files.readAllBytes(path);
			File archivoFinal = new File(JsfUtil.devolverPathReportesHtml(reporteHtmlfinal));
			FileOutputStream file = new FileOutputStream(archivoFinal);
			file.write(Files.readAllBytes(path));
			file.close();
						
			TipoDocumento tipoDoc = new TipoDocumento();
			if(autorizacion){
				tipoDoc.setId(TipoDocumentoSistema.RCOA_RSQ_AUTORIZACION_IMPORTACION_VUE.getIdTipoDocumento());
			}else{
				tipoDoc.setId(TipoDocumentoSistema.RCOA_RSQ_CORRECCION_IMPORTACION_VUE.getIdTipoDocumento());
			}			
			
			DocumentosSustanciasQuimicasRcoa documento = new DocumentosSustanciasQuimicasRcoa();
			documento.setNombre(nombreReporte);
			documento.setExtesion(".pdf");
			documento.setMime("application/pdf");					
			documento.setContenidoDocumento(archivoInforme);
			documento.setNombreTabla(SolicitudImportacionRSQ.class.getSimpleName());
			documento.setTipoDocumento(tipoDoc);
			documento.setIdTable(solicitud.getId());			
			int procesoId = (int)bandejaTareasBean.getProcessId();
			documento.setProcessinstanceid(procesoId);
			documento.setGestionarProductosQuimicosProyectoAmbiental(solicitud.getGestionarProductosQuimicosProyectoAmbiental());
			
			if(!token)
				accion = "fdm"; //Firma documento manualmente
			
			if(autorizacion && ("fd".equals(accion) || "fdm".equals(accion))){
				documento = documentoSustanciasQuimicasFacade.guardarDocumentoAlfrescoImportacion(solicitud.getTramite(),"REGISTRO SUSTANCIAS QUIMICAS", bandejaTareasBean.getProcessId(), documento, TipoDocumentoSistema.RCOA_RSQ_AUTORIZACION_IMPORTACION_VUE);
				//solicitud.setUsuarioAutorizaSolicitud(loginBean.getUsuario());
				solicitud.setNumeroDocumentoAutorizacion(entity.getCodigoOficio());
				solicitud.setFechaFinAutorizacion(new Date());
				//solicitud.setProcesoRequerimiento(Constantes.SOLICITUD_APROBADA_PROCESADA);
			}else if ("fd".equals(accion) || "fdm".equals(accion)){
				documento = documentoSustanciasQuimicasFacade.guardarDocumentoAlfrescoImportacion(solicitud.getTramite(),"REGISTRO SUSTANCIAS QUIMICAS", bandejaTareasBean.getProcessId(), documento, TipoDocumentoSistema.RCOA_RSQ_CORRECCION_IMPORTACION_VUE);
				//solicitud.setUsuarioCorrigeSolicitud(loginBean.getUsuario());
				solicitud.setNumeroDocumentoCorreccion(entity.getCodigoOficio());
				solicitud.setFechaFinCorreccion(new Date());
				//solicitud.setProcesoRequerimiento(correccionNoAprobada ? Constantes.CORRECCION_NO_APROBADA_PROCESADA:Constantes.CORRECCION_APROBADA_PROCESADA);
				//solicitud.setJustificacionCorreccion(justificacionNoAprobacion);
			}
			
			setDocumento(documento);
			
			solicitudImportacionRSQFacade.save(solicitud, loginBean.getUsuario()); //RG 20230425:: Se comenta para que solo grabe cuando se dé clic en Enviar		
		} catch (Exception e) {
			Log.error(e.getMessage());
			e.printStackTrace();
		} 	
    }
    
    private String generarCodigoRSQ() {
		String anioActual=secuenciasFacade.getCurrentYear();		
		String nombreSecuencia="SUSTANCIAS_QUIMICAS_IMPORTACION"+anioActual;
		
		try {
			return "MAAE-RSQ-IMP"
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
    	if(documento != null && null != documento.getFechaCreacion() && null != documento.getUsuarioCreacion()){
    		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
    	    String fechaDocumento = sdf.format(documento.getFechaCreacion());
    	    
    	    String fechaActual = sdf.format(new Date());		
    	   
    		if(!fechaActual.equals(fechaDocumento)){
    			documento = null;
    		}
        	
        	if(documento != null && !documento.getUsuarioCreacion().equals(loginBean.getUsuario().getNombre())){
        		documento = null;
        	}
    	}
    	
    	if((documento == null || documento.getId() == null) || "fd".equals(accion)){
    		generadorDocumentoVue();
    	}
    }
	
	public String firmarDocumento() {
		try {
			accion = "fd"; //firmarDocumento
			crearDocumento();
			
			String documentOffice = documentoSustanciasQuimicasFacade.direccionDescarga(documento);
			urlAlfresco = DigitalSign.sign(documentOffice, JsfUtil.getLoggedUser().getNombre());
			RequestContext context = RequestContext.getCurrentInstance();
			context.execute("window.location.href='"+urlAlfresco+"'");
			return urlAlfresco; 			
		} catch (Exception exception) {
			LOG.error("Ocurrió un error durante la firma del certificado:: ", exception);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
		
		return "";
	}
	
	public StreamedContent descargarDocumento() throws Exception {
		accion = "dd"; //descargarDocumento
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
      JsfUtil.validarPaginasUrlTareasBpm(bandejaTareasBean.getTarea(), "/prevencion/categoria2/v2/pagoServicios.jsf");
	}
	
	public void uploadListenerDocumentos(FileUploadEvent event) {
		if (documentoDescargado == true) {
			
			TipoDocumento tipoDoc = new TipoDocumento();
			if(autorizacion){
				tipoDoc.setId(TipoDocumentoSistema.RCOA_RSQ_AUTORIZACION_IMPORTACION_VUE.getIdTipoDocumento());
			}else{
				tipoDoc.setId(TipoDocumentoSistema.RCOA_RSQ_CORRECCION_IMPORTACION_VUE.getIdTipoDocumento());
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
			if (!correccionNoAprobada) {
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
							documento = documentoSustanciasQuimicasFacade.guardarDocumentoAlfrescoImportacion(solicitud.getTramite(),"REGISTRO SUSTANCIAS QUIMICAS", bandejaTareasBean.getProcessId(), documentoManual, TipoDocumentoSistema.RCOA_RSQ_AUTORIZACION_IMPORTACION_VUE);
						}else if (!correccionNoAprobada) {
							documento = documentoSustanciasQuimicasFacade.guardarDocumentoAlfrescoImportacion(solicitud.getTramite(),"REGISTRO SUSTANCIAS QUIMICAS", bandejaTareasBean.getProcessId(), documentoManual, TipoDocumentoSistema.RCOA_RSQ_CORRECCION_IMPORTACION_VUE);						
						}					
					}
				}			
			}else if (justificacionNoAprobacion==null || justificacionNoAprobacion.isEmpty()) {
				JsfUtil.addMessageError("Debe ingresar el motivo de no aprobación");
				return;
			}
			
			if(!token)
				accion = "fdm"; //Firma documento manualmente
			
			if(autorizacion && ("fd".equals(accion) || "fdm".equals(accion))){
				solicitud.setUsuarioAutorizaSolicitud(loginBean.getUsuario());
				solicitud.setProcesoRequerimiento(Constantes.SOLICITUD_APROBADA_PROCESADA);
				solicitud.setFechaFinAutorizacion(new Date());
			}else if ("fd".equals(accion) || "fdm".equals(accion)){
				solicitud.setUsuarioCorrigeSolicitud(loginBean.getUsuario());
				solicitud.setCorreccion(!correccionNoAprobada);
				solicitud.setProcesoRequerimiento(correccionNoAprobada ? Constantes.CORRECCION_NO_APROBADA_PROCESADA:Constantes.CORRECCION_APROBADA_PROCESADA);
				solicitud.setFechaFinCorreccion(new Date());
				
				if(correccionNoAprobada) {
					solicitud.setNumeroDocumentoCorreccion(null);
					solicitud.setProcesoRequerimiento(Constantes.CORRECCION_NO_APROBADA_PROCESADA);
					solicitud.setJustificacionCorreccion(justificacionNoAprobacion);
				}
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
		//FT
		// llmar a pagina ingresoInformacionImportacionVueVer 
		// al parecer debería abrir una nueva pestaña. MEJOR sería mostrarlo en el dialogo
		//lo siguiente no sirve
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

	public void cerrarDialogo() {
		
		System.out.println("Valor de justificacionNoAprobacion: "+justificacionNoAprobacion);
		
		if(justificacionNoAprobacion!=null) {
			RequestContext context = RequestContext.getCurrentInstance();
			context.execute("PF('signDialogDocumento1').hide();");
			
			//RequestContext.getCurrentInstance().execute("PF('signDialogDocumento1').hide();");
			//DefaultRequestContext.getCurrentInstance().execute("PF('finalizeDlg').show();");
		}
	}
	
	public String getJustificacionNoAprobacion() {
		return justificacionNoAprobacion;
	}

	public void setJustificacionNoAprobacion(String justificacionNoAprobacion) {
		this.justificacionNoAprobacion = justificacionNoAprobacion;
	}
	
	public void seleccionarListenerAux(){
		try {
			
			
			generarFormularioImportacionVue(Boolean.FALSE);
			
			//urlFormulario = "/pages/rcoa/sustanciasQuimicas/importacionDesdeVue/reporteImportacionSustanciasQuimicaVue.jsf"+"?idSolicImportRSQ="+solicitudImportacionRSQVueDTOSelected.getSolicitudImportacionRSQ().getId();
			RequestContext.getCurrentInstance().execute("PF('dlgVisualizeDoc').show();");
		} catch (Exception e) {
			Log.error(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void generarFormularioImportacionVue(Boolean marcaAgua) {
		try {
			PlantillaReporte plantillaReporte = plantillaReporteFacade.getPlantillaReporte(TipoDocumentoSistema.RCOA_FORMULARIO_VUE_IMPORTACION_SUSTANCIAS_QUIMICAS);
			
			EntityFormularioImportacionVueRSQDTO entity = cargarDatosReporteTipoFormulario();
			entity.setNombreReporte("FormularioImportacionRSQVue_" + UtilViabilidad.getFileNameEscaped(entity.getNumeroSolicitud().replace("/", "-")) + ".pdf");
			
			File formPdfAux = UtilGenerarInforme.generarFichero(plantillaReporte.getHtmlPlantilla(), entity.getNombreReporte(), true, entity);
			File formPdf = JsfUtil.fileMarcaAgua(formPdfAux, marcaAgua ? " - - BORRADOR - - " : " ", BaseColor.GRAY);

			Path path = Paths.get(formPdf.getAbsolutePath());
			archivoFormulario = Files.readAllBytes(path);
			
			String reporteHtmlfinal = entity.getNombreReporte().replace("/", "-");
			File archivoFinal = new File(JsfUtil.devolverPathReportesHtml(reporteHtmlfinal));
			FileOutputStream file = new FileOutputStream(archivoFinal);
			file.write(archivoFormulario);
			file.close();
			urlFormulario = JsfUtil.devolverContexto("/reportesHtml/" + entity.getNombreReporte());
			
			nombreFormulario = entity.getNombreReporte();
		} catch (Exception ex) {
			ex.printStackTrace();
			LOG.error("Error al cargar el formulario de Importación de Sustancias Químicas", ex);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}
	
	public EntityFormularioImportacionVueRSQDTO cargarDatosReporteTipoFormulario() throws ServiceException {
		EntityFormularioImportacionVueRSQDTO entity = new EntityFormularioImportacionVueRSQDTO();
		
		entity.setNumeroSolicitud(solicitud.getReqNo());
		entity.setFechaSolicitud(solicitud.getSolicitudImportacionRSQExtVue().getReqDe()); 
		entity.setCiudadSolicitud(solicitud.getSolicitudImportacionRSQExtVue().getReqCityNm());
		entity.setFechaAprobacion(solicitud.getSolicitudImportacionRSQExtVue().getApvDe());
		entity.setFechaInicioVigenciaLicencia(solicitud.getSolicitudImportacionRSQExtVue().getLicIniDe());
		entity.setFechaFinVigenciaLicencia(solicitud.getSolicitudImportacionRSQExtVue().getLicExpDe());
		entity.setClasificacionSolicitante(solicitud.getSolicitudImportacionRSQExtVue().getDclrClNm());
		entity.setTipoSolicitante(solicitud.getSolicitudImportacionRSQExtVue().getDclrIdtNoTypeNm());
		entity.setNumeroIdentificacionSolicitante(solicitud.getSolicitudImportacionRSQExtVue().getDclrIdtNo());
		entity.setNombreRazonSocialSolicitante(solicitud.getSolicitudImportacionRSQExtVue().getDclrCmpNm());
		entity.setNombreSolicitante(solicitud.getSolicitudImportacionRSQExtVue().getDclrNm());
		entity.setProvinciaSolicitante(solicitud.getSolicitudImportacionRSQExtVue().getDclrPrvhcNm());
		entity.setCantonSolicitante(solicitud.getSolicitudImportacionRSQExtVue().getDclrCutyNm());
		entity.setParroquiaSolicitante(solicitud.getSolicitudImportacionRSQExtVue().getDclrPrqiNm());
		entity.setDireccionSolicitante(solicitud.getSolicitudImportacionRSQExtVue().getDclrAd());
		entity.setTelefonoSolicitante(solicitud.getSolicitudImportacionRSQExtVue().getDclrTelNo());
		entity.setCelularSolicitante(solicitud.getSolicitudImportacionRSQExtVue().getDclrCelNo());
		entity.setCorreoElectronicoSoliciante(solicitud.getSolicitudImportacionRSQExtVue().getDclrEm());
		entity.setClasificacionImportador(solicitud.getSolicitudImportacionRSQExtVue().getImprClNm());
		entity.setTipoImportador(solicitud.getSolicitudImportacionRSQExtVue().getImprIdtNoTypeNm());
		entity.setNumeroIdentificacionImportador(solicitud.getSolicitudImportacionRSQExtVue().getImprIdtNo());
		entity.setNombreRazonSocialImportador(solicitud.getSolicitudImportacionRSQExtVue().getImprCmpNm());
		entity.setCodigoRSQ(solicitud.getSolicitudImportacionRSQExtVue().getImprRsqCd());
		entity.setNombreImportador(solicitud.getSolicitudImportacionRSQExtVue().getImprNm());
		entity.setProvinciaImportador(solicitud.getSolicitudImportacionRSQExtVue().getImprPrvhcNm());
		entity.setCantonImportador(solicitud.getSolicitudImportacionRSQExtVue().getImprCutyNm());
		entity.setParroquiaImportador(solicitud.getSolicitudImportacionRSQExtVue().getImprPrqiNm());
		entity.setDireccionImportador(solicitud.getSolicitudImportacionRSQExtVue().getImprAd());
		entity.setTelefonoImportador(solicitud.getSolicitudImportacionRSQExtVue().getImprTelNo());
		entity.setCorreoElectronicoImportador(solicitud.getSolicitudImportacionRSQExtVue().getImprEm());
		entity.setPaisEmbarque(solicitud.getSolicitudImportacionRSQExtVue().getLdCtryNm());
		entity.setMedioTransporte(solicitud.getSolicitudImportacionRSQExtVue().getLdTrspWayNm());
		entity.setPuertoEmbarque(solicitud.getSolicitudImportacionRSQExtVue().getLdPortNm());
		entity.setLugarDesembarque(solicitud.getSolicitudImportacionRSQExtVue().getLdUnldPlNm());

		List<EntityTablaFormularioImportacionVueRSQDTO> lEntityTablaFormularioImportacionVueRSQDTO = new ArrayList<>();
		
		String datosSustancias = "<table border=\"1\" cellpadding=\"7\" cellspacing=\"0\" style=\"width: 700px;font-size: 11px !important;border:1;\"><tbody>"
		+ "<tr><td style=\\\"width:20%\\\" align=\"center\">No.</td><td style=\"width:20%\" align=\"center\">Sustancia</td><td style=\"width:20%\" align=\"center\">Subpartida Arancelaria</td><td style=\"width:20%\" align=\"center\">País de Origen</td><td style=\"width:15%\" align=\"center\">Peso Neto Kg</td><td style=\"width:15%\" align=\"center\">Peso Bruto Kg</td><td style=\\\"width:20%\\\" align=\"center\">Tipo Recipiente</td></tr>";
		
		int con = 1;
		for (Iterator<DetalleSolicitudImportacionRSQ> iterator = lDetalleSolicitudImportacionRSQ.iterator(); iterator.hasNext();) {
			DetalleSolicitudImportacionRSQ detalleSolicitudImportacionRSQ = (DetalleSolicitudImportacionRSQ) iterator.next();
			
			EntityTablaFormularioImportacionVueRSQDTO etfivrDto = new EntityTablaFormularioImportacionVueRSQDTO();
			etfivrDto.setCodigoSustancia(detalleSolicitudImportacionRSQ.getActividadSustancia().getGestionarProductosQuimicosProyectoAmbiental().getSustanciaquimica().getNumeroCas());
			etfivrDto.setNombreSustancia(detalleSolicitudImportacionRSQ.getActividadSustancia().getGestionarProductosQuimicosProyectoAmbiental().getSustanciaquimica().getDescripcion());
			etfivrDto.setSubPartida(detalleSolicitudImportacionRSQ.getSubPartidaArancelaria());
			etfivrDto.setUbicacionGeografica(detalleSolicitudImportacionRSQ.getUbicacionGeografica().getNombre());
			etfivrDto.setPesoNeto(detalleSolicitudImportacionRSQ.getPesoNeto());
			etfivrDto.setPesoBruto(detalleSolicitudImportacionRSQ.getPesoBruto());
			etfivrDto.setTipoRecipiente(detalleSolicitudImportacionRSQ.getTipoRecipiente());
			
			lEntityTablaFormularioImportacionVueRSQDTO.add(etfivrDto);
			
			datosSustancias += "<tr>"
				+ "<td style=\"width:5%\">"
				+ con++
				+ "</td><td style=\"width:20%\">"
				+ etfivrDto.getNombreSustancia()
				+ "</td><td style=\"width:20%\">"
				+ etfivrDto.getSubPartida()
				+ "</td><td style=\"width:20%\">"
				+ etfivrDto.getUbicacionGeografica()
				+ "</td><td style=\"width:10%\">"
				+ etfivrDto.getPesoNeto()
				+ "</td><td style=\"width:10%\">"
				+ etfivrDto.getPesoBruto()
				+ "</td><td style=\"width:20%\">"
				+ etfivrDto.getTipoRecipiente()
				+ "</td>"
				+ "</tr>";
		}
		
		datosSustancias += "</tbody></table>";
		entity.setTablaSustancias(datosSustancias);
		
		return entity;
	}
	
	public void cargarDatosTablaVentanaRsq() throws ServiceException {
		RequestContext.getCurrentInstance().execute("PF('idDialog').show();");
	}
	
	public void limpiarTextAreaJustificacionNoAprobacion() {
		justificacionNoAprobacion = null;
	}
}
