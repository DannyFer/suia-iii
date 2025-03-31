package ec.gob.ambiente.prevencion.tdr.bean;

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
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class RevisarDocumentacionGeneralBean implements Serializable {
	private static final Logger LOGGER = Logger
			.getLogger(RevisarDocumentacionGeneralBean.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = -2013860911401149549L;

	@Getter
	@Setter
	private boolean requiereModificacion;

	@Getter
	@Setter
	private boolean cumpleCriterios;

	@Getter
	@Setter
	private String criteriosModificacion = "";

	@EJB
	private ProcesoFacade procesoFacade;

	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;

	@PostConstruct
	public void init() {

		// seleccionar el proyecto del proceso activo

		try {
			Map<String, Object> variables;
			variables = procesoFacade.recuperarVariablesProceso(loginBean.getUsuario(),
					bandejaTareasBean.getTarea().getProcessInstanceId());
			cumpleCriterios = Boolean.parseBoolean((String) variables
					.get("cumpleCriterios"));

		} catch (JbpmException e) {
			LOGGER.error("Error al cargar los datos.", e);
			JsfUtil.addMessageError("Error al cargar los datos.");
		}

	}
}
