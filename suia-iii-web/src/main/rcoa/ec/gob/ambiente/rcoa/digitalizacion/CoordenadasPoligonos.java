package ec.gob.ambiente.rcoa.digitalizacion;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.rcoa.digitalizacion.model.CoordenadaDigitalizacion;
import ec.gob.ambiente.suia.domain.TipoForma;

public class CoordenadasPoligonos {
	
	@Setter
	private List<CoordenadaDigitalizacion> coordenadas;
	
	@Getter
	@Setter
	private TipoForma tipoForma;
	
	@Getter
	@Setter
	private BigDecimal superficie;
	
	@Getter
	@Setter
	private String cadenaCoordenadas;

	public CoordenadasPoligonos(List<CoordenadaDigitalizacion> coordenadas,	TipoForma tipoForma) {
		super();
		this.coordenadas = coordenadas;
		this.tipoForma = tipoForma;
	}
	
	public List<CoordenadaDigitalizacion> getCoordenadas() {
		return coordenadas == null ? coordenadas = new ArrayList<CoordenadaDigitalizacion>() : coordenadas;
	}

	public CoordenadasPoligonos() {
		
	}
	
	

}
