package ec.gob.ambiente.prevencion.categoria2.controllers;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.prevencion.categoria2.bean.PagoServiciosCategoriaIIBean;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.CategoriaIIFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.FichaAmbientalPmaFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.TransaccionFinancieraFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.service.FichaAmbientalPmaServiceBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

@ManagedBean
@ViewScoped
public class PagoServiciosCategoriaIIController implements Serializable {
    private static final Logger LOGGER = Logger
            .getLogger(PagoServiciosCategoriaIIController.class);
    private static final long serialVersionUID = -3526934393718393L;

    @Getter
    @Setter
    @ManagedProperty(value = "#{pagoServiciosCategoriaIIBean}")
    private PagoServiciosCategoriaIIBean pagoServiciosCategoriaIIBean;

    @EJB
    private ProcesoFacade procesoFacade;

    @EJB
    private TransaccionFinancieraFacade transaccionFinancieraFacade;

    @EJB
    private CategoriaIIFacade categoriaIIFacade;

    @EJB
    private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;

    @EJB
    FichaAmbientalPmaServiceBean fichaAmbientalPmaServiceBean;

    @EJB
    private FichaAmbientalPmaFacade fichaAmbientalPmaFacade;

    @Getter
    @Setter
    @ManagedProperty(value = "#{proyectosBean}")
    private ProyectosBean proyectosBean;

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

    @Getter
    @Setter
    private String mensaje;
    
    public void completarPagoTarea() throws JbpmException {
        // Realizar pagos
        boolean pagoSatisfactorio = transaccionFinancieraFacade
                .realizarPago(pagoServiciosCategoriaIIBean.costoTramite,
                        pagoServiciosCategoriaIIBean.getTransaccionesFinancieras(), proyectosBean.getProyecto().getCodigo());
        if (pagoSatisfactorio) {
            // Guardar transacciones
            transaccionFinancieraFacade.guardarTransacciones(pagoServiciosCategoriaIIBean.getTransaccionesFinancieras(),
                    bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getTarea().getTaskName(),
                    bandejaTareasBean.getTarea().getProcessInstanceId(), bandejaTareasBean.getTarea().getProcessId(),
                    bandejaTareasBean.getTarea().getProcessName());

            try {
                // Aprobar la tarea
                taskBeanFacade.approveTask(loginBean.getNombreUsuario(),bandejaTareasBean.getTarea().getTaskId(),
                        bandejaTareasBean.getTarea().getProcessInstanceId(), null,
                        loginBean.getPassword(),
                        Constantes.getUrlBusinessCentral(), Constantes.getRemoteApiTimeout(), Constantes.getNotificationService());
                setMensaje("Se realizó correctamente la operación. Para continuar con el proceso de obtención del permiso ambiental debe completar el registro ambiental.");
                RequestContext.getCurrentInstance().execute("PF('continuarDialog').show();");
            }
            catch (Exception exp){
                JsfUtil.addMessageError("Ocurrió un error inesperado, por favor intente más adelante. Si el error persiste contacte a Mesa de ayuda.");
            }
        } else {
            JsfUtil.addMessageError("En estos momentos los servicios financieros están deshabilitados, por favor intente más tarde. Si este error persiste debe comunicarse con Mesa de Ayuda.");
        }
    }

    public void continuar() {
        JsfUtil.redirectTo(JsfUtil.NAVIGATION_TO_BANDEJA);
    }

    public String completarTarea() {
        if (pagoServiciosCategoriaIIBean.getTransaccionesFinancieras().size() == 0) {
            JsfUtil.addMessageError("Debe registrar un pago antes de continuar.");
            return "";
        }

        if (!pagoServiciosCategoriaIIBean.getCumpleMonto()) {
            JsfUtil.addMessageError("El monto de la transacción utilizada es insuficiente para completar el pago de tasas.");
            return "";
        }

        try {
            if (pagoServiciosCategoriaIIBean.Monto() > pagoServiciosCategoriaIIBean.costoTramite) {
                RequestContext.getCurrentInstance().execute("PF('dlg1').show();");
            } else if (pagoServiciosCategoriaIIBean.Monto() == pagoServiciosCategoriaIIBean.costoTramite) {
                completarPagoTarea();
            }
        } catch (JbpmException e) {
            LOGGER.error(e);
            JsfUtil.addMessageError("En estos momentos los servicios financieros están deshabilitados, por favor intente más tarde. Si este error persiste debe comunicarse con Mesa de Ayuda.");
        }
        return "";
    }
}