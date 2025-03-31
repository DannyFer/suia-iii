/**
 * Copyright (c) 2015 MAGMASOFT (Innovando tecnologia)
 * Todos los derechos reservados.
 * Este software es confidencial y debe usarlo de acorde con los términos de uso.
 */
package ec.gob.ambiente.suia.eia.mediofisico.service;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Laboratorio;
import ec.gob.ambiente.suia.exceptions.ServiceException;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Clase que 
 * @author Juan Gabriel Guzmán.
 * @version 1.0
 */
@Stateless
public class LaboratorioService {


    @EJB
    private CrudServiceBean crudServiceBean;
    public List<Laboratorio> getLaboratorios(String query) throws ServiceException{
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("paramQueryNombre", "%"+query.toUpperCase()+"%");
        try {
            return (List<Laboratorio>) crudServiceBean.findByNamedQuery(Laboratorio.FIND_BY_QUERY_NOMBRE, params);
        }
        catch(RuntimeException e){
            throw new ServiceException("No se pudo recuperar la lista de laboratorios", e);
        }

    }
}
