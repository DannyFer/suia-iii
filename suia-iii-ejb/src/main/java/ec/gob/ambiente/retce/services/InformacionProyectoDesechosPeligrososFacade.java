package ec.gob.ambiente.retce.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.retce.model.InformacionProyectoDesechosPeligrosos;
import ec.gob.ambiente.retce.model.InformacionProyecto;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Usuario;

@Stateless
public class InformacionProyectoDesechosPeligrososFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;

	public void saveRevision(InformacionProyectoDesechosPeligrosos obj,Usuario usuario) {		
		obj.setUsuarioRevision(usuario.getNombre());
		obj.setFechaRevision(new Date());
		crudServiceBean.saveOrUpdate(obj);
	}
	
	public InformacionProyectoDesechosPeligrosos findById(Integer id){
		try {
			InformacionProyectoDesechosPeligrosos informacionProyectoDesechosPeligrosos = (InformacionProyectoDesechosPeligrosos) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM InformacionProyectoDesechosPeligrosos o where o.id = :id")
					.setParameter("id", id).getSingleResult();			
			return informacionProyectoDesechosPeligrosos;			
		} catch (Exception e) {
			e.printStackTrace();
		}	
		return null;
	}
	
	public void save(InformacionProyectoDesechosPeligrosos obj, Usuario usuario){
		if(obj.getInformacionProyecto() != null){
			if(obj.getId()==null){
				obj.setUsuarioCreacion(usuario.getNombre());
				obj.setFechaCreacion(new Date());
			}
			else{
				obj.setUsuarioModificacion(usuario.getNombre());
				obj.setFechaModificacion(new Date());			
			}
			crudServiceBean.saveOrUpdate(obj);
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<InformacionProyectoDesechosPeligrosos> findByProyecto(InformacionProyecto informacion){
		List<InformacionProyectoDesechosPeligrosos> lista = new ArrayList<InformacionProyectoDesechosPeligrosos>();
		try {
			lista = (ArrayList<InformacionProyectoDesechosPeligrosos>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM InformacionProyectoDesechosPeligrosos o where o.estado = true and o.informacionProyecto.id = :id")
					.setParameter("id", informacion.getId()).getResultList();
			return lista;
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return lista;
	}

}
