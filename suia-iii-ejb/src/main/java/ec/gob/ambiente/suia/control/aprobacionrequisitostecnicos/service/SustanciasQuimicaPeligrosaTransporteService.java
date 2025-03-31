/*
 * Copyright 2015 MAGMASOFT
 * Todos los derechos reservados
 */
package ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.service;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.AprobacionRequisitosTecnicos;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.SustanciaQuimicaPeligrosaTransporte;
import ec.gob.ambiente.suia.exceptions.ServiceException;

/**
 * <b> Clase para los servicios de las sustancias para la transportacion. </b>
 * 
 * @author Javier Lucero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: Javier Lucero $, $Date: 18/06/2015 $]
 *          </p>
 */
@Stateless
public class SustanciasQuimicaPeligrosaTransporteService {

	private static final Logger LOG = Logger.getLogger(SustanciasQuimicaPeligrosaTransporteService.class);

	@EJB
	private CrudServiceBean crudServiceBean;

	/**
	 * 
	 * <b> Metodo que obtiene la lista de las sustancis peligrosas. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 24/06/2015]
	 * </p>
	 * 
	 * @param aprobacionRequisitosTecnicos
	 *            : proyecto
	 * @return List<SustanciaQuimicaPeligrosaTransporte>: lista de las
	 *         sustancias peligrosas
	 * @throws ServiceException
	 *             : Excepcion
	 */
	public List<SustanciaQuimicaPeligrosaTransporte> getListaSustanciaQuimicaPeligrosaTransporteUbicacion(
			AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos) throws ServiceException {
		List<SustanciaQuimicaPeligrosaTransporte> list = new ArrayList<SustanciaQuimicaPeligrosaTransporte>();
		Query query = crudServiceBean
				.getEntityManager()
				.createQuery(
						"FROM SustanciaQuimicaPeligrosaTransporte s where s.aprobacionRequisitosTecnicos =:aprobacionRequisitosTecnicos");
		query.setParameter("aprobacionRequisitosTecnicos", aprobacionRequisitosTecnicos);
		list = query.getResultList();
		return list;
	}

}
