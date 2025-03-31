package ec.gob.ambiente.retce.model;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.DesechoPeligroso;

public class ResumenManifiesto {

	@Getter
	@Setter
	private DesechoPeligroso desechoPeligroso;
	
	@Getter
	@Setter
	private Double totalDesecho;
	
	@Getter
	@Setter
	private Double totalUnidadesES;
}
