package ec.gob.ambiente.rcoa.viabilidadAmbiental.controllers;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.BaseColor;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaUbicacionFacade;
import ec.gob.ambiente.rcoa.model.AreasSnapProvincia;
import ec.gob.ambiente.rcoa.model.DetalleInterseccionProyectoAmbiental;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.DocumentosViabilidadFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.InterseccionViabilidadCoaFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.PlantillaReporteFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.PreguntasFormularioFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.RespuestasFormularioFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.ViabilidadCoaFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.CabeceraFormulario;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.DocumentoViabilidad;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.PreguntaFormulario;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.ProyectoTipoViabilidadCoa;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.RespuestaFormularioSnap;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.ViabilidadCoa;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Persona;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.Email;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilGenerarInforme;
import ec.gob.ambiente.suia.webservice.facade.AsignarTareaFacade;

@ManagedBean
@ViewScoped
public class RegistrarSnapController implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private final Logger LOG = Logger.getLogger(RegistrarSnapController.class);
	
	@Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;
	
	@ManagedProperty(value = "#{bandejaTareasBean}")
    @Getter
    @Setter
    private BandejaTareasBean bandejaTareasBean;

	@EJB
	private PreguntasFormularioFacade preguntasFormularioFacade;

	@EJB
	private ViabilidadCoaFacade viabilidadCoaFacade;

	@EJB
	private RespuestasFormularioFacade respuestasFormularioFacade;
	
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	
	@EJB
    private AsignarTareaFacade asignarTareaFacade;
	
	@EJB
	private AreaFacade areaFacade;
	
	@EJB
    private ProcesoFacade procesoFacade;
	
	@EJB
	private PlantillaReporteFacade plantillaReporteFacade;
	
	@EJB
	private DocumentosViabilidadFacade documentosFacade;
	
	@EJB
	private MensajeNotificacionFacade mensajeNotificacionFacade;
	
	@EJB
	private UsuarioFacade usuarioFacade;
	
	@EJB
	private InterseccionViabilidadCoaFacade interseccionViabilidadCoaFacade;
	
	@EJB
	private UbicacionGeograficaFacade ubicacionGeograficaFacade;
	
	@EJB
	private ProyectoLicenciaCoaUbicacionFacade proyectoLicenciaCoaUbicacionFacade;
	
	@Getter
	@Setter
	private ViabilidadCoa viabilidadProyecto;
	
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyectoLicenciaCoa;
	
	@Getter
	@Setter
	private ProyectoTipoViabilidadCoa proyectoTipoViabilidadCoa;
	
	@Getter
	@Setter
	private DocumentoViabilidad documentoSustento, documentoJuridico, documentoFormulario;

	@Getter
	@Setter
	private Boolean verGeneral, form1Guardado, form2Guardado, documentoGenerado;
	
	@Getter
	@Setter
	private Boolean existeConflictoLegal, documentoDescargado, isGuardado, esRevision; //para revision tecnico

	@Getter
	@Setter
	Integer idProyecto;
	
	private String notaSustento;

	@Getter
	@Setter
	private List<CabeceraFormulario> preguntasForm1, preguntasForm2;
	
	private Map<String, Object> variables;


	@PostConstruct
	private void iniciar() {
		try {
			variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());

			String idProyectoString=(String)variables.get("idProyecto");
			
			idProyecto = Integer.valueOf(idProyectoString); 
			verGeneral = true;
			form1Guardado = false;
			form2Guardado = false;
			
			isGuardado = false;
			
			esRevision = false;
			if (JsfUtil.getCurrentTask().getTaskName().contains("Revisar")) {
				esRevision = true;
				documentoDescargado = false;
				
				String nombre = viabilidadCoaFacade.getNombreTarea(JsfUtil.getCurrentTask().getTaskId());
				
				Boolean esSnapMae = false;
				if (nombre.contains("RevisarInformacionSnapMae")) {
					esSnapMae = true;
				}
				
				viabilidadProyecto = viabilidadCoaFacade.getViabilidadSnapPorProyectoTipo(idProyecto, esSnapMae);
			} else {
				viabilidadProyecto = new ViabilidadCoa();
			}
			
			proyectoLicenciaCoa = proyectoLicenciaCoaFacade.buscarProyectoPorId(idProyecto);
			
			proyectoTipoViabilidadCoa = viabilidadCoaFacade.getTipoViabilidadPorProyecto(idProyecto, true);
	
			preguntasForm1 = preguntasFormularioFacade.getListaCabecerasPorOrdenTipo(1, 1);
	
			List<CabeceraFormulario> preguntasOrden2 = preguntasFormularioFacade.getListaCabecerasPorOrdenTipo(2, 1);
			List<CabeceraFormulario> preguntasOrden3 = preguntasFormularioFacade.getListaCabecerasPorOrdenTipo(3, 1);
	
			preguntasForm2 = new ArrayList<>();
			if (preguntasOrden2 != null)
				preguntasForm2.addAll(preguntasOrden2);
	
			if (preguntasOrden3 != null)
				preguntasForm2.addAll(preguntasOrden3);
			
			if(proyectoTipoViabilidadCoa == null) {
				proyectoTipoViabilidadCoa = new ProyectoTipoViabilidadCoa();
				proyectoTipoViabilidadCoa.setIdProyectoLicencia(idProyecto);
				proyectoTipoViabilidadCoa.setEsViabilidadSnap(true);
				viabilidadCoaFacade.guardarProyectoTipoViabilidadCoa(proyectoTipoViabilidadCoa);
			}
	
			if (proyectoTipoViabilidadCoa != null) {
				cargarDatos();
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos RegistroSnap.");
		}
	}
	
	public void validarTareaBpm() {
		JsfUtil.validarPaginasUrlTareasBpm(JsfUtil.getCurrentTask(),
				"/pages/rcoa/viabilidadAmbiental/snap/ingresarInfoSnap.jsf");
	}
	
	public void validarTareaRevisionBpm() {
		JsfUtil.validarPaginasUrlTareasBpm(JsfUtil.getCurrentTask(),
				"/pages/rcoa/viabilidadAmbiental/snap/revisarInfoSnap.jsf");
	}
	
	public void cargarDatos() {
		for (CabeceraFormulario cabecera : preguntasForm1) {
			for (PreguntaFormulario preguntaForm : cabecera
					.getListaPreguntas()) {
				RespuestaFormularioSnap respuesta = respuestasFormularioFacade
						.getPorTipoProyectoViabilidadPregunta(proyectoTipoViabilidadCoa.getId(), preguntaForm.getId());
				if (respuesta != null) {
					preguntaForm.setRespuesta(respuesta);
				}
			}
		}
		
		for (CabeceraFormulario cabecera : preguntasForm2) {
			for (PreguntaFormulario preguntaForm : cabecera
					.getListaPreguntas()) {
				RespuestaFormularioSnap respuesta = respuestasFormularioFacade
						.getPorTipoProyectoViabilidadPregunta(proyectoTipoViabilidadCoa.getId(), preguntaForm.getId());
				if (respuesta != null) {
					preguntaForm.setRespuesta(respuesta);
				}
			}
		}
		
		List<DocumentoViabilidad> documentos = documentosFacade
				.getDocumentoPorTipoViabilidadTramite(
						proyectoTipoViabilidadCoa.getId(),
						TipoDocumentoSistema.RCOA_VIABILIDAD_SNAP_SUSTENTO
								.getIdTipoDocumento());
		if (documentos.size() > 0) {
			documentoSustento = documentos.get(0);
		}
		
		if(esRevision) {
			List<DocumentoViabilidad> documentosJuridico = documentosFacade
					.getDocumentoPorTipoTramite(
							viabilidadProyecto.getId(),
							TipoDocumentoSistema.RCOA_VIABILIDAD_ANALISIS_JURIDICO
									.getIdTipoDocumento());
			if (documentosJuridico.size() > 0) {
				documentoJuridico = documentosJuridico.get(0);
			}			
		}
	}

	public void guardarCampo(PreguntaFormulario pregunta) {
		try {
			guardarRespuesta(pregunta);
			
			JsfUtil.addMessageInfo(JsfUtil.REGISTRO_GUARDADO);
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al guardar los datos.");
		}
	}
	
	public void guardarRespuesta(PreguntaFormulario pregunta) {
		if (pregunta.getRespuesta() != null) {
			RespuestaFormularioSnap respuesta = pregunta.getRespuesta();
			respuesta.setIdProyectoViabilidad(proyectoTipoViabilidadCoa.getId());
			respuesta.setIdPregunta(pregunta.getId());

			respuestasFormularioFacade.guardar(respuesta);

			pregunta.setRespuesta(respuesta);
		}
	}

	public void guardarGeneral() {
		try {
			// si esta activo el panel con las primeras preguntas
			if (verGeneral) {
				for (CabeceraFormulario cabecera : preguntasForm1) {
					for (PreguntaFormulario preguntaForm : cabecera
							.getListaPreguntas()) {
						guardarRespuesta(preguntaForm);
					}
				}
				form1Guardado = true;
			} else{
				for (CabeceraFormulario cabecera : preguntasForm2) {
					for (PreguntaFormulario preguntaForm : cabecera
							.getListaPreguntas()) {
						guardarRespuesta(preguntaForm);
					}
				}
				
				form1Guardado = true;
				
				if (documentoSustento.getContenidoDocumento() != null) {
					documentoSustento.setIdProyectoViabilidad(proyectoTipoViabilidadCoa.getId());
					documentoSustento = documentosFacade.guardarDocumentoProceso(
							proyectoLicenciaCoa.getCodigoUnicoAmbiental(),
							"VIABILIDAD_AMBIENTAL", documentoSustento, 1, JsfUtil.getCurrentProcessInstanceId());
					
					if(documentoSustento == null || documentoSustento.getId() == null) {
						JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
						return;
					}
				}
			}

			JsfUtil.addMessageInfo(JsfUtil.REGISTRO_GUARDADO);
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al guardar los datos.");
		}
	}
	
	public void validateDatosIngreso(FacesContext context, UIComponent validate, Object value) {
		List<FacesMessage> errorMessages = new ArrayList<>();
		if (documentoSustento == null || (documentoSustento.getId() == null && documentoSustento.getContenidoDocumento() == null))
			errorMessages.add(new FacesMessage(
							FacesMessage.SEVERITY_ERROR,
							"El campo 'Adjuntar' es requerido.", null));
		
		if (!errorMessages.isEmpty())
			throw new ValidatorException(errorMessages);
	}

	public void enviar() {
		try {

			Map<String, Object> parametros = new HashMap<>();
			
			Map<Usuario, List<String>> notificaciones = new HashMap<>();
			
			Boolean esMae = false;
			Boolean esDelegada = false;
			
			Area areaTramite = proyectoLicenciaCoa.getAreaResponsable();
			
			List<DetalleInterseccionProyectoAmbiental> interseccionesProyecto = interseccionViabilidadCoaFacade.getDetalleInterseccionSnapPorProyecto(idProyecto);
			if(interseccionesProyecto != null) {
				for (DetalleInterseccionProyectoAmbiental detalle : interseccionesProyecto) {
					if(detalle.getInterseccionProyectoLicenciaAmbiental().getCapa().getId().equals(Constantes.ID_CAPA_SNAP)){
						List<AreasSnapProvincia> ubicacionesCapa = interseccionViabilidadCoaFacade.getUbicacionSnapPorCodigoCapa(detalle.getCodigoUnicoCapa());
						if(ubicacionesCapa != null) {
							List<Integer> idsProvincias = new ArrayList<>();
							for (AreasSnapProvincia area : ubicacionesCapa) {
								if(!idsProvincias.contains(area.getGeloIdProvincia()))
									idsProvincias.add(area.getGeloIdProvincia());
							}
							
							UbicacionesGeografica ubicacionAreaSnap = null;
							if(idsProvincias.size() > 1) {
								//si la capa esta en mas de una provincia se asigna a la DP de la provincia que llevo el Registro Preliminar
								//areaTramite = areaFacade.getAreaDireccionProvincialPorUbicacion(2, proyectoLicenciaCoa.getAreaResponsable().getUbicacionesGeografica());
								UbicacionesGeografica ubicacionPrincipal = proyectoLicenciaCoaUbicacionFacade.ubicacionPrincipal(proyectoLicenciaCoa).getUbicacionesGeografica();
								ubicacionAreaSnap = ubicacionPrincipal.getUbicacionesGeografica().getUbicacionesGeografica();
							} else {
								//si esta en una sola provinvia se busca la DP de la provincia en la que se encuentra el área
								ubicacionAreaSnap = ubicacionGeograficaFacade.buscarPorId(ubicacionesCapa.get(0).getGeloIdProvincia());
								//areaTramite = areaFacade.getAreaDireccionProvincialPorUbicacion(2, ubicacionAreaSnap);
							}
														
							AreasSnapProvincia areaSnap = getInfoAreaSnap(detalle.getCodigoUnicoCapa(), ubicacionAreaSnap.getId(), ubicacionesCapa);
							Usuario tecnicoResponsable = (areaSnap != null) ? areaSnap.getTecnicoResponsable() : null;
							
							if(areaSnap == null || tecnicoResponsable == null || !tecnicoResponsable.getEstado()) {
								LOG.error("No se encontro usuario responsable para el área " + areaSnap.getNombreAreaProtegida());
								JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
								JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
								return;
							}
							
							List<String> datos = new ArrayList<>();
							datos.add(ubicacionAreaSnap.getNombre());
							datos.add("área protegida " + areaSnap.getNombreAreaProtegida());
							notificaciones.put(tecnicoResponsable, datos);
							
							/**
							 * comentado para obtener el área utilizando la nueva forma
							 */
//							areaTramite = tecnicoResponsable.getArea();
							
							if(areaSnap.getTipoSubsitema().equals(1) && !esMae) {
								//se guarda la viabilidad cuando es un área de administracion del MAE y si aun no se ha generado la viabilidad 
								esMae = true;
								parametros.put("jefeAreaProtegida", tecnicoResponsable.getNombre());
								
								ViabilidadCoa viabilidadMAE = viabilidadCoaFacade.getViabilidadSnapPorProyectoTipo(idProyecto, true);
								if(viabilidadMAE == null) {
									viabilidadMAE = new ViabilidadCoa();
									viabilidadMAE.setIdProyectoLicencia(idProyecto);
									viabilidadMAE.setIdProyectoTipoViabilidad(proyectoTipoViabilidadCoa.getId());
									viabilidadMAE.setEsViabilidadSnap(true);
									viabilidadMAE.setEsAdministracionMae(true);
									viabilidadMAE.setAreaResponsable(areaTramite);
									viabilidadMAE.setViabilidadCompletada(false);
									viabilidadMAE.setAreaSnap(areaSnap);

									viabilidadCoaFacade.guardar(viabilidadMAE);
								} else {
									viabilidadMAE.setAreaResponsable(areaTramite);
									viabilidadMAE.setAreaSnap(areaSnap);
									viabilidadCoaFacade.guardar(viabilidadMAE);
								}
							}
							
							if(areaSnap.getTipoSubsitema().equals(2) && !esDelegada) {
								//se guarda la viabilidad cuando es un área de administracion DELEGADA y si aun no se ha generado la viabilidad
								esDelegada = true;
								parametros.put("tecnicoPatrimonio", tecnicoResponsable.getNombre());
								
								ViabilidadCoa viabilidadDelegada = viabilidadCoaFacade.getViabilidadSnapPorProyectoTipo(idProyecto, false);
								if(viabilidadDelegada == null) {
									viabilidadDelegada = new ViabilidadCoa();
									viabilidadDelegada.setIdProyectoLicencia(idProyecto);
									viabilidadDelegada.setIdProyectoTipoViabilidad(proyectoTipoViabilidadCoa.getId());
									viabilidadDelegada.setEsViabilidadSnap(true);
									viabilidadDelegada.setEsAdministracionMae(false);
									viabilidadDelegada.setAreaResponsable(areaTramite);
									viabilidadDelegada.setViabilidadCompletada(false);
									viabilidadDelegada.setAreaSnap(areaSnap);

									viabilidadCoaFacade.guardar(viabilidadDelegada);
								} else {
									viabilidadDelegada.setAreaResponsable(areaTramite);
									viabilidadDelegada.setAreaSnap(areaSnap);
									viabilidadCoaFacade.guardar(viabilidadDelegada);
								}
							}
							
						}else {
							LOG.error("No se encontro información de ubicacion en la tabla province_areas_snap para la capa " + detalle.getIdGeometria() + " - " + detalle.getNombreGeometria());
							JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
							JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
							return;
						}
					}
				}
			} else {
				LOG.error("No se encontro intersecciones para el proyecto " + proyectoLicenciaCoa.getCodigoUnicoAmbiental());
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
				JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
				return;
			}			
			
			//Generacion de documento de informacion ingresada por el operador, si no se genera mensaje error
			if(!generarDocumentoIngreso()) {
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
				return;
			}

			try {
				parametros.put("administracionDelegada", esDelegada);
				parametros.put("administracionMae", esMae);
				parametros.put(Constantes.USUARIO_VISTA_MIS_PROCESOS, JsfUtil.getLoggedUser().getNombre());
				
				procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);
				
				procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
				
				Usuario directorBio = areaFacade.getDirectorPlantaCentralPorArea("role.pc.director.biodiversidad");
				
				String formatoNotificacionTecnicos = mensajeNotificacionFacade
						.buscarMensajesNotificacion("bodyNotificacionIngresoTramiteViabilidadTecnico").getValor();
				
				String formatoNotificacionDirector = mensajeNotificacionFacade
						.buscarMensajesNotificacion("bodyNotificacionIngresoTramiteViabilidadSnap").getValor();
				
				//notificacion a los tecnicos
				for (Map.Entry<Usuario, List<String>> entry : notificaciones.entrySet()) {
				    Object[] parametrosCorreoTecnicos = new Object[] {
							proyectoLicenciaCoa.getNombreProyecto(), proyectoLicenciaCoa.getCodigoUnicoAmbiental(), 
							entry.getValue().get(0), entry.getValue().get(1) };
					String notificacionTecnicos = String.format(formatoNotificacionTecnicos, parametrosCorreoTecnicos);
					
					Email.sendEmail(entry.getKey(), "Nuevo trámite Viabilidad Ambiental", notificacionTecnicos, proyectoLicenciaCoa.getCodigoUnicoAmbiental(), loginBean.getUsuario());
					Thread.sleep(1000);
					
					// notificacion al Director Nacional de Biodiversidad 
					String nombreTecnico = entry.getKey().getNombre();
					Object[] parametrosCorreo = new Object[] {
							proyectoLicenciaCoa.getNombreProyecto(), proyectoLicenciaCoa.getCodigoUnicoAmbiental(), 
							entry.getValue().get(0), entry.getValue().get(1), nombreTecnico };
					String notificacionDirector = String.format(formatoNotificacionDirector, parametrosCorreo);

					Email.sendEmail(directorBio, "Nuevo trámite Viabilidad Ambiental", notificacionDirector, proyectoLicenciaCoa.getCodigoUnicoAmbiental(), loginBean.getUsuario());
					Thread.sleep(1000);
				}

				JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
				JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
			} catch (JbpmException e) {
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al enviar la tarea.");
		}
	}
	
	public AreasSnapProvincia getInfoAreaSnap(String codigoCapa, Integer idProvincia, List<AreasSnapProvincia> ubicacionesCapa) {
		//recupera el área a la cual se está asignando la viabilidad
		AreasSnapProvincia areaSnap = null;
		Usuario tecnicoResponsable = null;
		List<AreasSnapProvincia> areasSnapProvincia = interseccionViabilidadCoaFacade.getAreasSnapPorCapaProvincia(codigoCapa, idProvincia);
		if(areasSnapProvincia != null) {
			areaSnap = areasSnapProvincia.get(0);
			
			if(areaSnap.getTipoSubsitema().equals(1)) {
				//recuperar los usuarios responsables del área
				List<String> usuariosArea = new ArrayList<>();
				for (AreasSnapProvincia area : ubicacionesCapa) {
					if(area.getGeloIdProvincia().equals(areaSnap.getGeloIdProvincia()) && area.getUsuario() != null && !usuariosArea.contains(area.getUsuario().getNombre()))
						usuariosArea.add(area.getUsuario().getNombre());
				}
				
				if(usuariosArea.size() > 1) {
					//si existen dos usuarios asignados a la misma área y provincia. Se asigna el usuario con menor carga de trabajo
					List<Usuario> listaTecnicosResponsables = asignarTareaFacade
							.getCargaLaboralPorNombreUsuariosProceso(usuariosArea,
									Constantes.RCOA_PROCESO_VIABILIDAD);
					if(listaTecnicosResponsables != null && listaTecnicosResponsables.size() >0)
						tecnicoResponsable = listaTecnicosResponsables.get(0);
				} else {
					tecnicoResponsable = areaSnap.getUsuario();
				}
			} else {
				Area areaTramite = areaFacade.getAreaPorAreaAbreviacion(Constantes.SIGLAS_DIRECCION_AREAS_PROTEGIDAS);
				String tipoRol = "role.va.pc.tecnico.snap.delegada";
				
				String rolTecnico = Constantes.getRoleAreaName(tipoRol);
				
				//buscar usuarios por rol y area
				List<Usuario> listaUsuario = usuarioFacade.buscarUsuariosPorRolYArea(rolTecnico, areaTramite);
				if (listaUsuario == null || listaUsuario.size() == 0)
					return areaSnap;
				
				List<Usuario> listaTecnicosResponsables = asignarTareaFacade
						.getCargaLaboralPorUsuariosProceso(listaUsuario, Constantes.RCOA_PROCESO_VIABILIDAD);
				tecnicoResponsable = listaTecnicosResponsables.get(0);
			}
		}
		
		if(areaSnap != null)
			areaSnap.setTecnicoResponsable(tecnicoResponsable);
		
		return areaSnap;
	}

	public void siguiente(Boolean resultado) {
		verGeneral = resultado;
		
		if(!esRevision) {
			if (!resultado) 
				form1Guardado = false;
			else
				form2Guardado = false;
		}
	}

	public Boolean generarDocumentoIngreso() {
		try {
			
			PlantillaReporte plantillaDocumento = plantillaReporteFacade.getPlantillaReporte(TipoDocumentoSistema.RCOA_VIABILIDAD_SNAP_FORMULARIO_OPERADOR);
			
			cargarDatosDocumento();
	
			String nombreFichero = "FormularioViabilidadOperador" + proyectoTipoViabilidadCoa.getId() + ".pdf";
			String nombreReporte = "FormularioViabilidadOperador" + ".pdf";
	
			File oficioPdfAux = UtilGenerarInforme.generarFichero(
					plantillaDocumento.getHtmlPlantilla(),
					nombreReporte, true, viabilidadProyecto);
			
			File oficioPdf = JsfUtil.fileMarcaAgua(oficioPdfAux, " ", BaseColor.GRAY);
	
			Path path = Paths.get(oficioPdf.getAbsolutePath());
			byte[] archivoOficio = Files.readAllBytes(path);
			File archivoFinal = new File(JsfUtil.devolverPathReportesHtml(nombreFichero));
			FileOutputStream file = new FileOutputStream(archivoFinal);
			file.write(archivoOficio);
			file.close();
			
			DocumentoViabilidad documentoInforme = new DocumentoViabilidad();
			documentoInforme.setNombre("FormularioViabilidadOperador.pdf");
			documentoInforme.setContenidoDocumento(archivoOficio);
			documentoInforme.setMime("application/pdf");
			documentoInforme.setIdTipoDocumento(TipoDocumentoSistema.RCOA_VIABILIDAD_SNAP_FORMULARIO_OPERADOR.getIdTipoDocumento());
			documentoInforme.setIdProyectoViabilidad(proyectoTipoViabilidadCoa.getId());
	
			documentoInforme = documentosFacade.guardarDocumentoProceso(
					proyectoLicenciaCoa.getCodigoUnicoAmbiental(),
					"VIABILIDAD_AMBIENTAL", documentoInforme, 1, JsfUtil.getCurrentProcessInstanceId());
			
			if(documentoInforme != null && documentoInforme.getId() != null)
				return true;
			
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Error al generar el formulario del operador SNAP", e);
		}
		
		return false;
	}
	
	public void cargarDatosDocumento() {
		List<CabeceraFormulario> preguntasFormOperador = new ArrayList<>();
		preguntasFormOperador.addAll(preguntasForm1);
		preguntasFormOperador.addAll(preguntasForm2);
		
		StringBuilder stringBuilder = new StringBuilder();
		
		if (!preguntasFormOperador.isEmpty()){
			for (CabeceraFormulario cabecera : preguntasFormOperador) {
				stringBuilder.append("<p style=\"text-align: justify;\"><strong>" + cabecera.getDescripcion() + "</strong></p>");
				
				for (PreguntaFormulario preguntaForm : cabecera.getListaPreguntas()) {
					RespuestaFormularioSnap respuesta = respuestasFormularioFacade
							.getPorTipoProyectoViabilidadPregunta(proyectoTipoViabilidadCoa.getId(), preguntaForm.getId());
					if (respuesta != null) {
						stringBuilder.append("<p style=\"margin-left: 30px; text-align: justify;\">");
						stringBuilder.append("<em><strong>" + preguntaForm.getDescripcion() + "</strong></em><br/>");
						if(preguntaForm.getTipo().equals(2)) {
							stringBuilder.append(respuesta.getRespText());
						}
						stringBuilder.append("</p>");
					}
				}
			}
		}
		
		viabilidadProyecto.setRespuestasOperadorHtml(stringBuilder.toString());
	}
	
	public void uploadFile(FileUploadEvent event) {
		byte[] contenidoDocumento = event.getFile().getContents();
		documentoSustento = new DocumentoViabilidad();
		documentoSustento.setId(null);
		documentoSustento.setContenidoDocumento(contenidoDocumento);
		documentoSustento.setNombre(event.getFile().getFileName());
		documentoSustento.setMime("application/pdf");
		documentoSustento.setIdTipoDocumento(TipoDocumentoSistema.RCOA_VIABILIDAD_SNAP_SUSTENTO.getIdTipoDocumento());
	}
	
	public StreamedContent descargar(DocumentoViabilidad documentoDescarga) throws IOException {
		DefaultStreamedContent content = null;
		try {
			byte[] documentoContent = null;
			
			if (documentoDescarga != null && documentoDescarga.getIdAlfresco() != null) {
				documentoContent = documentosFacade.descargar(documentoDescarga.getIdAlfresco(), documentoDescarga.getFechaCreacion());
			} else if (documentoDescarga.getContenidoDocumento() != null) {
				documentoContent = documentoDescarga.getContenidoDocumento();
			}
			
			if (documentoDescarga != null && documentoDescarga.getNombre() != null
					&& documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(documentoDescarga.getNombre());
			} else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
			
			if(esRevision && documentoDescarga.getIdTipoDocumento().equals(TipoDocumentoSistema.RCOA_VIABILIDAD_SNAP_SUSTENTO.getIdTipoDocumento()))
				documentoDescargado = true;

		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
	
	//REVISION DE TECNICO
	public void actualizarExisteConflicto() {
		if(!viabilidadProyecto.getExisteConflictoLegal()){
			viabilidadProyecto.setDetalleConflictoLegal(null);
			documentoJuridico = null;
		}
	}
	
	public void guardarRevision() {
		try {
			viabilidadCoaFacade.guardar(viabilidadProyecto);
			
			if (documentoJuridico != null && documentoJuridico.getContenidoDocumento() != null) {
				documentoJuridico.setIdViabilidad(viabilidadProyecto.getId());
				documentoJuridico = documentosFacade.guardarDocumento(
						proyectoLicenciaCoa.getCodigoUnicoAmbiental(),
						"VIABILIDAD_AMBIENTAL", documentoJuridico, 2);
				
				if(documentoJuridico == null || documentoJuridico.getId() == null) {
					JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
					return;
				}
			}
			
			isGuardado = true;
			JsfUtil.addMessageInfo(JsfUtil.REGISTRO_GUARDADO);
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
		}
	}
	
	public void finalizarRevision() {
		try {
			Map<String, Object> parametros = new HashMap<>();
			
			String juridicoBpm = null;
			if(viabilidadProyecto.getEsAdministracionMae()) {
				parametros.put("existeConflictoLegalSnapMae", viabilidadProyecto.getExisteConflictoLegal());
				juridicoBpm =  (String) variables.get("abogadoAreaProtegida");
			} else {
				parametros.put("existeConflictoLegalSnapDelegada", viabilidadProyecto.getExisteConflictoLegal());
				juridicoBpm =  (String) variables.get("abogadoPatrimonio");
			}
			
			if(viabilidadProyecto.getExisteConflictoLegal()) {
				Area areaJuridico = proyectoLicenciaCoa.getAreaResponsable().getArea()!=null?proyectoLicenciaCoa.getAreaResponsable().getArea():proyectoLicenciaCoa.getAreaResponsable(); //el juridico para SNAP siempre se toma de las zonales
				String rolJuridico = Constantes.getRoleAreaName("role.tecnico.Juridico");
				List<Usuario> usuarioJuridico = usuarioFacade.buscarUsuariosPorRolYAreaEspecifica(rolJuridico, areaJuridico);
				
				if(usuarioJuridico==null || usuarioJuridico.isEmpty()){
					LOG.error("No se encontro usuario " + rolJuridico + " en " + areaJuridico);
					//JsfUtil.addMessageError("No se encontro usuario " + rolJuridico + " en " + areaJuridico);
					JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION_MESA_AYUDA);
					JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
					return;
				}
				
				Usuario analistaJuridico = usuarioJuridico.get(0); 
				if (juridicoBpm == null || !juridicoBpm.equals(analistaJuridico.getNombre())) {
					if(viabilidadProyecto.getEsAdministracionMae())
						parametros.put("abogadoAreaProtegida", analistaJuridico.getNombre());
					else
						parametros.put("abogadoPatrimonio", analistaJuridico.getNombre());
				}
			}
			
			procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);
			
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);

			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
			JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al enviar la tarea.");
		}
	}
	
	public void validateDatosRevision(FacesContext context, UIComponent validate, Object value) {
		List<FacesMessage> errorMessages = new ArrayList<>();
		if (!documentoDescargado)
			errorMessages.add(new FacesMessage(
							FacesMessage.SEVERITY_ERROR,
							"Debe descargar el documento de sustento.", null));
		if(viabilidadProyecto.getExisteConflictoLegal() != null){
			if (viabilidadProyecto.getExisteConflictoLegal() && (documentoJuridico == null || (documentoJuridico.getId() == null && documentoJuridico.getContenidoDocumento() == null)))
				errorMessages.add(new FacesMessage(
								FacesMessage.SEVERITY_ERROR,
								"El campo 'Adjuntar documentos de análisis para jurídico' es requerido.", null));
		}
		
		if (!errorMessages.isEmpty())
			throw new ValidatorException(errorMessages);
	}
	
	public void uploadFileJuridico(FileUploadEvent event) {
		byte[] contenidoDocumento = event.getFile().getContents();
		documentoJuridico = new DocumentoViabilidad();
		documentoJuridico.setId(null);
		documentoJuridico.setContenidoDocumento(contenidoDocumento);
		documentoJuridico.setNombre(event.getFile().getFileName());
		documentoJuridico.setMime("application/pdf");
		documentoJuridico.setIdTipoDocumento(TipoDocumentoSistema.RCOA_VIABILIDAD_ANALISIS_JURIDICO.getIdTipoDocumento());
	}
	
	public StreamedContent descargarFormulario() throws IOException {
		DefaultStreamedContent content = null;
		try {
			byte[] documentoContent = null;
			
			DocumentoViabilidad documento = null;
			
			List<DocumentoViabilidad> documentosFormulario = documentosFacade
					.getDocumentoPorTipoViabilidadTramite(
							proyectoTipoViabilidadCoa.getId(),
							TipoDocumentoSistema.RCOA_VIABILIDAD_SNAP_FORMULARIO_OPERADOR
									.getIdTipoDocumento());
			if (documentosFormulario.size() > 0) {
				documento = documentosFormulario.get(0);
			}
			
			if (documento != null && documento.getIdAlfresco() != null) {
				documentoContent = documentosFacade.descargar(documento.getIdAlfresco(), documento.getFechaCreacion());
			} 
			
			if (documento != null && documento.getNombre() != null
					&& documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(documento.getNombre());
			} else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
			
			if(esRevision)
				documentoDescargado = true;

		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
	
	public String getNotaSustento(){
		if(notaSustento==null){
			try {						
				notaSustento = mensajeNotificacionFacade.recuperarValorMensajeNotificacionSD("mensajeViabilidadSustentoSnap");			
			} catch (Exception e) {
				LOG.error("No se recupero mensaje sustento SNAP. "+e.getCause()+" "+e.getMessage());
			}
		}
		return notaSustento;
	}
}
