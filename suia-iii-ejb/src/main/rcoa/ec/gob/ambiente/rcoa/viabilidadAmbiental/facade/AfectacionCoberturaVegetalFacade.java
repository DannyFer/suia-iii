package ec.gob.ambiente.rcoa.viabilidadAmbiental.facade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.AfectacionCoberturaVegetal;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class AfectacionCoberturaVegetalFacade {

	@EJB
	private CrudServiceBean crudServiceBean;

	public void guardar(AfectacionCoberturaVegetal afectacion) {			
		crudServiceBean.saveOrUpdate(afectacion);
	}

	@SuppressWarnings("unchecked")
	public List<AfectacionCoberturaVegetal> getListaPorInformeTipo(Integer idInforme, Integer tipo) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idInforme", idInforme);
		parameters.put("tipo", tipo);

		try {
			List<AfectacionCoberturaVegetal> lista = (List<AfectacionCoberturaVegetal>) crudServiceBean
					.findByNamedQuery(AfectacionCoberturaVegetal.GET_LISTA_POR_INFORME_TIPO, parameters);

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
