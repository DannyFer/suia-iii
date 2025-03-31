package ec.gob.ambiente.rcoa.viabilidadAmbiental.facade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.UnidadHidrografica;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class UnidadHidrograficaFacade {

	@EJB
	private CrudServiceBean crudServiceBean;

	public void guardar(UnidadHidrografica unidad) {			
		crudServiceBean.saveOrUpdate(unidad);
	}

	@SuppressWarnings("unchecked")
	public List<UnidadHidrografica> getListaPorInformeInspeccion(Integer idInforme) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idInformeInspeccion", idInforme);

		try {
			List<UnidadHidrografica> lista = (List<UnidadHidrografica>) crudServiceBean
					.findByNamedQuery(UnidadHidrografica.GET_LISTA_POR_INFORME_INSPECCION, parameters);

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
	public List<UnidadHidrografica> getListaPorInformeFactibilidad(Integer idInforme) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idInformeFactibilidad", idInforme);

		try {
			List<UnidadHidrografica> lista = (List<UnidadHidrografica>) crudServiceBean
					.findByNamedQuery(UnidadHidrografica.GET_LISTA_POR_INFORME_FACTIBILIDAD, parameters);

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
