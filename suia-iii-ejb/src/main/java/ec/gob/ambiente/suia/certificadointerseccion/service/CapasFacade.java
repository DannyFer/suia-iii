package ec.gob.ambiente.suia.certificadointerseccion.service;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Capa;
import ec.gob.ambiente.suia.exceptions.ServiceException;

@Stateless
public class CapasFacade {

	@EJB
	CrudServiceBean crudServiceBean;

	/**
	 * Método que retorna una capa
	 * 
	 * @param id
	 *            de la capa
	 * @return
	 */
	public Capa getCapas(Integer idCapa) {
		return crudServiceBean.find(Capa.class, idCapa);
	}
	
	@SuppressWarnings("unchecked")
	public List<Capa> listaCapas() throws ServiceException {
	
		List<Capa> lista = crudServiceBean.getEntityManager().createQuery("Select c FROM Capa c").getResultList();
		
		return lista;
	}
	
	

}
