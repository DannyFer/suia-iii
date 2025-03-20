/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.administracion.facade;

import ec.gob.ambiente.suia.administracion.service.MenuService;
import ec.gob.ambiente.suia.domain.Menu;
import ec.gob.ambiente.suia.domain.RolUsuario;
import ec.gob.ambiente.suia.dto.EntityMenu;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.utils.EntityMenuComparator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 *
 * @author christian
 */
@LocalBean
@Stateless
public class MenuFacade {

    @EJB
    private MenuService menuService;

    public void guardar(final Menu menu) throws ServiceException {
        menuService.guardar(menu);
    }

    public List<Menu> listarTodo() throws ServiceException {
        return menuService.listarTodo();
    }

    public List<Menu> listarMenuPadre() throws ServiceException {
        return menuService.listarMenuPadre();
    }

    public List<Menu> listarPorMenuPadre(final Menu menu) throws ServiceException {
        return menuService.listarPorMenuPadre(menu);
    }

    public List<Menu> listarNodoFinalFalse() throws ServiceException {
        return menuService.listarNodoFinalFalse();
    }

    public List<EntityMenu> listarPorUsuarioRol(final List<RolUsuario> listaUsuarioRol) throws ServiceException {
        List<EntityMenu> listaDatos = new ArrayList<EntityMenu>();
        Long idUsuario = null;
        Long padre = null;
        List<Long> listaRoles = new ArrayList<Long>();
        for (RolUsuario u : listaUsuarioRol) {
            idUsuario = 0L;
            listaRoles.add(u.getIdRol().longValue());
        }
        List<Menu> listaAcceso = menuService.listarPorRolMuestra(listaRoles);
        for (Menu ac : listaAcceso) {
            padre = ac.getIdMenuPadre();
            listaDatos.add(new EntityMenu(ac.getId().longValue(), ac.getNombre(), ac.getAccion(), ac.getUrl(), padre, idUsuario, ac.isNodoFinal(), ac.getOrden(), ac.getIcono()));
        }
        Set<EntityMenu> listaNoRepetidos = new HashSet<EntityMenu>(listaDatos);
        List<EntityMenu> listaDatosRetorno = new ArrayList<EntityMenu>(listaNoRepetidos);
        Collections.sort(listaDatosRetorno, new EntityMenuComparator());
        return listaDatosRetorno;
    }

    public List<EntityMenu> listarPorUsuarioRolSoloNodosFinales(final List<RolUsuario> listaUsuarioRol) throws ServiceException {
        List<EntityMenu> listaDatos = new ArrayList<EntityMenu>();
        Long idUsuario = null;
        Long padre = null;
        List<Long> listaRoles = new ArrayList<Long>();
        for (RolUsuario u : listaUsuarioRol) {
            idUsuario = 0L;
            listaRoles.add(u.getIdRol().longValue());
        }
        List<Menu> listaAcceso = menuService.listarPorRolNoMuestra(listaRoles);
        for (Menu ac : listaAcceso) {
            padre = ac.getIdMenuPadre();
            listaDatos.add(new EntityMenu(ac.getId().longValue(), ac.getNombre(), ac.getAccion(), ac.getUrl(), padre, idUsuario, ac.isNodoFinal(), ac.getOrden(), ac.getIcono()));
        }
        Set<EntityMenu> listaNoRepetidos = new HashSet<EntityMenu>(listaDatos);
        List<EntityMenu> listaDatosRetorno = new ArrayList<EntityMenu>(listaNoRepetidos);
        Collections.sort(listaDatosRetorno, new EntityMenuComparator());
        return listaDatosRetorno;
    }

    public Menu obtenerPorNemonico(final String nemonico) {
        return menuService.obtenerPorNemonico(nemonico);
    }
}
