package ec.gob.ambiente.control.aprobacionrequisitostecnicos.bean;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.AprobacionRequisitosTecnicos;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@SessionScoped
public class ArtGeneralBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 837668889098796659L;

	public static final String URL_VER_ART = "/prevencion/licenciamiento-ambiental/eia/resumenEjecutivo/resumenEjecutivoVer.jsf";

	@Getter
	@Setter
	private AprobacionRequisitosTecnicos aprobacion;

	@Getter
	@Setter
	private boolean habilitarObservaciones;

	@Getter
	@Setter
	private boolean observacionesSoloLectura;

	@Getter
	@Setter
	private boolean mostrarComoTarea;

	@Getter
	@Setter
	private String sectionName;

	public String actionVerART(AprobacionRequisitosTecnicos aprobacion, boolean habilitarObservaciones,
			boolean observacionesSoloLectura, boolean mostrarComoTarea) {		
		if (!isTecnicoAutenticado()) {
			observacionesSoloLectura = true;
		}
		this.executeView(aprobacion, habilitarObservaciones, observacionesSoloLectura, mostrarComoTarea, false);
		return URL_VER_ART;
	}

	public void redirectVerART(AprobacionRequisitosTecnicos aprobacion, boolean habilitarObservaciones,
			boolean observacionesSoloLectura, boolean mostrarComoTarea) {
		this.executeView(aprobacion, habilitarObservaciones, observacionesSoloLectura, mostrarComoTarea, true);
	}

	public void executeView(AprobacionRequisitosTecnicos aprobacion, boolean habilitarObservaciones,
			boolean observacionesSoloLectura, boolean mostrarComoTarea, boolean redirect) {
		this.aprobacion = aprobacion;
		this.habilitarObservaciones = habilitarObservaciones;
		this.observacionesSoloLectura = observacionesSoloLectura;
		this.mostrarComoTarea = mostrarComoTarea;

		if (redirect)
			JsfUtil.redirectTo(URL_VER_ART);
	}

	public boolean isRenderObservaciones() {
		return !JsfUtil.getRelativeCurrentPage().contains(URL_VER_ART);
	}

	// public String getSectionName() {
	// return JsfUtil.getRelativeCurrentPage();
	// }

	public String getClassName() {
		return AprobacionRequisitosTecnicos.class.getSimpleName();
	}

	public boolean isTecnicoAutenticado() {
		boolean isTecnico = false;
		if (JsfUtil.getCurrentTask().getVariable("tecnico") != null) {
			String pinTecnico = JsfUtil.getCurrentTask().getVariable("tecnico").toString();
			if (pinTecnico != null && JsfUtil.getLoggedUser().getPin() != null
					&& JsfUtil.getLoggedUser().getPin().equals(pinTecnico))
				isTecnico = true;
		}
		return isTecnico;
	}
}
