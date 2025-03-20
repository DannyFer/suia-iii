package ec.gob.ambiente.suia.crud.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;

import org.hibernate.Session;
import org.hibernate.jdbc.Work;

/**
 * <b> Servicio para manejar la comunicacion con la base de datos postgis. </b>
 * 
 * @author Carlos Pupo
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Carlos Pupo, Fecha: Feb 25, 2015]
 *          </p>
 */
@Stateless
public class CrudServicePostgisBean {

	@PersistenceContext(unitName = "SuiaPU-Postgis")
	private EntityManager em;

	public EntityManager getEntityManager() {
		return em;
	}

	@SuppressWarnings("unchecked")
	public <T> T find(Class<?> type, Integer id) {
		return (T) this.getEntityManager().find(type, id);
	}

	public List<?> findAll(Class<?> type) {
		CriteriaQuery<?> cq = getEntityManager().getCriteriaBuilder().createQuery();
		cq.multiselect(cq.from(type));
		return (List<?>) getEntityManager().createQuery(cq).getResultList();
	}

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

	public Object executeNativeQueryUniqueResult(final String query) {
		final List<Object> resultList = new ArrayList<Object>();
		getEntityManager().unwrap(Session.class).doWork(new Work() {

			@Override
			public void execute(Connection connection) throws SQLException {
				PreparedStatement preparedStatement = connection.prepareStatement(query);
				ResultSet resultSet = preparedStatement.executeQuery();
				if (resultSet != null) {
					while (resultSet.next()) {
						resultList.add(resultSet.getObject(1));
					}
					resultSet.close();
					preparedStatement.close();
				}
			}
		});

		return resultList.isEmpty() ? null : resultList.get(0);
	}
}
