package ec.gob.ambiente.retce.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.retce.model.GeneradorDesechosPeligrososRetce;
import ec.gob.ambiente.retce.model.TransporteEmpresasGestoras;
import ec.gob.ambiente.retce.model.TransporteGestorAmbiental;
import ec.gob.ambiente.retce.model.TransporteMediosPropios;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;

@Stateless
public class TransporteFacade {

	@EJB
	private CrudServiceBean crudServiceBean;
	
	@EJB
	private ManifiestoFacade manifiestoFacade;
	
	@EJB
	private DocumentosFacade documentosFacade;

	public void guardarTransporteMediosPropios(TransporteMediosPropios transporte) {
		crudServiceBean.saveOrUpdate(transporte);
	}
	
	public void guardarTransporteEmpresaGestora(TransporteEmpresasGestoras empresa) {
		crudServiceBean.saveOrUpdate(empresa);
	}
	
	public void guardarListaTransporteEmpresaGestora(List<TransporteEmpresasGestoras> empresas) {
		crudServiceBean.saveOrUpdate(empresas);
	}
	
	public void guardarTransporteGestorAmbiental(TransporteGestorAmbiental transporte) {
		crudServiceBean.saveOrUpdate(transporte);
	}
	
	public void eliminarTransporteEmpresasGestoras(List<TransporteEmpresasGestoras> empresas) {
		crudServiceBean.delete(empresas);
	}

	@SuppressWarnings("unchecked") 
	public TransporteMediosPropios getTransporteMediosPropiosPorRgdRetce(Integer idRgdRetce) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idRgdRetce", idRgdRetce);

		try {
			List<TransporteMediosPropios> lista = (List<TransporteMediosPropios>) crudServiceBean
					.findByNamedQuery(
							TransporteMediosPropios.GET_POR_RGDRETCE,
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
	
	@SuppressWarnings("unchecked") 
	public TransporteMediosPropios getTransporteMediosPropiosPorId(Integer idMedioPropio) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idMedioPropio", idMedioPropio);

		try {
			List<TransporteMediosPropios> lista = (List<TransporteMediosPropios>) crudServiceBean
					.findByNamedQuery(TransporteMediosPropios.GET_POR_ID, parameters);

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
	public List<TransporteEmpresasGestoras> getEmpresasGestorasPorRgdRetce(Integer idRgdRetce, Integer actividad) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idRgdRetce", idRgdRetce);
		parameters.put("actividad", actividad);

		try {
			List<TransporteEmpresasGestoras> lista = (List<TransporteEmpresasGestoras>) crudServiceBean
					.findByNamedQuery(TransporteEmpresasGestoras.GET_POR_RGDRETCE,
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
	public TransporteGestorAmbiental getTransporteGestorAmbientalPorRgdRetce(Integer idRgdRetce) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idRgdRetce", idRgdRetce);

		try {
			List<TransporteGestorAmbiental> lista = (List<TransporteGestorAmbiental>) crudServiceBean
					.findByNamedQuery(TransporteGestorAmbiental.GET_POR_RGDRETCE,
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
	
	public void eliminarTransporte(Integer idRgdRetce, Integer tipoActividad){
		TransporteMediosPropios medioPropio =  getTransporteMediosPropiosPorRgdRetce(idRgdRetce);
		if(medioPropio != null){
			medioPropio.setEstado(false);
			crudServiceBean.saveOrUpdate(medioPropio);
			
			manifiestoFacade.eliminarManifiesto(manifiestoFacade.getManifiestosPorMedioPropio(medioPropio.getId()));
			
			List<Documento> documentosAutorizacion = documentosFacade.documentoXTablaIdXIdDoc(
					idRgdRetce,
					GeneradorDesechosPeligrososRetce.class.getSimpleName(),
					TipoDocumentoSistema.DOCUMENTO_AUTORIZACION_TRANSPORTE_MEDIO_PROPIO);
			
			if (documentosAutorizacion.size() > 0) {
				documentosAutorizacion.get(0).setEstado(false);
				crudServiceBean.saveOrUpdate(documentosAutorizacion.get(0));
			}	
		}
		
		List<TransporteEmpresasGestoras> empresasGestoras = getEmpresasGestorasPorRgdRetce(idRgdRetce, tipoActividad);
		if(empresasGestoras != null && empresasGestoras.size() > 0) {
			eliminarTransporteEmpresasGestoras(empresasGestoras);
		}
		
		TransporteGestorAmbiental transporteGestorAmbiental = getTransporteGestorAmbientalPorRgdRetce(idRgdRetce);
		if(transporteGestorAmbiental != null) {
			transporteGestorAmbiental.setEstado(false);
			crudServiceBean.saveOrUpdate(transporteGestorAmbiental);
			
			manifiestoFacade.eliminarManifiesto(manifiestoFacade.getManifiestosPorGestorAmbiental(transporteGestorAmbiental.getId()));
		}
	}
	
	@SuppressWarnings("unchecked") 
	public TransporteMediosPropios getTransporteMedioPropioHistorial(Integer idMedioPropio, Integer nroObservacion) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idMedioPropio", idMedioPropio);
		parameters.put("nroObservacion", nroObservacion);

		try {
			List<TransporteMediosPropios> lista = (List<TransporteMediosPropios>) crudServiceBean
					.findByNamedQuery(TransporteMediosPropios.GET_HISTORIAL_POR_ID_NRO_OBSERVACION, parameters);

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
	public TransporteEmpresasGestoras getTransporteEmpresasGestorasHistorial(Integer idEmpresa, Integer nroObservacion) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idEmpresa", idEmpresa);
		parameters.put("nroObservacion", nroObservacion);

		try {
			List<TransporteEmpresasGestoras> lista = (List<TransporteEmpresasGestoras>) crudServiceBean
					.findByNamedQuery(TransporteEmpresasGestoras.GET_HISTORIAL_POR_ID_NRO_OBSERVACION, parameters);

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
	public List<TransporteMediosPropios> getHistorialTransporteMedioPropio(Integer idMedioPropio) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idMedioPropio", idMedioPropio);

		try {
			List<TransporteMediosPropios> lista = (List<TransporteMediosPropios>) crudServiceBean
					.findByNamedQuery(TransporteMediosPropios.GET_HISTORIAL_POR_ID, parameters);

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
	public List<TransporteEmpresasGestoras> getHistorialTransporteEmpresasGestoras(Integer idEmpresa) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idEmpresa", idEmpresa);

		try {
			List<TransporteEmpresasGestoras> lista = (List<TransporteEmpresasGestoras>) crudServiceBean
					.findByNamedQuery(TransporteEmpresasGestoras.GET_HISTORIAL_POR_ID, parameters);

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
	public List<TransporteEmpresasGestoras> getHistorialEmpresasGestorasPorRgdRetce(Integer idRgdRetce, Integer actividad) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idRgdRetce", idRgdRetce);
		parameters.put("actividad", actividad);

		try {
			List<TransporteEmpresasGestoras> lista = (List<TransporteEmpresasGestoras>) crudServiceBean
					.findByNamedQuery(TransporteEmpresasGestoras.GET_HISTORIAL_POR_RGDRETCE,
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
	public List<TransporteEmpresasGestoras> getEmpresasEliminadasPorRgdRetce(Integer idRgdRetce, Integer actividad) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idRgdRetce", idRgdRetce);
		parameters.put("actividad", actividad);

		try {
			List<TransporteEmpresasGestoras> lista = (List<TransporteEmpresasGestoras>) crudServiceBean
					.findByNamedQuery(TransporteEmpresasGestoras.GET_HISTORIAL_EMPRESAS_ELIMINADAS_POR_RGDRETCE,
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
	public TransporteGestorAmbiental getTransporteGestorAmbientalHistorial(Integer idGestor, Integer nroObservacion) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idGestor", idGestor);
		parameters.put("nroObservacion", nroObservacion);

		try {
			List<TransporteGestorAmbiental> lista = (List<TransporteGestorAmbiental>) crudServiceBean
					.findByNamedQuery(TransporteGestorAmbiental.GET_HISTORIAL_POR_ID_NRO_OBSERVACION,
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

	@SuppressWarnings("unchecked") 
	public List<TransporteMediosPropios> getHistorialTransporteMedioPropioEliminado(Integer idRgdRetce) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idRgdRetce", idRgdRetce);

		try {
			List<TransporteMediosPropios> lista = (List<TransporteMediosPropios>) crudServiceBean
					.findByNamedQuery(TransporteMediosPropios.GET_HISTORIAL_ELIMINADOS_POR_RGDRETCE, parameters);

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
	public List<TransporteGestorAmbiental> getHistorialGestorAmbientalEliminado(Integer idRgdRetce) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idRgdRetce", idRgdRetce);

		try {
			List<TransporteGestorAmbiental> lista = (List<TransporteGestorAmbiental>) crudServiceBean
					.findByNamedQuery(TransporteGestorAmbiental.GET_HISTORIAL_ELIMINADOS_POR_RGDRETCE, parameters);

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
