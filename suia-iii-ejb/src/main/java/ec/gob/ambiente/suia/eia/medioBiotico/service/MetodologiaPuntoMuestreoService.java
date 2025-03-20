package ec.gob.ambiente.suia.eia.medioBiotico.service;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.MetodologiaPuntoMuestreo;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateless
public class MetodologiaPuntoMuestreoService {
    @EJB
    private CrudServiceBean crudServiceBean;

    @SuppressWarnings("unchecked")
    public List<MetodologiaPuntoMuestreo> listarMetodologias(boolean flora) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("flora",flora);
        return (List<MetodologiaPuntoMuestreo>) crudServiceBean
                .findByNamedQuery(MetodologiaPuntoMuestreo.LISTAR, params);
    }
}
