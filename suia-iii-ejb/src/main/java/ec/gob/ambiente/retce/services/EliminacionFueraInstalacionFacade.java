package ec.gob.ambiente.retce.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.retce.model.EliminacionFueraInstalacion;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class EliminacionFueraInstalacionFacade {

	@EJB
	private CrudServiceBean crudServiceBean;

	public void guardarEliminacion(List<EliminacionFueraInstalacion> eliminacion) {
		crudServiceBean.saveOrUpdate(eliminacion);
	}
	
	public void eliminarEliminacionFueraInstalacion(List<EliminacionFueraInstalacion> eliminacion) {
		crudServiceBean.saveOrUpdate(eliminacion);
	}
	
	@SuppressWarnings("unchecked") 
	public List<EliminacionFueraInstalacion> getEliminacionPorRgdRetce(Integer idRgdRetce) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idRgdRetce", idRgdRetce);

		try {
			List<EliminacionFueraInstalacion> lista = (List<EliminacionFueraInstalacion>) crudServiceBean
					.findByNamedQuery(EliminacionFueraInstalacion.GET_POR_RGDRETCE,
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
	public EliminacionFueraInstalacion getEliminacionFueraInstalacionHistorial(Integer idDesecho, Integer nroObservacion) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idDesecho", idDesecho);
		parameters.put("nroObservacion", nroObservacion);

		try {
			List<EliminacionFueraInstalacion> lista = (List<EliminacionFueraInstalacion>) crudServiceBean
					.findByNamedQuery(EliminacionFueraInstalacion.GET_HISTORIAL_POR_ID_NRO_OBSERVACION, parameters);

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
	public List<EliminacionFueraInstalacion> getHistorialEliminacionFueraInstalacion(Integer idDesecho) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idDesecho", idDesecho);

		try {
			List<EliminacionFueraInstalacion> lista = (List<EliminacionFueraInstalacion>) crudServiceBean
					.findByNamedQuery(EliminacionFueraInstalacion.GET_HISTORIAL_POR_ID, parameters);

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
	public List<EliminacionFueraInstalacion> getDesechosEliminadosPorRgdRetce(Integer idRgdRetce) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idRgdRetce", idRgdRetce);

		try {
			List<EliminacionFueraInstalacion> lista = (List<EliminacionFueraInstalacion>) crudServiceBean
					.findByNamedQuery(EliminacionFueraInstalacion.GET_HISTORIAL_DESECHOS_ELIMINADOS_POR_RGDRETCE,
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
