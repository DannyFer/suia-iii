package ec.gob.ambiente.rcoa.viabilidadAmbiental.facade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.CoordenadaSitioMuestral;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class CoordenadaSitioMuestralFacade {

	@EJB
	private CrudServiceBean crudServiceBean;
	
	public void guardar(CoordenadaSitioMuestral coordenada) {			
		crudServiceBean.saveOrUpdate(coordenada);
	}

	@SuppressWarnings("unchecked")
	public List<CoordenadaSitioMuestral> getListaCoordenadasPorSitioOrder( Integer idSitio) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idSitio", idSitio);

		try {
			List<CoordenadaSitioMuestral> lista = (List<CoordenadaSitioMuestral>) crudServiceBean
					.findByNamedQuery(CoordenadaSitioMuestral.GET_POR_SITIO_ORDEN, parameters);

			if (lista == null || lista.isEmpty()) {
				return null;
			} else {
				return lista;
			}
		} catch (Exception e) {
			return null;
		}
	}
	
}
