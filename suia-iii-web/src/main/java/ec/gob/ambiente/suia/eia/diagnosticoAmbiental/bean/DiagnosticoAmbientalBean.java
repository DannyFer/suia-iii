package ec.gob.ambiente.suia.eia.diagnosticoAmbiental.bean;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import lombok.Getter;
import lombok.Setter;

import org.primefaces.event.FileUploadEvent;

import ec.gob.ambiente.suia.domain.DiagnosticoAmbiental;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.eia.diagnosticoAmbiental.controller.DiagnosticoAmbientalController;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean(name = "diagnosticoAmbiental")
@ViewScoped
public class DiagnosticoAmbientalBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6604485067622874385L;
	
	@Getter
	@Setter
	private Integer eiaId;
	@Getter
	@Setter
	private Integer id;
	@Getter
	@Setter
	private String problematicaMedioFisico;
	@Getter
	@Setter
	private String problematicaMedioBiotico;
	@Getter
	@Setter
	private String problematicaCultural;
	@Getter
	@Setter
	private byte[] anexoDiagnostico;
	@Getter
	@Setter
	private String anexoDiagnosticoName;
	@Getter
	@Setter
	private String anexoDiagnosticoContenType;
	@Getter
	@Setter
	private boolean archivoEditado = false;
	
	
	@Inject
	@Getter
	private DiagnosticoAmbientalController diagnosticoAmbientalController;
	
	@PostConstruct
	public void init(){
		try {
			EstudioImpactoAmbiental eia = (EstudioImpactoAmbiental)JsfUtil.devolverObjetoSession(Constantes.SESSION_EIA_OBJECT);
			setEiaId(eia.getId());
			
			DiagnosticoAmbiental diagnostico = diagnosticoAmbientalController.obtenerDiagnostico(getEiaId());
			setId(diagnostico.getId());
			setAnexoDiagnostico(diagnostico.getAnexoDiagnostico());
			setAnexoDiagnosticoContenType(diagnostico.getAnexoDiagnosticoContenType());
			setAnexoDiagnosticoName(diagnostico.getAnexoDiagnosticoName());
			setProblematicaCultural(diagnostico.getProblematicaCultura());
			setProblematicaMedioBiotico(diagnostico.getProblematicaMedioBiotico());
			setProblematicaMedioFisico(diagnostico.getProblematicaMedioFisico());
			
		} catch (Exception e) {
			JsfUtil.addMessageError("Error al obtener datos iniciales");
		}
		
	}
	
	public void handleFileUpload(FileUploadEvent event) {
		if (getAnexoDiagnostico() != null) {
			setArchivoEditado(true);
		}
		setAnexoDiagnostico(event.getFile().getContents());
		setAnexoDiagnosticoContenType(event.getFile().getContentType());
		setAnexoDiagnosticoName(event.getFile().getFileName());
		FacesMessage message = new FacesMessage("Archivo cargado con éxito: ",
				event.getFile().getFileName());
		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	public String guardar() {
		try {
			diagnosticoAmbientalController.guardarDiagnostico(this);
			JsfUtil.addMessageInfo("Grabado exitoso");
			return JsfUtil.actionNavigateTo("/eia/default.jsf");
		} catch (Exception e) {
			JsfUtil.addMessageError("Error al grabar el registro");
			return null;
		}
	}

	public String cancelar() {
		return JsfUtil.actionNavigateTo("/eia/default.jsf");
	}
}
