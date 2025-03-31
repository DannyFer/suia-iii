/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.administracion.service;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Nacionalidad;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 * 
 * @author ishmael
 */
@Stateless
public class NacionalidadService {

	@EJB
	private CrudServiceBean crudServiceBean;

	@SuppressWarnings("unchecked")
	public List<Nacionalidad> buscarPorEstado(boolean estado)
			throws ServiceException {
		List<Nacionalidad> listaNacionalidad = null;
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("estado", estado);
			listaNacionalidad = (List<Nacionalidad>) crudServiceBean
					.findByNamedQuery(Nacionalidad.FIND_BY_STATE, params);
		} catch (RuntimeException e) {
			throw new ServiceException(e);
		}
		return listaNacionalidad;
	}

	public Nacionalidad buscarPorId(Integer id) throws ServiceException {
		Nacionalidad nacionalidad = null;
		try {
			nacionalidad = (Nacionalidad) crudServiceBean
					.getEntityManager()
					.createQuery(
							"SELECT n FROM Nacionalidad n where n.id=:paramId")
					.setParameter("paramId", id).getSingleResult();
		} catch (RuntimeException e) {
			throw new ServiceException(e);
		}
		return nacionalidad;
	}
}
