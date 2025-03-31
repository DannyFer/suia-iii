package ec.gob.ambiente.rcoa.facade;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.model.ProyectoCoaCiuuSubActividades;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCuaCiuu;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class ProyectoCoaCiuuSubActividadesFacade {

	@EJB
    private CrudServiceBean crudServiceBean;
	
	public void guardar(ProyectoCoaCiuuSubActividades subActividad) {        
        	crudServiceBean.saveOrUpdate(subActividad);        
    }
	
	@SuppressWarnings("unchecked")
	public List<ProyectoCoaCiuuSubActividades> cargarSubActividades(ProyectoLicenciaCuaCiuu ciiu)
	{
		List<ProyectoCoaCiuuSubActividades> lista = new ArrayList<ProyectoCoaCiuuSubActividades>();
		Query sql = crudServiceBean.getEntityManager().createQuery("select c from ProyectoCoaCiuuSubActividades c where c.proyectoLicenciaCuaCiuu =:ciiu and c.estado=true order by c.subActividad.orden");
		sql.setParameter("ciiu", ciiu);
		if(sql.getResultList().size()>0)
			lista=sql.getResultList();
		
		return lista;
	}
	
	public void eliminar(ProyectoLicenciaCuaCiuu ciiu){
		Query sqlUpdateMC = crudServiceBean.getEntityManager().createQuery("update ProyectoCoaCiuuSubActividades c set c.estado=false where c.proyectoLicenciaCuaCiuu.id=:id");
		sqlUpdateMC.setParameter("id", ciiu.getId());
		sqlUpdateMC.executeUpdate();
	}
}
