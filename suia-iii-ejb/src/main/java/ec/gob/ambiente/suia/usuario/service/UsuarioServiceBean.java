package ec.gob.ambiente.suia.usuario.service;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.*;
import ec.gob.ambiente.suia.dto.EntityUsuario;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.persona.facade.PersonaFacade;
import ec.gob.ambiente.suia.utils.EjecutarSentenciasNativas;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateless
public class UsuarioServiceBean {

	@EJB
	private CrudServiceBean crudServiceBean;
	@EJB
	private EjecutarSentenciasNativas ejecutarSentenciasNativas;
	@EJB
	private PersonaFacade personaFacade;

	@SuppressWarnings("unchecked")
	public List<Usuario> listarUsuarios() {

		List<Usuario> usuarios = (List<Usuario>) crudServiceBean.findAll(Usuario.class);
		return usuarios;
	}

	public Usuario buscarUsuario(String nombre) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("name", nombre != null ? nombre.trim() : "");
		try {
			Usuario usuario = (Usuario) crudServiceBean.findByNamedQuerySingleResult(Usuario.FIND_BY_USER, params);
			if (usuario != null) {
				usuario.getRolUsuarios().size();
				usuario.getPersona();
//				usuario.getArea();
			}
			return usuario;
		} catch (NoResultException ex) {

		}
		return null;
	}
	
	public Usuario buscarUsuarioUrl(String nombre) {
		Usuario usuarios = (Usuario) crudServiceBean.getEntityManager()
				.createQuery(" SELECT ru FROM Usuario ru where ru.usuarioCodigoCapcha =:nombre")
				.setParameter("nombre", nombre).getSingleResult();
		return usuarios;
	}

	public Usuario buscarUsuarioWithOutFilters(String nombre) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("name", nombre != null ? nombre.trim() : "");
		try {
			Usuario usuario = (Usuario) crudServiceBean.findByNamedQuerySingleResultWithOutFilters(
					Usuario.FIND_BY_USER, params);
			if (usuario != null) {
				usuario.getRolUsuarios().size();
				usuario.getPersona();
//				usuario.getArea();
			}
			return usuario;
		} catch (NoResultException ex) {

		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<Usuario> buscarUsuarioPorRol(String rol) {
		List<Usuario> usuarios = crudServiceBean.getEntityManager()
				.createQuery(" SELECT ru.usuario FROM RolUsuario ru where ru.rol.nombre =:rol")
				.setParameter("rol", rol).getResultList();
		return usuarios;
	}
	
	/**
	 * NO SE UTILIZA ESTE METODO
	 * @param rol
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Usuario> buscarUsuarioPorRolDP(String rol) {
		List<Usuario> usuarios = crudServiceBean.getEntityManager()
				.createQuery(" SELECT ru.usuario FROM RolUsuario ru "						
						+ "where ru.rol.nombre =:rol and ru.usuario.area.areaName='DIRECCIÓN NACIONAL DE PREVENCIÓN DE LA CONTAMINACIÓN AMBIENTAL' ")
				.setParameter("rol", rol).getResultList();
		return usuarios;
	}

	//cambio
	@SuppressWarnings("unchecked")
	public List<Usuario> buscarUsuarioPorRolNombreArea(String rol, String nombreArea) {
		
		List<Usuario> usuarios = crudServiceBean
		.getEntityManager()
		.createQuery(
				" SELECT ru.usuario FROM RolUsuario ru, AreaUsuario au "
						+ " where ru.usuario.id = au.usuario.id "
						+ " and ru.rol.nombre =:rol and au.area.areaName = :nombreArea"
						+ " and ru.usuario.estado = true and au.estado = true and ru.estado = true")
		.setParameter("rol", rol).setParameter("nombreArea", nombreArea).getResultList();
	
		
//		List<Usuario> usuarios = crudServiceBean
//				.getEntityManager()
//				.createQuery(
//						" SELECT u FROM RolUsuario ru"
//								+ "	inner join ru.usuario u"
//								+ " inner join ru.rol r "
//								+ " inner join u.area a"
//								+ " left join a.area ap "
//								+ " where r.nombre =:rol and (a.areaName = :nombreArea or ap.areaName = :nombreArea ) and u.estado = true")
//				.setParameter("rol", rol).setParameter("nombreArea", nombreArea).getResultList();
		return usuarios;
	}
	
	//cambio
	@SuppressWarnings("unchecked")
	public List<Usuario> buscarUsuarioPorRolNombreAreaUnico(String rol, String nombreArea, Integer usuarioId) throws ServiceException {
				
		List<Usuario> usuarios = crudServiceBean
				.getEntityManager()
				.createQuery(
						" SELECT ru.usuario FROM RolUsuario ru, AreaUsuario au  "						
								+ " where ru.usuario.id = au.usuario.id "
								+ " and ru.rol.nombre =:rol and (au.area.areaName = :nombreArea or au.area.area.areaName = :nombreArea ) "
								+ " and au.usuario.id <>:usuario and au.usuario.estado = true and au.estado = true")
				.setParameter("rol", rol).setParameter("nombreArea", nombreArea).setParameter("usuario", usuarioId).getResultList();
						
		return usuarios;
	}
	
	/**
	 * Cambio
	 * @param rol
	 * @param nombreArea
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Usuario> buscarUsuarioPorRolNombreArea(List<String> rol, String nombreArea) {
		
		List<Usuario> usuarios = crudServiceBean
		.getEntityManager()
		.createQuery(
				" SELECT ru.usuario FROM RolUsuario ru, AreaUsuario au "						
						+ " where ru.usuario.id = au.usuario.id "
						+ " and ru.rol.nombre in :rol and (au.area.areaName = :nombreArea or au.area.area.areaName = :nombreArea ) "
						+ " and ru.usuario.estado = true and au.estado = true")
		.setParameter("rol", rol).setParameter("nombreArea", nombreArea).getResultList();
		
//		List<Usuario> usuarios = crudServiceBean
//				.getEntityManager()
//				.createQuery(
//						" SELECT u FROM RolUsuario ru"
//								+ "	inner join ru.usuario u"
//								+ " inner join ru.rol r "
//								+ " inner join u.area a"
//								+ " left join a.area ap "
//								+ " where r.nombre in :rol and (a.areaName = :nombreArea or ap.areaName = :nombreArea ) and u.estado = true")
//				.setParameter("rol", rol).setParameter("nombreArea", nombreArea).getResultList();
		return usuarios;
	}

	public List<EntityUsuario> listarUsuarioEntity() throws ServiceException {
		StringBuilder sb = new StringBuilder();
		sb.append("select u.user_id, u.user_name, u.user_creation_date, r.role_name");
		sb.append(" from users AS u LEFT OUTER JOIN suia_iii.roles_users AS ru ON (u.user_id = ru.user_id)");
		sb.append(" LEFT OUTER JOIN suia_iii.roles AS r ON (r.role_id = ru.role_id) order by u.user_id");
		/*
		 * sb.append("Select u.user_id, u.user_name, u.user_creation_date, p.peop_name from users u left join people p on u.peop_id = p. peop_id where u.user_status = true");
		sb.append("Select u.user_id, u.user_name, u.user_creation_date, p.peop_name, o.orga_name_organization"
				+ " from users u left join people p on u.peop_id = p. peop_id left join organizations o on (o.peop_id = p.peop_id  and  u.user_name = o.orga_ruc) "
				+ " where u.user_status = true  order by u.user_name");
		 */
		return ejecutarSentenciasNativas.listarPorSentenciaSql(sb.toString(), EntityUsuario.class, null);
	}
	
	public List<EntityUsuario> listarUsuarioEntityListarAdmin() throws ServiceException {
			StringBuilder sb = new StringBuilder();
//	sb.append("Select u.user_id, u.user_name, u.user_creation_date, p.peop_name, o.orga_name_organization, u.user_status,r.role_name"
//			+ "  from users u left join people p on u.peop_id = p. peop_id left join organizations o on (o.peop_id = p.peop_id  and  u.user_name = o.orga_ruc)"
//			+ "	 LEFT OUTER JOIN suia_iii.roles_users AS ru ON (u.user_id = ru.user_id)"
//			+ "  LEFT OUTER JOIN suia_iii.roles AS r ON (r.role_id = ru.role_id)   order by u.user_name ");
			
			//para sacar la provincia del usuario
			sb.append("Select u.user_id, u.user_name, u.user_creation_date, p.peop_name, o.orga_name_organization, u.user_status,r.role_name, "
					+ "(select gelo_name from geographical_locations where gelo_id = "
					+ "(select gelo_parent_id from geographical_locations where gelo_id = g.gelo_parent_id) "
					+ ") as gelo_name "
					+ "from users u "
					+ "left join people p on u.peop_id = p. peop_id "
					+ "left join organizations o on (o.peop_id = p.peop_id and u.user_name = o.orga_ruc) "
					+ "LEFT OUTER JOIN suia_iii.roles_users AS ru ON (u.user_id = ru.user_id) and ru.rous_status=true "
					+ "LEFT OUTER JOIN suia_iii.roles AS r ON (r.role_id = ru.role_id) "
					+ "left join geographical_locations g on g.gelo_id = p.gelo_id "
					+ "order by u.user_name");
		return ejecutarSentenciasNativas.listarPorSentenciaSql(sb.toString(), EntityUsuario.class, null);
	}
	
			
					   
	
	public List<EntityUsuario> listarUsuarioEntityReasigancion() throws ServiceException {
//		StringBuilder sb = new StringBuilder();
//		sb.append("select u.user_id, u.user_name, u.user_creation_date, r.role_name");
//		sb.append(" from users AS u LEFT OUTER JOIN suia_iii.roles_users AS ru ON (u.user_id = ru.user_id)");
//		sb.append(" LEFT OUTER JOIN suia_iii.roles AS r ON (r.role_id = ru.role_id) order by u.user_id");
//		return ejecutarSentenciasNativas.listarPorSentenciaSql(sb.toString(), EntityUsuario.class, null);
		StringBuilder sb = new StringBuilder();
		sb.append("select u.user_id, u.user_name, u.user_creation_date, r.role_name, p.peop_name, u.user_status ");
		sb.append(" from users AS u LEFT OUTER JOIN suia_iii.roles_users AS ru ON (u.user_id = ru.user_id) and ru.rous_status in (true,false) ");
		sb.append(" LEFT OUTER JOIN suia_iii.roles AS r ON (r.role_id = ru.role_id)");
		sb.append(" left join people p on u.peop_id = p. peop_id ");
		sb.append(" LEFT OUTER JOIN areas_users au on au.user_id = u.user_id");
		sb.append(" LEFT OUTER JOIN areas as ar on (ar.area_id=au.area_id)");		
		sb.append(" where r.role_name not in ('MONITOREO MATRIZ CONTROL SEGUIMIENTO','AUTORIDAD AMBIENTAL PUNTOS MONITOREO PROVINCIAL','MAQUINARIA, EQUIPO Y TECNOLOGÍA','PUNTOS DE MONITOREO','TAXA-AUTORIDAD FISCALÍA','admin','PROPONENTE REA','PROPONENTE CEA','sujeto de control','user') "
				+ " and  u.user_status in (true,false) and au.arus_status in (true,false) order by u.user_id ");
		return ejecutarSentenciasNativas.listarPorSentenciaSql(sb.toString(), EntityUsuario.class, null);
	}
	

	public List<EntityUsuario> listarUsuarioEntityReasignacion(String nombreArea) throws ServiceException {
		StringBuilder sb = new StringBuilder();
		sb.append("select u.user_id, u.user_name, u.user_creation_date, r.role_name, p.peop_name, u.user_status ");
		sb.append(" from users AS u LEFT OUTER JOIN suia_iii.roles_users AS ru ON (u.user_id = ru.user_id) and ru.rous_status=true");
		sb.append(" LEFT OUTER JOIN suia_iii.roles AS r ON (r.role_id = ru.role_id)");
		sb.append(" left join people p on u.peop_id = p. peop_id ");
		sb.append(" LEFT OUTER JOIN areas_users au on au.user_id = u.user_id");
		sb.append(" LEFT OUTER JOIN areas as ar on (ar.area_id=au.area_id)");		
		sb.append(" where u.user_status=true and au.arus_status = true and ar.area_name = '"+nombreArea+"' order by u.user_id ");
		return ejecutarSentenciasNativas.listarPorSentenciaSql(sb.toString(), EntityUsuario.class, null);
	}
	
	
	public List<EntityUsuario> listarUsuarioEntityAdministrar(String nombreArea) throws ServiceException {
		StringBuilder sb = new StringBuilder();
		sb.append("select u.user_id, u.user_name, u.user_creation_date, r.role_name, p.peop_name, u.user_status ");
		sb.append(" from users AS u LEFT OUTER JOIN suia_iii.roles_users AS ru ON (u.user_id = ru.user_id) and ru.rous_status=true");
		sb.append(" LEFT OUTER JOIN suia_iii.roles AS r ON (r.role_id = ru.role_id)");
		sb.append(" left join people p on u.peop_id = p. peop_id ");
		sb.append(" left join areas_users au on au.user_id = u.user_id");
		sb.append(" LEFT OUTER JOIN areas as ar on (ar.area_id=au.area_id)");		
		sb.append(" where ar.area_name = '"+nombreArea+"' and au.arus_status = true order by u.user_id ");
		return ejecutarSentenciasNativas.listarPorSentenciaSql(sb.toString(), EntityUsuario.class, null);
	}

	public List<EntityUsuario> listarUsuariosCompletoEntity() throws ServiceException {
		StringBuilder sb = new StringBuilder();
		sb.append("Select u.user_id, u.user_name, u.user_creation_date, p.peop_name from users u left join people p on u.peop_id = p. peop_id where u.user_status = true");
		sb.append("Select u.user_id, u.user_name, u.user_creation_date, p.peop_name, o.orga_name_organization"
				+ " from users u left join people p on u.peop_id = p. peop_id left join organizations o on (o.peop_id = p.peop_id  and  u.user_name = o.orga_ruc) "
				+ " where u.user_status = true  order by u.user_name");
		return ejecutarSentenciasNativas.listarPorSentenciaSql(sb.toString(), EntityUsuario.class, null);
	}

	public List<EntityUsuario> listarUsuariosCompletoEntity(Integer inicio, Integer cantidad, String nombre,
			String nombreCompleto) throws ServiceException {
		String like = "";
		Map<String, Object> parametros = new HashMap<String, Object>();
		if (!nombre.isEmpty()) {
			like = " AND LOWER(u.user_name) like LOWER(:nombre)";
			parametros.put("nombre", "%" + nombre + "%");
		}
		if (!nombreCompleto.isEmpty()) {
			like += " AND (LOWER(p.peop_name) like LOWER(:nombreCompleto) or LOWER(o.orga_name_organization) like LOWER(:nombreCompleto))";
			parametros.put("nombreCompleto", "%" + nombreCompleto + "%");
		}

		StringBuilder sb = new StringBuilder();
		sb.append("Select u.user_id, u.user_name, u.user_creation_date, p.peop_name, o.orga_name_organization"
				+ " from users u left join people p on u.peop_id = p. peop_id "
				+ " left join organizations o on (o.peop_id = p.peop_id  and  u.user_name = o.orga_ruc and o.orga_status = true) "
				+ " where u.user_status = true " + like + " order by o.orga_name_organization, p.peop_name LIMIT "
				+ cantidad.toString() + " OFFSET " + inicio.toString());
		return ejecutarSentenciasNativas.listarPorSentenciaSql(sb.toString(), EntityUsuario.class, parametros);
	}

	public Integer contarUsuariosCompletoEntity(String nombre, String nombreCompleto) {
		String like = "";
		Map<String, Object> parametros = new HashMap<String, Object>();
		if (!nombre.isEmpty()) {
			like = " AND LOWER(u.user_name) like LOWER(:nombre)";
			parametros.put("nombre", "%" + nombre + "%");
		}
		if (!nombreCompleto.isEmpty()) {
			like += " AND (LOWER(p.peop_name) like LOWER(:nombreCompleto) or LOWER(o.orga_name_organization) like LOWER(:nombreCompleto))";
			parametros.put("nombreCompleto", "%" + nombreCompleto + "%");
		}

		StringBuilder sb = new StringBuilder();
		sb.append("Select count(*) "
				+ " from users u left join people p on u.peop_id = p. peop_id "
				+ " left join organizations o on (o.peop_id = p.peop_id  and  u.user_name = o.orga_ruc and o.orga_status = true)"
				+ " where u.user_status = true " + like);

		List<Object> result = crudServiceBean.findByNativeQuery(sb.toString(), parametros);

		for (Object object : result) {
			return (((BigInteger) object).intValue());
		}

		return 0;
	}

	public Usuario buscarUsuarioPorId(Integer idUsuario) throws ServiceException {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("id", idUsuario);
		return (Usuario) crudServiceBean.findByNamedQuerySingleResultWithOutFilters(Usuario.FIND_BY_ID,
				param);
	}

	public Long validaRolUnico(Integer idRol, Integer idUsuario) throws ServiceException {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("idRol", idRol);
		param.put("idUsuario", idUsuario);
		return (Long) crudServiceBean.findByNamedQuerySingleResult(RolUsuario.COUNT_ROL_USUARIO, param);
	}

	public Long validaRolUnicoPorProvincia(Integer idRol, Usuario idUsuario) throws ServiceException {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("idRol", idRol);
		param.put("idUsuario", idUsuario);
		return (Long) crudServiceBean.findByNamedQuerySingleResult(RolUsuario.COUNT_ROL_USUARIO, param);
	}
	
	private Usuario buscarPorLoginPassword(final String login, final String password) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("login", login);
		params.put("password", password);
		params.put("estado", true);
		Usuario usuario;
		try {
			usuario = (Usuario) crudServiceBean.findByNamedQuerySingleResult(Usuario.FIND_BY_LOGIN_PASSWORD_STATE,
					params);
		} catch (RuntimeException e) {
			usuario = null;
		}
		return usuario;
	}

	@SuppressWarnings("unchecked")
	public List<RolUsuario> listarPorLoginPassword(final String login, final String password) throws ServiceException {
		Usuario usuario = buscarPorLoginPassword(login, password);
		if (usuario == null) {
			throw new ServiceException("Usuario no existe");
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("usuario", usuario.getId());
		try {
			List<RolUsuario> listarPorUsuario = (List<RolUsuario>) crudServiceBean.findByNamedQuery(
					RolUsuario.FIND_BY_USER_ROLE, params);
			return listarPorUsuario;
		} catch (RuntimeException e) {
			throw new ServiceException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Usuario> buscarUsuariosPorRolYArea(String rol, Area area) {
		
		List<Usuario> usuarios = crudServiceBean
		.getEntityManager()
		.createQuery(
				" SELECT ru.usuario FROM RolUsuario ru, AreaUsuario au " 
						+ " where ru.usuario.id = au.usuario.id "
						+ " and ru.rol.nombre =:rol" + " and ( au.area.id=:area or au.area.area.id = :area )" 
						+ " and ru.usuario.estado = true and ru.estado = true" )
		.setParameter("rol", rol).setParameter("area", area.getId()).getResultList();
			
//		List<Usuario> usuarios = crudServiceBean
//				.getEntityManager()
//				.createQuery(
//						" SELECT u FROM RolUsuario ru" 
//								+ "	inner join ru.usuario u" 
//								+ " inner join ru.rol r"
//								+ " where r.nombre =:rol" + " and ( u.area.id=:area or u.area.area.id = :area )" + " and u.estado = true" )
//				.setParameter("rol", rol).setParameter("area", area.getId()).getResultList();

		return usuarios;
	}

	public List<Usuario> buscarUsuariosPorArea(Area area) {
		if (area.getAreaAbbreviation().equals("PTE"))
			return null;
		@SuppressWarnings("unchecked")
		List<Usuario> usuarios = crudServiceBean.getEntityManager()
		.createQuery("SELECT au.usuario FROM AreaUsuario au where au.area.id=:area or au.area.area.id = :area")
		.setParameter("area", area.getId()).getResultList();
		
		
//		List<Usuario> usuarios = crudServiceBean.getEntityManager()
//				.createQuery("SELECT u FROM Usuario u where u.area.id=:area or u.area.area.id = :area")
//				.setParameter("area", area.getId()).getResultList();
		return usuarios;
	}

	public List<Usuario> buscarUsuarioResponsableArea(Area area) {
		@SuppressWarnings("unchecked")
		List<Usuario> usuarios = crudServiceBean
				.getEntityManager()
				.createQuery(
						"SELECT au.usuario FROM AreaUsuario au where (au.area.id=:area or au.area.area.id = :area ) and au.usuario.esResponsableArea = true and au.estado = true")
				.setParameter("area", area.getId()).getResultList();		
		
//		List<Usuario> usuarios = crudServiceBean
//				.getEntityManager()
//				.createQuery(
//						"SELECT u FROM Usuario u where (u.area.id=:area or u.area.area.id = :area ) and u.esResponsableArea = true")
//				.setParameter("area", area.getId()).getResultList();
		return usuarios;
	}
	public List<Usuario> buscarUsuarioResponsableAreaMAAE(Area area) {
		@SuppressWarnings("unchecked")
		List<Usuario> usuarios = crudServiceBean
				.getEntityManager()
				.createQuery(
						"SELECT au.usuario FROM AreaUsuario au where au.area.id=:area and a.usuario.esResponsableArea = true and au.estado = true")
				.setParameter("area", area.getId()).getResultList();
		
//		List<Usuario> usuarios = crudServiceBean
//				.getEntityManager()
//				.createQuery(
//						"SELECT u FROM Usuario u where u.area.id=:area and u.esResponsableArea = true")
//				.setParameter("area", area.getId()).getResultList();
		
		return usuarios;
	}

	@SuppressWarnings("unchecked")
	public Usuario buscarUsuarioJefeInmediato(String nombreUsuario) {		
		List<Usuario> usuarios = crudServiceBean.getEntityManager()
				.createQuery("SELECT u FROM Usuario u where (u.nombre=:nombreUsuario)")
				.setParameter("nombreUsuario", nombreUsuario).getResultList();

		if (!usuarios.isEmpty()) {
			if (usuarios.get(0).getJefeInmediato() != null) {
				return usuarios.get(0).getJefeInmediato();
			} else {
				
				String idAreas = "";
				for(AreaUsuario area :usuarios.get(0).getListaAreaUsuario()){
					idAreas += (idAreas.equals("")) ? area.getArea() .getId() : "," + area.getArea() .getId();
				}
							
				
				usuarios = crudServiceBean
						.getEntityManager()
						.createQuery(
								"SELECT u FROM Usuario u, AreaUsuario au "
								+ "where u.id = au.usuario.id and "
								+ "(au.area.id in ("+idAreas+") or au.area.area.id in ("+idAreas+") ) and u.esResponsableArea = true")
						.getResultList();

				if (!usuarios.isEmpty()) {
					return usuarios.get(0);
				}
			}
		}

		return null;
	}

	public List<Usuario> buscarUsuariosPorRolYTipoArea(String rol, TipoArea tipoArea) {
		@SuppressWarnings("unchecked")
		List<Usuario> usuarios = crudServiceBean
				.getEntityManager()
				.createQuery(
						" SELECT au.usuario FROM RolUsuario ru, AreaUsuario au " 
						+ " where ru.usuario.id = au.usuario.id "
						+ " and ru.rol.nombre =:rol "
						+ " and au.area.tipoArea.id =:tipoArea "
						+ " and au.estado = true")
				.setParameter("rol", rol).setParameter("tipoArea", tipoArea.getId()).getResultList();
		
//		List<Usuario> usuarios = crudServiceBean
//				.getEntityManager()
//				.createQuery(
//						" SELECT u FROM RolUsuario ru" 
//								+ "	inner join ru.usuario u" 
//								+ " inner join ru.rol r"
//								+ " where r.nombre =:rol" 
//								+ " and  u.area.tipoArea.id =:tipoArea")
//				.setParameter("rol", rol).setParameter("tipoArea", tipoArea.getId()).getResultList();

		return usuarios;
	}

	public List<EntityUsuario> listarUsuarioSujetoControl() throws ServiceException {
		StringBuilder sb = new StringBuilder();
		sb.append("select distinct u.user_id, p.peop_name, u.user_pin from users u, suia_iii.roles_users ru, people p,suia_iii.roles r");
		sb.append(" where u.user_id = ru.user_id AND ru.role_id = ").append(Rol.SUJETO_CONTROL)
				.append(" AND r.role_id = ").append(Rol.SUJETO_CONTROL)
				.append(" AND p.peop_id = u.peop_id AND u.user_status = true");
		sb.append(" order by p.peop_name");
		return ejecutarSentenciasNativas.listarPorSentenciaSql(sb.toString(), EntityUsuario.class, null);
	}

	@SuppressWarnings("unchecked")
	public List<RolUsuario> listarPorIdUsuario(final Integer idUsuario) throws ServiceException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("usuario", idUsuario);
		try {
			List<RolUsuario> listarPorUsuario = (List<RolUsuario>) crudServiceBean.findByNamedQuery(
					RolUsuario.FIND_BY_USER_ROLE, params);
			return listarPorUsuario;
		} catch (RuntimeException e) {
			throw new ServiceException(e);
		}
	}

	public void guardarSuplantacion(final AuditoriaSuplantacion auditoriaSuplantacion) throws ServiceException {
		try {
			crudServiceBean.saveOrUpdate(auditoriaSuplantacion);
		} catch (RuntimeException e) {
			throw new ServiceException(e);
		}
	}

	public Usuario buscarUsuarioCompleta(String nombre) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("name", nombre != null ? nombre.trim() : "");
		try {
			Usuario usuario = (Usuario) crudServiceBean.findByNamedQuerySingleResult(Usuario.FIND_BY_USER, params);
			if (usuario != null) {
				usuario.getRolUsuarios().size();
				usuario.getPersona().getId();
				//ver 6 de agosto 2021 CF
//				if(usuario.getArea()!=null) {
//					usuario.getArea().getId();
//				}
//				try {
//					usuario.getArea().getId();
//				} catch (Exception e) {
//				}
				usuario.getPersona().getId();
				usuario.getPersona().getTipoTratos().getId();
				if(usuario.getPersona().getUbicacionesGeografica()!=null)
				usuario.getPersona().getUbicacionesGeografica().getUbicacionesGeografica().getUbicacionesGeografica().getId();
			}
			return usuario;
		} catch (NoResultException ex) {

		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<Usuario> buscarUsuarioPorNombres(List<String> nombres) {
		List<Usuario> usuarios = crudServiceBean.getEntityManager()
				.createQuery(" SELECT u FROM Usuario u where u.nombre in :nombres").setParameter("nombres", nombres)
				.getResultList();
		return usuarios;
	}
	
	public List<Usuario> buscarUsuarios(String rol,String descripcion) {
		@SuppressWarnings("unchecked")
		List<Usuario> usuarios = crudServiceBean
				.getEntityManager()
				.createQuery("SELECT ru.usuario FROM RolUsuario ru"
						+ " WHERE ru.estado=true AND ru.rol.estado=true AND ru.usuario.estado=true"
						+ " AND ru.rol.nombre =:rol" 
						+ " AND ru.descripcion LIKE :descripcion" )
				.setParameter("rol", rol).setParameter("descripcion", descripcion).getResultList();

		return usuarios;
	}
	
	//SD	
	public List<Usuario> buscarUsuariosPorRolTipoArea(String rol, TipoArea tipoArea) {
		@SuppressWarnings("unchecked")
		List<Usuario> usuarios = crudServiceBean
		.getEntityManager()
		.createQuery(
				" SELECT ru.usuario FROM RolUsuario ru, AreaUsuario au " 
						+ " where  ru.usuario.id = au.usuario.id and "
						+ " ru.rol.nombre = :rol " 
						+ " and au.area.tipoArea.id =:tipoArea "
						+ " and au.usuario.estado = true "
						+ " and au.estado = true")
						.setParameter("rol", rol).setParameter("tipoArea", tipoArea.getId()).getResultList();
		
//		List<Usuario> usuarios = crudServiceBean
//				.getEntityManager()
//				.createQuery(
//						" SELECT u FROM RolUsuario ru" + "	inner join ru.usuario u" + " inner join ru.rol r"
//								+ " where r.nombre =:rol" + " and  u.area.tipoArea.id =:tipoArea and u.estado = true")
//								.setParameter("rol", rol).setParameter("tipoArea", tipoArea.getId()).getResultList();

		return usuarios;
	}
	
	public List<Usuario> buscarUsuariosPorRolYAreaEspecifica(String rol, Area area) {
		@SuppressWarnings("unchecked")
		List<Usuario> usuarios = crudServiceBean
				.getEntityManager()
				.createQuery(
						" SELECT ru.usuario FROM RolUsuario ru, AreaUsuario au " 
								+ " where ru.usuario.id = au.usuario.id and "
								+ " ru.rol.nombre =:rol" 
								+ " and au.area.id=:area " 
								+ " and au.usuario.estado = true "
								+ " and au.estado = true" )
				.setParameter("rol", rol).setParameter("area", area.getId()).getResultList();

		return usuarios;
	}
	
	@SuppressWarnings("unchecked")
	public List<Usuario> buscarUsuarioPorRolActivo(String rol) {
		List<Usuario> usuarios = crudServiceBean.getEntityManager()
				.createQuery(" SELECT ru.usuario FROM RolUsuario ru where ru.rol.nombre =:rol and ru.estado = true")
				.setParameter("rol", rol).getResultList();
		return usuarios;
	}
	
	@SuppressWarnings("unchecked")
	public Usuario buscarUsuarioSujetoControl(String nombre){
		List<Usuario> usuarios = crudServiceBean
				.getEntityManager()
				.createQuery(
						" SELECT ru.usuario FROM RolUsuario ru, Usuario u " 
								+ " where ru.usuario.id = u.id "
								+ " and ru.estado = true "
								+ " and u.estado = true "
								+ " and u.nombre = :nombre "
								+ " and ru.rol.id = "  + Rol.SUJETO_CONTROL)
				.setParameter("nombre", nombre).getResultList();
		
		if(usuarios != null && !usuarios.isEmpty()){
			return usuarios.get(0);
		}
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<Usuario> buscarUsuariosPorRolYAreaAleatorio(String rol, Area area) {
		
		List<Usuario> usuarios = crudServiceBean
		.getEntityManager()
		.createQuery(
				" SELECT ru.usuario FROM RolUsuario ru, AreaUsuario au " 
						+ " where ru.usuario.id = au.usuario.id "
						+ " and ru.rol.nombre =:rol" + " and ( au.area.id=:area or au.area.area.id = :area )" 
						+ " and ru.usuario.estado = true and ru.estado = true ORDER BY random()" )
		.setParameter("rol", rol).setParameter("area", area.getId()).getResultList();

		return usuarios;
	}
	
	public Usuario buscarUsuarioPorIdDesactivado(Integer id){
		try {
			
			String sql = "select u.user_id, u.user_name, u.peop_id, u.user_pin from users u where user_id = "+ id;
			
			List<Object> result = crudServiceBean.findByNativeQuery(sql, null);
			
			List<Usuario> lista = new ArrayList<>();
			if(result.size() > 0){
				for (Object object : result) {
					
					Persona persona = personaFacade.buscarPersonaPorPin(((Object[]) object)[1].toString());
					lista.add(new Usuario((Object[])object, persona));
				}			
			}
			
			if(!lista.isEmpty())
				return lista.get(0);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

}