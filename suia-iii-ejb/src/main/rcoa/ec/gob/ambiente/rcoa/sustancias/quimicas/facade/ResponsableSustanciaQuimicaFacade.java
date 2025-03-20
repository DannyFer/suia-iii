package ec.gob.ambiente.rcoa.sustancias.quimicas.facade;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.sustancias.quimicas.model.RegistroSustanciaQuimica;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.ResponsableSustanciaQuimica;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Usuario;


@Stateless
public class ResponsableSustanciaQuimicaFacade {

	@EJB
	private CrudServiceBean crudServiceBean;
	
	public void guardar(ResponsableSustanciaQuimica obj, Usuario usuario){			
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
	public List<ResponsableSustanciaQuimica> obtenerResponsablePorRsq(RegistroSustanciaQuimica registroSustanciaQuimica) {
		List<ResponsableSustanciaQuimica> resultList=new ArrayList<>();
		try {
			Query query = crudServiceBean.getEntityManager().createQuery("SELECT o FROM ResponsableSustanciaQuimica o WHERE o.estado=true and o.registroSustanciaQuimica.id=:idRSQ ORDER BY 1");
			query.setParameter("idRSQ", registroSustanciaQuimica.getId());			
			resultList= (List<ResponsableSustanciaQuimica>)query.getResultList();
			for (ResponsableSustanciaQuimica item : resultList) {
				if(item.getLugar()!=null && !item.getLugar().isEmpty()) {
					String[] lugares=item.getLugar().split(";");
					item.setLugaresArray(lugares);
				}				
			}
		} catch (NoResultException nre) {
			//TODO
		} catch (Exception e) {
			e.printStackTrace();			
		}		
		return resultList;
	}
}
