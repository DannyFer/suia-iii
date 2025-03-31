package ec.gob.ambiente.rcoa.dto;

import java.math.BigDecimal;

import javax.ejb.Stateless;

import lombok.Getter;
import lombok.Setter;

@Stateless
public class EntityTablaFormularioImportacionVueRSQDTO {
	
	@Getter
	@Setter
	private String codigoSustancia, nombreSustancia,
	subPartida, ubicacionGeografica, tipoRecipiente;
	
	@Getter
	@Setter
	private BigDecimal pesoNeto, pesoBruto;
}
