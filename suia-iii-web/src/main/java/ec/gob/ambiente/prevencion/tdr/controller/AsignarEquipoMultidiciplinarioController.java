package ec.gob.ambiente.prevencion.tdr.controller;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.prevencion.tdr.bean.AsignarEquipoMultidiciplinarioBean;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.comun.bean.ReasignarTareaMultipleComunBean;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@RequestScoped
@ManagedBean
public class AsignarEquipoMultidiciplinarioController implements Serializable {

	private static final long serialVersionUID = -3524848484863L;
	private static final Logger LOGGER = Logger
			.getLogger(IniciarTdrController.class);
	@EJB
	private ProcesoFacade procesoFacade;

	@EJB
	private AreaFacade areaFacade;

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
	@ManagedProperty(value = "#{reasignarTareaMultipleComunBean}")
	private ReasignarTareaMultipleComunBean reasignarTareaMultipleComunBean;
	@Getter
	@Setter
	@ManagedProperty(value = "#{asignarEquipoMultidiciplinarioBean}")
	private AsignarEquipoMultidiciplinarioBean asignarEquipoMultidiciplinarioBean;

	public String asignarEquipoMultidiciplinario() {

		Map<String, Object> params = new ConcurrentHashMap<String, Object>();

		List<String> seleccionadas = Arrays
				.asList(asignarEquipoMultidiciplinarioBean
						.getAreasSeleccionadas());

		String usuariosA = "";
		params.put("requiereAmbiental", false);
		params.put("requiereFisico", false);
		params.put("requiereCartografo", false);
		params.put("requiereSocial", false);
		params.put("requiereBiotico", false);

		if (!seleccionadas.contains("tecnicoGeneral")
				&& !seleccionadas.contains("tecnicoSocial")) {
			JsfUtil.addMessageError("Debe seleccionar el Técnico Social.");
			return "";
		}
		if (seleccionadas.contains("tecnicoBiotico")) {
			Usuario coordinador = new Usuario(); // areaFacade
					//.getDirectorPlantaCentralPorArea("role.pc.coordinador.Biotico");
			params.put("requiereBiotico", true);
			params.put("u_CoordinadorBiotico", coordinador.getNombre());
			usuariosA += "Biotico, ";
			// System.out.println("u_TecnicoBiotico");
		}
		if (seleccionadas.contains("tecnicoSocial")) {
			Usuario coordinador = new Usuario(); // areaFacade
					//.getDirectorPlantaCentralPorArea("role.pc.coordinador.Social");
			params.put("requiereSocial", true);
			params.put("u_CoordinadorSocial", coordinador.getNombre());
			usuariosA += "Social, ";
			// System.out.println("u_TecnicoSocial");
		}
		if (seleccionadas.contains("tecnicoCartografo")) {
			Usuario coordinador = new Usuario(); // areaFacade
					//.getDirectorPlantaCentralPorArea("role.pc.coordinador.Cartografo");
			params.put("requiereCartografo", true);
			params.put("u_CoordinadorCartografo", coordinador.getNombre());
			usuariosA += "Cartografo, ";
			// System.out.println("u_TecnicoCartografo");
		}
		if (seleccionadas.contains("tecnicoFisico")) {
			Usuario coordinador = new Usuario(); // areaFacade
					//.getDirectorPlantaCentralPorArea("role.pc.coordinador.Fisico");
			params.put("requiereFisico", true);
			params.put("u_CoordinadorFisico", coordinador.getNombre());
			usuariosA += "Fisico, ";
			// System.out.println("u_TecnicoFisico");
		}
		if (seleccionadas.contains("tecnicoAmbiental")) {
			Usuario coordinador = new Usuario(); // areaFacade
					//.getDirectorPlantaCentralPorArea("role.pc.coordinador.Ambiental");
			params.put("requiereAmbiental", true);
			params.put("u_CoordinadorAmbiental", coordinador.getNombre());
			usuariosA += "Ambiental, ";
			// System.out.println("u_TecnicoAmbiental");
		}
		if (seleccionadas.contains("tecnicoGeneral")) {

			try {
				Map<String, Object> variables = procesoFacade
						.recuperarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getTarea()
								.getProcessInstanceId());
				String tipoAreaProyecto = (String) variables
						.get("tipoAreaProyecto");

				if (!tipoAreaProyecto
						.equalsIgnoreCase(Constantes.SIGLAS_TIPO_AREA_PC)) {
					/*Integer areaProyectoId = Integer
							.parseInt((String) variables.get("areaProyectoId"));*/
					//Area areaProyecto = areaFacade.getArea(areaProyectoId);

					Usuario coordinador = new Usuario(); // areaFacade
							//.getCoordinadorPorArea(areaProyecto);
					params.put("requiereAmbiental", true);
					params.put("u_CoordinadorGeneral", coordinador.getNombre());
					usuariosA += "General, ";
				} else {

					JsfUtil.addMessageError("Error al procesar los datos.");
					return "";
				}

			} catch (JbpmException e) {
				JsfUtil.addMessageError("Error al recuperar los datos del proyecto.");
				LOGGER.error("Error al recuperar los datos del proyecto.", e);
				return "";
			}

			// System.out.println("u_TecnicoAmbiental");
		}
		params.put("usuariosA", usuariosA);

		try {
			procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean
					.getTarea().getProcessInstanceId(), params);
			Map<String, Object> data = new ConcurrentHashMap<String, Object>();
			taskBeanFacade.approveTask(loginBean.getNombreUsuario(),
					bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), data,
					loginBean.getPassword(),
					Constantes.getUrlBusinessCentral(), Constantes.getRemoteApiTimeout(), Constantes.getNotificationService());

			JsfUtil.addMessageInfo("Se realizó correctamente la operación.");
			return JsfUtil.actionNavigateTo("/bandeja/bandejaTareas.jsf");
		} catch (JbpmException e) {
			LOGGER.error(e);
			JsfUtil.addMessageError("Error al realizar la operación.");
		}
		return "";
		//
		// reasignarTareaMultipleComunBean.initFunctionOnNotStatartedTask(
		// bandejaTareasBean.getTarea().getProcessInstanceId(),
		// "Asignar equipo multidiciplinario",
		// variablesFormUsersNamesAndRoles, null, new CompleteOperation() {
		//
		// public Object endOperation(Object object) {
		// Map<String, Object> data = new ConcurrentHashMap<String, Object>();
		//
		// try {
		// taskBeanFacade.approveTask(
		// loginBean.getNombreUsuario(),
		// bandejaTareasBean.getTarea().getTaskId(), data,
		// Constantes.getDeploymentId(),
		// loginBean.getPassword(),
		// Constantes.URL_BUSINESS_CENTRAL);
		// } catch (JbpmException e) {
		// LOGGER.error(e);
		// }
		// return null;
		// }
		// });
	}
}
