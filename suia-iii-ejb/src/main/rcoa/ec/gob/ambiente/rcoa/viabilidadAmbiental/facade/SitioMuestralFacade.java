package ec.gob.ambiente.rcoa.viabilidadAmbiental.facade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.SitioMuestral;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class SitioMuestralFacade {

	@EJB
	private CrudServiceBean crudServiceBean;

	public SitioMuestral guardar(SitioMuestral sitio) {			
		return crudServiceBean.saveOrUpdate(sitio);
	}

	@SuppressWarnings("unchecked")
	public List<SitioMuestral> getSitiosPorInformeTipo(Integer idInforme, Integer tipo) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idInforme", idInforme);
		parameters.put("tipo", tipo);

		try {
			List<SitioMuestral> lista = (List<SitioMuestral>) crudServiceBean
					.findByNamedQuery(SitioMuestral.GET_LISTA_SITIOS_POR_INFORME_TIPO, parameters);

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
