package ec.gob.ambiente.rcoa.registroGeneradorDesechos.controllers;

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
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.comun.bean.PagoServiciosBean;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean	
@ViewScoped
public class PagoRGDController {
	
	
	private static final Logger LOG = Logger.getLogger(PagoRGDController.class);
	
	@EJB
	private ProcesoFacade procesoFacade;
	
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	
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
	
	
	float costoTotal = 100;
	
	@Getter
	@Setter
	private double valorAPagar;
	
	@Getter
	@Setter
	private boolean visualizarPopup = false;
	
	@Getter
	@Setter
	private String mensaje;
	
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyecto;
	
	private String tramite;
	
	private Map<String, Object> variables;
	
	@PostConstruct
	public void init(){
		try {
			
//			variables=procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
//			tramite=(String)variables.get(Constantes.VARIABLE_PROCESO_TRAMITE);		
//			
//			proyecto = proyectoLicenciaCoaFacade.buscarProyecto(tramite);
//			
//			InventarioForestalCertificado inventario = inventarioForestalCertificadoFacade.getByIdRegistroPreliminar(proyecto.getId());
//									
//			valorAPagar = inventario.getPagoDesbroceCobertura();
//			
			mensaje = "El pago corresponde a la tasa por concepto de Registro de Generador de Residuos o "
					+ "Desechos Peligrosos y/o Especiales, con un valor de 180 USD, los cuales deben "
					+ " ser depositados en BAN Ecuador, a la cuenta corriente No. 370102 a nombre del MINISTERIO DEL AMBIENTE ";
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public void validarTareaBpm() {
//        JsfUtil.validarPaginasUrlTareasBpm(bandejaTareasBean.getTarea(), "/prevencion/categoria2/v2/pagoServicios.jsf");
    }
	
	public void continuar(){
		
		Map<String, Float> costoTotalProyecto= new ConcurrentHashMap<String, Float>();		
		costoTotalProyecto.put("valorAPagar", (float) valorAPagar);
		
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
                    LOG.error("Error al aprobar la tarea de pago del proceso de Certificado Ambiental.", exp);
                }
                return null;
            }
        }, costoTotalProyecto, null,null,null,false);
	}
	
	public void cancelar(){
		
		JsfUtil.actionNavigateToBandeja();
	}

}
