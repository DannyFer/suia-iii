/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.eia.pma.planMonitoreo.facade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.AspectoAmbientalPMA;
import ec.gob.ambiente.suia.domain.ComponentePMA;
import ec.gob.ambiente.suia.domain.Normativas;
import ec.gob.ambiente.suia.exceptions.ServiceException;

/**
 * @author Oscar Campana
 */
@LocalBean
@Stateless
public class NormativasFacade {


    @EJB
    private CrudServiceBean crudServiceBean;

    public void guardar(final List<Normativas> listaNormativas, final List<Normativas> listaNormativasEliminados) throws ServiceException {
        try {
            crudServiceBean.saveOrUpdate(listaNormativas);
            if (!listaNormativas.isEmpty()) {
                crudServiceBean.delete(listaNormativasEliminados);
            }

        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Recupera la lista de Normativas
     * @return Lista de Normativas
     * @throws ServiceException Si existe un error en la consulta hacia la base de datos
     */
    public List<Normativas> listar(String componente, Integer sector, Integer tipoProyecto) throws ServiceException {
        try {
        	
        	if(!sector.equals(1) && !sector.equals(2))
        	{
        		sector = 3;
        	}
        	Map<String, Object> params = new HashMap<String, Object>();
			params.put("componente", componente);
			params.put("sector", sector);
			params.put("tipoProyecto", tipoProyecto);
            List<Normativas> lista = (List<Normativas>) crudServiceBean.findByNamedQuery(Normativas.LISTAR,params);
 
            return lista;
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Recupera la lista de Normativas
     * @return Lista de Normativas
     * @throws ServiceException Si existe un error en la consulta hacia la base de datos
     */
    public List<Normativas> listarNormativasXComponente(String componente, Integer sector, Integer tipoProyecto) throws ServiceException {
        try {

            if(!sector.equals(1) && !sector.equals(2))
            {
                sector = 3;
            }
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("componente", componente);
            params.put("sector", sector);
            params.put("tipoProyecto", tipoProyecto);
            List<Normativas> lista = (List<Normativas>) crudServiceBean.findByNamedQuery(Normativas.LISTARNORMATIVAXCOMPONENTE,params);

            return lista;
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Recupera la lista de Normativas
     * @return Lista de Normativas
     * @throws ServiceException Si existe un error en la consulta hacia la base de datos
     */
    public List<Normativas> listarComponente(String descripcion, Integer sector, Integer tipoProyecto) throws ServiceException {
        try {
        	
        	if(!sector.equals(1) && !sector.equals(2))
        	{
        		sector = 3;
        	}
        	Map<String, Object> params = new HashMap<String, Object>();
			params.put("descripcion", descripcion);
			params.put("sector", sector);
			params.put("tipoProyecto", tipoProyecto);
            List<Normativas> lista = (List<Normativas>) crudServiceBean.findByNamedQuery(Normativas.LISTARCOMPONENTE,params);
 
            return lista;
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }
    
    
    /**
     * Recupera la lista de Normativas
     * @return Lista de Normativas
     * @throws ServiceException Si existe un error en la consulta hacia la base de datos
     */
    public List<Normativas> listar(Integer sector, Integer tipoProyecto) throws ServiceException {
        try {
        	
        	if(!sector.equals(1) && !sector.equals(2))
        	{
        		sector = 3;
        	}
        	Map<String, Object> params = new HashMap<String, Object>();
			//params.put("componente", componente);
			params.put("sector", sector);
			params.put("tipoProyecto", tipoProyecto);
            List<Normativas> lista = (List<Normativas>) crudServiceBean.findByNamedQuery(Normativas.LISTAR,params);
 
            return lista;
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }
    
    @SuppressWarnings("unchecked")
	public List<ComponentePMA> getComponentes(String nombre) throws Exception {
		Map<String, Object> parameters = new ConcurrentHashMap<String, Object>();
		parameters.put("nombre",nombre);
		List<ComponentePMA> result = (List<ComponentePMA>) crudServiceBean
				.findByNamedQuery(ComponentePMA.LISTAR,
						parameters);
		return result;
	}
    
    @SuppressWarnings("unchecked")
	public List<AspectoAmbientalPMA> getAspectosAmbientalesPorComponente(
			ComponentePMA componente) throws Exception {
		Map<String, Object> parameters = new ConcurrentHashMap<String, Object>();
		parameters.put("idComponente", componente.getId());
		List<AspectoAmbientalPMA> result = (List<AspectoAmbientalPMA>) crudServiceBean
				.findByNamedQuery(AspectoAmbientalPMA.FIND_BY_COMPONENT,parameters);
		return result;
	}

}
