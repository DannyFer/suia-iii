/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.eia.fauna.controller;

import ec.gob.ambiente.suia.catalogos.facade.CatalogoGeneralFacade;
import ec.gob.ambiente.suia.domain.CatalogoGeneral;
import ec.gob.ambiente.suia.domain.CoordenadaGeneral;
import ec.gob.ambiente.suia.domain.DetalleFauna;
import ec.gob.ambiente.suia.domain.DetalleFaunaEspecies;
import ec.gob.ambiente.suia.domain.DetalleFaunaSumaEspecies;
import ec.gob.ambiente.suia.domain.EiaOpciones;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.domain.Fauna;
import ec.gob.ambiente.suia.domain.PuntosMuestreo;
import ec.gob.ambiente.suia.domain.TipoCatalogo;
import ec.gob.ambiente.suia.dto.EntityAdjunto;
import ec.gob.ambiente.suia.eia.fauna.bean.FaunaBean;
import ec.gob.ambiente.suia.eia.fauna.facade.FaunaFacade;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletResponse;
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
public class FaunaController implements Serializable {

    private static final long serialVersionUID = -8694066038556272678L;

    @EJB
    private CatalogoGeneralFacade catalogoGeneralFacade;
    @EJB
    private FaunaFacade faunaFacade;

    @Getter
    @Setter
    private FaunaBean faunaBean;
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
            .getLogger(FaunaController.class);
    private static final int CUALITATIVOS_TAB = 0;
    private static final int CUANTITATIVOS_TAB = 1;
    private static final int RATIO = 1048576;
    @Getter
    @Setter
    @ManagedProperty(value = "#{combosFaunaController}")
    private CombosFaunaController combosFaunaController;
    @Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;
    private static final BigDecimal SIN_PUNTO = BigDecimal.ZERO;

    @PostConstruct
    private void cargarDatos() {
        try {
            setFaunaBean(new FaunaBean());
            getFaunaBean().iniciarDatos();
            getFaunaBean().setListaGruposTaxonomicos(catalogoGeneralFacade.obtenerCatalogoXTipo(TipoCatalogo.GRUPOS_TAXONOMICOS));
        } catch (Exception e) {
            LOG.error(e , e);
        }
    }

    private void iniciarEntityFaunaAdjunto() {
        getFaunaBean().getFauna().setAdjunto(new EntityAdjunto());
    }

    /**
     *
     * @param catalogoGeneral
     */
    public void seleccionarGrupoTaxonomico(CatalogoGeneral catalogoGeneral) {
        try {
            getFaunaBean().setGrupoTaxonomicoSeleccionado(catalogoGeneral);
            EstudioImpactoAmbiental es = (EstudioImpactoAmbiental) JsfUtil.devolverObjetoSession(Constantes.SESSION_EIA_OBJECT);
            getFaunaBean().setEstudioImpactoAmbiental(es);
            getFaunaBean().setFauna(faunaFacade.obtenerPorIdPorPinUsuario(loginBean.getUsuario().getPin(),
                    getFaunaBean().getEstudioImpactoAmbiental(), getFaunaBean().getGrupoTaxonomicoSeleccionado()));
            if (getFaunaBean().getFauna() != null && getFaunaBean().getFauna().getId() != null) {
                iniciarEntityFaunaAdjunto();
                getFaunaBean().setIdTipoMuestreo(getFaunaBean().getFauna().getIdCatalogoTipoMuestro().toString());
                getFaunaBean().getFauna().getAdjunto().setNombre("Descargar");
                cargarTablas();
            } else {
                getFaunaBean().setFauna(new Fauna());
                iniciarEntityFaunaAdjunto();
            }
        } catch (ServiceException e) {
            LOG.error(e , e);
        }
    }

    private void cargarRecuperarFauna() {
        for (PuntosMuestreo pm : getFaunaBean().getFauna().getPuntosMuestreoCollection()) {
            if (pm.getIndiceTab() == 0) {
                getFaunaBean().getListaPuntosMuestreo().add(pm);
                getFaunaBean().getListaDetalleFauna().addAll(pm.getDetailFaunaCollection());
            } else {
                getFaunaBean().getListaPuntosMuestreo1().add(pm);
                getFaunaBean().getListaDetalleFauna1().addAll(pm.getDetailFaunaCollection());
            }
        }
        if (getFaunaBean().getIdMacroinvertebradosAcuaticos() != getFaunaBean().getGrupoTaxonomicoSeleccionado().getId().intValue()) {
            Integer tipoMuestreo = Integer.valueOf(getFaunaBean().getIdTipoMuestreo());
            switch (tipoMuestreo) {
                case CatalogoGeneral.CUALITATIVO:
                    getFaunaBean().setListaCodigoPunto(new ArrayList<SelectItem>());
                    cargarListaPuntoCombo(getFaunaBean().getListaCodigoPunto(), getFaunaBean().getListaPuntosMuestreo());
                    reasignarIndiceCoordenada();
                    reasignarIndiceDetalle();
                    break;
                case CatalogoGeneral.CUANTITATIVO:
                    getFaunaBean().setListaCodigoPunto1(new ArrayList<SelectItem>());
                    cargarListaPuntoCombo(getFaunaBean().getListaCodigoPunto1(), getFaunaBean().getListaPuntosMuestreo1());
                    cargarTablaDetalleFaunaEspecie();
                    reasignarIndiceCoordenada1();
                    reasignarIndiceDetalle1();
                    break;
                default:
                    getFaunaBean().setListaCodigoPunto(new ArrayList<SelectItem>());
                    getFaunaBean().setListaCodigoPunto1(new ArrayList<SelectItem>());
                    cargarListaPuntoCombo(getFaunaBean().getListaCodigoPunto(), getFaunaBean().getListaPuntosMuestreo());
                    cargarListaPuntoCombo(getFaunaBean().getListaCodigoPunto1(), getFaunaBean().getListaPuntosMuestreo1());
                    cargarTablaDetalleFaunaEspecie();
                    reasignarIndiceCoordenada();
                    reasignarIndiceCoordenada1();
                    reasignarIndiceDetalle();
                    reasignarIndiceDetalle1();
            }
        } else {
            getFaunaBean().setListaCodigoPunto1(new ArrayList<SelectItem>());
            cargarListaPuntoCombo(getFaunaBean().getListaCodigoPunto1(), getFaunaBean().getListaPuntosMuestreo1());
            List<DetalleFaunaSumaEspecies> lista = new ArrayList<DetalleFaunaSumaEspecies>(faunaBean.getFauna().getDetalleFaunaSumaEspeciesCollection());
            getFaunaBean().setDetalleFaunaSumaEspecies(lista.get(0));
            getFaunaBean().setListaDetalleFaunaEspecies(new ArrayList<DetalleFaunaEspecies>(lista.get(0).getDetailFaunaSpeciesCollection()));
            reasignarIndiceCoordenada1();
            reasignarIndiceDetalle1();
            cargarTablaDetalleMacroinvertebrados();
            getFaunaBean().setIdTipoMuestreo("" + CatalogoGeneral.CUANTITATIVO);
        }
    }

    private void cargarTablaDetalleMacroinvertebrados() {
        for (DetalleFauna df : getFaunaBean().getListaDetalleFauna1()) {
            for (DetalleFaunaEspecies dfe : getFaunaBean().getListaDetalleFaunaEspecies()) {
                if (df.getIdPuntoMuestreo().equals(dfe.getPuntosMuestreoIds()) && df.getEspecie().equals(dfe.getEspecie())) {
                    df.setDetalleFaunaEspecies(dfe);
                    break;
                }
            }
        }
    }

    /**
     *
     */
    public void cargarTablas() {
        Integer tipoMuestreo = Integer.valueOf(getFaunaBean().getIdTipoMuestreo());
        getFaunaBean().getFauna().setCatalogoGruposTaxonomicos(getFaunaBean().getGrupoTaxonomicoSeleccionado());
        getFaunaBean().getFauna().setCatalogoTipoMuestreo(new CatalogoGeneral(tipoMuestreo));
        cargarCombosPorGrupoTaxanomico();
        if (getFaunaBean().getIdMacroinvertebradosAcuaticos() != getFaunaBean().getGrupoTaxonomicoSeleccionado().getId().intValue()) {
            switch (tipoMuestreo) {
                case CatalogoGeneral.CUALITATIVO:
                    getFaunaBean().setIndiceTab(CUALITATIVOS_TAB);
                    iniciarListasCualitativo();
                    break;
                case CatalogoGeneral.CUANTITATIVO:
                    getFaunaBean().setIndiceTab(CUANTITATIVOS_TAB);
                    iniciarListasCuantitativo();
                    break;
                default:
                    getFaunaBean().setIndiceTab(CUALITATIVOS_TAB);
                    iniciarListasCuantitativo();
                    iniciarListasCualitativo();
            }
        } else {
            getFaunaBean().setIndiceTab(CUANTITATIVOS_TAB);
            iniciarListasCuantitativo();
            getFaunaBean().setListaDetalleFaunaEspecies(new ArrayList<DetalleFaunaEspecies>());
            getFaunaBean().setIdTipoMuestreo("" + CatalogoGeneral.CUANTITATIVO);
        }
        if (getFaunaBean().getFauna().getId() != null) {
            cargarRecuperarFauna();
        }

    }

    private void iniciarListasCualitativo() {
        getFaunaBean().setListaPuntosMuestreo(new ArrayList<PuntosMuestreo>());
        getFaunaBean().setListaDetalleFauna(new ArrayList<DetalleFauna>());
        getFaunaBean().setListaPuntosMuestreoEditar(new ArrayList<PuntosMuestreo>());
        getFaunaBean().setListaDetalleFaunaEditar(new ArrayList<DetalleFauna>());
    }

    private void iniciarListasCuantitativo() {
        getFaunaBean().setDetalleFaunaSumaEspecies(new DetalleFaunaSumaEspecies());
        getFaunaBean().setListaPuntosMuestreo1(new ArrayList<PuntosMuestreo>());
        getFaunaBean().setListaDetalleFauna1(new ArrayList<DetalleFauna>());
        getFaunaBean().setListaPuntosMuestreo1Editar(new ArrayList<PuntosMuestreo>());
        getFaunaBean().setListaDetalleFauna1Editar(new ArrayList<DetalleFauna>());
    }

    /**
     *
     * @param event
     */
    public void handleFileUpload(FileUploadEvent event) {
        getFaunaBean().getFauna().getAdjunto().setNombre(event.getFile().getFileName());
        getFaunaBean().getFauna().getAdjunto().setArchivo(event.getFile().getContents());
        getFaunaBean().getFauna().getAdjunto().setMimeType(event.getFile().getContentType());
    }

    /**
     *
     */
    public void descargarAdjunto() {
        try {
            EntityAdjunto adj = faunaFacade.recuperarAdjunto(Fauna.class.getSimpleName(),
                    getFaunaBean().getFauna().getId());
            dowload(adj.getArchivo(), adj.getMimeType());
        } catch (Exception e) {
            LOG.error(e , e);
        }
    }

    /**
     *
     * @param adjunto
     * @param contentType
     * @throws IOException
     */
    public void dowload(byte[] adjunto, final String contentType) throws IOException {
        HttpServletResponse response = getResponse();
        response.reset();
        response.setContentType(contentType);
        InputStream in = new ByteArrayInputStream(adjunto);
        byte[] byteArray = new byte[RATIO];
        int size = 0;
        while ((size = in.read(byteArray)) != -1) {
            response.getOutputStream().
                    write(byteArray, 0, size);
        }
        getContext().responseComplete();
    }

    /**
     *
     * @return
     */
    public HttpServletResponse getResponse() {
        return (HttpServletResponse) getContext().getExternalContext().getResponse();

    }

    /**
     *
     * @return
     */
    protected FacesContext getContext() {
        return FacesContext.getCurrentInstance();
    }

    /**
     *
     */
    public void guardarCualitativo() {
        try {
            validarGuardarCualitativo();
            cargarFaunaGuardar();
            Map<String, EiaOpciones> mapOpciones = (Map<String, EiaOpciones>) JsfUtil.devolverObjetoSession(Constantes.SESSION_OPCIONES_EIA);
            faunaFacade.guardarFauna(getFaunaBean().getFauna(), getFaunaBean().getListaPuntosMuestreo(),
                    getFaunaBean().getListaDetalleFauna(),
                    getFaunaBean().getListaPuntosMuestreoEditar(), getFaunaBean().getListaDetalleFaunaEditar(),
                    mapOpciones.get(EiaOpciones.FAUNA_HIDRO));
            setFaunaBean(null);
            cargarDatos();
            JsfUtil.addMessageInfo(JsfUtil.REGISTRO_ACTUALIZADO);
        } catch (Exception e) {
            JsfUtil.addMessageError(e.getMessage());
            LOG.error(e , e);
        }

    }

    private void cargarFaunaGuardar() {
        getFaunaBean().getFauna().setEstudioImpactoAmbiental(getFaunaBean().getEstudioImpactoAmbiental());
        getFaunaBean().getFauna().setCatalogoPisoZoogeografico(getFaunaBean().getFauna().getIdCatalogoPisoZoogeografico() != null ? new CatalogoGeneral(getFaunaBean().getFauna().getIdCatalogoPisoZoogeografico()) : null);
        getFaunaBean().getFauna().setCatalogoCoberturaVegetal(getFaunaBean().getFauna().getIdCatalogoCoberturaVegetal() != null ? new CatalogoGeneral(getFaunaBean().getFauna().getIdCatalogoCoberturaVegetal()) : null);
        getFaunaBean().getFauna().setCatalogoZonasIctiohidrograficas(getFaunaBean().getFauna().getIdCatalogoZonasIctiohidrograficas() != null ? new CatalogoGeneral(getFaunaBean().getFauna().getIdCatalogoZonasIctiohidrograficas()) : null);
    }

    private void validarGuardarCualitativo() throws ServiceException {
        List<String> listaMsg = new ArrayList<String>();
        if (getFaunaBean().getFauna().getId() == null && getFaunaBean().getFauna().getAdjunto().getArchivo() == null) {
            listaMsg.add("Debe adjuntar Metodología aplicada.");
        }
        if (getFaunaBean().getIdTipoMuestreo().isEmpty()) {
            listaMsg.add("Debe seleccionar el tipo de muestreo.");
        }
        if (getFaunaBean().getListaPuntosMuestreo().isEmpty()) {
            listaMsg.add("Debe ingresar los puntos de muestreo.");
        }
        if (getFaunaBean().getListaDetalleFauna().isEmpty()) {
            listaMsg.add("Debe ingresar los individuos.");
        }
        if (getFaunaBean().getFauna().getEsfuerzoMuestreo() == null || getFaunaBean().getFauna().getEsfuerzoMuestreo().isEmpty()) {
            listaMsg.add("Debe ingresar el campo Esfuerzo de muestreo.");
        }
        if (!listaMsg.isEmpty()) {
            JsfUtil.addMessageError(listaMsg);
            throw new ServiceException();
        }
    }

    /**
     *
     */
    public void guardarCuantitativo() {
        try {
            validarGuardarCuantitativo();
            for (DetalleFaunaEspecies dfe : getFaunaBean().getListaDetalleFaunaEspecies()) {
                dfe.setDfseId(getFaunaBean().getDetalleFaunaSumaEspecies());
            }
            getFaunaBean().getDetalleFaunaSumaEspecies().setDetailFaunaSpeciesCollection(getFaunaBean().getListaDetalleFaunaEspecies());
            cargarFaunaGuardar();
            Map<String, EiaOpciones> mapOpciones = (Map<String, EiaOpciones>) JsfUtil.devolverObjetoSession(Constantes.SESSION_OPCIONES_EIA);
            faunaFacade.guardarFauna(getFaunaBean().getFauna(), getFaunaBean().getListaPuntosMuestreo1(),
                    getFaunaBean().getListaDetalleFauna1(), getFaunaBean().getDetalleFaunaSumaEspecies(),
                    getFaunaBean().getListaPuntosMuestreo1Editar(), getFaunaBean().getListaDetalleFauna1Editar(),
                    mapOpciones.get(EiaOpciones.FAUNA_HIDRO));
            setFaunaBean(null);
            cargarDatos();
            JsfUtil.addMessageInfo(JsfUtil.REGISTRO_ACTUALIZADO);
        } catch (Exception e) {
            JsfUtil.addMessageError(e.getMessage());
            LOG.error(e , e);
        }

    }

    /**
     *
     */
    public void guardarCuantitativMacroinvertebrados() {
        try {
            validarGuardarCuantitativo();
            for (DetalleFauna df : getFaunaBean().getListaDetalleFauna1()) {
                df.getDetalleFaunaEspecies().setPuntosMuestreoIds(df.getIdPuntoMuestreo());
                df.getDetalleFaunaEspecies().setDfseId(getFaunaBean().getDetalleFaunaSumaEspecies());
                df.getDetalleFaunaEspecies().setEspecie(df.getEspecie());
                getFaunaBean().getListaDetalleFaunaEspecies().add(df.getDetalleFaunaEspecies());
            }
            getFaunaBean().getDetalleFaunaSumaEspecies().setDetailFaunaSpeciesCollection(getFaunaBean().getListaDetalleFaunaEspecies());
            cargarFaunaGuardar();
            Map<String, EiaOpciones> mapOpciones = (Map<String, EiaOpciones>) JsfUtil.devolverObjetoSession(Constantes.SESSION_OPCIONES_EIA);
            faunaFacade.guardarFauna(getFaunaBean().getFauna(), getFaunaBean().getListaPuntosMuestreo1(),
                    getFaunaBean().getListaDetalleFauna1(), getFaunaBean().getDetalleFaunaSumaEspecies(),
                    getFaunaBean().getListaPuntosMuestreo1Editar(), getFaunaBean().getListaDetalleFauna1Editar(),
                    mapOpciones.get(EiaOpciones.FAUNA_HIDRO));
            setFaunaBean(null);
            cargarDatos();
            JsfUtil.addMessageInfo(JsfUtil.REGISTRO_ACTUALIZADO);
        } catch (Exception e) {
            JsfUtil.addMessageError(e.getMessage());
            LOG.error(e , e);
        }

    }

    private void validarGuardarCuantitativo() throws ServiceException {
        List<String> listaMsg = new ArrayList<String>();
        if (getFaunaBean().getFauna().getId() == null && getFaunaBean().getFauna().getAdjunto().getArchivo() == null) {
            listaMsg.add("Debe adjuntar Metodología aplicada.");
        }
        if (getFaunaBean().getIdTipoMuestreo().isEmpty()) {
            listaMsg.add("Debe seleccionar el tipo de muestreo.");
        }
        if (getFaunaBean().getListaPuntosMuestreo1().isEmpty()) {
            listaMsg.add("Debe ingresar los puntos de muestreo.");
        }
        if (getFaunaBean().getListaDetalleFauna1().isEmpty()) {
            listaMsg.add("Debe ingresar los individuos.");
        }
        if (getFaunaBean().getIdMacroinvertebradosAcuaticos() != getFaunaBean().getGrupoTaxonomicoSeleccionado().getId().intValue()
                && (getFaunaBean().getListaDetalleFaunaEspecies() == null || getFaunaBean().getListaDetalleFaunaEspecies().isEmpty())) {
            listaMsg.add("Debe generar la tabla de cuantitativo por especie.");
        }
        if (getFaunaBean().getDetalleFaunaSumaEspecies().getSumaRiqueza().isEmpty()) {
            listaMsg.add("Debe ingresar la suma total de riqueza.");
        }
        if (getFaunaBean().getDetalleFaunaSumaEspecies().getSumaAbundancia().isEmpty()) {
            listaMsg.add("Debe ingresar la suma total de abundancia.");
        }
        if (getFaunaBean().getDetalleFaunaSumaEspecies().getSumaShannon().isEmpty()) {
            listaMsg.add("Debe ingresar Shannon.");
        }
        if (getFaunaBean().getDetalleFaunaSumaEspecies().getSumaSimpson().isEmpty()) {
            listaMsg.add("Debe ingresar Simpson.");
        }

        if (!listaMsg.isEmpty()) {
            JsfUtil.addMessageError(listaMsg);
            throw new ServiceException();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Seccion Coordenada">
    /**
     *
     */
    public void guardarCoordenada() {
        try {
            validarCoordenada(getFaunaBean().getPuntosMuestreo());
            RequestContext context = RequestContext.getCurrentInstance();
            if (!getFaunaBean().getPuntosMuestreo().isEditar()) {
                getFaunaBean().getListaPuntosMuestreo().add(getFaunaBean().getPuntosMuestreo());
            }
            getFaunaBean().setListaCodigoPunto(new ArrayList<SelectItem>());
            cargarListaPuntoCombo(getFaunaBean().getListaCodigoPunto(), getFaunaBean().getListaPuntosMuestreo());
            reasignarIndiceCoordenada();
            context.addCallbackParam("puntoIn", true);
        } catch (Exception e) {
            LOG.error(e , e);
        }
    }

    /**
     *
     */
    public void guardarCoordenada1() {
        try {
            validarCoordenada(getFaunaBean().getPuntosMuestreo1());
            RequestContext context = RequestContext.getCurrentInstance();
            if (!getFaunaBean().getPuntosMuestreo1().isEditar()) {
                getFaunaBean().getListaPuntosMuestreo1().add(getFaunaBean().getPuntosMuestreo1());
            }
            getFaunaBean().setListaCodigoPunto1(new ArrayList<SelectItem>());
            cargarListaPuntoCombo(getFaunaBean().getListaCodigoPunto1(), getFaunaBean().getListaPuntosMuestreo1());
            reasignarIndiceCoordenada1();
            context.addCallbackParam("puntoIn1", true);
        } catch (Exception e) {
            LOG.error(e , e);
        }
    }

    private void validarCoordenada(PuntosMuestreo puntosMuestreo) {
        puntosMuestreo.getCoordenada().setDescripcion("0;" + loginBean.getUsuario().getPin());
        puntosMuestreo.getCoordenada().setNombreTabla(PuntosMuestreo.class.getSimpleName());
        puntosMuestreo.getCoordenada1().setDescripcion("1;" + loginBean.getUsuario().getPin());
        puntosMuestreo.getCoordenada1().setNombreTabla(PuntosMuestreo.class.getSimpleName());
        puntosMuestreo.getCoordenada1().setX(SIN_PUNTO.equals(puntosMuestreo.getCoordenada1().getX())
                ? null : puntosMuestreo.getCoordenada1().getX());
        puntosMuestreo.getCoordenada1().setY(SIN_PUNTO.equals(puntosMuestreo.getCoordenada1().getY())
                ? null : puntosMuestreo.getCoordenada1().getY());
        puntosMuestreo.getCoordenada1().setZ(SIN_PUNTO.equals(puntosMuestreo.getCoordenada1().getZ())
                ? null : puntosMuestreo.getCoordenada1().getZ());
    }

    private void cargarListaPuntoCombo(List<SelectItem> listaPuntosItem, List<PuntosMuestreo> listaPuntosMuestreo) {
        for (PuntosMuestreo pm : listaPuntosMuestreo) {
            listaPuntosItem.add(new SelectItem(pm.getCodigo(), pm.getCodigo()));
        }
    }

    /**
     *
     * @param idTab
     */
    public void agregarPuntoMuestreo(Integer idTab) {
        if (idTab == 0) {
            getFaunaBean().setPuntosMuestreo(new PuntosMuestreo());
            agragarCoordenadas(getFaunaBean().getPuntosMuestreo());
            getFaunaBean().getPuntosMuestreo().setIndiceTab(idTab);
        } else {
            getFaunaBean().setPuntosMuestreo1(new PuntosMuestreo());
            agragarCoordenadas(getFaunaBean().getPuntosMuestreo1());
            getFaunaBean().getPuntosMuestreo1().setIndiceTab(idTab);
        }
    }

    private void agragarCoordenadas(PuntosMuestreo puntosMuestreo) {
        puntosMuestreo.setFauna(getFaunaBean().getFauna());
        puntosMuestreo.setCoordenada(new CoordenadaGeneral());
        puntosMuestreo.setCoordenada1(new CoordenadaGeneral());
    }

    /**
     *
     * @param puntosMuestreo
     */
    public void seleccionarCoordenada(PuntosMuestreo puntosMuestreo) {
        puntosMuestreo.setEditar(true);
        getFaunaBean().setPuntosMuestreo(puntosMuestreo);
        if (getFaunaBean().getPuntosMuestreo().getCoordenada1() == null) {
            getFaunaBean().getPuntosMuestreo().setCoordenada1(new CoordenadaGeneral());
        }
    }

    /**
     *
     * @param puntosMuestreo
     */
    public void removerCoordeenada(PuntosMuestreo puntosMuestreo) {
        try {
            getFaunaBean().getListaPuntosMuestreo().remove(puntosMuestreo.getIndice());
            if (puntosMuestreo.getId() != null) {
                puntosMuestreo.setEstado(false);
                getFaunaBean().getListaPuntosMuestreoEditar().add(puntosMuestreo);
            }
            reasignarIndiceCoordenada();
        } catch (Exception e) {
            JsfUtil.addMessageError(e.getMessage());
            LOG.error(e , e);
        }
    }

    private void reasignarIndiceCoordenada() {
        int i = 0;
        for (PuntosMuestreo pm : getFaunaBean().getListaPuntosMuestreo()) {
            pm.setIndice(i);
            i++;
        }
    }

    /**
     *
     * @param puntosMuestreo
     */
    public void seleccionarCoordenada1(PuntosMuestreo puntosMuestreo) {
        puntosMuestreo.setEditar(true);
        getFaunaBean().setPuntosMuestreo1(puntosMuestreo);
        if (getFaunaBean().getPuntosMuestreo1().getCoordenada1() == null) {
            getFaunaBean().getPuntosMuestreo1().setCoordenada1(new CoordenadaGeneral());
        }
    }

    /**
     *
     * @param puntosMuestreo
     */
    public void removerCoordeenada1(PuntosMuestreo puntosMuestreo) {
        try {
            getFaunaBean().getListaPuntosMuestreo1().remove(puntosMuestreo.getIndice());
            if (puntosMuestreo.getId() != null) {
                puntosMuestreo.setEstado(false);
                getFaunaBean().getListaPuntosMuestreo1Editar().add(puntosMuestreo);
            }
            reasignarIndiceCoordenada1();
        } catch (Exception e) {
            JsfUtil.addMessageError(e.getMessage());
            LOG.error(e , e);
        }
    }

    private void reasignarIndiceCoordenada1() {
        int i = 0;
        for (PuntosMuestreo pm : getFaunaBean().getListaPuntosMuestreo1()) {
            pm.setIndice(i);
            i++;
        }
    }

    // </editor-fold> 
    // <editor-fold defaultstate="collapsed" desc="Seccion Individuo">
    private void cargarCombosPorGrupoTaxanomico() {
        combosFaunaController.cargarListaTipoRegistro(getFaunaBean().getGrupoTaxonomicoSeleccionado().getDescripcion().toUpperCase());
        combosFaunaController.cargarListaDistribucionVerticalEspecie(getFaunaBean().getGrupoTaxonomicoSeleccionado().getDescripcion().toUpperCase());
        combosFaunaController.cargarListaGremioAlimenticio(getFaunaBean().getGrupoTaxonomicoSeleccionado().getDescripcion().toUpperCase());
        combosFaunaController.cargarListaEspeciesMigratorias(getFaunaBean().getGrupoTaxonomicoSeleccionado().getDescripcion().toUpperCase());
        combosFaunaController.cargarListaUso(getFaunaBean().getGrupoTaxonomicoSeleccionado().getDescripcion().toUpperCase());
        if (getFaunaBean().getGrupoTaxonomicoSeleccionado().getId().intValue() == getFaunaBean().getIdHerpetofauna()) {
            combosFaunaController.cargarListaModosReproduccion(getFaunaBean().getGrupoTaxonomicoSeleccionado().getDescripcion().toUpperCase());
        }
        if (getFaunaBean().getGrupoTaxonomicoSeleccionado().getId().intValue() == getFaunaBean().getIdIctiofauna() || getFaunaBean().getGrupoTaxonomicoSeleccionado().getId().intValue() == getFaunaBean().getIdMacroinvertebradosAcuaticos()) {
            combosFaunaController.cargarListaSistemasHidrograficos();
            combosFaunaController.cargarListaDistribucionColumnaAgua(getFaunaBean().getGrupoTaxonomicoSeleccionado().getDescripcion().toUpperCase());
        }
        if (getFaunaBean().getGrupoTaxonomicoSeleccionado().getId().intValue() == getFaunaBean().getIdMacroinvertebradosAcuaticos()) {
            combosFaunaController.cargarListaGruposTroficos();
            combosFaunaController.cargarListaHabitosAlimenticios();
            combosFaunaController.cargarListaEspecieBioindicadoraCalidadAgua();
        }
    }

    /**
     *
     * @param idTab
     */
    public void agregarIndividuo(Integer idTab) {
        if (idTab == 0) {
            getFaunaBean().setDetalleFauna(new DetalleFauna());
            getFaunaBean().getDetalleFauna().setNumeroRegistro(getFaunaBean().getListaDetalleFauna().size() + 1);
            cargarCatalogosDetalleFauna(getFaunaBean().getDetalleFauna());
        } else {
            getFaunaBean().setDetalleFauna1(new DetalleFauna());
            getFaunaBean().getDetalleFauna1().setDetalleFaunaEspecies(new DetalleFaunaEspecies());
            getFaunaBean().getDetalleFauna1().setNumeroRegistro(getFaunaBean().getListaDetalleFauna1().size() + 1);
            cargarCatalogosDetalleFauna(getFaunaBean().getDetalleFauna1());
        }

    }

    private void cargarCatalogosDetalleFauna(DetalleFauna detalleFauna) {
        detalleFauna.setIdOtrosRastrosTipoRegistro(String.valueOf(CatalogoGeneral.OTROS_RASTROS_TIPO_REGISTRO));
        detalleFauna.setIdOtrosDistribucionVerticalEspecie(String.valueOf(CatalogoGeneral.OTROS_DISTRIBUCION_VERTICAL_ESPECIE));
        detalleFauna.setIdOtrosComportamientoSocial(String.valueOf(CatalogoGeneral.OTROS_COMPORTAMIENTO_SOCIAL));
        detalleFauna.setIdOtrosGremioAlimenticio(String.valueOf(CatalogoGeneral.OTROS_GREMIO_ALIMENTICIO));
        detalleFauna.setAdjunto(new EntityAdjunto());
    }

    /**
     *
     */
    public void agregarTablaIndividuo() {
        RequestContext context = RequestContext.getCurrentInstance();
        setCatalogosDetalleFauna(getFaunaBean().getDetalleFauna());
        if (!getFaunaBean().getDetalleFauna().isEditar()) {
            getFaunaBean().getListaDetalleFauna().add(getFaunaBean().getDetalleFauna());
        }
        reasignarIndiceDetalle();
        context.addCallbackParam("puntoIndividuo", true);
    }

    /**
     *
     */
    public void agregarTablaIndividuo1() {
        RequestContext context = RequestContext.getCurrentInstance();
        setCatalogosDetalleFauna(getFaunaBean().getDetalleFauna1());
        if (!getFaunaBean().getDetalleFauna1().isEditar()) {
            getFaunaBean().getListaDetalleFauna1().add(getFaunaBean().getDetalleFauna1());
        }
        reasignarIndiceDetalle1();
        context.addCallbackParam("puntoIndividuo1", true);
    }

    private void setCatalogosDetalleFauna(DetalleFauna detalleFauna) {
        detalleFauna.setCatalogoCites(detalleFauna.getIdCites() != null ? new CatalogoGeneral(detalleFauna.getIdCites()) : null);
        detalleFauna.setCatalogoColectasIncidentales(detalleFauna.getIdColectasIncidentales() != null ? new CatalogoGeneral(detalleFauna.getIdColectasIncidentales()) : null);
        detalleFauna.setCatalogoEspecieEndemica(detalleFauna.getIdEspecieEndemica() != null ? new CatalogoGeneral(detalleFauna.getIdEspecieEndemica()) : null);
        detalleFauna.setCatalogoComportamiento(detalleFauna.getIdComportamientoSocial() != null ? new CatalogoGeneral(detalleFauna.getIdComportamientoSocial()) : null);
        detalleFauna.setCatalogoCondicionesClimaticas(detalleFauna.getIdCondicionesClimaticas() != null ? new CatalogoGeneral(detalleFauna.getIdCondicionesClimaticas()) : null);
        detalleFauna.setCatalogoDistribucionEspecies(detalleFauna.getIdDistribucionVerticalEspecie() != null ? new CatalogoGeneral(detalleFauna.getIdDistribucionVerticalEspecie()) : null);
        detalleFauna.setCatalogoEspeciesBioindicadoras(detalleFauna.getIdEspeciesBioindicadoras() != null ? new CatalogoGeneral(detalleFauna.getIdEspeciesBioindicadoras()) : null);
        detalleFauna.setCatalogoEspeciesMigratorias(detalleFauna.getIdEspeciesMigratorias() != null ? new CatalogoGeneral(detalleFauna.getIdEspeciesMigratorias()) : null);
        detalleFauna.setCatalogoFaseLunar(detalleFauna.getIdFaseLunar() != null ? new CatalogoGeneral(detalleFauna.getIdFaseLunar()) : null);
        detalleFauna.setCatalogoGremioAlimenticio(detalleFauna.getIdGremioAlimenticio() != null ? new CatalogoGeneral(detalleFauna.getIdGremioAlimenticio()) : null);
        detalleFauna.setCatalogoLibroRojoEcuador(detalleFauna.getIdLibroRojoEcuador() != null ? new CatalogoGeneral(detalleFauna.getIdLibroRojoEcuador()) : null);
        detalleFauna.setCatalogoPatronActividad(detalleFauna.getIdPatronActividad() != null ? new CatalogoGeneral(detalleFauna.getIdPatronActividad()) : null);
        detalleFauna.setCatalogoSensibilidad(detalleFauna.getIdSensibilidad() != null ? new CatalogoGeneral(detalleFauna.getIdSensibilidad()) : null);
        detalleFauna.setCatalogoTipoRegistro(detalleFauna.getIdTipoRegistro() != null ? new CatalogoGeneral(detalleFauna.getIdTipoRegistro()) : null);
        detalleFauna.setCatalogoTipoVegetacion(detalleFauna.getIdTipoVegetacion() != null ? new CatalogoGeneral(detalleFauna.getIdTipoVegetacion()) : null);
        detalleFauna.setCatalogoUicnInternacional(detalleFauna.getIdUicnInternacional() != null ? new CatalogoGeneral(detalleFauna.getIdUicnInternacional()) : null);
        detalleFauna.setCatalogoUso(detalleFauna.getIdUso() != null ? new CatalogoGeneral(detalleFauna.getIdUso()) : null);
        detalleFauna.setCatalogoModoReproductivo(detalleFauna.getIdModoReproductivo() != null ? new CatalogoGeneral(detalleFauna.getIdModoReproductivo()) : null);
        detalleFauna.setCatalogoSistemasHidrograficos(detalleFauna.getIdSistemasHidrograficos() != null ? new CatalogoGeneral(detalleFauna.getIdSistemasHidrograficos()) : null);
        detalleFauna.setCatalogoDistribucionColumnaAgua(detalleFauna.getIdDistribucionColumnaAgua() != null ? new CatalogoGeneral(detalleFauna.getIdDistribucionColumnaAgua()) : null);
        detalleFauna.setCatalogoEpocaDelAnio(detalleFauna.getIdEpocaDelAnio() != null ? new CatalogoGeneral(detalleFauna.getIdEpocaDelAnio()) : null);
        detalleFauna.setCatalogoGruposTroficos(detalleFauna.getIdGruposTroficos() != null ? new CatalogoGeneral(detalleFauna.getIdGruposTroficos()) : null);
        detalleFauna.setCatalogoHabitoAlimenticio(detalleFauna.getIdHabitoAlimenticio() != null ? new CatalogoGeneral(detalleFauna.getIdHabitoAlimenticio()) : null);
        detalleFauna.setCatalogoEspecieBioindicadoraCalidadAgua(detalleFauna.getIdEspecieBioindicadoraCalidadAgua() != null ? new CatalogoGeneral(detalleFauna.getIdEspecieBioindicadoraCalidadAgua()) : null);
    }

    /**
     *
     * @param event
     */
    public void handleFileFotoCualitativoUpload(FileUploadEvent event) {
        if (getFaunaBean().getDetalleFauna().getAdjunto().getArchivo() != null) {
            getFaunaBean().getDetalleFauna().getAdjunto().setEditar(true);
        }
        getFaunaBean().getDetalleFauna().getAdjunto().setArchivo(event.getFile().getContents());
        getFaunaBean().getDetalleFauna().getAdjunto().setNombre(event.getFile().getFileName());
        getFaunaBean().getDetalleFauna().getAdjunto().setMimeType(event.getFile().getContentType());

    }

    /**
     *
     * @param detalleFauna
     */
    public void seleccionarDetalleFauna(DetalleFauna detalleFauna) {
        detalleFauna.setEditar(true);
        getFaunaBean().setDetalleFauna(detalleFauna);
    }

    /**
     *
     * @param detalleFauna
     */
    public void removerDetalleFuna(DetalleFauna detalleFauna) {
        try {
            getFaunaBean().getListaDetalleFauna().remove(detalleFauna);
            if (detalleFauna.getId() != null) {
                detalleFauna.setEstado(false);
                getFaunaBean().getListaDetalleFaunaEditar().add(detalleFauna);
            }
            reasignarIndiceDetalle();
        } catch (Exception e) {
            JsfUtil.addMessageError(e.getMessage());
            LOG.error(e , e);
        }
    }

    private void reasignarIndiceDetalle() {
        int i = 0;
        for (DetalleFauna df : getFaunaBean().getListaDetalleFauna()) {
            df.setIndice(i);
            df.setNumeroRegistro(i + 1);
            i++;
        }
    }

    /**
     *
     * @param event
     */
    public void handleFileFotoCuantitativoUpload(FileUploadEvent event) {
        if (getFaunaBean().getDetalleFauna1().getAdjunto().getArchivo() != null) {
            getFaunaBean().getDetalleFauna1().getAdjunto().setEditar(true);
        }
        getFaunaBean().getDetalleFauna1().getAdjunto().setArchivo(event.getFile().getContents());
        getFaunaBean().getDetalleFauna1().getAdjunto().setNombre(event.getFile().getFileName());
        getFaunaBean().getDetalleFauna1().getAdjunto().setMimeType(event.getFile().getContentType());

    }

    /**
     *
     * @param detalleFauna
     */
    public void seleccionarDetalleFauna1(DetalleFauna detalleFauna) {
        detalleFauna.setEditar(true);
        getFaunaBean().setDetalleFauna1(detalleFauna);
    }

    /**
     *
     * @param detalleFauna
     */
    public void removerDetalleFuna1(DetalleFauna detalleFauna) {
        try {
            getFaunaBean().getListaDetalleFauna1().remove(detalleFauna.getIndice());
            if (detalleFauna.getId() != null) {
                detalleFauna.setEstado(false);
                getFaunaBean().getListaDetalleFauna1Editar().add(detalleFauna);
            }
            reasignarIndiceDetalle1();
        } catch (Exception e) {
            JsfUtil.addMessageError(e.getMessage());
            LOG.error(e , e);
        }
    }

    private void reasignarIndiceDetalle1() {
        int i = 0;
        for (DetalleFauna df : getFaunaBean().getListaDetalleFauna1()) {
            df.setIndice(i);
            df.setNumeroRegistro(i + 1);
            i++;
        }
    }

    /**
     *
     */
    public void cargarOtrosTipoRegistro() {
        getFaunaBean().getDetalleFauna().setDescOtrosRastrosTipoRegistro(null);
    }

    /**
     *
     */
    public void cargarOtrosDistribucionVerticalEspecie() {
        getFaunaBean().getDetalleFauna().setDescOtrosDistribucionVerticalEspecie(null);
    }

    /**
     *
     */
    public void cargarOtrosComportamientoSocial() {
        getFaunaBean().getDetalleFauna().setDescOtrosComportamientoSocial(null);
    }

    /**
     *
     */
    public void cargarOtrosGremioAlimenticio() {
        getFaunaBean().getDetalleFauna().setDescOtrosGremioAlimenticio(null);
    }

    /**
     *
     */
    public void cargarTablaDetalleFaunaEspecie() {
        if (getFaunaBean().getFauna() != null && getFaunaBean().getFauna().getId() != null && getFaunaBean().getFauna().getDetalleFaunaSumaEspeciesCollection() != null && !getFaunaBean().getFauna().getDetalleFaunaSumaEspeciesCollection().isEmpty()) {
            List<DetalleFaunaSumaEspecies> lista = new ArrayList<DetalleFaunaSumaEspecies>(faunaBean.getFauna().getDetalleFaunaSumaEspeciesCollection());
            getFaunaBean().setDetalleFaunaSumaEspecies(lista.get(0));
            getFaunaBean().setListaDetalleFaunaEspecies(new ArrayList<DetalleFaunaEspecies>(lista.get(0).getDetailFaunaSpeciesCollection()));
        } else {
            getFaunaBean().setListaDetalleFaunaEspecies(new ArrayList<DetalleFaunaEspecies>());
        }
        List<DetalleFauna> listaDetalleFaunaCuantitavo = getFaunaBean().getListaDetalleFauna1();
        JsfUtil.ordenarLista(listaDetalleFaunaCuantitavo, "especie");
        Set<DetalleFauna> listaEspecieSinRepetir = new HashSet<DetalleFauna>(listaDetalleFaunaCuantitavo);
        if (getFaunaBean().getListaDetalleFaunaEspecies().isEmpty()) {
            for (DetalleFauna d : listaEspecieSinRepetir) {
                getFaunaBean().getListaDetalleFaunaEspecies().add(concatenar(d.getEspecie(), listaDetalleFaunaCuantitavo));
            }
        } else {
            List<DetalleFaunaEspecies> listaAux = new ArrayList<DetalleFaunaEspecies>();
            for (DetalleFauna de : listaEspecieSinRepetir) {
                int bandera = 0;
                for (DetalleFaunaEspecies d : getFaunaBean().getListaDetalleFaunaEspecies()) {
                    if (de.getEspecie().equals(d.getEspecie())) {
                        listaAux.add(concatenar(d, d.getEspecie(), listaDetalleFaunaCuantitavo));
                        bandera = 1;
                        break;
                    }
                }
                if (bandera == 0) {
                    listaAux.add(concatenar(de.getEspecie(), listaDetalleFaunaCuantitavo));
                }
            }

            getFaunaBean().setListaDetalleFaunaEspecies(listaAux);
        }
    }

    private DetalleFaunaEspecies concatenar(final String especie, final List<DetalleFauna> listaRepetidos) {
        DetalleFaunaEspecies obj = new DetalleFaunaEspecies();
        obj.setPuntosMuestreoIds("");
        obj.setOrdenConcatenado("");
        obj.setFamiliaConcatenado("");
        obj.setGeneroConcatenado("");
        obj.setNombreComunConcatenado("");
        obj.setCriterioSensibilidadConcatenado("");
        obj.setCriterioConcatenado("");
        obj.setEspecie(especie);
        obj.setCmbTipoRegistroConcatenado("");
        obj.setCmbTipoVegetacionConcatenado("");
        obj.setCmbCondicionesClimaticasConcatenado("");
        obj.setCmbComportamientoSocialConcatenado("");
        obj.setCmbGremioAlimenticioConcatenado("");
        obj.setCmbPatronActividadConcatenado("");
        obj.setCmbFaseLunarConcatenado("");
        obj.setCmbSensibilidadConcatenado("");
        obj.setCmbEspeciesMigratoriasConcatenado("");
        obj.setCmbEspeciesBioindicadorasConcatenado("");
        obj.setCmbUicnConcatenado("");
        obj.setCmbCitesConcatenado("");
        obj.setCmbLibroRojoConcatenado("");
        obj.setCmbDistribucionVerticalConcatenado("");
        obj.setCmbModoReproductivoConcatenado("");
        obj.setCmbUsoConcatenado("");
        obj.setCmbEndemicaConcatenado("");
        obj.setCmbSistemaHidrograficoConcatenado("");
        obj.setCmbDistribucionColumnaAguaConcatenado("");
        obj.setNumeroIndividuosConcatenado(0);
        String separador = ",";
        for (DetalleFauna d : listaRepetidos) {
            if (especie.equals(d.getEspecie())) {
                obj.setNumeroIndividuosConcatenado(obj.getNumeroIndividuosConcatenado() + 1);
                obj.setPuntosMuestreoIds(obj.getPuntosMuestreoIds() + d.getIdPuntoMuestreo() + separador);
                obj.setOrdenConcatenado(obj.getOrdenConcatenado() + d.getOrden() + separador);
                obj.setFamiliaConcatenado(obj.getFamiliaConcatenado() + d.getFamilia() + separador);
                obj.setGeneroConcatenado(obj.getGeneroConcatenado() + d.getGenero() + separador);
                obj.setNombreComunConcatenado(obj.getNombreComunConcatenado() + d.getNombreComun() + separador);
                obj.setCriterioSensibilidadConcatenado(obj.getCriterioSensibilidadConcatenado() + d.getCriterioSencibilidad() + separador);
                obj.setCriterioConcatenado(obj.getCriterioConcatenado() + d.getCriterio() + separador);
                obj.setCmbTipoRegistroConcatenado(obj.getCmbTipoRegistroConcatenado() + combosFaunaController.getMapaCatalogoGeneral().get(d.getIdTipoRegistro() == null ? "" : d.getIdTipoRegistro().toString()) + separador);
                obj.setCmbTipoVegetacionConcatenado(obj.getCmbTipoVegetacionConcatenado() + combosFaunaController.getMapaCatalogoGeneral().get(d.getIdTipoVegetacion() == null ? "" : d.getIdTipoVegetacion().toString()) + separador);
                obj.setCmbCondicionesClimaticasConcatenado(obj.getCmbCondicionesClimaticasConcatenado() + combosFaunaController.getMapaCatalogoGeneral().get(d.getIdCondicionesClimaticas() == null ? "" : d.getIdCondicionesClimaticas().toString()) + separador);
                obj.setCmbDistribucionVerticalConcatenado(obj.getCmbDistribucionVerticalConcatenado() + combosFaunaController.getMapaCatalogoGeneral().get(d.getIdDistribucionVerticalEspecie() == null ? "" : d.getIdDistribucionVerticalEspecie().toString()) + separador);
                obj.setCmbComportamientoSocialConcatenado(obj.getCmbComportamientoSocialConcatenado() + combosFaunaController.getMapaCatalogoGeneral().get(d.getIdComportamientoSocial() == null ? "" : d.getIdComportamientoSocial().toString()) + separador);
                obj.setCmbGremioAlimenticioConcatenado(obj.getCmbGremioAlimenticioConcatenado() + combosFaunaController.getMapaCatalogoGeneral().get(d.getIdGremioAlimenticio() == null ? "" : d.getIdGremioAlimenticio().toString()) + separador);
                obj.setCmbPatronActividadConcatenado(obj.getCmbPatronActividadConcatenado() + combosFaunaController.getMapaCatalogoGeneral().get(d.getIdPatronActividad() == null ? "" : d.getIdPatronActividad().toString()) + separador);
                obj.setCmbFaseLunarConcatenado(obj.getCmbFaseLunarConcatenado() + combosFaunaController.getMapaCatalogoGeneral().get(d.getIdFaseLunar() == null ? "" : d.getIdFaseLunar().toString()) + separador);
                obj.setCmbSensibilidadConcatenado(obj.getCmbSensibilidadConcatenado() + combosFaunaController.getMapaCatalogoGeneral().get(d.getIdSensibilidad() == null ? "" : d.getIdSensibilidad().toString()) + separador);
                obj.setCmbEspeciesMigratoriasConcatenado(obj.getCmbEspeciesMigratoriasConcatenado() + combosFaunaController.getMapaCatalogoGeneral().get(d.getIdEspeciesMigratorias() == null ? "" : d.getIdEspeciesMigratorias().toString()) + separador);
                obj.setCmbEspeciesBioindicadorasConcatenado(obj.getCmbEspeciesBioindicadorasConcatenado() + combosFaunaController.getMapaCatalogoGeneral().get(d.getIdEspeciesBioindicadoras() == null ? "" : d.getIdEspeciesBioindicadoras().toString()) + separador);
                obj.setCmbUicnConcatenado(obj.getCmbUicnConcatenado() + combosFaunaController.getMapaCatalogoGeneral().get(d.getIdUicnInternacional() == null ? "" : d.getIdUicnInternacional().toString()) + separador);
                obj.setCmbCitesConcatenado(obj.getCmbCitesConcatenado() + combosFaunaController.getMapaCatalogoGeneral().get(d.getIdCites() == null ? "" : d.getIdCites().toString()) + separador);
                obj.setCmbLibroRojoConcatenado(obj.getCmbLibroRojoConcatenado() + combosFaunaController.getMapaCatalogoGeneral().get(d.getIdLibroRojoEcuador() == null ? "" : d.getIdLibroRojoEcuador().toString()) + separador);
                obj.setCmbModoReproductivoConcatenado(obj.getCmbModoReproductivoConcatenado() + combosFaunaController.getMapaCatalogoGeneral().get(d.getIdModoReproductivo() == null ? "" : d.getIdModoReproductivo().toString()) + separador);
                obj.setCmbUsoConcatenado(obj.getCmbUsoConcatenado() + combosFaunaController.getMapaCatalogoGeneral().get(d.getIdUso() == null ? "" : d.getIdUso().toString()) + separador);
                obj.setCmbEndemicaConcatenado(obj.getCmbEndemicaConcatenado() + combosFaunaController.getMapaCatalogoGeneral().get(d.getIdEspecieEndemica() == null ? "" : d.getIdEspecieEndemica().toString()) + separador);
                obj.setCmbSistemaHidrograficoConcatenado(obj.getCmbSistemaHidrograficoConcatenado() + combosFaunaController.getMapaCatalogoGeneral().get(d.getIdSistemasHidrograficos() == null ? "" : d.getIdSistemasHidrograficos().toString()) + separador);
                obj.setCmbDistribucionColumnaAguaConcatenado(obj.getCmbDistribucionColumnaAguaConcatenado() + combosFaunaController.getMapaCatalogoGeneral().get(d.getIdDistribucionColumnaAgua() == null ? "" : d.getIdDistribucionColumnaAgua().toString()) + separador);
            }
        }
        obj.setPuntosMuestreoIds(devolverSinComa(obj.getPuntosMuestreoIds()));
        obj.setOrdenConcatenado(devolverSinComa(obj.getOrdenConcatenado()));
        obj.setFamiliaConcatenado(devolverSinComa(obj.getFamiliaConcatenado()));
        obj.setGeneroConcatenado(devolverSinComa(obj.getGeneroConcatenado()));
        obj.setNombreComunConcatenado(devolverSinComa(obj.getNombreComunConcatenado()));
        obj.setCriterioSensibilidadConcatenado(devolverSinComa(obj.getCriterioSensibilidadConcatenado()));
        obj.setCriterioConcatenado(devolverSinComa(obj.getCriterioConcatenado()));
        obj.setNumeroRegistro(getFaunaBean().getListaDetalleFaunaEspecies().size() + 1);
        obj.setCmbTipoRegistroConcatenado(devolverSinComa(obj.getCmbTipoRegistroConcatenado()));
        obj.setCmbTipoVegetacionConcatenado(devolverSinComa(obj.getCmbTipoVegetacionConcatenado()));
        obj.setCmbCondicionesClimaticasConcatenado(devolverSinComa(obj.getCmbCondicionesClimaticasConcatenado()));
        obj.setCmbDistribucionVerticalConcatenado(devolverSinComa(obj.getCmbDistribucionVerticalConcatenado()));
        obj.setCmbComportamientoSocialConcatenado(devolverSinComa(obj.getCmbComportamientoSocialConcatenado()));
        obj.setCmbGremioAlimenticioConcatenado(devolverSinComa(obj.getCmbGremioAlimenticioConcatenado()));
        obj.setCmbPatronActividadConcatenado(devolverSinComa(obj.getCmbPatronActividadConcatenado()));
        obj.setCmbFaseLunarConcatenado(devolverSinComa(obj.getCmbFaseLunarConcatenado()));
        obj.setCmbSensibilidadConcatenado(devolverSinComa(obj.getCmbSensibilidadConcatenado()));
        obj.setCmbEspeciesMigratoriasConcatenado(devolverSinComa(obj.getCmbEspeciesMigratoriasConcatenado()));
        obj.setCmbEspeciesBioindicadorasConcatenado(devolverSinComa(obj.getCmbEspeciesBioindicadorasConcatenado()));
        obj.setCmbUicnConcatenado(devolverSinComa(obj.getCmbUicnConcatenado()));
        obj.setCmbCitesConcatenado(devolverSinComa(obj.getCmbCitesConcatenado()));
        obj.setCmbLibroRojoConcatenado(devolverSinComa(obj.getCmbLibroRojoConcatenado()));
        obj.setCmbModoReproductivoConcatenado(devolverSinComa(obj.getCmbModoReproductivoConcatenado()));
        obj.setCmbUsoConcatenado(devolverSinComa(obj.getCmbUsoConcatenado()));
        obj.setCmbEndemicaConcatenado(devolverSinComa(obj.getCmbEndemicaConcatenado()));
        obj.setCmbSistemaHidrograficoConcatenado(devolverSinComa(obj.getCmbSistemaHidrograficoConcatenado()));
        obj.setCmbDistribucionColumnaAguaConcatenado(devolverSinComa(obj.getCmbDistribucionColumnaAguaConcatenado()));
        return obj;
    }

    private DetalleFaunaEspecies concatenar(DetalleFaunaEspecies det, final String especie, final List<DetalleFauna> listaRepetidos) {
        DetalleFaunaEspecies obj = det;
        obj.setPuntosMuestreoIds("");
        obj.setOrdenConcatenado("");
        obj.setFamiliaConcatenado("");
        obj.setGeneroConcatenado("");
        obj.setNombreComunConcatenado("");
        obj.setCriterioSensibilidadConcatenado("");
        obj.setCriterioConcatenado("");
        obj.setEspecie(especie);
        obj.setCmbTipoRegistroConcatenado("");
        obj.setCmbTipoVegetacionConcatenado("");
        obj.setCmbCondicionesClimaticasConcatenado("");
        obj.setCmbComportamientoSocialConcatenado("");
        obj.setCmbGremioAlimenticioConcatenado("");
        obj.setCmbPatronActividadConcatenado("");
        obj.setCmbFaseLunarConcatenado("");
        obj.setCmbSensibilidadConcatenado("");
        obj.setCmbEspeciesMigratoriasConcatenado("");
        obj.setCmbEspeciesBioindicadorasConcatenado("");
        obj.setCmbUicnConcatenado("");
        obj.setCmbCitesConcatenado("");
        obj.setCmbLibroRojoConcatenado("");
        obj.setCmbDistribucionVerticalConcatenado("");
        obj.setCmbModoReproductivoConcatenado("");
        obj.setCmbUsoConcatenado("");
        obj.setCmbEndemicaConcatenado("");
        obj.setCmbSistemaHidrograficoConcatenado("");
        obj.setCmbDistribucionColumnaAguaConcatenado("");
        obj.setNumeroIndividuosConcatenado(0);
        String separador = ",";
        for (DetalleFauna d : listaRepetidos) {
            if (especie.equals(d.getEspecie())) {
                obj.setNumeroIndividuosConcatenado(obj.getNumeroIndividuosConcatenado() + 1);
                obj.setPuntosMuestreoIds(obj.getPuntosMuestreoIds() + d.getIdPuntoMuestreo() + separador);
                obj.setOrdenConcatenado(obj.getOrdenConcatenado() + d.getOrden() + separador);
                obj.setFamiliaConcatenado(obj.getFamiliaConcatenado() + d.getFamilia() + separador);
                obj.setGeneroConcatenado(obj.getGeneroConcatenado() + d.getGenero() + separador);
                obj.setNombreComunConcatenado(obj.getNombreComunConcatenado() + d.getNombreComun() + separador);
                obj.setCriterioSensibilidadConcatenado(obj.getCriterioSensibilidadConcatenado() + d.getCriterioSencibilidad() + separador);
                obj.setCriterioConcatenado(obj.getCriterioConcatenado() + d.getCriterio() + separador);
                obj.setCmbTipoRegistroConcatenado(obj.getCmbTipoRegistroConcatenado() + combosFaunaController.getMapaCatalogoGeneral().get(d.getIdTipoRegistro() == null ? "" : d.getIdTipoRegistro().toString()) + separador);
                obj.setCmbTipoVegetacionConcatenado(obj.getCmbTipoVegetacionConcatenado() + combosFaunaController.getMapaCatalogoGeneral().get(d.getIdTipoVegetacion() == null ? "" : d.getIdTipoVegetacion().toString()) + separador);
                obj.setCmbCondicionesClimaticasConcatenado(obj.getCmbCondicionesClimaticasConcatenado() + combosFaunaController.getMapaCatalogoGeneral().get(d.getIdCondicionesClimaticas() == null ? "" : d.getIdCondicionesClimaticas().toString()) + separador);
                obj.setCmbDistribucionVerticalConcatenado(obj.getCmbDistribucionVerticalConcatenado() + combosFaunaController.getMapaCatalogoGeneral().get(d.getIdDistribucionVerticalEspecie() == null ? "" : d.getIdDistribucionVerticalEspecie().toString()) + separador);
                obj.setCmbComportamientoSocialConcatenado(obj.getCmbComportamientoSocialConcatenado() + combosFaunaController.getMapaCatalogoGeneral().get(d.getIdComportamientoSocial() == null ? "" : d.getIdComportamientoSocial().toString()) + separador);
                obj.setCmbGremioAlimenticioConcatenado(obj.getCmbGremioAlimenticioConcatenado() + combosFaunaController.getMapaCatalogoGeneral().get(d.getIdGremioAlimenticio() == null ? "" : d.getIdGremioAlimenticio().toString()) + separador);
                obj.setCmbPatronActividadConcatenado(obj.getCmbPatronActividadConcatenado() + combosFaunaController.getMapaCatalogoGeneral().get(d.getIdPatronActividad() == null ? "" : d.getIdPatronActividad().toString()) + separador);
                obj.setCmbFaseLunarConcatenado(obj.getCmbFaseLunarConcatenado() + combosFaunaController.getMapaCatalogoGeneral().get(d.getIdFaseLunar() == null ? "" : d.getIdFaseLunar().toString()) + separador);
                obj.setCmbSensibilidadConcatenado(obj.getCmbSensibilidadConcatenado() + combosFaunaController.getMapaCatalogoGeneral().get(d.getIdSensibilidad() == null ? "" : d.getIdSensibilidad().toString()) + separador);
                obj.setCmbEspeciesMigratoriasConcatenado(obj.getCmbEspeciesMigratoriasConcatenado() + combosFaunaController.getMapaCatalogoGeneral().get(d.getIdEspeciesMigratorias() == null ? "" : d.getIdEspeciesMigratorias().toString()) + separador);
                obj.setCmbEspeciesBioindicadorasConcatenado(obj.getCmbEspeciesBioindicadorasConcatenado() + combosFaunaController.getMapaCatalogoGeneral().get(d.getIdEspeciesBioindicadoras() == null ? "" : d.getIdEspeciesBioindicadoras().toString()) + separador);
                obj.setCmbUicnConcatenado(obj.getCmbUicnConcatenado() + combosFaunaController.getMapaCatalogoGeneral().get(d.getIdUicnInternacional() == null ? "" : d.getIdUicnInternacional().toString()) + separador);
                obj.setCmbCitesConcatenado(obj.getCmbCitesConcatenado() + combosFaunaController.getMapaCatalogoGeneral().get(d.getIdCites() == null ? "" : d.getIdCites().toString()) + separador);
                obj.setCmbLibroRojoConcatenado(obj.getCmbLibroRojoConcatenado() + combosFaunaController.getMapaCatalogoGeneral().get(d.getIdLibroRojoEcuador() == null ? "" : d.getIdLibroRojoEcuador().toString()) + separador);
                obj.setCmbModoReproductivoConcatenado(obj.getCmbModoReproductivoConcatenado() + combosFaunaController.getMapaCatalogoGeneral().get(d.getIdModoReproductivo() == null ? "" : d.getIdModoReproductivo().toString()) + separador);
                obj.setCmbUsoConcatenado(obj.getCmbUsoConcatenado() + combosFaunaController.getMapaCatalogoGeneral().get(d.getIdUso() == null ? "" : d.getIdUso().toString()) + separador);
                obj.setCmbEndemicaConcatenado(obj.getCmbEndemicaConcatenado() + combosFaunaController.getMapaCatalogoGeneral().get(d.getIdEspecieEndemica() == null ? "" : d.getIdEspecieEndemica().toString()) + separador);
                obj.setCmbSistemaHidrograficoConcatenado(obj.getCmbSistemaHidrograficoConcatenado() + combosFaunaController.getMapaCatalogoGeneral().get(d.getIdSistemasHidrograficos() == null ? "" : d.getIdSistemasHidrograficos().toString()) + separador);
                obj.setCmbDistribucionColumnaAguaConcatenado(obj.getCmbDistribucionColumnaAguaConcatenado() + combosFaunaController.getMapaCatalogoGeneral().get(d.getIdDistribucionColumnaAgua() == null ? "" : d.getIdDistribucionColumnaAgua().toString()) + separador);
            }
        }
        obj.setPuntosMuestreoIds(devolverSinComa(obj.getPuntosMuestreoIds()));
        obj.setOrdenConcatenado(devolverSinComa(obj.getOrdenConcatenado()));
        obj.setFamiliaConcatenado(devolverSinComa(obj.getFamiliaConcatenado()));
        obj.setGeneroConcatenado(devolverSinComa(obj.getGeneroConcatenado()));
        obj.setNombreComunConcatenado(devolverSinComa(obj.getNombreComunConcatenado()));
        obj.setCriterioSensibilidadConcatenado(devolverSinComa(obj.getCriterioSensibilidadConcatenado()));
        obj.setCriterioConcatenado(devolverSinComa(obj.getCriterioConcatenado()));
        obj.setNumeroRegistro(getFaunaBean().getListaDetalleFaunaEspecies().size() + 1);
        obj.setCmbTipoRegistroConcatenado(devolverSinComa(obj.getCmbTipoRegistroConcatenado()));
        obj.setCmbTipoVegetacionConcatenado(devolverSinComa(obj.getCmbTipoVegetacionConcatenado()));
        obj.setCmbCondicionesClimaticasConcatenado(devolverSinComa(obj.getCmbCondicionesClimaticasConcatenado()));
        obj.setCmbDistribucionVerticalConcatenado(devolverSinComa(obj.getCmbDistribucionVerticalConcatenado()));
        obj.setCmbComportamientoSocialConcatenado(devolverSinComa(obj.getCmbComportamientoSocialConcatenado()));
        obj.setCmbGremioAlimenticioConcatenado(devolverSinComa(obj.getCmbGremioAlimenticioConcatenado()));
        obj.setCmbPatronActividadConcatenado(devolverSinComa(obj.getCmbPatronActividadConcatenado()));
        obj.setCmbFaseLunarConcatenado(devolverSinComa(obj.getCmbFaseLunarConcatenado()));
        obj.setCmbSensibilidadConcatenado(devolverSinComa(obj.getCmbSensibilidadConcatenado()));
        obj.setCmbEspeciesMigratoriasConcatenado(devolverSinComa(obj.getCmbEspeciesMigratoriasConcatenado()));
        obj.setCmbEspeciesBioindicadorasConcatenado(devolverSinComa(obj.getCmbEspeciesBioindicadorasConcatenado()));
        obj.setCmbUicnConcatenado(devolverSinComa(obj.getCmbUicnConcatenado()));
        obj.setCmbCitesConcatenado(devolverSinComa(obj.getCmbCitesConcatenado()));
        obj.setCmbLibroRojoConcatenado(devolverSinComa(obj.getCmbLibroRojoConcatenado()));
        obj.setCmbModoReproductivoConcatenado(devolverSinComa(obj.getCmbModoReproductivoConcatenado()));
        obj.setCmbUsoConcatenado(devolverSinComa(obj.getCmbUsoConcatenado()));
        obj.setCmbEndemicaConcatenado(devolverSinComa(obj.getCmbEndemicaConcatenado()));
        obj.setCmbSistemaHidrograficoConcatenado(devolverSinComa(obj.getCmbSistemaHidrograficoConcatenado()));
        obj.setCmbDistribucionColumnaAguaConcatenado(devolverSinComa(obj.getCmbDistribucionColumnaAguaConcatenado()));
        return obj;
    }

    private String devolverSinComa(String s) {
        String resultado = "";
        if (s != null && !s.isEmpty()) {
            resultado = s.substring(0, s.length() - 1);
        }
        return resultado;
    }

    // </editor-fold> 
}
