package ec.gob.ambiente.rcoa.facade;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.model.EncuestaProyecto;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class EncuestaFacade {

	@EJB
	private CrudServiceBean crudServiceBean;

	public EncuestaProyecto guardar(EncuestaProyecto encuesta) {
		return crudServiceBean.saveOrUpdate(encuesta);
	}

	@SuppressWarnings("unchecked")
	public List<EncuestaProyecto> obtenerListaEncuestaProyecto(Integer idUsuario, Integer idProyecto) {

		List<EncuestaProyecto> list = new ArrayList<EncuestaProyecto>();
		try {

			Query sql = crudServiceBean.getEntityManager().createQuery(
					"SELECT i FROM EncuestaProyecto i WHERE i.usuario.id = :idUsuario and i.proyectoLicenciamientoAmbiental.id = :idProyecto order by i.opcionesEncuesta.id");
//			Query sql = crudServiceBean.getEntityManager().createQuery("SELECT o FROM OpcionesEncuesta o WHERE o.estado= true order by id");
			sql.setParameter("idUsuario", idUsuario);
			sql.setParameter("idProyecto", idProyecto);

			list = (List<EncuestaProyecto>) sql.getResultList();

		} catch (NoResultException nre) {
			// TODO: handle exception
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;

	}

	@SuppressWarnings("unchecked")
	public List<EncuestaProyecto> obtenerListaEncuestaProyectoRCOA(Integer idUsuario, Integer idProyectoRCOA) {

		List<EncuestaProyecto> list = new ArrayList<EncuestaProyecto>();
		try {

			Query sql = crudServiceBean.getEntityManager().createQuery("SELECT o FROM EncuestaProyecto o WHERE o.usuario.id = :idUsuario and o.proyectoLicenciaCoa.id = :idProyectoRCOA order by o.opcionesEncuesta.id");
			sql.setParameter("idUsuario", idUsuario);
			sql.setParameter("idProyectoRCOA", idProyectoRCOA);

			list = (List<EncuestaProyecto>) sql.getResultList();

		} catch (NoResultException nre) {
			// TODO: handle exception
			System.out.println("NRE " + nre);
		} catch (Exception e) {
			System.out.println("E "+e);
			e.printStackTrace();
		}

		return list;
	}
	
//	@SuppressWarnings("unchecked")
//	public Boolean existenSugerencias(Integer idProyectoRCOA) {
//		
//		boolean existeEncuesta = false;
//		List<EncuestaProyecto> list = new ArrayList<EncuestaProyecto>();
//		try {
//
//			Query sql = crudServiceBean.getEntityManager().createQuery("SELECT o FROM EncuestaProyecto o WHERE  o.proyectoLicenciaCoa.id = 383125 or o.proyectoLicenciamientoAmbiental.id = 383125");
//			sql.setParameter("idProyectoRCOA", idProyectoRCOA);
//
//			list = (List<EncuestaProyecto>) sql.getResultList();
//
//		} catch (NoResultException nre) {
//			// TODO: handle exception
//			System.out.println("NRE " + nre);
//		} catch (Exception e) {
//			System.out.println("E "+e);
//			e.printStackTrace();
//		}
//		
//		existeEncuesta = (list.size()>0)?true:false;
//
//		return existeEncuesta;
//	}


	public static String fechaEncuesta() {
		// String fecha = encuesta.getFecha();
		String pattern = "dd-MM-yyyy";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		String fecha = simpleDateFormat.format(new Date());

		return fecha;

	}

}
