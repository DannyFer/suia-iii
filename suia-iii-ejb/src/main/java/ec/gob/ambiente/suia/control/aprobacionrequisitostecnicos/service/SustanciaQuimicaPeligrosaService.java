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
import ec.gob.ambiente.suia.domain.ActividadesPorEtapa;
import ec.gob.ambiente.suia.domain.SustanciaQuimicaPeligrosa;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.AprobacionRequisitosTecnicos;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.SustanciaQuimicaPeligrosaTransporte;
import ec.gob.ambiente.suia.exceptions.ServiceException;

/**
 * <b> Clase para los servicios de las sustancias químicas peligrosas. </b>
 * 
 * @author Jonathan Guerrero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: Jonathan Guerrero $, $Date: 05/06/2015 $]
 *          </p>
 */
@Stateless
public class SustanciaQuimicaPeligrosaService {

	private static final Logger LOG = Logger.getLogger(SustanciaQuimicaPeligrosaService.class);

	@EJB
	private CrudServiceBean crudServiceBean;

	/**
	 * 
	 * <b> Método que obtiene la lista de todas las sustancias químicas
	 * peligrosas. </b>
	 * <p>
	 * [Author: Jonathan Guerrero, Date: 05/06/2015]
	 * </p>
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<SustanciaQuimicaPeligrosa> getListaSustanciasQuimicasPeligrosas(String filtro) {
		List<SustanciaQuimicaPeligrosa> sustanciaQuimicaPeligrosas = new ArrayList<SustanciaQuimicaPeligrosa>();
		try {
			Query query = null;
			if (filtro != null && !filtro.isEmpty()) {
				filtro = filtro.toLowerCase();
				query = crudServiceBean
						.getEntityManager()
						.createQuery(
								"FROM SustanciaQuimicaPeligrosa sqp WHERE (lower(sqp.descripcion) like :filtro) AND sqp.estado = true");
				query.setParameter("filtro", "%" + filtro + "%");
			} else {
				query = crudServiceBean.getEntityManager().createQuery(
						"From SustanciaQuimicaPeligrosa sqp where sqp.estado = true");
			}
			sustanciaQuimicaPeligrosas = query.getResultList();
			if (sustanciaQuimicaPeligrosas == null || sustanciaQuimicaPeligrosas.isEmpty()) {
				return null;
			} else {
				return sustanciaQuimicaPeligrosas;
			}
		} catch (Exception e) {
			LOG.error("Error al obtener las sustancias químicas peligrosas.", e);
		}
		return sustanciaQuimicaPeligrosas;
	}

	public List<ActividadesPorEtapa> getListaActividadPorEtapaID(int filtro) {
		List<ActividadesPorEtapa> actividadesPorEtapas = new ArrayList<ActividadesPorEtapa>();
		try {
			Query query = null;
				query = crudServiceBean
						.getEntityManager()
						.createQuery(
								"FROM ActividadesPorEtapa ape WHERE ape.etapasProyecto.id = :filtro");
				query.setParameter("filtro", filtro);

			actividadesPorEtapas = query.getResultList();

			if (actividadesPorEtapas == null || actividadesPorEtapas.isEmpty()) {
				return null;
			} else {
				return actividadesPorEtapas;

			}
		} catch (Exception e) {
			LOG.error("Error al obtener las actividades por etapa", e);
		}
		return actividadesPorEtapas;
	}


	public List<SustanciaQuimicaPeligrosa> buscarSustanciasQuimicasPadres(String filtro) {
		List<SustanciaQuimicaPeligrosa> sustancias = new ArrayList<SustanciaQuimicaPeligrosa>();
		if (filtro != null && !filtro.isEmpty()) {
			filtro = filtro.toLowerCase();
			Query query = crudServiceBean
					.getEntityManager()
					.createQuery(
							"FROM SustanciaQuimicaPeligrosa c WHERE lower(c.descripcion) like :filtro  ORDER BY c.id");
			query.setParameter("filtro", "%" + filtro + "%");
			sustancias = query.getResultList();
		} else {
			Query query = crudServiceBean.getEntityManager().createQuery(
					"FROM SustanciaQuimicaPeligrosa c WHERE c.sustanciaQuimicaPeligrosa = null ORDER BY c.id");
			sustancias = query.getResultList();
		}
		initIsSustanciasQuimicaPeligrosoFinal(sustancias);
		return sustancias;
	}

	public List<SustanciaQuimicaPeligrosaTransporte> getListaSustanciaQuimicaPeligrosaTransporteUbicacion(
			AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos) throws ServiceException {
		List<SustanciaQuimicaPeligrosaTransporte> list = new ArrayList<SustanciaQuimicaPeligrosaTransporte>();
		Query query = crudServiceBean
				.getEntityManager()
				.createQuery(
						"FROM SustanciaQuimicaPeligrosaTransporte s.aprobacionRequisitosTecnicos =:aprobacionRequisitosTecnicos");
		query.setParameter("aprobacionRequisitosTecnicos" , aprobacionRequisitosTecnicos );
		list = query.getResultList();
		return list;
	}

	public List<SustanciaQuimicaPeligrosa> buscarSustanciaQuimicaPorPadre(SustanciaQuimicaPeligrosa padre) {
		List<SustanciaQuimicaPeligrosa> sustancia = (List<SustanciaQuimicaPeligrosa>) crudServiceBean
				.getEntityManager()
				.createQuery("From SustanciaQuimicaPeligrosa c where c.sustanciaQuimicaPeligrosa =:padre order by c.id")
				.setParameter("padre", padre).getResultList();
		initIsSustanciasQuimicaPeligrosoFinal(sustancia);
		return sustancia;
	}

	private void initIsSustanciasQuimicaPeligrosoFinal(List<SustanciaQuimicaPeligrosa> sustancias) {
		if (sustancias != null)
			for (SustanciaQuimicaPeligrosa sustanciaQuimicaPeligrosa : sustancias)
				sustanciaQuimicaPeligrosa.isSustanciaFinal();
	}

	public SustanciaQuimicaPeligrosa guardarSustanciaQuimicaPeligrosa(
			SustanciaQuimicaPeligrosa sustanciaQuimicaPeligrosa) {
		return crudServiceBean.saveOrUpdate(sustanciaQuimicaPeligrosa);
	}
	
	public SustanciaQuimicaPeligrosa buscarSustanciaQuimicaPorId(Integer id) {
		try {
			SustanciaQuimicaPeligrosa sustancia = (SustanciaQuimicaPeligrosa) crudServiceBean
					.getEntityManager()
					.createQuery("From SustanciaQuimicaPeligrosa c where c.id =:id order by c.id")
					.setParameter("id", id).getSingleResult();
			return sustancia;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}		
	}
}
