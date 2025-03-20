package ec.gob.ambiente.suia.prevencion.planemergente.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.AccionEmergente;
import ec.gob.ambiente.suia.domain.EventoOcurrido;
import ec.gob.ambiente.suia.domain.IndicadorMedidaCorrectiva;
import ec.gob.ambiente.suia.domain.MedidaCorrectiva;

/**
 * Created by msit-erislan on 10/11/2015.
 */
@Stateless
public class AccionEmergenteService {

    @EJB
    private CrudServiceBean crudServiceBean;
    @EJB
    protected MedidaCorrectivaService medidaCorrectivaService;
    @EJB
    protected IndicadorMedidaCorrectivaService indicadorMedidaCorrectivaService;

    public void crear(List<EventoOcurrido> eventosOcurridos) {
        for (EventoOcurrido eventoOcurrido : eventosOcurridos) {
            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("eventoOcurrido", eventoOcurrido);
            List<AccionEmergente> accionsEmergentes = (List<AccionEmergente>) crudServiceBean.findByNamedQuery(AccionEmergente.FIND_BY_EVENTO_OCURRIDO, parameters);
            switch (accionsEmergentes.size()) {
                case 0:
                    AccionEmergente accionYaImplementada = new AccionEmergente(AccionEmergente.YA_IMPLEMENTADA);
                    accionYaImplementada.setEventoOcurrido(eventoOcurrido);
                    AccionEmergente accionPorImplementar = new AccionEmergente(AccionEmergente.POR_IMPLEMENTAR);
                    accionPorImplementar.setEventoOcurrido(eventoOcurrido);
                    crudServiceBean.saveOrUpdate(accionYaImplementada);
                    crudServiceBean.saveOrUpdate(accionPorImplementar);
                    break;
                case 1:
                    AccionEmergente accionEmergente = accionsEmergentes.get(0);
                    if (accionEmergente.getTipo() == AccionEmergente.YA_IMPLEMENTADA) {
                        accionPorImplementar = new AccionEmergente(AccionEmergente.POR_IMPLEMENTAR);
                        accionPorImplementar.setEventoOcurrido(eventoOcurrido);
                        crudServiceBean.saveOrUpdate(accionPorImplementar);
                    } else {
                        accionYaImplementada = new AccionEmergente(AccionEmergente.YA_IMPLEMENTADA);
                        accionYaImplementada.setEventoOcurrido(eventoOcurrido);
                        crudServiceBean.saveOrUpdate(accionYaImplementada);
                    }
                    break;
                case 2:
                default:
                    break;
            }
        }
    }

    public List<AccionEmergente> buscarPorEventos(List<EventoOcurrido> eventosOcurridos, Integer tipo) {
        if(eventosOcurridos.isEmpty())
            return new ArrayList<AccionEmergente>();
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("eventosOcurridos", eventosOcurridos);
        parameters.put("tipo", tipo);
        return (List<AccionEmergente>) crudServiceBean.findByNamedQuery(AccionEmergente.FIND_BY_EVENTOS_OCURRIDOS_AND_TYPE, parameters);
    }

    public AccionEmergente editar(AccionEmergente accionEmergente) {
        return crudServiceBean.saveOrUpdate(accionEmergente);
    }

    public void createAccionInformeFinal(AccionEmergente accionEmergente) {
        AccionEmergente accionInformeFinal = accionEmergente.copiar();
        accionInformeFinal.setTipo(AccionEmergente.INFORME_FINAL);
        List<MedidaCorrectiva> medidas = accionInformeFinal.getMedidaCorrectivas();
        accionInformeFinal.setMedidaCorrectivas(new ArrayList<MedidaCorrectiva>());
        accionInformeFinal = editar(accionInformeFinal);

        for (MedidaCorrectiva correctiva : medidas) {
            MedidaCorrectiva medidaCorrectiva = correctiva.copiar();
            medidaCorrectiva.setAccionEmergente(accionInformeFinal);
            List<IndicadorMedidaCorrectiva> indicadores = medidaCorrectiva.getIndicadores();
            medidaCorrectiva.setIndicadores(new ArrayList<IndicadorMedidaCorrectiva>());
            medidaCorrectiva = medidaCorrectivaService.guardar(medidaCorrectiva);

            for (IndicadorMedidaCorrectiva indicador : indicadores) {
                IndicadorMedidaCorrectiva indicadorMedidaCorrectiva = indicador.copiar();
                indicadorMedidaCorrectiva.setMedidaCorrectiva(medidaCorrectiva);
                indicadorMedidaCorrectiva = indicadorMedidaCorrectivaService.guardar(indicadorMedidaCorrectiva);
                medidaCorrectiva.getIndicadores().add(indicadorMedidaCorrectiva);
            }
            accionInformeFinal.getMedidaCorrectivas().add(medidaCorrectiva);
        }
    }
}
