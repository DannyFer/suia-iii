package ec.gob.ambiente.suia.prevencion.planemergente.service;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.IndicadorMedidaCorrectiva;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.List;

/**
 * Created by msit-erislan on 10/11/2015.
 */
@Stateless
public class IndicadorMedidaCorrectivaService {

    @EJB
    private CrudServiceBean crudServiceBean;

    public List<List<IndicadorMedidaCorrectiva>> guardar(List<List<IndicadorMedidaCorrectiva>> listIndicadores) {
        for (List<IndicadorMedidaCorrectiva> indicadores : listIndicadores) {
            for (IndicadorMedidaCorrectiva indicador : indicadores) {
                crudServiceBean.saveOrUpdate(indicador);
            }
        }
        return listIndicadores;
    }

    public void eliminar(List<IndicadorMedidaCorrectiva> indicadores) {
        for (IndicadorMedidaCorrectiva indicador : indicadores) {
            crudServiceBean.delete(indicador);
        }

    }

    public IndicadorMedidaCorrectiva guardar(IndicadorMedidaCorrectiva indicador) {
        return crudServiceBean.saveOrUpdate(indicador);
    }
}
