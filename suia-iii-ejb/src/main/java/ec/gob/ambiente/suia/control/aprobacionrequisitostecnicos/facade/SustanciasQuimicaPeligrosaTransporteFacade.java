/*

 * Copyright 2015 MAGMASOFT
 * Todos los derechos reservados
 */
package ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.service.SustanciasQuimicaPeligrosaTransporteService;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.RequisitosVehiculo;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.SustanciaQuimicaPeligrosaTransporte;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.SustanciaQuimicaPeligrosaTransporteUbicacionGeografica;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;

import java.util.ArrayList;

/**
 * <b> Clase para los servicios de la transportacion de las sustancias quimicas
 * peligrosas. </b>
 * 
 * @author Javier Lucero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: Javier Lucero $, $Date: 18/06/2015 $]
 *          </p>
 */
@Stateless
public class SustanciasQuimicaPeligrosaTransporteFacade {
	@EJB
	private CrudServiceBean crudServiceBean;

	@EJB
	private SustanciasQuimicaPeligrosaTransporteService peligrosaTransporteService;

	@EJB
	private DocumentosFacade documentosFacade;

	/**
	 * 
	 * <b> Metodo que guarda las sustancias quimicas peligrosas. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 24/06/2015]
	 * </p>
	 * 
	 * @param listaPeligrosoTransportes
	 *            : lista de sustancias
	 * @param listaPeligrosoTransportesEliminar
	 *            : lasta de sustancias a eliminar
	 * @param listaSustanciaUbicacionGeograficaEliminar
	 *            : lista de ubicaciones a eliminar
	 * @throws ServiceException
	 *             : Excepcion
	 */
	public void guardarSustanciaQuimicaPeligrosaTransportacion(
			List<SustanciaQuimicaPeligrosaTransporte> listaPeligrosoTransportes,
			List<SustanciaQuimicaPeligrosaTransporte> listaPeligrosoTransportesEliminar,
			List<SustanciaQuimicaPeligrosaTransporteUbicacionGeografica> listaSustanciaUbicacionGeograficaEliminar,
			long idProceso, long idTarea) throws ServiceException, CmisAlfrescoException {
		for (SustanciaQuimicaPeligrosaTransporte desPeligrosoTransporte : listaPeligrosoTransportes) {
			List<SustanciaQuimicaPeligrosaTransporteUbicacionGeografica> desechoPeligrosoTransporteUbicacionGeograficas = desPeligrosoTransporte
					.getSustanciasUbicacion();

			if (desPeligrosoTransporte.getDocumentoManualOperaciones().getExtesion() != null) {
				desPeligrosoTransporte.setDocumentoManualOperaciones(documentosFacade
						.subirFileAlfrescoSinProyectoAsociado(desPeligrosoTransporte.getDocumentoManualOperaciones(),
								desPeligrosoTransporte.getAprobacionRequisitosTecnicos().getSolicitud(), idProceso,
								idTarea, RequisitosVehiculo.class.getSimpleName(),
								AprobacionRequisitosTecnicosFacade.NOMBRE_CARPETA_REQUISITOS_VEHICULO,
								TipoDocumentoSistema.TIPO_DOCUMENTO_ANEXOS));
			} else {
				desPeligrosoTransporte.setDocumentoManualOperaciones(null);
			}
			SustanciaQuimicaPeligrosaTransporte desechoPersist = crudServiceBean.saveOrUpdate(desPeligrosoTransporte);
			if (!desechoPersist.isDestinoNivelNacional()) {
				for (SustanciaQuimicaPeligrosaTransporteUbicacionGeografica desechoUbicacion : desechoPeligrosoTransporteUbicacionGeograficas) {
					desechoUbicacion.setSustanciaQuimicaPeligrosaTransporte(desechoPersist);
					crudServiceBean.saveOrUpdate(desechoUbicacion);
				}

			}

		}
		crudServiceBean.delete(listaPeligrosoTransportesEliminar);
		crudServiceBean.delete(listaSustanciaUbicacionGeograficaEliminar);

	}

	/**
	 * 
	 * <b> Metodo que obtiene la lista de sustancias quimicas segun el proyecto
	 * con sus ubicaciones. </b>
	 * <p>
	 * [Author: magmasoft-027, Date: 24/06/2015]
	 * </p>
	 * 
	 * @param idAprobacionRequisitosTecnicos
	 * @return
	 * @throws ServiceException
	 */
	public List<SustanciaQuimicaPeligrosaTransporte> getListaSustanciaQuimicaPeligrosaTransporte(
			final Integer idAprobacionRequisitosTecnicos) throws ServiceException {
		Map<String, Object> parametros = new HashMap<String, Object>();
		parametros.put("idAprobacionRequisitosTecnicos", idAprobacionRequisitosTecnicos);
		List<SustanciaQuimicaPeligrosaTransporte> sustancias = (List<SustanciaQuimicaPeligrosaTransporte>) crudServiceBean
				.findByNamedQuery(SustanciaQuimicaPeligrosaTransporte.LISTAR_POR_APROBACION_REQUISITOS_TECNICOS,
						parametros);
		for (SustanciaQuimicaPeligrosaTransporte sus : sustancias) {
			if (!sus.isDestinoNivelNacional()) {
				sus.setSustanciasUbicacion(listarSustanciaUbicacionPorIdSustancia(sus.getId()));
			} else {
				sus.setSustanciasUbicacion(new ArrayList<SustanciaQuimicaPeligrosaTransporteUbicacionGeografica>());
			}

		}
		return sustancias;
	}
	
	@SuppressWarnings("unchecked")
	public List<SustanciaQuimicaPeligrosaTransporte> getListaSustanciaQuimicaPeligrosaTransporteLazy(Integer inicio, Integer cantidad,
			Integer idAprobacionRequisitosTecnicos) throws ServiceException {
		Map<String, Object> parametros = new HashMap<String, Object>();
		parametros.put("idAprobacionRequisitosTecnicos", idAprobacionRequisitosTecnicos);
		List<SustanciaQuimicaPeligrosaTransporte> sustancias = (List<SustanciaQuimicaPeligrosaTransporte>) crudServiceBean
				.findByNamedQueryPaginado(SustanciaQuimicaPeligrosaTransporte.LISTAR_POR_APROBACION_REQUISITOS_TECNICOS,
						parametros, inicio, cantidad);
		
		int indice = 0;
		for (SustanciaQuimicaPeligrosaTransporte sus : sustancias) {
			sus.setIndice(indice);

			if (!sus.isDestinoNivelNacional()) {
				sus.setSustanciasUbicacion(listarSustanciaUbicacionPorIdSustancia(sus.getId()));

				int indiceInt = 0;
				for (SustanciaQuimicaPeligrosaTransporteUbicacionGeografica susUbi : sus.getSustanciasUbicacion()) {
					susUbi.setIndice(indiceInt);
					indiceInt++;
				}

			} else {
				sus.setSustanciasUbicacion(new ArrayList<SustanciaQuimicaPeligrosaTransporteUbicacionGeografica>());
			}

			indice++;
		}

		return sustancias;
	}
	
	public Integer contarSustancias(final Integer idAprobacionRequisitosTecnicos) {
		StringBuilder sb = new StringBuilder();
		sb.append("Select count(*) "
				+ " from suia_iii.dangerous_chemistry_substances_transportation "
				+ " where apte_id = " + idAprobacionRequisitosTecnicos + " and datr_status = true ");

		List<Object> result = crudServiceBean.findByNativeQuery(sb.toString(), null);

		for (Object object : result) {
			return (((BigInteger) object).intValue());
		}

		return 0;
	}

	/**
	 * 
	 * <b> Metodo que obtiene todos las ubicaciones de cada una de las
	 * sustancias peligrosas. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 24/06/2015]
	 * </p>
	 * 
	 * @param idSustanciaQuimicaPeligrosa
	 *            : id de la sustancia
	 * @return List<SustanciaQuimicaPeligrosaTransporteUbicacionGeografica>:
	 *         lista de ubicaciones
	 * @throws ServiceException
	 *             : Excepcion
	 */
	public List<SustanciaQuimicaPeligrosaTransporteUbicacionGeografica> listarSustanciaUbicacionPorIdSustancia(
			final Integer idSustanciaQuimicaPeligrosa) throws ServiceException {
		Map<String, Object> parametros = new HashMap<String, Object>();
		parametros.put("idSustanciaQuimicaPeligrosa", idSustanciaQuimicaPeligrosa);
		List<SustanciaQuimicaPeligrosaTransporteUbicacionGeografica> sustanciaUbicacion = (List<SustanciaQuimicaPeligrosaTransporteUbicacionGeografica>) crudServiceBean
				.findByNamedQuery(SustanciaQuimicaPeligrosaTransporteUbicacionGeografica.LISTAR_POR_ID_SUSTANCIA,
						parametros);
		return sustanciaUbicacion;
	}

	public byte[] descargarFile(Documento documento) throws CmisAlfrescoException {
		return documentosFacade.descargar(documento.getIdAlfresco());
	}
	
	public void eliminarSustanciaQuimicaPeligrosaTransportacion(
			List<SustanciaQuimicaPeligrosaTransporte> listaPeligrosoTransportesEliminar,
			List<SustanciaQuimicaPeligrosaTransporteUbicacionGeografica> listaSustanciaUbicacionGeograficaEliminar) {
		crudServiceBean.delete(listaPeligrosoTransportesEliminar);
		crudServiceBean.delete(listaSustanciaUbicacionGeograficaEliminar);

	}

}
