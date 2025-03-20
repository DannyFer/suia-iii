/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.eia.descripcion.proyecto.controller;

import ec.gob.ambiente.suia.catalogos.facade.CatalogoGeneralFacade;
import ec.gob.ambiente.suia.domain.CatalogoGeneral;
import ec.gob.ambiente.suia.domain.DetalleProyectoDescripcionTrabajoActividad;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.domain.ProyectoDescripcionTrabajoActividad;
import ec.gob.ambiente.suia.domain.TipoCatalogo;
import ec.gob.ambiente.suia.eia.descripcion.proyecto.bean.ProyectoDescripcionTrabajoActividadBean;
import ec.gob.ambiente.suia.eia.descripcion.proyecto.facade.ProyectoDescripcionTrabajoActividadFacade;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.Constantes;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.event.FileUploadEvent;

/**
 *
 * @author christian
 */
@ManagedBean
@ViewScoped
public class ProyectoDescripcionTrabajoActividadController implements Serializable {

    private static final long serialVersionUID = -444566666L;
    @EJB
    private ProyectoDescripcionTrabajoActividadFacade proyectoDescripcionTrabajoActividadFacade;
    @EJB
    private CatalogoGeneralFacade catalogoGeneralFacade;

    @Getter
    @Setter
    private ProyectoDescripcionTrabajoActividadBean proyectoDescripcionTrabajoActividadBean;
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
            .getLogger(ProyectoDescripcionTrabajoActividadController.class);

    @PostConstruct
    private void cargarDatos() {
        setProyectoDescripcionTrabajoActividadBean(new ProyectoDescripcionTrabajoActividadBean());
        getProyectoDescripcionTrabajoActividadBean().iniciarDatos();
        cargarInicioActividades();
    }

    private void cargarInicioActividades() {
        EstudioImpactoAmbiental es = (EstudioImpactoAmbiental) JsfUtil.devolverObjetoSession(Constantes.SESSION_EIA_OBJECT);
        try {
            getProyectoDescripcionTrabajoActividadBean().setListaProyectoDescripcionTrabajoActividad(proyectoDescripcionTrabajoActividadFacade.listarPorEIA(es));
            if (getProyectoDescripcionTrabajoActividadBean().getListaProyectoDescripcionTrabajoActividad() == null || getProyectoDescripcionTrabajoActividadBean().getListaProyectoDescripcionTrabajoActividad().isEmpty()) {
                getProyectoDescripcionTrabajoActividadBean().setListaProyectoDescripcionTrabajoActividad(new ArrayList<ProyectoDescripcionTrabajoActividad>());
                List<CatalogoGeneral> listaCatalogo = catalogoGeneralFacade.obtenerCatalogoXTipo(TipoCatalogo.DESCRIPCION_PROYECTO_OBRA_ACTIVIDAD);
                for (CatalogoGeneral c : listaCatalogo) {
                    ProyectoDescripcionTrabajoActividad obj = new ProyectoDescripcionTrabajoActividad();
                    obj.setCatalogoTipoActividad(c);
                    obj.setEstado(true);
                    obj.setEistId(es);
                    getProyectoDescripcionTrabajoActividadBean().getListaProyectoDescripcionTrabajoActividad().add(obj);
                }
                proyectoDescripcionTrabajoActividadFacade.guardarPrimeraVez(getProyectoDescripcionTrabajoActividadBean().getListaProyectoDescripcionTrabajoActividad());
            }
        } catch (ServiceException e) {
            getProyectoDescripcionTrabajoActividadBean().setListaDetalleProyectoDescripcionTrabajoActividad(null);
            LOG.error(e , e);
        } catch (Exception e) {
            getProyectoDescripcionTrabajoActividadBean().setListaDetalleProyectoDescripcionTrabajoActividad(null);
            LOG.error(e , e);
        }
    }

    public void cargarDetalleActividades(boolean cronograma) {
        try {
            getProyectoDescripcionTrabajoActividadBean().setListaDetalleProyectoDescripcionTrabajoActividad(proyectoDescripcionTrabajoActividadFacade.listarPorProyecto(getProyectoDescripcionTrabajoActividadBean().getProyectoDescripcionTrabajoActividad()));
            if (getProyectoDescripcionTrabajoActividadBean().getListaDetalleProyectoDescripcionTrabajoActividad() == null || getProyectoDescripcionTrabajoActividadBean().getListaDetalleProyectoDescripcionTrabajoActividad().isEmpty()) {
                getProyectoDescripcionTrabajoActividadBean().setListaDetalleProyectoDescripcionTrabajoActividad(new ArrayList<DetalleProyectoDescripcionTrabajoActividad>());
            } else {
                reasignarIndice();
            }
            getProyectoDescripcionTrabajoActividadBean().setApareceCronograma(cronograma);
            getProyectoDescripcionTrabajoActividadBean().getProyectoDescripcionTrabajoActividad().setTieneCronograma(cronograma);
        } catch (ServiceException e) {
            LOG.error(e , e);
        } catch (RuntimeException e) {
            LOG.error(e , e);
        }
    }

    public void aniadirActividad() {
        DetalleProyectoDescripcionTrabajoActividad obj = new DetalleProyectoDescripcionTrabajoActividad();
        obj.setPdwaId(getProyectoDescripcionTrabajoActividadBean().getProyectoDescripcionTrabajoActividad());
        getProyectoDescripcionTrabajoActividadBean().getListaDetalleProyectoDescripcionTrabajoActividad().add(obj);
        reasignarIndice();
    }

    public void eliminarDetalle(DetalleProyectoDescripcionTrabajoActividad detalleProyectoDescripcionTrabajoActividad) {
        if (detalleProyectoDescripcionTrabajoActividad.getId() != null) {
            detalleProyectoDescripcionTrabajoActividad.setEstado(false);
            getProyectoDescripcionTrabajoActividadBean().getListaDetalleProyectoDescripcionTrabajoActividadEliminado().add(detalleProyectoDescripcionTrabajoActividad);
        }
        getProyectoDescripcionTrabajoActividadBean().getListaDetalleProyectoDescripcionTrabajoActividad().remove(detalleProyectoDescripcionTrabajoActividad.getIndice());
        reasignarIndice();
    }

    private void reasignarIndice() {
        int i = 0;
        for (DetalleProyectoDescripcionTrabajoActividad d : getProyectoDescripcionTrabajoActividadBean().getListaDetalleProyectoDescripcionTrabajoActividad()) {
            d.setIndice(i);
            i++;
        }
    }

    public void guardar() {
        try {
            if (!getProyectoDescripcionTrabajoActividadBean().getListaDetalleProyectoDescripcionTrabajoActividad().isEmpty()) {
                List<String> listaMensajes = validarCronograma();
                if (listaMensajes.isEmpty()) {
                    getProyectoDescripcionTrabajoActividadBean().getProyectoDescripcionTrabajoActividad().setDetalleProyectoDescripcionTrabajoActividadCollection(getProyectoDescripcionTrabajoActividadBean().getListaDetalleProyectoDescripcionTrabajoActividad());
                    proyectoDescripcionTrabajoActividadFacade.guardar(getProyectoDescripcionTrabajoActividadBean().getProyectoDescripcionTrabajoActividad(), getProyectoDescripcionTrabajoActividadBean().getListaDetalleProyectoDescripcionTrabajoActividadEliminado());
                    setProyectoDescripcionTrabajoActividadBean(null);
                    cargarDatos();
                    JsfUtil.addMessageInfo(JsfUtil.REGISTRO_ACTUALIZADO);
                } else {
                    JsfUtil.addMessageError(listaMensajes);
                }
            } else {
                JsfUtil.addMessageError("Debe ingresar por lo menos 1 actividad.");
            }
        } catch (ServiceException e) {
            JsfUtil.addMessageError(JsfUtil.ERROR_ACTUALIZAR_REGISTRO + " " + e.getMessage());
            LOG.error(e , e);
        } catch (ParseException e) {
            JsfUtil.addMessageError(JsfUtil.ERROR_ACTUALIZAR_REGISTRO + " " + e.getMessage());
            LOG.error(e , e);
        } catch (RuntimeException e) {
            JsfUtil.addMessageError(JsfUtil.ERROR_ACTUALIZAR_REGISTRO + " " + e.getMessage());
            LOG.error(e , e);
        }
    }

    public void cancelar() {
        JsfUtil.redirectTo("/pages/eia/detalleProyecto/detalleProyecto.jsf");
    }

    public void handleFileUpload(FileUploadEvent event) {
        LOG.info(event.getFile().getFileName() + " is uploaded.");
    }

    private List<String> validarCronograma() throws ParseException {
        List<String> listaMensajes = new ArrayList<String>();
        if (getProyectoDescripcionTrabajoActividadBean().isApareceCronograma()) {
            DateFormat formato = new SimpleDateFormat("MM-yyyy");
            for (DetalleProyectoDescripcionTrabajoActividad d : getProyectoDescripcionTrabajoActividadBean().getListaDetalleProyectoDescripcionTrabajoActividad()) {
                String fechaDesde = formato.format(d.getFechaDesde());
                String fechaHasta = formato.format(d.getFechaHasta());
                if (formato.parse(fechaDesde).getTime() > formato.parse(fechaHasta).getTime()) {
                    listaMensajes.add("La fecha inicio de la actividad " + d.getActividad() + " es mayor a la fecha de finalización.");
                }
            }
        }
        return listaMensajes;
    }
}
