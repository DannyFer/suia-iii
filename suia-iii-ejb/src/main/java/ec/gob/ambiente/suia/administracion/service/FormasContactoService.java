/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.administracion.service;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.FormasContacto;
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
public class FormasContactoService {

	@EJB
	private CrudServiceBean crudServiceBean;

	@SuppressWarnings("unchecked")
	public List<FormasContacto> buscarPorEstado(boolean estado)
			throws ServiceException {
		List<FormasContacto> listaFormasContacto = null;
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("estado", estado);
			listaFormasContacto = (List<FormasContacto>) crudServiceBean
					.findByNamedQuery(FormasContacto.FIND_BY_STATE, params);
		} catch (RuntimeException e) {
			throw new ServiceException(e);
		}
		return listaFormasContacto;
	}

	public FormasContacto buscarPorNombre(String nombre) {
		FormasContacto fc = (FormasContacto) crudServiceBean
				.getEntityManager()
				.createQuery(
						"SELECT c FROM FormasContacto c WHERE c.nombre =:paramNombre AND c.estado = true ORDER BY c.orden")
				.setParameter("paramNombre", nombre).getSingleResult();
		return fc;
	}
	
	public FormasContacto buscarPorId(Integer id) {
		FormasContacto fc = (FormasContacto) crudServiceBean
				.getEntityManager()
				.createQuery(
						"SELECT c FROM FormasContacto c WHERE c.id =:paramNombre AND c.estado = true ORDER BY c.orden")
				.setParameter("paramNombre", id).getSingleResult();
		return fc;
	}
}

