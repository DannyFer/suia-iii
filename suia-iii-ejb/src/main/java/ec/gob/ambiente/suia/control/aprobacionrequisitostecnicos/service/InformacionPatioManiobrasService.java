/*
 * Copyright 2015 MAGMASOFT
 * Todos los derechos reservados
 */
package ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.service;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.AprobacionRequisitosTecnicos;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.InformacionPatioManiobra;
import ec.gob.ambiente.suia.exceptions.ServiceException;

/**
 * <b> Clase para los servicios de los requisitos del conductor. </b>
 * 
 * @author Javier Lucero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: Javier Lucero $, $Date: 04/06/2015 $]
 *          </p>
 */
@Stateless
public class InformacionPatioManiobrasService {

	@EJB
	private CrudServiceBean crudServiceBean;

	/**
	 * 
	 * <b> Metodo que obtiene la lista de todos los conductores. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 05/06/2015]
	 * </p>
	 * 
	 * @return
	 */

	public InformacionPatioManiobra getInformacionPatioManiobra(
			AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos) throws ServiceException {
		List<InformacionPatioManiobra> listaInformacionPatio = null;
		try {
			listaInformacionPatio = crudServiceBean.getEntityManager()
					.createQuery("From InformacionPatioManiobra i where i.aprobacionRequisitosTecnicos=:aprobacion")
					.setParameter("aprobacion", aprobacionRequisitosTecnicos).getResultList();
			if (listaInformacionPatio == null || listaInformacionPatio.isEmpty()) {
				return null;
			} else {
				InformacionPatioManiobra info = listaInformacionPatio.get(0);				
				info.getUbicacionesGeografica().getUbicacionesGeografica().getUbicacionesGeografica();
				return info;
			}
		} catch (Exception e) {
			throw new ServiceException("Ocurrió un problema al recuperar los datos", e);
		}

	}
}
