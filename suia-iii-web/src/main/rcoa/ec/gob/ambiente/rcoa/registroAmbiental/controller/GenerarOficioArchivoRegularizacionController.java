package ec.gob.ambiente.rcoa.registroAmbiental.controller;

import java.io.ByteArrayInputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.rcoa.facade.DocumentosRegistroAmbientalFacade;
import ec.gob.ambiente.rcoa.facade.OficioPronunciamientoPPCFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.facade.RegistroAmbientalCoaFacade;
import ec.gob.ambiente.rcoa.model.DocumentoRegistroAmbiental;
import ec.gob.ambiente.rcoa.model.OficioPronunciamientoPPC;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.RegistroAmbientalRcoa;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.BeanLocator;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class GenerarOficioArchivoRegularizacionController {
	
	@EJB
	private ProcesoFacade procesoFacade;
	@EJB
	private RegistroAmbientalCoaFacade registroAmbientalCoaFacade;
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	@EJB
	private DocumentosRegistroAmbientalFacade documentoRegistroAmbientalFacade;
	@EJB
	private OficioPronunciamientoPPCFacade oficioPronunciamientoPPCFacade;
	@EJB
	private AreaFacade areaFacade;
	
	private Map<String, Object> variables;
	
	private String tramite;
	
	@Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;
	
	@Getter
	@Setter
	private RegistroAmbientalRcoa registroAmbiental;
	
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyecto;
	
	@Getter
	@Setter
	private String urlReporte, nombreReporte;
	
	@Getter
	@Setter
	private byte[] archivoReporte;
	
	@Getter
	@Setter
	private OficioPronunciamientoPPC oficioPronunciamientoPPC;
		
	@Getter
	@Setter
	private Area areaResponsable;
	
	@Getter
	@Setter
	private DocumentoRegistroAmbiental documentoOficio;
	
	@Setter
	@Getter
	private StreamedContent contentOficioArchivo;
	
	@Getter
	@Setter
	private boolean guardado = false;
	
	@PostConstruct
	public void init(){
		try {
			
			variables=procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
			tramite=(String)variables.get(Constantes.VARIABLE_PROCESO_TRAMITE);
			proyecto = proyectoLicenciaCoaFacade.buscarProyecto(tramite);
					
			registroAmbiental = registroAmbientalCoaFacade.obtenerRegistroAmbientalPorProyecto(proyecto);
			areaResponsable = proyecto.getAreaResponsable();
			
			oficioPronunciamientoPPC = new OficioPronunciamientoPPC();
			
			List<OficioPronunciamientoPPC> listaPronunciamiento = oficioPronunciamientoPPCFacade.buscarPorRegistro(registroAmbiental.getId());
			
			if(listaPronunciamiento != null && !listaPronunciamiento.isEmpty()){
				oficioPronunciamientoPPC = listaPronunciamiento.get(0);
			}else{
				oficioPronunciamientoPPC.setRegistroAmbiental(registroAmbiental);
				oficioPronunciamientoPPC.setArea(areaResponsable);
				oficioPronunciamientoPPC.setTipo(2);
				oficioPronunciamientoPPC = oficioPronunciamientoPPCFacade.guardiarOficioArchivo(oficioPronunciamientoPPC);
			}
						
			generarDocumento();
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}		
	
	public void generarDocumento(){
		try {
						
			GenerarDocumentoArchivoController generarDocumento = (GenerarDocumentoArchivoController)BeanLocator.getInstance(GenerarDocumentoArchivoController.class);
			oficioPronunciamientoPPC = generarDocumento.generarResolucionRegistroAmbiental(registroAmbiental, oficioPronunciamientoPPC,true);
			urlReporte = oficioPronunciamientoPPC.getUrl();			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public StreamedContent getStream(String name, byte[] fileContent) throws Exception {
		DefaultStreamedContent content = new DefaultStreamedContent();
		if (fileContent != null) {
			content = new DefaultStreamedContent(new ByteArrayInputStream(fileContent), "application/octet-stream");
			content.setName(name);
		}
		return content;
	}
	
	public void guardar(){
		try {
			
			oficioPronunciamientoPPCFacade.guardiarOficioArchivo(oficioPronunciamientoPPC);
			
			generarDocumento();
			
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
			
			guardado = true;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void completarTarea(){
		try {
			oficioPronunciamientoPPC.setFechaElaboracion(new Date());
			oficioPronunciamientoPPCFacade.guardiarOficioArchivo(oficioPronunciamientoPPC);
			
			Map<String, Object> parametros = new HashMap<>();
			
			parametros.put("director", oficioPronunciamientoPPC.getDirector().getNombre());
			
			procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(),bandejaTareasBean.getTarea().getTaskId(),bandejaTareasBean.getTarea().getProcessInstanceId(), null);
						
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
			JsfUtil.redirectTo(JsfUtil.NAVIGATION_TO_BANDEJA);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	

}
