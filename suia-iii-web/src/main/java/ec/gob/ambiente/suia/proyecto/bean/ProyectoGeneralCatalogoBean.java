package ec.gob.ambiente.suia.proyecto.bean;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.ProyectoGeneralCatalogo;

public class ProyectoGeneralCatalogoBean implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Getter
	@Setter
	private List<ProyectoGeneralCatalogo> proyectoGeneralCatalogos;
	
	
	
}
