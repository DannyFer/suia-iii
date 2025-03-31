/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.administracion.service;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Menu;
import ec.gob.ambiente.suia.exceptions.ServiceException;
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
public class MenuService {

    @EJB
    private CrudServiceBean crudServiceBean;

    @SuppressWarnings("unchecked")
    public void guardar(final Menu menu) throws ServiceException {
        try {
            crudServiceBean.saveOrUpdate(menu);
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public List<Menu> listarTodo() throws ServiceException {
        List<Menu> lista = null;
        try {
            lista = (List<Menu>) crudServiceBean.findByNamedQuery("Menu.findAll", null);
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
        return lista;
    }

    @SuppressWarnings("unchecked")
    public List<Menu> listarNodoFinalFalse() throws ServiceException {
        List<Menu> lista = null;
        try {
            lista = (List<Menu>) crudServiceBean.findByNamedQuery("Menu.listarNodoFinalFalse", null);
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
        return lista;
    }

    @SuppressWarnings("unchecked")
    public List<Menu> listarMenuPadre() throws ServiceException {
        List<Menu> lista = null;
        try {
            lista = (List<Menu>) crudServiceBean.findByNamedQuery("Menu.listarMenu", null);
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
        return lista;
    }

    @SuppressWarnings("unchecked")
    public List<Menu> listarPorMenuPadre(final Menu menu) throws ServiceException {
        List<Menu> lista = null;
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("menu", menu);
            lista = (List<Menu>) crudServiceBean.findByNamedQuery("Menu.listarPorMenuPadre", params);
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
        return lista;
    }

    @SuppressWarnings("unchecked")
    public List<Menu> listarPorRolMuestra(List<Long> listaRol) throws ServiceException {
        List<Menu> lista = null;
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("roles", listaRol.isEmpty() ? null : listaRol);
            lista = (List<Menu>) crudServiceBean.findByNamedQuery("Menu.listarPorRolMuestra", params);
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
        return lista;
    }

    @SuppressWarnings("unchecked")
    public List<Menu> listarPorRolNoMuestra(List<Long> listaRol) throws ServiceException {
        List<Menu> lista = null;
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("roles", listaRol.isEmpty() ? null : listaRol);
            lista = (List<Menu>) crudServiceBean.findByNamedQuery("Menu.listarPorRolNoMuestra", params);
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
        return lista;
    }

    @SuppressWarnings("unchecked")
    public Menu obtenerPorNemonico(final String nemonico) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("nemonico", nemonico);
        return (Menu) crudServiceBean.findByNamedQuerySingleResult("Menu.obtenerPorNemonico", params);
    }

}
