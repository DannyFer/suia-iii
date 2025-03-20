package ec.gob.ambiente.rcoa.sustanciasQuimicasImportacion.controllers;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
import ec.gob.ambiente.rcoa.model.GestionarProductosQuimicosProyectoAmbiental;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.ActividadSustanciaQuimicaFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.DetalleSolicitudImportacionRSQFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.DocumentosSustanciasQuimicasRcoaFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.RegistroSustanciaQuimicaFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.SolicitudImportacionRSQFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.ActividadSustancia;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.DetalleSolicitudImportacionRSQ;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.DocumentosSustanciasQuimicasRcoa;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.RegistroSustanciaQuimica;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.SolicitudImportacionRSQ;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class IngresoInformacionImportacionVerController {
	
	private static final Logger LOG = Logger.getLogger(IngresoInformacionImportacionVerController.class);
	
	@EJB
	private UbicacionGeograficaFacade ubicacionGeograficaFacade;
	@EJB
	private RegistroSustanciaQuimicaFacade registroSustanciaQumicaFacade;
	@EJB
	private ActividadSustanciaQuimicaFacade actividadSustanciaQuimicaFacade;
	@EJB
	private ProcesoFacade procesoFacade;
	@EJB
	private SolicitudImportacionRSQFacade solicitudImportacionRSQFacade;
	@EJB
	private DetalleSolicitudImportacionRSQFacade detalleSolicitudImportacionRSQFacade;
	@EJB
	private DocumentosSustanciasQuimicasRcoaFacade documentosFacade;
	@EJB
	private UsuarioFacade usuarioFacade;
	
	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;
	
	@ManagedProperty(value = "#{bandejaTareasBean}")
    @Getter
    @Setter
    private BandejaTareasBean bandejaTareasBean;
	
	@EJB(lookup = Constantes.JBPM_EJB_TASK_BEAN)
    private TaskBeanFacade taskBeanFacade;
    
    @ManagedProperty(value = "#{proyectosBean}")
    @Getter
    @Setter
    private ProyectosBean proyectosBean;
    
    private Map<String, Object> variables;	
   
	private String tramite;
	
	private boolean autorizacion;
	
	@Getter
	@Setter
	private boolean tipo;
		
	@Getter
	@Setter
	private GestionarProductosQuimicosProyectoAmbiental sustancia;
	
	@Getter
	@Setter
	private double cantidad, pesoNeto, pesoBruto;
	
	@Getter
	@Setter
	private List<UbicacionesGeografica> listaUbicaciones;
	
	@Getter
	@Setter
	private List<RegistroSustanciaQuimica> listaRegistrosRSQ;
	
	@Getter
	@Setter
	private RegistroSustanciaQuimica registroRSQ;
	
	@Getter
	@Setter
	private String tramiteRSQ;
	
	@Getter
	private List<ActividadSustancia> actividadSustanciaProyectoList;
	
	@Getter
	@Setter
	private ActividadSustancia actividadSustanciaSeleccionada;
	
	@Getter
	@Setter
	private SolicitudImportacionRSQ solicitud;
	
	@Getter
	@Setter
	private DetalleSolicitudImportacionRSQ detalle;
	
	@Getter
	@Setter
	private Integer idActividadSustanciaSeleccionada;
	
	@Getter
	@Setter
	private List<SolicitudImportacionRSQ> listaSolicitudes;
	
	@Getter
	@Setter
	private Integer idPais;
	
	@Getter
	@Setter
	private Double cupoCantidad;
	
	@Getter
	@Setter
	private DocumentosSustanciasQuimicasRcoa documentoEvidencia;
	
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyecto;
	
	@PostConstruct
	private void init(){
		try {
			variables=procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
			tramite=(String)variables.get(Constantes.VARIABLE_PROCESO_TRAMITE);	
			
			String autorizacionAux = (String)variables.get("emision");
			autorizacion= Boolean.valueOf(autorizacionAux);
						
			solicitud = solicitudImportacionRSQFacade.buscarPorTramite(tramite);
			
			if(solicitud != null){
				proyecto = solicitud.getRegistroSustanciaQuimica().getProyectoLicenciaCoa();
				detalle = detalleSolicitudImportacionRSQFacade.buscarPorSolicitud(solicitud);
			}	
						
			loginBean.getUsuario().getPersona().getNombre();
						
			tramiteRSQ = "";
			if(registroRSQ != null){
				tramiteRSQ = registroRSQ.getNumeroAplicacion();
				cargarDatosRSQ();
			}
			
			tipo = true;
			
		} catch (Exception e) {
			Log.error(e.getStackTrace());
			e.printStackTrace();
		}	
	}
	
	private void cargarDatosRSQ(){
		try {
			actividadSustanciaProyectoList = new ArrayList<ActividadSustancia>();
			actividadSustanciaProyectoList=actividadSustanciaQuimicaFacade.obtenerActividadesPorRSQImportacion(registroRSQ);
			
			solicitud = new SolicitudImportacionRSQ();	
			detalle = new DetalleSolicitudImportacionRSQ();
			detalle.setUnidadPesoBruto("kg");
			detalle.setUnidadPesoNeto("kg");
			
			if(registroRSQ != null && registroRSQ.getId() != null)
				listaSolicitudes = solicitudImportacionRSQFacade.listaPorRSQ(registroRSQ.getId());
			else
				listaSolicitudes = new ArrayList<>();		
			
			
		} catch (Exception e) {
			e.printStackTrace();			
		}
	}
	
	public List<RegistroSustanciaQuimica> getListaRegistro(){
		List<RegistroSustanciaQuimica> lista = new ArrayList<RegistroSustanciaQuimica>();		
		
		try {
			
			List<RegistroSustanciaQuimica> listaAux = registroSustanciaQumicaFacade.obtenerRegistrosSustanciasPorUsuario(loginBean.getUsuario());
			
			for(RegistroSustanciaQuimica reg : listaAux){
				List<ActividadSustancia> actividadSustanciaProyectoLista=actividadSustanciaQuimicaFacade.obtenerActividadesPorRSQImportacion(reg);
				
				if(actividadSustanciaProyectoLista != null && !actividadSustanciaProyectoLista.isEmpty()){
					lista.add(reg);
				}
			}			
			
		} catch (Exception e) {
			Log.error(e.getStackTrace());
			e.printStackTrace();
		}
		return lista;
	}
	
	public void ingresar(RegistroSustanciaQuimica registro){
		
		JsfUtil.cargarObjetoSession("registroRSQ", registro.getId());
		
		JsfUtil.redirectTo("/pages/rcoa/sustanciasQuimicas/importacion/ingresoInformacionImportacion.jsf");
		
	}
	
	public void sustanciaListener(){
		try {
			
			actividadSustanciaSeleccionada = actividadSustanciaQuimicaFacade.obtenerActividadesPorId(idActividadSustanciaSeleccionada);
			
			if(detalle.getCupoCantidad() != null){				
				if(actividadSustanciaSeleccionada.getGestionarProductosQuimicosProyectoAmbiental().getSustanciaquimica().getDescripcion().equals("Mercurio")){
					cupoCantidad = detalle.getCupoCantidad().doubleValue();
				}else{
					cupoCantidad = null;
				}	
			}else{

				if(actividadSustanciaSeleccionada.getGestionarProductosQuimicosProyectoAmbiental().getSustanciaquimica().getDescripcion().equals("Mercurio")){
					cupoCantidad = actividadSustanciaSeleccionada.getCupo();
				}else{
					cupoCantidad = null;
				}	
			}		
			
		} catch (Exception e) {
			Log.error(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void paisListener(){
		try {
			
			UbicacionesGeografica pais = ubicacionGeograficaFacade.nivelNAcional(idPais);
			detalle.setUbicacionGeografica(pais);
			
		} catch (Exception e) {
			Log.error(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void uploadDocumento(FileUploadEvent event){		
		documentoEvidencia = new DocumentosSustanciasQuimicasRcoa();
		documentoEvidencia = this.uploadListener(event, DocumentosSustanciasQuimicasRcoa.class, "pdf");
//		solicitud.setDocumentosSustanciasQuimicasRcoa(documento);
	}
	
	private DocumentosSustanciasQuimicasRcoa uploadListener(FileUploadEvent event, Class<?> clazz, String extension) {
		byte[] contenidoDocumento = event.getFile().getContents();
		DocumentosSustanciasQuimicasRcoa documento = crearDocumento(contenidoDocumento, clazz, extension);
		documento.setNombre(event.getFile().getFileName());
		return documento;
	}

	public DocumentosSustanciasQuimicasRcoa crearDocumento(byte[] contenidoDocumento, Class<?> clazz, String extension) {
		DocumentosSustanciasQuimicasRcoa documento = new DocumentosSustanciasQuimicasRcoa();
		documento.setContenidoDocumento(contenidoDocumento);		
		documento.setExtesion("." + extension);

		documento.setMime(extension == "pdf" ? "application/pdf" : "application/vnd.ms-excel");
		return documento;
	}
	
	public void guardar(){
		try {
						
			if(tipo){
				solicitud.setAnulacion(false);
				solicitud.setAutorizacion(true);
				solicitud.setGestionarProductosQuimicosProyectoAmbiental(actividadSustanciaSeleccionada.getGestionarProductosQuimicosProyectoAmbiental());	
				solicitud.setFechaInicioAutorizacion(new Date());
				solicitud.setRegistroSustanciaQuimica(registroRSQ);
				solicitud.setSustanciaQuimicaPeligrosa(actividadSustanciaSeleccionada.getGestionarProductosQuimicosProyectoAmbiental().getSustanciaquimica());
								
				solicitudImportacionRSQFacade.save(solicitud, loginBean.getUsuario());
				
				Double cupoGuardar = cupoCantidad - detalle.getPesoNeto().doubleValue(); 
				
				detalle.setCupoCantidad(BigDecimal.valueOf(cupoGuardar));				
				detalle.setSubPartidaArancelaria(actividadSustanciaSeleccionada.getGestionarProductosQuimicosProyectoAmbiental().getSustanciaquimica().getPartidaArancelaria());				
				detalle.setSolicitudImportacionRSQ(solicitud);
				
				detalleSolicitudImportacionRSQFacade.save(detalle, loginBean.getUsuario());
				
			}else{
				solicitud.setAnulacion(true);
				solicitud.setAutorizacion(false);
				solicitud.setRegistroSustanciaQuimica(registroRSQ);
				solicitud.setFechaAnulacion(new Date());
				
//				Double cupoGuardar = cupoCantidad + detalle.getPesoNeto().doubleValue(); 
//				
//				detalle.setCupoCantidad(BigDecimal.valueOf(cupoGuardar));
				
				if(documentoEvidencia == null || documentoEvidencia.getContenidoDocumento() == null){
					JsfUtil.addMessageError("Adjunte documento de Evidencia");
					return;
				}
				
				solicitudImportacionRSQFacade.save(solicitud, loginBean.getUsuario());
				
				if(documentoEvidencia != null && documentoEvidencia.getContenidoDocumento() != null){
					
					documentoEvidencia.setIdTable(solicitud.getId());					
					documentoEvidencia.setNombreTabla(SolicitudImportacionRSQ.class.getSimpleName());					
					DocumentosSustanciasQuimicasRcoa documento = documentosFacade.guardarDocumentoAlfrescoImportacion(solicitud.getTramite(), "IMPORTACION SUSTANCIAS QUIMICAS", bandejaTareasBean.getProcessId(), documentoEvidencia, TipoDocumentoSistema.RCOA_RSQ_JUSTIFICACION_ANULACION_IMPORTACION);
											
					solicitud.setDocumentosSustanciasQuimicasRcoa(documento);
				}	
				solicitudImportacionRSQFacade.save(solicitud, loginBean.getUsuario());
				detalle.setSolicitudImportacionRSQ(solicitud);				
				
				detalleSolicitudImportacionRSQFacade.save(detalle, loginBean.getUsuario());
			}			
			
		} catch (Exception e) {
			Log.error(e.getMessage());
			e.printStackTrace();
		}
		
		
	}
	
	public void enviar(){
		try {
			Usuario usuarioAutoridad = new Usuario();
			List<Usuario> listaUsuarios = usuarioFacade.buscarUsuarioPorRolActivo(Constantes.getRoleAreaName("role.area.subsecretario.calidad.ambiental"));
			
			if(listaUsuarios != null && !listaUsuarios.isEmpty()){
				usuarioAutoridad = listaUsuarios.get(0);
			}else
				usuarioAutoridad = null;
			
			Map<String, Object> parametros = new ConcurrentHashMap<String, Object>();
			parametros.put("usuario_operador", JsfUtil.getLoggedUser().getNombre());
			parametros.put("tramite", solicitud.getTramite());
			parametros.put("idProyecto", registroRSQ.getProyectoLicenciaCoa().getId());	
			parametros.put("usuario_subsecretario", usuarioAutoridad.getNombre());
			parametros.put("emision", tipo);

			procesoFacade.iniciarProceso(JsfUtil.getLoggedUser(),
					Constantes.RCOA_PROCESO_SUSTANCIAS_QUIMICAS_IMPORTACION, solicitud.getTramite(),
					parametros);
			
			JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");

		} catch (Exception e) {
			JsfUtil.addMessageError("Ha ocurrido un error. Por favor comuníquese con Mesa de Ayuda.");
			LOG.error(e.getMessage() + " " + e.getCause().getMessage());			
		}
		
	}
	
	public void seleccionarListener(Integer id){
		try {
			System.out.println("id seleccionada" + id);
			
			SolicitudImportacionRSQ solicitudSeleccionada = new SolicitudImportacionRSQ();
			solicitudSeleccionada = solicitudImportacionRSQFacade.buscarPorId(id);
			
			DetalleSolicitudImportacionRSQ detalleSeleccionado = detalleSolicitudImportacionRSQFacade.buscarPorSolicitud(solicitudSeleccionada);
			
			cupoCantidad = detalleSeleccionado.getCupoCantidad().doubleValue();			
			
			Double cupoGuardar = cupoCantidad + detalleSeleccionado.getPesoNeto().doubleValue(); 
			
			solicitud.setSolicitudAnulada(solicitudSeleccionada);
			detalle.setCupoCantidad(BigDecimal.valueOf(cupoGuardar));
			
		} catch (Exception e) {
			Log.error(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void tipoTramite(){
		
		if(tipo){
			solicitud.setSolicitudAnulada(null);
			solicitud.setJustificacionAnulacion(null);
			solicitud.setAutorizacion(true);
			solicitud.setDocumentosSustanciasQuimicasRcoa(null);
		}else{
			solicitud.setAnulacion(true);
			solicitud.setFechaInicioAutorizacion(null);
			solicitud.setGestionarProductosQuimicosProyectoAmbiental(null);
			detalle.setCupoCantidad(null);
			detalle.setPesoBruto(null);
			detalle.setPesoNeto(null);
			detalle.setSubPartidaArancelaria(null);
			detalle.setUbicacionGeografica(null);
		}
	}
	
	public StreamedContent descargarDocumento() throws Exception {		
		
		DefaultStreamedContent content = new DefaultStreamedContent();
		if (solicitud.getDocumentosSustanciasQuimicasRcoa() != null) {
			if (solicitud.getDocumentosSustanciasQuimicasRcoa().getContenidoDocumento() == null) {
				solicitud.getDocumentosSustanciasQuimicasRcoa().setContenidoDocumento(documentosFacade
						.descargar(solicitud.getDocumentosSustanciasQuimicasRcoa().getIdAlfresco()));
			}
			content = new DefaultStreamedContent(new ByteArrayInputStream(
					solicitud.getDocumentosSustanciasQuimicasRcoa().getContenidoDocumento()), solicitud.getDocumentosSustanciasQuimicasRcoa().getExtesion());
			content.setName(solicitud.getDocumentosSustanciasQuimicasRcoa().getNombre());
		}
		
		return content;
	}

}
