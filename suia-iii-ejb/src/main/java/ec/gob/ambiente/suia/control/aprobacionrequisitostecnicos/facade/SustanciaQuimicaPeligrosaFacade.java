/*
 * Copyright 2015 MAGMASOFT
 * Todos los derechos reservados
 */
package ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.service.SustanciaQuimicaPeligrosaService;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.SustanciaQuimicaPeligrosa;

/**
 * <b> Clase facade para las sustancias químicas peligrosas. </b>
 * 
 * @author Javier Lucero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: Javier Lucero $, $Date: 05/06/2015 $]
 *          </p>
 */
@Stateless
public class SustanciaQuimicaPeligrosaFacade {

	@EJB
	private CrudServiceBean crudServiceBean;

	@EJB
	private SustanciaQuimicaPeligrosaService sustanciaQuimicaPeligrosaService;

	public List<SustanciaQuimicaPeligrosa> getSustanciasQuimicasPeligrosas(String filtro) {
		return sustanciaQuimicaPeligrosaService.getListaSustanciasQuimicasPeligrosas(filtro);
	}

	public List<SustanciaQuimicaPeligrosa> buscarSustanciaQuimicaPadres(String filtro) {
		return sustanciaQuimicaPeligrosaService.buscarSustanciasQuimicasPadres(filtro);
	}

	public List<SustanciaQuimicaPeligrosa> buscarSustanciaQuimicaPorPadre(SustanciaQuimicaPeligrosa padre) {
		return sustanciaQuimicaPeligrosaService.buscarSustanciaQuimicaPorPadre(padre);
	}
	
	public SustanciaQuimicaPeligrosa buscarSustanciaQuimicaPorId(Integer id){
		return sustanciaQuimicaPeligrosaService.buscarSustanciaQuimicaPorId(id);
	}
}
