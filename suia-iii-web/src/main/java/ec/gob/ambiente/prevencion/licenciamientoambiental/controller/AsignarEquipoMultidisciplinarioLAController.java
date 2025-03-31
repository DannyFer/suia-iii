package ec.gob.ambiente.prevencion.licenciamientoambiental.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.jbpm.process.audit.ProcessInstanceLog;
import org.kie.api.task.model.TaskSummary;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.prevencion.licenciamientoambiental.bean.AsignarEquipoMultidisciplinarioLABean;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.administracion.service.RolService;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.comparator.OrdenarTareaPorEstadoComparator;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.Rol;
import ec.gob.ambiente.suia.domain.RolUsuario;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.dto.ResumeTarea;
import ec.gob.ambiente.suia.dto.ResumenInstanciaProceso;
import ec.gob.ambiente.suia.dto.Tarea;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.ProyectoLicenciaAmbientalFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.service.UsuarioServiceBean;
import ec.gob.ambiente.suia.utils.BeanLocator;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.ConvertidorObjetosDominioUtil;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.webservice.facade.AsignarTareaFacade;
import ec.gob.ambiente.suia.webservicesclientes.facade.JbpmSuiaCustomServicesFacade;

@RequestScoped
@ManagedBean
public class AsignarEquipoMultidisciplinarioLAController implements Serializable {

    private static final long serialVersionUID = -3524848369863L;
    private static final Logger LOGGER = Logger
            .getLogger(AsignarEquipoMultidisciplinarioLAController.class);
    @EJB
    private ProcesoFacade procesoFacade;

    @EJB
    private AreaFacade areaFacade;

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

    @Getter
    @Setter
    @ManagedProperty(value = "#{asignarEquipoMultidisciplinarioLABean}")
    private AsignarEquipoMultidisciplinarioLABean asignarEquipoMultidisciplinarioLABean;

    @EJB
    private ProyectoLicenciaAmbientalFacade proyectoLicenciaAmbientalFacade;

    @EJB
   	private CrudServiceBean crudServiceBean;
    
    @EJB
    private AsignarTareaFacade asignarTareaFacade;
    
    @EJB
    private UsuarioServiceBean usuarioServiceBean;
    
    @EJB
    private RolService rolService;

    public String asignarEquipoMultidiciplinario() throws ServiceException {
    	try {
    		Map<String, Object> params = new ConcurrentHashMap<String, Object>();

    		Map<String, Object> variables = procesoFacade
    				.recuperarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getTarea()
    						.getProcessInstanceId());

    		 Integer codigoSector=Integer.valueOf((String)variables.get("codigoSector"));
    		Integer idProyecto = Integer.parseInt((String) variables
    				.get(Constantes.ID_PROYECTO));

    		ProyectoLicenciamientoAmbiental proyecto = proyectoLicenciaAmbientalFacade.getProyectoPorId(idProyecto);
    		Area areaProyecto = areaFacade.getAreaFull(proyecto.getAreaResponsable().getId());

    		List<String> seleccionadas = Arrays
    				.asList(asignarEquipoMultidisciplinarioLABean
    						.getAreasSeleccionadas());

    		String[] usuariosA = new String[seleccionadas.size()];
            ProcessInstanceLog processInstanceLog=procesoFacade.getProcessInstanceLog(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getProcessInstanceId());
            String tramite=(String) variables.get("codigoProyecto");
            ResumenInstanciaProceso resumenInstanciaProceso=new ResumenInstanciaProceso(processInstanceLog, tramite);
           
    		if(codigoSector==2){
    			this.asignacionCoordinadoresAreaMinera(params, seleccionadas, usuariosA,variables,proyecto);
    		}else{
    			this.asignacionCoordinadoresAreas(params, seleccionadas, usuariosA);
    		}

    		if (seleccionadas.contains("tecnicoGeneral")) {

    			if (!areaProyecto.getTipoArea().getSiglas()
    					.equalsIgnoreCase(Constantes.SIGLAS_TIPO_AREA_PC)) {
    				Usuario coordinador = null;
    				try {
    					coordinador = areaFacade
    							.getCoordinadorPorArea(areaProyecto);
    				} catch (ServiceException e) {
    					LOGGER.error("Error al recuperar el coordinador del área.", e);
    					return "";
    				}
    				if (coordinador != null) {
    					params.put("requiereAmbiental", true);
    					params.put("u_CoordinadorGeneral", coordinador.getNombre());
    					usuariosA= new String[]{"General"};
    				} else {
    					JsfUtil.addMessageError("Error al recuperar el coordinador del área.");
    					return "";
    				}
    			} else {
    				JsfUtil.addMessageError("Error al procesar los datos.");
    				return "";
    			}

    		}
    		params.put("listaAreasEquipo", usuariosA);
    		try {
    			procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean
    					.getTarea().getProcessInstanceId(), params);
    			Map<String, Object> data = new ConcurrentHashMap<String, Object>();
    			procesoFacade.aprobarTarea(loginBean.getUsuario(),
    					bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), data);
    			JsfUtil.addMessageInfo("Se realizó correctamente la operación.");
    			procesoFacade.envioSeguimientoLicenciaAmbiental(loginBean.getUsuario(), bandejaTareasBean.getProcessId());
    			return JsfUtil.actionNavigateTo("/bandeja/bandejaTareas.jsf");
    		} catch (JbpmException e) {
    			LOGGER.error(e);
    			JsfUtil.addMessageError("Error al realizar la operación.");
    		}
    	} catch (JbpmException e) {
    		JsfUtil.addMessageError("Error al recuperar los datos del proyecto.");
    		LOGGER.error("Error al recuperar los datos del proyecto.", e);
    		return "";
    	}

    	return "";

    }
    
    public void asignacionCoordinadoresAreas(Map<String,Object> params,List<String> seleccionadas,String [] usuariosA){

    	Integer iterador = 0;

    	for (String area : seleccionadas) {

    		String areaActual = "";

    		if(area != null)
    			areaActual = area;

    		if (!areaActual.equals("tecnicoGeneral")) {
    			try {
    				areaActual = area.substring(7, area.length());
    				Usuario coordinador = areaFacade
    						.getDirectorPlantaCentralPorArea("role.pc.coordinador." + areaActual);
    				if (coordinador != null) {
    					params.put("requiere" + areaActual, true);
    					params.put("u_Coordinador" + areaActual, coordinador.getNombre());
    					usuariosA[iterador++] = areaActual;
    				} else {
    					JsfUtil.addMessageError("Error al recuperar técnico del área Biótica");

    				}

    			} catch (Exception e) {
    				LOGGER.error(e);
    				JsfUtil.addMessageError("Error al recuperar el coordinador del área" + " " + areaActual);

    			}
    		}
    	}
    }
    
    public boolean asignacionCoordinadoresAreaMinera(Map<String,Object> params,List<String> seleccionadas,String[] usuariosA,Map<String,Object> variables,ProyectoLicenciamientoAmbiental proyecto) throws ServiceException{
        
        String uCoordinador=(String)(variables.get("u_Coordinador"));
       
        Map<String, Object> parametrosQueryUsuario = new HashMap<String, Object>();
        parametrosQueryUsuario.put("name", uCoordinador);
        Usuario uCoordActual=crudServiceBean.findByNamedQuerySingleResult(Usuario.FIND_BY_USER,parametrosQueryUsuario);
        List<Rol> roles=uCoordActual!=null?rolService.listarPorUsuario(uCoordActual):new ArrayList<Rol>();   
       
        Area areaProyecto = areaFacade.getAreaFull(proyecto.getAreaResponsable().getId());
        Integer iterador = 0;
        for (String area : seleccionadas) {
        	String areaBpm=area.replace("tecnico", "");
        	area ="role.pc.coordinador."+areaBpm;
            
        	String areaActual = area.replace("tecnico", "");
            String rolSector=Constantes.getRoleAreaName(area);           
            
            Usuario coordinadorArea = null;

            for (Rol rol : roles) {               
                if(rol.getNombre().equals(rolSector)){
                    coordinadorArea=uCoordActual;
                    break;
                }
            }
            if(coordinadorArea==null){
                List<Usuario> coordinadores=asignarTareaFacade.getCargaLaboralPorUsuariosV2(rolSector,areaProyecto.getAreaName());
                coordinadorArea=coordinadores.isEmpty()?null:coordinadores.get(0);
            }
           
            if (coordinadorArea != null) {
                params.put("requiere" + areaBpm, true);
                params.put("u_Coordinador" + areaBpm, coordinadorArea.getNombre());
                usuariosA[iterador++] = areaBpm;
            } else {
                JsfUtil.addMessageError("Error al recuperar técnico del área "+areaActual);
                return false;
            }
        }       
        return true;
    }   
    
    public List<Tarea>tareas(ResumenInstanciaProceso resumenInstanciaProceso) {

    	List<Tarea>tareasProceso = null;
    	try {
    		List<Tarea> tareas = new ArrayList<Tarea>();
    		if (resumenInstanciaProceso.getEstadoString().equals("Completado")) {
    			List<ResumeTarea> resumeTareas = BeanLocator.getInstance(JbpmSuiaCustomServicesFacade.class)
    					.getResumenTareas(resumenInstanciaProceso.getProcessInstanceId());

    			for (ResumeTarea resumeTarea : resumeTareas) {

    				Tarea tarea = new Tarea();
    				if (!(resumeTarea.getStatus().equals("Exited") || resumeTarea.getStatus().equals("Created") || resumeTarea.getStatus().equals("Ready"))){
    					ConvertidorObjetosDominioUtil.convertirBamTaskSummaryATarea(resumeTarea, tarea);
    					if(!tareas.contains(tarea))
    						tareas.add(tarea);
    				}
    			}
    			Collections.sort(tareas, new OrdenarTareaPorEstadoComparator());
    			tareasProceso = tareas;

    		} else {
    			List<TaskSummary> taskSummaries = procesoFacade.getTaskBySelectFlow(JsfUtil.getLoggedUser(),
    					resumenInstanciaProceso.getProcessInstanceId());
    			int longitud = taskSummaries.size();
    			for (int i = 0; i < longitud; i++) {
    				Tarea tarea = new Tarea();
    				ConvertidorObjetosDominioUtil.convertirTaskSummaryATarea(taskSummaries.get(i), tarea);
    				if(!tareas.contains(tarea))
    					tareas.add(tarea);
    			}

    			Collections.sort(tareas, new OrdenarTareaPorEstadoComparator());
    			tareasProceso = tareas;

    		}

    	} catch (Exception e) {
    		e.getMessage();
    		JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
    	}
    	return tareasProceso;
    }


}