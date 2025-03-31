package ec.gob.ambiente.suia.ubicaciongeografica.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.Region;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.exceptions.ServiceException;

@Stateless
public class UbicacionGeograficaServiceBean {

	@EJB
	private CrudServiceBean crudServiceBean;

	@SuppressWarnings("unchecked")
	public List<UbicacionesGeografica> buscarUbicacionGeograficaPorId(Integer id) {

		List<UbicacionesGeografica> ubicaciones = crudServiceBean.getEntityManager()
				.createQuery(" FROM UbicacionesGeografica u where u.ubicacionesGeografica.id =:id order by u.nombre")
				.setParameter("id", id).getResultList();
		return ubicaciones;

	}

	@SuppressWarnings("unchecked")
	public List<UbicacionesGeografica> buscarUbicacionGeograficaPorPadre(UbicacionesGeografica padre) {

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("padre", padre);
		return (List<UbicacionesGeografica>) crudServiceBean.findByNamedQuery(UbicacionesGeografica.FIND_BY_FATHER,
				parameters);
		// List<UbicacionesGeografica> ubicaciones = crudServiceBean
		// .getEntityManager()
		// .createQuery(
		// " FROM UbicacionesGeografica u where u.ubicacionesGeografica.id =:id ")
		// .setParameter("id", id).getResultList();
		// return ubicaciones;

	}
	
	@SuppressWarnings("unchecked")
	public List<UbicacionesGeografica> buscarUbicacionGeograficaPadre(UbicacionesGeografica hijo) {

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("hijo", hijo);
		return (List<UbicacionesGeografica>) crudServiceBean.findByNamedQuery(UbicacionesGeografica.FIND_FATHER,
				parameters);
		// List<UbicacionesGeografica> ubicaciones = crudServiceBean
		// .getEntityManager()
		// .createQuery(
		// " FROM UbicacionesGeografica u where u.ubicacionesGeografica.id =:id ")
		// .setParameter("id", id).getResultList();
		// return ubicaciones;

	}

	public UbicacionesGeografica buscarUbicacionGeograficaPorNombre(String nombre) {
		UbicacionesGeografica ubicacion = (UbicacionesGeografica) crudServiceBean.getEntityManager()
				.createQuery(" FROM UbicacionesGeografica u where u.nombre =:nombre ").setParameter("nombre", nombre)
				.getSingleResult();
		return ubicacion;

	}

	@SuppressWarnings("unchecked")
	public List<UbicacionesGeografica> listarPais() {
		List<UbicacionesGeografica> ubicaciones = crudServiceBean.getEntityManager()
				.createQuery(" FROM UbicacionesGeografica u where u.ubicacionesGeografica.id is null ").getResultList();
		return ubicaciones;

	}

	public boolean isCanton(String codigoInec) {
		return codigoInec.trim().length() == 4 ? true : false;
	}

	public boolean isParroquia(String codigoInec) {
		return codigoInec.trim().length() == 6 ? true : false;
	}

	public boolean isProvincia(String codigoInec) {
		return codigoInec.trim().length() == 2 ? true : false;
	}

	public UbicacionesGeografica buscarUbicacionPorCodigoInec(String codigoInec) {
		UbicacionesGeografica ubicacion = (UbicacionesGeografica) crudServiceBean.getEntityManager()
				.createQuery(" FROM UbicacionesGeografica u where u.codificacionInec =:codificacionInec ")
				.setParameter("codificacionInec", codigoInec).getResultList().get(0);
		return ubicacion;
	}

	public UbicacionesGeografica buscarUbicacionGeograficaSuperior(String codigoInec) {
		String codigoInecSuperior;
		codigoInec = codigoInec.trim();
		if (codigoInec.length() > 2) {
			codigoInecSuperior = codigoInec.substring(0, codigoInec.length() - 2);
			return buscarUbicacionPorCodigoInec(codigoInecSuperior);
		}
		return null;

	}

	public UbicacionesGeografica getSuperior(String codigoInec) {
		if (isProvincia(codigoInec)) {
			return null;
		} else if (isCanton(codigoInec) || isParroquia(codigoInec)) {
			return buscarUbicacionGeograficaSuperior(codigoInec);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<UbicacionesGeografica> listarProvincia() throws ServiceException {
		List<UbicacionesGeografica> listaProvincia = null;
		try {
			listaProvincia = (List<UbicacionesGeografica>) crudServiceBean.findByNamedQuery(
					UbicacionesGeografica.FIND_PROVINCE, null);
		} catch (RuntimeException e) {
			throw new ServiceException(e);
		}
		return listaProvincia;
	}

	@SuppressWarnings("unchecked")
	public List<UbicacionesGeografica> listarPorPadre(UbicacionesGeografica padre) throws ServiceException {
		List<UbicacionesGeografica> listaProvincia = null;
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("padre", padre);
			listaProvincia = (List<UbicacionesGeografica>) crudServiceBean.findByNamedQuery(
					UbicacionesGeografica.FIND_BY_FATHER, params);
		} catch (RuntimeException e) {
			throw new ServiceException(e);
		}
		return listaProvincia;
	}

	public UbicacionesGeografica buscarPorId(Integer id) throws ServiceException {
		try {

			UbicacionesGeografica ug = crudServiceBean.find(UbicacionesGeografica.class, id);
			if (ug != null) {
				ug.getUbicacionesGeografica().getId();
				if(ug.getUbicacionesGeografica().getUbicacionesGeografica() != null)
					ug.getUbicacionesGeografica().getUbicacionesGeografica().getId();
			}
			return ug;
		} catch (RuntimeException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * Metodo para obtener ubicaciones geograficas de un Proyecto Minero (con o
	 * sin concesiones)
	 * 
	 * @param proyecto
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<UbicacionesGeografica> listarPorProyecto(final ProyectoLicenciamientoAmbiental proyecto,boolean concesiones) {
		StringBuilder sql = new StringBuilder();
		if(!concesiones){
			sql.append("select distinct g.* from geographical_locations g, suia_iii.projects_locations p ");
			sql.append("where g.gelo_id = p.gelo_id and p.prlo_status=true and p.pren_id=").append(proyecto.getId());
			//sql.append(" union all ");
		}else{
			sql.append("select g.* from geographical_locations g, suia_iii.mining_grants mg, ");
			sql.append("suia_iii.mining_grants_locations ml ");
			sql.append("where mg.migr_id = ml.migr_id and mg.pren_id = ").append(proyecto.getId());
			sql.append("and ml.gelo_id = g.gelo_id ");
		}
		
		Query q = crudServiceBean.getEntityManager().createNativeQuery(sql.toString(), UbicacionesGeografica.class);
		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<Region> listaRegionesEcuador() {
		return (List<Region>) crudServiceBean.findByNamedQuery(Region.LISTAR_TODO, null);
	}

	@SuppressWarnings("unchecked")
	public List<UbicacionesGeografica> getProvinciasAguasTerritoriales() {
		Map<String, Object> parameters = new ConcurrentHashMap<String, Object>();
		parameters.put("identificadores", Arrays.asList(UbicacionesGeografica.AGUAS_TERRITORIALES));
		return (List<UbicacionesGeografica>) crudServiceBean.findByNamedQuery(
				UbicacionesGeografica.FIND_AGUAS_TERRITORIALES, parameters);
	}
	
	public UbicacionesGeografica nivelNAcional(Integer id)
	{		
		Query q = crudServiceBean.getEntityManager().createQuery("select g from UbicacionesGeografica g where g.id = :id");
		q.setParameter("id", id);
		if(q.getResultList().size()>0)
			return (UbicacionesGeografica) q.getResultList().get(0);
		else
			return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<UbicacionesGeografica> getListaPaises() {

		Map<String, Object> parameters = new HashMap<String, Object>();
		return (List<UbicacionesGeografica>) crudServiceBean.findByNamedQuery(UbicacionesGeografica.FIND_PAISES,
				parameters);
	}
	
	@SuppressWarnings("unchecked")
	public List<UbicacionesGeografica> buscarUbicacionGeograficaPorParroquia(String inecParroquia) {
		
		Map<String, Object> parametros = new HashMap<String, Object>();
		parametros.put("codeInec", inecParroquia);

		List<UbicacionesGeografica> ubicaciones = (List<UbicacionesGeografica>) crudServiceBean.findByNamedQuery(UbicacionesGeografica.FIND_PARROQUIA,parametros);
		return ubicaciones;

	}
}
