/*
 * Copyright 2015 MAGMASOFT
 * Todos los derechos reservados
 */
package ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.service;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.AprobacionRequisitosTecnicos;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.DesechoPeligrosoTransporte;
import ec.gob.ambiente.suia.exceptions.ServiceException;

/**
 * <b> Clase para los servicios de la transportacion de los desechos peligrosos.
 * </b>
 * 
 * @author Javier Lucero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: Javier Lucero $, $Date: 22/06/2015 $]
 *          </p>
 */
@Stateless
public class DesechoPeligrosoTransporteService {

	private static final Logger LOG = Logger.getLogger(DesechoPeligrosoTransporteService.class);

	@EJB
	private CrudServiceBean crudServiceBean;

	/**
	 * 
	 * <b> Metodo para obtener todos los desechos peligrosos por el proyecto..
	 * </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 23/06/2015]
	 * </p>
	 * 
	 * @param aprobacionRequisitosTecnicos
	 *            : proyecto
	 * @return List<DesechoPeligrosoTransporte>: lista de los desechos
	 * @throws ServiceException
	 *             : Excepcion
	 */
	public List<DesechoPeligrosoTransporte> getListaDesechoPeligrosoTransporteUbicacion(
			AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos) throws ServiceException {

		Query query = crudServiceBean
				.getEntityManager()
				.createQuery(
						"FROM DesechoPeligrosoTransporte s where s.aprobacionRequisitosTecnicos =:aprobacionRequisitosTecnicos");
		query.setParameter("aprobacionRequisitosTecnicos", aprobacionRequisitosTecnicos);

		return (List<DesechoPeligrosoTransporte>) query.getResultList();
	}

}
