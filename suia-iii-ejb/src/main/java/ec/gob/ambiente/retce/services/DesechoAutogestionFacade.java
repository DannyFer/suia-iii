package ec.gob.ambiente.retce.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.retce.model.DesechoAutogestion;
import ec.gob.ambiente.retce.model.DesechoEliminacionAutogestion;
import ec.gob.ambiente.retce.model.DesechoGeneradoEliminacion;
import ec.gob.ambiente.retce.model.SubstanciasRetce;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class DesechoAutogestionFacade {

	@EJB
	private CrudServiceBean crudServiceBean;

	public void guardarDesechosAutogestion(List<DesechoAutogestion> desechoAutogestion) {
		crudServiceBean.saveOrUpdate(desechoAutogestion);
	}
	
	public void guardarDesechoAutogestion(DesechoAutogestion desechoAutogestion) {
		crudServiceBean.saveOrUpdate(desechoAutogestion);
	}
	
	public void guardarDesechoEliminacionAutogestion(DesechoEliminacionAutogestion desechoAutogestion) {
		crudServiceBean.saveOrUpdate(desechoAutogestion);
	}
	
	public void guardarDesechosEliminacionAutogestion(List<DesechoEliminacionAutogestion> desechosAutogestion) {
		crudServiceBean.saveOrUpdate(desechosAutogestion);
	}
	
	public void guardarDesechoGeneradoEliminacionAutogestion(List<DesechoGeneradoEliminacion> desechoAutogestion) {
		crudServiceBean.saveOrUpdate(desechoAutogestion);
	}
	
	public void eliminarDesechosAutogestion(List<DesechoAutogestion> listaDesechosAutogestionEliminar) {
		for (DesechoAutogestion desechoAutogestion : listaDesechosAutogestionEliminar) {
			for (DesechoEliminacionAutogestion desechoPeligroso : desechoAutogestion.getListaDesechosEliminacionAutogestion()) {
				desechoPeligroso.setEstado(false);
				
				for (DesechoGeneradoEliminacion desechoGenerado : desechoPeligroso.getListaDesechosGeneradosPorEliminacion()) {
					desechoGenerado.setEstado(false);
				}
				crudServiceBean.saveOrUpdate(desechoPeligroso.getListaDesechosGeneradosPorEliminacion());
			}
			
			for(SubstanciasRetce sustancia : desechoAutogestion.getListaSustanciasRetce()) {
				sustancia.setEstado(false);
				crudServiceBean.saveOrUpdate(sustancia);
			}
			
			desechoAutogestion.setEstado(false);
			crudServiceBean.saveOrUpdate(desechoAutogestion.getListaDesechosEliminacionAutogestion());
		}
		
		crudServiceBean.saveOrUpdate(listaDesechosAutogestionEliminar);
	}
	
	public void eliminarDesechosEliminacionAutogestion(List<DesechoEliminacionAutogestion> listaDesechosEliminacionAutogestionEliminar) {
		for (DesechoEliminacionAutogestion desechoPeligroso : listaDesechosEliminacionAutogestionEliminar) {			
			for (DesechoGeneradoEliminacion desechoGenerado : desechoPeligroso.getListaDesechosGeneradosPorEliminacion()) {
				desechoGenerado.setEstado(false);
			}
			crudServiceBean.saveOrUpdate(desechoPeligroso.getListaDesechosGeneradosPorEliminacion());
		}
		crudServiceBean.saveOrUpdate(listaDesechosEliminacionAutogestionEliminar);
	}
	
	public void eliminarDesechosGenerados(List<DesechoGeneradoEliminacion> listaDesechosGeneradosEliminar) {
		crudServiceBean.saveOrUpdate(listaDesechosGeneradosEliminar);
	}

	@SuppressWarnings("unchecked") 
	public List<DesechoAutogestion> getListaDesechosAutogestion(Integer idRgdRetce) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idRgdRetce", idRgdRetce);

		try {
			List<DesechoAutogestion> lista = (List<DesechoAutogestion>) crudServiceBean
					.findByNamedQuery(
							DesechoAutogestion.GET_LISTA_DESECHOS_AUTOGESTION_POR_RGDRETCE,
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
	public DesechoGeneradoEliminacion getDesechoGeneradoHistorial(Integer idDesecho, Integer nroObservacion) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idDesecho", idDesecho);
		parameters.put("nroObservacion", nroObservacion);

		try {
			List<DesechoGeneradoEliminacion> lista = (List<DesechoGeneradoEliminacion>) crudServiceBean
					.findByNamedQuery(DesechoGeneradoEliminacion.GET_HISTORIAL_POR_ID_NRO_REVISION, parameters);

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
	public DesechoEliminacionAutogestion getDesechoEliminacionHistorial(Integer idDesecho, Integer nroObservacion) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idDesecho", idDesecho);
		parameters.put("nroObservacion", nroObservacion);

		try {
			List<DesechoEliminacionAutogestion> lista = (List<DesechoEliminacionAutogestion>) crudServiceBean
					.findByNamedQuery(DesechoEliminacionAutogestion.GET_HISTORIAL_POR_ID_NRO_OBSERVACION, parameters);

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
	public DesechoAutogestion getDesechoAutogestionHistorial(Integer idDesecho, Integer nroObservacion) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idDesecho", idDesecho);
		parameters.put("nroObservacion", nroObservacion);

		try {
			List<DesechoAutogestion> lista = (List<DesechoAutogestion>) crudServiceBean
					.findByNamedQuery(DesechoAutogestion.GET_HISTORIAL_POR_ID, parameters);

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
	public List<DesechoEliminacionAutogestion> getDesechoEliminacionHistorial(Integer idDesecho) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idDesecho", idDesecho);

		try {
			List<DesechoEliminacionAutogestion> lista = (List<DesechoEliminacionAutogestion>) crudServiceBean
					.findByNamedQuery(DesechoEliminacionAutogestion.GET_HISTORIAL_POR_ID, parameters);

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
	public List<DesechoGeneradoEliminacion> getHistorialDesechoGenerado(Integer idDesecho) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idDesecho", idDesecho);

		try {
			List<DesechoGeneradoEliminacion> lista = (List<DesechoGeneradoEliminacion>) crudServiceBean
					.findByNamedQuery(DesechoGeneradoEliminacion.GET_HISTORIAL_POR_ID, parameters);

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
	public List<DesechoAutogestion> getListaDesechosAutogestionEliminados(Integer idRgdRetce) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idRgdRetce", idRgdRetce);

		try {
			List<DesechoAutogestion> lista = (List<DesechoAutogestion>) crudServiceBean
					.findByNamedQuery(
							DesechoAutogestion.GET_HISTORIAL_DESECHOS_ELIMINADOS_POR_RGDRETCE,
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
	public List<DesechoEliminacionAutogestion> getDesechosEliminacionPorDesecho(Integer idDesecho) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idDesecho", idDesecho);

		try {
			List<DesechoEliminacionAutogestion> lista = (List<DesechoEliminacionAutogestion>) crudServiceBean
					.findByNamedQuery(DesechoEliminacionAutogestion.GET_ELIMINADOS_POR_ID_DESECHO_AUTOGESTION, parameters);

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
	public List<DesechoGeneradoEliminacion> getDesechoGeneradoPorEliminacion(Integer idTipoEliminacion) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idTipo", idTipoEliminacion);

		try {
			List<DesechoGeneradoEliminacion> lista = (List<DesechoGeneradoEliminacion>) crudServiceBean
					.findByNamedQuery(DesechoGeneradoEliminacion.GET_ELIMINADOS_POR_ID_ELIMINACION, parameters);

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
