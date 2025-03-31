package ec.gob.ambiente.suia.catalogos.facade;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.catalogos.service.CatalogoFisicoService;
import ec.gob.ambiente.suia.domain.CatalogoGeneralFisico;

@Stateless
public class CatalogoFisicoFacade {

	@EJB
	private CatalogoFisicoService fisicoService;

	public List<CatalogoGeneralFisico> obtenerListaFisicoTipo(
			String codigoCategoria) {
		return fisicoService.obtenerListaFisicoTipo(codigoCategoria);
	}

	public List<CatalogoGeneralFisico> obtenerListaFisicoTipo(
			List<String> codigoCategoria) {
		return fisicoService.obtenerListaFisicoTipo(codigoCategoria);
	}

	public List<CatalogoGeneralFisico> obtenerListaClima() {
		return fisicoService.obtenerListaClima();
	}

	public List<CatalogoGeneralFisico> obtenerListaTiposSuelo() {
		return fisicoService.obtenerListaTiposSuelo();
	}

	public List<CatalogoGeneralFisico> obtenerListaFisicoIdTipo(Integer codigo) {
		return fisicoService.obtenerListaFisicoIdTipo(codigo);
	}

	public List<CatalogoGeneralFisico> obtenerListaFisicoIds(String ids) {
		return fisicoService.obtenerListaFisicoIds(ids);
	}
}
