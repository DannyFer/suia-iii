package ec.gob.ambiente.retce.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.retce.model.GestorDesechosEliminacion;
import ec.gob.ambiente.retce.model.GestorDesechosEliminacionDesechoNoPeligroso;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class GestorDesechosEliminacionDesechoNoPeligrosoFacade {

	@EJB
	private CrudServiceBean crudServiceBean;
	
	public GestorDesechosEliminacionDesechoNoPeligroso guardarDesechoNoPeligroso(GestorDesechosEliminacionDesechoNoPeligroso desecho) {
		return crudServiceBean.saveOrUpdate(desecho);
	}
	
	public GestorDesechosEliminacionDesechoNoPeligroso desechoNoPeligroso(GestorDesechosEliminacion gestor) {
		GestorDesechosEliminacionDesechoNoPeligroso desecho= new GestorDesechosEliminacionDesechoNoPeligroso();
		try
		{
			desecho=(GestorDesechosEliminacionDesechoNoPeligroso) crudServiceBean.
					getEntityManager().
					createQuery("select d from GestorDesechosEliminacionDesechoNoPeligroso d where d.gestorDesechosEliminacion=:gestor and d.estado=true").
					setParameter("gestor", gestor).
					getResultList().get(0);
			return desecho;
		}
		catch(Exception e)
		{
			return desecho;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<GestorDesechosEliminacionDesechoNoPeligroso> getByDesechoEliminacion(Integer idDesechoEliminacion) {
		List<GestorDesechosEliminacionDesechoNoPeligroso> lista = new ArrayList<GestorDesechosEliminacionDesechoNoPeligroso>();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idDesechoEliminacion", idDesechoEliminacion);
		try {
			lista = (List<GestorDesechosEliminacionDesechoNoPeligroso>) crudServiceBean.findByNamedQuery(GestorDesechosEliminacionDesechoNoPeligroso.GET_BY_DESECHO_ELIMINACION,parameters);
		} catch (Exception e) {
			e.printStackTrace();
			return lista;
		}
		return lista;
	}
}
