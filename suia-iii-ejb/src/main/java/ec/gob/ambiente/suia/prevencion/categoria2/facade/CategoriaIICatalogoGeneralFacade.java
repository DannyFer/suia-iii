package ec.gob.ambiente.suia.prevencion.categoria2.facade;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.domain.CategoriaIICatalogoGeneral;
import ec.gob.ambiente.suia.prevencion.categoria2.service.CategoriaIICatalogoGeneralBean;

/**
 * 
 * @author Frank Torres
 * 
 */
@Stateless
public class CategoriaIICatalogoGeneralFacade {

	@EJB
	private CategoriaIICatalogoGeneralBean categoriaIICatalogoGeneralBean;

	public void guardarCatalogoGeneralFichaAmbiental(
			CategoriaIICatalogoGeneral categoriaIICatalogoGeneral) {
		categoriaIICatalogoGeneralBean
				.guardarCatalogoGeneralFichaAmbiental(categoriaIICatalogoGeneral);
	}

	@SuppressWarnings("unchecked")
	public List<CategoriaIICatalogoGeneral> getCatalogoGeneralPorIdProyecto(
			String idProyecto) {
		return categoriaIICatalogoGeneralBean
				.getCatalogoGeneralPorIdProyecto(idProyecto);
	}
}