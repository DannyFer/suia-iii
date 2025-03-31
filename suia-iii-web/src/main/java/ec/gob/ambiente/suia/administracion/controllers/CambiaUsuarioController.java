/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.administracion.controllers;

import java.io.Serializable;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.core.datamodel.LazyUserCustomDataModel;
import ec.gob.ambiente.suia.administracion.bean.CambiaUsuarioBean;
import ec.gob.ambiente.suia.domain.AuditoriaSuplantacion;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.dto.EntityUsuario;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.login.controller.LoginController;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;

/**
 *
 * @author christian
 */
@ManagedBean
@ViewScoped
public class CambiaUsuarioController implements Serializable {

    private static final long serialVersionUID = 4938961411435459916L;
    @EJB
    private UsuarioFacade usuarioFacade;
    @Getter
    @Setter
    private CambiaUsuarioBean cambiaUsuarioBean;
    @Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;
    @Setter
    @ManagedProperty(value = "#{loginController}")
    private LoginController loginController;

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
            .getLogger(CambiaUsuarioController.class);

    @PostConstruct
    private void cargarDatos() {
        try {
        	if(JsfUtil.getLoggedUser()!=null && Usuario.isUserInRole(JsfUtil.getLoggedUser(), "admin")){
        		 setCambiaUsuarioBean(new CambiaUsuarioBean());
                 getCambiaUsuarioBean().setListaUsuariosFilterLazy(new LazyUserCustomDataModel());
    		}else {
    			JsfUtil.redirectTo("/start.jsf");
    		}
        } catch (RuntimeException e) {
            LOG.error(e, e);
        }
    }

    public void seleccionar(EntityUsuario usuario) {
        try {
            AuditoriaSuplantacion au = new AuditoriaSuplantacion();
            au.setUsuarioMae(loginBean.getUsuario());
            au.setFechaSuplantacion(new Date());
            getLoginBean().setUsuario(usuarioFacade.buscarUsuarioPorId(new Integer(usuario.getId())));
            au.setUsuarioSuplantado(loginBean.getUsuario());
            getLoginBean().setNombreUsuario(getLoginBean().getUsuario().getNombre());
            getLoginBean().setPassword(loginBean.getUsuario().getPassword());
            JsfUtil.eliminarObjetoSession("bandejaTareasBean", "notificacionesBean", "proyectosBean");
            usuarioFacade.guardarSuplantacion(au);
            loginController.loginSuplantacion(getLoginBean().getUsuario());
        } catch (ServiceException e) {
            LOG.error(e, e);
        } catch (RuntimeException e) {
            LOG.error(e, e);
        }
    }
}
