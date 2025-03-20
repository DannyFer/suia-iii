/*
 * Copyright 2015 MAGMASOFT
 * Todos los derechos reservados
 */
package ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;


import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.AprobacionRequisitosTecnicos;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.ModalidadIncineracion;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.ModalidadIncineracionDesecho;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.ModalidadIncineracionDesechoProcesar;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.ModalidadIncineracionFormulacion;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.ProgramaCalendarizadoIncineracion;
import ec.gob.ambiente.suia.exceptions.ServiceException;

/**
 * <b> Clase que contiene los servicios para modalidad incineración. </b>
 * 
 * @author vero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: vero $, $Date: 19/06/2015 $]
 *          </p>
 */
@Stateless
public class ModalidadIncineracionService {

	@EJB
	private CrudServiceBean crudServiceBean;

	@SuppressWarnings("unchecked")
	public ModalidadIncineracion getModalidadTratamiento(AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos)
			throws ServiceException {
		List<ModalidadIncineracion> lista = null;
		try {
			lista = crudServiceBean
					.getEntityManager()
					.createQuery(
							"From ModalidadIncineracion m where m.aprobacionRequisitosTecnicos.id=:aprobacion and m.estado = true")
					.setParameter("aprobacion", aprobacionRequisitosTecnicos.getId()).getResultList();
			if (lista == null || lista.isEmpty()) {
				return null;
			} else {
				for (ModalidadIncineracionDesecho desecho : lista.get(0).getModalidadIncineracionDesechos()) {
					desecho.setDesecho(buscarDesechoPeligrososPorId(desecho.getIdDesecho()));
				}

				lista.get(0).getModalidadIncineracionDesechoProcesados().size();
				for (ModalidadIncineracionDesechoProcesar procesar : lista.get(0)
						.getModalidadIncineracionDesechoProcesados()) {
					procesar.setDesecho(buscarDesechoPeligrososPorId(procesar.getIdDesecho()));
				}

				lista.get(0).setModalidadIncineracionFormulaciones(
						getModalidadIncineracionFormulaciones(lista.get(0).getId()));

				return lista.get(0);
			}
		} catch (Exception e) {
			throw new ServiceException("Ocurrió un problema al recuperar los datos");
		}

	}

	public List<ModalidadIncineracionFormulacion> getModalidadIncineracionFormulaciones(final Integer idModalidad) {
		Map<String, Object> parametros = new HashMap<String, Object>();
		parametros.put("idModalidad", idModalidad);
		List<ModalidadIncineracionFormulacion> formulacion = (List<ModalidadIncineracionFormulacion>) crudServiceBean
				.findByNamedQuery(ModalidadIncineracionFormulacion.LISTAR_POR_ID, parametros);
		return formulacion;
	}

	public DesechoPeligroso buscarDesechoPeligrososPorId(Integer id) {
		return crudServiceBean.find(DesechoPeligroso.class, id);
	}

	@SuppressWarnings("unchecked")
	public List<ProgramaCalendarizadoIncineracion> getCalendarioActividades(ModalidadIncineracion modalidad)
			throws ServiceException {
		List<ProgramaCalendarizadoIncineracion> lista = null;
		try {
			lista = crudServiceBean.getEntityManager()
					.createQuery("From ProgramaCalendarizadoIncineracion m where m.modalidadIncineracion=:modalidad")
					.setParameter("modalidad", modalidad).getResultList();

			return lista;

		} catch (Exception e) {
			throw new ServiceException("Ocurrió un problema al recuperar los datos");
		}

	}
}
