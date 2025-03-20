package ec.gob.ambiente.suia.desechoPeligrosoFisico.dao;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.desechosfisicos.Processed;

/**
 * 
 * @author karla.carvajal
 *
 */
@Stateless
public class ProcessedFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	/**
	 * Obtiene un listado de trámites por compañía y tipo
	 * @param type El tipo de búsqueda (Instalación, Compañía, Tipo de Trámite)
	 * @param search El parámetro de búsqueda
	 * @return List<Processed>
	 */
	public List<Processed> getProcessedByCompanyTypeInstallation(int type,  String search)
	{			
		List<Processed> processeds = new ArrayList<Processed>();
		if(type == 1)
		{
			String search_ = "%" + search + "%";
			TypedQuery<Processed> query = crudServiceBean.getEntityManager().createQuery("select p from Processed p where upper(p.procProponent) like :search and p.procStatus = true order by p.procProponent", Processed.class);
			query.setParameter("search", search_.toUpperCase());
			
			processeds = (List<Processed>) query.getResultList();
		}
		
		if(type == 2)
		{
			TypedQuery<Processed> query = crudServiceBean.getEntityManager().createQuery("select p from Processed p where p.installation.instId = :search and p.procStatus = true order by p.procProponent", Processed.class);
			query.setParameter("search", Integer.parseInt(search));
			
			processeds = (List<Processed>) query.getResultList();
		}
		
		if(type == 3)
		{
			TypedQuery<Processed> query = crudServiceBean.getEntityManager().createQuery("select p from Processed p where p.type.cataId = :search and p.procStatus = true order by p.procProponent", Processed.class);
			query.setParameter("search", Integer.parseInt(search));
			
			processeds = (List<Processed>) query.getResultList();
		}
		
		return processeds;
	}
}