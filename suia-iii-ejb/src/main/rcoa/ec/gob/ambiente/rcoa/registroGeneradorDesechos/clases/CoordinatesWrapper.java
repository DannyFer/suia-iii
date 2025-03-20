package ec.gob.ambiente.rcoa.registroGeneradorDesechos.clases;

import java.util.ArrayList;
import java.util.List;

import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.CoordenadaRgdCoa;
import ec.gob.ambiente.suia.domain.TipoForma;
import lombok.Getter;
import lombok.Setter;

public class CoordinatesWrapper {
	
	@Setter
	private List<CoordenadaRgdCoa> coordenadas;
	
	@Getter
	@Setter
	private TipoForma tipoForma;
	
	public CoordinatesWrapper(List<CoordenadaRgdCoa> coordenadas, TipoForma tipoForma){
		super();
		this.coordenadas = coordenadas;
		this.tipoForma = tipoForma;
	}

	public CoordinatesWrapper() {		
	}
	
	public List<CoordenadaRgdCoa> getCoordenadas(){
		return coordenadas == null ? coordenadas = new ArrayList<CoordenadaRgdCoa>() : coordenadas;
	}

}
