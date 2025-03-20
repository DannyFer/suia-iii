/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.prevencion.participacionsocial.facade;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.domain.ParticipacionSocialAmbiental;
import ec.gob.ambiente.suia.domain.RegistroMediosParticipacionSocial;
import ec.gob.ambiente.suia.prevencion.participacionsocial.service.RegistroMediosParticipacionSocialService;

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
public class RegistroMediosParticipacionSocialFacade {
    @EJB
    RegistroMediosParticipacionSocialService registroMediosParticipacionSocialService;


    public List<RegistroMediosParticipacionSocial> consultar(ParticipacionSocialAmbiental participacionSocialAmbiental) {
        return registroMediosParticipacionSocialService.consultar(participacionSocialAmbiental);
    }

    public void guardar(List<RegistroMediosParticipacionSocial> registros, List<RegistroMediosParticipacionSocial> registrosEliminados) {
        registroMediosParticipacionSocialService.guardar(registros, registrosEliminados);
    }

}
