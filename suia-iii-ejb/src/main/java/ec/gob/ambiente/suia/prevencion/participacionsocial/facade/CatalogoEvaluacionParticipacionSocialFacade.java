/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.prevencion.participacionsocial.facade;

import ec.gob.ambiente.suia.domain.CatalogoEvaluacionParticipacionSocial;
import ec.gob.ambiente.suia.prevencion.participacionsocial.service.CatalogoEvaluacionParticipacionSocialService;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.List;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 *
 * @author frank torres
 * @version Revision: 1.0
 *          <p>
 *          [Autor: frank torres, Fecha: 11/03/2015]
 *          </p>
 */
@Stateless
public class CatalogoEvaluacionParticipacionSocialFacade {
    @EJB
    CatalogoEvaluacionParticipacionSocialService catalogoEvaluacionParticipacionSocialService;

    public CatalogoEvaluacionParticipacionSocial guardar(CatalogoEvaluacionParticipacionSocial catalogoEvaluacionParticipacionSocial) {
        return catalogoEvaluacionParticipacionSocialService.guardar(catalogoEvaluacionParticipacionSocial);
    }


    public List<CatalogoEvaluacionParticipacionSocial> buscarCatalogosEvaluacionPSPorGrupo(String grupo) {
        return catalogoEvaluacionParticipacionSocialService.buscarCatalogosEvaluacionPSPorGrupo(grupo);
    }


}
