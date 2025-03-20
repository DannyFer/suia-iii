/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.eia.descripcion.proyecto.controller;

import ec.gob.ambiente.suia.domain.EiaOpciones;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.domain.IdentificacionSitiosContaminadosEia;
import ec.gob.ambiente.suia.eia.descripcion.proyecto.bean.IdentificacionSitiosContaminadosEiaBean;
import ec.gob.ambiente.suia.eia.descripcion.proyecto.facade.IdentificacionSitiosContaminadosEiaFacade;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;

/**
 *
 * @author christian
 */
@ManagedBean
@ViewScoped
public class IdentificacionSitiosContaminadosEiaController implements Serializable {

    private static final long serialVersionUID = -1328225072072219839L;

    @EJB
    private IdentificacionSitiosContaminadosEiaFacade identificacionSitiosContaminadosEiaFacade;

    @Getter
    @Setter
    private IdentificacionSitiosContaminadosEiaBean identificacionSitiosContaminadosEiaBean;
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
            .getLogger(IdentificacionSitiosContaminadosEiaController.class);

    @PostConstruct
    private void cargarDatos() {
        setIdentificacionSitiosContaminadosEiaBean(new IdentificacionSitiosContaminadosEiaBean());
        getIdentificacionSitiosContaminadosEiaBean().iniciarDatos();
        getIdentificacionSitiosContaminadosEiaBean().setEstudioImpactoAmbiental((EstudioImpactoAmbiental) JsfUtil.devolverObjetoSession(Constantes.SESSION_EIA_OBJECT));
        iniciarSitios();
    }

    private void iniciarSitios() {
        try {
            getIdentificacionSitiosContaminadosEiaBean().setListaIdentificacionSitiosContaminadosEia(identificacionSitiosContaminadosEiaFacade.listarPorEia(getIdentificacionSitiosContaminadosEiaBean().getEstudioImpactoAmbiental()));
            if (getIdentificacionSitiosContaminadosEiaBean().getListaIdentificacionSitiosContaminadosEia() == null || getIdentificacionSitiosContaminadosEiaBean().getListaIdentificacionSitiosContaminadosEia().isEmpty()) {
                getIdentificacionSitiosContaminadosEiaBean().setListaIdentificacionSitiosContaminadosEia(new ArrayList<IdentificacionSitiosContaminadosEia>());
            }
            reasignarIndice();
        } catch (ServiceException e) {
            LOG.error(e , e);
        } catch (RuntimeException e) {
            LOG.error(e , e);
        }
    }

    public void handleFileUpload(FileUploadEvent event) {
        getIdentificacionSitiosContaminadosEiaBean().getEntityAdjunto().setArchivo(event.getFile().getContents());
        getIdentificacionSitiosContaminadosEiaBean().getEntityAdjunto().setExtension(JsfUtil.devuelveExtension(event.getFile().getFileName()));
        getIdentificacionSitiosContaminadosEiaBean().getEntityAdjunto().setMimeType(event.getFile().getContentType());
        LOG.info(event.getFile().getFileName() + " is uploaded.");
    }

    public void nuevoSitio() {
        getIdentificacionSitiosContaminadosEiaBean().setIdentificacionSitiosContaminadosEia(new IdentificacionSitiosContaminadosEia());
        getIdentificacionSitiosContaminadosEiaBean().getIdentificacionSitiosContaminadosEia().setEistId(getIdentificacionSitiosContaminadosEiaBean().getEstudioImpactoAmbiental());
        getIdentificacionSitiosContaminadosEiaBean().getIdentificacionSitiosContaminadosEia().setEstado(true);
    }

    public void agregarSitio() {
        if (!getIdentificacionSitiosContaminadosEiaBean().getIdentificacionSitiosContaminadosEia().isEditar()) {
            getIdentificacionSitiosContaminadosEiaBean().getListaIdentificacionSitiosContaminadosEia().add(getIdentificacionSitiosContaminadosEiaBean().getIdentificacionSitiosContaminadosEia());
        }
        getIdentificacionSitiosContaminadosEiaBean().setIdentificacionSitiosContaminadosEia(null);
        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("sitiosContaminados", true);
        reasignarIndice();
    }

    public void eliminarDetalle(IdentificacionSitiosContaminadosEia identificacionSitiosContaminadosEia) {
        if (identificacionSitiosContaminadosEia.getId() != null) {
            identificacionSitiosContaminadosEia.setEstado(false);
            getIdentificacionSitiosContaminadosEiaBean().getListaIdentificacionSitiosContaminadosEiaEliminar().add(identificacionSitiosContaminadosEia);
            getIdentificacionSitiosContaminadosEiaBean().getListaIdentificacionSitiosContaminadosEia().remove(identificacionSitiosContaminadosEia.getIndice());
        } else {
            getIdentificacionSitiosContaminadosEiaBean().getListaIdentificacionSitiosContaminadosEia().remove(identificacionSitiosContaminadosEia.getIndice());
        }
    }

    public void editarDetalle(IdentificacionSitiosContaminadosEia identificacionSitiosContaminadosEia) {
        identificacionSitiosContaminadosEia.setEditar(true);
        getIdentificacionSitiosContaminadosEiaBean().setIdentificacionSitiosContaminadosEia(identificacionSitiosContaminadosEia);
    }

    private void reasignarIndice() {
        int i = 0;
        for (IdentificacionSitiosContaminadosEia eia : getIdentificacionSitiosContaminadosEiaBean().getListaIdentificacionSitiosContaminadosEia()) {
            eia.setIndice(i);
            i++;
        }
    }

    public void guardar() {
        try {
            getIdentificacionSitiosContaminadosEiaBean().validarFormulario();
            Map<String, EiaOpciones> mapOpciones = (Map<String, EiaOpciones>) JsfUtil.devolverObjetoSession(Constantes.SESSION_OPCIONES_EIA);
            List<IdentificacionSitiosContaminadosEia> listaGuardar = new ArrayList<IdentificacionSitiosContaminadosEia>();
            listaGuardar.addAll(getIdentificacionSitiosContaminadosEiaBean().getListaIdentificacionSitiosContaminadosEia());
            listaGuardar.addAll(getIdentificacionSitiosContaminadosEiaBean().getListaIdentificacionSitiosContaminadosEiaEliminar());
            StringBuilder nombreArchivo = new StringBuilder();
            nombreArchivo.append("EIA");
            nombreArchivo.append("IdentificacionSitiosContaminados 0");
            nombreArchivo.append(getIdentificacionSitiosContaminadosEiaBean().getEstudioImpactoAmbiental().getId());
            nombreArchivo.append(".").append(getIdentificacionSitiosContaminadosEiaBean().getEntityAdjunto().getExtension());
            getIdentificacionSitiosContaminadosEiaBean().getEntityAdjunto().setNombre(nombreArchivo.toString());
            identificacionSitiosContaminadosEiaFacade.guardarConAdjunto(listaGuardar, getIdentificacionSitiosContaminadosEiaBean().getEstudioImpactoAmbiental(), getIdentificacionSitiosContaminadosEiaBean().getEntityAdjunto(), mapOpciones.get(EiaOpciones.IDENTIFICACION_SITIOS_FUENTES_CONTAMINADOS_HIDRO));
            setIdentificacionSitiosContaminadosEiaBean(null);
            cargarDatos();
            JsfUtil.addMessageInfo(JsfUtil.REGISTRO_ACTUALIZADO);
        } catch (ServiceException e) {
            JsfUtil.addMessageError(JsfUtil.ERROR_ACTUALIZAR_REGISTRO + " " + e.getMessage());
            LOG.error(e , e);
        } catch (RuntimeException e) {
            JsfUtil.addMessageError(JsfUtil.ERROR_ACTUALIZAR_REGISTRO + " " + e.getMessage());
            LOG.error(e , e);
        }
    }

    public void cancelar() {
        JsfUtil.redirectTo("/pages/eia/identificacionSitiosContaminados/identificacionSitiosContaminados.jsf");
    }
}
