package ec.gob.ambiente.suia.catalogos.facade;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.catalogos.service.CatalogoAnexoService;
import ec.gob.ambiente.suia.domain.CatalogoAnexo;

@Stateless
public class CatalogoAnexoFacade {

	@EJB
	private CatalogoAnexoService AnexoService;

	@Deprecated
	public List<CatalogoAnexo> obtenerListaAnexo() {
		return AnexoService.obtenerListaAnexo();
	}
	
	public List<CatalogoAnexo> obtenerListaAnexoParents() {
		return AnexoService.obtenerListaAnexoParents();
	}

}
