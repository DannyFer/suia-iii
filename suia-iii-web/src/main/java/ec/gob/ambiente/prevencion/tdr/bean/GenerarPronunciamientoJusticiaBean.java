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
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.utils.Constantes;

@ManagedBean
@ViewScoped
public class GenerarPronunciamientoJusticiaBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5938875987939111853L;

	@EJB
	private ProcesoFacade procesoFacade;

	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoFacade;

	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;

	@Getter
	@Setter
	private String asunto;

	@Setter
	@Getter
	Boolean completado = false;

	@Setter
	@Getter
	private ProyectoLicenciamientoAmbiental proyectoActivo;

	@Getter
	@Setter
	@ManagedProperty(value = "#{proyectosBean}")
	private ProyectosBean proyectosBean;

	@Getter
	@Setter
	private boolean procesoIniciado;

	@Getter
	@Setter
	private boolean necesitaPronunciamientoJusticia;

	@Getter
	@Setter
	private String codigoOficio = "";

	@SuppressWarnings("unused")
	@PostConstruct
	public void init() {
		Map<String, String> params = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap();
		// inicialización de las variables para cargar pantalla
		try {
			// seleccionar el proyecto del proceso activo
			Map<String, Object> variables = procesoFacade
					.recuperarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getTarea()
							.getProcessInstanceId());
			Integer idProyecto = Integer.parseInt((String) variables
					.get(Constantes.ID_PROYECTO));

			if (idProyecto != null) {
				proyectoActivo = proyectoFacade
						.buscarProyectosLicenciamientoAmbientalPorId(idProyecto);
			}
		} catch (JbpmException e) {
		}
	}
}
