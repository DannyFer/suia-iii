package ec.gob.ambiente.prevencion.certificado.ambiental.controllers;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;

import ec.gob.ambiente.core.interfaces.CompleteOperation;
import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.CertificadoAmbientalSumatoriaFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.InventarioForestalAmbientalFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.model.CertificadoAmbientalSumatoria;
import ec.gob.ambiente.rcoa.inventarioForestal.model.InventarioForestalAmbiental;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.comun.bean.PagoServiciosBean;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;

@ManagedBean
@ViewScoped
public class PagoCertificadoControllerV1 {
	
	
	private static final Logger LOG = Logger.getLogger(PagoCertificadoControllerV1.class);
	
	@EJB
	private ProcesoFacade procesoFacade;
	
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	@EJB
	private InventarioForestalAmbientalFacade inventarioForestalAmbientalFacade;
	@EJB
	private CertificadoAmbientalSumatoriaFacade certificadoAmbientalSumatoriaFacade;
	
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
			
			variables=procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
			tramite=(String)variables.get(Constantes.VARIABLE_PROCESO_TRAMITE);		
			
			proyecto = proyectoLicenciaCoaFacade.buscarProyecto(tramite);
			
			InventarioForestalAmbiental inventario = inventarioForestalAmbientalFacade.getByIdRegistroPreliminar(proyecto.getId());		
			
			CertificadoAmbientalSumatoria suma = new CertificadoAmbientalSumatoria();
			suma = certificadoAmbientalSumatoriaFacade.getByIdInventarioForestalAmbiental(inventario.getId());
			
			valorAPagar = suma.getPagoDesbroceCobertura();
			
			
			mensaje = 	"En cumplimiento al acuerdo ministerial 083-B publicado en el registro oficial edición especial Nro. 387 del 4 de noviembre del 2015. "+
		        	"Para continuar con el proceso de obtención del permiso ambiental, usted debe realizar el pago de Remoción de Cobertura Vegetal Nativa "+ 
		        	"por el valor de " + valorAPagar 
		        	+ " USD, el pago tendrá que realizar en la cuenta corriente No. 3001480596 sublínea No. 190499 correspondiente al MINISTERIO DEL AMBIENTE, AGUA Y TRANSICIÓN ECOLOGÍCA";
						
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public void validarTareaBpm() {
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
