package ec.gob.ambiente.suia.eia.pma.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.DetalleEvaluacionAspectoAmbiental;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.domain.PlanManejoAmbiental;
import ec.gob.ambiente.suia.domain.TipoPlanManejoAmbiental;


public class PlanManejoAmbientalBean implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 3711308882367089100L;

	@Getter
	@Setter
	private List<DetalleEvaluacionAspectoAmbiental> detalleEvaluacionAspectoAmbientals;

	@Getter
	@Setter
	List<PlanManejoAmbiental> planesManejoAmbiental;

	@Getter
	@Setter
	private TipoPlanManejoAmbiental tipoPlan;

	@Getter
	@Setter
	List<TipoPlanManejoAmbiental> listaTipoPlanes;

	@Getter
	@Setter
	private EstudioImpactoAmbiental estudioImpactoAmbiental;

	@PostConstruct
	public void init() {
		setDetalleEvaluacionAspectoAmbientals(new ArrayList<DetalleEvaluacionAspectoAmbiental>());
		setPlanesManejoAmbiental(new ArrayList<PlanManejoAmbiental>());

	}



}
