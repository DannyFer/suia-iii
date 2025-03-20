package ec.gob.ambiente.retce.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.retce.model.InformacionProyectoAprobacionRequisitosTecnicos;
import ec.gob.ambiente.retce.model.InformacionProyecto;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Usuario;

@Stateless
public class InformacionProyectoAprobacionRequisitosTecnicosFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	public InformacionProyectoAprobacionRequisitosTecnicos findById(Integer id){
		try {
			InformacionProyectoAprobacionRequisitosTecnicos informacionProyectoART = (InformacionProyectoAprobacionRequisitosTecnicos) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM InformacionProyectoAprobacionRequisitosTecnicos o where o.id = :id")
					.setParameter("id", id).getSingleResult();			
			return informacionProyectoART;			
		} catch (Exception e) {
			e.printStackTrace();
		}	
		return null;
	}
	
	public void save(InformacionProyectoAprobacionRequisitosTecnicos obj, Usuario usuario){
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
	public List<InformacionProyectoAprobacionRequisitosTecnicos> findByProyecto(InformacionProyecto informacion){
		List<InformacionProyectoAprobacionRequisitosTecnicos> lista = new ArrayList<InformacionProyectoAprobacionRequisitosTecnicos>();
		try {
			lista = (ArrayList<InformacionProyectoAprobacionRequisitosTecnicos>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM InformacionProyectoAprobacionRequisitosTecnicos o where o.estado = true and o.informacionProyecto.id = :id")
					.setParameter("id", informacion.getId()).getResultList();
			return lista;
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return lista;
	}

}
