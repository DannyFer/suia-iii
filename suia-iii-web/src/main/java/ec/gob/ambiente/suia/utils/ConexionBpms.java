package ec.gob.ambiente.suia.utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import com.google.common.util.concurrent.ExecutionError;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.GeneradorDesechosPeligrosos;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;

@Stateless
public class ConexionBpms{

	@EJB
    private CrudServiceBean crudServiceBean;
    
    @EJB
	private ProcesoFacade procesoFacade;
   
    public String dblinkBpmsSuiaiii=Constantes.getDblinkBpmsSuiaiii();
    public String dblinkBpmsHyd=Constantes.getDblinkBpmsHyD();
    public String dblinkSuiaVerde=Constantes.getDblinkSuiaVerde();
    
    //private String dblink="dbname=suia_bpms_enlisy host=172.16.0.179 port=5532 user=postgres password=postgres";
//  private String dblink="dbname=suia_bpms_enlisy host=172.16.14.105 port=5532 user=postgres password=postgres";        
//  private String dblink="dbname=bpms_pruebas host=172.16.0.143 port=5532 user=postgres password=postgres";
    

    public void updateStatusBamTaskSummary(long processinstanceid, long taskid, Usuario usuario ,String estado)
    {        
        String sqlBam="select dblink_exec('"+dblinkBpmsSuiaiii+"','update bamtasksummary set status=''"+estado+"'' where processinstanceid=''"+processinstanceid+"''and taskid=''"+taskid+"''') as result";
        Query query = crudServiceBean.getEntityManager().createNativeQuery(sqlBam);
        if(query.getResultList().size()>0){
            query.getSingleResult();
        }
    }

    public void updateVariables(long processinstanceid,String variableid,String value)
    {        
//        Query query = crudServiceBean.getEntityManager()
//                .createNativeQuery("select * from  dblink('"+dblink+"','select variableid from variableinstancelog where  processinstanceid=''"+processinstanceid+"'' and variableid=''"+variableid+"''')"
//                        + "as (variableid varchar(255))");    
//        Object resultList = (Object) query.getResultList();
//        if (resultList!=null) {
//            System.out.println("variable "+resultList.toString());
//        }
        if(value.length()>255)
            value=value.substring(0, 254);
        String sql="select dblink_exec('"+dblinkBpmsSuiaiii+"','update variableinstancelog set value=''"+value+"'' where processinstanceid=''"+processinstanceid+"''and variableid=''"+variableid+"''') as result";
        Query query = crudServiceBean.getEntityManager().createNativeQuery(sql);
        if(query.getResultList().size()>0)
            query.getSingleResult();
       
    }
    
    @SuppressWarnings("unchecked")
	public void reasigment(Usuario usuario, long taskid, String cedulaactual, String cedulaanterior, long processinstanceid){
    		
    		String nombreProceso="";
    		
    		Query sqlProcesos = crudServiceBean.getEntityManager().createNativeQuery(
    		        "select * from  dblink('"+dblinkBpmsSuiaiii+"','select processid from variableinstancelog where processinstanceid=''"+processinstanceid+"'' group by processid') as (processid varchar)");     		
    		List<Object>  resultPro = new ArrayList<Object>();
    		resultPro=sqlProcesos.getResultList();
    		if (resultPro!=null) {		    	 
    			for(Object a: resultPro)
    			{
    				if(a!=null)
    				nombreProceso=a.toString();
    			}

    		}
    		if(nombreProceso.equals("SUIA.LicenciaAmbiental"))
    		{
    			reasignarTareasSuiaIII(usuario, taskid, cedulaactual, cedulaanterior, processinstanceid);
    		}
    		if(nombreProceso.equals("suia.participacion-social"))
    		{
    			reasignarTareasSuiaIII(usuario, taskid, cedulaactual, cedulaanterior, processinstanceid);
    		}
    		if(nombreProceso.equals("mae-procesos.GeneradorDesechos"))
    		{
    			generadorDesechos(usuario, taskid, cedulaactual, cedulaanterior, processinstanceid);    			
    		}
    		if(nombreProceso.equals("Suia.AprobracionRequisitosTecnicosGesTrans2"))
    		{
    			reasignarTareasSuiaIII(usuario, taskid, cedulaactual, cedulaanterior, processinstanceid);
    		}
    		if(nombreProceso.equals("mae-procesos.Eliminarproyecto"))
    		{
    			reasignarTareasSuiaIII(usuario, taskid, cedulaactual, cedulaanterior, processinstanceid);
    		}
    		if(nombreProceso.equals("mae-procesos.registro-ambiental"))
    		{
    			reasignarTareasSuiaIII(usuario, taskid, cedulaactual, cedulaanterior, processinstanceid);
    		}
    		if(nombreProceso.equals("Certificado-viabilidad-ambiental"))
    		{
    			reasignarTareasSuiaIII(usuario, taskid, cedulaactual, cedulaanterior, processinstanceid);
    		}
    		if(nombreProceso.equals("mae-procesos.RegistroAmbiental"))
    		{
    			reasignarTareasSuiaIII(usuario, taskid, cedulaactual, cedulaanterior, processinstanceid);
    		}
    		if(nombreProceso.equals("mae-procesos.RequisitosPrevios"))
    		{
    			reasignarTareasSuiaIII(usuario, taskid, cedulaactual, cedulaanterior, processinstanceid);
    		}
    		if(nombreProceso.equals("mae-procesos.CertificadoAmbiental"))
    		{
    			reasignarTareasSuiaIII(usuario, taskid, cedulaactual, cedulaanterior, processinstanceid);
    		}
    		if(nombreProceso.equals("mae-procesos.RegistroEmisionesTransferenciaContaminantesEcuador")) {
    			reasignarTareasSuiaIII(usuario, taskid, cedulaactual, cedulaanterior, processinstanceid);
    		}
    		if(nombreProceso.contains("rcoa.")){
    			reasignarTareasSuiaIII(usuario, taskid, cedulaactual, cedulaanterior, processinstanceid);
    		}
    		
    }

    @SuppressWarnings({ "unchecked" })
	private void reasignarTareasSuiaIII(Usuario usuario, long taskid, String cedulaactual, String cedulaanterior, long processinstanceid){
    	try {	

    		Query sqlVariableid = crudServiceBean.getEntityManager().createNativeQuery(
    				"select * from  dblink('"+dblinkBpmsSuiaiii+"','select variableid from variableinstancelog where processinstanceid=''"+processinstanceid+"'' and value=''"+cedulaanterior+"'' group by variableid order by 1') as (variableid varchar)"); 
    		List<Object>  resultListV = new ArrayList<Object>();
    		resultListV=sqlVariableid.getResultList();
    		Map<String, Object> params = new ConcurrentHashMap<String, Object>();
    		if (resultListV!=null) {		    	 
    			for(Object a: resultListV)
    			{
    				params.put(a.toString(), cedulaactual);
    			}
    		}        

    		procesoFacade.modificarVariablesProceso(usuario, processinstanceid, params);

//    		String sql="select dblink_exec('"+dblinkBpmsSuiaiii+"','update task set status=''InProgress'',createdby_id=''"+cedulaactual+"'', actualowner_id=''"+cedulaactual+"'' where id=''"+taskid+"''') as result";
//    		Query query = crudServiceBean.getEntityManager().createNativeQuery(sql);
//    		if(query.getResultList().size()>0)
//    			query.getSingleResult();
//
//    		String sqlBam="select dblink_exec('"+dblinkBpmsSuiaiii+"','update bamtasksummary set duration=50,startdate=createddate,enddate=createddate, status=''InProgress'',userid=''"+cedulaactual+"'' where taskid=''"+taskid+"''') as result";
//    		Query queryBam = crudServiceBean.getEntityManager().createNativeQuery(sqlBam);
//    		if(queryBam.getResultList().size()>0)
//    			queryBam.getSingleResult();
//
//    		String sql1="select dblink_exec('"+dblinkBpmsSuiaiii+"','update peopleassignments_potowners set entity_id=''"+cedulaactual+"'' where task_id=''"+taskid+"''') as result";
//    		Query query1 = crudServiceBean.getEntityManager().createNativeQuery(sql1);
//    		if(query1.getResultList().size()>0)
//    			query1.getSingleResult();
    	}
    	catch (JbpmException e) {
    		JsfUtil.addMessageError("Error al reasignar la tarea");
    	}  
    }
    
    @SuppressWarnings("unchecked")
	private void generadorDesechos(Usuario usuario, long taskid, String cedulaactual, String cedulaanterior, long processinstanceid)
    {
    	Query sqlroles = crudServiceBean.getEntityManager().createNativeQuery(
    			"select u.user_name,r.role_name from users  u "
    					+ "inner join areas a on a.area_id=u.area_id " 
    					+ "inner join suia_iii.roles_users ru on ru.user_id=u.user_id "
    					+ "inner join suia_iii.roles r on r.role_id=ru.role_id "
    					+ "left join areas ap on ap.area_id = a.area_parent_id "
    					+ "where u.user_name='"+cedulaactual+"' and ru.rous_status=true");
    	
    	List<Object[]> resultListroles = (List<Object[]>) sqlroles.getResultList();
    	Boolean estado=false;
    	Boolean estadocoordinador=false;
    	Boolean estadotecnico=false;
    	if (resultListroles.size() > 0) {
    		Map<String, Object> params = new ConcurrentHashMap<String, Object>();
    		for (int i = 0; i < resultListroles.size(); i++) {
    			Object[] roles = (Object[]) resultListroles.get(i);    					
    			if(roles[1].equals("TÉCNICO REASIGNACIÓN COORDINADOR PROVINCIAL") || roles[1].equals("TÉCNICO REASIGNACION COORDINADOR"))
    			{
    				params.put("coordinador", cedulaactual);
    				estado=true;
    				estadocoordinador=true;
    			}
    			else if(roles[1].equals("TÉCNICO ANALISTA DE REGISTRO"))
    			{
    				params.put("tecnico", cedulaactual);
    				estado=true;
    				estadotecnico=true;
    			}
    			else if(roles[1].equals("AUTORIDAD AMBIENTAL"))
    			{
    				params.put("director", cedulaactual);
    				estado=true;
    			}
    			else if(roles[1].equals("SUBSECRETARIO DE CALIDAD AMBIENTAL"))
    			{
    				params.put("subsecretaria", cedulaactual);
    				estado=true;
    			}
    			else if(roles[1].equals("GERENTE"))
    			{
    				params.put("director", cedulaactual);
    				estado=true;
    			}
    		}
    		if(estado)
    		{
    			try {	
    				////////////////////funcional/////////////////////////////////////////

//		    		Query sqlVariableid = crudServiceBean.getEntityManager().createNativeQuery(
//		    				"select * from  dblink('"+dblink+"','select variableid from variableinstancelog where processinstanceid=''"+processinstanceid+"'' and value=''"+cedulaanterior+"'' group by variableid order by 1') as (variableid varchar)"); 
//		    		List<Object>  resultListV = new ArrayList<Object>();
//		    		resultListV=sqlVariableid.getResultList();
//		    		Map<String, Object> params = new ConcurrentHashMap<String, Object>();
//		    		if (resultListV!=null) {		    	 
//		    			for(Object a: resultListV)
//		    			{
//		    				params.put(a.toString(), cedulaactual);
//		    			}
//
//		    		}        

    				procesoFacade.modificarVariablesProceso(usuario, processinstanceid, params);

//    				String sql="select dblink_exec('"+dblinkBpmsSuiaiii+"','update task set status=''InProgress'',createdby_id=''"+cedulaactual+"'', actualowner_id=''"+cedulaactual+"'' where id=''"+taskid+"''') as result";
//    				Query query = crudServiceBean.getEntityManager().createNativeQuery(sql);
//    				if(query.getResultList().size()>0)
//    					query.getSingleResult();
//
//    				String sqlBam="select dblink_exec('"+dblinkBpmsSuiaiii+"','update bamtasksummary set duration=50,startdate=createddate,enddate=createddate, status=''InProgress'',userid=''"+cedulaactual+"'' where taskid=''"+taskid+"''') as result";
//    				Query queryBam = crudServiceBean.getEntityManager().createNativeQuery(sqlBam);
//    				if(queryBam.getResultList().size()>0)
//    					queryBam.getSingleResult();
//
//    				String sql1="select dblink_exec('"+dblinkBpmsSuiaiii+"','update peopleassignments_potowners set entity_id=''"+cedulaactual+"'' where task_id=''"+taskid+"''') as result";
//    				Query query1 = crudServiceBean.getEntityManager().createNativeQuery(sql1);
//    				if(query1.getResultList().size()>0)
//    					query1.getSingleResult();

    				try
    				{
    					if(estadotecnico)
    					{
    						String sql3="update suia_iii.technical_report_waste_generators set trrg_creator_user = '"+cedulaactual+"', trrg_user_update = '"+cedulaactual+"' where trrg_process_instance_id = "+processinstanceid+" and trrg_status = true";
    						Query query3 = crudServiceBean.getEntityManager().createNativeQuery(sql3);
    						query3.executeUpdate();
    					}
    				}
    				catch(ExecutionError e)
    				{}

    				try
    				{
    					if(estadocoordinador)
    					{
//    						String sql4="update suia_iii.office_observation_waste_generators set oowg_creator_user = '"+cedulaactual+"', oowg_user_update = '"+cedulaactual+"' where oowg_process_instance_id = "+processinstanceid+" and oowg_status = true";
//    						Query query4 = crudServiceBean.getEntityManager().createNativeQuery(sql4);
//    						query4.executeUpdate();
    					}
    				}
    				
    				catch(ExecutionError e)
    				{}

    				//ACTUALIZAR DIRECTOR generador de desechos------------------------------------------------
    				Query querydblinkHWGE = crudServiceBean.getEntityManager().createNativeQuery("select * from  dblink('"+dblinkBpmsSuiaiii+"','select value from variableinstancelog where processinstanceid=''"+processinstanceid+"'' and variableid =''idRegistroGenerador''') as (value varchar)"); 
    				List<Object>  resultList = new ArrayList<Object>();
    				resultList=querydblinkHWGE.getResultList();
    				Integer idGenerador = null;
    				if (resultList!=null) {
    					for(Object a: resultList)
    					{
    						idGenerador=Integer.parseInt(a.toString());
    					}
    					GeneradorDesechosPeligrosos generador = new GeneradorDesechosPeligrosos();
    					Query queryArea = crudServiceBean.getEntityManager().createQuery("select g from GeneradorDesechosPeligrosos g where g.id=:hwgeId");
    					queryArea.setParameter("hwgeId", idGenerador);
    					if(queryArea.getResultList().size()>0)
    					{
    						generador=(GeneradorDesechosPeligrosos) queryArea.getResultList().get(0);
//    						String sqlDir="select u.user_name from users u, areas a, suia_iii.roles_users ru, suia_iii.roles r where "
//    								+ "u.area_id = a.area_id AND  u.user_id = ru.user_id and ru.role_id = r.role_id and "
//    								+ "r.role_name = 'AUTORIDAD AMBIENTAL' and a.area_id = "+generador.getAreaResponsable().getId()+" and u.user_status = true";    						
    						
    						String sqlDir="select u.user_name from users  u "
    								+ "inner join areas a on a.area_id=u.area_id "
    								+ "inner join suia_iii.roles_users ru on ru.user_id=u.user_id "
    								+ "inner join suia_iii.roles r on r.role_id=ru.role_id "
    								+ "left join areas ap on ap.area_id = a.area_parent_id "
    								+ "where r.role_name IN ('AUTORIDAD AMBIENTAL') and "
    								+ "(a.area_name like '%"+generador.getAreaResponsable().getAreaName()+"%' or "
    								+ "ap.area_name like '%"+generador.getAreaResponsable().getAreaName()+"%') and u.user_status=true and ru.rous_status=true";
    						
    						
    						Query usname = crudServiceBean.getEntityManager().createNativeQuery(sqlDir);
    						List<Object>  resul = new ArrayList<Object>();
    						resul=usname.getResultList();
    						String valueUser="";
    						if (resul!=null) {
    							for(Object dir: resul)
    							{
    								valueUser=dir.toString();
//    								String sqlDirector="select dblink_exec('"+dblinkBpmsSuiaiii+"','update variableinstancelog set value=''"+valueUser+"'' where processinstanceid=''"+processinstanceid+"'' and variableid=''director''') as result";
//        							Query queryDirector = crudServiceBean.getEntityManager().createNativeQuery(sqlDirector);
//        							if(queryDirector.getResultList().size()>0)
//        								queryDirector.getSingleResult();
    							}
    							if(!valueUser.equals(cedulaactual))
    							{
    								Map<String, Object> params1 = new ConcurrentHashMap<String, Object>();
    								params1.put("director", valueUser);
    								try {
    									procesoFacade.modificarVariablesProceso(usuario, processinstanceid, params1);
    								} catch (JbpmException e) {
    									e.printStackTrace();
    								}
    							}
    						}	
    					}	   
    				}	
    				//------------------------------------fin de actualizar DIRECTOR

    			} catch (JbpmException e) {

    				JsfUtil.addMessageError("Error al reasignar la tarea");
    			}    	 					
    		}
    		else
    		{
    			JsfUtil.addMessageError("Error al reasignar la tarea, no tiene roles respectivos para Generado de desechos");
    		}
    	}
    	else
    	{
    		JsfUtil.addMessageError("Error al reasignar la tarea, no tiene roles respectivos para Generado de desechos");
    	}
    }
    
//    HIDROCARBUROS
    @SuppressWarnings("unchecked")
	public void reasigmenthyd(Usuario usuario, long taskid, String cedulaactual, long processinstanceid, String proyecto){
		
		String actualownerid="";
		Query sqlProcesosUser = crudServiceBean.getEntityManager().createNativeQuery(
		        "select * from  dblink('"+dblinkBpmsHyd+"','select actualowner_id from task where id=''"+taskid+"''') as (actualowner_id varchar)");     		
		List<Object>  resultProUser = new ArrayList<Object>();
		resultProUser=sqlProcesosUser.getResultList();
		if (resultProUser!=null) {		    	 
			for(Object a: resultProUser)
			{
				if(a!=null)
					actualownerid=a.toString();
			}

		}		
		Query sqlroles = crudServiceBean.getEntityManager().createNativeQuery(
    			"select u.user_name,r.role_name from users  u "
    					+ "inner join areas a on a.area_id=u.area_id " 
    					+ "inner join suia_iii.roles_users ru on ru.user_id=u.user_id "
    					+ "inner join suia_iii.roles r on r.role_id=ru.role_id "
    					+ "left join areas ap on ap.area_id = a.area_parent_id "
    					+ "where u.user_name='"+cedulaactual+"'");
		List<Object[]> resultListroles = (List<Object[]>) sqlroles.getResultList();
    	boolean estadoFacilitador=false;
    	if (resultListroles.size() > 0) {
    		for (int i = 0; i < resultListroles.size(); i++) {
    			Object[] roles = (Object[]) resultListroles.get(i);    					
    			if(roles[1].equals("FACILITADOR"))
    			{
    				estadoFacilitador=true;
    			}
    		}
    	}
    	if(estadoFacilitador)
    	{
    		hidrocarburosFacilitador(taskid,cedulaactual,actualownerid,processinstanceid,proyecto);
    		
    		Query sqlVariableid = crudServiceBean.getEntityManager().createNativeQuery(
    				"select * from  dblink('"+dblinkBpmsHyd+"','select variableid from variableinstancelog where processinstanceid=''"+processinstanceid+"'' and value=''"+actualownerid+"'' group by variableid order by 1') as (variableid varchar)"); 
    		List<Object>  resultListV = new ArrayList<Object>();
    		resultListV=sqlVariableid.getResultList();
    		Map<String, Object> params = new ConcurrentHashMap<String, Object>();
    		if (resultListV!=null) {		    	 
    			for(Object a: resultListV)
    			{
    				params.put(a.toString(), cedulaactual);
    			}
    			try {
					procesoFacade.modificarVariablesProcesoH(usuario, processinstanceid, params);
				} catch (JbpmException e) {
					System.out.println("Error en la tarea hidro:::"+taskid);
				}
    		}
    	}
    	else
    	{
//    		hidrocarburos(taskid,cedulaactual,actualownerid,processinstanceid);
    		
    		Query sqlVariableid = crudServiceBean.getEntityManager().createNativeQuery(
    				"select * from  dblink('"+dblinkBpmsHyd+"','select variableid from variableinstancelog where processinstanceid=''"+processinstanceid+"'' and value=''"+actualownerid+"'' group by variableid order by 1') as (variableid varchar)"); 
    		List<Object>  resultListV = new ArrayList<Object>();
    		resultListV=sqlVariableid.getResultList();
    		Map<String, Object> params = new ConcurrentHashMap<String, Object>();
    		if (resultListV!=null) {		    	 
    			for(Object a: resultListV)
    			{
    				params.put(a.toString(), cedulaactual);
    			}
    			try {
					procesoFacade.modificarVariablesProcesoH(usuario, processinstanceid, params);
				} catch (JbpmException e) {
					System.out.println("Error en la tarea hidro:::"+taskid);
				}
    		}    
    	}
		
    }
    public void hidrocarburos(long taskid,String cedulaactual,String cedulaanterior,long processinstanceid)
    {
    	try
    	{
    		String sql="select dblink_exec('"+dblinkBpmsHyd+"','update task set createdby_id=''"+cedulaactual+"'', actualowner_id=''"+cedulaactual+"'' where id=''"+taskid+"''') as result";
    		Query query = crudServiceBean.getEntityManager().createNativeQuery(sql);
    		if(query.getResultList().size()>0)
    			query.getSingleResult();

    		String sql1="select dblink_exec('"+dblinkBpmsHyd+"','update peopleassignments_potowners set entity_id=''"+cedulaactual+"'' where task_id=''"+taskid+"''') as result";
    		Query query1 = crudServiceBean.getEntityManager().createNativeQuery(sql1);
    		if(query1.getResultList().size()>0)
    			query1.getSingleResult();
    		
    		String sql2="select dblink_exec('"+dblinkBpmsHyd+"','update variableinstancelog set value=''"+cedulaactual+"'' where processinstanceid=''"+processinstanceid+"'' and value=''"+cedulaanterior+"''') as result";
    		Query query2 = crudServiceBean.getEntityManager().createNativeQuery(sql2);
    		if(query2.getResultList().size()>0)
    			query2.getSingleResult();
    	}
    	catch(ExecutionError e)
    	{
    		JsfUtil.addMessageError("Error al reasignar la tarea");
    	}
    }
    
    @SuppressWarnings("unchecked")
	public void hidrocarburosFacilitador(long taskid,String cedulaactual,String cedulaanterior,long processinstanceid,String proyecto)
    {
    	try
    	{

    		Integer idusuarioAnterior = null;
    		Integer idusuarioActual=null;
    		Query sqlusuarioAnt = crudServiceBean.getEntityManager().createNativeQuery(
    				"select * from  dblink('"+dblinkSuiaVerde+"',"
    						+ "'select id,nombreusuario from usuario where nombreusuario=''"+cedulaanterior+"''') as (id integer,nombreusuario text)");
    		List<Object>  resultuser = new ArrayList<Object>();
    		resultuser=sqlusuarioAnt.getResultList();
    		if (resultuser.size() > 0) {
        		for (int i = 0; i < resultuser.size(); i++) {
        			Object[] usuario = (Object[]) resultuser.get(i);    					
        			idusuarioAnterior=Integer.valueOf(usuario[0].toString());        			
        		}
        	}
    		Query sqlusuarioAct = crudServiceBean.getEntityManager().createNativeQuery(
    				"select * from  dblink('"+dblinkSuiaVerde+"',"
    						+ "'select id,nombreusuario from usuario where nombreusuario=''"+cedulaactual+"''') as (id integer,nombreusuario text)");
    		List<Object>  resultuserAct = new ArrayList<Object>();
    		resultuserAct=sqlusuarioAct.getResultList();
    		if (resultuserAct.size() > 0) {
        		for (int i = 0; i < resultuserAct.size(); i++) {
        			Object[] usuario = (Object[]) resultuserAct.get(i);    					
        			idusuarioActual=Integer.valueOf(usuario[0].toString());        			
        		}
        	}
    		
    		String sqlSLPPS="select dblink_exec('"+dblinkSuiaVerde+"','update suia_licensing.facilitator_instance_pps set fain_user_name=''"+cedulaactual+"''  where fain_user_name=''"+cedulaanterior+"'' and fain_id_instance_pps=''"+processinstanceid+"''') as result";
    		Query querySLPPS = crudServiceBean.getEntityManager().createNativeQuery(sqlSLPPS);
    		if(querySLPPS.getResultList().size()>0)
    			querySLPPS.getSingleResult();

    		String sqlIVPPS="select dblink_exec('"+dblinkSuiaVerde+"','update proyectofacilitador set usuario_id =''"+idusuarioActual+"'' where proyecto_id =''"+proyecto+"'' and usuario_id=''"+idusuarioAnterior+"''') as result";
    		Query queryIVPPS = crudServiceBean.getEntityManager().createNativeQuery(sqlIVPPS);
    		if(queryIVPPS.getResultList().size()>0)
    			queryIVPPS.getSingleResult();
    		
//    		String sql="select dblink_exec('"+dblinkBpmsHyd+"','update task set createdby_id=''"+cedulaactual+"'', actualowner_id=''"+cedulaactual+"'' where id=''"+taskid+"''') as result";
//    		Query query = crudServiceBean.getEntityManager().createNativeQuery(sql);
//    		if(query.getResultList().size()>0)
//    			query.getSingleResult();
//
//    		String sql1="select dblink_exec('"+dblinkBpmsHyd+"','update peopleassignments_potowners set entity_id=''"+cedulaactual+"'' where task_id=''"+taskid+"''') as result";
//    		Query query1 = crudServiceBean.getEntityManager().createNativeQuery(sql1);
//    		if(query1.getResultList().size()>0)
//    			query1.getSingleResult();
//    		
//    		String sql2="select dblink_exec('"+dblinkBpmsHyd+"','update variableinstancelog set value=''"+cedulaactual+"'' where processinstanceid=''"+processinstanceid+"'' and value=''"+cedulaanterior+"''') as result";
//    		Query query2 = crudServiceBean.getEntityManager().createNativeQuery(sql2);
//    		if(query2.getResultList().size()>0)
//    			query2.getSingleResult();    		
    		
    	}
    	catch(ExecutionError e)
    	{
    		JsfUtil.addMessageError("Error al reasignar la tarea");
    	}
    }
    
    @SuppressWarnings("unchecked")
	public boolean verificarroltecnicoRGD(String cedulaactual)
    {
    	Query sqlroles = crudServiceBean.getEntityManager().createNativeQuery(
    			"select u.user_name,r.role_name from users  u "
    					//+ "inner join areas a on a.area_id=u.area_id " 
    					+ "inner join suia_iii.roles_users ru on ru.user_id=u.user_id "
    					+ "inner join suia_iii.roles r on r.role_id=ru.role_id "
    					//+ "left join areas ap on ap.area_id = a.area_parent_id "
    					+ "where u.user_name='"+cedulaactual+"' and u.user_status=true and ru.rous_status=true");

    	List<Object[]> resultListroles = (List<Object[]>) sqlroles.getResultList();
    	if (resultListroles.size() > 0) {
    		for (int i = 0; i < resultListroles.size(); i++) {
    			Object[] roles = (Object[]) resultListroles.get(i);    					
    			if(roles[1].equals("TÉCNICO ANALISTA DE REGISTRO"))
    			{
    				return true;
    			}
    		}
    	}
    	return false;
    }
    
    @SuppressWarnings("unchecked")
	public String deploymentId(long taskid, String busqueda){
    	String result="";
    	try {
    		String dblink="";
    		if(busqueda.equals("S"))//S = suia-iii
    			dblink=dblinkBpmsSuiaiii;
    		else
    			dblink=dblinkBpmsHyd;
    		
    		Query sqlDeploymentId = crudServiceBean.getEntityManager().createNativeQuery(
    				"select * from  dblink('"+dblink+"','select deploymentid from task where id="+taskid+"') as (deploymentid varchar)"); 
    		List<Object>  resultListV = new ArrayList<Object>();
    		resultListV=sqlDeploymentId.getResultList();    		
    		if (resultListV!=null) {		    	 
    			for(Object a: resultListV)
    			{
    				result= a.toString();
    			}
    		}
    		return result;    		
    	}
    	catch (Exception e) {
    		JsfUtil.addMessageError("Error al reasignar la tarea");
    		return result;
    	}  
    }
    
    @SuppressWarnings("unchecked")
	public Date processInsLogEndDate(long processInstanceId){
    	Date result = null;
    	try {    				
    		Query sqlDeploymentId = crudServiceBean.getEntityManager().createNativeQuery(
    				"select * from  dblink('"+dblinkBpmsSuiaiii+"','select end_date from processinstancelog where processinstanceid="+processInstanceId+"') as (fin timestamp)"); 
    		List<Object>  resultListV = new ArrayList<Object>();
    		resultListV=sqlDeploymentId.getResultList();    		
    		if (resultListV!=null) {		    	 
    			for(Object a: resultListV)
    			{	
    				Timestamp time = (Timestamp) a;    				    				
    				Date date= new Date(time.getTime());
    				return date;
    			}
    		}
    		return result;    		
    	}
    	catch (Exception e) {
    		System.out.println("Error al recuperar fecha fin del proceso - processinstanceId:"+processInstanceId);
    		return result;
    	}  
    }
    
}
