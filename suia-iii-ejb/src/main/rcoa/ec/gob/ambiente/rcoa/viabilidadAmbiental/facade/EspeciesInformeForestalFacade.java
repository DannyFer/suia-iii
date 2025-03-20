package ec.gob.ambiente.rcoa.viabilidadAmbiental.facade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.EspeciesInformeForestal;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class EspeciesInformeForestalFacade {

	@EJB
	private CrudServiceBean crudServiceBean;

	public EspeciesInformeForestal guardar(EspeciesInformeForestal especie) {			
		return crudServiceBean.saveOrUpdate(especie);
	}

	@SuppressWarnings("unchecked")
	public List<EspeciesInformeForestal> getListaPorSitioMuestral(Integer idSitio) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idSitio", idSitio);

		try {
			List<EspeciesInformeForestal> lista = (List<EspeciesInformeForestal>) crudServiceBean
					.findByNamedQuery(EspeciesInformeForestal.GET_LISTA_ESPECIES_POR_SITIO, parameters);

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
