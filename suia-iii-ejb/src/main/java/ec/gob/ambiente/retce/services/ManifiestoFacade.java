package ec.gob.ambiente.retce.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.retce.model.DetalleManifiestoDesecho;
import ec.gob.ambiente.retce.model.Manifiesto;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class ManifiestoFacade {

	@EJB
	private CrudServiceBean crudServiceBean;

	public void guardarManifiestos(List<Manifiesto> manifiestos) {
		crudServiceBean.saveOrUpdate(manifiestos);
	}
	
	public void guardarManifiesto(Manifiesto manifiesto) {
		crudServiceBean.saveOrUpdate(manifiesto);
	}
	
	public void guardarDetallesManifiesto(List<DetalleManifiestoDesecho> detallesManifiesto) {
		crudServiceBean.saveOrUpdate(detallesManifiesto);
	}

	@SuppressWarnings("unchecked")
	public List<Manifiesto> getManifiestosPorMedioPropio(Integer idMedioPropio) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idMedioPropio", idMedioPropio);

		try {
			List<Manifiesto> lista = (List<Manifiesto>) crudServiceBean
					.findByNamedQuery(Manifiesto.GET_LISTA_POR_TRANSPORTE_MEDIO_PROPIO,
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
	public List<Manifiesto> getManifiestosPorGestorAmbiental(Integer idGestorAmbiental) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idGestorAmbiental", idGestorAmbiental);

		try {
			List<Manifiesto> lista = (List<Manifiesto>) crudServiceBean
					.findByNamedQuery(Manifiesto.GET_LISTA_POR_GESTOR_AMBIENTAL,
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
	
	public void eliminarManifiesto(List<Manifiesto> manifiestosEliminar) {
		
		for (Manifiesto manifiesto : manifiestosEliminar) {
			manifiesto.setEstado(false);
			
			List<DetalleManifiestoDesecho> detallesManifiesto = manifiesto.getListaManifiestoDesechos();
			
			for (DetalleManifiestoDesecho detalleManifiestoDesecho : detallesManifiesto) {
				detalleManifiestoDesecho.setEstado(false);
			}
			
			crudServiceBean.saveOrUpdate(detallesManifiesto);
			
//			Documento documento = manifiesto.getManifiestoUnico();
//			if (documento != null) {
//				documento.setEstado(false);
//
//				crudServiceBean.saveOrUpdate(documento);
//			} no se modifica el documento para recuperar en el historial
		}
		
		crudServiceBean.saveOrUpdate(manifiestosEliminar);
	}
	
	public void eliminarDetallesManifiesto(List<DetalleManifiestoDesecho> detallesManifiesto) {
		crudServiceBean.saveOrUpdate(detallesManifiesto);
	}
	
	
	@SuppressWarnings("unchecked") 
	public Manifiesto getManifiestoHistorial(Integer idManifiesto, Integer nroObservacion) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idManifiesto", idManifiesto);
		parameters.put("nroObservacion", nroObservacion);

		try {
			List<Manifiesto> lista = (List<Manifiesto>) crudServiceBean
					.findByNamedQuery(Manifiesto.GET_HISTORIAL_POR_ID_NRO_OBSERVACION, parameters);

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
	public DetalleManifiestoDesecho getDetalleManifiestoHistorial(Integer idDetalle, Integer nroObservacion) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idDetalle", idDetalle);
		parameters.put("nroObservacion", nroObservacion);

		try {
			List<DetalleManifiestoDesecho> lista = (List<DetalleManifiestoDesecho>) crudServiceBean
					.findByNamedQuery(DetalleManifiestoDesecho.GET_HISTORIAL_POR_ID_NRO_OBSERVACION, parameters);

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
	public List<Manifiesto> getManifiestoHistorial(Integer idManifiesto) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idManifiesto", idManifiesto);

		try {
			List<Manifiesto> lista = (List<Manifiesto>) crudServiceBean
					.findByNamedQuery(Manifiesto.GET_HISTORIAL_POR_ID, parameters);

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
	public List<DetalleManifiestoDesecho> getDetalleManifiestoHistorial(Integer idDetalle) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idDetalle", idDetalle);

		try {
			List<DetalleManifiestoDesecho> lista = (List<DetalleManifiestoDesecho>) crudServiceBean
					.findByNamedQuery(DetalleManifiestoDesecho.GET_HISTORIAL_POR_ID, parameters);

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
	public List<Manifiesto> getManifiestosEliminadosPorMedioPropio(Integer idMedioPropio) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idMedioPropio", idMedioPropio);

		try {
			List<Manifiesto> lista = (List<Manifiesto>) crudServiceBean
					.findByNamedQuery(Manifiesto.GET_HISTORIAL_ELIMINADOS_POR_TRANSPORTE_MEDIO_PROPIO,
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
	public List<Manifiesto> getManifiestosEliminadosPorGestorAmbiental(Integer idGestorAmbiental) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idGestorAmbiental", idGestorAmbiental);

		try {
			List<Manifiesto> lista = (List<Manifiesto>) crudServiceBean
					.findByNamedQuery(Manifiesto.GET_HISTORIAL_ELIMINADOS_POR_GESTOR_AMBIENTAL,
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
	public List<DetalleManifiestoDesecho> getDetalleManifiestoPorIdManifiesto(Integer idManifiesto) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idManifiesto", idManifiesto);

		try {
			List<DetalleManifiestoDesecho> lista = (List<DetalleManifiestoDesecho>) crudServiceBean
					.findByNamedQuery(DetalleManifiestoDesecho.GET_POR_ID_MANIFIESTO, parameters);

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
	public List<DetalleManifiestoDesecho> getDetallesEliminadosPorIdManifiesto(Integer idManifiesto) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idManifiesto", idManifiesto);

		try {
			List<DetalleManifiestoDesecho> lista = (List<DetalleManifiestoDesecho>) crudServiceBean
					.findByNamedQuery(DetalleManifiestoDesecho.GET_HISTORIAL_ELIMINADOS_POR_MANIFIESTO, parameters);

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
