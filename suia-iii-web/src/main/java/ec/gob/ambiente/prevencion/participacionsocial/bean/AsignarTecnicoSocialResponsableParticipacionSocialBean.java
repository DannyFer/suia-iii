package ec.gob.ambiente.prevencion.participacionsocial.bean;

import java.io.Serializable;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

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
public class AsignarTecnicoSocialResponsableParticipacionSocialBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4110811433754391492L;

	private static final Logger LOGGER = Logger
			.getLogger(AsignarTecnicoSocialResponsableParticipacionSocialBean.class);
	@EJB
	private ProcesoFacade procesoFacade;
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

	@Getter
	@Setter
	private String numeroFacilitadores;

	@PostConstruct
	public void init() {
		Map<String, Object> variables;
		try {
			variables = procesoFacade.recuperarVariablesProceso(loginBean.getUsuario(),
					bandejaTareasBean.getTarea().getProcessInstanceId());
			numeroFacilitadores = (String) variables.get("numeroFacilitadores");

		} catch (JbpmException e) {
			LOGGER.error("Error al recuperar los datos del proceso.", e);
		}
	}

}
