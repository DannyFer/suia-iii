/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.prevencion.categoria2.controllers;

import ec.gob.ambiente.prevencion.categoria2.bean.CronogramaConstruccionBean;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.domain.Actividad;
import ec.gob.ambiente.suia.domain.CatalogoCategoriaFase;
import ec.gob.ambiente.suia.domain.CronogramaActividadesPma;
import ec.gob.ambiente.suia.domain.Fase;
import ec.gob.ambiente.suia.domain.FichaAmbientalPma;
import ec.gob.ambiente.suia.domain.TipoSubsector;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.CronogramaConstruccionFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.FaseFichaAmbientalFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.FichaAmbientalPmaFacade;
import ec.gob.ambiente.suia.utils.EjecutarSentenciasNativas;
import ec.gob.ambiente.suia.utils.JsfUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.context.RequestContext;
import org.primefaces.extensions.model.timeline.TimelineEvent;
import org.primefaces.extensions.model.timeline.TimelineModel;

/**
 *
 * @author ishmael
 */
@ManagedBean
@ViewScoped
public class CronogramaCostruccionController implements Serializable {

    private static final long serialVersionUID = -6065104918833011557L;

    @Getter
    @Setter
    private CronogramaConstruccionBean cronogramaConstruccionBean;

    @EJB
    private CronogramaConstruccionFacade cronogramaConstruccionFacade;
    @EJB
    private FichaAmbientalPmaFacade fichaAmbientalPmaFacade;
    @EJB
    private FaseFichaAmbientalFacade faseFichaAmbientalFacade;
    @ManagedProperty(value = "#{proyectosBean}")
    @Getter
    @Setter
    private ProyectosBean proyectosBean;

    @EJB
    private EjecutarSentenciasNativas ejecutarSentenciasNativas;

    private TimelineModel model;
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
            .getLogger(CronogramaCostruccionController.class);

    @PostConstruct
    protected void initialize() {
        cronogramaConstruccionBean = new CronogramaConstruccionBean();
        validarRegistro();
        cronogramaConstruccionBean.setFicha(fichaAmbientalPmaFacade.getFichaAmbientalPorIdProyecto(proyectosBean.getProyecto().getId()));
        cargarCatalogoTipoActividad();
        cronogramaConstruccionBean.setZoomMax(1000L * 60 * 60 * 24 * 31 * 12);
        cronogramaConstruccionBean.setZoomMin(1000L * 60 * 60 * 24 * 31 * 6);
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.set(cal.get(Calendar.YEAR), Calendar.JANUARY, 0, 0, 0, 0);
        cronogramaConstruccionBean.setStart(cal.getTime());
        cal.set(cal.get(Calendar.YEAR), Calendar.DECEMBER, 31, 0, 0, 0);
        cronogramaConstruccionBean.setEnd(cal.getTime());

    }

    private void validarRegistro() {
        if (proyectosBean.getProyecto().getCatalogoCategoria().getTipoSubsector().getCodigo().equals(TipoSubsector.CODIGO_MINERIA_EXPLORACION_INICIAL)) {
            RequestContext context = RequestContext.getCurrentInstance();
            cronogramaConstruccionBean.setMensaje("No aplica para este tipo de sector");
            context.execute("PF('dlgInfo').show();");
        }
    }

    private void cargarCatalogoTipoActividad() {
        try {
            List<CatalogoCategoriaFase> listaAux = faseFichaAmbientalFacade.obtenerCatalogoCategoriaFasesPorFicha(cronogramaConstruccionBean.getFicha().getId());
            if (listaAux == null || listaAux.isEmpty()) {
                RequestContext context = RequestContext.getCurrentInstance();
                cronogramaConstruccionBean.setMensaje("Debe ingresar la información solicitada en el punto 3.");
                context.execute("PF('dlgInfo').show();");
                cronogramaConstruccionBean.setHabilitar(false);
            } else {
                cronogramaConstruccionBean.setListaTipoActividad(new ArrayList<CatalogoCategoriaFase>());
                CatalogoCategoriaFase cg = new CatalogoCategoriaFase();
                Fase fase = new Fase();
                fase.setNombre("Seleccione");
                cg.setFase(fase);
                cronogramaConstruccionBean.getListaTipoActividad().add(cg);
                cronogramaConstruccionBean.getListaTipoActividad().addAll(listaAux);
                cronogramaConstruccionBean.setHabilitar(true);
            }
        } catch (Exception e) {
            LOG.error(e , e);
        }
    }

    public void seleccionarTipoActividad() {
        try {
            if (cronogramaConstruccionBean.getCatalogoGeneralSeleccionado().getId() != null) {
                recuperarCronograma(cronogramaConstruccionBean.getFicha(), cronogramaConstruccionBean.getCatalogoGeneralSeleccionado());
            }
        } catch (ServiceException e) {
            LOG.error(e , e);
        }

    }

    private void recuperarCronograma(FichaAmbientalPma f, CatalogoCategoriaFase cg) throws ServiceException {
        cronogramaConstruccionBean.setListaActividadEliminar(new ArrayList<Actividad>());
        cronogramaConstruccionBean.setCronogramaActividadesPma(cronogramaConstruccionFacade.obtenerPorFichaPmaCatalogo(f, cg));
        if (cronogramaConstruccionBean.getCronogramaActividadesPma() != null) {
            cronogramaConstruccionBean.setListaActividadTabla(cronogramaConstruccionBean.getCronogramaActividadesPma().getActividadList());
            cronogramaConstruccionBean.getCronogramaActividadesPma().setActividadList(null);
            reasignarIndiceTabla();
        } else {
            cronogramaConstruccionBean.setCronogramaActividadesPma(new CronogramaActividadesPma());
            cronogramaConstruccionBean.getCronogramaActividadesPma().setCatalogoCategoriaFase(cg);
            cronogramaConstruccionBean.setListaActividadTabla(new ArrayList<Actividad>());
        }

    }

    private void reasignarIndiceTabla() {
        model = new TimelineModel();
        int i = 0;
        for (Actividad a : cronogramaConstruccionBean.getListaActividadTabla()) {
            model.add(new TimelineEvent(a, a.getFechaInicio(),
                    a.getFechaFin()));
            a.setIndice(i);
            a.setEditar(false);
            i++;
        }
    }

    public void guardar() {
        try {
            if (validarGuardar()) {
                cronogramaConstruccionBean.getCronogramaActividadesPma().setFichaAmbientalPma(cronogramaConstruccionBean.getFicha());
                cronogramaConstruccionBean.getCronogramaActividadesPma().setActividadList(cronogramaConstruccionBean.getListaActividadTabla());
                cronogramaConstruccionFacade.guardar(cronogramaConstruccionBean.getCronogramaActividadesPma(),
                        cronogramaConstruccionBean.getListaActividadEliminar(), cronogramaConstruccionBean.getFicha());
                cronogramaConstruccionBean = null;
                initialize();
                JsfUtil.addMessageInfo(JsfUtil.REGISTRO_GUARDADO);
            }
        } catch (Exception e) {
            LOG.error(e , e);
        }

    }

    private boolean validarGuardar() {
        List<String> msgs = new ArrayList<String>();
        if (cronogramaConstruccionBean.getCatalogoGeneralSeleccionado() == null || cronogramaConstruccionBean.getCatalogoGeneralSeleccionado().getId() == null) {
            msgs.add("Debe seleccionar un tipo de actividad");
        }
        if (cronogramaConstruccionBean.getListaActividadTabla().isEmpty()) {
            msgs.add("Debe ingresar actividades.");
        }
        if (!msgs.isEmpty()) {
            JsfUtil.addMessageError(msgs);
            return false;
        }
        return true;
    }

    private boolean validarActividad() {
        List<String> msgs = new ArrayList<String>();
        if (cronogramaConstruccionBean.getActividad().getDescripcion() == null || cronogramaConstruccionBean.getActividad().getDescripcion().isEmpty()) {
            msgs.add("Debe ingresar la actividad.");
        } else {
            for (Actividad a : cronogramaConstruccionBean.getListaActividadTabla()) {
                if (!a.isEditar() && cronogramaConstruccionBean.getActividad().getDescripcion().equals(a.getDescripcion())) {
                    msgs.add("Está ingresando una actividad que ya existe.");
                    break;
                }
            }
        }
        if (cronogramaConstruccionBean.getActividad().getFechaFin() == null) {
            msgs.add("Debe ingresar la fecha hasta.");
        }
        if (cronogramaConstruccionBean.getActividad().getFechaFin() != null
                && cronogramaConstruccionBean.getActividad().getFechaFin().before(cronogramaConstruccionBean.getActividad().getFechaInicio())) {
            msgs.add("La fecha hasta no debe ser anterior a la fecha desde.");
        }
        if (!msgs.isEmpty()) {
            JsfUtil.addMessageError(msgs);
            return false;
        }
        return true;
    }

    public TimelineModel getModel() {
        return model;
    }

    public void agregarActividad() {
        try {
            Actividad a = new Actividad();
            a.setFechaInicio(new Date());
            cronogramaConstruccionBean.setActividad(a);
        } catch (Exception e) {
            LOG.error(e , e);
        }
    }

    public void guardarActividad() {
        try {
            RequestContext context = RequestContext.getCurrentInstance();
            if (validarActividad()) {
                if (!cronogramaConstruccionBean.getActividad().isEditar()) {
                    cronogramaConstruccionBean.getListaActividadTabla().add(cronogramaConstruccionBean.getActividad());
                }
                reasignarIndiceTabla();
                context.addCallbackParam("actividadIn", true);
            } else {
                context.addCallbackParam("actividadIn", false);
            }
        } catch (Exception e) {
            LOG.error(e , e);
        }
    }

    public void seleccionarActividad(Actividad actividad) {
        actividad.setEditar(true);
        cronogramaConstruccionBean.setActividad(actividad);
    }

    public void remover() {
        cronogramaConstruccionBean.getListaActividadTabla().remove(cronogramaConstruccionBean.getActividad().getIndice());
        if (cronogramaConstruccionBean.getActividad().getId() != null) {
            cronogramaConstruccionBean.getListaActividadEliminar().add(cronogramaConstruccionBean.getActividad());
        }
        reasignarIndiceTabla();
    }
}
