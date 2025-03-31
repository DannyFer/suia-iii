package ec.gob.ambiente.suia.eia.pma.facade;

import java.io.Serializable;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.domain.TipoPlanManejoAmbiental;
import ec.gob.ambiente.suia.eia.pma.service.TipoPlanManejoAmbientalService;

@Stateless
public class TipoPlanManejoAmbientalFacade implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3609872455745014706L;
	
	@EJB
	private TipoPlanManejoAmbientalService tipoPlanManejoAmbientalService;
	
	
	public List<TipoPlanManejoAmbiental> obtenerListaTipoPlanManejoAmbiental() throws Exception {
		return tipoPlanManejoAmbientalService.obtenerListaTipoPlanManejoAmbiental();
	}

	public TipoPlanManejoAmbiental obtenerTipoPlanManejoAmbiental(Integer id) throws Exception {
		return tipoPlanManejoAmbientalService.obtenerTipoPlanManejoAmbiental(id);
	}
	
	public List<TipoPlanManejoAmbiental> obtenerListaTipoPlanManejoAmbientalPorTipo(String tipo) throws Exception {
		return tipoPlanManejoAmbientalService.obtenerTipoPlanManejoAmbientalPorTipo(tipo);
	}

}
