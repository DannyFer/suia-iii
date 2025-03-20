package ec.gob.ambiente.suia.survey;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class SurveyAcceptanceFacade {
	

	@EJB
	private CrudServiceBean crudServiceBean;
	
	
	public void guardar(SurveyAcceptance objeto){
		try {			
			crudServiceBean.saveOrUpdate(objeto);
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	@SuppressWarnings("unchecked")
	public List<SurveyAcceptance> buscarAceptacion(String tramite, String proceso){
		List<SurveyAcceptance> lista = new ArrayList<>();
		
		try {
			
			lista = (List<SurveyAcceptance>) crudServiceBean.getEntityManager()
					.createQuery("Select s from SurveyAcceptance s where s.estado = true and s.tramite = :tramite and s.nombreProceso = :proceso")
					.setParameter("tramite", tramite).setParameter("proceso", proceso).getResultList();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;
	}

}
