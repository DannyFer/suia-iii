/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.administracion.service;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.FormasContacto;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.Persona;
import ec.gob.ambiente.suia.exceptions.ServiceException;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ishmael
 */
@Stateless
public class ContactoService {

	@EJB
	private CrudServiceBean crudServiceBean;

	@SuppressWarnings("unchecked")
	public List<Contacto> buscarPorPersona(final Persona persona)
			throws ServiceException {
		List<Contacto> listaContacto = null;
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("persona", persona);
			listaContacto = (List<Contacto>) crudServiceBean.findByNamedQuery(
					Contacto.FIND_BY_PERSON, params);
			for (Contacto contacto : listaContacto) {
				contacto.getFormasContacto().getId();
			}
		} catch (RuntimeException e) {
			throw new ServiceException(e);
		}
		return listaContacto;
	}

	public Contacto buscarMailPorPersona(Integer idPersona, String correo)
			throws ServiceException {
		Contacto c = null;
		try {
			c = (Contacto) crudServiceBean
					.getEntityManager()
					.createQuery(
							"SELECT c FROM Contacto c WHERE c.estado=true and c.formasContacto.id =:idFormaContacto and c.valor = :valor and c.persona.id=:idPersParam")
					.setParameter("idPersParam", idPersona)
					.setParameter("valor", correo)
					.setParameter("idFormaContacto", FormasContacto.EMAIL)
					.getSingleResult();
		} catch (RuntimeException se) {
			throw new ServiceException(se);
		}
		return c;
	}

	public void actualizarMail(Contacto c) {
		crudServiceBean.getEntityManager().merge(c);
	}

	@SuppressWarnings("unchecked")
	public List<Contacto> buscarPorOrganizacion(final Organizacion organizacion)
			throws ServiceException {
		List<Contacto> listaContacto = null;
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("organizacion", organizacion);
			listaContacto = (List<Contacto>) crudServiceBean.findByNamedQuery(
					Contacto.FIND_BY_ORGANIZATION, params);
			for (Contacto contacto : listaContacto) {
				contacto.getFormasContacto().getId();
			}
		} catch (RuntimeException e) {
			throw new ServiceException(e);
		}
		return listaContacto;
	}

	@SuppressWarnings("unchecked")
	public List<Contacto> buscarUsuarioNativeQuery(final String userName)
			throws ServiceException {
		List<Contacto> listaContacto = new ArrayList<Contacto>();
		try {
			
			listaContacto = (List<Contacto>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT c FROM Contacto c WHERE c.estado=true and c.organizacion.ruc= :userName ORDER BY c.id ASC")
					.setParameter("userName", userName)					
					.getResultList();
			
			if(listaContacto.isEmpty())
				listaContacto = (List<Contacto>) crudServiceBean
				.getEntityManager()
				.createQuery("SELECT c FROM Contacto c WHERE c.estado=true and c.persona.pin= :userName ORDER BY c.id ASC")
				.setParameter("userName", userName)					
				.getResultList();

		} catch (RuntimeException e) {
			throw new ServiceException(e);
		}
		return listaContacto;
	}

	/**
	 * Se consulta si existe el mail registrado
	 * 
	 * @param nombre
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Contacto> buscarMail(String valor) throws ServiceException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("codigoEmail", FormasContacto.EMAIL);
		params.put("valor", valor != null ? valor.trim() : "");
		return (List<Contacto>) crudServiceBean.findByNamedQueryPaginado(
				Contacto.FIND_BY_EMAIL, params,0,10);
	}
}
