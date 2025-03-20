package ec.gob.ambiente.rcoa.viabilidadAmbiental.facade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.CabeceraFormulario;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class PreguntasFormularioFacade {

	@EJB
	private CrudServiceBean crudServiceBean;

	@SuppressWarnings("unchecked")
	public List<CabeceraFormulario> getListaCabecerasPorOrdenTipo(Integer orden, Integer tipo) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("orden", orden);
		parameters.put("tipo", tipo);

		try {
			List<CabeceraFormulario> lista = (List<CabeceraFormulario>) crudServiceBean
					.findByNamedQuery(CabeceraFormulario.GET_LISTA_PREGUNTAS_POR_ORDEN_TIPO, parameters);

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
	public List<CabeceraFormulario> getListaCabecerasPorTipo( Integer tipo) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("tipo", tipo);

		try {
			List<CabeceraFormulario> lista = (List<CabeceraFormulario>) crudServiceBean
					.findByNamedQuery(CabeceraFormulario.GET_LISTA_PREGUNTAS_POR_TIPO, parameters);

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
