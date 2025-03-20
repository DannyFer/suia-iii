package ec.gob.ambiente.control.programasremediacion.bean;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.cronogramaactividades.facade.CronogramaActividadesFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.CronogramaActividades;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.FileUtil;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class IngresarProgramaRemediacionBean implements Serializable {
	private static final long serialVersionUID = -3526371287113639646L;
        
	@Setter
	@Getter
	private UploadedFile file;

	@Setter
	@Getter
	private Map<String, File> listFileProgramaRemediacion;

	@Setter
	@Getter
	private List<String> listLabelProgramaRemediacion;

	@Setter
	@Getter
	private String activeLabel;

	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;

	@Getter
	private StreamedContent fileDownload;

	// datos del calendario de actividaes
	@Setter
	@Getter
	private String requerimientos;
	@Setter
	@Getter
	private String descripcionActividades;
	@Setter
	@Getter
	private Date fechaInicio;

	@Setter
	@Getter
	private Date fechaFin;
	@Setter
	@Getter
	private String responsable;
	@Setter
	@Getter
	private String mediosVerificacion;
	@Setter
	@Getter
	private long actividadActivaId;
	@Setter
	@Getter
	private CronogramaActividades actividadActiva;
	private Map<String, List<String>> listaIdentificadores;

	@Setter
	@Getter
	private List<CronogramaActividades> cronograma;

	@EJB
	private CronogramaActividadesFacade cronogramaFacade;
	@EJB
	private CrudServiceBean crudServiceBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;

	@EJB(lookup = Constantes.JBPM_EJB_TASK_BEAN)
	private TaskBeanFacade taskBeanFacade;

	@PostConstruct
	public void init() {
		listLabelProgramaRemediacion = new ArrayList<String>();
		listLabelProgramaRemediacion.add("1.0 Datos Generales");
		listLabelProgramaRemediacion.add("1.2 Descripción del evento.");
		listLabelProgramaRemediacion
				.add("1.3 Diagnóstico y caracterización de la Zona Afectada.");

		listFileProgramaRemediacion = FileUtil.readTmpFile(Long
				.toString(bandejaTareasBean.getTarea().getProcessInstanceId()));
		cronograma = new ArrayList<CronogramaActividades>();

		cronograma = cronogramaFacade
				.buscarCronogramaActividadesPorIdProceso(bandejaTareasBean
						.getTarea().getProcessInstanceId());
		actividadActivaId = 0;

		// ///////
		listaIdentificadores = new HashMap<String, List<String>>();

		// 1
		List<String> l1 = new ArrayList<String>();
		l1.add("1. Datos Generales");
		listaIdentificadores.put("1. Datos Generales", l1);
		// 2
		List<String> l2 = new ArrayList<String>();
		l2.add("2.1 Causas del evento");
		l2.add("2.2 Acciones inmediatas (se deberán incluir las acividades preliminares de limpieza realizadas)");
		l2.add("2.3.1 Tipo");
		l2.add("2.3.2 Volumen");
		l2.add("2.3.3 Características");
		listaIdentificadores.put("2. Descripción del evento", l2);

		// 3
		List<String> l3 = new ArrayList<String>();
		l3.add("3.1.1 Medio físico");
		l3.add("3.1.2 Medio Biótico");
		l3.add("3.1.3 Medio Socioeconómico (descripción de la ponlación y actividades que se realizan en la zona)");
		l3.add("3.2.1 Afectación directa");
		l3.add("3.2.2 Afectación indirecta");
		l3.add("3.3.1 Medio físico");
		l3.add("3.3.2 Medio Biótico");
		l3.add("3.3.3 Medio Socioeconómico");
		listaIdentificadores.put(
				"3. Diagnóstico y caracterización de la Zona Afectada", l3);

		// 4
		List<String> l4 = new ArrayList<String>();
		l4.add("4.1 Identifiación de impactos");
		l4.add("4.2 Evaluación de impactos");
		listaIdentificadores.put(
				"4. Identificación y evaluación de impactos ambientales", l4);

		// 5
		List<String> l5 = new ArrayList<String>();
		l5.add("5.1 Tratamiento de suelos");
		l5.add("5.2 Tratamiento de Vegetación Contaminada");
		l5.add("5.3 Tratamiento-Remediación de cuerpos Hídricos");
		listaIdentificadores.put(
				"5. Descripción de las tecnologías de remediación", l5);
		// 6
		List<String> l6 = new ArrayList<String>();
		l6.add("6. Análisis de alternativas tecnológicas");
		listaIdentificadores
				.put("6. Análisis de alternativas tecnológicas", l6);
		// 7
		List<String> l7 = new ArrayList<String>();
		l7.add("7. Metas de Remediación");
		listaIdentificadores.put("7. Metas de Remediación", l7);
		// 8
		List<String> l8 = new ArrayList<String>();
		l8.add("8. Técnicas de rehabilitación y uso posterior del sitio remediado");
		listaIdentificadores
				.put("8. Técnicas de rehabilitación y uso posterior del sitio remediado",
						l8);

		// 9
		List<String> l9 = new ArrayList<String>();
		l9.add("9.1 Definición de los puntos de monitoreo");
		l9.add("9.2 Frecuencia de monitoreo");
		listaIdentificadores.put("9. Plan de monitoreo ambiental", l9);

		// 10
		List<String> l10 = new ArrayList<String>();
		l10.add("10.1 Tipos de desechos");
		l10.add("10.2 Almacenamiento temporal");
		l10.add("10.3 Transporte");
		l10.add("10.4 Sistemas de eliminación");
		l10.add("10.5 Disposición final");
		listaIdentificadores.put("10. Plan de manejo de desechos", l10);

		// 11
		List<String> l11 = new ArrayList<String>();
		l11.add("11.1.1 Marco legal");
		l11.add("11.1.2 Metodología");
		l11.add("11.1.3 Superficie afectada (descripción)");
		l11.add("11.1.4 Descripción de afectaciones");
		l11.add("11.1.5 Valoraciones de daños (descripción)");
		l11.add("11.2.1 Marco legal");
		l11.add("11.2.2 Metodología");
		l11.add("11.2.3 Superficie afectada (descripción)");
		l11.add("11.2.4 Descripción de afectaciones");
		l11.add("11.2.5 Valoraciones de daños (descripción)");
		listaIdentificadores
				.put("11. Valoración de daños apicando el Acuerdo interministerial 001",
						l11);
		// 13
		List<String> l13 = new ArrayList<String>();
		l13.add("13. Referencias bibliográficas");
		listaIdentificadores.put("13. Referencias bibliográficas", l13);
		// 14
		List<String> l14 = new ArrayList<String>();
		l14.add("14. Anexos, registro fotográfico y pago de tasas");
		listaIdentificadores.put(
				"14. Anexos, registro fotográfico y pago de tasas", l14);
	}

	public void seleccionarProyectoActivo() {
	}

	public void handleFileUpload(FileUploadEvent event) {
		String path = FileUtil.addTmpFile(event, Long
				.toString(bandejaTareasBean.getTarea().getProcessInstanceId()),
				event.getFile().getFileName(), activeLabel);

		File f = new File(path);
		listFileProgramaRemediacion.put(activeLabel, f);

	}

	public void downloadFile() {
		if (listFileProgramaRemediacion.containsKey(activeLabel)) {
			fileDownload = FileUtil.downloadTmpFile(Long
					.toString(bandejaTareasBean.getTarea()
							.getProcessInstanceId()), activeLabel);
		}
	}

	public void deleteActiveFile() {
		FileUtil.deleteTmpFile(Long.toString(bandejaTareasBean.getTarea()
				.getProcessInstanceId()), activeLabel);
		listFileProgramaRemediacion.remove(activeLabel);
	}

	public void downloadFileD(String text) {
		FileUtil.downloadFile(Long.toString(bandejaTareasBean.getTarea()
				.getProcessInstanceId()), text);

	}


	public void editarActividadCronograma(CronogramaActividades actividad) {
		actividadActivaId = actividad.getId();
		// CronogramaActividades actividadActiva = cronogramaFacade
		// .buscarCronogramaActividadesPorId(actividadActivaId);

		actividadActiva = actividad;
		this.responsable = actividadActiva.getResponsable();
		this.mediosVerificacion = actividadActiva.getMediosVerificacion();
		this.descripcionActividades = actividadActiva
				.getDescripcionActividades();
		this.requerimientos = actividadActiva.getRequerimientos();
		this.fechaInicio = actividadActiva.getFechaInicio();
		this.fechaFin = actividadActiva.getFechaFin();
	}

	public void eliminarActividadCronograma(CronogramaActividades actividad) {
		crudServiceBean.delete(actividad);
		cronograma.remove(actividad);
		JsfUtil.addMessageInfo("Se eliminó la actividad con éxito.");
	}

	public void limpiarActividadCronograma() {
		this.responsable = "";
		this.mediosVerificacion = "";
		this.requerimientos = "";
		this.descripcionActividades = "";
		this.fechaInicio = new Date();
		this.fechaFin = new Date();
		this.actividadActivaId = 0;
	}

	public void scrollTo(String componentId) {
		RequestContext.getCurrentInstance().scrollTo(componentId);
	}

	public void actualizarFechaFin() {
		if (fechaFin.before(fechaInicio)) {
			fechaFin = (Date) fechaInicio.clone();
		}

	}

	public boolean validarLista(String idCadena) {

		if (listaIdentificadores.containsKey(idCadena)) {
			List<String> tmp = listaIdentificadores.get(idCadena);
			for (String llaves : tmp) {
				if (!this.listFileProgramaRemediacion.containsKey(llaves)) {
					return false;
				}
			}
			return true;
		} else {
			return this.listFileProgramaRemediacion.containsKey(idCadena);
		}
	}
}
