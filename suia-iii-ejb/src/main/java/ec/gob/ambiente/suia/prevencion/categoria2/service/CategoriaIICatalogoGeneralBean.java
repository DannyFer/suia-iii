package ec.gob.ambiente.suia.prevencion.categoria2.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.CategoriaIICatalogoGeneral;

/**
 * 
 * @author Frank Torres
 * 
 */
@Stateless
public class CategoriaIICatalogoGeneralBean {

	@EJB
	private CrudServiceBean crudServiceBean;

	public void guardarCatalogoGeneralFichaAmbiental(
			CategoriaIICatalogoGeneral categoriaIICatalogoGeneral) {
		crudServiceBean.saveOrUpdate(categoriaIICatalogoGeneral);
	}

	@SuppressWarnings("unchecked")
	public List<CategoriaIICatalogoGeneral> getCatalogoGeneralPorIdProyecto(
			String idProyecto) {
		Map<String, Object> parameters = new ConcurrentHashMap<String, Object>();
		parameters.put("idProyecto", idProyecto);
		try {
			List<CategoriaIICatalogoGeneral> result = (List<CategoriaIICatalogoGeneral>) crudServiceBean
					.findByNamedQuery(
							CategoriaIICatalogoGeneral.FIND_BY_PROJECT,
							parameters);

			return result;
		} catch (Exception e) {
			return null;
		}
	}
}