package ec.gob.ambiente.rcoa.sustancias.quimicas.facade;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.sustancias.quimicas.model.SolicitudImportacionRSQExtVue;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class SolicitudImportacionRSQExtVueFacade {
	
	@EJB
    private CrudServiceBean crudServiceBean;
	
	@EJB
	private SecuenciasFacade secuenciasFacade;
	
	public SolicitudImportacionRSQExtVue buscarPorTramiteVue(String tramite){
		try {
			
			Query query = crudServiceBean.getEntityManager().createQuery("SELECT s from SolicitudImportacionRSQExtVue s where s.tramite = :tramite and s.estado = true");
			query.setParameter("tramite", tramite);
			List<SolicitudImportacionRSQExtVue> lista= new ArrayList<>(); 
			lista.add((SolicitudImportacionRSQExtVue)query.getSingleResult());
			
			if(lista != null && !lista.isEmpty()){
				return lista.get(0);
			}else 
				return null;
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}	
	
	public SolicitudImportacionRSQExtVue buscarPorId(Integer id){
		try {
			
			Query query = crudServiceBean.getEntityManager().createQuery("SELECT s from SolicitudImportacionRSQExtVue s where s.idImportRequestExt = :id");
			query.setParameter("id", id);
			List<SolicitudImportacionRSQExtVue> lista= (List<SolicitudImportacionRSQExtVue>)query.getResultList();
			
			if(lista != null && !lista.isEmpty()){
				return lista.get(0);
			}else 
				return null;
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
