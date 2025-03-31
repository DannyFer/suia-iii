package ec.gob.ambiente.suia.bandeja;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.jbpm.process.audit.ProcessInstanceLog;
import org.kie.api.task.model.TaskSummary;

import ec.fugu.ambiente.consultoring.projects.Task;
import ec.fugu.ambiente.consultoring.retasking.ProyectoLicenciaVo;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.hyd.service.TaskSummaryDto;
import ec.gob.ambiente.integracion.facade.IntegracionFacade;
import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.suia.builders.TaskSummaryCustomBuilder;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.dto.TaskSummaryCustom;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;

/**
 * @author carlos.pupo
 * @version Revision: 1.0
 *          <p>
 *          [Autor: carlos.pupo, Fecha: 13/03/2015]
 *          </p>
 */
@Stateless
public class BandejaFacade {

	private static final Logger LOG = Logger.getLogger(BandejaFacade.class);

	private Map<Long, ProcessInstanceLog> processInstanceLogList;
	private Map<Long, Map<String, Object>> processInstanceVariable;

	@EJB(lookup = Constantes.JBPM_EJB_TASK_BEAN)
	private TaskBeanFacade taskBeanFacade;

	@EJB
	private ProcesoFacade procesoFacade;

	@EJB
	private IntegracionFacade integracionFacade;
	
	@EJB
	private UsuarioFacade usuarioFacade;
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	
	public String dblinkBpmsSuiaiii=Constantes.getDblinkBpmsSuiaiii();
	
	public String dblinkBpmsHidrocarburos=Constantes.getDblinkBpmsHyD();
	
	public String dblinkIVCategorias=Constantes.getDblinkSuiaVerde();
	
	public Long countTareasIVCategorias(Map<String,Object> filters, Usuario usuario, String actorId)
	{	
		Long catidadTareasIVC = 0L;
		
		String tramite="";
		String flujo="";
		String actividad="";
		
		if (filters != null) {
            if (filters.containsKey("procedure")) {
            	tramite = filters.get("procedure").toString();
            }
            if (filters.containsKey("processName")) {
            	flujo = filters.get("processName").toString();
            }
            if (filters.containsKey("taskNameHuman")) {
            	actividad = filters.get("taskNameHuman").toString();
            }
        }	
		
		try {
			if (Constantes.getAppIntegrationSuiaEnabled()) {
				catidadTareasIVC = integracionFacade.
				getCountTaskByUserIVCategorias(tramite, flujo, actividad, actorId, usuario.getNombre(), usuario.getPasswordSha1Base64());
				
			}
		} catch (Exception ex) {
			LOG.error("Ocurrió un error recuperando el contador de tareas del sistema iv categorias", ex);
		}
		return catidadTareasIVC;
	}
	
	@SuppressWarnings("unchecked")
	public Integer countTareasHidrocarburosPorRoles(Map<String,Object> filters, Usuario usuario, String actorId)
	{	
		Integer countrows = 0;		
		String rolesIVCat="";
		String sqlPeoplePotowners="";
		String sqlfiltro = "";
		
		String sqlRolesIVCategorias="select * "
				+ "from dblink('"+dblinkIVCategorias+"',' "
				+ "select rolid "
				+ "from "
				+ "rolusuario "
				+ "where "
				+ "usuarioid =(select id from usuario where nombreusuario =''"+usuario.getNombre()+"'')') as ( "
				+ "count bigint)";
		
		Query qRolesIVCategorias = crudServiceBean.getEntityManager().createNativeQuery(sqlRolesIVCategorias);		
		List<Object[]> resultList = (List<Object[]>) qRolesIVCategorias.getResultList();	
		
		if (resultList.size() > 0) {
			for (int i = 0; i < resultList.size(); i++) {
				Object rolId = (Object) resultList.get(i);
				rolesIVCat+= (rolesIVCat.equals("")) ? "''"+rolId.toString()+"''" : ",''"+rolId.toString()+"''";
			}
			sqlPeoplePotowners=" t.id in (select distinct(task_id) from peopleassignments_potowners where entity_id in ("+rolesIVCat+")) and ";
		}else {
			return 0;
		}		
		
		if (filters != null) {
            if (filters.containsKey("procedure")) {
            	sqlfiltro = sqlfiltro + "and UPPER(v.value) like ''%"+filters.get("procedure").toString().toUpperCase()+"%'' ";
            }
            if (filters.containsKey("processName")) {
            	sqlfiltro = sqlfiltro + "and UPPER(p.processname) like ''%"+filters.get("processName").toString().toUpperCase()+"%'' ";
            }
            if (filters.containsKey("taskNameHuman")) {
            	sqlfiltro = sqlfiltro + "and UPPER(i.shorttext) like ''%"+filters.get("taskNameHuman").toString().toUpperCase()+"%'' ";
            }
        }
		
		String sql="select * "
				+ "from dblink('"+dblinkBpmsHidrocarburos+"',' "
				+ "select count(*) "
				+ "from "
				+ "task t,variableinstancelog v, processinstancelog p ,i18ntext i "
				+ "where "
				+ "v.processinstanceid=t.processinstanceid and "
				+ "p.processinstanceid=t.processinstanceid and "
				+ "i.task_names_id=t.id and "
				+ "t.status in(''Ready'',''Reserved'',''InProgress'') and "
				+ sqlPeoplePotowners
				+ " v.variableid=''codigoProyecto'' "+sqlfiltro+"') as ( "
				+ "count bigint)";
		
		Query q = crudServiceBean.getEntityManager().createNativeQuery(sql);		
		if(q.getResultList().size()>0)
			countrows=((BigInteger) q.getSingleResult()).intValue();
		
		return countrows;
	}
	
	public Integer countTareasHidrocarburosPorUsuario(Map<String,Object> filters, Usuario usuario, String actorId)
	{	
		Integer countrows = 0;		
		String sqlfiltro = "";		
		
		if (filters != null) {
            if (filters.containsKey("procedure")) {
            	sqlfiltro = sqlfiltro + "and UPPER(v.value) like ''%"+filters.get("procedure").toString().toUpperCase()+"%'' ";
            }
            if (filters.containsKey("processName")) {
            	sqlfiltro = sqlfiltro + "and UPPER(p.processname) like ''%"+filters.get("processName").toString().toUpperCase()+"%'' ";
            }
            if (filters.containsKey("taskNameHuman")) {
            	sqlfiltro = sqlfiltro + "and UPPER(i.shorttext) like ''%"+filters.get("taskNameHuman").toString().toUpperCase()+"%'' ";
            }
        }
		
		String sql="select * "
				+ "from dblink('"+dblinkBpmsHidrocarburos+"',' "
				+ "select count(*) "
				+ "from "
				+ "task t,variableinstancelog v, processinstancelog p ,i18ntext i "
				+ "where "
				+ "v.processinstanceid=t.processinstanceid and "
				+ "p.processinstanceid=t.processinstanceid and "
				+ "i.task_names_id=t.id and "
				+ "(t.actualowner_id =''"+usuario.getNombre()+"'' or t.createdby_id=''"+usuario.getNombre()+"'') and "
				+ "t.status in(''Ready'',''Reserved'',''InProgress'') and "
				+ " v.variableid=''codigoProyecto'' "+sqlfiltro+"') as ( "
				+ "count bigint)";
		
		Query q = crudServiceBean.getEntityManager().createNativeQuery(sql);		
		if(q.getResultList().size()>0)
			countrows=((BigInteger) q.getSingleResult()).intValue();
		
		return countrows;
	}
	
	public Integer countTareasSuiaIII(Map<String,Object> filters, Usuario usuario, String actorId)
	{	
		Integer countrows = 0;		
		
		String sqlfiltro = "";
		if (filters != null) {
            if (filters.containsKey("procedure")) {
            	sqlfiltro = sqlfiltro + "and UPPER(v.value) like ''%"+filters.get("procedure").toString().toUpperCase()+"%'' ";
            }
            if (filters.containsKey("processName")) {
            	sqlfiltro = sqlfiltro + "and UPPER(p.processname) like ''%"+filters.get("processName").toString().toUpperCase()+"%'' ";
            }
            if (filters.containsKey("taskNameHuman")) {
            	sqlfiltro = sqlfiltro + "and UPPER(i.shorttext) like ''%"+filters.get("taskNameHuman").toString().toUpperCase()+"%'' ";
            }
        }
		
		sqlfiltro = sqlfiltro + "and p.externalid like ''%suia-iii%'' ";
		
		String sql="select * "
				+ "from dblink('"+dblinkBpmsSuiaiii+"',' "
				+ "select count(distinct t.id) "
				+ "from "
				+ "task t,variableinstancelog v, processinstancelog p ,i18ntext i "
				+ "where "
				+ "v.processinstanceid=t.processinstanceid and "
				+ "p.processinstanceid=t.processinstanceid and "
				+ "i.task_names_id=t.id and "
				+ "(t.actualowner_id =''"+usuario.getNombre()+"'') and "
				+ "t.status in(''Reserved'',''InProgress'') and "
				+ "v.variableid=''tramite'' "+sqlfiltro+"') as ( "
				+ "count bigint)";
		
		Query q = crudServiceBean.getEntityManager().createNativeQuery(sql);		
		if(q.getResultList().size()>0) 
			countrows=((BigInteger) q.getSingleResult()).intValue();
		
		return countrows;
	}
	
	public List<TaskSummaryCustom> getTasksIVCategorias(int pageSize, int limit, Map<String,Object> filters,String actorId, Usuario usuario) throws Exception {
		List<TaskSummaryCustom> tasksIVCategorias = new ArrayList<TaskSummaryCustom>();
					
		String tramite="";
		String flujo="";
		String actividad="";
		
		if (filters != null) {
            if (filters.containsKey("procedure")) {
            	tramite = filters.get("procedure").toString();
            }
            if (filters.containsKey("processName")) {
            	flujo = filters.get("processName").toString();
            }
            if (filters.containsKey("taskNameHuman")) {
            	actividad = filters.get("taskNameHuman").toString();
            }
        }
		
		try {
			if (Constantes.getAppIntegrationSuiaEnabled()) {
				List<Task> tasksFromSuia = integracionFacade.getTaskByUserFromSuiaPaginado(pageSize, limit,tramite,flujo,actividad, actorId, usuario.getNombre(),
						usuario.getPasswordSha1Base64());
				for (Task task : tasksFromSuia) {
					tasksIVCategorias.add(new TaskSummaryCustomBuilder().fromSuiaII(task).build());
				}
			}
		} catch (Exception ex) {
			LOG.error("Ocurrió un error recuperando tareas del sistema iv categorias", ex);
		}
		return tasksIVCategorias;
	}
	
	public List<TaskSummaryCustom> getTasksHidrocarburosPorUsuario(int pageSize, int limit, Map<String,Object> filters,String actorId, Usuario usuario) throws Exception {

		List<TaskSummaryCustom> tasksHidrocarburos = new ArrayList<TaskSummaryCustom>();	
		processInstanceVariable = new HashMap<Long, Map<String, Object>>();
		processInstanceLogList = new HashMap<Long, ProcessInstanceLog>();	
		
		String sqlfiltro = "";
		
		try {
			if (filters != null) {
	            if (filters.containsKey("procedure")) {
	            	sqlfiltro = sqlfiltro + "and UPPER(v.value) like ''%"+filters.get("procedure").toString().toUpperCase()+"%'' ";
	            }
	            if (filters.containsKey("processName")) {
	            	sqlfiltro = sqlfiltro + "and UPPER(p.processname) like ''%"+filters.get("processName").toString().toUpperCase()+"%'' ";
	            }
	            if (filters.containsKey("taskNameHuman")) {
	            	sqlfiltro = sqlfiltro + "and UPPER(i.shorttext) like ''%"+filters.get("taskNameHuman").toString().toUpperCase()+"%'' ";
	            }
	        }	
			
			Query query = crudServiceBean.getEntityManager().createNativeQuery(
					"select * "
					+ "from dblink('"+dblinkBpmsHidrocarburos+"',' "
					+ "select v.value as tramite,p.processname as flujo, "
					+ "i.shorttext as actividad,t.id as taskid,t.status, "
					+ "p.processinstanceid,t.activationtime,t.processid "
					+ "from "
					+ "task t,variableinstancelog v, processinstancelog p ,i18ntext i "
					+ "where "
					+ "v.processinstanceid=t.processinstanceid and "
					+ "p.processinstanceid=t.processinstanceid and "
					+ "i.task_names_id=t.id and "
					+ "(t.actualowner_id =''"+usuario.getNombre()+"'' or t.createdby_id=''"+usuario.getNombre()+"'') and "
					+ "t.status in(''Ready'',''Reserved'',''InProgress'') and "
					+ "v.variableid=''codigoProyecto'' "+sqlfiltro+" order by 4 desc LIMIT "+limit+" OFFSET "+pageSize+"') as ( "
					+ "tramite character varying(255), "
					+ "flujo character varying(255), "
					+ "actividad character varying(255), "
					+ "taskid bigint, "
					+ "status character varying(255),"
					+ "processinstanceid bigint,"
					+ "activationtime timestamp,"
					+ "processid character varying(255))");			
			
			@SuppressWarnings("unchecked")
			List<Object[]> resultListHidrocarburos = (List<Object[]>) query.getResultList();			
			if (resultListHidrocarburos.size() > 0) {				
				for (int i = 0; i < resultListHidrocarburos.size(); i++) {
					Object[] dataProject = (Object[]) resultListHidrocarburos.get(i);
					TaskSummaryDto taskSummaryDto= new TaskSummaryDto();
					
					Date fechaActivacion=(Date) dataProject[6];
					taskSummaryDto.setActivationTime(fechaActivacion.getTime());
					taskSummaryDto.setFlowName(getNameProcessHidrocarburos(dataProject[7].toString()));
					taskSummaryDto.setName(dataProject[2].toString());
					taskSummaryDto.setProcessId(dataProject[7].toString());
					taskSummaryDto.setProcessInstanceId(Long.valueOf(dataProject[5].toString()));
					taskSummaryDto.setStep(dataProject[0].toString());
					taskSummaryDto.setTaskId(Long.valueOf(dataProject[3].toString()));
					
					tasksHidrocarburos.add(new TaskSummaryCustomBuilder().fromHidrocarburos(taskSummaryDto).build());
				}
			}

		} catch (Exception ex) {
			LOG.error("Ocurrió un error recuperando tareas del usuario en hidrocarburos", ex);
		}
		return tasksHidrocarburos;
	}
	
	@SuppressWarnings("unchecked")
	public List<TaskSummaryCustom> getTasksHidrocarburosPorRoles(int pageSize, int limit, Map<String,Object> filters,String actorId, Usuario usuario) throws Exception {

		List<TaskSummaryCustom> tasksHidrocarburos = new ArrayList<TaskSummaryCustom>();	
		processInstanceVariable = new HashMap<Long, Map<String, Object>>();
		processInstanceLogList = new HashMap<Long, ProcessInstanceLog>();	
		
		String rolesIVCat="";
		String sqlPeoplePotowners="";
		String sqlfiltro = "";
		
		String sqlRolesIVCategorias="select * "
				+ "from dblink('"+dblinkIVCategorias+"',' "
				+ "select rolid "
				+ "from "
				+ "rolusuario "
				+ "where "
				+ "usuarioid =(select id from usuario where nombreusuario =''"+usuario.getNombre()+"'')') as ( "
				+ "count bigint)";
		
		Query qRolesIVCategorias = crudServiceBean.getEntityManager().createNativeQuery(sqlRolesIVCategorias);		
		List<Object[]> resultList = (List<Object[]>) qRolesIVCategorias.getResultList();	
		
		if (resultList.size() > 0) {
			for (int i = 0; i < resultList.size(); i++) {
				Object rolId = (Object) resultList.get(i);
				rolesIVCat+= (rolesIVCat.equals("")) ? "''"+rolId.toString()+"''" : ",''"+rolId.toString()+"''";
			}
			sqlPeoplePotowners=" t.id in (select distinct(task_id) from peopleassignments_potowners where entity_id in ("+rolesIVCat+")) and ";
		}
		
		try {
			if (filters != null) {
	            if (filters.containsKey("procedure")) {
	            	sqlfiltro = sqlfiltro + "and UPPER(v.value) like ''%"+filters.get("procedure").toString().toUpperCase()+"%'' ";
	            }
	            if (filters.containsKey("processName")) {
	            	sqlfiltro = sqlfiltro + "and UPPER(p.processname) like ''%"+filters.get("processName").toString().toUpperCase()+"%'' ";
	            }
	            if (filters.containsKey("taskNameHuman")) {
	            	sqlfiltro = sqlfiltro + "and UPPER(i.shorttext) like ''%"+filters.get("taskNameHuman").toString().toUpperCase()+"%'' ";
	            }
	        }	
			
			Query query = crudServiceBean.getEntityManager().createNativeQuery(
					"select * "
					+ "from dblink('"+dblinkBpmsHidrocarburos+"',' "
					+ "select v.value as tramite,p.processname as flujo, "
					+ "i.shorttext as actividad,t.id as taskid,t.status, "
					+ "p.processinstanceid,t.activationtime,t.processid "
					+ "from "
					+ "task t,variableinstancelog v, processinstancelog p ,i18ntext i "
					+ "where "
					+ "v.processinstanceid=t.processinstanceid and "
					+ "p.processinstanceid=t.processinstanceid and "
					+ "i.task_names_id=t.id and "
					+ "t.status in(''Ready'',''Reserved'',''InProgress'') and "
					+ sqlPeoplePotowners
					+ "v.variableid=''codigoProyecto'' "+sqlfiltro+" order by 4 desc LIMIT "+limit+" OFFSET "+pageSize+"') as ( "
					+ "tramite character varying(255), "
					+ "flujo character varying(255), "
					+ "actividad character varying(255), "
					+ "taskid bigint, "
					+ "status character varying(255),"
					+ "processinstanceid bigint,"
					+ "activationtime timestamp,"
					+ "processid character varying(255))");			
			
			List<Object[]> resultListHidrocarburos = (List<Object[]>) query.getResultList();			
			if (resultListHidrocarburos.size() > 0) {				
				for (int i = 0; i < resultListHidrocarburos.size(); i++) {
					Object[] dataProject = (Object[]) resultListHidrocarburos.get(i);
					TaskSummaryDto taskSummaryDto= new TaskSummaryDto();
					
					Date fechaActivacion=(Date) dataProject[6];
					taskSummaryDto.setActivationTime(fechaActivacion.getTime());
					taskSummaryDto.setFlowName(getNameProcessHidrocarburos(dataProject[7].toString()));
					taskSummaryDto.setName(dataProject[2].toString());
					taskSummaryDto.setProcessId(dataProject[7].toString());
					taskSummaryDto.setProcessInstanceId(Long.valueOf(dataProject[5].toString()));
					taskSummaryDto.setStep(dataProject[0].toString());
					taskSummaryDto.setTaskId(Long.valueOf(dataProject[3].toString()));
					
					tasksHidrocarburos.add(new TaskSummaryCustomBuilder().fromHidrocarburos(taskSummaryDto).build());
				}
			}

		} catch (Exception ex) {
			LOG.error("Ocurrió un error recuperando tareas del usuario en hidrocarburos", ex);
		}
		return tasksHidrocarburos;
	}
	
	public List<TaskSummaryCustom> getTasksSuiaIII(int pageSize, int limit, Map<String,Object> filters,String actorId, Usuario usuario) throws Exception {
		List<TaskSummaryCustom> tasksSuiaIII = new ArrayList<TaskSummaryCustom>();

		List<TaskSummary> tasksSumaries;
		processInstanceVariable = new HashMap<Long, Map<String, Object>>();
		processInstanceLogList = new HashMap<Long, ProcessInstanceLog>();		
		try {
			String sqlfiltro = "";
			if (filters != null) {
	            if (filters.containsKey("procedure")) {
	            	sqlfiltro = sqlfiltro + "and UPPER(v.value) like ''%"+filters.get("procedure").toString().toUpperCase()+"%'' ";
	            }
	            if (filters.containsKey("processName")) {
	            	sqlfiltro = sqlfiltro + "and UPPER(p.processname) like ''%"+filters.get("processName").toString().toUpperCase()+"%'' ";
	            }
	            if (filters.containsKey("taskNameHuman")) {
	            	sqlfiltro = sqlfiltro + "and UPPER(i.shorttext) like ''%"+filters.get("taskNameHuman").toString().toUpperCase()+"%'' ";
	            }
	        }	
			
			sqlfiltro = sqlfiltro + "and p.externalid like ''%suia-iii%'' "; //para buscar solo los procesos de regularizacion
			
			Query query = crudServiceBean.getEntityManager().createNativeQuery(
					"select * "
					+ "from dblink('"+dblinkBpmsSuiaiii+"',' "
					+ "select distinct v.value as tramite,p.processname as flujo, "
					+ "i.shorttext as actividad,t.id as taskid,t.status, "
					+ "p.processinstanceid "
					+ "from "
					+ "task t,variableinstancelog v, processinstancelog p ,i18ntext i "
					+ "where "
					+ "v.processinstanceid=t.processinstanceid and "
					+ "p.processinstanceid=t.processinstanceid and "
					+ "i.task_names_id=t.id and "
					+ "(t.actualowner_id =''"+usuario.getNombre()+"'' or t.createdby_id=''"+usuario.getNombre()+"'') and "
					+ "t.status in(''Reserved'',''InProgress'') and "
					+ "(v.variableid=''tramite'' or v.variableid=''u_tramite'') "+sqlfiltro+" order by 4 desc LIMIT "+limit+" OFFSET "+pageSize+"') as ( "
					+ "tramite character varying(255), "
					+ "flujo character varying(255), "
					+ "actividad character varying(255), "
					+ "taskid bigint, "
					+ "status character varying(255),"
					+ "processinstanceid bigint)");
			@SuppressWarnings("unchecked")
			List<Object[]> resultList = (List<Object[]>) query.getResultList();			
			if (resultList.size() > 0) {
				for (int i = 0; i < resultList.size(); i++) {
					Object[] dataProject = (Object[]) resultList.get(i);
					tasksSumaries=taskBeanFacade.actualTaskSummaryBandeja(actorId, Long.valueOf(dataProject[5].toString()), Constantes.getDeploymentId(), usuario.getPassword(), Constantes.getUrlBusinessCentral(), Constantes.getRemoteApiTimeout());
					for (TaskSummary tas : tasksSumaries) {
						boolean ingresado=false;
						for (TaskSummaryCustom custom : tasksSuiaIII) {
							if(custom.getTaskId()==tas.getId()){
								ingresado=true;
								break;
							}
						}
						if(!ingresado)
						tasksSuiaIII.add(new TaskSummaryCustomBuilder().fromSuiaIII1((String)dataProject[1], tas,(String)dataProject[0]).build());
					}
				}
			}

		} catch (Exception ex) {
			LOG.error("Ocurrió un error recuperando tareas del usuario en el sistema suia", ex);
		}
		return tasksSuiaIII;
	}
	//---FIN-----------------------------------------------------------------------------------------------------------------------------------

	public List<TaskSummaryCustom> getTasksByUserForRetasking(String actorId, Usuario usuario) throws Exception {
		List<TaskSummaryCustom> tasks = new ArrayList<TaskSummaryCustom>();
		List<TaskSummary> tasksSumaries;
		processInstanceVariable = new HashMap<Long, Map<String, Object>>();
		processInstanceLogList = new HashMap<Long, ProcessInstanceLog>();

		try {
			try {
				if (Constantes.getAppIntegrationSuiaEnabled()) {
					List<ProyectoLicenciaVo> tasksFromSuia = integracionFacade
							.getTaskByUserFromSuiaForRetasking(actorId);
					for (ProyectoLicenciaVo proyectoLicenciaVo : tasksFromSuia) {
						tasks.add(new TaskSummaryCustomBuilder().fromSuiaII(proyectoLicenciaVo).build());
					}
				}
			} catch (Exception ex) {
				LOG.error("Ocurrió un error recuperando tareas del sistema suia para la reasignación", ex);
			}

			try {
				tasksSumaries = taskBeanFacade.retrieveTaskList(actorId, usuario.getNombre(),
						Constantes.getDeploymentId(), usuario.getPassword(), Constantes.getUrlBusinessCentral(),
						Constantes.getRemoteApiTimeout());
				for (TaskSummary taskSummary : tasksSumaries) {
					tasks.add(new TaskSummaryCustomBuilder().fromSuiaIII(
							getProcessVariables(taskSummary.getProcessInstanceId(), usuario),
							getProcessName(taskSummary.getProcessInstanceId(), usuario), taskSummary).build());
				}
			} catch (Exception ex) {
				LOG.error("Ocurrió un error recuperando tareas para la reasignación", ex);
			}
			
//			hidrocarburos
			try {				
				Usuario usuarioRea = usuarioFacade.buscarUsuario(actorId);
				List<TaskSummaryDto> tasksFromHidrocarburos = integracionFacade.getTaskByUserFromHidrocarburos(actorId,
						usuarioRea.getNombre(), usuarioRea.getPasswordSha1Base64());
				for (TaskSummaryDto taskSummaryDto : tasksFromHidrocarburos) {
					tasks.add(new TaskSummaryCustomBuilder().fromHidrocarburos(taskSummaryDto).build());
				}
			} catch (Exception ex) {
				LOG.error("Ocurrió un error recuperando tareas para la reasignación", ex);
			}

		} catch (NullPointerException e) {
			throw new Exception("No tiene tareas asignadas para reasignación", e);
		}

		return tasks;
	}

	private ProcessInstanceLog getProcessInstanceLog(long processInstanceID, Usuario usuario) throws JbpmException {
		if (processInstanceLogList.containsValue(processInstanceID)) {
			return processInstanceLogList.get(processInstanceID);
		} else {
			return procesoFacade.getProcessInstanceLog(usuario, processInstanceID);
		}

	}

	public String getProcessName(long processInstanceID, Usuario usuario) {

		try {
			ProcessInstanceLog processInstanceLog = getProcessInstanceLog(processInstanceID, usuario);
			if(processInstanceLog==null){
			return	TaskSummaryCustom.LABEL_UNKNOW;
			}else{
			return processInstanceLog.getProcessName();
			}
		} catch (JbpmException e) {
			LOG.error("Ocurrio un error al recuperar la instancia del proceso (" + Long.toString(processInstanceID)
					+ ").", e);
		}
		return TaskSummaryCustom.LABEL_UNKNOW;
	}

	public Map<String, Object> getProcessVariables(long processInstanceID, Usuario usuario) {
		Map<String, Object> variables = new HashMap<String, Object>();
		try {

			if (processInstanceVariable.containsKey(processInstanceID)) {
				variables = processInstanceVariable.get(processInstanceID);
			} else {
				variables = procesoFacade.recuperarVariablesProceso(usuario, processInstanceID);
			}
		} catch (JbpmException e) {
			LOG.error("Ocurrio un error al recuperar las variables del proceso: " + processInstanceID, e);
		}
		return variables;
	}
	
	public String getNameProcessHidrocarburos(String processId)
    {
        String name = "Sin Nombre";
    
        if("Hydrocarbons.TDR".equals(processId))
        {
            name = "Términos de Referencia";
        }
        if("Hydrocarbons.TDREnte".equals(processId))
        {
            name = "Términos de Referencia";
        }
        if("Hydrocarbons.PPS".equals(processId))
        {
            name = "Participación Social";
        }
        if("Hydrocarbons.PPSEnte".equals(processId))
        {
            name = "Participación Social";
        }
        if("Hydrocarbons.EIA".equals(processId))
        {
            name = "Estudios de Impacto Ambiental";
        }
        if("Hydrocarbons.EIAEnte".equals(processId))
        {
            name = "Estudios de Impacto Ambiental";
        }
        if("Hydrocarbons.Inclusion".equals(processId))
        {
            name = "Inclusión";
        }
        if("Hydrocarbons.pagoLicencia".equals(processId))
        {
            name = "Hidrocarburos - Emisión de Licencia";
        }
        if("Hydrocarbons.pagoLicenciaEnte".equals(processId))
        {
            name = "Hidrocarburos - Emisión de Licencia";
        }
        if("Hydrocarbons.Migracion".equals(processId))
        {
            name = "Revisión y aprobación - Documentos complementarios";
        }        
        if("Hydrocarbons.PPSVisitaPrevia".equals(processId))
        {
            name = "Participación Social Visita Previa";
        }
        if("Hydrocarbons.PPSVisitaPreviaSoporte".equals(processId))
        {
            name = "Participación Social Visita Previa Soporte";
        }
        if("Hydrocarbons.PPSAccionesComplementarias".equals(processId))
        {
            name = "Participación Social Acciones Complementarias";
        }            
        
        return name;
    }   
	
	public String getNombreTarea(Long idTarea) throws Exception {
		String nombre = null;		
		try {
			
			Query query = crudServiceBean.getEntityManager().createNativeQuery(
					"select * "
					+ "from dblink('"+dblinkBpmsSuiaiii+"',' "
					+ "select t.formname "
					+ "from "
					+ "task t "
					+ "where "
					+ "t.id = " + idTarea + " "
					+ "') as ( "
					+ "nombre character varying(255))");
			
			@SuppressWarnings("unchecked")
			List<Object[]> resultList = (List<Object[]>) query.getResultList();			
			if (resultList.size() > 0) {
				nombre = String.valueOf(resultList.get(0));
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return nombre;
	}
	
	public List<Date> getFechasTarea(String nombreTarea, Long idProceso) {
		List<Date> fechas = new ArrayList<>();
		try {
			Query query = crudServiceBean.getEntityManager().createNativeQuery(
					"select * "
					+ "from dblink('"+dblinkBpmsSuiaiii+"',' "
					+ "select t.createddate, t.enddate "
					+ "from "
					+ "bamtasksummary t "
					+ "where "
					+ "t.processinstanceid = " + idProceso + " "
					+ "and t.taskname = ''" + nombreTarea + "'' order by taskid desc limit 1"
					+ "') as ( "
					+ "fecha_inicio timestamp, fecha_fin timestamp)");
			
			@SuppressWarnings("unchecked")
			List<Object[]> resultList = (List<Object[]>) query.getResultList();			
			if (resultList.size() > 0) {
				Date fechaInicio = (Date) resultList.get(0)[0];
				Date fechaFin = (Date) resultList.get(0)[1];
				
				fechas.add(fechaInicio);
				fechas.add(fechaFin);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return fechas;
	}
	
	public List<Date> getFechasTareaNombre(String nombreTarea, Long idProceso) {
		List<Date> fechas = new ArrayList<>();
		try {
			Integer idTarea = null;
			
			Query query1 = crudServiceBean.getEntityManager().createNativeQuery(
					"select * "
					+ "from dblink('"+dblinkBpmsSuiaiii+"',' "
					+ "select t.id "
					+ "from "
					+ "task t "
					+ "where "
					+ "t.formname = ''" + nombreTarea + "'' "
					+ "and t.processinstanceid = " + idProceso + " "
					+ "') as ( "
					+ "id integer)");
			
			@SuppressWarnings("unchecked")
			List<Object[]> resultList1 = (List<Object[]>) query1.getResultList();			
			if (resultList1.size() > 0) {
				String id = String.valueOf(resultList1.get(0));
				idTarea = Integer.parseInt(id);
			}
			
			if(idTarea != null) {
				Query query = crudServiceBean.getEntityManager().createNativeQuery(
						"select * "
						+ "from dblink('"+dblinkBpmsSuiaiii+"',' "
						+ "select t.createddate, t.enddate "
						+ "from "
						+ "bamtasksummary t "
						+ "where "
						+ "t.processinstanceid = " + idProceso + " "
						+ "and t.taskid = ''" + idTarea + "'' order by taskid desc limit 1"
						+ "') as ( "
						+ "fecha_inicio timestamp, fecha_fin timestamp)");
				
				@SuppressWarnings("unchecked")
				List<Object[]> resultList = (List<Object[]>) query.getResultList();			
				if (resultList.size() > 0) {
					Date fechaInicio = (Date) resultList.get(0)[0];
					Date fechaFin = (Date) resultList.get(0)[1];
					
					fechas.add(fechaInicio);
					fechas.add(fechaFin);
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return fechas;
	}
}
