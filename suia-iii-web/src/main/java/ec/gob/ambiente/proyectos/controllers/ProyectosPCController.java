package ec.gob.ambiente.proyectos.controllers;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
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

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.jbpm.process.audit.ProcessInstanceLog;
import org.kie.api.task.model.TaskSummary;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.integracion.facade.IntegracionFacade;
import ec.gob.ambiente.proyectos.bean.ProyectosAdminBean;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.proyectos.bean.ResumenYEstadosEtapasBean;
import ec.gob.ambiente.proyectos.datamodel.LazyProyectPCDataModel;
import ec.gob.ambiente.rcoa.facade.DocumentosCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.model.DocumentosCOA;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.suia.administracion.facade.ContactoFacade;
import ec.gob.ambiente.suia.administracion.facade.RolFacade;
import ec.gob.ambiente.suia.comparator.OrdenarTareaPorEstadoComparator;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.FacilitadorProyecto;
import ec.gob.ambiente.suia.domain.Flujo;
import ec.gob.ambiente.suia.domain.FormasContacto;
import ec.gob.ambiente.suia.domain.ProcesoSuspendido;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.TipoSector;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.dto.ProyectoCustom;
import ec.gob.ambiente.suia.dto.ResumeTarea;
import ec.gob.ambiente.suia.dto.Tarea;
import ec.gob.ambiente.suia.eia.ficha.bean.EstudioImpactoAmbientalBean;
import ec.gob.ambiente.suia.facilitador.facade.FacilitadorProyectoFacade;
import ec.gob.ambiente.suia.integracion.bean.ContenidoExterno;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.notificacionautoridades.controllers.NotificacionAutoridadesController;
import ec.gob.ambiente.suia.prevencion.planemergente.service.TipoSectorFaseService;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoSuspendidoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.BeanLocator;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.ConvertidorObjetosDominioUtil;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.webservicesclientes.facade.JbpmSuiaCustomServicesFacade;

@ManagedBean
@ViewScoped
public class ProyectosPCController {
	
	private static final long serialVersionUID = -4523371587113629686L;

	private static final Logger LOG = Logger.getLogger(ProyectosAdminController.class);
	private static final String COMPLETADA = "Completada";

	@Getter
	@Setter
	@ManagedProperty(value = "#{proyectosAdminBean}")
	private ProyectosAdminBean proyectosBean;
	
	@Getter
	@Setter
	@ManagedProperty(value = "#{proyectosBean}")
	private ProyectosBean proyectosBeanR;
	
	@Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;

	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;

	@EJB
	private ProcesoFacade procesoFacade;

	@EJB
	private DocumentosFacade documentosFacade;

	@EJB
	private UsuarioFacade usuarioFacade;
	
	@EJB
	private ProcesoSuspendidoFacade procesoSuspendidoFacade;
	
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	
	@EJB
	private DocumentosCoaFacade documentosCoaFacade;
	@EJB
	private RolFacade rolFacade;
	
//	@EJB
//	private 

	@Getter
	@Setter
	private boolean habilitarEstadosEtapas;

	@Getter
	@Setter
	private boolean deletionActive;

	@Getter
	@Setter
	private boolean updateSuiaActive;
	
	@Getter	
	private boolean esAdmin;
	
	//Cris F: nuevo Facade eliminacion
	@EJB
	private FacilitadorProyectoFacade facilitadorProyectoFacade;
	@EJB
	private MensajeNotificacionFacade mensajeNotificacionFacade;
	@EJB
	private ContactoFacade contactoFacade;
	
	@Getter
	@Setter
	private DocumentosCOA documentoRCoa;
	
	@Getter
	@Setter
	private List<TipoSector> listaSectoresAux;
	
	@Getter
	@Setter
	private List<String> listaSectores;
	
	private boolean rolHidrocarburos;
	private boolean rolMineria;
	private boolean rolOtrosSectores;
	private boolean rolElectrico;

	// fin variables nuevas
	
	@PostConstruct
	private void init() {
		try {
			
			 rolHidrocarburos = rolFacade.isUserInRole(JsfUtil.getLoggedUser(), "FUNCIONARIO PLANTA CENTRAL HIDROCARBUROS");
		     rolMineria = rolFacade.isUserInRole(JsfUtil.getLoggedUser(), "FUNCIONARIO PLANTA CENTRAL MINERIA");
		     rolOtrosSectores = rolFacade.isUserInRole(JsfUtil.getLoggedUser(), "FUNCIONARIO PLANTA CENTRAL OTROS SECTORES");
		     rolElectrico = rolFacade.isUserInRole(JsfUtil.getLoggedUser(), "FUNCIONARIO PLANTA CENTRAL ELECTRICO");
		     
		     String sector= "";
		     if(rolHidrocarburos){
		        	sector = "Hidrocarburos";        	
		        }else if(rolMineria){
		        	sector = "Minería";        	
		        }else if(rolOtrosSectores){
		        	sector = "Otros Sectores";        	
		        }else if(rolElectrico){
		        	sector = "Eléctrico";
		        }
			
			
			proyectosBean.setProyectosLazy(new LazyProyectPCDataModel(JsfUtil.getLoggedUser(), sector));
			deletionActive = Usuario.isUserInRole(JsfUtil.getLoggedUser(), "admin") || Usuario.isUserInRole(JsfUtil.getLoggedUser(), "AUTORIDAD AMBIENTAL")||/*Usuario.isUserInRole(JsfUtil.getLoggedUser(), "AUTORIDAD AMBIENTAL2 MAE") ||*/ Usuario.isUserInRole(JsfUtil.getLoggedUser(), "AUTORIDAD AMBIENTAL MAE");
			updateSuiaActive = Usuario.isUserInRole(JsfUtil.getLoggedUser(),
					"TÉCNICO REASIGNACIÓN COORDINADOR PROVINCIAL")
					|| Usuario.isUserInRole(JsfUtil.getLoggedUser(), "admin");
//			esAdmin=Usuario.isUserInRole(JsfUtil.getLoggedUser(), "admin");//Si es autoridad, no puede eliminar, solo suspender
			esAdmin = true;
			
//			listaSectoresAux = proyectoLicenciamientoAmbientalFacade.getTiposSectores();
//			
//			listaSectores = new ArrayList<>();
//			for(TipoSector sector : listaSectoresAux){
//				listaSectores.add(sector.getNombre());
//			}
			
		} catch (Exception e) {
			LOG.error("Error cargando proyectos", e);
		}
	}

	public String seleccionar(ProyectoCustom proyectoCustom) {
		switch (proyectoCustom.getSourceType()) {
		case ProyectoCustom.SOURCE_TYPE_INTERNAL:

			try {
				ProyectoLicenciamientoAmbiental proyecto = proyectoLicenciamientoAmbientalFacade
						.buscarProyectosLicenciamientoAmbientalPorId(Integer.parseInt(proyectoCustom.getId()));
				proyectosBean.setProyectoToShow(proyecto);
				if(proyectoCustom.getCategoriaNombrePublico().contains("Licencia") && Usuario.isUserInRole(JsfUtil.getLoggedUser(), "SENAGUA")){
					return JsfUtil.getBean(EstudioImpactoAmbientalBean.class).verEIA2(proyecto);
				}else {
					return JsfUtil.actionNavigateTo("/proyectos/resumenProyecto.jsf");
				}
			} catch (Exception e) {
				LOG.error(e.getMessage());
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
				return null;
			}

		case ProyectoCustom.SOURCE_TYPE_EXTERNAL_SUIA:

			JsfUtil.getBean(ContenidoExterno.class).executeAction(proyectoCustom,
					IntegracionFacade.IntegrationActions.mostrar_dashboard);

			break;
		case ProyectoCustom.SOURCE_TYPE_RCOA:
			try {
				Integer idProyecto = Integer.parseInt(proyectoCustom.getId());
				proyectosBeanR.setProyectoRcoa(proyectoLicenciaCoaFacade.buscarProyectoPorId(idProyecto));
				return JsfUtil.actionNavigateTo("/proyectos/resumenProyectoRcoa.jsf");
			} catch (Exception e) {
				LOG.error(e.getMessage());
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
				return null;
			}
		}

		return null;
	}
	
	public String verProyectoSeleccionado(ProyectoLicenciamientoAmbiental proyecto) {
		try {
			if(proyecto!=null){
			proyectosBean.setProyectoToShow(proyecto);
			return JsfUtil.actionNavigateTo("/proyectos/resumenProyecto.jsf");
			}
		} catch (CmisAlfrescoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public String eliminarProyectoIntegracion(ProyectoCustom proyectoCustom) {
		switch (proyectoCustom.getSourceType()) {
		case ProyectoCustom.SOURCE_TYPE_EXTERNAL_SUIA:

			JsfUtil.getBean(ContenidoExterno.class).executeAction(proyectoCustom,
					IntegracionFacade.IntegrationActions.eliminar_proyecto_II);

			break;
		}

		return null;
	}

	public String verProyectoNoFinalizado(ProyectoCustom proyecto) throws NumberFormatException, CmisAlfrescoException {
		proyectosBean.setProyectoToEdit(proyectoLicenciamientoAmbientalFacade
				.buscarProyectosLicenciamientoAmbientalPorId(Integer.parseInt(proyecto.getId())));
		return JsfUtil.actionNavigateTo("/prevencion/registroProyecto/verProyecto.jsf");
	}

	public void verificarProyectosNoFinalizados() {
		if (!proyectosBean.getProyectosNoFinalizados().isEmpty()) {
			JsfUtil.addCallbackParam("mostrarNoFinalizados");
		}
	}

	
	boolean esHidrocarburos= false;
	public void marcar(ProyectoCustom proyectoCustom) {
		switch (proyectoCustom.getSourceType()) {
		case ProyectoCustom.SOURCE_TYPE_INTERNAL:

			proyectoCustom.setMotivoEliminar("");
			proyectosBean.setProyectoCustom(proyectoCustom);

			break;

		case ProyectoCustom.SOURCE_TYPE_EXTERNAL_SUIA:

			JsfUtil.getBean(ContenidoExterno.class).executeAction(proyectoCustom,
					IntegracionFacade.IntegrationActions.eliminar_proyecto);

			break;
		}
		documentoAdjunto=new Documento();
		esHidrocarburos= false;
	}
	
	public void marcarH(ProyectoCustom proyectoCustom) {
		switch (proyectoCustom.getSourceType()) {
		case ProyectoCustom.SOURCE_TYPE_INTERNAL:
			proyectoCustom.setMotivoEliminar("");
			proyectosBean.setProyectoCustom(proyectoCustom);
			break;

		case ProyectoCustom.SOURCE_TYPE_EXTERNAL_SUIA:		
			proyectoCustom.setMotivoEliminar("");
			proyectosBean.setProyectoCustom(proyectoCustom);
			esHidrocarburos= true;
			break;
			
		case ProyectoCustom.SOURCE_TYPE_RCOA:
			proyectoCustom.setMotivoEliminar("");
			proyectosBean.setProyectoCustom(proyectoCustom);
			break;
		}		
		documentoAdjunto=new Documento();
	}

	public void eliminar() {
		try {
			ProyectoLicenciamientoAmbiental proyecto = proyectoLicenciamientoAmbientalFacade
					.buscarProyectosLicenciamientoAmbientalPorId(Integer.parseInt(proyectosBean.getProyectoCustom()
							.getId()));

			Usuario director = usuarioFacade.getDirectorProvincialArea(proyecto.getAreaResponsable());
			if (director == null) {
				JsfUtil.addMessageError("No se ha podido identificar la autoridad responsable para este proyecto.");
				return;
			}

			String notificacionInicioProcesoTitle = "Se ha iniciado el proceso de eliminación del proyecto";
			String notificacionInicioProcesoBody = "Atendiendo a su solicitud, se ha dado inicio al proceso de eliminación de su proyecto: <br/><br/>"
					+ "<b>"
					+ proyecto.getCodigo()
					+ "</b><br/><br/>"
					+ proyecto.getNombre()
					+ ".<br/><br/>"
					+ "Usted recibirá en poco tiempo, por esta vía, el resultado de este proceso.";

			String notificacionSolicitudDirectorTitle = "El proyecto está pendiente para su eliminación";
			String notificacionSolicitudDirectorBody = "El proyecto está pendiente para su eliminación, por favor, revise la solicitud.";

			Map<String, Object> parametros = new HashMap<String, Object>();

			parametros.put(Constantes.ID_PROYECTO, proyecto.getId());
			parametros.put("codigoProyecto", proyecto.getCodigo());
			parametros.put("administrador", JsfUtil.getLoggedUser().getNombre());
			parametros.put("proponente", proyecto.getUsuario().getNombre());
			parametros.put("director", director.getNombre());
			parametros.put("notificarInicioProcesoProponenteTitle", notificacionInicioProcesoTitle);
			parametros.put("notificarInicioProcesoProponenteBody", notificacionInicioProcesoBody);
			parametros.put("notificarSolicitudDirectorTitle", notificacionSolicitudDirectorTitle);
			parametros.put("notificarSolicitudDirectorBody", notificacionSolicitudDirectorBody);

			procesoFacade.iniciarProceso(JsfUtil.getLoggedUser(), Constantes.NOMBRE_PROCESO_ELIMINAR_PROYECTO,
					proyectosBean.getProyectoCustom().getCodigo(), parametros);
			proyecto.setMotivoEliminar(proyectosBean.getProyectoCustom().getMotivoEliminar());
			proyectoLicenciamientoAmbientalFacade.actualizarSimpleProyecto(proyecto);

			if(documentoAdjunto!=null && documentoAdjunto.getContenidoDocumento() !=null)
			{
				documentoAdjunto=guardarDocumento(proyecto.getCodigo(), documentoAdjunto, TipoDocumentoSistema.ARCHIVACION_PROYECTO_ADJUNTO);					
				JsfUtil.addMessageInfo("Documento subido exitosamente");					
			}
			JsfUtil.addMessageInfo("Se ha iniciado el proceso de eliminación del proyecto.");

			JsfUtil.addCallbackParam("eliminarProyecto");
		} catch (Exception ex) {
			LOG.error(ex);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_INICIAR_PROCESO);
		}
	}

	public void iniciarProceso(Flujo flujo) {
		try {
			JsfUtil.redirectTo(flujo.getUrlInicioFlujo() + "?flujoId=" + flujo.getId());
		} catch (Exception e) {
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}

	public void verTareas(Long processInstanceId, String estadoProceso) {
		try {
			List<Tarea> tareas = new ArrayList<Tarea>();
			if (COMPLETADA.equals(estadoProceso)) {
				List<ResumeTarea> resumeTareas = BeanLocator.getInstance(JbpmSuiaCustomServicesFacade.class)
						.getResumenTareas(processInstanceId);
				for (ResumeTarea resumeTarea : resumeTareas) {
					Tarea tarea = new Tarea();
					ConvertidorObjetosDominioUtil.convertirBamTaskSummaryATarea(resumeTarea, tarea);
					tareas.add(tarea);
				}
				Collections.sort(tareas, new OrdenarTareaPorEstadoComparator());

				JsfUtil.getBean(ResumenYEstadosEtapasBean.class).setTareas(tareas);

			} else {
				List<TaskSummary> taskSummaries = procesoFacade.getTaskBySelectFlow(JsfUtil.getLoggedUser(),
						processInstanceId);
				int longitud = taskSummaries.size();
				for (int i = 0; i < longitud; i++) {
					Tarea tarea = new Tarea();
					ConvertidorObjetosDominioUtil.convertirTaskSummaryATarea(taskSummaries.get(i), tarea);
					tareas.add(tarea);
				}

				Collections.sort(tareas, new OrdenarTareaPorEstadoComparator());

				JsfUtil.getBean(ResumenYEstadosEtapasBean.class).setTareas(tareas);

			}
		} catch (Exception exception) {
			exception.getMessage();
			LOG.error(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION + exception.getMessage());
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}

	public void verDocumentos(Long processInstanceId) {
		try {
			JsfUtil.getBean(ResumenYEstadosEtapasBean.class).setDocumentos(
					documentosFacade.recuperarDocumentosConArchivosPorFlujo(processInstanceId));
		} catch (Exception e) {
			LOG.error(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION + e.getMessage());
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}

	public StreamedContent getStream(Documento documento) {
		if (documento.getContenidoDocumento() != null) {
			InputStream is = new ByteArrayInputStream(documento.getContenidoDocumento());
			return new DefaultStreamedContent(is, documento.getMime(), documento.getNombre());
		} else {
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
		return null;
	}

	public boolean isProyectoSuia(ProyectoCustom proyectoCustom) {

		if (!proyectoCustom.isInternal()
				&& !proyectoLicenciamientoAmbientalFacade.isProyectoLegadoHidrocarburos(proyectoCustom.getCodigo(),
						JsfUtil.getLoggedUser())) {
			proyectoCustom.setSourceType(ProyectoCustom.SOURCE_TYPE_EXTERNAL_SUIA);
			return true;
		}
		return false;
	}
	
	@Getter
    @Setter
    private Documento documentoAdjunto;
	
	public void uploadFile(FileUploadEvent event) {
		byte[] contenidoDocumento = event.getFile().getContents();
		documentoAdjunto=new Documento();
		documentoAdjunto.setContenidoDocumento(contenidoDocumento);
		documentoAdjunto.setNombre(event.getFile().getFileName());		
		String ext = JsfUtil.devuelveExtension(event.getFile().getFileName());
		String mime = ext.contains("pdf")||ext.contains("rar")||ext.contains("zip")?"application":"image";
		mime+="/"+ext;
		documentoAdjunto.setExtesion("."+ext);
		documentoAdjunto.setMime(mime);
		//pdf|rar|zip|png|jpe?g|gif|
	}
	
    private Documento guardarDocumento(String codigoProyecto,Documento documento,TipoDocumentoSistema tipoDocumento){
    	try {        	
        	documento.setNombreTabla(ProyectoLicenciamientoAmbiental.class.getSimpleName());
    		return documentosFacade.guardarDocumentoAlfresco(codigoProyecto,Constantes.CARPETA_ARCHIVACION,null,documento,tipoDocumento,null);
    		
    	} catch (Exception e) {
    		LOG.error(e.getMessage());
    		return null;
    	}    	
    }
	
	public void suspenderProyecto(Integer idProyecto) {
		try {
			
			String codigoProyecto=proyectosBean.getProyectoCustom().getCodigo();
			if(documentoAdjunto.getContenidoDocumento()==null)
			{
				JsfUtil.addMessageError("El documento adjunto es requerido");
				return;
			}else
			{
				ProyectoLicenciamientoAmbiental p=proyectoLicenciamientoAmbientalFacade.getProyectoPorCodigo(codigoProyecto);
				documentoAdjunto.setIdTable(p!=null?p.getId():0);				
				documentoAdjunto=guardarDocumento(codigoProyecto, documentoAdjunto, TipoDocumentoSistema.ARCHIVACION_PROYECTO_ADJUNTO);					
				if(documentoAdjunto.getId()==null)
				{
					JsfUtil.addMessageError("Error al guardar el documento");
					return;
				}
			}
			
			
			if(esHidrocarburos)
			{
				procesoSuspendidoFacade.modificarPropietarioTareas4Categorias(codigoProyecto, proyectosBean.getProyectoCustom().getMotivoEliminar(),true);
				ProcesoSuspendido ps =procesoSuspendidoFacade.getProcesoSuspendidoPorCodigo(codigoProyecto);
				if(ps==null)
					ps=new ProcesoSuspendido();
				
				ps.setSuspendido(true);
				ps.setCodigo(codigoProyecto);
				ps.setTipoProyecto(ProcesoSuspendido.TIPO_PROYECTO_HIDROCARBUROS);
				ps.setDescripcion(proyectosBean.getProyectoCustom().getMotivoEliminar());
				/*if(documentoAdjunto!=null && documentoAdjunto.getContenidoDocumento() !=null)
				{
					documentoAdjunto=guardarDocumento(codigoProyecto, documentoAdjunto, TipoDocumentoSistema.SUSPENCION_ELIMINACION_ADJUNTO);					
					if(documentoAdjunto.getId()!=null)
					{
						JsfUtil.addMessageInfo("Documento subido exitosamente");
					}						
					else{
						JsfUtil.addMessageError("Error al guardar el documento");
						return;
					}
				}*/
				
				procesoSuspendidoFacade.guardar(ps,JsfUtil.getLoggedUser().getNombre());	
				procesoSuspendidoFacade.setEstadoProyecto(codigoProyecto,false);
				JsfUtil.addMessageInfo("Se ha archivado el proyecto.");
				JsfUtil.addCallbackParam("eliminarProyecto");
				return;
				
			}
			
			ProyectoLicenciamientoAmbiental proyecto= new ProyectoLicenciamientoAmbiental();
			if(idProyecto==null || idProyecto==0)
			{
				proyecto = proyectoLicenciamientoAmbientalFacade.buscarProyectosLicenciamientoAmbientalPorId(Integer.parseInt(proyectosBean.getProyectoCustom().getId()));
			}else{
				proyecto = proyectoLicenciamientoAmbientalFacade.buscarProyectosLicenciamientoAmbientalPorId(idProyecto);
			}
			
			
			if(proyecto.getMotivoEliminar() != null && !proyecto.getMotivoEliminar().trim().isEmpty()){
					JsfUtil.addMessageError("El proyecto ha iniciado el proceso de Eliminación: "+proyecto.getMotivoEliminar());
					return;
			}					
			
			Boolean tareasModificadas = false;
			if(proyecto.getCatalogoCategoria().getCategoria().getCodigo().equals("I")) {
				//si es un certiicado ambiental no se requiere ejecutar la modificacion de tareas porque el certificado no tiene proceso en bpm
				List<ProcessInstanceLog> process = procesoFacade.getProcessInstancesLogsVariableValue(JsfUtil.getLoggedUser(),Constantes.VARIABLE_PROCESO_TRAMITE, proyecto.getCodigo());
				if(process.size() == 0)
					tareasModificadas = true;
				for (ProcessInstanceLog processInstanceLog : process) {
					if(processInstanceLog.getStatus()==1) {
						tareasModificadas = false;
						break;
					}
				}
			}
			
			if(!tareasModificadas)
				tareasModificadas = procesoSuspendidoFacade.modificarPropietarioTareas(proyecto.getCodigo().toString(),JsfUtil.getLoggedUser(),true);
						
			if(tareasModificadas)
			{
				ProcesoSuspendido ps =procesoSuspendidoFacade.getProcesoSuspendidoPorCodigo(proyecto.getCodigo());
				if(ps==null)
					ps=new ProcesoSuspendido();
				
				ps.setSuspendido(true);
				ps.setCodigo(proyecto.getCodigo());
				ps.setTipoProyecto(ProcesoSuspendido.TIPO_PROYECTO_REGURALIZACION);
				ps.setDescripcion(proyectosBean.getProyectoCustom().getMotivoEliminar());
				/*if(documentoAdjunto!=null && documentoAdjunto.getContenidoDocumento() !=null)
				{
					documentoAdjunto=guardarDocumento(proyecto.getCodigo(), documentoAdjunto, TipoDocumentoSistema.SUSPENCION_ELIMINACION_ADJUNTO);					
					JsfUtil.addMessageInfo("Documento subido exitosamente");					
				}*/
				
				procesoSuspendidoFacade.guardar(ps,JsfUtil.getLoggedUser().getNombre());
				procesoSuspendidoFacade.setEstadoProyecto(proyecto.getId(),false);

				//Cris F: correo de eliminación a los facilitadores
				boolean variable = procesoSuspendidoFacade.verificarEvaluacionSocial(proyecto.getCodigo(), proyecto.getUsuario());
				
				if(variable){
					//El proceso tiene evaluación social
					List<FacilitadorProyecto> facilitadorProyectoList = facilitadorProyectoFacade.listarFacilitadoresProyecto(proyecto);
					
					if(facilitadorProyectoList != null && !facilitadorProyectoList.isEmpty()){					

						for(FacilitadorProyecto facilitador : facilitadorProyectoList){
							
							Usuario usuario = usuarioFacade.buscarUsuarioPorId(facilitador.getUsuario().getId());
				    		
				    		List<Contacto> listaContactos = contactoFacade.buscarPorPersona(usuario.getPersona());
							String emailFacilitador = "";
							for(Contacto contacto : listaContactos){
								if(contacto.getFormasContacto().getId() == FormasContacto.EMAIL){
									emailFacilitador = contacto.getValor();
									break;
								}				
							}
							
							String nombreFacilitador = usuario.getPersona().getNombre();						
							
							String mensaje = mensajeNotificacionFacade.recuperarValorMensajeNotificacion("bodyNotificacionEliminacionProyectoFacilitadores", new Object[]{});
							mensaje = mensaje.replace("nombre_facilitador", nombreFacilitador);
							mensaje = mensaje.replace("codigo_proyecto", proyecto.getCodigo());
			                
			                NotificacionAutoridadesController mail_a = new NotificacionAutoridadesController();
			    			mail_a.sendEmailInformacionProponente(emailFacilitador, nombreFacilitador, mensaje, "Proceso archivado y/o dado de baja", proyecto.getCodigo(), usuario, loginBean.getUsuario());
						}					
					}				
				}
				//fin de correo
				
				
				JsfUtil.addMessageInfo("Se ha archivado el proyecto.");
			}else{
				JsfUtil.addMessageError("No se encontraron tareas para archivar");
			}
			
			
			
			JsfUtil.addCallbackParam("eliminarProyecto");
		} catch (Exception ex) {
			LOG.error(ex);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}	
	
	
	public void suspenderProyectoRcoa(Integer idProyecto) {
		try {
			
			String codigoProyecto=proyectosBean.getProyectoCustom().getCodigo();
			if(documentoRCoa.getContenidoDocumento()==null)
			{
				JsfUtil.addMessageError("El documento adjunto es requerido");
				return;
			}else
			{
				ProyectoLicenciaCoa p = proyectoLicenciaCoaFacade.buscarProyecto(codigoProyecto);				
				documentoRCoa.setIdTabla(p!=null?p.getId():0);	
				
				documentoRCoa = guardarDocumentoRcoa(codigoProyecto, documentoRCoa, TipoDocumentoSistema.ARCHIVACION_PROYECTO_ADJUNTO);			
				if(documentoRCoa.getId()==null)
				{
					JsfUtil.addMessageError("Error al guardar el documento");
					return;
				}
			}			
					
			
			ProyectoLicenciaCoa proyecto= new ProyectoLicenciaCoa();
			if(idProyecto==null || idProyecto==0){
				proyecto = proyectoLicenciaCoaFacade.buscarProyectoPorId(Integer.parseInt(proyectosBean.getProyectoCustom().getId()));
			}else{
				proyecto = proyectoLicenciaCoaFacade.buscarProyectoPorId(idProyecto);
			}
						
			if(proyecto.getRazonEliminacion() != null && !proyecto.getRazonEliminacion().trim().isEmpty()){
				JsfUtil.addMessageError("El proyecto ha iniciado el proceso de Eliminación: "+proyecto.getRazonEliminacion());
				return;
			}					
			
			Boolean tareasModificadas = false;
			
			tareasModificadas = procesoSuspendidoFacade.modificarPropietarioTareasRcoa(proyecto.getCodigoUnicoAmbiental().toString(),JsfUtil.getLoggedUser(),true);									
			
			
			if(tareasModificadas)
			{
				ProcesoSuspendido ps =procesoSuspendidoFacade.getProcesoSuspendidoPorCodigo(proyecto.getCodigoUnicoAmbiental());
				if(ps==null)
					ps=new ProcesoSuspendido();
				
				ps.setSuspendido(true);
				ps.setCodigo(proyecto.getCodigoUnicoAmbiental());
				ps.setTipoProyecto(ProcesoSuspendido.TIPO_PROYECTO_REGURALIZACION);
				ps.setDescripcion(proyectosBean.getProyectoCustom().getMotivoEliminar());
				
				procesoSuspendidoFacade.guardar(ps,JsfUtil.getLoggedUser().getNombre());
				procesoSuspendidoFacade.setEstadoProyectoRcoa(proyecto.getId(),false);
			
				JsfUtil.addMessageInfo("Se ha archivado el proyecto.");	
			}
			
			JsfUtil.addCallbackParam("eliminarProyecto");
		} catch (Exception ex) {
			LOG.error(ex);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}
	
	public void uploadFileRcoa(FileUploadEvent event) {
		byte[] contenidoDocumento = event.getFile().getContents();
		documentoRCoa=new DocumentosCOA();
		documentoRCoa.setContenidoDocumento(contenidoDocumento);
		documentoRCoa.setNombreDocumento(event.getFile().getFileName());		
		String ext = JsfUtil.devuelveExtension(event.getFile().getFileName());
		String mime = ext.contains("pdf")||ext.contains("rar")||ext.contains("zip")?"application":"image";
		mime+="/"+ext;
		documentoRCoa.setExtencionDocumento("."+ext);
		documentoRCoa.setTipo(mime);
	}
	
	private DocumentosCOA guardarDocumentoRcoa(String codigoProyecto,DocumentosCOA documento,TipoDocumentoSistema tipoDocumento){
    	try {        	
        	documento.setNombreTabla(ProyectoLicenciamientoAmbiental.class.getSimpleName());
        	
        	return documentosCoaFacade.guardarDocumentoAlfresco(codigoProyecto, "ARCHIVACION PROYECTO", null, documento, tipoDocumento);
    		
    	} catch (Exception e) {
    		LOG.error(e.getMessage());
    		return null;
    	}    	
    }

}
