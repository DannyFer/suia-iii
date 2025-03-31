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
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.ModalidadTratamiento;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.ProgramaCalendarizadoTratamiento;
import ec.gob.ambiente.suia.exceptions.ServiceException;

/**
 * <b> Clase que contiene los servicios para modalidad tratamiento. </b>
 * 
 * @author vero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: vero $, $Date: 10/06/2015 $]
 *          </p>
 */
@Stateless
public class ModalidadTratamientoService {
	@EJB
	private CrudServiceBean crudServiceBean;

	@SuppressWarnings("unchecked")
	public ModalidadTratamiento getModalidadTratamiento(AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos)
			throws ServiceException {
		List<ModalidadTratamiento> lista = null;
		try {
			lista = crudServiceBean.getEntityManager()
					.createQuery("From ModalidadTratamiento m where m.aprobacionRequisitosTecnicos=:aprobacion")
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

	@SuppressWarnings("unchecked")
	public List<ProgramaCalendarizadoTratamiento> getCalendarioActividades(ModalidadTratamiento modalidad)
			throws ServiceException {
		List<ProgramaCalendarizadoTratamiento> lista = null;
		try {
			lista = crudServiceBean
					.getEntityManager()
					.createQuery(
							"From ProgramaCalendarizadoTratamiento p where p.modalidadTratamiento=:modalidad")
					.setParameter("modalidad", modalidad).getResultList();

			return lista;

		} catch (Exception e) {
			throw new ServiceException("Ocurrió un problema al recuperar los datos");
		}

	}
}
