package ec.gob.ambiente.prevencion.categoria2.bean;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.FichaAmbientalPma;
import ec.gob.ambiente.suia.domain.PlanSector;

public class DetallePlanManejoAmbientalBean {

	@Getter @Setter
	private Integer id;
	@Getter @Setter
	private PlanSector planSector;
	@Getter @Setter
	private Date fechaInicio;
	@Getter @Setter
	private Date fechaFin;
	@Getter @Setter
	private FichaAmbientalPma fichaAmbientalPma;
}
