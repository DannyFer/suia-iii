package ec.gob.ambiente.rcoa.viabilidadAmbientalForestal.controllers;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.DocumentosViabilidadFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.ObservacionesViabilidadFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.DocumentoViabilidad;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.ObservacionesViabilidad;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.ViabilidadCoa;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.DigitalSign;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.webservice.facade.AsignarTareaFacade;

@ManagedBean
@ViewScoped
public class RevisarDocumentosApoyoViabilidadPfnController implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private final Logger LOG = Logger.getLogger(RevisarDocumentosApoyoViabilidadPfnController.class);
	
	@ManagedProperty(value = "#{bandejaTareasBean}")
    @Getter
    @Setter
    private BandejaTareasBean bandejaTareasBean;
	
	@ManagedProperty(value = "#{informeOficioApoyoViabilidadPfnBean}")
	@Getter
	@Setter
	private InformeOficioApoyoViabilidadPfnBean informeOficioApoyoViabilidadPfnBean;
	
	@EJB
	private ProcesoFacade procesoFacade;
	
	@EJB
    private ObservacionesViabilidadFacade observacionesViabilidadFacade;
	
	@EJB
	private DocumentosViabilidadFacade documentosFacade;
	
	@EJB
	private UsuarioFacade usuarioFacade;
	
	@EJB
    private AsignarTareaFacade asignarTareaFacade;
	
	@Getter
	@Setter
	private ViabilidadCoa viabilidadProyecto;
	
	@Getter
	@Setter
	private DocumentoViabilidad documentoOficio, documentoFirmado;

	@Getter
	@Setter
	private Boolean documentosRequiereCorrecciones, habilitarEnviar, esProduccion;
	
	@Getter
	@Setter
	private String urlInforme, nombreInforme, urlOficio, nombreOficio, urlAlfresco;
	
	@Getter
	@Setter
	private byte[] archivoOficio, archivoInforme;
	
	@Getter
	@Setter
	private Integer panelMostrar, activeIndex, numeroRevision;

	@Getter
	@Setter
	private boolean token, descargado, subido;
	
	@PostConstruct
	private void iniciar() {
		JsfUtil.getLoggedUser().getNombre();
		try {
			
			esProduccion = Constantes.getPropertyAsBoolean("ambiente.produccion") ;
			
			verificaToken();
			
			viabilidadProyecto = informeOficioApoyoViabilidadPfnBean.getViabilidadProyecto();
						
			generarOficio(true);
			
			numeroRevision = Integer.valueOf((String) informeOficioApoyoViabilidadPfnBean.getVariables().get("numeroRevisionInformacion"));
			
			buscarInforme();
			
			panelMostrar = 1;
			activeIndex = 0;
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos.");
		}
	}
	
	public void validarTareaBpm() {
		JsfUtil.validarPaginasUrlTareasBpm(JsfUtil.getCurrentTask(),
				"/pages/rcoa/viabilidadAmbientalForestal/revisarDocumentosApoyo.jsf");
	}
	
	public boolean verificaToken() {
		if(esProduccion) {
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
	
	public void buscarInforme() {
		try {
			List<DocumentoViabilidad> documentos = documentosFacade
					.getDocumentoPorTipoTramite(viabilidadProyecto.getId(),
							TipoDocumentoSistema.RCOA_VIABILIDAD_PFN_II_INFORME_APOYO.getIdTipoDocumento());
			if(documentos.size() > 0) {
				DocumentoViabilidad documentoInforme = documentos.get(0);
				
				File fileDoc = documentosFacade.getDocumentoPorIdAlfresco(documentoInforme.getIdAlfresco());
				
				Path path = Paths.get(fileDoc.getAbsolutePath());
				byte[] contenido = Files.readAllBytes(path);
				String reporteHtmlfinal = documentoInforme.getNombre().replace("/", "-");
				File fileInforme = new File(JsfUtil.devolverPathReportesHtml(reporteHtmlfinal));
				FileOutputStream file = new FileOutputStream(fileInforme);
				file.write(contenido);
				file.close();
				
				urlInforme = JsfUtil.devolverContexto("/reportesHtml/" + documentoInforme.getNombre());
				nombreInforme = documentoInforme.getNombre();
				archivoInforme = contenido;
			}
		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
	}
	
	public StreamedContent getStream(String name, byte[] fileContent) throws Exception {
		DefaultStreamedContent content = new DefaultStreamedContent();
		if (fileContent != null) {
			content = new DefaultStreamedContent(new ByteArrayInputStream(fileContent), "application/octet-stream");
			content.setName(name);
		}
		return content;
	}
	
	public void validarExisteObservacionesInformeOficio() {
		try {
			documentosRequiereCorrecciones = false;
			List<ObservacionesViabilidad> observacionesInforme = observacionesViabilidadFacade.listarPorIdClaseNombreClaseNoCorregidas(
							informeOficioApoyoViabilidadPfnBean.getViabilidadProyecto().getId(), "revisionDocumentosApoyoPfn_" + numeroRevision);
			
			if(observacionesInforme.size() > 0) {
				documentosRequiereCorrecciones = true;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar las observaciones del informe oficio.");
		}
	}
	
	public void siguiente() {
        setActiveIndex(1);
        panelMostrar = 2;
	}
	
	public void atras() {
        setActiveIndex(0);
        panelMostrar = 1;
	}
	
	public void tabChange() {
		panelMostrar = activeIndex + 1;
	}
	
	public void generarOficio(Boolean marcaAgua) {
		informeOficioApoyoViabilidadPfnBean.generarOficio(marcaAgua);
		
		urlOficio = informeOficioApoyoViabilidadPfnBean.getOficioPronunciamiento().getOficioPath();
		nombreOficio = informeOficioApoyoViabilidadPfnBean.getOficioPronunciamiento().getNombreFichero();
		archivoOficio = informeOficioApoyoViabilidadPfnBean.getOficioPronunciamiento().getArchivoOficio();
	}
	
	public void guardarDocumentosFirma() { 
		try {
			
			informeOficioApoyoViabilidadPfnBean.guardarOficio();
			
			generarOficio(false);
			documentoOficio = informeOficioApoyoViabilidadPfnBean.guardarDocumentoOficioAlfresco();
			
			if(documentoOficio == null || documentoOficio.getId() == null) {
				LOG.error("No se encontro el oficio de pronunciamiento forestal");
				JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
				return;
			}
			
			String documentOffice = documentosFacade.direccionDescarga(documentoOficio);
			urlAlfresco = DigitalSign.sign(documentOffice, JsfUtil.getLoggedUser().getNombre());
		} catch (Exception e) {	
			e.printStackTrace();						
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos.");
		}
		
		RequestContext.getCurrentInstance().update("formDialogs:");
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('signDialog').show();");
	}
	
	public StreamedContent descargar() {
		DefaultStreamedContent content = null;
		try {
			
			byte[] documentoContent = informeOficioApoyoViabilidadPfnBean.getOficioPronunciamiento().getArchivoOficio();
			String nombreDoc= informeOficioApoyoViabilidadPfnBean.getOficioPronunciamiento().getNombreFichero();
			
			if (documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent),  "application/pdf");
				content.setName(nombreDoc);
			} else
				JsfUtil.addMessageError("Error al generar el archivo");
			
			descargado = true;
		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}

	public void uploadListenerDocumentos(FileUploadEvent event) {
		
		if(descargado){
			documentoFirmado = new DocumentoViabilidad();
			documentoFirmado.setNombre(event.getFile().getFileName());
			documentoFirmado.setContenidoDocumento(event.getFile().getContents());
			documentoFirmado.setMime("application/pdf");
			documentoFirmado.setIdTipoDocumento(informeOficioApoyoViabilidadPfnBean.getTipoOficio().getIdTipoDocumento());
			documentoFirmado.setIdViabilidad(viabilidadProyecto.getId());

			subido = true;
		}else{
			JsfUtil.addMessageError("No ha descargado el documento");
		}
	}
	
	public void enviar() {
		try {			
			Map<String, Object> parametros = new HashMap<>();
			
			// buscar usuarios por rol y area
			String tipoRol = "role.va.pfn.tecnico.forestal.apoyo";
			String rolTecnico = Constantes.getRoleAreaName(tipoRol);

			List<Usuario> listaTecnicosApoyo = asignarTareaFacade
					.getCargaLaboralPorUsuariosV2(rolTecnico, viabilidadProyecto.getAreaApoyo().getAreaName());			

			if(listaTecnicosApoyo==null || listaTecnicosApoyo.isEmpty()){
				LOG.error("No se encontro usuario " + rolTecnico + " en " + viabilidadProyecto.getAreaApoyo().getAreaName());
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION_MESA_AYUDA);
				JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
				return;
			}
			
			// recuperar tecnico de bpm y validar si el usuario existe en el listado anterior
			String usrTecnico = (String) informeOficioApoyoViabilidadPfnBean.getVariables().get("tecnicoApoyo");
			Usuario tecnicoApoyoResponsable = null;
			Usuario usuarioTecnico = usuarioFacade.buscarUsuario(usrTecnico);
			if (usuarioTecnico != null && usuarioTecnico.getEstado().equals(true)) {
				if (listaTecnicosApoyo != null && listaTecnicosApoyo.size() >= 0
						&& listaTecnicosApoyo.contains(usuarioTecnico)) {
					tecnicoApoyoResponsable = usuarioTecnico;
				}
			}
			
			if (tecnicoApoyoResponsable == null) {
				Usuario tecnicoResponsable = listaTecnicosApoyo.get(0);
				parametros.put("tecnicoApoyo", tecnicoResponsable.getNombre()); 
			}
			
			parametros.put("existeObsInformeApoyo", documentosRequiereCorrecciones);
			
			procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
			
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
			JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
			
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al enviar la tarea.");
		}
	}
	
	public void finalizar() {
		try {
			if (token) {
				String idAlfrescoOficio = documentoOficio.getIdAlfresco();

				if (!documentosFacade.verificarFirmaVersion(idAlfrescoOficio)) {
					JsfUtil.addMessageError("El Memorando no está firmado electrónicamente.");
					return;
				}
			} else {
				if(!subido) {
					JsfUtil.addMessageError("Debe adjuntar el Memorando firmado.");
					return;
				}
				
				documentoOficio = documentosFacade.guardarDocumentoProceso(
						informeOficioApoyoViabilidadPfnBean.getProyectoLicenciaCoa().getCodigoUnicoAmbiental(),
						"VIABILIDAD_AMBIENTAL", documentoFirmado, 2, JsfUtil.getCurrentProcessInstanceId());
				
			}
			
			Map<String, Object> parametros = new HashMap<>();
			
			parametros.put("existeObsInformeApoyo", false);
			
			// buscar usuarios por rol y area
			Area areaTramite = viabilidadProyecto.getAreaResponsable();
			String tipoRol = "role.va.pfn.tecnico.forestal";
			String rolTecnico = Constantes.getRoleAreaName(tipoRol);

			List<Usuario> listaTecnicos = asignarTareaFacade
					.getCargaLaboralPorUsuariosV2(rolTecnico, areaTramite.getAreaName());			

			if(listaTecnicos==null || listaTecnicos.isEmpty()){
				LOG.error("No se encontro usuario " + rolTecnico + " en " + areaTramite.getAreaName());
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION_MESA_AYUDA);
				JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
				return;
			}
			
			// recuperar tecnico de bpm y validar si el usuario existe en el listado anterior
			String usrTecnico = (String) informeOficioApoyoViabilidadPfnBean.getVariables().get("tecnicoResponsable");
			Usuario tecnicoResponsable = null;
			Usuario usuarioTecnico = usuarioFacade.buscarUsuario(usrTecnico);
			if (usuarioTecnico != null && usuarioTecnico.getEstado().equals(true)) {
				if (listaTecnicos != null && listaTecnicos.size() >= 0
						&& listaTecnicos.contains(usuarioTecnico)) {
					tecnicoResponsable = usuarioTecnico;
				}
			}
			
			if (tecnicoResponsable == null) {
				tecnicoResponsable = listaTecnicos.get(0);
				parametros.put("tecnicoResponsable", tecnicoResponsable.getNombre()); 
			}
				
			procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
			
			informeOficioApoyoViabilidadPfnBean.actualizarOficioFirmado();
			
			documentoOficio.setIdProceso(JsfUtil.getCurrentProcessInstanceId());
			documentosFacade.guardar(documentoOficio);

			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
			JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Error al realizar la operación.");
		}
	}
	
}
