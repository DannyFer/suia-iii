package ec.gob.ambiente.rcoa.facade;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoaUbicacion;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCuaCiuu;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;

@Stateless
public class ProyectoLicenciaCuaCiuuFacade {
	 @EJB
	    private CrudServiceBean crudServiceBean;
	 
	 public ProyectoLicenciaCuaCiuu actividadproyecto (ProyectoLicenciaCoa proyecto){
		 ProyectoLicenciaCuaCiuu proyectoLicenciaCuaCiuu = new ProyectoLicenciaCuaCiuu();
			
			Query sql =crudServiceBean.getEntityManager().createQuery("Select s From ProyectoLicenciaCuaCiuu s "
					+ " where proyectoLicenciaCoa=:proyecto and orderJerarquia=1");
			sql.setParameter("proyecto", proyecto);	
			if(sql.getResultList().size()>0)
				proyectoLicenciaCuaCiuu=(ProyectoLicenciaCuaCiuu) sql.getSingleResult();
			
			return proyectoLicenciaCuaCiuu;
			
	 }

	 public ProyectoLicenciaCuaCiuu actividadproyectoSec1 (ProyectoLicenciaCoa proyecto){
		 ProyectoLicenciaCuaCiuu proyectoLicenciaCuaCiuu = new ProyectoLicenciaCuaCiuu();
									
		 	Query sql =crudServiceBean.getEntityManager().createQuery("Select s From ProyectoLicenciaCuaCiuu s "
					+ " where proyectoLicenciaCoa=:proyecto and orderJerarquia=2");
			sql.setParameter("proyecto", proyecto);	
			if(sql.getResultList().size()>0)
				proyectoLicenciaCuaCiuu=(ProyectoLicenciaCuaCiuu) sql.getSingleResult();
			
			return proyectoLicenciaCuaCiuu;
			
	 }
	 public ProyectoLicenciaCuaCiuu actividadproyectoSec2 (ProyectoLicenciaCoa proyecto){
		 ProyectoLicenciaCuaCiuu proyectoLicenciaCuaCiuu = new ProyectoLicenciaCuaCiuu();
			
			Query sql =crudServiceBean.getEntityManager().createQuery("Select s From ProyectoLicenciaCuaCiuu s "
					+ " where proyectoLicenciaCoa=:proyecto and orderJerarquia=3");
			sql.setParameter("proyecto", proyecto);	
			if(sql.getResultList().size()>0)
				proyectoLicenciaCuaCiuu=(ProyectoLicenciaCuaCiuu) sql.getSingleResult();
			
			return proyectoLicenciaCuaCiuu;
			
	 }
	 public void guardar(ProyectoLicenciaCuaCiuu ciiu) {        
		crudServiceBean.saveOrUpdate(ciiu);        
	}
	 
	 @SuppressWarnings("unchecked")
	public List<ProyectoLicenciaCuaCiuu> actividadesPorProyecto(ProyectoLicenciaCoa proyecto){
		 List<ProyectoLicenciaCuaCiuu> proyectoLicenciaCuaCiuu = new ArrayList<>();
			
			Query sql =crudServiceBean.getEntityManager().createQuery("Select s From ProyectoLicenciaCuaCiuu s "
					+ " where proyectoLicenciaCoa=:proyecto and s.estado = true order by s.orderJerarquia");
			sql.setParameter("proyecto", proyecto);	
			if(sql.getResultList().size()>0)
				proyectoLicenciaCuaCiuu=(List<ProyectoLicenciaCuaCiuu>) sql.getResultList();
			
			return proyectoLicenciaCuaCiuu;
			
	 }
	 
	 public ProyectoLicenciaCuaCiuu actividadPrincipal (ProyectoLicenciaCoa proyecto){
		 ProyectoLicenciaCuaCiuu proyectoLicenciaCuaCiuu=null;

		 Query sql =crudServiceBean.getEntityManager().createQuery("Select s From ProyectoLicenciaCuaCiuu s "
				 + " where s.proyectoLicenciaCoa=:proyecto and s.primario=true and s.estado=true");
		 sql.setParameter("proyecto", proyecto);	
		 if(sql.getResultList().size()>0)
			 proyectoLicenciaCuaCiuu=(ProyectoLicenciaCuaCiuu) sql.getSingleResult();

		 return proyectoLicenciaCuaCiuu;
	 }
	 
	 public ProyectoLicenciaCuaCiuu actividadPrincipal (String proyecto){
		 ProyectoLicenciaCuaCiuu proyectoLicenciaCuaCiuu=null;

		 Query sql =crudServiceBean.getEntityManager().createQuery("Select s From ProyectoLicenciaCuaCiuu s "
				 + " where s.proyectoLicenciaCoa.codigoUnicoAmbiental=:proyecto and s.primario=true and s.estado=true");
		 sql.setParameter("proyecto", proyecto);	
		 if(sql.getResultList().size()>0)
			 proyectoLicenciaCuaCiuu=(ProyectoLicenciaCuaCiuu) sql.getSingleResult();

		 return proyectoLicenciaCuaCiuu;
	 }
	 
	 public void eliminar(ProyectoLicenciaCoa proyecto){		
		 Query sqlUpdate = crudServiceBean.getEntityManager().createQuery("update ProyectoLicenciaCuaCiuu p set p.estado=false where p.proyectoLicenciaCoa.id=:id");
		 sqlUpdate.setParameter("id", proyecto.getId());
		 sqlUpdate.executeUpdate();		
	 }
}
