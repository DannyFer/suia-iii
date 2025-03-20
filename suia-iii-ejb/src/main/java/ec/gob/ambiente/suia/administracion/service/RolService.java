/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.administracion.service;

import ec.gob.ambiente.suia.administracion.enums.RolEnum;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Menu;
import ec.gob.ambiente.suia.domain.MenuRoles;
import ec.gob.ambiente.suia.domain.Rol;
import ec.gob.ambiente.suia.domain.RolUsuario;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.utils.EjecutarSentenciasNativas;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 *
 * @author christian
 */
@LocalBean
@Stateless
public class RolService {

    @EJB
    private CrudServiceBean crudServiceBean;
    @EJB
    private EjecutarSentenciasNativas ejecutarSentenciasNativas;

    @SuppressWarnings("unchecked")
    public List<Rol> listarRoles() throws ServiceException {
        List<Rol> listaRol = null;
        try {
            listaRol = (List<Rol>) crudServiceBean.findByNamedQuery(Rol.LISTAR_TODO, null);
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
        return listaRol;
    }

    public Rol guardar(final Rol rol) throws ServiceException {
        try {
            return crudServiceBean.saveOrUpdate(rol);
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }

    public void eliminarMenuRol(final Rol rol) throws ServiceException {
        try {
//            String sql = "DELETE FROM suia_iii.menu_roles WHERE role_id = " + rol.getId();
        	String sql = "update suia_iii.menu_roles set mero_status=false WHERE role_id = " + rol.getId();
            
            ejecutarSentenciasNativas.ejecutarSentenciasNativasDml(sql);
        } catch (SQLException e) {
            throw new ServiceException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public List<MenuRoles> listarPorRol(final Rol rol) throws ServiceException {
        List<MenuRoles> listaRol = null;
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("rol", rol);
            listaRol = (List<MenuRoles>) crudServiceBean.findByNamedQuery("MenuRoles.listarPorRol", params);
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
        return listaRol;
    }
    
    @SuppressWarnings("unchecked")
    public List<MenuRoles> listarPorRolMenu(final Rol rol,final Menu menu) throws ServiceException {
        List<MenuRoles> listaRol = null;
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("rol", rol);
            params.put("menuId", menu);
            
            listaRol = (List<MenuRoles>) crudServiceBean.findByNamedQuery("MenuRoles.listarPorRolMenu", params);
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
        return listaRol;
    }

    @SuppressWarnings("unchecked")
    public List<Rol> listarPorUsuario(Usuario usuario) throws ServiceException {
        List<Rol> listaRol = null;
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("usuario", usuario.getId());
            listaRol = (List<Rol>) crudServiceBean.findByNamedQuery(RolUsuario.FIND_BY_USER, params);
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
        return listaRol;
    }

    public void eliminarRolUsuario(final Usuario usuario) throws ServiceException {
        try {
//            String sql = "DELETE FROM suia_iii.roles_users WHERE user_id = " + usuario.getId();
        	String sql = "update suia_iii.roles_users set rous_status=false where user_id = " + usuario.getId();
            
            
            ejecutarSentenciasNativas.ejecutarSentenciasNativasDml(sql);
        } catch (SQLException e) {
            throw new ServiceException(e);
        }
    }

    public Rol obtenerRolProponente() throws ServiceException {
        Rol rolProponente = null;
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("nombre", RolEnum.SUJETO_CONTROL_PROPONENTE.getNemonico());
            rolProponente = (Rol) crudServiceBean.findByNamedQuerySingleResult(Rol.OBTENER_POR_NOMBRE, params);
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
        return rolProponente;
    }
    
    @SuppressWarnings("unchecked")
    public List<Rol> listarRolesSinAdmin() throws ServiceException {
        List<Rol> listaRol = null;
        try {
            listaRol = (List<Rol>) crudServiceBean.getEntityManager().createQuery("SELECT r FROM Rol r where r.nombre is not 'admin' ORDER BY r.nombre").getResultList();
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
        return listaRol;
    }
    
    @SuppressWarnings("unchecked")
    public List<Rol> listarRolesActivos(Usuario usuario) throws ServiceException {
        List<Rol> listaRol = null;
        try {
            listaRol = (List<Rol>) crudServiceBean.getEntityManager().createQuery("SELECT ru.rol FROM RolUsuario ru WHERE ru.idUsuario = :usuario and ru.estado = true").setParameter("usuario", usuario.getId()).getResultList();
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
        return listaRol;
    }
    
    @SuppressWarnings("unchecked")
    public List<Rol> listarRolesSNAP() throws ServiceException {
        List<Rol> listaRol = null;
        try {
            listaRol = (List<Rol>) crudServiceBean.getEntityManager().createQuery("SELECT r FROM Rol r where nombre in ('ADMINISTRADOR ÁREA PROTEGIDA', 'RESPONSABLE DE ÁREAS PROTEGIDAS') ORDER BY r.nombre").getResultList();
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
        return listaRol;
    }


}
