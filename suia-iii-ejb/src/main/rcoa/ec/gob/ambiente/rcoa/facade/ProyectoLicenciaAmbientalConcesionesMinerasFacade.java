package ec.gob.ambiente.rcoa.facade;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.model.ProyectoLicenciaAmbientalCoaCiuuBloques;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaAmbientalConcesionesMineras;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCuaCiuu;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class ProyectoLicenciaAmbientalConcesionesMinerasFacade{

	@EJB
    private CrudServiceBean crudServiceBean;
	
	public void guardar(ProyectoLicenciaAmbientalConcesionesMineras concesion) {        
        	crudServiceBean.saveOrUpdate(concesion);        
    }
	
	public void eliminar(ProyectoLicenciaCuaCiuu ciiu){
		Query sqlUpdateMC = crudServiceBean.getEntityManager().createQuery("update ProyectoLicenciaAmbientalConcesionesMineras c set c.estado=false where c.proyectoLicenciaCuaCiuu.id=:id");
		sqlUpdateMC.setParameter("id", ciiu.getId());
		sqlUpdateMC.executeUpdate();
	}
	
	@SuppressWarnings("unchecked")
	public List<ProyectoLicenciaAmbientalConcesionesMineras> cargarConcesiones(ProyectoLicenciaCoa proyecto)
	{
		List<ProyectoLicenciaAmbientalConcesionesMineras> lista = new ArrayList<ProyectoLicenciaAmbientalConcesionesMineras>();
		Query sql = crudServiceBean.getEntityManager().createQuery("select c from ProyectoLicenciaAmbientalConcesionesMineras c where c.proyectoLicenciaCuaCiuu.proyectoLicenciaCoa=:proyecto and c.estado=true");
		sql.setParameter("proyecto", proyecto);
		if(sql.getResultList().size()>0)
			lista=sql.getResultList();
		
		return lista;
	}
}
