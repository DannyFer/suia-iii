package ec.gob.ambiente.rcoa.viabilidadTecnica.facade;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.viabilidadTecnica.model.ViabilidadTecnica;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Usuario;

@Stateless
public class ViabilidadTecFacade {

	@EJB
	private CrudServiceBean crudServiceBean;
	
	public void guardarViabilidadTecnica(ViabilidadTecnica viabilidad) {			
		crudServiceBean.saveOrUpdate(viabilidad);
	}

	@SuppressWarnings("unchecked")
	public List<ViabilidadTecnica> getListaViabilidadPorUsuario(Integer user) {		
		List<ViabilidadTecnica> listaViabilidad = new ArrayList<ViabilidadTecnica>();
		try {
			Query sql = crudServiceBean.getEntityManager().createQuery("SELECT v FROM ViabilidadTecnica v where v.usuario.id = :user and v.estado = true order by id desc ");
			sql.setParameter("user", user);
			if(sql.getResultList().size() > 0)
				listaViabilidad = sql.getResultList();

			if (listaViabilidad == null || listaViabilidad.isEmpty()) {
				return null;
			} else {
				return listaViabilidad;
			}
		} catch (Exception e) {
			return null;
		}
	}
	
}
