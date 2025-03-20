package ec.gob.ambiente.suia.prevencion.planemergente.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Fase;
import ec.gob.ambiente.suia.domain.TipoSector;
import ec.gob.ambiente.suia.domain.TipoSectorFase;

/**
 * Created by msit-erislan on 10/11/2015.
 */
@Stateless
public class TipoSectorFaseService {

    @EJB
    private CrudServiceBean crudServiceBean;

    public List<Fase> findFaseByTipoSector(TipoSector tipoSector) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("tipoSector", tipoSector);
        return (List<Fase>) crudServiceBean.findByNamedQuery(TipoSectorFase.FIND_FASES_BY_TIPO_SECTOR, parameters);
    }
}
