package ec.gob.ambiente.prevencion.registrogeneradordesechos.controller;

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

import ec.gob.ambiente.core.interfaces.CompleteOperation;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.comun.bean.ReasignarTareaComunBean;
import ec.gob.ambiente.suia.domain.GeneradorDesechosPeligrosos;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.registrogeneradordesechos.facade.RegistroGeneradorDesechosFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class AsignarTecnicoResposableRGController implements Serializable {

	private static final long serialVersionUID = -3259687492908394088L;

	private static final Logger LOGGER = Logger.getLogger(AsignarTecnicoResposableRGController.class);

	@EJB
	private ProcesoFacade procesoFacade;

	@EJB
	private AreaFacade areaFacade;

	@EJB
	private RegistroGeneradorDesechosFacade registroGeneradorDesechosFacade;

	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{reasignarTareaComunBean}")
	private ReasignarTareaComunBean reasignarTareaComunBean;

	private ProcessInstanceLog proces;
	private String roleType;
	private String subarea;

	@Getter
	private GeneradorDesechosPeligrosos generador;

	public void delegarTecnico() {
		reasignarTareaComunBean.initFunctionOnNotStatartedTask(proces.getExternalId(), bandejaTareasBean.getTarea()
				.getProcessInstanceId(), "Delegar a técnico responsable para analizar la información.", "tecnico",
				Constantes.getRoleAreaName(roleType), subarea, "/bandeja/bandejaTareas.jsf", new CompleteOperation() {

					public Object endOperation(Object object) {
						return null;
					}
				});
	}

	@PostConstruct
	private void init() {
		subarea = null;
		try {
			Integer idGenerador = Integer.parseInt(JsfUtil.getBean(BandejaTareasBean.class).getTarea()
					.getVariable(GeneradorDesechosPeligrosos.VARIABLE_ID_GENERADOR).toString());

			generador = registroGeneradorDesechosFacade.get(idGenerador);
			boolean plantaCentral = generador.getAreaResponsable().getTipoArea().getSiglas()
					.equals(Constantes.getRoleAreaName("area.plantacentral"));

			roleType = plantaCentral ? "role.pc.tecnico" : "role.area.tecnico.registro.generador";
			subarea = generador.getAreaResponsable().getAreaName();

			proces = procesoFacade.getProcessInstanceLog(loginBean.getUsuario(), bandejaTareasBean.getTarea().getProcessInstanceId());
		} catch (Exception e) {
			LOGGER.error("Error al recuperar los datos del registro de generador.", e);
		}
	}

	public void validarTareaBpm() {
		JsfUtil.validarPaginasUrlTareasBpm(bandejaTareasBean.getTarea(),
				"/prevencion/registrogeneradordesechos/asignarTecnicoResponsable.jsf");
	}
}
