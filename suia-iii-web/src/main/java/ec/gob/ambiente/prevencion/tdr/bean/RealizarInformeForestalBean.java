package ec.gob.ambiente.prevencion.tdr.bean;

import java.io.Serializable;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;

@ManagedBean
@ViewScoped
public class RealizarInformeForestalBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2493121777913502962L;

	private static final Logger LOGGER = Logger
			.getLogger(RealizarInformeForestalBean.class);

	@Getter
	@Setter
	private String informe;

	@Getter
	@Setter
	private String area = "";

	@Getter
	@Setter
	private String observaciones;

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

	@EJB
	private ProcesoFacade procesoFacade;

	@PostConstruct
	public void init() {

		try {

			Map<String, String> params = FacesContext.getCurrentInstance()
					.getExternalContext().getRequestParameterMap();
			String area_tmp = params.get("area");
			if (area_tmp != null && !area_tmp.isEmpty()) {
				area = area_tmp;

			}
			// seleccionar el proyecto del proceso activo
			Map<String, Object> variables = procesoFacade
					.recuperarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getTarea()
							.getProcessInstanceId());

			if (variables.containsKey("informe" + area))
				informe = (String) variables.get("informe" + area);
			if (variables.containsKey("informe" + area + "Observacion"))
				observaciones = (String) variables.get("informe" + area
						+ "Observacion");

		} catch (JbpmException e) {
			LOGGER.error("Error al obtener la información.", e);
		}
	}

}
