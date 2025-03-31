package ec.gob.ambiente.rcoa.estudioImpactoAmbiental.controller;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.kie.api.task.model.TaskSummary;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.CatalogoGeneralEsIAFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.DocumentosImpactoEstudioFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.ObservacionesEsIAFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.ProrrogaModificacionEstudioFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.CatalogoGeneralEsIA;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.DocumentoEstudioImpacto;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.InformeTecnicoEsIA;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.ObservacionesEsIA;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.OficioPronunciamientoEsIA;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.ProrrogaModificacionEstudio;
import ec.gob.ambiente.rcoa.facade.ProcesosArchivadosFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaUbicacionFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCuaCiuuFacade;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoaUbicacion;
import ec.gob.ambiente.rcoa.util.BuscarUsuarioBean;
import ec.gob.ambiente.rcoa.util.NotificacionInternaUtil;
import ec.gob.ambiente.rcoa.utils.DiasHabilesUtil;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.administracion.facade.FeriadosFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.BandejaFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.MensajeNotificacion;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.DigitalSign;
import ec.gob.ambiente.suia.utils.Email;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;

@ManagedBean
@ViewScoped
public class RevisarInformeConsolidadoOficioController implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ManagedProperty(value = "#{bandejaTareasBean}")
    @Getter
    @Setter
    private BandejaTareasBean bandejaTareasBean;
	
	@ManagedProperty(value = "#{pronunciamientoBean}")
	@Getter
	@Setter
	private PronunciamientoBean pronunciamientoBean;
	
	@ManagedProperty(value = "#{informeTecnicoConsolidadoEIABean}")
	@Getter
	@Setter
	private InformeTecnicoConsolidadoEIABean informeTecnicoConsolidadoEIABean;
	
	@Getter
    @Setter
    @ManagedProperty(value = "#{buscarUsuarioBean}")
    private BuscarUsuarioBean buscarUsuarioBean;
	
	@EJB
	private ProcesoFacade procesoFacade;
	
	@EJB
	private AreaFacade areaFacade;
	
	@EJB
	private UsuarioFacade usuarioFacade;
	
	@EJB
	private DocumentosFacade documentosGeneralFacade;
	
	@EJB
	private DocumentosImpactoEstudioFacade documentosFacade;

	@EJB
    private ObservacionesEsIAFacade observacionesEsIAFacade;
	
	@EJB
	private BandejaFacade bandejaFacade;
	
	@EJB
	private ProrrogaModificacionEstudioFacade prorrogaModificacionEstudioFacade;
	
	@EJB
	private CatalogoGeneralEsIAFacade catalogoGeneralEsIAFacade;
	
	@EJB
	private FeriadosFacade feriadosFacade;
	
	@EJB
	private OrganizacionFacade organizacionFacade;
	
	@EJB
	private MensajeNotificacionFacade mensajeNotificacionFacade;
	
	@EJB
	private ProyectoLicenciaCoaUbicacionFacade proyectoLicenciaCoaUbicacionFacade;
	
	@EJB
	private ProyectoLicenciaCuaCiuuFacade proyectoLicenciaCuaCiuuFacade;
	
	@EJB
	private ProcesosArchivadosFacade procesosArchivadosFacade;
	@EJB
	private DiasHabilesUtil diasHabilesUtil;
	
	@Getter
	@Setter
	private Boolean esCoordinador, requiereCorrecciones, visualizarMemoOficio;
	
	@Getter
	@Setter
	private boolean token, descargado, subido, documentosAceptados, firmaSoloToken;
	
	@Getter
	@Setter
	private Integer idInformePrincipal;
	
	@Getter
	@Setter
	private DocumentoEstudioImpacto documentoManual;
	
	@Getter
	@Setter
	private String urlAlfresco;
	
	@Getter
	@Setter
	private boolean proyectoPlantaCentralAprobacion;
	
	@PostConstruct
	private void iniciar() {
		JsfUtil.getLoggedUser().getNombre();
		try {
			firmaSoloToken = Constantes.getPropertyAsBoolean("ambiente.produccion");
			verificaToken();
			String tarea = bandejaFacade.getNombreTarea(JsfUtil.getCurrentTask().getTaskId());
			
			if(tarea.contains("Coord"))
				esCoordinador = true;
			else
				esCoordinador = false;
			visualizarMemoOficio = true;
			//siempre es oficio porque es la respuesta al operador
			pronunciamientoBean.setTipoOficio(OficioPronunciamientoEsIA.oficio);

			informeTecnicoConsolidadoEIABean.recuperarInformeTecnico();

			if(informeTecnicoConsolidadoEIABean.getInformeTecnico() != null) {
				pronunciamientoBean.setIdInforme(informeTecnicoConsolidadoEIABean.getInformeTecnico().getId());
				
				idInformePrincipal = informeTecnicoConsolidadoEIABean.getInformeTecnico().getIdInformePrincipal();
				if(idInformePrincipal == null)
					idInformePrincipal = informeTecnicoConsolidadoEIABean.getInformeTecnico().getId();
			}

			if(visualizarMemoOficio) {
				pronunciamientoBean.cargarDatos();
				pronunciamientoBean.generarOficio(true);
			}
			
			if(tarea.contains("Direc") && informeTecnicoConsolidadoEIABean.getInformeTecnico().getTipoPronunciamiento().equals(1)){						
				proyectoPlantaCentralAprobacion = true;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al recuperar la información.");
		}
	}
	
	public void validarTareaBpm() {

	}
	
	public boolean verificaToken() {
		if(firmaSoloToken) {
			token = true;
			return token;
		}
		
		token = false;
		if (JsfUtil.getLoggedUser().getToken() != null
				&& JsfUtil.getLoggedUser().getToken())
			token = true;
		return token;
	}
	
	public void guardarToken() {
		Usuario usuario = JsfUtil.getLoggedUser();
		usuario.setToken(token);
		try {
			usuarioFacade.guardar(usuario);
			verificaToken();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void enviar() {
		try {			
			Map<String, Object> parametros = new HashMap<>();
			parametros.put("observacionesInformeConsolidado", true);
			parametros.put("tipoPronunciamiento", 1);  //Tipo pronunciamiento 1. Aprobacion - 2. Observacion - 3. Archivado tercera observacion
			parametros.put("requiereRevisionDirector", false); //true si es planta central o galapagos
			procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
				
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
			JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
		}
	}
	
	public void validarExisteObservacionesInformeOficio() {
		try {
			requiereCorrecciones = false;
			List<ObservacionesEsIA> observaciones= observacionesEsIAFacade.listarPorIdClaseNombreClaseNoCorregidas(
					idInformePrincipal, "InfomeOficioConsolidado");
			
			if(observaciones.size() > 0) {
				requiereCorrecciones = true;
			}	
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al recuperar las observaciones.");
		}
	}
	
	public void finalizar() {
		try {
			String tipoDocumento = "informe técnico";
			if(!esCoordinador)
				tipoDocumento = (pronunciamientoBean.getTipoOficio().equals(1)) ? "memorando" : "oficio de pronunciamiento";
			
			if (token) {
				String idAlfresco = informeTecnicoConsolidadoEIABean.getDocumentoInforme().getAlfrescoId();
				if(!esCoordinador) {
					idAlfresco = pronunciamientoBean.getDocumentoOficio().getAlfrescoId();
				}
				
				if (!documentosFacade.verificarFirmaVersion(idAlfresco)) {
					JsfUtil.addMessageError("El " + tipoDocumento + " no está firmado electrónicamente.");
					return;
				}
			} else {
				if(subido) {
					if(esCoordinador)
						informeTecnicoConsolidadoEIABean.guardarDocumentoFirmaManual(documentoManual);
					else
						pronunciamientoBean.guardarDocumentoFirmaManual(documentoManual);
				} else {
					JsfUtil.addMessageError("Debe adjuntar el " + tipoDocumento + " firmado.");
					return;
				}				
			}
			
			Map<String, Object> parametros = new HashMap<>();
			parametros.put("observacionesInformeConsolidado", requiereCorrecciones);
			parametros.put("tipoPronunciamiento", informeTecnicoConsolidadoEIABean.getInformeTecnico().getTipoPronunciamiento());  //Tipo pronunciamiento 1. Aprobacion - 2. Observacion - 3. Archivado tercera observacion - 4 observado sustancial 
			parametros.put("requiereRevisionDirector", informeTecnicoConsolidadoEIABean.isEsPlantaCentral()); //true si es planta central o galapagos
						
			Area area=new Area();
			
			area = informeTecnicoConsolidadoEIABean.getProyecto().getAreaResponsable();

			if(esCoordinador){
				
				//si es planta central se va a revision 
				if(informeTecnicoConsolidadoEIABean.getProyecto().getAreaResponsable().getTipoArea().getSiglas().equalsIgnoreCase("PC")){
					parametros.put("requiereRevisionDirector", true);
					String roleKey="role.esia.pc.autoridad";
					Usuario usuarioAutoridad = new Usuario();
					usuarioAutoridad = buscarUsuarioBean.buscarUsuario(roleKey, area);
					if(usuarioAutoridad==null )			
					{
						JsfUtil.addMessageError("Ha ocurrido un error. Por favor comuníquese con Mesa de Ayuda");
						return;
					}
					parametros.put("usuarioDirector", usuarioAutoridad.getNombre());
				}else if(informeTecnicoConsolidadoEIABean.getProyecto().getAreaResponsable().getTipoArea().getSiglas().equalsIgnoreCase("DP")){
					//galápagos
					String roleKey="role.esia.cz.autoridad";
					Usuario usuarioAutoridad = new Usuario();
					List<Usuario> usuarios = usuarioFacade.buscarUsuariosPorRolYArea(Constantes.getRoleAreaName(roleKey), area);
					if(usuarios != null && !usuarios.isEmpty()){
						usuarioAutoridad = usuarios.get(0);	
					}else{
						JsfUtil.addMessageError("Ha ocurrido un error. Por favor comuníquese con Mesa de Ayuda");
						return;
					}					
					
					parametros.put("requiereRevisionDirector", true);
					parametros.put("usuarioDirector", usuarioAutoridad.getNombre());
				}
				else{				
					//usuarios de gads y zonales

					String roleKey="role.esia.cz.autoridad";
					Usuario usuarioAutoridad = new Usuario();
					
					List<Usuario> usuarios = new ArrayList<Usuario>();					
					if(informeTecnicoConsolidadoEIABean.getProyecto().getAreaResponsable().getTipoArea().getSiglas().equals("OT")){
						usuarios = usuarioFacade.buscarUsuariosPorRolYArea(Constantes.getRoleAreaName(roleKey), area.getArea());				
					}else{
						usuarios = usuarioFacade.buscarUsuariosPorRolYArea(Constantes.getRoleAreaName(roleKey), area);			
					}	
					
					if(usuarios != null && !usuarios.isEmpty()){
						usuarioAutoridad = usuarios.get(0);
					}else{
						JsfUtil.addMessageError("Ha ocurrido un error. Por favor comuníquese con Mesa de Ayuda");
						return;
					}
					
					parametros.put("requiereRevisionDirector", false);
					parametros.put("usuarioAutoridad", usuarioAutoridad.getNombre());					
				}						
				
			}else{			
				
				String tarea = bandejaFacade.getNombreTarea(JsfUtil.getCurrentTask().getTaskId());
				Boolean esDirector = false;
				Boolean esAutoridad = false;
				
				if(tarea.contains("Direc"))
					esDirector = true;
				
				if(tarea.contains("Autoridad"))
					esAutoridad = true;
				
				if(esDirector){
					
					if(informeTecnicoConsolidadoEIABean.getProyecto().getAreaResponsable().getTipoArea().getSiglas().equalsIgnoreCase("PC")){						
						
						String roleKey = "role.area.subsecretario.calidad.ambiental";
						
						Usuario usuarioAutoridad = new Usuario();
						List<Usuario> usuarios = usuarioFacade.buscarUsuarioPorRol(Constantes.getRoleAreaName(roleKey));
						if(usuarios != null && !usuarios.isEmpty()){
							usuarioAutoridad = usuarios.get(0);	
						}else{
							JsfUtil.addMessageError("Ha ocurrido un error. Por favor comuníquese con Mesa de Ayuda");
							return;
						}
												
						parametros.put("usuarioAutoridad", usuarioAutoridad.getNombre());
					}else{
						
						String roleKey = "role.esia.ga.autoridad";
						
						Usuario usuarioAutoridad = new Usuario();
						List<Usuario> usuarios = usuarioFacade.buscarUsuariosPorRolYArea(Constantes.getRoleAreaName(roleKey), area);
						if(usuarios != null && !usuarios.isEmpty()){
							usuarioAutoridad = usuarios.get(0);	
						}else{
							JsfUtil.addMessageError("Ha ocurrido un error. Por favor comuníquese con Mesa de Ayuda");
							return;
						}
						parametros.put("usuarioAutoridad", usuarioAutoridad.getNombre());	
					}
					
					if(informeTecnicoConsolidadoEIABean.getInformeTecnico().getTipoPronunciamiento() != 1 && !requiereCorrecciones){
						guardarInfoProrroga();
					}					
				}
				
				if(esAutoridad && informeTecnicoConsolidadoEIABean.getInformeTecnico().getTipoPronunciamiento() != 1 && !requiereCorrecciones){
					guardarInfoProrroga();
				}				
										
			}

			procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);
			
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
			
			if(!esCoordinador) {
				//si firma la autoridad establezco los ids de procesos para visualización en resumen de tareas
				informeTecnicoConsolidadoEIABean.getDocumentoInforme().setIdProceso(bandejaTareasBean.getProcessId());
				documentosFacade.guardar(informeTecnicoConsolidadoEIABean.getDocumentoInforme());
				
				pronunciamientoBean.getDocumentoOficio().setIdProceso(bandejaTareasBean.getProcessId());
				documentosFacade.guardar(pronunciamientoBean.getDocumentoOficio());
				
				if(informeTecnicoConsolidadoEIABean.getProyecto().getEstadoActualizacionCertInterseccion().equals(1) &&
						informeTecnicoConsolidadoEIABean.getInformeTecnico().getTipoPronunciamiento().equals(2)) {
					informeTecnicoConsolidadoEIABean.guadarProyecto();
				}
				
				try
				{
					enviarNotificacionPronunciamiento();
				}catch (Exception e) {
					e.printStackTrace();
				}
				
				
				//para finalizar la tarea de encuesta y descarga de pronunciamiento
				List<TaskSummary> listaTareas = procesoFacade.getTaskReservedInProgress(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
				if(listaTareas != null && listaTareas.size() > 0) {				
					for (TaskSummary tarea : listaTareas) {
						String nombretarea = bandejaFacade.getNombreTarea(tarea.getId());
						if(nombretarea.equals("descargarPronunciamiento")) {
							Usuario usuarioTarea = usuarioFacade.buscarUsuario(tarea.getActualOwner().getId());

							String fechaAvance = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss:SS").format(new Date());

							Map<String, Object> param = new HashMap<>();
					
							param.put("finalizadoAutomaticoSisDescargaEsIA", fechaAvance);
							
							procesoFacade.modificarVariablesProceso(usuarioTarea, bandejaTareasBean.getTarea().getProcessInstanceId(), param);

							procesoFacade.aprobarTarea(usuarioTarea, tarea.getId(), bandejaTareasBean.getTarea().getProcessInstanceId(), null);
							
							//información de fin de proceso
							Integer tipoResultado = informeTecnicoConsolidadoEIABean.getInformeTecnico().getTipoPronunciamiento();
							CatalogoGeneralEsIA resultado = catalogoGeneralEsIAFacade.buscarPorCodigo("pronunciamiento_estudio_" + tipoResultado);
							
							if(resultado != null)
								informeTecnicoConsolidadoEIABean.getEsiaProyecto().setResultadoFinalEia(resultado.getId());
							informeTecnicoConsolidadoEIABean.getEsiaProyecto().setFechaFinEstudio(new Date());
							informeTecnicoConsolidadoEIABean.guardarEsia();
							
							Integer tipoPronunciamiento = tipoResultado;
							if(tipoPronunciamiento.equals(1)) {
								SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
								Date fechaSuspensionBypass = sdf.parse(Constantes.getFechaSuspensionPpcBypass());
								Date fechaProyecto = sdf.parse(informeTecnicoConsolidadoEIABean.getProyecto().getFechaGeneracionCua().toString());
								
								String tramite = informeTecnicoConsolidadoEIABean.getProyecto().getCodigoUnicoAmbiental();
								parametros = new ConcurrentHashMap<String, Object>();
								parametros.put("operador", usuarioTarea.getNombre());
								parametros.put("tramite", tramite);					
								parametros.put("idProyecto", informeTecnicoConsolidadoEIABean.getProyecto().getId());
								parametros.put("numeroFacilitadores", informeTecnicoConsolidadoEIABean.getEsiaProyecto().getNumeroFacilitadores());
								parametros.put("facilitadorAdicional", false);
								
								Boolean existeNormativa = Constantes.getPropertyAsBoolean("rcoa.existe.normativa.ppc");
								String tecnicoResponsable = (String) informeTecnicoConsolidadoEIABean.getVariables().get("tecnicoResponsable");
								if(existeNormativa) {
									parametros.put("tecnicoRealizoEIA", tecnicoResponsable);
									procesoFacade.iniciarProceso(usuarioTarea, Constantes.RCOA_PROCESO_PARTICIPACION_CIUDADANA, tramite, parametros);
								} else {
									parametros.put("tecnicoResponsableEIA", tecnicoResponsable);
									//suspensión de PPC por solicitud Ticket#10351907
									if(fechaProyecto.compareTo(fechaSuspensionBypass) >= 0) {
										//envio notificación suspensión proyectos registrados a partir de 2021-10-12
										parametros.put("tecnicoResponsable", tecnicoResponsable);
										procesoFacade.iniciarProceso(usuarioTarea, Constantes.RCOA_PROCESO_BYPASS_PARTICIPACION_CIUDADANA, tramite, parametros);
										try
										{
											enviarNotificacionSuspension();
										}catch (Exception e) {
											e.printStackTrace();
										}									
									} else {
										procesoFacade.iniciarProceso(usuarioTarea, Constantes.RCOA_PROCESO_PARTICIPACION_CIUDADANA_BYPASS, tramite, parametros);
									}
								}
							} else if(tipoResultado.equals(InformeTecnicoEsIA.terceraRevision)) {
								procesosArchivadosFacade.guardarArchivado(informeTecnicoConsolidadoEIABean.getProyecto(), 3);
							} else if(tipoResultado.equals(InformeTecnicoEsIA.observadoSustancial)) {
								procesosArchivadosFacade.guardarArchivado(informeTecnicoConsolidadoEIABean.getProyecto(), 4);
							}
							
							break;
						} 
					}
				}				
			}
			
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
			JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
		}
	}

	public void guardarDocumentos() {
		try {
			
			if(esCoordinador) {
				if(informeTecnicoConsolidadoEIABean.getDocumentoInforme() != null) {
					if (documentosFacade.verificarFirmaVersion(informeTecnicoConsolidadoEIABean.getDocumentoInforme().getAlfrescoId())) {

						token = true;
					}
					
					String documentOffice = documentosFacade.direccionDescarga(informeTecnicoConsolidadoEIABean.getDocumentoInforme());
					urlAlfresco = DigitalSign.sign(documentOffice, JsfUtil.getLoggedUser().getNombre());
				}
			} else {
				//es autoridad
				pronunciamientoBean.guardarDocumentosAlfresco();
				
				if (pronunciamientoBean.getDocumentoOficio() != null) {
					String documentOffice = documentosFacade.direccionDescarga(pronunciamientoBean.getDocumentoOficio());
					urlAlfresco = DigitalSign.sign(documentOffice, JsfUtil.getLoggedUser().getNombre());
				}
			}
			
		} catch (Exception exception) {
			exception.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
		
		RequestContext.getCurrentInstance().update("formDialogFirma:");
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('signDialog').show();");
	}
	
	public void guardarInformeOficio() {
		try {
			if(visualizarMemoOficio) {
				pronunciamientoBean.guardarDatos();
				pronunciamientoBean.actualizarCodigoOficio();
			}
			documentosAceptados = true;
			JsfUtil.addMessageInfo(JsfUtil.REGISTRO_GUARDADO);
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
		}
	}
	
	public StreamedContent descargar(Integer tipoDocumento) {
		DefaultStreamedContent content = null;
		try {
			byte[] documentoContent = null;
			String nombre = "";
			if(tipoDocumento.equals(1)) {
				documentoContent = informeTecnicoConsolidadoEIABean.getArchivoReporte();
				nombre = informeTecnicoConsolidadoEIABean.getNombreReporte();
			} else {
				documentoContent = pronunciamientoBean.getArchivoReporte();
				nombre = pronunciamientoBean.getNombreReporte();
			}
			
			if (documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(nombre);
			} else
				JsfUtil.addMessageError("Error al generar el archivo");

			descargado = true;
		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
	
	public void uploadListenerDocumentos(FileUploadEvent event) {
		if (descargado== true) {
			byte[] contenidoDocumento = event.getFile().getContents();

			documentoManual=new DocumentoEstudioImpacto();
			documentoManual.setContenidoDocumento(contenidoDocumento);
			documentoManual.setNombre(event.getFile().getFileName());
			documentoManual.setExtesion(".pdf");		
			documentoManual.setMime("application/pdf");

			subido = true;
			JsfUtil.addMessageInfo("Documento subido exitosamente");
		} else{
			JsfUtil.addMessageError("No ha descargado el documento para la firma");
		}
	}
	
	public void finalizarAprobacionDirector(){
		try {
			
			Map<String, Object> parametros = new HashMap<>();
			
			String tarea = bandejaFacade.getNombreTarea(JsfUtil.getCurrentTask().getTaskId());
			Boolean esDirector = false;
			Area area = informeTecnicoConsolidadoEIABean.getProyecto().getAreaResponsable();
			
			if(tarea.contains("Direc"))
				esDirector = true;
			
			if(esDirector){
				
				if(informeTecnicoConsolidadoEIABean.getProyecto().getAreaResponsable().getTipoArea().getSiglas().equalsIgnoreCase("PC")){						
					
					String roleKey = "role.area.subsecretario.calidad.ambiental";
					
					Usuario usuarioAutoridad = new Usuario();
					List<Usuario> usuarios = usuarioFacade.buscarUsuarioPorRol(Constantes.getRoleAreaName(roleKey));
					if(usuarios != null && !usuarios.isEmpty()){
						usuarioAutoridad = usuarios.get(0);	
					}else{
						JsfUtil.addMessageError("Ha ocurrido un error. Por favor comuníquese con Mesa de Ayuda");
						return;
					}
											
					parametros.put("usuarioAutoridad", usuarioAutoridad.getNombre());
				}else{
					
					String roleKey = "role.esia.ga.autoridad";
					
					Usuario usuarioAutoridad = new Usuario();
					List<Usuario> usuarios = usuarioFacade.buscarUsuariosPorRolYArea(Constantes.getRoleAreaName(roleKey), area);
					if(usuarios != null && !usuarios.isEmpty()){
						usuarioAutoridad = usuarios.get(0);	
					}else{
						JsfUtil.addMessageError("Ha ocurrido un error. Por favor comuníquese con Mesa de Ayuda");
						return;
					}
					parametros.put("usuarioAutoridad", usuarioAutoridad.getNombre());	
				}
				
				procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);
				
				procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
				
				JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
				JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void enviarNotificacionSuspension() throws ServiceException {
		String nombreOperador = "";
		Usuario usuarioOperador = informeTecnicoConsolidadoEIABean.getProyecto().getUsuario();
		
		if (usuarioOperador.getPersona().getOrganizaciones().size() == 0) {
			nombreOperador = usuarioOperador.getPersona().getNombre();
		} else {
			Organizacion organizacion = organizacionFacade.buscarPorRuc(usuarioOperador.getNombre());
			nombreOperador = organizacion.getNombre();
			usuarioOperador.getPersona().setContactos(organizacion.getContactos());
		}
		
		Object[] parametrosCorreoNuevo = new Object[] {nombreOperador};
		
		String notificacion = mensajeNotificacionFacade.recuperarValorMensajeNotificacion(
				"bodyNotificacionSupensionPpcBypassEmail", parametrosCorreoNuevo);
		
		Email.sendEmail(usuarioOperador, "Regularización Ambiental Nacional", notificacion, pronunciamientoBean.getProyecto().getCodigoUnicoAmbiental(), JsfUtil.getLoggedUser());
	}
	
	private void enviarNotificacionPronunciamiento() throws Exception {
		String nombreOperador = "";
		Usuario usuarioOperador = informeTecnicoConsolidadoEIABean.getProyecto().getUsuario();
		
		if (usuarioOperador.getPersona().getOrganizaciones().size() == 0) {
			nombreOperador = usuarioOperador.getPersona().getNombre();
		} else {
			Organizacion organizacion = organizacionFacade.buscarPorRuc(usuarioOperador.getNombre());
			if (organizacion != null)
			{
				nombreOperador = organizacion.getNombre();
				usuarioOperador.getPersona().setContactos(organizacion.getContactos());	
			}
		}
		
		Integer tipoResultado = informeTecnicoConsolidadoEIABean.getInformeTecnico().getTipoPronunciamiento();
		String nombreDocumento = (tipoResultado.equals(InformeTecnicoEsIA.aprobado)) ? "Pronunciamiento Aprobación.pdf" : "Pronunciamiento Observación.pdf";
		
		List<String> listaArchivos = new ArrayList<>();
		FileOutputStream file;
		File fileArchivo = new File(System.getProperty("java.io.tmpdir")+"/"+nombreDocumento);
		File fileArchivoInforme = new File(System.getProperty("java.io.tmpdir")+"/"+"informe_tecnico.pdf");
		
		try {
			file = new FileOutputStream(fileArchivo);
			file.write(documentosFacade.descargar(pronunciamientoBean.getDocumentoOficio().getAlfrescoId()));
			file.close();
			listaArchivos.add(nombreDocumento);
			//aumento en informe tecnico consolidado para enviarlo como adjunto por correo al operador
			file = new FileOutputStream(fileArchivoInforme);
			file.write(documentosFacade.descargar(informeTecnicoConsolidadoEIABean.getDocumentoInforme().getAlfrescoId()));
			file.close();
			listaArchivos.add("informe_tecnico.pdf");
		} catch (Exception e) {
			e.printStackTrace();
		}		
		
		Calendar fecha = Calendar.getInstance();
		int anio = fecha.get(Calendar.YEAR);
		
		Date fechaOficio = pronunciamientoBean.getOficioPronunciamiento().getFechaOficio();
		String fechaOficioString = JsfUtil.devuelveFechaEnLetras(fechaOficio);
		
		ProyectoLicenciaCoaUbicacion proyectoLicenciaCoaUbicacion = proyectoLicenciaCoaUbicacionFacade.ubicacionPrincipal(informeTecnicoConsolidadoEIABean.getProyecto());
		UbicacionesGeografica ubicacion = proyectoLicenciaCoaUbicacion.getUbicacionesGeografica();
		
		String provincia = ubicacion.getUbicacionesGeografica().getUbicacionesGeografica().getNombre();
		String canton = ubicacion.getUbicacionesGeografica().getNombre();
		String parroquia = ubicacion.getNombre();
		
		Object[] parametrosCorreoNuevo = new Object[] {nombreOperador, pronunciamientoBean.getOficioPronunciamiento().getCodigoOficio(),
				fechaOficioString, informeTecnicoConsolidadoEIABean.getProyecto().getNombreProyecto(),
				informeTecnicoConsolidadoEIABean.getProyecto().getCodigoUnicoAmbiental(), provincia, canton, parroquia,
				anio};
			
		String plantillaMsj = (tipoResultado.equals(InformeTecnicoEsIA.terceraRevision)) ? "bodyNotificacionEmisionPronunciamientoArchivoEsIA" : "bodyNotificacionEmisionPronunciamientoEsIA";
		
		MensajeNotificacion mensajeNotificacion =  mensajeNotificacionFacade.buscarMensajesNotificacion(plantillaMsj);
			
		String notificacion = String.format(mensajeNotificacion.getValor(), parametrosCorreoNuevo);
			
		Email.sendEmailAdjuntos(usuarioOperador, mensajeNotificacion.getAsunto(), notificacion, listaArchivos, pronunciamientoBean.getProyecto().getCodigoUnicoAmbiental(), JsfUtil.getLoggedUser());
			
		NotificacionInternaUtil.remitirNotificacionesEstudio(pronunciamientoBean.getProyecto(), mensajeNotificacion.getAsunto(), notificacion, listaArchivos);				
		//borrar archivos
		if (fileArchivo.exists()) {
			fileArchivo.delete();
		}
		
	}

	private void guardarInfoProrroga() throws Exception {
		ProrrogaModificacionEstudio prorroga = prorrogaModificacionEstudioFacade.getPorEstudio(informeTecnicoConsolidadoEIABean.getEsiaProyecto().getId());
		
		int dias = 0;
		if(prorroga == null){
			//primera vez 15 días
			CatalogoGeneralEsIA valor = catalogoGeneralEsIAFacade.buscarPorCodigo("dias_primera_observacion");					
			dias = Integer.valueOf(valor.getDescripcion());
			
			prorroga = new ProrrogaModificacionEstudio();
		}else{
			if(!prorroga.getNumeroRevision().equals(informeTecnicoConsolidadoEIABean.getNumeroObservaciones())) {
				prorroga = new ProrrogaModificacionEstudio();
				
				//desde la segunda 10 dias
				CatalogoGeneralEsIA valor = catalogoGeneralEsIAFacade.buscarPorCodigo("dias_seg_ter_observacion");					
				dias = Integer.valueOf(valor.getDescripcion());
			} else {
				dias = prorroga.getNumeroDiasPorRevision();
			}
		}
		
		Date fechaInicio = diasHabilesUtil.recuperarFechaHabil(new Date(), 1, true);//se agrega 1 dia porque el plazo inicia desde mañana o el siguiente dia habil
		Date fechaFin = diasHabilesUtil.recuperarFechaHabil(new Date(), dias, false);
				
		prorroga.setNumeroDiasPorRevision(dias);
		prorroga.setFechaInicioModificacion(fechaInicio);
		prorroga.setFechaFinModificacion(fechaFin);
		prorroga.setNumeroRevision(informeTecnicoConsolidadoEIABean.getNumeroObservaciones());
		prorroga.setInformacionProyectoEia(informeTecnicoConsolidadoEIABean.getEsiaProyecto());
		
		prorrogaModificacionEstudioFacade.guardar(prorroga);
	}
}
