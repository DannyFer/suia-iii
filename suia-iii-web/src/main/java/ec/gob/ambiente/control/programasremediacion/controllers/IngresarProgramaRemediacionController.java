package ec.gob.ambiente.control.programasremediacion.controllers;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import ec.gob.ambiente.control.programasremediacion.bean.IngresarProgramaRemediacionBean;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.CronogramaActividades;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@RequestScoped
@ManagedBean
public class IngresarProgramaRemediacionController {

	@Getter
	@Setter
	@ManagedProperty(value = "#{ingresarProgramaRemediacionBean}")
	private IngresarProgramaRemediacionBean ingresarProgramaRemediacionBean;

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

	@EJB
	private CrudServiceBean crudServiceBean;

	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoFacade;

	private static final Logger LOGGER = Logger
			.getLogger(IngresarProgramaRemediacionController.class);

	public void adicionarActividadCronograma() {
		try {
			long processInstanceID = bandejaTareasBean.getTarea()
					.getProcessInstanceId();

			Map<String, Object> variable = procesoFacade
					.recuperarVariablesProceso(loginBean.getUsuario(), processInstanceID);
			Integer idProyectoLicenciamiento = Integer
					.parseInt((String) variable.get("proyectoActivo"));

			ProyectoLicenciamientoAmbiental proyecto = proyectoFacade
					.buscarProyectosLicenciamientoAmbientalPorId(idProyectoLicenciamiento);

			CronogramaActividades actividad;
			if (ingresarProgramaRemediacionBean.getActividadActivaId() == 0) {
				actividad = new CronogramaActividades();
			} else {
				actividad = ingresarProgramaRemediacionBean
						.getActividadActiva();
			}
			actividad.setResponsable(ingresarProgramaRemediacionBean
					.getResponsable());

			actividad.setIdProceso(bandejaTareasBean.getTarea()
					.getProcessInstanceId());
			actividad.setMediosVerificacion(ingresarProgramaRemediacionBean
					.getMediosVerificacion());
			actividad.setDescripcionActividades(ingresarProgramaRemediacionBean
					.getDescripcionActividades());
			actividad.setRequerimientos(ingresarProgramaRemediacionBean
					.getRequerimientos());
			actividad
					.setFechaInicio(new Timestamp(
							ingresarProgramaRemediacionBean.getFechaInicio()
									.getTime()));
			actividad.setFechaFin(new Timestamp(ingresarProgramaRemediacionBean
					.getFechaFin().getTime()));
			actividad.setProyectoLicenciamientoAmbiental(proyecto);

			if (ingresarProgramaRemediacionBean.getActividadActivaId() == 0) {
				List<CronogramaActividades> cronograma = ingresarProgramaRemediacionBean
						.getCronograma();
				cronograma.add(actividad);
				ingresarProgramaRemediacionBean.setCronograma(cronograma);
				crudServiceBean.saveOrUpdate(actividad);
			} else {

				crudServiceBean.saveOrUpdate(actividad);
			}
			ingresarProgramaRemediacionBean.limpiarActividadCronograma();
			JsfUtil.addMessageInfo("Se guardó la actividad con éxito.");
		} catch (JbpmException e) {
			LOGGER.error("Error al ingresar la actividad al cronograma.", e);
			JsfUtil.addMessageError("Error al ingresar la actividad al cronograma.");
		}
	}

	public String enviarProgramaRemediacion() {
		if (true) {
			try {

				Map<String, Object> params = new ConcurrentHashMap<String, Object>();
				SimpleDateFormat dt1 = new SimpleDateFormat("dd/MM/yyyy",
						Locale.getDefault());
				Date fecha = new Date();
				long processInstanceID = bandejaTareasBean.getTarea()
						.getProcessInstanceId();
				Map<String, Object> variable = procesoFacade
						.recuperarVariablesProceso(loginBean.getUsuario(), processInstanceID);

				Integer idProyectoLicenciamiento = Integer
						.parseInt((String) variable.get("proyectoActivo"));

				ProyectoLicenciamientoAmbiental proyecto = proyectoFacade
						.buscarProyectosLicenciamientoAmbientalPorId(idProyectoLicenciamiento);

				params.put("u_director", "msit");
				params.put("u_coordinador", "msit");
				// revisar
				params.put("u_director_ca", "msit");
				params.put("u_director_pn", "msit");
				params.put("u_director_pras", "msit");
				params.put(
						"mensaje_director_seguimiento",
						"El Sujeto de Control " + loginBean.getNombreUsuario()
								+ " " + " ingresó al proyecto '"
								+ proyecto.getNombre() + "' en la fecha "
								+ dt1.format(fecha)
								+ " un programa de remediación.");
				procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), processInstanceID,
						params);

				Map<String, Object> data = new ConcurrentHashMap<String, Object>();

				taskBeanFacade.approveTask(loginBean.getNombreUsuario(),
						bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), data,
						loginBean.getPassword(),
						Constantes.getUrlBusinessCentral(), Constantes.getRemoteApiTimeout(), Constantes.getNotificationService());

				JsfUtil.addMessageInfo("Se ingresó correctamente el Programa de remediación.");
				return JsfUtil.actionNavigateTo("/bandeja/bandejaTareas.jsf");
			} catch (JbpmException e) {
				LOGGER.error(
						"Error al iniciar la tarea de identificar proyecto del programa de remediación",
						e);
				JsfUtil.addMessageError("Ocurrio un error al enviar la información, intente mas tarde.");
			}

		} 
		return "";
	}
}
