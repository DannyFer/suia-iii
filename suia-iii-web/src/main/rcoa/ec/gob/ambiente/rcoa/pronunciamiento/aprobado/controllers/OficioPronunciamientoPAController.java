package ec.gob.ambiente.rcoa.pronunciamiento.aprobado.controllers;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.BaseColor;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.rcoa.certificado.interseccion.facade.CertificadoInterseccionCoaFacade;
import ec.gob.ambiente.rcoa.certificado.interseccion.model.CertificadoInterseccionOficioCoa;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.DocumentosImpactoEstudioFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.InformacionProyectoEIACoaFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.ObservacionesEsIAFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.OficioPronunciamientoEsIAFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.DocumentoEstudioImpacto;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.InformacionProyectoEia;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.InformeTecnicoEsIA;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.ObservacionesEsIA;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.OficioPronunciamientoEsIA;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaUbicacionFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCuaCiuuFacade;
import ec.gob.ambiente.rcoa.model.CatalogoCIUU;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCuaCiuu;
import ec.gob.ambiente.rcoa.util.BuscarUsuarioBean;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.PlantillaReporteFacade;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
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
public class OficioPronunciamientoPAController implements Serializable {

	private static final long serialVersionUID = -1L;

	private static final Logger LOG = Logger.getLogger(OficioPronunciamientoPAController.class);
	
	//EJBs	
	@EJB
	private AreaFacade areaFacade;
			
	@EJB
	private DocumentosImpactoEstudioFacade documentosImpactoEstudioFacade;
	
	@EJB
	private InformacionProyectoEIACoaFacade informacionProyectoEIACoaFacade; 
	
	@EJB
    private ObservacionesEsIAFacade observacionesEsIAFacade;
	
	@EJB
	private OficioPronunciamientoEsIAFacade oficioPronunciamientoEsIAFacade;
	   
    @EJB
    private OrganizacionFacade organizacionFacade;
    
    @EJB
    private PlantillaReporteFacade plantillaReporteFacade;
	
	@EJB
	private ProcesoFacade procesoFacade;
	
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	    
	@EJB
	private ProyectoLicenciaCoaUbicacionFacade proyectoLicenciaCoaUbicacionFacade; 
	
	@EJB
    private SecuenciasFacade secuenciasFacade;
	
	@EJB
	private UsuarioFacade usuarioFacade;
	
	@EJB
	private ProyectoLicenciaCuaCiuuFacade proyectoLicenciaCuaCiuuFacade;
	
	@EJB
	private CertificadoInterseccionCoaFacade certificadoInterseccionCoaFacade;
		    
	//BEANs
    @ManagedProperty(value = "#{bandejaTareasBean}")
    @Getter
    @Setter
    private BandejaTareasBean bandejaTareasBean;
    
    @Getter
    @Setter
    @ManagedProperty(value = "#{buscarUsuarioBean}")
    private BuscarUsuarioBean buscarUsuarioBean;
     
   	//OBJs
    @Getter
    @Setter
    private DocumentoEstudioImpacto documentoEstudioImpacto;
    
    private InformacionProyectoEia informacionProyectoEia;
      
	@Getter
	@Setter
	private OficioPronunciamientoEsIA oficio;
	
	private ProyectoLicenciaCoa proyectoLicenciaCoa;
	
	private UbicacionesGeografica ubicacionesGeografica;
	
	@Getter
	@Setter
	private Usuario usuarioAutoridad;
	
	//MAPs
	private Map<String, Object> variables;

    //STRINGs
	private String nombreTarea,revisionActual,tipoPronunciamiento;
	
	@Getter
	@Setter
	private String urlOficio;
	
	@Getter
	private String varTramite, nombreDocFirmado;
	
	//INTEGERs
	private Integer numeroRevision;
		
	//BOOLEANs
	private boolean firmadoFisico,observacionesViables,plantaCentral;
	
	@Getter
	boolean habilitarEditar,habilitarFirmar;
	
	@Getter
	@Setter
	boolean token, descargado;	
	
	@Getter
	@Setter
	private boolean requiereCorrecciones, documentosAceptados, editarObservaciones;
	
	@Getter
	@Setter
	private boolean showMsjOficioArchivacion;

	@PostConstruct
	public void init() {
		try {
			cargarDatosTarea();
			cargarDatosProyecto();		
			
			token= JsfUtil.getTokenUser();
			
			descargado = false;
			
			validarExisteObservacionesInformeOficio();
			
			if (habilitarEditar && oficio.getTipoOficio().equals(3)) {
		        RequestContext context = RequestContext.getCurrentInstance();
		        context.execute("PF('msjOficioArchivacion').show();");
		        
		        showMsjOficioArchivacion = true;
			}
	        
		} catch (Exception e) {
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos OficioArchivadoPAController.");
		}
	}
	
	private void cargarDatosTarea() throws ServiceException{

		try {
			variables=procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
			varTramite=(String)variables.get("tramite");
			observacionesViables=variables.containsKey("observaciones_viables")?(Boolean.valueOf((String)variables.get("observaciones_viables"))):false;
			plantaCentral=variables.containsKey("planta_central")?(Boolean.valueOf((String)variables.get("planta_central"))):false;
			numeroRevision=variables.containsKey("numero_revision")?(Integer.valueOf((String)variables.get("numero_revision"))):1;
			tipoPronunciamiento=variables.containsKey("tipo_pronunciamiento")?((String)variables.get("tipo_pronunciamiento")):"";
			revisionActual=variables.containsKey("revision_actual")?((String)variables.get("revision_actual")):"";
			
			nombreTarea=bandejaTareasBean.getTarea().getTaskName().toUpperCase();
			if(nombreTarea.contains("ELABORAR OFICIO PRONUNCIAMIENTO")) {
				habilitarEditar=true;
				editarObservaciones = true;
			}else if(nombreTarea.contains("REVISAR OFICIO")) {
				habilitarEditar = false;
				editarObservaciones = false;
				if(tipoPronunciamiento.equals("observado") && plantaCentral && !revisionActual.contains("coordinador"))
					habilitarFirmar=true;
			}else if(nombreTarea.contains("FIRMAR OFICIO")) {
				habilitarFirmar=true;
			}else if(nombreTarea.contains("REALIZAR CORRECCIONES")) {
				habilitarEditar=true;
				editarObservaciones = true;
			}
			
		} catch (JbpmException e) {
			LOG.error("Error al cargarDatosTarea "+e.getCause()+" "+e.getMessage());
		}
	}
	
	private void cargarDatosProyecto() throws ServiceException, CmisAlfrescoException{		
		proyectoLicenciaCoa=proyectoLicenciaCoaFacade.buscarProyecto(varTramite);
		informacionProyectoEia=informacionProyectoEIACoaFacade.obtenerInformacionProyectoEIAPorProyecto(proyectoLicenciaCoa);
		oficio=oficioPronunciamientoEsIAFacade.obtenerOficioPronunciamiento(informacionProyectoEia,2,numeroRevision);
		ubicacionesGeografica=proyectoLicenciaCoaUbicacionFacade.buscarPorProyecto(proyectoLicenciaCoa).get(0);
		
		if(oficio==null) {
			oficio=new OficioPronunciamientoEsIA(informacionProyectoEia,2,numeroRevision);			
		}else {
			documentoEstudioImpacto=documentosImpactoEstudioFacade.documentoXTablaIdXIdDoc(oficio.getId(), oficio.getClass().getSimpleName(), tipoDocumentoOficio());
		}
		
		if(documentoEstudioImpacto==null) {
			documentoEstudioImpacto=new DocumentoEstudioImpacto();
		}		
		
		if(habilitarEditar) {
			if(!observacionesViables) {
				oficio.setTipoOficio(OficioPronunciamientoEsIA.oficioAprobado);
			}else {
				if(tipoPronunciamiento.contains("archivado")) {
					oficio.setTipoOficio(OficioPronunciamientoEsIA.oficioArchivoAutomatico);
				}else {
					List<ObservacionesEsIA> observaciones = observacionesEsIAFacade.listarPorIdClaseNombreClaseNoCorregidas(informacionProyectoEia.getId(),"PronunciamientoAprobacionFinal");
					if (observaciones == null || observaciones.isEmpty()) {
						oficio.setTipoOficio(OficioPronunciamientoEsIA.oficioAprobado);
					}else {
						oficio.setTipoOficio(numeroRevision>1?OficioPronunciamientoEsIA.oficioArchivoAutomatico:OficioPronunciamientoEsIA.oficioObservado);
					}
				}			
			}
		}else if(habilitarFirmar && tipoPronunciamiento.contains("archivado")) {
			oficio.setTipoOficio(OficioPronunciamientoEsIA.oficioArchivoAutomatico);
		}		
		
		usuarioAutoridad=buscarUsuario("autoridad");
		
		generarDocumentoOficio(true);		
	}	
	
	public String getTipoOficio() {
		if(oficio.getTipoOficio()==null)
			return null;
		
		switch (oficio.getTipoOficio()) {
		case 4:
			return "Aprobado";
		case 5:
			return "Observado";
		case 3:
			return "Archivado";

		default:
			return "";
		}	
	}
	
	public String getUrlDocumento() {
		return urlOficio;		
	}	
	
	private TipoDocumentoSistema tipoDocumentoOficio() {
		
		switch (oficio.getTipoOficio().intValue()) {
		case 3:
			return TipoDocumentoSistema.RCOA_PA_OFICIO_ARCHIVADO;
		case 4:
			return TipoDocumentoSistema.RCOA_PA_OFICIO_APROBADO;
		case 5:
			return TipoDocumentoSistema.RCOA_PA_OFICIO_OBSERVADO;
		default:
			return null;
		}
		
	}
	
	private String getLabelProponente() {       
        try {
            Organizacion organizacion = organizacionFacade.buscarPorRuc(proyectoLicenciaCoa.getUsuario().getNombre());
            if (organizacion != null) {
                return organizacion.getNombre();
            } 
            return proyectoLicenciaCoa.getUsuario().getPersona().getNombre();
            
        } catch (ServiceException e) {
            e.printStackTrace();
            LOG.error(e.getMessage());
        }
        return null;
    }
	
	public void tipoPronunciamientoListener() {
		oficio.setAsunto(null);
		oficio.setAntecedentes(null);
		oficio.setPronunciamiento(null);
		oficio.setConclusiones(null);
		generarDocumentoOficio(false);
	}
	
	public void generarDocumentoOficio(boolean marcaAgua)
	{	
		if(oficio.getTipoOficio()==null)
			return;
		
		oficio.setFechaModificacion(new Date());
		oficioPronunciamientoEsIAFacade.guardar(oficio, proyectoLicenciaCoa.getAreaResponsable());
		
		FileOutputStream file;
		try {
			
			String nombreReporte= "OficioPronunciamientoFinal";
			
			Object plantillaHtml;
			if(tipoDocumentoOficio().equals(TipoDocumentoSistema.RCOA_PA_OFICIO_ARCHIVADO)) {
				CertificadoInterseccionOficioCoa oficioCI = certificadoInterseccionCoaFacade.obtenerPorCodigoProyecto(proyectoLicenciaCoa.getCodigoUnicoAmbiental());
				Boolean interseca = (oficioCI.getInterseccionViabilidad() == null || oficioCI.getInterseccionViabilidad().isEmpty()) ? false : true;
				plantillaHtml=new OficioArchivadoPAHtml(oficio, getLabelProponente(), ubicacionesGeografica, interseca, usuarioAutoridad, oficioCI);
			}else if(tipoDocumentoOficio().equals(TipoDocumentoSistema.RCOA_PA_OFICIO_APROBADO)) {
				plantillaHtml=new OficioAprobadoPAHtml(oficio,getLabelProponente(),usuarioAutoridad);
			}else if(tipoDocumentoOficio().equals(TipoDocumentoSistema.RCOA_PA_OFICIO_OBSERVADO)){
				plantillaHtml=new OficioObservadoPAHtml(oficio,getLabelProponente(),usuarioAutoridad);
			}else {
				return;
			}		
			
			PlantillaReporte plantillaReporte = this.plantillaReporteFacade.getPlantillaReporte(tipoDocumentoOficio());
			File informePdf = JsfUtil.fileMarcaAgua(UtilGenerarInforme.generarFichero(plantillaReporte.getHtmlPlantilla(), nombreReporte, true,plantillaHtml,null),marcaAgua?" - - BORRADOR - - ":" ",BaseColor.GRAY);
	        Path path = Paths.get(informePdf.getAbsolutePath());
	        byte[] byteArchivo = Files.readAllBytes(path);
	        File fileArchivo = new File(JsfUtil.devolverPathReportesHtml(informePdf.getName()));
	        file = new FileOutputStream(fileArchivo);
	        file.write(byteArchivo);
	        file.close();
	        urlOficio=JsfUtil.devolverContexto("/reportesHtml/"+ informePdf.getName());   
	        
	        documentoEstudioImpacto.cargarArchivo(byteArchivo, nombreReporte+".pdf");	       
	        
	       } catch (Exception e) {
	       	e.printStackTrace();
	       	JsfUtil.addMessageError("No se pudo generar el documento, Comuníquese con Mesa de Ayuda");
	       }
	}	
	
	public StreamedContent descargarDocumento() {
		try {
			descargado = false;
			generarDocumentoOficio(false);
            byte[] byteFile = documentoEstudioImpacto.getContenidoDocumento();
            if (byteFile != null) {
            	descargado = true;
                InputStream is = new ByteArrayInputStream(byteFile);
                return new DefaultStreamedContent(is, "application/pdf", documentoEstudioImpacto.getNombre());
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
	
	public void uploadFileFirmado(FileUploadEvent event) {
		try{		
			if(descargado) {
				nombreDocFirmado = event.getFile().getFileName();
				documentoEstudioImpacto.cargarArchivo(event.getFile().getContents(),event.getFile().getFileName());
		    	documentosImpactoEstudioFacade.ingresarDocumento(documentoEstudioImpacto, oficio.getId(), varTramite, tipoDocumentoOficio(), documentoEstudioImpacto.getNombre(), oficio.getClass().getSimpleName(), bandejaTareasBean.getTarea().getProcessInstanceId());
				firmadoFisico=true;
				JsfUtil.addMessageInfo("El documento fue adjuntado con éxito");
			} else{
				JsfUtil.addMessageError("No ha descargado el documento para la firma");
			}
		}catch(Exception e)
		{
			nombreDocFirmado = null;
			JsfUtil.addMessageError("Error al guardar el documento del Oficio.");
			e.printStackTrace();
		}		
	}	
	
	public String firmaElectronica() {
        try {
        	generarDocumentoOficio(false);
        	documentosImpactoEstudioFacade.ingresarDocumento(documentoEstudioImpacto, oficio.getId(), varTramite, tipoDocumentoOficio(), documentoEstudioImpacto.getNombre(), oficio.getClass().getSimpleName(), bandejaTareasBean.getTarea().getProcessInstanceId());
        	return DigitalSign.sign(documentosImpactoEstudioFacade.direccionDescarga(documentoEstudioImpacto), JsfUtil.getLoggedUser().getNombre());
        } catch (Throwable e) {
            LOG.error(e);
            JsfUtil.addMessageError("Error al realizar la operación.");
            return "";
        }
    }
	
	public boolean validarTipoOficio(){		
		if(getTipoOficio()!=null) {
			try {
				List<ObservacionesEsIA> observaciones = observacionesEsIAFacade.listarPorIdClaseNombreClaseNoCorregidas(informacionProyectoEia.getId(),"PronunciamientoAprobacionFinal");
				if (getTipoOficio().contains("Aprobado") && observaciones != null && !observaciones.isEmpty()) {
					JsfUtil.addMessageError("Existen Observaciones Sin Corregir");
					return false;
				}
				if (getTipoOficio().contains("Observado") && (observaciones == null || observaciones.isEmpty())) {
					JsfUtil.addMessageError("No Existen Observaciones Sin Corregir");
					return false;
				}
				return true;
					
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	public boolean validarFirma(){		
		if((token && documentosImpactoEstudioFacade.verificarFirmaVersion(documentoEstudioImpacto.getAlfrescoId()))
		||(!token && firmadoFisico)){
			return true;
		}
		return false;
	}
	
	public void guardar(boolean generarDocumento) {
		try {
			oficioPronunciamientoEsIAFacade.guardar(oficio, proyectoLicenciaCoa.getAreaResponsable());
			
			if(generarDocumento) {
				generarDocumentoOficio(true);
				JsfUtil.addMessageInfo("Verifique la información antes de enviar."); 
			}
		} catch (Exception e) {
			LOG.error("error al guardar ", e);
			JsfUtil.addMessageError("Ocurrió un error al guardar. Comuníquese con mesa de ayuda.");
		}		
	}
	
	private Usuario buscarUsuario(String tipoUsuario){
		if(habilitarFirmar && tipoUsuario.contains("autoridad")) {
			return JsfUtil.getLoggedUser();
		}
		
		Area areaTramite = proyectoLicenciaCoa.getAreaResponsable();
		
		String tipoArea=proyectoLicenciaCoa.getAreaResponsable().getTipoArea().getSiglas().toLowerCase();
		
		if(tipoArea.toUpperCase().equals(Constantes.SIGLAS_TIPO_AREA_OT))
			tipoArea = "cz";
		
		String roleKey="role.pa."+tipoArea+"."+tipoUsuario;
		
		if(tipoUsuario.contains("autoridad")) {
			if(tipoArea.contains("pc"))
				roleKey = roleKey +".archivado";
			
			if(tipoArea.toUpperCase().equals("CZ"))
				areaTramite = areaTramite.getArea();
		}
		
		if(tipoArea.contains("pc") && tipoUsuario.contains("coordinador")) {
			ProyectoLicenciaCuaCiuu proyectoCiuuPrincipal = proyectoLicenciaCuaCiuuFacade.actividadPrincipal(proyectoLicenciaCoa);
			CatalogoCIUU actividadPrincipal = proyectoCiuuPrincipal.getCatalogoCIUU();
			Integer idSector = actividadPrincipal.getTipoSector().getId();
			roleKey = "role.esia.pc.coordinador.tipoSector." + idSector;
		}
				
		try {			
			if(tipoUsuario.contains("autoridad")) {
				if(tipoArea.contains("pc") && oficio.getTipoOficio().intValue() == 4 || tipoArea.contains("pc") && oficio.getTipoOficio().intValue() == 3){	//aprobado o archivado
					String rolDirector = Constantes.getRoleAreaName("role.ppc.pc.subsecretario");
					List<Usuario> listaUsuarios = usuarioFacade.buscarUsuarioPorRolActivo(rolDirector);
					
					if(listaUsuarios != null && !listaUsuarios.isEmpty()){
						return listaUsuarios.get(0);					
					}
				}
				if(tipoArea.contains("pc") && oficio.getTipoOficio().intValue() == 5){ //observado
					roleKey="role.esia.pc.autoridad";
					Usuario usuarioAutoridad = new Usuario();
					usuarioAutoridad = buscarUsuarioBean.buscarUsuario(roleKey, areaTramite);
					return usuarioAutoridad;
				}
			}
			
			return buscarUsuarioBean.buscarUsuario(roleKey,areaTramite);
			
		} catch (Exception e) {
			LOG.error("No se ha encontrado usuario con en rol "+Constantes.getRoleAreaName(roleKey)+"("+roleKey+") en el area "+proyectoLicenciaCoa.getAreaResponsable().getAreaName());			
		}
		
		return null;
	}
	
	public void enviar()
	{
		try {
			if(usuarioAutoridad==null) {
				JsfUtil.addMessageError();
				return;
			}
			
			
			Map<String, Object> parametros = new ConcurrentHashMap<String, Object>();
			
			if(nombreTarea.contains("ELABORAR OFICIO") || nombreTarea.contains("REALIZAR CORRECCIONES")) {
				if(!validarTipoOficio()) {					
					return;
				}
				Usuario usuario=buscarUsuario("coordinador");
				if(usuario!=null) {
					parametros.put("usuario_coordinador", usuario.getNombre());
					parametros.put("revision_actual", "coordinador");
				}else {
					JsfUtil.addMessageError();
					return;
				}
				parametros.put("tipo_pronunciamiento", getTipoOficio().toLowerCase());
			}
			
			if(nombreTarea.contains("REVISAR OFICIO")) {
				// si es aprobado y planta central, se envia a otra revision
				if(revisionActual.contains("coordinador")) {
					if(plantaCentral) {
						Usuario usuario=buscarUsuario("director");
						if(usuario!=null) {
							parametros.put("usuario_director", usuario.getNombre());
							parametros.put("revision_actual", "director");							
						}else {
							JsfUtil.addMessageError();
							return;
						}
					}					
				}
				parametros.put("usuario_autoridad", usuarioAutoridad.getNombre());
								
			}
			
			if(habilitarFirmar && !validarFirma()){
				JsfUtil.addMessageError("Debe firmar el Oficio");
				return;
			}else {
				guardar(false);
			}
			
			parametros.put("existenObservaciones", requiereCorrecciones);
			
			procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getProcessInstanceId(), parametros);
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
	        JsfUtil.redirectToBandeja();
		} catch (/*Jbpm*/Exception e) {
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
			e.printStackTrace();
		}
	}

	public String abrirEstudio(){
		JsfUtil.cargarObjetoSession(InformeTecnicoEsIA.class.getSimpleName(), informacionProyectoEia.getId());
		return JsfUtil.actionNavigateTo("/pages/rcoa/estudioImpactoAmbiental/default.jsf");		
	}
	
	public StreamedContent getStream(String name, byte[] fileContent) throws Exception {
		DefaultStreamedContent content = new DefaultStreamedContent();
		if (fileContent != null) {
			content = new DefaultStreamedContent(new ByteArrayInputStream(fileContent), "application/octet-stream");
			content.setName(name);
		}
		return content;
	}	

	public void validarExisteObservacionesInformeOficio() {
		try {
			requiereCorrecciones = false;
			List<ObservacionesEsIA> observaciones= observacionesEsIAFacade.listarPorIdClaseNombreClaseNoCorregidas(
					oficio.getId(), "OficioPronunciamientoPA");
			
			if(observaciones.size() > 0) {
				requiereCorrecciones = true;
			}	
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al recuperar las observaciones.");
		}
	}
	
	public void guardarOficio() {
		try {
			documentosAceptados = true;
			JsfUtil.addMessageInfo(JsfUtil.REGISTRO_GUARDADO);
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
		}
	}
	
	public void enviarCoordinador(){		
		try {
			
			if(habilitarFirmar && !validarFirma()){
				JsfUtil.addMessageError("Debe firmar el Oficio");
				return;
			}
			
			Map<String, Object> parametros = new ConcurrentHashMap<String, Object>();
			
			Area area = proyectoLicenciaCoa.getAreaResponsable();
			
			if(getTipoOficio().toLowerCase().contains("aprobado")) {
				if(proyectoLicenciaCoa.getAreaResponsable().getTipoArea().getSiglas().equalsIgnoreCase("PC")){						
					
					parametros.put("requiereRevisionDirector", true);
					String roleKey="role.esia.pc.autoridad";
					Usuario usuarioAutoridad = new Usuario();
					usuarioAutoridad = buscarUsuarioBean.buscarUsuario(roleKey, area);
					if(usuarioAutoridad==null )			
					{
						JsfUtil.addMessageError("Ha ocurrido un error. Por favor comuníquese con Mesa de Ayuda");
						return;
					}
					parametros.put("usuario_director", usuarioAutoridad.getNombre());
					parametros.put("planta_central", true);
					
				}else if(proyectoLicenciaCoa.getAreaResponsable().getTipoArea().getSiglas().equalsIgnoreCase("DP")){
					//galápagos
					String roleKey="role.esia.ga.autoridad";
					Usuario usuarioAutoridad = new Usuario();
					List<Usuario> usuarios = usuarioFacade.buscarUsuariosPorRolYArea(Constantes.getRoleAreaName(roleKey), area);
					if(usuarios != null && !usuarios.isEmpty()){
						usuarioAutoridad = usuarios.get(0);	
					}else{
						JsfUtil.addMessageError("Ha ocurrido un error. Por favor comuníquese con Mesa de Ayuda");
						return;
					}					
					
					parametros.put("usuario_autoridad", usuarioAutoridad.getNombre());
					parametros.put("planta_central", false);
				}else{
					String roleKey="role.esia.cz.autoridad";
					Usuario usuarioAutoridad = new Usuario();
					
					List<Usuario> usuarios = new ArrayList<Usuario>();					
					if(proyectoLicenciaCoa.getAreaResponsable().getTipoArea().getSiglas().equals("OT")){
						usuarios = usuarioFacade.buscarUsuariosPorRolYArea(Constantes.getRoleAreaName(roleKey), area.getArea());				
					}else{
						usuarios = usuarioFacade.buscarUsuariosPorRolYArea(Constantes.getRoleAreaName(roleKey), area);			
					}	
					
					if(usuarios != null && !usuarios.isEmpty()){
						usuarioAutoridad = usuarios.get(0);
					}else{
						JsfUtil.addMessageError("Ha ocurrido un error. Por favor comuníquese con Mesa de Ayuda");
						return;
					}
					
					parametros.put("usuario_autoridad", usuarioAutoridad.getNombre());
					parametros.put("planta_central", false);
				}
			}else{
				//observacion
				if(proyectoLicenciaCoa.getAreaResponsable().getTipoArea().getSiglas().equalsIgnoreCase("PC")){						
					
					parametros.put("requiereRevisionDirector", true);
					String roleKey="role.esia.pc.autoridad";
					Usuario usuarioAutoridad = new Usuario();
					usuarioAutoridad = buscarUsuarioBean.buscarUsuario(roleKey, area);
					if(usuarioAutoridad==null )			
					{
						JsfUtil.addMessageError("Ha ocurrido un error. Por favor comuníquese con Mesa de Ayuda");
						return;
					}
					parametros.put("usuario_director", usuarioAutoridad.getNombre());
					parametros.put("planta_central", true);
					
				}else if(proyectoLicenciaCoa.getAreaResponsable().getTipoArea().getSiglas().equalsIgnoreCase("DP")){
					//galápagos
					String roleKey="role.esia.cz.autoridad";
					Usuario usuarioAutoridad = new Usuario();
					List<Usuario> usuarios = usuarioFacade.buscarUsuariosPorRolYArea(Constantes.getRoleAreaName(roleKey), area);
					if(usuarios != null && !usuarios.isEmpty()){
						usuarioAutoridad = usuarios.get(0);	
					}else{
						JsfUtil.addMessageError("Ha ocurrido un error. Por favor comuníquese con Mesa de Ayuda");
						return;
					}					
					
					parametros.put("usuario_autoridad", usuarioAutoridad.getNombre());
					parametros.put("planta_central", false);
				}else{
					String roleKey="role.esia.cz.autoridad";
					Usuario usuarioAutoridad = new Usuario();
					
					List<Usuario> usuarios = new ArrayList<Usuario>();					
					if(proyectoLicenciaCoa.getAreaResponsable().getTipoArea().getSiglas().equals("OT")){
						usuarios = usuarioFacade.buscarUsuariosPorRolYArea(Constantes.getRoleAreaName(roleKey), area.getArea());				
					}else{
						usuarios = usuarioFacade.buscarUsuariosPorRolYArea(Constantes.getRoleAreaName(roleKey), area);			
					}	
					
					if(usuarios != null && !usuarios.isEmpty()){
						usuarioAutoridad = usuarios.get(0);
					}else{
						JsfUtil.addMessageError("Ha ocurrido un error. Por favor comuníquese con Mesa de Ayuda");
						return;
					}
					
					parametros.put("usuario_autoridad", usuarioAutoridad.getNombre());
					parametros.put("planta_central", false);
				}		
				
			}
			
			parametros.put("existenObservaciones", requiereCorrecciones);
			
			procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);
			
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);	
			
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
	        JsfUtil.redirectToBandeja();
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public void enviarObservaciones(){
		try {
			Map<String, Object> parametros = new ConcurrentHashMap<String, Object>();
			
			parametros.put("existenObservaciones", requiereCorrecciones);
			procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);
			
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);	
			
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
	        JsfUtil.redirectToBandeja();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}