package ec.gob.ambiente.prevencion.categoria2.v2.bean;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.core.interfaces.CompleteOperation;
import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.comun.bean.PagoServiciosBean;
import ec.gob.ambiente.suia.domain.FichaAmbientalMineria;
import ec.gob.ambiente.suia.inventarioForestalPma.facade.InventarioForestalPmaFacade;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.FichaAmbientalPmaFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.ficha.mineria.facade.FichaAmbientalMineriaFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.validaPago.PagoConfiguracionesUtil;

@ManagedBean
@ViewScoped
public class PagoServiciosCategoriaIIBeanV2 implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -2013517581401149879L;

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
            .getLogger(PagoServiciosCategoriaIIBeanV2.class);

    @EJB
    private ProcesoFacade procesoFacade;

    @Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;

    @EJB(lookup = Constantes.JBPM_EJB_TASK_BEAN)
    private TaskBeanFacade taskBeanFacade;

    @Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;

    /**** modulo pago 2016-03-03 ****/
    @Getter
    @Setter
    private FichaAmbientalMineria fichaAmbientalMineria;
    
    @EJB
    private FichaAmbientalMineriaFacade fichaAmbientalMineriaFacade;
    
    @EJB
    private OrganizacionFacade organizacionFacade;
    @EJB
    private ProyectoLicenciamientoAmbientalFacade proyectoFacade;
    @EJB
    private FichaAmbientalPmaFacade fichaAmbientalFacade;
    @EJB
    private InventarioForestalPmaFacade inventarioForestalPmaFacade;
    @Inject
    private PagoConfiguracionesUtil pagoUtil;
    /**** modulo pago 2016-03-03 ****/
    
    @PostConstruct
    public void init() {
        try {
            //Se recuperan la variables del proceso
            Map<String, Object> variables = procesoFacade
                    .recuperarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getTarea().getProcessInstanceId());

            Float costoTotal = Float.parseFloat((String) variables.get("pagoTotal"));

            /**** modulo pago 2016-03-03 ****/
            Map<String, Float> costosProyecto = new ConcurrentHashMap<String, Float>();
            //costoTotal = pagoUtil.validarCostoRegistroAmbiental(costoTotal, bandejaTareasBean.getTarea().getProjectId(), bandejaTareasBean.getTarea().getProcessName().toString());
            costosProyecto = pagoUtil.validarCostoRegistroAmbiental(costoTotal, bandejaTareasBean.getTarea().getProjectId(), bandejaTareasBean.getTarea().getProcessName().toString());
            /**** modulo pago 2016-03-03 ****/
            
            //Ejecutar componente de pago
            JsfUtil.getBean(PagoServiciosBean.class).initFunctionWithProjectOrMotive("/bandeja/bandejaTareas.jsf", new CompleteOperation() {
                @Override
                public Object endOperation(Object object) {
                    try {
                        taskBeanFacade.approveTask(loginBean.getNombreUsuario(),bandejaTareasBean.getTarea().getTaskId(),
                                bandejaTareasBean.getTarea().getProcessInstanceId(), null,
                                loginBean.getPassword(),
                                Constantes.getUrlBusinessCentral(), Constantes.getRemoteApiTimeout(), Constantes.getNotificationService());
                    } catch (Exception exp) {
                        JsfUtil.addMessageError("Ocurrió un error inesperado, por favor intente más adelante. Si el error persiste contacte a Mesa de ayuda.");
                        LOG.error("Error al aprobar la tarea de pago del proceso de Registro ambiental.", exp);
                    }
                    return null;
                }
            }, costosProyecto, null,null,null,false);

        } catch (Exception e) {
            JsfUtil.addMessageError("Error al realizar la operación. Intente más tarde.");
            LOG.error("Error al recuperar datos del proceso de Registro Ambiental.", e);
        }
    }

    
    public void validarTareaBpm() {
        JsfUtil.validarPaginasUrlTareasBpm(bandejaTareasBean.getTarea(), "/prevencion/categoria2/v2/pagoServicios.jsf");
    }
}