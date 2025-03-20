package ec.gob.ambiente.control.retce.controllers;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.prevencion.tdr.controller.RevisarDocumentacionGeneralController;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.retce.model.EmisionesAtmosfericas;
import ec.gob.ambiente.retce.model.InformeTecnicoRetce;
import ec.gob.ambiente.retce.model.OficioPronunciamientoRetce;
import ec.gob.ambiente.retce.services.EmisionesAtmosfericasFacade;
import ec.gob.ambiente.retce.services.OficioRetceFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.InformeTecnicoGeneralLA;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.DigitalSign;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ViewScoped
@ManagedBean
public class FirmarPronunciamientoEmisionesController {
	
	 private static final Logger LOGGER = Logger.getLogger(RevisarDocumentacionGeneralController.class);
	
	@EJB
    private ProcesoFacade procesoFacade;
    @EJB
    private DocumentosFacade documentosFacade;
    @EJB
    private EmisionesAtmosfericasFacade emisionesAtmosfericasFacade;
    @EJB
    private OficioRetceFacade oficioRetceFacade;
    @EJB
    private UsuarioFacade usuarioFacade;
    
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

    @Getter
    @Setter
    private Documento documento;

    private String documentOffice = "";
    
    @Getter
	private boolean esPronunciamientoAprobacion;
    
    @Getter
    @Setter
    private boolean subido;
    @Getter
    @Setter
    private boolean token;
    
    @Getter
    @Setter
    private OficioPronunciamientoRetce oficio;
    
    private String tramite;
    
    private int docVersion;
    
    @Getter
	@Setter
	private Boolean descargarOficio=false;
    
    private Map<String, Object> variables;
    
    @PostConstruct
    public void init(){    	
    	try {
    		variables=procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
			tramite=(String)variables.get(Constantes.VARIABLE_PROCESO_TRAMITE);		
        	
        	this.esPronunciamientoAprobacion = Boolean.parseBoolean(JsfUtil.getCurrentTask().getVariable("pronunciamiento_aprobado").toString());
        	
        	token = loginBean.getUsuario().getToken() != null && loginBean.getUsuario().getToken();
        	
        	oficio = oficioRetceFacade.getOficio(tramite, TipoDocumentoSistema.OFICIO_PRONUNCIAMIENTO_EMISION_ATMOSFERICA);
        	    	
        	if (token) {
                documentOffice = documentosFacade.direccionDescarga(InformeTecnicoRetce.class.getSimpleName(), proyectosBean.getProyecto().getId(), TipoDocumentoSistema.OFICIO_PRONUNCIAMIENTO_EMISION_ATMOSFERICA);
            }    	
        	
        	List<Documento> documentos = documentosFacade.documentoXTablaIdXIdDoc(oficio.getId(), "OficioEmisionesAtmosfericas", TipoDocumentoSistema.OFICIO_PRONUNCIAMIENTO_EMISION_ATMOSFERICA);
        	
        	 if (documentos.size() > 0) {
                 documento = documentos.get(0);

                 String id = documento.getIdAlfresco();
                 
                 this.docVersion = Integer.parseInt(id.substring(id.length()-1));
                 
             } else {
                 JsfUtil.addMessageError("Error al realizar la operación.");
             }    
		} catch (Exception e) {
			LOGGER.error(e);
            JsfUtil.addMessageError("Error al cargar los datos. Por favor intente más tarde.");
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
		if (descargarOficio == true) {
			byte[] contenidoDocumento = event.getFile().getContents();
			documento.setContenidoDocumento(contenidoDocumento);
			documento.setNombre(event.getFile().getFileName());
			subido = true;
			JsfUtil.addMessageInfo("Documento subido exitosamente");
		} else {
			JsfUtil.addMessageError("Debes descargar el documento");
		}
	}
    
    public void completarTarea() {
    	try {
    		//Falta lo de la firma
    		
    		
    		//si va esta parte por lo que en esta misma pantalla se va a realizar el ingreso de observaciones
    		Map<String, Object> params=new HashMap<>();
//    		params.put("tiene_observaciones_informe_oficio", false);
//    		procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), params);    		
    		
    		taskBeanFacade.approveTask(loginBean.getNombreUsuario(),
                    bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getTarea().getProcessInstanceId(), null, loginBean.getPassword(),
                    Constantes.getUrlBusinessCentral(), Constantes.getRemoteApiTimeout(), Constantes.getNotificationService());
//            this.documentosFacade.actualizarDocumento(documento);
            JsfUtil.addMessageInfo("Se realizó correctamente la operación.");           
           JsfUtil.actionNavigateTo("/bandeja/bandejaTareas.jsf");
		} catch (Exception e) {
			e.printStackTrace();
		}    	
    }
    

}
