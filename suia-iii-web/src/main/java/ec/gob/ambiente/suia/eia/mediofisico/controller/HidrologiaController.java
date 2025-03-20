package ec.gob.ambiente.suia.eia.mediofisico.controller;

import ec.gob.ambiente.suia.catalogos.facade.CatalogoUsoFacade;
import ec.gob.ambiente.suia.domain.*;
import ec.gob.ambiente.suia.domain.enums.TipoCategoriaCuerpoHidrico;
import ec.gob.ambiente.suia.eia.mediofisico.bean.CuerpoHidricoBean;
import ec.gob.ambiente.suia.eia.mediofisico.facade.CalidadComponenteFacade;
import ec.gob.ambiente.suia.eia.mediofisico.facade.CuerpoHidricoFacade;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.validacionseccion.facade.ValidacionSeccionesFacade;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@ViewScoped
@ManagedBean
public class HidrologiaController implements Serializable {

    private static final long serialVersionUID = 8113289863726317470L;
    private static final Logger LOG = Logger
            .getLogger(HidrologiaController.class);
    @EJB
    private CuerpoHidricoFacade cuerpoHidricoFacade;
    @EJB
    private CatalogoUsoFacade catalogoUsoFacade;
    @EJB
    private CalidadComponenteFacade calidadComponenteFacade;
    @Getter
    @Setter
    private CuerpoHidricoBean cuerpoHidricoBean;
    @Getter
    @Setter
    private List<CatalogoUso> listaCatalogoUso;

    @EJB
    private ValidacionSeccionesFacade validacionSeccionesFacade;

    /**
     *
     */
    @PostConstruct
    public void inicio() {
        cuerpoHidricoBean = new CuerpoHidricoBean();
        EstudioImpactoAmbiental es = (EstudioImpactoAmbiental) JsfUtil.devolverObjetoSession(Constantes.SESSION_EIA_OBJECT);
        cuerpoHidricoBean.setEstudioImpactoAmbiental(es);

        cuerpoHidricoBean.setCuerpoHidrico(new CuerpoHidrico());
        cuerpoHidricoBean.setListaEntidadesRemover(new ArrayList());

        recuperarCuerpoHidrico();
        cargarCatalogoUsos();

    }

    private void inicializarUsosSeleccionados() {

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
    }

    private void recuperarCuerpoHidrico() {
        try {
            cuerpoHidricoBean.setListaCuerposHidricos(cuerpoHidricoFacade.cuerpoHidricoXEiaId(cuerpoHidricoBean.getEstudioImpactoAmbiental()));
        } catch (Exception e) {
            LOG.error(e, e);
        }
    }

    /**
     *
     */
    public void agregarCuerpoHidrico() {
        cuerpoHidricoBean.setCuerpoHidrico(new CuerpoHidrico());
        cuerpoHidricoBean.getCuerpoHidrico().setCoordenadaGeneralList(new ArrayList<CoordenadaGeneral>());
        cuerpoHidricoBean.getCuerpoHidrico().setUsosCuerposHidricos(new ArrayList<UsoCuerpoHidrico>());
        cuerpoHidricoBean.getCuerpoHidrico().setEstudioImpactoAmbiental(cuerpoHidricoBean.getEstudioImpactoAmbiental());
        inicializarUsosSeleccionados();
    }

    /**
     *
     */
    public void seleccionarCuerpoHidrico(CuerpoHidrico cuerpoHidrico) {
        cuerpoHidrico.setEditar(true);
        cuerpoHidricoBean.setCuerpoHidrico(cuerpoHidrico);
        inicializarUsosSeleccionados();
    }


    /**
     *
     *
     */
    public void removerCuerpoHidrico(CuerpoHidrico cuerpoHidrico) {
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
    }

    /**
     *
     */
    public void guardarCuerpoHidrico() {
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
    }

    private void actualizarUsosCuerposHidricos() {
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
    }

    private Boolean buscarCatalogoUsoGuardado(CatalogoUso catalogoUso) {

        for (UsoCuerpoHidrico usoCuerpoHidrico : cuerpoHidricoBean.getCuerpoHidrico().getUsosCuerposHidricos()) {
            if (usoCuerpoHidrico.getCatalogoUso().getId().equals(catalogoUso.getId()))
                return true;
        }
        return false;
    }

    private void buscarCatalogosUsosEliminados() {

        for (UsoCuerpoHidrico usoCuerpoHidrico : cuerpoHidricoBean.getCuerpoHidrico().getUsosCuerposHidricos()) {
            boolean encontrado = false;
            for (CatalogoUso catalogoUso : cuerpoHidricoBean.getListaUsos()) {
                if (usoCuerpoHidrico.getCatalogoUso().getId().equals(catalogoUso.getId())) {
                    encontrado = true;
                    break;
                }
            }
            if (!encontrado) {
                cuerpoHidricoBean.getListaEntidadesRemover().add(usoCuerpoHidrico);
            }
        }
    }

    /**
     *
     */
    public void cerrarCuerpoHidrico() {
        try {

            JsfUtil.addCallbackParam("addCuerpoHidrico");
            cuerpoHidricoBean.setListaCuerposHidricos(new ArrayList<CuerpoHidrico>());

        } catch (Exception e) {
            JsfUtil.addMessageError(e.getMessage());
            LOG.error(e, e);
        }
    }

//    private void reasignarIndiceCuerpoHidrico() {
//        int i = 0;
//        for (CuerpoHidrico in : cuerpoHidricoBean.getListaCuerposHidricos()) {
//            in.setIndice(i);
//            i++;
//        }
//    }


    /**
     *
     */
    public void agregarCoordenada() {
        cuerpoHidricoBean.setCoordenadaGeneral(new CoordenadaGeneral());
    }

    /**
     * @param coordenada
     */
    public void seleccionarCoordenada(CoordenadaGeneral coordenada) {
        coordenada.setEditar(true);
        cuerpoHidricoBean.setCoordenadaGeneral(coordenada);
    }

    /**
     * @param coordenada
     */
    public void removerCoordenada(CoordenadaGeneral coordenada) {
        try {
            cuerpoHidricoBean.getCuerpoHidrico().getCoordenadaGeneralList().remove(coordenada.getIndice());
            if (coordenada.getId() != null) {
                coordenada.setEstado(false);
                cuerpoHidricoBean.getListaEntidadesRemover().add(coordenada);
            }

        } catch (Exception e) {
            JsfUtil.addMessageError(e.getMessage());
            LOG.error(e, e);
        }
    }

    /**
     *
     */
    public void guardarCoordenada() {
        try {

            if (!cuerpoHidricoBean.getCoordenadaGeneral().isEditar()) {
                cuerpoHidricoBean.getCuerpoHidrico().getCoordenadaGeneralList().add(cuerpoHidricoBean.getCoordenadaGeneral());
            }

            JsfUtil.addCallbackParam("addCoordenadaCH");
        } catch (Exception e) {
            JsfUtil.addMessageError(e.getMessage());
            LOG.error(e, e);
        }
    }

    private void validarGuardarCoordenadas() throws ServiceException {

        if (cuerpoHidricoBean.getCuerpoHidrico().getCoordenadaGeneralList() == null || cuerpoHidricoBean.getCuerpoHidrico().getCoordenadaGeneralList().isEmpty()) {
            throw new ServiceException("Debe ingresar las coordenadas.");
        } else {
            if (cuerpoHidricoBean.getCuerpoHidrico().getCoordenadaGeneralList().size() < 2) {
                throw new ServiceException("Debe ingresar 2 coordenadas.");
            }
        }
    }

    public String cargarTipoCategoriaCuerpoHidrico(Integer indice) {
        return TipoCategoriaCuerpoHidrico.values()[indice-1].getDescripcion();
    }

    /**
     *
     */
    public void guardar() {
        try {
            //cuerpoHidricoBean.getCuerpoHidrico().setEstudioImpactoAmbiental(cuerpoHidricoBean.getEstudioImpactoAmbiental());

            cuerpoHidricoFacade.guardar(cuerpoHidricoBean.getListaCuerposHidricos(), cuerpoHidricoBean.getListaEntidadesRemover());

            recuperarCuerpoHidrico();

            validacionSeccionesFacade.guardarValidacionSeccion("EIA",
                    "hidrologia", cuerpoHidricoBean
                            .getEstudioImpactoAmbiental().getId()
                            .toString());

            JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
        } catch (ServiceException e) {

            JsfUtil.addMessageError(e.getMessage());
            e.printStackTrace();
            LOG.error(e, e);
        }
    }

    private void cargarCatalogoUsos() {
        listaCatalogoUso = new ArrayList<CatalogoUso>();
        listaCatalogoUso.addAll(catalogoUsoFacade.obtenerCatalogosUsosByNormativa());
    }

    public void cerrar() {
        try {

            JsfUtil.addCallbackParam("addCuerpoHidrico");

        } catch (Exception e) {
            JsfUtil.addMessageError(e.getMessage());
            LOG.error(e, e);
        }
    }


}



