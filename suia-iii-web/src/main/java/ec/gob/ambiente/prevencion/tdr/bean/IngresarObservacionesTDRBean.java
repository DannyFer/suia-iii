package ec.gob.ambiente.prevencion.tdr.bean;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.ObservacionTdrEiaLiciencia;
import ec.gob.ambiente.suia.domain.TdrEiaLicencia;
import ec.gob.ambiente.suia.observaciontdreialiciencia.facade.ObservacionTdrEiaFacade;

@ManagedBean
@ViewScoped
public class IngresarObservacionesTDRBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1411093434786994690L;

	@Setter
	@Getter
	private Boolean antecedentes;

	@Setter
	@Getter
	private Boolean evaluacionTecnica;

	@Setter
	@Getter
	private Boolean caracteristicasImportantesProyecto;
	@Setter
	@Getter
	private Boolean fichaTecnica;
	@Setter
	@Getter
	private Boolean introduccion;
	@Setter
	@Getter
	private Boolean diagnosticoAmbientalLineaBase;
	@Setter
	@Getter
	private Boolean descripcionProyecto;
	@Setter
	@Getter
	private Boolean determinacionAreaInfluencia;
	@Setter
	@Getter
	private Boolean identificacionEvaluacionValoracion;
	@Setter
	@Getter
	private Boolean identificacionSitiosContaminados;
	@Setter
	@Getter
	private Boolean planManejoAmbiental;
	@Setter
	@Getter
	private Boolean planMonitoreo;
	@Setter
	@Getter
	private Boolean inventarioForestal;
	@Setter
	@Getter
	private Boolean anexos;
	@Setter
	@Getter
	private String test = ".......";

	@Getter
	@Setter
	ObservacionTdrEiaLiciencia observacionTdrEiaLiciencia;

	@EJB
	@Getter
	@Setter
	ObservacionTdrEiaFacade observacionTdrEiaFacade;

	@EJB
	@Getter
	@Setter
	CrudServiceBean crudServiceBean;

	@PostConstruct
	public void init() {
		observacionTdrEiaLiciencia = new ObservacionTdrEiaLiciencia();
	}

	public void inicializarValores(TdrEiaLicencia tdrEia, String componente) {

		System.out.print("................................................");
		observacionTdrEiaLiciencia = observacionTdrEiaFacade
				.getObservacionTdrEiaLicienciaPorIdTdrComponente(
						tdrEia.getId(), componente);

		if (observacionTdrEiaLiciencia != null) {

			if (observacionTdrEiaLiciencia.getAntecedentes() != null) {
				antecedentes = !observacionTdrEiaLiciencia.getAntecedentes()
						.isEmpty();
			}

			if (observacionTdrEiaLiciencia.getEvaluacionTecnica() != null) {
				evaluacionTecnica = !observacionTdrEiaLiciencia
						.getEvaluacionTecnica().isEmpty();
			}

			if (observacionTdrEiaLiciencia
					.getCaracteristicasImportantesProyecto() != null) {
				caracteristicasImportantesProyecto = !observacionTdrEiaLiciencia
						.getCaracteristicasImportantesProyecto().isEmpty();
			}

			if (observacionTdrEiaLiciencia.getFichaTecnica() != null) {
				fichaTecnica = !observacionTdrEiaLiciencia.getFichaTecnica()
						.isEmpty();
			}

			if (observacionTdrEiaLiciencia.getIntroduccion() != null) {
				introduccion = !observacionTdrEiaLiciencia.getIntroduccion()
						.isEmpty();
			}

			if (observacionTdrEiaLiciencia.getDiagnosticoAmbientalLineaBase() != null) {
				diagnosticoAmbientalLineaBase = !observacionTdrEiaLiciencia
						.getDiagnosticoAmbientalLineaBase().isEmpty();
			}

			if (observacionTdrEiaLiciencia.getDescripcionProyecto() != null) {
				descripcionProyecto = !observacionTdrEiaLiciencia
						.getDescripcionProyecto().isEmpty();
			}

			if (observacionTdrEiaLiciencia.getDeterminacionAreaInfluencia() != null) {
				determinacionAreaInfluencia = !observacionTdrEiaLiciencia
						.getDeterminacionAreaInfluencia().isEmpty();
			}

			if (observacionTdrEiaLiciencia
					.getIdentificacionEvaluacionValoracion() != null) {
				identificacionEvaluacionValoracion = !observacionTdrEiaLiciencia
						.getIdentificacionEvaluacionValoracion().isEmpty();
			}

			if (observacionTdrEiaLiciencia
					.getIdentificacionSitiosContaminados() != null) {
				identificacionSitiosContaminados = !observacionTdrEiaLiciencia
						.getIdentificacionSitiosContaminados().isEmpty();
			}

			if (observacionTdrEiaLiciencia.getPlanManejoAmbiental() != null) {
				planManejoAmbiental = !observacionTdrEiaLiciencia
						.getPlanManejoAmbiental().isEmpty();
			}

			if (observacionTdrEiaLiciencia.getPlanMonitoreo() != null) {
				planMonitoreo = !observacionTdrEiaLiciencia.getPlanMonitoreo()
						.isEmpty();
			}

			if (observacionTdrEiaLiciencia.getInventarioForestal() != null) {
				inventarioForestal = !observacionTdrEiaLiciencia
						.getInventarioForestal().isEmpty();
			}

			if (observacionTdrEiaLiciencia.getAnexos() != null) {
				anexos = !observacionTdrEiaLiciencia.getAnexos().isEmpty();
			}

		} else {
			observacionTdrEiaLiciencia = new ObservacionTdrEiaLiciencia();
			observacionTdrEiaLiciencia.setComponente(componente);
			observacionTdrEiaLiciencia.setTdrEia(tdrEia);
			observacionTdrEiaLiciencia = crudServiceBean
					.saveOrUpdate(observacionTdrEiaLiciencia);
		}
	}
}
