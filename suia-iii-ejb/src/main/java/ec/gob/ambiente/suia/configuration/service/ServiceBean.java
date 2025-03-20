package ec.gob.ambiente.suia.configuration.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;

@Stateless
public class ServiceBean {

	@PersistenceContext(unitName = "SuiaPU")
	private EntityManager em;

	public EntityManager getEntityManager() {
		return em;
	}

	/**
	 * <b> Almacena un registro en la base de datos. </b>
	 * 
	 * @author Carlos Pupo
	 */
	public void save(Object entidad) {
		getEntityManager().persist(entidad);
		getEntityManager().flush();
		getEntityManager().refresh(entidad);
	}

	/**
	 * <b> Almacena un registro en la base de datos. </b>
	 * 
	 * @author Carlos Pupo
	 */
	public void update(Object entidad) {
		getEntityManager().merge(entidad);
	}

	/**
	 * <b> Elimina logicamente un registro en la base de datos. </b>
	 * 
	 * @author Carlos Pupo
	 */
	public void delete(Object entidad) {
		getEntityManager().remove(getEntityManager().merge(entidad));
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
	public Object find(Class<?> type, Object id) {
		return this.getEntityManager().find(type, id);
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
	public List<?> findAll(Class<?> type) {
		CriteriaQuery<?> cq = getEntityManager().getCriteriaBuilder().createQuery();
		cq.multiselect(cq.from(type));
		return (List<?>) getEntityManager().createQuery(cq).getResultList();
	}
}
