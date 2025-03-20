/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.utils;

import ec.gob.ambiente.suia.exceptions.ServiceException;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author christian
 */
@LocalBean
@Stateless
public class EjecutarSentenciasNativas {

    private final UtilBaseDatos utilBaseDatos = new UtilBaseDatos();
    private Connection con;
    private Statement stt;
    @PersistenceContext(unitName = "SuiaPU")
    private EntityManager entityManager;

    /**
     *
     * @param sql
     * @throws SQLException
     */
    @SuppressWarnings("unchecked")
    public void ejecutarSentenciasNativasDml(final String sql) throws SQLException {
        try {
            this.con = utilBaseDatos.getConexion();
            this.stt = this.con.createStatement();
            this.stt.executeUpdate(sql);
            this.stt.close();
        } catch (SQLException e) {
            throw new SQLException(e);
        } finally {
            this.con.close();
        }
    }

    /**
     *
     * @param sql
     * @throws SQLException
     */
    @SuppressWarnings("unchecked")
    public void ejecutarSentenciasNativasDml(final List<String> sql) throws SQLException {
        try {
            this.con = utilBaseDatos.getConexion();
            this.con.setAutoCommit(false);
            this.stt = this.con.createStatement();
            for (String s : sql) {
                this.stt.addBatch(s);
            }
            this.stt.executeBatch();
            this.stt.close();
            this.con.commit();
        } catch (SQLException ex) {
            this.con.rollback();
            throw new SQLException("error al ejecutar sentencia", ex);
        } finally {
            this.stt.close();
            this.con.close();
        }
    }

    /**
     * metodo que devuelve una lista de datos de cualquier tipo siempre y cuanto
     * el tipo osea el pojo tenga un constructor de tipo con los parámetros de
     * la consulta estos datos deben ser de tipo String el sql debe escojer
     * minimo 2 campos con 1 no funciona
     *
     * @param <T>
     * @param sql
     * @param claseRetorno
     * @param parametros
     * @throws ServiceException
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> listarPorSentenciaSql(final String sql, final Class<?> claseRetorno, final Map<String, Object> parametros) throws ServiceException {
        Query query = getEntityManager().createNativeQuery(sql);
        if (parametros != null) {
            for (Map.Entry<?, ?> en : parametros.entrySet()) {
                query.setParameter(en.getKey().toString(), en.getValue());
            }
        }
        return (List<T>) RetornoListaSql.retornar(query.getResultList(), claseRetorno);
    }

    /**
     * @return the entityManager
     */
    private EntityManager getEntityManager() {
        return entityManager;
    }
}
