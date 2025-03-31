/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.prevencion.participacionsocial.facade;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.domain.MecanismoParticipacionSocialAmbiental;
import ec.gob.ambiente.suia.domain.ParticipacionSocialAmbiental;
import ec.gob.ambiente.suia.prevencion.participacionsocial.service.MecanismosParticipacionSocialService;

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
public class MecanismosParticipacionSocialFacade {
    @EJB
    MecanismosParticipacionSocialService mecanismoParticipacionSocialAmbientalService;


    public List<MecanismoParticipacionSocialAmbiental> consultar(ParticipacionSocialAmbiental participacionSocialAmbiental) {
        return mecanismoParticipacionSocialAmbientalService.consultar(participacionSocialAmbiental);
    }
    
    public List<MecanismoParticipacionSocialAmbiental> consultarPublicaPermanente(ParticipacionSocialAmbiental participacionSocialAmbiental) {
        return mecanismoParticipacionSocialAmbientalService.consultarPublicaPermanente(participacionSocialAmbiental);
    }   
    
    public List<MecanismoParticipacionSocialAmbiental> consultarInicioCierre(ParticipacionSocialAmbiental participacionSocialAmbiental) {
        return mecanismoParticipacionSocialAmbientalService.consultarInicioCierre(participacionSocialAmbiental);
    }

    public void guardar(List<MecanismoParticipacionSocialAmbiental> registros, List<MecanismoParticipacionSocialAmbiental> registrosEliminados) {
        mecanismoParticipacionSocialAmbientalService.guardar(registros, registrosEliminados);
    }

}
