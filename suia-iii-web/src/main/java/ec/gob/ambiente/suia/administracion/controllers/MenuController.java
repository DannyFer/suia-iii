/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.administracion.controllers;

import ec.gob.ambiente.suia.administracion.bean.MenuBean;
import ec.gob.ambiente.suia.administracion.enums.MenuEnum;
import ec.gob.ambiente.suia.administracion.facade.MenuFacade;
import ec.gob.ambiente.suia.domain.Menu;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.utils.JsfUtil;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

/**
 *
 * @author christian
 */
@ManagedBean
@ViewScoped
public class MenuController implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -444566667L;

    @EJB
    private MenuFacade menuFacade;

    @Getter
    @Setter
    private MenuBean menuBean;
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
            .getLogger(MenuController.class);

    @PostConstruct
    private void cargarDatos() {
        JsfUtil.validarPagina("menu.jsf");
        setMenuBean(new MenuBean());
        iniciarArbol();
    }

    private void crearArbolMenu() {
        try {
            List<Menu> lista = menuFacade.listarTodo();
            if (lista == null || lista.isEmpty()) {
                Menu menu = new Menu();
                menu.setNombre("MENU SUIA");
                menu.setAccion(null);
                menu.setUrl(null);
                menu.setIcono("");
                menu.setNodoFinal(false);
                menu.setNemonico(MenuEnum.MENU_SUIA.getNemonico());
                menu.setOrden(0);
                menu.setEstado(true);
                menu.setMuestraMenu(true);
                menu.setPadreId(null);
                menuFacade.guardar(menu);
            }
        } catch (ServiceException e) {
            LOG.error(e, e);
        }
    }

    private void iniciarArbol() {
        crearArbolMenu();
        getMenuBean().setRoot(new DefaultTreeNode());
        cargarArbol(null, null);
    }

    private void cargarArbol(List<Menu> menus, TreeNode nodo) {
        try {
            if (nodo == null) {
                List<Menu> menusH = menuFacade.listarMenuPadre();
                cargarArbol(menusH, getMenuBean().getRoot());
            } else {
                for (Menu m : menus) {
                    TreeNode nodoH = new DefaultTreeNode(m, nodo);
                    nodoH.setExpanded(true);
                    List<Menu> menusN = menuFacade.listarPorMenuPadre(m);
                    cargarArbol(menusN, nodoH);
                }
            }
        } catch (ServiceException e) {
            LOG.error(e, e);
        }
    }

    public void seleccionarAgregar(Menu menu) {
        getMenuBean().setMenu(menu);
        getMenuBean().setMenuH(new Menu());
        getMenuBean().getMenuH().setEstado(true);
        getMenuBean().getMenuH().setMuestraMenu(true);
        getMenuBean().getMenuH().setUrl("S/N");
        getMenuBean().getMenuH().setIcono("ui-icon-gear");
        getMenuBean().setIdPadre(getMenuBean().getMenu().getId().toString());
    }

    public void seleccionarActualizar(Menu menu) {
        getMenuBean().setMenuH(menu);
        getMenuBean().setIdPadre(getMenuBean().getMenuH().getIdMenuPadre().toString());
    }

    public void guardar() {
        List<String> listaErrores = null;
        try {
            getMenuBean().getMenuH().setNemonico(null);
            listaErrores = getMenuBean().validarDatos();
            if (!listaErrores.isEmpty()) {
                throw new ServiceException();
            }
            getMenuBean().getMenuH().setPadreId(new Menu(new Integer(getMenuBean().getIdPadre())));
            menuFacade.guardar(getMenuBean().getMenuH());
            setMenuBean(null);
            JsfUtil.addMessageInfo(JsfUtil.REGISTRO_ACTUALIZADO);
            cargarDatos();
        } catch (ServiceException e) {
            if (!listaErrores.isEmpty()) {
                JsfUtil.addMessageError(listaErrores);
            } else {
                JsfUtil.addMessageError(JsfUtil.ERROR_ACTUALIZAR_REGISTRO + " " + e.getMessage());
            }
            LOG.error(e, e);
        } catch (RuntimeException e) {
            JsfUtil.addMessageError(JsfUtil.ERROR_ACTUALIZAR_REGISTRO + " " + e.getMessage());
            LOG.error(e, e);
        }
    }
}
