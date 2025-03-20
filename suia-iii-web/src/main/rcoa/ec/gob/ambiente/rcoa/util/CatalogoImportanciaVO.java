package ec.gob.ambiente.rcoa.util;

import ec.gob.ambiente.rcoa.model.CatalogoCIUU;
import ec.gob.ambiente.rcoa.model.CombinacionSubActividades;
import ec.gob.ambiente.rcoa.model.SubActividades;
import lombok.Getter;
import lombok.Setter;

public class CatalogoImportanciaVO {
	
	@Getter
	@Setter
	private CatalogoCIUU catalogo;
	@Getter
	@Setter
	private Integer tipoPermiso;
	@Getter
	@Setter
	private Integer importancia;
	@Getter
	@Setter
	private String  wf;
	@Getter
	@Setter
	private SubActividades subActividades;
	@Getter
	@Setter
	private CombinacionSubActividades combinacionSubActividades;

}
