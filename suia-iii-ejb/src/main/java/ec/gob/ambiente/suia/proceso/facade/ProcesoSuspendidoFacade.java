package ec.gob.ambiente.suia.proceso.facade;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import lombok.Getter;
import lombok.Setter;

import org.jbpm.process.audit.ProcessInstanceLog;
import org.jbpm.process.audit.VariableInstanceLog;
import org.kie.api.task.model.TaskSummary;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.suia.administracion.facade.ContactoFacade;
import ec.gob.ambiente.suia.administracion.facade.FeriadosFacade;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.AprobacionRequisitosTecnicosFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.FormasContacto;
import ec.gob.ambiente.suia.domain.Holiday;
import ec.gob.ambiente.suia.domain.ProcesoSuspendido;
import ec.gob.ambiente.suia.domain.ProcesoSuspendidoHistorico;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.AprobacionRequisitosTecnicos;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.notificacionautoridades.controllers.NotificacionAutoridadesController;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.utils.Constantes;

@Stateless
public class ProcesoSuspendidoFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	@EJB
	private ProcesoFacade procesoFacade;
	
	@EJB
	private ContactoFacade contactoFacade;
	
	@EJB 
	private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;
	
	@EJB
	private FeriadosFacade feriadosFacade;
	
	public String dblinkBpmsSuiaiii=Constantes.getDblinkBpmsSuiaiii();
	
	//@Getter
	//@Setter
	//private boolean proyectos30Dias=false;	
	
	@Getter
	@Setter
	private List<Holiday> listHoliday = new ArrayList<Holiday>();
	
	public String dblinkSuiaVerde=Constantes.getDblinkSuiaVerde();
	
	public String dblinkSuiaHidro=Constantes.getDblinkBpmsHyD();
	
	public List<ProcesoSuspendido> getProcesosSuspendidos()
	{
		@SuppressWarnings("unchecked")
		List<ProcesoSuspendido> resultList = (List<ProcesoSuspendido>) crudServiceBean
				.getEntityManager()
				.createQuery("From ProcesoSuspendido p where p.estado = true and p.suspendido=true")
				.getResultList();
		return resultList;
	}
		
	public ProcesoSuspendido guardar(ProcesoSuspendido procesoSuspendido,String userName){
		if(procesoSuspendido.getId()==null)
		{
			procesoSuspendido.setUsuarioCreacion(userName);
			procesoSuspendido.setFechaCreacion(new Date());
		}else{
			procesoSuspendido.setUsuarioModificacion(userName);
			procesoSuspendido.setFechaModificacion(new Date());
		}
		
		if(procesoSuspendido.getSuspendido()){
			procesoSuspendido.setDiasReactivados(null);
			procesoSuspendido.setFechaActivacion(null);
		}		
		procesoSuspendido=crudServiceBean.saveOrUpdate(procesoSuspendido);		
		ProcesoSuspendidoHistorico historico=new ProcesoSuspendidoHistorico();
		historico.setProcesoSuspendido(procesoSuspendido);
		historico.setCodigo(procesoSuspendido.getCodigo());
		historico.setDescripcion(procesoSuspendido.getDescripcion());
		historico.setTipoProyecto(procesoSuspendido.getTipoProyecto());
		historico.setUsuarioCreacion(procesoSuspendido.getUsuarioCreacion());
		historico.setUsuarioModificacion(procesoSuspendido.getUsuarioModificacion());
		historico.setFechaCreacion(procesoSuspendido.getFechaCreacion());
		historico.setFechaModificacion(procesoSuspendido.getFechaModificacion());
		historico.setSuspendido(procesoSuspendido.getSuspendido());
		historico.setEstado(procesoSuspendido.getEstado());
		historico.setDiasReactivados(procesoSuspendido.getDiasReactivados());
		historico.setFechaActivacion(procesoSuspendido.getFechaActivacion());
		crudServiceBean.saveOrUpdate(historico);
		return procesoSuspendido;
	}
	
	public ProcesoSuspendido getProcesoSuspendidoPorId(Integer id) {
		try {
			ProcesoSuspendido procesoSuspendido = (ProcesoSuspendido) crudServiceBean
					.getEntityManager()
					.createQuery("From ProcesoSuspendido p where p.id =:id order by 1 desc")
					.setParameter("id", id.intValue()).getResultList().get(0);		

			return procesoSuspendido;
		} catch (Exception e) {
			return null;
		}
	}
	
	public ProcesoSuspendido getProcesoSuspendidoPorCodigo(String codigo) {
		try {
			ProcesoSuspendido procesoSuspendido = (ProcesoSuspendido) crudServiceBean
					.getEntityManager()
					.createQuery("From ProcesoSuspendido p where p.codigo =:codigo and p.estado = true order by 1 desc")
					.setParameter("codigo", codigo).getResultList().get(0);		

			return procesoSuspendido;
		} catch (Exception e) {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<ProcesoSuspendido> getProcesoSuspendidoPorTipo(String tipoProyecto,boolean suspendido) {
		try {
			List<ProcesoSuspendido> procesoSuspendidos = (List<ProcesoSuspendido>) crudServiceBean
					.getEntityManager()
					.createQuery("From ProcesoSuspendido p where p.estado = true and p.tipoProyecto =:tipoProyecto and p.suspendido = :suspendido order by 1 desc")
					.setParameter("tipoProyecto", tipoProyecto)
					.setParameter("suspendido", suspendido).getResultList();		

			return procesoSuspendidos;
		} catch (Exception e) {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	private List<ProcesoSuspendido> getProcesosReactivados() {
		try {
			List<ProcesoSuspendido> procesoReactivados = (List<ProcesoSuspendido>) crudServiceBean
					.getEntityManager()
					.createQuery("From ProcesoSuspendido p where p.estado = true and p.suspendido = false and p.diasReactivados !=null order by 1")
					.getResultList();
			return procesoReactivados;
		} catch (Exception e) {
			return new ArrayList<ProcesoSuspendido>();
		}
	}

	public void suspenderProyectosReactivados() {
		try {
			List<ProcesoSuspendido> procesoReactivados =getProcesosReactivados();
			for (ProcesoSuspendido proyecto : procesoReactivados) {
				if(!tieneReactivacionDias(proyecto.getCodigo())){
					suspenderProyecto(proyecto.getCodigo(),proyecto.getDiasReactivados(),ProcesoSuspendido.TIPO_PROYECTO_REGURALIZACION);					
				}	
			}			
		} catch (Exception e) {			
			e.printStackTrace();
		}
	}
	
	@Deprecated
	public ProyectoLicenciamientoAmbiental getProyectoPorCodigo(String codigo,boolean estado) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("codigo", codigo);
		params.put("estado", estado);

		String queryString = "SELECT p.pren_id, p.pren_code, p.pren_status, p.pren_delete_reason "
				+ "FROM suia_iii.projects_environmental_licensing p "
				+ "WHERE p.pren_status = :estado AND p.pren_code = :codigo ";				

		List<Object> result = crudServiceBean.findByNativeQuery(queryString, params);	
		
		for (Object object : result) {			
			Object[] array=(Object[]) object;
			
			ProyectoLicenciamientoAmbiental proyecto=new ProyectoLicenciamientoAmbiental();		
			proyecto.setId((Integer)array[0]);			
			proyecto.setCodigo((String)array[1]);
			proyecto.setEstado((Boolean)array[2]);
			proyecto.setMotivoEliminar((String)array[3]);
			return proyecto;
		}

		return null;
	}
	
	public ProyectoLicenciamientoAmbiental getProyectoPorCodigo(String codigo) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("codigo", codigo);		

		String queryString = "SELECT p.pren_id, p.pren_code, p.pren_status, p.pren_delete_reason "
				+ "FROM suia_iii.projects_environmental_licensing p "
				+ "WHERE p.pren_code = :codigo ";				

		List<Object> result = crudServiceBean.findByNativeQuery(queryString, params);	
		
		for (Object object : result) {			
			Object[] array=(Object[]) object;
			
			ProyectoLicenciamientoAmbiental proyecto=new ProyectoLicenciamientoAmbiental();		
			proyecto.setId((Integer)array[0]);			
			proyecto.setCodigo((String)array[1]);
			proyecto.setEstado((Boolean)array[2]);
			proyecto.setMotivoEliminar((String)array[3]);
			return proyecto;
		}

		return null;
	}
	
	public int setEstadoProyecto(Integer id,boolean estado) {
		
		String queryString = "update suia_iii.projects_environmental_licensing set pren_status = "+estado+",pren_delete_reason="+(estado?"null":"'SUSPENDIDO'")+" where pren_id = "+id;
		return crudServiceBean.getEntityManager().createNativeQuery(queryString).executeUpdate();
			
	}
	
	public int setEstadoProyecto(String codigo,boolean estado) {
		
		String queryString = "update suia_iii.projects_environmental_licensing set pren_status = "+estado+",pren_delete_reason="+(estado?"null":"'SUSPENDIDO'")+" where pren_code = '"+codigo+"'";
		return crudServiceBean.getEntityManager().createNativeQuery(queryString).executeUpdate();
			
	}
	
	public int setEstadoProyectoRGD(Integer id,boolean estado) {
		
		String queryString = "update suia_iii.hazardous_wastes_generators set hwge_status = "+estado+",hwge_delete_reason="+(estado?"null":"'SUSPENDIDO'")+" where hwge_id = "+id;
		return crudServiceBean.getEntityManager().createNativeQuery(queryString).executeUpdate();
			
	}
	
	public int setEstadoProyectoART(Integer id,boolean estado) {
		
		String queryString = "update suia_iii.approval_technical_requirements set apte_status = "+estado+",apte_delete_reason="+(estado?"null":"'SUSPENDIDO'")+" where apte_id = "+id;
		return crudServiceBean.getEntityManager().createNativeQuery(queryString).executeUpdate();
			
	}
	
	public int setEstadoProyectoRcoa(Integer id,boolean estado) {
		
		java.util.Date now = new Date();		
		Timestamp fecha = new java.sql.Timestamp(now.getTime());
		
		String queryString = "update coa_mae.project_licencing_coa set prco_status = "+estado+ ", prco_deactivation_date = '" + fecha + "', prco_delete_reason="+(estado?"null":"'SUSPENDIDO'")+" where prco_id = "+id;
		return crudServiceBean.getEntityManager().createNativeQuery(queryString).executeUpdate();
			
	}
	
	public boolean proyectoEnTramite(String codigoProyecto, Usuario usuario) {
		try {
			
			List<ProcessInstanceLog> process = procesoFacade.getProcessInstancesLogsVariableValue(usuario,Constantes.VARIABLE_PROCESO_TRAMITE, codigoProyecto);
			for (ProcessInstanceLog processInstanceLog : process) {
				if(processInstanceLog.getProcessId().equals(Constantes.NOMBRE_PROCESO_LICENCIA_AMBIENTAL) || processInstanceLog.getProcessId().equals(Constantes.NOMBRE_PROCESO_CATEGORIA2V2)|| processInstanceLog.getProcessId().equals(Constantes.NOMBRE_PROCESO_REQUISITOS_PREVIOS)){
					List<TaskSummary> listTS=  procesoFacade.obtenerTareaReserved(processInstanceLog.getId(), usuario);
					if(!listTS.isEmpty())
						return true;
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	public List<String> listaTecnicos(String codigoProyecto, Usuario usuario) {
		List<String> listaTecnicos=new ArrayList<String>();
		try {
			Map<String, String> variableTecnico=new HashMap<String,String>();
			variableTecnico.put(Constantes.NOMBRE_PROCESO_LICENCIA_AMBIENTAL, "u_TecnicoResponsable");
			variableTecnico.put(Constantes.NOMBRE_PROCESO_PARTICIPACION_SOCIAL, "u_Tecnico");
			variableTecnico.put(Constantes.NOMBRE_PROCESO_GENERADOR_DESECHOS, "tecnico");
			variableTecnico.put(Constantes.NOMBRE_PROCESO_REQUISITOS_PREVIOS, "u_ResponsablePronunciamientoForestal;u_ResponsablePronunciamientoBiodiversidad");
			variableTecnico.put(Constantes.NOMBRE_PROCESO_CERTIFICADO_AMBIENTAL, "usuario_jefe_area;usuario_tecnico_forestal;usuario_tecnico_patrimonio");
									
			List<ProcessInstanceLog> process = procesoFacade.getProcessInstancesLogsVariableValue(usuario,Constantes.VARIABLE_PROCESO_TRAMITE, codigoProyecto);
			for (ProcessInstanceLog processInstanceLog : process) {
				List<TaskSummary> listTS=  procesoFacade.obtenerTareaReserved(processInstanceLog.getId(), usuario);
				if(!listTS.isEmpty())
				{
					String[] nombreVariables=variableTecnico.get(processInstanceLog.getProcessId()).split(";");
					for (String variable : nombreVariables) {						
						Map<String, Object> variablesProceso= procesoFacade.recuperarVariablesProceso(usuario, processInstanceLog.getProcessInstanceId());
						String tecnico=(String)variablesProceso.get(variable);						
						if(tecnico!=null)
							listaTecnicos.add(tecnico);
					}
				}
			}
		} catch (Exception e) {
			return new ArrayList<String>();
		}
		return listaTecnicos;
	}
	
	
	
	/**
	 * 
	 * @param proyecto
	 * @param usuario
	 * @param suspender(true) o reactivar(false)
	 * @return
	 */
	public boolean modificarPropietarioTareas(String codigoProyecto, Usuario usuario,boolean suspender) {
		boolean eliminar=false;
		try {
			try {
				List<ProcessInstanceLog> process = procesoFacade.getProcessInstancesLogsVariableValue(usuario,Constantes.VARIABLE_PROCESO_TRAMITE, codigoProyecto);
				for (ProcessInstanceLog processInstanceLog : process) {
					if(processInstanceLog.getProcessName().equals("Eliminar proyecto")){
						return false;
					}				
				}				
				
				/*boolean procesoLicencia=false;
				boolean procesoEvaluacionSocial=false;
				for (ProcessInstanceLog processInstanceLog : process) {
					if(processInstanceLog.getProcessName().equals("Evaluacion Social")){
						procesoEvaluacionSocial= true;
					}
					if(processInstanceLog.getProcessName().equals("Licencia Ambiental")){
						procesoLicencia= true;
					}
				}				
				
				if (usuario == null){
					if(procesoLicencia && procesoEvaluacionSocial && suspender){
						return false;
						}
					}else{*/
				for (ProcessInstanceLog processInstanceLog : process) {
					try {
						if(processInstanceLog.getStatus()==1) {
//							List<TaskSummary> listTS=  procesoFacade.obtenerTareaReserved(processInstanceLog.getId(), usuario);
							List<TaskSummary> listTS=  procesoFacade.obtenerTareaReserved(processInstanceLog.getProcessInstanceId(), usuario);
							for (TaskSummary taskSummary : listTS) {
								String sql="select dblink_exec('"+dblinkBpmsSuiaiii+"','update task set actualowner_id="+(suspender?"null":"createdby_id")+" where id=''"+taskSummary.getId()+"''') as result";								
								Query query = crudServiceBean.getEntityManager().createNativeQuery(sql);
								if(query.getResultList().size()>0){
									query.getSingleResult();
									eliminar=true;
								}
								eliminar=true;
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				//}
			} catch (JbpmException jex) {
				jex.printStackTrace();
			}			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return eliminar;
	}
	
	public boolean modificarPropietarioTareasRGD(String codigoProyecto, Usuario usuario,boolean suspender,boolean reactivarPago,String numeroTramitePago,String ip) {
		boolean eliminar=false;
		try {
			try {
				List<ProcessInstanceLog> process = procesoFacade.getProcessInstancesLogsVariableValue(usuario,Constantes.VARIABLE_PROCESO_TRAMITE, codigoProyecto);
				for (ProcessInstanceLog processInstanceLog : process) {
					try {
						
						if(processInstanceLog.getProcessName().equals("Registro de generador de desechos especiales y peligrosos")){
							if(processInstanceLog.getStatus()==1) {
								List<TaskSummary> listTS=  procesoFacade.obtenerTareaReserved(processInstanceLog.getId(), usuario);
								for (TaskSummary taskSummary : listTS) {
									String sql="select dblink_exec('"+dblinkBpmsSuiaiii+"','update task set actualowner_id="+(suspender?"null":"createdby_id")+" where id=''"+taskSummary.getId()+"''') as result";								
									Query query = crudServiceBean.getEntityManager().createNativeQuery(sql);
									if(query.getResultList().size()>0){
										query.getSingleResult();
										eliminar=true;
									}
									eliminar=true;
								}
							}
						}						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (JbpmException jex) {
				jex.printStackTrace();
			}			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		if(eliminar && suspender && reactivarPago && numeroTramitePago!=null)
		{
			String sql="select * from dblink('"+dblinkSuiaVerde+"',"
					+ "'select h.id_online_payment_historical,h.online_payment_id,h.retired_value,h.tramit_number,p.used_value  "
					+ "from online_payment.online_payments_historical h "
					+ "inner join online_payment.online_payments p on (p.id_online_payment=h.online_payment_id)  "
					//+ "where h.project_id=''"+codigoProyecto+"'' "
					+ "where h.tramit_number=''"+numeroTramitePago+"'' "
					+ "order by 1 desc limit 1')"
					+ "as (id_online_payment_historical integer,online_payment_id integer,retired_value text,tramit_number text,used_value text)";
			Query queryPago = crudServiceBean.getEntityManager().createNativeQuery(sql);
			List<Object[]> result = (List<Object[]>) queryPago.getResultList();
	    	if (result.size() > 0) {
	    		for (int i = 0; i < result.size(); i++) {
	    			Object[] pagoHistorico = (Object[]) result.get(i);
	    			Integer idPagoHistorico=(Integer)pagoHistorico[0];
	    			Integer idPago=(Integer)pagoHistorico[1];
//	    			String valorS=(String)pagoHistorico[2];
	    			String tramite=(String)pagoHistorico[3];
	    			String valorUsadoS=(String)pagoHistorico[4];
	    			
	    			Double valor=180.0;
	    			String valorS="180.0";
//	    			Double valor=Double.valueOf(valorS);	    			
	    			if(valor>0)
	    			{
	    				String sqlInsert="select dblink_exec('"+dblinkSuiaVerde+"',"
	    						+ "'insert into online_payment.online_payments_historical  "
	    						+ "(id_online_payment_historical, description, project_id, retired_value,sender_ip, tramit_number, update_date, value_updated, online_payment_id) "
	    						+ "VALUES (nextval(''online_payment.seq_id_online_payments_historical''), ''Se reactiva la transacción por desactivación del proyecto'', "
	    						+ "''"+codigoProyecto+"'', ''0.00'',''"+ip+"'', ''"+tramite+"'', clock_timestamp(),  ''"+valorS+"'',"+idPago+")') as result";
	    				crudServiceBean.getEntityManager().createNativeQuery(sqlInsert).getResultList();	    				
	    				
	    				Double valorUsado=Double.valueOf(valorUsadoS)-valor;
	    				valorUsado=valorUsado>=0.0?valorUsado:0.0;
	    				
	    				String sqlUpdate="select dblink_exec('"+dblinkSuiaVerde+"',"
	    						+ "'update online_payment.online_payments  "
	    						+ "SET is_used = false, used_value = ''"+valorUsado+"'' "
	    						+ "WHERE tramit_number = ''"+tramite+"''') as result ";	    						
	    				crudServiceBean.getEntityManager().createNativeQuery(sqlUpdate).getResultList();
	    			}	    			
	    		}
	    	}
		}
		
		return eliminar;
	}
	
	public double valorPagoRGD(String numeroTramitePago)
	{
		String sql="select * from dblink('"+dblinkSuiaVerde+"',"
				+ "'select h.id_online_payment_historical,h.online_payment_id,h.retired_value,h.tramit_number,p.used_value  "
				+ "from online_payment.online_payments_historical h "
				+ "inner join online_payment.online_payments p on (p.id_online_payment=h.online_payment_id)  "
				+ "where h.tramit_number=''"+numeroTramitePago+"'' "
				+ "order by 1 desc limit 1')"
				+ "as (id_online_payment_historical integer,online_payment_id integer,retired_value text,tramit_number text,used_value text)";
		Query queryPago = crudServiceBean.getEntityManager().createNativeQuery(sql);
		List<Object[]> result = (List<Object[]>) queryPago.getResultList();
		Double valor=0.0;
		if (result.size() > 0) {
    		for (int i = 0; i < result.size(); i++) {
    			Object[] pagoHistorico = (Object[]) result.get(i);
    			Integer idPagoHistorico=(Integer)pagoHistorico[0];
    			Integer idPago=(Integer)pagoHistorico[1];
    			String valorS=(String)pagoHistorico[2];
    			String tramite=(String)pagoHistorico[3];
    			String valorUsadoS=(String)pagoHistorico[4];
    			
    			valor=Double.valueOf(valorS);
    		}
		}
		return valor;
	}
	
	@SuppressWarnings("unchecked")
	public boolean modificarPropietarioTareas4Categorias(String codigoProyecto, String motivo,boolean suspender) {				
		//			eliminar proyecto jbpmdb-hidrocarburos
		String sqlHyd="select dblink_exec('"+dblinkSuiaVerde+"','update proyectolicenciaambiental set estadoproyecto="+!suspender+","+(suspender?"eliminadopor=null,":"")+"motivoeliminar=''"+motivo+"'',fechaestadoproyecto=now() where id=''"+codigoProyecto+"''') as result";
		Query query = crudServiceBean.getEntityManager().createNativeQuery(sqlHyd);
		if(query.getResultList().size()>0)
		{
			query.getSingleResult();					
			String sqltaskbpmhyd="select * from dblink('"+dblinkSuiaHidro+"',"
					+ "'select id from task where processinstanceid "
					+ "in(select processinstanceid from variableinstancelog where value=''"+codigoProyecto+"'') "
					+ "and status in(''InProgress'',''Ready'',''Reserved'')') as (id integer)";
			Query queryProceso = crudServiceBean.getEntityManager().createNativeQuery(sqltaskbpmhyd);
			List<Object>  resultPro = new ArrayList<Object>();
    		resultPro=queryProceso.getResultList();
    		if (resultPro!=null) {		    	 
    			for(Object taskId: resultPro)
    			{
    				String sqlupdateTask="select dblink_exec('"+dblinkSuiaHidro+"','update task set actualowner_id="+(suspender?"null":"createdby_id")+",status="+(suspender?"''Exited''":"(select coalesce(max(status),''Reserved'') from bamtasksummary where taskid ="+taskId+")")+" where id="+taskId+"') as result";
    				Query queryTask = crudServiceBean.getEntityManager().createNativeQuery(sqlupdateTask);
    				queryTask.getSingleResult();
    			}
    		}
    		if(suspender)
    		{
    			String sqllistfacilitadores="select * from dblink('"+dblinkSuiaVerde+"',"
    					+ "'select u.nombreusuario,upper(p.nombresapellidos),c.valor "
    					+ "from proyectofacilitador f,usuario u,persona p,contacto c "
    					+ "where f.aceptaproyecto=''SI'' and f.proyecto_id=''"+codigoProyecto+"'' "
    					+ "and f.usuario_id=u.id and u.nombreusuario=p.pin and c.entidad=p.id and c.tipocontacto=''EMAIL''')"
    					+ "as (nombreusuario text,nombresapellidos text,valor text)";
    			Query queryFacilitadores = crudServiceBean.getEntityManager().createNativeQuery(sqllistfacilitadores);
    			List<Object[]> resultListFacilitadores = (List<Object[]>) queryFacilitadores.getResultList();
    	    	if (resultListFacilitadores.size() > 0) {
    	    		for (int i = 0; i < resultListFacilitadores.size(); i++) {
    	    			Object[] facilitador = (Object[]) resultListFacilitadores.get(i);
    	    			NotificacionAutoridadesController email=new NotificacionAutoridadesController();
    	    			email.sendEmailBajaProyectoFacilitadores("Notificaci&oacute;n", facilitador[2].toString(), codigoProyecto, facilitador[1].toString());
    	    		}
    	    	}
    		}
    		return true;
		}
		
		return false;
	}
	
	@SuppressWarnings("unchecked")
	@Deprecated
	public boolean modificarPropietarioTareasHidrocarburos_(String codigoProyecto, String motivo) {				
		//			eliminar proyecto jbpmdb-hidrocarburos
		String sqlHyd="select dblink_exec('"+dblinkSuiaVerde+"','update proyectolicenciaambiental set estadoproyecto=false,motivoeliminar=''"+motivo+"'',fechaestadoproyecto=now() where id=''"+codigoProyecto+"''') as result";
		Query query = crudServiceBean.getEntityManager().createNativeQuery(sqlHyd);
		if(query.getResultList().size()>0)
		{
			query.getSingleResult();					
			String sqltaskbpmhyd="select * from dblink('"+dblinkSuiaHidro+"',"
					+ "'select id from task where processinstanceid "
					+ "in(select processinstanceid from variableinstancelog where value=''"+codigoProyecto+"'') "
					+ "and status in(''InProgress'',''Ready'',''Reserved'')') as (id integer)";
			Query queryProceso = crudServiceBean.getEntityManager().createNativeQuery(sqltaskbpmhyd);
			List<Object>  resultPro = new ArrayList<Object>();
    		resultPro=queryProceso.getResultList();
    		if (resultPro!=null) {		    	 
    			for(Object a: resultPro)
    			{
    				String sqlupdateTask="select dblink_exec('"+dblinkSuiaHidro+"','update task set actualowner_id=null,status=''Exited'' where id="+a+"') as result";
    				Query queryTask = crudServiceBean.getEntityManager().createNativeQuery(sqlupdateTask);
    				queryTask.getSingleResult();
    			}
    		}
			String sqllistfacilitadores="select * from dblink('"+dblinkSuiaVerde+"',"
					+ "'select u.nombreusuario,upper(p.nombresapellidos),c.valor "
					+ "from proyectofacilitador f,usuario u,persona p,contacto c "
					+ "where f.aceptaproyecto=''SI'' and f.proyecto_id=''"+codigoProyecto+"'' "
					+ "and f.usuario_id=u.id and u.nombreusuario=p.pin and c.entidad=p.id and c.tipocontacto=''EMAIL''')"
					+ "as (nombreusuario text,nombresapellidos text,valor text)";
			Query queryFacilitadores = crudServiceBean.getEntityManager().createNativeQuery(sqllistfacilitadores);
			List<Object[]> resultListFacilitadores = (List<Object[]>) queryFacilitadores.getResultList();
	    	if (resultListFacilitadores.size() > 0) {
	    		for (int i = 0; i < resultListFacilitadores.size(); i++) {
	    			Object[] facilitador = (Object[]) resultListFacilitadores.get(i);
	    			NotificacionAutoridadesController email=new NotificacionAutoridadesController();
	    			email.sendEmailBajaProyectoFacilitadores("Notificaci&oacute;n", facilitador[2].toString(), codigoProyecto, facilitador[1].toString());
	    		}
	    	}
		}
		
		return true;
	}
	
	public boolean envioNotificacion90Dias(Usuario usuario, ProyectoLicenciamientoAmbiental proyecto,Integer numDias) {		
		try {
			List<Contacto> contactos = contactoFacade.buscarUsuarioNativeQuery(usuario.getNombre());
			
			if(contactos.size()==0){
				contactos = contactoFacade.buscarUsuarioNativeQuery(usuario.getNombre());
			}
			
			String email= null;
			String nombreProponente=null;
			
			for (Contacto contacto : contactos) {
				if (contacto.getFormasContacto().getId().equals(FormasContacto.EMAIL)){
					email=contacto.getValor();
					nombreProponente=contacto.getOrganizacion()!=null?contacto.getOrganizacion().getNombre():contacto.getPersona().getNombre();
					
					if(nombreProponente!=null)
					break;
				}
			}

			Usuario usuarioEnvio = new Usuario();
			usuarioEnvio.setNombrePersona(Constantes.SIGLAS_SITEAA);
			NotificacionAutoridadesController mail_a = new NotificacionAutoridadesController();
			mail_a.sendEmailNotificacion90Dias(email, "NOTIFICACIÓN DE ARCHIVO DE PROYECTO", "Este correo fue enviado usando JavaMail", nombreProponente, proyecto.getCodigo(), proyecto.getNombre(), nombreProponente,numDias, usuario, usuarioEnvio);
			Thread.sleep(2000);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
	public boolean envioNotificacion90DiasART(Usuario usuario,String codigo, String nombre,boolean proyecto90dias) {		
		try {
			List<Contacto> contactos = contactoFacade.buscarUsuarioNativeQuery(usuario.getNombre());
			
			if(contactos.size()==0){
				contactos = contactoFacade.buscarUsuarioNativeQuery(usuario.getNombre());
			}
			
			String email= null;
			String nombreProponente=null;
			
			for (Contacto contacto : contactos) {
				if (contacto.getFormasContacto().getId().equals(FormasContacto.EMAIL)){
					email=contacto.getValor();
					nombreProponente=contacto.getOrganizacion()!=null?contacto.getOrganizacion().getNombre():contacto.getPersona().getNombre();
					
					if(nombreProponente!=null)
					break;
				}
			}
			Usuario usuarioEnvio = new Usuario();
			usuarioEnvio.setNombrePersona(Constantes.SIGLAS_SITEAA);
			NotificacionAutoridadesController mail_a = new NotificacionAutoridadesController();			
			mail_a.sendEmailNotificacion90Dias(email, "NOTIFICACIÓN DE ARCHIVO DE PROYECTO", "Este correo fue enviado usando JavaMail", nombreProponente, codigo, nombre, nombreProponente,90, usuario, usuarioEnvio);
			Thread.sleep(2000);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	public boolean envioNotificacionAntesDeArchivar(Usuario usuario, ProyectoLicenciamientoAmbiental proyecto) {		
		try {
			List<Contacto> contactos = contactoFacade.buscarUsuarioNativeQuery(usuario.getNombre());
			
			if(contactos.size()==0){
				contactos = contactoFacade.buscarUsuarioNativeQuery(usuario.getNombre());
			}
			
			String email= null;
			String nombreProponente=null;
			
			for (Contacto contacto : contactos) {
				if (contacto.getFormasContacto().getId().equals(FormasContacto.EMAIL)){
					email=contacto.getValor();
					nombreProponente=contacto.getOrganizacion()!=null?contacto.getOrganizacion().getNombre():contacto.getPersona().getNombre();
					
					if(nombreProponente!=null)
					break;
				}
			}

			Usuario usuarioEnvio = new Usuario();
			usuarioEnvio.setNombrePersona(Constantes.SIGLAS_SITEAA);
			NotificacionAutoridadesController mail_a = new NotificacionAutoridadesController();
			mail_a.sendEmailNotificacionAntesArchivar(email, "NOTIFICACIÓN DE ARCHIVO DE PROYECTO", "Este correo fue enviado usando JavaMail", nombreProponente, proyecto.getCodigo(), proyecto.getNombre(), nombreProponente, usuario, usuarioEnvio);
			Thread.sleep(2000);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public void obtenerProyectosMayor90DiasRegistro(){
		String sql=" select * from dblink('"+dblinkBpmsSuiaiii+"','select distinct(value),processid from variableinstancelog where "
				 + " processinstanceid in( SELECT t.processinstanceid from task t, variableinstancelog v where t.status not in "
				 + " (''Completed'',''Exited'',''Ready'') and (SELECT 1+count (*) as days FROM generate_series(0, "
				 + " (cast(now() as date) - cast(t.activationtime as date))) i WHERE date_part(''isodow'', cast(now() as date) + i) NOT IN (6,7))>=90 and "
				 + " t.actualowner_id is not null and t.createdby_id is not null and t.formname is not null and t.processid in "
				 + " (select distinct(processid) from task where processid is not null) and t.processinstanceid=v.processinstanceid "
				 + " and t.actualowner_id=v.value and (v.variableid in(''proponente'',''u_Proponente'',''u_Promotor'',''sujetoControl'')) "
				 + " order by 1) and variableid=''tramite'' and value not in(select v.value from variableinstancelog v, processinstancelog p"
				 + " where p.processinstanceid=v.processinstanceid and v.variableid=''tramite'' and v.processid "
				 + " in(''mae-procesos.Eliminarproyecto'',''mae-procesos.GeneradorDesechos'') and p.status!=2)') as "
				 + " (tramite varchar, proceso varchar) where proceso ='mae-procesos.registro-ambiental' or proceso ='mae-procesos.RegistroAmbiental' order by 1 desc limit 10";								
		
		Query query = crudServiceBean.getEntityManager().createNativeQuery(sql);
		List<Object>  resultList = new ArrayList<Object>();
		resultList=query.getResultList();
		List<String[]>listaCodigosProyectos= new ArrayList<String[]>();		
		if (resultList.size() > 0) {
			System.out.println("lista de proyectos: " + resultList.size());
			for (Object a : resultList) {
				Object[] row = (Object[]) a;
				listaCodigosProyectos.add(new String[] { (String) row[0],(String) row[1] });
			}
		}
		for (String[] codigoProyecto : listaCodigosProyectos) {
			suspenderProyectoRegistroAmbiental(codigoProyecto[0].toString(),ProcesoSuspendido.TIPO_PROYECTO_REGURALIZACION);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void obtenerProyectosMayor90DiasART(){
		String sql=" select * from dblink('"+dblinkBpmsSuiaiii+"','select distinct(value),processid from variableinstancelog where "
				 + " processinstanceid in( SELECT t.processinstanceid from task t, variableinstancelog v where t.status not in "
				 + " (''Completed'',''Exited'',''Ready'') and (SELECT 1+count (*) as days FROM generate_series(0, "
				 + " (cast(now() as date) - cast(t.activationtime as date))) i WHERE date_part(''isodow'', cast(now() as date) + i) NOT IN (6,7))>=90 and "
				 + " t.actualowner_id is not null and t.createdby_id is not null and t.formname is not null and t.processid in "
				 + " (select distinct(processid) from task where processid is not null) and t.processinstanceid=v.processinstanceid "
				 + " and t.actualowner_id=v.value and (v.variableid in(''proponente'',''u_Proponente'',''u_Promotor'',''sujetoControl'')) "
				 + " order by 1) and variableid=''tramite'' and value not in(select v.value from variableinstancelog v, processinstancelog p"
				 + " where p.processinstanceid=v.processinstanceid and v.variableid=''tramite'' and v.processid "
				 + " in(''mae-procesos.Eliminarproyecto'',''mae-procesos.GeneradorDesechos'') and p.status!=2)') as "
				 + " (tramite varchar, proceso varchar) where proceso ='Suia.AprobracionRequisitosTecnicosGesTrans2' and tramite like '%MAE-RA%' order by 1 desc limit 15";								
		
		Query query = crudServiceBean.getEntityManager().createNativeQuery(sql);
		List<Object>  resultList = new ArrayList<Object>();
		resultList=query.getResultList();
		List<String[]>listaCodigosProyectos= new ArrayList<String[]>();		
		if (resultList.size() > 0) {
			System.out.println("lista de proyectos: " + resultList.size());
			for (Object a : resultList) {
				Object[] row = (Object[]) a;
				listaCodigosProyectos.add(new String[] { (String) row[0],(String) row[1] });
			}
		}
		for (String[] codigoProyecto : listaCodigosProyectos) {
			suspenderProyectoART(codigoProyecto[0].toString(),ProcesoSuspendido.TIPO_PROYECTO_REGURALIZACION);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void obtenerProyectosMayor90DiasLicencia(){
		String sql=" select * from dblink('"+dblinkBpmsSuiaiii+"','select distinct(value),processid from variableinstancelog where "
				 + " processinstanceid in( SELECT t.processinstanceid from task t, variableinstancelog v where t.status not in "
				 + " (''Completed'',''Exited'',''Ready'') and (SELECT 1+count (*) as days FROM generate_series(0, "
				 + " (cast(now() as date) - cast(t.activationtime as date))) i WHERE date_part(''isodow'', cast(now() as date) + i) NOT IN (6,7))>=90 and "
				 + " t.actualowner_id is not null and t.createdby_id is not null and t.formname is not null and t.processid in "
				 + " (select distinct(processid) from task where processid is not null) and t.processinstanceid=v.processinstanceid "
				 + " and t.actualowner_id=v.value and (v.variableid in(''proponente'',''u_Proponente'',''u_Promotor'',''sujetoControl'')) "
				 + " order by 1) and variableid=''tramite'' and value not in(select v.value from variableinstancelog v, processinstancelog p"
				 + " where p.processinstanceid=v.processinstanceid and v.variableid=''tramite'' and v.processid "
				 + " in(''mae-procesos.Eliminarproyecto'',''mae-procesos.GeneradorDesechos'') and p.status!=2)') as "
				 + " (tramite varchar, proceso varchar) where proceso ='SUIA.LicenciaAmbiental' order by 1 desc limit 10 ";								
		
		Query query = crudServiceBean.getEntityManager().createNativeQuery(sql);
		List<Object>  resultList = new ArrayList<Object>();
		resultList=query.getResultList();
		List<String[]>listaCodigosProyectos= new ArrayList<String[]>();		
		if (resultList.size() > 0) {
			System.out.println("lista de proyectos: " + resultList.size());
			for (Object a : resultList) {
				Object[] row = (Object[]) a;
				listaCodigosProyectos.add(new String[] { (String) row[0],(String) row[1] });
			}
		}
		for (String[] codigoProyecto : listaCodigosProyectos) {
			suspenderProyectoLicenciaAmbiental(codigoProyecto[0].toString(),ProcesoSuspendido.TIPO_PROYECTO_REGURALIZACION);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void obtenerProyectosMayor90DiasEvaluacionSocial(){
		String sql=" select * from dblink('"+dblinkBpmsSuiaiii+"','select distinct(value),processid from variableinstancelog where "
				 + " processinstanceid in( SELECT t.processinstanceid from task t, variableinstancelog v where t.status not in "
				 + " (''Completed'',''Exited'',''Ready'') and (SELECT 1+count (*) as days FROM generate_series(0, "
				 + " (cast(now() as date) - cast(t.activationtime as date))) i WHERE date_part(''isodow'', cast(now() as date) + i) NOT IN (6,7))>=90 and "
				 + " t.actualowner_id is not null and t.createdby_id is not null and t.formname is not null and t.processid in "
				 + " (select distinct(processid) from task where processid is not null) and t.processinstanceid=v.processinstanceid "
				 + " and t.actualowner_id=v.value and (v.variableid in(''proponente'',''u_Proponente'',''u_Promotor'',''sujetoControl'')) "
				 + " order by 1) and variableid=''tramite'' and value not in(select v.value from variableinstancelog v, processinstancelog p"
				 + " where p.processinstanceid=v.processinstanceid and v.variableid=''tramite'' and v.processid "
				 + " in(''mae-procesos.Eliminarproyecto'',''mae-procesos.GeneradorDesechos'') and p.status!=2)') as "
				 + " (tramite varchar, proceso varchar) where proceso ='suia.participacion-social' order by 1 desc limit 5 ";								
		
		Query query = crudServiceBean.getEntityManager().createNativeQuery(sql);
		List<Object>  resultList = new ArrayList<Object>();
		resultList=query.getResultList();
		List<String[]>listaCodigosProyectos= new ArrayList<String[]>();		
		if (resultList.size() > 0) {
			System.out.println("lista de proyectos: " + resultList.size());
			for (Object a : resultList) {
				Object[] row = (Object[]) a;
				listaCodigosProyectos.add(new String[] { (String) row[0],(String) row[1] });
			}
		}
		for (String[] codigoProyecto : listaCodigosProyectos) {
			suspenderProyectoEvaluacionSocial(codigoProyecto[0].toString(),ProcesoSuspendido.TIPO_PROYECTO_REGURALIZACION);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void obtenerProyectosMayor90DiasViabilidad(){
		String sql=" select * from dblink('"+dblinkBpmsSuiaiii+"','select distinct(value),processid from variableinstancelog where "
				 + " processinstanceid in( SELECT t.processinstanceid from task t, variableinstancelog v where t.status not in "
				 + " (''Completed'',''Exited'',''Ready'') and (SELECT 1+count (*) as days FROM generate_series(0, "
				 + " (cast(now() as date) - cast(t.activationtime as date))) i WHERE date_part(''isodow'', cast(now() as date) + i) NOT IN (6,7))>=90 and "
				 + " t.actualowner_id is not null and t.createdby_id is not null and t.formname is not null and t.processid in "
				 + " (select distinct(processid) from task where processid is not null) and t.processinstanceid=v.processinstanceid "
				 + " and t.actualowner_id=v.value and (v.variableid in(''proponente'',''u_Proponente'',''u_Promotor'',''sujetoControl'')) "
				 + " order by 1) and variableid=''tramite'' and value not in(select v.value from variableinstancelog v, processinstancelog p"
				 + " where p.processinstanceid=v.processinstanceid and v.variableid=''tramite'' and v.processid "
				 + " in(''mae-procesos.Eliminarproyecto'',''mae-procesos.GeneradorDesechos'') and p.status!=2)') as "
				 + " (tramite varchar, proceso varchar) where proceso ='mae-procesos.RequisitosPrevios' order by 1 desc limit 15 ";								
		
		Query query = crudServiceBean.getEntityManager().createNativeQuery(sql);
		List<Object>  resultList = new ArrayList<Object>();
		resultList=query.getResultList();
		List<String[]>listaCodigosProyectos= new ArrayList<String[]>();		
		if (resultList.size() > 0) {
			System.out.println("lista de proyectos: " + resultList.size());
			for (Object a : resultList) {
				Object[] row = (Object[]) a;
				listaCodigosProyectos.add(new String[] { (String) row[0],(String) row[1] });
			}
		}
		for (String[] codigoProyecto : listaCodigosProyectos) {
			suspenderProyectoLicenciaAmbiental(codigoProyecto[0].toString(),ProcesoSuspendido.TIPO_PROYECTO_REGURALIZACION);
		}
	}
	
	
	
//	empieza codigo desde aqui 

	@SuppressWarnings("unchecked")
	public void obtenerProyectosMayor90DiasRegistro1() {
		String sql = " select * from dblink('" + dblinkBpmsSuiaiii
				+ "','select distinct(value), v.processid, t.activationtime, t.actualowner_id from variableinstancelog v, task t  where "
				+ " v.processinstanceid in( SELECT t.processinstanceid from task t, variableinstancelog v where t.status not in "
				+ " (''Completed'',''Exited'',''Ready'') and (SELECT 1+count (*) as days FROM generate_series(0, "
				+ " (cast(now() as date) - cast(t.activationtime as date))) i WHERE date_part(''isodow'', cast(now() as date) + i) NOT IN (6,7))>=90 and "
				+ " t.actualowner_id is not null and t.createdby_id is not null and t.formname is not null and t.processid in "
				+ " (select distinct(processid) from task where processid is not null) and t.processinstanceid=v.processinstanceid "
				+ " and t.actualowner_id=v.value and (v.variableid in(''proponente'',''u_Proponente'',''u_Promotor'',''sujetoControl'')) "
				+ " order by 1) and variableid=''tramite'' and value not in(select v.value from variableinstancelog v, processinstancelog p"
				+ " where p.processinstanceid=v.processinstanceid and v.variableid=''tramite'' and v.processid "
				+ " in(''mae-procesos.Eliminarproyecto'',''mae-procesos.GeneradorDesechos'') and p.status!=2) and v.processinstanceid = t.processinstanceid') as "
				+ " (tramite varchar, proceso varchar, fecha varchar, usuario varchar) where proceso ='mae-procesos.registro-ambiental' or proceso ='mae-procesos.RegistroAmbiental' order by 3 desc limit 10";

		Query query = crudServiceBean.getEntityManager().createNativeQuery(sql);
		List<Object> resultList = new ArrayList<Object>();
		resultList = query.getResultList();
		List<String[]> listaCodigosProyectos = new ArrayList<String[]>();
		if (resultList.size() > 0) {
			System.out.println("lista de proyectos: " + resultList.size());

			for (Object a : resultList) {
				Object[] row = (Object[]) a;
				listaCodigosProyectos
						.add(new String[] { (String) row[0], (String) row[1], (String) row[2], (String) row[3] });
			}
		}
		for (String[] codigoProyecto : listaCodigosProyectos) {
			suspenderProyectoRegistroAmbiental1(codigoProyecto[0].toString(),
					ProcesoSuspendido.TIPO_PROYECTO_REGURALIZACION, codigoProyecto[2].toString(),
					codigoProyecto[3].toString());
		}
	}

	@SuppressWarnings("unchecked")
	public void obtenerProyectosMayor90DiasViabilidad1() {
		String sql = " select * from dblink('" + dblinkBpmsSuiaiii
				+ "','select distinct(value), v.processid, t.activationtime, t.actualowner_id from variableinstancelog v, task t where "
				+ " v.processinstanceid in( SELECT t.processinstanceid from task t, variableinstancelog v where t.status not in "
				+ " (''Completed'',''Exited'',''Ready'') and (SELECT 1+count (*) as days FROM generate_series(0, "
				+ " (cast(now() as date) - cast(t.activationtime as date))) i WHERE date_part(''isodow'', cast(now() as date) + i) NOT IN (6,7))>=90 and "
				+ " t.actualowner_id is not null and t.createdby_id is not null and t.formname is not null and t.processid in "
				+ " (select distinct(processid) from task where processid is not null) and t.processinstanceid=v.processinstanceid "
				+ " and t.actualowner_id=v.value and (v.variableid in(''proponente'',''u_Proponente'',''u_Promotor'',''sujetoControl'')) "
				+ " order by 1) and variableid=''tramite'' and value not in(select v.value from variableinstancelog v, processinstancelog p"
				+ " where p.processinstanceid=v.processinstanceid and v.variableid=''tramite'' and v.processid "
				+ " in(''mae-procesos.Eliminarproyecto'',''mae-procesos.GeneradorDesechos'') and p.status!=2) and v.processinstanceid = t.processinstanceid') as "
				+ " (tramite varchar, proceso varchar, fecha varchar, usuario varchar) where proceso ='mae-procesos.RequisitosPrevios' order by 1 desc limit 15 ";
		System.out.println("query viabilidad.................: " + sql);
		Query query = crudServiceBean.getEntityManager().createNativeQuery(sql);
		List<Object> resultList = new ArrayList<Object>();
		resultList = query.getResultList();
		List<String[]> listaCodigosProyectos = new ArrayList<String[]>();
		if (resultList.size() > 0) {
			System.out.println("lista de proyectos: " + resultList.size());

			for (Object a : resultList) {
				Object[] row = (Object[]) a;
				listaCodigosProyectos
						.add(new String[] { (String) row[0], (String) row[1], (String) row[2], (String) row[3] });
			}
		}
		for (String[] codigoProyecto : listaCodigosProyectos) {
			suspenderProyectoRegistroAmbiental1(codigoProyecto[0].toString(),
					ProcesoSuspendido.TIPO_PROYECTO_REGURALIZACION, codigoProyecto[2].toString(),
					codigoProyecto[3].toString());

		}
	}

	@SuppressWarnings("unchecked")
	public void obtenerProyectosMayor90DiasART1() {
		String sql = " select * from dblink('" + dblinkBpmsSuiaiii
				+ "','select distinct(value), v.processid, t.activationtime, t.actualowner_id from variableinstancelog v, task t where "
				+ " v.processinstanceid in( SELECT t.processinstanceid from task t, variableinstancelog v where t.status not in "
				+ " (''Completed'',''Exited'',''Ready'') and (SELECT 1+count (*) as days FROM generate_series(0, "
				+ " (cast(now() as date) - cast(t.activationtime as date))) i WHERE date_part(''isodow'', cast(now() as date) + i) NOT IN (6,7))>=90 and "
				+ " t.actualowner_id is not null and t.createdby_id is not null and t.formname is not null and t.processid in "
				+ " (select distinct(processid) from task where processid is not null) and t.processinstanceid=v.processinstanceid "
				+ " and t.actualowner_id=v.value and (v.variableid in(''proponente'',''u_Proponente'',''u_Promotor'',''sujetoControl'')) "
				+ " order by 1) and variableid=''tramite'' and value not in(select v.value from variableinstancelog v, processinstancelog p"
				+ " where p.processinstanceid=v.processinstanceid and v.variableid=''tramite'' and v.processid "
				+ " in(''mae-procesos.Eliminarproyecto'',''mae-procesos.GeneradorDesechos'') and p.status!=2) and v.processinstanceid = t.processinstanceid') as"
				+ " (tramite varchar, proceso varchar, fecha varchar, usuario varchar) where proceso ='Suia.AprobracionRequisitosTecnicosGesTrans2' and tramite like '%MAE-RA%' order by 1 desc limit 15";
		System.out.println("query diasEvaluacion.................: " + sql);
		Query query = crudServiceBean.getEntityManager().createNativeQuery(sql);
		List<Object> resultList = new ArrayList<Object>();
		resultList = query.getResultList();
		List<String[]> listaCodigosProyectos = new ArrayList<String[]>();
		if (resultList.size() > 0) {
			System.out.println("lista de proyectos: " + resultList.size());
			for (Object a : resultList) {
				Object[] row = (Object[]) a;
				listaCodigosProyectos
						.add(new String[] { (String) row[0], (String) row[1], (String) row[2], (String) row[3] });
			}
		}
		for (String[] codigoProyecto : listaCodigosProyectos) {
			suspenderProyectoRegistroAmbiental1(codigoProyecto[0].toString(),
					ProcesoSuspendido.TIPO_PROYECTO_REGURALIZACION, codigoProyecto[2].toString(),
					codigoProyecto[3].toString());
		}
	}

	@SuppressWarnings("unchecked")
	public void obtenerProyectosMayor90DiasLicencia1() {
		String sql = " select * from dblink('" + dblinkBpmsSuiaiii
				+ "','select distinct(value), v.processid, t.activationtime, t.actualowner_id from variableinstancelog v, task t where "
				+ " v.processinstanceid in( SELECT t.processinstanceid from task t, variableinstancelog v where t.status not in "
				+ " (''Completed'',''Exited'',''Ready'') and (SELECT 1+count (*) as days FROM generate_series(0, "
				+ " (cast(now() as date) - cast(t.activationtime as date))) i WHERE date_part(''isodow'', cast(now() as date) + i) NOT IN (6,7))>=90 and "
				+ " t.actualowner_id is not null and t.createdby_id is not null and t.formname is not null and t.processid in "
				+ " (select distinct(processid) from task where processid is not null) and t.processinstanceid=v.processinstanceid "
				+ " and t.actualowner_id=v.value and (v.variableid in(''proponente'',''u_Proponente'',''u_Promotor'',''sujetoControl'')) "
				+ " order by 1) and variableid=''tramite'' and value not in(select v.value from variableinstancelog v, processinstancelog p"
				+ " where p.processinstanceid=v.processinstanceid and v.variableid=''tramite'' and v.processid "
				+ " in(''mae-procesos.Eliminarproyecto'',''mae-procesos.GeneradorDesechos'') and p.status!=2) and v.processinstanceid = t.processinstanceid') as"
				+ " (tramite varchar, proceso varchar, fecha varchar, usuario varchar) where proceso ='SUIA.LicenciaAmbiental' order by 1 desc limit 10 ";
		System.out.println("query diasLicencia.................: " + sql);
		Query query = crudServiceBean.getEntityManager().createNativeQuery(sql);
		List<Object> resultList = new ArrayList<Object>();
		resultList = query.getResultList();
		List<String[]> listaCodigosProyectos = new ArrayList<String[]>();
		if (resultList.size() > 0) {
			System.out.println("lista de proyectos: " + resultList.size());
			for (Object a : resultList) {
				Object[] row = (Object[]) a;
				listaCodigosProyectos
						.add(new String[] { (String) row[0], (String) row[1], (String) row[2], (String) row[3] });
			}
		}
		for (String[] codigoProyecto : listaCodigosProyectos) {
			suspenderProyectoRegistroAmbiental1(codigoProyecto[0].toString(),
					ProcesoSuspendido.TIPO_PROYECTO_REGURALIZACION, codigoProyecto[2].toString(),
					codigoProyecto[3].toString());
		}
	}

	@SuppressWarnings("unchecked")
	public void obtenerProyectosMayor90DiasEvaluacionSocial1() {
		String sql = " select * from dblink('" + dblinkBpmsSuiaiii
				+ "','select distinct(value), v.processid, t.activationtime, t.actualowner_id from variableinstancelog v, task t where "
				+ " v.processinstanceid in( SELECT t.processinstanceid from task t, variableinstancelog v where t.status not in "
				+ " (''Completed'',''Exited'',''Ready'') and (SELECT 1+count (*) as days FROM generate_series(0, "
				+ " (cast(now() as date) - cast(t.activationtime as date))) i WHERE date_part(''isodow'', cast(now() as date) + i) NOT IN (6,7))>=90 and "
				+ " t.actualowner_id is not null and t.createdby_id is not null and t.formname is not null and t.processid in "
				+ " (select distinct(processid) from task where processid is not null) and t.processinstanceid=v.processinstanceid "
				+ " and t.actualowner_id=v.value and (v.variableid in(''proponente'',''u_Proponente'',''u_Promotor'',''sujetoControl'')) "
				+ " order by 1) and variableid=''tramite'' and value not in(select v.value from variableinstancelog v, processinstancelog p"
				+ " where p.processinstanceid=v.processinstanceid and v.variableid=''tramite'' and v.processid "
				+ " in(''mae-procesos.Eliminarproyecto'',''mae-procesos.GeneradorDesechos'') and p.status!=2) and v.processinstanceid = t.processinstanceid') as"
				+ " (tramite varchar, proceso varchar, fecha varchar, usuario varchar) where proceso ='suia.participacion-social' order by 1 desc limit 5 ";
		System.out.println("query diasEvaluacion.................: " + sql);
		Query query = crudServiceBean.getEntityManager().createNativeQuery(sql);
		List<Object> resultList = new ArrayList<Object>();
		resultList = query.getResultList();
		List<String[]> listaCodigosProyectos = new ArrayList<String[]>();
		if (resultList.size() > 0) {
			System.out.println("lista de proyectos: " + resultList.size());
			for (Object a : resultList) {
				Object[] row = (Object[]) a;
				listaCodigosProyectos
						.add(new String[] { (String) row[0], (String) row[1], (String) row[2], (String) row[3] });
			}
		}
		for (String[] codigoProyecto : listaCodigosProyectos) {
			suspenderProyectoRegistroAmbiental1(codigoProyecto[0].toString(),
					ProcesoSuspendido.TIPO_PROYECTO_REGURALIZACION, codigoProyecto[2].toString(),
					codigoProyecto[3].toString());
		}
	}

//	y termina en la parte de aqui 
	
	//*******************************	desde aqui empieza el siguiente metodo

		public void suspenderProyectoRegistroAmbiental1(String codigoProyecto, String fecha, String parametro3,
				String usuario) {
			try {

				Date fechaActual = new Date();
				SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd");
				String fechaActivacionTarea = formatoFecha.format(fechaActual);
				Date fechaInicial = formatoFecha.parse(parametro3);
				Date fechaFinal = formatoFecha.parse(fechaActivacionTarea);

				int dias = (int) ((fechaFinal.getTime() - fechaInicial.getTime()) / 86400000);

				listHoliday = feriadosFacade.listarFeriados();
				int contador = 0;
				for (Holiday holiday : listHoliday) {
					Date hol = holiday.getFechaInicio();
					String holi = formatoFecha.format(hol);
					Date feriado = formatoFecha.parse(holi);
//					la busqueda despues de fecha inicial y antes de fecha final
					if ((feriado.after(fechaInicial)) && (feriado.before(fechaFinal))) {
						contador++;
					}
				}
				System.out.println("proyecto....coa................: " + codigoProyecto);
				dias = dias - contador;

				if (dias == 90 || dias >= 90) {

					ProyectoLicenciamientoAmbiental proyecto = new ProyectoLicenciamientoAmbiental();
					proyecto = proyectoLicenciamientoAmbientalFacade.getProyectoPorCodigo(codigoProyecto);

//				if (proyecto==null) {
//					ProyectoLicenciaCoa proyectoCoa = new ProyectoLicenciaCoa();  
//					proyectoCoa = proyectosLicenciaCoaFacade.getProyectoPorCodigo(codigoProyecto);
//				    }

					updateEstadoProyecto_tab_processinstancelog(codigoProyecto);
					updateEstadoProyecto_tab_task(codigoProyecto);
					updateEstadoProyecto_tab_bamtasksummary(codigoProyecto);
					insertEstadoProyecto_tab_process_suspended(codigoProyecto, fechaActivacionTarea);
						updateEstadoProyecto_tab_project_licencing_coa(codigoProyecto, fechaActivacionTarea);
						updateEstadoProyecto_tab_projects_environmental_licensing(codigoProyecto, fechaActivacionTarea);

					envioNotificacion90Dias(proyecto.getUsuario(), proyecto, 90);
					System.out.println("proyecto desactivado por 90 días: " + proyecto.getCodigo());
				}

			} catch (Exception ex) {
				System.out.println("error: " + ex);
			}
		}

		public void updateEstadoProyecto_tab_processinstancelog(String codigoProyecto) {

			String queryString = "select dblink_exec('" + dblinkBpmsSuiaiii
					+ "','UPDATE processinstancelog SET status = 4 WHERE processinstanceid = (select processinstanceid from task where processinstanceid = (select max(processinstanceid) from task where processinstanceid in ( select processinstanceid from variableinstancelog where value = ''"
					+ codigoProyecto
					+ "''  and variableid = ''tramite'' ) and status = ''Reserved'')and status = ''Reserved'')') as result";
			Query query = crudServiceBean.getEntityManager().createNativeQuery(queryString);
			query.getSingleResult();
		}

		public void updateEstadoProyecto_tab_task(String codigoProyecto) {

			String queryString = "select dblink_exec('" + dblinkBpmsSuiaiii
					+ "','UPDATE task SET status = ''Exited'' WHERE processinstanceid = (select processinstanceid from task where processinstanceid in (select processinstanceid from variableinstancelog where value = ''"
					+ codigoProyecto + "''  and variableid = ''tramite'' ) and status = ''Reserved'')') as result";
			Query query = crudServiceBean.getEntityManager().createNativeQuery(queryString);
			query.getSingleResult();
		}

		public void updateEstadoProyecto_tab_bamtasksummary(String codigoProyecto) {

			String queryString = "select dblink_exec('" + dblinkBpmsSuiaiii
					+ "','UPDATE public.bamtasksummary SET status = ''Exited'' WHERE processinstanceid = (select processinstanceid from task where processinstanceid = (select processinstanceid from variableinstancelog where value = ''"
					+ codigoProyecto
					+ "'' and variableid = ''tramite'' ) and status = ''Reserved'')and status = ''Reserved''') as result";
			Query query = crudServiceBean.getEntityManager().createNativeQuery(queryString);
			query.getSingleResult();
		}

	//******begin********** preguntar a Hirmita que se debe de hacer ahi ya que este proceso desactivaba la anteriores y ahi no existia la tabla oa_mae.project_licencing_coa
		public int updateEstadoProyecto_tab_projects_environmental_licensing(String codigoProyecto,
				String fechaActivacionTarea) {

			String updateQuery = "UPDATE suia_iii.projects_environmental_licensing SET pren_status = 'false', pren_delete_reason = 'AM 061-Disposición Quinta', pren_deactivation_date = '"
					+ fechaActivacionTarea + "' where pren_code = '" + codigoProyecto + "'";
			return crudServiceBean.getEntityManager().createNativeQuery(updateQuery).executeUpdate();
		}

		public int updateEstadoProyecto_tab_project_licencing_coa(String codigoProyecto, String fechaActivacionTarea) {

			String updateQuery = "UPDATE coa_mae.project_licencing_coa SET prco_status = 'false', prco_delete_reason = 'AM 061-Disposición Quinta', prco_deactivation_date = '"
					+ fechaActivacionTarea + "' where prco_cua = '" + codigoProyecto + "'";
			return crudServiceBean.getEntityManager().createNativeQuery(updateQuery).executeUpdate();
		}

	//*******fin********* preguntar a Hirmita que se debe de hacer ahi ya que este proceso desactivaba la anteriores y ahi no existia la tabla oa_mae.project_licencing_coa

		public void insertEstadoProyecto_tab_process_suspended(String codigoProyecto, String fechaActivacionTarea) {
			
			String sqlInsert = "INSERT INTO suia_iii.process_suspended(prsu_id, prsu_code, prsu_desciption, prsu_suspended, prsu_status, prsu_creation_date, prsu_date_update, prsu_creator_user, prsu_user_update, prsu_type_project, prsu_reactivated_days, prsu_reactivated_date) VALUES (((select max(prsu_id) from suia_iii.process_suspended) + 1), '" 
								+ codigoProyecto + "', 'AM 061-Disposición Quinta', 'true', 'true', '" + fechaActivacionTarea+ "', null, 'Automatico', null, null, null, null)";
			crudServiceBean.getEntityManager().createNativeQuery(sqlInsert).getResultList();
		}
		
		
		

	//	
	//**************************	aqui termina el siguiente metodo
	
	public void suspenderProyectoRegistroAmbiental(String codigoProyecto,String tipoProyecto) {
		try {
			boolean diasXsuspender=false;
			ProyectoLicenciamientoAmbiental proyecto= new ProyectoLicenciamientoAmbiental();
			proyecto = proyectoLicenciamientoAmbientalFacade.getProyectoPorCodigo(codigoProyecto);
			
			if(proyecto==null  || (proyecto.getMotivoEliminar() != null && !proyecto.getMotivoEliminar().trim().isEmpty())){
				return;
			}
			
			SimpleDateFormat simpleDate= new SimpleDateFormat("yyyy-MM-dd");
	        Date dateRegistro;
	        Date dateCompare;
			
			dateRegistro = simpleDate.parse(proyecto.getFechaCreacion().toString());
			//fecha para desactivar tarea de pago en el proponente mayor a 30 días. 
			dateCompare=simpleDate.parse(Constantes.getFechaAcuerdoRgdUsoSuelos());
			
			List<VariableInstanceLog>listVariable=procesoFacade.getVariableInstanceLogs(proyecto.getUsuario(),Constantes.VARIABLE_PROCESO_TRAMITE, proyecto.getCodigo());
			TaskSummary taskSummary=procesoFacade.getCurrenTask(proyecto.getUsuario(), listVariable.get(0).getProcessInstanceId());
    		
			if(dateRegistro.after(dateCompare)){
	    		if(taskSummary.getName().contains("pago"))
	    		diasXsuspender =true;
	    	}
			
			SimpleDateFormat formatoFecha= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	        String fechaActivacionTarea=formatoFecha.format(taskSummary.getActivationTime());
			
			Integer numeroDias=0;
			numeroDias=proyectoLicenciamientoAmbientalFacade.numDiasPorFecha(fechaActivacionTarea);
			proyecto.setNumDiasParaDesactivacion(numeroDias);
			
			listHoliday=feriadosFacade.listarFeriados();
			for (Holiday holiday : listHoliday) {
				if(taskSummary.getActivationTime().before(holiday.getFechaInicio())&& new Date().after(holiday.getFechaFin())){
					proyecto.setNumDiasParaDesactivacion(proyecto.getNumDiasParaDesactivacion()-holiday.getTotalDias());
				}
			}
			
			if(proyecto.getNumDiasParaDesactivacion()>=90 || (proyecto.getNumDiasParaDesactivacion()>=30 && diasXsuspender)){
				
				if(modificarPropietarioTareas(proyecto.getCodigo().toString(),proyecto.getUsuario(),true))
				{
					ProcesoSuspendido ps =new ProcesoSuspendido();			
					ps.setTipoProyecto(tipoProyecto);
					ps.setSuspendido(true);
					ps.setCodigo(proyecto.getCodigo());
					ps.setDescripcion("AM 061-Disposición Quinta");				
					ps.setUsuarioCreacion("system");
					guardar(ps,proyecto.getUsuario().getNombre().toString());
					setEstadoProyecto(proyecto.getId(),false);

					if(diasXsuspender){
						envioNotificacion90Dias(proyecto.getUsuario(), proyecto,30);
						System.out.println("proyecto desactivado por 30 días: "+proyecto.getCodigo());
//						proyectos30Dias=false;
					}else {
						envioNotificacion90Dias(proyecto.getUsuario(), proyecto,90);
						System.out.println("proyecto desactivado por 90 días: "+proyecto.getCodigo());
					}
				}
			}	
		}	catch (Exception ex) {
			System.out.println("error: "+ex);
		}
	}
	@EJB
	private AprobacionRequisitosTecnicosFacade aprobacionRequisitosTecnicosFacade;
	
	public void suspenderProyectoART(String codigoProyecto,String tipoProyecto) {
		
		try {
			String tramite=codigoProyecto;
			AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos= new AprobacionRequisitosTecnicos();
			if (codigoProyecto.startsWith("MAE-RA") || codigoProyecto.startsWith("MAAE-RA")
				|| codigoProyecto.startsWith("MAATE-RA")) {	
				try {
					aprobacionRequisitosTecnicos=aprobacionRequisitosTecnicosFacade.getAprobacionRequisitosTecnicosByProyectoLicenciaAmbiental(codigoProyecto);
				} catch (Exception e) {
					
				}
				
			}else {
				aprobacionRequisitosTecnicos =aprobacionRequisitosTecnicosFacade.getAprobacionRequisitosTecnicosBySolicitud(codigoProyecto);
			}
			
			if((codigoProyecto.startsWith("MAE-RA") || codigoProyecto.startsWith("MAAE-RA") 
			|| codigoProyecto.startsWith("MAATE-RA") ) && aprobacionRequisitosTecnicos==null){
				suspenderProyectoLicenciaAmbiental(codigoProyecto,ProcesoSuspendido.TIPO_PROYECTO_REGURALIZACION);
				return;
			}
			long idProceso=0;
			List<VariableInstanceLog>listVariable=procesoFacade.getVariableInstanceLogs(aprobacionRequisitosTecnicos.getUsuario(),Constantes.VARIABLE_PROCESO_TRAMITE, tramite);
			for (VariableInstanceLog variableInstanceLog : listVariable) {
				if(variableInstanceLog.getProcessId().equals("Suia.AprobracionRequisitosTecnicosGesTrans2")){
					idProceso=variableInstanceLog.getProcessInstanceId();
				}
			}
			
			TaskSummary taskSummary=procesoFacade.getCurrenTask(aprobacionRequisitosTecnicos.getUsuario(), idProceso);
			
			SimpleDateFormat formatoFecha= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	        String fechaActivacionTarea=formatoFecha.format(taskSummary.getCreatedOn());
			
			Integer numeroDias=0;
			numeroDias=proyectoLicenciamientoAmbientalFacade.numDiasPorFecha(fechaActivacionTarea);
			aprobacionRequisitosTecnicos.setNumDiasParaDesactivacion(numeroDias);
			
			listHoliday=feriadosFacade.listarFeriados();
			for (Holiday holiday : listHoliday) {
				if(taskSummary.getActivationTime().before(holiday.getFechaInicio())&& new Date().after(holiday.getFechaFin())){
					aprobacionRequisitosTecnicos.setNumDiasParaDesactivacion(aprobacionRequisitosTecnicos.getNumDiasParaDesactivacion()-holiday.getTotalDias());
				}
			}
			
			if(aprobacionRequisitosTecnicos.getNumDiasParaDesactivacion()>=90){
				
				if(modificarPropietarioTareas(tramite,aprobacionRequisitosTecnicos.getUsuario(),true))
				{
					ProcesoSuspendido ps =new ProcesoSuspendido();			
					ps.setTipoProyecto(tipoProyecto);
					ps.setSuspendido(true);
					ps.setCodigo(aprobacionRequisitosTecnicos.getSolicitud());
					if(codigoProyecto.startsWith("MAE-RA") || codigoProyecto.startsWith("MAAE-RA")
						|| codigoProyecto.startsWith("MAATE-RA")){
						ProyectoLicenciamientoAmbiental proyecto= new ProyectoLicenciamientoAmbiental();
						proyecto = proyectoLicenciamientoAmbientalFacade.getProyectoPorCodigo(codigoProyecto);
						setEstadoProyecto(proyecto.getId(),false);
						ps.setCodigo(proyecto.getCodigo());
					}
					
					ps.setDescripcion("AM 061-Disposición Quinta");				
					ps.setUsuarioCreacion("system");
					guardar(ps,aprobacionRequisitosTecnicos.getUsuario().getNombre().toString());
					setEstadoProyectoART(aprobacionRequisitosTecnicos.getId(),false);
					

					envioNotificacion90DiasART(aprobacionRequisitosTecnicos.getUsuario(), tramite,aprobacionRequisitosTecnicos.getNombreProyecto(),true);
					System.out.println("proyecto desactivado por 90 días: "+tramite);
					
				}
			}	
		}	catch (Exception ex) {
			System.out.println("error: "+ex);
		}
	}
	
	public void suspenderProyectoLicenciaAmbiental(String codigoProyecto,String tipoProyecto) {
		try {
			ProyectoLicenciamientoAmbiental proyecto= new ProyectoLicenciamientoAmbiental();
			proyecto = proyectoLicenciamientoAmbientalFacade.getProyectoPorCodigo(codigoProyecto);
			
			if(proyecto==null  || (proyecto.getMotivoEliminar() != null && !proyecto.getMotivoEliminar().trim().isEmpty())){
				return;
			}
			
			
			long idProceso=0;
			List<VariableInstanceLog>listVariable=procesoFacade.getVariableInstanceLogs(proyecto.getUsuario(),Constantes.VARIABLE_PROCESO_TRAMITE, proyecto.getCodigo());
			for (VariableInstanceLog variableInstanceLog : listVariable) {
				if(variableInstanceLog.getProcessId().equals("SUIA.LicenciaAmbiental")){
					idProceso=variableInstanceLog.getProcessInstanceId();
				}
			}
			
			TaskSummary taskSummary=procesoFacade.getCurrenTask(proyecto.getUsuario(), idProceso);
			
			SimpleDateFormat formatoFecha= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	        String fechaActivacionTarea=formatoFecha.format(taskSummary.getCreatedOn());
			
			Integer numeroDias=0;
			numeroDias=proyectoLicenciamientoAmbientalFacade.numDiasPorFecha(fechaActivacionTarea);
			proyecto.setNumDiasParaDesactivacion(numeroDias);
			listHoliday=feriadosFacade.listarFeriados();
			for (Holiday holiday : listHoliday) {
				if(taskSummary.getActivationTime().before(holiday.getFechaInicio())&& new Date().after(holiday.getFechaFin())){
					proyecto.setNumDiasParaDesactivacion(proyecto.getNumDiasParaDesactivacion()-holiday.getTotalDias());
				}
			}
			Integer dias=90;
			if(taskSummary.getName().contains("Ingresar Estudio de Impacto Ambiental") && proyecto.getTipoSector().getId()==2){
				if(proyecto.getIdEstadoAprobacionTdr()!=null && (proyecto.getIdEstadoAprobacionTdr()==1 || proyecto.getIdEstadoAprobacionTdr()==2)){
					
					Date dateRegistro;
				    Date dateCompare;
				    
					SimpleDateFormat simpleDate= new SimpleDateFormat("yyyy-MM-dd");
				    dateRegistro = simpleDate.parse(proyecto.getFechaCreacion().toString());
					dateCompare=simpleDate.parse(Constantes.getFechaBloqueoTdrMineria());
					if(dateRegistro.before(dateCompare)){
						dias=120;
					}
					
				}else {
					return;
				}
			}
			
			if(taskSummary.getName().contains("Descargar pronunciamiento")){
				dias=30;
			}
			
			if(taskSummary.getName().contains("pago")){
				dias=395;
			}
			
			
			if(proyecto.getNumDiasParaDesactivacion()>=dias){
				
				if(modificarPropietarioTareas(proyecto.getCodigo().toString(),proyecto.getUsuario(),true))
				{
					ProcesoSuspendido ps =new ProcesoSuspendido();			
					ps.setTipoProyecto(tipoProyecto);
					ps.setSuspendido(true);
					ps.setCodigo(proyecto.getCodigo());
					ps.setDescripcion("AM 061-Disposición Quinta");				
					ps.setUsuarioCreacion("system");
					guardar(ps,proyecto.getUsuario().getNombre().toString());
					setEstadoProyecto(proyecto.getId(),false);
					
//					envioNotificacion90Dias(proyecto.getUsuario(), proyecto,dias==30?false:true);
					envioNotificacion90Dias(proyecto.getUsuario(), proyecto,dias==30?30:dias);
					System.out.println("proyecto desactivado por "+dias+" días: "+proyecto.getCodigo());
				}
			}	
		}	catch (Exception ex) {
			System.out.println("error: "+ex);
		}
	}
	
	public void suspenderProyectoEvaluacionSocial(String codigoProyecto,String tipoProyecto) {
		try {
			ProyectoLicenciamientoAmbiental proyecto= new ProyectoLicenciamientoAmbiental();
			proyecto = proyectoLicenciamientoAmbientalFacade.getProyectoPorCodigo(codigoProyecto);
			
			if(proyecto==null  || (proyecto.getMotivoEliminar() != null && !proyecto.getMotivoEliminar().trim().isEmpty())){
				return;
			}
			
			long idProceso=0;
			List<VariableInstanceLog>listVariable=procesoFacade.getVariableInstanceLogs(proyecto.getUsuario(),Constantes.VARIABLE_PROCESO_TRAMITE, proyecto.getCodigo());
			for (VariableInstanceLog variableInstanceLog : listVariable) {
				if(variableInstanceLog.getProcessId().equals("suia.participacion-social")){
					idProceso=variableInstanceLog.getProcessInstanceId();
				}
			}
			
			TaskSummary taskSummary=procesoFacade.getCurrenTask(proyecto.getUsuario(), idProceso);
			
			SimpleDateFormat formatoFecha= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	        String fechaActivacionTarea=formatoFecha.format(taskSummary.getCreatedOn());
			
			Integer numeroDias=0;
			numeroDias=proyectoLicenciamientoAmbientalFacade.numDiasPorFecha(fechaActivacionTarea);
			proyecto.setNumDiasParaDesactivacion(numeroDias);
			listHoliday=feriadosFacade.listarFeriados();
			for (Holiday holiday : listHoliday) {
				if(taskSummary.getActivationTime().before(holiday.getFechaInicio())&& new Date().after(holiday.getFechaFin())){
					proyecto.setNumDiasParaDesactivacion(proyecto.getNumDiasParaDesactivacion()-holiday.getTotalDias());
				}
			}
			
			Date dateRegistro;
		    Date dateCompare;
		    boolean archivar=false;
		    
		    SimpleDateFormat simpleDate= new SimpleDateFormat("yyyy-MM-dd");
		    dateRegistro = simpleDate.parse(proyecto.getFechaCreacion().toString());
			dateCompare=simpleDate.parse(Constantes.getFechaProyectosSinPccAntes());
			
			if(dateRegistro.before(dateCompare)){
				archivar=true;
			}
			
			if(proyecto.getNumDiasParaDesactivacion()>=90 && archivar){
				
				if(modificarPropietarioTareas(proyecto.getCodigo().toString(),proyecto.getUsuario(),true))
				{
					ProcesoSuspendido ps =new ProcesoSuspendido();			
					ps.setTipoProyecto(tipoProyecto);
					ps.setSuspendido(true);
					ps.setCodigo(proyecto.getCodigo());
					ps.setDescripcion("AM 061-Disposición Quinta");				
					ps.setUsuarioCreacion("system");
					guardar(ps,proyecto.getUsuario().getNombre().toString());
					setEstadoProyecto(proyecto.getId(),false);
					
					envioNotificacion90Dias(proyecto.getUsuario(), proyecto,90);
					System.out.println("proyecto desactivado por 90 días: "+proyecto.getCodigo());
				}
			}	
		}	catch (Exception ex) {
			System.out.println("error: "+ex);
		}
	}
	
	public void obtenerProyectosNoFinalizaRegistro(){
		List<ProyectoLicenciamientoAmbiental>listProyectos= new ArrayList<ProyectoLicenciamientoAmbiental>();
		listProyectos=proyectoLicenciamientoAmbientalFacade.listProyectosNoFinalizanRegistro();
		if(listProyectos.size()>0){
			try {
				listHoliday=feriadosFacade.listarFeriados();
			} catch (ServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for (ProyectoLicenciamientoAmbiental proyectoLicenciamientoAmbiental : listProyectos) {
				Integer numeroDias=0;
				numeroDias=proyectoLicenciamientoAmbientalFacade.numDiasPorFechaProyecto(proyectoLicenciamientoAmbiental.getFechaCreacion());
				proyectoLicenciamientoAmbiental.setNumDiasParaDesactivacion(numeroDias);
				for (Holiday holiday : listHoliday) {
					if(proyectoLicenciamientoAmbiental.getFechaCreacion().before(holiday.getFechaInicio())&& new Date().after(holiday.getFechaFin())){
						proyectoLicenciamientoAmbiental.setNumDiasParaDesactivacion(proyectoLicenciamientoAmbiental.getNumDiasParaDesactivacion()-holiday.getTotalDias());
					}
				}
				if(proyectoLicenciamientoAmbiental.getNumDiasParaDesactivacion()>=90){
					ProcesoSuspendido ps =new ProcesoSuspendido();			
					
					ps.setSuspendido(true);
					ps.setCodigo(proyectoLicenciamientoAmbiental.getCodigo());
					ps.setDescripcion("AM 061-Disposición Quinta");				
					
					ps.setUsuarioCreacion("system");
					guardar(ps,proyectoLicenciamientoAmbiental.getUsuario().getNombre().toString());
					setEstadoProyecto(proyectoLicenciamientoAmbiental.getId(),false);
					
					envioNotificacion90Dias(proyectoLicenciamientoAmbiental.getUsuario(), proyectoLicenciamientoAmbiental,90);
				}
			}
		}
	}
	
	public List<ProyectoLicenciamientoAmbiental>listProyectosNoFinalizanRegistro(){
		return proyectoLicenciamientoAmbientalFacade.listProyectosNoFinalizanRegistro();
	}
	
	@SuppressWarnings("unchecked")
	public void obtenerProyectosMayor90Dias(){
		String sql="select * from dblink('"+dblinkBpmsSuiaiii+"','select distinct(value),processid from variableinstancelog where processinstanceid in("
				+ "SELECT t.processinstanceid from task t, variableinstancelog v "
				+ "where t.formname not ilike ''%pago%'' and t.status not in (''Completed'',''Exited'',''Ready'') "
				+ "and EXTRACT(DAY FROM NOW() - t.activationtime)>=123 "
				+ "and t.actualowner_id is not null and t.createdby_id is not null and t.formname is not null "
				+ "and t.processid in (select distinct(processid) from task where processid is not null) "
				+ "and t.processinstanceid=v.processinstanceid and t.actualowner_id=v.value "
				+ "and (v.variableid=''proponente'' or v.variableid=''u_Proponente'' or v.variableid=''u_Promotor'' "
				+ "or v.variableid=''sujetoControl'') order by 1) and variableid=''tramite'' "
				+ "and value not in(select v.value from variableinstancelog v, processinstancelog p "
				+ "where p.processinstanceid=v.processinstanceid and v.variableid=''tramite'' "
				+ "and v.processid =''mae-procesos.Eliminarproyecto'' and p.status!=2)') as "
				+ "(tramite varchar, proceso varchar) where proceso!='mae-procesos.GeneradorDesechos' "
				+ "and proceso!='Suia.AprobracionRequisitosTecnicosGesTrans2' order by 2 asc limit 30";								
		
		Query query = crudServiceBean.getEntityManager().createNativeQuery(sql);
		List<Object>  resultList = new ArrayList<Object>();
		resultList=query.getResultList();
		List<String[]>listaCodigosProyectos= new ArrayList<String[]>();		
		if (resultList.size() > 0) {
			System.out.println("lista de proyectos: " + resultList.size());
			for (Object a : resultList) {
				Object[] row = (Object[]) a;
				listaCodigosProyectos.add(new String[] { (String) row[0],(String) row[1] });
			}
		}
		for (String[] codigoProyecto : listaCodigosProyectos) {
			if(!tieneReactivacionDias(codigoProyecto[0].toString()))
				suspenderProyecto(codigoProyecto[0].toString(),90,ProcesoSuspendido.TIPO_PROYECTO_REGURALIZACION);
		}
	}
	
	//
	/**
	 * Validar la fecha maxima de activacion
	 * @param codigoProyecto
	 * @return true cuando el proyecto es reactivado y tiene dias disponibles
	 */
	private boolean tieneReactivacionDias(String codigoProyecto)
	{
		ProcesoSuspendido ps=getProcesoSuspendidoPorCodigo(codigoProyecto);
		if(ps!=null && ps.getSuspendido()==false && ps.getFechaActivacion()!=null)
		{
			Calendar fechaActual=Calendar.getInstance();
			Calendar fechaActivacion=Calendar.getInstance();
			fechaActivacion.setTime(ps.getFechaActivacion());
			if(!fechaActual.after(fechaActivacion))
				return true;
		}		
		return false;
	}	 
	
	@SuppressWarnings("unchecked")
	public void notifcarProyectos(){
		String sql=" select * from dblink('"+dblinkBpmsSuiaiii+"','select distinct(v.value),v.processid from task t, variableinstancelog v where formname =''Descargarpronunciamiento'' "
				 + " and EXTRACT(DAY FROM NOW()-t.activationtime)>=25 and v.processinstanceid=t.processinstanceid and t.status not in (''Completed'',''Exited'',''Ready'') "
				 + " and v.variableid=''tramite''') as (tramite varchar, proceso varchar) order by 1 asc";
		
		String sql1="select * from dblink('"+dblinkBpmsSuiaiii+"','select distinct(value),processid from variableinstancelog where processinstanceid in("
				+ "SELECT t.processinstanceid from task t, variableinstancelog v "
				+ "where t.formname not ilike ''%pago%'' and t.status not in (''Completed'',''Exited'',''Ready'') "
				+ "and EXTRACT(DAY FROM NOW() - t.activationtime)>=102 "
				+ "and t.actualowner_id is not null and t.createdby_id is not null and t.formname is not null "
				+ "and t.processid in (select distinct(processid) from task where processid is not null) "
				+ "and t.processinstanceid=v.processinstanceid and t.actualowner_id=v.value "
				+ "and (v.variableid=''proponente'' or v.variableid=''u_Proponente'' or v.variableid=''u_Promotor'' "
				+ "or v.variableid=''sujetoControl'') order by 1) and variableid=''tramite'' "
				+ "and value not in(select v.value from variableinstancelog v, processinstancelog p "
				+ "where p.processinstanceid=v.processinstanceid and v.variableid=''tramite'' "
				+ "and v.processid =''mae-procesos.Eliminarproyecto'' and p.status!=2)') as "
				+ "(tramite varchar, proceso varchar) where proceso!='mae-procesos.GeneradorDesechos' "
				+ "and proceso!='Suia.AprobracionRequisitosTecnicosGesTrans2' order by 2 asc limit 30";
		
		Query query1 = crudServiceBean.getEntityManager().createNativeQuery(sql1);
		Query query = crudServiceBean.getEntityManager().createNativeQuery(sql);
		
		List<Object>  resultList1 = new ArrayList<Object>();
		resultList1=query1.getResultList();
		
		List<Object>  resultList = new ArrayList<Object>();
		resultList=query.getResultList();
		List<String[]>listaCodigosProyectos= new ArrayList<String[]>();		
		if (resultList.size() > 0) {
			System.out.println("lista de proyectos: " + resultList.size());
			for (Object a : resultList) {
				Object[] row = (Object[]) a;
				listaCodigosProyectos.add(new String[] { (String) row[0],(String) row[1] });
			}
		}
		if (resultList1.size() > 0) {
			System.out.println("lista de proyectos: " + resultList1.size());
			for (Object a : resultList1) {
				Object[] row = (Object[]) a;
				listaCodigosProyectos.add(new String[] { (String) row[0],(String) row[1] });
			}
		}
		for (String[] codigoProyecto : listaCodigosProyectos) {
			notificacionProyecto(codigoProyecto[0].toString());
		}
	}
	
	@SuppressWarnings("unchecked")
	public void obtenerProyectosMayor30DiasDescargarPronunciamientoPagos(){
		String sql=" select * from dblink('"+dblinkBpmsSuiaiii+"','select distinct(v.value),v.processid from task t, variableinstancelog v where formname in(''Descargarpronunciamiento'',"
				+ "''realizar_pago'',''Realizarpagoporserviciosadministrativos'',''pagoLicenciaAmbiental'') and (SELECT 1+count (*) as days FROM generate_series(0, (cast(now() as date) - cast(t.activationtime as date))) i WHERE date_part(''isodow'', cast(now() as date) + i) "
				 + " NOT IN (6,7))>=30 and v.processinstanceid=t.processinstanceid and t.status not in (''Completed'',''Exited'',''Ready'')"
				 + " and v.variableid=''tramite''') as (tramite varchar, proceso varchar) order by 1 desc limit 30";								
		
		Query query = crudServiceBean.getEntityManager().createNativeQuery(sql);
		List<Object>  resultList = new ArrayList<Object>();
		resultList=query.getResultList();
		List<String[]>listaCodigosProyectos= new ArrayList<String[]>();		
		if (resultList.size() > 0) {
			System.out.println("lista de proyectos: " + resultList.size());
			for (Object a : resultList) {
				Object[] row = (Object[]) a;
				listaCodigosProyectos.add(new String[] { (String) row[0],(String) row[1] });
			}
		}
		for (String[] codigoProyecto : listaCodigosProyectos) {
			suspenderProyectoDescargaPronunciamiento(codigoProyecto[0].toString(),ProcesoSuspendido.TIPO_PROYECTO_REGURALIZACION);
		}
	}
	
	public void suspenderProyectoDescargaPronunciamiento(String codigoProyecto,String tipoProyecto) {
		try {
			boolean diasPago=false;
			ProyectoLicenciamientoAmbiental proyecto= new ProyectoLicenciamientoAmbiental();
			proyecto = proyectoLicenciamientoAmbientalFacade.getProyectoPorCodigo(codigoProyecto);
			
			if(proyecto==null  || (proyecto.getMotivoEliminar() != null && !proyecto.getMotivoEliminar().trim().isEmpty())){
				return;
			}
			
			 Date dateRegistro;
		     Date dateCompare;
		     
		     SimpleDateFormat simpleDate= new SimpleDateFormat("yyyy-MM-dd");
		     dateRegistro = simpleDate.parse(proyecto.getFechaCreacion().toString());
			//fecha para desactivar tarea de pago en el proponente mayor a 30 días. 
			 dateCompare=simpleDate.parse(Constantes.getFechaAcuerdoRgdUsoSuelos());
			
			List<VariableInstanceLog>listVariable=procesoFacade.getVariableInstanceLogs(proyecto.getUsuario(),Constantes.VARIABLE_PROCESO_TRAMITE, proyecto.getCodigo());
			TaskSummary taskSummary=procesoFacade.getCurrenTask(proyecto.getUsuario(), listVariable.get(0).getProcessInstanceId());
    		
			if(dateRegistro.after(dateCompare)){
	    		if(taskSummary.getName().contains("pago"))
	    			diasPago =true;
			}
			
			SimpleDateFormat formatoFecha= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	        String fechaActivacionTarea=formatoFecha.format(taskSummary.getActivationTime());
			
			Integer numeroDias=0;
			numeroDias=proyectoLicenciamientoAmbientalFacade.numDiasPorFecha(fechaActivacionTarea);
			proyecto.setNumDiasParaDesactivacion(numeroDias);
			listHoliday=feriadosFacade.listarFeriados();
			for (Holiday holiday : listHoliday) {
				if(taskSummary.getActivationTime().before(holiday.getFechaInicio())&& new Date().after(holiday.getFechaFin())){
					proyecto.setNumDiasParaDesactivacion(proyecto.getNumDiasParaDesactivacion()-holiday.getTotalDias());
				}
			}
			
			if((proyecto.getNumDiasParaDesactivacion()>=30 && !diasPago) || (proyecto.getNumDiasParaDesactivacion()>=30 && diasPago)){
				if(modificarPropietarioTareas(proyecto.getCodigo().toString(),proyecto.getUsuario(),true))
				{
					ProcesoSuspendido ps =new ProcesoSuspendido();			
					ps.setTipoProyecto(tipoProyecto);
					ps.setSuspendido(true);
					ps.setCodigo(proyecto.getCodigo());
					ps.setDescripcion("AM 061-Disposición Quinta");				
					ps.setUsuarioCreacion("system");
					guardar(ps,proyecto.getUsuario().getNombre().toString());
					setEstadoProyecto(proyecto.getId(),false);
					envioNotificacion90Dias(proyecto.getUsuario(), proyecto,30);
					System.out.println("proyecto desactivado por 30 días: "+proyecto.getCodigo());					
				}
			}	
		}	catch (Exception ex) {
				ex.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public void obtenerProyectosMayor30DiasDescargarPronunciamiento(){
		String sql=" select * from dblink('"+dblinkBpmsSuiaiii+"','select distinct(v.value),v.processid from task t, variableinstancelog v where formname =''Descargarpronunciamiento'' "
				 + " and EXTRACT(DAY FROM NOW()-t.activationtime)>=40 and v.processinstanceid=t.processinstanceid and t.status not in (''Completed'',''Exited'',''Ready'')"
				 + " and v.variableid=''tramite''') as (tramite varchar, proceso varchar) order by 1 asc limit 30";								
		
		Query query = crudServiceBean.getEntityManager().createNativeQuery(sql);
		List<Object>  resultList = new ArrayList<Object>();
		resultList=query.getResultList();
		List<String[]>listaCodigosProyectos= new ArrayList<String[]>();		
		if (resultList.size() > 0) {
			System.out.println("lista de proyectos: " + resultList.size());
			for (Object a : resultList) {
				Object[] row = (Object[]) a;
				listaCodigosProyectos.add(new String[] { (String) row[0],(String) row[1] });
			}
		}
		for (String[] codigoProyecto : listaCodigosProyectos) {
			if(!tieneReactivacionDias(codigoProyecto[0].toString()))
				suspenderProyecto(codigoProyecto[0].toString(),30,ProcesoSuspendido.TIPO_PROYECTO_REGURALIZACION);
		}
	}
	
	public void suspenderProyecto(String codigoProyecto, Integer numDias,String tipoProyecto) {
		try {
			ProyectoLicenciamientoAmbiental proyecto= new ProyectoLicenciamientoAmbiental();
			
			proyecto = proyectoLicenciamientoAmbientalFacade.getProyectoPorCodigo(codigoProyecto);
			
			if(proyecto==null  || (proyecto.getMotivoEliminar() != null && !proyecto.getMotivoEliminar().trim().isEmpty())){
				return;
			}					
			
			if(modificarPropietarioTareas(proyecto.getCodigo().toString(),proyecto.getUsuario(),true))
			{				
				ProcesoSuspendido ps = getProcesoSuspendidoPorCodigo(codigoProyecto);
				if(ps==null)
					ps=new ProcesoSuspendido();
				
				ps.setTipoProyecto(tipoProyecto);
				ps.setSuspendido(true);
				ps.setCodigo(proyecto.getCodigo());
				ps.setDescripcion("Se desactiva por que no se tuvo respuesta por parte del proponente en el lapso de "+numDias+" dias");				
				guardar(ps,"system");
				setEstadoProyecto(proyecto.getId(),false);
				envioNotificacion90Dias(proyecto.getUsuario(), proyecto,numDias);				
				System.out.println("proyecto desactivado por "+numDias+" días: "+proyecto.getCodigo());			
				
			}
		} catch (Exception ex) {
			
		}
	}
	
	public void notificacionProyecto(String codigoProyecto) {

		try {
			ProyectoLicenciamientoAmbiental proyecto= new ProyectoLicenciamientoAmbiental();			
			proyecto = proyectoLicenciamientoAmbientalFacade.getProyectoPorCodigo(codigoProyecto);

			if(proyecto!=null){
				envioNotificacionAntesDeArchivar(proyecto.getUsuario(), proyecto);
			}					
		} catch (Exception ex) {
			
		}
	}
	
	/**
	 * Cris F: Buscar participación por número de trámite
	 */
	public boolean verificarEvaluacionSocial(String codigoProyecto, Usuario usuario){
		try {
			
			List<ProcessInstanceLog> process = procesoFacade.getProcessInstancesLogsVariableValue(usuario,Constantes.VARIABLE_PROCESO_TRAMITE, codigoProyecto);
			
			boolean procesoEvaluacionSocial=false;
			for (ProcessInstanceLog processInstanceLog : process) {
				if(processInstanceLog.getProcessName().equals("Evaluacion Social")){
					procesoEvaluacionSocial= true;
				}				
			}
			
			return procesoEvaluacionSocial;
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * WR:
	 */
	@SuppressWarnings("unchecked")
	public boolean procesosActivosIVCategorias(String codigoProyecto){
		boolean activoProcesos=false;
		try {
			String sql="select * from dblink('"+dblinkSuiaVerde+"', "
					+ "'select state_ from jbpm4_hist_procinst  where "
					+ "key_ like ''%"+codigoProyecto+"%''') as (estado text)";
			Query querySql = crudServiceBean.getEntityManager().createNativeQuery(sql);
			List<Object>  resultPro = new ArrayList<Object>();
    		resultPro=querySql.getResultList();
    		if (resultPro!=null) {		    	 
    			for(Object a: resultPro)
    			{
    				if(a.equals("active"))
    					activoProcesos=true;
    			}
    		}
			return activoProcesos;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean procesosFinalizadoHidrocarburos(String codigoProyecto){
		boolean finalizado=false;
		try {
			String sql="select * from dblink('"+dblinkSuiaHidro+"', "
					+ "'select id from processinstancelog where processname in(''Hydrocarbons.pagoLicencia'',''Hydrocarbons.pagoLicenciaEnte'') and status=2 "
					+ "and processinstanceid in(select processinstanceid from variableinstancelog where value=''"+codigoProyecto+"'')') as (id integer)";
			Query querySql = crudServiceBean.getEntityManager().createNativeQuery(sql);
    		if (querySql.getResultList().size()>0) {
    					finalizado=true;
    		}
			return finalizado;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean modificarPropietarioTareas4Categorias1(String codigoProyecto, String motivo,boolean suspender) {
		String sqlHyd="select dblink_exec('"+dblinkSuiaVerde+"','update proyectolicenciaambiental set estadoproyecto="+!suspender+","+(suspender?"eliminadopor=null,":"")+"motivoeliminar=''"+motivo+"'',fechaestadoproyecto=now() where id=''"+codigoProyecto+"''') as result";
		Query query = crudServiceBean.getEntityManager().createNativeQuery(sqlHyd);
		if(query.getResultList().size()>0)
		{
			query.getSingleResult();					
			String sqltaskbpmhyd="select * from dblink('"+dblinkSuiaVerde+"', "
					+ "'select dbid_,assignee_ from jbpm4_task where execution_id_  like ''%"+codigoProyecto+"%''') as (id integer,usuario text)";
			Query queryProceso = crudServiceBean.getEntityManager().createNativeQuery(sqltaskbpmhyd);
			List<Object[]>  resultPro = new ArrayList<Object[]>();
    		resultPro=queryProceso.getResultList();
    		if (resultPro!=null) {		    	 
    			for(int i=0;i<resultPro.size();i++)
    			{
    				Object[] asignado = (Object[]) resultPro.get(i);
    				String usuario=asignado[1].toString()+".archivado";
    				Integer codigo=Integer.valueOf(asignado[0].toString());
    				String sqlupdateTask="select dblink_exec('"+dblinkSuiaVerde+"', "
    						+ "'update jbpm4_task set assignee_=''"+usuario+"'' where dbid_="+codigo+"') as result";
    				Query queryTask = crudServiceBean.getEntityManager().createNativeQuery(sqlupdateTask);
    				queryTask.getSingleResult();
    			}
    		}
    		if(suspender)
    		{
    			String sqllistfacilitadores="select * from dblink('"+dblinkSuiaVerde+"',"
    					+ "'select u.nombreusuario,upper(p.nombresapellidos),c.valor "
    					+ "from proyectofacilitador f,usuario u,persona p,contacto c "
    					+ "where f.aceptaproyecto=''SI'' and f.proyecto_id=''"+codigoProyecto+"'' "
    					+ "and f.usuario_id=u.id and u.nombreusuario=p.pin and c.entidad=p.id and c.tipocontacto=''EMAIL''')"
    					+ "as (nombreusuario text,nombresapellidos text,valor text)";
    			Query queryFacilitadores = crudServiceBean.getEntityManager().createNativeQuery(sqllistfacilitadores);
    			List<Object[]> resultListFacilitadores = (List<Object[]>) queryFacilitadores.getResultList();
    	    	if (resultListFacilitadores.size() > 0) {
    	    		for (int i = 0; i < resultListFacilitadores.size(); i++) {
    	    			Object[] facilitador = (Object[]) resultListFacilitadores.get(i);
    	    			NotificacionAutoridadesController email=new NotificacionAutoridadesController();
    	    			email.sendEmailBajaProyectoFacilitadores("Notificaci&oacute;n", facilitador[2].toString(), codigoProyecto, facilitador[1].toString());
    	    		}
    	    	}
    		}
    		return true;
		}
		
		return false;
	}
	
	public boolean verificarTareasRGD(String codigoProyecto, Usuario usuario) {
		boolean eliminar=false;
		try {
			try {
				List<ProcessInstanceLog> process = procesoFacade.getProcessInstancesLogsVariableValue(usuario,Constantes.VARIABLE_PROCESO_TRAMITE, codigoProyecto);
				for (ProcessInstanceLog processInstanceLog : process) {
					try {
						
						if(processInstanceLog.getProcessName().equals("Registro de generador de desechos especiales y peligrosos")){
							if(processInstanceLog.getStatus()==1) {
								List<TaskSummary> listTS=  procesoFacade.obtenerTareaReserved(processInstanceLog.getId(), usuario);
								for (TaskSummary taskSummary : listTS) {
									if(taskSummary.getName().equals("Recibir pronunciamiento y registro de generador"))
									{
										eliminar=false;
										break;
									}
									else
									{
										eliminar=true;
									}
								}
							}
							if(processInstanceLog.getStatus()==2) {
								eliminar=false;
							}
						}						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (JbpmException jex) {
				jex.printStackTrace();
			}			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return eliminar;
	}
	
	public List<String> variablesProcesoTecnicoProponente(String codigoProyecto, Usuario usuario){
		List<String> obj = new ArrayList<String>();
		try {
			List<ProcessInstanceLog> process = procesoFacade.getProcessInstancesLogsVariableValue(usuario,Constantes.VARIABLE_PROCESO_TRAMITE, codigoProyecto);
			for (ProcessInstanceLog processInstanceLog : process) {
				if(processInstanceLog.getProcessName().equals("Licencia Ambiental")){
					Map<String, Object> objMap;
					objMap=procesoFacade.recuperarVariablesProceso(usuario, processInstanceLog.getProcessInstanceId());
					for (String i : objMap.keySet()) {
						System.out.println("key: " + i + " value: " + objMap.get(i));
						if(i.equals("sujetoControl") || i.equals("u_Proponente") || i.equals("u_Promotor") || i.equals("proponente"))
						{
							obj.add(objMap.get(i).toString());
							break;
						}
					}
					for (String j : objMap.keySet()) {
						System.out.println("key: " + j + " value: " + objMap.get(j));
						if(j.equals("u_TecnicoResponsable"))
						{
							obj.add(objMap.get(j).toString());
							break;
						}
					}
				}
			}
			return obj;

		} catch (Exception e) {
			e.printStackTrace();
			return obj;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<String> variablesHidrocarburosTecnicoProponente(String codigoProyecto){
		List<String> obj = new ArrayList<String>();
		try {
			String sql="select * from dblink('"+dblinkSuiaHidro+"', "
					+ "'select value from variableinstancelog where processinstanceid in( "
					+ "select processinstanceid from variableinstancelog where value=''"+codigoProyecto+"'' order by 1 desc limit 1) "
					+ "and variableid =''usuarioHidrocarburos''') as (value text)";
//					+ "union "
//					+ "select value from variableinstancelog where processinstanceid in( "
//					+ "select processinstanceid from variableinstancelog where value=''"+codigoProyecto+"'' order by 1 desc limit 1) "
//					+ "and variableid =''usuarioProponente''') as (value text)";
			Query querySql = crudServiceBean.getEntityManager().createNativeQuery(sql);
			List<Object>  result = new ArrayList<Object>();
    		result=querySql.getResultList();
    		if (result!=null) {		    	 
    			for(Object a: result)
    			{
    				obj.add(a.toString());
    			}
    		}
    		
    		String sql1="select * from dblink('"+dblinkSuiaVerde+"', "
					+ "'select usuarioldap from proyectolicenciaambiental where id=''"+codigoProyecto+"''') as (usuarioldap text)";
			Query querySql1 = crudServiceBean.getEntityManager().createNativeQuery(sql1);
			List<Object>  result1 = new ArrayList<Object>();
    		result1=querySql1.getResultList();
    		if (result1!=null) {		    	 
    			for(Object a1: result1)
    			{
    				obj.add(a1.toString());
    			}
    		}
    		
			return obj;
		} catch (Exception e) {
			e.printStackTrace();
			return obj;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<String> variablesIVCatTecnicoProponente(String codigoProyecto){
		List<String> obj = new ArrayList<String>();
		try {
			String sql="select * from dblink('"+dblinkSuiaVerde+"', "
					+ "'select stringvalue from variable where proyecto=''"+codigoProyecto+"'' and nombre=''Técnico Analista'' order by 1 desc limit 1') as (stringvalue text)";
			Query querySql = crudServiceBean.getEntityManager().createNativeQuery(sql);
			List<Object>  result = new ArrayList<Object>();
    		result=querySql.getResultList();
    		if (result!=null) {		    	 
    			for(Object a: result)
    			{
    				obj.add(a.toString());
    			}
    		}
    		
    		String sql1="select * from dblink('"+dblinkSuiaVerde+"', "
					+ "'select usuarioldap from proyectolicenciaambiental where id=''"+codigoProyecto+"''') as (usuarioldap text)";
			Query querySql1 = crudServiceBean.getEntityManager().createNativeQuery(sql1);
			List<Object>  result1 = new ArrayList<Object>();
    		result1=querySql1.getResultList();
    		if (result1!=null) {		    	 
    			for(Object a1: result1)
    			{
    				obj.add(a1.toString());
    			}
    		}
    		
			return obj;
		} catch (Exception e) {
			e.printStackTrace();
			return obj;
		}
	}
	
	public boolean modificarPropietarioTareasRcoa(String codigoProyecto, Usuario usuario,boolean suspender) {
		boolean eliminar=false;
		try {
			try {
				List<ProcessInstanceLog> process = procesoFacade.getProcessInstancesLogsVariableValue(usuario,Constantes.VARIABLE_PROCESO_TRAMITE, codigoProyecto);
				for (ProcessInstanceLog processInstanceLog : process) {
					if(processInstanceLog.getProcessName().equals("Eliminar proyecto")){
						return false;
					}				
				}				
				
				for (ProcessInstanceLog processInstanceLog : process) {
					try {
						if(processInstanceLog.getStatus()==1) {
							List<TaskSummary> listTS=  procesoFacade.obtenerTareaReserved(processInstanceLog.getProcessInstanceId(), usuario);
							for (TaskSummary taskSummary : listTS) {
								String sql="select dblink_exec('"+dblinkBpmsSuiaiii+"','update task set status=''Exited'', actualowner_id="+(suspender?"null":"createdby_id")+" where id=''"+taskSummary.getId()+"''') as result";
								Query query = crudServiceBean.getEntityManager().createNativeQuery(sql);
								if(query.getResultList().size()>0){
									query.getSingleResult();
									eliminar=true;
								}
								eliminar=true;
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				//}
			} catch (JbpmException jex) {
				jex.printStackTrace();
			}			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return eliminar;
	}
	
}