package ec.gob.ambiente.rcoa.bypassParticipacionCiudadana.controllers;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.rcoa.bypassParticipacionCiudadana.facade.DocumentoBPCFacade;
import ec.gob.ambiente.rcoa.bypassParticipacionCiudadana.facade.ExpedienteBPCFacade;
import ec.gob.ambiente.rcoa.bypassParticipacionCiudadana.model.DocumentoBPC;
import ec.gob.ambiente.rcoa.bypassParticipacionCiudadana.model.ExpedienteBPC;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.InformacionProyectoEIACoaFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.InformacionProyectoEia;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;

@ManagedBean
@ViewScoped
public class IngresarExpedienteBPCController {

	@Getter
	@Setter
	@ManagedProperty(value = "#{proyectosBean}")
	private ProyectosBean proyectosBean;
	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;
	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;

	@EJB
	private DocumentoBPCFacade documentoBPCFacade;
	@EJB
	private InformacionProyectoEIACoaFacade informacionProyectoEIACoaFacade;
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	@EJB
	private ExpedienteBPCFacade expedienteBPCFacade;
	@EJB
	private ProcesoFacade procesoFacade;

	@Getter
	@Setter
	private ExpedienteBPC expedienteBPC = new ExpedienteBPC();
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyectoLicenciaCoa = new ProyectoLicenciaCoa();
	@Getter
	@Setter
	private ExpedienteBPC pronunciamientoAprobado = new ExpedienteBPC();
	@Getter
	@Setter
	private List<DocumentoBPC> listaDocumentoBPC = new ArrayList<DocumentoBPC>();
	@Getter
	@Setter
	private List<DocumentoBPC> listaDocumentosEsIA = new ArrayList<DocumentoBPC>();
	@Getter
	@Setter
	private List<DocumentoBPC> listaDocumentosEliminar = new ArrayList<DocumentoBPC>();
	@Getter
	@Setter
	private Map<String, Object> variables;
	@Getter
	@Setter
	private InformacionProyectoEia informacionProyectoEia;
	@Getter
	@Setter
	private Usuario usuarioAutoridad;	
	@Getter
	@Setter
	private Area areaAutoridad;
	@Getter
	@Setter
	private String tramite = "";
	@Getter
	@Setter
	private byte[] documento;
	@Getter
	@Setter
	private Integer idProyecto;
	@Getter
	@Setter
	private Boolean tieneResolucionFisica;
	@Getter
	@Setter
	private String operador,tecnicoResponsable;	
	
	@Inject
	private UtilBPCBypass utilPPC;

	// Constantes
	public static final String FOLDER_ALFRESCO = "PARTICIPACION_CIUDADANA_BYPASS";

	@PostConstruct
	public void inicio() {
		try {
			variables = procesoFacade.recuperarVariablesProceso(loginBean.getUsuario(),bandejaTareasBean.getTarea().getProcessInstanceId());
			tramite = (String) variables.get("tramite");
			idProyecto = Integer.valueOf((String) variables.get("idProyecto"));
			proyectoLicenciaCoa = proyectoLicenciaCoaFacade.buscarProyecto(tramite);
			informacionProyectoEia = informacionProyectoEIACoaFacade.obtenerInformacionProyectoEIAPorProyecto(proyectoLicenciaCoa);
			operador = (String) variables.get("operador");
			tecnicoResponsable = (String) variables.get("tecnicoResponsable");
			String rolAutoridad = "autoridad";
			if (proyectoLicenciaCoa.getAreaResponsable().getTipoArea().getSiglas().equalsIgnoreCase("PC"))
				rolAutoridad = "subsecretario";

			usuarioAutoridad = utilPPC.asignarRol(proyectoLicenciaCoa, rolAutoridad);

			areaAutoridad = new Area();

			if (usuarioAutoridad.getListaAreaUsuario() != null && usuarioAutoridad.getListaAreaUsuario().size() == 1) {
				areaAutoridad = usuarioAutoridad.getListaAreaUsuario().get(0).getArea();
			} else {
				areaAutoridad = proyectoLicenciaCoa.getAreaResponsable();
			}
			verInformacion();
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos.");
		}
	}

	private void verInformacion() {
		expedienteBPC = new ExpedienteBPC();
		expedienteBPC = expedienteBPCFacade.getByProyectoLicenciaCoa(idProyecto);

		listaDocumentoBPC = documentoBPCFacade.documentoXTablaIdXIdDoc(expedienteBPC.getId(),ExpedienteBPC.class.getSimpleName(), TipoDocumentoSistema.RCOA_PPC_BYPASSS_ADJUNTOS);
		listaDocumentosEsIA = documentoBPCFacade.documentoXTablaIdXIdDoc(expedienteBPC.getId(),ExpedienteBPC.class.getSimpleName(), TipoDocumentoSistema.RCOA_PPC_BYPASSS_EIA);
	}
	

	public void uploadDocPPC(FileUploadEvent event) {
		DocumentoBPC documento = new DocumentoBPC();
		byte[] contenidoDocumento = event.getFile().getContents();
		documento.setContenidoDocumento(contenidoDocumento);
		documento.setNombreDocumento(event.getFile().getFileName());
		documento.setExtencionDocumento(".pdf");
		documento.setMimeDocumento("application/pdf");
		documento.setNombreTabla(ExpedienteBPC.class.getSimpleName());
		documento.setDescripcionTabla("Expediente BPC");
		documento.setIdProceso(bandejaTareasBean.getTarea().getProcessInstanceId());

		listaDocumentoBPC.add(documento);

		JsfUtil.addMessageInfo("Documento subido exitosamente");
	}

	public void uploadDocEstudio(FileUploadEvent event) {
		DocumentoBPC documento = new DocumentoBPC();
		byte[] contenidoDocumento = event.getFile().getContents();
		documento.setContenidoDocumento(contenidoDocumento);
		documento.setNombreDocumento(event.getFile().getFileName());
		documento.setExtencionDocumento(".pdf");
		documento.setMimeDocumento("application/pdf");
		documento.setNombreTabla(ExpedienteBPC.class.getSimpleName());
		documento.setDescripcionTabla("Estudio modificado");
		documento.setIdProceso(bandejaTareasBean.getTarea().getProcessInstanceId());

		listaDocumentosEsIA.add(documento);

		JsfUtil.addMessageInfo("Documento subido exitosamente");
	}

	public StreamedContent descargarDocumento(DocumentoBPC documento) throws Exception {
		DefaultStreamedContent content = new DefaultStreamedContent();
		if (documento != null) {
			if (documento.getContenidoDocumento() == null) {
				documento.setContenidoDocumento(documentoBPCFacade.descargar(documento.getIdAlfresco()));
			}
			content = new DefaultStreamedContent(new ByteArrayInputStream(documento.getContenidoDocumento()),
					documento.getExtencionDocumento());
			content.setName(documento.getNombreDocumento());
		}
		return content;

	}

	public void eliminarDocumentoBPC(DocumentoBPC documento) {
		try {
			listaDocumentoBPC.remove(documento);
			listaDocumentosEliminar.add(documento);
			JsfUtil.addMessageInfo("Documento eliminado correctamente");

		} catch (Exception e) {
		}
	}

	public void eliminarDocumentoEiA(DocumentoBPC documento) {
		try {
			listaDocumentosEsIA.remove(documento);
			listaDocumentosEliminar.add(documento);
			JsfUtil.addMessageInfo("Documento eliminado correctamente");
		} catch (Exception e) {
		}
	}

	public void participacion() {
		if (!expedienteBPC.getPronunciamientoFisico()) {
			expedienteBPC.setTieneResolucionFisica(null);
		}
	}

	public void cambioPronunciamiento() {
		if (expedienteBPC.getPronunciamientoAprobado() != null && !expedienteBPC.getPronunciamientoAprobado()) {
			expedienteBPC.setTieneResolucionFisica(null);
			List<DocumentoBPC> documentosEliminar = new ArrayList<>(listaDocumentosEsIA);
			for (DocumentoBPC documento : documentosEliminar) {
				listaDocumentosEsIA.remove(documento);
				listaDocumentosEliminar.add(documento);
			}
		}
	}

	public void guardarExpediente() {
		try {
			expedienteBPC.setProyectoLicenciaCoa(proyectoLicenciaCoa);
			expedienteBPC = expedienteBPCFacade.guardar(expedienteBPC);

			if (listaDocumentoBPC == null || listaDocumentoBPC.isEmpty()) {
				JsfUtil.addMessageError("Debe adjuntar por lo menos un archivo en la sección Adjuntar los archivos del Proceso de Participación Ciudadana");
				return;
			}

			for (DocumentoBPC documento : listaDocumentoBPC) {
				if (documento.getId() == null) {
					documento.setIdTabla(expedienteBPC.getId());
					documento = documentoBPCFacade.guardarDocumentoAlfresco(
							proyectoLicenciaCoa.getCodigoUnicoAmbiental(), FOLDER_ALFRESCO,
							bandejaTareasBean.getTarea().getProcessInstanceId(), documento,
							TipoDocumentoSistema.RCOA_PPC_BYPASSS_ADJUNTOS);
				}
			}

			if (pronunciamientoAprobado != null) {
				if (listaDocumentosEsIA != null && !listaDocumentosEsIA.isEmpty()) {
					for (DocumentoBPC documento : listaDocumentosEsIA) {
						if (documento.getId() == null) {
							documento.setIdTabla(expedienteBPC.getId());
							documento = documentoBPCFacade.guardarDocumentoAlfresco(
									proyectoLicenciaCoa.getCodigoUnicoAmbiental(), FOLDER_ALFRESCO,
									bandejaTareasBean.getTarea().getProcessInstanceId(), documento,
									TipoDocumentoSistema.RCOA_PPC_BYPASSS_EIA);
						}
					}
				}
			} else {
				if (listaDocumentosEsIA != null && !listaDocumentosEsIA.isEmpty()) {
					for (DocumentoBPC documento : listaDocumentosEsIA) {
						if (documento.getId() != null) {
							listaDocumentosEliminar.add(documento);
						}
					}
				}
				listaDocumentosEsIA = new ArrayList<>();
			}

			for (DocumentoBPC documento : listaDocumentosEliminar) {
				if (documento.getId() != null) {
					documento.setEstado(false);
					documentoBPCFacade.guardar(documento);
				}
			}

			verInformacion();

			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);

		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_CARGAR_DATOS);
		}

	}

	public void completarTarea() {
		try {
			guardarExpediente();
			if (listaDocumentoBPC == null || listaDocumentoBPC.isEmpty()) {
				return;
			}
			Map<String, Object> parametros = new ConcurrentHashMap<String, Object>();
			parametros.put("esFisico", expedienteBPC.getPronunciamientoFisico());
			parametros.put("esAprobacion", expedienteBPC.getPronunciamientoAprobado());
			
			procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);			
			
			if(expedienteBPC.getPronunciamientoFisico() && expedienteBPC.getPronunciamientoAprobado()) {
				if ((expedienteBPC != null) && (expedienteBPC.getId() != null) && (expedienteBPC.getTieneResolucionFisica() != null) && (expedienteBPC.getTieneResolucionFisica())) {
					generarTareaResolucionFisica();
				} else {
					generarTareaNoResolucionFisica();
				}
			}
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
			JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");

		} catch (JbpmException e) {
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION_MESA_AYUDA);
			e.printStackTrace();
		}
	}
	
	private void generarTareaResolucionFisica() {
		Usuario usuarioTarea = new Usuario();
		usuarioTarea = loginBean.getUsuario();
		Map<String, Object> parametros = new ConcurrentHashMap<String, Object>();
		parametros.put("tramite", tramite);
		parametros.put("idProyecto", idProyecto);
		parametros.put("operador", operador);
		parametros.put("tecnicoResponsable", tecnicoResponsable);
		parametros.put("esFisico", true);
		parametros.put("autoridadAmbiental", usuarioAutoridad.getNombre());
		try {
			Long idProceso = procesoFacade.iniciarProceso(usuarioTarea,
					Constantes.ROCA_PROCESO_RESOLUCION_LICENCIA_AMBIENTAL, tramite, parametros);
			System.out.println("proceso ResolucionLicenciaAmbiental ::::" + idProceso);
		} catch (JbpmException e) {
			e.printStackTrace();
		}
	}
	
	private void generarTareaNoResolucionFisica() {
		Usuario usuarioTarea = new Usuario();
		usuarioTarea = loginBean.getUsuario();
		Map<String, Object> parametros = new ConcurrentHashMap<String, Object>();
		parametros.put("tramite", tramite);
		parametros.put("idProyecto", idProyecto);
		parametros.put("operador", operador);
		parametros.put("tecnicoResponsable", tecnicoResponsable);
		parametros.put("esFisico", false);
		parametros.put("autoridadAmbiental", usuarioAutoridad.getNombre());
		try {
			Long idProceso = procesoFacade.iniciarProceso(usuarioTarea,
					Constantes.ROCA_PROCESO_RESOLUCION_LICENCIA_AMBIENTAL, tramite, parametros);
			System.out.println("proceso ResolucionLicenciaAmbiental ::::" + idProceso);
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
		} catch (JbpmException e) {
			e.printStackTrace();
		}
}
}
