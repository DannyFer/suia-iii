package ec.gob.ambiente.suia.eia.ficha.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Consultor;
import ec.gob.ambiente.suia.exceptions.ServiceException;

@Stateless
public class ConsultorCalificadoService implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4033202364040695039L;
	
	@EJB
	private CrudServiceBean crudServiceBean;

	public Consultor consultorCalificado(int id) {
		return crudServiceBean.find(Consultor.class, id);
	}

	/**
	 * Lista de todos los consultores calificados
	 * @return
	 */
	public List<Consultor> consultoresCalificados() throws ServiceException{
		return (List<Consultor>) crudServiceBean.findAll(Consultor.class);
	}
	
	public List<Consultor> consultorUnicoCalificado(Integer id) {
		List<Consultor> consultor= new  ArrayList<Consultor>();
		consultor= (List<Consultor>) crudServiceBean.getEntityManager().createQuery("select c From Consultor c where c.usuarioCalificado=:id")
				.setParameter("id", id).getResultList();		
		return consultor;
	}
	
	public List<Consultor> consultoresCalificadosVigentes() {
		List<Consultor> consultor= new  ArrayList<Consultor>();
		consultor= (List<Consultor>) crudServiceBean.getEntityManager().createQuery("select c From Consultor c where c.estadoCertificado in (1) ")
				.getResultList();		
		return consultor;
	}
	
	public Consultor buscarConsultor(Integer id) {
		try {
			Consultor consultor= new  Consultor();
			consultor= (Consultor) crudServiceBean.getEntityManager().createQuery("select c From Consultor c where c.id=:id")
					.setParameter("id", id).getSingleResult();		
			return consultor;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public Consultor buscarConsultorPorRuc(String ruc) {
		try {
			Consultor consultor= new  Consultor();
			List<Consultor> consultores= new  ArrayList<Consultor>();
			consultores= (List<Consultor>) crudServiceBean.getEntityManager().createQuery("select c From Consultor c where c.ruc=:ruc")
					.setParameter("ruc", ruc).getResultList();		
			if(consultores != null && consultores.size() > 0)
				consultor = consultores.get(0);
			return consultor;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
