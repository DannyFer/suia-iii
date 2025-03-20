package ec.gob.ambiente.rcoa.participacionCiudadana.controller;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.alfresco.service.AlfrescoServiceBean;
import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.rcoa.participacionCiudadana.facade.DocumentoPPCFacade;
import ec.gob.ambiente.rcoa.participacionCiudadana.facade.FacilitadorPPCFacade;
import ec.gob.ambiente.rcoa.participacionCiudadana.model.DocumentosPPC;
import ec.gob.ambiente.rcoa.participacionCiudadana.model.FacilitadorPPC;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.Email;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;

@ManagedBean
@ViewScoped
public class IngresarPlanificacionPPCController {
	
	private static final Logger LOGGER = Logger.getLogger(IngresarPlanificacionPPCController.class);
	private final String nombreDocumento = "Formatos_Participación_Social.zip";
	
	@EJB
	private ProcesoFacade procesoFacade;
	
	@EJB
    private DocumentosFacade documentosSuiaIIIFacade;
	
	@EJB(lookup = Constantes.ALFRESCO_SERVICE_BEAN)
    private AlfrescoServiceBean alfrescoServiceBean;
	
	@EJB
	private DocumentoPPCFacade documentosFacade;
	
	@EJB
	private FacilitadorPPCFacade facilitadorFacade;
	
	@EJB
	private MensajeNotificacionFacade mensajeNotificacionFacade;
	
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
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;
	
	@Getter
	@Setter
	private Map<String, Object> variables;
	
	@Getter
    @Setter
    private String tramite = "";
	
	@Getter
	@Setter
	private Integer numeroFacilitadores;
	
	@Getter
    @Setter
    private byte[] documento;
	
	@Getter
	@Setter
	private DocumentosPPC documentoInformePlanificacion;
	
	@PostConstruct
	public void inicio()
	{
		try {			
			variables = procesoFacade.recuperarVariablesProceso(loginBean.getUsuario(),bandejaTareasBean.getTarea().getProcessInstanceId());
			tramite = (String) variables.get("tramite");
			numeroFacilitadores = Integer.valueOf((String) variables.get("numeroFacilitadores"));
		} catch (JbpmException e) {

		}
	}
	
	public StreamedContent getStream() throws Exception {
        DefaultStreamedContent content = new DefaultStreamedContent();
        documento = documentosSuiaIIIFacade.descargarDocumentoPorNombre(nombreDocumento);
        if (documento != null) {
            content = new DefaultStreamedContent(new ByteArrayInputStream(documento), "application/zip");
            content.setName(nombreDocumento);
        }
        return content;
    }
	
	public void uploadInformePlanificacion(FileUploadEvent event)
	{
		documentoInformePlanificacion = new DocumentosPPC();
		byte[] contenido=event.getFile().getContents();
		documentoInformePlanificacion.setContenidoDocumento(contenido);
		documentoInformePlanificacion.setNombreDocumento(event.getFile().getFileName());
		documentoInformePlanificacion.setExtencionDocumento(".pdf");		
		documentoInformePlanificacion.setTipo("application/pdf");
		documentoInformePlanificacion.setNombreTabla("INFORME PLANIFICACION");
		documentoInformePlanificacion.setIdTabla(proyectosBean.getProyectoRcoa().getId());
		documentoInformePlanificacion.setIdProceso(bandejaTareasBean.getTarea().getProcessInstanceId());
	}
	
	public boolean guardarInformePlanificacion()
	{
		boolean estado = false;		
		try {
			documentosFacade.guardarDocumentoAlfresco(proyectosBean.getProyectoRcoa().getCodigoUnicoAmbiental(), "PARTICIPACION CIUDADANA", bandejaTareasBean.getTarea().getProcessInstanceId(), documentoInformePlanificacion, TipoDocumentoSistema.RCOA_PPC_INFORME_PLANIFICACION);
			estado=true;
		} catch (ServiceException | CmisAlfrescoException e) {				
			estado=false;
		}
		return estado;
	}
	
	public void completarTarea(){
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			
			if(documentoInformePlanificacion==null || documentoInformePlanificacion.getContenidoDocumento()==null)
			{
				JsfUtil.addMessageError("Adjuntar el informe de planificación.");
				return;
			}
			if(!guardarInformePlanificacion())
			{                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              
				JsfUtil.addMessageError("Error al guardar el archivo informe de planificación.");
				return;
			}			
			
			List<FacilitadorPPC> listaFacilitadores = new ArrayList<FacilitadorPPC>();
			if(numeroFacilitadores>1)
			{
				params.put("variosFacilitadores", true);				
				//enviar los facilitadores menos el principal
				listaFacilitadores=facilitadorFacade.facilitadores(proyectosBean.getProyectoRcoa());
				String[] facilitadoresLista = new String[listaFacilitadores.size()];				
				Integer cont = 0;
				for (FacilitadorPPC usuario : listaFacilitadores) {
					facilitadoresLista[cont++] = usuario.getUsuario().getNombre();
				}
				params.put("listaFacilitadores", facilitadoresLista);
			}
			else
				params.put("variosFacilitadores", false);
            
            
            procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getTarea().getProcessInstanceId(), params);
            Map<String, Object> data = new ConcurrentHashMap<String, Object>();
            procesoFacade.aprobarTarea(loginBean.getUsuario(), bandejaTareasBean.getTarea().getTaskId(),  bandejaTareasBean.getProcessId(), data);
            
            if(numeroFacilitadores>1)
            	enviarNotificacion(listaFacilitadores);
            
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
			JsfUtil.redirectTo(JsfUtil.NAVIGATION_TO_BANDEJA);            
		} catch (Exception e) {					
			LOGGER.error("Error al finalizar tarea", e);
		}
	}
	
	public void enviarNotificacion(List<FacilitadorPPC> listaFacilitadores) {		
		try {
			for (FacilitadorPPC faciPPC : listaFacilitadores) {
				Usuario facilitador = faciPPC.getUsuario();
				Object[] parametrosCorreoFP = new Object[] {facilitador.getPersona().getTipoTratos().getNombre(), 
						facilitador.getPersona().getNombre(),proyectosBean.getProyectoRcoa().getNombreProyecto(), 
						proyectosBean.getProyectoRcoa().getCodigoUnicoAmbiental() };
				
				String notificacion = mensajeNotificacionFacade.recuperarValorMensajeNotificacion(
								"bodyNotificacionCoordinadorFacilitadoresPPC",
								parametrosCorreoFP);
				
				Email.sendEmail(facilitador, "Regularización Ambiental Nacional", notificacion, tramite, loginBean.getUsuario());
				Thread.sleep(2000);
			}
		} catch (Exception e) {					
			e.printStackTrace();
		}
	}

}
