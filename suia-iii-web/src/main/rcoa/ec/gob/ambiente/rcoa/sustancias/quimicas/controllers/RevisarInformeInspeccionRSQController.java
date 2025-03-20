package ec.gob.ambiente.rcoa.sustancias.quimicas.controllers;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.rcoa.enums.TipoInformeOficioEnum;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.DocumentosRSQFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.InformesOficiosRSQFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.ObservacionesRSQFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.RegistroSustanciaQuimicaFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.DocumentosSustanciasQuimicasRcoa;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.InformeOficioRSQ;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.ObservacionesFormulariosRSQ;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.RegistroSustanciaQuimica;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.AreaUsuario;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;

@ManagedBean
@ViewScoped
public class RevisarInformeInspeccionRSQController implements Serializable {

	private static final long serialVersionUID = -1L;

	private static final Logger LOG = Logger.getLogger(RevisarInformeInspeccionRSQController.class);
	
	@EJB
	private DocumentosRSQFacade documentosRSQFacade;
	
	@EJB
	private InformesOficiosRSQFacade informesOficiosRSQFacade;	
	
    @EJB(lookup = Constantes.JBPM_EJB_TASK_BEAN)
    private TaskBeanFacade taskBeanFacade;
    
    @EJB
    private ObservacionesRSQFacade observacionesRSQFacade;
   
	@EJB
	private ProcesoFacade procesoFacade;
	
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	    
	@EJB
	private RegistroSustanciaQuimicaFacade registroSustanciaQuimicaFacade;  
	
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

	
	
	@Getter
	@Setter
	private InformeOficioRSQ informe;
		
    @Getter
    @Setter
    private DocumentosSustanciasQuimicasRcoa documentoInforme;
	
	Map<String, Object> variables;
		
	private String varTramite;
      	
	private ProyectoLicenciaCoa proyectoLicenciaCoa;
	
	private RegistroSustanciaQuimica registroSustanciaQuimica;
	
	@Setter
	@Getter
	private Boolean requiereCorreccionesInforme;
	
	private Integer numeroRevision;
	
	@Setter
	@Getter
	private String paramUsuarioApoyo;
	
	@Getter
	@Setter
	private boolean descargado = false;

	@PostConstruct
	public void init() {
		try {			
			paramUsuarioApoyo=JsfUtil.getRequestParameter("area");
			cargarDatosTarea();
			cargarDatosProyecto();	
			
		} catch (Exception e) {
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos RevisarInformeInspeccionRSQController.");
		}
	}
	
	private void cargarDatosTarea() throws ServiceException{

		try {
			variables=procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
			varTramite=(String)variables.get("tramite");
			numeroRevision=variables.containsKey("numero_revision")?(Integer.valueOf((String)variables.get("numero_revision"))):1;
		} catch (JbpmException e) {
			LOG.error("Error al recuperar variables numero_observaciones "+e.getCause()+" "+e.getMessage());
		}
	}
	
	private void cargarDatosProyecto(){		
		proyectoLicenciaCoa=proyectoLicenciaCoaFacade.buscarProyecto(varTramite);
		registroSustanciaQuimica=registroSustanciaQuimicaFacade.obtenerRegistroPorProyecto(proyectoLicenciaCoa);
		if(registroSustanciaQuimica!=null) {
			Usuario usuario=usuarioFacade.buscarUsuario(paramUsuarioApoyo);
			
			Area areaUsuario = new Area();
			if(usuario.getListaAreaUsuario() != null && usuario.getListaAreaUsuario().size() == 1){
				areaUsuario = usuario.getListaAreaUsuario().get(0).getArea();
				informe = informesOficiosRSQFacade.obtenerPorRSQArea(registroSustanciaQuimica,TipoInformeOficioEnum.INFORME_INSPECCION,numeroRevision,areaUsuario);
			}else{
				for(AreaUsuario areasUs : usuario.getListaAreaUsuario()){
					informe = informesOficiosRSQFacade.obtenerPorRSQArea(registroSustanciaQuimica,TipoInformeOficioEnum.INFORME_INSPECCION,numeroRevision,areasUs.getArea());
					if(informe != null){
						break;
					}
				}
			}
						
			documentoInforme=documentosRSQFacade.obtenerDocumentoPorTipo(TipoDocumentoSistema.RCOA_RSQ_INFORME_INSPECCION_APROBADO_OBSERVADO, InformeOficioRSQ.class.getSimpleName(),informe.getId());
		}
	}
	
	public StreamedContent descargarDocumento() {
		try {
			
			if(documentoInforme.getContenidoDocumento()==null) {
				documentoInforme.setContenidoDocumento(documentosRSQFacade.descargar(documentoInforme.getIdAlfresco()));
			}
            byte[] byteFile = documentoInforme.getContenidoDocumento();
            if (byteFile != null) {
                InputStream is = new ByteArrayInputStream(byteFile);
                descargado = true;
                return new DefaultStreamedContent(is, "application/pdf", documentoInforme.getNombre());
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
	
	private boolean validarObservaciones() {
		List<ObservacionesFormulariosRSQ> lista=new ArrayList<ObservacionesFormulariosRSQ>();
		try {
			lista=observacionesRSQFacade.listarPorIdClaseNombreClaseNoCorregidas(informe.getId(), InformeOficioRSQ.class.getSimpleName());
			if(requiereCorreccionesInforme) {
				if(lista==null || lista.isEmpty()) {
					JsfUtil.addMessageWarning("Debe agregar al menos 1 Observación");
					return false;
				}
			}else {
				
				if(lista!=null && !lista.isEmpty()) {
					JsfUtil.addMessageWarning("Existen Observaciones Sin Corregir");
					return false;
				}
			}
			
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
	}
	
	public void enviar()
	{
		if(!descargado){
			JsfUtil.addMessageWarning("Debe descargar el documento de informe técnico.");
			return;
		}			
		
		if(validarObservaciones()) {
			try {
				
				Map<String, Object> params=new HashMap<>();							
				params.put("observaciones_informe_apoyo",requiereCorreccionesInforme);
				procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getProcessId(), params);
				procesoFacade.aprobarTarea(loginBean.getUsuario(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
				JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
		        JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
			} catch (/*Jbpm*/Exception e) {
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
				e.printStackTrace();
			}
		}
		
	}
}