/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.eia.mediobiotico.controller;

import ec.gob.ambiente.suia.catalogos.facade.CatalogoGeneralFacade;
import ec.gob.ambiente.suia.domain.CatalogoGeneral;
import ec.gob.ambiente.suia.domain.EiaOpciones;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.domain.TipoCatalogo;
import ec.gob.ambiente.suia.dto.EntityAdjunto;
import ec.gob.ambiente.suia.eia.fauna.facade.MedioBioticoFacade;
import ec.gob.ambiente.suia.eia.mediobiotico.bean.MedioBioticoBean;
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
import javax.faces.model.SelectItem;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.event.FileUploadEvent;

/**
 *
 * @author ishmael
 */
@ManagedBean
@ViewScoped
public class MedioBioticoController implements Serializable {

    private static final long serialVersionUID = 6640392364686694792L;

    @EJB
    private CatalogoGeneralFacade catalogoGeneralFacade;
    @EJB
    private MedioBioticoFacade medioBioticoFacade;

    @Getter
    @Setter
    private MedioBioticoBean medioBioticoBean;

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
            .getLogger(MedioBioticoController.class);

    /**
     *
     */
    @PostConstruct
    public void inicio() {
        try {
            medioBioticoBean = new MedioBioticoBean();
            medioBioticoBean.instanciarAdjuntos();
            medioBioticoBean.setListaSeleccionaEcosistemas(null);
            cargarMedioBiotico();
            cargarListaTipoMuestreo();
        } catch (Exception e) {
            LOG.error(e , e);
        }
    }

    private void cargarListaTipoMuestreo() {
        medioBioticoBean.setListaEcosistemas(new ArrayList<SelectItem>());
        try {
            List<CatalogoGeneral> listaDatos = catalogoGeneralFacade.obtenerCatalogoXTipo(TipoCatalogo.ECOSISTEMAS);
            for (CatalogoGeneral c : listaDatos) {
                medioBioticoBean.getListaEcosistemas().add(new SelectItem(c.getId().toString(), c.getDescripcion()));
            }
        } catch (Exception e) {
            LOG.error(e , e);
        }
    }

    /**
     *
     * @param event
     */
    public void handleFileUpload(FileUploadEvent event) {
        Map<String, Object> atributos = event.getComponent().getAttributes();
        String key = atributos.get("id").toString();
        medioBioticoBean.getAdjuntosMap().get(key).setArchivo(event.getFile().getContents());
        medioBioticoBean.getAdjuntosMap().get(key).setMimeType(event.getFile().getContentType());
        medioBioticoBean.getAdjuntosMap().get(key).setNombre(medioBioticoBean.getAdjuntosMap().get(key).getNombre()
                + "." + JsfUtil.devuelveExtension(event.getFile().getFileName()));
        medioBioticoBean.setAdjunto(event.getFile().getFileName());
    }

    /**
     *
     */
    public void guardar() {
        try {
            Map<String, EiaOpciones> mapOpciones = (Map<String, EiaOpciones>) JsfUtil.devolverObjetoSession(Constantes.SESSION_OPCIONES_EIA);
            medioBioticoBean.getMedioBiotico().setEcosistemas(JsfUtil.transformaVector(medioBioticoBean.getListaSeleccionaEcosistemas()));
            medioBioticoFacade.guardar(medioBioticoBean.getMedioBiotico(),
                    new ArrayList<EntityAdjunto>(medioBioticoBean.getAdjuntosMap().values()), mapOpciones.get(EiaOpciones.MEDIO_BIOTICO_HIDRO));
            cargarMedioBiotico();
            JsfUtil.addMessageInfo(JsfUtil.REGISTRO_ACTUALIZADO);
        } catch (Exception e) {
            LOG.error(e , e);
        }
    }

    private void cargarMedioBiotico() throws ServiceException {
        EstudioImpactoAmbiental es = (EstudioImpactoAmbiental) JsfUtil.devolverObjetoSession(Constantes.SESSION_EIA_OBJECT);
        medioBioticoBean.setMedioBiotico(medioBioticoFacade.recuperarPorEia(es));
        medioBioticoBean.getMedioBiotico().setEstudioImpactoAmbiental(es);
        if (medioBioticoBean.getMedioBiotico().isPersisted()) {
            medioBioticoBean.setListaSeleccionaEcosistemas(JsfUtil.devuelveVector(medioBioticoBean.getMedioBiotico().getEcosistemas()));
        }
    }

}
