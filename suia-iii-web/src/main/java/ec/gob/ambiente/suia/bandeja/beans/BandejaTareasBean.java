package ec.gob.ambiente.suia.bandeja.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.html.HtmlPanelGrid;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.core.converters.ListToStringConverter;
import ec.gob.ambiente.suia.comun.classes.Selectable;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.dto.TaskSummaryCustom;
import ec.gob.ambiente.suia.tramiteresolver.base.TramiteResolverClass;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@SessionScoped
public class BandejaTareasBean implements Serializable {

//	private final Logger LOG = Logger.getLogger(BandejaTareasBean.class);
	private static final long serialVersionUID = -7945028814622611935L;

	@Getter
	@Setter
	private List<TaskSummaryCustom> tareas;

	@Getter
	@Setter
	private TaskSummaryCustom tarea;

	@Getter
	@Setter
	private long processId;

	@Getter
	private TramiteResolverClass<?> resolverTramite;

	@Getter
	@Setter
	private HtmlPanelGrid htmlPanelGridTramite;
	
	@Getter
	@Setter
	private List<TaskSummaryCustom> tareasFirmaMasiva;
	
	@Getter
	@Setter
	private Boolean tramiteDiagnosticoAmbiental = false;
	
	@Getter
	@Setter
	private Boolean verObservacionesPlaguicidas = false;
	
	@Getter
	@Setter
	private List<ProyectoLicenciamientoAmbiental> registrosAmbientales;
		
	@Getter
	@Setter
	private List<Selectable<ProyectoLicenciamientoAmbiental>> listaProyectos;

	public void addTareas(Map<String, List<TaskSummaryCustom>> tasksMap) {
		setTareas(new ArrayList<TaskSummaryCustom>());
		boolean allErrors = true;
		List<String> errors = new ArrayList<String>();
		List<String> errorsAreas = new ArrayList<String>();
		if (tasksMap.get(TaskSummaryCustom.SOURCE_TYPE_INTERNAL) == null) {
			errors.add("No se han podido cargar las tareas de Registro Ambiental.");
			errorsAreas.add("Registro Ambiental");
		} else {
			getTareas().addAll(tasksMap.get(TaskSummaryCustom.SOURCE_TYPE_INTERNAL));
			allErrors = false;
		}
		if (tasksMap.get(TaskSummaryCustom.SOURCE_TYPE_EXTERNAL_SUIA) == null) {
			errors.add("No se han podido cargar las tareas de Licenciamiento Ambiental.");
			errorsAreas.add("Licenciamiento Ambiental");
		} else {
			getTareas().addAll(tasksMap.get(TaskSummaryCustom.SOURCE_TYPE_EXTERNAL_SUIA));
			allErrors = false;
		}
		if (tasksMap.get(TaskSummaryCustom.SOURCE_TYPE_EXTERNAL_HYDROCARBONS) == null) {
			errors.add("No se han podido cargar las tareas de Proyectos Hidrocarburíferos.");
			errorsAreas.add("Proyectos Hidrocarburíferos");
		} else {
			getTareas().addAll(tasksMap.get(TaskSummaryCustom.SOURCE_TYPE_EXTERNAL_HYDROCARBONS));
			allErrors = false;
		}

		if ((allErrors && !errors.isEmpty())
				|| (!Constantes.getAppIntegrationSuiaEnabled() && !Constantes.getAppIntegrationHydrocarbonsEnabled() && !errors
						.isEmpty()))
			JsfUtil.addMessageError("Ha ocurrido un error al cargar las tareas. Intente nuevamente y si el problema persiste, contacte a Mesa de Ayuda.");
		else {
			for (String error : errors) {
				JsfUtil.addMessageError(error);
			}
			if (!errorsAreas.isEmpty())
				JsfUtil.addMessageWarning("Si usted cuenta con actividades pendientes en <b>"
						+ ListToStringConverter.getListToString(errorsAreas) + "</b>, contacte a Mesa de Ayuda.");
		}
	}

	public void initHtmlPanelGridTramite() {
		try {
			resolverTramite = null;
			String clazz = getTarea().getProcedureResolverClass();
			if (clazz != null) {
				resolverTramite = (TramiteResolverClass<?>) Class.forName(clazz).newInstance();
				resolverTramite.setTramite(getTarea().getVariable(resolverTramite.getNombreVariableResolverTramite()));
				resolverTramite.initProponente();
				resolverTramite.initTramite();				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void initResolverTramite () {
		resolverTramite = null;
	}
	
	public String getCodigoLabel() {
		if(tarea.getProcessId().compareTo(Constantes.RCOA_DECLARACION_SUSTANCIA_QUIMICA)==0)
			return "Proyecto";
		return "Código";
	}
	
	public Boolean visualizarActividad() {
		String tareaDescargaGuias = Constantes.NEW_TASK_NAME_DESCARGA_GUIAS_ESIA;
		if(tarea != null && (tarea.getProcessId().compareTo(Constantes.RCOA_ESTUDIO_IMPACTO_AMBIENTAL)==0 
				|| tarea.getProcessId().compareTo(Constantes.RCOA_ESTUDIO_IMPACTO_AMBIENTAL_v2)==0)
				&& tarea.getTaskName().toUpperCase().equals(tareaDescargaGuias.toUpperCase()))
			return false;
		
		return true;
	}
}
