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

import ec.gob.ambiente.rcoa.emisionLicenciaAmbiental.model.ObservacionesResolucionAmbiental;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.utils.EjecutarSentenciasNativas;



@Stateless
public class ObservacionesResolucionAmbientalFacade {
	
	@EJB
    private CrudServiceBean crudServiceBean;
    @EJB
    private EjecutarSentenciasNativas ejecutarSentenciasNativas;
    
    public void guardar(List<ObservacionesResolucionAmbiental> listaObservaciones) throws ServiceException {
        try {
            crudServiceBean.saveOrUpdate(listaObservaciones);
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }

    public List<ObservacionesResolucionAmbiental> listarPorIdClaseNombreClase(Integer idClase, String nombreClase, String... secciones) throws ServiceException {
        try {
            if (secciones.length == 1 && secciones[0] != null && secciones[0].equals("*")) {
                return listarPorIdClaseNombreClase(idClase, nombreClase);
            }
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("idClase", idClase);
            params.put("nombreClase", nombreClase);
            params.put("seccion", Arrays.asList(secciones));
            @SuppressWarnings("unchecked")
			List<ObservacionesResolucionAmbiental> lista = (List<ObservacionesResolucionAmbiental>) crudServiceBean.findByNamedQuery(ObservacionesResolucionAmbiental.LISTAR_POR_ID_CLASE_NOMBRE_CLASE_SECCION, params);
            for (ObservacionesResolucionAmbiental ob : lista) {
                ob.setDisabled(true);
            }
            return lista;
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }
    
    public List<ObservacionesResolucionAmbiental> listarPorIdClaseNombreClase(Integer idClase, String nombreClase) throws ServiceException {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("idClase", idClase);
            params.put("nombreClase", nombreClase);
            @SuppressWarnings("unchecked")
			List<ObservacionesResolucionAmbiental> lista = (List<ObservacionesResolucionAmbiental>) crudServiceBean.findByNamedQuery(ObservacionesResolucionAmbiental.LISTAR_POR_ID_CLASE_NOMBRE_CLASE, params);
            for (ObservacionesResolucionAmbiental ob : lista) {
                ob.setDisabled(true);
            }
            return lista;
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }
    
    public List<ObservacionesResolucionAmbiental> listarPorIdClaseNombreClaseNoCorregidas(Integer idClase, String nombreClase) throws ServiceException {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("idClase", idClase);
            params.put("nombreClase", nombreClase);
            @SuppressWarnings("unchecked")
			List<ObservacionesResolucionAmbiental> lista = (List<ObservacionesResolucionAmbiental>) crudServiceBean.findByNamedQuery(ObservacionesResolucionAmbiental.LISTAR_POR_ID_CLASE_NOMBRE_CLASE_NO_CORREGIDAS, params);
            for (ObservacionesResolucionAmbiental ob : lista) {
                ob.setDisabled(true);
            }
            return lista;
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }
    
    @SuppressWarnings("unchecked")
	public List<ObservacionesResolucionAmbiental> listarPorIdClaseNombreClaseUsuarioNoCorregidas(Integer idClase, String nombreClase, Integer idUsuario) throws ServiceException {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("idClase", idClase);
            params.put("nombreClase", nombreClase);
            params.put("idUsuario", idUsuario);
			List<ObservacionesResolucionAmbiental> lista = (List<ObservacionesResolucionAmbiental>) crudServiceBean.findByNamedQuery(ObservacionesResolucionAmbiental.LISTAR_POR_ID_CLASE_NOMBRE_CLASE_USUARIO_NO_CORREGIDAS, params);
            for (ObservacionesResolucionAmbiental ob : lista) {
                ob.setDisabled(true);
            }
            return lista;
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }
    
    @SuppressWarnings("unchecked")
    public List<ObservacionesResolucionAmbiental> listarPorIdUsuario(Integer id) throws ServiceException {
	     try {
	         Map<String, Object> params = new HashMap<String, Object>();
	         params.put("idUsuario", id);
             List<ObservacionesResolucionAmbiental> lista = (List<ObservacionesResolucionAmbiental>) crudServiceBean.findByNamedQuery(ObservacionesResolucionAmbiental.LISTAR_POR_ID_USUARIO, params);
             return lista;
	     } catch (RuntimeException e) {
	         throw new ServiceException(e);
	     }
    }
    
    public void guardar(ObservacionesResolucionAmbiental observacionesInventarioForestal) {
	    crudServiceBean.saveOrUpdate(observacionesInventarioForestal);
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
	public List<ObservacionesResolucionAmbiental> listarHistorialPorIdClaseNombreClase(Integer idClase, String nombreClase, Date fechaRegistro, String... secciones) throws ServiceException {
		try {
			Query query = crudServiceBean.getEntityManager()
					.createQuery(
							"SELECT u FROM ObservacionesResolucionAmbiental u "
									+ "where u.idClase = :idClase AND u.nombreClase = :nombreClase AND u.seccionFormulario in :seccion "
									+ "AND fechaRegistro >= :fechaRegistro "
									+ "order by u.seccionFormulario, u.id");
			query.setParameter("idClase", idClase);
			query.setParameter("nombreClase", nombreClase);
			query.setParameter("fechaRegistro", fechaRegistro);
			query.setParameter("seccion", Arrays.asList(secciones));

			List<ObservacionesResolucionAmbiental> result = (List<ObservacionesResolucionAmbiental>) query.getResultList();
			if (result != null && !result.isEmpty())
				return result;

		} catch (NoResultException e) {
			return new ArrayList<ObservacionesResolucionAmbiental>();
		}
		return new ArrayList<ObservacionesResolucionAmbiental>();
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
	public List<ObservacionesResolucionAmbiental> listarHistorialPorIdClaseNombreClaseHistorico(Integer idClase, String nombreClase, Date fechaRegistro, String... secciones) throws ServiceException {
		try {
			Query query = crudServiceBean.getEntityManager()
					.createQuery(
							"SELECT u FROM ObservacionesResolucionAmbiental u "
									+ "where u.idClase = :idClase AND u.nombreClase = :nombreClase AND u.seccionFormulario in :seccion "
									+ "AND fechaRegistro < :fechaRegistro "
									+ "order by u.seccionFormulario, u.id");
			query.setParameter("idClase", idClase);
			query.setParameter("nombreClase", nombreClase);
			query.setParameter("fechaRegistro", fechaRegistro);
			query.setParameter("seccion", Arrays.asList(secciones));

			List<ObservacionesResolucionAmbiental> result = (List<ObservacionesResolucionAmbiental>) query.getResultList();
			if (result != null && !result.isEmpty())
				return result;

		} catch (NoResultException e) {
			return new ArrayList<ObservacionesResolucionAmbiental>();
		}
		return new ArrayList<ObservacionesResolucionAmbiental>();
	}

}
