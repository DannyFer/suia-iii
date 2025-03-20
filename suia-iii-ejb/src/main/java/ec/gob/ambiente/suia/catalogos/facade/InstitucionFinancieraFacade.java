package ec.gob.ambiente.suia.catalogos.facade;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.InstitucionFinanciera;

@Stateless
public class InstitucionFinancieraFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;

	public List<InstitucionFinanciera> obtenerListaInstitucionesFinancieras() {
		@SuppressWarnings("unchecked")
		List<InstitucionFinanciera> result = (List<InstitucionFinanciera>) crudServiceBean
				.findByNamedQuery(InstitucionFinanciera.FIND_ALL, null);
		if (result != null && !result.isEmpty()) {
			return result;
		}
		return null;
	}
	/**
     * Nombre:SUIA
     * Descripción: listado de instituciones financiera entes
     * ParametrosIngreso:
     * PArametrosSalida:
     * Fecha:17/09/2015
     * */
	
	@SuppressWarnings("unchecked")
	public List<InstitucionFinanciera> obtenerEntesAcreditados()
			throws Exception {
		List<InstitucionFinanciera> financieras = crudServiceBean
				.getEntityManager()
				.createQuery(
						"SELECT if FROM InstitucionFinanciera if where if.institucionFinanciera.id=4")
				.getResultList();
		if (financieras != null && !financieras.isEmpty()) {
			return financieras;
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<InstitucionFinanciera> obtenerListaInstitucionesFinancieras(
			String nombreUbicacion, String codigoInec) {
		List<InstitucionFinanciera> result = (List<InstitucionFinanciera>) crudServiceBean
				.findByNamedQuery(InstitucionFinanciera.FIND_MAIN, null);
		if (result != null && !result.isEmpty()) {
			List<InstitucionFinanciera> tesoreriasEntesAcr = crudServiceBean
					.getEntityManager()
					.createQuery(
							"SELECT if FROM InstitucionFinanciera if where UPPER(if.nombreInstitucion) LIKE UPPER(:nombreUbicacion) "
									+ "AND if.institucionFinanciera.id=5 AND split_part(if.codigoInstitucion,'-',2) LIKE :codigoInec")
					.setParameter("nombreUbicacion", "%" + nombreUbicacion)
					.setParameter("codigoInec", codigoInec+"%").getResultList();
			if (tesoreriasEntesAcr != null && !tesoreriasEntesAcr.isEmpty()) {
				for (InstitucionFinanciera insfin : tesoreriasEntesAcr) {
					result.add(insfin);
				}
			}
			return result;
		}
		return null;
	}
	
	/**
	  * FIN listado de instituciones financiera entes
	  */
	

	
	/**
	 * Walter
	 * se realizo la separacion por entes acreditados y planta central.
	 */
	@SuppressWarnings("unchecked")
	public List<InstitucionFinanciera> obtenerListaInstitucionesFinancierasPC() {
		List<InstitucionFinanciera> result = (List<InstitucionFinanciera>) crudServiceBean
				.findByNamedQuery(InstitucionFinanciera.FIND_MAIN, null);		
			return result;		
	}

	@SuppressWarnings("unchecked")
	public List<InstitucionFinanciera> obtenerInstitucionFinancieraPorId(Integer idBanco) {
		List<InstitucionFinanciera> result = crudServiceBean
				.getEntityManager()
				.createQuery(
						"SELECT if FROM InstitucionFinanciera if where if.id = :idInstitucion AND if.estado=true")
				.setParameter("idInstitucion", idBanco).getResultList();
	return result;
	}
	
	
	@SuppressWarnings("unchecked")
	public List<InstitucionFinanciera> obtenerInstitucionFinancieraPagoOnline(String nameInstitute) {
		List<InstitucionFinanciera> result = crudServiceBean
				.getEntityManager()
				.createQuery(
						"SELECT if FROM InstitucionFinanciera if where if.nombreInstitucion = :nameInstitute AND if.estado=true")
				.setParameter("nameInstitute", nameInstitute).getResultList();
	return result;
	}
	
	
	@SuppressWarnings("unchecked")
	public List<InstitucionFinanciera> obtenerListaInstitucionesFinancierasProv(
			String nombreUbicacion, String codigoInec) {
		List<InstitucionFinanciera> result= new ArrayList<InstitucionFinanciera>();
			List<InstitucionFinanciera> tesoreriasEntesAcr = crudServiceBean
					.getEntityManager()
					.createQuery(
							"SELECT if FROM InstitucionFinanciera if where UPPER(if.nombreInstitucion) LIKE UPPER(:nombreUbicacion) "
									+ "AND UPPER(if.nombreInstitucion) LIKE UPPER('%prov%') "
									+ "AND if.institucionFinanciera.id=5 AND split_part(if.codigoInstitucion,'-',2) LIKE :codigoInec")
					.setParameter("nombreUbicacion", "%" + nombreUbicacion)
					.setParameter("codigoInec", codigoInec+"%").getResultList();
			if (tesoreriasEntesAcr != null && !tesoreriasEntesAcr.isEmpty()) {
				for (InstitucionFinanciera insfin : tesoreriasEntesAcr) {
					result.add(insfin);
				}
				return result;
			}
			return null;			
	}
	@SuppressWarnings("unchecked")
	public List<InstitucionFinanciera> obtenerListaInstitucionesFinancierasMuni(
			String nombreUbicacion, String codigoInec) {
		List<InstitucionFinanciera> result= new ArrayList<InstitucionFinanciera>();
			List<InstitucionFinanciera> tesoreriasEntesAcr = crudServiceBean
					.getEntityManager()
					.createQuery(
							"SELECT if FROM InstitucionFinanciera if where "
									+ "UPPER(if.nombreInstitucion) LIKE UPPER('%muni%') "
									+ "AND "
									+ "if.institucionFinanciera.id=5 AND split_part(if.codigoInstitucion,'-',2) LIKE :codigoInec")
					.setParameter("codigoInec", codigoInec+"%").getResultList();
			if (tesoreriasEntesAcr != null && !tesoreriasEntesAcr.isEmpty()) {
				for (InstitucionFinanciera insfin : tesoreriasEntesAcr) {
					result.add(insfin);
				}
				return result;
			}
			return null;			
	}
	/**
	 * fin de la separacion por entes acreditados y planta central. 
	 */
	
	@SuppressWarnings("unchecked")
	public List<InstitucionFinanciera> obtenerPorNombre(String nombre) {
		
		List<InstitucionFinanciera> result = crudServiceBean
					.getEntityManager()
					.createQuery(
							"SELECT if FROM InstitucionFinanciera if where UPPER(if.nombreInstitucion) LIKE UPPER(:nombre) AND if.estado=true")
					.setParameter("nombre", "%" + nombre).getResultList();
		
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<InstitucionFinanciera> obtenerInstitucionesNut()throws Exception {
		List<InstitucionFinanciera> financieras = crudServiceBean
				.getEntityManager().createQuery(
						"SELECT if FROM InstitucionFinanciera if where  if.convenioNut is true ORDER BY if.nombreInstitucion)")
				.getResultList();
		if (financieras != null && !financieras.isEmpty()) {
			return financieras;
		}
		return null;
	}
}
