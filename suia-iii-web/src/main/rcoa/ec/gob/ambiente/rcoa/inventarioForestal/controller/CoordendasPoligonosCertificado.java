package ec.gob.ambiente.rcoa.inventarioForestal.controller;

import java.util.ArrayList;
import java.util.List;

import ec.gob.ambiente.rcoa.inventarioForestal.model.CoordenadasInventarioForestalCertificado;
import ec.gob.ambiente.suia.domain.TipoForma;
import lombok.Getter;
import lombok.Setter;

public class CoordendasPoligonosCertificado {
	@Setter
	private List<CoordenadasInventarioForestalCertificado> coordenadas;

	@Getter
	@Setter
	private TipoForma tipoForma;

	public CoordendasPoligonosCertificado(List<CoordenadasInventarioForestalCertificado> coordenadas, TipoForma tipoForma) {
		super();
		this.coordenadas = coordenadas;
		this.tipoForma = tipoForma;
	}
	
	public List<CoordenadasInventarioForestalCertificado> getCoordenadas() {
		return coordenadas == null ? coordenadas = new ArrayList<CoordenadasInventarioForestalCertificado>() : coordenadas;
	}

	public CoordendasPoligonosCertificado() {
	}
}
