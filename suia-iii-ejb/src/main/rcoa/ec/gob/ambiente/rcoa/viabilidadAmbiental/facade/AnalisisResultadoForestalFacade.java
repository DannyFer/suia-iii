package ec.gob.ambiente.rcoa.viabilidadAmbiental.facade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.AnalisisResultadoForestal;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class AnalisisResultadoForestalFacade {

	@EJB
	private CrudServiceBean crudServiceBean;

	public AnalisisResultadoForestal guardar(AnalisisResultadoForestal especie) {			
		return crudServiceBean.saveOrUpdate(especie);
	}

	@SuppressWarnings("unchecked")
	public AnalisisResultadoForestal getEspecieMayorValor(Integer idSitio, Integer tipo) {
		String columnaOrden = "id";
		switch (tipo) {
			case 1:
				columnaOrden = "valorDnr";
				break;
			case 2:
				columnaOrden = "valorDmr";
				break;
			case 3:
				columnaOrden = "valorIvi";
				break;
			default:
				break;
			}

		String querySql = "Select a from AnalisisResultadoForestal a where a.idSitio=:idSitio and a.estado=true order by a." + columnaOrden + " desc";

		Query sql = crudServiceBean.getEntityManager().createQuery(querySql);
		sql.setParameter("idSitio", idSitio);
		if (sql.getResultList().size() > 0) {
			List<AnalisisResultadoForestal> analisis = (List<AnalisisResultadoForestal>) sql.getResultList();
			return analisis.get(0);
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	public List<AnalisisResultadoForestal> getListaPorSitio(Integer idSitio) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idSitio", idSitio);

		try {
			List<AnalisisResultadoForestal> lista = (List<AnalisisResultadoForestal>) crudServiceBean
					.findByNamedQuery(AnalisisResultadoForestal.GET_LISTA_POR_SITIO, parameters);

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
