package ec.gob.ambiente.prevencion.categoria1.controllers;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import ec.gob.ambiente.core.interfaces.CompleteOperation;
import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.comun.bean.PagoServiciosBean;
import ec.gob.ambiente.suia.domain.InventarioForestalPma;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.inventarioForestalPma.facade.InventarioForestalPmaFacade;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class PagoCAController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -648425166867760782L;



	private static final Logger LOG = Logger
			.getLogger(PagoCAController.class);

	@EJB
	private InventarioForestalPmaFacade inventarioForestalPmaFacade;
	
    @EJB(lookup = Constantes.JBPM_EJB_TASK_BEAN)
    private TaskBeanFacade taskBeanFacade;
    
    @ManagedProperty(value = "#{bandejaTareasBean}")
    @Getter
    @Setter
    private BandejaTareasBean bandejaTareasBean;
    
    @Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{proyectosBean}")
	private ProyectosBean proyectosBean;

	@Getter
	@Setter
	private InventarioForestalPma inventarioForestalPma;
	
	float costoTotal;

	@PostConstruct
	public void init() {
		try {
			
			inventarioForestalPma = inventarioForestalPmaFacade.obtenerInventarioForestalPmaPorProyecto(proyectosBean.getProyecto().getId());
			costoTotal = inventarioForestalPma.getMaderaEnPie() * Float.parseFloat(Constantes.getPropertyAsString("costo.factor.covertura.vegetal"));
			Map<String, Float> costoTotalProyecto= new ConcurrentHashMap<String, Float>();
			costoTotalProyecto.put("coberturaVegetal", costoTotal);
			costoTotalProyecto.put("valorAPagar", 0f);
			
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
            }, costoTotalProyecto, null,null,null,false);
			
		} catch (ServiceException e) {
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos de pago.");
		}
	}
	
    public void validarTareaBpm() {
        //JsfUtil.validarPaginasUrlTareasBpm(bandejaTareasBean.getTarea(), "/prevencion/certificadoambiental/pago.jsf");
    }
	
}