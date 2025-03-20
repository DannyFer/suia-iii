package ec.gob.ambiente.rcoa.viabilidadAmbiental.facade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.EspeciesInspeccionForestal;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class EspeciesInspeccionForestalFacade {

	@EJB
	private CrudServiceBean crudServiceBean;

	public void guardarEspecie(EspeciesInspeccionForestal especie) {			
		crudServiceBean.saveOrUpdate(especie);
	}

	@SuppressWarnings("unchecked")
	public List<EspeciesInspeccionForestal> getListaPorInformeTipo(Integer idInforme, Integer tipo) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idInforme", idInforme);
		parameters.put("tipo", tipo);

		try {
			List<EspeciesInspeccionForestal> lista = (List<EspeciesInspeccionForestal>) crudServiceBean
					.findByNamedQuery(EspeciesInspeccionForestal.GET_LISTA_ESPECIES_POR_INFORME_TIPO, parameters);

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
