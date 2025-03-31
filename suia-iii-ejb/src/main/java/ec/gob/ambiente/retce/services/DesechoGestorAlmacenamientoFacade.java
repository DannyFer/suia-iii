package ec.gob.ambiente.retce.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.retce.model.DesechoGestorAlmacenamiento;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class DesechoGestorAlmacenamientoFacade {

	@EJB
	private CrudServiceBean crudServiceBean;

	@SuppressWarnings("unchecked")
	public DesechoGestorAlmacenamiento getById(Integer id) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("id", id);
		try {
			DesechoGestorAlmacenamiento lista = (DesechoGestorAlmacenamiento) crudServiceBean.findByNamedQuery(DesechoGestorAlmacenamiento.FIND_BY_ID,parameters).get(0);
			return lista;
		} catch (Exception e) {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<DesechoGestorAlmacenamiento> getByManifiestoUnico(Integer idManifiestoUnico) {
		List<DesechoGestorAlmacenamiento> lista = new ArrayList<DesechoGestorAlmacenamiento>();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idManifiestoUnico", idManifiestoUnico);
		try {
			lista = (List<DesechoGestorAlmacenamiento>) crudServiceBean.findByNamedQuery(DesechoGestorAlmacenamiento.GET_BY_MANIFIESTO,parameters);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;
	}
	
	public DesechoGestorAlmacenamiento guardar(DesechoGestorAlmacenamiento desechoGestorAlmacenamiento){
        crudServiceBean.saveOrUpdate(desechoGestorAlmacenamiento);
        return desechoGestorAlmacenamiento;
	}

}
