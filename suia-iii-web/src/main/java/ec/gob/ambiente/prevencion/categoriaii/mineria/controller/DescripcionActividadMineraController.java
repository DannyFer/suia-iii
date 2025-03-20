package ec.gob.ambiente.prevencion.categoriaii.mineria.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import lombok.Getter;
import lombok.Setter;

import org.primefaces.context.RequestContext;

import ec.gob.ambiente.prevencion.categoriaii.mineria.bean.DescripcionActividadMineraBean;
import ec.gob.ambiente.suia.catalogos.facade.CatalogoFisicoFacade;
import ec.gob.ambiente.suia.domain.ActividadMinera;
import ec.gob.ambiente.suia.domain.CatalogoActividadComercial;
import ec.gob.ambiente.suia.domain.CatalogoCategoriaFase;
import ec.gob.ambiente.suia.domain.CatalogoGeneralFisico;
import ec.gob.ambiente.suia.domain.CatalogoHerramientaArtesanal;
import ec.gob.ambiente.suia.domain.CatalogoInstalacion;
import ec.gob.ambiente.suia.domain.CatalogoTipoMaterial;
import ec.gob.ambiente.suia.domain.ConcesionMinera;
import ec.gob.ambiente.suia.domain.DescripcionActividadMineria;
import ec.gob.ambiente.suia.domain.FichaAmbientalMineria;
import ec.gob.ambiente.suia.domain.FichaMineriaInsumos;
import ec.gob.ambiente.suia.domain.HerramientaMinera;
import ec.gob.ambiente.suia.domain.Instalacion;
import ec.gob.ambiente.suia.domain.ProcesoMinero;
import ec.gob.ambiente.suia.domain.TipoCatalogo;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.FaseFichaAmbientalFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.ficha.mineria.facade.ActividadMineriaFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.ficha.mineria.facade.CaracteristicasGeneralesMineriaFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.ficha.mineria.facade.DescripcionActividadMineriaFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.ficha.mineria.facade.FichaAmbientalMineriaFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

/**
 *
 * @author christian
 */
@ManagedBean
@ViewScoped
public class DescripcionActividadMineraController implements Serializable {

    private static final long serialVersionUID = -6837604502268924329L;

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
            .getLogger(FichaMineriaController.class);
    @EJB
    private DescripcionActividadMineriaFacade descripcionActividadMineriaFacade;

    @Getter
    @Setter
    private DescripcionActividadMineraBean descripcionActividadMineraBean;

    @Getter
    @Setter
    private String tieneLicenciaAmbiental;

    @EJB
    private FaseFichaAmbientalFacade faseFichaAmbientalFacade;

    @EJB
    private ActividadMineriaFacade actividadMineriaFacade;

    @EJB
    private CaracteristicasGeneralesMineriaFacade caracteristicasGeneralesMineriaFacade;

    @EJB
    private CatalogoFisicoFacade catalogoFisicoFacade;

    @EJB
    private FichaAmbientalMineriaFacade fichaAmbientalMineriaFacade;

    private static final String NUMERO_LICENCIA = "Número de licencia ";

    private static final String OBSERVACION = "Observaciones ";

    private static final String ENTRE_OTROS = "Entre otros";

    private static final SelectItem SELECCIONE = new SelectItem(null, "Seleccione");

    private static final int OPERACION = 2;

    @PostConstruct
    private void cargarDatos() {
        descripcionActividadMineraBean = new DescripcionActividadMineraBean();
        getDescripcionActividadMineraBean().iniciarDatos();
        FichaAmbientalMineria f = (FichaAmbientalMineria) JsfUtil.devolverObjetoSession(Constantes.SESSION_FICHA_AMBIENTAL_MINERA_OBJECT);
        getDescripcionActividadMineraBean().setFichaAmbientalMineria(fichaAmbientalMineriaFacade.obtenerPorId(f.getId()));
        getDescripcionActividadMineraBean().setCatalogoCategoriaFases(listaFasesPorSubsector());
        getDescripcionActividadMineraBean().setFaseSeleccionada(getDescripcionActividadMineraBean().getCatalogoCategoriaFases().get(0));
        getDescripcionActividadMineraBean().setListaActividades(listaActividadesPorFase());
        getDescripcionActividadMineraBean().setListaActividadesSeleccionadas(listarActividadesSeleccionadas());
//        reasignarIndiceActividad();
        getDescripcionActividadMineraBean().setCatalogoTipoObtencion(cargarTipoObtencion());
        getDescripcionActividadMineraBean().setListaHerramientas(listarHerramientaPorFicha());
//        reasignarIndiceHerramienta();
        getDescripcionActividadMineraBean().setCatalogoInstalaciones(cargarInstalaciones());
        getDescripcionActividadMineraBean().setListaInstalaciones(listarInstalacionPorFicha());
//        reasignarIndiceInstalacion();
        cargarDescripcion();
        catalogoUnidadMedida();
        if (getDescripcionActividadMineraBean().getDescripcionActividadMineria().isTieneLicenciaAmbiental()) {
            getDescripcionActividadMineraBean().setEtiqueta(NUMERO_LICENCIA);
            setTieneLicenciaAmbiental("Si");
        } else {
            getDescripcionActividadMineraBean().setEtiqueta(OBSERVACION);
            setTieneLicenciaAmbiental("No");
        }
        cargarInsumos();
        validarConcesion();
        
        //Cris F: carga de historial
        cargarHistorial();
        
        
        reasignarIndiceActividad();
        reasignarIndiceHerramienta();
        reasignarIndiceInstalacion();
        reasignarIndice();
    }

    public boolean activarConcesion() {
        return getDescripcionActividadMineraBean().getFichaAmbientalMineria().getProyectoLicenciamientoAmbiental().isMineriaAreasConcesionadas();
    }

    public void validarConcesion() {
        if (activarConcesion()) {
            try {
                List<ConcesionMinera> concesiones = descripcionActividadMineriaFacade.concesionesMinerasPorProyecto(getDescripcionActividadMineraBean().getFichaAmbientalMineria().getProyectoLicenciamientoAmbiental().getId());
                if (concesiones != null && !concesiones.isEmpty()) {
                    getDescripcionActividadMineraBean().getFichaAmbientalMineria().setCodigoConcesion(concesiones.get(0).getCodigo());
                    getDescripcionActividadMineraBean().getFichaAmbientalMineria().setNombreConcesion(concesiones.get(0).getNombre());
                } else {
                    getDescripcionActividadMineraBean().getFichaAmbientalMineria().setCodigoConcesion("");
                    getDescripcionActividadMineraBean().getFichaAmbientalMineria().setNombreConcesion("");
                }
            } catch (Exception e) {
                getDescripcionActividadMineraBean().getFichaAmbientalMineria().setCodigoConcesion("");
                getDescripcionActividadMineraBean().getFichaAmbientalMineria().setNombreConcesion("");
                LOG.info("Error al cargar concesiones. ", e);
            }
        } else {
            getDescripcionActividadMineraBean().getFichaAmbientalMineria().setCodigoConcesion("");
            getDescripcionActividadMineraBean().getFichaAmbientalMineria().setNombreConcesion("");
        }
    }

    private void cargarInsumos() {
        try {
            getDescripcionActividadMineraBean().setListaInsumosAgregados(descripcionActividadMineriaFacade.listarPorFicha(getDescripcionActividadMineraBean().getFichaAmbientalMineria()));
//            reasignarIndice();
            getDescripcionActividadMineraBean().setListaInsumos(new ArrayList<SelectItem>());
            getDescripcionActividadMineraBean().setListaHijosInsumos(new ArrayList<SelectItem>());
            getDescripcionActividadMineraBean().getListaInsumos().add(SELECCIONE);
            getDescripcionActividadMineraBean().getListaHijosInsumos().add(SELECCIONE);
            getDescripcionActividadMineraBean().setListaCatalogoInsumos(catalogoFisicoFacade.obtenerListaFisicoTipo(TipoCatalogo.CODIGO_INSUMOS_MINERIA));
            for (CatalogoGeneralFisico c : getDescripcionActividadMineraBean().getListaCatalogoInsumos()) {
                if (c.getIdPadre() == null) {
                    getDescripcionActividadMineraBean().getListaInsumos().add(new SelectItem(c.getId(), c.getDescripcion()));
                }
            }
        } catch (Exception e) {
            LOG.error(e, e);
        }
    }

    public void cargarHijosInsumos() {
        try {
            getDescripcionActividadMineraBean().setListaHijosInsumos(new ArrayList<SelectItem>());
            getDescripcionActividadMineraBean().getListaHijosInsumos().add(SELECCIONE);
            getDescripcionActividadMineraBean().setListaCatalogoInsumos(catalogoFisicoFacade.obtenerListaFisicoTipo(TipoCatalogo.CODIGO_INSUMOS_MINERIA));
            for (CatalogoGeneralFisico c1 : getDescripcionActividadMineraBean().getListaCatalogoInsumos()) {
                if (c1.getIdPadre() != null && getDescripcionActividadMineraBean().getIdInsumos().equals(c1.getIdPadre().toString())) {
                    getDescripcionActividadMineraBean().getListaHijosInsumos().add(new SelectItem(c1.getId(), c1.getDescripcion()));
                }
            }
            getDescripcionActividadMineraBean().setApareceOtrosInsumos(false);
            getDescripcionActividadMineraBean().setApareceOtrosHijosInsumos(false);
        } catch (Exception e) {
            LOG.error(e, e);
        }
    }

    public void cargarOtrosHijosInsumos() {
        CatalogoGeneralFisico c = localizarIdInsumo(new Integer(getDescripcionActividadMineraBean().getIdHijosInsumos()));
        if ("Otros".equalsIgnoreCase(c.getDescripcion())) {
            getDescripcionActividadMineraBean().setApareceOtrosHijosInsumos(true);
            getDescripcionActividadMineraBean().setNumeroColumnas("4");
        } else {
            getDescripcionActividadMineraBean().setApareceOtrosHijosInsumos(false);
        }
    }

    public void agregarInsumo() {
        List<String> mensajes = new ArrayList<String>();
        if (getDescripcionActividadMineraBean().getIdInsumos() == null || getDescripcionActividadMineraBean().getIdInsumos().isEmpty()) {
            mensajes.add("El campo 'Tipo de insumo' es requerido.");
            if (getDescripcionActividadMineraBean().getIdHijosInsumos() == null || getDescripcionActividadMineraBean().getIdHijosInsumos().isEmpty()) {
                mensajes.add("El campo 'Insumo' es requerido.");
            }

        } else {
            CatalogoGeneralFisico c = localizarIdInsumo(new Integer(getDescripcionActividadMineraBean().getIdInsumos()));
            if (!ENTRE_OTROS.equalsIgnoreCase(c.getDescripcion()) && (getDescripcionActividadMineraBean().getIdHijosInsumos() == null || getDescripcionActividadMineraBean().getIdHijosInsumos().isEmpty())) {
                mensajes.add("El campo 'Insumo' es requerido.");
            }
        }

        if (getDescripcionActividadMineraBean().getFichaMineriaInsumos().getCantidadHijoInsumo() == null) {
            mensajes.add("El campo '*Cantidad' es requerido.");
        } else {
            if (getDescripcionActividadMineraBean().getFichaMineriaInsumos().getCantidadHijoInsumo() == 0) {
                mensajes.add("El campo '*Cantidad' debe ser mayor a 0.");
            }
        }
        validarInsumos(mensajes);
        valoresInsumos();
    }

    private void validarInsumos(List<String> mensajes) {
        if (mensajes.isEmpty()) {
            getDescripcionActividadMineraBean().getFichaMineriaInsumos().setCatalogoInsumo(localizarIdInsumo(new Integer(getDescripcionActividadMineraBean().getIdInsumos())));
            if (getDescripcionActividadMineraBean().getIdHijosInsumos() != null && !getDescripcionActividadMineraBean().getIdHijosInsumos().isEmpty()) {
                getDescripcionActividadMineraBean().getFichaMineriaInsumos().setCatalogoHijoInsumo(localizarIdInsumo(new Integer(getDescripcionActividadMineraBean().getIdHijosInsumos())));
                getDescripcionActividadMineraBean().getFichaMineriaInsumos().setIdCatalogoHijoInsumo(getDescripcionActividadMineraBean().getFichaMineriaInsumos().getCatalogoHijoInsumo().getId());

            }
            if (getDescripcionActividadMineraBean().getOperacion() == 0) {
                if (!validarMaterialInsumo()) {
                    getDescripcionActividadMineraBean().getListaInsumosAgregados().add(getDescripcionActividadMineraBean().getFichaMineriaInsumos());
                } else {
                    JsfUtil.addMessageError("Este insumo ya esta en la lista.");
                }
            } else {
                getDescripcionActividadMineraBean().getListaInsumosAgregados().set(getDescripcionActividadMineraBean().getFichaMineriaInsumos().getIndice(), getDescripcionActividadMineraBean().getFichaMineriaInsumos());
            }
        } else {
            JsfUtil.addMessageError(mensajes);
        }
    }

    private void valoresInsumos() {
        reasignarIndice();
        getDescripcionActividadMineraBean().setFichaMineriaInsumos(new FichaMineriaInsumos());
        getDescripcionActividadMineraBean().setIdInsumos(null);
        getDescripcionActividadMineraBean().setIdHijosInsumos(null);
        getDescripcionActividadMineraBean().setApareceOtrosHijosInsumos(false);
        getDescripcionActividadMineraBean().setApareceOtrosInsumos(false);
        getDescripcionActividadMineraBean().setOperacion(0);
    }

    public void eliminarInsumo(int indice) {
        FichaMineriaInsumos f = getDescripcionActividadMineraBean().getListaInsumosAgregados().get(indice);
        if (f.getId() != null) {
            f.setEstado(false);
            getDescripcionActividadMineraBean().getListaInsumosEliminados().add(f);
        }
        getDescripcionActividadMineraBean().getListaInsumosAgregados().remove(indice);
        reasignarIndice();
    }

    public void seleccionarInsumo(FichaMineriaInsumos fichaMineriaInsumos, int operacion) {
        getDescripcionActividadMineraBean().setFichaMineriaInsumos(fichaMineriaInsumos);
        getDescripcionActividadMineraBean().setIdInsumos(fichaMineriaInsumos.getCatalogoInsumo().getId().toString());
        cargarHijosInsumos();
        if (ENTRE_OTROS.equalsIgnoreCase(fichaMineriaInsumos.getCatalogoInsumo().getDescripcion())) {
            getDescripcionActividadMineraBean().setApareceOtrosInsumos(true);
            getDescripcionActividadMineraBean().setApareceOtrosHijosInsumos(true);
            getDescripcionActividadMineraBean().setNumeroColumnas("4");
        } else {
            if (getDescripcionActividadMineraBean().getFichaMineriaInsumos().getHijoInsumoOtro() != null && !getDescripcionActividadMineraBean().getFichaMineriaInsumos().getHijoInsumoOtro().isEmpty()) {
                getDescripcionActividadMineraBean().setApareceOtrosInsumos(false);
                getDescripcionActividadMineraBean().setApareceOtrosHijosInsumos(true);
                getDescripcionActividadMineraBean().setNumeroColumnas("3");
            } else {
                getDescripcionActividadMineraBean().setApareceOtrosInsumos(false);
                getDescripcionActividadMineraBean().setApareceOtrosHijosInsumos(false);
                getDescripcionActividadMineraBean().setNumeroColumnas("4");
            }
        }
        if (getDescripcionActividadMineraBean().getFichaMineriaInsumos().getCatalogoHijoInsumo() != null) {
            getDescripcionActividadMineraBean().setIdHijosInsumos(fichaMineriaInsumos.getCatalogoHijoInsumo().getId().toString());
        }
        getDescripcionActividadMineraBean().setOperacion(operacion);
    }

    private boolean validarMaterialInsumo() {
        boolean retorno = false;
        if (getDescripcionActividadMineraBean().getIdHijosInsumos() != null && !getDescripcionActividadMineraBean().getIdHijosInsumos().isEmpty() && (getDescripcionActividadMineraBean().getListaInsumosAgregados() != null && !getDescripcionActividadMineraBean().getListaInsumosAgregados().isEmpty())) {
            for (FichaMineriaInsumos f : getDescripcionActividadMineraBean().getListaInsumosAgregados()) {
                if (f.getCatalogoHijoInsumo() != null && f.getCatalogoHijoInsumo().getId().toString().equals(getDescripcionActividadMineraBean().getIdHijosInsumos())) {
                    retorno = true;
                    break;
                }
            }
        }
        return retorno;
    }

    private void reasignarIndice() {
        int i = 0;
        for (FichaMineriaInsumos f : getDescripcionActividadMineraBean().getListaInsumosAgregados()) {
            f.setIndice(i);
            i++;
        }
    }

    private CatalogoGeneralFisico localizarIdInsumo(Integer insumo) {
        CatalogoGeneralFisico catalogoGeneralFisico = null;
        for (CatalogoGeneralFisico c : getDescripcionActividadMineraBean().getListaCatalogoInsumos()) {
            if (c.getId().equals(insumo)) {
                catalogoGeneralFisico = c;
                break;
            }
        }
        return catalogoGeneralFisico;
    }

    private List<CatalogoCategoriaFase> listaFasesPorSubsector() {
        List<CatalogoCategoriaFase> lista;
        try {
            lista = faseFichaAmbientalFacade.obtenerCatalogoCategoriaFasesPorSubsectorCodigo(getDescripcionActividadMineraBean().getFichaAmbientalMineria().getProyectoLicenciamientoAmbiental()
                    .getCatalogoCategoria().getTipoSubsector().getCodigo());
        } catch (Exception e) {
            LOG.error("Error al consultar las fases asociadas a la actividad minera artesanal", e);
            lista = null;
        }
        return lista;
    }

    private List<CatalogoActividadComercial> listaActividadesPorFase() {
        List<CatalogoActividadComercial> lista;
        try {
            lista = descripcionActividadMineriaFacade.getCatalogoActividadComercial(getDescripcionActividadMineraBean().getFaseSeleccionada().getId());
        } catch (Exception e) {
            LOG.error("Error al cargar lista de actividades de minera artesanal", e);
            lista = null;
        }
        return lista;
    }

    private List<ActividadMinera> listarActividadesSeleccionadas() {
        List<ActividadMinera> lista;

        try {
            lista = actividadMineriaFacade.listarPorFichaAmbiental(getDescripcionActividadMineraBean().getFichaAmbientalMineria());
            
            for(ActividadMinera actividad : lista){
            	if(actividad.getFechaHistorico() != null)
            		actividad.setNuevoEnModificacion(true);
            }            
        } catch (Exception e) {
            LOG.error("Error al listar actividades", e);
            lista = null;
        }
        return lista;
    }

    public void agregarActividadMinera() {
        try {
            List<String> mensajes = new ArrayList<String>();
            if (getDescripcionActividadMineraBean().getActividadMinera().getActividadComercial() == null) {
                mensajes.add("El campo 'Actividad' es requerido.");
            }
            if (getDescripcionActividadMineraBean().getActividadMinera().getDiasDuracion() == 0) {
                mensajes.add("El campo 'Duración (días)' es requerido.");
            }
            if (getDescripcionActividadMineraBean().getActividadMinera().getDescripcion() == "") {
                mensajes.add("El campo 'Descripción' es requerido.");
            }
            validarActividadMinera(mensajes);
        } catch (Exception e) {
            LOG.error("Error al agregar una actividad minera", e);
            JsfUtil.addMessageError(JsfUtil.ERROR_GUARDAR_REGISTRO);
        }
    }

    private void validarActividadMinera(List<String> mensajes) {
        if (mensajes.isEmpty()) {
            if (getDescripcionActividadMineraBean().getOperacion() == 0) {
                if (!validarActividad()) {
                    getDescripcionActividadMineraBean().getActividadMinera().setFichaAmbientalMineria(descripcionActividadMineraBean.getFichaAmbientalMineria());
                    getDescripcionActividadMineraBean().getListaActividadesSeleccionadas().add(getDescripcionActividadMineraBean().getActividadMinera());
                    reasignarIndiceActividad();
                    getDescripcionActividadMineraBean().setActividadMinera(new ActividadMinera());
                } else {
                    JsfUtil.addMessageError("La actividad ya fue agregada.");
                }
            } else {
                getDescripcionActividadMineraBean().getListaActividadesSeleccionadas().set(getDescripcionActividadMineraBean().getActividadMinera().getIndice(), getDescripcionActividadMineraBean().getActividadMinera());
                getDescripcionActividadMineraBean().setActividadMinera(new ActividadMinera());
                getDescripcionActividadMineraBean().setOperacion(0);
            }
        } else {
            JsfUtil.addMessageError(mensajes);
        }
    }

    private boolean validarActividad() {
        boolean retorno = false;
        if (getDescripcionActividadMineraBean().getListaActividadesSeleccionadas() != null
                && !getDescripcionActividadMineraBean().getListaActividadesSeleccionadas().isEmpty()) {
            for (ActividadMinera a : getDescripcionActividadMineraBean().getListaActividadesSeleccionadas()) {
                if (a.getActividadComercial().getDescripcion().equals(getDescripcionActividadMineraBean().getActividadMinera().getActividadComercial().getDescripcion())) {
                    retorno = true;
                    break;
                }
            }
        }
        return retorno;
    }

    private void reasignarIndiceActividad() {
        int i = 0;
        for (ActividadMinera a : getDescripcionActividadMineraBean().getListaActividadesSeleccionadas()) {
            a.setIndice(i);
            i++;
        }
    }

    public void editarActividadMinera(ActividadMinera actividadMinera, int operacion) {
        getDescripcionActividadMineraBean().setActividadMinera(actividadMinera);
        getDescripcionActividadMineraBean().setOperacion(operacion);
    }

    public void eliminarActividadMinera(ActividadMinera actividadMinera) {
        ActividadMinera actividad = getDescripcionActividadMineraBean().getListaActividadesSeleccionadas().get(actividadMinera.getIndice());
        try {
            if (actividad.getId() != null) {
                actividad.setEstado(false);
                getDescripcionActividadMineraBean().getListaActividadesEliminadas().add(actividad);
            }
            getDescripcionActividadMineraBean().getListaActividadesSeleccionadas().remove(actividadMinera.getIndice());
            reasignarIndiceActividad();
        } catch (Exception e) {
            LOG.info("Error al intentar remover actividad minera.", e);
        }
    }

    public List<CatalogoTipoMaterial> cargarTipoObtencion() {
        List<CatalogoTipoMaterial> tiposMateriales = new ArrayList<CatalogoTipoMaterial>();
        if (getDescripcionActividadMineraBean().getFichaAmbientalMineria().getCatalogoTipoMaterial() != null) {
            try {
                String[] opcionesTipoMaterial = JsfUtil.devuelveVector(getDescripcionActividadMineraBean().getFichaAmbientalMineria().getCatalogoTipoMaterial());
                for (String string : opcionesTipoMaterial) {
                    tiposMateriales.add(caracteristicasGeneralesMineriaFacade.obtenerPorId(Integer.parseInt(string)));
                }
            } catch (NumberFormatException e) {
                LOG.error("Error al cargar tipo de obtencion.", e);
                tiposMateriales = null;
            }
        } else {
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('dlgInfo').show();");
        }
        return tiposMateriales;
    }

    public void cargarProcesos() {
        getDescripcionActividadMineraBean().setCatalogoProcesosMineros(listarProcesosMineros(getDescripcionActividadMineraBean().getHerramientaMinera().getTipoObtencion().getId()));
    }

    public List<ProcesoMinero> listarProcesosMineros(Integer idTipoObtencion) {
        List<ProcesoMinero> lista;
        try {
            lista = descripcionActividadMineriaFacade.getProcesosMinerosXTipoObtencion(idTipoObtencion);
        } catch (Exception e) {
            LOG.error("Error al cargar proceso mineros", e);
            lista = null;
        }
        return lista;
    }

    public void cargarHerramientas() {
        getDescripcionActividadMineraBean().setCatalogoHerramientas(listarHerramientasArtesanales(getDescripcionActividadMineraBean().getHerramientaMinera().getProcesoMinero().getId()));
    }

    public List<CatalogoHerramientaArtesanal> listarHerramientasArtesanales(Integer idProceso) {
        List<CatalogoHerramientaArtesanal> lista;
        try {
            lista = descripcionActividadMineriaFacade.getHerramientasPorProceso(idProceso);
        } catch (Exception e) {
            LOG.error("Error al cargar herramientas para mineria artesanal", e);
            lista = null;
        }
        return lista;
    }

    private List<HerramientaMinera> listarHerramientaPorFicha() {
        List<HerramientaMinera> lista;
        try {
            lista = descripcionActividadMineriaFacade.listarHerramientaPorFichaAmbiental(getDescripcionActividadMineraBean().getFichaAmbientalMineria());
        } catch (Exception e) {
            LOG.error("Error al listar herramientas", e);
            lista = null;
        }
        return lista;
    }

    public void agregarHerramientaMinera() {
        try {
            List<String> mensajes = new ArrayList<String>();
            if (getDescripcionActividadMineraBean().getHerramientaMinera().getTipoObtencion() == null) {
                mensajes.add("El campo 'Tipo de Obtención' es requerido.");
            }
            if (getDescripcionActividadMineraBean().getHerramientaMinera().getProcesoMinero() == null) {
                mensajes.add("El campo 'Proceso ' es requerido.");
            }
            if (getDescripcionActividadMineraBean().getHerramientaMinera().getCatalogoHerramienta() == null) {
                mensajes.add("El campo 'Herramienta' es requerido.");
            }else if (getDescripcionActividadMineraBean().getHerramientaMinera().getCatalogoHerramienta().getNombre().compareTo("Otros") == 0){
            	if (getDescripcionActividadMineraBean().getHerramientaMinera().getDescripcion()==null || getDescripcionActividadMineraBean().getHerramientaMinera().getDescripcion().isEmpty()){
            		mensajes.add("El campo 'Otro' es requerido.");
            	}
            }else{
            	getDescripcionActividadMineraBean().getHerramientaMinera().setDescripcion(null);
            }
            if (getDescripcionActividadMineraBean().getHerramientaMinera().getCantidadHerramientas() == 0) {
                mensajes.add("El campo 'Cantidad' es requerido.");
            }
            
            validarHerramientasMineria(mensajes);
        } catch (Exception e) {
            LOG.error("Error al agregar una actividad minera.", e);
            JsfUtil.addMessageError(JsfUtil.ERROR_GUARDAR_REGISTRO);
        }
    }

    private void validarHerramientasMineria(List<String> mensajes) {
        if (mensajes.isEmpty()) {
            if (getDescripcionActividadMineraBean().getOperacion() == 0) {
                if (!validarHerramienta()) {
                    getDescripcionActividadMineraBean().getHerramientaMinera().setFichaAmbientalMineria(descripcionActividadMineraBean.getFichaAmbientalMineria());
                    getDescripcionActividadMineraBean().getListaHerramientas().add(getDescripcionActividadMineraBean().getHerramientaMinera());
                    reasignarIndiceHerramienta();
                    getDescripcionActividadMineraBean().setHerramientaMinera(new HerramientaMinera());
                    getDescripcionActividadMineraBean().getHerramientaMinera().setTipoObtencion(new CatalogoTipoMaterial());
                    getDescripcionActividadMineraBean().getHerramientaMinera().setProcesoMinero(new ProcesoMinero());
                } else {
                    JsfUtil.addMessageError("La herramienta ya fue agregada.");
                }
            } else {
                getDescripcionActividadMineraBean().getListaHerramientas().set(getDescripcionActividadMineraBean().getHerramientaMinera().getIndice(), getDescripcionActividadMineraBean().getHerramientaMinera());
                getDescripcionActividadMineraBean().setHerramientaMinera(new HerramientaMinera());
                getDescripcionActividadMineraBean().getHerramientaMinera().setTipoObtencion(new CatalogoTipoMaterial());
                getDescripcionActividadMineraBean().getHerramientaMinera().setProcesoMinero(new ProcesoMinero());
                getDescripcionActividadMineraBean().setOperacion(0);
            }
        } else {
            JsfUtil.addMessageError(mensajes);
        }

    }

    private boolean validarHerramienta() {
        boolean retorno = false;
        if (getDescripcionActividadMineraBean().getListaHerramientas() != null
                && !getDescripcionActividadMineraBean().getListaHerramientas().isEmpty()) {
            for (HerramientaMinera h : getDescripcionActividadMineraBean().getListaHerramientas()) {
                if (h.getCatalogoHerramienta().getId() == getDescripcionActividadMineraBean().getHerramientaMinera().getCatalogoHerramienta().getId()) {
                    retorno = true;
                    break;
                }
            }
        }
        return retorno;
    }

    private void reasignarIndiceHerramienta() {
        int i = 0;
        for (HerramientaMinera h : getDescripcionActividadMineraBean().getListaHerramientas()) {
            h.setIndice(i);
            i++;
        }
    }

    public void editarHerramienta(HerramientaMinera herramientaMinera, int operacion) {
        try {
            getDescripcionActividadMineraBean().setHerramientaMinera(herramientaMinera);
            getDescripcionActividadMineraBean().getHerramientaMinera().setTipoObtencion(herramientaMinera.getCatalogoHerramienta().getProcesoMinero().getCatalogoTipoMaterial());

            getDescripcionActividadMineraBean().getHerramientaMinera().setProcesoMinero(herramientaMinera.getCatalogoHerramienta().getProcesoMinero());

            getDescripcionActividadMineraBean().getHerramientaMinera().setCatalogoHerramienta(herramientaMinera.getCatalogoHerramienta());

            getDescripcionActividadMineraBean().getHerramientaMinera().getCatalogoHerramienta().setProcesoMinero(herramientaMinera.getCatalogoHerramienta().getProcesoMinero());
            getDescripcionActividadMineraBean().getHerramientaMinera().getCatalogoHerramienta().getProcesoMinero().setCatalogoTipoMaterial(herramientaMinera.getCatalogoHerramienta().getProcesoMinero().getCatalogoTipoMaterial());
            getDescripcionActividadMineraBean().setOperacion(operacion);
            cargarProcesos();
            cargarHerramientas();
        } catch (Exception e) {
            LOG.error("Error al seleccionar herramienta", e);
        }
    }

    public void eliminarHerramienta(HerramientaMinera herramientaMinera) {
        HerramientaMinera herramienta = getDescripcionActividadMineraBean().getListaHerramientas().get(herramientaMinera.getIndice());
        try {
            if (herramienta.getId() != null) {
                herramienta.setEstado(false);
                getDescripcionActividadMineraBean().getListaHerramientasEliminadas().add(herramienta);
            }
            getDescripcionActividadMineraBean().getListaHerramientas().remove(herramientaMinera.getIndice());
            reasignarIndiceHerramienta();
        } catch (Exception e) {
            LOG.info("Error al intentar remover herramienta minera", e);
        }
    }

    /**
     * Carga instalaciones para mineria
     * @return
     */
    public List<CatalogoInstalacion> cargarInstalaciones() {
        List<CatalogoInstalacion> lista;
        try {
            lista = descripcionActividadMineriaFacade.getCatalogoInstalaciones(
                    getDescripcionActividadMineraBean().getFichaAmbientalMineria().
                    getProyectoLicenciamientoAmbiental().getCatalogoCategoria().getTipoSubsector().getCodigo());
        } catch (Exception e) {
            LOG.error("Error al cargar instalaciones mineras", e);
            lista = null;
        }
        return lista;
    }

    /**
     * Lista instalaciones asociadas a una ficha minera
     * @return
     */
    private List<Instalacion> listarInstalacionPorFicha() {
        List<Instalacion> lista;
        try {
            lista = descripcionActividadMineriaFacade.listarInstalacionesPorFichaAmbiental(getDescripcionActividadMineraBean().getFichaAmbientalMineria());
        } catch (Exception e) {
            LOG.error("Error al listar instalaciones guardadas", e);
            lista = null;
        }
        return lista;
    }

    public void agregarInstalacion() {
        try {
            List<String> mensajes = new ArrayList<String>();
            if (getDescripcionActividadMineraBean().getInstalacion().getCatalogoInstalacion() == null) {
                mensajes.add("El campo 'Instalación' es requerido.");
            }
            if (getDescripcionActividadMineraBean().getInstalacion().getDescripcion() == "") {
                mensajes.add("El campo 'Descripción' es requerido.");
            }
            validarInstalaciones(mensajes);
        } catch (Exception e) {
            LOG.error("Error al agregar una actividad minera.", e);
            JsfUtil.addMessageError(JsfUtil.ERROR_GUARDAR_REGISTRO);
        }
    }

    private void validarInstalaciones(List<String> mensajes) {
        if (mensajes.isEmpty()) {
            if (getDescripcionActividadMineraBean().getOperacion() == 0) {
                if (!validarInstalacion()) {
                    getDescripcionActividadMineraBean().getInstalacion().setFichaAmbientalMineria(descripcionActividadMineraBean.getFichaAmbientalMineria());
                    getDescripcionActividadMineraBean().getListaInstalaciones().add(getDescripcionActividadMineraBean().getInstalacion());
                    reasignarIndiceInstalacion();
                    getDescripcionActividadMineraBean().setInstalacion(new Instalacion());
                } else {
                    JsfUtil.addMessageError("La instalación ya fue agregada");
                }
            } else {
                getDescripcionActividadMineraBean().getListaInstalaciones().set(getDescripcionActividadMineraBean().getInstalacion().getIndice(), getDescripcionActividadMineraBean().getInstalacion());
                getDescripcionActividadMineraBean().setInstalacion(new Instalacion());
                getDescripcionActividadMineraBean().setOperacion(0);
            }
        } else {
            JsfUtil.addMessageError(mensajes);
        }
    }

    private boolean validarInstalacion() {
        boolean retorno = false;
        if (getDescripcionActividadMineraBean().getListaInstalaciones() != null
                && !getDescripcionActividadMineraBean().getListaInstalaciones().isEmpty()) {
            for (Instalacion a : getDescripcionActividadMineraBean().getListaInstalaciones()) {
                if (a.getCatalogoInstalacion().getNombre().equals(getDescripcionActividadMineraBean().getInstalacion().getCatalogoInstalacion().getNombre())) {
                    retorno = true;
                    break;
                }
            }
        }
        return retorno;
    }

    public void editarInstalacion(Instalacion instalacion) {
        getDescripcionActividadMineraBean().setInstalacion(instalacion);
        getDescripcionActividadMineraBean().setOperacion(OPERACION);
    }

    public void eliminarInstalacion(Instalacion instalacion) {
        Instalacion i = getDescripcionActividadMineraBean().getListaInstalaciones().get(instalacion.getIndice());
        try {
            if (i.getId() != null) {
                i.setEstado(false);
                getDescripcionActividadMineraBean().getListaInstalacionesEliminadas().add(i);
            }
            getDescripcionActividadMineraBean().getListaInstalaciones().remove(instalacion.getIndice());
            reasignarIndiceInstalacion();
        } catch (Exception e) {
            LOG.info("Error al intentar remover instalacion minera", e);
        }
    }

    private void reasignarIndiceInstalacion() {
        int i = 0;
        for (Instalacion intalacion : getDescripcionActividadMineraBean().getListaInstalaciones()) {
            intalacion.setIndice(i);
            i++;
        }
    }
    
    private void catalogoUnidadMedida(){
    	try {
			getDescripcionActividadMineraBean().setCatalogoUnidadMedida(descripcionActividadMineriaFacade.catalogoUnidadMedida());
		} catch (ServiceException e) {
            LOG.error("Error al cargar unidades de medida", e);
        } catch (RuntimeException e) {
            LOG.error("Error al cargar unidades de medida", e);
        }
    }

    private void cargarDescripcion() {
        try {
            getDescripcionActividadMineraBean().setDescripcionActividadMineria(descripcionActividadMineriaFacade.obtenerPorFichaMineria(getDescripcionActividadMineraBean().getFichaAmbientalMineria()));
            if (getDescripcionActividadMineraBean().getDescripcionActividadMineria() == null) {
                getDescripcionActividadMineraBean().setDescripcionActividadMineria(new DescripcionActividadMineria());
            }
        } catch (ServiceException e) {
            LOG.error(e, e);
        } catch (RuntimeException e) {
            LOG.error(e, e);
        }
    }

    public void guardar() {
        try {
            if (validarRegistro()) {
                getDescripcionActividadMineraBean().getDescripcionActividadMineria().setFichaAmbientalMineria(getDescripcionActividadMineraBean().getFichaAmbientalMineria());
                List<FichaMineriaInsumos> listaInsumos = new ArrayList<FichaMineriaInsumos>();
                listaInsumos.addAll(getDescripcionActividadMineraBean().getListaInsumosAgregados());
                listaInsumos.addAll(getDescripcionActividadMineraBean().getListaInsumosEliminados());

                List<ActividadMinera> listaActividadMineras = new ArrayList<ActividadMinera>();
                listaActividadMineras.addAll(getDescripcionActividadMineraBean().getListaActividadesSeleccionadas());
                listaActividadMineras.addAll(getDescripcionActividadMineraBean().getListaActividadesEliminadas());

                List<Instalacion> listaInstalaciones = new ArrayList<Instalacion>();
                listaInstalaciones.addAll(getDescripcionActividadMineraBean().getListaInstalaciones());
                listaInstalaciones.addAll(getDescripcionActividadMineraBean().getListaInstalacionesEliminadas());

                List<HerramientaMinera> listaHerramientas = new ArrayList<HerramientaMinera>();
                listaHerramientas.addAll(getDescripcionActividadMineraBean().getListaHerramientas());
                listaHerramientas.addAll(getDescripcionActividadMineraBean().getListaHerramientasEliminadas());

                getDescripcionActividadMineraBean().getFichaAmbientalMineria().setValidarDescripcionActividad(true);
                fichaAmbientalMineriaFacade.guardarFicha(getDescripcionActividadMineraBean().getFichaAmbientalMineria());
//                descripcionActividadMineriaFacade.guardar(getDescripcionActividadMineraBean().getDescripcionActividadMineria(),
//                        listaActividadMineras, listaInstalaciones, listaHerramientas, listaInsumos);
                //nuevo codigo de guardar para historial
                descripcionActividadMineriaFacade.guardarConHistorial(getDescripcionActividadMineraBean().getDescripcionActividadMineria(),
                        listaActividadMineras, listaInstalaciones, listaHerramientas, listaInsumos);
                
                setDescripcionActividadMineraBean(null);
                cargarDatos();
                JsfUtil.addMessageInfo(JsfUtil.REGISTRO_GUARDADO);
            }
        } catch (ServiceException e) {
            JsfUtil.addMessageError(JsfUtil.ERROR_GUARDAR_REGISTRO + " " + e.getMessage());
            LOG.error(e, e);
        } catch (RuntimeException e) {
            JsfUtil.addMessageError(JsfUtil.ERROR_GUARDAR_REGISTRO + " " + e.getMessage());
            LOG.error(e, e);
        }
    }

    public boolean validarRegistro() {
        List<String> listaMensajes = new ArrayList<String>();
        if (getDescripcionActividadMineraBean().getDescripcionActividadMineria().getNumeroPersonasLaboran() == null) {
            listaMensajes.add("El campo 'N°. de personas que laboraran' es requerido.");
        }
        if (getDescripcionActividadMineraBean().getDescripcionActividadMineria().getMontoInversion() == null) {
            listaMensajes.add("El campo 'Monto de la inversión' es requerido.");
        }
        if (getDescripcionActividadMineraBean().getDescripcionActividadMineria().getVolumenProduccionDiario() == null || getDescripcionActividadMineraBean().getDescripcionActividadMineria().getVolumenProduccionDiario().isEmpty()) {
            listaMensajes.add("El campo 'Volumen de producción diario (metros cúbicos)' es requerido.");
        }
        if(mineriaentes()){
        if (getDescripcionActividadMineraBean().getDescripcionActividadMineria().getNombrePlantaBeneficio() == null || getDescripcionActividadMineraBean().getDescripcionActividadMineria().getNombrePlantaBeneficio().isEmpty()) {
            listaMensajes.add("El campo 'Nombre de la planta beneficio' es requerido.");
        }
        }
        if (getDescripcionActividadMineraBean().getDescripcionActividadMineria().getNumeroObservacionLicenciaAmbiental() == null || getDescripcionActividadMineraBean().getDescripcionActividadMineria().getNumeroObservacionLicenciaAmbiental().isEmpty()) {
            listaMensajes.add("El campo '" + getDescripcionActividadMineraBean().getEtiqueta() + "' es requerido.");
        }
        if (getDescripcionActividadMineraBean().getListaActividadesSeleccionadas() == null || getDescripcionActividadMineraBean().getListaActividadesSeleccionadas().isEmpty()) {
            listaMensajes.add("Debe ingresar las actividades del proceso.");
        }
        return validarRegistroAux(listaMensajes);
    }

    private boolean validarRegistroAux(List<String> listaMensajes) {
        if (getDescripcionActividadMineraBean().getListaInstalaciones() == null || getDescripcionActividadMineraBean().getListaInstalaciones().isEmpty()) {
            listaMensajes.add("Debe ingresar las instalaciones utilizadas.");
        }
        if (getDescripcionActividadMineraBean().getListaHerramientas() == null || getDescripcionActividadMineraBean().getListaHerramientas().isEmpty()) {
            listaMensajes.add("Debe ingresar la maquinaria, herramientas y equipos utilizados.");
        }
        if (getDescripcionActividadMineraBean().getListaInsumosAgregados() == null || getDescripcionActividadMineraBean().getListaInsumosAgregados().isEmpty()) {
            listaMensajes.add("Debe ingresar los insumos utilizados.");
        }
        if (listaMensajes.isEmpty()) {
            return true;
        } else {
            JsfUtil.addMessageError(listaMensajes);
            return false;
        }

    }

    public void cancelar() {

    }

    public void cambiarEtiqueta() {
        getDescripcionActividadMineraBean().getDescripcionActividadMineria().setNumeroObservacionLicenciaAmbiental(null);
        if ("Si".equalsIgnoreCase(getTieneLicenciaAmbiental())) {
            getDescripcionActividadMineraBean().getDescripcionActividadMineria().setTieneLicenciaAmbiental(true);
            getDescripcionActividadMineraBean().setEtiqueta(NUMERO_LICENCIA);
        } else {
            getDescripcionActividadMineraBean().getDescripcionActividadMineria().setTieneLicenciaAmbiental(false);
            getDescripcionActividadMineraBean().setEtiqueta(OBSERVACION);
        }
    }

    public boolean mineriaentes() {
    	return !getDescripcionActividadMineraBean().getFichaAmbientalMineria().
        getProyectoLicenciamientoAmbiental().getCatalogoCategoria().getCodigo().equals("21.02.01.02");

	}
    
    /**
     * Cris F: metodo para encontrar el historial
     */
    @Getter
    @Setter
    private List<DescripcionActividadMineria> listaDescripcionActividadMineriaHistorial;
    @Getter
    @Setter
    private List<ActividadMinera> listaActividadMineraHistorial;    
    @Getter
    @Setter
    private List<HerramientaMinera> listaHerramientaMineraHistorial, listaHerramientaMineraModificados, listaHerramientaMineraEliminados;
    @Getter
    @Setter
    private List<Instalacion> listaInstalacionesHistorial, listaInstalacionesModificadas, listaInstalacionesEliminadas;
    @Getter
    @Setter
    private List<ActividadMinera> listaActividadMineraModificados, listaActividadMineraEliminados;
    @Getter
    @Setter
    private List<FichaMineriaInsumos> listaFichaMineriaInsumosHistorial, listaFichaMineriaInsumosModificados, listaFichaMineriaInsumosEliminados; 
    
    private void cargarHistorial(){
    	try {   		
			listaDescripcionActividadMineriaHistorial = descripcionActividadMineriaFacade.obtenerPorFichaMineriaHistorial(descripcionActividadMineraBean.getFichaAmbientalMineria());
			if(listaDescripcionActividadMineriaHistorial == null)
				listaDescripcionActividadMineriaHistorial = new ArrayList<DescripcionActividadMineria>();
				
			//Actividad Minera
			listaActividadMineraHistorial = actividadMineriaFacade.listarPorFichaAmbientalHistorial(descripcionActividadMineraBean.getFichaAmbientalMineria());
			
			if(listaActividadMineraHistorial != null && !listaActividadMineraHistorial.isEmpty()){
				obtenerActividadMineraHistorial(getDescripcionActividadMineraBean().getListaActividadesSeleccionadas(), listaActividadMineraHistorial);
			}
			
			for(ActividadMinera actividad : getDescripcionActividadMineraBean().getListaActividadesSeleccionadas()){
				if(actividad.getFechaHistorico() != null && actividad.getIdRegistroOriginal() == null){
					actividad.setNuevoEnModificacion(true);
				}
				
				if(listaActividadMineraModificados != null && !listaActividadMineraModificados.isEmpty()){
					if(obtenerActividadMineraModificado(actividad)){
						actividad.setRegistroModificado(true);
					}
				}
			}
			//fin Actividad Minera
			
			//Herramientas
			List<HerramientaMinera> listaHerramientasHistorico = descripcionActividadMineriaFacade.listarHerramientaPorFichaAmbientalHistorial(descripcionActividadMineraBean.getFichaAmbientalMineria());
			
			if(listaHerramientasHistorico != null && !listaHerramientasHistorico.isEmpty()){
				obtenerHerramientasHistorial(getDescripcionActividadMineraBean().getListaHerramientas(), listaHerramientasHistorico);
			}
			
			for(HerramientaMinera herramienta : getDescripcionActividadMineraBean().getListaHerramientas()){
				if(herramienta.getFechaHistorico() != null && herramienta.getIdRegistroOriginal() == null){
					herramienta.setNuevoEnModificacion(true);
				}
				
				if(listaHerramientaMineraModificados != null && !listaHerramientaMineraModificados.isEmpty()){
					if(obtenerHerramientaMineraModificada(herramienta)){
						herramienta.setRegistroModificado(true);
					}
				}				
			}
			//Fin herramientas
			
			//Instalaciones
			List<Instalacion> listaInstalacionHistorico = descripcionActividadMineriaFacade.listarInstalacionesPorFichaAmbientalHistorial(descripcionActividadMineraBean.getFichaAmbientalMineria());
			
			if(listaInstalacionHistorico != null && !listaInstalacionHistorico.isEmpty()){
				obtenerInstalacionHistorial(getDescripcionActividadMineraBean().getListaInstalaciones(), listaInstalacionHistorico);
			}
			
			for(Instalacion instalacion : getDescripcionActividadMineraBean().getListaInstalaciones()){
				if(instalacion.getFechaHistorico() != null && instalacion.getIdRegistroOriginal() == null){
					instalacion.setNuevoEnModificacion(true);
				}
				
				if(listaInstalacionesModificadas != null && !listaInstalacionesModificadas.isEmpty()){
					if(obtenerInstalacionModificada(instalacion)){
						instalacion.setRegistroModificado(true);
					}
				}
			}
			//Fin instalaciones
			
			//Insumos
			List<FichaMineriaInsumos> listaInsumosHistorico = descripcionActividadMineriaFacade.listarPorFichaHistorial(descripcionActividadMineraBean.getFichaAmbientalMineria());
			
			if(listaInsumosHistorico != null && !listaInsumosHistorico.isEmpty()){
				obtenerInsumosHistorial(getDescripcionActividadMineraBean().getListaInsumosAgregados(), listaInsumosHistorico);
			}
			
			for(FichaMineriaInsumos insumo : getDescripcionActividadMineraBean().getListaInsumosAgregados()){
				if(insumo.getFechaHistorico() != null && insumo.getIdRegistroOriginal() == null){
					insumo.setNuevoEnModificacion(true);
				}
				
				if(listaFichaMineriaInsumosModificados != null && !listaFichaMineriaInsumosModificados.isEmpty()){
					if(obtenerInsumosModificado(insumo)){
						insumo.setRegistroModificado(true);
					}
				}
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    private void obtenerActividadMineraHistorial(List<ActividadMinera> listaActividadMineraActual, List<ActividadMinera> listaActividadMineraHistorial){
    	listaActividadMineraModificados = new ArrayList<ActividadMinera>();
    	listaActividadMineraEliminados = new ArrayList<ActividadMinera>();
    	
    	for(ActividadMinera actividadHistorial : listaActividadMineraHistorial){
    		Comparator<ActividadMinera> c = new Comparator<ActividadMinera>() {
    			
    			@Override
    			public int compare(ActividadMinera a1, ActividadMinera a2) {				
    				return a1.getId().compareTo(a2.getId());
    			}
    		};
    		
    		Collections.sort(listaActividadMineraActual,c);
    		
    		int index = Collections.binarySearch(listaActividadMineraActual, new ActividadMinera(actividadHistorial.getIdRegistroOriginal()), c);
    		if(index >= 0)
    			listaActividadMineraModificados.add(actividadHistorial);
    		else
    			listaActividadMineraEliminados.add(actividadHistorial);
    	}    	
    }
    
    private boolean obtenerActividadMineraModificado(ActividadMinera actividadModificado){
    	boolean existeModificacion = false;
    	
    	for(ActividadMinera actividad : listaActividadMineraModificados){
    		if(actividad.getIdRegistroOriginal() != null){
    			if(actividad.getIdRegistroOriginal().equals(actividadModificado.getId())){
    				existeModificacion = true;
    				break;
    			}
    		}
    	}
    	
    	return existeModificacion;
    }
    
    private void obtenerHerramientasHistorial(List<HerramientaMinera> listaHerramientasActual, List<HerramientaMinera> listaHerramientasHistorial){
    	listaHerramientaMineraModificados = new ArrayList<HerramientaMinera>();
    	listaHerramientaMineraEliminados = new ArrayList<HerramientaMinera>();
    	
    	for(HerramientaMinera herramientaHistorial : listaHerramientasHistorial){
    		Comparator<HerramientaMinera> c = new Comparator<HerramientaMinera>() {
				
				@Override
				public int compare(HerramientaMinera h1, HerramientaMinera h2) {
					return h1.getId().compareTo(h2.getId());
				}
			};
			Collections.sort(listaHerramientasActual, c);
			
			int index = Collections.binarySearch(listaHerramientasActual, new HerramientaMinera(herramientaHistorial.getIdRegistroOriginal()), c);
			
			if(index >= 0){
				listaHerramientaMineraModificados.add(herramientaHistorial);				
			}else
				listaHerramientaMineraEliminados.add(herramientaHistorial);
    	}
    }
    
    private boolean obtenerHerramientaMineraModificada(HerramientaMinera herramientaModificada){
    	boolean existeModificacion = false;
    	
    	for(HerramientaMinera herramienta : listaHerramientaMineraModificados){
    		if(herramienta.getIdRegistroOriginal() != null){
    			if(herramienta.getIdRegistroOriginal().equals(herramientaModificada.getId())){
    				existeModificacion = true;
    				break;
    			}
    		}
    	}    	
    	return existeModificacion;
    }
    
    private void obtenerInstalacionHistorial(List<Instalacion> listaInstalacionesActual, List<Instalacion> listaInstalacionHistorial){
    	listaInstalacionesModificadas = new ArrayList<Instalacion>();
    	listaInstalacionesEliminadas = new ArrayList<Instalacion>();
    	
    	for(Instalacion instalacionHistorial : listaInstalacionHistorial){
    		Comparator<Instalacion> c = new Comparator<Instalacion>() {
				
				@Override
				public int compare(Instalacion o1, Instalacion o2) {					
					return o1.getId().compareTo(o2.getId());
				}
			};
			Collections.sort(listaInstalacionesActual, c);
			
			int index = Collections.binarySearch(listaInstalacionesActual, new Instalacion(instalacionHistorial.getIdRegistroOriginal()), c);
			
			if(index >= 0){
				listaInstalacionesModificadas.add(instalacionHistorial);				
			}else{
				listaInstalacionesEliminadas.add(instalacionHistorial);
			}
    	}
    }
    
    private boolean obtenerInstalacionModificada(Instalacion instalacionModificada){
    	boolean existeModificacion = false;
    	
    	for(Instalacion instalacion: listaInstalacionesModificadas){
    		if(instalacion.getIdRegistroOriginal() != null){
    			if(instalacion.getIdRegistroOriginal().equals(instalacionModificada.getId())){
    				existeModificacion = true;
    				break;
    			}
    		}
    	}
    	return existeModificacion;
    }
    
    private void obtenerInsumosHistorial(List<FichaMineriaInsumos> listaInsumosActual, List<FichaMineriaInsumos> listaInsumoHistorial){
    	listaFichaMineriaInsumosModificados = new ArrayList<FichaMineriaInsumos>();
    	listaFichaMineriaInsumosEliminados = new ArrayList<FichaMineriaInsumos>();
    	
    	for(FichaMineriaInsumos insumoHistorial : listaInsumoHistorial){
    		Comparator<FichaMineriaInsumos> c = new Comparator<FichaMineriaInsumos>() {
				
				@Override
				public int compare(FichaMineriaInsumos m1, FichaMineriaInsumos m2) {					
					return m1.getId().compareTo(m2.getId());
				}
			};
			Collections.sort(listaInsumosActual, c);
			
			int index = Collections.binarySearch(listaInsumosActual, new FichaMineriaInsumos(insumoHistorial.getIdRegistroOriginal()), c);
			
			if(index >= 0){
				listaFichaMineriaInsumosModificados.add(insumoHistorial);
			}else{
				listaFichaMineriaInsumosEliminados.add(insumoHistorial);
			}
    	}
    }
    
    private boolean obtenerInsumosModificado(FichaMineriaInsumos insumoModificado){
    	boolean existeModificacion = false;
    	
    	for(FichaMineriaInsumos insumo : listaFichaMineriaInsumosModificados){
    		if(insumo.getIdRegistroOriginal() != null){
    			if(insumo.getIdRegistroOriginal().equals(insumoModificado.getId())){
    				existeModificacion = true;
    				break;
    			}
    		}
    	}
    	return existeModificacion;
    }
    
    public void obtenerHistoricoActividad(ActividadMinera actividadModificada){   
    	listaActividadMineraHistorial = new ArrayList<ActividadMinera>();
    	
    	for(ActividadMinera actividadMod : listaActividadMineraModificados){
    		if(actividadModificada.getId().equals(actividadMod.getIdRegistroOriginal())){
    			listaActividadMineraHistorial.add(actividadMod);
    		}
    	}
    	RequestContext.getCurrentInstance().update("tblActividadesHistorial");
		RequestContext.getCurrentInstance().execute("PF('actividadesDlg').show()");
    }
    
    public void obtenerListaActividad(){
    	listaActividadMineraHistorial = new ArrayList<ActividadMinera>();
    	listaActividadMineraHistorial = listaActividadMineraEliminados;
    	RequestContext.getCurrentInstance().update("tblActividadesHistorial");
		RequestContext.getCurrentInstance().execute("PF('actividadesDlg').show()");
    }
    
    public void obtenerHistoricoHerramienta(HerramientaMinera herramientaModificada){
    	listaHerramientaMineraHistorial = new ArrayList<HerramientaMinera>();
    	
    	for(HerramientaMinera herramientaMod : listaHerramientaMineraModificados){
    		if(herramientaModificada.getId().equals(herramientaMod.getIdRegistroOriginal())){
    			listaHerramientaMineraHistorial.add(herramientaMod);
    		}
    	}
    	
    	RequestContext.getCurrentInstance().update("tblHerramientasHistorial");
		RequestContext.getCurrentInstance().execute("PF('herramientasDlg').show()");
    }
    
    public void obtenerListaHerramientasMineras(){
    	listaHerramientaMineraHistorial = new ArrayList<HerramientaMinera>();
    	listaHerramientaMineraHistorial = listaHerramientaMineraEliminados;
    	
    	RequestContext.getCurrentInstance().update("tblHerramientasHistorial");
		RequestContext.getCurrentInstance().execute("PF('herramientasDlg').show()");
    }
    
    public void obtenerHistoricoInstalacion(Instalacion instalacionModificada){
    	listaInstalacionesHistorial = new ArrayList<Instalacion>();
    	
    	for(Instalacion instalacionMod : listaInstalacionesModificadas){
    		if(instalacionModificada.getId().equals(instalacionMod.getIdRegistroOriginal())){
    			listaInstalacionesHistorial.add(instalacionMod);
    		}
    	}
    	
    	RequestContext.getCurrentInstance().update("tblInstalacionesHistorial");
		RequestContext.getCurrentInstance().execute("PF('instalacionesDlg').show()");
    }
    
    public void obtenerListaInstalaciones(){
    	listaInstalacionesHistorial = new ArrayList<Instalacion>();
    	listaInstalacionesHistorial = listaInstalacionesEliminadas;
    	
    	RequestContext.getCurrentInstance().update("tblInstalacionesHistorial");
		RequestContext.getCurrentInstance().execute("PF('instalacionesDlg').show()");
    }
    
    public void obtenerHistoricoFichaMineriaInsumo(FichaMineriaInsumos insumoModificado){
    	listaFichaMineriaInsumosHistorial = new ArrayList<FichaMineriaInsumos>();
    	
    	for(FichaMineriaInsumos insumoMod : listaFichaMineriaInsumosModificados){
    		if(insumoModificado.getId().equals(insumoMod.getIdRegistroOriginal())){
    			listaFichaMineriaInsumosHistorial.add(insumoMod);
    		}
    	}
    	
    	RequestContext.getCurrentInstance().update("tblInsumosHistorial");
		RequestContext.getCurrentInstance().execute("PF('insumosDlg').show()");
    }
    
    public void obtenerListaFichaMineriaInsumos(){
    	listaFichaMineriaInsumosHistorial = new ArrayList<FichaMineriaInsumos>();
    	listaFichaMineriaInsumosHistorial = listaFichaMineriaInsumosEliminados;
    	
    	RequestContext.getCurrentInstance().update("tblInsumosHistorial");
		RequestContext.getCurrentInstance().execute("PF('insumosDlg').show()");    	
    }
    
}
