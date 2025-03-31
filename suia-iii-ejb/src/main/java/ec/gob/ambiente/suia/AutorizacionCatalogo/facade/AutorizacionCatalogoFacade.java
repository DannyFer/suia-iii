package ec.gob.ambiente.suia.AutorizacionCatalogo.facade;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.AutorizacionCatalogo.service.AutorizaionCatalogoService;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.AreasAutorizadasCatalogo;
import ec.gob.ambiente.suia.domain.CatalogoCategoriaSistema;

@Stateless
public class AutorizacionCatalogoFacade {
	@EJB
    private AutorizaionCatalogoService autorizaionCatalogoService;
	
	
	public AreasAutorizadasCatalogo getaAreasAutorizadasCatalogo(CatalogoCategoriaSistema cataId,
			Area areaId) {
		return autorizaionCatalogoService.getAreasAutorizadasCatalogo(cataId, areaId);

	}
	 
}
