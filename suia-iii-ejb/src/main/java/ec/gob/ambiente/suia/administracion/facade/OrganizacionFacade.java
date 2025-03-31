/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.administracion.facade;

import ec.gob.ambiente.suia.administracion.service.OrganizacionService;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.Persona;
import ec.gob.ambiente.suia.exceptions.ServiceException;

import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 *
 * @author ishmael
 */
@Stateless
public class OrganizacionFacade {

    @EJB
    private OrganizacionService organizacionService;
    @EJB
    private CrudServiceBean crudServiceBean;

    @Deprecated
    public Organizacion buscarPorPersona(final Persona persona) throws ServiceException {
        return organizacionService.buscarPorPersona(persona);
    }
    public Organizacion buscarPorPersona(final Persona persona, String nombre) throws ServiceException {
        return organizacionService.buscarPorPersonaUsuario(persona, nombre);
    }

    public Organizacion buscarPorRuc(String ruc) throws ServiceException {
        return organizacionService.buscarPorRuc(ruc);
    }

    public void modificar(Organizacion organizacion) throws ServiceException {
        crudServiceBean.saveOrUpdate(organizacion);
    }
  public Boolean tieneOrganizacionPorPersona(final Persona persona)  {
        return organizacionService.tieneOrganizacionPorPersona(persona);
    }
}
