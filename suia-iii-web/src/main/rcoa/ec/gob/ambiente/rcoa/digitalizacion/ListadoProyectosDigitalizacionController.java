package ec.gob.ambiente.rcoa.digitalizacion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;
import org.jbpm.process.audit.ProcessInstanceLog;
import org.kie.api.task.model.TaskSummary;
import org.primefaces.context.RequestContext;
import org.primefaces.model.LazyDataModel;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.rcoa.agrupacionAutorizaciones.facade.AutorizacionesAdministrativasFacade;
import ec.gob.ambiente.rcoa.agrupacionAutorizaciones.model.AutorizacionAdministrativa;
import ec.gob.ambiente.rcoa.digitalizacion.facade.AutorizacionAdministrativaAmbientalFacade;
import ec.gob.ambiente.rcoa.digitalizacion.facade.DocumentoDigitalizacionFacade;
import ec.gob.ambiente.rcoa.digitalizacion.facade.LicenciaAmbientalFisicaFacade;
import ec.gob.ambiente.rcoa.digitalizacion.facade.ProcesosDigitalizacionFacade;
import ec.gob.ambiente.rcoa.digitalizacion.facade.ProyectoAsociadoDigitalizacionFacade;
import ec.gob.ambiente.rcoa.digitalizacion.model.AutorizacionAdministrativaAmbiental;
import ec.gob.ambiente.rcoa.digitalizacion.model.DocumentoDigitalizacion;
import ec.gob.ambiente.rcoa.dto.EntityLicenciaFisica;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.bean.DatosOperadorRgdBean;
import ec.gob.ambiente.rcoa.util.BuscarDocumentosBean;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.comparator.OrdenarTareaPorEstadoComparator;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.AreaUsuario;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.dto.ResumeTarea;
import ec.gob.ambiente.suia.dto.Tarea;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.BeanLocator;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.ConvertidorObjetosDominioUtil;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.webservicesclientes.facade.JbpmSuiaCustomServicesFacade;
import lombok.Getter;
import lombok.Setter;

@ViewScoped
@ManagedBean
public class ListadoProyectosDigitalizacionController {
	
	Logger LOG = Logger.getLogger(ListadoProyectosDigitalizacionController.class);
	
	@EJB
	private LicenciaAmbientalFisicaFacade licenciaAmbientalFisicaFacade;
	
	@EJB
	private AutorizacionesAdministrativasFacade autorizacionesAdministrativasFacade;
	@EJB
	private ProyectoAsociadoDigitalizacionFacade proyectoAsociadoDigitalizacionFacade;
	@EJB
	private AutorizacionAdministrativaAmbientalFacade autorizacionAdministrativaAmbientalFacade;
	@EJB
	private ProcesoFacade procesoFacade;
	@EJB
	private DocumentosFacade documentosFacade;
	@EJB
	private ProcesosDigitalizacionFacade procesosDigitalizacionFacade;
	@EJB
	private DocumentoDigitalizacionFacade documentoDigitalizacionFacade;

	@Getter
	@Setter
	@ManagedProperty(value = "#{datosOperadorRgdBean}")
	private DatosOperadorRgdBean datosOperadorRgdBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{tareasProcesosBean}")
	private TareasProcesosBean tareasProcesosBean;
	
	@Getter
    @Setter
    @ManagedProperty(value = "#{buscarDocumentosBean}")
    private BuscarDocumentosBean buscarDocumentosBean;
	
	@Getter
	@Setter
	@ManagedProperty(value = "#{autorizacionAdministrativaAmbientalBean}")
	private AutorizacionAdministrativaAmbientalBean autorizacionAdministrativaAmbientalBean;
	
	@Getter
	@Setter
	private Boolean permisoFisico;
	
	@Getter
	@Setter
	private List<EntityLicenciaFisica> listaProyectosFisicos;
	
	@Getter
	@Setter
	private List<EntityLicenciaFisica> listaProyectosFisicosFilter;

	@Getter
	@Setter
	private LazyDataModel<AutorizacionAdministrativa> listaProyectosSuiaLazy;
	
	@Getter
	@Setter
	private List<AutorizacionAdministrativa> listaProyectosSuiaFilter;
	
	@Getter
	@Setter
	private List<AutorizacionAdministrativa> listaProyectosDigitalizados;
	
	@Getter
	@Setter
	private boolean esTecnico;
	
	@PostConstruct
	public void init(){
		try {
			bandejaTareasBean.setProcessId(0);
			listaProyectosFisicos = new ArrayList<EntityLicenciaFisica>();
			esTecnico = true;
			for (AreaUsuario areaUser : JsfUtil.getLoggedUser().getListaAreaUsuario()) {
				if (areaUser.getArea().getAreaName().contains("PROPONENTE")) {
					esTecnico = false;
					datosOperadorRgdBean.buscarDatosOperador(JsfUtil.getLoggedUser());
					break;
				}
			}
			if(esTecnico){
				listaProyectosDigitalizados = autorizacionesAdministrativasFacade.getProyectosDigitalizadosPorArea(JsfUtil.getLoggedUser().getId());
			}
		} catch (Exception e) {
			LOG.error("Error al iniciar la carga en digitalización", e);
		}
	}
	
	
	public void seleccionar(){
		RequestContext requestContext = RequestContext.getCurrentInstance();
		if(permisoFisico){
			cargarProyectosFisicos();
			requestContext.execute("PF('tableProyectos').clearFilters()");
			RequestContext.getCurrentInstance().update("form:pnlProyectosFisico:tableProyectos");
		}else{
			cargarProyectosSuia();
		}
	}
	
	public void cargarProyectosFisicos(){
		try {
			listaProyectosFisicos = new ArrayList<EntityLicenciaFisica>();
			String identificacion = null;
			if(!esTecnico){
				identificacion = JsfUtil.getLoggedUser().getNombre();
			}
			listaProyectosFisicos = licenciaAmbientalFisicaFacade.busquedaProyectos(identificacion);
		} catch (Exception e) {
			LOG.error("Error al iniciar la carga en digitalización", e);
		}
	}
	
	public void cargarProyectosSuia(){
		try {
			autorizacionAdministrativaAmbientalBean.setAutorizacionAdministrativaPrincipal(null);
			autorizacionAdministrativaAmbientalBean.setListaProyectosSeleccionados(null);
			autorizacionAdministrativaAmbientalBean.setAutorizacionFisicaSeleccionada(null);
			autorizacionAdministrativaAmbientalBean.setAutorizacionAdministrativaSeleccionada(null);
			autorizacionAdministrativaAmbientalBean.setIniciarRGD(false);

			listaProyectosSuiaLazy = new LazyProyectosDigitalizacionDataModel(null, esTecnico);
		} catch (Exception e) {
			LOG.error("Error al iniciar la carga en digitalización", e);
		}
		
	}
	
	public void redirigirNuevo(){
		JsfUtil.cargarObjetoSession("nuevoRegistro", true);
		JsfUtil.cargarObjetoSession("editardatos", false);
		JsfUtil.cargarObjetoSession("tramite", null);
		autorizacionAdministrativaAmbientalBean.setEsRegistroNuevo(true);
		autorizacionAdministrativaAmbientalBean.setIniciarRGD(false);
		autorizacionAdministrativaAmbientalBean.setAutorizacionAdministrativaPrincipal(null);
		autorizacionAdministrativaAmbientalBean.setListaProyectosSeleccionados(null);
		autorizacionAdministrativaAmbientalBean.setAutorizacionFisicaSeleccionada(null);
		autorizacionAdministrativaAmbientalBean.setAutorizacionAdministrativaSeleccionada(null);
		JsfUtil.redirectTo("/pages/rcoa/digitalizacion/ingresoInformacionAAA.jsf");
	}
	
	public void redirigirNuevoConRGD(){
		JsfUtil.cargarObjetoSession("nuevoRegistro", true);
		JsfUtil.cargarObjetoSession("editardatos", false);
		JsfUtil.cargarObjetoSession("tramite", null);
		autorizacionAdministrativaAmbientalBean.setEsRegistroNuevo(true);
		autorizacionAdministrativaAmbientalBean.setIniciarRGD(true);
		autorizacionAdministrativaAmbientalBean.setAutorizacionAdministrativaPrincipal(null);
		autorizacionAdministrativaAmbientalBean.setListaProyectosSeleccionados(null);
		autorizacionAdministrativaAmbientalBean.setAutorizacionFisicaSeleccionada(null);
		autorizacionAdministrativaAmbientalBean.setAutorizacionAdministrativaSeleccionada(null);
		JsfUtil.redirectTo("/pages/rcoa/digitalizacion/ingresoInformacionAAA.jsf");
	}
	
	public void completarInformacionFisico(EntityLicenciaFisica item){
		JsfUtil.cargarObjetoSession("nuevoRegistro", false);
		JsfUtil.cargarObjetoSession("editardatos", false);
		bandejaTareasBean.setProcessId(0);
		AutorizacionAdministrativaAmbiental autorizacion = autorizacionAdministrativaAmbientalFacade.obtenerAAAPorIdProyecto(item.getId(), 1);
		if(autorizacion != null && autorizacion.getCodigoProyecto() != null){
			if(autorizacion.getIdProceso() != null)
				bandejaTareasBean.setProcessId(autorizacion.getIdProceso());
			JsfUtil.cargarObjetoSession("tramite", autorizacion.getCodigoProyecto());
		}else{
			JsfUtil.cargarObjetoSession("tramite", null);
		}
		autorizacionAdministrativaAmbientalBean.setAutorizacionAdministrativaPrincipal(null);
		autorizacionAdministrativaAmbientalBean.setAutorizacionFisicaSeleccionada(item);
		autorizacionAdministrativaAmbientalBean.setAutorizacionAdministrativaSeleccionada(null);
		autorizacionAdministrativaAmbientalBean.setIniciarRGD(false);
		JsfUtil.redirectTo("/pages/rcoa/digitalizacion/ingresoFormDigitalizacion.jsf");
	}

	public void ingresarInformacionDigitalizacion(AutorizacionAdministrativa item){
		JsfUtil.cargarObjetoSession("nuevoRegistro", false);
		JsfUtil.cargarObjetoSession("editardatos", false);
		bandejaTareasBean.setProcessId(0);
		Integer sistema = 0;
		switch (item.getFuente()) {
		case "2":
			sistema = 4;
			break;
		case "1":
			sistema = 2;
			break;
		case "5":
			sistema = 3;
			break;

		default:
			break;
		}
		AutorizacionAdministrativaAmbiental autorizacion = autorizacionAdministrativaAmbientalFacade.obtenerAAAPorIdProyecto(item.getId(), sistema);
		autorizacionAdministrativaAmbientalBean.setAutorizacionAdministrativaSeleccionada(item);
		autorizacionAdministrativaAmbientalBean.setAutorizacionAdministrativaPrincipal(null);
		autorizacionAdministrativaAmbientalBean.setAutorizacionFisicaSeleccionada(null);
		autorizacionAdministrativaAmbientalBean.setIniciarRGD(false);
		if(autorizacion != null && autorizacion.getCodigoProyecto() != null){
			JsfUtil.cargarObjetoSession("tramite", autorizacion.getCodigoProyecto());
			if(autorizacion.getIdProceso() != null)
				bandejaTareasBean.setProcessId(autorizacion.getIdProceso());
			if(autorizacion.isFinalizado())
				JsfUtil.redirectTo("/pages/rcoa/digitalizacion/asociacionProyectoDigitalizacion.jsf");
			else
				JsfUtil.redirectTo("/pages/rcoa/digitalizacion/ingresoFormDigitalizacion.jsf");
		}else{
			JsfUtil.cargarObjetoSession("tramite", item.getCodigo());
			JsfUtil.redirectTo("/pages/rcoa/digitalizacion/ingresoFormDigitalizacion.jsf");
		}
	}
	
	public void completarInformacionSuia(AutorizacionAdministrativa item){
		JsfUtil.cargarObjetoSession("nuevoRegistro", false);
		JsfUtil.cargarObjetoSession("editardatos", false);
		bandejaTareasBean.setProcessId(0);
		Integer sistema = 0;
		switch (item.getFuente()) {
		case "2":
			sistema = 4;
			break;
		case "1":
			sistema = 2;
			break;
		case "5":
			sistema = 3;
			break;

		default:
			break;
		}
		AutorizacionAdministrativaAmbiental autorizacion = autorizacionAdministrativaAmbientalFacade.obtenerAAAPorIdProyecto(item.getId(), sistema);
		autorizacionAdministrativaAmbientalBean.setAutorizacionAdministrativaSeleccionada(item);
		autorizacionAdministrativaAmbientalBean.setAutorizacionAdministrativaPrincipal(null);
		autorizacionAdministrativaAmbientalBean.setAutorizacionFisicaSeleccionada(null);
		autorizacionAdministrativaAmbientalBean.setIniciarRGD(false);
		if(autorizacion != null && autorizacion.getCodigoProyecto() != null){
			JsfUtil.cargarObjetoSession("tramite", autorizacion.getCodigoProyecto());
			if(autorizacion.getIdProceso() != null)
				bandejaTareasBean.setProcessId(autorizacion.getIdProceso());
			if(autorizacion.isFinalizado())
				JsfUtil.redirectTo("/pages/rcoa/digitalizacion/asociacionProyectoDigitalizacion.jsf");
			else
				JsfUtil.redirectTo("/pages/rcoa/digitalizacion/ingresoFormDigitalizacion.jsf");
		}else{
			JsfUtil.cargarObjetoSession("tramite", item.getCodigo());
			JsfUtil.redirectTo("/pages/rcoa/digitalizacion/ingresoFormDigitalizacion.jsf");
		}
	}
	
	public void completarInformacionDigitalizacion(AutorizacionAdministrativa item){
		JsfUtil.cargarObjetoSession("nuevoRegistro", false);
		JsfUtil.cargarObjetoSession("editardatos", true);
		JsfUtil.cargarObjetoSession("tramite", item.getCodigo());
		autorizacionAdministrativaAmbientalBean.setAutorizacionAdministrativaPrincipal(null);
		autorizacionAdministrativaAmbientalBean.setAutorizacionAdministrativaSeleccionada(null);
		autorizacionAdministrativaAmbientalBean.setAutorizacionFisicaSeleccionada(null);
		autorizacionAdministrativaAmbientalBean.setIniciarRGD(false);
		JsfUtil.redirectTo("/pages/rcoa/digitalizacion/ingresoInformacionAAA.jsf");
	}
	
	public void asociar(AutorizacionAdministrativa autorizacionSeleccionada){
		try {
			AutorizacionAdministrativaAmbiental autorizacionAdministrativa = new AutorizacionAdministrativaAmbiental();
			autorizacionAdministrativa = autorizacionAdministrativaAmbientalFacade.obtenerAAAPorCodigoProyecto(autorizacionSeleccionada.getCodigo());
			if(autorizacionAdministrativa != null && autorizacionAdministrativa.getId() != null){
				JsfUtil.cargarObjetoSession("autorizacionAdministrativa", autorizacionAdministrativa);
				JsfUtil.redirectTo("/pages/rcoa/digitalizacion/asociacionProyectoDigitalizacion.jsf");
			}
		} catch (Exception e) {
			LOG.error("Error al enviar la información", e);
		}
	}
	
	public void RedirigirAAA(){
		JsfUtil.redirectTo("/pages/rcoa/digitalizacion/digitalizacionAAA.jsf");
	}
	
	private Long getProcessInstanceId(AutorizacionAdministrativa proyectoAsociado) throws JbpmException{
		List<ProcessInstanceLog> listProcessProject = new ArrayList<ProcessInstanceLog>();
		List<String> listaProcesso = new ArrayList<String>();
		switch (proyectoAsociado.getFuente()) {
		case "4": // proyectos SUIA
			listaProcesso.add(Constantes.NOMBRE_PROCESO_CATEGORIA2V2);
			listaProcesso.add(Constantes.NOMBRE_PROCESO_CATEGORIA2);
			listaProcesso.add(Constantes.NOMBRE_PROCESO_LICENCIA_AMBIENTAL);
			listProcessProject = procesoFacade.getProcessInstancesLogsVariableValue(JsfUtil.getLoggedUser(), Constantes.ID_PROYECTO, proyectoAsociado.getId().toString());
			break;
		case "10":  // RGD
			listaProcesso.add(Constantes.NOMBRE_PROCESO_GENERADOR_DESECHOS);
			listProcessProject = procesoFacade.getProcessInstancesLogsVariableValue(JsfUtil.getLoggedUser(), "idRegistroGenerador", proyectoAsociado.getId().toString());
			if(listProcessProject == null || listProcessProject.size() == 0){
				// RGD
				listaProcesso.add(Constantes.RCOA_REGISTRO_GENERADOR_DESECHOS);
				listProcessProject = procesoFacade.getProcessInstancesLogsVariableValue(JsfUtil.getLoggedUser(), "idProyecto", proyectoAsociado.getIdDigitalizacion().toString());
			}
			break;
		case "11":  // ART
			listaProcesso.add(Constantes.NOMBRE_PROCESO_APROBACION_REQUISITOS_TECNICOS);
			listProcessProject = procesoFacade.getProcessInstancesLogsVariableValue(JsfUtil.getLoggedUser(), "numeroSolicitud", proyectoAsociado.getCodigo());
			break;
		case "0":  // proyecto nuevo digitalizado
		case "1":  // proyecto fisico digitalizado
			listaProcesso.add(Constantes.PROCESO_DIGITALIZACION);
			listProcessProject = procesoFacade.getProcessInstancesLogsVariableValue(JsfUtil.getLoggedUser(), "tramite", proyectoAsociado.getCodigo());
			break;

		default:
			break;
		}
		Long proceso = 0L;
		Collections.sort(listProcessProject, new Comparator<ProcessInstanceLog>() {
			@Override
			public int compare(ProcessInstanceLog o1, ProcessInstanceLog o2) {
				return new Long(o1.getProcessInstanceId()).compareTo(new Long(o2.getProcessInstanceId()));
			}
		});
		
		if (listProcessProject != null) {
			for (int j = 0; j < listProcessProject.size(); j++) {
				if (listProcessProject.get(j).getStatus() != 4) {
					String nombreproceso = listProcessProject.get(j).getProcessId();
					if(listaProcesso.contains(nombreproceso)){
						proceso = listProcessProject.get(j).getProcessInstanceId();
					}
				}
			}
		}
		return proceso;
	}
	
	public void verTareasProceso(AutorizacionAdministrativa proyectoAsociado) throws JbpmException, ServiceException{
		tareasProcesosBean.setTareas(new ArrayList<Tarea>());
		Long procesoId = 0l;
		switch (proyectoAsociado.getFuente()) {
		case "2": // 4categorias
		case "3": // sector subsector
			tareasProcesosBean.setTareas(procesosDigitalizacionFacade.getTareas4Categorias(proyectoAsociado.getCodigo(), proyectoAsociado.getFuente()));
			break;
		case "0": // NUEVO digitalizacion 
		case "1": // fisico digitalizado
		case "4": // SUIA
		case "10": // RGD
		case "11": // ART
			procesoId = getProcessInstanceId(proyectoAsociado);
			tareasProcesosBean.setTareas(verTareas(procesoId, "Completado"));
			break;
		default:
			break;
		}
	}
	
    public List<Tarea> verTareas(Long processInstanceId, String estadoProceso) throws ServiceException {
        try {
            List<Tarea> tareasAux = new ArrayList<Tarea>();
            if (estadoProceso.equals("Completado")) {
                List<ResumeTarea> resumeTareas = BeanLocator.getInstance(JbpmSuiaCustomServicesFacade.class)
                        .getResumenTareas(processInstanceId);
                for (ResumeTarea resumeTarea : resumeTareas) {
                    Tarea tarea = new Tarea();
                    ConvertidorObjetosDominioUtil.convertirBamTaskSummaryATarea(resumeTarea, tarea);
                    tareasAux.add(tarea);
                }
                Collections.sort(tareasAux, new OrdenarTareaPorEstadoComparator());
            } else {
                List<TaskSummary> taskSummaries = procesoFacade.getTaskBySelectFlow(JsfUtil.getLoggedUser(),
                        processInstanceId);
                int longitud = taskSummaries.size();
                for (int i = 0; i < longitud; i++) {
                    Tarea tarea = new Tarea();
                    ConvertidorObjetosDominioUtil.convertirTaskSummaryATarea(taskSummaries.get(i), tarea);
                    tareasAux.add(tarea);
                }
                Collections.sort(tareasAux, new OrdenarTareaPorEstadoComparator());
            }
            return tareasAux;
        } catch (Exception exception) {
            LOG.error("Ocurrio un error en var tareas", exception);
            return null;
        }
    }


	public void verDocumentos(AutorizacionAdministrativa proyectoAsociado) {
		tareasProcesosBean.setDocumentos(new ArrayList<Documento>());
		try {
			Long procesoId = 0l;
			switch (proyectoAsociado.getFuente()) {
			case "2": // 4categorias
			case "3": // sector subsector
				//tareasProcesosBean.setDocumentos(procesosDigitalizacionFacade.getDocumentos4Categorias(proyectoAsociado.getCodigo(), proyectoAsociado.getFuente()));
				break;
			case "4": // SUIA
			case "10": // RGD
			case "11": // ART
				procesoId = getProcessInstanceId(proyectoAsociado);
				tareasProcesosBean.setDocumentos(documentosFacade.recuperarDocumentosConArchivosPorFlujoTodasVersionesNombresUnicos(procesoId));
				break;
			case "1": // proyectos fisicos
				//tareasProcesosBean.setDocumentos(procesosDigitalizacionFacade.getDocumentos4Categorias(proyectoAsociado.getId().toString(), proyectoAsociado.getFuente()));
				break;
			case "0": // digitalizados
				//tareasProcesosBean.setDocumentos(obtenerDocumentosDigitalizacion(proyectoAsociado.getId()));
				break;
			default:
				break;
			}
			if(tareasProcesosBean.getDocumentos().size() == 0){
				switch (proyectoAsociado.getFuente()) {
					case "10": // RGD
						tareasProcesosBean.setDocumentos(buscarDocumentosBean.buscarDocumentos(procesoId, true, null));
					break;

				default:
					tareasProcesosBean.setDocumentos(obtenerDocumentosDigitalizacion(proyectoAsociado.getIdDigitalizacion()));
					break;
				}
			}
		} catch (Exception e) {
			LOG.error("Error obteniendo documentos por proceso.", e);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}
	
	private List<Documento> obtenerDocumentosDigitalizacion(Integer autorizacionId){
		List<Documento> listaDocumentos = new ArrayList<Documento>();
		//mapa
		listaDocumentos = crearDocumento(autorizacionId, TipoDocumentoSistema.DIGITALIZACION_MAPA, listaDocumentos);
		// certificado de interseccion
		listaDocumentos = crearDocumento(autorizacionId, TipoDocumentoSistema.DIGITALIZACION_CERTIFICADO_INTERSECCION, listaDocumentos);
		//resolucion
		listaDocumentos = crearDocumento(autorizacionId, TipoDocumentoSistema.DIGITALIZACION_RESOLUCION, listaDocumentos);
		//ficha ambiental
		listaDocumentos = crearDocumento(autorizacionId, TipoDocumentoSistema.DIGITALIZACION_FICHA_AMBIENTAL, listaDocumentos);
		//estudio impacto ambiental
		listaDocumentos = crearDocumento(autorizacionId, TipoDocumentoSistema.DIGITALIZACION_ESTUDIO_IMPACTO_AMBIENTAL, listaDocumentos);
		//documentos habilitantes
		listaDocumentos = crearDocumento(autorizacionId, TipoDocumentoSistema.DIGITALIZACION_DOCUMENTOS_HABILITANTES, listaDocumentos);
		//documentos habilitantes
		listaDocumentos = crearDocumento(autorizacionId, TipoDocumentoSistema.DIGITALIZACION_OTROS_DOCUMENTOS, listaDocumentos);
		return listaDocumentos;
	}
	
	private List<Documento>  crearDocumento(Integer autorizacionId, TipoDocumentoSistema tipoDocumento, List<Documento> listaDocumentosDigitalizados){
		byte[] contenido;
		Documento documentoAux = new Documento();
		List<DocumentoDigitalizacion> listaDocumentosDig = documentoDigitalizacionFacade.descargarDocumentoDigitalizacion(autorizacionId, AutorizacionAdministrativaAmbiental.class.getSimpleName(), tipoDocumento);
		if(listaDocumentosDig != null && !listaDocumentosDig.isEmpty()){
			try {
				DocumentoDigitalizacion documentoDig = listaDocumentosDig.get(0);
				contenido = documentoDigitalizacionFacade.descargar(documentoDig.getIdAlfresco());
				if(contenido != null){
					documentoAux.setId(documentoDig.getId());
					documentoAux.setNombre(documentoDig.getNombre());
					documentoAux.setExtesion(documentoDig.getExtension());
					documentoAux.setDescripcion(documentoDig.getDescripcion());
					documentoAux.setIdAlfresco(documentoDig.getIdAlfresco());
					documentoAux.setContenidoDocumento(contenido);
					listaDocumentosDigitalizados.add(documentoAux);
				}
			} catch (CmisAlfrescoException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return listaDocumentosDigitalizados;
	}
}
