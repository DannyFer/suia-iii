package ec.gob.ambiente.suia.facilitador.service;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Persona;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.usuario.service.UsuarioServiceBean;
import ec.gob.ambiente.suia.utils.Constantes;

@Stateless
public class FacilitadorServiceBean {

	public static final String FACILITADOR_ROLE = "role.usuario.facilitador";
	@EJB
	private CrudServiceBean crudServiceBean;
	@EJB
	private UsuarioServiceBean usuarioServiceBean;
	@EJB
	private AreaFacade areaFacade;
	
	public String dblinkBpmsSuiaiii=Constantes.getDblinkBpmsSuiaiii();
	
	public String dblinkSuiaVerde=Constantes.getDblinkSuiaVerde();
	
	@SuppressWarnings("unchecked")
	public List<Usuario> buscarFacilitadores(List<Usuario> excluir) {
		String excluir_cadena = "";
		if (excluir.size() > 0) {
			String ids_exclude = "";
			for (Usuario usuario : excluir) {
				if (ids_exclude.isEmpty()) {
					ids_exclude = Integer.toString(usuario.getId());
				} else {
					ids_exclude += ", " + Integer.toString(usuario.getId());
				}
			}
			excluir_cadena = " and au.usuario.id not in (" + ids_exclude + ")";
		}

		String rol = Constantes
				.getRoleAreaName(FacilitadorServiceBean.FACILITADOR_ROLE);

		List<Usuario> usuarios = crudServiceBean
				.getEntityManager()
				.createQuery(
						" SELECT au.usuario FROM RolUsuario ru, AreaUsuario au" 
								+ " where ru.usuario.id = au.usuario.id and "
								+ " ru.rol.nombre =:rol AND "
								+ " au.area.areaName='DIRECCIÓN REGULARIZACIÓN AMBIENTAL' and au.usuario.estado = true " + excluir_cadena
				+ " and au.usuario.esFacilitador = true")
				.setParameter("rol", rol).getResultList();		
		
		return usuarios;

	}
	
	@SuppressWarnings("unchecked")
	public List<Usuario> buscarFacilitadores(List<Usuario> excluir,String nombreArea) {
		
		String excluir_cadena = "";
		String ids_exclude = "";
		String nombreAreaAux=nombreArea;
		Integer areaid= null;
		if (excluir.size() > 0) {
			for (Usuario usuario : excluir) {
				if (ids_exclude.isEmpty()) {
					ids_exclude = Integer.toString(usuario.getId());
				} else {
					ids_exclude += ", " + Integer.toString(usuario.getId());	
				}
			}
			excluir_cadena = " and u.user_id not in (" + ids_exclude + ")";			
		}
		
		List<Usuario> usuarios;
		String rol = Constantes.getRoleAreaName(FacilitadorServiceBean.FACILITADOR_ROLE);
		if(!nombreArea.equals("MUY ILUSTRE MUNICIPALIDAD DE GUAYAQUIL")&&!nombreArea.equals("GOBIERNO AUTÓNOMO DESCENTRALIZADO PROVINCIAL DE EL ORO")){
			nombreArea="DIRECCIÓN REGULARIZACIÓN AMBIENTAL";
			areaid=1139;		
		}else {
			nombreArea=nombreAreaAux;
			Area area= new Area();
			area =areaFacade.getareaFa(nombreArea);
			areaid=area.getId();
		}
		if(!excluir_cadena.equals("")){
			nombreArea="DIRECCIÓN REGULARIZACIÓN AMBIENTAL";
			areaid=1139;
		}	
		String basenombre=Constantes.getPropertyAsString("basebpm");
		String puerto=Constantes.getPropertyAsString("basebpmpuerto");
		String ip=Constantes.getPropertyAsString("basebpmip");
		String usuriobpms=Constantes.getPropertyAsString("usuriobpms");
		String passwordpms=Constantes.getPropertyAsString("passwordpms");
		String sqllista=null;
		if (areaid != 257){
			sqllista="select  u.user_id,user_name,user_password, pe.peop_name  from ( "
					+ " select coalesce((p.total + xx.total), p.total) as total, user_id, user_name, user_password, user_docu_id, user_temp_password, user_token, user_creation_user, "
					+ " user_creation_date, user_user_update, user_date_update, justification_access, temp_password, area_id, user_status, "
					+ " user_subrogante,   user_creator_user,  user_data_complete,   user_date_expiration, user_functionary, user_edif_id, user_observations, "
					+ " user_pin, peop_id,  user_justification_access,  user_central_functionary,  user_is_area_boss,  user_active_as_facilitator, "
					+ " user_immediate_superior, user_work_performance_ratio from ( "
					+ " select    "
					+ " coalesce(total, 0) as total, u.* from ( "
					+ " select ru.user_id,  total, user_name, user_password, user_docu_id, user_temp_password, user_token, user_creation_user, " 
					+ " user_creation_date, user_user_update, user_date_update, justification_access, temp_password, area_id, user_status, "
					+ " user_subrogante,   user_creator_user,  user_data_complete,   user_date_expiration, user_functionary, user_edif_id, user_observations, "
					+ " user_pin, peop_id,  user_justification_access,  user_central_functionary,  user_is_area_boss,  user_active_as_facilitator, "
					+ " user_immediate_superior, user_work_performance_ratio "
					+ " from suia_iii.roles_users ru "
					+ " left join  "
					+ " ( "
					+ " select user_id, total, user_name, user_password, user_docu_id, user_temp_password, user_token, user_creation_user, " 
					+ " user_creation_date, user_user_update, user_date_update, justification_access, temp_password, area_id, user_status, "
					+ " user_subrogante,   user_creator_user,  user_data_complete,   user_date_expiration, user_functionary, user_edif_id, user_observations, "
					+ " user_pin, peop_id,  user_justification_access,  user_central_functionary,  user_is_area_boss,  user_active_as_facilitator, "
					+ " user_immediate_superior, user_work_performance_ratio "
					+ " from (  "
					+ " select user_id, sum (total) as total, user_name, user_password, user_docu_id, user_temp_password, user_token, user_creation_user, " 
					+ " user_creation_date, user_user_update, user_date_update, justification_access, temp_password, area_id, user_status, "
					+ " user_subrogante,   user_creator_user,  user_data_complete,   user_date_expiration, user_functionary, user_edif_id, user_observations, "
					+ " user_pin, peop_id,  user_justification_access,  user_central_functionary,  user_is_area_boss,  user_active_as_facilitator, "
					+ " user_immediate_superior, user_work_performance_ratio from ( "
					+ " ( "
					+ " select u.user_id, sum(total) as total, user_name, user_password, user_docu_id, user_temp_password, user_token, user_creation_user, " 
					+ " user_creation_date, user_user_update, user_date_update, justification_access, temp_password, area_id, user_status, "
					+ " user_subrogante,   user_creator_user,  user_data_complete,   user_date_expiration, user_functionary, user_edif_id, user_observations, "
					+ " user_pin, peop_id,  user_justification_access,  user_central_functionary,  user_is_area_boss,  user_active_as_facilitator, "
					+ " user_immediate_superior, user_work_performance_ratio "
					+ " from ( "  
					+ " (select pf.user_id as user_id, sum (prfl_resettaskok) as total, user_name, user_password, user_docu_id, user_temp_password, user_token, user_creation_user, " 
					+ " user_creation_date, user_user_update, user_date_update, justification_access, temp_password, area_id, user_status, "
					+ " user_subrogante,   user_creator_user,  user_data_complete,   user_date_expiration, user_functionary, user_edif_id, user_observations, "
					+ " user_pin, peop_id,  user_justification_access,  user_central_functionary,  user_is_area_boss,  user_active_as_facilitator, "
					+ " user_immediate_superior, user_work_performance_ratio "
					+ " from suia_iii.project_facilitators pf "
					+ " inner join users u on u.user_id = pf.user_id "
					+ " where " 
					+ " prfa_acceptation is true "
					+ " and prfa_status is true "
					+ " group by "
					+ " pf.user_id, user_name, user_password, user_docu_id, user_temp_password, user_token, user_creation_user, " 
					+ " user_creation_date, user_user_update, user_date_update, justification_access, temp_password, area_id, user_status, "
					+ " user_subrogante,   user_creator_user,  user_data_complete,   user_date_expiration, user_functionary, user_edif_id, user_observations, "
					+ " user_pin, peop_id,  user_justification_access,  user_central_functionary,  user_is_area_boss,  user_active_as_facilitator, "
					+ " user_immediate_superior, user_work_performance_ratio, prfa_acceptation ,prfl_resettaskok "
					+ " order by pf.user_id "
					+ " ) "
					+ " )u "
					+ " group by "
					+ " user_id, user_name, user_password, user_docu_id, user_temp_password, user_token, user_creation_user, " 
					+ " user_creation_date, user_user_update, user_date_update, justification_access, temp_password, area_id, user_status, "
					+ " user_subrogante,   user_creator_user,  user_data_complete,   user_date_expiration, user_functionary, user_edif_id, user_observations, "
					+ " user_pin, peop_id,  user_justification_access,  user_central_functionary,  user_is_area_boss,  user_active_as_facilitator, "
					+ " user_immediate_superior, user_work_performance_ratio "
					+ " ) "
					+ " union all "
					+ " ( "
					+ " select user_id, sum(total) as total,  user_name, user_password, user_docu_id, user_temp_password, user_token, user_creation_user, " 
					+ "   user_creation_date, user_user_update, user_date_update, justification_access, temp_password, area_id, user_status, "
					+ " user_subrogante,   user_creator_user,  user_data_complete,   user_date_expiration, user_functionary, user_edif_id, user_observations, "
					+ " user_pin, peop_id,  user_justification_access,  user_central_functionary,  user_is_area_boss,  user_active_as_facilitator,"
					+ " user_immediate_superior, user_work_performance_ratio "
					+ " from( "
					+ " (select pf.user_id as user_id, (prfl_resettask) as total, user_name, user_password, user_docu_id, user_temp_password, user_token, user_creation_user, " 
					+ " user_creation_date, user_user_update, user_date_update, justification_access, temp_password, area_id, user_status, "
					+ " user_subrogante,   user_creator_user,  user_data_complete,   user_date_expiration, user_functionary, user_edif_id, user_observations, "
					+ " user_pin, peop_id,  user_justification_access,  user_central_functionary,  user_is_area_boss,  user_active_as_facilitator, "
					+ " user_immediate_superior, user_work_performance_ratio "
					+ " from suia_iii.project_facilitators_log pf " 
					+ " inner join users u on u.user_id = pf.user_id "
					+ " where " 
					+ " prfl_automatic_negation is false "
					+ " group by "
					+ " pf.user_id, user_name, user_password, user_docu_id, user_temp_password, user_token, user_creation_user, " 
					+ " user_creation_date, user_user_update, user_date_update, justification_access, temp_password, area_id, user_status, "
					+ " user_subrogante,   user_creator_user,  user_data_complete,   user_date_expiration, user_functionary, user_edif_id, user_observations, "
					+ " user_pin, peop_id,  user_justification_access,  user_central_functionary,  user_is_area_boss,  user_active_as_facilitator, "
					+ " user_immediate_superior, user_work_performance_ratio, prfl_automatic_negation, prfl_resettask " 
					+ " order by pf.user_id "
					+ " ) "
					+ " )y "
					+ " group by "
					+ " user_id, user_name, user_password, user_docu_id, user_temp_password, user_token, user_creation_user, " 
					+ " user_creation_date, user_user_update, user_date_update, justification_access, temp_password, area_id, user_status, "
					+ " user_subrogante,   user_creator_user,  user_data_complete,   user_date_expiration, user_functionary, user_edif_id, user_observations, "
					+ " user_pin, peop_id,  user_justification_access,  user_central_functionary,  user_is_area_boss,  user_active_as_facilitator, "
					+ " user_immediate_superior, user_work_performance_ratio "
					+ " ) "  
					+ " order by 1 " 
					+ " ) t "
					+ " group by user_id, user_name, user_password, user_docu_id, user_temp_password, user_token, user_creation_user, " 
					+ " user_creation_date, user_user_update, user_date_update, justification_access, temp_password, area_id, user_status, "
					+ " user_subrogante,   user_creator_user,  user_data_complete,   user_date_expiration, user_functionary, user_edif_id, user_observations, "
					+ " user_pin, peop_id,  user_justification_access,  user_central_functionary,  user_is_area_boss,  user_active_as_facilitator, "
					+ " user_immediate_superior, user_work_performance_ratio "
					+ " ) t "
					+ " )p on p.user_id = ru.user_id " 
					+ " where " 
					+ " ru.role_id = 6 and user_status = true and rous_status = true"
					+ " order by 1, 2 "
					+ " ) v "
					+ " inner join users u on v.user_id = u.user_id "
					+ " order by  "
					+ " total, USER_CREATION_DATE "
					+ " )p "
					+ " left join ( " 
					+ " select actualowner_id ,   total " 
					+ " from ( "
					+ " select  t1.actualowner_id, total "
					+ " from dblink('"+dblinkBpmsSuiaiii+"', "
					+ " 'select  "
					+ " actualowner_id, count(*) as total "
					+ " from   "
					+ " task   "
					+ " where  "
					+ " (status in (''InProgress'' ,''Reserved'') and formname=''recibir_informacion'')"
					+ " group by actualowner_id "
					+ " '   "
					+ " ) as t1 " 
					+ " (   "
					+ " actualowner_id character varying(255), total integer "
					+ " )  group by actualowner_id, total "
					+ " ) y group by actualowner_id, total "
					+ " )xx on xx.actualowner_id  = user_name "
					+ " order by 1,9 ) u inner join people pe on pe.peop_id = u.peop_id "
					+ "  where area_id="+areaid +" "+ excluir_cadena;		
		}else{		
			sqllista=" select user_id, user_name, user_password, i.peop_name from ("
					+ "	select user_id, user_name, user_password, p.peop_name "
					+ "	, carga ,  usuario, nombreusuario, fechacreacion, (gestionados + total) as gestionados"
					+ "	from ( select total, user_id, user_name, user_password, peop_name"
					+ "	,carga ,  usuario, nombreusuario, fechacreacion, gestionados"
					+ "	from (select  total, u.user_id,user_name,user_password, pe.peop_name  from ("
					+ "	 select coalesce((p.total + xx.total), p.total) as total, user_id, user_name, user_password, user_docu_id, user_temp_password, user_token, user_creation_user,"
					+ "	user_creation_date, user_user_update, user_date_update, justification_access, temp_password, area_id, user_status,"
					+ "	user_subrogante,   user_creator_user,  user_data_complete,   user_date_expiration, user_functionary, user_edif_id, user_observations,"
					+ "	user_pin, peop_id,  user_justification_access,  user_central_functionary,  user_is_area_boss,  user_active_as_facilitator,"
					+ "	user_immediate_superior, user_work_performance_ratio from ("
					+ "	select coalesce(total, 0) as total, u.* from ( select ru.user_id,  total, user_name, user_password, user_docu_id, user_temp_password, user_token, user_creation_user,"
					+ "	user_creation_date, user_user_update, user_date_update, justification_access, temp_password, area_id, user_status,"
					+ "	user_subrogante,   user_creator_user,  user_data_complete,   user_date_expiration, user_functionary, user_edif_id, user_observations,"
					+ "	user_pin, peop_id,  user_justification_access,  user_central_functionary,  user_is_area_boss,  user_active_as_facilitator,"
					+ "	user_immediate_superior, user_work_performance_ratio"
					+ "	from suia_iii.roles_users ru"
					+ "	left join("
					+ "	select user_id, total, user_name, user_password, user_docu_id, user_temp_password, user_token, user_creation_user,"
					+ "	user_creation_date, user_user_update, user_date_update, justification_access, temp_password, area_id, user_status,"
					+ "	user_subrogante,   user_creator_user,  user_data_complete,   user_date_expiration, user_functionary, user_edif_id, user_observations,"
					+ "	user_pin, peop_id,  user_justification_access,  user_central_functionary,  user_is_area_boss,  user_active_as_facilitator,"
					+ "	user_immediate_superior, user_work_performance_ratio"
					+ "	from ( select user_id, sum (total) as total, user_name, user_password, user_docu_id, user_temp_password, user_token, user_creation_user,"
					+ "	user_creation_date, user_user_update, user_date_update, justification_access, temp_password, area_id, user_status,"
					+ "	user_subrogante,   user_creator_user,  user_data_complete,   user_date_expiration, user_functionary, user_edif_id, user_observations,"
					+ "	user_pin, peop_id,  user_justification_access,  user_central_functionary,  user_is_area_boss,  user_active_as_facilitator,"
					+ "	user_immediate_superior, user_work_performance_ratio from ("
					+ "	(select user_id, sum(total) as total, user_name, user_password, user_docu_id, user_temp_password, user_token, user_creation_user,"
					+ "	user_creation_date, user_user_update, user_date_update, justification_access, temp_password, area_id, user_status,"
					+ "	user_subrogante,   user_creator_user,  user_data_complete,   user_date_expiration, user_functionary, user_edif_id, user_observations,"
					+ "	user_pin, peop_id,  user_justification_access,  user_central_functionary,  user_is_area_boss,  user_active_as_facilitator,"
					+ "	user_immediate_superior, user_work_performance_ratio"
					+ "	from ( (select pf.user_id as user_id, sum (prfl_resettaskok) as total, user_name, user_password, user_docu_id, user_temp_password, user_token, user_creation_user,"
					+ "	user_creation_date, user_user_update, user_date_update, justification_access, temp_password, area_id, user_status,"
					+ "	user_subrogante,   user_creator_user,  user_data_complete,   user_date_expiration, user_functionary, user_edif_id, user_observations,"
					+ "	user_pin, peop_id,  user_justification_access,  user_central_functionary,  user_is_area_boss,  user_active_as_facilitator,"
					+ "	user_immediate_superior, user_work_performance_ratio"
					+ "	from suia_iii.project_facilitators pf inner join users u on u.user_id = pf.user_id"
					+ "	where prfa_acceptation is true and prfa_status is true group by"
					+ "	pf.user_id, user_name, user_password, user_docu_id, user_temp_password, user_token, user_creation_user,"
					+ "	user_creation_date, user_user_update, user_date_update, justification_access, temp_password, area_id, user_status,"
					+ "	user_subrogante,   user_creator_user,  user_data_complete,   user_date_expiration, user_functionary, user_edif_id, user_observations,"
					+ "	user_pin, peop_id,  user_justification_access,  user_central_functionary,  user_is_area_boss,  user_active_as_facilitator,"
					+ "	user_immediate_superior, user_work_performance_ratio, prfa_acceptation, prfl_resettaskok"
					+ "	order by pf.user_id ) )u group by"
					+ "	user_id, user_name, user_password, user_docu_id, user_temp_password, user_token, user_creation_user,"
					+ "	user_creation_date, user_user_update, user_date_update, justification_access, temp_password, area_id, user_status,"
					+ "	user_subrogante,   user_creator_user,  user_data_complete,   user_date_expiration, user_functionary, user_edif_id, user_observations,"
					+ "	user_pin, peop_id,  user_justification_access,  user_central_functionary,  user_is_area_boss,  user_active_as_facilitator,"
					+ "	user_immediate_superior, user_work_performance_ratio ) union all ("
					+ "	select user_id, sum(total) as total,  user_name, user_password, user_docu_id, user_temp_password, user_token, user_creation_user,"
					+ "	user_creation_date, user_user_update, user_date_update, justification_access, temp_password, area_id, user_status,"
					+ "	user_subrogante,   user_creator_user,  user_data_complete,   user_date_expiration, user_functionary, user_edif_id, user_observations,"
					+ "	user_pin, peop_id,  user_justification_access,  user_central_functionary,  user_is_area_boss,  user_active_as_facilitator,"
					+ "	user_immediate_superior, user_work_performance_ratio"
					+ "	from((select pf.user_id as user_id, (prfl_resettask) as total, user_name, user_password, user_docu_id, user_temp_password, user_token, user_creation_user,"
					+ "	user_creation_date, user_user_update, user_date_update, justification_access, temp_password, area_id, user_status,"
					+ "	user_subrogante,   user_creator_user,  user_data_complete,   user_date_expiration, user_functionary, user_edif_id, user_observations,"
					+ "	user_pin, peop_id,  user_justification_access,  user_central_functionary,  user_is_area_boss,  user_active_as_facilitator,"
					+ "	user_immediate_superior, user_work_performance_ratio from suia_iii.project_facilitators_log pf"
					+ "	inner join users u on u.user_id = pf.user_id"
					+ "	where prfl_automatic_negation is false group by"
					+ "	pf.user_id, user_name, user_password, user_docu_id, user_temp_password, user_token, user_creation_user,"
					+ "	user_creation_date, user_user_update, user_date_update, justification_access, temp_password, area_id, user_status,"
					+ "	user_subrogante,   user_creator_user,  user_data_complete,   user_date_expiration, user_functionary, user_edif_id, user_observations,"
					+ "	user_pin, peop_id,  user_justification_access,  user_central_functionary,  user_is_area_boss,  user_active_as_facilitator,"
					+ "	user_immediate_superior, user_work_performance_ratio, prfl_automatic_negation, prfl_resettask"
					+ "	order by pf.user_id ))y group by"
					+ "	user_id, user_name, user_password, user_docu_id, user_temp_password, user_token, user_creation_user,"
					+ "	user_creation_date, user_user_update, user_date_update, justification_access, temp_password, area_id, user_status,"
					+ "	user_subrogante,   user_creator_user,  user_data_complete,   user_date_expiration, user_functionary, user_edif_id, user_observations,"
					+ "	user_pin, peop_id,  user_justification_access,  user_central_functionary,  user_is_area_boss,  user_active_as_facilitator,"
					+ "	user_immediate_superior, user_work_performance_ratio ) order by 1 ) t"
					+ "	group by user_id, user_name, user_password, user_docu_id, user_temp_password, user_token, user_creation_user,"
					+ "	user_creation_date, user_user_update, user_date_update, justification_access, temp_password, area_id, user_status,"
					+ "	user_subrogante,   user_creator_user,  user_data_complete,   user_date_expiration, user_functionary, user_edif_id, user_observations,"
					+ "	user_pin, peop_id,  user_justification_access,  user_central_functionary,  user_is_area_boss,  user_active_as_facilitator,"
					+ "	user_immediate_superior, user_work_performance_ratio) t)p on p.user_id = ru.user_id"
					+ "	where ru.role_id = 6 and user_status = true and rous_status = true order by 1, 2 ) v inner join users u on v.user_id = u.user_id order by total, USER_CREATION_DATE)p"
					+ "	left join ( select actualowner_id ,   total"
					+ "	from ( select  t1.actualowner_id, total"
					+ "	from dblink('"+dblinkBpmsSuiaiii+"',"
					+ "	'select"
					+ "	actualowner_id, count(*) as total"
					+ "	from task   where  (status in (''InProgress'' ,''Reserved'') and formname=''recibir_informacion'')  group by actualowner_id"
					+ "	') as t1(actualowner_id character varying(255), total integer"
					+ "	)  group by actualowner_id, total  ) y group by actualowner_id, total )xx on xx.actualowner_id  = user_name"
					+ "	order by 1,9 ) u inner join people pe on pe.peop_id = u.peop_id"
					+ "	where area_id="+areaid +" "+ excluir_cadena +" ) p inner join ("
					+ "	select carga ,  usuario, nombreusuario, fechacreacion, gestionados"
					+ "	from ( select   carga ,  usuario, nombreusuario, fechacreacion, gestionados"
					+ "	from dblink('"+dblinkSuiaVerde+"','select carga, usuario, nombreusuario, fechacreacion, case when gestionados is null then 0 else gestionados end as gestionados"
					+ "	from (  select DISTINCT (select count(*) from jbpm4_task where assignee_ = u.nombreusuario) as Carga, u.id as usuario,u.nombreusuario,u.fechacreacion,"
					+ "	(select sum(pf.resetgestionados) from proyectofacilitador as pf  where pf.usuario_id = u.id and (pf.aceptaproyecto = ''SI'' or"
					+ "	pf.aceptaproyecto = ''PENDIENTE'' or pf.aceptaproyecto = ''NO'')) as gestionados  from persona p"
					+ "	inner join usuario u on p.id=u.entidad_id  inner join rolusuario ru on u.id=ru.usuarioid"
					+ "	inner join rol r on r.id=ru.rolid" 
					+ "	where u.estadousuario = TRUE  AND ( r.id = 7)"
					+ "	order by  gestionados,fechacreacion  ) y"
					+ "	order by gestionados, fechacreacion' ) as t1 (carga integer,  usuario character varying(255), nombreusuario character varying(255), fechacreacion timestamp, gestionados integer"
					+ "	) ) y group by carga ,  usuario, nombreusuario, fechacreacion, gestionados )xx on xx.nombreusuario  = user_name"
					+ "	)p order by gestionados, fechacreacion)i";
		}

		
				List<Object []> lista = crudServiceBean.getEntityManager().createNativeQuery(sqllista).getResultList();        		
				List<Usuario> usuariosList = new ArrayList<Usuario>();
				if (lista.size() > 0) {
					for (Object[] facilitator : lista) {
						Usuario usuari= new Usuario();
						Persona persona= new Persona();
						String f=String.valueOf(facilitator[0]);
						usuari.setId(Integer.parseInt(f));
						usuari.setNombre((String)facilitator[1]);
						usuari.setPassword((String)facilitator[2]);
						persona.setNombre((String) facilitator[3]);
						usuari.setPersona(persona);
						usuariosList.add(usuari);
					}
					
				}
				
				return usuariosList;
		
//		Query query = crudServiceBean.getEntityManager().createQuery("SELECT u FROM Usuario u, RolUsuario ru INNER JOIN ru.usuario us INNER JOIN ru.rol r WHERE r.nombre =:rol AND u.area.areaName like :nombreArea and u.estado = true and u.esFacilitador=true "+excluir_cadena+" GROUP BY u");
//		query.setParameter("rol", rol);
//        query.setParameter("nombreArea", nombreArea);
//        usuarios=query.getResultList();  
//		return usuarios;
	}

	/**
	 * NO SE UTILIZA ESTE METODO
	 * @return
	 */
	public List<Usuario> listarFacilitadores() {
		return usuarioServiceBean.buscarUsuarioPorRolDP(Constantes.getRoleAreaName(FacilitadorServiceBean.FACILITADOR_ROLE));
	}
	
	@SuppressWarnings("unchecked")
	public List<Usuario> buscarFacilitadoresAleatorio(List<Usuario> excluir) {
		String excluir_cadena = "";
		if (excluir.size() > 0) {
			String ids_exclude = "";
			for (Usuario usuario : excluir) {
				if (ids_exclude.isEmpty()) {
					ids_exclude = Integer.toString(usuario.getId());
				} else {
					ids_exclude += ", " + Integer.toString(usuario.getId());
				}
			}
			excluir_cadena = " and au.usuario.id not in (" + ids_exclude + ")";
		}

		String rol = Constantes
				.getRoleAreaName(FacilitadorServiceBean.FACILITADOR_ROLE);
		
		Area area = areaFacade.getAreaPorAreaAbreviacion(Constantes.SIGLAS_DIRECCION_REGULARIZACION_AMBIENTAL);
		
		List<Usuario> usuarios  = crudServiceBean
				.getEntityManager()
				.createQuery(
						" SELECT au.usuario FROM RolUsuario ru, AreaUsuario au" 
								+ " where ru.usuario.id = au.usuario.id and "
								+ " ru.rol.nombre =:rol AND "
								+ " au.area.id =:idArea and "
								+ " au.estado = true and ru.estado = true and au.usuario.estado = true " + excluir_cadena
				+ " and au.usuario.esFacilitador = true ORDER BY random()")
				.setParameter("rol", rol).setParameter("idArea", area.getId()).getResultList();

		return usuarios;
	}

}
