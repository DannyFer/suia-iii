package ec.gob.ambiente.rcoa.facade;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.model.ProyectoLicenciaAmbientalCoaCiuuBloques;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCuaCiuu;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class ProyectoLicenciaAmbientalCoaCiuuBloquesFacade {

	@EJB
    private CrudServiceBean crudServiceBean;
	
	public void guardar(ProyectoLicenciaAmbientalCoaCiuuBloques bloques) {        
        	crudServiceBean.saveOrUpdate(bloques);        
    }
	
	@SuppressWarnings("unchecked")
	public List<ProyectoLicenciaAmbientalCoaCiuuBloques> cargarBloques(ProyectoLicenciaCoa proyecto)
	{
		List<ProyectoLicenciaAmbientalCoaCiuuBloques> lista = new ArrayList<ProyectoLicenciaAmbientalCoaCiuuBloques>();
		Query sql = crudServiceBean.getEntityManager().createQuery("select c from ProyectoLicenciaAmbientalCoaCiuuBloques c where c.proyectoLicenciaCuaCiuu.proyectoLicenciaCoa=:proyecto and c.estado=true");
		sql.setParameter("proyecto", proyecto);
		if(sql.getResultList().size()>0)
			lista=sql.getResultList();
		
		return lista;
	}
	
	public void eliminar(ProyectoLicenciaCuaCiuu ciiu){
		Query sqlUpdateMC = crudServiceBean.getEntityManager().createQuery("update ProyectoLicenciaAmbientalCoaCiuuBloques c set c.estado=false where c.proyectoLicenciaCuaCiuu.id=:id");
		sqlUpdateMC.setParameter("id", ciiu.getId());
		sqlUpdateMC.executeUpdate();
	}
}
