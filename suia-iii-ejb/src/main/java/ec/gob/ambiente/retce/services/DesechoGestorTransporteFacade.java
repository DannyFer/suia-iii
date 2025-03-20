package ec.gob.ambiente.retce.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.retce.model.DesechoGestorTransporte;
import ec.gob.ambiente.retce.model.Ruta;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class DesechoGestorTransporteFacade {

	@EJB
	private CrudServiceBean crudServiceBean;

	@SuppressWarnings("unchecked")
	public DesechoGestorTransporte getById(Integer id) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("id", id);
		try {
			DesechoGestorTransporte lista = (DesechoGestorTransporte) crudServiceBean.findByNamedQuery(DesechoGestorTransporte.FIND_BY_ID,parameters).get(0);
			return lista;
		} catch (Exception e) {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<DesechoGestorTransporte> getByManifiestoUnico(Integer idManifiestoUnico) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idManifiestoUnico", idManifiestoUnico);
		try {
			List<DesechoGestorTransporte> lista = (List<DesechoGestorTransporte>) crudServiceBean.findByNamedQuery(DesechoGestorTransporte.GET_BY_MANIFIESTO,parameters);
			return lista;
		} catch (Exception e) {
			return null;
		}
	}
	
	public DesechoGestorTransporte guardar(DesechoGestorTransporte desechoGestorTransporte){
		desechoGestorTransporte = crudServiceBean.saveOrUpdate(desechoGestorTransporte);
		return desechoGestorTransporte;
	}

}
