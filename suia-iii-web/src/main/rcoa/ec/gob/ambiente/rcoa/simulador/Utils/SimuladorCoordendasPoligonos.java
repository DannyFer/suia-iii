package ec.gob.ambiente.rcoa.simulador.Utils;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import ec.gob.ambiente.rcoa.simulador.model.SimuladorCoordenadasProyecto;
import ec.gob.ambiente.suia.domain.TipoForma;
import lombok.Getter;
import lombok.Setter;

public class SimuladorCoordendasPoligonos {
	@Setter
	private List<SimuladorCoordenadasProyecto> coordenadas;

	@Getter
	@Setter
	private TipoForma tipoForma;
	
	@Getter
	@Setter
	private BigDecimal superficie;

	public SimuladorCoordendasPoligonos(List<SimuladorCoordenadasProyecto> coordenadas, TipoForma tipoForma) {
		super();
		this.coordenadas = coordenadas;
		this.tipoForma = tipoForma;
	}
	
	public List<SimuladorCoordenadasProyecto> getCoordenadas() {
		return coordenadas == null ? coordenadas = new ArrayList<SimuladorCoordenadasProyecto>() : coordenadas;
	}

	public SimuladorCoordendasPoligonos() {
	}
}
