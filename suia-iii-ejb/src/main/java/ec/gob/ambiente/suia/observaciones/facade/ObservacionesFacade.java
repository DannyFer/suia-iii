/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.observaciones.facade;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.ObservacionesFormularios;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.utils.EjecutarSentenciasNativas;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author christian
 */
@LocalBean
@Stateless
public class ObservacionesFacade {

    @EJB
    private CrudServiceBean crudServiceBean;
    @EJB
    private EjecutarSentenciasNativas ejecutarSentenciasNativas;

    public void guardar(List<ObservacionesFormularios> listaObservaciones) throws ServiceException {
        try {
//            if (!listaEliminar.isEmpty()) {
//                for (ObservacionesFormularios obj : listaEliminar) {
//                    ejecutarSentenciasNativas.ejecutarSentenciasNativasDml("UPDATE suia_iii.form_comments set foco_status= WHERE foco_id = " + obj.getId());
//                }
//            }

            crudServiceBean.saveOrUpdate(listaObservaciones);
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }

    public List<ObservacionesFormularios> listarPorIdClaseNombreClase(Integer idClase, String nombreClase, String... secciones) throws ServiceException {
        try {
            if (secciones.length == 1 && secciones[0] != null && secciones[0].equals("*")) {
                return listarPorIdClaseNombreClase(idClase, nombreClase);
            }
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("idClase", idClase);
            params.put("nombreClase", nombreClase);
            params.put("seccion", Arrays.asList(secciones));
            @SuppressWarnings("unchecked")
			List<ObservacionesFormularios> lista = (List<ObservacionesFormularios>) crudServiceBean.findByNamedQuery(ObservacionesFormularios.LISTAR_POR_ID_CLASE_NOMBRE_CLASE_SECCION, params);
            for (ObservacionesFormularios ob : lista) {
                ob.setDisabled(true);
            }
            return lista;
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }

    public List<ObservacionesFormularios> listarPorIdClaseNombreClase(Integer idClase, String nombreClase) throws ServiceException {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("idClase", idClase);
            params.put("nombreClase", nombreClase);
            @SuppressWarnings("unchecked")
			List<ObservacionesFormularios> lista = (List<ObservacionesFormularios>) crudServiceBean.findByNamedQuery(ObservacionesFormularios.LISTAR_POR_ID_CLASE_NOMBRE_CLASE, params);
            for (ObservacionesFormularios ob : lista) {
                ob.setDisabled(true);
            }
            return lista;
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }
    
    public List<ObservacionesFormularios> listarPorIdClaseNombreClaseNoCorregidas(Integer idClase, String nombreClase) throws ServiceException {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("idClase", idClase);
            params.put("nombreClase", nombreClase);
            @SuppressWarnings("unchecked")
			List<ObservacionesFormularios> lista = (List<ObservacionesFormularios>) crudServiceBean.findByNamedQuery(ObservacionesFormularios.LISTAR_POR_ID_CLASE_NOMBRE_CLASE_NO_CORREGIDAS, params);
            for (ObservacionesFormularios ob : lista) {
                ob.setDisabled(true);
            }
            return lista;
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }
    
    @SuppressWarnings("unchecked")
	public List<ObservacionesFormularios> listarPorIdClaseNombreClaseUsuarioNoCorregidas(Integer idClase, String nombreClase, Integer idUsuario) throws ServiceException {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("idClase", idClase);
            params.put("nombreClase", nombreClase);
            params.put("idUsuario", idUsuario);
			List<ObservacionesFormularios> lista = (List<ObservacionesFormularios>) crudServiceBean.findByNamedQuery(ObservacionesFormularios.LISTAR_POR_ID_CLASE_NOMBRE_CLASE_USUARIO_NO_CORREGIDAS, params);
            for (ObservacionesFormularios ob : lista) {
                ob.setDisabled(true);
            }
            return lista;
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }
    
    @SuppressWarnings("unchecked")
    public List<ObservacionesFormularios> listarPorIdUsuario(Integer id) throws ServiceException {
	     try {
	         Map<String, Object> params = new HashMap<String, Object>();
	         params.put("idUsuario", id);
	                 List<ObservacionesFormularios> lista = (List<ObservacionesFormularios>) crudServiceBean.findByNamedQuery(ObservacionesFormularios.LISTAR_POR_ID_USUARIO, params);
	                 return lista;
	     } catch (RuntimeException e) {
	         throw new ServiceException(e);
	     }
    }
 
	 public void guardar(ObservacionesFormularios observacionesFormularios)
	 {
	    crudServiceBean.saveOrUpdate(observacionesFormularios);
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
	public List<ObservacionesFormularios> listarHistorialPorIdClaseNombreClase(Integer idClase, String nombreClase, Date fechaRegistro, String... secciones) throws ServiceException {
		try {
			Query query = crudServiceBean.getEntityManager()
					.createQuery(
							"SELECT u FROM ObservacionesFormularios u "
									+ "where u.idClase = :idClase AND u.nombreClase = :nombreClase AND u.seccionFormulario in :seccion "
									+ "AND fechaRegistro >= :fechaRegistro "
									+ "order by u.seccionFormulario, u.id");
			query.setParameter("idClase", idClase);
			query.setParameter("nombreClase", nombreClase);
			query.setParameter("fechaRegistro", fechaRegistro);
			query.setParameter("seccion", Arrays.asList(secciones));

			List<ObservacionesFormularios> result = (List<ObservacionesFormularios>) query.getResultList();
			if (result != null && !result.isEmpty())
				return result;

		} catch (NoResultException e) {
			return new ArrayList<ObservacionesFormularios>();
		}
		return new ArrayList<ObservacionesFormularios>();
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
	public List<ObservacionesFormularios> listarHistorialPorIdClaseNombreClaseHistorico(Integer idClase, String nombreClase, Date fechaRegistro, String... secciones) throws ServiceException {
		try {
			Query query = crudServiceBean.getEntityManager()
					.createQuery(
							"SELECT u FROM ObservacionesFormularios u "
									+ "where u.idClase = :idClase AND u.nombreClase = :nombreClase AND u.seccionFormulario in :seccion "
									+ "AND fechaRegistro < :fechaRegistro "
									+ "order by u.seccionFormulario, u.id");
			query.setParameter("idClase", idClase);
			query.setParameter("nombreClase", nombreClase);
			query.setParameter("fechaRegistro", fechaRegistro);
			query.setParameter("seccion", Arrays.asList(secciones));

			List<ObservacionesFormularios> result = (List<ObservacionesFormularios>) query.getResultList();
			if (result != null && !result.isEmpty())
				return result;

		} catch (NoResultException e) {
			return new ArrayList<ObservacionesFormularios>();
		}
		return new ArrayList<ObservacionesFormularios>();
	}
}
