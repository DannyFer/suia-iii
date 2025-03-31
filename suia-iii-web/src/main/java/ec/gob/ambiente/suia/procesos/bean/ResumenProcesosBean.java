/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.procesos.bean;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.jbpm.process.audit.ProcessInstanceLog;
import org.kie.api.task.model.TaskSummary;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.rcoa.facade.DocumentosProcesoRcoaFacade;
import ec.gob.ambiente.rcoa.util.BuscarDocumentosBean;
import ec.gob.ambiente.suia.comparator.OrdenarTareaPorEstadoComparator;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.desechoPeligrosoFisico.service.ProcessedService;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.RolUsuario;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.desechosfisicos.Processed;
import ec.gob.ambiente.suia.dto.ResumeTarea;
import ec.gob.ambiente.suia.dto.ResumenInstanciaProceso;
import ec.gob.ambiente.suia.dto.Tarea;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;
import ec.gob.ambiente.suia.utils.BeanLocator;
import ec.gob.ambiente.suia.utils.ConexionBpms;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.ConvertidorObjetosDominioUtil;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.webservicesclientes.facade.JbpmSuiaCustomServicesFacade;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author carlos.pupo
 * @version Revision: 1.0
 *          <p>
 *          [Autor: carlos.pupo, Fecha: 31/08/2015]
 *          </p>
 */
@ManagedBean
@ViewScoped
public class ResumenProcesosBean implements Serializable {

	private static final long serialVersionUID = -5336977660064571036L;
	
	@Getter
    @Setter
    @ManagedProperty(value = "#{buscarDocumentosBean}")
    private BuscarDocumentosBean buscarDocumentosBean;
	
	@EJB
	private DocumentosProcesoRcoaFacade documentosProcesoRcoaFacade;

	@EJB
	protected ProcesoFacade procesoFacade;

	@EJB
	private DocumentosFacade documentosFacade;
	
	@EJB
	private ConexionBpms conexionBpms;

	@Getter
	protected Map<String, List<ResumenInstanciaProceso>> process;

	private List<String> processNames;

	@Setter
	private List<Documento> documentos;

	@Setter
	private List<Tarea> tareas;

	private static final Logger LOG = Logger.getLogger(ResumenProcesosBean.class);
	
	@Getter
	private List<Processed> processeds = new ArrayList<Processed>();
	
	@EJB
	private ProcessedService processedService;
	
	@EJB
	private UbicacionGeograficaFacade ubicacionGeograficaFacade;
	
	@EJB
    ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade; 
	
	@Getter
	@Setter
    private LazyDataModel <ResumenInstanciaProceso> listarProcesoPaginador;

	@PostConstruct
	public void init() {
		try {
			
			process = new HashMap<String, List<ResumenInstanciaProceso>>();

			List<ProcessInstanceLog> processHistoryUser = procesoFacade
					.getProcessInstancesLogsVariableValueUpdated(JsfUtil.getLoggedUser(),
							Constantes.USUARIO_VISTA_MIS_PROCESOS, JsfUtil.getLoggedUser().getNombre());

			Map<String, Object> variablesProceso = new HashMap<String, Object>();

			for (ProcessInstanceLog log : processHistoryUser) {
				if (!process.containsKey(log.getProcessName()))
					process.put(log.getProcessName(), new ArrayList<ResumenInstanciaProceso>());

				variablesProceso=procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), log.getProcessInstanceId());

				String tramite = "(Desconocido)";
				tramite=(String)variablesProceso.get("tramite")==null?tramite:(String)variablesProceso.get("tramite");
				ResumenInstanciaProceso proceso = new ResumenInstanciaProceso(procesoFacade.getProcessInstanceLog(JsfUtil.getLoggedUser(), log.getProcessInstanceId()), tramite);
				// aumentado para mostrar resumen de rgd
				if(proceso.getProcessId().contains("rcoa.RegistrodeGeneradordeDesechosPeligrososyEspeciales")){
					String tipoRGD = (String)variablesProceso.get(Constantes.VARIABLE_TIPO_RGD);
					proceso.setMostrarResumenRgd(tipoRGD);
				}
				if(proceso.getEstado()!=4){
					if(proceso.getEstado()==2)
					{
						proceso.setFin(conexionBpms.processInsLogEndDate(log.getProcessInstanceId()));
					}
					if(proceso.getTramite().startsWith("MAE-RA") 
						|| proceso.getTramite().startsWith("MAAE-RA")
						|| proceso.getTramite().startsWith("MAATE-RA"))
					{
						if(!proyectoLicenciamientoAmbientalFacade.getRGDArchivado(proyectoLicenciamientoAmbientalFacade.getProyectoPorCodigo(proceso.getTramite(), true))){
							process.get(log.getProcessName()).add(proceso);
						}
					}
					else
					{
						if(!proyectoLicenciamientoAmbientalFacade.getRGDArchivado(proceso.getTramite()))
							process.get(log.getProcessName()).add(proceso);
					}
				}
				
				if(proceso.getProcessId().contains("rcoa.DeclaracionSustanciasQuimicas")){
					proceso.setDeshabilitarDocumentos(true);
					if(proceso.getFin() == null){
						proceso.setDeshabilitarTareas(true);
					}else{
						proceso.setDeshabilitarTareas(false);
					}					
				}else{
					proceso.setDeshabilitarDocumentos(false);
					proceso.setDeshabilitarTareas(false);
				}
			}
			HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
			String url = request.getRequestURI();
			if (url.equals("/suia-iii/procesos/listProcessed.jsf")) {
				fillProcesseds();
			}

		} catch (Exception exception) {
			JsfUtil.addMessageError("Error al cargar el resumen de procesos.");
			LOG.error("No se puede obtener el resumen de procesos", exception);
		}
	}

	public void verTareas(ResumenInstanciaProceso resumenInstanciaProceso) {
		this.tareas = null;
		try {
			List<Tarea> tareas = new ArrayList<Tarea>();
			if (resumenInstanciaProceso.getEstadoString().equals("Completado")) {
				List<ResumeTarea> resumeTareas = BeanLocator.getInstance(JbpmSuiaCustomServicesFacade.class)
						.getResumenTareas(resumenInstanciaProceso.getProcessInstanceId());
				for (ResumeTarea resumeTarea : resumeTareas) {
					Tarea tarea = new Tarea();
					if (!(resumeTarea.getStatus().equals("Exited") || resumeTarea.getStatus().equals("Created") || resumeTarea.getStatus().equals("Ready"))){
					ConvertidorObjetosDominioUtil.convertirBamTaskSummaryATarea(resumeTarea, tarea);
					if(!tareas.contains(tarea))
						tareas.add(tarea);
					}
				}
				Collections.sort(tareas, new OrdenarTareaPorEstadoComparator());
				this.tareas = tareas;

			} else {
				List<TaskSummary> taskSummaries = procesoFacade.getTaskBySelectFlow(JsfUtil.getLoggedUser(),
						resumenInstanciaProceso.getProcessInstanceId());
				int longitud = taskSummaries.size();
				for (int i = 0; i < longitud; i++) {
					Tarea tarea = new Tarea();
					ConvertidorObjetosDominioUtil.convertirTaskSummaryATarea(taskSummaries.get(i), tarea);
					if(!tareas.contains(tarea))
						tareas.add(tarea);
				}

				Collections.sort(tareas, new OrdenarTareaPorEstadoComparator());
				this.tareas = tareas;
			}
		} catch (Exception e) {
			e.getMessage();
			LOG.error("Error obteniendo tareas por proceso.", e);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}

	public void verDocumentos(ResumenInstanciaProceso resumenInstanciaProceso) {
		try {
			this.documentos = null;
			
			if(resumenInstanciaProceso.getProcessId().toUpperCase().contains("RCOA")) {				
				this.documentos = buscarDocumentosBean.buscarDocumentos(resumenInstanciaProceso.getProcessInstanceId(), true, null);
				
			} else {
				if(resumenInstanciaProceso.getProcessId().equals("suia.participacion-social")){
					this.documentos = documentosFacade
							.recuperarDocumentosConArchivosPorFlujoTodasVersiones(resumenInstanciaProceso
									.getProcessInstanceId());
				}else{					
					if (resumenInstanciaProceso.getProcessId().equals("mae-procesos.GeneradorDesechos")
							&& isSujetoControl(JsfUtil.getLoggedUser())) {
						List<Documento> listaDocumentos = documentosFacade
								.recuperarDocumentosConArchivosPorFlujoTodasVersionesNombresUnicos(resumenInstanciaProceso.getProcessInstanceId());
						this.documentos = new ArrayList<>();
						for (Documento documento : listaDocumentos) {
							if (documento.getTipoDocumento().getId() != 2500)
								this.documentos.add(documento);
						}
					} else {
						this.documentos = documentosFacade
								.recuperarDocumentosConArchivosPorFlujoTodasVersionesNombresUnicos(resumenInstanciaProceso
										.getProcessInstanceId());
					}
				}		
			}
		} catch (Exception e) {
			LOG.error("Error obteniendo documentos por proceso.", e);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}
	
	public boolean isSujetoControl(Usuario usuario){
		for (RolUsuario rolUsuario : usuario.getRolUsuarios()) {
			if(rolUsuario.getRol().getNombre().contains("sujeto")){
					return true;
			}
		}
		return false;
	}

	public StreamedContent getStream(Documento documento) throws CmisAlfrescoException {
		if (documento.getContenidoDocumento() != null) {
			byte[] archivoObtenido = documentosProcesoRcoaFacade.descargar(documento.getIdAlfresco(), documento.getFechaCreacion());
			documento.setContenidoDocumento(archivoObtenido);
			InputStream is = new ByteArrayInputStream(documento.getContenidoDocumento());
			return new DefaultStreamedContent(is, documento.getMime(), documento.getNombre());
		} else {
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
		return null;
	}

	public List<Documento> getDocumentos() {
		return documentos == null ? documentos = new ArrayList<Documento>() : documentos;
	}

	public List<Tarea> getTareas() {
		return tareas == null ? tareas = new ArrayList<Tarea>() : tareas;
	}

	public void onToggleProcess(ToggleEvent event) {

	}

	public List<String> getProcessNames() {
		processNames = processNames == null ? processNames = new ArrayList<String>() : processNames;
		if (process != null && processNames.isEmpty()) {
			processNames.addAll(process.keySet());
			Collections.sort(processNames);
		}
		return processNames;
	}
	
	public void fillProcesseds()
	{
		processeds = new ArrayList<Processed>();
		processeds = processedService.getProcessedByCompanyTypeInstallation(1, "");	
	}
	
	public String getProvinciaByCantonId(Integer cantonId){
		UbicacionesGeografica ubicacionesGeografica= new UbicacionesGeografica();
		String nombreProvincia="";
		try {
			ubicacionesGeografica=ubicacionGeograficaFacade.buscarPorId(cantonId);
			nombreProvincia=ubicacionesGeografica.getUbicacionesGeografica().getNombre();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return nombreProvincia;
	}
	
	public void verRgd(ResumenInstanciaProceso resumenInstanciaProceso){
		JsfUtil.cargarObjetoSession("idProceso",resumenInstanciaProceso.getProcessInstanceId());
		if (resumenInstanciaProceso.getMostrarResumenRgd().equals(Constantes.TIPO_RGD_REP))
			JsfUtil.redirectTo("/pages/rcoa/generadorDesechos/informacionRegistroGeneradorVerREP.jsf");
		else
			JsfUtil.redirectTo("/pages/rcoa/generadorDesechos/informacionRegistroGeneradorVer.jsf");
	}
	
}
