package ec.gob.ambiente.rcoa.sustanciasQuimicasImportacion.controllers;

import java.io.ByteArrayInputStream;
import java.util.Date;
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
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.DetalleSolicitudImportacionRSQFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.DocumentosSustanciasQuimicasRcoaFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.SolicitudImportacionRSQFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.DetalleSolicitudImportacionRSQ;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.DocumentosSustanciasQuimicasRcoa;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.SolicitudImportacionRSQ;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.PlantillaReporteFacade;
import ec.gob.ambiente.suia.administracion.facade.ContactoFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class DescargarAutorizacionAnulacionImportacionController {
	
	private final Logger LOG = Logger.getLogger(DescargarAutorizacionAnulacionImportacionController.class);
	
	@EJB
	private ProcesoFacade procesoFacade;
	@EJB
	private UsuarioFacade usuarioFacade;
	@EJB
	private SolicitudImportacionRSQFacade solicitudImportacionRSQFacade;
	@EJB
	private DetalleSolicitudImportacionRSQFacade detalleSolicitudImportacionRSQFacade;
	@EJB
	private ContactoFacade contactoFacade;
	@EJB
	private OrganizacionFacade organizacionFacade;
	@EJB
	private PlantillaReporteFacade plantillaReporteFacade;
	@EJB
	private DocumentosSustanciasQuimicasRcoaFacade   documentoSustanciasQuimicasFacade;
	@EJB
	private SecuenciasFacade secuenciasFacade;
	
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
    
    private Map<String, Object> variables;	
    
    @Getter
	@Setter
	private String tramite;
    
    private boolean autorizacion, documentoDescargado;
    
    @Getter
    @Setter
    private String nombreFormulario;
    
    @Getter
    @Setter
    private DocumentosSustanciasQuimicasRcoa documento;
    
    @Getter
	@Setter
	private String codigo, codigoRSQ;
    
    @Getter
	@Setter
	private SolicitudImportacionRSQ solicitud;
	
	@Getter
	@Setter
	private DetalleSolicitudImportacionRSQ detalle;
    
    @PostConstruct
	public void init(){
		try {
						
			variables=procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
			tramite=(String)variables.get(Constantes.VARIABLE_PROCESO_TRAMITE);
			
			String autorizacionAux = (String)variables.get("emision");
			autorizacion= Boolean.valueOf(autorizacionAux);
			
			solicitud = solicitudImportacionRSQFacade.buscarPorTramite(tramite);
			
			codigoRSQ = solicitud.getRegistroSustanciaQuimica().getNumeroAplicacion();
			codigo = tramite;
			
//			documento = documentoSustanciasQuimicasFacade.findById(solicitud.getDocumentosSustanciasQuimicasRcoa().getId());
			
			if(autorizacion){
				List<DocumentosSustanciasQuimicasRcoa> documentoList = documentoSustanciasQuimicasFacade.documentoXIdTablaIdTipoDoc(solicitud.getId(), SolicitudImportacionRSQ.class.getName(), TipoDocumentoSistema.RCOA_RSQ_AUTORIZACION_IMPORTACION);
				if(documentoList != null && !documentoList.isEmpty()){
					documento = documentoList.get(0);
				}
				
				nombreFormulario = "Descargar Autorización de Importación";
			}else{
				List<DocumentosSustanciasQuimicasRcoa> documentoList = documentoSustanciasQuimicasFacade.documentoXIdTablaIdTipoDoc(solicitud.getId(), SolicitudImportacionRSQ.class.getName(), TipoDocumentoSistema.RCOA_RSQ_ANULACION_IMPORTACION);
				if(documentoList != null && !documentoList.isEmpty()){
					documento = documentoList.get(0);
				}
				nombreFormulario = "Descargar el documento de Anulación de Importación";
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}		
    }
    
    public StreamedContent descargarDocumento() throws Exception {		
		
		DefaultStreamedContent content = new DefaultStreamedContent();
		if (documento != null) {
			if (documento.getContenidoDocumento() == null) {
				documento.setContenidoDocumento(documentoSustanciasQuimicasFacade
						.descargar(documento.getIdAlfresco()));
			}
			content = new DefaultStreamedContent(new ByteArrayInputStream(
					documento.getContenidoDocumento()), documento.getExtesion());
			content.setName(documento.getNombre());
		}
		
		documentoDescargado = true;
		return content;
	}
    
    public void finalizar(){
		try {			
			
			if(documento.getProcessinstanceid() == null){
				Long idProceso = bandejaTareasBean.getProcessId();
				
				documento.setProcessinstanceid(idProceso.intValue());
				documentoSustanciasQuimicasFacade.save(documento, JsfUtil.getLoggedUser());	
			}					
			
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
				
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	public void cerrar(){
		if(documentoDescargado){
			JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
		}else{
			JsfUtil.addMessageError("Debe descargar el documento Oficio para poder Finalizar.");
		}		
	}
	
	public void cancelar(){
		if(documentoDescargado){
			JsfUtil.addMessageError("Su documento fue descargado, terminando el trámite.");
			JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
		}else{
			JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
		}		
	}

}
