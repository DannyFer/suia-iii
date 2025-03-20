package ec.gob.ambiente.rcoa.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.rcoa.model.SubActividades;

public class SubactividadDto {

	@Getter
    @Setter
    private Integer bloque;
	
	@Getter
    @Setter
    private List<SubActividades> subActividades;

    
}
