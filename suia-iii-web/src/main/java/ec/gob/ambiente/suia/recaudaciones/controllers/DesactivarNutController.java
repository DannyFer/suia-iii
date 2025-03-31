package ec.gob.ambiente.suia.recaudaciones.controllers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.jbpm.process.audit.ProcessInstanceLog;
import org.kie.api.task.model.TaskSummary;
import org.primefaces.context.RequestContext;

import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.recaudaciones.facade.DocumentosNUTFacade;
import ec.gob.ambiente.suia.recaudaciones.facade.NumeroUnicoTransaccionalFacade;
import ec.gob.ambiente.suia.recaudaciones.facade.ProyectosConPagoSinNutFacade;
import ec.gob.ambiente.suia.recaudaciones.model.DocumentoNUT;
import ec.gob.ambiente.suia.recaudaciones.model.NumeroUnicoTransaccional;
import ec.gob.ambiente.suia.recaudaciones.model.ProyectosConPagoSinNut;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ViewScoped
@ManagedBean
public class DesactivarNutController implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@EJB
	private NumeroUnicoTransaccionalFacade numeroUnicoTransaccionalFacade;
	@EJB
	private DocumentosNUTFacade documentosNUTFacade;
	@EJB
	private ProcesoFacade procesoFacade;
	@EJB
	private UsuarioFacade usuarioFacade;
	@EJB
	private ProyectosConPagoSinNutFacade proyectosConPagoSinNutFacade;

	@Getter
	@Setter
	private NumeroUnicoTransaccional numeroNut;
	@Getter
	@Setter
	private ProcessInstanceLog procesoNut;
	
	@Getter
	@Setter
	private List<NumeroUnicoTransaccional> listNutTramite;
	
	@Getter
	@Setter
	private String proyecto, motivo, cedulaOperador, nombreOperador;
	@Getter
	@Setter
	private Boolean proyectoHabilitado, deshabilitarNut, deshabilitarPago;
	
	@PostConstruct
	private void init() {
		Boolean esAdmin = Usuario.isUserInRole(JsfUtil.getLoggedUser(), "admin");
		
		if(!esAdmin) {
			JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
		}
	}
	
	private void inicializar() {
		numeroNut = null;
		procesoNut = null;
		
		listNutTramite = new ArrayList<>();
		
		motivo = null;
		cedulaOperador = null;
		nombreOperador = null;
		
		proyectoHabilitado = true;
		deshabilitarNut = null;
	}
	
	public void limpiar() {
		inicializar();
		proyecto = null;
		proyectoHabilitado = null;
		
		RequestContext.getCurrentInstance().update("form:pnlDataNut");
	}

	public void buscarNut() {
		try {
			
			inicializar();
			
			List<Long> idProcesos = new ArrayList<>();
 			
			List<ProcessInstanceLog> procesosProyecto = procesoFacade.getProcessInstancesLogsVariableValue(JsfUtil.getLoggedUser(),
					Constantes.VARIABLE_PROCESO_TRAMITE, proyecto);
			if(procesosProyecto == null || procesosProyecto.size() == 0) {
				proyectoHabilitado = false;
				listNutTramite = new ArrayList<>();
				JsfUtil.addMessageError("No se ha encontrado procesos válidos para el proyecto.");
			} else {
				List<NumeroUnicoTransaccional> listNutTramiteAux = numeroUnicoTransaccionalFacade.listNutPorTramiteSinFiltro(proyecto);
				
				if(listNutTramiteAux.size() > 0) {
					for (NumeroUnicoTransaccional nut : listNutTramiteAux) {
						if(!nut.getSolicitudUsuario().getInstitucionFinanciera().getCodigoInstitucion().equals("KUSHKI")) {
							List<DocumentoNUT> documentosNut = documentosNUTFacade.listaPorNutConProcesoAsignadoSinFiltros(nut.getId());
							if(documentosNut != null && documentosNut.size() > 0) {
								ProcessInstanceLog procesoNut = procesoFacade.getProcessInstanceLog(JsfUtil.getLoggedUser(), documentosNut.get(0).getIdProceso());
								
								if(procesoNut != null && procesoNut.getProcessName() != null) {
									nut.setProceso(procesoNut);
									listNutTramite.add(nut);
									
									idProcesos.add(procesoNut.getProcessInstanceId());
								}
							}
						}
					}
				}
				
				for (ProcessInstanceLog proceso : procesosProyecto) {
					if(!idProcesos.contains(proceso.getProcessInstanceId())) {
						List<TaskSummary> listaTareasPendientes = procesoFacade.getTaskReservedInProgress(JsfUtil.getLoggedUser(), proceso.getProcessInstanceId());
						if(listaTareasPendientes.size() > 0) {
							for (TaskSummary tarea : listaTareasPendientes) {
								if(tarea.getName().toLowerCase().contains("pago")) {
									procesoNut = procesoFacade.getProcessInstanceLog(JsfUtil.getLoggedUser(), tarea.getProcessInstanceId());
									
									Usuario operador = usuarioFacade.buscarUsuario(tarea.getActualOwner().getId());
									
									NumeroUnicoTransaccional nutNuevo = new NumeroUnicoTransaccional();
									nutNuevo.setProceso(procesoNut);
									nutNuevo.setOperador(operador);
									
									ProyectosConPagoSinNut proyectoLiberado = proyectosConPagoSinNutFacade.buscarPorProyectoPorUsuarioPagoLiberado(proyecto, operador, procesoNut.getProcessInstanceId());
									if(proyectoLiberado != null && proyectoLiberado.getId() != null) {
										nutNuevo.setProyectoLiberado(proyectoLiberado);
									}
									
									listNutTramite.add(nutNuevo);
									break;
								}
							}
						}
					}
				}
			}
			
		} catch (Exception e) {
			 inicializar();
			
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_CARGAR_DATOS);
		}
	}
	
	public void visualizarNut(NumeroUnicoTransaccional nutItem) {
		try {
			procesoNut = null;
			
			numeroNut = nutItem;
			procesoNut = nutItem.getProceso();

			deshabilitarNut = (!numeroNut.getEstadosNut().getId().equals(4) && !numeroNut.getEstadosNut().getId().equals(3)) ? true : false;
			
			Usuario operador = nutItem.getSolicitudUsuario().getUsuario();
			cedulaOperador = operador.getNombre();
			
			List<String> infoOperador = usuarioFacade.recuperarNombreOperador(operador);
			nombreOperador = infoOperador.get(0);
			
			RequestContext.getCurrentInstance().execute("PF('dlgInfoNut').show();");
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_CARGAR_DATOS);
		}
	}
	
	public void abrirPagoManual(NumeroUnicoTransaccional nutItem) {
		try {
			procesoNut = null;
			deshabilitarPago = true;
			
			numeroNut = nutItem;
			procesoNut = nutItem.getProceso();
			
			cedulaOperador = nutItem.getOperador().getNombre();
			
			List<String> infoOperador = usuarioFacade.recuperarNombreOperador(nutItem.getOperador());
			nombreOperador = infoOperador.get(0);
			
			if(nutItem.getProyectoLiberado() != null) {
				deshabilitarPago = false;
			}
			
			RequestContext.getCurrentInstance().execute("PF('dlgPagoManual').show();");
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_CARGAR_DATOS);
		}
	}
	
	public void desactivarNut() {
		try {
			numeroUnicoTransaccionalFacade.activarRegistroDePagoManual(numeroNut.getId(), motivo, proyecto, 
					numeroNut.getSolicitudUsuario().getUsuario().getId(), JsfUtil.getLoggedUser().getNombre(), procesoNut.getProcessInstanceId());
			
			JsfUtil.addMessageInfo("La desactivación del NUT " + numeroNut.getNutCodigo() + " se realizó satisfactoriamente.");
			
			inicializar();
			
			proyecto = null;
			proyectoHabilitado = null;
			
			JsfUtil.addCallbackParam("addMotivo");
			RequestContext.getCurrentInstance().update("form:pnlDataNut");
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}
	
	public void activarPagoManual() {
		try {
			Usuario operador = usuarioFacade.buscarUsuario(cedulaOperador);
			
			numeroUnicoTransaccionalFacade.activarRegistroDePagoManual(null, motivo, proyecto, 
					operador.getId(), JsfUtil.getLoggedUser().getNombre(), procesoNut.getProcessInstanceId());
			
			JsfUtil.addMessageInfo("La activación del registro de pago manual del proyecto " + proyecto + " se realizó satisfactoriamente.");
			
			inicializar();
			
			proyecto = null;
			proyectoHabilitado = null;
			
			JsfUtil.addCallbackParam("addMotivoManual");
			RequestContext.getCurrentInstance().update("form:pnlDataNut");
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}
}
