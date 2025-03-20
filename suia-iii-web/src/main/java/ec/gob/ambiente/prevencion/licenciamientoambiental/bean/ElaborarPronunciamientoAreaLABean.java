package ec.gob.ambiente.prevencion.licenciamientoambiental.bean;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
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
import javax.faces.context.FacesContext;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.BaseColor;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.InformeProvincialGADFacade;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.InformeTecnicoGeneralLA;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.Pronunciamiento;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.RolUsuario;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.pronunciamiento.facade.PronunciamientoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilFichaMineria;
import ec.gob.ambiente.suia.utils.UtilGenerarInforme;
import ec.gob.ambiente.suia.webservice.facade.AsignarTareaFacade;

@ManagedBean
@ViewScoped
public class ElaborarPronunciamientoAreaLABean implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 517362920397883926L;

    private static final Logger log = Logger.getLogger(ElaborarPronunciamientoAreaLABean.class);
    private final String clasePronunciamiento = "Pronunciamiento";
    @Setter
    @Getter
    private String area;
    @Setter
    @Getter
    private String tipo;
    @Setter
    @Getter
    private String registro;
    @Setter
    @Getter
    private String numeroResolucion;
    @Setter
    @Getter
    private String urlPage;
    @Setter
    @Getter
    private String fileNameImageLocation = "Escoger un archivo";
    @Setter
    @Getter
    private File imageLocationFile;
    @Setter
    @Getter
    private StreamedContent image = null;
    @Getter
    @Setter
    private Documento documento;
    
    @Getter
    @Setter
    private Documento documentoMemorando;
    
    @Setter
    @Getter
    private ProyectoLicenciamientoAmbiental proyectoActivo;
    @EJB
    private ProyectoLicenciamientoAmbientalFacade proyectoFacade;
    @EJB
    private ProcesoFacade procesoFacade;
    @EJB
    private PronunciamientoFacade pronunciamientoFacade;
    @EJB
    private DocumentosFacade documentosFacade;
    @EJB
	private AreaFacade areaFacade;
    @Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;
    @Getter
    @Setter
    @ManagedProperty(value = "#{proyectosBean}")
    private ProyectosBean proyectosBean;
    @Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;
    @Getter
    @Setter
    private boolean corregir;
    @Getter
    @Setter
    private Pronunciamiento pronunciamiento;
    
    @Getter
	@Setter
	private String urlPronunciamientoForestal;
    
    @EJB
	private InformeProvincialGADFacade informeProvincialGADFacade;
    
    public String plantillaHtml = null;
	
	@Getter
	@Setter
	byte[] byteArchivo; 
	
	@Getter
	@Setter
	private File fileArchivo;
	
	@Getter
    @Setter
    private Map<String, Object> variables;
	
	@EJB
	private UsuarioFacade usuarioFacade;

	@EJB
    private SecuenciasFacade secuenciasFacade;
	
	@Getter
	@Setter 
	private boolean visibleMemorando;
	
	@EJB
    private AsignarTareaFacade asignarTareaFacade;
	
	private String siglasForestal = "DB";
	private String siglasBiodiversidad = "DAPOFC";
	
    @PostConstruct
    public void init() {
        inicializar();
        asignarProyectoActivo();
        String areaEsp = area;
        if (tipo != null) {
            areaEsp += tipo;
        } 
        
        pronunciamiento = pronunciamientoFacade.getPronunciamientosPorClaseTipo(clasePronunciamiento, Long.parseLong(proyectoActivo.getId().toString()), areaEsp);
      
        if (pronunciamiento == null) {
            pronunciamiento = new Pronunciamiento();
            pronunciamiento.setNombreClase(clasePronunciamiento);
            pronunciamiento.setIdClase(proyectoActivo.getId());
            pronunciamiento.setTipo(areaEsp);
            pronunciamiento.setUsuario(loginBean.getUsuario());
            if(areaEsp.equals("Forestal") || areaEsp.equals("Biodiversidad")){
            pronunciamiento.setNumeroTramite(generarCodigoMemorando(proyectoActivo.getAreaResponsable()));
            visibleMemorando=true;
            }
        }
        
        if(pronunciamiento.getNumeroTramite()==null && (areaEsp.equals("Forestal") || areaEsp.equals("Biodiversidad")) && !bandejaTareasBean.getTarea().getTaskName().equals("Revisar pronunciamiento")){
            pronunciamiento.setNumeroTramite(generarCodigoMemorando(proyectoActivo.getAreaResponsable()));
        }
        
		if (pronunciamiento.getNumeroTramite() != null 
				&& proyectoActivo.getAreaResponsable().getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_PC)
				&& !bandejaTareasBean.getTarea().getTaskName().equals("Firmar Pronunciamiento ")) {
			if (areaEsp.equals("Forestal") && !pronunciamiento.getNumeroTramite().contains(siglasForestal)) {
				pronunciamiento.setNumeroTramite(generarCodigoMemorando(proyectoActivo.getAreaResponsable()));
				pronunciamientoFacade.modificarRegistro(pronunciamiento);
			} else if (areaEsp.equals("Biodiversidad")
					&& !pronunciamiento.getNumeroTramite().contains(siglasBiodiversidad)) {
				pronunciamiento.setNumeroTramite(generarCodigoMemorando(proyectoActivo.getAreaResponsable()));
				pronunciamientoFacade.modificarRegistro(pronunciamiento);
			}
		}
        
        List<Documento> documentos = documentosFacade.documentoXTablaIdXIdDoc(proyectosBean.getProyecto().getId(),
                InformeTecnicoGeneralLA.class.getSimpleName(), TipoDocumentoSistema.ADJUNTO_TDR);
        if (documentos.size() > 0) {
            documento = documentos.get(0);
        } else {
            if (tipo != null && !tipo.isEmpty()) {
                JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
            }

        }
        try {
			variables = procesoFacade.recuperarVariablesProceso(loginBean.getUsuario(),
			        bandejaTareasBean.getTarea().getProcessInstanceId());
		} catch (JbpmException e) {
			e.printStackTrace();
		}
        if(pronunciamiento.getNumeroTramite() != null && (areaEsp.equals("Forestal") || areaEsp.equals("Biodiversidad"))){
        	if(bandejaTareasBean.getTarea().getTaskName().equals("Revisar pronunciamiento")){
        		generarDocumentoPronunciamiento(false);
        	}else {
        		generarDocumentoPronunciamiento(true);
			}
        visibleMemorando=true;
        }
    }


    /**
     * Metodo para inicializar variables
     */
    public void inicializar() {
        Map<String, String> params = FacesContext.getCurrentInstance()
                .getExternalContext().getRequestParameterMap();
        String area_tmp = params.get("area");
        area = "";
        tipo = "";
        corregir = false;

        if (area_tmp != null && !area_tmp.isEmpty()) {
            area = area_tmp;

        }
        if (params.containsKey("tipo")) {
            tipo = params.get("tipo");
        }
    }

    public void asignarProyectoActivo() {
        try {
            if (bandejaTareasBean.getTarea() != null) {
               /* Map<String, Object> variables = procesoFacade
                        .recuperarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getTarea()
                                .getProcessInstanceId());
                Integer idProyecto = Integer.parseInt((String) variables
                        .get(Constantes.ID_PROYECTO));
                
                proyectoActivo = proyectoFacade
                        .buscarProyectosLicenciamientoAmbientalPorId(idProyecto);*/
            	
            	proyectoActivo=proyectosBean.getProyecto();
            }

        } catch (Exception e) {
            log.error("Error al inicializar el proyecto", e);
        }
    }

    public void validarTareaBpm() {
        String url = "/prevencion/licenciamiento-ambiental/elaborarPronunciamientoArea.jsf?area=" + area;
        if (tipo != null && !tipo.isEmpty()) {
            url += "&tipo=" + tipo;
        }
        JsfUtil.validarPaginasUrlTareasBpm(bandejaTareasBean.getTarea(), url);
    }


    public StreamedContent getStream() throws Exception {
        DefaultStreamedContent content = new DefaultStreamedContent();
        if (documento != null) {
            if (documento.getContenidoDocumento() == null) {
                documento.setContenidoDocumento(documentosFacade.descargar(documento.getIdAlfresco()));
            }
            content = new DefaultStreamedContent(new ByteArrayInputStream(
                    documento.getContenidoDocumento()), "application/pdf");
            content.setName(documento.getNombre());

        }
        return content;

    }
	
    public void generarDocumentoPronunciamiento(boolean marcaAgua)
	{			
		FileOutputStream file;
		try {
			String autoridad="";
			String nombreReporte="";
			String rol="";
			String variableid="";
			if(getArea().equals("Forestal")){
				nombreReporte= "Pronunciamiento_Forestal";	
			}else {
				nombreReporte= "Pronunciamiento_Biodiversidad";
			}
			
			if(proyectosBean.getProyecto().isAreaResponsableEnteAcreditado()){
				autoridad= variables.get("u_MaximaAutoridad").toString();
				rol="AUTORIDAD AMBIENTAL";
				variableid="u_MaximaAutoridad";
			}else {
				autoridad= variables.get("u_DirectorDPNCA").toString();
				rol=proyectosBean.getProyecto().getAreaResponsable().getTipoArea().getId()==1?"AUTORIDAD AMBIENTAL MAE":"AUTORIDAD AMBIENTAL";
				variableid="u_DirectorDPNCA";
			}
			
			Usuario usuarioAutoridad= new Usuario();
			if (autoridad != null && !autoridad.isEmpty()) {
				try {
//					usuarioAutoridad=usuarioFacade.buscarUsuarioCompleta(autoridad);
					usuarioAutoridad=actualizarAutoridad(autoridad, rol, variableid);
					pronunciamiento.setTratamiento(usuarioAutoridad.getPersona().getTipoTratos().getNombre());
					pronunciamiento.setTitulo(usuarioAutoridad.getPersona().getTitulo());
					pronunciamiento.setNombreAutoridad(usuarioAutoridad.getPersona().getNombre());
					/*String rolAutoridad="";
					for (RolUsuario rolUsuario : usuarioAutoridad.getRolUsuarios()) {
						if(rolUsuario.getRol().getNombre().equals("AUTORIDAD AMBIENTAL") || rolUsuario.getRol().getNombre().equals("AUTORIDAD AMBIENTAL MAE")){
							rolAutoridad=rolUsuario.getRol().getNombre();
						}
					}*/
					pronunciamiento.setRolAutoridad(usuarioAutoridad.getPersona().getPosicion()==null?"":usuarioAutoridad.getPersona().getPosicion());
				} catch (Exception e) {
					JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION_MESA_AYUDA);
					log.error("====>>> No existe Autoridad Ambiental");
				}
			}else {
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION_MESA_AYUDA);
				log.error("No se econtro la cédula de la Autoridad Ambiental");
			}
			pronunciamiento.setLugarEmision(loginBean.getUsuario().getPersona().getUbicacionesGeografica().getUbicacionesGeografica().getNombre());
			pronunciamiento.setNumeroMemorando(pronunciamiento.getNumeroTramite());
			pronunciamiento.setFechaDocumento(JsfUtil.devuelveFechaEnLetrasSinHora(new Date()));
			pronunciamiento.setCodigoProyecto(proyectoActivo.getCodigo());
			if(getArea().equals("Forestal")){
				String autoridadForestal=variables.get("u_DirectorForestal").toString();
				Usuario usuarioForestal=new Usuario();
				if (autoridadForestal != null && !autoridadForestal.isEmpty()) {
					try {
						
						if(proyectoActivo.getAreaResponsable().getId()==257 || proyectoActivo.getAreaResponsable().getAreaAbbreviation().equals(Constantes.SIGLAS_DIRECCION_REGULARIZACION_AMBIENTAL))
							rol="DIRECTOR FORESTAL";
						else
							rol="COORDINADOR FORESTAL";
						
//						usuarioForestal=usuarioFacade.buscarUsuarioCompleta(autoridadForestal);
						usuarioForestal=actualizarAutoridad(autoridadForestal, rol, "u_DirectorForestal");
						
						pronunciamiento.setTratamientoDNF(usuarioForestal.getPersona().getTipoTratos().getNombre());
						pronunciamiento.setTituloDNF((usuarioForestal.getPersona().getPosicion()) == null ? "" : usuarioForestal.getPersona().getPosicion());
						pronunciamiento.setNombreAutoridadDNF(usuarioForestal.getPersona().getNombre());
						String rolDNF="";
						for (RolUsuario rolUsuario : usuarioForestal.getRolUsuarios()) {
							if(rolUsuario.getRol().getNombre().equals("DIRECTOR FORESTAL")){
								rolDNF=rolUsuario.getRol().getNombre();
								if(proyectoActivo.getAreaResponsable().getId()==257){
									rolDNF="DIRECTOR/A NACIONAL FORESTAL";
								} else if(proyectoActivo.getAreaResponsable().getAreaAbbreviation().equals(Constantes.SIGLAS_DIRECCION_REGULARIZACION_AMBIENTAL)) {
									rolDNF="DIRECTOR/A BOSQUES";
								}
								break;
							}
							if(rolUsuario.getRol().getNombre().equals("COORDINADOR FORESTAL")){
								rolDNF=rolUsuario.getRol().getNombre();
							}
						}
						pronunciamiento.setRolDNF(rolDNF);					
					} catch (Exception e) {
						JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION_MESA_AYUDA);
						log.error("No existe DIRECTOR FORESTAL/COORDINADOR FORESTAL");
					}
				}else {
					JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION_MESA_AYUDA);
					log.error("No se econtro la cédula de la DIRECTOR FORESTAL/COORDINADOR FORESTAL");
				}
			}else {
				String autoridadBiodiversidad=variables.get("u_DirectorBiodiversidad").toString();
				Usuario usuarioBiodiversidad=new Usuario();
				if (autoridadBiodiversidad != null && !autoridadBiodiversidad.isEmpty()) {
					try {
						
						if(proyectoActivo.getAreaResponsable().getId()==257)
							rol="DIRECTOR BIODIVERSIDAD";
						else
							rol="COORDINADOR BIODIVERSIDAD";
						
//						usuarioBiodiversidad=usuarioFacade.buscarUsuarioCompleta(autoridadBiodiversidad);
						usuarioBiodiversidad=actualizarAutoridad(autoridadBiodiversidad, rol, "u_DirectorBiodiversidad");
						
						pronunciamiento.setTratamientoDNF(usuarioBiodiversidad.getPersona().getTipoTratos().getNombre());
						pronunciamiento.setTituloDNF(usuarioBiodiversidad.getPersona().getPosicion());
						pronunciamiento.setNombreAutoridadDNF(usuarioBiodiversidad.getPersona().getNombre());
						String rolDNF="";
						for (RolUsuario rolUsuario : usuarioBiodiversidad.getRolUsuarios()) {
							if(rolUsuario.getRol().getNombre().equals("DIRECTOR BIODIVERSIDAD")){
								rolDNF=rolUsuario.getRol().getNombre();
								if(proyectoActivo.getAreaResponsable().getId()==257){
									rolDNF="DIRECTOR/A NACIONAL BIODIVERSIDAD";
								}
								break;
							}
							if(rolUsuario.getRol().getNombre().equals("COORDINADOR BIODIVERSIDAD")){
								rolDNF=rolUsuario.getRol().getNombre();
							}
						}
						pronunciamiento.setRolDNF(rolDNF);					
					} catch (Exception e) {
						JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION_MESA_AYUDA);
						log.error("No existe DIRECTOR BIODIVERSIDAD/COORDINADOR BIODIVERSIDAD");
					}
				}else {
					JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION_MESA_AYUDA);
					log.error("No se econtro la cédula de la DIRECTOR BIODIVERSIDAD/COORDINADOR BIODIVERSIDAD");
				}
			}
			
			if (pronunciamiento.getAsunto() != null && !pronunciamiento.getAsunto().isEmpty())
			pronunciamiento.setAsunto(pronunciamiento.getAsunto());
			if(pronunciamiento.getAsunto() != null)
			pronunciamiento.setPronunciamiento(pronunciamiento.getContenido());
			
			pronunciamiento.setAreaResponsable(proyectoActivo.getAreaResponsable().getAreaAbbreviation());
			if(proyectoActivo.isAreaResponsableEnteAcreditado()){
				pronunciamiento.setMemorando("Oficio");
			}else {
				pronunciamiento.setMemorando("Memorando");
			}

			pronunciamiento.setElaboradoTecnico(pronunciamiento.getUsuario()!=null?pronunciamiento.getUsuario().getPersona().getNombre():loginBean.getUsuario().getPersona().getNombre());			
			pronunciamiento.setElaboradoTecnico(JsfUtil.devuelveSiglas(pronunciamiento.getElaboradoTecnico()));
			
			pronunciamiento.setTituloTecnico(pronunciamiento.getUsuario().getPersona().getPosicion());
			PlantillaReporte plantillaReporte = this.informeProvincialGADFacade.obtenerPlantillaReporte(TipoDocumentoSistema.TIPO_PRONUNCIAMIENTO_FORESTAL.getIdTipoDocumento());
			BaseColor colorGrayWhite = new BaseColor(213, 216, 220);
			File informePdf = JsfUtil.fileMarcaAgua(UtilGenerarInforme.generarFichero(plantillaReporte.getHtmlPlantilla(), nombreReporte, true,pronunciamiento,null),marcaAgua?" - - BORRADOR - - ":" ",colorGrayWhite);
            Path path = Paths.get(informePdf.getAbsolutePath());
            byteArchivo = Files.readAllBytes(path);
            fileArchivo = new File(JsfUtil.devolverPathReportesHtml(informePdf.getName()));
            file = new FileOutputStream(fileArchivo);
            file.write(byteArchivo);
            file.close();
            setUrlPronunciamientoForestal(JsfUtil.devolverContexto("/reportesHtml/"+ informePdf.getName()));
            
            if(!marcaAgua){
            	documentoMemorando = new Documento();
        		JsfUtil.uploadApdoDocument(informePdf, documentoMemorando);						
        		documentoMemorando.setMime("application/pdf");
        		documentoMemorando.setIdTable(pronunciamiento.getId());
        		if(getArea().equals("Forestal")){
        			documentoMemorando.setNombreTabla("PRONUNCIAMIENTO FORESTAL");
        			documentoMemorando.setNombre("PronunciamientoForestal.pdf");
        			documentoMemorando.setExtesion(".pdf");
        			documentosFacade.guardarDocumentoAlfrescoSinProyecto(proyectoActivo.getCodigo(), "PRONUNCIAMIENTO_FORESTAL", bandejaTareasBean.getTarea().getProcessInstanceId(), documentoMemorando, TipoDocumentoSistema.TIPO_PRONUNCIAMIENTO_FORESTAL,null);
        		}else {
        			documentoMemorando.setNombreTabla("PRONUNCIAMIENTO BIODIVERSIDAD");
        			documentoMemorando.setNombre("PronunciamientoBiodiversidad.pdf");
        			documentoMemorando.setExtesion(".pdf");
            		documentosFacade.guardarDocumentoAlfrescoSinProyecto(proyectoActivo.getCodigo(), "PRONUNCIAMIENTO_BIODIVERSIDAD", bandejaTareasBean.getTarea().getProcessInstanceId(), documentoMemorando, TipoDocumentoSistema.TIPO_PRONUNCIAMIENTO_BIODIVERSIDAD,null);
				}
            }
        } catch (Exception e) {
        	e.printStackTrace();
        }
	}
    
    public String generarCodigoMemorando(Area area) {
        try {
        	String mae=Constantes.SIGLAS_INSTITUCION + "-";
        	if(proyectosBean.getProyecto().isAreaResponsableEnteAcreditado()){
        		mae= mae.replace(Constantes.SIGLAS_INSTITUCION, "GAD");
        	}
        	
        	String siglasArea="";
        	
        	if(area.getId()==1139){
        		if(getArea().equals("Forestal")){
        			// siglasArea="DNF-";
        			// Direccion de Bosques
        			siglasArea=siglasForestal + "-";
            	}
            	if(getArea().equals("Biodiversidad")){
            		// siglasArea="DNB-";
            		// Direccion de Areas Protegidas
            		siglasArea=siglasBiodiversidad + "-";
            	}	
        	}else {
        		siglasArea=area.getAreaAbbreviation()+"-";
			}
        	
            
        	return mae
        			+siglasArea
                    + secuenciasFacade.getCurrentYear()
                    + "-"
                    + (getArea().equals("Forestal")?secuenciasFacade.getSecuenciaMemorandoForestal():secuenciasFacade.getSecuenciaMemorandoBiodiversidad())
                    + "-M";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    
    
    public Usuario actualizarAutoridad(String cedula, String rol,String variableid) throws JbpmException
    {    	
    	Map<String, Object> params = new ConcurrentHashMap<String, Object>();
    	Usuario maximaAutoridad =usuarioFacade.buscarUsuarioCompleta(cedula);
    	if(maximaAutoridad==null)
    	{  
    		if(rol.equals("DIRECTOR FORESTAL"))
    		{
    			String area = "DIRECCIÓN NACIONAL FORESTAL";
    			
    			if(proyectoActivo.getAreaResponsable().getAreaAbbreviation().equals(Constantes.SIGLAS_DIRECCION_REGULARIZACION_AMBIENTAL)) {
    				Area areaBosques = areaFacade.getAreaPorAreaAbreviacion(Constantes.SIGLAS_DIRECCION_BOSQUES);
    				area = areaBosques.getAreaName();
    			}
    			
    			maximaAutoridad=asignarTareaFacade.autoridadXRolArea(rol, area);
    			if(maximaAutoridad!=null)
    			{
    				params.put(variableid,maximaAutoridad.getNombre());
    				procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getTarea().getProcessInstanceId(), params);
    			}
    			else
    				System.out.println("====>>> No existe usuario con el ROL:"+rol+" del AREA:"+proyectosBean.getProyecto().getAreaResponsable().getAreaName());
    		}
    		else if(rol.equals("DIRECTOR BIODIVERSIDAD")){
    			maximaAutoridad=asignarTareaFacade.autoridadXRolArea(rol, "DIRECCIÓN NACIONAL DE BIODIVERSIDAD");
    			if(maximaAutoridad!=null)
    			{
    				params.put(variableid,maximaAutoridad.getNombre());
    				procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getTarea().getProcessInstanceId(), params);
    			}
    			else
    				System.out.println("====>>> No existe usuario con el ROL:"+rol+" del AREA:"+proyectosBean.getProyecto().getAreaResponsable().getAreaName());
    		}
    		else
    		{
    			if(proyectosBean.getProyecto().getAreaResponsable().getArea()!=null && !rol.contains("COORDINADOR") ) //los coordinadores se buscan en las zonales
    				if (proyectosBean.getProyecto().getAreaResponsable().getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_PC)) {
    					maximaAutoridad=asignarTareaFacade.autoridadXRolArea(rol, proyectosBean.getProyecto().getAreaResponsable().getAreaName());
    				} else {
    					maximaAutoridad=asignarTareaFacade.autoridadXRolArea(rol, proyectosBean.getProyecto().getAreaResponsable().getArea().getAreaName());
    				}
    			else
    				maximaAutoridad=asignarTareaFacade.autoridadXRolArea(rol, proyectosBean.getProyecto().getAreaResponsable().getAreaName());
    			if(maximaAutoridad!=null)
    			{
    				params.put(variableid,maximaAutoridad.getNombre());
    				procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getTarea().getProcessInstanceId(), params);
    			}
    			else
    				System.out.println("====>>> No existe usuario con el ROL:"+rol+" del AREA:"+proyectosBean.getProyecto().getAreaResponsable().getAreaName());
    		}
    	}
    	else
    	{
    		List<String> roles = new ArrayList<String>();
    		for(RolUsuario res: maximaAutoridad.getRolUsuarios())
    		{
    			roles.add(res.getRol().getNombre());
    		}
    		if(!roles.contains(rol))
    		{
    			if(rol.equals("DIRECTOR FORESTAL"))
        		{
        			String area = "DIRECCIÓN NACIONAL FORESTAL";
        			
        			if(proyectoActivo.getAreaResponsable().getAreaAbbreviation().equals(Constantes.SIGLAS_DIRECCION_REGULARIZACION_AMBIENTAL)) {
        				Area areaBosques = areaFacade.getAreaPorAreaAbreviacion(Constantes.SIGLAS_DIRECCION_BOSQUES);
        				area = areaBosques.getAreaName();
        			}
        			
        			maximaAutoridad=asignarTareaFacade.autoridadXRolArea(rol, area);
        			if(maximaAutoridad!=null)
        			{
        				params.put(variableid,maximaAutoridad.getNombre());
        				procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getTarea().getProcessInstanceId(), params);
        			}
        			else
        				System.out.println("====>>> No existe usuario con el ROL:"+rol+" del AREA:"+proyectosBean.getProyecto().getAreaResponsable().getAreaName());
        		}
        		else if(rol.equals("DIRECTOR BIODIVERSIDAD")){
        			maximaAutoridad=asignarTareaFacade.autoridadXRolArea(rol, "DIRECCIÓN NACIONAL DE BIODIVERSIDAD");
        			if(maximaAutoridad!=null)
        			{
        				params.put(variableid,maximaAutoridad.getNombre());
        				procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getTarea().getProcessInstanceId(), params);
        			}
        			else
        				System.out.println("====>>> No existe usuario con el ROL:"+rol+" del AREA:"+proyectosBean.getProyecto().getAreaResponsable().getAreaName());
        		}
        		else
        		{
        			if(proyectosBean.getProyecto().getAreaResponsable().getArea()!=null && !rol.contains("COORDINADOR") ) //los coordinadores se buscan en las zonales
        				if (proyectosBean.getProyecto().getAreaResponsable().getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_PC)) {
        					maximaAutoridad=asignarTareaFacade.autoridadXRolArea(rol, proyectosBean.getProyecto().getAreaResponsable().getAreaName());
        				} else {
        					maximaAutoridad=asignarTareaFacade.autoridadXRolArea(rol, proyectosBean.getProyecto().getAreaResponsable().getArea().getAreaName());
        				}
        			else
        				maximaAutoridad=asignarTareaFacade.autoridadXRolArea(rol, proyectosBean.getProyecto().getAreaResponsable().getAreaName());
        			if(maximaAutoridad!=null)
        			{
        				params.put(variableid,maximaAutoridad.getNombre());
        				procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getTarea().getProcessInstanceId(), params);
        			}
        			else
        				System.out.println("====>>> No existe usuario con el ROL:"+rol+" del AREA:"+proyectosBean.getProyecto().getAreaResponsable().getAreaName());
        		}
    		} else {
				if (proyectosBean.getProyecto().getAreaResponsable().getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_PC)
						&& rol.contains("AUTORIDAD AMBIENTAL MAE")) { //actualizar usuario para PC cuando se busca la autoridad
					Usuario autoridadPC = asignarTareaFacade.autoridadXRolArea(rol, proyectosBean.getProyecto().getAreaResponsable().getAreaName());
					if (autoridadPC != null) {
						if (!autoridadPC.getNombre().equals(maximaAutoridad.getNombre())) {
							maximaAutoridad = autoridadPC;
							params.put(variableid,maximaAutoridad.getNombre());
							procesoFacade.modificarVariablesProceso(loginBean.getUsuario(),bandejaTareasBean.getTarea().getProcessInstanceId(),params);
						}
					} else
						System.out.println("====>>> No existe usuario con el ROL:" + rol + " del AREA:" + proyectosBean.getProyecto().getAreaResponsable().getAreaName());
				}
    		}
    	}    	
    	return maximaAutoridad;
    }
    
    public void descargarPronunciamiento() {
        String[] tokens = fileArchivo.getName().split("\\.(?=[^\\.]+$)");
        UtilFichaMineria.descargar(fileArchivo.getAbsolutePath(), tokens[0]);
    }
}
