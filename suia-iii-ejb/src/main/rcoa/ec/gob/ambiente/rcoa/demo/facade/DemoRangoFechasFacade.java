package ec.gob.ambiente.rcoa.demo.facade;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.demo.model.DemoRangoFechas;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class DemoRangoFechasFacade {

	@EJB
	private CrudServiceBean crudServiceBean;

	@SuppressWarnings("unchecked")
	public List<DemoRangoFechas> rangoFechas() {

		List<DemoRangoFechas> list = new ArrayList<DemoRangoFechas>();
		try {

			Query sql = crudServiceBean.getEntityManager().createQuery("SELECT i FROM DemoRangoFechas i order by i.id");
//			Query sql = crudServiceBean.getEntityManager().createQuery("SELECT o FROM OpcionesEncuesta o WHERE o.estado= true order by id");

			list = (List<DemoRangoFechas>) sql.getResultList();

		} catch (NoResultException nre) {
			// TODO: handle exception
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;

	}

	
	@SuppressWarnings("unchecked")
	public List<DemoRangoFechas> buscarUbicacionGeograficaPorIdRangoFechas() {

		List<DemoRangoFechas> ubicacionesHabilitadas = new ArrayList<DemoRangoFechas>();
		
		Query sql = crudServiceBean.getEntityManager().createQuery(
				"SELECT i FROM DemoRangoFechas i WHERE  now() BETWEEN i.fecha_inicio AND i.fecha_fin+1");
		
		
		
	
		ubicacionesHabilitadas = (List<DemoRangoFechas>) sql.getResultList();
		
		return ubicacionesHabilitadas;

	}
	

}
