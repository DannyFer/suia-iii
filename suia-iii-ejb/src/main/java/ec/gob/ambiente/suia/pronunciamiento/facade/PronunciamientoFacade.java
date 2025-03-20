/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.pronunciamiento.facade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Pronunciamiento;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import ec.gob.ambiente.suia.domain.enums.EstadoPronunciamiento;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author mayriliscs
 * @version Revision: 1.0
 *          <p>
 *          [Autor: mayriliscs, Fecha: 12/01/2015]
 *          </p>
 */
@Stateless
public class PronunciamientoFacade {

	private static final Logger LOG = Logger.getLogger(PronunciamientoFacade.class);
	@EJB
	private CrudServiceBean crudServiceBean;
	@EJB
	private ProcesoFacade procesoFacade;

	public Pronunciamiento getPronunciamiento(int id) {
		return crudServiceBean.find(Pronunciamiento.class, id);
	}

	public void saveOrUpdate(EntidadBase objeto) {
		crudServiceBean.saveOrUpdate(objeto);
	}

	public void modificarRegistro(EntidadBase objeto) {
		crudServiceBean.saveOrUpdate(objeto);
	}

	public List<Pronunciamiento> getPronunciamientos(List<Integer> identificadores) {
		List<Pronunciamiento> resultado = new ArrayList<Pronunciamiento>();
		for (int i = 0; i < identificadores.size(); i++) {
			Pronunciamiento pronunciamiento = getPronunciamiento(identificadores.get(i));
			if (pronunciamiento != null)
				resultado.add(pronunciamiento);
		}
		return resultado;
	}

	@SuppressWarnings("unchecked")
	public Pronunciamiento getPronunciamientosPorUsuarioYProceso(List<Integer> identificadores, int idUsuario) {

		Map<String, Object> parameters = new ConcurrentHashMap<String, Object>();
		parameters.put("idUsuario", idUsuario);
		parameters.put("identificadores", identificadores);

		List<Pronunciamiento> result = (List<Pronunciamiento>) crudServiceBean.findByNamedQuery(
				Pronunciamiento.FIND_BY_USER_AND_PROCESS, parameters);
		if (result != null && !result.isEmpty()) {
			return result.get(0);
		}
		return null;
	}

	public void solicitarPronunciamientos(List<Usuario> usuarios, long idInstanciaProceso, Usuario usuario) {
		for (int i = 0; i < usuarios.size(); i++) {
			Pronunciamiento pronunciamiento = new Pronunciamiento();
			pronunciamiento.setUsuario(usuarios.get(i));
			pronunciamiento.setEstadoPronunciamiento(EstadoPronunciamiento.PENDIENTE);
			crudServiceBean.saveOrUpdate(pronunciamiento);
			adicionarPronunciamientoVariableProceso(pronunciamiento, idInstanciaProceso, usuario);
		}
	}

	public void adicionarPronunciamientoVariableProceso(Pronunciamiento pronunciamiento, long idInstanciaProceso,
			Usuario usuario) {
		String identificadoresPronunciamientos = "";
		if (pronunciamiento.getId() != 0) {
			identificadoresPronunciamientos = obtenerIdsPronunciamientosSolicitados(idInstanciaProceso, usuario)
					+ pronunciamiento.getId() + Constantes.SEPARADOR;

			Map<String, Object> params = new HashMap<String, Object>();
			params.put("identificadoresPronunciamientos", identificadoresPronunciamientos);

			try {
				procesoFacade.modificarVariablesProceso(usuario, idInstanciaProceso, params);
			} catch (JbpmException e) {
				LOG.error("Error al iniciar el proceso", e);
			}
		}
	}

	public String obtenerIdsPronunciamientosSolicitados(long idInstanciaProceso, Usuario usuario) {
		String idPronunciamientos = "";
		try {
			if (idInstanciaProceso != 0) {
				Map<String, Object> variablesProceso = procesoFacade.recuperarVariablesProceso(usuario,
						idInstanciaProceso);
				idPronunciamientos = (String) variablesProceso.get("identificadoresPronunciamientos");
			}
		} catch (JbpmException e) {
			LOG.error("Error al iniciar inicializar el bean", e);
		}
		return idPronunciamientos == null ? "" : idPronunciamientos;
	}

	public List<Integer> obtenerIdsIntegerPronunciamientosSolicitados(long idInstanciaProceso, Usuario usuario) {
		String idPronunciamientos = "";
		List<Integer> identificadores = new ArrayList<Integer>();
		try {
			if (idInstanciaProceso != 0) {
				Map<String, Object> variablesProceso = procesoFacade.recuperarVariablesProceso(usuario,
						idInstanciaProceso);
				idPronunciamientos = (String) variablesProceso.get("identificadoresPronunciamientos");
				String[] arreglo;
				if (idPronunciamientos != null && !idPronunciamientos.isEmpty()) {
					arreglo = idPronunciamientos.split(Constantes.SEPARADOR);

					for (int i = 0; i < arreglo.length; i++) {
						identificadores.add(Integer.parseInt(arreglo[i]));
					}
				}
			}
		} catch (JbpmException e) {
			LOG.error("Error al iniciar inicializar el bean", e);
		}
		return identificadores;
	}

	// ----------

	@SuppressWarnings("unchecked")
	public Pronunciamiento getPronunciamientosPorUsuarioClaseTipo(int idUsuario, String nombreClase, Integer idClase,
			String tipo) {

		Map<String, Object> parameters = new ConcurrentHashMap<String, Object>();
		parameters.put("nombreClase", nombreClase);
		parameters.put("idUsuario", idUsuario);
		parameters.put("idClase", idClase);
		parameters.put("tipo", tipo);

		List<Pronunciamiento> result = (List<Pronunciamiento>) crudServiceBean.findByNamedQuery(
				Pronunciamiento.FIND_BY_USER_TYPE_CLASS, parameters);
		if (result != null && !result.isEmpty()) {
			return result.get(0);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public Pronunciamiento getPronunciamientosPorClaseTipo(String nombreClase, long idClase, String tipo) {

		Map<String, Object> parameters = new ConcurrentHashMap<String, Object>();
		parameters.put("nombreClase", nombreClase);
		parameters.put("idClase", idClase);
		parameters.put("tipo", tipo);

		List<Pronunciamiento> result = (List<Pronunciamiento>) crudServiceBean.findByNamedQuery(
				Pronunciamiento.FIND_BY_TYPE_CLASS, parameters);
		if (result != null && !result.isEmpty()) {
			return result.get(0);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<Pronunciamiento> getPronunciamientosPorClase(String nombreClase, long idClase) {

		Map<String, Object> parameters = new ConcurrentHashMap<String, Object>();
		parameters.put("nombreClase", nombreClase);
		parameters.put("idClase", idClase);

		return (List<Pronunciamiento>) crudServiceBean.findByNamedQuery(Pronunciamiento.FIND_BY_CLASS, parameters);

	}
}
