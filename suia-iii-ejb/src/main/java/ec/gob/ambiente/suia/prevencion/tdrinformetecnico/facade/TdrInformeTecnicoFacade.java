/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.prevencion.tdrinformetecnico.facade;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.TdrInformeTecnico;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author frank torres rodriguez
 * @version Revision: 1.0
 *          <p>
 *          [Autor: frank torres rodriguez, Fecha: 11/02/2015]
 *          </p>
 */
@Stateless
public class TdrInformeTecnicoFacade {

	// private static final Logger LOG = Logger.getLogger(TdrFacade.class);

	@EJB
	private CrudServiceBean crudServiceBean;

	public void modificarRegistro(EntidadBase objeto) {
		crudServiceBean.saveOrUpdate(objeto);
	}

	public void saveOrUpdate(EntidadBase objeto) {
		crudServiceBean.saveOrUpdate(objeto);
	}

	public void delete(EntidadBase objeto) {
		crudServiceBean.delete(objeto);
	}

	public TdrInformeTecnico tdrEiaLicencia(int id) {
		return crudServiceBean.find(TdrInformeTecnico.class, id);
	}

	@SuppressWarnings("unchecked")
	public TdrInformeTecnico getTdrInformeTecnicoPorIdTdr(Integer idTdrEia) {
		Map<String, Object> parameters = new ConcurrentHashMap<String, Object>();
		parameters.put("idTdrEia", idTdrEia);

		List<TdrInformeTecnico> result = (List<TdrInformeTecnico>) crudServiceBean
				.findByNamedQuery(TdrInformeTecnico.FIND_BY_TDR, parameters);
		if (result != null && !result.isEmpty()) {
			return result.get(0);
		}
		return null;
	}
}
