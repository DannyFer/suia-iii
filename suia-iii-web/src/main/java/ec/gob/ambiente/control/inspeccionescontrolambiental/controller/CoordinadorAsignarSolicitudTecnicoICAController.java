package ec.gob.ambiente.control.inspeccionescontrolambiental.controller;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import lombok.Getter;

import org.apache.log4j.Logger;
import org.jbpm.process.audit.ProcessInstanceLog;

import ec.gob.ambiente.core.interfaces.CompleteOperation;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.comun.bean.ReasignarTareaComunBean;
import ec.gob.ambiente.suia.control.inspeccionescontrolambiental.facade.InspeccionControlAmbientalFacade;
import ec.gob.ambiente.suia.domain.SolicitudInspeccionControlAmbiental;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class CoordinadorAsignarSolicitudTecnicoICAController implements Serializable {

	private static final long serialVersionUID = 295135559833572252L;

	private static final Logger LOGGER = Logger.getLogger(CoordinadorAsignarSolicitudTecnicoICAController.class);

	@EJB
	private ProcesoFacade procesoFacade;

	@EJB
	private AreaFacade areaFacade;

	@EJB
	private InspeccionControlAmbientalFacade inspeccionControlAmbientalFacade;

	private ProcessInstanceLog proces;
	private String roleType;
	private String subarea;

	@Getter
	private SolicitudInspeccionControlAmbiental solicitud;

	public void delegarTecnico() {
		JsfUtil.getBean(ReasignarTareaComunBean.class).initFunctionOnNotStatartedTask(
				proces.getExternalId(),
				JsfUtil.getCurrentProcessInstanceId(),
				"Delegar a técnico responsable para ejecutar la inspección de control" + ".", "tecnico",
				Constantes.getRoleAreaName(roleType), subarea, null, new CompleteOperation() {

					public Object endOperation(Object object) {
						return null;
					}
				});
	}

	@PostConstruct
	private void init() {
		subarea = null;
		try {
			Integer idSolicitud = Integer.parseInt(JsfUtil.getBean(BandejaTareasBean.class).getTarea()
					.getVariable(SolicitudInspeccionControlAmbiental.VARIABLE_ID_SOLICITUD).toString());

			solicitud = inspeccionControlAmbientalFacade.get(idSolicitud);
			boolean plantaCentral = solicitud.getAreaResponsable().getTipoArea().getSiglas()
					.equals(Constantes.getRoleAreaName("area.plantacentral"));

			roleType = plantaCentral ? "role.pc.tecnico" : "role.area.tecnico";
			subarea = solicitud.getAreaResponsable().getAreaName();

			proces = procesoFacade
					.getProcessInstanceLog(JsfUtil.getLoggedUser(), JsfUtil.getCurrentProcessInstanceId());
		} catch (Exception e) {
			LOGGER.error("Error al recuperar los datos de la inspeccion.", e);
		}
	}

	public void validarTareaBpm() {
		JsfUtil.validarPaginasUrlTareasBpm(JsfUtil.getCurrentTask(),
				"/control/inspeccionescontrolambiental/coordinadorSolicitudInspeccion.jsf");
	}
}
