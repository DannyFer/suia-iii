/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.eia.descripcion.proyecto.controller;

import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.catalogos.facade.CatalogoGeneralFacade;
import ec.gob.ambiente.suia.domain.CatalogoGeneral;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.domain.ProyectoGeneralCatalogo;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.TipoCatalogo;
import ec.gob.ambiente.suia.eia.descripcion.proyecto.bean.MarcoLegalBean;
import ec.gob.ambiente.suia.proyectos.facade.ProyectoGeneralCatalogoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import java.io.Serializable;
import java.util.ArrayList;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author christian
 */
@ManagedBean
@ViewScoped
public class MarcoLegalController implements Serializable {

    private static final long serialVersionUID = -3789057071821232503L;
    @EJB
    private CatalogoGeneralFacade catalogoGeneralFacade;
    @EJB
    private ProyectoGeneralCatalogoFacade proyectoGeneralCatalogoFacade;
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
            .getLogger(MarcoLegalController.class);

    @Getter
    @Setter
    private MarcoLegalBean marcoLegalBean;
    @Getter
    @Setter
    @ManagedProperty(value = "#{proyectosBean}")
    private ProyectosBean proyectosBean;
    private EstudioImpactoAmbiental estudioImpactoAmbiental;

    @PostConstruct
    private void cargarDatos() {
        setMarcoLegalBean(new MarcoLegalBean());
        getMarcoLegalBean().iniciarDatos();
        iniciarMarcoLegal();
    }

    private void iniciarMarcoLegal() {
        try {
            this.estudioImpactoAmbiental = (EstudioImpactoAmbiental) JsfUtil.devolverObjetoSession(Constantes.SESSION_EIA_OBJECT);
            //nombre proyecto MAE-RA-2015-1296
            getMarcoLegalBean().setListaMarcoLegal(catalogoGeneralFacade.obtenerCatalogoXTipo(TipoCatalogo.TIPO_CATALOGO_MARCO_LEGAL));
            getMarcoLegalBean().setListaMarcoLegalTdr(new ArrayList<ProyectoGeneralCatalogo>());
            getMarcoLegalBean().setListaMarcoLegalTdr(proyectoGeneralCatalogoFacade.listarProyectoGeneralCatalogoPorProyecto(this.estudioImpactoAmbiental.getIdProyecto()));
            for (CatalogoGeneral c : getMarcoLegalBean().getListaMarcoLegal()) {
                c.setSeleccionado(seleccionado(c));
            }
        } catch (Exception e) {
            LOG.error(e , e);
        }
    }

    private boolean seleccionado(CatalogoGeneral c) {
        boolean bandera = false;
        for (ProyectoGeneralCatalogo p : getMarcoLegalBean().getListaMarcoLegalTdr()) {
            if (c.getId().equals(p.getIdCatalogo())) {
                bandera = true;
                break;
            }
        }
        return bandera;
    }

    public void guardar() {
        ProyectoGeneralCatalogo p1 = null;
        if (getMarcoLegalBean().getListaMarcoLegalTdr() != null && !getMarcoLegalBean().getListaMarcoLegalTdr().isEmpty()) {
            getMarcoLegalBean().getListaMarcoLegalTdr().get(0);
        }
        try {
            for (CatalogoGeneral c : getMarcoLegalBean().getListaMarcoLegal()) {
                if (c.isSeleccionado() && validaEntrada(c)) {
                    getMarcoLegalBean().getListaMarcoLegalTdr().add(devolverProyecto(c, p1));
                }
            }
            proyectoGeneralCatalogoFacade.saveOrUpdateProyectoGeneralCatalogo(getMarcoLegalBean().getListaMarcoLegalTdr());
            setMarcoLegalBean(null);
            cargarDatos();
            JsfUtil.addMessageInfo(JsfUtil.REGISTRO_ACTUALIZADO);
        } catch (RuntimeException e) {
            LOG.error(e , e);
            JsfUtil.addMessageError(JsfUtil.ERROR_ACTUALIZAR_REGISTRO + " " + e.getMessage());
        }
    }

    private ProyectoGeneralCatalogo devolverProyecto(CatalogoGeneral c, ProyectoGeneralCatalogo p) {
        ProyectoGeneralCatalogo obj = new ProyectoGeneralCatalogo();
        obj.setCatalogoGeneral(c);
        if (p != null) {
            obj.setTdrEiaLicencia(p.getTdrEiaLicencia());
            obj.setProyectoLicenciamientoAmbiental(p.getProyectoLicenciamientoAmbiental());
        } else {
            obj.setProyectoLicenciamientoAmbiental(new ProyectoLicenciamientoAmbiental(this.estudioImpactoAmbiental.getIdProyecto()));
        }
        return obj;
    }

    private boolean validaEntrada(CatalogoGeneral c) {
        boolean bandera = true;
        for (ProyectoGeneralCatalogo p : getMarcoLegalBean().getListaMarcoLegalTdr()) {
            if (c.getId().equals(p.getIdCatalogo())) {
                bandera = false;
                break;
            }
        }
        return bandera;
    }

    public void cancelar() {
        JsfUtil.redirectTo("/pages/eia/marcoLegal/marcoLegal.jsf");
    }
    public void cancelarEia() {
    	JsfUtil.redirectTo("/prevencion/licenciamiento-ambiental/eia/default.jsf");
    }
}
