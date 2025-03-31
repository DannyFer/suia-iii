package ec.gob.ambiente.suia.eia.ficha.facade;

import java.io.Serializable;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.ConsultorNoCalificado;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.eia.ficha.service.ConsultorNoCalificadoService;

/**
 * 
 * @author lili
 *
 */
@Stateless
public class ConsultorNoCalificadoFacade implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6272053392145052473L;

	@EJB
	private ConsultorNoCalificadoService consultorNoCalificadoService;
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	/**
	 * Lista de consultores no calificados por EIA
	 * @param estudio
	 * @return
	 * @throws Exception
	 */
	public List<ConsultorNoCalificado> listaConsultoresNoCalificadosPorEia(EstudioImpactoAmbiental estudio) throws Exception{
		return consultorNoCalificadoService.consultoresNoCalificadosPorEia(estudio);
	}
	
	public List<ConsultorNoCalificado> buscarConsultoresNoCalificadosHistorico(Integer idHistorico){
		try{
			Query query = crudServiceBean.getEntityManager().createQuery(
					"SELECT c FROM ConsultorNoCalificado c WHERE c.idHistorico = :idHistorico order by id desc");
			query.setParameter("idHistorico", idHistorico);
			
			return (List<ConsultorNoCalificado>) query.getResultList();
			
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public ConsultorNoCalificado buscarConsultorNoCalificado(Integer idConsultor) throws Exception{
		return consultorNoCalificadoService.consultorNoCalificado(idConsultor);
	}
	
	public List<ConsultorNoCalificado> buscarConsultorNoCalificadoHistorico(Integer idHistorico, Integer numeroNotificacion){
		try{
			Query query = crudServiceBean.getEntityManager().createQuery(
					"SELECT c FROM ConsultorNoCalificado c WHERE c.idHistorico = :idHistorico "
					+ "and c.numeroNotificacion = :numeroNotificacion order by id desc");
			query.setParameter("idHistorico", idHistorico);
			query.setParameter("numeroNotificacion", numeroNotificacion);
			
			return (List<ConsultorNoCalificado>) query.getResultList();
			
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public List<ConsultorNoCalificado> buscarConsultoresNoCalificadosModificados(Integer estudio){
		try{
			Query query = crudServiceBean.getEntityManager().createQuery(
					"SELECT c FROM ConsultorNoCalificado c WHERE c.estudioImpactoAmbiental.id = :estudio "
					+ "order by id asc");
			query.setParameter("estudio", estudio);
			
			return (List<ConsultorNoCalificado>) query.getResultList();
			
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
}
