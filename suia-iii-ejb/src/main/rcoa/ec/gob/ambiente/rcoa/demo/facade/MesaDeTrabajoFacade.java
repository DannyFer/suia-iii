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
public class MesaDeTrabajoFacade {

	@EJB
	private CrudServiceBean crudServiceBean;
	
	@SuppressWarnings("unchecked")
	public List<MesaDeTrabajo> obtenerMesaDeTrabajo() {

		List<MesaDeTrabajo> list = new ArrayList<MesaDeTrabajo>();
		String queryString = "select * from public.demo_mesatrabajo dm where mesa_esmesa = true order by mesa_id";
		try {
			Query sql = crudServiceBean.getEntityManager().createNativeQuery(queryString);
//			Query sql = crudServiceBean.getEntityManager().createQuery("SELECT i FROM MesaDeTrabajo i");
//			Query sql = crudServiceBean.getEntityManager().createQuery("SELECT o FROM OpcionesEncuesta o WHERE o.estado= true order by id");

			list = (List<MesaDeTrabajo>) sql.getResultList();

		} catch (NoResultException nre) {
			// TODO: handle exception
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;

	}
	
	@SuppressWarnings("unchecked")
	public List<String> obtenerMesaDeTrabajo2() {

		List<String> list = new ArrayList<String>();
		String queryString = "select mesa_name from public.demo_mesatrabajo dm where mesa_esmesa = true order by mesa_id";
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
	
	@SuppressWarnings("unchecked")
	public List<String> obtenerArticulos(String mesaSeleccionada) {

		List<String> list = new ArrayList<String>();
		String queryString = "select mesa_name from public.demo_mesatrabajo dm where mesa_idmesa  = :mesaSeleccionada order by mesa_id asc";
		try {
			Query sql = crudServiceBean.getEntityManager().createNativeQuery(queryString);
			sql.setParameter("mesaSeleccionada",mesaSeleccionada);
			
			list = (List<String>) sql.getResultList();

		} catch (NoResultException nre) {
			// TODO: handle exception
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;

	}
	
	@SuppressWarnings("unchecked")
	public String obtenerArticulosDescripcion(String articuloSeleccionado) {

		String result = "";
		String queryString = "select mesa_detallearticulo from public.demo_mesatrabajo dm where mesa_idarticulo  = :articuloSeleccionado order by mesa_id";
		try {
			Query sql = crudServiceBean.getEntityManager().createNativeQuery(queryString);
			sql.setParameter("articuloSeleccionado",articuloSeleccionado);
			
			result = sql.getResultList().toString();

		} catch (NoResultException nre) {
			// TODO: handle exception
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;

	}
	
}
