package ec.gob.ambiente.rcoa.registroGeneradorDesechos.controllers;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.kie.api.task.model.TaskSummary;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.dto.TaskSummaryCustom;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class ProcesoRegistroGeneradorController {
	
	private static final Logger LOG = Logger.getLogger(ProcesoRegistroGeneradorController.class);
	
	@EJB
	private ProcesoFacade procesoFacade;
	@EJB
	private SecuenciasFacade secuenciasFacade;
	
	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;
	
	@ManagedProperty(value = "#{bandejaTareasBean}")
	@Getter
	@Setter
	private BandejaTareasBean bandejaTareasBean;
	
	private Long idProceso;

	public void iniciarProceso(){
		if(iniciar()){
			bandejaTareasBean.setProcessId(idProceso);
			try {
				TaskSummary tareaActual = procesoFacade.getCurrenTask(JsfUtil.getLoggedUser(), idProceso);
				if(tareaActual != null){
					TaskSummaryCustom tarea = new TaskSummaryCustom();
					tarea.setTaskSummary(tareaActual);
					tarea.setTaskName(tareaActual.getName());
					tarea.setProcessName("Registro de Generador de Residuos y Desechos Peligrosos y/o Especiales");
					tarea.setProcessInstanceId(idProceso);
					tarea.setTaskId(tareaActual.getId());
					bandejaTareasBean.setTarea(tarea);
				}
			} catch (JbpmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        	JsfUtil.redirectTo("/pages/rcoa/generadorDesechos/informacionRegistroGeneradorREP.jsf");
		}
	}
	
	private boolean iniciar() {
		try {
			String tramite = generarNumeroSolicitud();
			Map<String, Object> parametros = new ConcurrentHashMap<String, Object>();
			parametros.put("operador", JsfUtil.getLoggedUser().getNombre());
			parametros.put("tramite", tramite);
			parametros.put("emisionVariosProyectos", false);
			parametros.put("responsabilidadExtendida", true);
			parametros.put(Constantes.USUARIO_VISTA_MIS_PROCESOS, JsfUtil.getLoggedUser().getNombre());
			parametros.put(Constantes.VARIABLE_TIPO_RGD, Constantes.TIPO_RGD_REP);
			idProceso = procesoFacade.iniciarProceso(JsfUtil.getLoggedUser(),
					Constantes.RCOA_REGISTRO_GENERADOR_DESECHOS, tramite,
					parametros);
			return true;
		} catch (Exception e) {
			JsfUtil.addMessageError("Ha ocurrido un error. Por favor comuníquese con Mesa de Ayuda.");
			LOG.error(e.getMessage() + " " + e.getCause().getMessage());
			return false;
		}
	}
	
	public String generarNumeroSolicitud() {
	try {
		return Constantes.SIGLAS_INSTITUCION + "-SOL-RGD-" + secuenciasFacade.getCurrentYear() + "-"
				+ secuenciasFacade.getNextValueDedicateSequence("MAAE-SOL-RGD",4);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
}
