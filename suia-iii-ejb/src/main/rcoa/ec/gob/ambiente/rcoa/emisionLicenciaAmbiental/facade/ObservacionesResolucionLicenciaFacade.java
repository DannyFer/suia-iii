package ec.gob.ambiente.rcoa.emisionLicenciaAmbiental.facade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.emisionLicenciaAmbiental.model.ObservacionResolucionLicencia;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.utils.EjecutarSentenciasNativas;



@Stateless
public class ObservacionesResolucionLicenciaFacade {

	@EJB
    private CrudServiceBean crudServiceBean;
    @EJB
    private EjecutarSentenciasNativas ejecutarSentenciasNativas;
    
    public void guardar(List<ObservacionResolucionLicencia> listaObservaciones) throws ServiceException {
        try {
            crudServiceBean.saveOrUpdate(listaObservaciones);
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }
    
    public List<ObservacionResolucionLicencia> listarPorIdClaseNombreClase(Integer idClase, String nombreClase, String... secciones) throws ServiceException {
        try {
            if (secciones.length == 1 && secciones[0] != null && secciones[0].equals("*")) {
                return listarPorIdClaseNombreClase(idClase, nombreClase);
            }
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("idClase", idClase);
            params.put("nombreClase", nombreClase);
            params.put("seccion", Arrays.asList(secciones));
            @SuppressWarnings("unchecked")
			List<ObservacionResolucionLicencia> lista = (List<ObservacionResolucionLicencia>) crudServiceBean.findByNamedQuery(ObservacionResolucionLicencia.LISTAR_POR_ID_CLASE_NOMBRE_CLASE_SECCION, params);
            for (ObservacionResolucionLicencia ob : lista) {
                ob.setDisabled(true);
            }
            return lista;
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }
    
    public List<ObservacionResolucionLicencia> listarPorIdClaseNombreClase(Integer idClase, String nombreClase) throws ServiceException {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("idClase", idClase);
            params.put("nombreClase", nombreClase);
            @SuppressWarnings("unchecked")
			List<ObservacionResolucionLicencia> lista = (List<ObservacionResolucionLicencia>) crudServiceBean.findByNamedQuery(ObservacionResolucionLicencia.LISTAR_POR_ID_CLASE_NOMBRE_CLASE, params);
            for (ObservacionResolucionLicencia ob : lista) {
                ob.setDisabled(true);
            }
            return lista;
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }
    
    public List<ObservacionResolucionLicencia> listarPorIdClaseNombreClaseNoCorregidas(Integer idClase, String nombreClase) throws ServiceException {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("idClase", idClase);
            params.put("nombreClase", nombreClase);
            @SuppressWarnings("unchecked")
			List<ObservacionResolucionLicencia> lista = (List<ObservacionResolucionLicencia>) crudServiceBean.findByNamedQuery(ObservacionResolucionLicencia.LISTAR_POR_ID_CLASE_NOMBRE_CLASE_NO_CORREGIDAS, params);
            for (ObservacionResolucionLicencia ob : lista) {
                ob.setDisabled(true);
            }
            return lista;
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }
    
    @SuppressWarnings("unchecked")
	public List<ObservacionResolucionLicencia> listarPorIdClaseNombreClaseUsuarioNoCorregidas(Integer idClase, String nombreClase, Integer idUsuario) throws ServiceException {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("idClase", idClase);
            params.put("nombreClase", nombreClase);
            params.put("idUsuario", idUsuario);
			List<ObservacionResolucionLicencia> lista = (List<ObservacionResolucionLicencia>) crudServiceBean.findByNamedQuery(ObservacionResolucionLicencia.LISTAR_POR_ID_CLASE_NOMBRE_CLASE_USUARIO_NO_CORREGIDAS, params);
            for (ObservacionResolucionLicencia ob : lista) {
                ob.setDisabled(true);
            }
            return lista;
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }
    
    @SuppressWarnings("unchecked")
    public List<ObservacionResolucionLicencia> listarPorIdUsuario(Integer id) throws ServiceException {
	     try {
	         Map<String, Object> params = new HashMap<String, Object>();
	         params.put("idUsuario", id);
             List<ObservacionResolucionLicencia> lista = (List<ObservacionResolucionLicencia>) crudServiceBean.findByNamedQuery(ObservacionResolucionLicencia.LISTAR_POR_ID_USUARIO, params);
             return lista;
	     } catch (RuntimeException e) {
	         throw new ServiceException(e);
	     }
    }
    
    public void guardar(ObservacionResolucionLicencia ObservacionResolucionLicencia) {
	    crudServiceBean.saveOrUpdate(ObservacionResolucionLicencia);
    }
    
    /**
	 * MarielaG
	 * Recupera las observaciones del informe y oficio (aprobacion u observacion) actuales, mediante la fecha de registro del informe
	 * @param idClase
	 * @param nombreClase
	 * @param fechaRegistro
	 * @param secciones
	 * @return
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public List<ObservacionResolucionLicencia> listarHistorialPorIdClaseNombreClase(Integer idClase, String nombreClase, Date fechaRegistro, String... secciones) throws ServiceException {
		try {
			Query query = crudServiceBean.getEntityManager()
					.createQuery(
							"SELECT u FROM ObservacionResolucionLicencia u "
									+ "where u.idClase = :idClase AND u.nombreClase = :nombreClase AND u.seccionFormulario in :seccion "
									+ "AND fechaRegistro >= :fechaRegistro "
									+ "order by u.seccionFormulario, u.id");
			query.setParameter("idClase", idClase);
			query.setParameter("nombreClase", nombreClase);
			query.setParameter("fechaRegistro", fechaRegistro);
			query.setParameter("seccion", Arrays.asList(secciones));

			List<ObservacionResolucionLicencia> result = (List<ObservacionResolucionLicencia>) query.getResultList();
			if (result != null && !result.isEmpty())
				return result;

		} catch (NoResultException e) {
			return new ArrayList<ObservacionResolucionLicencia>();
		}
		return new ArrayList<ObservacionResolucionLicencia>();
	}
	
	/**
	 * Cris F
	 * Recupera las anteriores observaciones del informe y oficio (aprobacion u observacion) mediante la fecha de registro del informe
	 * @param idClase
	 * @param nombreClase
	 * @param fechaRegistro
	 * @param secciones
	 * @return
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public List<ObservacionResolucionLicencia> listarHistorialPorIdClaseNombreClaseHistorico(Integer idClase, String nombreClase, Date fechaRegistro, String... secciones) throws ServiceException {
		try {
			Query query = crudServiceBean.getEntityManager()
					.createQuery(
							"SELECT u FROM ObservacionesViabilidad u "
									+ "where u.idClase = :idClase AND u.nombreClase = :nombreClase AND u.seccionFormulario in :seccion "
									+ "AND fechaRegistro < :fechaRegistro "
									+ "order by u.seccionFormulario, u.id");
			query.setParameter("idClase", idClase);
			query.setParameter("nombreClase", nombreClase);
			query.setParameter("fechaRegistro", fechaRegistro);
			query.setParameter("seccion", Arrays.asList(secciones));

			List<ObservacionResolucionLicencia> result = (List<ObservacionResolucionLicencia>) query.getResultList();
			if (result != null && !result.isEmpty())
				return result;

		} catch (NoResultException e) {
			return new ArrayList<ObservacionResolucionLicencia>();
		}
		return new ArrayList<ObservacionResolucionLicencia>();
	}

}
