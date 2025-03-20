/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.observaciontdreialiciencia.facade;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.ObservacionTdrEiaLiciencia;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author mayriliscs
 * @version Revision: 1.0
 *          <p>
 *          [Autor: mayriliscs, Fecha: 30/01/2015]
 *          </p>
 */
@Stateless
public class ObservacionTdrEiaFacade {

	// private static final Logger LOG = Logger.getLogger(TdrFacade.class);

	@EJB
	private CrudServiceBean crudServiceBean;

	public void modificarRegistro(EntidadBase objeto) {
		crudServiceBean.saveOrUpdate(objeto);
	}

	public void saveOrUpdate(EntidadBase objeto) {
		crudServiceBean.saveOrUpdate(objeto);
	}
	
	public ObservacionTdrEiaLiciencia guardarObservacionTdrEia(ObservacionTdrEiaLiciencia objeto) {
		return crudServiceBean.saveOrUpdate(objeto);
	}

	public void delete(EntidadBase objeto) {
		crudServiceBean.delete(objeto);
	}

	public ObservacionTdrEiaLiciencia observacionTdrEiaLiciencia(int id) {
		return crudServiceBean.find(ObservacionTdrEiaLiciencia.class, id);
	}

	@SuppressWarnings("unchecked")
	public List<ObservacionTdrEiaLiciencia> getObservacionTdrEiaLicienciaPorIdTdr(
			Integer idTdrEia) {
		Map<String, Object> parameters = new ConcurrentHashMap<String, Object>();
		parameters.put("idTdrEia", idTdrEia);

		return (List<ObservacionTdrEiaLiciencia>) crudServiceBean
				.findByNamedQuery(ObservacionTdrEiaLiciencia.FIND_BY_TDR,
						parameters);

	}

	@SuppressWarnings("unchecked")
	public ObservacionTdrEiaLiciencia getObservacionTdrEiaLicienciaPorIdTdrComponente(
			Integer idTdrEia, String componente) {
		Map<String, Object> parameters = new ConcurrentHashMap<String, Object>();
		parameters.put("idTdrEia", idTdrEia);
		parameters.put("componente", componente);

		List<ObservacionTdrEiaLiciencia> result = (List<ObservacionTdrEiaLiciencia>) crudServiceBean
				.findByNamedQuery(
						ObservacionTdrEiaLiciencia.FIND_BY_TDR_COMPONENT,
						parameters);
		if (result != null && !result.isEmpty()) {
			return result.get(0);
		}
		return null;
	}
}
