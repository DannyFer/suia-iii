package ec.gob.ambiente.rcoa.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import ec.gob.ambiente.rcoa.model.CoordenadasProyecto;
import ec.gob.ambiente.suia.domain.TipoForma;
import lombok.Getter;
import lombok.Setter;

public class CoordendasPoligonos {
	@Setter
	private List<CoordenadasProyecto> coordenadas;

	@Getter
	@Setter
	private TipoForma tipoForma;
	
	@Getter
	@Setter
	private BigDecimal superficie;
	
	@Getter
	@Setter
	private String cadenaCoordenadas;

	public CoordendasPoligonos(List<CoordenadasProyecto> coordenadas, TipoForma tipoForma) {
		super();
		this.coordenadas = coordenadas;
		this.tipoForma = tipoForma;
	}
	
	public List<CoordenadasProyecto> getCoordenadas() {
		return coordenadas == null ? coordenadas = new ArrayList<CoordenadasProyecto>() : coordenadas;
	}

	public CoordendasPoligonos() {
	}
}
