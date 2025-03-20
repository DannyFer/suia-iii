package ec.gob.ambiente.suia.catalogos.facade;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.catalogos.service.CatalogoSocialService;
import ec.gob.ambiente.suia.domain.CatalogoGeneralSocial;

@Stateless
public class CatalogoSocialFacade {

	@EJB
	private CatalogoSocialService SocialService;

	public List<CatalogoGeneralSocial> obtenerListaSocialTipo(
			String codigoCategoria) {
		return SocialService.obtenerListaSocialTipo(codigoCategoria);
	}

	public List<CatalogoGeneralSocial> obtenerListaSocialTipo(
			List<String> codigoCategoria) {
		return SocialService.obtenerListaSocialTipo(codigoCategoria);
	}

	public List<CatalogoGeneralSocial> obtenerListaSocialIdTipo(Integer codigo) {
		return SocialService.obtenerListaSocialIdTipo(codigo);
	}

	public List<CatalogoGeneralSocial> obtenerListaSocialIds(String ids) {
		return SocialService.obtenerListaSocialIds(ids);
	}

	public List<CatalogoGeneralSocial> obtenerListaSocialPorFichaPorTipoCatalogo(
			Integer idFicha, Integer idTipoCatalogo) {
		return SocialService.obtenerListaSocialPorFichaPorTipoCatalogo(idFicha,
				idTipoCatalogo);
	}
}
