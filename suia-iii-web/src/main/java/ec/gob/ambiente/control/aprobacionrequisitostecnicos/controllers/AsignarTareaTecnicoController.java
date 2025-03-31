package ec.gob.ambiente.control.aprobacionrequisitostecnicos.controllers;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.jbpm.process.audit.ProcessInstanceLog;

import ec.gob.ambiente.control.aprobacionrequisitostecnicos.bean.AprobacionRequisitosTecnicosBean;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.comun.bean.ReasignarTareaComunBean;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.AprobacionRequisitosTecnicosFacade;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.AprobacionRequisitosTecnicos;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.webservice.facade.AsignarTareaFacade;

@ManagedBean
@ViewScoped
public class AsignarTareaTecnicoController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5353585185550961926L;

	private static final Logger LOG = Logger.getLogger(AsignarTareaTecnicoController.class);

	@Setter
	@Getter
	private AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos;

	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;
	@Getter
	@Setter
	@ManagedProperty(value = "#{reasignarTareaComunBean}")
	private ReasignarTareaComunBean reasignarTareaComunBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;

	@Setter
	@Getter
	@ManagedProperty(value = "#{aprobacionRequisitosTecnicosBean}")
	private AprobacionRequisitosTecnicosBean aprobacionRequisitosTecnicosBean;

	@EJB
	private AsignarTareaFacade asignarTareaFacade;

	@EJB
	private AprobacionRequisitosTecnicosFacade aprobacionRequisitosTecnicosFacade;

	@EJB
	private AreaFacade areaFacade;

	@EJB
	private ProcesoFacade procesoFacade;

	private Area area;

	private ProcessInstanceLog proces;

	@PostConstruct
	public void init() {

		try {
			proces = procesoFacade.getProcessInstanceLog(loginBean.getUsuario(), bandejaTareasBean.getTarea()
					.getProcessInstanceId());

			aprobacionRequisitosTecnicos = aprobacionRequisitosTecnicosFacade.recuperarAprobacionRequisitosTecnicos(
					bandejaTareasBean.getProcessId(), loginBean.getUsuario());

			area = aprobacionRequisitosTecnicos.getAreaResponsable();
		} catch (Exception e) {
			LOG.error("Error al consultar tecnicos", e);
			JsfUtil.addMessageError("No se pudo obtener los datos iniciales.");
		}
	}

	public void delegarTecnico() {
		reasignarTareaComunBean.initFunctionOnNotStatartedTask(proces.getExternalId(), bandejaTareasBean.getTarea()
				.getProcessInstanceId(), "Delegar a técnico responsable para analizar la información.", "tecnico",
				Constantes.getRoleAreaName(AprobacionRequisitosTecnicosFacade.ROLE_TECNICO_CALIDAD_AMBIENTAL), area
						.getAreaName(), null, null);
	}

}
