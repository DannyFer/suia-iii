package ec.gob.ambiente.prevencion.registroautorizacionesadministrativas.bean;

import java.io.Serializable;
import java.util.Map;

import org.apache.log4j.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.OficioObservaciones;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.oficioobservaciones.facade.OficioObservacionesFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class ElaborarOficioObservacionesBean implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4144104281532365233L;
	private static final Logger LOGGER = Logger
			.getLogger(ElaborarOficioObservacionesBean.class);
	@Setter
	@Getter
	private String asunto;

	@Setter
	@Getter
	private String observacion;

	@Setter
	@Getter
	private String correccion;

	@Setter
	@Getter
	private Boolean correcto;

	@EJB
	private OficioObservacionesFacade oficioObservacionesFacade;

	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;

	@EJB(lookup = Constantes.JBPM_EJB_TASK_BEAN)
	private TaskBeanFacade taskBeanFacade;

	@Getter
	@Setter
	private OficioObservaciones oficioObservaciones;
	@EJB
	private ProcesoFacade procesoFacade;

	@PostConstruct
	public void init() {
		asunto = "";
		observacion = "";

		correcto = true;
		try {
			oficioObservaciones = oficioObservacionesFacade
					.oficioObservacionesPorIdProceso(bandejaTareasBean
							.getTarea().getProcessInstanceId());
			if (oficioObservaciones != null) {

				asunto = oficioObservaciones.getAsunto();
				observacion = oficioObservaciones.getObservacion();
				try {
					Map<String, Object> variables = procesoFacade
							.recuperarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean
									.getTarea().getProcessInstanceId());
					if (variables.containsKey("correccion")) {
						correccion = (String) variables.get("correccion");
						correcto = false;
					}

				} catch (JbpmException e) {
					LOGGER.error("Error al obtener la información.", e);
					JsfUtil.addMessageError("Ocurrio un error al obtener la información.");
				}
			}

		} catch (Exception e) {

			LOGGER.error("Error al obtener la información.", e);

		}

	}
}
