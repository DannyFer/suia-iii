/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.desechoPeligroso.facade;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.desechoPeligroso.service.IncompatibilidadDesechoServiceBean;
import ec.gob.ambiente.suia.domain.IncompatibilidadDesechoPeligroso;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author mayriliscs
 * @version Revision: 1.0
 *          <p>
 *          [Autor: mayriliscs, Fecha: 16/03/2015]
 *          </p>
 */
@Stateless
public class IncompatibilidadDesechoFacade {

	@EJB
	private IncompatibilidadDesechoServiceBean serviceBean;

	public List<IncompatibilidadDesechoPeligroso> listarIncompatibilidadDesechoPeligrosos() {
		return serviceBean.listarIncompatibilidadDesechoPeligrosos();
	}

	public List<IncompatibilidadDesechoPeligroso> buscarIncompatibilidadDesechoPeligrososPadres(String filtro) {
		return serviceBean.buscarIncompatibilidadDesechoPeligrososPadres(filtro);
	}

	public List<IncompatibilidadDesechoPeligroso> buscarIncompatibilidadDesechoPeligrososPorPadre(
			IncompatibilidadDesechoPeligroso padre) {
		return serviceBean.buscarIncompatibilidadDesechoPeligrososPorPadre(padre);
	}
}
