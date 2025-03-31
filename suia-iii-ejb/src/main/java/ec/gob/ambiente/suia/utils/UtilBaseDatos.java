/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.utils;

import java.sql.Connection;
import java.sql.SQLException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 *
 * @author christian
 */
public class UtilBaseDatos {

    /**
     * Retorna una conexion a la base de datos
     *
     * @return
     * @throws java.sql.SQLException
     */
    @SuppressWarnings("unchecked")
    public Connection getConexion() throws SQLException {
        try {
            return dataSource("java:jboss/datasources/SuiaDS").getConnection();
        } catch (SQLException e) {
            throw new SQLException("error de conexion a la base de datos", e);
        } catch (NamingException e) {
            throw new SQLException("error de conexion a la base de datos", e);
        }
    }

    @SuppressWarnings("unchecked")
    private DataSource dataSource(String jndi) throws NamingException {
        try {
            DataSource ds;
            InitialContext ctx = new InitialContext();
            ds = (DataSource) ctx.lookup(jndi);
            return ds;
        } catch (NamingException ex) {
            throw ex;
        }
    }

    @SuppressWarnings("unchecked")
    public Connection getConexionBpm() throws SQLException {
        try {
            return dataSource("java:jboss/datasources/bpms").getConnection();
        } catch (SQLException e) {
            throw new SQLException("error de conexion a la base de datos", e);
        } catch (NamingException e) {
            throw new SQLException("error de conexion a la base de datos", e);
        }
    }
}
