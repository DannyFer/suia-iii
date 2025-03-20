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
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.DocumentosViabilidadFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.InformeInspeccionForestalFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.ObservacionesViabilidadFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.ViabilidadCoaFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.DocumentoViabilidad;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.InformeInspeccionForestal;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.ObservacionesViabilidad;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.ViabilidadCoa;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.webservice.facade.AsignarTareaFacade;

@ManagedBean
@ViewScoped
public class RevisarInformeViabilidadPfnController implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private final Logger LOG = Logger.getLogger(RevisarInformeViabilidadPfnController.class);
	
	@ManagedProperty(value = "#{bandejaTareasBean}")
    @Getter
    @Setter
    private BandejaTareasBean bandejaTareasBean;
	
	@EJB
	private ProcesoFacade procesoFacade;
	
	@EJB
    private ObservacionesViabilidadFacade observacionesViabilidadFacade;
	
	@EJB
    private ViabilidadCoaFacade viabilidadCoaFacade;
	
	@EJB
	private DocumentosViabilidadFacade documentosFacade;
	
	@EJB
	private UsuarioFacade usuarioFacade;
	
	@EJB
    private AsignarTareaFacade asignarTareaFacade;
	
	@EJB
	private InformeInspeccionForestalFacade informeInspeccionFacade;
	
	@Getter
	@Setter
	private ViabilidadCoa viabilidadProyecto;
	
	@Getter
	@Setter
	private DocumentoViabilidad documentoInformeFactibilidad;
	
	@Getter
	@Setter
	private TipoDocumentoSistema tipoInforme;

	@Getter
	@Setter
	private Boolean documentosRequiereCorrecciones;
	
	@Getter
	@Setter
	private String urlInforme, nombreInforme;
	
	@Getter
	@Setter
	private String claseObservaciones, seccionObservaciones;
	
	@Getter
	@Setter
	private byte[] archivoInforme;
	
	@Getter
	@Setter
	private Integer numeroRevision;
	
	@Getter
	@Setter
	private Map<String, Object> variables;
	
	@PostConstruct
	private void iniciar() {
		JsfUtil.getLoggedUser().getNombre();
		try {			
			variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
			String idProyectoString = (String) variables.get("idProyecto");
			Integer idProyecto = Integer.valueOf(idProyectoString);
			numeroRevision = Integer.valueOf((String) variables.get("numeroRevisionInformacion"));
			
			viabilidadProyecto = viabilidadCoaFacade.getViabilidadForestalPorProyecto(idProyecto);
			
			tipoInforme = TipoDocumentoSistema.RCOA_VIABILIDAD_PFN_II_INFORME_VIABILIDAD;
			seccionObservaciones = "Revision informe de viabilidad PFN por PC";
			claseObservaciones = "revisionInformeViabilidadPfnPc";
			
			if(!buscarInforme()) {
				return;
			}

			List<DocumentoViabilidad> documentos = documentosFacade
					.getDocumentoPorTipoTramite(viabilidadProyecto.getId(), TipoDocumentoSistema.RCOA_VIABILIDAD_PFN_II_INFORME_FACTIBILIDAD.getIdTipoDocumento());
			if(documentos.size() > 0) {
				documentoInformeFactibilidad = documentos.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos.");
		}
	}
	
	public void validarTareaBpm() {
		JsfUtil.validarPaginasUrlTareasBpm(JsfUtil.getCurrentTask(),
				"/pages/rcoa/viabilidadAmbientalForestal/revisarInformeViabilidad.jsf");
	}
	
	public Boolean buscarInforme() {
		try {
			
			InformeInspeccionForestal informeInspeccion = informeInspeccionFacade.getInformePorViabilidad(viabilidadProyecto.getId());
			
			if(!informeInspeccion.getTipoInforme().equals(InformeInspeccionForestal.viabilidad)) {
				return false;
			}
			
			List<DocumentoViabilidad> documentos = documentosFacade
					.getDocumentoPorTipoTramite(viabilidadProyecto.getId(), tipoInforme.getIdTipoDocumento());
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
			e.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
			return false;
		}
		
		return true;
	}
	
	public StreamedContent getStream() throws Exception {
		DefaultStreamedContent content = new DefaultStreamedContent();
		if (archivoInforme != null) {
			content = new DefaultStreamedContent(new ByteArrayInputStream(archivoInforme), "application/octet-stream");
			content.setName(nombreInforme);
		}
		return content;
	}
	
	public void validarExisteObservacionesInformeOficio() {
		try {
			documentosRequiereCorrecciones = false;
			List<ObservacionesViabilidad> observacionesInforme = observacionesViabilidadFacade.listarPorIdClaseNombreClaseNoCorregidas(
							viabilidadProyecto.getId(), claseObservaciones);
			
			if(observacionesInforme.size() > 0) {
				documentosRequiereCorrecciones = true;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar las observaciones del informe oficio.");
		}
	}

	public StreamedContent descargarInforme() {
		DefaultStreamedContent content = null;
		try {

			if(documentoInformeFactibilidad != null) {
				byte[] documentoContent = documentosFacade.descargar(documentoInformeFactibilidad.getIdAlfresco());
				String nombreDoc= documentoInformeFactibilidad.getNombre();
				
				if (documentoContent != null) {
					content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent),  "application/pdf");
					content.setName(nombreDoc);
				} else
					JsfUtil.addMessageError("Error al descargar el archivo");
			}
		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
	
	public void enviar() {
		try {
			
			Map<String, Object> parametros = new HashMap<>();

			parametros.put("existeObservaciones", documentosRequiereCorrecciones);

			if(documentosRequiereCorrecciones) {
				//buscar tecnico responsable por rol y area
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
				String usrTecnico = (String) variables.get("tecnicoResponsable");
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
			} 
			
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
}
