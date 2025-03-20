package ec.gob.ambiente.retce.services;


import javax.ejb.EJB;
import javax.ejb.Stateless;


import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.TipoSector;


@Stateless
public class TipoSectorFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	public TipoSector findByName(String nombre){
		try {
			TipoSector tipoSector = (TipoSector) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM TipoSector o where o.nombre = :nombre")
					.setParameter("nombre", nombre).getSingleResult();
			return tipoSector;
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return null;
	}

}
