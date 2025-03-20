package ec.gob.ambiente.suia.carga.facade;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.kie.api.task.model.TaskSummary;

import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.AreaUsuario;
import ec.gob.ambiente.suia.domain.Persona;
import ec.gob.ambiente.suia.domain.RolUsuario;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.dto.UserWorkload;
import ec.gob.ambiente.suia.persona.facade.PersonaFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.webservice.facade.AsignarTareaFacade;

/**
 * 
 * <b> Facade que permite reasignar la tarea por carga laboral </b>
 * 
 * @author pganan
 * @version Revision: 1.0
 *          <p>
 *          [Autor: pganan, Fecha: 22/12/2014]
 *          </p>
 */
@Stateless
public class CargaLaboralFacade {

	@EJB
	private UsuarioFacade usuarioFacade;

	@EJB
	private PersonaFacade personaFacade;

	@EJB(lookup = Constantes.JBPM_EJB_TASK_BEAN)
	private TaskBeanFacade taskBeanFacade;

	@EJB
	private CrudServiceBean crudServiceBean;
	
	@EJB
	private AsignarTareaFacade asignarTareaFacade;

	/**
	 * 
	 * <b> Método que permite obtener la cantidad de tarea por actor.</b>
	 * 
	 * @author pganan
	 * @version Revision: 1.0
	 *          <p>
	 *          [Autor: pganan, Fecha: 17/11/2014]
	 *          </p>
	 * @param usuario
	 * @param password
	 * @param deploymentId
	 * @return
	 */
	public int tareasPorUsuario(String actorId, String userName, String password, String deploymentId) {
		try {
			int cant = 0;
			List<TaskSummary> tareas = null;
			tareas = taskBeanFacade.retrieveTaskList(actorId, userName, deploymentId, password,
					Constantes.getUrlBusinessCentral(), Constantes.getRemoteApiTimeout());
			cant = tareas.size();
			return cant;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 
	 * <b> Método que permite obtener la carga laboral. </b>
	 * 
	 * @author carlos.pupo
	 * @version Revision: 1.0
	 *          <p>
	 *          [Autor: carlos.pupo, Fecha: 22/12/2014]
	 *          </p>
	 * @return
	 */
	public List<UserWorkload> cargaLaboralPorUsuario(Usuario usuarioTemplate) {
		return listarUsuariosContenganRol(usuarioTemplate, null, 1000, 0);
	}

	/**
	 * 
	 * <b> Método que permite obtener la carga laboral. </b>
	 * 
	 * @author carlos.pupo
	 * @version Revision: 1.0
	 *          <p>
	 *          [Autor: carlos.pupo, Fecha: 22/12/2014]
	 *          </p>
	 * @return
	 */
	public List<UserWorkload> cargaLaboralPorUsuario(Usuario usuarioTemplate, String deploymentId, String userName,
			String password) {
		List<UserWorkload> usuarios = listarUsuariosContenganRol(usuarioTemplate, null, 100, 0);
		int total = 0;

		for (UserWorkload usuario : usuarios) {
			usuario.setTramites(tareasPorUsuario(usuario.getUserName(), userName, password, deploymentId));
			total += usuario.getTramites();
		}

		if (total > 0) {
			for (UserWorkload usuario : usuarios) {
				usuario.setCarga(calcularPorcentaje(usuario.getTramites(), total));
			}
		}

		return usuarios;
	}

	/**
	 * <b> Lista los usuarios por roles. </b>
	 * 
	 * @author carlos.pupo
	 * @version Revision: 1.0
	 *          <p>
	 *          [Autor: carlos.pupo]
	 *          </p>
	 * @return
	 */
	public List<UserWorkload> listarUsuariosContenganRol(Usuario usuario, String nameLike, int size, int start) {
		usuario = crudServiceBean.find(Usuario.class, usuario.getId());
		List<Integer> rolesIds = new ArrayList<Integer>();
		for (RolUsuario rolUsuario : usuario.getRolUsuarios()) {
			rolesIds.add(rolUsuario.getIdRol());
		}
		
		String conditionRoles = (rolesIds.size() > 0) ? " AND ru.role_id IN :rolesIds " : "" ;
		
		List<Integer> areasId = new ArrayList<Integer>();
		for(AreaUsuario areaUsuario : usuario.getListaAreaUsuario()){
			areasId.add(areaUsuario.getArea().getId());
		}
		
		if(nameLike == null)
			nameLike = "";
		nameLike = "%" + nameLike.toLowerCase() + "%";

		String nativeQuery = "SELECT u.user_id, u.user_name, p.peop_name FROM users u, people p, suia_iii.roles_users ru, suia_iii.roles r, areas_users au "
				+ "WHERE u.user_name <> :userName AND u.area_id IN :areasId and au.user_id = ru.user_id "
				+ " AND (lower(unaccent(u.user_name)) like unaccent(:nameLike) OR "
				+ "lower(unaccent(p.peop_name)) like unaccent(:nameLike) OR lower(unaccent(r.role_name)) like unaccent(:nameLike)) "
				+ " AND u.peop_id = p.peop_id AND ru.user_id = u.user_id AND r.role_id = ru.role_id "
				+ conditionRoles
				+ " AND u.user_status = true AND au.arus_status = true LIMIT :size OFFSET :start";		

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("userName", usuario.getNombre());
		if (rolesIds.size() > 0)
			parameters.put("rolesIds", rolesIds);
		parameters.put("size", size);
		parameters.put("start", start);
		parameters.put("nameLike", nameLike);
		parameters.put("areasId", areasId);

		List<Object> result = crudServiceBean.findByNativeQuery(nativeQuery, parameters);

		List<UserWorkload> users = new ArrayList<UserWorkload>();
		for (Object object : result) {
			users.add(new UserWorkload((Object[]) object));
		}

		return users;
	}
	
	/**
	 * <b> Lista los usuarios por roles. </b>
	 * 
	 * @author carlos.pupo
	 * @version Revision: 1.0
	 *          <p>
	 *          [Autor: carlos.pupo]
	 *          </p>
	 * @return
	 */
	public List<UserWorkload> listarUsuariosContenganRol(Usuario usuario, String nameLike) {
		usuario = crudServiceBean.find(Usuario.class, usuario.getId());
		List<Integer> rolesIds = new ArrayList<Integer>();
		for (RolUsuario rolUsuario : usuario.getRolUsuarios()) {
			rolesIds.add(rolUsuario.getIdRol());
		}	
		
		String conditionRoles = (rolesIds.size() > 0) ? " AND ru.role_id IN :rolesIds " : "" ;
		
		if(nameLike == null)
			nameLike = "";
		nameLike = "%" + nameLike.toLowerCase() + "%";
		
		List<Integer> areasId = new ArrayList<Integer>();
		for(AreaUsuario areaUsuario : usuario.getListaAreaUsuario()){
			areasId.add(areaUsuario.getArea().getId());
		}

		String nativeQuery = "SELECT distinct(u.user_id), u.user_name, p.peop_name "
				+ "FROM users u, people p, suia_iii.roles_users ru, suia_iii.roles r, areas_users au "
				+ "WHERE u.user_name <> :userName AND au.user_id = u.user_id AND au.area_id IN :areasId "
				+ " AND (lower(unaccent(u.user_name)) like unaccent(:nameLike) OR "
				+ " lower(unaccent(p.peop_name)) like unaccent(:nameLike) OR lower(unaccent(r.role_name)) like unaccent(:nameLike)) "
				+ " AND u.peop_id = p.peop_id AND ru.user_id = u.user_id AND r.role_id = ru.role_id "
				+ conditionRoles
				+ " AND u.user_status = true and au.arus_status = true";

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("userName", usuario.getNombre());
		if (rolesIds.size() > 0)
			parameters.put("rolesIds", rolesIds);
		parameters.put("nameLike", nameLike);
		parameters.put("areasId", areasId);

		List<Object> result = crudServiceBean.findByNativeQuery(nativeQuery, parameters);

		List<UserWorkload> users = new ArrayList<UserWorkload>();
		for (Object object : result) {
			users.add(new UserWorkload((Object[]) object));
		}

		return users;
	}

	/**
	 * <b> Cuenta la lista los usuarios por roles. </b>
	 * 
	 * @author carlos.pupo
	 * @version Revision: 1.0
	 *          <p>
	 *          [Autor: carlos.pupo]
	 *          </p>
	 * @return
	 */
	public int listarUsuariosContenganRolCount(Usuario usuario, String nameLike) {
		usuario = crudServiceBean.find(Usuario.class, usuario.getId());
		List<Integer> rolesIds = new ArrayList<Integer>();
		for (RolUsuario rolUsuario : usuario.getRolUsuarios()) {
			rolesIds.add(rolUsuario.getIdRol());
		}
		
		String conditionRoles = (rolesIds.size() > 0) ? " AND ru.role_id IN :rolesIds " : "" ;
		
		List<Integer> areasId = new ArrayList<Integer>();
		for(AreaUsuario areaUsuario : usuario.getListaAreaUsuario()){
			areasId.add(areaUsuario.getArea().getId());
		}

		if(nameLike == null)
			nameLike = "";
		nameLike = "%" + nameLike.toLowerCase() + "%";
		
		String nativeQuery = "SELECT COUNT(u.user_id) FROM users u, people p, suia_iii.roles_users ru, suia_iii.roles r, areas_users au "
				+ "WHERE u.user_name <> :userName AND u.area_id IN :areaId and au.user_id = ru.user_id "
				+ " AND (lower(unaccent(u.user_name)) like unaccent(:nameLike) OR "
				+ " lower(unaccent(p.peop_name)) like unaccent(:nameLike) OR lower(unaccent(r.role_name)) like unaccent(:nameLike)) "
				+ " AND u.peop_id = p.peop_id AND ru.user_id = u.user_id AND r.role_id = ru.role_id "
				+ conditionRoles
				+ " AND u.user_status = true AND au.arus_status = true";

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("userName", usuario.getNombre());
		if (rolesIds.size() > 0)
			parameters.put("rolesIds", rolesIds);
		parameters.put("nameLike", nameLike);
		parameters.put("areasId", areasId);

		BigInteger count = (BigInteger) crudServiceBean.findByNativeQuery(nativeQuery, parameters).get(0);

		return count.intValue();
	}

	/**
	 * 
	 * <b> Método que permite obtener la carga laboral. </b>
	 * 
	 * @author carlos.pupo
	 * @version Revision: 1.0
	 *          <p>
	 *          [Autor: carlos.pupo, Fecha: 22/12/2014]
	 *          </p>
	 * @return
	 */
	public List<Usuario> cargaLaboralPorUsuario(String rol, String subArea, String deploymentId, String userName,
			String password) {
		List<Usuario> usuarios = usuarioFacade.buscarUsuarioPorRol(rol);
		Persona persona = null;
		int total = 0;
		for (Usuario usuario : usuarios) {
			for (RolUsuario rolUsuario : usuario.getRolUsuarios()) {
				rolUsuario.getRol().getNombre();
			}
			usuario.setNumeroTramites(tareasPorUsuario(usuario.getNombre(), userName, password, deploymentId));
			persona = personaFacade.buscarPersonaPorUsuario(usuario.getId());
			if (persona != null) {
				usuario.setNombrePersona(persona.getNombre());
			}
			total += usuario.getNumeroTramites();
		}
		if (total > 0) {
			for (Usuario usuario : usuarios) {
				usuario.setCarga(calcularPorcentaje(usuario.getNumeroTramites(), total));
			}
		}
		return usuarios;
	}

	public List<Usuario> cargaLaboralPorUsuarioAreas(String rol, Area[] subArea, String deploymentId, String userName,
			String password) {
		return cargaLaboralPorUsuario(rol, null, deploymentId, userName, password);
	}

	public List<Usuario> cargaLaboralPorUsuarioArea(String rol, String area){
		return asignarTareaFacade.getCargaLaboralPorUsuariosV2(rol, area);
	}
	public float calcularPorcentaje(int tareas, int total) {
		try {
			return (tareas * 100) / total;
		} catch (Exception e) {
			return 0;
		}
	}
	
	public List<UserWorkload> listarUsuariosContenganRolP(Usuario usuario, List<String> namesLike) {
		usuario = crudServiceBean.find(Usuario.class, usuario.getId());
		
		String nativeQuery = "SELECT distinct(u.user_id), u.user_name, p.peop_name  from suia_iii.roles_users ru "
				+ "inner join suia_iii.roles r on ru.role_id = r.role_id "
				+ "inner join public.users u on u.user_id = ru.user_id "
				+ "inner join public.people p on u.peop_id = p.peop_id "
				+ "where r.role_name IN :nameLike and ru.rous_status = true "
				+ "and u.user_status =true and u.user_name <> :userName";


		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("userName", usuario.getNombre());
		parameters.put("nameLike", namesLike);

		List<Object> result = crudServiceBean.findByNativeQuery(nativeQuery, parameters);

		List<UserWorkload> users = new ArrayList<UserWorkload>();
		for (Object object : result) {
			users.add(new UserWorkload((Object[]) object));
		}

		return users;
	}
}
