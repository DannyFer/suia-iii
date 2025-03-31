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
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.ModalidadDisposicionFinal;
import ec.gob.ambiente.suia.exceptions.ServiceException;

/**
 * <b> Clase que contiene los servicios para modalidad disposición final. </b>
 * 
 * @author Jonathan Guerrero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: Jonathan Guerrero $, $Date: 22/06/2015 $]
 *          </p>
 */
@Stateless
public class ModalidadDisposicionFinalService {
	@EJB
	private CrudServiceBean crudServiceBean;

	@SuppressWarnings("unchecked")
	public ModalidadDisposicionFinal getModalidadDisposicionFinal(
			AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos) throws ServiceException {
		List<ModalidadDisposicionFinal> lista = null;
		try {
			lista = crudServiceBean
					.getEntityManager()
					.createQuery(
							"From ModalidadDisposicionFinal m where m.aprobacionRequisitosTecnicos=:aprobacion AND m.estado = true")
					.setParameter("aprobacion", aprobacionRequisitosTecnicos).getResultList();
			if (lista == null || lista.isEmpty()) {
				return null;
			} else {
				return lista.get(0);
			}
		} catch (Exception e) {
			throw new ServiceException("Ocurrió un problema al recuperar los datos");
		}
	}
}
