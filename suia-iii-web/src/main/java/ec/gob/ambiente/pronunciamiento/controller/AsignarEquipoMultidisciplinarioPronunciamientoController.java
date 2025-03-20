package ec.gob.ambiente.pronunciamiento.controller;

import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.pronunciamiento.bean.AsignarEquipoMultidisciplinarioPronunciamientoBean;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequestScoped
@ManagedBean
public class AsignarEquipoMultidisciplinarioPronunciamientoController implements Serializable {

	private static final long serialVersionUID = 8274287251237899566L;

	private static final Logger LOGGER = Logger
			.getLogger(AsignarEquipoMultidisciplinarioPronunciamientoController.class);
	@EJB
	private ProcesoFacade procesoFacade;

	@EJB
	private AreaFacade areaFacade;

	@EJB(lookup = Constantes.JBPM_EJB_TASK_BEAN)
	private TaskBeanFacade taskBeanFacade;

	@Getter
	@Setter
	@ManagedProperty(value = "#{asignarEquipoMultidisciplinarioPronunciamientoBean}")
	private AsignarEquipoMultidisciplinarioPronunciamientoBean asignarEquipoMultidisciplinarioPronunciamientoBean;

	public String asignarEquipoMultidiciplinario() {
		try {
			Map<String, Object> params = new ConcurrentHashMap<>();

			List<String> seleccionadas = Arrays.asList(asignarEquipoMultidisciplinarioPronunciamientoBean
					.getAreasSeleccionadas());

			String[] usuariosA = new String[seleccionadas.size()];

			Integer iterador = 0;
			for (String area : seleccionadas) {
				String[] temporal = area.split("--");
				String areaActual = temporal[0];
				Area areaResponsable = areaFacade.getAreaFull(Integer.parseInt(temporal[1]));

				Usuario coordinador = areaFacade.getUsuarioPorRolArea(areaActual, areaResponsable);
				if (coordinador != null) {
					params.put(areaActual, coordinador.getNombre());
					usuariosA[iterador++] = areaActual;
				} else {
					JsfUtil.addMessageError("Error al recuperar el coordinador");
					return "";
				}
			}

			params.put("listaAreasEquipo", usuariosA);

			params.put("equipoMultidisciplinarioDatosAdicionalesIndicaciones", asignarEquipoMultidisciplinarioPronunciamientoBean.getIndicaciones());
			procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), JsfUtil.getBean(BandejaTareasBean.class)
					.getTarea().getProcessInstanceId(), params);
			Map<String, Object> data = new ConcurrentHashMap<>();

			procesoFacade.aprobarTarea(JsfUtil.getBean(LoginBean.class).getUsuario(),
					JsfUtil.getBean(BandejaTareasBean.class).getTarea().getTaskId(),
					JsfUtil.getBean(BandejaTareasBean.class).getProcessId(), data);

			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
			return JsfUtil.actionNavigateTo("/bandeja/bandejaTareas.jsf");
		} catch (Exception e) {
			LOGGER.error(e);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
		return "";
	}

	public String obtenerNombreArea(String idAreaString) {

		String nombreArea = "Nombre área";
		try {
			int idArea = Integer
					.parseInt(idAreaString.substring(idAreaString.indexOf("--") + 2, idAreaString.length()));
			idAreaString = idAreaString.replace("coordinador", "tecnico");
			String especialidad = Constantes.getRoleAreaName(idAreaString.substring(0, idAreaString.indexOf("--")));

			Area area = areaFacade.getArea(idArea);
			if (area != null && especialidad != null && !especialidad.isEmpty()) {
				nombreArea = especialidad + " - " + area.getAreaName();
			}
		} catch (Exception e) {
			LOGGER.error("Ocurrió un error al recuperar las especialidades y áreas del equipo multidischiplinario" + e);
		}

		return nombreArea;
	}
}
