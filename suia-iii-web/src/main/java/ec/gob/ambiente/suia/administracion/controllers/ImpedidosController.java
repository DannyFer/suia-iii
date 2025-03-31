/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.administracion.controllers;

import ec.gob.ambiente.suia.administracion.bean.ImpedidosBean;
import ec.gob.ambiente.suia.administracion.facade.ImpedidosFacade;
import ec.gob.ambiente.suia.domain.Impedido;
import ec.gob.ambiente.suia.domain.TipoImpedimento;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.utils.JsfUtil;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author christian
 */
@ManagedBean
@ViewScoped
public class ImpedidosController implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -2515823246722417238L;
    @EJB
    private ImpedidosFacade impedidosFacade;
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
            .getLogger(ImpedidosController.class);
    @Getter
    @Setter
    private ImpedidosBean impedidosBean;

    @PostConstruct
    private void cargarDatos() {
        try {
            JsfUtil.validarPagina("impedidos.jsf");
            setImpedidosBean(new ImpedidosBean());
            getImpedidosBean().iniciarDatos();
            getImpedidosBean().setListaImpedidos(impedidosFacade.listarTodo());
        } catch (ServiceException e) {
            LOG.error(e, e);
        }
    }

    public void seleccionarEditar() {
        getImpedidosBean().setApareceTabla(false);
        getImpedidosBean().setSoloLectura(false);
        getImpedidosBean().setIdTipoImpedido(getImpedidosBean().getImpedido().getIdTipoImpedimento().toString());
    }

    public void seleccionarVer() {
        getImpedidosBean().setApareceTabla(false);
        getImpedidosBean().setSoloLectura(true);
        getImpedidosBean().setIdTipoImpedido(getImpedidosBean().getImpedido().getIdTipoImpedimento().toString());
    }

    public void nuevo() {
        getImpedidosBean().setImpedido(new Impedido());
        getImpedidosBean().getImpedido().setEstado(true);
        getImpedidosBean().setApareceTabla(false);
    }

    public void guardar() {
        try {
            getImpedidosBean().getImpedido().setPrtyId(new TipoImpedimento(new Integer(getImpedidosBean().getIdTipoImpedido())));
            impedidosFacade.guardar(getImpedidosBean().getImpedido());
            setImpedidosBean(null);
            cargarDatos();
            JsfUtil.addMessageInfo(JsfUtil.REGISTRO_ACTUALIZADO);
        } catch (ServiceException e) {
            JsfUtil.addMessageError(JsfUtil.ERROR_ACTUALIZAR_REGISTRO + " " + e.getMessage());
            LOG.error(e, e);
        }
    }

    public void cancelar() {
        setImpedidosBean(null);
        cargarDatos();
    }

}
