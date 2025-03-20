package ec.gob.ambiente.control.retce.controllers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
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
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.control.retce.beans.InformeTecnicoOficioGeneradorBean;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.AreaUsuario;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.ObservacionesFormularios;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.observaciones.facade.ObservacionesFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.usuario.service.UsuarioServiceBean;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.DigitalSign;
import ec.gob.ambiente.suia.utils.Email;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class RevisionInformeOficioGeneradorController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final Logger LOG = Logger.getLogger(RevisionInformeOficioGeneradorController.class);
	
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
	private ObservacionesFacade observacionesFacade;
	
	@EJB
	private AreaFacade areaFacade;
	
	@EJB
	private ProcesoFacade procesoFacade;
	
	@EJB
    private UsuarioFacade usuarioFacade;
	
	@EJB
	private DocumentosFacade documentosFacade;
	
	@EJB
	private MensajeNotificacionFacade mensajeNotificacionFacade;
	
	@EJB
    private OrganizacionFacade organizacionFacade;
	
	@Getter
	@Setter
	private Boolean mostrarInforme, informeOficioCorrectos, mostrarFirma, esAutoridad;
	
	@Getter
	@Setter
	private String urlReporte, nombreReporte, nombreDocumentoFirmado;
	
	@Getter
	@Setter
	private byte[] archivoReporte;
	
	@Getter
	@Setter
	private boolean token, subido;
	
	@Getter
	@Setter
	private Integer panelMostrar;

	@PostConstruct
	public void init() {
		try {
			esAutoridad = false;
			if (JsfUtil.getCurrentTask().getTaskName().contains("Firmar")) {
				esAutoridad = true;
				verificaToken();
			}
				
			informeTecnicoOficioGeneradorBean.generarInforme(true);
			mostrarInforme = true;
			panelMostrar = 1;
			
			urlReporte = informeTecnicoOficioGeneradorBean.getInforme().getInformePath();
			nombreReporte = informeTecnicoOficioGeneradorBean.getInforme().getNombreReporte();
			archivoReporte = informeTecnicoOficioGeneradorBean.getInforme().getArchivoInforme();
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos InformeTecnicoRetce.");
		}
	}
	
	public void guardarToken() {
		Usuario usuario = JsfUtil.getLoggedUser();
		usuario.setToken(token);
		try {
			usuarioFacade.guardar(usuario);
			verificaToken();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean verificaToken() {
		token = false;
		if (JsfUtil.getLoggedUser().getToken() != null && JsfUtil.getLoggedUser().getToken())
			token = true;
		return token;
	}
	
	public void validarTareaBpm() {
		String url = "/control/retce/generadorDesechos/revisarInforme.jsf";
		if(JsfUtil.getCurrentTask().getTaskName().contains("Firmar"))
			url = "/control/retce/generadorDesechos/firmarPronunciamiento.jsf";
			
		JsfUtil.validarPaginasUrlTareasBpm(JsfUtil.getCurrentTask(), url);
	}
	
	public void guardar() {
				
		JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
		informeTecnicoOficioGeneradorBean.generarInforme(true);
	}
	
	public void aceptarInforme() {
		informeTecnicoOficioGeneradorBean.generarOficio(true);
		
		urlReporte = informeTecnicoOficioGeneradorBean.getOficioRetce().getOficioPath();
		nombreReporte = informeTecnicoOficioGeneradorBean.getOficioRetce().getNombreReporte();
		archivoReporte = informeTecnicoOficioGeneradorBean.getOficioRetce().getArchivoOficio();
		
		mostrarInforme = false;
	}
	
	public void verInforme() {
		informeTecnicoOficioGeneradorBean.generarInforme(true);
		
		urlReporte = informeTecnicoOficioGeneradorBean.getInforme().getInformePath();
		nombreReporte = informeTecnicoOficioGeneradorBean.getInforme().getNombreReporte();
		archivoReporte= informeTecnicoOficioGeneradorBean.getInforme().getArchivoInforme();
		
		mostrarInforme = true;
	}
	
	public void validarExisteObservacionesInformeOficio() {
		try {
			informeOficioCorrectos = false;
			List<ObservacionesFormularios> observacionesInforme = observacionesFacade.listarPorIdClaseNombreClaseNoCorregidas(
							informeTecnicoOficioGeneradorBean.getInforme().getId(), "informeGeneradorRetce");
			
			informeTecnicoOficioGeneradorBean.getOficio();
			List<ObservacionesFormularios> observacionesOficio = observacionesFacade.listarPorIdClaseNombreClaseNoCorregidas(
					informeTecnicoOficioGeneradorBean.getOficioRetce().getId(), "oficioGeneradorRetce");
			
			if(observacionesInforme.size() == 0 && observacionesOficio.size() == 0) {
				informeOficioCorrectos = true;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar las observaciones del informe oficio RETCE.");
		}
	}
	
	public void enviar() {
		try {			
			Map<String, Object> parametros = new HashMap<>();
			
			String usuarioCoordinadorBpm = JsfUtil.getCurrentTask().getVariable("usuario_coordinador").toString();
			//correccion para obtener el area del registro generador de desechos
			Area areaTramiteCoordinador = areaFacade.getArea(informeTecnicoOficioGeneradorBean.getGeneradorDesechosRetce().getIdArea());			
			Usuario usuarioCoordinador = areaFacade.getCoordinadorPorArea(areaTramiteCoordinador);
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
			if (usuarioCoordinador == null) {
				LOG.error("No se encontro usuario coordinador en " + informeTecnicoOficioGeneradorBean.getAreaTramite().getAreaName());
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
				JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
			} else if (!usuarioCoordinadorBpm.equals(usuarioCoordinador.getNombre())) {
				parametros.put("usuario_coordinador", usuarioCoordinador.getNombre());
			}

			String usuarioAutoridadBpm = JsfUtil.getCurrentTask().getVariable("usuario_autoridad").toString();
			if (!usuarioAutoridadBpm.equals(informeTecnicoOficioGeneradorBean.getUsuarioAutoridad().getNombre())) {
				parametros.put("usuario_autoridad", informeTecnicoOficioGeneradorBean.getUsuarioAutoridad().getNombre());
			}
			
			parametros.put("tiene_observaciones_informe_oficio", !informeOficioCorrectos);
			
			try {
				procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);
				procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
				
				if(!informeOficioCorrectos)
					enviarNotificacion(informeTecnicoOficioGeneradorBean.getUsuarioElabora(), "bodyNotificacionRemiteObservacionesInformeRetce", "Observaciones emitidas al informe y oficio RETCE");
				else
					enviarNotificacion(informeTecnicoOficioGeneradorBean.getUsuarioAutoridad(), "bodyNotificacionRemiteInformeOficioRetce", "Revisión de informe y oficio RETCE");
	
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
	
	public void enviarNotificacion(Usuario destinatario, String nombreNotificacion, String asunto) {
		try{
			String tipoUsuario = "coordinador";
			if (esAutoridad)
				tipoUsuario = "director";

			Object[] parametrosCorreo = new Object[] { tipoUsuario,
					JsfUtil.getLoggedUser().getPersona().getNombre(),
					informeTecnicoOficioGeneradorBean.getGeneradorDesechosRetce().getCodigoGenerador() };
			String notificacion = mensajeNotificacionFacade
					.recuperarValorMensajeNotificacion(nombreNotificacion, parametrosCorreo);
			
			Email.sendEmail(destinatario, "Observaciones emitidas al informe y oficio RETCE", notificacion, informeTecnicoOficioGeneradorBean.getInformacionProyecto().getCodigo(), JsfUtil.getLoggedUser());
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al enviar la tarea.");
		}
	}
	
	public void atras() {
		switch (panelMostrar) {
		case 2:
			informeTecnicoOficioGeneradorBean.generarInforme(true);
			
			urlReporte = informeTecnicoOficioGeneradorBean.getInforme().getInformePath();
			nombreReporte = informeTecnicoOficioGeneradorBean.getInforme().getNombreReporte();
			archivoReporte= informeTecnicoOficioGeneradorBean.getInforme().getArchivoInforme();
			
			mostrarInforme = true;
			panelMostrar = 1;
			break;
		case 3:
			informeTecnicoOficioGeneradorBean.generarOficio(true);
			urlReporte = informeTecnicoOficioGeneradorBean.getOficioRetce().getOficioPath();
			nombreReporte = informeTecnicoOficioGeneradorBean.getOficioRetce().getNombreReporte();
			archivoReporte = informeTecnicoOficioGeneradorBean.getOficioRetce().getArchivoOficio();
			
			mostrarInforme = false;
			panelMostrar = 2;
		default:
			break;
		}
		
	}
	
	public void siguiente() {
		switch (panelMostrar) {
		case 1:
			informeTecnicoOficioGeneradorBean.guardarOficio();
			informeTecnicoOficioGeneradorBean.generarOficio(true);
			urlReporte = informeTecnicoOficioGeneradorBean.getOficioRetce().getOficioPath();
			nombreReporte = informeTecnicoOficioGeneradorBean.getOficioRetce().getNombreReporte();
			archivoReporte = informeTecnicoOficioGeneradorBean.getOficioRetce().getArchivoOficio();
			
			mostrarInforme = false;
			panelMostrar = 2;
			break;
		
		default:
			break;
		}
	}
	
	public void firmarInformeOficio() {		
		mostrarFirma = true;
		panelMostrar = 3;
	}
	
	public StreamedContent descargar() throws IOException {
		DefaultStreamedContent content = null;
		try {
			informeTecnicoOficioGeneradorBean.generarDocumentoOficio();
			
			byte[] documentoContent = informeTecnicoOficioGeneradorBean.getOficioRetce().getArchivoOficio();
			
			if (documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(informeTecnicoOficioGeneradorBean.getOficioRetce().getNombreReporte());
			} else
				JsfUtil.addMessageError("Error al generar el archivo");

		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
	
	public void uploadListenerDocumentos(FileUploadEvent event) {
		if(informeTecnicoOficioGeneradorBean.getDocumentoOficioPronunciamiento() != null) {
	        byte[] contenidoDocumento = event.getFile().getContents();
	        
	        nombreDocumentoFirmado = event.getFile().getFileName();
	
	        informeTecnicoOficioGeneradorBean.getDocumentoOficioPronunciamiento().setContenidoDocumento(contenidoDocumento);
	        informeTecnicoOficioGeneradorBean.getDocumentoOficioPronunciamiento().setNombre(event.getFile().getFileName());
	        
	        subido = true;
		} else{
			JsfUtil.addMessageError("No ha realizado la descarga del oficio");
		}
    }
	
	public String firmarDocumento() {
		try {
			informeTecnicoOficioGeneradorBean.subirOficio();
			
			if(informeTecnicoOficioGeneradorBean.getDocumentoOficioPronunciamiento() != null) {
				String documentOffice = documentosFacade.direccionDescarga(informeTecnicoOficioGeneradorBean.getDocumentoOficioPronunciamiento());
				return DigitalSign.sign(documentOffice, JsfUtil.getLoggedUser().getNombre());
			}
		} catch (Exception exception) {
			LOG.error("Ocurrió un error durante la firma del oficio por el director", exception);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
		return "";
	}
	
	public void completarTarea() {
		try {
			Documento documento = informeTecnicoOficioGeneradorBean.getDocumentoOficioPronunciamiento();
			if (documento != null) {
				if (subido) {
					informeTecnicoOficioGeneradorBean.getOficioRetce().setArchivoOficio(
									informeTecnicoOficioGeneradorBean.getDocumentoOficioPronunciamiento().getContenidoDocumento());
					informeTecnicoOficioGeneradorBean.guardarDocumentoOficio();
				}

				String idAlfresco = informeTecnicoOficioGeneradorBean.getDocumentoOficioPronunciamiento().getIdAlfresco();

				if (token && !documentosFacade.verificarFirmaVersion(idAlfresco)) {
					JsfUtil.addMessageError("El oficio no está firmado electrónicamente.");
					return;
				} else if (!token && !subido) {
					JsfUtil.addMessageError("Debe adjuntar el oficio firmado.");
					return;
				}
				
				informeTecnicoOficioGeneradorBean.subirInforme();

				Map<String, Object> parametros = new HashMap<>();
				
				parametros.put("pronunciamiento_aprobado", informeTecnicoOficioGeneradorBean.getInforme().getEsReporteAprobacion());
				procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);
				
				procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getTarea().getProcessInstanceId(), null);
				
				String usuario = JsfUtil.getCurrentTask().getVariable("usuario_operador").toString();
				Usuario usuarioOperador=usuarioFacade.buscarUsuario(usuario);
				Organizacion organizacion = organizacionFacade.buscarPorRuc(usuario);
				String nombreOperador = JsfUtil.getNombreOperador(usuarioOperador, organizacion);
				
				Object[] parametrosCorreo = new Object[] {
						nombreOperador, informeTecnicoOficioGeneradorBean.getGeneradorDesechosRetce().getCodigoGenerador() };
				String notificacion = mensajeNotificacionFacade.recuperarValorMensajeNotificacion(
								"bodyNotificacionPronunciamientoEmitido", parametrosCorreo);
				
				Email.sendEmail(usuarioOperador, "Pronunciamiento RETCE emitido", notificacion, informeTecnicoOficioGeneradorBean.getInformacionProyecto().getCodigo(), JsfUtil.getLoggedUser());

				JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
				JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
			} else {
				JsfUtil.addMessageError("Error al realizar la operación el oficio no está firmado.");
			}
		} catch (Exception e) {
			LOG.error(e);
			JsfUtil.addMessageError("Error al realizar la operación.");
		}
	}
}
