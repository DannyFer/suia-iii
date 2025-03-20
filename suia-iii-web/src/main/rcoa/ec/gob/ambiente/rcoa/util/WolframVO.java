package ec.gob.ambiente.rcoa.util;

import ec.gob.ambiente.rcoa.model.CatalogoCIUU;
import lombok.Getter;
import lombok.Setter;

public class WolframVO {
	@Getter
	@Setter
	private String nivel3;
	@Getter
	@Setter
	private String nivel5;
	@Getter
	@Setter
	private Integer importancia;
	@Getter
	@Setter
	private Boolean calculo;
	@Getter
	@Setter
	private Integer puesto;
	@Getter
	@Setter
	private CatalogoCIUU catalago;
}
