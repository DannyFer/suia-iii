package ec.gob.ambiente.suia.prevencion.planemergente.service;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.EventoCoordenada;

/**
 * Created by msit-erislan on 10/11/2015.
 */
@Stateless
public class EventoCoordenadaService {

    @EJB
    private CrudServiceBean crudServiceBean;

    public List<EventoCoordenada> crear(List<EventoCoordenada> coordenadas) {
        for(EventoCoordenada coordenada: coordenadas) {
            coordenada = crudServiceBean.saveOrUpdate(coordenada);
        }
        return coordenadas;
    }

    public void borrarSiExiste(EventoCoordenada eventoCoordenada) {
        if(crudServiceBean.find(EventoCoordenada.class, eventoCoordenada.getId()) != null) {
            crudServiceBean.delete(eventoCoordenada);
        }
    }

    public void borrarSiExiste(List<EventoCoordenada> eventosCoordenadas) {
        for(EventoCoordenada eventoCoordenada: eventosCoordenadas)
            borrarSiExiste(eventoCoordenada);
    }
}
