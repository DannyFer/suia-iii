package ec.gob.ambiente.control.retce.controllers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
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

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.BaseColor;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.retce.model.DescargasLiquidas;
import ec.gob.ambiente.retce.model.DetalleDescargasLiquidas;
import ec.gob.ambiente.retce.model.GestorDesechosPeligrosos;
import ec.gob.ambiente.retce.model.InformeTecnicoRetce;
import ec.gob.ambiente.retce.model.OficioPronunciamientoRetce;
import ec.gob.ambiente.retce.services.DatoObtenidoMedicionDescargasFacade;
import ec.gob.ambiente.retce.services.DatosLaboratorioFacade;
import ec.gob.ambiente.retce.services.DescargasLiquidasFacade;
import ec.gob.ambiente.retce.services.DetalleDescargasLiquidasFacade;
import ec.gob.ambiente.retce.services.GestorDesechosPeligrososFacade;
import ec.gob.ambiente.retce.services.InformeTecnicoRetceFacade;
import ec.gob.ambiente.retce.services.OficioRetceFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.DocumentosTareasProceso;
import ec.gob.ambiente.suia.domain.ObservacionesFormularios;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.observaciones.facade.ObservacionesFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.reportes.UtilDocumento;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.DigitalSign;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilGenerarInforme;

@ManagedBean
@ViewScoped
public class InformeOficioGestorController implements Serializable {

	private static final long serialVersionUID = 1L;
	private final Logger LOG = Logger.getLogger(InformeOficioGestorController.class);
	
	/*BEANs*/
	@ManagedProperty(value = "#{bandejaTareasBean}")
    @Getter
    @Setter
    private BandejaTareasBean bandejaTareasBean;
	
	/*EJBs*/
	@EJB
	private DocumentosFacade documentosFacade;
	
	@EJB
	private GestorDesechosPeligrososFacade gestorDesechosPeligrososFacade;

	@EJB
	private InformeTecnicoRetceFacade informeTecnicoRetceFacade;
	
	@EJB
	private ObservacionesFacade observacionesFacade;
	
	@EJB
	private OficioRetceFacade oficioRetceFacade;
	
	@EJB
	private OrganizacionFacade organizacionFacade;
	
	@EJB
	private ProcesoFacade procesoFacade;
	
	@EJB
	private UsuarioFacade usuarioFacade;
	
	/*Object*/
	private Documento documentoInforme,documentoOficio;
	
	private GestorDesechosPeligrosos gestorDesechos;
	
	@Getter
	private InformeTecnicoRetce informe;
	
	private Map<String, Object> variables;
	
	@Getter
	private OficioPronunciamientoRetce oficio;
	
	private OficioPronunciamientoRetce oficioAnterior;
	
	private Usuario usuarioOperador,usuarioElabora,usuarioRevisa,usuarioFirma;
		
	/*Boolean*/
	@Getter
	private boolean habilitarElaborar=false;
	
	@Getter
	private boolean habilitarRevisar=false;
	
	@Getter
	private boolean habilitarFirmar=false;
	
	@Getter
	private boolean habilitarObservaciones=true;
	
	@Getter
	private boolean editarObservaciones=false;
	
	@Getter
	private boolean oficioGenerado=false;
		
	@Getter
	private boolean verOficio=false;
		
	private Boolean tieneObservacionesInformeOficio;
	
	private boolean uploadFirma=false;
	
	@Setter
	@Getter
	private boolean token;
	
	/*Integer*/
	@Setter
	@Getter
	private Integer numeroObservaciones,numeroRevision;
	
	/*String*/
    private String tramite,nombreOperador,nombreRepresentanteLegal;
    
    @Getter	
	private String urlPdf;
    
    private List<ObservacionesFormularios> observacionesTecnicoList;

    @PostConstruct
	public void init() {
		try {
			variables=procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
			tramite=(String)variables.get(Constantes.VARIABLE_PROCESO_TRAMITE);
			numeroObservaciones=variables.containsKey("numero_observaciones")?(Integer.valueOf((String)variables.get("numero_observaciones"))):0;
			tieneObservacionesInformeOficio=variables.containsKey("tiene_observaciones_informe_oficio")?Boolean.valueOf((String)variables.get("tiene_observaciones_informe_oficio")):null;
			
			String usuarioProponente=(String)variables.get("usuario_operador");
			String usuarioTecnico=(String)variables.get("usuario_tecnico");
			String usuarioCoordinador=(String)variables.get("usuario_coordinador");
			
			usuarioOperador=usuarioFacade.buscarUsuario(usuarioProponente);
			usuarioElabora=usuarioFacade.buscarUsuario(usuarioTecnico);
			usuarioRevisa=usuarioFacade.buscarUsuario(usuarioCoordinador);

			numeroRevision=1+numeroObservaciones;
						
			String nombreTarea=bandejaTareasBean.getTarea().getTaskName().toUpperCase();
	    	if(nombreTarea.contains("ELABORAR")){
	    		habilitarElaborar=true;
	    		if(tieneObservacionesInformeOficio==null){
		    		habilitarObservaciones=false;
		    	}
	    	}else if(nombreTarea.contains("FIRMAR")){
	    		habilitarRevisar=true;
	    		habilitarFirmar=true;
	    		habilitarObservaciones=true;
	    		editarObservaciones=true;
	    		oficioGenerado=true;
	    		token=JsfUtil.getLoggedUser().getToken()==null?false:JsfUtil.getLoggedUser().getToken();
	    	}else{//REVISAR
	    		habilitarRevisar=true;
	    		habilitarObservaciones=true;
	    		editarObservaciones=true;
	    		oficioGenerado=true;
	    	}
			
			Organizacion organizacion=organizacionFacade.buscarPorRuc(usuarioOperador.getNombre());
			if(organizacion!=null){
				nombreOperador=organizacion.getNombre();
				nombreRepresentanteLegal=organizacion.getPersona().getNombre();
			}else{
				nombreOperador=usuarioOperador.getPersona().getNombre();
				nombreRepresentanteLegal="";
			}
			
			gestorDesechos=gestorDesechosPeligrososFacade.findByCodigo(tramite);			
			informe = informeTecnicoRetceFacade.getInforme(gestorDesechos.getCodigo(),TipoDocumentoSistema.INFORME_TECNICO_GESTOR_DESECHOS,numeroRevision);
			oficio = oficioRetceFacade.getOficio(gestorDesechos.getCodigo(),TipoDocumentoSistema.OFICIO_PRONUNCIAMIENTO_GESTOR_DESECHOS,numeroRevision);
			
			if (informe == null) {
				informe = new InformeTecnicoRetce(gestorDesechos.getCodigo(),TipoDocumentoSistema.INFORME_TECNICO_GESTOR_DESECHOS,numeroRevision);
				documentoInforme=new Documento();
			}else{
				documentoInforme=documentosFacade.documentoXTablaIdXIdDocUnico(informe.getId(), InformeTecnicoRetce.class.getSimpleName(),TipoDocumentoSistema.INFORME_TECNICO_GESTOR_DESECHOS);
				if(documentoInforme==null)
					documentoInforme=new Documento();
			}
			if (oficio == null) {
				oficio = new OficioPronunciamientoRetce(gestorDesechos.getCodigo(),TipoDocumentoSistema.OFICIO_PRONUNCIAMIENTO_GESTOR_DESECHOS,numeroRevision);
				documentoOficio=new Documento();
			}else{
				documentoOficio=documentosFacade.documentoXTablaIdXIdDocUnico(oficio.getId(), OficioPronunciamientoRetce.class.getSimpleName(),TipoDocumentoSistema.OFICIO_PRONUNCIAMIENTO_GESTOR_DESECHOS);
				if(documentoOficio==null)
					documentoOficio=new Documento();
			}
			
			oficioAnterior=null;			
			if(numeroObservaciones>0){
				oficioAnterior=oficioRetceFacade.getOficio(gestorDesechos.getCodigo(),TipoDocumentoSistema.OFICIO_PRONUNCIAMIENTO_GESTOR_DESECHOS,numeroRevision-1);								
			}
			buscarObservacionesTecnico();
			generarDocumento(true,verOficio?TipoDocumentoSistema.OFICIO_PRONUNCIAMIENTO_GESTOR_DESECHOS:TipoDocumentoSistema.INFORME_TECNICO_GESTOR_DESECHOS);
			
		} catch (Exception e) {
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos InformeTecnicoOficioRetce.");
		}
	}  
        
    private void buscarUsuarioFirma(){    	
    	usuarioFirma=habilitarFirmar?JsfUtil.getLoggedUser():JsfUtil.getBean(ProcesoRetceController.class).buscarAutoridadFirma(gestorDesechos.getInformacionProyecto().getAreaSeguimiento(), informe.getEsReporteAprobacion());    	
    }
    
    private void generarDocumento(boolean marcaAgua,TipoDocumentoSistema tipoDocumentoSistema)
	{	
		try {			
			Object entityDocumento=null;
			String nombreReporte=null;
			String numeroDocumento=null;
			Integer idtable=null;
			
			if(tipoDocumentoSistema==TipoDocumentoSistema.INFORME_TECNICO_GESTOR_DESECHOS){
				nombreReporte= InformeTecnicoRetce.class.getSimpleName();
				entityDocumento=new InformeTecnicoGestorHTML(informe,gestorDesechos,nombreOperador,nombreRepresentanteLegal,usuarioElabora,usuarioRevisa,oficioAnterior,observacionesTecnicoList);
				numeroDocumento=informe.getNumeroInforme();
				idtable=informe.getId();
	        	
			}else if(tipoDocumentoSistema==TipoDocumentoSistema.OFICIO_PRONUNCIAMIENTO_GESTOR_DESECHOS){
				if(usuarioFirma==null){
					buscarUsuarioFirma();
				}					
				nombreReporte= OficioPronunciamientoRetce.class.getSimpleName();
				entityDocumento=new OficioPronunciamientoGestorHTML(oficio,informe,gestorDesechos,nombreOperador,nombreRepresentanteLegal,usuarioElabora,usuarioFirma,observacionesTecnicoList);
				numeroDocumento=oficio.getNumeroOficio();
				idtable=oficio.getId();	
			}else{
				return;
			}
			
			PlantillaReporte plantillaReporte = informeTecnicoRetceFacade.getPlantillaReporte(tipoDocumentoSistema);
			File file = JsfUtil.fileMarcaAgua(UtilGenerarInforme.generarFichero(plantillaReporte.getHtmlPlantilla(), nombreReporte, true,entityDocumento,null),marcaAgua?" - - BORRADOR - - ":" ",BaseColor.GRAY);
	        Path path = Paths.get(file.getAbsolutePath());
	        byte[] byteArchivo = Files.readAllBytes(path);
	        File fileArchivo = new File(JsfUtil.devolverPathReportesHtml(file.getName()));
	        FileOutputStream fileOutputStream = new FileOutputStream(fileArchivo);
	        fileOutputStream.write(byteArchivo);
	        fileOutputStream.close();
	        this.urlPdf=JsfUtil.devolverContexto("/reportesHtml/"+ file.getName());
	        
	        Documento documento = tipoDocumentoSistema==TipoDocumentoSistema.INFORME_TECNICO_GESTOR_DESECHOS?documentoInforme:documentoOficio;
        	JsfUtil.uploadApdoDocument(file, documento);        	
        	documento.setNombreTabla(nombreReporte);
        	documento.setIdTable(idtable);	
        	documento.setNombre(nombreReporte+" "+numeroDocumento+".pdf");
        	documento.setExtesion(".pdf");
        	documento.setMime("application/pdf");  
	        
	       } catch (Exception e) {
	    	   JsfUtil.addMessageError("Ocurrió un error al generar el documento. Comuníquese con mesa de ayuda.");
	    	   LOG.error("Error al generar Documento InformeTecnicoDescargasController. "+e.getMessage());
	       	   e.printStackTrace();
	       }
		
	}
    
    private Documento guardarDocumento(Documento documento)
    {	
    	DocumentosTareasProceso documentoTarea = new DocumentosTareasProceso();
    	documentoTarea.setIdTarea(bandejaTareasBean.getTarea().getTaskId());		    	
    	documentoTarea.setProcessInstanceId(bandejaTareasBean.getProcessId());
    	try {
			return documentosFacade.guardarDocumentoAlfrescoSinProyecto(gestorDesechos.getCodigo(),"INFORMES_OFICIOS",documentoTarea.getProcessInstanceId(),documento,documento.equals(documentoOficio)?TipoDocumentoSistema.OFICIO_PRONUNCIAMIENTO_GESTOR_DESECHOS:TipoDocumentoSistema.INFORME_TECNICO_GESTOR_DESECHOS,documentoTarea);			
		} catch (ServiceException e) {			
			e.printStackTrace();
		} catch (CmisAlfrescoException e) {			
			e.printStackTrace();
		}
    	return null;
    }
	
	public void verOficioListener(){
		verOficio=!verOficio;
		generarDocumento(true,verOficio?TipoDocumentoSistema.OFICIO_PRONUNCIAMIENTO_GESTOR_DESECHOS:TipoDocumentoSistema.INFORME_TECNICO_GESTOR_DESECHOS);		
	}
	
	public void guardar(){
		if(verOficio){
			oficio.setIdInformeTecnico(informe.getId());
			oficioRetceFacade.guardar(oficio,gestorDesechos.getInformacionProyecto().getAreaResponsable(),JsfUtil.getLoggedUser());
			generarDocumento(true,TipoDocumentoSistema.OFICIO_PRONUNCIAMIENTO_GESTOR_DESECHOS);
			oficioGenerado=true;
		}else{
			informeTecnicoRetceFacade.guardar(informe,gestorDesechos.getInformacionProyecto().getAreaResponsable(),JsfUtil.getLoggedUser());
			generarDocumento(true,TipoDocumentoSistema.INFORME_TECNICO_GESTOR_DESECHOS);			
		}
		
		if(verOficio)
			oficioGenerado=true;	
	}
	
	public void setInformeOficioCorrectos(Boolean temp){
	}
	public Boolean getInformeOficioCorrectos(){
		try {
			if(informe!=null && oficio!=null){
				List<ObservacionesFormularios> obsInforme=observacionesFacade.listarPorIdClaseNombreClaseNoCorregidas(informe.getId(),InformeTecnicoRetce.class.getSimpleName());
				List<ObservacionesFormularios> obsOficio=observacionesFacade.listarPorIdClaseNombreClaseNoCorregidas(oficio.getId(),OficioPronunciamientoRetce.class.getSimpleName());
				if(obsInforme.isEmpty() && obsOficio.isEmpty())
					return true;
			}
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	private void buscarObservacionesTecnico(){		
		try {
			observacionesTecnicoList=observacionesFacade.listarPorIdClaseNombreClaseNoCorregidas(gestorDesechos.getId(),GestorDesechosPeligrosos.class.getSimpleName());			
		} catch (ServiceException e) {
			e.printStackTrace();
		}		
	}
	
	private StreamedContent getStreamedContentDocumento(Documento documento) {
		try {
			if(documento.getContenidoDocumento()==null && documento.getId()!=null)
				documento.setContenidoDocumento(documentosFacade.descargar(documento.getIdAlfresco()));
			return UtilDocumento.getStreamedContent(documento);
		} catch (CmisAlfrescoException e) {
			LOG.error(e);
			e.printStackTrace();
			return null;
		}		
	}
	
	public StreamedContent descargarFirmar() {
		generarDocumento(false, TipoDocumentoSistema.OFICIO_PRONUNCIAMIENTO_GESTOR_DESECHOS);		
		return getStreamedContentDocumento(documentoOficio);
	}
	
	public void uploadFileFirmar(FileUploadEvent event) {		
		documentoOficio.setContenidoDocumento(event.getFile().getContents());
		uploadFirma=true;
		JsfUtil.addMessageInfo("Documento subido exitosamente");		
	}
	
	public String firmar() {
        try {
        	generarDocumento(false, TipoDocumentoSistema.OFICIO_PRONUNCIAMIENTO_GESTOR_DESECHOS);
        	guardarDocumento(documentoOficio);
        	String documentUrl = documentosFacade.direccionDescarga(OficioPronunciamientoRetce.class.getSimpleName(), oficio.getId(), TipoDocumentoSistema.OFICIO_PRONUNCIAMIENTO_GESTOR_DESECHOS);
        	return DigitalSign.sign(documentUrl, JsfUtil.getLoggedUser().getNombre());
        } catch (Throwable e) {
            LOG.error(e);
            JsfUtil.addMessageError("Error al realizar la operación.");
            return "";
        }
    }
	
	private boolean validar(){
		boolean validar=true;
    	if(usuarioRevisa==null){
    		validar=false;    		
    	}
    	if(usuarioFirma==null){
    		validar=false;    		
    	}  	
    	
    	if(!validar)
    		JsfUtil.addMessageError("Ha ocurrido un error. Por favor comuníquese con Mesa de Ayuda");
    	return validar;    
	}
	
	public boolean isOficioFirmado(){		
		if((token && documentoOficio.getIdAlfresco()!=null && documentosFacade.verificarFirmaVersion(documentoOficio.getIdAlfresco()))
		||(!token && uploadFirma)){
					return true;
				}
		return false;
	}
	
	public void enviar(){
		if(!validar())
			return;
		
		Map<String, Object> params=new HashMap<>();
		
		if(habilitarElaborar){			
			informe.setFinalizado(true);
			informeTecnicoRetceFacade.guardar(informe, gestorDesechos.getInformacionProyecto().getAreaResponsable(), JsfUtil.getLoggedUser());
			params.put("pronunciamiento_aprobado",informe.getEsReporteAprobacion());
			params.put("usuario_autoridad",usuarioFirma.getNombre());
		}
		
		if(habilitarRevisar){
			params.put("tiene_observaciones_informe_oficio",!getInformeOficioCorrectos());
		}
		
		if(habilitarFirmar && getInformeOficioCorrectos()){
			if(!isOficioFirmado()){
				JsfUtil.addMessageError("El documento no ha sido firmado.");
				return;
			}			
			generarDocumento(false,TipoDocumentoSistema.INFORME_TECNICO_GESTOR_DESECHOS);
			guardarDocumento(documentoInforme);
			if(!token)
				guardarDocumento(documentoOficio);
		}
		try {								
			procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), params);
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
	        JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
		} catch (JbpmException e) {
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
			e.printStackTrace();
		}
	}
}
