/*
 * Copyright 2015 MAGMASOFT
 * Todos los derechos reservados
 */
package ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;

import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.service.EliminacionDesechoService;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.AprobacionRequisitosTecnicos;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.EliminacionDesecho;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.EliminacionRecepcion;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.RecepcionDesechoPeligroso;
import ec.gob.ambiente.suia.dto.EntityRecepcionDesecho;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.utils.EjecutarSentenciasNativas;

/**
 * <b> Clase para los servicios de la eliminacion de desechos. </b>
 * 
 * @author Javier Lucero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: Javier Lucero $, $Date: 11/06/2015 $]
 *          </p>
 */
@Stateless
public class EliminacionDesechoFacade {

	private static final Logger LOG = Logger.getLogger(EliminacionDesechoFacade.class);

	@EJB
	private CrudServiceBean crudServiceBean;

	@EJB
	private EliminacionDesechoService eliminacionDesechoService;

	@EJB
	private EjecutarSentenciasNativas ejecutarSentenciasNativas;

	@EJB
	private ModalidadesFacade modalidadFacade;

	public EliminacionDesecho guardarEliminacionDesecho(EliminacionDesecho eliminacionDesecho) {
		return crudServiceBean.saveOrUpdate(eliminacionDesecho);
	}

	/**
	 * 
	 * <b> Metodo que obtiene la lista de los desechos recepcion segun el
	 * proyecto. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 30/06/2015]
	 * </p>
	 * 
	 * @param idAprobacionRequisitosTecnicos
	 *            : id de proyecto
	 * @return List<EntityRecepcionDesecho>: lista de entidad de recepcion
	 * @throws ServiceException
	 *             : Excepcion
	 */
	public List<EntityRecepcionDesecho> obtenerPorAprobacionRequisitosTecnicos(
			final Integer idAprobacionRequisitosTecnicos) throws ServiceException {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT r.rehw_id, d.wada_key, d.wada_description, c.geca_description");
		sql.append(" FROM suia_iii.receipt_hazardous_waste r, suia_iii.waste_dangerous d, general_catalogs c");
		sql.append(
				" WHERE r.rehw_status='TRUE' and r.wada_id = d.wada_id and c.geca_id = r.rehw_physical_state_id and r.apte_id =")
				.append(idAprobacionRequisitosTecnicos);
		return ejecutarSentenciasNativas.listarPorSentenciaSql(sql.toString(), EntityRecepcionDesecho.class, null);
	}
	
	public List<EntityRecepcionDesecho> obtenerDesechoByAprobacionRequisitosTecnicos(
			final Integer idAprobacionRequisitosTecnicos) throws ServiceException {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT r.rehw_id, d.wada_key, d.wada_description, c.geca_description, d.wada_id");
		sql.append(" FROM suia_iii.receipt_hazardous_waste r, suia_iii.waste_dangerous d, general_catalogs c");
		sql.append(
				" WHERE r.rehw_status='TRUE' and r.wada_id = d.wada_id and c.geca_id = r.rehw_physical_state_id and r.apte_id =")
				.append(idAprobacionRequisitosTecnicos);
		return ejecutarSentenciasNativas.listarPorSentenciaSql(sql.toString(), EntityRecepcionDesecho.class, null);
	}

	/**
	 * 
	 * <b> Metodo que obtiene la lista de eliminacion segun el proyecto. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 30/06/2015]
	 * </p>
	 * 
	 * @param idAprobacionRequisitosTecnicos
	 *            : id del proyecto
	 * @return List<EliminacionRecepcion>: lista de eliminacion o disposicion
	 *         final
	 * @throws ServiceException
	 *             : Excepcion
	 */
	public List<EliminacionRecepcion> listarEliminacionPorAprobacionRequistos(
			final Integer idAprobacionRequisitosTecnicos) throws ServiceException {
		Map<String, Object> parametros = new HashMap<String, Object>();
		parametros.put("idAprobacionRequisitosTecnicos", idAprobacionRequisitosTecnicos);
		List<EliminacionRecepcion> eliminaciones = (List<EliminacionRecepcion>) crudServiceBean.findByNamedQuery(
				EliminacionRecepcion.LISTAR_POR_ID_APROBACION, parametros);
		for (EliminacionRecepcion eli : eliminaciones) {
			eli.setEliminacionDesechos(listarEliminacionRecepcionPorId(eli.getId()));
		}
		return eliminaciones;
	}

	/**
	 * 
	 * <b> Metodo que lista de los desechos segun la disposicion final. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 30/06/2015]
	 * </p>
	 * 
	 * @param idEliminacionRecepcion
	 *            : id de la recepcion
	 * @return List<EliminacionDesecho>: lista de desechos
	 * @throws ServiceException
	 *             : Excepcion
	 */
	public List<EliminacionDesecho> listarEliminacionRecepcionPorId(final Integer idEliminacionRecepcion)
			throws ServiceException {
		Map<String, Object> parametros = new HashMap<String, Object>();
		parametros.put("idEliminacionRecepcion", idEliminacionRecepcion);
		List<EliminacionDesecho> eliminacionesDesecho = (List<EliminacionDesecho>) crudServiceBean.findByNamedQuery(
				EliminacionDesecho.LISTAR_POR_ID_ELIMINACION, parametros);
		return eliminacionesDesecho;
	}

	/**
	 * 
	 * <b> Metodo que obtiene el desecho segun su id. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 30/06/2015]
	 * </p>
	 * 
	 * @param idEliminacionDesecho
	 *            : id de la disposicion final
	 * @return DesechoPeligroso: desecho
	 * @throws ServiceException
	 *             : Excepcion
	 */
	public DesechoPeligroso obtenerDesecho(final Integer idEliminacionDesecho) throws ServiceException {
		DesechoPeligroso desechos = new DesechoPeligroso();
		try {
			desechos = (DesechoPeligroso) crudServiceBean.getEntityManager()
					.createQuery("From DesechoPeligroso d where d.id = :id ").setParameter("id", idEliminacionDesecho)
					.getSingleResult();
		} catch (Exception e) {
			LOG.error("Error al obtener el desecho.", e);
		}
		return desechos;

	}

	/**
	 * 
	 * <b> Metodo que guard la disposicion final. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 30/06/2015]
	 * </p>
	 * 
	 * @param listaEliminacionRecepcion
	 *            : lista de las disposiciones
	 * @param listaEliminacionRecepcionEliminar
	 *            : lista para la eliminacion de las eliminaciones
	 * @param listaEliminacionDesechoEliminar
	 *            : lista para la eliminacion de los desechos
	 * @throws ServiceException
	 *             : Excepcion
	 */
	public void guardarDisposicion(List<EliminacionRecepcion> listaEliminacionRecepcion,
			List<EliminacionRecepcion> listaEliminacionRecepcionEliminar,
			List<EliminacionDesecho> listaEliminacionDesechoEliminar,
			AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos) throws ServiceException {

		for (EliminacionRecepcion eli : listaEliminacionRecepcion) {
			eli.setRecepcion(new RecepcionDesechoPeligroso(eli.getEntityRecepcionDesecho().getIdRecepcion()));
			List<EliminacionDesecho> listaElimacionDesecho = eli.getEliminacionDesechos();
			EliminacionRecepcion eliPersist = crudServiceBean.saveOrUpdate(eli);
			for (EliminacionDesecho eliDesecho : listaElimacionDesecho) {
				eliDesecho.setEliminacionRecepcion(eliPersist);
				crudServiceBean.saveOrUpdate(eliDesecho);
			}
		}

		crudServiceBean.delete(listaEliminacionRecepcionEliminar);
		crudServiceBean.delete(listaEliminacionDesechoEliminar);
		modalidadFacade.eliminarDesechoModalidadAsociada(listaEliminacionDesechoEliminar, aprobacionRequisitosTecnicos);
		agregarNuevosDesechosModalidad(aprobacionRequisitosTecnicos);
	}

	private void agregarNuevosDesechosModalidad(AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos)
			throws ServiceException {
		modalidadFacade.guardarNuevosDesechosPorModalidad(aprobacionRequisitosTecnicos);
	}

	public boolean validarDesechoSinGenerador(final Integer idAprobacionRequisitosTecnicos) throws ServiceException {
		boolean existe = false;
		try {
			int totalDesechos = eliminacionDesechoService
					.getTotalDesechoSinGeneradorNativeQuery(idAprobacionRequisitosTecnicos);
			if (totalDesechos > 0) {
				existe = true;
			}
		} catch (Exception e) {
			throw new ServiceException(e);
		}
		return existe;

	}

	public boolean validarDesechoSinGeneradorPorProyecto(final String codigoProyecto, final String codigoSolicitud) throws ServiceException {
		boolean existe = false;
		try {
			int totalDesechos = eliminacionDesechoService
					.getTotalDesechoSinGeneradorPorProyectoNativeQuery(codigoProyecto, codigoSolicitud);
			if (totalDesechos > 0) {
				existe = true;
			}
		} catch (Exception e) {
			throw new ServiceException(e);
		}
		return existe;

	}

	public boolean proyectoTieneGeneradorEnCurso(final Integer idProyecto) throws ServiceException {
		int totalGeneadores = 0;
		try {
			totalGeneadores = eliminacionDesechoService
					.getTotalGeneradoresEnCursoPorProyectoNativeQuery(idProyecto);

		} catch (Exception e) {
			throw new ServiceException(e);
		}
		return totalGeneadores > 0;
	}

	public boolean proyectoTieneGeneradorEnCurso(final String codSolicitud) throws ServiceException {
		int totalGeneadores = 0;
		try {
			totalGeneadores = eliminacionDesechoService
					.getTotalGeneradoresEnCursoPorCodSolicitudNativeQuery(codSolicitud);

		} catch (Exception e) {
			throw new ServiceException(e);
		}
		return totalGeneadores > 0;
	}

	public boolean validarEliminacionRecepcionDesechos(final Integer idAprobacionRequisitosTecnicos)
			throws ServiceException {
		boolean existe = false;
		try {
			int totalDesechos = eliminacionDesechoService
					.getTotalDesechosEliminacionNativeQuery(idAprobacionRequisitosTecnicos);
			int totalAlmacenRecepcion = eliminacionDesechoService
					.getTotalEliminacionRecepcionNativeQuery(idAprobacionRequisitosTecnicos);
			if (totalDesechos == totalAlmacenRecepcion) {
				existe = true;
			}
		} catch (Exception e) {
			throw new ServiceException(e);
		}
		return existe;

	}

	public void eliminarRecepcionPorIdRecepcion(final Integer idRecepcionDesechoPeligroso,
			AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos) throws ServiceException {
		Map<String, Object> parametros = new HashMap<String, Object>();
		parametros.put("idRecepcionDesechoPeligroso", idRecepcionDesechoPeligroso);
		List<EliminacionRecepcion> eliminacionRecepcion = (List<EliminacionRecepcion>) crudServiceBean
				.findByNamedQuery(EliminacionRecepcion.LISTAR_POR_ID_RECEPCION, parametros);

		if (eliminacionRecepcion != null && !eliminacionRecepcion.isEmpty()) {
			for (EliminacionRecepcion eliminacionRecepcion1 : eliminacionRecepcion) {
				List<EliminacionDesecho> eliminacionDesechos = listarEliminacionRecepcionPorId(eliminacionRecepcion1
						.getId());
				if (eliminacionDesechos != null && !eliminacionDesechos.isEmpty()) {
					modalidadFacade.eliminarDesechoModalidadAsociada(eliminacionDesechos, aprobacionRequisitosTecnicos);
				}
				crudServiceBean.delete(eliminacionDesechos);

			}
			crudServiceBean.delete(eliminacionRecepcion);

		}
	}

	public List<EliminacionRecepcion> listaEliminacionRecepcion(final Integer idRecepcionDesechoPeligroso)
			throws ServiceException {
		Map<String, Object> parametros = new HashMap<String, Object>();
		parametros.put("idRecepcionDesechoPeligroso", idRecepcionDesechoPeligroso);
		List<EliminacionRecepcion> eliminacionRecepcion = (List<EliminacionRecepcion>) crudServiceBean
				.findByNamedQuery(EliminacionRecepcion.LISTAR_POR_ID_RECEPCION, parametros);
		return eliminacionRecepcion;
	}

}
