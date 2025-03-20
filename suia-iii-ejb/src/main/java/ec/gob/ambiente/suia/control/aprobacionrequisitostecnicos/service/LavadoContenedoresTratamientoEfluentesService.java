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
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.LavadoContenedoresYTratamientoEfluentes;

@Stateless
public class LavadoContenedoresTratamientoEfluentesService {

	@EJB
	private CrudServiceBean crudServiceBean;

	/**
	 * 
	 * <b> Método para recuperar los datos de
	 * LavadoContenedoresYTratamientoEfluentes. </b>
	 * <p>
	 * [Author: vero, Date: 08/06/2015]
	 * </p>
	 * 
	 * @return
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public List<LavadoContenedoresYTratamientoEfluentes> getListaLavadoContenedoresYTratamientoEfluentes(
			AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos) {
		List<LavadoContenedoresYTratamientoEfluentes> listaLavadoContenedores = new ArrayList<LavadoContenedoresYTratamientoEfluentes>();
		// try {
		listaLavadoContenedores = (List<LavadoContenedoresYTratamientoEfluentes>) crudServiceBean
				.getEntityManager()
				.createQuery(
						"From LavadoContenedoresYTratamientoEfluentes l where l.aprobacionRequisitosTecnicos=:aprobacionRequisitosTecnicos")
				.setParameter("aprobacionRequisitosTecnicos", aprobacionRequisitosTecnicos).getResultList();
		if (listaLavadoContenedores == null || listaLavadoContenedores.isEmpty()) {
			return null;
		} else {
			return listaLavadoContenedores;
		}
		// } catch (Exception e) {
		// throw new
		// ServiceException("Ocurrió un problema al recuperar los datos");
		// }

	}
}
