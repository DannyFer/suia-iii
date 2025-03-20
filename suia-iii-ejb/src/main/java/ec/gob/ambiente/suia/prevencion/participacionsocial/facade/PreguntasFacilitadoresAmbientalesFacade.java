/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.prevencion.participacionsocial.facade;

import ec.gob.ambiente.suia.domain.PreguntasFacilitadoresAmbientales;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.prevencion.participacionsocial.service.PreguntasFacilitadoresAmbientalesService;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.List;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 *
 * @author frank torres
 * @version Revision: 1.0
 *          <p>
 *          [Autor: frank torres, Fecha: 27/08/2015]
 *          </p>
 */
@Stateless
public class PreguntasFacilitadoresAmbientalesFacade {
    @EJB
    private PreguntasFacilitadoresAmbientalesService preguntasFacilitadoresAmbientalesService;

    public PreguntasFacilitadoresAmbientales guardar(PreguntasFacilitadoresAmbientales preguntasFacilitadoresAmbientales) {
        return preguntasFacilitadoresAmbientalesService.guardar(preguntasFacilitadoresAmbientales);
    }

    public List<PreguntasFacilitadoresAmbientales> obtenerPreguntasPorParticipacion(Integer idParticipacion) throws ServiceException {
        return preguntasFacilitadoresAmbientalesService.obtenerPreguntasPorParticipacion(idParticipacion);

    }

    public void guardarPreguntasPorParticipacion(List<PreguntasFacilitadoresAmbientales> preguntas) throws ServiceException {
        preguntasFacilitadoresAmbientalesService.guardarPreguntasPorParticipacion(preguntas);
    }

}

