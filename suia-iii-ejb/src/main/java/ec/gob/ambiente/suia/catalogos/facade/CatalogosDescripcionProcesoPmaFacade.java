package ec.gob.ambiente.suia.catalogos.facade;

import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.catalogos.service.CatalogosDescripcionProcesoPmaService;

@Stateless
public class CatalogosDescripcionProcesoPmaFacade {

	@EJB
	private CatalogosDescripcionProcesoPmaService catalogosDescripcionProcesoPmaService;

	public Map<String, List<?>> getCatalogos(List<String> aplicaCatalogoList, List<Integer> catalogoCategoriaFaseIdList, Integer idActividadEspecial, String codigoSubsector) throws Exception{
		return catalogosDescripcionProcesoPmaService.getCatalogos(aplicaCatalogoList, catalogoCategoriaFaseIdList, idActividadEspecial, codigoSubsector);
	}

}
