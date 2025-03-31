package ec.gob.ambiente.suia.catalogos.facade;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.catalogos.service.CatalogoGeneralService;
import ec.gob.ambiente.suia.domain.CatalogoGeneral;
import ec.gob.ambiente.suia.domain.TipoCatalogo;

@Stateless
public class CatalogoGeneralFacade {

	@EJB
	CatalogoGeneralService catalogoGeneralService;

	public List<CatalogoGeneral> obtenerCatalogoXTipo(int tipoId)
			throws Exception {
		return catalogoGeneralService.obtenerCatalogoXTipo(tipoId);
	}

	public List<CatalogoGeneral> obtenerCatalogoXTipoOrdenado(int tipoId)
			throws Exception {
		return catalogoGeneralService.obtenerCatalogoXTipoOrdenado(tipoId);
	}

	public List<CatalogoGeneral> obtenerCatalogoXPadre(int padreId)
			throws Exception {
		return catalogoGeneralService.obtenerCatalogoXPadre(padreId);
	}

	public List<CatalogoGeneral> obtenerCatalogoXPadreOrdenado(int padreId)
			throws Exception {
		return catalogoGeneralService.obtenerCatalogoXPadreOrdenado(padreId);
	}

	public List<CatalogoGeneral> obtenerCatalogoXPadreOrdenadoDescripcion(int padreId)
			throws Exception {
		return catalogoGeneralService.obtenerCatalogoXPadreOrdenadoDescripcion(padreId);
	}

	public List<CatalogoGeneral> obtenerCatalogoXTipoXCodigo(int tipoId,
			String codigo) throws Exception {
		return catalogoGeneralService.obtenerCatalogoXTipoXCodigo(tipoId,
				codigo);
	}

	public CatalogoGeneral obtenerCatalogoXId(Integer id) throws Exception {
		return catalogoGeneralService.obtenerCatalogoXId(id);
	}

	public List<CatalogoGeneral> obtenerCatalogoXPadreXCodigo(int padreId,
			String codigo) throws Exception {
		return catalogoGeneralService.obtenerCatalogoXPadreXCodigo(padreId,
				codigo);
	}
	
	public List<CatalogoGeneral> obtenerCatalogoXTipoSeleccionado(int idCategoria, int idProyecto)
			throws Exception {
		return catalogoGeneralService.obtenerCatalogoXTipoSeleccionado(idCategoria, idProyecto);
	}
	
	public List<CatalogoGeneral> obtenerCatalogoMedioFisicoXIdProyecto(int idProyecto)
			throws Exception {
		return catalogoGeneralService.catalogoMedioFisicoXTipoSeleccionado(TipoCatalogo.MEDIO_FISICO, idProyecto);
	}
	
	public List<CatalogoGeneral> obtenerCatalogoMedioBioticoXIdProyecto(int idProyecto)
			throws Exception {
		return catalogoGeneralService.catalogoMedioBioticoXTipoSeleccionado(TipoCatalogo.MEDIO_BIOTICO, idProyecto);
	}
	
	public List<CatalogoGeneral> obtenerCatalogoMedioSocialXIdProyecto(int idProyecto)
			throws Exception {
		return catalogoGeneralService.catalogoMedioSocialXTipoSeleccionado(TipoCatalogo.MEDIO_SOCIAL, idProyecto);
	}

	public CatalogoGeneral obtenerXTipoXCodigo(int tipoId, String codigo) throws Exception {
		return catalogoGeneralService.obtenerXTipoXCodigo(tipoId, codigo);
	}
}
