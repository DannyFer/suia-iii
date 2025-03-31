package ec.gob.ambiente.suia.cargaTrabajo.service;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.*;
import ec.gob.ambiente.suia.exceptions.ServiceException;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateless
public class CargaTrabajoServiceBean {

	@EJB
	private CrudServiceBean crudServiceBean;

    @SuppressWarnings("unchecked")
    public List<CargaTrabajo> listarCargarTrabajo(Usuario usuario) throws ServiceException {
        List<CargaTrabajo> listaTrabajo = null;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("usuarioId", usuario.getId());
        try {
        	listaTrabajo = (List<CargaTrabajo>) crudServiceBean.findByNamedQuery(CargaTrabajo.OBTENER_POR_USUARIO, params);
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
        return listaTrabajo;
    }

    @SuppressWarnings("unchecked")
    public List<CargaTrabajo> listarCargarTrabajoTodos() throws ServiceException {
        List<CargaTrabajo> listaTrabajo = null;
        try {
        	listaTrabajo = (List<CargaTrabajo>) crudServiceBean.findByNamedQuery(CargaTrabajo.OBTENER_TODOS, null);
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
        return listaTrabajo;
    }

    @SuppressWarnings("unchecked")
    public List<CargaTrabajoRevision> listarCargarTrabajoRevisiones(Integer cargaTrabajoId) throws ServiceException {
        List<CargaTrabajoRevision> listaTrabajo = null;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("cargaTrabajoId", cargaTrabajoId);
        try {
        	listaTrabajo = (List<CargaTrabajoRevision>) crudServiceBean.findByNamedQuery(CargaTrabajoRevision.LISTAR_POR_CARGATRABAJO, params);
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
        return listaTrabajo;
    }

    @SuppressWarnings("unchecked")
    public List<CargaTrabajoRevision> listarCargarTrabajoRevisionesAtrazadas(Usuario usuario) throws ServiceException {
        List<CargaTrabajoRevision> listaTrabajo = null;
        try {
    		String sqlPproyecto="  with revision as( "
    							+ "select wolo_id, max(wlre_id ) wlre_id "
    							+ " from suia_iii.work_load_revision "
    							+ " where wlre_status = true "
    							+ " group by wolo_id "
    							+ " order by count(*) desc"
    							+ " ) "
    							+ "select a.* "
    							+ " from suia_iii.work_load_revision a inner join suia_iii.work_load b on a.wolo_id = b.wolo_id and b.wolo_status_processed not in ('A') "
    							+ " inner join revision c on a.wlre_id = c.wlre_id "
    							+ " where b.wolo_status = true and a.wlre_status = true  and a.wlre_status_processed = 'T'"
    							+ " and a.wlre_pronouncement = 'O' and a.wlre_output_date is not null and a.wlre_term is not null "
    							+ " and a.wlre_term < dias_laborable (cast(a.wlre_output_date as date),  cast(now() as date)) "
    							+ " and b.user_creator ='"+usuario.getNombre()+"'";

    		Query queryProyecto =  crudServiceBean.getEntityManager().createNativeQuery(sqlPproyecto, CargaTrabajoRevision.class);
    		listaTrabajo = (List<CargaTrabajoRevision>) queryProyecto.getResultList();
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
        return listaTrabajo;
    }

    @SuppressWarnings("unchecked")
    public List<CargaTrabajo> listarCargarTrabajoObligacionesAtrazadas(Usuario usuario) throws ServiceException {
        List<CargaTrabajo> listaTrabajo = null;
        try {
    		String sqlPproyecto="  select * "
    							+ " from suia_iii.work_load  "
    							+ " where wolo_status = true  "
    							+ " and ( "
    							+ " wolo_obligations_audit and wolo_obligations_audit_date < now() "
    							+ " or wolo_obligations_informe and wolo_obligations_informe_date < now() "
    							+ " or wolo_obligations_monitoring and wolo_obligations_monitoring_date  < now() "
    							+ " or wolo_obligations_tdr and wolo_obligations_tdr_date < now() "
    							+ ") "
    							+" and user_creator ='"+usuario.getNombre()+"'";

    		Query queryProyecto =  crudServiceBean.getEntityManager().createNativeQuery(sqlPproyecto, CargaTrabajo.class);
    		listaTrabajo = (List<CargaTrabajo>) queryProyecto.getResultList();
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
        return listaTrabajo;
    }

	public List<CargaTrabajo> consultarExisteCargaTrabajo(String codigo, Integer id) {
		List<CargaTrabajo> carga= new  ArrayList<CargaTrabajo>();
		try{
		carga = (List<CargaTrabajo>) crudServiceBean.getEntityManager()
				.createQuery("select c From CargaTrabajo c where c.codigo=:codigo and c.id <> :id ")
				.setParameter("codigo", codigo)
				.setParameter("id", id).getResultList();
		} catch(Exception ex){
			
		}
		return carga;
	}

	/**
	 * actualizo el codigo del tramite generando un secuencial
	 * @param cargaTrabajoId
	 */
	public void guardarCodigotramite(Integer cargaTrabajoId) {
		try {
			StringBuilder sql = new StringBuilder();
			sql.append(" with servicio as( ");
			sql.append("select (coalesce(max(cast(substring(replace(wolo_code_procedure, c.geca_code, ''), strpos(replace(wolo_code_procedure, c.geca_code, ''), '-') + 1, 5)as Integer)), '0') + 1) as codigo, c.geca_id servicioid, c.geca_code "); 
			sql.append("from suia_iii.work_load w right join general_catalogs c on w.wolo_service = c.geca_id and  wolo_code_procedure is not null ");
			sql.append(" and  w.wolo_id <> "+cargaTrabajoId);
			sql.append("where c.caty_id = 217 ");
			sql.append("group by c.geca_id, c.geca_code "); 
			sql.append(") ");
			sql.append(" ");
			sql.append("update suia_iii.work_load  ");
			sql.append("set wolo_code_procedure = s.geca_code || extract(year from now()) || '-' || lpad(cast(codigo as text), 4, '0') ");
			sql.append("from servicio s  "); 
			sql.append("where suia_iii.work_load.wolo_service = s.servicioid ");
			sql.append("and suia_iii.work_load.wolo_id = "+cargaTrabajoId);
			crudServiceBean.insertUpdateByNativeQuery(sql.toString(), null);
		} catch (Exception e) {

		}
	}

	public void guardarCargaTrabajohistorico(Integer cargaTrabajoId) {
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("INSERT INTO suia_iii.work_load_history( ");
			sql.append("wolo_id, wlhi_register_date, user_id, wlhi_exists_code_suia, "); 
			sql.append("wlhi_administrative_unit_id, wlhi_priority, pren_id, wlhi_code,  ");
			sql.append("wlhi_project_name, wlhi_number_resolution, wlhi_resolution_date,  ");
			sql.append("gelo_id, wlhi_block, wlhi_sender, wlhi_affair, wlhi_status_processed, "); 
			sql.append("wlhi_type_sector, wlhi_status, wlhi_level_national, user_update, user_update_date , ");
			sql.append("wlhi_service, wlhi_4categories, wlhi_code_procedure, wlhi_obligations_monitoring, wlhi_obligations_monitoring_date, "); 
			sql.append("wlhi_obligations_audit, wlhi_obligations_audit_date, wlhi_obligations_informe, wlhi_obligations_informe_date, "); 
			sql.append("wlhi_obligations_tdr, wlhi_obligations_tdr_date )");
			sql.append("select wolo_id, wolo_register_date, user_id, wolo_exists_code_suia,  ");
			sql.append("wolo_administrative_unit_id, wolo_priority, pren_id, wolo_code, "); 
			sql.append("wolo_project_name, wolo_number_resolution, wolo_resolution_date,  ");
			sql.append("gelo_id, wolo_block, wolo_sender, wolo_affair, wolo_status_processed, "); 
			sql.append(" wolo_type_sector, wolo_status, wolo_level_national, user_update, user_update_date, ");
			sql.append("wolo_service, wolo_4categories, wolo_code_procedure, wolo_obligations_monitoring, wolo_obligations_monitoring_date, "); 
			sql.append("wolo_obligations_audit, wolo_obligations_audit_date, wolo_obligations_informe, wolo_obligations_informe_date, "); 
			sql.append("wolo_obligations_tdr, wolo_obligations_tdr_date ");
			sql.append("from suia_iii.work_load  ");
			sql.append("where wolo_id = "+cargaTrabajoId);
			crudServiceBean.insertUpdateByNativeQuery(sql.toString(), null);
		} catch (Exception e) {

		}
	}

	public void guardarCargaTrabajohistoricoRevision(Integer cargaTrabajoRevisionId) {
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("INSERT INTO suia_iii.work_load_revision_history( ");
			sql.append("wlre_id, wlrh_goal_year, wlrh_goal, wlrh_status_processed, wlrh_input_document, "); 
			sql.append("wlrh_input_date, wlrh_type_procedure, wlrh_output_document, wlrh_output_date,   ");
			sql.append("wlrh_pronouncement, wlrh_term, wlrh_complaints, wlrh_number_trained, wlrh_observation, user_update, user_creation_date, user_update_date)  ");
			sql.append("select 	wlre_id, wlre_goal_year, wlre_goal, wlre_status_processed, wlre_input_document,  ");
			sql.append("wlre_input_date, wlre_type_procedure, wlre_output_document, wlre_output_date,  "); 
			sql.append("wlre_pronouncement, wlre_term, wlre_complaints, wlre_number_trained, wlre_observation, user_update, user_creation_date, user_update_date ");
			sql.append("from  suia_iii.work_load_revision ");
			sql.append("where wlre_id =  "+cargaTrabajoRevisionId);
			crudServiceBean.insertUpdateByNativeQuery(sql.toString(), null);
		} catch (Exception e) {

		}		
	}

}
