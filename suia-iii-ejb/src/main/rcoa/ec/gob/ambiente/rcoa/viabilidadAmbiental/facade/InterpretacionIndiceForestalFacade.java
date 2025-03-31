package ec.gob.ambiente.rcoa.viabilidadAmbiental.facade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.InterpretacionIndiceForestal;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class InterpretacionIndiceForestalFacade {

	@EJB
	private CrudServiceBean crudServiceBean;

	public void guardar(InterpretacionIndiceForestal interpretacion) {			
		crudServiceBean.saveOrUpdate(interpretacion);
	}

	@SuppressWarnings("unchecked")
	public List<InterpretacionIndiceForestal> getListaPorInformeTipoSitio(Integer idInforme, Integer tipoSitio) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idInforme", idInforme);
		parameters.put("tipoSitio", tipoSitio);

		try {
			List<InterpretacionIndiceForestal> lista = (List<InterpretacionIndiceForestal>) crudServiceBean
					.findByNamedQuery(InterpretacionIndiceForestal.GET_LISTA_POR_INFORME_TIPO_SITIO, parameters);

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
