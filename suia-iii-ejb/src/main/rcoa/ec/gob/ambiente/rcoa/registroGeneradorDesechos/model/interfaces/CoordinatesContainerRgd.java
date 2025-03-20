package ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.interfaces;

import java.util.List;

import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.CoordenadaRgdCoa;
import ec.gob.ambiente.suia.domain.TipoForma;

public interface CoordinatesContainerRgd {
	
	public TipoForma getTipoForma();
	
	public List<CoordenadaRgdCoa> getCoordenadas();

}
