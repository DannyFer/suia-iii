package ec.gob.ambiente.rcoa.sustancias.quimicas.controllers;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.BaseColor;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.rcoa.enums.CatalogoTipoCoaEnum;
import ec.gob.ambiente.rcoa.enums.TipoInformeOficioEnum;
import ec.gob.ambiente.rcoa.facade.CatalogoCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.model.CatalogoGeneralCoa;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.ActividadSustanciaQuimicaFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.AnalisisTecnicoRSQFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.DocumentosRSQFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.InformesOficiosRSQFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.ObservacionesRSQFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.RegistroSustanciaQuimicaFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.UbicacionSustanciaQuimicaFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.ActividadSustancia;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.AnalisisTecnicoRSQ;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.DocumentosSustanciasQuimicasRcoa;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.InformeOficioRSQ;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.ObservacionesFormulariosRSQ;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.RegistroSustanciaQuimica;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.UbicacionSustancia;
import ec.gob.ambiente.rcoa.util.BuscarUsuarioBean;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.administracion.facade.ContactoFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.notificacionautoridades.controllers.NotificacionAutoridadesController;
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
public class RevisarOficioPronunciamientoRSQController implements Serializable {

	private static final long serialVersionUID = -1L;

	private static final Logger LOG = Logger.getLogger(RevisarOficioPronunciamientoRSQController.class);
	
	//EJBs
	@EJB
	private ActividadSustanciaQuimicaFacade actividadSustanciaQuimicaFacade;
	
	@EJB
	private AnalisisTecnicoRSQFacade analisisTecnicoRSQFacade;
	
	@EJB
	private AreaFacade areaFacade;
	
	@EJB
	private CatalogoCoaFacade catalogoCoaFacade;
	
	@EJB
	private DocumentosRSQFacade documentosRSQFacade;

	@EJB
	private InformesOficiosRSQFacade informesOficiosRSQFacade;
	    
    @EJB
    private ObservacionesRSQFacade observacionesRSQFacade;
    
    @EJB
    private OrganizacionFacade organizacionFacade;

	@EJB
	private UsuarioFacade usuarioFacade;
	
	@EJB
	private ProcesoFacade procesoFacade;
	
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	    
	@EJB
	private RegistroSustanciaQuimicaFacade registroSustanciaQuimicaFacade;  
	
	@EJB
    private SecuenciasFacade secuenciasFacade;
	
	@EJB
    private UbicacionSustanciaQuimicaFacade ubicacionSustanciaQuimicaFacade;
	
	@EJB
	private ContactoFacade contactoFacade;
	
	@EJB
	private MensajeNotificacionFacade mensajeNotificacionFacade;
	    
	//BEANs
    @ManagedProperty(value = "#{bandejaTareasBean}")
    @Getter
    @Setter
    private BandejaTareasBean bandejaTareasBean;
    
    @Getter
    @Setter
    @ManagedProperty(value = "#{buscarUsuarioBean}")
    private BuscarUsuarioBean buscarUsuarioBean;
     
	//LISTs
    @Getter
    private List<ActividadSustancia> actividadSustanciaLista;
    
    @Getter
    private List<AnalisisTecnicoRSQ> analisisTecnicoRSQLista;    
    
    @Getter
    private List<CatalogoGeneralCoa> catalogoHallazgosLista;
    
    @Getter
    private List<UbicacionSustancia> ubicacionSustanciaProyectoLista;
    
    @Getter
    private List<ActividadSustancia> actividadSustanciaTodoLista;
    
    //OBJs
    @Getter
    @Setter
    private DocumentosSustanciasQuimicasRcoa documentoInforme,documentoOficio,documentoRsq;
    
	@Getter
	@Setter
	private InformeOficioRSQ informe,oficio;
	
	private ProyectoLicenciaCoa proyectoLicenciaCoa;
	
	private RegistroSustanciaQuimica registroSustanciaQuimica;
	
	@Getter
	@Setter
	private Usuario usuarioAutoridad;
	
	//MAPs
    private Map<String, Object> variables;
	
	//STRINGs   		
    @Getter
	@Setter
	private String urlInforme,urlOficio,urlRsq;
    
    private String varTramite,varUsuarioRevisa;
    
    //INTEGERs
    private Integer numeroRevision;
    
    //BOOLEANs
    @Getter
	@Setter
	Boolean obsInforme,obsOficio,obsRsq;
	
	@Getter
	boolean pronunAprobado;
	
	@Getter
	@Setter
	boolean token,editarToken;
	
	@Setter
	@Getter
	private boolean verInforme,verOficio,verRsq;

	@PostConstruct
	public void init() {
		try {
			cargarDatosTarea();
			cargarDatosProyecto();
			
			verInforme=true;
			verOficio=false;
			verRsq=false;
			
			token= JsfUtil.getTokenUser();
			editarToken=!Constantes.getAmbienteProduccion();
		} catch (Exception e) {
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos OficioPronunciamientoRSQController.");
		}
	}
	
	private void cargarDatosTarea() throws ServiceException{

		try {
			variables=procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
			varTramite=(String)variables.get("tramite");
			numeroRevision=variables.containsKey("numero_revision")?(Integer.valueOf((String)variables.get("numero_revision"))):1;
			varUsuarioRevisa=variables.containsKey("revision_actual")?((String)variables.get("revision_actual")):"coordinador";
		} catch (JbpmException e) {
			LOG.error("Error al cargarDatosTarea "+e.getCause()+" "+e.getMessage());
		}
	}
	
	private void cargarDatosProyecto() throws ServiceException, CmisAlfrescoException{		
		proyectoLicenciaCoa=proyectoLicenciaCoaFacade.buscarProyecto(varTramite);
		registroSustanciaQuimica=registroSustanciaQuimicaFacade.obtenerRegistroPorProyecto(proyectoLicenciaCoa);
		analisisTecnicoRSQLista=new ArrayList<>();
		actividadSustanciaLista=new ArrayList<>();
		actividadSustanciaTodoLista = new ArrayList<>();
		
		if(registroSustanciaQuimica!=null) {
			informe = informesOficiosRSQFacade.obtenerPorRSQ(registroSustanciaQuimica,TipoInformeOficioEnum.INFORME_TECNICO,numeroRevision);
			oficio=informesOficiosRSQFacade.obtenerPorRSQ(registroSustanciaQuimica,TipoInformeOficioEnum.OFICIO_PRONUNCIAMIENTO,numeroRevision);
			ubicacionSustanciaProyectoLista=ubicacionSustanciaQuimicaFacade.obtenerUbicacionesPorRSQ(registroSustanciaQuimica);
			
			pronunAprobado=registroSustanciaQuimica.pronunciamientoAprobado();
			
			List<ActividadSustancia> actividadSustanciaListaTodo=actividadSustanciaQuimicaFacade.obtenerActividadesPorRSQ(registroSustanciaQuimica);
			for (ActividadSustancia actividadSustancia : actividadSustanciaListaTodo) {
				if(actividadSustancia.getCupo()!=null) {
					actividadSustanciaLista.add(actividadSustancia);
				}
				actividadSustanciaTodoLista.add(actividadSustancia);
			}
			
			analisisTecnicoRSQLista=analisisTecnicoRSQFacade.obtenerPorInformeOficio(informe,CatalogoTipoCoaEnum.RSQ_INFORME_REQUISITOS);
			documentoInforme=documentosRSQFacade.obtenerDocumentoPorTipo(pronunAprobado?TipoDocumentoSistema.RCOA_RSQ_INFORME_TECNICO_APROBADO:TipoDocumentoSistema.RCOA_RSQ_INFORME_TECNICO_NEGADO,"InformeOficioRSQ",informe.getId());
			urlInforme=documentosRSQFacade.direccionDescarga(documentoInforme.getIdAlfresco());
			documentoRsq=documentosRSQFacade.obtenerDocumentoPorTipo(TipoDocumentoSistema.RCOA_RSQ_REGISTRO_SUSANCIAS_QUIMICAS,RegistroSustanciaQuimica.class.getSimpleName(),registroSustanciaQuimica.getId());
						
			if (oficio == null) {
				oficio = new InformeOficioRSQ(registroSustanciaQuimica,TipoInformeOficioEnum.OFICIO_PRONUNCIAMIENTO,numeroRevision,registroSustanciaQuimica.getArea());
			}else{
//				documentoOficio=documentosRSQFacade.obtenerDocumentoPorTipo(pronunAprobado?TipoDocumentoSistema.RCOA_RSQ_OFICIO_APROBADO:TipoDocumentoSistema.RCOA_RSQ_OFICIO_NEGADO,"InformeOficioRSQ",oficio.getId());
				documentoOficio=documentosRSQFacade.obtenerDocumentoPorTipo(pronunAprobado?TipoDocumentoSistema.RCOA_RSQ_OFICIO_APROBADO:TipoDocumentoSistema.RCOA_RSQ_OFICIO_NEGADO,"InformeOficioRSQ",null);
			}
			
			if (documentoOficio == null) {
//				documentoOficio = new DocumentosSustanciasQuimicasRcoa(pronunAprobado?TipoDocumentoSistema.RCOA_RSQ_OFICIO_APROBADO:TipoDocumentoSistema.RCOA_RSQ_OFICIO_NEGADO,"InformeOficioRSQ",oficio.getId(),bandejaTareasBean.getTarea().getProcessInstanceId());
				documentoOficio = new DocumentosSustanciasQuimicasRcoa(pronunAprobado?TipoDocumentoSistema.RCOA_RSQ_OFICIO_APROBADO:TipoDocumentoSistema.RCOA_RSQ_OFICIO_NEGADO,"InformeOficioRSQ",oficio.getId(),null);
	        }
			
			if (documentoRsq == null) {
//				documentoRsq = new DocumentosSustanciasQuimicasRcoa(TipoDocumentoSistema.RCOA_RSQ_REGISTRO_SUSANCIAS_QUIMICAS,RegistroSustanciaQuimica.class.getSimpleName(),registroSustanciaQuimica.getId(),bandejaTareasBean.getTarea().getProcessInstanceId());
				documentoRsq = new DocumentosSustanciasQuimicasRcoa(TipoDocumentoSistema.RCOA_RSQ_REGISTRO_SUSANCIAS_QUIMICAS,RegistroSustanciaQuimica.class.getSimpleName(),registroSustanciaQuimica.getId(),null);
	        }
			
			if(analisisTecnicoRSQLista.isEmpty()) {
				catalogoHallazgosLista=catalogoCoaFacade.obtenerCatalogo(CatalogoTipoCoaEnum.RSQ_INFORME_REQUISITOS);
				for (CatalogoGeneralCoa hallazgo : catalogoHallazgosLista) {
					analisisTecnicoRSQLista.add(new AnalisisTecnicoRSQ(informe,CatalogoTipoCoaEnum.RSQ_INFORME_REQUISITOS,hallazgo));											
				}			
			}			
		}		
	}
	
	
	public String getTituloDocumento() {
		if(verInforme)
			return "Informe Técnico";
		if(verOficio)
			return "Oficio Pronunciamiento";
		if(verRsq)
			return "Registro Sustancias Quimicas";
		return "";
	}	
	
	public String getUrlDocumento() {
		if(verInforme)
			return urlInforme;
		if(verOficio)
			return urlOficio;
		if(verRsq)
			return urlRsq;
		return "";
	}
		
	public void verListener(boolean siguiente) {
		if(siguiente && verInforme) {			
			verInforme=false;
			verOficio=true;
			generarDocumentoOficio(true);
			return;
		}
		
		if(siguiente && verOficio) {
			//guardar(false);
			verOficio=false;
			verRsq=true;
			generarDocumentoRsq(true);
			return;
		}
		
		if(!siguiente && verOficio) {
			verOficio=false;
			verInforme=true;
			return;
		}
		
		if(!siguiente && verRsq) {
			verRsq=false;
			verOficio=true;	
			return;
		}
	}
	
	public boolean getVerFirmar() throws ServiceException{
		if(verInforme) {
			return false;
		}
				
		if(existeObsInfOfiRsq()) {
			return false;
		}
		
		if(varUsuarioRevisa.contains("director")) {			
			if(!esPlantaCentral() &&((!pronunAprobado && verOficio) || (pronunAprobado && verRsq))) {
				return true;
			}
		}
		
		if(varUsuarioRevisa.contains("subsecretario")) {
			if((!pronunAprobado && verOficio) || (pronunAprobado && verRsq)) {
				return true;
			}
		}
		
		return false;
	}
	
	public boolean getVerEnviar() throws ServiceException{
		if(verInforme) {
			return false;
		}		

		if(varUsuarioRevisa.contains("coordinador")) {
			if((!pronunAprobado && verOficio)||(pronunAprobado && verRsq)){
				return true;
			}
			return false;			
		}
		
		if(pronunAprobado && !verRsq) {
			return false;
		}
		
		return !getVerFirmar();
	}
	
	private boolean esPlantaCentral(){
		return registroSustanciaQuimica.getArea().getTipoArea().getId().intValue()==1;
	}
	
	private boolean existeObsInfOfiRsq(){
		try {
			return !observacionesRSQFacade.listarPorIdClaseNombreClaseNoCorregidas(informe.getId(),InformeOficioRSQ.class.getSimpleName()).isEmpty();
						
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	private Boolean buscarObservaciones(String seccion){
		try {
			List<ObservacionesFormulariosRSQ> obsLista=observacionesRSQFacade.listarPorIdClaseNombreClaseNoCorregidas(informe.getId(),InformeOficioRSQ.class.getSimpleName());
			for (ObservacionesFormulariosRSQ item : obsLista) {
				if(item.getSeccionFormulario().contains(seccion)) {
					return true;
				}
			}			
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	public void validarObservaciones(FacesContext context, UIComponent validate, Object value) {
		
		if(obsInforme!=null && (obsInforme != buscarObservaciones("RSQ Informe Tecnico"))) {
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_WARN,(obsInforme?"No Existen Observaciones":"Existen Observaciones"), null));
		}	
		
		if(obsOficio!=null &&(obsOficio != buscarObservaciones("RSQ Oficio Pronunciamiento"))) {
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_WARN,obsOficio?"No Existen Observaciones":"Existen Observaciones", null));
		}
		
		if(obsRsq!=null && (obsRsq != buscarObservaciones("RSQ Registro Sustancias Quimicas"))) {
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_WARN,obsRsq?"No Existen Observaciones":"Existen Observaciones", null));
		}		
	}
	
	
	private String getLabelProponente() {       
        try {
            Organizacion organizacion = organizacionFacade.buscarPorRuc(informe.getRegistroSustanciaQuimica().getProyectoLicenciaCoa().getUsuario().getNombre());
            if (organizacion != null) {
                return organizacion.getNombre();
            } 
            return informe.getRegistroSustanciaQuimica().getProyectoLicenciaCoa().getUsuario().getPersona().getNombre();
            
        } catch (ServiceException e) {
            e.printStackTrace();
            LOG.error(e.getMessage());
        }
        return null;
    }
	
	public Date getCurrentDate() {
		return new Date();
	}
	
	private Usuario buscarUsuario(Area area,String tipo){
		String roleKey="role.rsq."+(area.getTipoArea().getId().intValue()==1?"pc":"cz")+"."+tipo;
	
		if(area.getTipoArea().getId().intValue()==1 && tipo.contains("autoridad")) {
			area=areaFacade.getArea(253);//SCA
		}
		try {
			return buscarUsuarioBean.buscarUsuario(roleKey,area);
		} catch (Exception e) {
			LOG.error("No se ha encontrado usuario con en rol "+roleKey+" en el area "+area.getAreaName());			
		}
		
		return null;
	}
		
	public void generarDocumentoOficio(boolean marcaAgua)
	{		
		FileOutputStream file;
		try {	
			Area areaTramite = registroSustanciaQuimica.getArea();
			if(areaTramite.getTipoArea().getSiglas().equalsIgnoreCase(Constantes.SIGLAS_TIPO_AREA_OT))
				areaTramite = areaTramite.getArea();
			
			usuarioAutoridad=buscarUsuario(areaTramite,"autoridad");
			if(usuarioAutoridad==null) {
				JsfUtil.addMessageError("No se pudo generar el oficio, Comuníquese on mesa de ayuda");
				return;
			}
			
			if(!marcaAgua){
	        	informesOficiosRSQFacade.guardarInforme(oficio);
	        }
			
			String nombreReporte= "OficioPronunciamientoRSQ";
			
			PlantillaReporte plantillaReporte = this.informesOficiosRSQFacade.obtenerPlantillaReporte(pronunAprobado?TipoDocumentoSistema.RCOA_RSQ_OFICIO_APROBADO.getIdTipoDocumento():TipoDocumentoSistema.RCOA_RSQ_OFICIO_NEGADO.getIdTipoDocumento());
			File informePdf = JsfUtil.fileMarcaAgua(UtilGenerarInforme.generarFichero(plantillaReporte.getHtmlPlantilla(), nombreReporte, true,new OficioPronunciamientoRSQHtml(oficio,informe,actividadSustanciaLista,getLabelProponente(),usuarioAutoridad,JsfUtil.getLoggedUser()),null),marcaAgua?" - - BORRADOR - - ":" ",BaseColor.GRAY);
	        Path path = Paths.get(informePdf.getAbsolutePath());
	        byte[] byteArchivo = Files.readAllBytes(path);
	        File fileArchivo = new File(JsfUtil.devolverPathReportesHtml(informePdf.getName()));
	        file = new FileOutputStream(fileArchivo);
	        file.write(byteArchivo);
	        file.close();
	        urlOficio=JsfUtil.devolverContexto("/reportesHtml/"+ informePdf.getName()); 
	        
	        if(!marcaAgua){	        			    	
	        	JsfUtil.uploadDocumentoRSQ(informePdf, documentoOficio);	        	
	        	documentosRSQFacade.guardarDocumento(varTramite, documentoOficio,oficio.getId());
	        }
	        
	       } catch (Exception e) {
	       	e.printStackTrace();
	       }
	}
	
	public void generarDocumentoRsq(boolean marcaAgua)
	{			
		FileOutputStream file;
		try {
			if(!marcaAgua){ //&& oficio.getId()==null) {
				informesOficiosRSQFacade.guardarInforme(oficio);
			}
			
			registroSustanciaQuimica.setVigenciaDesde(new Date());
			
			Calendar fechaAprobacion = Calendar.getInstance();
			fechaAprobacion.set(Calendar.MONTH, 11);
			fechaAprobacion.set(Calendar.DATE, 31);
			Date lastDayOfMonth = fechaAprobacion.getTime();
			registroSustanciaQuimica.setVigenciaHasta(lastDayOfMonth);
			
			if(!marcaAgua){
//				Calendar calendar=Calendar.getInstance();
//				calendar.setTime(new Date());
//				calendar.add(Calendar.YEAR, 2);
//				registroSustanciaQuimica.setVigenciaHasta(calendar.getTime());								
				registroSustanciaQuimicaFacade.guardar(registroSustanciaQuimica, JsfUtil.getLoggedUser());				
	        }
			
			informe.setRegistroSustanciaQuimica(registroSustanciaQuimica);
			
			String nombreReporte= "DocumentoRSQ";
			
			PlantillaReporte plantillaReporte = this.informesOficiosRSQFacade.obtenerPlantillaReporte(TipoDocumentoSistema.RCOA_RSQ_REGISTRO_SUSANCIAS_QUIMICAS.getIdTipoDocumento());
			File filePdf = JsfUtil.fileMarcaAgua(UtilGenerarInforme.generarFichero(plantillaReporte.getHtmlPlantilla(), nombreReporte, true,new RegistroSustanciasQuimicasHtml(informe,oficio.getCodigo(),actividadSustanciaLista,ubicacionSustanciaProyectoLista,getLabelProponente(),usuarioAutoridad, actividadSustanciaTodoLista),null),marcaAgua?" - - BORRADOR - - ":" ",BaseColor.GRAY);
	        Path path = Paths.get(filePdf.getAbsolutePath());
	        byte[] byteArchivo = Files.readAllBytes(path);
	        File fileArchivo = new File(JsfUtil.devolverPathReportesHtml(filePdf.getName()));
	        file = new FileOutputStream(fileArchivo);
	        file.write(byteArchivo);
	        file.close();
	        urlRsq=JsfUtil.devolverContexto("/reportesHtml/"+ filePdf.getName());
	        
	        if(!marcaAgua){	        			    	
	        	JsfUtil.uploadDocumentoRSQ(filePdf, documentoRsq);
	        	documentosRSQFacade.guardarDocumento(varTramite, documentoRsq,registroSustanciaQuimica.getId());
	        }
	        
	       } catch (Exception e) {
	       	e.printStackTrace();
	       }
	}
	
	public StreamedContent descargarDocumento() {
		try {
			generarDocumentoOficio(false);
            byte[] byteFile = documentoOficio.getContenidoDocumento();
            if (byteFile != null) {
                InputStream is = new ByteArrayInputStream(byteFile);
                return new DefaultStreamedContent(is, "application/pdf", documentoOficio.getNombre());
            } else {
                JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
                return null;
            }
		} catch (Exception e) {
			LOG.error("error al descargar ", e);
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
            return null;
		}
	}
	
	public StreamedContent descargarDocumentoRsq() {
		try {
			generarDocumentoRsq(false);
            byte[] byteFile = documentoRsq.getContenidoDocumento();
            if (byteFile != null) {
                InputStream is = new ByteArrayInputStream(byteFile);
                return new DefaultStreamedContent(is, "application/pdf", documentoRsq.getNombre());
            } else {
                JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
                return null;
            }
		} catch (Exception e) {
			LOG.error("error al descargar ", e);
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
            return null;
		}
	}
	
	boolean firmadoFisico;
	public void uploadFileFirmado(FileUploadEvent event) {
		try{						
			documentoOficio.cargarArchivo(event.getFile().getContents(),event.getFile().getFileName());	
			informesOficiosRSQFacade.guardarInforme(oficio);
	    	documentosRSQFacade.guardarDocumento(varTramite, documentoOficio,oficio.getId());
			firmadoFisico=true;
			JsfUtil.addMessageInfo("El oficio de pronunciamiento fue adjuntado con éxito");
		}catch(Exception e)
		{
			JsfUtil.addMessageError("Error al guardar el documento del Informe.");
			e.printStackTrace();
		}		
	}	
	
	boolean firmadoFisicoRsq;
	public void uploadFileFirmadoRsq(FileUploadEvent event) {
		try{		
			documentoRsq.cargarArchivo(event.getFile().getContents(),event.getFile().getFileName());			
	    	documentosRSQFacade.guardarDocumento(varTramite, documentoRsq,registroSustanciaQuimica.getId());
	    	firmadoFisicoRsq=true;
			JsfUtil.addMessageInfo("El documento de Registro de Sustancias Qumicas fue adjuntado con éxito");
		}catch(Exception e)
		{
			JsfUtil.addMessageError("Error al guardar el documento del Informe.");
			e.printStackTrace();
		}		
	}
	
	public String firmaElectronica() {
        try {
        	generarDocumentoOficio(false);
        	documentosRSQFacade.guardarDocumento(varTramite, documentoOficio,oficio.getId());        	
        	return DigitalSign.sign(documentosRSQFacade.direccionDescarga(documentoOficio.getIdAlfresco()), JsfUtil.getLoggedUser().getNombre());
        } catch (Throwable e) {
            LOG.error(e);
            JsfUtil.addMessageError("Error al realizar la operación.");
            return "";
        }
    }
	
	public String firmaElectronicaRsq() {
        try {
        	generarDocumentoRsq(false);
        	documentosRSQFacade.guardarDocumento(varTramite, documentoRsq,registroSustanciaQuimica.getId());        	
        	return DigitalSign.sign(documentosRSQFacade.direccionDescarga(documentoRsq.getIdAlfresco()), JsfUtil.getLoggedUser().getNombre());
        } catch (Throwable e) {
            LOG.error(e);
            JsfUtil.addMessageError("Error al realizar la operación.");
            return "";
        }
    }
	
	public boolean validarFirma(){
		boolean oficioFirmado=false;
		boolean rsqFirmado=false;
		if((token && documentosRSQFacade.verificarFirmaVersion(documentoInforme.getIdAlfresco()))
		||(!token && firmadoFisico)){
			oficioFirmado =true;
		}
		if(pronunAprobado) {
			if((token && documentosRSQFacade.verificarFirmaVersion(documentoRsq.getIdAlfresco()))
			||(!token && firmadoFisicoRsq)){
				rsqFirmado =true;
			}
			return oficioFirmado && rsqFirmado;
		}else {
			return oficioFirmado;
		}
	}
	
	public void enviar()
	{
		try {	
			
			if(getVerFirmar() && !existeObsInfOfiRsq() && !validarFirma()) {
				JsfUtil.addMessageError("Debe firmar los documentos");
				return;
			}
			
			Map<String, Object> params=new HashMap<>();	
			if(varUsuarioRevisa.contains("coordinador")) {
				Area areaTramite = registroSustanciaQuimica.getArea();
				if(areaTramite.getTipoArea().getSiglas().equalsIgnoreCase(Constantes.SIGLAS_TIPO_AREA_OT))
					areaTramite = areaTramite.getArea();
				
				Usuario usuario=buscarUsuario(areaTramite,"autoridad");

				if(usuario==null) {
					JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
					return;
				}
				params.put("usuario_director",usuario.getNombre());
				params.put("revision_actual","director");				
			}
			
			if(varUsuarioRevisa.contains("director")) {				
				if(!existeObsInfOfiRsq()) {
					params.put("planta_central",esPlantaCentral());
					
					if(esPlantaCentral()) {
						Usuario usuario=buscarUsuario(registroSustanciaQuimica.getArea(),"autoridad");
						if(usuario==null) {
							JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
							return;
						}
						params.put("usuario_subsecretario",usuario.getNombre());
						params.put("revision_actual","subsecretario");
					}else if(!validarFirma()) {
						JsfUtil.addMessageError("Debe firmar los documentos");
						return;
					}
					
					if(pronunAprobado){
						if(!esPlantaCentral()) {
							enviarCorreoPermisos();
						}
					}
				}								
			}		
			
			if(varUsuarioRevisa.contains("subsecretario")) {
				if(pronunAprobado){
					enviarCorreoPermisos();
				}
			}
			
			params.put("observaciones_"+varUsuarioRevisa,existeObsInfOfiRsq());
			procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), params);
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
	        JsfUtil.redirectToBandeja();
		} catch (/*Jbpm*/Exception e) {
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
			e.printStackTrace();
		}
	}
	
	private void enviarCorreoPermisos(){
		try {
			
			List<Usuario> listaUsuarios = new ArrayList<>();
							
			String roleKeyDirector = Constantes.getRoleAreaName("role.rsq.pc.director");
			List<Usuario> listaUsuariosAux = usuarioFacade.buscarUsuarioPorRolActivo(roleKeyDirector);
			
			String roleKey=Constantes.getRoleAreaName("role.rsq.pc.tecnico.drsd");			
			List<Usuario> listaUsuariosTec = usuarioFacade.buscarUsuarioPorRolActivo(roleKey);
			
			listaUsuarios.addAll(listaUsuariosAux);
			listaUsuarios.addAll(listaUsuariosTec);
			
			for(Usuario usuario : listaUsuarios){
				String emailDestino = "";	
				
				List<Contacto> contacto = contactoFacade.buscarPorPersona(usuario.getPersona());
				for (Contacto con : contacto){
					if(con.getFormasContacto().getId() == 5	&& con.getEstado().equals(true)){
						emailDestino = con.getValor();
						break;
					}
				}
				
				Object[] parametrosCorreo = new Object[] {usuario.getPersona().getNombre(), getLabelProponente()};
				
				String notificacion = "";
					notificacion = mensajeNotificacionFacade.recuperarValorMensajeNotificacion(
							"bodyNotificacionIngresoRSQ",
							parametrosCorreo);	
				
				
				Usuario usuarioEnvio = new Usuario();
				usuarioEnvio.setNombrePersona(Constantes.SIGLAS_SITEAA);
				NotificacionAutoridadesController email = new NotificacionAutoridadesController();
				email.sendEmailInformacionProponente(emailDestino, "", notificacion, "Emisión de nuevo Registro de Sustancias Químicas en SUIA", varTramite, usuario, usuarioEnvio);		        	
		        
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}

}