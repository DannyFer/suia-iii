package ec.gob.ambiente.retce.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.retce.model.DisposicionFueraInstalacion;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class DisposicionFueraInstalacionFacade {

	@EJB
	private CrudServiceBean crudServiceBean;

	public void guardarListaDisposicion(List<DisposicionFueraInstalacion> listaDisposicion) {
		crudServiceBean.saveOrUpdate(listaDisposicion);
	}
	
	public void eliminarListaDisposicion(List<DisposicionFueraInstalacion> listaDisposicion) {
		crudServiceBean.saveOrUpdate(listaDisposicion);
	}
	
	
	@SuppressWarnings("unchecked") 
	public List<DisposicionFueraInstalacion> getDisposicionPorRgdRetce(Integer idRgdRetce) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idRgdRetce", idRgdRetce);

		try {
			List<DisposicionFueraInstalacion> lista = (List<DisposicionFueraInstalacion>) crudServiceBean
					.findByNamedQuery(DisposicionFueraInstalacion.GET_POR_RGDRETCE,
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
	public DisposicionFueraInstalacion getDisposicionFueraInstalacionHistorial(Integer idDesecho, Integer nroObservacion) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idDesecho", idDesecho);
		parameters.put("nroObservacion", nroObservacion);

		try {
			List<DisposicionFueraInstalacion> lista = (List<DisposicionFueraInstalacion>) crudServiceBean
					.findByNamedQuery(DisposicionFueraInstalacion.GET_HISTORIAL_POR_ID_NRO_OBSERVACION, parameters);

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
	public List<DisposicionFueraInstalacion> getDesechosEliminadosPorRgdRetce(Integer idRgdRetce) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idRgdRetce", idRgdRetce);

		try {
			List<DisposicionFueraInstalacion> lista = (List<DisposicionFueraInstalacion>) crudServiceBean
					.findByNamedQuery(DisposicionFueraInstalacion.GET_HISTORIAL_DESECHOS_ELIMINADOS_POR_RGDRETCE,
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
