package ec.gob.ambiente.rcoa.viabilidadAmbiental.facade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.CoordenadaForestal;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class CoordenadasForestalFacade {

	@EJB
	private CrudServiceBean crudServiceBean;
	
	public void guardarCoordenada(CoordenadaForestal coordenada) {			
		crudServiceBean.saveOrUpdate(coordenada);
	}

	@SuppressWarnings("unchecked")
	public List<CoordenadaForestal> getListaCoordenadasPorInforme( Integer idInforme) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idInforme", idInforme);

		try {
			List<CoordenadaForestal> lista = (List<CoordenadaForestal>) crudServiceBean
					.findByNamedQuery(CoordenadaForestal.GET_POR_INFORME, parameters);

			if (lista == null || lista.isEmpty()) {
				return null;
			} else {
				return lista;
			}
		} catch (Exception e) {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<CoordenadaForestal> getListaCoordenadasPorInformeOrder( Integer idInforme) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idInforme", idInforme);

		try {
			List<CoordenadaForestal> lista = (List<CoordenadaForestal>) crudServiceBean
					.findByNamedQuery(CoordenadaForestal.GET_POR_INFORME_ORDEN, parameters);

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
