/*
 * Copyright 2015 MAGMASOFT
 * Todos los derechos reservados
 */
package ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.service.RequisitosConductorService;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.AprobacionRequisitosTecnicos;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.RequisitosConductor;
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
public class RequisitosConductorFacade {

	@EJB
	private CrudServiceBean crudServiceBean;

	@EJB
	private RequisitosConductorService requisitosConductorService;

	@EJB
	private AprobacionRequisitosTecnicosFacade aprobacionRequisitosTecnicosFacade;

	@EJB
	private ValidacionSeccionesFacade validacionSeccionesFacade;

	public RequisitosConductor guardarConductor(RequisitosConductor conductor) {
		return crudServiceBean.saveOrUpdate(conductor);
	}

	public List<RequisitosConductor> getListaConductores(AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos) {
		return requisitosConductorService.getListaConductores(aprobacionRequisitosTecnicos);
	}

	public boolean isPageRequitosConductorRequerida(AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos) throws ServiceException {
		if (aprobacionRequisitosTecnicos.isIniciadoPorNecesidad()) {
			return false;
		} else {
			return aprobacionRequisitosTecnicos.isProyectoExPost();
		}

	}

	public void guardarPaginaComoCompleta(AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos)
			throws ServiceException {
		validacionSeccionesFacade.guardarValidacionSeccion("AprobacionRequisitosTecnicos", "requisitosConductor",
				aprobacionRequisitosTecnicos.getId().toString());
	}

	public void guardarPaginaComoIncompleta(AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos)
			throws ServiceException {
		validacionSeccionesFacade.guardarValidacionSeccion("AprobacionRequisitosTecnicos", "requisitosConductor",
				aprobacionRequisitosTecnicos.getId().toString(), false);
	}

	public Integer getNumeroConductores(AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos) {
		List<RequisitosConductor> conductores = getListaConductores(aprobacionRequisitosTecnicos);
		return (conductores != null) ? conductores.size() : 0;
	}

}
