package ec.gob.ambiente.suia.administracion.controllers;

import ec.gob.ambiente.suia.administracion.bean.UbicacionGeograficaBean;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author ishmael
 */
@ManagedBean
@ViewScoped
public class UbicacionGeograficaController implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -8381767370115274828L;

    @EJB
    private UbicacionGeograficaFacade ubicacionGeograficaFacade;

    @Getter
    @Setter
    private UbicacionGeograficaBean ubicacionGeograficaBean;
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
            .getLogger(PromotorController.class);

    @PostConstruct
    public void inicio() {
        ubicacionGeograficaBean = new UbicacionGeograficaBean();
        cargarProvincias();
    }

    private void cargarProvincias() {
        try {
            ubicacionGeograficaBean.setListaProvincia(ubicacionGeograficaFacade.listarProvincia());
        } catch (Exception e) {
            LOG.error(e, e);
        }
    }

    public void cargarCanton() {
        try {
            if (ubicacionGeograficaBean.getIdProvincia() != null && !ubicacionGeograficaBean.getIdProvincia().isEmpty()) {
                ubicacionGeograficaBean.setListaCanton(ubicacionGeograficaFacade.listarPorPadre(new UbicacionesGeografica(Integer.valueOf(ubicacionGeograficaBean.getIdProvincia()))));
            } else {
                ubicacionGeograficaBean.setListaCanton(null);
                ubicacionGeograficaBean.setIdCanton(null);
                ubicacionGeograficaBean.setListaParroquia(null);
            }
        } catch (NumberFormatException e) {
            LOG.error(e, e);
        } catch (ServiceException e) {
            LOG.error(e, e);
        }
    }

    public void cargarParroquia() {
        try {
            if (ubicacionGeograficaBean.getIdCanton() != null && !ubicacionGeograficaBean.getIdCanton().isEmpty()) {
                ubicacionGeograficaBean.setListaParroquia(ubicacionGeograficaFacade.listarPorPadre(new UbicacionesGeografica(Integer.valueOf(ubicacionGeograficaBean.getIdCanton()))));
            }
        } catch (NumberFormatException e) {
            LOG.error(e, e);
        } catch (ServiceException e) {
            LOG.error(e, e);
        }
    }

    public void cargarParametros() {
        try {
            UbicacionesGeografica ugParroquia = ubicacionGeograficaFacade.buscarPorId(Integer.valueOf(ubicacionGeograficaBean.getIdParroquia()));
            ubicacionGeograficaBean.setIdCanton(ugParroquia.getUbicacionesGeografica().getId().toString());
            UbicacionesGeografica ugCanton = ubicacionGeograficaFacade.buscarPorId(Integer.valueOf(ubicacionGeograficaBean.getIdCanton()));
            ubicacionGeograficaBean.setIdProvincia(ugCanton.getUbicacionesGeografica().getId().toString());
            cargarProvincias();
            cargarCanton();
            cargarParroquia();
        } catch (NumberFormatException e) {
            LOG.error(e, e);
        } catch (ServiceException e) {
            LOG.error(e, e);
        }
    }
    
    public void reiniciarValores() {
        getUbicacionGeograficaBean().setIdCanton(null);
        getUbicacionGeograficaBean().setIdParroquia(null);
        getUbicacionGeograficaBean().setIdProvincia(null);
    }
}
