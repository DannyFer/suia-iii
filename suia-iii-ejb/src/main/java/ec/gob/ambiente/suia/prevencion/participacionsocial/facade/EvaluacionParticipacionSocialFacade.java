/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.prevencion.participacionsocial.facade;

import ec.gob.ambiente.suia.domain.EvaluacionParticipacionSocial;
import ec.gob.ambiente.suia.prevencion.participacionsocial.service.EvaluacionParticipacionSocialService;

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
public class EvaluacionParticipacionSocialFacade {
    @EJB
    EvaluacionParticipacionSocialService evaluacionParticipacionSocialService;

    public EvaluacionParticipacionSocial guardar(EvaluacionParticipacionSocial evaluacionParticipacionSocial) {
        return evaluacionParticipacionSocialService.guardar(evaluacionParticipacionSocial);
    }

    public void guardarLista(List<EvaluacionParticipacionSocial> evaluacionParticipacionSocial) {
        evaluacionParticipacionSocialService.guardarLista(evaluacionParticipacionSocial);
    }

    public List<EvaluacionParticipacionSocial> buscarEvaluacionParticipacionSocial(Integer idProyecto) {
        return evaluacionParticipacionSocialService.buscarEvaluacionParticipacionSocial(idProyecto);
    }


}
