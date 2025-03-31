package ec.gob.ambiente.igualar.fechas.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.utils.Constantes;

@Stateless
public class ProcesoFechasFacade {
	
	public String dblinkBpmsSuiaiii=Constantes.getDblinkBpmsSuiaiii();
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	@SuppressWarnings("unchecked")
	public List<ProcesoFechas> listConsultarFechasXProceso (Integer idProcesoAnterior,Integer idProcesoActual){
		
		List<ProcesoFechas> listProcesosFechasAnterior= new ArrayList<ProcesoFechas>();
		List<ProcesoFechas> listProcesosFechasActuales= new ArrayList<ProcesoFechas>();
		
		String sqlFechasProcesoAnterior = "select * from dblink('"+dblinkBpmsSuiaiii+"','select b.taskid,b.createddate,b.startdate,b.enddate,b.status,b.taskname,b.userid from bamtasksummary b "
							+ "where b.processinstanceid ="+idProcesoAnterior+" order by taskname,userid,enddate,taskid') as t1 (taskid integer, createddate timestamp, startdate timestamp, enddate timestamp, "
							+ "status character varying,taskname character varying,userid character varying)";		
		Query queryFechasAnterior =  crudServiceBean.getEntityManager().createNativeQuery(sqlFechasProcesoAnterior);
		List<Object>  resultFechasAnterior = queryFechasAnterior.getResultList();
		
		if(resultFechasAnterior.size()>0){
			for (int i = 0; i < resultFechasAnterior.size(); i++) {
				ProcesoFechas fechas= new ProcesoFechas();
				Object [] objAux = (Object[])resultFechasAnterior.get(i);
				fechas.setTaskIdAnterior(((Integer)objAux[0]));
				fechas.setCreatedDateAnterior(((Timestamp)objAux[1]));
				fechas.setStartDateAnterior(((Timestamp)objAux[2]));
				fechas.setEndDateAnterior(((Timestamp)objAux[3]));
				fechas.setStatusAnterior(((String)objAux[4]));
				fechas.setTaskNameAnterior(((String)objAux[5]));
				fechas.setUserIdAnterior(((String)objAux[6]));
				listProcesosFechasAnterior.add(fechas);
			}			
		}
		
		String sqlFechasProcesoActual = "select * from dblink('"+dblinkBpmsSuiaiii+"','select b.taskid,b.taskname,b.userid from bamtasksummary b "
									  + "where processinstanceid ="+idProcesoActual+" order by taskname,userid,enddate,taskid') "
									  + "as t1 (taskid integer, taskname character varying,userid character varying)";		
		Query queryFechasActual =  crudServiceBean.getEntityManager().createNativeQuery(sqlFechasProcesoActual);
		List<Object>  resultFechasActual = queryFechasActual.getResultList();
		
		if(resultFechasActual.size()>0){
			for (int i = 0; i < resultFechasActual.size(); i++) {
				ProcesoFechas fechas= new ProcesoFechas();
				Object [] objAux = (Object[])resultFechasActual.get(i);
				fechas.setTaskIdActual(((Integer)objAux[0]));
				fechas.setTaskNameActual(((String)objAux[1]));
				fechas.setUserIdActual(((String)objAux[2]));
				fechas.setIdProcesoAnterior(idProcesoAnterior);
				fechas.setIdProcesoActual(idProcesoActual);
				listProcesosFechasActuales.add(fechas);
			}			
		}
		
		for (int i = 0; i < listProcesosFechasAnterior.size(); i++) {
			for (int j = 0; j < listProcesosFechasActuales.size(); j++) {
				if(listProcesosFechasAnterior.get(i).getTaskNameAnterior().compareTo(listProcesosFechasActuales.get(j).getTaskNameActual())==0){
					listProcesosFechasAnterior.get(i).setTaskIdActual(listProcesosFechasActuales.get(j).getTaskIdActual());
					listProcesosFechasAnterior.get(i).setTaskNameActual(listProcesosFechasActuales.get(j).getTaskNameActual());
					listProcesosFechasAnterior.get(i).setUserIdActual(listProcesosFechasActuales.get(j).getUserIdActual());
					listProcesosFechasAnterior.get(i).setIdProcesoAnterior(listProcesosFechasActuales.get(j).getIdProcesoAnterior());
					listProcesosFechasAnterior.get(i).setIdProcesoActual(listProcesosFechasActuales.get(j).getIdProcesoActual());
					listProcesosFechasActuales.remove(j);
				}
			}			
		}
		return listProcesosFechasAnterior;
	}
	
	
	public boolean actualizarProcesosActual(ProcesoFechas procesoFechas){
		String sqlSuiaIII="select dblink_exec('"+dblinkBpmsSuiaiii+"','update processinstancelog set start_date=(select start_date + INTERVAL ''1 second'' from processinstancelog where processinstanceid ="+procesoFechas.getIdProcesoAnterior()+") where processinstanceid ="+procesoFechas.getIdProcesoActual()+"') as result";
		
		Query query = crudServiceBean.getEntityManager().createNativeQuery(sqlSuiaIII);
		if(query.getResultList().size()>0)
			return true;
		return false;
		
	}
	
	public boolean actualizarProcesosAnterior(ProcesoFechas procesoFechas){
		String sqlSuiaIII="select dblink_exec('"+dblinkBpmsSuiaiii+"','update processinstancelog set status=''4'' where processinstanceid ="+procesoFechas.getIdProcesoAnterior()+"') as result";
		
		Query query = crudServiceBean.getEntityManager().createNativeQuery(sqlSuiaIII);
		if(query.getResultList().size()>0)
			return true;
		return false;
		
	}
	
	public boolean actualizarProcesosBamtaskSummary(ProcesoFechas procesoFechas){
		String sqlSuiaIII="select dblink_exec('"+dblinkBpmsSuiaiii+"','update bamtasksummary set createddate=''"+procesoFechas.getCreatedDateAnterior()+"'',startdate =''"+procesoFechas.getStartDateAnterior()+"'',enddate=''"+procesoFechas.getEndDateAnterior()+"'' where taskid="+procesoFechas.getTaskIdActual()+"') as result";
		
		Query query = crudServiceBean.getEntityManager().createNativeQuery(sqlSuiaIII);
		if(query.getResultList().size()>0)
			return true;
		return false;
	}
	
	public boolean actualizarProcesosTask(ProcesoFechas procesoFechas){
		String sqlSuiaIII="select dblink_exec('"+dblinkBpmsSuiaiii+"','update task set activationtime =''"+procesoFechas.getCreatedDateAnterior()+"'',createdon=''"+procesoFechas.getCreatedDateAnterior()+"'' where id="+procesoFechas.getTaskIdActual()+"') as result";
		
		Query query = crudServiceBean.getEntityManager().createNativeQuery(sqlSuiaIII);
		if(query.getResultList().size()>0)
			return true;
		return false;
	}
	
	public boolean actualizarProcesosTaskAnterior(ProcesoFechas procesoFechas){
		String sqlSuiaIII="select dblink_exec('"+dblinkBpmsSuiaiii+"','update task set actualowner_id =null where id="+procesoFechas.getTaskIdAnterior()+"') as result";
		
		Query query = crudServiceBean.getEntityManager().createNativeQuery(sqlSuiaIII);
		if(query.getResultList().size()>0)
			return true;
		return false;
	}
	
	public boolean actualizarProcesosTaskevent(ProcesoFechas procesoFechas){
		String sqlSuiaIII="select dblink_exec('"+dblinkBpmsSuiaiii+"','update taskevent set logtime =''"+procesoFechas.getEndDateAnterior()+"'' where type=''"+procesoFechas.getStatusAnterior().toUpperCase()+"'' and taskid="+procesoFechas.getTaskIdActual()+"') as result";
		
		Query query = crudServiceBean.getEntityManager().createNativeQuery(sqlSuiaIII);
		if(query.getResultList().size()>0)
			return true;
		return false;
	}
	
	public boolean actualizarProcesosUsauario(ProcesoFechas procesoFechas){
		String sqltask="select dblink_exec('"+dblinkBpmsSuiaiii+"','UPDATE task set actualowner_id =''"+procesoFechas.getUserIdAnterior()+"'',createdby_id=''"+procesoFechas.getUserIdAnterior()+"'' where id="+procesoFechas.getTaskIdActual()+"') as result";
		Query query = crudServiceBean.getEntityManager().createNativeQuery(sqltask);
		
		String sqlbamtasksummary="select dblink_exec('"+dblinkBpmsSuiaiii+"','UPDATE bamtasksummary  set userid=''"+procesoFechas.getUserIdAnterior()+"'' where taskid ="+procesoFechas.getTaskIdActual()+"') as result";
		Query query1 = crudServiceBean.getEntityManager().createNativeQuery(sqlbamtasksummary);
		
		String sqltaskevent="select dblink_exec('"+dblinkBpmsSuiaiii+"','UPDATE taskevent set userid =''"+procesoFechas.getUserIdAnterior()+"'' where taskid="+procesoFechas.getTaskIdActual()+"') as result";
		Query query2 = crudServiceBean.getEntityManager().createNativeQuery(sqltaskevent);
		
		String sqlpeopleassignments="select dblink_exec('"+dblinkBpmsSuiaiii+"','UPDATE peopleassignments_potowners set entity_id =''"+procesoFechas.getUserIdAnterior()+"'' where task_id="+procesoFechas.getTaskIdActual()+"') as result";
		Query query3 = crudServiceBean.getEntityManager().createNativeQuery(sqlpeopleassignments);
		
		if(query.getResultList().size()>0 && query1.getResultList().size()>0 && query2.getResultList().size()>0 && query3.getResultList().size()>0)
			return true;
		return false;
	}
	
}
