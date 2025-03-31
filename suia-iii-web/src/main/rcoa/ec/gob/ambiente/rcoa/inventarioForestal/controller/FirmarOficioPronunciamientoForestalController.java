package ec.gob.ambiente.rcoa.inventarioForestal.controller;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
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

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.DocumentoInventarioForestalFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.InventarioForestalAmbientalFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.model.DocumentoInventarioForestal;
import ec.gob.ambiente.rcoa.inventarioForestal.model.InventarioForestalAmbiental;
import ec.gob.ambiente.rcoa.inventarioForestal.model.RegistroEspeciesForestales;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.DigitalSign;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;

@ManagedBean
@ViewScoped
public class FirmarOficioPronunciamientoForestalController {
	
	private static final Logger LOG = Logger.getLogger(FirmarOficioPronunciamientoForestalController.class);
	
	@EJB
	private ProcesoFacade procesoFacade;
	private Map<String, Object> variables;
	
	/*BEANs*/
	@Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;
	@ManagedProperty(value = "#{loginBean}")
	@Setter
	@Getter
	private LoginBean loginBean;
    
	/*EJBs*/
	@EJB
	private CrudServiceBean crudServiceBean;
    
    
    // FACADES GENERALES
	@EJB
	private DocumentoInventarioForestalFacade documentoInventarioForestalFacade;
	@EJB
    private UsuarioFacade usuarioFacade;
	@EJB
	private InventarioForestalAmbientalFacade inventarioForestalAmbientalFacade;
	
    
    /*List*/
    @Setter
	@Getter
	private List<RegistroEspeciesForestales> listRegistroEspeciesForestales = new ArrayList<RegistroEspeciesForestales>();
    

    /*Object*/
    @Setter
    @Getter
    private ProyectoLicenciaCoa proyectoLicenciaCoa = new ProyectoLicenciaCoa();
    @Setter
    @Getter
	private DocumentoInventarioForestal oficioPronunciamiento = new DocumentoInventarioForestal();
    
    @Getter
	@Setter
	private boolean token, esAutoridad=false, informeSubido, deshabilitarToken, oficioSubido;
    @Getter
	@Setter
	private String nombreDocumentoFirmado;
    @Getter
    private Integer idRegistroPreliminar;
    @Setter
    @Getter
    private String tramite, autoridadAmbiental, nombreOficioFirmado;
    
    
    /*CONSTANTES*/
    public static final Integer TIPO_INVENTARIO = 1;
   
    
    @PostConstruct
	public void init() {
    	try {
    		variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(),bandejaTareasBean.getProcessId());
    		tramite = (String) variables.get(Constantes.VARIABLE_PROCESO_TRAMITE);
    		idRegistroPreliminar = Integer.valueOf((String)variables.get(Constantes.ID_PROYECTO));
    		autoridadAmbiental = (String) variables.get("autoridadAmbiental");
    		InventarioForestalAmbiental inventarioForestalAmbiental = inventarioForestalAmbientalFacade.getByIdRegistroPreliminar(idRegistroPreliminar);
    		Integer idInventarioForestalRegistro = inventarioForestalAmbiental.getId();
    		oficioPronunciamiento = documentoInventarioForestalFacade.getByInventarioTipoDocumento(idInventarioForestalRegistro, TipoDocumentoSistema.INVENTARIO_FORESTAL_OFICIO_PRONUNCIAMIENTO);
    		
    		String usuario = JsfUtil.getLoggedUser().getNombre();
			if(usuario.equals(autoridadAmbiental)) {
				esAutoridad = true;
			}
			
			token = verificaToken();
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos.");
		}
	}
    
   
    private boolean verificaToken() {
		boolean hasToken = false;
		if (JsfUtil.getLoggedUser().getToken() != null && JsfUtil.getLoggedUser().getToken())
			hasToken = true;
		return hasToken;
	}
    public void guardarToken() {
		Usuario usuario = JsfUtil.getLoggedUser();
		usuario.setToken(token);
		try {
			usuarioFacade.guardar(usuario);
			verificaToken();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    public StreamedContent descargarOficio() throws Exception {
    	DefaultStreamedContent content = new DefaultStreamedContent();
		content = new DefaultStreamedContent(new ByteArrayInputStream(oficioPronunciamiento.getContenidoDocumento()), oficioPronunciamiento.getMimeDocumento());
		content.setName(oficioPronunciamiento.getNombreDocumento());
		return content;
	}
    public String firmarOficio() {
    	try {
			
			if(oficioPronunciamiento != null) {
				String documentOffice = documentoInventarioForestalFacade.direccionDescarga(oficioPronunciamiento);
				return DigitalSign.sign(documentOffice, loginBean.getUsuario().getNombre());
			}
		} catch (Exception exception) {
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
    	return "";
    }
    
    public void subirOficioFirmado(FileUploadEvent event) {
		byte[] contenidoDocumento = event.getFile().getContents();
		oficioPronunciamiento.setContenidoDocumento(contenidoDocumento);
		oficioPronunciamiento.setNombreDocumento(event.getFile().getFileName());
		try {
			documentoInventarioForestalFacade.guardarDocumentoAlfrescoInventarioForestal(oficioPronunciamiento.getInventarioForestalAmbiental().getProyectoLicenciaCoa().getCodigoUnicoAmbiental(), "INVENTARIO_FORESTAL", 0L, oficioPronunciamiento, TipoDocumentoSistema.INVENTARIO_FORESTAL_OFICIO_PRONUNCIAMIENTO);
			oficioSubido=true;
			JsfUtil.addMessageInfo("Documento subido exitosamente");
		} catch (ServiceException e) {
			oficioSubido=false;
			JsfUtil.addMessageError("Erro al subir el documento");
			e.printStackTrace();
		} catch (CmisAlfrescoException e) {
			oficioSubido=false;
			JsfUtil.addMessageError("Erro al subir el documento");
			e.printStackTrace();
		}
		
		
	}
    
    public void enviar(){
		try {
			
			if((token && !documentoInventarioForestalFacade.verificarFirmaAlfresco(oficioPronunciamiento.getIdAlfresco()))
					  ||(!token && !oficioSubido)){					
						JsfUtil.addMessageError("El documento no ha sido firmado.");
						return;
			}
			
			procesoFacade.aprobarTarea(loginBean.getUsuario(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
	        JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
		} catch (JbpmException e) {
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
			e.printStackTrace();
		}
	}


}