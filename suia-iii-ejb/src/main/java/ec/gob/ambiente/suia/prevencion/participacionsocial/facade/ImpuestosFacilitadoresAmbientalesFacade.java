/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.prevencion.participacionsocial.facade;

import ec.gob.ambiente.suia.domain.ImpuestosFacilitadoresAmbientales;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.prevencion.participacionsocial.service.ImpuestosFacilitadoresAmbientalesService;

import javax.ejb.EJB;
import javax.ejb.Stateless;

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
public class ImpuestosFacilitadoresAmbientalesFacade {
    @EJB
    private ImpuestosFacilitadoresAmbientalesService impuestosFacilitadoresAmbientalesService;

    public ImpuestosFacilitadoresAmbientales guardar(ImpuestosFacilitadoresAmbientales impuestosFacilitadoresAmbientales)
            throws Exception {

        return impuestosFacilitadoresAmbientalesService.guardar(impuestosFacilitadoresAmbientales);
    }

    public ImpuestosFacilitadoresAmbientales obtenerImpuestoPorUbicacion(String codigoInec) throws ServiceException {

        return impuestosFacilitadoresAmbientalesService.obtenerImpuestoPorUbicacion(codigoInec);
    }

}

