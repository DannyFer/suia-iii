package ec.gob.ambiente.proyectos.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.suia.administracion.facade.ContactoFacade;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.FacilitadorProyecto;
import ec.gob.ambiente.suia.domain.FormasContacto;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.facilitador.facade.FacilitadorProyectoFacade;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.notificacionautoridades.controllers.NotificacionAutoridadesController;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoSuspendidoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class EliminarProyectoBean implements Serializable {

	private static final long serialVersionUID = 3404230403375114434L;

	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;

	@Getter
	private ProyectoLicenciamientoAmbiental proyecto;

	@EJB
	private ProcesoFacade procesoFacade;	
	
	//Cris F: nuevo Facade eliminacion
	@EJB
	private ProcesoSuspendidoFacade procesoSuspendidoFacade;
	
	@EJB
	private FacilitadorProyectoFacade facilitadorProyectoFacade;
	@EJB
    private MensajeNotificacionFacade mensajeNotificacionFacade;
	@EJB
	private UsuarioFacade usuarioFacade;
	@EJB
	private ContactoFacade contactoFacade;
	//fin variables nuevas

	@Getter
	@Setter
	private String observaciones;

	private static final Logger LOG = Logger.getLogger(EliminarProyectoBean.class);

	@PostConstruct
	public void init() {
		try {
			Integer idProyecto = Integer.parseInt(procesoFacade
					.recuperarVariablesProceso(JsfUtil.getLoggedUser(), JsfUtil.getCurrentProcessInstanceId())
					.get(Constantes.ID_PROYECTO).toString());
			proyecto = proyectoLicenciamientoAmbientalFacade.getProyectoPorId(idProyecto);
		} catch (Exception e) {
			LOG.error("Error al recuperar datos del proceso de eliminación de proyecto", e);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_INICIAR_TAREA);
		}
	}

	public String eliminar() {
		try {
			String notificarResultadosAdministradorTitle = "Se ha eliminado el proyecto.";
			String notificarResultadosAdministradorBody = "Se ha eliminado el proyecto solicitado.<br/>"
					+ "El proceso de eliminación ha concluido.<br/><br/><b>Observaciones del director:"
					+ "</b><br/>" + observaciones;

			String notificarDecisionProponenteTitle = "El proceso de eliminación del proyecto ha concluido";
			String notificarDecisionProponenteBody = "El proceso de eliminación del proyecto ha concluido, "
					+ "<b>su proyecto se ha eliminado</b> satisfactoriamente. A continuación, las observaciones "
					+ "emitidas durante el proceso:<br/><br/><hr/><b>Mesa de Ayuda</b> solicitó la eliminación "
					+ "ingresando el motivo:<br/><p>'" + proyecto.getMotivoEliminar() + "'</p><br/><b>El director</b> "
					+ "ejecutó la eliminación ingresando las observaciones:<br/><p>'" + observaciones
					+ "'</p><hr/>FIN";
			boolean desactivarTareas;
			desactivarTareas= proyectoLicenciamientoAmbientalFacade.eliminarProyecto(proyecto, JsfUtil.getLoggedUser());
			if(desactivarTareas)
			terminarProceso(observaciones, notificarResultadosAdministradorTitle, notificarResultadosAdministradorBody,
					notificarDecisionProponenteTitle, notificarDecisionProponenteBody);
			
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

						Usuario usuarioEnvio = new Usuario();
						usuarioEnvio.setNombrePersona(Constantes.SIGLAS_SITEAA);
		                NotificacionAutoridadesController mail_a = new NotificacionAutoridadesController();
		    			mail_a.sendEmailInformacionProponente(emailFacilitador, nombreFacilitador, mensaje, "Proceso archivado y/o dado de baja", proyecto.getCodigo(), usuario, usuarioEnvio);
					}					
				}				
			}
			//fin de correo

			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
		} catch (Exception ex) {
			LOG.error("Error al completar tarea eliminar del proceso eliminación de proyecto", ex);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
		}
		return JsfUtil.actionNavigateToBandeja();
	}

	public String rechazar() {
		try {
			String notificarResultadosAdministradorTitle = "Se ha rechazado la solicitud de eliminación.";
			String notificarResultadosAdministradorBody = "Se ha rechazado la solicitud de eliminación del "
					+ "proyecto.<br/>El proceso de eliminación ha concluido invalidando la solicitud.<br/><br/>"
					+ "<b>Observaciones del director:</b><br/>" + observaciones;

			String notificarDecisionProponenteTitle = "El proceso de eliminación del proyecto ha concluido. "
					+ "Solicitud rechazada";
			String notificarDecisionProponenteBody = "El proceso de eliminación del proyecto ha concluido, "
					+ "<b>su proyecto no se ha eliminado, la solicitud fue rechazada</b>. A continuación, las "
					+ "observaciones emitidas durante el proceso:<br/><br/><hr/><b>Mesa de Ayuda</b> solicitó la "
					+ "eliminación ingresando el motivo:<br/><p>'" + proyecto.getMotivoEliminar() + "'</p><br/>"
					+ "<b>El director</b> rechazó la solicitud de eliminación ingresando las "
					+ "observaciones:<br/><p>'" + observaciones + "'</p><hr/>FIN";

			terminarProceso(observaciones, notificarResultadosAdministradorTitle, notificarResultadosAdministradorBody,
					notificarDecisionProponenteTitle, notificarDecisionProponenteBody);

			proyecto.setMotivoEliminar(null);
			proyectoLicenciamientoAmbientalFacade.actualizarSimpleProyecto(proyecto);
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
		} catch (Exception ex) {
			LOG.error("Error al completar tarea rechazar del proceso eliminación de proyecto", ex);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
		}
		return JsfUtil.actionNavigateToBandeja();
	}

	public String cancelar() {
		return JsfUtil.actionNavigateToBandeja();
	}

	private void terminarProceso(String observaciones, String notificarResultadosAdministradorTitle,
			String notificarResultadosAdministradorBody, String notificarDecisionProponenteTitle,
			String notificarDecisionProponenteBody) throws JbpmException {

		Map<String, Object> params = new HashMap<String, Object>();

		params.put("notificarResultadosAdministradorTitle", notificarResultadosAdministradorTitle);
		params.put("notificarResultadosAdministradorBody", notificarResultadosAdministradorBody);
		params.put("observacionesDirector", observaciones);
		params.put("notificarDecisionProponenteTitle", notificarDecisionProponenteTitle);
		params.put("notificarDecisionProponenteBody", notificarDecisionProponenteBody);

		//Eliminamos las tildes y los caracteres especiales.
		Set<String> paramsKeys = params.keySet();
		Object value;
		String text="";
		for (String key : paramsKeys){
			value = params.get(key);
			if (value.getClass().getName().equalsIgnoreCase("java.lang.String"))
				text = (String)value;
				if (text.contains("<") && text.contains(">"))//Asumimos que es un texto en formato HTML
					params.put(key, reemplazarCaracteresEspecialesHTML(text));
				else
					params.put(key, reemplazarCaracteresEspeciales(text));

		}
		procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), JsfUtil.getCurrentProcessInstanceId(), params);
		procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), JsfUtil.getCurrentTask().getTaskId(), JsfUtil.getCurrentTask().getProcessInstanceId(), null);
	}

	private static final String ORIGINAL
			= "ÁáÉéÍíÓóÚúÑñÜü";
	private static final String REPLACEMENT
			= "AaEeIiOoUuNnUu";
	private static final String REPLACEMENT_HTML
			= "&Aacute;--&aacute;--&Eacute;--&eacute;--" +
			"&Iacute;--&iacute;--&Oacute;--&oacute;--&Uacute;--" +
			"&uacute;--&Ntilde;--&ntilde;--U--u";

	/***
	 * @Autor Denis Linares
	 * Método que reemplaza las tíldes y caracteres especiales de una cadena te texto plano por otros.
	 * Esto se utiliza para evitar errores el el BPM.
	 * @param value
	 * @return
	 */
	private String reemplazarCaracteresEspeciales(String value) {

		if (value != null && !value.isEmpty()) {
			char[] array = value.toCharArray();
			for (int index = 0; index < array.length; index++) {
				int pos = ORIGINAL.indexOf(array[index]);
				if (pos > -1) {
					array[index] = REPLACEMENT.charAt(pos);
				}
			}
			return new String(array);
		}

		return value;
	}

	/***
	 * @Autor Denis Linares
	 * Método que reemplaza las tíldes y caracteres especiales de una cadena de texto en formato HTML por otros.
	 * Esto se utiliza para evitar errores el el BPM.
	 * @param value
	 * @return
	 */
	private String reemplazarCaracteresEspecialesHTML(String value){

		if (value != null && !value.isEmpty()) {
			String[] replacementArr = REPLACEMENT_HTML.split("--");
			char[] array = value.toCharArray();

			ArrayList<String> foundList = new ArrayList<String>();
			String character;
			for (int index = 0; index < array.length; index++) {
				int pos = ORIGINAL.indexOf(array[index]);

				character = new Character(array[index]).toString();
				if (pos > -1 && !foundList.contains(character)) {
					value = value.replace(character, replacementArr[pos]);
					foundList.add(character);
				}
			}
		}
		return value;
	}
}
