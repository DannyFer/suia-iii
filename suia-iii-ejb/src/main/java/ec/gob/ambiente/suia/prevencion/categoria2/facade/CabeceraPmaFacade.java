package ec.gob.ambiente.suia.prevencion.categoria2.facade;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.domain.CabeceraPma;
import ec.gob.ambiente.suia.domain.FichaAmbientalPma;
import ec.gob.ambiente.suia.prevencion.categoria2.service.CabeceraPmaServiceBean;
import ec.gob.ambiente.suia.prevencion.categoria2.service.CronogramaValoradoServiceBean;

/**
 * 
 * @author fernando.borja
 * 
 */
@Stateless
public class CabeceraPmaFacade {

	@EJB
	private CabeceraPmaServiceBean cabeceraBean;
	
	@EJB 
	private CronogramaValoradoServiceBean cronogramaBean;

	public List<CabeceraPma> getCabeceraPmaPorFicha(FichaAmbientalPma ficha) {
		return cabeceraBean.getCabeceraPmaPorFicha(ficha.getId());
	}

	public CabeceraPma getCabeceraPma(Integer id) {
		return cabeceraBean.getCabecera(id);
	}

}
