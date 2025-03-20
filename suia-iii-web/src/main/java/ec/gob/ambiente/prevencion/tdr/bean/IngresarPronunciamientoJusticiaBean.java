package ec.gob.ambiente.prevencion.tdr.bean;

import java.io.File;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.model.UploadedFile;

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
public class IngresarPronunciamientoJusticiaBean {

	private static final Logger LOGGER = Logger
			.getLogger(IngresarPronunciamientoJusticiaBean.class);

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

	@Setter
	@Getter
	private ProyectoLicenciamientoAmbiental proyectoActivo;

	@Getter
	@Setter
	@ManagedProperty(value = "#{proyectosBean}")
	private ProyectosBean proyectosBean;

	@Getter
	@Setter
	private UploadedFile fichero;

	@Setter
	@Getter
	private File pronunciamiento;

	@Setter
	@Getter
	private String pronunciamientoNombre = "";

	@Getter
	@Setter
	private boolean procesoIniciado;

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
                try {
                    proyectoActivo = proyectoFacade
                            .cargarProyectoFullPorId(idProyecto);
                }
                catch (Exception e){
                    LOGGER.error("Error cargando el proyecto", e);
                    JsfUtil.addMessageError("Error cargando los datos del proyecto.");
                }
			}
		} catch (JbpmException e) {
		}
	}

}
