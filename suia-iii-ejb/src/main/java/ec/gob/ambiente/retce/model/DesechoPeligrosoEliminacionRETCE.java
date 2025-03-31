package ec.gob.ambiente.retce.model;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.domain.TipoEliminacionDesecho;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.EliminacionDesecho;

public class DesechoPeligrosoEliminacionRETCE {

	@Getter
	@Setter
	private DesechoPeligroso desechoPeligroso;
	
	@Getter
	@Setter
	private TipoEliminacionDesecho modalidad;
	
	@Getter
	@Setter
	private TipoEliminacionDesecho tipoEliminacionDesecho;
	
	@Getter
	@Setter
	private EliminacionDesecho eliminacionDesecho;
	
}
