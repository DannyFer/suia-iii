/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package observaciones;

import ec.gob.ambiente.suia.domain.ObservacionesFormularios;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.observaciones.facade.ObservacionesLecturaFacade;
import ec.gob.ambiente.suia.reasignacion.controllers.AsignacionMasivaController;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author christian
 */
@ManagedBean
@ViewScoped
public class ObservacionesLecturaController implements Serializable {

    private static final long serialVersionUID = -818737854503594856L;
    private static final Logger LOG = Logger.getLogger(AsignacionMasivaController.class);
    @EJB
    private ObservacionesLecturaFacade observacionesLecturaFacade;
    @Getter
    @Setter
    private ObservacionesBean observacionesBB;

    @PostConstruct
    private void cargarDatos() {
        setObservacionesBB(new ObservacionesBean());
    }


    public void agregarObservacion(String idClase, String nombreClase, String seccion) {
        ObservacionesFormularios ob = new ObservacionesFormularios();
        ob.setCampo("");
        ob.setDescripcion("");
        ob.setIdClase(new Integer(idClase));
        ob.setNombreClase(nombreClase);
        ob.setSeccionFormulario(seccion);
        ob.setUsuario(JsfUtil.getLoggedUser());
        ob.setFechaRegistro(new Date());
        ob.setDisabled(false);
        getObservacionesBB().getMapaSecciones().get(seccion).add(ob);
        getObservacionesBB().getListaObservaciones().add(ob);
        reasignarIndice(seccion);
    }

    public void guardar(String seccion) {
        try {
            observacionesLecturaFacade.guardar(observacionesBB.getMapaSecciones().get(seccion),
                    getObservacionesBB().getListaObservaciones());
            JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
        } catch (ServiceException e) {
            JsfUtil.addMessageError(JsfUtil.ERROR_ACTUALIZAR_REGISTRO + " " + e.getMessage());
            LOG.error(e, e);
        } catch (RuntimeException e) {
            JsfUtil.addMessageError(JsfUtil.ERROR_ACTUALIZAR_REGISTRO + " " + e.getMessage());
            LOG.error(e, e);
        }
    }

    public void cargarDatosIniciales(String nombreClase, Integer idClase, String... secciones) {
        if (!getObservacionesBB().getNombreClase().equals(nombreClase) || getObservacionesBB().getListaObservaciones()
                .isEmpty()) {
            cargarObservaciones(nombreClase, idClase, secciones);
        }
    }

    public void cargarDatosInicialesLectura(String nombreClase, Integer idClase, String... secciones) {
        if (getObservacionesBB().getListaObservaciones().isEmpty()) {
            cargarObservaciones(nombreClase, idClase, secciones);
        }
    }

    public void cargarObservaciones(String nombreClase, Integer idClase, String... secciones) {
        try {
            getObservacionesBB().setNombreClase(nombreClase);
            getObservacionesBB().setListaObservaciones(
                    observacionesLecturaFacade.listarPorIdClaseNombreClase(idClase, nombreClase, secciones));
            if (getObservacionesBB().getListaObservaciones() != null && !getObservacionesBB().getListaObservaciones()
                    .isEmpty()) {
                for (ObservacionesFormularios ob : getObservacionesBB().getListaObservaciones()) {
                    if (ob.getIdUsuario().equals(JsfUtil.getLoggedUser().getId())) {
                        ob.setDisabled(false);
                    }
                }
            }
            for (ObservacionesFormularios ob : getObservacionesBB().getListaObservaciones()) {
                List<ObservacionesFormularios> elementos = new ArrayList<>();
                String seccion = ob.getSeccionFormulario();
                if (getObservacionesBB().getMapaSecciones().containsKey(seccion)) {
                    elementos = getObservacionesBB().getMapaSecciones().get(ob.getSeccionFormulario());
                }
                elementos.add(ob);
                getObservacionesBB().getMapaSecciones().put(seccion, elementos);
            }

            for (String s : getObservacionesBB().getMapaSecciones().keySet()) {
                reasignarIndice(s);
            }

              /*  for (String s : secciones) {
                    List<ObservacionesFormularios> lista = new ArrayList<ObservacionesFormularios>();
                    for (ObservacionesFormularios ob : getObservacionesBB().getListaObservaciones()) {
                        if (s.equals(ob.getSeccionFormulario())) {
                            lista.add(ob);
                        }
                    }
                    getObservacionesBB().getMapaSecciones().put(s, lista);
                }
                for (String s : secciones) {
                    reasignarIndice(s);
                } */
        } catch (ServiceException e) {
            LOG.error(e, e);
        } catch (RuntimeException e) {
            LOG.error(e, e);
        }
    }

    public void reasignarIndice(String seccion) {
        if (getObservacionesBB().getMapaSecciones() != null && !getObservacionesBB().getMapaSecciones().isEmpty()) {
            if (getObservacionesBB().getMapaSecciones().get(seccion) != null && !getObservacionesBB().getMapaSecciones()
                    .get(seccion).isEmpty()) {
                int i = 0;
                for (ObservacionesFormularios ob : getObservacionesBB().getMapaSecciones().get(seccion)) {
                    ob.setIndice(i);
                    i++;
                }
            }
        }

    }

    public void eliminar(String seccion, ObservacionesFormularios observacionesFormularios) {
        ObservacionesFormularios obj = getObservacionesBB().getMapaSecciones().get(seccion)
                .get(observacionesFormularios.getIndice());
        if (obj.getId() != null) {
//            getObservacionesBB().getListaObservacionesEliminar().add(obj);
        }
        getObservacionesBB().getMapaSecciones().get(seccion).remove(observacionesFormularios.getIndice());
        reasignarIndice(seccion);
        guardar(seccion);
    }
}
