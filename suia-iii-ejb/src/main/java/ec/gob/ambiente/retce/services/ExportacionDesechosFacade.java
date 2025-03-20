package ec.gob.ambiente.retce.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.retce.model.DesechoExportacion;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class ExportacionDesechosFacade {

	@EJB
	private CrudServiceBean crudServiceBean;

	public void guardarExportacionDesecho(List<DesechoExportacion> desechosExportacion) {
		crudServiceBean.saveOrUpdate(desechosExportacion);
	}
	
	public void guardarExportacionDesecho(DesechoExportacion desechoExportacion) {
		crudServiceBean.saveOrUpdate(desechoExportacion);
	}
	
	public void eliminarExportacionDesechos(List<DesechoExportacion> desechosExportacion) {
		crudServiceBean.saveOrUpdate(desechosExportacion);
	}

	@SuppressWarnings("unchecked")
	public List<DesechoExportacion> getDesechosExportacionPorRgdRetce(Integer idRgdRetce) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idRgdRetce", idRgdRetce);

		try {
			List<DesechoExportacion> lista = (List<DesechoExportacion>) crudServiceBean
					.findByNamedQuery(
							DesechoExportacion.GET_LISTA_DESECHOS_EXPORTACION_POR_RGDRETCE,
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
	public DesechoExportacion getDesechoExportacionHistorial(Integer idDesecho, Integer nroObservacion) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idDesecho", idDesecho);
		parameters.put("nroObservacion", nroObservacion);

		try {
			List<DesechoExportacion> lista = (List<DesechoExportacion>) crudServiceBean
					.findByNamedQuery(DesechoExportacion.GET_HISTORIAL_POR_ID_NRO_OBSERVACION, parameters);

			if (lista == null || lista.isEmpty()) {
				return null;
			} else {
				return lista.get(0);
			}
		} catch (Exception e) {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked") 
	public List<DesechoExportacion> getHistorialDesechoExportacion(Integer idDesecho) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idDesecho", idDesecho);

		try {
			List<DesechoExportacion> lista = (List<DesechoExportacion>) crudServiceBean
					.findByNamedQuery(DesechoExportacion.GET_HISTORIAL_POR_ID, parameters);

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
	public List<DesechoExportacion> getDesechosEliminadosPorRgdRetce(Integer idRgdRetce) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idRgdRetce", idRgdRetce);

		try {
			List<DesechoExportacion> lista = (List<DesechoExportacion>) crudServiceBean
					.findByNamedQuery(
							DesechoExportacion.GET_HISTORIAL_ELIMINADOS_POR_RGDRETCE,
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
}
