package ec.gob.ambiente.control.aprobacionrequisitostecnicos.controllers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

import ec.gob.ambiente.control.aprobacionrequisitostecnicos.bean.AlmacenTemporalBean;
import ec.gob.ambiente.control.aprobacionrequisitostecnicos.bean.AprobacionRequisitosTecnicosBean;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.AlmacenFacade;
import ec.gob.ambiente.suia.domain.TipoEnvase;
import ec.gob.ambiente.suia.domain.TipoIluminacion;
import ec.gob.ambiente.suia.domain.TipoLocal;
import ec.gob.ambiente.suia.domain.TipoMaterialConstruccion;
import ec.gob.ambiente.suia.domain.TipoVentilacion;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.Almacen;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.AlmacenRecepcion;
import ec.gob.ambiente.suia.dto.EntityRecepcionDesecho;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.validacionseccion.facade.ValidacionSeccionesFacade;


@ViewScoped
@ManagedBean
public class AlmacenamientoTemporalController implements Serializable {

    private static final Logger LOG = Logger.getLogger(AlmacenamientoTemporalController.class);
    private static final long serialVersionUID = 7384572568264083243L;
    //private static final String REQUERIDO_POST = "' es requerido.";
    //private static final String REQUERIDO_PRE = "El campo '";
    private static final Double VALOR_CERO = 0.0;
    private static final String ESTADO_LIQUIDO = "Líquido";
    private static final String ESTADO_SEMISOLIDO = "Semisólido";
    public static final String[] TIPOS_ELIMINACION_OTROS = new String[] { "OMT1", "R2", "RE3", "RE4", "RM7", "RS5",
            "CP4", "TDB4", "DF3" };

    @EJB
    private AlmacenFacade almacenFacade;
    @Getter
    @Setter
    private AlmacenTemporalBean almacenTemporalBean;

    @EJB
    private ValidacionSeccionesFacade validacionSeccionesFacade;

    @Getter
    @Setter
    private List<TipoLocal> locales;

    @Getter
    @Setter
    private List<TipoVentilacion> ventilaciones;

    @Getter
    @Setter
    private List<TipoMaterialConstruccion> materiales;

    @Getter
    @Setter
    private List<TipoIluminacion> iluminaciones;

    @Getter
    @Setter
    private List<TipoEnvase> tipoEnvases;

    @Setter
    @Getter
    @ManagedProperty(value = "#{aprobacionRequisitosTecnicosBean}")
    private AprobacionRequisitosTecnicosBean aprobacionRequisitosTecnicosBean;

    /**
     *
     */
    @PostConstruct
    public void inicio() {

        almacenTemporalBean = new AlmacenTemporalBean();
        this.locales = new ArrayList<TipoLocal>();
        this.materiales = new ArrayList<TipoMaterialConstruccion>();
        this.ventilaciones = new ArrayList<TipoVentilacion>();
        this.iluminaciones = new ArrayList<TipoIluminacion>();
        this.tipoEnvases = new ArrayList<TipoEnvase>();


        almacenTemporalBean.setAlmacen(new Almacen());
        almacenTemporalBean.setListaAlmacenes(new ArrayList<Almacen>());
        almacenTemporalBean.setListaEntidadesRemover(new ArrayList());
        almacenTemporalBean.setListaEntityRecepcionDesecho(new ArrayList());



        //almacenTemporalBean.setMostrarFosasRetencion(false);

        try {
            almacenTemporalBean.getAlmacen().setAprobacionRequisitosTecnicos(aprobacionRequisitosTecnicosBean
                    .getAprobacionRequisitosTecnicos());
            almacenTemporalBean.setAprobacionRequisitosTecnicos(aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos());
            if (existeIngresoRecepcion()) {
                obtenerCaracteristicas();
                //iniciarDatos();
                aprobacionRequisitosTecnicosBean.verART(Almacen.class.getName());
            } else {
                RequestContext context = RequestContext.getCurrentInstance();
                context.execute("PF('importanteWdgt').show();");
            }

        } catch (Exception e) {
            LOG.error(e, e);
            JsfUtil.addMessageError("Error al cargar la aprobacion");
        }

        obtenerListaAlmacenes();


    }

    private void obtenerListaAlmacenes() {
        try {
            List<Almacen> listaAlmacen = almacenFacade.listarAlmacenesPorAprobacionRequistos(almacenTemporalBean
                    .getAprobacionRequisitosTecnicos().getId());
            if (listaAlmacen != null && !listaAlmacen.isEmpty()) {
                almacenTemporalBean.setListaAlmacenes(listaAlmacen);
                int indice = 0;
                for (Almacen alm : almacenTemporalBean.getListaAlmacenes()) {
                    alm.setIndice(indice);
                    cargarDetalleAlmacen(alm.getAlmacenRecepciones());
                    indice++;
                }
            }
        } catch (Exception e) {
            LOG.error("Error al obtener los almacenes.", e);
            JsfUtil.addMessageError("No se puede obtener los almacenes.");
        }
    }

    private void cargarDetalleAlmacen(List<AlmacenRecepcion> listaAlmacenRecepcion) {
        int indice = 0;
        for (AlmacenRecepcion almRecp : listaAlmacenRecepcion) {
            almRecp.setIndice(indice);
            for (EntityRecepcionDesecho e : almacenTemporalBean.getListaEntityRecepcionDesecho()) {
                if (almRecp.getIdRecepcionDesechoPeligroso().intValue() == e.getIdRecepcion()) {
                    almRecp.setEntityRecepcionDesecho(e);
                    break;
                }
            }
            indice++;
        }
    }

    /*private void recuperarCuerpoHidrico() {
        try {
            almacenTemporalBean.setListaAlmacenes(Facade.cuerpoHidricoXEiaId(cuerpoHidricoBean.getEstudioImpactoAmbiental()));
        } catch (Exception e) {
            LOG.error(e, e);
        }
    }*/

    private boolean existeIngresoRecepcion() {
        boolean existe = true;
        obtenerListaDesechos();
        if (almacenTemporalBean.getListaEntityRecepcionDesecho() == null
                || almacenTemporalBean.getListaEntityRecepcionDesecho().isEmpty()) {
            existe = false;
        }
        return existe;
    }

    private void obtenerListaDesechos() {
        try {
            almacenTemporalBean.setListaEntityRecepcionDesecho(almacenFacade.obtenerPorAprobacionRequisitosTecnicos(almacenTemporalBean.getAprobacionRequisitosTecnicos().getId()));
        } catch (Exception e) {
            LOG.error("Error al obtener los desechos.", e);
            JsfUtil.addMessageError("No se puede obtener los desechos.");
        }
    }

    private void obtenerCaracteristicas() {
        try {
            this.locales = almacenFacade.getLocales();
            this.materiales = almacenFacade.getMateriales();
            this.ventilaciones = almacenFacade.getVentilacion();
            this.iluminaciones = almacenFacade.getIluminacion();
            this.tipoEnvases = almacenFacade.getTipoEnvase();
        } catch (Exception e) {
            LOG.error("Error al obtener las caracteristicas.", e);
            JsfUtil.addMessageError("No se puede obtener los caracteristicas.");
        }
    }

    /*private void inicializarUsosSeleccionados() {

        int i = 0;
        CatalogoUso[] seleccionados = new CatalogoUso[listaCatalogoUso.size()];
        for (CatalogoUso catalogoUso : listaCatalogoUso) {
            boolean seleccionado = false;
            for (UsoCuerpoHidrico usoCuerpoHidrico : cuerpoHidricoBean.getCuerpoHidrico().getUsosCuerposHidricos()) {
                if (catalogoUso.getId().equals(usoCuerpoHidrico.getCatalogoUso().getId())) {
                    seleccionados[i++] = catalogoUso;
                    seleccionado = true;
                    break;
                }
            }
            if (!seleccionado)
                seleccionados[i++] = null;
        }
        cuerpoHidricoBean.setListaUsos(seleccionados);
    }*/

    public void agregarAlmacen() {
        RequestContext context = RequestContext.getCurrentInstance();
        almacenTemporalBean.setAlmacen(new Almacen());
        almacenTemporalBean.getAlmacen().setAlmacenRecepciones(new ArrayList<AlmacenRecepcion>());
        almacenTemporalBean.setMostrarFosasRetencion(false);
        /*almacenTemporalBean.getAlmacen().setCantidad(
                almacenTemporalBean.getAlmacen().getAltura()
                        * almacenTemporalBean.getAlmacen().getAncho()
                        * almacenTemporalBean.getAlmacen().getLargo());*/

        //if (validarGuardarAlmacen()) {


            /*if (almacenTemporalBean.getAlmacen().isEditar()) {
                if (!almacenTemporalBean.getAlmacen().equals(almacenTemporalBean.getAlmacenAux())
                        || almacenTemporalBean.getAlmacen().isModificado() != almacenTemporalBean
                        .getAlmacenAux().isModificado()
                        || almacenTemporalBean.getAlmacenAux().getAlmacenRecepciones().size() < almacenTemporalBean
                        .getAlmacen().getAlmacenRecepciones().size()) {

                    if (existeModificados()) {
                        almacenTemporalBean.getAlmacenesModificacion().set(
                                almacenTemporalBean.getIndiceModificadoExiste(),
                                almacenTemporalBean.getAlmacen());
                    } else {
                        almacenTemporalBean.getAlmacenesModificacion().add(
                                almacenTemporalBean.getAlmacen());
                    }
                }
            } else {
                almacenTemporalBean.getAlmacenesModificacion().add(almacenTemporalBean.getAlmacen());
                almacenTemporalBean.getAlmacenes().add(almacenTemporalBean.getAlmacen());
                ponerIndiceAlmacen();
            }*/
            //context.addCallbackParam("almacenIn", true);
        //} else {
            //context.addCallbackParam("almacenIn", false);
        //}

        /*cuerpoHidricoBean.getCuerpoHidrico().setUsosCuerposHidricos(new ArrayList<UsoCuerpoHidrico>());
        cuerpoHidricoBean.getCuerpoHidrico().setEstudioImpactoAmbiental(cuerpoHidricoBean.getEstudioImpactoAmbiental());
        inicializarUsosSeleccionados();*/
    }

    public void seleccionarAlmacen(Almacen almacen) {
        almacen.setEditar(true);
        almacenTemporalBean.setAlmacen(almacen);
        validarFosasRetencion(almacen.getAlmacenRecepciones());
        //inicializarUsosSeleccionados();
    }

    public void removerAlmacen(Almacen almacen) {
        try {
                almacenTemporalBean.getListaAlmacenes().remove(almacen);
                if (almacen.getId() != null) {
                    almacen.setEstado(false);
                    almacenTemporalBean.getListaEntidadesRemover().add(almacen);
                }
        } catch (Exception e) {
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
            LOG.error(e, e);
        }
    }

    public void agregarDesecho() {

        almacenTemporalBean.setAlmacenRecepcion(new AlmacenRecepcion());
        //almacenTemporalBean.getAlmacenRecepcion().setEntityRecepcionDesecho(new EntityRecepcionDesecho());


    }

    public void seleccionarAlmacenRecepcion(AlmacenRecepcion almacenRecepcion) {
        almacenRecepcion.setEditar(true);
        almacenTemporalBean.setAlmacenRecepcion(almacenRecepcion);
    }

    public void eliminarAlmacenRecepcion(AlmacenRecepcion almacenRecepcion) {
        try {
            almacenTemporalBean.getAlmacen().getAlmacenRecepciones().remove(almacenRecepcion);
            if (almacenRecepcion.getId() != null) {
                almacenRecepcion.setEstado(false);
                almacenTemporalBean.getListaEntidadesRemover().add(almacenRecepcion);
            }

        } catch (Exception e) {
            JsfUtil.addMessageError(e.getMessage());
            LOG.error(e, e);
        }
        validarFosasRetencion(almacenTemporalBean.getAlmacen().getAlmacenRecepciones());
        if (!almacenTemporalBean.isMostrarFosasRetencion()) {
            almacenTemporalBean.getAlmacen().setCapacidadFosas(null);
        }
    }

    /*public boolean validarGuardarAlmacen() {
        List<String> listaMensajes = new ArrayList<String>();
        if (almacenamientoTemporalBean.getAlmacen().getIdentificacion() == null
                || almacenamientoTemporalBean.getAlmacen().getIdentificacion().isEmpty()) {
            listaMensajes.add(REQUERIDO_PRE + "Identificación de Almacén" + REQUERIDO_POST);
        }
        if (almacenamientoTemporalBean.getAlmacen().getAlmacenRecepciones().isEmpty()) {
            listaMensajes.add("Debe adicionar desechos.");
        }
        if (almacenamientoTemporalBean.getAlmacen().getTipoLocal() == null) {
            listaMensajes.add(REQUERIDO_PRE + "Local" + REQUERIDO_POST);
        }
        if (almacenamientoTemporalBean.getAlmacen().getTipoMaterialConstruccion() == null) {
            listaMensajes.add(REQUERIDO_PRE + "Material" + REQUERIDO_POST);
        }
        if (almacenamientoTemporalBean.getAlmacen().getTipoVentilacion() == null) {
            listaMensajes.add(REQUERIDO_PRE + "Ventilación" + REQUERIDO_POST);
        }
        if (almacenamientoTemporalBean.getAlmacen().getTipoIluminacion() == null) {
            listaMensajes.add(REQUERIDO_PRE + "Iluminación" + REQUERIDO_POST);
        }
        if (almacenamientoTemporalBean.getAlmacen().getLargo().equals(VALOR_CERO)) {
            listaMensajes.add(REQUERIDO_PRE + "Largo" + REQUERIDO_POST);
        }
        if (almacenamientoTemporalBean.getAlmacen().getAncho().equals(VALOR_CERO)) {
            listaMensajes.add(REQUERIDO_PRE + "Ancho" + REQUERIDO_POST);
        }
        if (almacenamientoTemporalBean.getAlmacen().getAltura().equals(VALOR_CERO)) {
            listaMensajes.add(REQUERIDO_PRE + "Altura" + REQUERIDO_POST);
        }
        if (almacenamientoTemporalBean.isMostrarFosasRetencion()
                && almacenamientoTemporalBean.getAlmacen().getCapacidadFosas().equals(VALOR_CERO)) {
            listaMensajes.add(REQUERIDO_PRE + "Capacidad de fosas" + REQUERIDO_POST);
        }
        if (almacenamientoTemporalBean.getAlmacen().getExtincionIncendio() == null
                || almacenamientoTemporalBean.getAlmacen().getExtincionIncendio().isEmpty()) {
            listaMensajes
                    .add(REQUERIDO_PRE + "Breve descripción de sistema de extinción de incendios" + REQUERIDO_POST);
        }
        if (buscarIdAlmacen()) {
            listaMensajes.add("La identificación del Almacén introducido ya existe.");
        }
        if (listaMensajes.isEmpty()) {
            return true;
        } else {
            JsfUtil.addMessageError(listaMensajes);
            return false;
        }
    }*/

    /**
     *
     */
    /*public void seleccionarCuerpoHidrico(CuerpoHidrico cuerpoHidrico) {
        cuerpoHidrico.setEditar(true);
        cuerpoHidricoBean.setCuerpoHidrico(cuerpoHidrico);
        inicializarUsosSeleccionados();
    }*/


    /**
     *
     *
     */
    /*public void removerCuerpoHidrico(CuerpoHidrico cuerpoHidrico) {
        try {
            if (calidadComponenteFacade.getMuestras(cuerpoHidrico).size() == 0) {
                cuerpoHidricoBean.getListaCuerposHidricos().remove(cuerpoHidrico);
                if (cuerpoHidrico.getId() != null) {
                    cuerpoHidrico.setEstado(false);
                    cuerpoHidricoBean.getListaEntidadesRemover().add(cuerpoHidrico);
                  //  cuerpoHidricoBean.getListaEntidadesRemover().add(cuerpoHidrico.getCoordenadaGeneralList());
                    //cuerpoHidricoBean.getListaEntidadesRemover().add(cuerpoHidrico.getUsosCuerposHidricos());
                }
            } else {
                JsfUtil.addMessageError("No se puede eliminar el cuerpo hídrico seleccionado porque está asociado a una o varias muestras en el punto Calidad del agua.");
            }
        } catch (Exception e) {
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
            LOG.error(e, e);
        }
    }*/

    /**
     *
     */
    /*public void guardarCuerpoHidrico() {
        try {

            validarGuardarCoordenadas();

            if (!cuerpoHidricoBean.getCuerpoHidrico().isEditar()) {
                cuerpoHidricoBean.getListaCuerposHidricos().add(cuerpoHidricoBean.getCuerpoHidrico());
            }
            actualizarUsosCuerposHidricos();

            JsfUtil.addCallbackParam("addCuerpoHidrico");

        } catch (Exception e) {
            JsfUtil.addMessageError(e.getMessage());
            LOG.error(e, e);
        }
    }*/

    /*private void actualizarUsosCuerposHidricos() {
        List<UsoCuerpoHidrico> usosCuerposHidricos = new ArrayList<UsoCuerpoHidrico>();

        for (CatalogoUso catalogoUso : cuerpoHidricoBean.getListaUsos()) {
            UsoCuerpoHidrico usoCuerpoHidrico = new UsoCuerpoHidrico();
            usoCuerpoHidrico.setCuerpoHidrico(cuerpoHidricoBean.getCuerpoHidrico());
            usoCuerpoHidrico.setCatalogoUso(catalogoUso);

            Boolean encontrado = buscarCatalogoUsoGuardado(catalogoUso);

            if (!encontrado)
                cuerpoHidricoBean.getCuerpoHidrico().getUsosCuerposHidricos().add(usoCuerpoHidrico);

        }

        buscarCatalogosUsosEliminados();
    }*/

    public void cerrarAlmacen() {
        try {

            JsfUtil.addCallbackParam("addAlmacen");
            almacenTemporalBean.setListaAlmacenes(new ArrayList<Almacen>());

        } catch (Exception e) {
            JsfUtil.addMessageError(e.getMessage());
            LOG.error(e, e);
        }
    }

    /*private void reasignarIndiceCuerpoHidrico() {
        int i = 0;
        for (CuerpoHidrico in : cuerpoHidricoBean.getListaCuerposHidricos()) {
            in.setIndice(i);
            i++;
        }
    }*/

    /*public void agregarCoordenada() {
        cuerpoHidricoBean.setCoordenadaGeneral(new CoordenadaGeneral());
    }*/

    public void seleccionarDesecho(AlmacenRecepcion almacenRecepcion) {
        almacenRecepcion.setEditar(true);
        almacenTemporalBean.setAlmacenRecepcion(almacenRecepcion);
    }

    public void removerAlmacenRecepcion(AlmacenRecepcion almacenRecepcion) {
        try {
            almacenTemporalBean.getAlmacen().getAlmacenRecepciones().remove(almacenRecepcion.getIndice());
            if (almacenRecepcion.getId() != null) {
                almacenRecepcion.setEstado(false);
                almacenTemporalBean.getListaEntidadesRemover().add(almacenRecepcion);
            }

        } catch (Exception e) {
            JsfUtil.addMessageError(e.getMessage());
            LOG.error(e, e);
        }
    }

    public void guardarAlmacen() {
        RequestContext context = RequestContext.getCurrentInstance();
        if (validarGuardarAlmacen()) {
            almacenTemporalBean.getAlmacen().setCantidad(almacenTemporalBean.getAlmacen().getAltura()*
                    almacenTemporalBean.getAlmacen().getAncho()*
                    almacenTemporalBean.getAlmacen().getLargo());

            //validarGuardarCoordenadas();

            if (!almacenTemporalBean.getAlmacen().isEditar()) {

                almacenTemporalBean.getAlmacen().setAprobacionRequisitosTecnicos(aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos());
                almacenTemporalBean.getListaAlmacenes().add(almacenTemporalBean.getAlmacen());
            }
            //actualizarUsosCuerposHidricos();

            JsfUtil.addCallbackParam("addAlmacen");


        }
    }

    public void guardarAlmacenRecepcion() {
        RequestContext context = RequestContext.getCurrentInstance();
        if(validarGuardarAlmacenRecepcion()){

        try {

            if (!almacenTemporalBean.getAlmacenRecepcion().isEditar()) {
                almacenTemporalBean.getAlmacen().getAlmacenRecepciones().add(almacenTemporalBean.getAlmacenRecepcion());
            }

            JsfUtil.addCallbackParam("addDesechos");
        } catch (Exception e) {
            JsfUtil.addMessageError(e.getMessage());
            LOG.error(e, e);
        }
        validarFosasRetencion(almacenTemporalBean.getAlmacen().getAlmacenRecepciones());
        if (!almacenTemporalBean.isMostrarFosasRetencion()) {
            almacenTemporalBean.getAlmacen().setCapacidadFosas(null);
        }
        }
    }

    public void validarFosasRetencion(List<AlmacenRecepcion> almacenRecepciones) {
        getAlmacenTemporalBean().setMostrarFosasRetencion(false);
        for (AlmacenRecepcion almacenRecepcion : almacenRecepciones) {
            if (ESTADO_LIQUIDO.equalsIgnoreCase(almacenRecepcion.getEntityRecepcionDesecho().getTipoEstado())
                    || ESTADO_SEMISOLIDO.equalsIgnoreCase(almacenRecepcion.getEntityRecepcionDesecho().getTipoEstado())) {
                getAlmacenTemporalBean().setMostrarFosasRetencion(true);
                break;
            }
        }
    }

    public boolean validarGuardarAlmacen() {
        List<String> listaMensajes = new ArrayList<String>();
        if (almacenTemporalBean.getAlmacen().getAlmacenRecepciones().isEmpty()) {
            listaMensajes.add("Debe adicionar desechos.");
        }
        if (buscarIdAlmacen()) {
            listaMensajes.add("La identificación del Almacén introducido ya existe.");
        }
        if (listaMensajes.isEmpty()) {
            return true;
        } else {
            JsfUtil.addMessageError(listaMensajes);
            return false;
        }
    }

    public boolean validarGuardarAlmacenRecepcion(){
        List<String> listaMensajes = new ArrayList<String>();
        if(buscarIdAlmacenDesechoEstadoTipoEnvase()){
            listaMensajes.add("No puede existir un desecho con un mismo estado y tipo de envase en el mismo almacén");
        }
        if (listaMensajes.isEmpty()) {
            return true;
        } else {
            JsfUtil.addMessageError(listaMensajes);
            return false;
        }
    }

    public boolean buscarIdAlmacen() {
        boolean existe = false;
        if (almacenTemporalBean.getAlmacen().isEditar()) {
            almacenTemporalBean.setAlmacenAux(almacenTemporalBean.getAlmacen());
            if (!almacenTemporalBean.getAlmacen().getIdentificacion()
                    .equals(almacenTemporalBean.getAlmacenAux().getIdentificacion())) {
                existe = buscarIdentificacionAlmacen();
            }
        } else {
            existe = buscarIdentificacionAlmacen();
        }
        return existe;
    }

    public boolean buscarIdAlmacenDesechoEstadoTipoEnvase() {
        boolean existe = false;
        if (almacenTemporalBean.getAlmacenRecepcion().isEditar()) {
            almacenTemporalBean.setAlmacenRecepcionAux(almacenTemporalBean.getAlmacenRecepcion());
            almacenTemporalBean.setAlmacenAux(almacenTemporalBean.getAlmacen());
            if (!almacenTemporalBean.getAlmacenRecepcion().getId()
                    .equals(almacenTemporalBean.getAlmacenRecepcionAux().getId()) && (!almacenTemporalBean.getAlmacenRecepcion().getEntityRecepcionDesecho().equals(almacenTemporalBean.
                            getAlmacenRecepcionAux().getEntityRecepcionDesecho()))) {
                existe = buscarIdentificacionAlmacenDesechoEstadoTipoEnvase();
            }
        } else {
            existe = buscarIdentificacionAlmacenDesechoEstadoTipoEnvase();
        }
        return existe;
    }

    /*public String cargarTipoCategoriaCuerpoHidrico(Integer indice) {
        return TipoCategoriaCuerpoHidrico.values()[indice-1].getDescripcion();
    }*/

    public boolean buscarIdentificacionAlmacen() {
        boolean existe = false;
        for (Almacen alm : almacenTemporalBean.getListaAlmacenes()) {
            if (alm.getIdentificacion().equalsIgnoreCase(almacenTemporalBean.getAlmacen().getIdentificacion())) {
                existe = true;
                break;
            }
        }
        return existe;
    }

    public boolean buscarIdentificacionAlmacenDesechoEstadoTipoEnvase() {
        boolean existe = false;
        ArrayList<EntityRecepcionDesecho> listaVerificar= new ArrayList<EntityRecepcionDesecho>();
        for (AlmacenRecepcion almacenRecepcion: almacenTemporalBean.getAlmacen().getAlmacenRecepciones()){
            if (almacenRecepcion.getEntityRecepcionDesecho().equals(almacenTemporalBean.getAlmacenRecepcion().getEntityRecepcionDesecho()) && (almacenRecepcion.getTipoEnvase().
                    equals(almacenTemporalBean.getAlmacenRecepcion().getTipoEnvase()))) {
                existe = true;
                break;
            }
            //listaVerificar.add(almacenRecepcion.getEntityRecepcionDesecho());
        }
        /*for (EntityRecepcionDesecho recepcionDesecho : listaVerificar) {

        }*/
        return existe;
    }
    /**
     *
     */
    public void guardar() {
        try {
            if (validarGuardar()) {
            //cuerpoHidricoBean.getCuerpoHidrico().setEstudioImpactoAmbiental(cuerpoHidricoBean.getEstudioImpactoAmbiental());

            almacenFacade.guardar(almacenTemporalBean.getListaAlmacenes(), almacenTemporalBean.getListaEntidadesRemover());

            obtenerListaAlmacenes();

            validacionSeccionesFacade.guardarValidacionSeccion("AprobacionRequisitosTecnicos",
                    "almacenamientoTemporal", almacenTemporalBean.getAprobacionRequisitosTecnicos().getId()
                            .toString());

                JsfUtil.addMessageInfo(JsfUtil.REGISTRO_GUARDADO);
                validarNumeroDesechosIngresados();
            }
        } catch (ServiceException e) {

            JsfUtil.addMessageError(e.getMessage());
            e.printStackTrace();
            LOG.error(e, e);
        }
    }

    public boolean validarGuardar() {
        List<String> listaMensajes = new ArrayList<String>();
        if (almacenTemporalBean.getListaAlmacenes().isEmpty()) {
            listaMensajes.add("Debe ingresar al menos un almacen.");
        }
        if (listaMensajes.isEmpty()) {
            return true;
        } else {
            JsfUtil.addMessageError(listaMensajes);
            return false;
        }
    }

    private void validarNumeroDesechosIngresados() throws ServiceException {
        RequestContext context = RequestContext.getCurrentInstance();
        boolean existe = almacenFacade.validarAlmacenRecepcionDesechos(aprobacionRequisitosTecnicosBean
                .getAprobacionRequisitosTecnicos().getId());
        if (existe) {
            context.addCallbackParam("numDesechosAlmacen", false);
        } else {
            context.addCallbackParam("numDesechosAlmacen", true);
        }

    }

    /*private void cargarCatalogoUsos() {
        listaCatalogoUso = new ArrayList<CatalogoUso>();
        listaCatalogoUso.addAll(catalogoUsoFacade.obtenerCatalogosUsosByNormativa());
    }*/

    public void cerrar() {
        try {

            JsfUtil.addCallbackParam("addAlmacen");

        } catch (Exception e) {
            JsfUtil.addMessageError(e.getMessage());
            LOG.error(e, e);
        }
    }


}



