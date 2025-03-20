package ec.gob.ambiente.retce.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.retce.model.GestorDesechosEliminacion;
import ec.gob.ambiente.retce.model.GestorDesechosEliminacionDesechoPeligroso;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.DesechoPeligroso;

@Stateless
public class GestorDesechosEliminacionDesechoPeligrosoFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;	
	
	public GestorDesechosEliminacionDesechoPeligroso guardarDesechoPeligroso(GestorDesechosEliminacionDesechoPeligroso desecho) {
		return crudServiceBean.saveOrUpdate(desecho);
	}
	
	public boolean existeDesechoPeligroso(GestorDesechosEliminacion gestor, DesechoPeligroso desecho) {
		boolean hayDesecho=false;
		try
		{
			 Query sql= crudServiceBean.
					getEntityManager().
					createQuery("select d from GestorDesechosEliminacionDesechoPeligroso d where d.gestorDesechosEliminacion=:gestor and d.desechoPeligroso=:desecho and d.estado=true");
			 
			 sql.setParameter("gestor", gestor);
			 sql.setParameter("desecho", desecho);
			 if(sql.getResultList().size()>0)
				 hayDesecho=true;
		}
		catch(Exception e)
		{
			
		}
		return hayDesecho;
	}
	
	@SuppressWarnings("unchecked")
	public List<GestorDesechosEliminacionDesechoPeligroso> getByDesechoEliminacion(Integer idDesechoEliminacion) {
		List<GestorDesechosEliminacionDesechoPeligroso> lista = new ArrayList<GestorDesechosEliminacionDesechoPeligroso>();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idDesechoEliminacion", idDesechoEliminacion);
		try {
			lista = (List<GestorDesechosEliminacionDesechoPeligroso>) crudServiceBean.findByNamedQuery(GestorDesechosEliminacionDesechoPeligroso.GET_BY_DESECHO_ELIMINACION,parameters);
		} catch (Exception e) {
			e.printStackTrace();
			return lista;
		}
		return lista;
	}

}
