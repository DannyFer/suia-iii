package ec.gob.ambiente.suia.prevencion.planemergente.service;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.AccionEmergenteCoordenada;

/**
 * Created by msit-erislan on 10/11/2015.
 */
@Stateless
public class AccionEmergenteCoordenadaService {

    @EJB
    private CrudServiceBean crudServiceBean;

    public List<AccionEmergenteCoordenada> crear(List<AccionEmergenteCoordenada> coordenadas) {
        for(AccionEmergenteCoordenada coordenada: coordenadas) {
            coordenada = crudServiceBean.saveOrUpdate(coordenada);
        }
        return coordenadas;
    }

    public void borrarSiExiste(AccionEmergenteCoordenada accionEmergenteCoordenada) {
        if(crudServiceBean.find(AccionEmergenteCoordenada.class, accionEmergenteCoordenada.getId()) != null) {
            crudServiceBean.delete(accionEmergenteCoordenada);
        }
    }

    public void borrarSiExiste(List<AccionEmergenteCoordenada> coordenadas) {
        for(AccionEmergenteCoordenada coordenada: coordenadas)
            borrarSiExiste(coordenada);
    }
}
