/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.eia.descripcion.proyecto.controller;

import ec.gob.ambiente.suia.catalogos.facade.CatalogoGeneralFacade;
import ec.gob.ambiente.suia.domain.CatalogoGeneral;
import ec.gob.ambiente.suia.domain.DeterminacionAreaInfluenciaEIA;
import ec.gob.ambiente.suia.domain.EiaOpciones;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.domain.TipoCatalogo;
import ec.gob.ambiente.suia.dto.EntityAdjunto;
import ec.gob.ambiente.suia.eia.descripcion.proyecto.bean.DeterminacionAreaInfluenciaEIABean;
import ec.gob.ambiente.suia.eia.descripcion.proyecto.facade.DeterminacionAreaInfluenciaEiaFacade;
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
public class DeteminacionAreaInflueciaEIAController implements Serializable {

    private static final long serialVersionUID = 1572525482381028668L;

    @EJB
    private DeterminacionAreaInfluenciaEiaFacade determinacionAreaInfluenciaEiaFacade;
    @EJB
    private CatalogoGeneralFacade catalogoGeneralFacade;

    @Getter
    @Setter
    private DeterminacionAreaInfluenciaEIABean determinacionAreaInfluenciaEIABean;
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
            .getLogger(DeteminacionAreaInflueciaEIAController.class);

    @PostConstruct
    private void cargarDatos() {
        setDeterminacionAreaInfluenciaEIABean(new DeterminacionAreaInfluenciaEIABean());
        getDeterminacionAreaInfluenciaEIABean().iniciarDatos();
        cargarAnalisis();
    }

    private void cargarAnalisis() {
        EstudioImpactoAmbiental es = (EstudioImpactoAmbiental) JsfUtil.devolverObjetoSession(Constantes.SESSION_EIA_OBJECT);
        try {
            getDeterminacionAreaInfluenciaEIABean().setListaDeterminacionAreaInfluenciaEIA(determinacionAreaInfluenciaEiaFacade.listarPorEIA(es));
            if (getDeterminacionAreaInfluenciaEIABean().getListaDeterminacionAreaInfluenciaEIA() == null || getDeterminacionAreaInfluenciaEIABean().getListaDeterminacionAreaInfluenciaEIA().isEmpty()) {
                getDeterminacionAreaInfluenciaEIABean().setListaDeterminacionAreaInfluenciaEIA(new ArrayList<DeterminacionAreaInfluenciaEIA>());
                List<CatalogoGeneral> listaCatalogo = catalogoGeneralFacade.obtenerCatalogoXTipo(TipoCatalogo.TIPO_DETERMINACION_AREA_INFLUENCIA_EIA);
                for (CatalogoGeneral c : listaCatalogo) {
                    DeterminacionAreaInfluenciaEIA obj = new DeterminacionAreaInfluenciaEIA();
                    obj.setEstado(true);
                    obj.setGecaId(c);
                    obj.setEistId(es);
                    getDeterminacionAreaInfluenciaEIABean().getListaDeterminacionAreaInfluenciaEIA().add(obj);
                }
                determinacionAreaInfluenciaEiaFacade.guardar(getDeterminacionAreaInfluenciaEIABean().getListaDeterminacionAreaInfluenciaEIA());
                reasignarIndice();
            }
        } catch (Exception e) {
            LOG.error(e , e);
        }
    }

    public void handleFileUpload(FileUploadEvent event) {
        getDeterminacionAreaInfluenciaEIABean().getEntityAdjunto().setArchivo(event.getFile().getContents());
        getDeterminacionAreaInfluenciaEIABean().getEntityAdjunto().setExtension(JsfUtil.devuelveExtension(event.getFile().getFileName()));
        getDeterminacionAreaInfluenciaEIABean().getEntityAdjunto().setMimeType(event.getFile().getContentType());
        LOG.info(event.getFile().getFileName() + " is uploaded.");
    }
    
    public void handleFileUploadDetalle(FileUploadEvent event) {
        Map<String, Object> id = event.getComponent().getAttributes();
        int indice = new Integer(id.get("idAnalisis").toString());
        DeterminacionAreaInfluenciaEIA ana = getDeterminacionAreaInfluenciaEIABean().getListaDeterminacionAreaInfluenciaEIA().get(indice);
        EntityAdjunto obj = new EntityAdjunto();
        obj.setArchivo(event.getFile().getContents());
        obj.setExtension(JsfUtil.devuelveExtension(event.getFile().getFileName()));
        obj.setMimeType(event.getFile().getContentType());
        ana.setEntityAdjunto(obj);
        getDeterminacionAreaInfluenciaEIABean().getListaDeterminacionAreaInfluenciaEIA().set(indice, ana);
        reasignarIndice();
        LOG.info(event.getFile().getFileName() + " is uploaded.");
    }

    public void guardar() {
        try {
            EstudioImpactoAmbiental es = (EstudioImpactoAmbiental) JsfUtil.devolverObjetoSession(Constantes.SESSION_EIA_OBJECT);
            Map<String, EiaOpciones> mapOpciones = (Map<String, EiaOpciones>) JsfUtil.devolverObjetoSession(Constantes.SESSION_OPCIONES_EIA);
            getDeterminacionAreaInfluenciaEIABean().validarFormulario();
            StringBuilder nombreArchivo = new StringBuilder();
            nombreArchivo.append("EIA");
            nombreArchivo.append("Determinacion area influencia 0");
            nombreArchivo.append(es.getId());
            nombreArchivo.append(".").append(getDeterminacionAreaInfluenciaEIABean().getEntityAdjunto().getExtension());
            getDeterminacionAreaInfluenciaEIABean().getEntityAdjunto().setNombre(nombreArchivo.toString());            
            determinacionAreaInfluenciaEiaFacade.guardarConAdjunto(getDeterminacionAreaInfluenciaEIABean().getListaDeterminacionAreaInfluenciaEIA(), es, getDeterminacionAreaInfluenciaEIABean().getEntityAdjunto(), mapOpciones.get(EiaOpciones.DETERMINACION_AREA_INFLUENCIA_HIDRO));
            setDeterminacionAreaInfluenciaEIABean(null);
            cargarDatos();
            JsfUtil.addMessageInfo(JsfUtil.REGISTRO_ACTUALIZADO);
        } catch (ServiceException e) {
            LOG.error(e , e);
            JsfUtil.addMessageError(JsfUtil.ERROR_ACTUALIZAR_REGISTRO + " " + e.getMessage());
        } catch (RuntimeException e) {
            LOG.error(e , e);
            JsfUtil.addMessageError(JsfUtil.ERROR_ACTUALIZAR_REGISTRO + " " + e.getMessage());
        }
    }

    public void cancelar() {
        JsfUtil.redirectTo("/pages/eia/determinacionAreaInfluenciaEia/determinacionAreaInfluenciaEia.jsf");
    }
    
    private void reasignarIndice() {
        int i = 0;
        for (DeterminacionAreaInfluenciaEIA ana : getDeterminacionAreaInfluenciaEIABean().getListaDeterminacionAreaInfluenciaEIA()) {
            ana.setIndice(i);
            i++;
        }
    }
    
}
