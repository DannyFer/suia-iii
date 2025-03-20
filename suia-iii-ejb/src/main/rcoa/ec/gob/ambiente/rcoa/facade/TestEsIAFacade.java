package ec.gob.ambiente.rcoa.facade;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.suia.administracion.facade.FeriadosFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;

@Stateless 
public class TestEsIAFacade {
	
	
	@EJB
	private CrudServiceBean crudServiceBean;
	@EJB
	private ProcesoFacade procesoFacade;
	@EJB
	private UsuarioFacade usuarioFacade;	
	@EJB
	private FeriadosFacade feriadosFacade;
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	

	@SuppressWarnings("unchecked")
	private void igualFechaTareas(Long idProcesoActual, Long idNuevoProceso, Long idTareaProcesoActual, Long idTareaNuevoProceso) {
		//igualar fechas tareas
		
		String sqlUpdateBamtasksummary="select dblink_exec('"+Constantes.getDblinkBpmsSuiaiii()
				 +"','UPDATE bamtasksummary "
				 + "SET createddate = (select createddate from bamtasksummary where processinstanceid = " + idProcesoActual+" and taskid = "+idTareaProcesoActual+"),  "
				 + "enddate = (select enddate from bamtasksummary where processinstanceid = " + idProcesoActual+" and taskid = "+idTareaProcesoActual+"),  "
				 + "startdate = (select startdate from bamtasksummary where processinstanceid = " + idProcesoActual+" and taskid = "+idTareaProcesoActual+") "
				 + "WHERE processinstanceid = "+idNuevoProceso+" and taskid = "+idTareaNuevoProceso+"') as result";
		
		Query queryUpdateBamtasksummary = crudServiceBean.getEntityManager().createNativeQuery(sqlUpdateBamtasksummary);
		if (queryUpdateBamtasksummary.getResultList().size() > 0) {
			queryUpdateBamtasksummary.getSingleResult();
		}
        
        String sqlUpdateTask="select dblink_exec('"+Constantes.getDblinkBpmsSuiaiii()
				 +"','UPDATE task "
				 + "set activationtime = (select activationtime from task where processinstanceid = "+idProcesoActual+" and id = "+idTareaProcesoActual+"),  "
				 + "createdon = (select createdon from task where processinstanceid = "+idProcesoActual+" and id = "+idTareaProcesoActual+") "
				 + "where processinstanceid = "+idNuevoProceso+" and id = "+idTareaNuevoProceso+"') as result";
        
        Query queryUpdateTask = crudServiceBean.getEntityManager().createNativeQuery(sqlUpdateTask);
		if(queryUpdateTask.getResultList().size()>0) {
			queryUpdateTask.getSingleResult();
		}
		
		String sqlTaskevent="select * from dblink('"+Constantes.getDblinkBpmsSuiaiii()+"', "
			       	+ "' SELECT taskid, logtime, type FROM taskevent  "
			       	+ "WHERE taskid = "+idTareaProcesoActual+" ') as (id text, time varchar, type text)";
		
		List<Object[]> listTaskevents = (List<Object[]>) crudServiceBean.getEntityManager().createNativeQuery(sqlTaskevent).getResultList();
		
		for (int t = 0; t < listTaskevents.size(); t++) {
			Object[] dataTaskevent = (Object[]) listTaskevents.get(t);
			String sqlUpdateTaskevent="select dblink_exec('"+Constantes.getDblinkBpmsSuiaiii()
							 +"','update taskevent set logtime = ''"+dataTaskevent[1].toString()+"'' "
							 		+ " where taskid = "+idTareaNuevoProceso+" and type = ''"+dataTaskevent[2].toString()+"'' ') as result";
			 
			Query queryUpdate = crudServiceBean.getEntityManager().createNativeQuery(sqlUpdateTaskevent);
			if(queryUpdate.getResultList().size()>0)
				queryUpdate.getSingleResult();
		}
	}
	
	private void abortarProcesoAnterior(Long idProcesoActual) {
		//abortar proceso anterior
		
		String sqlUpdateAbortarProcess="select dblink_exec('"+Constantes.getDblinkBpmsSuiaiii()
				 +"','update processinstancelog set status = 4 "
				 + " where processinstanceid = "+idProcesoActual+"') as result";
		 
		 Query queryUpdateAbortarProcess = crudServiceBean.getEntityManager().createNativeQuery(sqlUpdateAbortarProcess);
        if(queryUpdateAbortarProcess.getResultList().size()>0){
        	queryUpdateAbortarProcess.getSingleResult();
        }
        
        String sqlUpdateAbortarSumm="select dblink_exec('"+Constantes.getDblinkBpmsSuiaiii()
				 +"','update bamtasksummary set status = ''Exited'' "
				 + "where processinstanceid =  "+idProcesoActual+"') as result";
		 
		 Query queryUpdateAbortarSumm = crudServiceBean.getEntityManager().createNativeQuery(sqlUpdateAbortarSumm);
        if(queryUpdateAbortarSumm.getResultList().size()>0) {
        	queryUpdateAbortarSumm.getSingleResult();
        }
        
        String sqlUpdate="select dblink_exec('"+Constantes.getDblinkBpmsSuiaiii()
				 +"','update task set status = ''Exited'' "
				 + "where processinstanceid = "+idProcesoActual+"') as result";
		 
		 Query queryUpdate = crudServiceBean.getEntityManager().createNativeQuery(sqlUpdate);
        if(queryUpdate.getResultList().size()>0) {
        	queryUpdate.getSingleResult();
        }
	}

	@SuppressWarnings("unchecked")
	public void igualarProcesoBack(Long idProcesoAnterior, Long idNuevoProceso) {
		try {
			String sqlTareasProcesoActual="select * from dblink('"+Constantes.getDblinkBpmsSuiaiii()+"', "
	            	+ "'select id, status, formname from task "
	            	+ "where processinstanceid = "+ idProcesoAnterior +" order by id') as (id text, status text, formname text)"; 
			
	    	List<Object[]> listTareasProcesoAnterior = (List<Object[]>) crudServiceBean.getEntityManager().createNativeQuery(sqlTareasProcesoActual).getResultList();

			if(listTareasProcesoAnterior.size() > 0) {
				for (int j = 0; j < listTareasProcesoAnterior.size(); j++) {							
					Object[] dataTareaProcesoAnterior = (Object[]) listTareasProcesoAnterior.get(j);
					Long idTareaProcesoAnterior = Long.parseLong(dataTareaProcesoAnterior[0].toString());
					String estadoTareaProcesoAnterior = dataTareaProcesoAnterior[1].toString();
					
					String sqlTareaActivaNuevoProceso="select * from dblink('"+Constantes.getDblinkBpmsSuiaiii()+"', "
			            	+ "'select id, actualowner_id, formname from task "
			            	+ "where processinstanceid = "+ idNuevoProceso +" and status = ''Reserved'' ') as (id text, actualowner_id text, formname text)";
					
					List<Object[]> listTareaActivaNuevoProceso = (List<Object[]>) crudServiceBean.getEntityManager().createNativeQuery(sqlTareaActivaNuevoProceso).getResultList();
					
					if (listTareaActivaNuevoProceso.size() > 0) {
						Object[] dataTarea = (Object[]) listTareaActivaNuevoProceso.get(0);
						
						Long idTareaNuevoProceso = Long.parseLong(dataTarea[0].toString());
						Usuario usuarioTarea = usuarioFacade.buscarUsuario(dataTarea[1].toString());
						
						if(estadoTareaProcesoAnterior.equals("Completed")) {
							procesoFacade.aprobarTarea(usuarioTarea, idTareaNuevoProceso, idNuevoProceso, null);
						}
						
						if(dataTarea[2].toString().equals(dataTareaProcesoAnterior[2].toString())) {
							
							igualFechaTareas(idProcesoAnterior, idNuevoProceso, idTareaProcesoAnterior, idTareaNuevoProceso);
						
						}
					}
				}
			}

			// igualar fecha proceso
			String sqlUpdateProcess = "select dblink_exec('" + Constantes.getDblinkBpmsSuiaiii() + "',"
					+ "'update processinstancelog set "
					+ "start_date = (select start_date from  processinstancelog where processinstanceid = " + idProcesoAnterior + "), "
					+ "parentprocessinstanceid = (select parentprocessinstanceid from  processinstancelog where processinstanceid = " + idProcesoAnterior + ") "
					+ "where processinstanceid = " + idNuevoProceso+ "') as result";

			Query queryUpdateProcess = crudServiceBean.getEntityManager().createNativeQuery(sqlUpdateProcess);
			if (queryUpdateProcess.getResultList().size() > 0) {
				queryUpdateProcess.getSingleResult();
			}

			abortarProcesoAnterior(idProcesoAnterior);
			
			//actualizar documentos del proceso
			String sqlUpdateDocumentos = "update coa_mae.documents_coa "
					+ "set docu_process_instance_id = " + idNuevoProceso 
					+ " where docu_process_instance_id = " + idProcesoAnterior ;

			crudServiceBean.insertUpdateByNativeQuery(sqlUpdateDocumentos.toString(), null);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
