/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.eia.descripcion.proyecto.controller;

import ec.gob.ambiente.suia.domain.CatalogoGeneral;
import ec.gob.ambiente.suia.domain.DeterminacionAreasInfluenciaProyecto;
import ec.gob.ambiente.suia.domain.EiaOpciones;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.eia.descripcion.proyecto.bean.DeterminacionAreasInfluenciaBean;
import ec.gob.ambiente.suia.eia.descripcion.proyecto.facade.DeterminacionAreasInfluenciaFacade;
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
import org.primefaces.event.FileUploadEvent;

/**
 *
 * @author christian
 */
@ManagedBean
@ViewScoped
public class DeterminacionAreasInfluenciaController implements Serializable {

    private static final long serialVersionUID = -444566665L;

    @EJB
    private DeterminacionAreasInfluenciaFacade determinacionAreasInfluenciaFacade;

    @Getter
    @Setter
    private DeterminacionAreasInfluenciaBean determinacionAreasInfluenciaBean;

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
            .getLogger(DeterminacionAreasInfluenciaController.class);

    @PostConstruct
    private void cargarDatos() {
        setDeterminacionAreasInfluenciaBean(new DeterminacionAreasInfluenciaBean());
        getDeterminacionAreasInfluenciaBean().iniciarDatos();
        cargarActividades();
    }

    public void handleFileUpload(FileUploadEvent event) {
        getDeterminacionAreasInfluenciaBean().getEntityAdjunto().setArchivo(event.getFile().getContents());
        getDeterminacionAreasInfluenciaBean().getEntityAdjunto().setExtension(JsfUtil.devuelveExtension(event.getFile().getFileName()));
        getDeterminacionAreasInfluenciaBean().getEntityAdjunto().setMimeType(event.getFile().getContentType());
        LOG.info(event.getFile().getFileName() + " is uploaded.");
    }

    private void cargarActividades() {
        try {
            getDeterminacionAreasInfluenciaBean().setEstudioImpactoAmbiental((EstudioImpactoAmbiental) JsfUtil.devolverObjetoSession(Constantes.SESSION_EIA_OBJECT));
            List<DeterminacionAreasInfluenciaProyecto> lista = determinacionAreasInfluenciaFacade.listarPorEIATipo(getDeterminacionAreasInfluenciaBean().getEstudioImpactoAmbiental(), DeterminacionAreasInfluenciaProyecto.TIPO_INFLUENCIA_DIRECTA);
            if (lista == null || lista.isEmpty()) {
                getDeterminacionAreasInfluenciaBean().setListaAreasInfluenciaProyectosDirecta(new ArrayList<DeterminacionAreasInfluenciaProyecto>());
                getDeterminacionAreasInfluenciaBean().setListaAreasInfluenciaProyectosInDirecta(new ArrayList<DeterminacionAreasInfluenciaProyecto>());
                getDeterminacionAreasInfluenciaBean().setListaDistanciaElementosProyecto(new ArrayList<DeterminacionAreasInfluenciaProyecto>());
            } else {
                getDeterminacionAreasInfluenciaBean().setListaAreasInfluenciaProyectosDirecta(lista);
                getDeterminacionAreasInfluenciaBean().setListaAreasInfluenciaProyectosInDirecta(determinacionAreasInfluenciaFacade.listarPorEIATipo(getDeterminacionAreasInfluenciaBean().getEstudioImpactoAmbiental(), DeterminacionAreasInfluenciaProyecto.TIPO_INFLUENCIA_INDIRECTA));
                getDeterminacionAreasInfluenciaBean().setListaDistanciaElementosProyecto(determinacionAreasInfluenciaFacade.listarPorEIATipo(getDeterminacionAreasInfluenciaBean().getEstudioImpactoAmbiental(), DeterminacionAreasInfluenciaProyecto.DIFERENCIA_ELEMENTOS_PROYECTO));
            }
            reasignarIndice();
        } catch (ServiceException e) {
            LOG.error(e , e);
        } catch (RuntimeException e) {
            LOG.error(e , e);
        }
    }

    public void aniadirDirecta() {
        DeterminacionAreasInfluenciaProyecto obj = new DeterminacionAreasInfluenciaProyecto();
        obj.setEistId(getDeterminacionAreasInfluenciaBean().getEstudioImpactoAmbiental());
        obj.setTipoInfluecia(DeterminacionAreasInfluenciaProyecto.TIPO_INFLUENCIA_DIRECTA);
        getDeterminacionAreasInfluenciaBean().getListaAreasInfluenciaProyectosDirecta().add(obj);

        reasignarIndice();
    }

    private void reasignarIndice() {
        int i = 0;
        if (getDeterminacionAreasInfluenciaBean().getListaAreasInfluenciaProyectosDirecta() != null) {
            for (DeterminacionAreasInfluenciaProyecto d : getDeterminacionAreasInfluenciaBean().getListaAreasInfluenciaProyectosDirecta()) {
                d.setIndice(i);
                i++;
            }
        }
        i = 0;
        if (getDeterminacionAreasInfluenciaBean().getListaAreasInfluenciaProyectosInDirecta() != null) {
            for (DeterminacionAreasInfluenciaProyecto d : getDeterminacionAreasInfluenciaBean().getListaAreasInfluenciaProyectosInDirecta()) {
                d.setIndice(i);
                i++;
            }
        }
        i = 0;
        if (getDeterminacionAreasInfluenciaBean().getListaDistanciaElementosProyecto() != null) {
            for (DeterminacionAreasInfluenciaProyecto d : getDeterminacionAreasInfluenciaBean().getListaDistanciaElementosProyecto()) {
                d.setIndice(i);
                i++;
            }
        }
    }

    public void generarInfluenciaIndirecta() {
        if (getDeterminacionAreasInfluenciaBean().getListaAreasInfluenciaProyectosDirecta() != null && !getDeterminacionAreasInfluenciaBean().getListaAreasInfluenciaProyectosDirecta().isEmpty()) {
            for (DeterminacionAreasInfluenciaProyecto d : getDeterminacionAreasInfluenciaBean().getListaAreasInfluenciaProyectosDirecta()) {
                int bandera = retornaExistencia(d);
                if (bandera == 0) {
                    DeterminacionAreasInfluenciaProyecto obj = new DeterminacionAreasInfluenciaProyecto();
                    obj.setInfraestructuraActividadesProyecto(d.getInfraestructuraActividadesProyecto());
                    obj.setEistId(d.getEistId());
                    obj.setTipoInfluecia(DeterminacionAreasInfluenciaProyecto.TIPO_INFLUENCIA_INDIRECTA);
                    getDeterminacionAreasInfluenciaBean().getListaAreasInfluenciaProyectosInDirecta().add(obj);
                }
            }
        }
        generarDistanciaElementosProyecto();
    }

    private int retornaExistencia(final DeterminacionAreasInfluenciaProyecto d) {
        int bandera = 0;
        for (DeterminacionAreasInfluenciaProyecto de : getDeterminacionAreasInfluenciaBean().getListaAreasInfluenciaProyectosInDirecta()) {
            if (d.getInfraestructuraActividadesProyecto().equals(de.getInfraestructuraActividadesProyecto())) {
                bandera = 1;
                break;
            }
        }
        return bandera;
    }

    private void generarDistanciaElementosProyecto() {
        if (getDeterminacionAreasInfluenciaBean().getListaAreasInfluenciaProyectosDirecta() != null && !getDeterminacionAreasInfluenciaBean().getListaAreasInfluenciaProyectosDirecta().isEmpty()) {
            for (DeterminacionAreasInfluenciaProyecto d : getDeterminacionAreasInfluenciaBean().getListaAreasInfluenciaProyectosDirecta()) {
                int bandera = retornaExistencia1(d);
                if (bandera == 0) {
                    DeterminacionAreasInfluenciaProyecto obj = new DeterminacionAreasInfluenciaProyecto();
                    obj.setEistId(d.getEistId());
                    obj.setInfraestructuraActividadesProyecto(d.getInfraestructuraActividadesProyecto());
                    obj.setTipoInfluecia(DeterminacionAreasInfluenciaProyecto.DIFERENCIA_ELEMENTOS_PROYECTO);
                    getDeterminacionAreasInfluenciaBean().getListaDistanciaElementosProyecto().add(obj);
                }
            }
        }
    }

    private void procesarElementosProyecto() {
        for (DeterminacionAreasInfluenciaProyecto d : getDeterminacionAreasInfluenciaBean().getListaDistanciaElementosProyecto()) {
            d.setGecaId(new CatalogoGeneral(d.getIdCatalogoElementosSensibles()));
        }
    }

    private int retornaExistencia1(final DeterminacionAreasInfluenciaProyecto d) {
        int bandera = 0;
        for (DeterminacionAreasInfluenciaProyecto de : getDeterminacionAreasInfluenciaBean().getListaDistanciaElementosProyecto()) {
            if (d.getInfraestructuraActividadesProyecto().equals(de.getInfraestructuraActividadesProyecto())) {
                bandera = 1;
                break;
            }
        }
        return bandera;
    }

    public void guardar() {
        try {
            Map<String, EiaOpciones> mapOpciones = (Map<String, EiaOpciones>) JsfUtil.devolverObjetoSession(Constantes.SESSION_OPCIONES_EIA);
            List<String> listaMensajes = validarFormulario();
            if (listaMensajes.isEmpty()) {
                procesarElementosProyecto();
                List<DeterminacionAreasInfluenciaProyecto> lista = new ArrayList<DeterminacionAreasInfluenciaProyecto>();
                lista.addAll(getDeterminacionAreasInfluenciaBean().getListaAreasInfluenciaProyectosDirecta());
                lista.addAll(getDeterminacionAreasInfluenciaBean().getListaAreasInfluenciaProyectosInDirecta());
                lista.addAll(getDeterminacionAreasInfluenciaBean().getListaDistanciaElementosProyecto());
                lista.addAll(getDeterminacionAreasInfluenciaBean().getListaAreasInfluenciaProyectosDirectaEliminar());
                lista.addAll(getDeterminacionAreasInfluenciaBean().getListaAreasInfluenciaProyectosInDirectaEliminar());
                lista.addAll(getDeterminacionAreasInfluenciaBean().getListaDistanciaElementosProyectoEliminar());
                StringBuilder nombreArchivo = new StringBuilder();
                nombreArchivo.append("EIA");
                nombreArchivo.append("Medio socio economico cultural 0");
                nombreArchivo.append(getDeterminacionAreasInfluenciaBean().getEstudioImpactoAmbiental().getId());
                nombreArchivo.append(".").append(getDeterminacionAreasInfluenciaBean().getEntityAdjunto().getExtension());
                getDeterminacionAreasInfluenciaBean().getEntityAdjunto().setNombre(nombreArchivo.toString());
                determinacionAreasInfluenciaFacade.guardarConAdjunto(lista, getDeterminacionAreasInfluenciaBean().getEstudioImpactoAmbiental(), getDeterminacionAreasInfluenciaBean().getEntityAdjunto(), mapOpciones.get(EiaOpciones.MEDIO_SOCIO_ECONOMICO_CULTURAL_HIDRO));
                setDeterminacionAreasInfluenciaBean(null);
                JsfUtil.addMessageInfo(JsfUtil.REGISTRO_ACTUALIZADO);
                cargarDatos();
            } else {
                JsfUtil.addMessageError(listaMensajes);
            }
        } catch (ServiceException e) {
            JsfUtil.addMessageError(JsfUtil.ERROR_ACTUALIZAR_REGISTRO + " " + e.getMessage());
            LOG.error(e , e);
        } catch (RuntimeException e) {
            JsfUtil.addMessageError(JsfUtil.ERROR_ACTUALIZAR_REGISTRO + " " + e.getMessage());
            LOG.error(e , e);
        }
    }

    public void cancelar() {
        JsfUtil.redirectTo("/pages/eia/determinacionAreaInfluencia/determinacionAreaInfluencia.jsf");
    }

    private List<String> validarFormulario() {
        List<String> listaMensajes = new ArrayList<String>();
        int tamanio = getDeterminacionAreasInfluenciaBean().getListaAreasInfluenciaProyectosDirecta().size();
        int tamanio1 = getDeterminacionAreasInfluenciaBean().getListaAreasInfluenciaProyectosInDirecta().size();
        int tamanio2 = getDeterminacionAreasInfluenciaBean().getListaDistanciaElementosProyecto().size();
        if (tamanio == 0) {
            listaMensajes.add("Debe ingresar por lo menos una área de influencia directa.");
        }

        if (tamanio != tamanio1) {
            listaMensajes.add("El número de registros de la sección de área de influencia directa debe ser igual a área de influencia indirecta.");
        }
        if (tamanio != tamanio2) {
            listaMensajes.add("El número de registros de la sección de área de influencia directa debe ser igual a distancias entre los elementos del proyecto y los elementos sensibles del medio social.");
        }
        if (getDeterminacionAreasInfluenciaBean().getEntityAdjunto().getArchivo() == null) {
            listaMensajes.add("Debe seleccionar un archivo.");
        }
        return listaMensajes;
    }

    public void eliminarDetalle(DeterminacionAreasInfluenciaProyecto determinacionAreasInfluenciaProyecto) {
        if (determinacionAreasInfluenciaProyecto.getId() != null) {
            determinacionAreasInfluenciaProyecto.setEstado(false);
            DeterminacionAreasInfluenciaProyecto obj = getDeterminacionAreasInfluenciaBean().getListaAreasInfluenciaProyectosInDirecta().get(determinacionAreasInfluenciaProyecto.getIndice());
            obj.setEstado(false);
            DeterminacionAreasInfluenciaProyecto obj1 = getDeterminacionAreasInfluenciaBean().getListaDistanciaElementosProyecto().get(determinacionAreasInfluenciaProyecto.getIndice());
            obj1.setEstado(false);
            getDeterminacionAreasInfluenciaBean().getListaAreasInfluenciaProyectosDirectaEliminar().add(determinacionAreasInfluenciaProyecto);
            getDeterminacionAreasInfluenciaBean().getListaAreasInfluenciaProyectosInDirectaEliminar().add(obj);
            getDeterminacionAreasInfluenciaBean().getListaDistanciaElementosProyectoEliminar().add(obj1);
        }
        getDeterminacionAreasInfluenciaBean().getListaAreasInfluenciaProyectosDirecta().remove(determinacionAreasInfluenciaProyecto.getIndice());
        getDeterminacionAreasInfluenciaBean().getListaAreasInfluenciaProyectosInDirecta().remove(determinacionAreasInfluenciaProyecto.getIndice());
        getDeterminacionAreasInfluenciaBean().getListaDistanciaElementosProyecto().remove(determinacionAreasInfluenciaProyecto.getIndice());
        reasignarIndice();
    }

}
