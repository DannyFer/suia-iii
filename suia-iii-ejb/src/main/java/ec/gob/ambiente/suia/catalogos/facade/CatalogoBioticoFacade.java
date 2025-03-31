package ec.gob.ambiente.suia.catalogos.facade;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.catalogos.service.CatalogoBioticoService;
import ec.gob.ambiente.suia.domain.CatalogoGeneralBiotico;

@Stateless
public class CatalogoBioticoFacade {

	@EJB
	private CatalogoBioticoService BioticoService;

	public List<CatalogoGeneralBiotico> obtenerListaBioticoTipo(
			String codigoCategoria) {
		return BioticoService.obtenerListaBioticoTipo(codigoCategoria);
	}
	public List<CatalogoGeneralBiotico> obtenerListaBioticoTipo(
			List<String> codigoCategoria) {
		return BioticoService.obtenerListaBioticoTipo(codigoCategoria);
	}

	public List<CatalogoGeneralBiotico> obtenerListaBioticoIdTipo(Integer codigo) {
		return BioticoService.obtenerListaBioticoIdTipo(codigo);
	}
	
	public CatalogoGeneralBiotico obtenerIdOtroPorCatergoria(String codigoCategoria){
		return BioticoService.obtenerIdOtroPorCategoria(codigoCategoria);
	}
        public List<CatalogoGeneralBiotico> obtenerListaBioticoIds(String ids) {
            return BioticoService.obtenerListaBioticoIds(ids);
        }  
        
    public List<CatalogoGeneralBiotico> obtenerListaBioticoIdPadre(Integer id){
    	return BioticoService.obtenerListaBioticoIdPadre(id);
    }
}
