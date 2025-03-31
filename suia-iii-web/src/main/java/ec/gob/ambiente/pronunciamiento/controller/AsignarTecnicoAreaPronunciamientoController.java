package ec.gob.ambiente.pronunciamiento.controller;

import java.io.Serializable;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.jbpm.process.audit.ProcessInstanceLog;

import ec.gob.ambiente.core.interfaces.CompleteOperation;
import ec.gob.ambiente.pronunciamiento.bean.AsignarTecnicoAreaPronunciamientoBean;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.comun.bean.ReasignarTareaComunBean;
import ec.gob.ambiente.suia.domain.GeneradorDesechosPeligrosos;
import ec.gob.ambiente.suia.prevencion.registrogeneradordesechos.facade.RegistroGeneradorDesechosFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@RequestScoped
@ManagedBean
public class AsignarTecnicoAreaPronunciamientoController implements Serializable {

	private static final long serialVersionUID = 3334198305909510687L;

	private static final Logger LOGGER = Logger.getLogger(AsignarTecnicoAreaPronunciamientoController.class);
	
	@Getter
	@Setter
	@ManagedProperty(value = "#{proyectosBean}")
	private ProyectosBean proyectosBean;
	
	@EJB
	private ProcesoFacade procesoFacade;

	private String roleType;

	private String subarea;

	private ProcessInstanceLog proces;

	@Getter
	@Setter
	private CompleteOperation completeOperation;

	@Getter
	@Setter
	@ManagedProperty(value = "#{asignarTecnicoAreaPronunciamientoBean}")
	private AsignarTecnicoAreaPronunciamientoBean asignarTecnicoAreaPronunciamientoBean;

	@EJB
	private AreaFacade areaFacade;
	@EJB
    private RegistroGeneradorDesechosFacade registroGeneradorDesechosFacade;

	public void delegarTecnico() {

		String usuario = asignarTecnicoAreaPronunciamientoBean.getUsuario();
		JsfUtil.getBean(ReasignarTareaComunBean.class).initFunctionOnNotStatartedTask(proces.getExternalId(),
				JsfUtil.getBean(BandejaTareasBean.class).getTarea().getProcessInstanceId(),
				"Delegar a técnico para analizar la información y elaborar el pronunciamiento.", usuario,
				roleType, subarea, "/bandeja/bandejaTareas.jsf", new CompleteOperation() {

					public Object endOperation(Object object) {
						return null;
					}
				});
	}

	public void validarTareaBpm() {
		JsfUtil.validarPaginasUrlTareasBpm(JsfUtil.getBean(BandejaTareasBean.class).getTarea(),
				"/pronunciamiento/asignarTecnicoArea.jsf?area=" + asignarTecnicoAreaPronunciamientoBean.getArea());
	}

	@PostConstruct
	private void init() {
		try {
			//roleType = "role.pc.tecnico.Social";
			roleType = ("T\u00C9CNICO " + asignarTecnicoAreaPronunciamientoBean.getArea().replace("coordinador", "tecnico")).toUpperCase();

			// Busca los tecnicos de la misma area del proyecto
			if(JsfUtil.getBean(BandejaTareasBean.class).getTarea().getProcessId().equals(Constantes.NOMBRE_PROCESO_GENERADOR_DESECHOS)) {
				Map<String, Object> variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), 
						JsfUtil.getBean(BandejaTareasBean.class).getTarea().getProcessInstanceId());
				String idRegistroGenerador = (String) variables.get("idRegistroGenerador");
				Integer idGenerador = Integer.valueOf(idRegistroGenerador);
				
				GeneradorDesechosPeligrosos ultimoRGD = registroGeneradorDesechosFacade.get(idGenerador);
				subarea = ultimoRGD.getAreaResponsable().getAreaName();
				
				roleType = Constantes.getRoleAreaName(asignarTecnicoAreaPronunciamientoBean.getArea().replace("coordinador", "tecnico"));
			} else {
				subarea = proyectosBean.getProyecto().getAreaResponsable().getAreaName();
			}

			proces = procesoFacade.getProcessInstanceLog(JsfUtil.getLoggedUser(),
					JsfUtil.getBean(BandejaTareasBean.class).getTarea().getProcessInstanceId());

			/*if (completeOperation != null)
				roleType = completeOperation.toString();*/

		} catch (Exception exception) {
			LOGGER.error(JsfUtil.MESSAGE_ERROR_INICIAR_TAREA, exception);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_INICIAR_TAREA);
		}
	}

	public String obtenerNombreArea(String idAreaString) {
		String nombreArea = "Nombre área";
		try {
			idAreaString = idAreaString.replace("coordinador", "tecnico");
			nombreArea = Constantes.getRoleAreaName(idAreaString);

		} catch (Exception e) {
			LOGGER.error("Ocurrió un error al recuperar las especialidades y áreas del equipo multidischiplinario" + e);
		}
		return nombreArea;
	}
}
