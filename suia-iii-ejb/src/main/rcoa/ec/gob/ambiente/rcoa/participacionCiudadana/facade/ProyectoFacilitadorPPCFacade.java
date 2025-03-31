package ec.gob.ambiente.rcoa.participacionCiudadana.facade;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import ec.gob.ambiente.rcoa.participacionCiudadana.model.ProyectoFacilitadorPPC;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class ProyectoFacilitadorPPCFacade {
	
	private static final Logger LOG = Logger.getLogger(ProyectoFacilitadorPPCFacade.class);

	@EJB
	private CrudServiceBean crudServiceBean;

	public ProyectoFacilitadorPPC guardar(ProyectoFacilitadorPPC proyectoFacilitadorPPC){
		return crudServiceBean.saveOrUpdate(proyectoFacilitadorPPC);
	}
	
	public ProyectoFacilitadorPPC getByIdProyecto(Integer idProyecto) {
		ProyectoFacilitadorPPC result = new ProyectoFacilitadorPPC();
		Query sql = crudServiceBean.getEntityManager().createQuery("SELECT pfppc FROM ProyectoFacilitadorPPC pfppc "
				+ "WHERE pfppc.proyectoLicenciaCoa.id = :idProyecto");
		sql.setParameter("idProyecto", idProyecto);
		if (sql.getResultList().size() > 0){
			result =(ProyectoFacilitadorPPC) sql.getResultList().get(0);
		}
		return result;
	}
	
	public List<ProyectoFacilitadorPPC> buscarProyectos(Date fechaInicio, Date fechaFin){
		
		List<ProyectoFacilitadorPPC> lista = new ArrayList<ProyectoFacilitadorPPC>();
		try{
			
			Query sql = crudServiceBean.getEntityManager().createQuery("SELECT  p FROM ProyectoFacilitadorPPC p WHERE ");
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return lista;
	}

}
