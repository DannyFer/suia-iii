package ec.gob.ambiente.suia.domain.interfaces;

import java.util.List;

import ec.gob.ambiente.suia.domain.Coordenada;
import ec.gob.ambiente.suia.domain.TipoForma;

public interface CoordinatesContainer {

	public TipoForma getTipoForma();

	public List<Coordenada> getCoordenadas();
}
