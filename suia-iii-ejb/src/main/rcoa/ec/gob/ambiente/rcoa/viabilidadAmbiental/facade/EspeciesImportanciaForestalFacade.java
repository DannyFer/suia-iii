package ec.gob.ambiente.rcoa.viabilidadAmbiental.facade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.EspeciesImportanciaForestal;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class EspeciesImportanciaForestalFacade {

	@EJB
	private CrudServiceBean crudServiceBean;

	public EspeciesImportanciaForestal guardar(EspeciesImportanciaForestal especie) {			
		return crudServiceBean.saveOrUpdate(especie);
	}

	@SuppressWarnings("unchecked")
	public List<EspeciesImportanciaForestal> getListaPorInforme(Integer idInformeFactibilidad) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idInformeFactibilidad", idInformeFactibilidad);

		try {
			List<EspeciesImportanciaForestal> lista = (List<EspeciesImportanciaForestal>) crudServiceBean
					.findByNamedQuery(EspeciesImportanciaForestal.GET_LISTA_POR_INFORME, parameters);

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
