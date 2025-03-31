package ec.gob.ambiente.retce.services;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.retce.model.DerramesComponenteAfectado;
import ec.gob.ambiente.retce.model.ProyectoComponente;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class ProyectoComponenteFacade{	
	
	@EJB
	private CrudServiceBean crudServiceBean;	
	
	public void save(ProyectoComponente obj) {
		crudServiceBean.saveOrUpdate(obj);
	}
	
	@SuppressWarnings("unchecked")
	public List<ProyectoComponente> findByProyecto(DerramesComponenteAfectado objDerrame, String tipoComponente){		
		List<ProyectoComponente> producto = new ArrayList<ProyectoComponente>();
		try{
			producto = (List<ProyectoComponente>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM ProyectoComponente o where o.estado=true and o.tipo =:tipoComponente and o.derrameComponenteAfectado.id = :id")
					.setParameter("id", objDerrame.getId())
					.setParameter("tipoComponente", tipoComponente)
					.getResultList();
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return producto;
	}
	
	public void deshactivarComponentes(DerramesComponenteAfectado objDerrame){
		String sql = " UPDATE retce.project_information_component "
				+ "SET pric_status = false, pric_date_update = NOW() "
				+ "WHERE spac_id = "+ objDerrame.getId()+" AND pric_status = true;";
		Query query = crudServiceBean.getEntityManager().createNativeQuery(sql);
		query.executeUpdate();
	}
	
	public void deshactivarAdjuntoDocumento(DerramesComponenteAfectado objDerrame){
		String sql = " update suia_iii.documents  "
						+ " set docu_status = false, docu_date_update = now()"
						+ " where docu_table_class = 'DerramesComponenteAfectado' and docu_table_id = "+ objDerrame.getId()+" and docu_status = true " ;
		Query query = crudServiceBean.getEntityManager().createNativeQuery(sql);
		query.executeUpdate();
	}
}