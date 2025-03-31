package ec.gob.ambiente.suia.crud.service;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;

import org.hibernate.Filter;
import org.hibernate.Session;

import ec.gob.ambiente.suia.domain.base.EntidadBase;
import ec.gob.ambiente.suia.exceptions.ServiceException;

import java.util.ArrayList;

/**
 * <b> Servicio para manejar la comunicacion con la base de datos. </b>
 * 
 * @author Rene Enriquez
 * @version Revision: 2.0
 *          <p>
 *          [Autor: Rene Enriquez, Fecha: Oct 28, 2014]
 *          </p>
 */
@Stateless
public class CrudServiceBean {

	@PersistenceContext(unitName = "SuiaPU")
	private EntityManager em;

	public EntityManager getEntityManager() {
		enableFilters();
		return em;
	}
	
	public EntityManager getEntityManagerWithOutFilters() {
		return em;
	}

	private void enableFilters() {
		Session session = resolveHibernateSession();
		Filter filter = session.getEnabledFilter(EntidadBase.FILTER_ACTIVE);
		if (filter == null) {
			filter = session.enableFilter(EntidadBase.FILTER_ACTIVE);
		}
	}

	private Session resolveHibernateSession() {
		Session session = em.unwrap(org.hibernate.Session.class);
		return session;
	}

	public void invalidEntity(String message, boolean show) {
		if (show)
			throw new IllegalArgumentException(message);
	}

	/**
	 * 
	 * <b> Obtiene el valor proximo de la secuencia.</b>
	 * 
	 * @author Carlos Pupo
	 * @version Revision: 1.0
	 *          <p>
	 *          [Autor: Carlos Pupo, Fecha: 31/01/2015]
	 *          </p>
	 * @return
	 */
	public Object getSecuenceNextValue(String sequenceName, String schema) {
		if (schema != null)
			sequenceName = schema + "." + sequenceName;
		Query query = getEntityManager().createNativeQuery(
				"SELECT nextval( '" + sequenceName + "' ) FROM generate_series( 1, 1)");
		return query.getSingleResult();
	}
	
	/**
	 * 
	 * <b> Actualiza el valor de la secuencia.</b>
	 * 
	 * @author Carlos Pupo
	 * @version Revision: 1.0
	 *          <p>
	 *          [Autor: Carlos Pupo, Fecha: 31/01/2015]
	 *          </p>
	 * @return
	 */
	public void updateSecuenceValue(String sequenceName, String schema, long value) {
		if (schema != null)
			sequenceName = schema + "." + sequenceName;
		Query query = getEntityManager().createNativeQuery(
				"SELECT setval( '" + sequenceName + "', " + value + ", true)");
		query.getSingleResult();
	}

	/**
	 * <b> Almacena o actualiza un registro en la base de datos. </b>
	 * 
	 * @author Carlos Pupo
	 */
	public <T extends EntidadBase> T saveOrUpdate(T entidad) {
		if (!entidad.isPersisted()) {
			getEntityManager().persist(entidad);
			getEntityManager().flush();
			getEntityManager().refresh(entidad);
		} else {
			getEntityManager().merge(entidad);
		}
		return entidad;
	}
	
	public <T extends EntidadBase> T saveOrUpdatetotal(T entidad) {
//		if (!entidad.isPersisted()) {
			getEntityManager().persist(entidad);
			getEntityManager().flush();
			getEntityManager().refresh(entidad);
//		} else {
//			getEntityManager().merge(entidad);
//		}
		return entidad;
	}

	/**
	 * <b> Almacena o actualiza una lista de registros en la base de datos. </b>
	 * 
	 * @author Carlos Pupo
	 */
	public List<? extends EntidadBase> saveOrUpdate(List<? extends EntidadBase> lista) {
		for (EntidadBase t : lista) {
			saveOrUpdate(t);
		}
		return lista;
	}

	/**
	 * <b> Almacena o actualiza una lista de registros en la base de datos. </b>
	 * 
	 * @author Carlos Pupo
	 */
	public List<? extends EntidadBase> saveOrUpdateReturnList(List<? extends EntidadBase> lista) {
		for (EntidadBase t : lista) {
			saveOrUpdate(t);
		}
		return lista;
	}

	/**
	 * <b> Elimina logicamente un registro en la base de datos. </b>
	 * 
	 * @author Carlos Pupo
	 */
	public <T extends EntidadBase> T delete(T entidad) {
		entidad.setEstado(false);
		getEntityManager().merge(entidad);
		return entidad;
	}

	/**
	 * <b> Elimina logicamente una lista de registros en la base de datos. </b>
	 * 
	 * @author Carlos Pupo
	 */
	public List<? extends EntidadBase> delete(List<? extends EntidadBase> lista) {
		for (EntidadBase t : lista) {
			delete(t);
		}
		return lista;
	}

	/**
	 * 
	 * <b> Ejecuta un namedQery con los parametros indicados en el mapa, en el que la clave del mapa es el nombre del
	 * parametro. </b>
	 * 
	 * @author rene
	 * @version Revision: 1.0
	 *          <p>
	 *          [Autor: rene, Fecha: Oct 28, 2014]
	 *          </p>
	 * @param namedQueryName
	 *            nombre del namedQuery
	 * @param parameters
	 *            parametros del query
	 * @return resultado de la consulta
	 */
	@SuppressWarnings("unchecked")
	public List<? extends EntidadBase> findByNamedQuery(final String namedQueryName,
			final Map<String, Object> parameters) {
		Query query = getEntityManager().createNamedQuery(namedQueryName);
		if (parameters != null) {
			for (Entry<String, Object> entry : parameters.entrySet()) {
				query.setParameter(entry.getKey(), entry.getValue());
			}
		}
		return query.getResultList();
	}
	
	/**
	 * 
	 * <b> Ejecuta un namedQery con los parametros indicados en el mapa, en el que la clave del mapa es el nombre del
	 * parametro retorna lista generica. </b>
	 * 
	 * @author christian
	 * @version Revision: 1.0
	 *          <p>
	 *          [Autor: rene, Fecha: Oct 28, 2014]
	 *          </p>
	 * @param <T>
	 *            retorno generico
	 * @param namedQueryName
	 *            nombre del namedQuery
	 * @param parameters
	 *            parametros del query
	 * @return resultado de la consulta
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> findByNamedQueryGeneric(final String namedQueryName, final Map<String, Object> parameters) {
		Query query = getEntityManager().createNamedQuery(namedQueryName);
		if (parameters != null) {
			for (Entry<String, Object> entry : parameters.entrySet()) {
				query.setParameter(entry.getKey(), entry.getValue());
			}
		}
		return query.getResultList();
	}

	/**
	 * 
	 * <b> Ejecuta un namedQery con los parametros indicados en el mapa, en el que la clave del mapa es el nombre del
	 * parametro. </b>
	 * 
	 * @author rene
	 * @version Revision: 1.0
	 *          <p>
	 *          [Autor: carlos.pupo, Fecha: Oct 28, 2014]
	 *          </p>
	 * @param namedQueryName
	 *            nombre del namedQuery
	 * @param parameters
	 *            parametros del query
	 * @return resultado de la consulta
	 */
	@SuppressWarnings("unchecked")
	public <T> T findByNamedQuerySingleResult(final String namedQueryName, final Map<String, Object> parameters) {
		Query query = getEntityManager().createNamedQuery(namedQueryName);
		if (parameters != null) {
			for (Entry<String, Object> entry : parameters.entrySet()) {
				query.setParameter(entry.getKey(), entry.getValue());
			}
		}
		try {
			return (T) query.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}
	}
	
	/**
	 * 
	 * <b> Ejecuta un namedQery, sin activar filtros, con los parametros indicados en el mapa, en el que la clave del mapa es el nombre del
	 * parametro. </b>
	 * 
	 * @author carlos.pupo
	 * @version Revision: 1.0
	 *          <p>
	 *          [Autor: carlos.pupo, Fecha: Sep 2, 2015]
	 *          </p>
	 * @param namedQueryName
	 *            nombre del namedQuery
	 * @param parameters
	 *            parametros del query
	 * @return resultado de la consulta
	 */
	@SuppressWarnings("unchecked")
	public <T> T findByNamedQuerySingleResultWithOutFilters(final String namedQueryName, final Map<String, Object> parameters) {
		Query query = getEntityManagerWithOutFilters().createNamedQuery(namedQueryName);
		if (parameters != null) {
			for (Entry<String, Object> entry : parameters.entrySet()) {
				query.setParameter(entry.getKey(), entry.getValue());
			}
		}
		try {
			return (T) query.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}
	}

	/**
	 * 
	 * <b> Ejecuta un namedQery con los parametros indicados en el mapa, en el que la clave del mapa es el nombre del
	 * parametro, los parametro de limites indican el rango del total de los registros que se necesitan. </b>
	 * 
	 * @author rene
	 * @version Revision: 1.0
	 *          <p>
	 *          [Autor: rene, Fecha: Oct 28, 2014]
	 *          </p>
	 * @param namedQueryName
	 *            nombre del namedQuery
	 * @param parameters
	 *            parametros del query
	 * @param limiteInicio
	 *            rango de inicio
	 * @param limiteFin
	 *            rango de fin
	 * @return resultado de la consulta
	 */
	@SuppressWarnings("unchecked")
	public List<? extends EntidadBase> findByNamedQueryPaginado(final String namedQueryName,
			final Map<String, Object> parameters, int limiteInicio, int limiteFin) {
		Query query = getEntityManager().createNamedQuery(namedQueryName).setFirstResult(limiteInicio)
				.setMaxResults(limiteFin);
		if (parameters != null) {
			Set<Entry<String, Object>> parameterSet = parameters.entrySet();
			for (Entry<String, Object> entry : parameterSet) {
				query.setParameter(entry.getKey(), entry.getValue());
			}
		}
		return query.getResultList();
	}

	/**
	 * 
	 * <b> Busca una entidad por el id. </b>
	 * 
	 * @author Carlos Pupo
	 * @version Revision: 1.0
	 *          <p>
	 *          [Autor: Carlos Pupo, Fecha: 19/01/2015]
	 *          </p>
	 * @param type
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T extends EntidadBase> T find(Class<? extends EntidadBase> type, Integer id) {
		return (T) this.getEntityManager().find(type, id);
	}

	/**
	 * 
	 * <b> Busca todas las entidades de la clase. </b>
	 * 
	 * @author Carlos Pupo
	 * @version Revision: 1.0
	 *          <p>
	 *          [Autor: Carlos Pupo, Fecha: 19/01/2015]
	 *          </p>
	 * @param type
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<? extends EntidadBase> findAll(Class<? extends EntidadBase> type) {
		CriteriaQuery<?> cq = getEntityManager().getCriteriaBuilder().createQuery();
		cq.multiselect(cq.from(type));
		return (List<? extends EntidadBase>) getEntityManager().createQuery(cq).getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<Object> findByNativeQuery(String stringQuery, final Map<String, Object> parameters) {
		Query query = getEntityManager().createNativeQuery(stringQuery);
		if (parameters != null) {
			for (Entry<String, Object> entry : parameters.entrySet()) {
				query.setParameter(entry.getKey(), entry.getValue());
			}
		}
		return query.getResultList();
	}

	@SuppressWarnings("rawtypes")
	public List findNativeQuery(String sql, Class tipoClase) throws ServiceException {
		try {
			Query query = getEntityManager().createNativeQuery(sql, tipoClase);
			List lista = query.getResultList();
			if (lista != null && !lista.isEmpty()) {
				return lista;
			} else {
				return new ArrayList();
			}
		} catch (Exception e) {
			throw new ServiceException("Existe un error en la consulta ", e);
		}
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> findByNativeQuery(String stringQuery, final Map<String, Object> parameters, Class<T> clazz) {
		Query query = getEntityManager().createNativeQuery(stringQuery, clazz);
		if (parameters != null) {
			for (Entry<String, Object> entry : parameters.entrySet()) {
				query.setParameter(entry.getKey(), entry.getValue());
			}
		}
		return query.getResultList();
	}
	
	public int insertUpdateByNativeQuery(String stringQuery, final Map<String, Object> parameters) {
		Query query = getEntityManager().createNativeQuery(stringQuery);
		if (parameters != null) {
			for (Entry<String, Object> entry : parameters.entrySet()) {
				query.setParameter(entry.getKey(), entry.getValue());
			}
		}
		return query.executeUpdate();
	}
	
	public <T extends EntidadBase> T saveOrUpdateAlert(T entidad) {	
			getEntityManager().merge(entidad);
		return entidad;
	}

	@SuppressWarnings("unchecked")
	public List<? extends EntidadBase> findByNamedQueryWithOutFilters(final String namedQueryName,
			final Map<String, Object> parameters) {
		Query query = getEntityManagerWithOutFilters().createNamedQuery(namedQueryName);
		if (parameters != null) {
			for (Entry<String, Object> entry : parameters.entrySet()) {
				query.setParameter(entry.getKey(), entry.getValue());
			}
		}
		return query.getResultList();
	}
	
}
