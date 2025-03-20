package ec.gob.ambiente.retce.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.retce.model.FormaInformacionProyecto;
import ec.gob.ambiente.retce.model.InformacionProyecto;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Usuario;

@Stateless
public class FormaInformacionProyectoFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	public FormaInformacionProyecto findById(Integer id){
		try {
			FormaInformacionProyecto formaInformacionProyecto = (FormaInformacionProyecto) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM FormaInformacionProyecto o where o.id = :id")
					.setParameter("id", id).getSingleResult();			
			return formaInformacionProyecto;			
		} catch (Exception e) {
			e.printStackTrace();
		}	
		return null;
	}
	
	public void save(FormaInformacionProyecto obj, Usuario usuario){
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
	
	@SuppressWarnings("unchecked")
	public List<FormaInformacionProyecto> findByInformacionProyecto(InformacionProyecto informacion){
		List<FormaInformacionProyecto> lista = new ArrayList<FormaInformacionProyecto>();
		try {
			lista = (ArrayList<FormaInformacionProyecto>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM FormaInformacionProyecto o where o.estado = true and o.informacionProyecto.id = :id")
					.setParameter("id", informacion.getId()).getResultList();
			return lista;
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return lista;
	}

}
