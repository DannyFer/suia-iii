package ec.gob.ambiente.rcoa.facade;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoaCiiuResiduos;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class ProyectoLicenciaCoaCiiuResiduosFacade {
	 @EJB
	    private CrudServiceBean crudServiceBean;
	 

	public void guardar(ProyectoLicenciaCoaCiiuResiduos ciiuResiduos) {        
		crudServiceBean.saveOrUpdate(ciiuResiduos);        
	}

	@SuppressWarnings("unchecked")
	public List<ProyectoLicenciaCoaCiiuResiduos> cargarResiduosPorActividad(Integer idProyectoCoaCiiu)
	{
		List<ProyectoLicenciaCoaCiiuResiduos> lista = new ArrayList<ProyectoLicenciaCoaCiiuResiduos>();

		String sqlString = "select c from ProyectoLicenciaCoaCiiuResiduos c where c.proyectoLicenciaCuaCiuu.id=:idProyectoCoaCiiu and c.estado=true order by c.desechoPeligroso.id ";

		Query sql = crudServiceBean.getEntityManager().createQuery(sqlString);
		sql.setParameter("idProyectoCoaCiiu", idProyectoCoaCiiu);
		if(sql.getResultList().size()>0)
			lista=sql.getResultList();
		
		return lista;
	}
	 
	public void eliminar(List<ProyectoLicenciaCoaCiiuResiduos> ciiuResiduos) {        
		crudServiceBean.saveOrUpdate(ciiuResiduos);        
	}
}
