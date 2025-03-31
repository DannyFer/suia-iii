/*
 * Copyright 2015 MAGMASOFT
 * Todos los derechos reservados
 */
package ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.service.DesechoPeligrosoTransporteService;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.AprobacionRequisitosTecnicos;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.DesechoEspecialRecoleccion;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.DesechoPeligrosoTransporte;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.DesechoPeligrosoTransporteUbicacionGeografica;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.dto.EntityRecepcionDesecho;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.utils.EjecutarSentenciasNativas;

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
public class DesechoPeligrosoTransporteFacade {

	@EJB
	private CrudServiceBean crudServiceBean;
	
	@EJB
	private EjecutarSentenciasNativas ejecutarSentenciasNativas;

	@EJB
	private DesechoPeligrosoTransporteService desechoPeligrosoTransporteService;

	@EJB
	private DesechoPeligrosoTransporteFacade desechoPeligrosoTransportacionFacade;

	@EJB
	private DocumentosFacade documentosFacade;

	/**
	 * 
	 * <b> Metodo para guardar los desechos para transportacion y los eliminados
	 * respectivamente. </b>
	 * <p>
	 * [Author: Javier Lucero , Date: 23/06/2015]
	 * </p>
	 * 
	 * @param listaPeligrosoTransportes
	 *            : lista de desechos para la transportar
	 * @param listaPeligrosoTransportesEliminar
	 *            : lista de desechos que se eliminan
	 * @param listaDesechosUbicacionGeograficaEliminar
	 *            : lista de las ubicaciones para la eliminacion
	 * @throws ServiceException
	 *             : Excepcion
	 */
	public void guardarDesechoPeligrosoTransportacion(List<DesechoPeligrosoTransporte> listaPeligrosoTransportes,
			List<DesechoPeligrosoTransporte> listaPeligrosoTransportesEliminar,
			List<DesechoPeligrosoTransporteUbicacionGeografica> listaDesechosUbicacionGeograficaEliminar,
			List<DesechoEspecialRecoleccion> desechoEspecialRecoleccionEliminar) throws ServiceException {

		boolean isNew = false;
		for (DesechoPeligrosoTransporte desPeligrosoTransporte : listaPeligrosoTransportes) {
			List<DesechoPeligrosoTransporteUbicacionGeografica> desechoPeligrosoTransporteUbicacionGeograficas = desPeligrosoTransporte
					.getDesechosUbicaciones();
			isNew = desPeligrosoTransporte.getId() == null ? true : false;
			DesechoPeligrosoTransporte desechoPersist = crudServiceBean.saveOrUpdate(desPeligrosoTransporte);
			if (desPeligrosoTransporte.getDesechoPeligroso().getClave().startsWith("Q.")) {

				DesechoEspecialRecoleccion desechoEspecialRecoleccion;
				if (isNew) {
					desechoEspecialRecoleccion = new DesechoEspecialRecoleccion();
					desechoEspecialRecoleccion.setDesechoPeligroso(desPeligrosoTransporte.getDesechoPeligroso());
					desechoEspecialRecoleccion.setAprobacionRequisitosTecnicos(desPeligrosoTransporte
							.getAprobacionRequisitosTecnicos());
					desechoPeligrosoTransportacionFacade.guardarRecoleccionDesechoEspecial(desechoEspecialRecoleccion);
				}
			}
			if (!desechoPersist.isOrigenNivelNacional()) {
				for (DesechoPeligrosoTransporteUbicacionGeografica desechoUbicacion : desechoPeligrosoTransporteUbicacionGeograficas) {
					desechoUbicacion.setDesechoPeligrosoTransporte(desechoPersist);
					crudServiceBean.saveOrUpdate(desechoUbicacion);
				}
			}

		}
		for (DesechoPeligrosoTransporte desechoPeligrosoTransporte : listaPeligrosoTransportesEliminar) {
			if (desechoPeligrosoTransporte.getDesechoPeligroso().getClave().startsWith("Q.")) {
				DesechoEspecialRecoleccion recoleccion = obtenerDesechoEspecialRecoleccionPorProyecto(
						desechoPeligrosoTransporte.getDesechoPeligroso().getId(), desechoPeligrosoTransporte
								.getAprobacionRequisitosTecnicos().getId());
				crudServiceBean.delete(recoleccion);
			}
		}
		crudServiceBean.delete(desechoEspecialRecoleccionEliminar);
		crudServiceBean.delete(listaPeligrosoTransportesEliminar);
		crudServiceBean.delete(listaDesechosUbicacionGeograficaEliminar);

	}

	/**
	 * 
	 * <b> Metodo para obtener todos los desechos peligrosos por el proyecto.
	 * </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 23/06/2015]
	 * </p>
	 * 
	 * @param idAprobacionRequisitosTecnicos
	 *            : proyecto
	 * @return List<DesechoPeligrosoTransporte>: lista de desechos
	 * @throws ServiceException
	 *             : Excepcion
	 */
	public List<DesechoPeligrosoTransporte> getListaListaDesechoPeligrosoTransporte(
			final Integer idAprobacionRequisitosTecnicos) throws ServiceException {
		Map<String, Object> parametros = new HashMap<String, Object>();
		parametros.put("idAprobacionRequisitosTecnicos", idAprobacionRequisitosTecnicos);
		List<DesechoPeligrosoTransporte> desechos = (List<DesechoPeligrosoTransporte>) crudServiceBean
				.findByNamedQuery(DesechoPeligrosoTransporte.LISTAR_POR_APROBACION_REQUISITOS_TECNICOS, parametros);
		for (DesechoPeligrosoTransporte des : desechos) {
			des.setDesechoPeligroso(buscarDesechoPeligrososPorId(des.getIdDesecho()));
			if (!des.isOrigenNivelNacional()) {
				des.setDesechosUbicaciones(listarDesechosUbicacionPorIdDesecho(des.getId()));
			} else {
				des.setDesechosUbicaciones(new ArrayList<DesechoPeligrosoTransporteUbicacionGeografica>());
			}

		}
		return desechos;
	}

	public void eliminarListaDesechoPeligrosoTransporteExistentes(
			AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos) {
		List<DesechoPeligrosoTransporte> desechos = null;
		try {
			desechos = getListaListaDesechoPeligrosoTransporte(aprobacionRequisitosTecnicos.getId());
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		if (desechos != null)
			eliminarListaDesechoPeligrosoTransporte(desechos);
	}

	public void eliminarListaDesechoPeligrosoTransporte(List<DesechoPeligrosoTransporte> desechos) {
		crudServiceBean.delete(desechos);
	}

	public DesechoPeligroso buscarDesechoPeligrososPorId(Integer id) {
		return crudServiceBean.find(DesechoPeligroso.class, id);
	}

	/**
	 * 
	 * <b> Metodo que obtiene todos las ubicacionee de los desechos. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 23/06/2015]
	 * </p>
	 * 
	 * @param idDesechoPeligrosoTransporte
	 *            : id del desecho a transportar
	 * @return List<DesechoPeligrosoTransporteUbicacionGeografica>: lista de las
	 *         ubicaciones
	 * @throws ServiceException
	 *             : Excepcion
	 */
	public List<DesechoPeligrosoTransporteUbicacionGeografica> listarDesechosUbicacionPorIdDesecho(
			final Integer idDesechoPeligrosoTransporte) throws ServiceException {
		Map<String, Object> parametros = new HashMap<String, Object>();
		parametros.put("idDesechoPeligrosoTransporte", idDesechoPeligrosoTransporte);
		List<DesechoPeligrosoTransporteUbicacionGeografica> desechosUbicacion = (List<DesechoPeligrosoTransporteUbicacionGeografica>) crudServiceBean
				.findByNamedQuery(DesechoPeligrosoTransporteUbicacionGeografica.LISTAR_POR_ID_DESECHO, parametros);
		return desechosUbicacion;
	}

	/**
	 * 
	 * <b> Metodo que guarda la lista de desechos especiales. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 09/07/2015]
	 * </p>
	 * 
	 * @param desechosEspeciales
	 *            : desechos especiales
	 * @return List<DesechoEspecialRecoleccion>: lista de desechos
	 * @throws ServiceException
	 *             : Excepcion
	 * @throws CmisAlfrescoException
	 *             : Excepcion
	 */
	public List<DesechoEspecialRecoleccion> guardarListaRecoleccionDesechoEspecial(
			List<DesechoEspecialRecoleccion> desechosEspeciales, long idProceso, long idTarea, boolean isProyectoExPost)
			throws ServiceException, CmisAlfrescoException {

		
			for (DesechoEspecialRecoleccion desechoEspecial : desechosEspeciales) {				
				if(isProyectoExPost){
				desechoEspecial.setDocumentoMecanica(documentosFacade.subirFileAlfrescoSinProyectoAsociado(
						desechoEspecial.getDocumentoMecanica(), desechoEspecial.getAprobacionRequisitosTecnicos()
								.getProyecto(), idProceso, idTarea, DesechoEspecialRecoleccion.class.getSimpleName(),
						AprobacionRequisitosTecnicosFacade.NOMBRE_CARPETA_DESECHOS_BIOLOGICOS,
						TipoDocumentoSistema.TIPO_DOCUMENTO_ANEXOS));
				desechoEspecial.setDocumentoEnfriamiento(documentosFacade.subirFileAlfrescoSinProyectoAsociado(
						desechoEspecial.getDocumentoEnfriamiento(), desechoEspecial.getAprobacionRequisitosTecnicos()
								.getProyecto(), idProceso, idTarea, DesechoEspecialRecoleccion.class.getSimpleName(),
						AprobacionRequisitosTecnicosFacade.NOMBRE_CARPETA_DESECHOS_BIOLOGICOS,
						TipoDocumentoSistema.TIPO_DOCUMENTO_ANEXOS));
				desechoEspecial.setDocumentoCaptacion(documentosFacade.subirFileAlfrescoSinProyectoAsociado(
						desechoEspecial.getDocumentoCaptacion(), desechoEspecial.getAprobacionRequisitosTecnicos()
								.getProyecto(), idProceso, idTarea, DesechoEspecialRecoleccion.class.getSimpleName(),
						AprobacionRequisitosTecnicosFacade.NOMBRE_CARPETA_DESECHOS_BIOLOGICOS,
						TipoDocumentoSistema.TIPO_DOCUMENTO_ANEXOS));
				}
				else{
					desechoEspecial.setDocumentoMecanica(null);
					desechoEspecial.setDocumentoEnfriamiento(null);
					desechoEspecial.setDocumentoCaptacion(null);
				}
			
		}
		return (List<DesechoEspecialRecoleccion>) crudServiceBean.saveOrUpdate(desechosEspeciales);
	}

	/**
	 * 
	 * <b> Metodo que guarda la recoleccion de desecho. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 09/07/2015]
	 * </p>
	 * 
	 * @param desechoEspecial
	 *            : desecho especial
	 * @return DesechoEspecialRecoleccion; Desecho especial
	 */
	public DesechoEspecialRecoleccion guardarRecoleccionDesechoEspecial(DesechoEspecialRecoleccion desechoEspecial) {
		return crudServiceBean.saveOrUpdate(desechoEspecial);
	}

	/**
	 * 
	 * <b> Metodo que obtiene la lista de los desechos segun el proyecto. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 09/07/2015]
	 * </p>
	 * 
	 * @param idAprobacionRequisitosTecnicos
	 *            : id del proyecto
	 * @return List<DesechoEspecialRecoleccion> : lista de desechos
	 * @throws ServiceException
	 *             : Excepcion
	 */
	public List<DesechoEspecialRecoleccion> listaDesechoEspecialRecoleccionPorProyecto(
			final Integer idAprobacionRequisitosTecnicos) throws ServiceException {
		Map<String, Object> parametros = new HashMap<String, Object>();
		parametros.put("idAprobacionRequisitosTecnicos", idAprobacionRequisitosTecnicos);
		List<DesechoEspecialRecoleccion> desechosRecoleccion = (List<DesechoEspecialRecoleccion>) crudServiceBean
				.findByNamedQuery(DesechoEspecialRecoleccion.LISTAR_POR_APROBACION_REQUISITOS_TECNICOS, parametros);
		if (desechosRecoleccion != null || !desechosRecoleccion.isEmpty()) {
			for (DesechoEspecialRecoleccion desechos : desechosRecoleccion) {
				desechos.getDesechoPeligroso();
			}
		}

		return desechosRecoleccion;

	}

	public void eliminarDesechoEspecialRecoleccionExistentes(AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos) {
		List<DesechoEspecialRecoleccion> desechos = null;
		try {
			desechos = listaDesechoEspecialRecoleccionPorProyecto(aprobacionRequisitosTecnicos.getId());
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		if (desechos != null)
			eliminarDesechoEspecialRecoleccion(desechos);
	}

	public void eliminarDesechoEspecialRecoleccion(List<DesechoEspecialRecoleccion> desechos) {
		crudServiceBean.delete(desechos);
	}

	/**
	 * 
	 * <b> Metodo que obtiene el desecho especial por su id y por su proyecto .
	 * </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 09/07/2015]
	 * </p>
	 * 
	 * @param idDesecho
	 *            : id del desecho
	 * @param idAprobacionRequisitosTecnicos
	 *            : id del proyecto
	 * @return DesechoEspecialRecoleccion: Desecho especial
	 * @throws ServiceException
	 *             : Excepcion
	 */
	public DesechoEspecialRecoleccion obtenerDesechoEspecialRecoleccionPorProyecto(final Integer idDesecho,
			final Integer idAprobacionRequisitosTecnicos) throws ServiceException {
		Map<String, Object> parametros = new HashMap<String, Object>();
		parametros.put("idDesecho", idDesecho);
		parametros.put("idAprobacionRequisitosTecnicos", idAprobacionRequisitosTecnicos);
		List<DesechoEspecialRecoleccion> desechosRecoleccion = (List<DesechoEspecialRecoleccion>) crudServiceBean
				.findByNamedQuery(DesechoEspecialRecoleccion.OBTENER_DESECHO_POR_PROYECTO, parametros);
		if (desechosRecoleccion != null && !desechosRecoleccion.isEmpty()) {
			return desechosRecoleccion.get(0);
		}
		return null;

	}
	
	// Lista de desechos peligrosos de varios ART fase TRANSPORTE
	public List<DesechoPeligrosoTransporte> getDesechoByVariosART(
			final String idsAprobacionRequisitosTecnicos) throws ServiceException {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT t.wadt_id, d.wada_key, d.wada_description, d.wada_id");
		sql.append(" FROM suia_iii.waste_dangerous_transportation t, suia_iii.waste_dangerous d");
		sql.append(" WHERE t.wadt_status='TRUE' and t.wada_id = d.wada_id and t.apte_id in (")
			.append(idsAprobacionRequisitosTecnicos)
			.append(")");
		return ejecutarSentenciasNativas.listarPorSentenciaSql(sql.toString(), DesechoEspecialRecoleccion.class, null);
	}

}
