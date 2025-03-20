package ec.gob.ambiente.suia.prevencion.planemergente.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.EventoEtapaFase;
import ec.gob.ambiente.suia.domain.EventoOcurrido;
import ec.gob.ambiente.suia.domain.PlanEmergente;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;

/**
 * Created by msit-erislan on 10/11/2015.
 */
@Stateless
public class EventoOcurridoService {

    @EJB
    private CrudServiceBean crudServiceBean;

    public EventoOcurrido crear(EventoOcurrido eventoOcurrido) {
        return crudServiceBean.saveOrUpdate(eventoOcurrido);
    }

    public List<EventoOcurrido> buscarPorProyecto(ProyectoLicenciamientoAmbiental proyectoLicenciamientoAmbiental) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("proyecto", proyectoLicenciamientoAmbiental);
        return (List<EventoOcurrido>) crudServiceBean.findByNamedQuery(EventoOcurrido.FIND_ALL_BY_PROYECTO, parameters);
    }

    public List<EventoOcurrido> buscarPorPlanEmergente(PlanEmergente planEmergente) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("planEmergente", planEmergente.getId());
        return (List<EventoOcurrido>) crudServiceBean.findByNamedQuery(EventoOcurrido.FIND_ALL_BY_PLAN_EMERGENTE, parameters);
    }

    public void borrarSiExiste(EventoOcurrido eventoOcurrido) {
        if(eventoOcurrido.getId() != null && crudServiceBean.find(EventoOcurrido.class, eventoOcurrido.getId()) != null) {
            crudServiceBean.delete(eventoOcurrido);
        }
    }

    public void borrarSiExiste(List<EventoOcurrido> eventosOcurridos) {
        for(EventoOcurrido eventoOcurrido: eventosOcurridos)
            borrarSiExiste(eventoOcurrido);
    }

    public void borrarEtapasSiExisten(List<EventoEtapaFase> eventoEtapaFases) {
        for (EventoEtapaFase eventoEtapaFase : eventoEtapaFases) {
            if(eventoEtapaFase.getId() != null && crudServiceBean.find(EventoEtapaFase.class, eventoEtapaFase.getId()) != null) {
                crudServiceBean.delete(eventoEtapaFase);
            }
        }
    }

    public List<EventoEtapaFase> crearEtapas(List<EventoEtapaFase> eventoEtapaFases) {
        for (EventoEtapaFase eventoEtapaFase : eventoEtapaFases) {
            eventoEtapaFase = crudServiceBean.saveOrUpdate(eventoEtapaFase);
        }
        return eventoEtapaFases;
    }

    public EventoOcurrido editar(EventoOcurrido eventoOcurrido) {
        return crudServiceBean.saveOrUpdate(eventoOcurrido);
    }
}
