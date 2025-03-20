package ec.gob.ambiente.rcoa.estudioImpactoAmbiental.controller;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
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

import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.DocumentosImpactoEstudioFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.ObservacionesEsIAFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.PlanManejoAmbientalEsIAFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.DocumentoEstudioImpacto;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.InformeTecnicoEsIA;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.ObservacionesEsIA;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.OficioPronunciamientoEsIA;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCuaCiuuFacade;
import ec.gob.ambiente.rcoa.model.CatalogoCIUU;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCuaCiuu;
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
public class ElaborarInformeConsolidadoController implements Serializable {

	private static final long serialVersionUID = 1L;

	private final Logger LOG = Logger.getLogger(ElaborarInformeConsolidadoController.class);
	
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
	private InformeTecnicoConsolidadoEIABean informeTecnicoEsIABean;
	
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
	private ProyectoLicenciaCuaCiuuFacade proyectoLicenciaCuaCiuuFacade;
	
	@EJB
	private PlanManejoAmbientalEsIAFacade planManejoAmbientalEsIAFacade;
	
	@Getter
	@Setter
	private Boolean mostrarInforme, esReporteAprobacion, informeGuardado, oficioGuardado;
	
	@Getter
	@Setter
	private DocumentoEstudioImpacto documentoManual;
	
	@Getter
	@Setter
	private String urlAlfresco, nombreClaseObservacionesPma;
	
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
	private Boolean existeObservacionesEstudio, mostrarTabOficio;
	
	@PostConstruct
	private void iniciar() {
		JsfUtil.getLoggedUser().getNombre();

		firmaSoloToken = Constantes.getPropertyAsBoolean("ambiente.produccion");
		if(!firmaSoloToken){
			verificaToken();
		}else{
			token = true;
		}
		
		informeGuardado = false;
		oficioGuardado = false;
		habilitarFirma = false;
		mostrarTabOficio = true;
		
		informeTecnicoEsIABean.generarInformeConsolidado(true);
		validarExisteObservacionesEstudio();
		if(existeObservacionesEstudio)
			informeTecnicoEsIABean.getInformeTecnico().setTipoPronunciamiento(informeTecnicoEsIABean.isEsTerceraObservacion()? InformeTecnicoEsIA.terceraRevision : InformeTecnicoEsIA.observado);
		else
			informeTecnicoEsIABean.getInformeTecnico().setTipoPronunciamiento(InformeTecnicoEsIA.aprobado);

		//siempre es oficio porque es la respuesta al operador
		pronunciamientoBean.setTipoOficio(OficioPronunciamientoEsIA.oficio);
		
		pronunciamientoBean.cargarDatos();
		pronunciamientoBean.setIdInforme(informeTecnicoEsIABean.getInformeTecnico().getId());
		pronunciamientoBean.generarOficio(true);
		informeTecnicoEsIABean.cargarListaTipoPronunciamiento();
	}
	
	public void validarTareaBpm() {

	}
	
	public boolean verificaToken() {
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
			String nombreClaseObservaciones1 = InformeTecnicoEsIA.class.getSimpleName() + "_" + informeTecnicoEsIABean.getInformeTecnico().getUsuarioCreacion() + "_" + informeTecnicoEsIABean.getInformeTecnico().getTipoInforme();
			String nombreClaseObservaciones2 = InformeTecnicoEsIA.class.getSimpleName() + "_" + informeTecnicoEsIABean.getInformeTecnico().getTipoInforme();
			List<String> nombreClaseObservaciones = new ArrayList<>();
			nombreClaseObservaciones.add(nombreClaseObservaciones1);
			nombreClaseObservaciones.add(nombreClaseObservaciones2);
			
			nombreClaseObservacionesPma = nombreClaseObservaciones2;
			
			existeObservacionesEstudio = false;
			List<ObservacionesEsIA> observaciones= observacionesEsIAFacade.listarPorIdClaseNombreClaseNoCorregidas(
					informeTecnicoEsIABean.getEsiaProyecto().getId(), nombreClaseObservaciones);
			
			List<ObservacionesEsIA> observacionesApoyo = observacionesInformesApoyo(informeTecnicoEsIABean.getEsiaProyecto().getId(), nombreClaseObservaciones);
			
			
			if(observaciones.size() > 0 || observacionesApoyo.size() > 0) {
				existeObservacionesEstudio = true;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al recuperar las observaciones.");
		}
	}
	
	private List<ObservacionesEsIA> observacionesInformesApoyo(Integer idClase, List<String> nombreClaseObservaciones){
		List<ObservacionesEsIA> listaObs = new ArrayList<>();		
		try {			
			List<ObservacionesEsIA> listaObsAux = observacionesEsIAFacade.listarPorIdClaseNoCorregidas(idClase, nombreClaseObservaciones.get(0));
			
			if(nombreClaseObservaciones.size() > 1) {
				for (ObservacionesEsIA item : listaObsAux) {
					if(!item.getNombreClase().equals(nombreClaseObservaciones.get(1))) {
						listaObs.add(item);
					}
				}
			} else {
				listaObs.addAll(listaObsAux);
			}
			
		} catch (Exception e) {
			LOG.error("Error en obtener las observaciones", e);
		}		
		return listaObs;
	}
	
	public void guardarInformeOficio() {
		try {
			
			validarCodigoDocumentos();
			
			informeTecnicoEsIABean.guardarDatos();
			pronunciamientoBean.guardarDatos();
			
			if(informeTecnicoEsIABean.getRequiereIngresoPlan()) {
				String observacionesPma = planManejoAmbientalEsIAFacade.validarIngresoObservacionesPma(informeTecnicoEsIABean.getEsiaProyecto().getId(), nombreClaseObservacionesPma);
				if(observacionesPma != null) {
					habilitarFirma = false;
					JsfUtil.addMessageError(observacionesPma);
					return;
				}
			}
			
			habilitarFirma = true;
			JsfUtil.addMessageInfo(JsfUtil.REGISTRO_GUARDADO);
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
		}
	}

	public void validateDatosIngreso(FacesContext context, UIComponent validate, Object value) {
		if(context.getMessageList().size() > 0) {
			habilitarFirma = false;
			informeTecnicoEsIABean.setEsRequerido(false);
			pronunciamientoBean.setEsRequerido(false);
			pronunciamientoBean.setValorMinimo(0.0);
			
			RequestContext.getCurrentInstance().update(":form:pnlButtons");
	        RequestContext reqContext = RequestContext.getCurrentInstance();
	        reqContext.execute("PF('dlgConfirmar').hide();");
		}
	}
	
	public void esRequerido(Boolean requerido) {
		informeTecnicoEsIABean.setEsRequerido(requerido);
		pronunciamientoBean.setEsRequerido(requerido);
		
		pronunciamientoBean.setValorMinimo(requerido ? 0.1 : 0.0);
		if(!requerido)
			habilitarFirma = false;
	}
	
	public void confirmarSustanciales() {
		RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('dlgConfirmar').show();");
	}
	
	public void guardarDocumentos() {
		try {
			if(informeTecnicoEsIABean.getEsiaProyecto().getInspeccion() != null && informeTecnicoEsIABean.getEsiaProyecto().getInspeccion()){
				if(informeTecnicoEsIABean.getDocumentoInspeccion() == null || informeTecnicoEsIABean.getDocumentoInspeccion().getId() == null){
					JsfUtil.addMessageError("El documento de inspección es requerido.");
					habilitarFirma = false;
					return;
				}
			}
			informeTecnicoEsIABean.guardarDocumentosAlfresco();

			if (informeTecnicoEsIABean.getDocumentoInforme() != null) {
				String documentOffice = documentosFacade.direccionDescarga(informeTecnicoEsIABean.getDocumentoInforme());
				urlAlfresco = DigitalSign.sign(documentOffice, JsfUtil.getLoggedUser().getNombre());
			}
		} catch (Exception exception) {
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
			habilitarFirma = false;
			return;
		}
		
		RequestContext.getCurrentInstance().update("formDialogFirma:");
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('signDialog').show();");
        
        RequestContext.getCurrentInstance().update("formDialogFirma:pnlFirmaToken");
	}

	public void uploadDocumentoEIA(FileUploadEvent file) {
		habilitarFirma = false;
		informeTecnicoEsIABean.uploadDocumentoEIA(file);
	}
	
	public String abrirEstudio(){
		JsfUtil.cargarObjetoSession(InformeTecnicoEsIA.class.getSimpleName(), informeTecnicoEsIABean.getInformeTecnico().getId());
		return JsfUtil.actionNavigateTo("/pages/rcoa/estudioImpactoAmbiental/default.jsf");		
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
		try {
			Map<String, Object> parametros = new HashMap<>();
			parametros.put("observacionesInformesApoyo", false);
			if (token) {
				String idAlfresco = informeTecnicoEsIABean.getDocumentoInforme().getAlfrescoId();

				if (!documentosFacade.verificarFirmaVersion(idAlfresco)) {
					JsfUtil.addMessageError("El informe técnico no está firmado electrónicamente.");
					return;
				}
			} else {
				if(subido) {
					DocumentoEstudioImpacto documentoFinal = documentosFacade.guardarDocumentoAlfrescoCA(informeTecnicoEsIABean.getProyecto().getCodigoUnicoAmbiental(), 
						"INFORME_TECNICO", documentoManual, TipoDocumentoSistema.EIA_INFORME_TECNICO_RCOA);

					informeTecnicoEsIABean.setDocumentoInforme(documentoFinal);
				} else {
					JsfUtil.addMessageError("Debe adjuntar el informe técnico firmado.");
					return;
				}
			}
			String rolAutoridad="";
			Usuario usuarioAutoridad = new Usuario();
			Area areaResponsable = informeTecnicoEsIABean.getProyecto().getAreaResponsable();
			if(areaResponsable.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_OT)){
				rolAutoridad = "role.zonal.responsable.oficina.tecnica";
			}else if(areaResponsable.getTipoArea().getSiglas().toUpperCase().equals(Constantes.SIGLAS_TIPO_AREA_ZONALES) || areaResponsable.getTipoArea().getSiglas().equals("EA")){
				rolAutoridad = "role.esia.gad.coordinador";
			}else if(areaResponsable.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_PC)){
				ProyectoLicenciaCuaCiuu proyectoCiuuPrincipal = proyectoLicenciaCuaCiuuFacade.actividadPrincipal(informeTecnicoEsIABean.getProyecto());
				CatalogoCIUU actividadPrincipal = proyectoCiuuPrincipal.getCatalogoCIUU();
				Integer idSector = actividadPrincipal.getTipoSector().getId();
				String tipoRol = "role.esia.pc.coordinador.tipoSector." + idSector;
				rolAutoridad = tipoRol;
			}else if(areaResponsable.getId().equals(Constantes.ID_PARQUE_NACIONAL_GALAPAGOS)){
				rolAutoridad = "role.esia.ga.coordinador";
			}

			if(usuarioAutoridad.getId() == null){
				usuarioAutoridad = buscarUsuarioBean.buscarUsuario(rolAutoridad, areaResponsable);
			}
			if (usuarioAutoridad == null ) {
				LOG.error("No se encontro usuario " + Constantes.getRoleAreaName(rolAutoridad) + " en " + areaResponsable.getAreaName());
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
				return;
			}
			parametros.put("usuarioCoordinador", usuarioAutoridad.getNombre());

			procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
			if(!existeObservacionesEstudio) {
				informeTecnicoEsIABean.getDocumentoInforme().setIdProceso(bandejaTareasBean.getProcessId());
				documentosFacade.guardar(informeTecnicoEsIABean.getDocumentoInforme());
			}
			
			if(informeTecnicoEsIABean.getRequiereIngresoPlan()) {
				planManejoAmbientalEsIAFacade.actualizarEstadoRevisionPma(informeTecnicoEsIABean.getEsiaProyecto().getId(), true);
			}
			
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
			JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
		}
	}

	public void enviar() {
		try {
			Map<String, Object> parametros = new HashMap<>();
			parametros.put("observacionesInformesApoyo", true);
			if(informeTecnicoEsIABean.getSelectedTecnicos() != null && informeTecnicoEsIABean.getSelectedTecnicos().size() > 0) {
				String[] listaTecnicosApoyo = new String[informeTecnicoEsIABean.getSelectedTecnicos().size()];
				Integer contador = 0;
				for (InformeTecnicoEsIA informe : informeTecnicoEsIABean.getSelectedTecnicos()) {
					listaTecnicosApoyo[contador++] = informe.getUsuarioCreacion();
				}
				parametros.put("listaTecnicosApoyo", listaTecnicosApoyo);
			} else {
				JsfUtil.addMessageError("Debe seleccionar el técnico para revisar las observaciones.");
				return;
			}

			procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);

			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
			JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
		}
	}
	
	public StreamedContent descargarPlantillaOficio() throws IOException {
		DefaultStreamedContent content = null;
		try {
			String nombreFormato = "Anexo09_EsIA_Oficio de aprobación.docx";
			String nombreDoc = "Oficio de aprobacion";
			
			if(!informeTecnicoEsIABean.getInformeTecnico().getTipoPronunciamiento().equals(InformeTecnicoEsIA.aprobado)) {
				nombreFormato = "Anexo10_EsIA_Oficio de observación.docx";
				nombreDoc = "Oficio de observacion";
			}
        	
            byte[] documentoGuia = documentosGeneralFacade.descargarDocumentoPorNombre(nombreFormato);
            if (documentoGuia != null) {
                content = new DefaultStreamedContent(new ByteArrayInputStream(documentoGuia));
				content.setName(nombreDoc + ".docx");
				return content;
            } else {
                JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
                return null;
            }
		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
	
	public void validarCodigoDocumentos() {
		Calendar cal = Calendar.getInstance();
		int anioActual = cal.get(Calendar.YEAR);
		
		String codigoInforme = informeTecnicoEsIABean.getInformeTecnico().getCodigoInforme();
		String codigoOficio = pronunciamientoBean.getOficioPronunciamiento().getCodigoOficio();
		
		if (codigoInforme != null && !codigoInforme.contains("-" + anioActual)) {
			 informeTecnicoEsIABean.getInformeTecnico().setCodigoInforme(null);
		}
		
		if (codigoOficio != null && !codigoOficio.contains("-" + anioActual)) {
			pronunciamientoBean.getOficioPronunciamiento().setCodigoOficio(null);
		}
	}
}
