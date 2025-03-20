package ec.gob.ambiente.suia.prevencion.planemergente.service;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.CatalogoCategoriaSistema;
import ec.gob.ambiente.suia.domain.PlanEmergente;
import ec.gob.ambiente.suia.domain.TipoSector;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by msit-erislan on 10/11/2015.
 */
@Stateless
public class PlanEmergenteService {

    @EJB
    private CrudServiceBean crudServiceBean;

    public List<TipoSector> getSectores() {
        Map<String, Object> parameters = new HashMap<String, Object>();
        List<Integer> ids = new ArrayList<Integer>();
        ids.add(TipoSector.TIPO_SECTOR_HIDROCARBUROS);
        ids.add(TipoSector.TIPO_SECTOR_MINERIA);
        ids.add(TipoSector.TIPO_SECTOR_ELECTRICO);
        ids.add(TipoSector.TIPO_SECTOR_TELECOMUNICACIONES);
        ids.add(TipoSector.TIPO_SECTOR_SANEAMIENTO);
        ids.add(TipoSector.TIPO_SECTOR_OTROS);
        parameters.put("ids", ids);
        return (List<TipoSector>) crudServiceBean.findByNamedQuery(TipoSector.FIND_BY_ID_IN_LIST, parameters);
    }

    public List<CatalogoCategoriaSistema> getCategoriasPorSector(TipoSector tipoSector) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("tipoSector", tipoSector);
        return (List<CatalogoCategoriaSistema>) crudServiceBean.findByNamedQuery(CatalogoCategoriaSistema.FIND_ALL_BY_TIPO_SECTOR, parameters);
    }

    public List<CatalogoCategoriaSistema> getCategoriasPorSectorId(Integer id) {
        TipoSector tipoSector = crudServiceBean.find(TipoSector.class, id);
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("tipoSector", tipoSector);
        return (List<CatalogoCategoriaSistema>) crudServiceBean.findByNamedQuery(CatalogoCategoriaSistema.FIND_ALL_BY_TIPO_SECTOR, parameters);
    }

    public PlanEmergente create(PlanEmergente planEmergente) {
        return crudServiceBean.saveOrUpdate(planEmergente);
    }

    public PlanEmergente getById(Integer id) {
        return crudServiceBean.find(PlanEmergente.class, id);
    }
}
