package ec.gob.ambiente.prevencion.tdr.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.entidadResponsable.facade.EntidadResponsableFacade;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class AsignarEquipoMultidiciplinarioBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3795570410203061744L;
	private static final Logger LOGGER = Logger
			.getLogger(AsignarEquipoMultidiciplinarioBean.class);

	@Getter
	@Setter
	private String[] areasSeleccionadas;

	@Getter
	@Setter
	private List<String> areas;

	@EJB
	ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;

	@EJB
	EntidadResponsableFacade entidadResponsableFacade;

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
	@EJB
	ProyectoLicenciamientoAmbientalFacade proyectoFacade;
	@EJB
	AreaFacade areaFacade;

	@PostConstruct
	public void init() {
		areas = new ArrayList<String>();

		try {

			Map<String, Object> variables = procesoFacade
					.recuperarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getTarea()
							.getProcessInstanceId());
			Integer idProyecto = Integer.parseInt((String) variables
					.get(Constantes.ID_PROYECTO));

			ProyectoLicenciamientoAmbiental proyecto = proyectoFacade
					.buscarProyectosLicenciamientoAmbientalPorId(idProyecto);

			Area areaProyecto = entidadResponsableFacade
					.obtenerEntidadResponsable(proyecto);
			areaProyecto = areaFacade.getAreaFull(areaProyecto.getId());

			if (areaProyecto.getTipoArea().getSiglas().equalsIgnoreCase("PC")) {

				areas.add("tecnicoSocial");
				areas.add("tecnicoCartografo");
				areas.add("tecnicoFisico");
				areas.add("tecnicoAmbiental");
				areas.add("tecnicoBiotico");
				areasSeleccionadas = new String[1];
				areasSeleccionadas[0] = "tecnicoSocial";
			} else {
				areas.add("tecnicoGeneral");
			}
		} catch (Exception e) {

			JsfUtil.addMessageError("Error al obtener los datos del proyecto.");
			LOGGER.error("Error al obtener los datos del proyecto.", e);

		}
	}

}
