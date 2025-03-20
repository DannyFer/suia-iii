package ec.gob.ambiente.suia.eia.mediofisico.service;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Clima;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateless
public class ClimaMedioFisicoService {
    @EJB
    private CrudServiceBean crudServiceBean;

    @SuppressWarnings("unchecked")
    public List<Clima> climaXEiaId(EstudioImpactoAmbiental estudioImpactoAmbiental) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("estudioImpactoAmbiental", estudioImpactoAmbiental);

        List<Clima> listaClima = (List<Clima>) crudServiceBean
                .findByNamedQuery(Clima.OBTENER_POR_EIA, params);

        return listaClima;
    }

}
