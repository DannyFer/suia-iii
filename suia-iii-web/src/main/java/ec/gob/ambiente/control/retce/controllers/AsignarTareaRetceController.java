package ec.gob.ambiente.control.retce.controllers;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.jbpm.process.audit.ProcessInstanceLog;

import ec.gob.ambiente.core.interfaces.CompleteOperation;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.retce.model.DescargasLiquidas;
import ec.gob.ambiente.retce.model.EmisionesAtmosfericas;
import ec.gob.ambiente.retce.model.GeneradorDesechosPeligrososRetce;
import ec.gob.ambiente.retce.model.GestorDesechosPeligrosos;
import ec.gob.ambiente.retce.services.DescargasLiquidasFacade;
import ec.gob.ambiente.retce.services.EmisionesAtmosfericasFacade;
import ec.gob.ambiente.retce.services.GeneradorDesechosPeligrososFacade;
import ec.gob.ambiente.retce.services.GestorDesechosPeligrososFacade;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.comun.bean.ReasignarTareaComunBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.dto.TaskSummaryCustom;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class AsignarTareaRetceController {
	
	private static final Logger LOG = Logger.getLogger(AsignarTareaRetceController.class);
    
    @EJB
    private ProcesoFacade procesoFacade;
    @EJB
	private GeneradorDesechosPeligrososFacade generadorDesechosPeligrososFacade;
    @EJB
	private AreaFacade areaFacade;
    @EJB
	private EmisionesAtmosfericasFacade emisionesAtmosfericasFacade;
    @EJB
	private DescargasLiquidasFacade descargasLiquidasFacade;
    @EJB
	private GestorDesechosPeligrososFacade gestorDesechosPeligrososFacade;

    @Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;

    @EJB(lookup = Constantes.JBPM_EJB_TASK_BEAN)
    private TaskBeanFacade taskBeanFacade;

    @Getter
    @Setter
    @ManagedProperty(value = "#{reasignarTareaComunBean}")
    private ReasignarTareaComunBean reasignarTareaComunBean;    

    public void delegarTecnico() {
        try {
        	ProcessInstanceLog proces = procesoFacade.getProcessInstanceLog(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getProcessInstanceId());
            Map<String, Object> variables=procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
            String roleKeyTecnico=(String)variables.get("role_key_tecnico");
            
            String tramite = (String)variables.get("tramite");
            String tipoTramite = (String)variables.get("tipo_tramite");
            Area areaTramite = null;
            
			switch (tipoTramite) {
			case "emisionesAtmosfericas":
				EmisionesAtmosfericas emisionAtmosferica = emisionesAtmosfericasFacade.findByCodigo(tramite);
				if(emisionAtmosferica != null)
					areaTramite = emisionAtmosferica.getInformacionProyecto().getAreaResponsable();
				break;
			case "descargas":
				DescargasLiquidas descargasLiquidas = descargasLiquidasFacade.findByCodigo(tramite);
				if(descargasLiquidas != null)
					areaTramite = descargasLiquidas.getInformacionProyecto().getAreaResponsable();
				break;
			case "generadorDesechos":
				GeneradorDesechosPeligrososRetce generador = generadorDesechosPeligrososFacade.getRgdRetcePorCodigo(tramite);
				if(generador != null)
					areaTramite = areaFacade.getArea(generador.getIdArea());
				break;
			case "gestorDesechos":
				GestorDesechosPeligrosos gestorDesechos = gestorDesechosPeligrososFacade.findByCodigo(tramite);
				if(gestorDesechos != null)
					areaTramite = areaFacade.getArea(gestorDesechos.getArea());
				break;

			default:
				break;
			}
			
			if(areaTramite == null) {
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
				return;
			}
            
            //buscar el rol del usuario y determinar cual debe asignar
            reasignarTareaComunBean.initFunctionOnNotStatartedTask(proces.getExternalId(),
                    bandejaTareasBean.getTarea().getProcessInstanceId(),
                    "Delegar a técnico para analizar la información..", "usuario_tecnico",
                    // Busca los tecnicos de la misma area del coordinador
                    Constantes.getRoleAreaName(roleKeyTecnico), areaTramite.getAreaName(),
                    "/bandeja/bandejaTareas.jsf", new CompleteOperation() {

                        public Object endOperation(Object object) {
                            Map<String, Object> data = new ConcurrentHashMap<String, Object>();

                            try {
                                procesoFacade.aprobarTarea(
                                		JsfUtil.getLoggedUser(),
                                        bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(),
                                        data);
                            } catch (JbpmException e) {
                                LOG.error(e);
                            }
                            return null;
                        }
                    });


            

                            
        } catch (JbpmException e) {
            JsfUtil.addMessageError("Error al procesar la información.");
            LOG.error(e);
        }
    }

    public void validarTareaBpm() {    	
    	TaskSummaryCustom taskSummaryCustom=bandejaTareasBean.getTarea();    	
		if (taskSummaryCustom == null|| taskSummaryCustom.getTaskSummary() == null || !taskSummaryCustom.getTaskSummary().getDescription().contains("/control/retce/asignarTarea")) {
			JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
		} 
    }
}