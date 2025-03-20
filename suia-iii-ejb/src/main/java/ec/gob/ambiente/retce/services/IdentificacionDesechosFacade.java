package ec.gob.ambiente.retce.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.retce.model.IdentificacionDesecho;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class IdentificacionDesechosFacade {

	@EJB
	private CrudServiceBean crudServiceBean;

	public void guardarIdentificacion(
			List<IdentificacionDesecho> identificaciones) {
		crudServiceBean.saveOrUpdate(identificaciones);
	}

	@SuppressWarnings("unchecked")
	public List<IdentificacionDesecho> getIdentificacionDesechosPorRgdRetce(Integer idRgdRetce) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idRgdRetce", idRgdRetce);

		try {
			List<IdentificacionDesecho> lista = (List<IdentificacionDesecho>) crudServiceBean
					.findByNamedQuery(
							IdentificacionDesecho.GET_LISTA_IDENTIFICACION_DESECHOS_POR_RGDRETCE,
							parameters);

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
	public void eliminarIdentificacionDesechosPorRgdRetce(Integer idRgdRetce) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idRgdRetce", idRgdRetce);

		try {
			List<IdentificacionDesecho> lista = (List<IdentificacionDesecho>) crudServiceBean
					.findByNamedQuery(
							IdentificacionDesecho.GET_LISTA_IDENTIFICACION_DESECHOS_POR_RGDRETCE,
							parameters);

			if (lista == null || lista.isEmpty()) {
			} else {
				for (IdentificacionDesecho identificacionDesecho : lista) {
					identificacionDesecho.setEstado(false);
					crudServiceBean.saveOrUpdate(identificacionDesecho);
				}
			}
		} catch (Exception e) {
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<IdentificacionDesecho> getIdentificacionDesechosHistorialPorRgdRetce(Integer idRgdRetce, Integer nroObservacion) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idRgdRetce", idRgdRetce);
		parameters.put("nroObservacion", nroObservacion);

		try {
			List<IdentificacionDesecho> lista = (List<IdentificacionDesecho>) crudServiceBean
					.findByNamedQuery(
							IdentificacionDesecho.GET_HISTORIAL_POR_RGDRETCE,
							parameters);

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
	public List<IdentificacionDesecho> getIdentificacionDesechosHistorialPorIdIdentificacion(Integer idIdentificacion) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idIdentificacion", idIdentificacion);

		try {
			List<IdentificacionDesecho> lista = (List<IdentificacionDesecho>) crudServiceBean
					.findByNamedQuery(IdentificacionDesecho.GET_HISTORIAL_POR_ID, parameters);

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
	public IdentificacionDesecho getIdentificacionDesechosPorRgdRetcePorDesecho(Integer idRgdRetce, Integer idDesecho) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idRgdRetce", idRgdRetce);
		parameters.put("idDesecho", idDesecho);
		try {
			List<IdentificacionDesecho> lista = (List<IdentificacionDesecho>) crudServiceBean
					.findByNamedQuery(
							IdentificacionDesecho.GET_POR_RGDRETCE_POR_DESECHO,
							parameters);

			if (lista == null || lista.isEmpty()) {
				return null;
			} else {
				return lista.get(0);
			}
		} catch (Exception e) {
			return null;
		}
	}
}
