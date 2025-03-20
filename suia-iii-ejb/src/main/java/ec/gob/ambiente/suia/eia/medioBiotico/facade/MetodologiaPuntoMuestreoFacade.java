package ec.gob.ambiente.suia.eia.medioBiotico.facade;

import ec.gob.ambiente.suia.domain.MetodologiaPuntoMuestreo;
import ec.gob.ambiente.suia.eia.medioBiotico.service.MetodologiaPuntoMuestreoService;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.List;

@Stateless
public class MetodologiaPuntoMuestreoFacade {
	@EJB
	private MetodologiaPuntoMuestreoService metodologiaPuntoMuestreoService;
   public List<MetodologiaPuntoMuestreo> listarMetodologias(boolean flora) {
        return  metodologiaPuntoMuestreoService.listarMetodologias(flora);
    }
}
