/*
 * Copyright 2015 MAGMASOFT
 * Todos los derechos reservados
 */
package ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.service.InformacionPatioManiobrasService;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.AprobacionRequisitosTecnicos;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.InformacionPatioManiobra;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.validacionseccion.facade.ValidacionSeccionesFacade;

/**
 * <b> Clase facade para los requisitos del conductor. </b>
 * 
 * @author Javier Lucero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: Javier Lucero $, $Date: 04/06/2015 $]
 *          </p>
 */
@Stateless
public class InformacionPatioManiobrasFacade {

	@EJB
	private CrudServiceBean crudServiceBean;

	@EJB
	private InformacionPatioManiobrasService informacionPatioManiobrasService;

	@EJB
	private AprobacionRequisitosTecnicosFacade aprobacionRequisitosTecnicosFacade;

	@EJB
	private RequisitosVehiculoFacade requisitosVehiculoFacade;

	@EJB
	private RequisitosConductorFacade requisitosConductorFacade;

	@EJB
	private AreaFacade areaFacade;

	@EJB
	private ValidacionSeccionesFacade validacionSeccionesFacade;

	public InformacionPatioManiobra guardar(InformacionPatioManiobra informacionPatioManiobra) throws ServiceException {
		return crudServiceBean.saveOrUpdate(informacionPatioManiobra);

	}

	public InformacionPatioManiobra getInformacionPatioManiobra(
			AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos) throws ServiceException {
		return informacionPatioManiobrasService.getInformacionPatioManiobra(aprobacionRequisitosTecnicos);
	}

	public boolean isNumVehiculosPatioDiferenteRequisitosVehiculoConductores(
			AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos, Integer numeroVehiculosPatio)
			throws ServiceException {
		if (!numeroVehiculosPatio.equals(requisitosVehiculoFacade
				.getNumeroVehiculosRegistrados(aprobacionRequisitosTecnicos))
				|| !numeroVehiculosPatio.equals(requisitosConductorFacade
						.getNumeroConductores(aprobacionRequisitosTecnicos))) {
			return true;
		}
		return false;

	}
}
