package ec.gob.ambiente.rcoa.facade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.model.EspecialistaAmbiental;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class EspecialistaAmbientalCoaFacade {
	
	@EJB
    private CrudServiceBean crudServiceBean;
	
	public EspecialistaAmbiental guardar(EspecialistaAmbiental especialista) {        
    	return crudServiceBean.saveOrUpdate(especialista);        
	}
	
	@SuppressWarnings("unchecked")
	public EspecialistaAmbiental buscarEspecialistaPorNumregistroSenecyt(String identificacion, String registroNumero)
	{
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("identificacion", identificacion);
		parameters.put("numeroregistro", registroNumero);
		
		List<EspecialistaAmbiental> lista = (List<EspecialistaAmbiental>) crudServiceBean
					.findByNamedQuery(
							EspecialistaAmbiental.GET_POR_NUMEROREGISTRO_POR_IDENTIFICACION,
							parameters);
		if(lista.size() > 0 ){
			return lista.get(0);
		}
		return  null;
	}
	
	public EspecialistaAmbiental getById(Integer idEspecialistaAmbiental) {
		EspecialistaAmbiental result = new EspecialistaAmbiental();
		Query sql = crudServiceBean.getEntityManager().createQuery("SELECT e FROM EspecialistaAmbiental e WHERE e.id=:idEspecialistaAmbiental");
		sql.setParameter("idEspecialistaAmbiental", idEspecialistaAmbiental);
		if (sql.getResultList().size() > 0)
			result = (EspecialistaAmbiental) sql.getResultList().get(0);
		return result;
	}

}