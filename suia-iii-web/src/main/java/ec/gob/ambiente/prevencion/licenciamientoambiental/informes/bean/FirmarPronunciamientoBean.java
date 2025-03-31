package ec.gob.ambiente.prevencion.licenciamientoambiental.informes.bean;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
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
import observaciones.ObservacionesController;

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.prevencion.licenciamientoambiental.bean.ElaborarPronunciamientoAreaLABean;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.ObservacionesFormularios;
import ec.gob.ambiente.suia.domain.Pronunciamiento;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.ProyectoLicenciaAmbientalFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.pronunciamiento.facade.PronunciamientoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.DigitalSign;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class FirmarPronunciamientoBean implements Serializable {

    // ///////////////////////
    // CONSTANTES
    // ///////////////////////
    public static final String TEMP_CONST_NOMBRE_SECUENCIA_NUMERO = "seq_number_job_order_statement";
    public static final String TEMP_CONST_NOMBRE_ESQUEMA_SUIA = "suia_iii";
    public static final int TEMP_CONST_TAMANIO_NUMERO_OFICIO = 6;
    /**
     *
     */
    private static final long serialVersionUID = 165683211928358047L;

    private final Logger LOG = Logger.getLogger(FirmarPronunciamientoBean.class);

    @ManagedProperty(value = "#{proyectosBean}")
    @Getter
    @Setter
    private ProyectosBean proyectosBean;

    @ManagedProperty(value = "#{loginBean}")
    @Getter
    @Setter
    private LoginBean loginBean;

    @EJB
    private DocumentosFacade documentosFacade;

    @EJB
    private ProcesoFacade procesoFacade;
    
    @EJB
    private PronunciamientoFacade pronunciamientoFacade;
    
    @EJB
    private ProyectoLicenciaAmbientalFacade proyectoLicenciaAmbientalFacade;
    
    @Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;

    @Getter
    @Setter
    private String area;
    @Getter
    @Setter
    private String tipo;
    
    @Getter
    @Setter
    private Boolean correcto;
    
    private String documentOffice = "";
    
    @Getter
    @Setter
    @ManagedProperty(value = "#{observacionesController}")
    private ObservacionesController observacionesController;
    
    @Getter
    @Setter
    @ManagedProperty(value = "#{elaborarPronunciamientoAreaLABean}")
    private ElaborarPronunciamientoAreaLABean elaborarPronunciamientoAreaLABean;
    
    @Getter
    @Setter
    private ProyectoLicenciamientoAmbiental proyecto;
    
    @Getter
    @Setter
    private Pronunciamiento pronunciamiento;
    
    @Getter
    @Setter
	private Documento documentoForestal = new Documento();
    
    @Getter
    @Setter
	private Documento documentoBiodiversidad = new Documento();
    
    private byte[] documentoDescargaForestal;
    
    private byte[] documentoDescargaBiodiversidad;
    
    private final String clasePronunciamiento = "Pronunciamiento";
    
    @Getter
    @Setter
    private Documento documento= new  Documento();
    
    @Getter
    @Setter
    private boolean token;
    
    @EJB
    private UsuarioFacade usuarioFacade;
    
    @Getter
    @Setter
    private boolean subido;
    
    @Getter
	@Setter
	private boolean ambienteProduccion;
    
    @Getter
	@Setter
	public static String tipoAmbiente = Constantes.getPropertyAsString("ambiente.produccion");
    
    @Getter
    @Setter
    private Documento documentoSubido= new  Documento();
    
    @PostConstruct
    public void init() {
    	correcto = true;
    	
    	verificaToken();
    	
    	Map<String, String> params = FacesContext.getCurrentInstance()
                .getExternalContext().getRequestParameterMap();
        area = params.get("area");
        tipo = params.get("tipo");

        String areaEsp = area;
        if (tipo != null) {
            areaEsp += tipo;
        }
        
        ambienteProduccion = Boolean.parseBoolean(tipoAmbiente);
        
        try {
            proyecto = proyectosBean.getProyecto();
            if(areaEsp!=null){
            	pronunciamiento = pronunciamientoFacade.getPronunciamientosPorClaseTipo(clasePronunciamiento, Long.parseLong(proyecto.getId().toString()), areaEsp);
            	validaMemorando(pronunciamiento);
            }
            
        } catch (Exception e) {
            JsfUtil.addMessageError("Error al realizar la operación. Intente más tarde.");
            LOG.error("Error al realizar la operación.", e);
        }
        
        documentoSubido = new Documento();
    }


    @SuppressWarnings("static-access")
	public String firmarDocumento() {
        try {
            if (!documentOffice.isEmpty() && documentOffice!=null) {
            	DigitalSign firmaE = new DigitalSign();
                return firmaE.sign(documentOffice, loginBean.getUsuario().getNombre()); // loginBean.getUsuario()				
			}else{
				JsfUtil.addMessageError("No se encontró el documento.");
	            return "";
			}

        } catch (Throwable e) {
            LOG.error(e);
            JsfUtil.addMessageError("Error al realizar la operación.");
            return "";
        }
    }
    
    public boolean verificaToken (){
		token = false;
		if (JsfUtil.getLoggedUser().getToken() != null && JsfUtil.getLoggedUser().getToken())
			token = true;
		return token;
	}

	
	public void guardarToken(){
		Usuario usuario= JsfUtil.getLoggedUser();
		usuario.setToken(token);
		try {
			usuarioFacade.guardar(usuario);
			verificaToken ();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

    public String completarTarea() {
        try {
        	Map<String, Object> params = new ConcurrentHashMap<String, Object>();
        	 if (validarObservaciones(correcto)) {
                 params.put("requiereModificacionesPronuciamientoA",
                         !getCorrecto());
                 params.put("requiereModificacionesPronuciamientoA" + getArea(),
                         !getCorrecto());
                 procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean
                         .getTarea().getProcessInstanceId(), params);
                 }
        	if(validarObservaciones(correcto) && !correcto){
        		Map<String, Object> data = new ConcurrentHashMap<String, Object>();
        		procesoFacade.aprobarTarea(loginBean.getUsuario(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), data);
        		JsfUtil.addMessageInfo("Se realizó correctamente la operación.");
                return JsfUtil.actionNavigateTo("/bandeja/bandejaTareas.jsf");
        	}
        	 
        	if (token && !documentosFacade.verificarFirmaVersion(documento.getIdAlfresco())) {
        		JsfUtil.addMessageError("El documento no está firmado.");
        		return "";
        	}else if (!token) {
				if (subido) {
					if (documento.getTipoDocumento().getId() == 260) {
						documentosFacade.guardarDocumentoAlfrescoSinProyecto(
										proyecto.getCodigo(),
										"PRONUNCIAMIENTO_FORESTAL",
										bandejaTareasBean.getTarea()
										.getProcessInstanceId(),
										documento,
										TipoDocumentoSistema.TIPO_PRONUNCIAMIENTO_FORESTAL,
										null);
					} else {
						documentosFacade.guardarDocumentoAlfrescoSinProyecto(
										proyecto.getCodigo(),
										"PRONUNCIAMIENTO_BIODIVERSIDAD",
										bandejaTareasBean.getTarea()
										.getProcessInstanceId(),
										documento,
										TipoDocumentoSistema.TIPO_PRONUNCIAMIENTO_BIODIVERSIDAD,
										null);
					}
					this.documentosFacade.actualizarDocumento(documento);
				}else{
        			JsfUtil.addMessageError("Debe adjuntar el pronunciamiento firmado");
        			return "";
        		}
        	}
            
            Map<String, Object> data = new ConcurrentHashMap<String, Object>();
            procesoFacade.aprobarTarea(loginBean.getUsuario(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), data);

            JsfUtil.addMessageInfo("Se realizó correctamente la operación.");
            return JsfUtil.actionNavigateTo("/bandeja/bandejaTareas.jsf");
        	
            //Se aprueba la tarea
            //Map<String, Object> data = new ConcurrentHashMap<String, Object>();
            //procesoFacade.aprobarTarea(loginBean.getUsuario(), bandejaTareasBean.getTarea().getTaskId(),  bandejaTareasBean.getProcessId(), data);

            //JsfUtil.addMessageInfo("Se realizó correctamente la operación.");
            //return JsfUtil.actionNavigateTo("/bandeja/bandejaTareas.jsf");
        } catch (Exception e) {
            LOG.error(e);
            JsfUtil.addMessageError("Error al realizar la operación.");
        }
        return "";
    }
    
    public void validarTareaBpm() {
        String url = "/prevencion/licenciamiento-ambiental/firmarPronunciamientoArea.jsf?area="
                + getArea();
        if (getTipo() != null && !getTipo().isEmpty()) {
            url += "&tipo=" + getTipo();
        }
        JsfUtil.validarPaginasUrlTareasBpm(bandejaTareasBean.getTarea(), url);
    }
    
    public boolean validaMemorando(Pronunciamiento pronunciamiento) throws CmisAlfrescoException, ServiceException{
   	   
    	String nombreTabla="";
		if(pronunciamiento.getTipo().equals("Forestal")){
		   nombreTabla="PRONUNCIAMIENTO FORESTAL";
		   List<Documento>ListDocumentoForestal= new ArrayList<Documento>();
		   ListDocumentoForestal=documentosFacade.documentoXTablaIdXIdDoc(pronunciamiento.getId(), nombreTabla, TipoDocumentoSistema.TIPO_PRONUNCIAMIENTO_FORESTAL);
		   
		   documento = ListDocumentoForestal.get(0);
		   if(!documento.getUsuarioCreacion().equals(JsfUtil.getLoggedUser().getNombre()) 
					|| !JsfUtil.getSimpleDateFormat(documento.getFechaCreacion()).equals(JsfUtil.getSimpleDateFormat(new Date()))) {
			   documento.setEstado(false);
			   documentosFacade.actualizarDocumento(documento);
			   generarGuardarDocumento();
			   
			   ListDocumentoForestal= new ArrayList<Documento>();
			   ListDocumentoForestal=documentosFacade.documentoXTablaIdXIdDoc(pronunciamiento.getId(), nombreTabla, TipoDocumentoSistema.TIPO_PRONUNCIAMIENTO_FORESTAL);			   
		   }	   
		   
		   documentOffice = documentosFacade.direccionDescarga(nombreTabla, pronunciamiento.getId(), TipoDocumentoSistema.TIPO_PRONUNCIAMIENTO_FORESTAL);
		   
		   if(ListDocumentoForestal.size()>0){
			   documentoForestal =ListDocumentoForestal.get(0);
			   documento=documentoForestal;
			   descargarDocumento(pronunciamiento.getTipo());
			   return true;
		   }
		  }else if(pronunciamiento.getTipo().equals("Biodiversidad")) {
		   nombreTabla="PRONUNCIAMIENTO BIODIVERSIDAD";
		   List<Documento>ListDocumentoBiodiversidad= new ArrayList<Documento>();
		   ListDocumentoBiodiversidad=documentosFacade.documentoXTablaIdXIdDoc(pronunciamiento.getId(), nombreTabla, TipoDocumentoSistema.TIPO_PRONUNCIAMIENTO_BIODIVERSIDAD);
		   
		   documento = ListDocumentoBiodiversidad.get(0);
		   if(!documento.getUsuarioCreacion().equals(JsfUtil.getLoggedUser().getNombre()) 
					|| !JsfUtil.getSimpleDateFormat(documento.getFechaCreacion()).equals(JsfUtil.getSimpleDateFormat(new Date()))) {
			   documento.setEstado(false);
			   documentosFacade.actualizarDocumento(documento);
			   generarGuardarDocumento();
			   ListDocumentoBiodiversidad= new ArrayList<Documento>();
			   ListDocumentoBiodiversidad=documentosFacade.documentoXTablaIdXIdDoc(pronunciamiento.getId(), nombreTabla, TipoDocumentoSistema.TIPO_PRONUNCIAMIENTO_BIODIVERSIDAD);			   
		   }
		   
		   documentOffice = documentosFacade.direccionDescarga(nombreTabla, pronunciamiento.getId(), TipoDocumentoSistema.TIPO_PRONUNCIAMIENTO_BIODIVERSIDAD);
		   
		   if(ListDocumentoBiodiversidad.size()>0){
			   documentoBiodiversidad = ListDocumentoBiodiversidad.get(0);
			   documento=documentoBiodiversidad;
			   descargarDocumento(pronunciamiento.getTipo());
			   return true;
		   }
		}
	   return false;
   }
    
    public void descargarDocumento(String tipo) throws CmisAlfrescoException{
    	if(tipo.equals("Forestal")){
    		if(documentoDescargaForestal==null)
    		documentoDescargaForestal=documentosFacade.descargar(documentoForestal.getIdAlfresco());
    	}else {
    		if(documentoDescargaBiodiversidad==null)
    		documentoDescargaBiodiversidad=documentosFacade.descargar(documentoBiodiversidad.getIdAlfresco());
    	}
    }
    
    public StreamedContent getStreamDocumento(String tipo) throws Exception {
        
    	DefaultStreamedContent content = new DefaultStreamedContent();
        if(tipo.equals("Forestal")){
        	if (documentoDescargaForestal != null) {
            content = new DefaultStreamedContent(new ByteArrayInputStream(
               		documentoDescargaForestal), "application/pdf");
            content.setName("PronunciamientoForestal.pdf");
            }
        }else {
        	if (documentoDescargaBiodiversidad != null) {
        	content = new DefaultStreamedContent(new ByteArrayInputStream(
               		documentoDescargaBiodiversidad), "application/pdf");
            content.setName("PronunciamientoBiodiversidad.pdf");
        	}
        }
        return content;
    } 
    
    public Boolean validarObservaciones(Boolean estado) {
        List<ObservacionesFormularios> observaciones = observacionesController.getObservacionesBB().getMapaSecciones().get(getArea());

        if (estado) {
            for (ObservacionesFormularios observacion : observaciones) {
                if (!observacion.isObservacionCorregida()) {

                    JsfUtil.addMessageError("Existen observaciones sin corregir. Por favor rectifique los datos.");
                    return false;
                }
            }
        } else {
            int posicion = 0;
            int cantidad = observaciones.size();
            Boolean encontrado = false;
            while (!encontrado && posicion < cantidad) {
                if (!observaciones.get(posicion++).isObservacionCorregida()) {
                    encontrado = true;
                }
            }
            if (!encontrado) {
                JsfUtil.addMessageError("No existen observaciones sin corregir. Por favor rectifique los datos.");
                return false;
            }
        }
        return true;
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


    public void uploadListenerDocumentos(FileUploadEvent event) {
        byte[] contenidoDocumento = event.getFile().getContents();

        documento.setContenidoDocumento(contenidoDocumento);
        documento.setNombre(event.getFile().getFileName());
        subido = true;
    }
    
    public void generarGuardarDocumento(){
    	try {
    			elaborarPronunciamientoAreaLABean.generarDocumentoPronunciamiento(false);			
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}
