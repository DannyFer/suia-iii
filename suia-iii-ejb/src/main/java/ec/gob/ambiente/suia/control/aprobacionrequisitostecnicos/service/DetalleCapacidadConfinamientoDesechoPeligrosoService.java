/*
 * Copyright 2015 MAGMASOFT
 * Todos los derechos reservados
 */
package ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.service;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.DetalleCapacidadConfinamientoDesechoPeligroso;
import ec.gob.ambiente.suia.exceptions.ServiceException;

/**
 * <b> Clase que contiene los servicios para detalle capacidad confinamiento desecho peligroso. </b>
 * 
 * @author Jonathan Guerrero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: Jonathan Guerrero $, $Date: 22/06/2015 $]
 *          </p>
 */
@Stateless
public class DetalleCapacidadConfinamientoDesechoPeligrosoService {
	@EJB
	private CrudServiceBean crudServiceBean;

	@SuppressWarnings("unchecked")
	public List<DetalleCapacidadConfinamientoDesechoPeligroso> getDetalleCapacidadConfinamientoDesechoPeligrosos(
			Integer idModalidadDispoFinal) throws ServiceException {
		List<DetalleCapacidadConfinamientoDesechoPeligroso> lista = null;
		try {
			lista = crudServiceBean
					.getEntityManager()
					.createQuery(
							"From DetalleCapacidadConfinamientoDesechoPeligroso d where d.modalidadDisposicionFinal.id=:paramIdModDispFin AND d.estado = true")
					.setParameter("paramIdModDispFin", idModalidadDispoFinal).getResultList();
			if (lista == null || lista.isEmpty()) {
				return null;
			} else {
				return lista;
			}
		} catch (Exception e) {
			throw new ServiceException("Ocurrió un problema al recuperar los datos");
		}

	}

}
