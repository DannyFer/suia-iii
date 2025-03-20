/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.administracion.facade;

import ec.gob.ambiente.suia.administracion.service.ReglaFacilitadoresCatalogoCategoriaService;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * @author christian
 */
@LocalBean
@Stateless
public class ReglaFacilitadoresCatalogoCategoriaFacade {
    @EJB
    private ReglaFacilitadoresCatalogoCategoriaService reglaFacilitadoresCatalogoCategoriaService;

    public Boolean listarAreasPadre(Integer idEstudio, Integer idCategoria) {
        return reglaFacilitadoresCatalogoCategoriaService.listarAreasPadre(idEstudio, idCategoria);
    }
}