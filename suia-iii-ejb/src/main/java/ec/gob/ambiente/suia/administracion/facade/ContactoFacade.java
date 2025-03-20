/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.administracion.facade;

import ec.gob.ambiente.suia.administracion.service.ContactoService;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.Persona;
import ec.gob.ambiente.suia.exceptions.ServiceException;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 * 
 * @author ishmael
 */
@Stateless
public class ContactoFacade {

	@EJB
	private ContactoService contactoService;
	@EJB
	private CrudServiceBean crudServiceBean;

	public List<Contacto> buscarPorPersona(final Persona persona)
			throws ServiceException {
		return contactoService.buscarPorPersona(persona);
	}

	public List<Contacto> buscarPorOrganizacion(final Organizacion organizacion)
			throws ServiceException {
		return contactoService.buscarPorOrganizacion(organizacion);
	}

	public List<Contacto> buscarUsuarioNativeQuery(final String userName)
			throws ServiceException {
		return contactoService.buscarUsuarioNativeQuery(userName);
	}

	public Contacto guardar(Contacto contacto) throws ServiceException {
		return crudServiceBean.saveOrUpdate(contacto);
	}

	public void modificar(Contacto contacto) throws ServiceException {
		crudServiceBean.saveOrUpdate(contacto);
	}

	public List<Contacto> buscarMail(String valor) throws ServiceException {
		return contactoService.buscarMail(valor);
	}

	public Contacto buscarMailPorPersona(Integer idPersona, String correo)
			throws ServiceException {
		return contactoService.buscarMailPorPersona(idPersona, correo);
	}

	public void actualizarMail(Contacto c) {
		contactoService.actualizarMail(c);
	}
}
