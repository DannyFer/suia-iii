package ec.gob.ambiente.rcoa.viabilidadAmbientalSnap.controllers;

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

import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.DocumentosViabilidadFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.ObservacionesViabilidadFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.DocumentoViabilidad;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.ObservacionesViabilidad;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.ViabilidadCoa;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.DigitalSign;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class RevisarInformeAreasSnapController implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ManagedProperty(value = "#{bandejaTareasBean}")
    @Getter
    @Setter
    private BandejaTareasBean bandejaTareasBean;
	
	@ManagedProperty(value = "#{generarInformeViabilidadSnapBean}")
	@Getter
	@Setter
	private GenerarInformeViabilidadSnapBean generarInformeViabilidadSnapBean;
	
	@EJB
	private DocumentosViabilidadFacade documentosFacade;
	
	@EJB
	private ObservacionesViabilidadFacade observacionesViabilidadFacade;
	
	@EJB
	private ProcesoFacade procesoFacade;
	
	@Getter
	@Setter
	private ViabilidadCoa viabilidadProyecto;
	
	@Getter
	@Setter
	private DocumentoViabilidad documentoInforme;

	@Getter
	@Setter
	private Boolean requiereCorrecciones;
	
	@Getter
	@Setter
	private String urlAlfresco, urlInforme, nombreInforme;
	
	@Getter
	@Setter
	private byte[] archivoInforme;

	@Getter
	@Setter
	private boolean revisionGuardada, esProduccion;
	
	@Getter
	@Setter
	private Integer idProyecto, idViabilidad, nroOtrasFirmasAp, numeroRevision;
	
	@PostConstruct
	private void iniciar() {
		try {
			
			generarInformeViabilidadSnapBean.generarInforme(true);
			generarInformeViabilidadSnapBean.setCamposSoloLectura(true);
			
			viabilidadProyecto = generarInformeViabilidadSnapBean.getViabilidadProyecto();
			
			esProduccion = Constantes.getPropertyAsBoolean("ambiente.produccion") ;
			
			nroOtrasFirmasAp = generarInformeViabilidadSnapBean.getVariables().containsKey("nroFirmasAdicionalesInforme") 
					? (Integer.valueOf((String) generarInformeViabilidadSnapBean.getVariables().get("nroFirmasAdicionalesInforme"))) : 0;
			
			buscarInformeInspeccion();
			
			revisionGuardada = false;
			
			requiereCorrecciones = false;

			numeroRevision = Integer.valueOf((String) generarInformeViabilidadSnapBean.getVariables().get("numeroRevision"));
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al iniciar la tarea.");
		}
	}
	
	public void validarTareaBpm() {
		JsfUtil.validarPaginasUrlTareasBpm(JsfUtil.getCurrentTask(),
				"/pages/rcoa/viabilidadAmbientalSnap/revisarInformeAreas.jsf");
	}
	
	public void buscarInformeInspeccion() {
		try {
			List<DocumentoViabilidad> documentos = documentosFacade
					.getDocumentoPorTipoTramite(viabilidadProyecto.getId(),
							TipoDocumentoSistema.RCOA_VIABILIDAD_SNAP_II_INFORME_TECNICO.getIdTipoDocumento());
			if(documentos.size() > 0) {
				documentoInforme = documentos.get(0);
				
				File fileDoc = documentosFacade.getDocumentoPorIdAlfresco(documentoInforme.getIdAlfresco());
				
				Path path = Paths.get(fileDoc.getAbsolutePath());
				byte[] contenido = Files.readAllBytes(path);
				String reporteHtmlfinal = documentoInforme.getNombre().replace("/", "-");
				File archivoFinal = new File(JsfUtil.devolverPathReportesHtml(reporteHtmlfinal));
				FileOutputStream file = new FileOutputStream(archivoFinal);
				file.write(contenido);
				file.close();
				
				urlInforme = JsfUtil.devolverContexto("/reportesHtml/" + documentoInforme.getNombre());
				nombreInforme = documentoInforme.getNombre();
				archivoInforme = contenido;
				
				String documentOffice = documentosFacade.direccionDescarga(documentoInforme);
				urlAlfresco = DigitalSign.sign(documentOffice, JsfUtil.getLoggedUser().getNombre());
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
	
	public void validarExisteObservacionesInforme() {
		try {
			requiereCorrecciones = true;
			
			List<ObservacionesViabilidad> observaciones = observacionesViabilidadFacade.listarPorIdClaseNombreClaseNoCorregidas(
					viabilidadProyecto.getId(), "revisionJefesOtrasAreas_" + numeroRevision);
			
			if(observaciones.size() == 0) {
				requiereCorrecciones = false;
			}
			
			if(!requiereCorrecciones) {
				RequestContext context = RequestContext.getCurrentInstance();
				context.execute("PF('btnFinalizar').disable();");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al recuperar las observaciones del oficio SNAP.");
		}
	}
	
	public void enviar() {
		try {
			Map<String, Object> parametros = new HashMap<>();
			
			parametros.put("existeObservacionesInforme", requiereCorrecciones);
			
			//verificar jefe de area responsable
			String jefeElabora = generarInformeViabilidadSnapBean.getVariables().get("responsableAreaProtegida").toString();
			if(!jefeElabora.equals(viabilidadProyecto.getAreaSnap().getUsuario().getNombre())) {
				parametros.put("responsableAreaProtegida", viabilidadProyecto.getAreaSnap().getUsuario().getNombre());
			}
			
			procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);
			
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
			
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
			JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}
	
	public void completarTarea() {
		try {
			
			String idAlfresco = documentoInforme.getIdAlfresco();

			if (esProduccion && !documentosFacade.verificarFirmaVersion(idAlfresco)) {
				JsfUtil.addMessageError("El informe no está firmado electrónicamente.");
				return;
			}
			
			nroOtrasFirmasAp = nroOtrasFirmasAp + 1;
			
			Map<String, Object> parametros = new HashMap<>();
			parametros.put("existeObservacionesInforme", requiereCorrecciones);
			parametros.put("nroFirmasAdicionalesInforme", nroOtrasFirmasAp);
			
			if(nroOtrasFirmasAp.equals(generarInformeViabilidadSnapBean.getAreasInterseca().size())) {
				parametros.put("firmasCompletas", true);
				
				//verificar jefe de area responsable
				String jefeElabora = generarInformeViabilidadSnapBean.getVariables().get("responsableAreaProtegida").toString();
				if(!jefeElabora.equals(viabilidadProyecto.getAreaSnap().getUsuario().getNombre())) {
					parametros.put("responsableAreaProtegida", viabilidadProyecto.getAreaSnap().getUsuario().getNombre());
				} 
			} else {
				parametros.put("firmasCompletas", false);
				
				parametros.put("otroResponsableAP", generarInformeViabilidadSnapBean.getAreasInterseca().get(nroOtrasFirmasAp).getTecnicoResponsable().getNombre());
			}
			
			procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);
			
			try {
				procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
				
				JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
				JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
				
			}catch (Exception e)  {
				nroOtrasFirmasAp = nroOtrasFirmasAp - 1;
				parametros = new HashMap<>();
				parametros.put("nroFirmasAdicionalesInforme", nroOtrasFirmasAp);
				procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);
				
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}
	
}
