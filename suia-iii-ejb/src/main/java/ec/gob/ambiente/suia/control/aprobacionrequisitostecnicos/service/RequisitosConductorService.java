/*
 * Copyright 2015 MAGMASOFT
 * Todos los derechos reservados
 */
package ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.service;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.AprobacionRequisitosTecnicos;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.RequisitosConductor;

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
public class RequisitosConductorService {

	private static final Logger LOG = Logger.getLogger(RequisitosConductorService.class);

	@EJB
	private CrudServiceBean crudServiceBean;

	/**
	 * 
	 * <b> Metodo que obtiene la lista de todos los conductores. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 05/06/2015]
	 * </p>
	 * 
	 * @return List<RequisitosConductor> : Lista de los conductores
	 */
	public List<RequisitosConductor> getListaConductores(AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos) {
		List<RequisitosConductor> conductores = new ArrayList<RequisitosConductor>();
		try {
			conductores = (List<RequisitosConductor>) crudServiceBean.getEntityManager()
					.createQuery("From RequisitosConductor r where r.estado=true and r.aprobacionRequisitosTecnicos=:aprobacion ")
					.setParameter("aprobacion", aprobacionRequisitosTecnicos).getResultList();
			if (conductores == null || conductores.isEmpty()) {
				return null;
			} else {
				return conductores;
			}
		} catch (Exception e) {
			LOG.error("Error al obtener los requisitos del conductor.", e);
		}
		return conductores;

	}
}
