package ec.gob.ambiente.suia.tipoarea.facade;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.domain.TipoArea;
import ec.gob.ambiente.suia.tipoarea.service.TipoAreaService;

/**
 * 
 * @author frank torres
 */
@LocalBean
@Stateless
public class TipoAreaFacade {

	@EJB
	private TipoAreaService tipoAreaService;

	public TipoArea getTipoAreaPorSiglas(String siglas) {
		return tipoAreaService.getTipoAreaPorSiglas(siglas);

	}

}
