package ec.gob.ambiente.rcoa.consultores.facade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.consultores.model.ConsultorRcoa;
import ec.gob.ambiente.rcoa.consultores.model.EquipoMultidisciplinarioConsultor;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class ConsultorRcoaFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	@SuppressWarnings("unchecked")
	public List<ConsultorRcoa> consultoresCalificadosRcoa(){
		List<ConsultorRcoa> consultor= new  ArrayList<ConsultorRcoa>();
		consultor= (List<ConsultorRcoa>) crudServiceBean.getEntityManager().createQuery("select distinct c From ConsultorRcoa c where c.estadoCertificado in (1) and estado = true ").getResultList();		
		return consultor;
	}

	@SuppressWarnings("unchecked")
	public ConsultorRcoa getConsultorCalificado(Integer idOrganizacion) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idOrganizacion", idOrganizacion);
		List<ConsultorRcoa> lista = (ArrayList<ConsultorRcoa>) crudServiceBean.findByNamedQuery(ConsultorRcoa.GET_CONSULTOR_POR_ORGANIZACION, parameters);
		if (lista != null && lista.size() > 0) {
			return lista.get(0);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public ConsultorRcoa getConsultorPorRuc(String nombre) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("nombre", nombre);
		List<ConsultorRcoa> lista = (ArrayList<ConsultorRcoa>) crudServiceBean.findByNamedQuery(ConsultorRcoa.GET_CONSULTOR_POR_RUC, parameters);
		if (lista != null && lista.size() > 0) {
			return lista.get(0);
		}
		return null;
	}


	@SuppressWarnings("unchecked")
	public List<EquipoMultidisciplinarioConsultor> getEquipoMultidisciplinario(Integer idConsultor) {
		
		List<EquipoMultidisciplinarioConsultor> resultList=new ArrayList<EquipoMultidisciplinarioConsultor>();
		try {
			Query query = crudServiceBean.getEntityManager().createQuery("SELECT o FROM EquipoMultidisciplinarioConsultor o JOIN FETCH o.consultor WHERE o.consultor.id=:idConsultor AND o.estado=true ");
			query.setParameter("idConsultor", idConsultor);
			resultList=(List<EquipoMultidisciplinarioConsultor>)query.getResultList();
		} catch (NoResultException nre) {
		} catch (Exception e) {
			e.printStackTrace();
			
		}		
		return resultList;
	}
	
	@SuppressWarnings("unchecked")
	public EquipoMultidisciplinarioConsultor getConsultorPorIdentificacion(String cedula) {
		
		List<EquipoMultidisciplinarioConsultor> resultList=new ArrayList<EquipoMultidisciplinarioConsultor>();
		try {
			Query query = crudServiceBean.getEntityManager().createQuery("SELECT o FROM EquipoMultidisciplinarioConsultor o WHERE o.identificador=:identificador and o.estado=true ");
			query.setParameter("identificador", cedula);
			resultList=(List<EquipoMultidisciplinarioConsultor>)query.getResultList();
			
			if(resultList != null && !resultList.isEmpty()) {
				return resultList.get(0);
			}
			
		} catch (NoResultException nre) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			
		}		
		return null;
	}
}