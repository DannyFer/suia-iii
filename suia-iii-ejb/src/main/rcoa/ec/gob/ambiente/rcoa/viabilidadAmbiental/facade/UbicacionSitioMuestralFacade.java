package ec.gob.ambiente.rcoa.viabilidadAmbiental.facade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.UbicacionSitioMuestral;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class UbicacionSitioMuestralFacade {

	@EJB
	private CrudServiceBean crudServiceBean;

	public void guardar(UbicacionSitioMuestral ubicacion) {			
		crudServiceBean.saveOrUpdate(ubicacion);
	}

	@SuppressWarnings("unchecked")
	public List<UbicacionSitioMuestral> getListaPorInforme(Integer idSitio) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idSitio", idSitio);

		try {
			List<UbicacionSitioMuestral> lista = (List<UbicacionSitioMuestral>) crudServiceBean
					.findByNamedQuery(UbicacionSitioMuestral.GET_LISTA_POR_SITIO, parameters);

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
