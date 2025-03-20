package ec.gob.ambiente.retce.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.retce.model.ManifiestoUnico;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.DesechoPeligroso;

@Stateless
public class ManifiestoUnicoFacade {

	@EJB
	private CrudServiceBean crudServiceBean;

	@SuppressWarnings("unchecked")
	public List<ManifiestoUnico> getByGestorDesechoPeligroso(Integer idGestorDesechosPeligroso) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idGestorDesechosPeligroso", idGestorDesechosPeligroso);
		try {
			List<ManifiestoUnico> lista = (List<ManifiestoUnico>) crudServiceBean.findByNamedQuery(ManifiestoUnico.GET_BY_GESTOR_DESECHO_PELIGROSO,parameters);
			return lista;
		} catch (Exception e) {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<ManifiestoUnico> getByGestorFase(Integer idGestorDesechosPeligroso, Integer idFase) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idGestorDesechosPeligroso", idGestorDesechosPeligroso);
		parameters.put("idFase", idFase);
		try {
			List<ManifiestoUnico> lista = (List<ManifiestoUnico>) crudServiceBean.findByNamedQuery(ManifiestoUnico.GET_BY_GESTOR_FASE,parameters);
			return lista;
		} catch (Exception e) {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<ManifiestoUnico> getByGestorFaseTipo(Integer idGestorDesechosPeligroso, Integer idFase, Integer idTipo) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idGestorDesechosPeligroso", idGestorDesechosPeligroso);
		parameters.put("idFase", idFase);
		parameters.put("idTipo", idTipo);
		try {
			List<ManifiestoUnico> lista = (List<ManifiestoUnico>) crudServiceBean.findByNamedQuery(ManifiestoUnico.GET_BY_GESTOR_FASE_TIPO,parameters);
			return lista;
		} catch (Exception e) {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public ManifiestoUnico getById(Integer idManifiestoUnico) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idManifiestoUnico", idManifiestoUnico);
		return (ManifiestoUnico) crudServiceBean.findByNamedQuery(ManifiestoUnico.FIND_BY_ID, parameters).get(0);
	}
	
	public ManifiestoUnico guardar(ManifiestoUnico manifiestoUnico){
        crudServiceBean.saveOrUpdate(manifiestoUnico);
        return manifiestoUnico;
	}

}
