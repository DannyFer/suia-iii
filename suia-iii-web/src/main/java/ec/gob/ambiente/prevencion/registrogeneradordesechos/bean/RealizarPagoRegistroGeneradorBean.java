package ec.gob.ambiente.prevencion.registrogeneradordesechos.bean;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

import ec.gob.ambiente.core.interfaces.CompleteOperation;
import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.prevencion.registrogeneradordesechos.controller.MotivoRegistroGeneradorDesechosController;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.builders.TaskSummaryCustomBuilder;
import ec.gob.ambiente.suia.comun.bean.PagoServiciosBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.GeneradorDesechosPeligrosos;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.RegistroGeneradorDesechosAsociado;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.registrogeneradordesechos.facade.RegistroGeneradorDesechosFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.tramiteresolver.RegistroGeneradorTramiteResolver;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class RealizarPagoRegistroGeneradorBean implements Serializable {

	private static final long serialVersionUID = -4092357212445589458L;

	private static final Logger LOGGER = Logger.getLogger(RealizarPagoRegistroGeneradorBean.class);

	public static final String LOAD_AFTER = "loadAfter";
	public static final String LOAD_AFTER_VALUE = "pending";

	private ProyectoLicenciamientoAmbiental proyecto;
	
	private RegistroGeneradorDesechosAsociado registroGeneradorDesechosAsociado;
	
	private Documento documento;

	@Getter
	private boolean permitirContinuar;

	@Getter
	private boolean procesoIniciado;

	private boolean showModalOtrosProcesos;

	@ManagedProperty(value = "#{loginBean}")
	@Getter
	@Setter
	private LoginBean loginBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;

	@Getter
	private int procesosActivos;

	@EJB
	private ProcesoFacade procesoFacade;

	@EJB
	private RegistroGeneradorDesechosFacade registroGeneradorDesechosFacade;
	
	@EJB
	private DocumentosFacade documentosFacade;
	
	@EJB
	private CrudServiceBean crudServiceBean;
		
	@Getter
	@Setter
	@ManagedProperty(value = "#{proyectosBean}")
	private ProyectosBean proyectosBean;

	@EJB
	ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;

	@PostConstruct
	private void init() {
		HttpServletRequest servletRequest = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		permitirContinuar = true;
		String param = JsfUtil
				.getRequestParameter(MotivoRegistroGeneradorDesechosController.MOTIVO_REGISTRO_GENERADOR_DESECHOS);
		if (param != null
				&& param.equals(MotivoRegistroGeneradorDesechosController.MOTIVO_REGISTRO_GENERADOR_DESECHOS_ASOCIADO)) {
			proyecto=(ProyectoLicenciamientoAmbiental)servletRequest.getSession().getAttribute("proyecto");
			if (proyecto == null) {
				permitirContinuar = false;
				JsfUtil.addMessageError("No se puede continuar con el registro de generador de desechos para el proyecto seleccionado. Contacte a Mesa de Ayuda.");
			}
		}

		param = JsfUtil.getRequestParameter(LOAD_AFTER);
		if (param != null && param.equals(LOAD_AFTER_VALUE))
			procesoIniciado = true;

		if (!procesoIniciado) {
			try {
				procesosActivos = procesoFacade.getActiveProcessInstancesLogsVariableValue(JsfUtil.getLoggedUser(),
						Constantes.NOMBRE_PROCESO_GENERADOR_DESECHOS, "sujetoControl",
						JsfUtil.getLoggedUser().getNombre()).size();
				if (procesosActivos > 0)
					showModalOtrosProcesos = true;
			} catch (JbpmException e) {
				LOGGER.error("Error verificando instancias del proceso Registro de generador", e);
			}
		}
	}

	public String getUsuarioAutenticado() {
		return JsfUtil.getBean(LoginBean.class).getInfoToShow();
	}

	@SuppressWarnings("unchecked")
	public void iniciarProceso() {
		boolean error = false;
		String solicitud = "";
		String workspace="";
		Map<String, Float> paramsValorAPagar = new ConcurrentHashMap<String, Float>();
   	    paramsValorAPagar.put("valorAPagar", (float) 180);
		try {
			Object[] result = registroGeneradorDesechosFacade.iniciarEmisionProcesoRegistroGenerador(JsfUtil.getLoggedUser(), RegistroGeneradorTramiteResolver.class, proyecto);

			if (proyecto != null){
				proyecto.setRgdEncurso(true);
				proyectoLicenciamientoAmbientalFacade.actualizarProyecto(proyecto);
			}

			JsfUtil.getBean(BandejaTareasBean.class).setProcessId((Long)result[0]);
			JsfUtil.getBean(BandejaTareasBean.class).setTarea(
					new TaskSummaryCustomBuilder().fromSuiaIII((Map<String, Object>)result[1],
							"Registro de generador de desechos especiales y peligrosos",
							procesoFacade.getCurrenTask(JsfUtil.getLoggedUser(), (Long)result[0])).build());
			proyectosBean.setProyecto(proyecto);
			solicitud = ((Map<String, Object>)result[1]).get(GeneradorDesechosPeligrosos.VARIABLE_NUMERO_SOLICITUD).toString();

			//cambios			
			HttpServletRequest servletRequest1 = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
			documento = (Documento) servletRequest1.getSession().getAttribute("documentoLicencia");
			if(documento.getNombre()!=null){
			HttpServletRequest servletRequest2 = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();	
			registroGeneradorDesechosAsociado=(RegistroGeneradorDesechosAsociado)servletRequest2.getSession().getAttribute("registroAsociado");
			registroGeneradorDesechosAsociado.setCodigoDesecho(solicitud);
			registroGeneradorDesechosAsociado.setNombreDocumento(documento.getNombre());
				try {
					workspace = documentosFacade.guardarDocumentoAlfrescoDesechosIVCategorias(solicitud, JsfUtil.getBean(BandejaTareasBean.class).getTarea().getProcessName().toString(), JsfUtil.getBean(BandejaTareasBean.class).getProcessId(), documento);
					workspace= workspace.substring(0, workspace.lastIndexOf(";"));
					registroGeneradorDesechosAsociado.setIdAlfresco(workspace);
					crudServiceBean.saveOrUpdate(registroGeneradorDesechosAsociado);
				} catch (ServiceException e) {
					LOGGER.error("Error verificando instancias del proceso Registro de generador", e);
					e.printStackTrace();
				} catch (CmisAlfrescoException e) {
					LOGGER.error("Error verificando documento para subir alfresco", e);
					e.printStackTrace();
				}
			}
			boolean sendMail=false;
			sendMail=procesoFacade.envioSeguimientoRGD(JsfUtil.getLoggedUser(), JsfUtil.getBean(BandejaTareasBean.class).getTarea().getProcessInstanceId());
			if(!sendMail){
				LOGGER.debug("Fallo en el envío de notificaciones.");
			}
		} catch (JbpmException e) {
			error = true;
			LOGGER.debug("Error iniciando procesos Registro de generador", e);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_INICIAR_PROCESO);
		}
		if (error)
			return;
		
		
   	    
		JsfUtil.getBean(PagoServiciosBean.class).initFunctionWithProjectOrMotive(
				null,
				new CompleteOperation() {

					@Override
					public Object endOperation(Object object) {
						try {
							procesoFacade.buscarAprobarActualTareaProceso(JsfUtil.getLoggedUser(),
									JsfUtil.getBean(BandejaTareasBean.class).getProcessId(), null);
						} catch (JbpmException e) {
							LOGGER.debug("Error completando tarea de pago del proceso Registro de generador", e);
							JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
						}
						return null;
					}
				}, paramsValorAPagar, proyecto != null ? null : solicitud, null,
				JsfUtil.getMessageFromBundle("helps", "generadorDesecho.ingresoTransacciones"), true);
	}

	public void continuar() {
		Map<String, Float> paramsValorAPagar = new ConcurrentHashMap<String, Float>();
   	    paramsValorAPagar.put("valorAPagar", (float) 180);
   	    
		try {
			String numeroSolicitud = procesoFacade
					.recuperarVariablesProceso(JsfUtil.getLoggedUser(),
							JsfUtil.getBean(BandejaTareasBean.class).getProcessId())
					.get(GeneradorDesechosPeligrosos.VARIABLE_NUMERO_SOLICITUD).toString();

			JsfUtil.getBean(PagoServiciosBean.class).initFunctionWithProjectOrMotive(
					null,
					new CompleteOperation() {

						@Override
						public Object endOperation(Object object) {
							try {						
								procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(),
										JsfUtil.getBean(BandejaTareasBean.class).getTarea().getTaskId(), JsfUtil
												.getBean(BandejaTareasBean.class).getProcessId(), null);								

								procesoFacade.envioSeguimientoRGD(JsfUtil.getLoggedUser(), JsfUtil
										.getBean(BandejaTareasBean.class).getProcessId());
							} catch (JbpmException e) {
								LOGGER.debug("Error completando tarea de pago del proceso Registro de generador", e);
								JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
							}
							return null;
						}
					},
					paramsValorAPagar,
					(proyectosBean.getProyecto() != null && proyectosBean.getProyecto().isPersisted()) ? null
							: numeroSolicitud, null,
					JsfUtil.getMessageFromBundle("helps", "generadorDesecho.ingresoTransacciones"), true);
		} catch (JbpmException e) {
			JsfUtil.addMessageError("Ha ocurrido un error recuperando el trámite del registro de generador.");
			e.printStackTrace();
		}
	}

	public String cancelar() {
		return procesoIniciado ? JsfUtil.actionNavigateToBandeja() : JsfUtil.actionNavigateTo("/procesos/procesos");
	}

	public boolean isShowModalOtrosProcesos() {
		if (showModalOtrosProcesos)
			JsfUtil.addCallbackParam("showCertificadoIntercepcion");
		return showModalOtrosProcesos;
	}
}
