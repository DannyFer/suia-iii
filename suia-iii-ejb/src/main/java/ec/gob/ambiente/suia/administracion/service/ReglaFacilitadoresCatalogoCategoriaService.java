/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.administracion.service;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.ReglaFacilitadoresCatalogoCategoria;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author frank
 */
@LocalBean
@Stateless
public class ReglaFacilitadoresCatalogoCategoriaService {

    @EJB
    private CrudServiceBean crudServiceBean;

    public Boolean listarAreasPadre(Integer idEstudio, Integer idCategoria) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("idEstudio", idEstudio);
        param.put("idCategoria", idCategoria);
        List<ReglaFacilitadoresCatalogoCategoria> lista = (List<ReglaFacilitadoresCatalogoCategoria>)
                crudServiceBean.findByNamedQuery(
                        ReglaFacilitadoresCatalogoCategoria.BUSCAR_REGLA, param);

        return lista.size() > 0;

    }


}
