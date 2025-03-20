/*
 * Copyright 2015 MAGMASOFT
 * Todos los derechos reservados
 */
package ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.service;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.AprobacionRequisitosTecnicos;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.RecepcionDesechoPeligroso;
import ec.gob.ambiente.suia.exceptions.ServiceException;

/**
 * <b> Clase que contiene los servicios de la pagina recepción de desechos. </b>
 * 
 * @author vero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: vero $, $Date: 09/06/2015 $]
 *          </p>
 */
@Stateless
public class RecepcionDesechoPeligrosoService {

	@EJB
	private CrudServiceBean crudServiceBean;

	public List<RecepcionDesechoPeligroso> getListaRecepcionDesechosPeligrosos(
			AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos) throws ServiceException {
		List<RecepcionDesechoPeligroso> listaRecepcionDesechos = new ArrayList<RecepcionDesechoPeligroso>();
		try {
			listaRecepcionDesechos = (List<RecepcionDesechoPeligroso>) crudServiceBean.getEntityManager()
					.createQuery("From RecepcionDesechoPeligroso r where r.aprobacionRequisitosTecnicos=:aprobacion")
					.setParameter("aprobacion", aprobacionRequisitosTecnicos).getResultList();
			if (listaRecepcionDesechos == null || listaRecepcionDesechos.isEmpty()) {
				return null;
			} else {
				return listaRecepcionDesechos;
			}
		} catch (Exception e) {
			throw new ServiceException("Ocurrió un problema al recuperar los datos", e);
		}

	}
}
