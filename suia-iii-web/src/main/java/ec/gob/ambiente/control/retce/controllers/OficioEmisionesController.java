package ec.gob.ambiente.control.retce.controllers;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import lombok.Getter;
import lombok.Setter;
import observaciones.ObservacionesController;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.retce.model.DatoObtenidoMedicion;
import ec.gob.ambiente.retce.model.DetalleEmisionesAtmosfericas;
import ec.gob.ambiente.retce.model.EmisionesAtmosfericas;
import ec.gob.ambiente.retce.model.InformacionProyecto;
import ec.gob.ambiente.retce.model.InformeTecnicoRetce;
import ec.gob.ambiente.retce.model.OficioPronunciamientoRetce;
import ec.gob.ambiente.retce.services.DatoObtenidoMedicionFacade;
import ec.gob.ambiente.retce.services.DatosLaboratorioFacade;
import ec.gob.ambiente.retce.services.DetalleEmisionesAtmosfericasFacade;
import ec.gob.ambiente.retce.services.EmisionesAtmosfericasFacade;
import ec.gob.ambiente.retce.services.InformacionProyectoFacade;
import ec.gob.ambiente.retce.services.InformeTecnicoRetceFacade;
import ec.gob.ambiente.retce.services.OficioRetceFacade;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.DocumentosTareasProceso;
import ec.gob.ambiente.suia.domain.ObservacionesFormularios;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.TipoDocumento;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.observaciones.facade.ObservacionesFacade;
import ec.gob.ambiente.suia.persona.facade.PersonaFacade;
import ec.gob.ambiente.suia.prevencion.licenciamientoAmbiental.facade.InformeOficioFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.Email;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilGenerarInforme;

@ViewScoped
@ManagedBean
public class OficioEmisionesController {

	@EJB
	private InformeTecnicoRetceFacade informeTecnicoRetceFacade;
	@EJB
	private InformacionProyectoFacade informacionProyectoFacade;
	@EJB
	private EmisionesAtmosfericasFacade emisionAtmosfericaFacade;
	@EJB
	private InformeOficioFacade informeOficioFacade;
	@EJB
	private UsuarioFacade usuarioFacade;
	@EJB
	private AreaFacade areaFacade;
	@EJB
	private OrganizacionFacade organizacionFacade;
	@EJB
	private PersonaFacade personaFacade;
	@EJB
	private DetalleEmisionesAtmosfericasFacade detalleEmisionFacade;
	@EJB
	private DatoObtenidoMedicionFacade datoObtenidoMedicionFacade;
	@EJB
	private DatosLaboratorioFacade datosLaboratorioFacade;
	@EJB
	private OficioRetceFacade oficioRetceFacade;
	@EJB
	private DocumentosFacade documentosFacade;
	@EJB
	private ObservacionesFacade observacionesFacade;
	@EJB
	private MensajeNotificacionFacade mensajeNotificacionFacade;

	@ManagedProperty(value = "#{bandejaTareasBean}")
	@Getter
	@Setter
	private BandejaTareasBean bandejaTareasBean;

	@Getter
	@Setter
	private PlantillaReporte plantillaReporte;

	@Getter
	@Setter
	private InformeTecnicoRetce informeTecnicoRetce;

	@Getter
	@Setter
	private String tipo = "";

	@Getter
	@Setter
	private boolean revisar;

	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;

	@Getter
	@Setter
	private TipoDocumento tipoDocumento;

	@Getter
	@Setter
	private FormatoInformeEmisiones formatoInforme;

	@Getter
	@Setter
	private FormatoOficioEmisiones formatoOficio;

	@Getter
	@Setter
	private InformacionProyecto informacionProyecto;

	@Getter
	@Setter
	private EmisionesAtmosfericas emisionAtmosferica;

	@Getter
	@Setter
	private List<DetalleEmisionesAtmosfericas> listaDetalles;

	@Getter
	@Setter
	private String informePath;

	@Getter
	@Setter
	private String nombreReporte;
	@Getter
	@Setter
	private String nombreFichero;

	@Getter
	@Setter
	private File informePdf, archivoFinal;

	@Getter
	@Setter
	private byte[] archivoInforme;

	@Getter
	@Setter
	private List<DatoObtenidoMedicion> listaDatosObtenidosTotal;

	private Map<String, Object> variables;

	@EJB
	private ProcesoFacade procesoFacade;

	private String tramite;

	@Setter
	@Getter
	private Integer numeroObservaciones, numeroRevision;

	private Usuario usuarioOperador, usuarioFirma, usuarioElabora, usuarioRevisa;

	@Setter
	@Getter
	private boolean verOficio = false;

	@Setter
	@Getter
	private boolean oficioGenerado = false;

	@Getter
	private OficioPronunciamientoRetce oficio;

	@Setter
	@Getter
	private boolean habilitarElaborar = false;

	@Setter
	@Getter
	private boolean habilitarObservaciones = true;

	@Setter
	@Getter
	private boolean editarObservaciones = false;

	@Getter
	@Setter
	private boolean requiereModificaciones, requiereModificacionesOficio;

	private String nombreOperador, nombreRepresentanteLegal;

	@Setter
	@Getter
	private String urlPdf;

	private List<ObservacionesFormularios> observacionesTramite;

	@Getter
	@Setter
	@ManagedProperty(value = "#{observacionesController}")
	private ObservacionesController observacionesController;
	
	@Getter
	@Setter
	@ManagedProperty(value = "#{InformeTecnicoEmisionesController}")
	private InformeTecnicoEmisionesController informeTecnicoController;

	@PostConstruct
	public void init() {
		try {
			variables=procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
			tramite=(String)variables.get(Constantes.VARIABLE_PROCESO_TRAMITE);		
			String numObservaciones = (String)variables.get("numero_observaciones");
			if(numObservaciones != null)
				numeroObservaciones= Integer.valueOf(numObservaciones);
			String usuarioProponente=(String)variables.get("usuario_operador");
			String usuarioTecnico=(String)variables.get("usuario_tecnico");
			String usuarioCoordinador=(String)variables.get("usuario_coordinador");
			informeTecnicoRetce = new InformeTecnicoRetce();
			String variableModificaciones = (String)variables.get("tiene_observaciones_informe_oficio");
			if(variableModificaciones != null){
				requiereModificaciones = Boolean.parseBoolean(variableModificaciones);
			}			
			
			if(numeroObservaciones==null){
				numeroObservaciones=0;
			}
			
			numeroRevision=1+numeroObservaciones;
			
			usuarioRevisa=usuarioFacade.buscarUsuario(usuarioCoordinador);		
			usuarioOperador=usuarioFacade.buscarUsuario(usuarioProponente);
			usuarioElabora=usuarioFacade.buscarUsuario(usuarioTecnico);
			
			String nombreTarea=bandejaTareasBean.getTarea().getTaskName().toUpperCase();
	    	if(nombreTarea.contains("ELABORAR")){
	    		habilitarElaborar=true;
	    		revisar = false;
	    		if(numeroObservaciones==0){
	    			habilitarObservaciones=false;
	    		}
	    	}else{	    	
	    		revisar = true;
	    		editarObservaciones=false;
	    		oficioGenerado=true;
	    		habilitarObservaciones=true;
	    	}
	    		    	
	    	Organizacion organizacion=organizacionFacade.buscarPorRuc(usuarioOperador.getNombre());
			if(organizacion!=null){
				nombreOperador=organizacion.getNombre();
				nombreRepresentanteLegal=organizacion.getPersona().getNombre();
			}else{
				nombreOperador=usuarioOperador.getPersona().getNombre();
				nombreRepresentanteLegal="";
			}
						
			emisionAtmosferica = emisionAtmosfericaFacade.findByCodigo(tramite);
    					
    		tipoDocumento = new TipoDocumento();    		    		 		
    		
    		informacionProyecto = emisionAtmosferica.getInformacionProyecto();
    		
    		informeTecnicoRetce = informeTecnicoRetceFacade.getInforme(emisionAtmosferica.getCodigo(), TipoDocumentoSistema.INFORME_TECNICO_EMISIONES_ATMOSFERICAS, numeroRevision); 		    		
    		
    		oficio = oficioRetceFacade.getOficio(emisionAtmosferica.getCodigo(), TipoDocumentoSistema.OFICIO_PRONUNCIAMIENTO_EMISION_ATMOSFERICA, numeroRevision);
    		if(oficio == null){
    			oficio = new OficioPronunciamientoRetce(emisionAtmosferica.getCodigo(), TipoDocumentoSistema.OFICIO_PRONUNCIAMIENTO_EMISION_ATMOSFERICA, numeroRevision);
    		}
    		
    		List<ObservacionesFormularios> observaciones = observacionesFacade.listarPorIdClaseNombreClaseNoCorregidas(
					oficio.getId(), "oficioEmisionesRetce");
    		
			if (observaciones != null && !observaciones.isEmpty()) {
				habilitarObservaciones = true;
			} else {
				habilitarObservaciones = false;
			}
    		
    		visualizarDocumento(true, TipoDocumentoSistema.OFICIO_PRONUNCIAMIENTO_EMISION_ATMOSFERICA);			

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void buscarUsuarioFirma() {
		usuarioFirma = JsfUtil.getBean(ProcesoRetceController.class)
				.buscarAutoridadFirma(emisionAtmosferica.getInformacionProyecto().getAreaSeguimiento(),
						informeTecnicoRetce.getEsReporteAprobacion());
	}
	
	public void visualizarDocumento(boolean marcaAgua, TipoDocumentoSistema tipoDocumentoSistema) {
		
		try {
			
			String nombreReporte=null;
			String numeroDocumento=null;
			Integer idtable=null;
			Documento documento = new Documento();		
			File file = null;
			
			tipoDocumento.setId(TipoDocumentoSistema.OFICIO_PRONUNCIAMIENTO_EMISION_ATMOSFERICA.getIdTipoDocumento());				
			plantillaReporte = informeOficioFacade.obtenerPlantillaReporte(this.getTipoDocumento().getId());				
			
			nombreFichero = "OficioEmisionesAtmosfericas" + emisionAtmosferica.getCodigo()+".pdf";
			nombreReporte = "OficioEmisionesAtmosfericas";
			
			/*if(oficio.getNumeroOficio() == null){
				oficio.setNumeroOficio("MAAE-XXXX-RETCE-20XX-XXXX");
			}*/
			
			oficio.setFechaOficio(new Date());				
			
			formatoOficio = new FormatoOficioEmisiones(oficio, emisionAtmosferica, JsfUtil.getLoggedUser(), emisionAtmosferica.getUsuarioCreacion());
			formatoOficio.setUbicacion(JsfUtil.getLoggedUser().getPersona().getUbicacionesGeografica().getUbicacionesGeografica().getNombre());
			formatoOficio.setRazonSocial(nombreOperador);
			formatoOficio.setRepresentanteLegal(nombreRepresentanteLegal);
			
			buscarUsuarioFirma();
			formatoOficio.setNombreFirma(usuarioFirma.getPersona().getNombre());
			
			
			numeroDocumento=oficio.getNumeroOficio();
			idtable=oficio.getId();	
			
			file = UtilGenerarInforme.generarFichero(
                    plantillaReporte.getHtmlPlantilla(), nombreReporte, true,
                    formatoOficio);
            setInformePdf(file);
				
			
			Path path = Paths.get(file.getAbsolutePath());
	        byte[] byteArchivo = Files.readAllBytes(path);
	        File fileArchivo = new File(JsfUtil.devolverPathReportesHtml(file.getName()));
	        FileOutputStream fileOutputStream = new FileOutputStream(fileArchivo);
	        fileOutputStream.write(byteArchivo);
	        fileOutputStream.close();
	        this.urlPdf=JsfUtil.devolverContexto("/reportesHtml/"+ file.getName());
			
		
			 if(!marcaAgua){
				JsfUtil.uploadApdoDocument(file, documento);
				documento.setMime("application/pdf");
				documento.setNombreTabla(nombreReporte);
				documento.setIdTable(idtable);
				documento.setNombre(nombreReporte + "_" + numeroDocumento + ".pdf");
				documento.setExtesion(".pdf");

				DocumentosTareasProceso documentoTarea = new DocumentosTareasProceso();
				documentoTarea.setIdTarea(bandejaTareasBean.getTarea().getTaskId());
				documentoTarea.setProcessInstanceId(bandejaTareasBean.getProcessId());

				documentosFacade.guardarDocumentoAlfrescoSinProyecto(emisionAtmosferica.getCodigo(),"INFORMES_OFICIOS",documentoTarea.getProcessInstanceId(),documento,tipoDocumentoSistema,documentoTarea);		    	
			}
						
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}	
	
	public StreamedContent getStream() throws Exception {
        DefaultStreamedContent content = new DefaultStreamedContent();
        if (archivoInforme != null) {
            content = new DefaultStreamedContent(new ByteArrayInputStream(
                    archivoInforme), "application/octet-stream");
            content.setName(nombreReporte);
        }
        return content;
    }
	
	public void guardar(){		
		try {
			oficio.setIdInformeTecnico(informeTecnicoRetce.getId());
			oficioRetceFacade.guardar(oficio,emisionAtmosferica.getInformacionProyecto().getAreaResponsable(),JsfUtil.getLoggedUser());
			visualizarDocumento(true, TipoDocumentoSistema.OFICIO_PRONUNCIAMIENTO_EMISION_ATMOSFERICA);
			oficioGenerado=true;		
		JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public void enviar(){		
		try {
			
			Map<String, Object> params=new HashMap<>();
			params.put("pronunciamiento_aprobado",informeTecnicoRetce.getEsReporteAprobacion());
					
			procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), params);
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
			
			notificacion();
						
	        JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
		} catch (JbpmException e) {
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
			e.printStackTrace();
		}				
	}
	
	private void notificacion(){
		try {					
			String mensaje = mensajeNotificacionFacade.recuperarValorMensajeNotificacion("bodyNotificacionRemiteInformeOficioRetce", new Object[]{"tecnico", loginBean.getUsuario().getPersona().getNombre(), tramite});
			Email.sendEmail(usuarioRevisa, "Envio de Informe técnico y oficio para revisión", mensaje, tramite, loginBean.getUsuario());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
		
	public void atras(){
		
		JsfUtil.redirectTo("/control/retce/emisionesAtmosfericas/realizarInforme.jsf");
	}
	

}
