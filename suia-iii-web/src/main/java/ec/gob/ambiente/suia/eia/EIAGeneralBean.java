package ec.gob.ambiente.suia.eia;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@SessionScoped
public class EIAGeneralBean {

	public static final String URL_VER_EIA = "/prevencion/licenciamiento-ambiental/eia/resumenEjecutivo/resumenEjecutivoVer.jsf";

	@Getter
	@Setter
	private EstudioImpactoAmbiental estudioImpactoAmbiental;

	@Getter
	@Setter
	private boolean habilitarObservaciones;

	@Getter
	@Setter
	private boolean observacionesSoloLectura;

	@Getter
	@Setter
	private boolean mostrarComoTarea;

	public String actionVerEIA(EstudioImpactoAmbiental estudio, boolean habilitarObservaciones,
			boolean observacionesSoloLectura, boolean mostrarComoTarea) {
		this.executeView(estudio, habilitarObservaciones, observacionesSoloLectura, mostrarComoTarea, false);
		return URL_VER_EIA;
	}

	public void redirectVerEIA(EstudioImpactoAmbiental estudio, boolean habilitarObservaciones,
			boolean observacionesSoloLectura, boolean mostrarComoTarea) {
		this.executeView(estudio, habilitarObservaciones, observacionesSoloLectura, mostrarComoTarea, true);
	}

	public void executeView(EstudioImpactoAmbiental estudio, boolean habilitarObservaciones,
			boolean observacionesSoloLectura, boolean mostrarComoTarea, boolean redirect) {
		this.estudioImpactoAmbiental = estudio;
		this.habilitarObservaciones = habilitarObservaciones;
		this.observacionesSoloLectura = observacionesSoloLectura;
		this.mostrarComoTarea = mostrarComoTarea;

		if (redirect)
			JsfUtil.redirectTo(URL_VER_EIA);
	}

	public boolean isRenderObservaciones() {
		return !JsfUtil.getRelativeCurrentPage().contains(URL_VER_EIA);
	}

	public String getSectionName() {
		return JsfUtil.getRelativeCurrentPage();
	}

	public String getClassName() {
		return EstudioImpactoAmbiental.class.getSimpleName();
	}
}
