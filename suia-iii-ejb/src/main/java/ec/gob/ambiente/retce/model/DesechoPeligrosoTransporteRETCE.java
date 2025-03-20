package ec.gob.ambiente.retce.model;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.DesechoPeligrosoTransporte;

public class DesechoPeligrosoTransporteRETCE {

	@Getter
	@Setter
	private DesechoPeligrosoTransporte desechoPeligrosoTransporte;
	
	@Getter
	@Setter
	private DesechoPeligroso desechoPeligroso;
	
}
