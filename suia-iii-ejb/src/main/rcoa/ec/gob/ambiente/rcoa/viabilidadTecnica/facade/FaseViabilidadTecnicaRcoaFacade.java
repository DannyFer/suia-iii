package ec.gob.ambiente.rcoa.viabilidadTecnica.facade;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.model.RegistroAmbientalRcoa;
import ec.gob.ambiente.rcoa.viabilidadTecnica.model.FaseViabilidadTecnicaRcoa;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Usuario;

@Stateless
public class FaseViabilidadTecnicaRcoaFacade {
	
	@EJB
    private CrudServiceBean crudServiceBean;
	
	public void guardar(FaseViabilidadTecnicaRcoa obj, Usuario usuario){
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
	
	public FaseViabilidadTecnicaRcoa buscarPorId(Integer id){
		try {
			return (FaseViabilidadTecnicaRcoa)crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM FaseViabilidadTecnicaRcoa o where o.id = :id")
					.setParameter("id", id).getSingleResult();	
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<FaseViabilidadTecnicaRcoa> obtenerfasesPorRegistroAmbiental(RegistroAmbientalRcoa registro){
		List<FaseViabilidadTecnicaRcoa> lista = new ArrayList<FaseViabilidadTecnicaRcoa>();
		try {
			Query sql = crudServiceBean.getEntityManager().createQuery("Select f from FaseViabilidadTecnicaRcoa f "
					+ "where f.viabilidadTecnicaProyecto.registroAmbientalRcoa.id = :id and f.estado= true ");
			sql.setParameter("id", registro.getId());
			
			lista = (List<FaseViabilidadTecnicaRcoa>)sql.getResultList();
			
			return lista;	
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;
	}

}
