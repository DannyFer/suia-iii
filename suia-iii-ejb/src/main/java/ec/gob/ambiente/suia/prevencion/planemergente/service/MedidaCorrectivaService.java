package ec.gob.ambiente.suia.prevencion.planemergente.service;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.MedidaCorrectiva;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.List;

/**
 * Created by msit-erislan on 10/11/2015.
 */
@Stateless
public class MedidaCorrectivaService {

    @EJB
    private CrudServiceBean crudServiceBean;

    public List<MedidaCorrectiva> guardar(List<MedidaCorrectiva> medidasCorrectivas) {
        for (MedidaCorrectiva medidaCorrectiva : medidasCorrectivas) {
            crudServiceBean.saveOrUpdate(medidaCorrectiva);
        }
        return medidasCorrectivas;
    }

    public void eliminar(List<MedidaCorrectiva> medidasCorrectivas) {
        for (MedidaCorrectiva medidaCorrectiva : medidasCorrectivas) {
            crudServiceBean.delete(medidaCorrectiva);
        }
    }

    public MedidaCorrectiva guardar(MedidaCorrectiva correctiva) {
        return crudServiceBean.saveOrUpdate(correctiva);
    }
}
