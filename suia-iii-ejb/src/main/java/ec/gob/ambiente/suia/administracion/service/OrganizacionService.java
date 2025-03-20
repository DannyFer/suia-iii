/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.administracion.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.Persona;
import ec.gob.ambiente.suia.exceptions.ServiceException;

/**
 * @author ishmael
 */
@Stateless
public class OrganizacionService {

    @EJB
    private CrudServiceBean crudServiceBean;

    public Organizacion buscarPorPersona(final Persona persona) throws ServiceException {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("persona", persona);
//            Organizacion org = crudServiceBean.findByNamedQuerySingleResult(Organizacion.FIND_BY_PERSON, params);
            List<Organizacion> orgs = (List<Organizacion>) crudServiceBean.findByNamedQuery(Organizacion.FIND_BY_PERSON, params);
            if (orgs.size() > 0) {
                return orgs.get(0);
            } else {
                return null;
               // throw new ServiceException("Error al recuperar la organización a la que pertenece la persona.");
            }
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }

    public Organizacion buscarPorPersonaUsuario(final Persona persona, String nombre) throws ServiceException {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("persona", persona);
            params.put("nombre", nombre);
//            Organizacion org = crudServiceBean.findByNamedQuerySingleResult(Organizacion.FIND_BY_PERSON, params);
            List<Organizacion> orgs = (List<Organizacion>) crudServiceBean.findByNamedQuery(Organizacion.FIND_BY_PERSON_NAME, params);
            if (orgs.size() > 0) {
                return orgs.get(0);
            } else {
                return null;
               // throw new ServiceException("Error al recuperar la organización a la que pertenece la persona.");
            }
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }

    public Organizacion buscarPorRuc(String ruc) throws ServiceException {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("ruc", ruc);
            List<Organizacion> orgs = (List<Organizacion>) crudServiceBean.findByNamedQuery(Organizacion.FIND_BY_RUC, params);
            if (orgs.size() > 0) {
                return orgs.get(0);
            } else {
                return null;
                // throw new ServiceException("Error al recuperar la organización a la que pertenece la persona.");
            }
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }

    public Boolean tieneOrganizacionPorPersona(final Persona persona)  {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("persona", persona);
//            Organizacion org = crudServiceBean.findByNamedQuerySingleResult(Organizacion.FIND_BY_PERSON, params);
            List<Organizacion> orgs = (List<Organizacion>) crudServiceBean.findByNamedQuery(Organizacion.FIND_BY_PERSON, params);
           return orgs.size()>0;
        } catch (RuntimeException e) {
           return false;
        }
    }
}
