package ec.gob.ambiente.rcoa.demo.facade;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.demo.model.MesaDeTrabajo;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class NotaFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	@SuppressWarnings("unchecked")
	public List<String> obtenerNotas() {

		List<String> list = new ArrayList<String>();
		String queryString = "select nota_descripcion from public.demo_notas";
		try {
			Query sql = crudServiceBean.getEntityManager().createNativeQuery(queryString);
//			Query sql = crudServiceBean.getEntityManager().createQuery("SELECT i FROM MesaDeTrabajo i");
//			Query sql = crudServiceBean.getEntityManager().createQuery("SELECT o FROM OpcionesEncuesta o WHERE o.estado= true order by id");

			list = (List<String>) sql.getResultList();

		} catch (NoResultException nre) {
			// TODO: handle exception
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;

	}

}
