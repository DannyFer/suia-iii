package ec.gob.ambiente.prevencion.participacionsocial.controller;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.prevencion.participacionsocial.bean.OficioPSBean;
import ec.gob.ambiente.prevencion.tdr.controller.RevisarDocumentacionGeneralController;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.administracion.facade.ContactoFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.FacilitadorProyecto;
import ec.gob.ambiente.suia.domain.FormasContacto;
import ec.gob.ambiente.suia.domain.ParticipacionSocialAmbiental;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.facilitador.facade.FacilitadorProyectoFacade;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.notificacionautoridades.controllers.NotificacionAutoridadesController;
import ec.gob.ambiente.suia.prevencion.participacionsocial.facade.ParticipacionSocialFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.DigitalSign;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ManagedBean
@ViewScoped
public class FirmarOficioPSController implements Serializable {
    private static final Logger LOGGER = Logger
            .getLogger(RevisarDocumentacionGeneralController.class);
    private static final long serialVersionUID = -2814933203035634101L;

    @EJB
    private ProcesoFacade procesoFacade;
    @EJB
    private DocumentosFacade documentosFacade;

    @EJB
    private ParticipacionSocialFacade participacionSocialFacade;
    
    //CF: aumento de EJB
    @EJB
    private FacilitadorProyectoFacade facilitadorProyectoFacade;
    
    @EJB
    private UsuarioFacade usuarioFacade;
    @EJB
    private ContactoFacade contactoFacade;
    @EJB
    private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;

    @Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;

    @EJB(lookup = Constantes.JBPM_EJB_TASK_BEAN)
    private TaskBeanFacade taskBeanFacade;

    @Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;

    @ManagedProperty(value = "#{proyectosBean}")
    @Getter
    @Setter
    private ProyectosBean proyectosBean;

    @Getter
    @Setter
    private Documento documento;

    private String documentOffice = "";

    @Getter
    @Setter
    private boolean mostrarInforme;

    @Getter
    @Setter
    private ParticipacionSocialAmbiental participacionSocialAmbiental;

    @Getter
    @Setter
    private boolean subido;
    @Getter
    @Setter
    private boolean token;

    @Getter
    private Boolean esPronunciamientoAprobacion;

    private int docOficioPSVersion;


    @PostConstruct
    public void init() {
        try {
        	
            Object variable = JsfUtil.getCurrentTask().getVariable("esPronunciamientoAprobacion");

            if (variable == null)
                variable = JsfUtil.getCurrentTask().getVariable("existenObservaciones");

            this.esPronunciamientoAprobacion = variable != null ? Boolean
                    .parseBoolean(variable.toString()) : null;

            token = loginBean.getUsuario().getToken() != null && loginBean.getUsuario().getToken();
            OficioPSBean oficioPSBean = JsfUtil.getBean(OficioPSBean.class);
            oficioPSBean.configurarVariables(token);
            oficioPSBean.visualizarOficio();
            oficioPSBean.guardarDocumento();
            if (token) {


                documentOffice = documentosFacade.direccionDescarga(ParticipacionSocialAmbiental.class.getSimpleName()
                        , proyectosBean.getProyecto().getId(), TipoDocumentoSistema.PS_OFICIO_FINAL);
            }
            List<Documento> documentos = documentosFacade.documentoXTablaIdXIdDoc(proyectosBean.getProyecto().getId(),
                    (ParticipacionSocialAmbiental.class.getSimpleName()), TipoDocumentoSistema.PS_OFICIO_FINAL);
            if (documentos.size() > 0) {
                documento = documentos.get(0);
                String id = documento.getIdAlfresco();
                this.docOficioPSVersion = Integer.parseInt(id.substring(id.length()-1));
            } else {
                JsfUtil.addMessageError("Error al realizar la operación.");

            }

            participacionSocialAmbiental = participacionSocialFacade.buscarCrearParticipacionSocialAmbiental(proyectosBean.getProyecto());


        } catch (CmisAlfrescoException | ServiceException e) {
            LOGGER.error(e);
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
        } catch (Exception e) {
            LOGGER.error(e);
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_CARGAR_DATOS);
        }

    }

    public String firmarDocumento() {
        try {


            DigitalSign firmaE = new DigitalSign();
            return firmaE.sign(documentOffice, loginBean.getUsuario().getNombre()); // loginBean.getUsuario()
        } catch (Throwable e) {
            LOGGER.error(e);
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
            if (documento != null) {
                if (subido) {

                    Documento documentoN = new Documento();
                    documentoN.setContenidoDocumento(documento.getContenidoDocumento());
                    documentoN.setNombreTabla(documento.getNombreTabla());
                    documentoN.setIdTable(documento.getIdTable());
                    documentoN.setExtesion(documento.getExtesion());
                    documentoN.setNombre(documento.getNombre());
                    documentoN.setMime("application/pdf");
                    Map<String, Documento> documentos = new HashMap<>();
                    documentos.put("oficio", documentoN);
                    participacionSocialFacade.ingresarInformes(
                            documentos, bandejaTareasBean
                                    .getProcessId(), bandejaTareasBean.getTarea()
                                    .getTaskId(), proyectosBean.getProyecto().getCodigo());
                }
            }

            String idAlfresco = documento.getIdAlfresco();

            idAlfresco = documento.getIdAlfresco().substring(0,idAlfresco.length()-1);
            idAlfresco= idAlfresco+""+(++this.docOficioPSVersion);

            documento.setIdAlfresco(idAlfresco);
            if (!token || documentosFacade.verificarFirmaVersion(idAlfresco)) {

                Map<String, Object> params = new ConcurrentHashMap<String, Object>();
                params.put("oficioAprobacion", participacionSocialAmbiental.getPronunciamientoFavorable());
                params.put("cumpleFacilitador", participacionSocialAmbiental.getCumplioFacilitador());
                if (participacionSocialAmbiental.getPronunciamientoFavorable()) {
                    params.put("completadoProcesoPS", true);
                }
                
                enviarMailFacilitador();
                procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean
                        .getTarea().getProcessInstanceId(), params);


                Map<String, Object> data = new ConcurrentHashMap<String, Object>();
                procesoFacade.aprobarTarea(loginBean.getUsuario(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), data);

                this.documentosFacade.actualizarDocumento(documento);

                JsfUtil.addMessageInfo("Se realizó correctamente la operación.");
                return JsfUtil.actionNavigateTo("/bandeja/bandejaTareas.jsf");
            } else {
                JsfUtil.addMessageError("El documento no está firmado.");
                return "";
            }
        } catch (JbpmException e) {
            LOGGER.error(e);
            JsfUtil.addMessageError("Error al realizar la operación.");
        } catch (Exception e) {
            LOGGER.error(e);
            JsfUtil.addMessageError("Error al realizar la operación.");
        }
        return "";
    }

    public void validarTareaBpm() {

        String url = "/prevencion/participacionsocial/firmarOficio.jsf";

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

    public void uploadListenerDocumentos(FileUploadEvent event) {
        byte[] contenidoDocumento = event.getFile().getContents();

        documento.setContenidoDocumento(contenidoDocumento);
        documento.setNombre(event.getFile().getFileName());
        subido = true;
    }
    
    /**
     * CF: envio de mail para los facilitadores
     * @throws CmisAlfrescoException 
     */
    private void enviarMailFacilitador(){    	
    	
    	try {
    		Documento documentoDescargar = new Documento();
        	
       	 	List<Documento> documentos = documentosFacade.documentoXTablaIdXIdDoc(proyectosBean.getProyecto().getId(),
                    (ParticipacionSocialAmbiental.class.getSimpleName()), TipoDocumentoSistema.PS_OFICIO_FINAL);
            if (documentos.size() > 0) {
                documentoDescargar = documentos.get(0);
                
                if (documento != null) {                   
                   documentoDescargar.setContenidoDocumento(documentosFacade.descargar(documentoDescargar.getIdAlfresco()));                    
                }
            } else {
                JsfUtil.addMessageError("Error al realizar la operación.");
            } 
            
            String url = System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + documentoDescargar.getDescripcion();      
            String nombreDocumento = documentoDescargar.getDescripcion(); 
           	
           	List<FacilitadorProyecto> facilitadores = facilitadorProyectoFacade.listarFacilitadoresProyecto(proyectosBean.getProyecto());
           	
           	String nombreProceso = proyectosBean.getProyecto().getNombre();
           	
       		UbicacionesGeografica ubicacion = proyectoLicenciamientoAmbientalFacade.getUbicacionProyectoPorIdProyecto(proyectosBean.getProyecto().getId());
       		String tramite = proyectosBean.getProyecto().getCodigo();
       		
       		String ubicacionProyecto = proyectosBean.getProyecto().getDireccionProyecto()+ 
       				" - Cantón: " + ubicacion.getUbicacionesGeografica().getNombre() + 
       				" - Provincia: " + ubicacion.getUbicacionesGeografica().getUbicacionesGeografica().getNombre();   		
       		
           
           	for(FacilitadorProyecto facilitador : facilitadores){
           		
           		Usuario usuario = usuarioFacade.buscarUsuarioPorId(facilitador.getUsuario().getId());
           		String nombreFacilitador = usuario.getPersona().getNombre();
           		
           		List<Contacto> listaContactos = contactoFacade.buscarPorPersona(usuario.getPersona());
       			String emailFacilitador = "";
       			for(Contacto contacto : listaContactos){
       				if(contacto.getFormasContacto().getId() == FormasContacto.EMAIL){
       					emailFacilitador = contacto.getValor();
       					break;
       				}				
       			}
       						
       			NotificacionAutoridadesController mail_a = new NotificacionAutoridadesController();
       			mail_a.sendEmailFacilitadoresFinalProceso("Final Proceso", nombreFacilitador, emailFacilitador, url, tramite, nombreProceso, ubicacionProyecto, nombreDocumento, usuario, loginBean.getUsuario());
           	}       	
		} catch (Exception e) {
			e.printStackTrace();
		}
    }


}
