package ec.gob.ambiente.suia.catalogos.facade;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.catalogos.service.ClimaService;
import ec.gob.ambiente.suia.domain.CatalogoGeneralFisico;
import ec.gob.ambiente.suia.domain.CategoriaIICatalogoGeneralFisico;

@Stateless
public class ClimaFacade {

	@EJB
	private ClimaService climaService;

	public List<CategoriaIICatalogoGeneralFisico> climasSeleccionadosCategoriaII(
			Integer idProyecto) {
		return climaService.climasSeleccionadosCategoriaII(idProyecto);
	}

	public List<CatalogoGeneralFisico> obtenerListaClima() {
		return climaService.obtenerListaClima();
	}

}
