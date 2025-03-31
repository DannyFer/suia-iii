/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.administracion.facade;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.administracion.service.RolService;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Holiday;
import ec.gob.ambiente.suia.exceptions.ServiceException;

/**
 *
 * @author mariela
 */
@Stateless
public class FeriadosFacade {

    @EJB
    private RolService rolService;
    @EJB
    private CrudServiceBean crudServiceBean;

    public List<Holiday> listarFeriados() throws ServiceException {
        List<Holiday> listaFeriados = null;
        try {
        	listaFeriados = (List<Holiday>) crudServiceBean.findByNamedQuery(Holiday.LISTAR_TODO, null);
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
        return listaFeriados;
    }
    
    public List<Holiday> listarFeriadosPorRango(Date fechaInicio, Date fechaFin) throws ServiceException {
    	@SuppressWarnings("unchecked")
		List<Holiday> listaFeriados = (List<Holiday>) crudServiceBean.getEntityManager().createQuery(
						"SELECT h FROM Holiday h "
						+ "where ((:fechaInicio BETWEEN h.fechaInicio and h.fechaFin or "
						+ ":fechaFin BETWEEN h.fechaInicio and h.fechaFin) or "
						+ "(h.fechaInicio >= :fechaInicio and h.fechaFin <= :fechaFin)) "
						+ "ORDER BY h.fechaInicio desc")
				.setParameter("fechaInicio", fechaInicio).setParameter("fechaFin", fechaFin).getResultList();

		if (listaFeriados != null && !listaFeriados.isEmpty()) {
			return listaFeriados;
		} else {
			return null;
		}
    }
    
    public List<Holiday> listarFeriadosDiferentesPorRango(Date fechaInicio, Date fechaFin, Integer idFeriado) throws ServiceException {
        @SuppressWarnings("unchecked")
		List<Holiday> listaFeriados = (List<Holiday>) crudServiceBean.getEntityManager().createQuery(
						"SELECT h FROM Holiday h "
						+ "where ((:fechaInicio BETWEEN h.fechaInicio and h.fechaFin or "
						+ ":fechaFin BETWEEN h.fechaInicio and h.fechaFin) or "
						+ "(h.fechaInicio >= :fechaInicio and h.fechaFin <= :fechaFin)) "
						+ "and h.id != :idFeriado "
						+ "ORDER BY h.fechaInicio desc")
				.setParameter("fechaInicio", fechaInicio).setParameter("fechaFin", fechaFin).setParameter("idFeriado", idFeriado).getResultList();

		if (listaFeriados != null && !listaFeriados.isEmpty()) {
			return listaFeriados;
		} else {
			return null;
		}
	}
    
    public List<Holiday> listarFeriadosPorRangoLocalidad(Date fechaInicio, Date fechaFin, Integer localidad) throws ServiceException {
        List<Holiday> listaFeriados = null;
        try {
        	Map<String, Object> params = new HashMap<String, Object>();
            params.put("fechaInicio", fechaInicio);
            params.put("fechaFin", fechaFin);
            params.put("localidad", localidad);
            
        	listaFeriados = (List<Holiday>) crudServiceBean.findByNamedQuery(Holiday.LISTAR_POR_RANGO_LOCALIDAD, params);
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
        return listaFeriados;
    }

    public void guardar(Holiday feriado) throws ServiceException {
        try {
        	crudServiceBean.saveOrUpdate(feriado);
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }
    
    public void eliminar(Holiday feriado) throws ServiceException {
        try {
        	feriado.setEstado(false);
        	crudServiceBean.saveOrUpdate(feriado);
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }
    
    //Cris F: buscar feriados Nacionales
    public List<Holiday> listarFeriadosNacionalesPorRangoFechas(Date fechaInicio, Date fechaFin) throws ServiceException {
        @SuppressWarnings("unchecked")
		List<Holiday> listaFeriados = (List<Holiday>) crudServiceBean.getEntityManager().createQuery(
						"SELECT h FROM Holiday h "
						+ "where ((:fechaInicio BETWEEN h.fechaInicio and h.fechaFin or "
						+ ":fechaFin BETWEEN h.fechaInicio and h.fechaFin) or "
						+ "(h.fechaInicio >= :fechaInicio and h.fechaFin <= :fechaFin)) and "
						+ "h.esFeriadoNacional = true "
						+ "ORDER BY h.fechaInicio desc")
				.setParameter("fechaInicio", fechaInicio).setParameter("fechaFin", fechaFin).getResultList();

		if (listaFeriados != null && !listaFeriados.isEmpty()) {
			return listaFeriados;
		} else {
			return null;
		}
	}
    
    public int getDiasFeriadosNacionalesPorRangoFechas(Date fechaInicio, Date fechaFin) throws ServiceException {
    	Long totalDias = (Long) crudServiceBean.getEntityManager().createQuery(
						"SELECT SUM(h.totalDias) FROM Holiday h "
						+ "where ((:fechaInicio BETWEEN h.fechaInicio and h.fechaFin or "
						+ ":fechaFin BETWEEN h.fechaInicio and h.fechaFin) or "
						+ "(h.fechaInicio >= :fechaInicio and h.fechaFin <= :fechaFin)) and "
						+ "h.esFeriadoNacional = true and h.estado = true ")
				.setParameter("fechaInicio", fechaInicio).setParameter("fechaFin", fechaFin).getSingleResult();

		if (totalDias != null) {
			return totalDias.intValue();
		} else {
			return 0;
		}
	}
}
