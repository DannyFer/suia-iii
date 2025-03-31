package ec.gob.ambiente.rcoa.dto;

import java.math.BigDecimal;

import javax.ejb.Stateless;

import lombok.Getter;
import lombok.Setter;

@Stateless
public class EntityTablaReporteImportacionesVueRSQDTO {
	
	@Getter
	@Setter
	private Integer indice;
	
	@Getter
	@Setter
	private String numeroSolicitud, tipoSolicitud, fechaInicio,
	fechaFin, estado;
	
	
}
