package ec.gob.ambiente.suia.prevencion.categoria2.service;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Coordenada;

/**
 * 
 * @author karla.carvajal
 * 
 */
@Stateless
public class CoordenadaPmaServiceBean {

	@EJB
	private CrudServiceBean crudServiceBean;
	
	
	@SuppressWarnings("unchecked")
	public List<Coordenada> getCoordenadasProyectoPma(Integer idProyecto)
	{		
		List<Coordenada> coordenadas = new ArrayList<Coordenada>();
		coordenadas = (List<Coordenada>) crudServiceBean.getEntityManager()
				.createQuery("From Coordenada c where c.formasProyecto.idProyecto =:idProyecto and c.estado = true")
				.setParameter("idProyecto", idProyecto).getResultList();				
		
		return coordenadas;	
	}
}