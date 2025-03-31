/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.prevencion.participacionsocial.facade;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.domain.CatalogoMediosParticipacionSocial;
import ec.gob.ambiente.suia.prevencion.participacionsocial.service.CatalogoMediosParticipacionSocialService;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 *
 * @author oscar campana
 * @version Revision: 1.0
 *          <p>
 *          [Autor: oscar campana, Fecha: 16/09/2015]
 *          </p>
 */
@Stateless
public class CatalogoMediosParticipacionSocialFacade {
    @EJB
    CatalogoMediosParticipacionSocialService catalogoMediosParticipacionSocialService;


    public List<CatalogoMediosParticipacionSocial> obtenerCatalogoCompleto() {
        return catalogoMediosParticipacionSocialService.obtenerCatalogo();
    }

    public List<CatalogoMediosParticipacionSocial> buscarCatalogoMediosParticipacionSocialPorId(Integer id) {
        return catalogoMediosParticipacionSocialService.buscarCatalogoMediosParticipacionSocialPorId(id);
    }

}
