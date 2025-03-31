package ec.gob.ambiente.suia.survey;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.configuration.service.ServiceBean;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class SurveyResponseFacade {
	@EJB
	private ServiceBean serviceBean;

	@EJB
	private CrudServiceBean crudServiceBean;

	@SuppressWarnings("unchecked")
	public boolean findByProject(String codigoProyecto) {

		boolean resultado = false;
		List<SurveyResponse> query = crudServiceBean
				.getEntityManager()
				.createNativeQuery(
						"select * from suia_survey.survey_response where srvrsp_project =:codigoProyecto", SurveyResponse.class)
				.setParameter("codigoProyecto", codigoProyecto)
				.getResultList();

		if (query.size() > 0) {
			resultado = true;
		}

		return resultado;

	}
	
	@SuppressWarnings("unchecked")
	public boolean findByProjectApp(String codigoProyecto, String app) {

		boolean resultado = false;
		List<SurveyResponse> query = crudServiceBean
				.getEntityManager()
				.createNativeQuery(
						"select * from suia_survey.survey_response where srvrsp_project =:codigoProyecto and srvrsp_app = :app", SurveyResponse.class)
				.setParameter("codigoProyecto", codigoProyecto).setParameter("app", app)
				.getResultList();

		if (query.size() > 0) {
			resultado = true;
		}

		return resultado;

	}

}
