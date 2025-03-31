package ec.gob.ambiente.rcoa.viabilidadTecnica.facade;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.viabilidadTecnica.model.AspectoViabilidadTecnica;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Usuario;

@Stateless
public class AspectoViabilidadTecnicaFacade {
	
	@EJB
    private CrudServiceBean crudServiceBean;
	
	public void guardar(AspectoViabilidadTecnica obj, Usuario usuario){
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
	
	public AspectoViabilidadTecnica buscarPorId(Integer id){
		try {
			return (AspectoViabilidadTecnica)crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM AspectoViabilidadTecnica o where o.id = :id")
					.setParameter("id", id).getSingleResult();			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<AspectoViabilidadTecnica> obtenerAspectoAmbientalPorPlan(Integer planId){
		List<AspectoViabilidadTecnica> lista = new ArrayList<>();
		try {
			Query sql = crudServiceBean.getEntityManager().createQuery("SELECT m FROM AspectoViabilidadTecnica m where m.estado = true "
					+ "and m.planManejoAmbientalPma.id = :planId and m.manejaDesechos = true");
			sql.setParameter("planId", planId);
			
			lista = (List<AspectoViabilidadTecnica>)sql.getResultList();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;
	}

}
