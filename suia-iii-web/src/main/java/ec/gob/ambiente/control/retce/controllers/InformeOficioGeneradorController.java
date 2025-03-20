package ec.gob.ambiente.control.retce.controllers;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.control.retce.beans.InformeTecnicoOficioGeneradorBean;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.AreaUsuario;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.service.UsuarioServiceBean;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.Email;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class InformeOficioGeneradorController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final Logger LOG = Logger.getLogger(InformeOficioGeneradorController.class);
	
	@ManagedProperty(value = "#{bandejaTareasBean}")
    @Getter
    @Setter
    private BandejaTareasBean bandejaTareasBean;
	
	@ManagedProperty(value = "#{informeTecnicoOficioGeneradorBean}")
	@Getter
	@Setter
	private InformeTecnicoOficioGeneradorBean informeTecnicoOficioGeneradorBean;
	
	@EJB
	private UsuarioServiceBean usuarioServiceBean;
	
	@EJB
	private ProcesoFacade procesoFacade;
	
	@EJB
	private AreaFacade areaFacade;
	
	@EJB
	private MensajeNotificacionFacade mensajeNotificacionFacade;
	
	@Getter
	@Setter
	private Boolean mostrarInforme;
	
	@Getter
	@Setter
	private String urlReporte, nombreReporte;
	
	@Getter
	@Setter
	private byte[] archivoReporte;

	@PostConstruct
	public void init() {
		try {
			informeTecnicoOficioGeneradorBean.generarInforme(true);
			mostrarInforme = true;
			
			urlReporte = informeTecnicoOficioGeneradorBean.getInforme().getInformePath();
			nombreReporte = informeTecnicoOficioGeneradorBean.getInforme().getNombreReporte();
			archivoReporte = informeTecnicoOficioGeneradorBean.getInforme().getArchivoInforme();
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos InformeTecnicoRetce.");
		}
	}
	
	public void validarTareaBpm() {
		JsfUtil.validarPaginasUrlTareasBpm(JsfUtil.getCurrentTask(),
				"/control/retce/generadorDesechos/realizarInforme.jsf");
	}
	
	public void guardarInforme() {
		informeTecnicoOficioGeneradorBean.guardarInforme();
				
		informeTecnicoOficioGeneradorBean.generarInforme(true);
		
		urlReporte = informeTecnicoOficioGeneradorBean.getInforme().getInformePath();
		nombreReporte = informeTecnicoOficioGeneradorBean.getInforme().getNombreReporte();
		archivoReporte = informeTecnicoOficioGeneradorBean.getInforme().getArchivoInforme();
		
		JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
	}
	
	public void aceptarInforme() {
		if (informeTecnicoOficioGeneradorBean.getInforme().getConclusiones() == null || 
				informeTecnicoOficioGeneradorBean.getInforme().getConclusiones().isEmpty()){
			JsfUtil.addMessageError("Debe ingresar las conclusiones.");
			return;
		}
		
		informeTecnicoOficioGeneradorBean.guardarInforme();
		
		informeTecnicoOficioGeneradorBean.generarOficio(true);
		
		urlReporte = informeTecnicoOficioGeneradorBean.getOficioRetce().getOficioPath();
		nombreReporte = informeTecnicoOficioGeneradorBean.getOficioRetce().getNombreReporte();
		archivoReporte = informeTecnicoOficioGeneradorBean.getOficioRetce().getArchivoOficio();
		
		mostrarInforme = false;
	}
	
	public void guardarRegresar() {
		informeTecnicoOficioGeneradorBean.generarInforme(true);
		
		urlReporte = informeTecnicoOficioGeneradorBean.getInforme().getInformePath();
		nombreReporte = informeTecnicoOficioGeneradorBean.getInforme().getNombreReporte();
		archivoReporte= informeTecnicoOficioGeneradorBean.getInforme().getArchivoInforme();
		
		mostrarInforme = true;
	}
	
	public void guardarOficio() {
		informeTecnicoOficioGeneradorBean.guardarInforme();
		informeTecnicoOficioGeneradorBean.guardarOficio();

		JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
		informeTecnicoOficioGeneradorBean.generarOficio(true);
	}

	public void aceptar() {
		try {
			informeTecnicoOficioGeneradorBean.guardarOficio();
			
			Map<String, Object> parametros = new HashMap<>();
			
			String usuarioCoordinadorBpm = JsfUtil.getCurrentTask().getVariable("usuario_coordinador").toString();
			//correccion para obtener el area del registro generador de desechos
			Area areaTramiteCoordinador = areaFacade.getArea(informeTecnicoOficioGeneradorBean.getGeneradorDesechosRetce().getIdArea());
			Usuario usuarioCoordinador=areaFacade.getCoordinadorPorArea(areaTramiteCoordinador);
			//si hay mas de un usuario coordinador buco el que sea principal en el areaTramite 
			List<Usuario> listaUsuarios = usuarioServiceBean.buscarUsuariosPorRolYAreaEspecifica(Constantes.getRoleAreaName("role.area.coordinador"), areaTramiteCoordinador);
			if(listaUsuarios != null && listaUsuarios.size() > 1 ){
				for (Usuario objUsuario : listaUsuarios) {
					for(AreaUsuario areausuario :objUsuario.getListaAreaUsuario()){
						if(areausuario.getArea().equals(areaTramiteCoordinador)){
							if(areausuario.getPrincipal() != null && areausuario.getPrincipal()){
								usuarioCoordinador = objUsuario;
								break;
							}
						}
					}
				}
			}
			if(usuarioCoordinador==null){
				LOG.error("No se encontro usuario coordinador en " + informeTecnicoOficioGeneradorBean.getAreaTramite().getAreaName());
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
				JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
			} else if(!usuarioCoordinadorBpm.equals(usuarioCoordinador.getNombre())) {
				parametros.put("usuario_coordinador",usuarioCoordinador.getNombre());
			}
			
			parametros.put("usuario_autoridad", informeTecnicoOficioGeneradorBean.getUsuarioAutoridad().getNombre());
			
			try {
				procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);
				procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
				
				Boolean tieneObservaciones = false;
				if(JsfUtil.getCurrentTask().getVariable("tiene_observaciones_informe_oficio") != null)
					tieneObservaciones = Boolean.valueOf(JsfUtil.getCurrentTask().getVariable("tiene_observaciones_informe_oficio").toString());
				
				String notificacion = "";
				String asunto = "";
				if(tieneObservaciones) {
					Object[] parametrosCorreo = new Object[] {JsfUtil.getLoggedUser().getPersona().getNombre(),
							informeTecnicoOficioGeneradorBean.getGeneradorDesechosRetce().getCodigoGenerador() };
					notificacion = mensajeNotificacionFacade.recuperarValorMensajeNotificacion(
									"bodyNotificacionTecnicoRemiteCorreccionesRetce", parametrosCorreo);
					asunto = "Correcciones realizadas al informe y oficio RETCE";
				} else {
					Object[] parametrosCorreo = new Object[] {"técnico", JsfUtil.getLoggedUser().getPersona().getNombre(),
							informeTecnicoOficioGeneradorBean.getGeneradorDesechosRetce().getCodigoGenerador() };
					notificacion = mensajeNotificacionFacade.recuperarValorMensajeNotificacion(
									"bodyNotificacionRemiteInformeOficioRetce", parametrosCorreo);
					asunto = "Revisar informe y oficio RETCE";
				}				

				Email.sendEmail(usuarioCoordinador, asunto, notificacion, informeTecnicoOficioGeneradorBean.getInformacionProyecto().getCodigo(), JsfUtil.getLoggedUser());
	
				JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
				JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
			} catch (JbpmException e) {
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al enviar la tarea.");
		}
	}
	
}
