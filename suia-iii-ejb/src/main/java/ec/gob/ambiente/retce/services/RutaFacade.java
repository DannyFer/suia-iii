package ec.gob.ambiente.retce.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.retce.model.GestorDesechosPeligrosos;
import ec.gob.ambiente.retce.model.Ruta;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class RutaFacade {

	@EJB
	private CrudServiceBean crudServiceBean;
	
	@SuppressWarnings("unchecked")
	public Ruta getById(Integer id) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("id", id);
		try {
			Ruta lista = (Ruta) crudServiceBean.findByNamedQuery(Ruta.FIND_BY_ID,parameters).get(0);
			return lista;
		} catch (Exception e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<Ruta> getByGestorDesechoPeligroso(Integer idGestorDesechoPeligroso) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idGestorDesechoPeligroso", idGestorDesechoPeligroso);
		try {
			List<Ruta> lista = (List<Ruta>) crudServiceBean.findByNamedQuery(Ruta.GET_BY_GESTOR_DESECHO_PELIGROSO,parameters);
			return lista;
		} catch (Exception e) {
			return null;
		}
	}
	
	public Ruta guardar(Ruta ruta){
		ruta = crudServiceBean.saveOrUpdate(ruta);
        return ruta;
	}

}
