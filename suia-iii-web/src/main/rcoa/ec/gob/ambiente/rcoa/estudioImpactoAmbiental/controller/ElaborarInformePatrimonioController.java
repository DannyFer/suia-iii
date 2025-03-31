package ec.gob.ambiente.rcoa.estudioImpactoAmbiental.controller;
import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.DocumentosImpactoEstudioFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.ObservacionesEsIAFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.PlanManejoAmbientalEsIAFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.DocumentoEstudioImpacto;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.InformeTecnicoEsIA;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.ObservacionesEsIA;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.OficioPronunciamientoEsIA;
import ec.gob.ambiente.rcoa.util.BuscarUsuarioBean;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.DigitalSign;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class ElaborarInformePatrimonioController implements Serializable {
	
	private final Logger LOG = Logger.getLogger(ElaborarInformePatrimonioController.class);

	private static final long serialVersionUID = 1L;
	
	@ManagedProperty(value = "#{bandejaTareasBean}")
    @Getter
    @Setter
    private BandejaTareasBean bandejaTareasBean;
	
	@ManagedProperty(value = "#{memoOficioPatrimonioBean}")
	@Getter
	@Setter
	private MemoOficioPatrimonioBean memoOficioPatrimonioBean;
	
	@ManagedProperty(value = "#{informeTecnicoEsIABean}")
	@Getter
	@Setter
	private InformeTecnicoEsIABean informeTecnicoEsIABean;
	
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
	private PlanManejoAmbientalEsIAFacade planManejoAmbientalEsIAFacade;
	
	@Getter
    @Setter
    @ManagedProperty(value = "#{buscarUsuarioBean}")
    private BuscarUsuarioBean buscarUsuarioBean;
	
	@Getter
	@Setter
	@ManagedProperty(value = "#{proyectosBean}")
	private ProyectosBean proyectosBean;
	
	@Getter
	@Setter
	private Boolean mostrarInforme, esReporteAprobacion, informeGuardado, oficioGuardado;
	
	@Getter
	@Setter
	private DocumentoEstudioImpacto documentoManual;
	
	@Getter
	@Setter
	private String urlAlfresco, resumenObservaciones;
	
	@Getter
	@Setter
	private String urlReporte, nombreReporte, tipoOficio;
	
	@Getter
	@Setter
	private byte[] archivoReporte;
	
	@Getter
	@Setter
	private boolean token, descargado, subido, habilitarFirma, firmaSoloToken;

	@Getter
	@Setter
	private Boolean existeObservacionesEstudio, generarMemoOficio;
	
	@PostConstruct
	private void iniciar() {
		JsfUtil.getLoggedUser().getNombre();
		
		firmaSoloToken = Constantes.getPropertyAsBoolean("ambiente.produccion");
		
		verificaToken();
		
		informeGuardado = false;
		oficioGuardado = false;
		habilitarFirma = false;
		
		informeTecnicoEsIABean.generarInforme(true);
		
		generarMemoOficio = false;
		if(informeTecnicoEsIABean.getProyecto().getAreaResponsable().getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_PC)){
			//si es planta central se genera un memo 
			memoOficioPatrimonioBean.setTipoOficio(OficioPronunciamientoEsIA.memorando);
			generarMemoOficio = true;
		} else if (!informeTecnicoEsIABean.getProyecto().getAreaResponsable().getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_OT)
				&& !informeTecnicoEsIABean.getProyecto().getAreaResponsable().getId().equals(Constantes.ID_PARQUE_NACIONAL_GALAPAGOS)) {
			//si es GAD se genera un oficio de respuesta por inventario
			memoOficioPatrimonioBean.setTipoOficio(OficioPronunciamientoEsIA.oficio);
			generarMemoOficio = true;
		}
		
		if(generarMemoOficio) {
			memoOficioPatrimonioBean.setIdInforme(informeTecnicoEsIABean.getInformeTecnico().getId());
			memoOficioPatrimonioBean.setInformeTecnico(informeTecnicoEsIABean.getInformeTecnico());
			memoOficioPatrimonioBean.cargarDatos();
			memoOficioPatrimonioBean.generarOficio(true);
		}	
		
	}
	
	public void validarTareaBpm() {
		JsfUtil.validarPaginasUrlTareasBpm(JsfUtil.getCurrentTask(),
				"/pages/rcoa/estudioImpactoAmbiental/revisarEsIAElaborarInformePatrimonio.jsf");
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

	public void validarExisteObservacionesEstudio() {
		try {
			existeObservacionesEstudio = false;
			String nombreClaseObservaciones = "InformacionProyectoEia_"+JsfUtil.getLoggedUser().getNombre();
			List<ObservacionesEsIA> observaciones= observacionesEsIAFacade.listarPorIdClaseNombreClaseNoCorregidas(
					informeTecnicoEsIABean.getEsiaProyecto().getId(), nombreClaseObservaciones);
			
			if(observaciones.size() > 0) {
				existeObservacionesEstudio = true;
				resumenObservaciones = "<ul>";
				for (ObservacionesEsIA observacionesEsIA : observaciones) {
					resumenObservaciones += "<li>";
					resumenObservaciones += observacionesEsIA.getCampo() + ": " + observacionesEsIA.getDescripcion();
					resumenObservaciones += "</li>";
				}
				resumenObservaciones += "</ul>";
				
				informeTecnicoEsIABean.getInformeTecnico().setObservaciones(resumenObservaciones);
			}
			
			if(existeObservacionesEstudio)
				informeTecnicoEsIABean.getInformeTecnico().setTipoPronunciamiento(2);
			else
				informeTecnicoEsIABean.getInformeTecnico().setTipoPronunciamiento(1);
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al recuperar las observaciones.");
		}
	}
	
	public String abrirEstudio(){
		JsfUtil.cargarObjetoSession(InformeTecnicoEsIA.class.getSimpleName(), informeTecnicoEsIABean.getInformeTecnico().getId());
		JsfUtil.cargarObjetoSession("RevisionEIA"+proyectosBean.getProyectoRcoa().getCodigoUnicoAmbiental() + informeTecnicoEsIABean.getInformeTecnico().getId(), true);
		return JsfUtil.actionNavigateTo("/pages/rcoa/estudioImpactoAmbiental/default.jsf");		
	}
	
	public void esRequerido(Boolean requerido) {
		informeTecnicoEsIABean.setEsRequerido(requerido);
		memoOficioPatrimonioBean.setEsRequerido(requerido);
		
		if(!requerido)
			habilitarFirma = false;
	}
	
	public void validateDatosIngreso(FacesContext context, UIComponent validate, Object value) {
		if(context.getMessageList().size() > 0) {
			habilitarFirma = false;
			informeTecnicoEsIABean.setEsRequerido(false);
			memoOficioPatrimonioBean.setEsRequerido(false);
			
			RequestContext.getCurrentInstance().update(":form:pnlButtons");
	        RequestContext reqContext = RequestContext.getCurrentInstance();
	        reqContext.execute("PF('dlgConfirmar').hide();");
		}
	}

	public void guardarInformeOficio() {
		try {
			informeTecnicoEsIABean.guardarDatos();
			
			Boolean validado = false;
			
			if(generarMemoOficio) {
				memoOficioPatrimonioBean.setIdInforme(informeTecnicoEsIABean.getInformeTecnico().getId());			
				memoOficioPatrimonioBean.guardarDatos();
				
				if(memoOficioPatrimonioBean.getUsuarioAutoridad() != null) {
					validado = true;
				}
			} else {
				validado = true;
			}		
			
			if(validado) {
				if(informeTecnicoEsIABean.getRequiereIngresoPlan()) {
					String observacionesPma = planManejoAmbientalEsIAFacade.validarIngresoObservacionesPma(informeTecnicoEsIABean.getEsiaProyecto().getId(), informeTecnicoEsIABean.getNombreClaseObservacionesPma());
					if(observacionesPma != null) {
						habilitarFirma = false;
						JsfUtil.addMessageError(observacionesPma);
						return;
					}
				}
				
				habilitarFirma = true;
				JsfUtil.addMessageInfo(JsfUtil.REGISTRO_GUARDADO);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
		}
	}
	
	public void guardarDocumentos() {
		try {
			informeTecnicoEsIABean.guardarDocumentosAlfresco();

			if (informeTecnicoEsIABean.getDocumentoInforme() != null) {
				String documentOffice = documentosFacade.direccionDescarga(informeTecnicoEsIABean.getDocumentoInforme());
				urlAlfresco = DigitalSign.sign(documentOffice, JsfUtil.getLoggedUser().getNombre());
			}
		} catch (Exception exception) {
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
		
		RequestContext.getCurrentInstance().update("formDialogFirma:");
		RequestContext.getCurrentInstance().update("formDialogFirma:pnlFirmaToken");
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('signDialog').show();");
	}
	
	public StreamedContent descargar() {
		DefaultStreamedContent content = null;
		try {
			
			byte[] documentoContent = informeTecnicoEsIABean.getInformeTecnico().getArchivo();
			
			if (documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(informeTecnicoEsIABean.getInformeTecnico().getNombreReporte());
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
			documentoManual.setNombreTabla(InformeTecnicoEsIA.class.getSimpleName());
			documentoManual.setIdTable(informeTecnicoEsIABean.getInformeTecnico().getId());

			subido = true;
			JsfUtil.addMessageInfo("Documento subido exitosamente");
		} else{
			JsfUtil.addMessageError("No ha descargado el documento para la firma");
		}
	}
	
	public void finalizar() {
		RequestContext context = RequestContext.getCurrentInstance();
		
		try {
			
			if (token) {
				String idAlfresco = informeTecnicoEsIABean.getDocumentoInforme().getAlfrescoId();

				if (!documentosFacade.verificarFirmaVersion(idAlfresco)) {
					JsfUtil.addMessageError("El informe técnico no está firmado electrónicamente.");
					context.execute("PF('btnEnviar').enable();");
					return;
				}
			} else {
				if(subido) {
					DocumentoEstudioImpacto documentoFinal = documentosFacade.guardarDocumentoAlfrescoCA(informeTecnicoEsIABean.getProyecto().getCodigoUnicoAmbiental(), 
						"INFORME_TECNICO", documentoManual, TipoDocumentoSistema.EIA_INFORME_TECNICO_RCOA);

					informeTecnicoEsIABean.setDocumentoInforme(documentoFinal);
				} else {
					JsfUtil.addMessageError("Debe adjuntar el informe técnico firmado.");
					context.execute("PF('btnEnviar').enable();");
					return;
				}				
			}
			
			if(generarMemoOficio) {
				Boolean esPlantaCentral = false;
				Boolean esGad = false;
				if (informeTecnicoEsIABean.getProyecto().getAreaResponsable().getTipoArea().getSiglas().equalsIgnoreCase("PC")) 
					esPlantaCentral = true;
				else if (!informeTecnicoEsIABean.getProyecto().getAreaResponsable().getTipoArea().getSiglas().equalsIgnoreCase("ZONALES")
						&& !informeTecnicoEsIABean.getProyecto().getAreaResponsable().getId().equals(Constantes.ID_PARQUE_NACIONAL_GALAPAGOS))
					esGad = true;
				
				Map<String, Object> parametros = new HashMap<>();
				
				if(informeTecnicoEsIABean.getInformeTecnico().getTipoInforme().equals(InformeTecnicoEsIA.snap)){
					Area areaConservacion = (esPlantaCentral) ? areaFacade.getAreaPorAreaAbreviacion(Constantes.SIGLAS_DIRECCION_AREAS_PROTEGIDAS) : informeTecnicoEsIABean.getProyecto().getAreaResponsable(); 
					String tipoRol = (esPlantaCentral) ? "role.esia.pc.coordinador.conservacion" : "role.esia.cz.coordinador";
					String coordinadorConservacion = recuperarUsuario(areaConservacion, tipoRol);
					if(coordinadorConservacion == null) {
				        context.execute("PF('btnEnviar').enable();");
						return;
					} else
						parametros.put("coordinadorConservacion", coordinadorConservacion);
				} else {
					Area areaBosques = (esPlantaCentral) ? areaFacade.getAreaPorAreaAbreviacion(Constantes.SIGLAS_DIRECCION_BOSQUES) : informeTecnicoEsIABean.getProyecto().getAreaResponsable(); 
					String tipoRol = (esPlantaCentral) ? "role.esia.pc.coordinador.bosques" : "role.esia.cz.coordinador";
					
					if(esGad) {
						areaBosques = areaFacade.getAreaCoordinacionZonal(informeTecnicoEsIABean.getUbicacionPrincipal().getUbicacionesGeografica());
					}
					String coordinadorBosques = recuperarUsuario(areaBosques, tipoRol);
					if(coordinadorBosques == null) {
				        context.execute("PF('btnEnviar').enable();");
						return;
					} else {
						if(!esGad)
							parametros.put("coordinadorBosques", coordinadorBosques);
						else
							parametros.put("usuarioCoordinadorInventario", coordinadorBosques);
					}
				}
				
				procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);
			}
			
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
			
			context.execute("PF('btnCerrar').disable();");
			
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
			JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
			
		} catch (Exception e) {
			context.execute("PF('btnEnviar').enable();");
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
		}
	}
	
	public String recuperarUsuario(Area areaResponsable , String tipoRol) {				
		String rol = Constantes.getRoleAreaName(tipoRol);
		
		Usuario usuarioResponsable=buscarUsuarioBean.buscarUsuario(tipoRol, areaResponsable);
		
		if(usuarioResponsable == null){
			LOG.error("No se encontró usuario " + rol + " en " + areaResponsable.getAreaName());
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
			return null;
		}		
		
		return usuarioResponsable.getNombre();
	}
	
	public boolean habilitarGuardar() {
		Object revisadoEIA=JsfUtil.devolverObjetoSession("RevisionEIA"+proyectosBean.getProyectoRcoa().getCodigoUnicoAmbiental()+informeTecnicoEsIABean.getInformeTecnico().getId());		
		return revisadoEIA!=null;
	}
		
}
