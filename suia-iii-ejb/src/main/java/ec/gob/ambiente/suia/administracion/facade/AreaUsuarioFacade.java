package ec.gob.ambiente.suia.administracion.facade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.AreaUsuario;
import ec.gob.ambiente.suia.domain.UnidadArea;
import ec.gob.ambiente.suia.domain.UnidadAreaUsuario;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.dto.EntityUsuario;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.utils.EjecutarSentenciasNativas;

@LocalBean
@Stateless
public class AreaUsuarioFacade {
	
	@EJB
    private CrudServiceBean crudServiceBean;
	@EJB
	private EjecutarSentenciasNativas ejecutarSentenciasNativas;
	@EJB
	private UnidadAreaFacade unidadAreaFacade;
	
	@SuppressWarnings("unchecked")
	public List<AreaUsuario> buscarAreaUsuario(Usuario usuario){
		List<AreaUsuario> lista = new ArrayList<AreaUsuario>();
		try {
			
			Query q = crudServiceBean.getEntityManager().createQuery("Select a from AreaUsuario a where a.estado = true and a.usuario.id = :id and a.area.estado = true");
			q.setParameter("id", usuario.getId());
			
			lista = (List<AreaUsuario>)q.getResultList();		
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return lista;
	}
	
	public void guardarAreasUsuario(List<Area> listaAreas, Usuario usuario){		
		try{
			
			List<AreaUsuario> lista = buscarAreaUsuario(usuario);	
			
			List<UnidadAreaUsuario> listaUau = buscarporUsuario(usuario);
			
			if(lista != null && !lista.isEmpty()){
				for(AreaUsuario au : lista){
					au.setEstado(false);
					guardar(au, usuario);
				}
			}
			
			if(listaUau != null && !listaUau.isEmpty()){
				for(UnidadAreaUsuario item : listaUau){
					item.setEstado(false);
					guardarUAU(item, usuario);
				}
			}
			
			List<Area>listaAreasUnidadesSeleccionadasAux = new ArrayList<Area>();
			listaAreasUnidadesSeleccionadasAux.addAll(listaAreas);
			
			List<Area> listaAreasGuardar = new ArrayList<>();
			
			for(Area a1 : listaAreas){
				
				Comparator<Area> c = new Comparator<Area>() {
					
					@Override
					public int compare(Area o1, Area o2) {							
						return o1.getId().compareTo(o2.getId());
					}
				};
				
				Collections.sort(listaAreasGuardar, c);
				int index = Collections.binarySearch(listaAreasGuardar, new Area(a1.getId()), c);
				
				if(index >= 0){

				}else{
					a1.setListaUnidades(new ArrayList<Area>());
					listaAreasGuardar.add(a1);
				}				
			}
			
			
			for(Area a1 : listaAreas){
				for(Area a2 : listaAreasGuardar){
					if(a1.getIdUnidadArea() != null && a1.getId().equals(a2.getId())){
						a2.getListaUnidades().add(a1);
					}
				}
			}
			
			
			int i = 0;
			for(Area area : listaAreasGuardar){			
				
				AreaUsuario au = new AreaUsuario();
				au.setArea(area);
				au.setUsuario(usuario);
				au.setEstado(true);	
				if(i == 0){
					au.setPrincipal(true);
				}
				au.setOrden(i +1);
				
				if(area.getListaUnidades() != null && !area.getListaUnidades().isEmpty()){					
					
					guardar(au, usuario);
					
					for(Area a : area.getListaUnidades()){
						UnidadArea ua = unidadAreaFacade.buscarPorID(a.getIdUnidadArea());
						
						UnidadAreaUsuario uau = new UnidadAreaUsuario();	
						uau.setAreaUsuario(au);
						uau.setUnidadArea(ua);
						uau.setEstado(true);
											
						guardarUAU(uau, usuario);						
					}		
					
				}else{
					guardar(au, usuario);
				}
				i++;	
			}			
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	
	@SuppressWarnings("unchecked")
	public List<UnidadAreaUsuario> buscarporUsuario(Usuario usuario){
		try {
			List<UnidadAreaUsuario> lista = new ArrayList<UnidadAreaUsuario>();
			
			Query q = crudServiceBean.getEntityManager().createQuery("Select a from UnidadAreaUsuario a where a.estado = true and a.areaUsuario.usuario.id = :id");
			q.setParameter("id", usuario.getId());
			
			lista = (ArrayList<UnidadAreaUsuario>)q.getResultList();
			
			if(lista != null & !lista.isEmpty()){
				return lista;
			}	
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public void guardar(AreaUsuario obj, Usuario usuario){
		try {
			if(obj.getId()==null){
				obj.setUsuarioCreacion(usuario.getNombre());
				obj.setFechaCreacion(new Date());			
			}
			else{
				obj.setUsuarioModificacion(usuario.getNombre());
				obj.setFechaModificacion(new Date());			
			}
			crudServiceBean.saveOrUpdate(obj);	
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public AreaUsuario guardarObjeto(AreaUsuario obj, Usuario usuario){
		try {
			if(obj.getId()==null){
				obj.setUsuarioCreacion(usuario.getNombre());
				obj.setFechaCreacion(new Date());			
			}
			else{
				obj.setUsuarioModificacion(usuario.getNombre());
				obj.setFechaModificacion(new Date());			
			}
			return crudServiceBean.saveOrUpdate(obj);	
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void guardarUAU(UnidadAreaUsuario obj, Usuario usuario){
		try {
			if(obj.getId()==null){
				obj.setUsuarioCreacion(usuario.getNombre());
				obj.setFechaCreacion(new Date());			
			}
			else{
				obj.setUsuarioModificacion(usuario.getNombre());
				obj.setFechaModificacion(new Date());			
			}
			crudServiceBean.saveOrUpdate(obj);	
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<AreaUsuario> buscarPorAreaUsuario(Usuario usuario, String nombre){
		List<AreaUsuario> lista = new ArrayList<AreaUsuario>();
		try {
			
			Query q = crudServiceBean.getEntityManager().createQuery("Select a from AreaUsuario a where a.estado = true and a.usuario.id = :id and a.area.areaName = :nombre");
			q.setParameter("id", usuario.getId());
			q.setParameter("nombre", nombre);
			
			lista = (List<AreaUsuario>)q.getResultList();		
			
			if(lista != null && !lista.isEmpty()){
				return lista;
			}else{
				lista = new ArrayList<AreaUsuario>();
			}		
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;
	}
	
	@SuppressWarnings("unchecked")
	public String buscarAreaUsuarioNombres(Usuario usuario){
		List<AreaUsuario> lista = new ArrayList<AreaUsuario>();
		String nombres = "";
		try {
			
			Query q = crudServiceBean.getEntityManager().createQuery("Select a from AreaUsuario a where a.estado = true and a.usuario.id = :id");
			q.setParameter("id", usuario.getId());
			
			lista = (List<AreaUsuario>)q.getResultList();	
			
			if(lista != null && !lista.isEmpty()){
				for(AreaUsuario au : lista){
					nombres += au.getArea().getAreaName();
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return nombres;
	}
		
	@SuppressWarnings("unchecked")
	public List<AreaUsuario> buscarAreaUsuarioAbreviacion(Usuario usuario, String nombre){
		List<AreaUsuario> lista = new ArrayList<AreaUsuario>();
		try {
			
			Query q = crudServiceBean.getEntityManager().createQuery("Select a from AreaUsuario a where a.estado = true and a.usuario.id = :id and a.area.areaAbbreviation = :abreviacion");
			q.setParameter("id", usuario.getId());
			q.setParameter("abreviacion", nombre);
			
			lista = (List<AreaUsuario>)q.getResultList();		
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;
	}
	
	@SuppressWarnings("unchecked")
	public String buscarAreaUsuarioNombresSql(Usuario usuario){
		List<AreaUsuario> lista = new ArrayList<AreaUsuario>();
		String nombres = "";
		try {
			
			Query q = crudServiceBean.getEntityManager().createQuery("Select a from AreaUsuario a where a.estado = true and a.usuario.id = :id");
			q.setParameter("id", usuario.getId());
			
			lista = (List<AreaUsuario>)q.getResultList();	
			
			if(lista != null && !lista.isEmpty()){
				for(AreaUsuario au : lista){
					nombres += au.getArea().getAreaName() + ",";
				}
			}
			if(!nombres.isEmpty()){
				nombres = nombres.substring(0, nombres.length()-1);
			}			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return nombres;
	}
	
	@SuppressWarnings("unchecked")
	public String buscarAreaUsuarioIdSql(Usuario usuario){
		List<AreaUsuario> lista = new ArrayList<AreaUsuario>();
		String nombres = "";
		try {
			
			Query q = crudServiceBean.getEntityManager().createQuery("Select a from AreaUsuario a where a.estado = true and a.usuario.id = :id");
			q.setParameter("id", usuario.getId());
			
			lista = (List<AreaUsuario>)q.getResultList();	
			
			if(lista != null && !lista.isEmpty()){
				for(AreaUsuario au : lista){
					nombres += au.getArea().getId() + ",";
				}
			}
			
			if(!nombres.isEmpty()){
				nombres = nombres.substring(0, nombres.length()-1);
			}			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return nombres;
	}
			
	public List<EntityUsuario> listarUsuarioEntityAdministrar(Usuario usuario) throws ServiceException {
		StringBuilder sb = new StringBuilder();
		sb.append("select distinct u.user_id, u.user_name, u.user_creation_date, p.peop_name, null, u.user_status, r.role_name, (select gelo_name from geographical_locations where gelo_id = (select gelo_parent_id from geographical_locations where gelo_id = g.gelo_parent_id) ) as gelo_name ");
		sb.append(" from users AS u LEFT OUTER JOIN suia_iii.roles_users AS ru ON (u.user_id = ru.user_id) and ru.rous_status=true");
		sb.append(" LEFT OUTER JOIN suia_iii.roles AS r ON (r.role_id = ru.role_id)");
		sb.append(" left join people p on u.peop_id = p. peop_id ");		
		sb.append(" INNER JOIN areas_users as au on (u.user_id = au.user_id)");	
		sb.append(" JOIN areas as ar on (ar.area_id=au.area_id)");	
		sb.append(" JOIN geographical_locations as g on (g.gelo_id=p.gelo_id)");
				
		String ids = buscarAreaUsuarioIdSql(usuario);
		
		if(!ids.isEmpty()){
			sb.append(" where ar.area_id in ("+ ids +") order by u.user_id ");
			return ejecutarSentenciasNativas.listarPorSentenciaSql(sb.toString(), EntityUsuario.class, null);
		}else{
			List<EntityUsuario> lista = new ArrayList<EntityUsuario>();
			return lista;
		}		
	}
	
	public List<EntityUsuario> listarUsuarioEntityAdministradorInstitucional(Usuario usuario) throws ServiceException {
		StringBuilder sb = new StringBuilder();
		sb.append("select distinct u.user_id, u.user_name, u.user_creation_date, p.peop_name, null, u.user_status, r.role_name, (select gelo_name from geographical_locations where gelo_id = (select gelo_parent_id from geographical_locations where gelo_id = g.gelo_parent_id) ) as gelo_name ");
		sb.append(" from users AS u LEFT OUTER JOIN suia_iii.roles_users AS ru ON (u.user_id = ru.user_id) and ru.rous_status=true");
		sb.append(" LEFT OUTER JOIN suia_iii.roles AS r ON (r.role_id = ru.role_id)");
		sb.append(" left join people p on u.peop_id = p. peop_id ");		
		sb.append(" INNER JOIN areas_users as au on (u.user_id = au.user_id)");	
		sb.append(" JOIN areas as ar on (ar.area_id=au.area_id)");	
		sb.append(" JOIN geographical_locations as g on (g.gelo_id=p.gelo_id)");
				
		String ids = buscarAreaUsuarioIdSqlAI(usuario);
		
		if(!ids.isEmpty()){
			sb.append(" where ar.area_id in ("+ ids +") and au.arus_status=true order by u.user_id ");
			return ejecutarSentenciasNativas.listarPorSentenciaSql(sb.toString(), EntityUsuario.class, null);
		}else{
			List<EntityUsuario> lista = new ArrayList<EntityUsuario>();
			return lista;
		}		
	}
	
	@SuppressWarnings("unchecked")
	public List<Usuario> buscarUsuarioResponsableArea(Usuario usuario) {
		List<Usuario> usuarios = crudServiceBean
				.getEntityManager()
				.createQuery(
						"SELECT u.usuario FROM AreaUsuario u where u.area.id in :area and u.usuario.esResponsableArea = true")
				.setParameter("area", buscarAreaUsuarioIdSqlInt(usuario)).getResultList();
		return usuarios;
	}
	
	@SuppressWarnings("unchecked")
	public String buscarAreaUsuarioIdSqlAI(Usuario usuario){
		List<AreaUsuario> lista = new ArrayList<AreaUsuario>();
		String nombres = "";
		try {
			
			Query q = crudServiceBean.getEntityManager().createQuery("Select a from AreaUsuario a where a.estado = true and a.usuario.id = :id");
			q.setParameter("id", usuario.getId());
			
			lista = (List<AreaUsuario>)q.getResultList();	
			
			if(lista != null && !lista.isEmpty()){
				for(AreaUsuario au : lista){
					if(au.getArea().getTipoArea().getId().equals(5)){
						//zonales
						Query q1 = crudServiceBean.getEntityManager().createQuery("Select a from Area a where a.estado = true and a.area = :idPadre");
						q1.setParameter("idPadre", au.getArea());
						
						List<Area> lista1 = (List<Area>)q1.getResultList();
						for(Area area1 : lista1){
							nombres += area1.getId() + ",";
						}						
					}else if(au.getArea().getTipoArea().getId().equals(6)){
						//OT
						Query q1 = crudServiceBean.getEntityManager().createQuery("Select a from Area a where a.estado = true and (a.area = :idPadre or a = :idPadre)");
						q1.setParameter("idPadre", au.getArea().getArea());
						
						List<Area> lista1 = (List<Area>)q1.getResultList();
						for(Area area1 : lista1){
							nombres += area1.getId() + ",";
						}						
					}else{
						nombres += au.getArea().getId() + ",";
					}				
				}
			}
			
			if(!nombres.isEmpty()){
				nombres = nombres.substring(0, nombres.length()-1);
			}			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return nombres;
	}
	
	
	@SuppressWarnings("unchecked")
	public List<Area> buscarAreas(Usuario usuario){
		List<Area> lista = new ArrayList<Area>();
		try {
			
			Query q = crudServiceBean.getEntityManager().createQuery("Select a.area from AreaUsuario a where a.estado = true and a.usuario.id = :id and a.area.estado = true ");
			q.setParameter("id", usuario.getId());
			
			lista = (List<Area>)q.getResultList();		
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;
	}
	
	@SuppressWarnings("unchecked")
	public List<Integer> buscarAreaUsuarioIdSqlInt(Usuario usuario){
		List<AreaUsuario> lista = new ArrayList<AreaUsuario>();
		List<Integer> listaIds = new ArrayList<Integer>();		
		try {						
			Query q = crudServiceBean.getEntityManager().createQuery("Select a from AreaUsuario a where a.estado = true and a.usuario.id = :id and a.area.estado = true ");
			q.setParameter("id", usuario.getId());
			
			lista = (List<AreaUsuario>)q.getResultList();	
			
			if(lista != null && !lista.isEmpty()){
				for(AreaUsuario au : lista){
					listaIds.add(au.getArea().getId());
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listaIds;
	}
	
	public List<EntityUsuario> listarUsuarioEntityJefesArea(String role) throws ServiceException {
		StringBuilder sb = new StringBuilder();
		sb.append("select distinct u.user_id, u.user_name, u.user_creation_date, p.peop_name, null, u.user_status, r.role_name, (select gelo_name from geographical_locations where gelo_id = (select gelo_parent_id from geographical_locations where gelo_id = g.gelo_parent_id) ) as gelo_name ");
		sb.append(" from users AS u LEFT OUTER JOIN suia_iii.roles_users AS ru ON (u.user_id = ru.user_id) and ru.rous_status=true");
		sb.append(" LEFT OUTER JOIN suia_iii.roles AS r ON (r.role_id = ru.role_id)");
		sb.append(" left join people p on u.peop_id = p. peop_id ");		
		sb.append(" INNER JOIN areas_users as au on (u.user_id = au.user_id)");	
		sb.append(" JOIN areas as ar on (ar.area_id=au.area_id)");	
		sb.append(" JOIN geographical_locations as g on (g.gelo_id=p.gelo_id)");
				
		sb.append(" where r.role_name ='" + role +"' order by u.user_id ");
		return ejecutarSentenciasNativas.listarPorSentenciaSql(sb.toString(), EntityUsuario.class, null);
			
	}
	
	public List<EntityUsuario> listarUsuariosJefesArea(String role) throws ServiceException {
		StringBuilder sb = new StringBuilder();
		sb.append("select distinct u.user_id, u.user_name, u.user_creation_date, p.peop_name, null, u.user_status, r.role_name, (select gelo_name from geographical_locations where gelo_id = (select gelo_parent_id from geographical_locations where gelo_id = g.gelo_parent_id) ) as gelo_name ");
		sb.append(" from users AS u LEFT OUTER JOIN suia_iii.roles_users AS ru ON (u.user_id = ru.user_id) and ru.rous_status=true");
		sb.append(" LEFT OUTER JOIN suia_iii.roles AS r ON (r.role_id = ru.role_id)");
		sb.append(" left join people p on u.peop_id = p. peop_id ");		
		sb.append(" INNER JOIN areas_users as au on (u.user_id = au.user_id)");	
		sb.append(" JOIN areas as ar on (ar.area_id=au.area_id)");	
		sb.append(" JOIN geographical_locations as g on (g.gelo_id=p.gelo_id)");
				
		sb.append(" where r.role_name ='" + role +"' and u.user_status = true order by u.user_id ");
		return ejecutarSentenciasNativas.listarPorSentenciaSql(sb.toString(), EntityUsuario.class, null);
	}
	
	public List<EntityUsuario> listaUsuariosSnap() throws ServiceException{
		
		StringBuilder sb = new StringBuilder();
		sb.append("select distinct u.user_id, u.user_name, u.user_creation_date, p.peop_name, null, u.user_status, p.peop_pin, ");
		sb.append("(select gelo_name from geographical_locations where gelo_id = (select gelo_parent_id from geographical_locations where gelo_id = g.gelo_parent_id) ) as gelo_name ");
		sb.append(" from coa_mae.province_areas_snap AS s ");		
		sb.append(" INNER JOIN users as u on (u.user_id = s.user_id)");		
		sb.append(" left join people p on u.peop_id = p. peop_id ");		
		sb.append(" INNER JOIN areas_users as au on (u.user_id = au.user_id)");	
		sb.append(" JOIN areas as ar on (ar.area_id=au.area_id)");	
		sb.append(" JOIN geographical_locations as g on (g.gelo_id=p.gelo_id)");
		sb.append(" where s.prar_status = true and u.user_status = true order by u.user_id ");
		return ejecutarSentenciasNativas.listarPorSentenciaSql(sb.toString(), EntityUsuario.class, null);
	}

}
