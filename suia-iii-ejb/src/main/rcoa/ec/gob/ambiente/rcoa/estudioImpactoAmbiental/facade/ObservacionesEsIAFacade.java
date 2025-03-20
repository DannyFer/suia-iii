/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.ObservacionesEsIA;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.utils.EjecutarSentenciasNativas;

/**
 * @author christian
 */
@LocalBean
@Stateless
public class ObservacionesEsIAFacade {

    @EJB
    private CrudServiceBean crudServiceBean;
    @EJB
    private EjecutarSentenciasNativas ejecutarSentenciasNativas;

    public void guardar(List<ObservacionesEsIA> listaObservaciones) throws ServiceException {
        try {
            crudServiceBean.saveOrUpdate(listaObservaciones);
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public List<ObservacionesEsIA> listarPorIdClaseNombreClase(Integer idClase, List<String> nombreClase, String... secciones) throws ServiceException {
        try {
            if (secciones.length == 1 && secciones[0] != null && secciones[0].equals("*")) {
                return listarPorIdClaseNombreClase(idClase, nombreClase);
            }
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("idClase", idClase);
            params.put("nombreClase", nombreClase.get(0));
            params.put("seccion", Arrays.asList(secciones));
			List<ObservacionesEsIA> lista = (List<ObservacionesEsIA>) crudServiceBean.findByNamedQuery(ObservacionesEsIA.LISTAR_POR_ID_CLASE_NOMBRE_CLASE_SECCION, params);
            
            
            if(nombreClase.size() > 1) {
            	params = new HashMap<String, Object>();
                params.put("idClase", idClase);
                params.put("nombreClase", nombreClase.get(1));
                params.put("seccion", Arrays.asList(secciones));
            	List<ObservacionesEsIA> lista2 = (List<ObservacionesEsIA>) crudServiceBean.findByNamedQuery(ObservacionesEsIA.LISTAR_POR_ID_CLASE_NOMBRE_CLASE_SECCION, params);
            	
            	lista.addAll(lista2);
            }
            
            for (ObservacionesEsIA ob : lista) {
                ob.setDisabled(true);
            }
            return lista;
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }

    public List<ObservacionesEsIA> listarPorIdClaseNombreClase(Integer idClase, String nombreClase) throws ServiceException {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("idClase", idClase);
            params.put("nombreClase", nombreClase);
            @SuppressWarnings("unchecked")
			List<ObservacionesEsIA> lista = (List<ObservacionesEsIA>) crudServiceBean.findByNamedQuery(ObservacionesEsIA.LISTAR_POR_ID_CLASE_NOMBRE_CLASE, params);
            for (ObservacionesEsIA ob : lista) {
                ob.setDisabled(true);
            }
            return lista;
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }
    
    public List<ObservacionesEsIA> listarPorIdClaseNombreClaseNoCorregidas(Integer idClase, String nombreClase) throws ServiceException {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("idClase", idClase);
            params.put("nombreClase", nombreClase);
            @SuppressWarnings("unchecked")
			List<ObservacionesEsIA> lista = (List<ObservacionesEsIA>) crudServiceBean.findByNamedQuery(ObservacionesEsIA.LISTAR_POR_ID_CLASE_NOMBRE_CLASE_NO_CORREGIDAS, params);
            for (ObservacionesEsIA ob : lista) {
                ob.setDisabled(true);
            }
            return lista;
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }
    
	@SuppressWarnings("unchecked")
	public List<ObservacionesEsIA> listarPorIdClaseNombreClaseNoCorregidas(Integer idClase, List<String> nombreClase) throws ServiceException {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("idClase", idClase);
            params.put("nombreClase", nombreClase.get(0));
            
			List<ObservacionesEsIA> lista = (List<ObservacionesEsIA>) crudServiceBean.findByNamedQuery(ObservacionesEsIA.LISTAR_POR_ID_CLASE_NOMBRE_CLASE_NO_CORREGIDAS, params);
            
            if(nombreClase.size() > 1) {
            	params = new HashMap<String, Object>();
                params.put("idClase", idClase);
                params.put("nombreClase", nombreClase.get(1));
            	List<ObservacionesEsIA> lista2 = (List<ObservacionesEsIA>) crudServiceBean.findByNamedQuery(ObservacionesEsIA.LISTAR_POR_ID_CLASE_NOMBRE_CLASE_NO_CORREGIDAS, params);
            	
            	lista.addAll(lista2);
            }
            
            for (ObservacionesEsIA ob : lista) {
                ob.setDisabled(true);
            }
            return lista;
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }
    
    @SuppressWarnings("unchecked")
	public List<ObservacionesEsIA> listarPorIdClaseNombreClaseUsuarioNoCorregidas(Integer idClase, String nombreClase, Integer idUsuario) throws ServiceException {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("idClase", idClase);
            params.put("nombreClase", nombreClase);
            params.put("idUsuario", idUsuario);
			List<ObservacionesEsIA> lista = (List<ObservacionesEsIA>) crudServiceBean.findByNamedQuery(ObservacionesEsIA.LISTAR_POR_ID_CLASE_NOMBRE_CLASE_USUARIO_NO_CORREGIDAS, params);
            for (ObservacionesEsIA ob : lista) {
                ob.setDisabled(true);
            }
            return lista;
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }
    
    @SuppressWarnings("unchecked")
    public List<ObservacionesEsIA> listarPorIdUsuario(Integer id) throws ServiceException {
	     try {
	         Map<String, Object> params = new HashMap<String, Object>();
	         params.put("idUsuario", id);
	                 List<ObservacionesEsIA> lista = (List<ObservacionesEsIA>) crudServiceBean.findByNamedQuery(ObservacionesEsIA.LISTAR_POR_ID_USUARIO, params);
	                 return lista;
	     } catch (RuntimeException e) {
	         throw new ServiceException(e);
	     }
    }
 
	 public void guardar(ObservacionesEsIA ObservacionesEsIA)
	 {
	    crudServiceBean.saveOrUpdate(ObservacionesEsIA);
	 }
	 
	 public List<ObservacionesEsIA> listarPorIdClaseSeccionNoCorregidas(Integer idClase, String... secciones) throws ServiceException {
	        try {
	            Map<String, Object> params = new HashMap<String, Object>();
	            params.put("idClase", idClase);
	            params.put("seccion", Arrays.asList(secciones));
	            @SuppressWarnings("unchecked")
				List<ObservacionesEsIA> lista = (List<ObservacionesEsIA>) crudServiceBean.findByNamedQuery(ObservacionesEsIA.LISTAR_POR_ID_CLASE_SECCION_NO_CORREGIDAS, params);
	            for (ObservacionesEsIA ob : lista) {
	                ob.setDisabled(true);
	            }
	            return lista;
	        } catch (RuntimeException e) {
	            throw new ServiceException(e);
	        }
	    }
	 
	public List<ObservacionesEsIA> listarPorIdClaseNoCorregidas(Integer idClase, String nombreClase) throws ServiceException {
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("idClase", idClase);	
			params.put("nombreClase", nombreClase);
			@SuppressWarnings("unchecked")
			List<ObservacionesEsIA> listaAux = (List<ObservacionesEsIA>) crudServiceBean
					.findByNamedQuery(ObservacionesEsIA.LISTAR_POR_ID_CLASE_NO_CORREGIDAS,params);
			
			List<ObservacionesEsIA> lista = new ArrayList<>();
			
			for (ObservacionesEsIA ob : listaAux) {
				if(!lista.contains(ob)){
					lista.add(ob);
				}
			}
			
			for (ObservacionesEsIA ob : lista) {
				ob.setDisabled(true);
			}
			return lista;
		} catch (RuntimeException e) {
			throw new ServiceException(e);
		}
	}
	
	public List<ObservacionesEsIA> listarPorIdClaseSeccion(Integer idClase, String... secciones) throws ServiceException {
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("idClase", idClase);
			params.put("seccion", Arrays.asList(secciones));
			@SuppressWarnings("unchecked")
			List<ObservacionesEsIA> lista = (List<ObservacionesEsIA>) crudServiceBean.findByNamedQuery(ObservacionesEsIA.LISTAR_POR_ID_CLASE_SECCION_ALL,params);
			for (ObservacionesEsIA ob : lista) {
				ob.setDisabled(true);
			}
			return lista;
		} catch (RuntimeException e) {
			throw new ServiceException(e);
		}
	}
	
	
	public List<ObservacionesEsIA> listarPorIdClaseNombreClaseSeccionNoCorregidas(Integer idClase, String nombreClase, String... secciones) throws ServiceException {
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("idClase", idClase);
            params.put("nombreClase", nombreClase);
            params.put("seccion", Arrays.asList(secciones));
			@SuppressWarnings("unchecked")
			List<ObservacionesEsIA> lista = (List<ObservacionesEsIA>) crudServiceBean
					.findByNamedQuery(
							ObservacionesEsIA.LISTAR_POR_ID_CLASE_NOMBRE_CLASE_SECCION_NO_CORREGIDAS,
							params);
			for (ObservacionesEsIA ob : lista) {
				ob.setDisabled(true);
			}
			return lista;
		} catch (RuntimeException e) {
			throw new ServiceException(e);
		}
	}
	
	public List<ObservacionesEsIA> listarPorIdClaseNoCorregidasTodas(Integer idClase) throws ServiceException{
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("idClase", idClase);
			
			@SuppressWarnings("unchecked")
			List<ObservacionesEsIA> lista = (List<ObservacionesEsIA>) crudServiceBean
					.findByNamedQuery(
							ObservacionesEsIA.LISTAR_POR_ID_CLASE_NO_CORREGIDAS_TODAS,
							params);
			return lista;
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

}
