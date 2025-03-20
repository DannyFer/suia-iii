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

import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.service.AlmacenService;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.TipoEnvase;
import ec.gob.ambiente.suia.domain.TipoIluminacion;
import ec.gob.ambiente.suia.domain.TipoLocal;
import ec.gob.ambiente.suia.domain.TipoMaterialConstruccion;
import ec.gob.ambiente.suia.domain.TipoVentilacion;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.Almacen;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.AlmacenRecepcion;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.RecepcionDesechoPeligroso;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.dto.EntityRecepcionDesecho;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.utils.EjecutarSentenciasNativas;

/**
 * <b> Clase para los servicios del almacen. </b>
 * 
 * @author Javier Lucero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: Javier Lucero $, $Date: 09/06/2015 $]
 *          </p>
 */
@Stateless
public class AlmacenFacade {

	private static final Logger LOG = Logger.getLogger(AlmacenFacade.class);

	@EJB
	private CrudServiceBean crudServiceBean;

	@EJB
	private AlmacenService almacenService;
	@EJB
	private EjecutarSentenciasNativas ejecutarSentenciasNativas;

	public Almacen guardarAlmacen(Almacen almacen) {
		return crudServiceBean.saveOrUpdate(almacen);
	}

	public List<Almacen> getListaAlmacen() {
		return almacenService.getListaAlmacen();
	}

	public List<EntityRecepcionDesecho> obtenerPorAprobacionRequisitosTecnicos(
			final Integer idAprobacionRequisitosTecnicos) throws ServiceException {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT r.rehw_id, d.wada_key, d.wada_description, c.psty_name");
		sql.append(" FROM suia_iii.receipt_hazardous_waste r, suia_iii.waste_dangerous d, suia_iii.phisical_state_types c");
		sql.append(
				" WHERE r.rehw_status='TRUE' and r.wada_id = d.wada_id and c.psty_id = r.rehw_physical_state_id and r.apte_id =")
				.append(idAprobacionRequisitosTecnicos);
		return ejecutarSentenciasNativas.listarPorSentenciaSql(sql.toString(), EntityRecepcionDesecho.class, null);
	}

	public List<Almacen> listarAlmacenesPorAprobacionRequistos(final Integer idAprobacionRequisitosTecnicos)
			throws ServiceException {
		Map<String, Object> parametros = new HashMap<String, Object>();
		parametros.put("idAprobacionRequisitosTecnicos", idAprobacionRequisitosTecnicos);
		List<Almacen> almacenes = (List<Almacen>) crudServiceBean.findByNamedQuery(
				Almacen.LISTAR_POR_APROBACION_REQUISITOS_TECNICOS, parametros);
		for (Almacen alm : almacenes) {
			alm.setAlmacenRecepciones(listarAlmacenRecepcionPorIdAlmacen(alm.getId()));
		}
		return almacenes;
	}

	public List<AlmacenRecepcion> listarAlmacenRecepcionPorIdAlmacen(final Integer idAlmacen) throws ServiceException {
		Map<String, Object> parametros = new HashMap<String, Object>();
		parametros.put("idAlmacen", idAlmacen);
		List<AlmacenRecepcion> almaceneRecepcion = (List<AlmacenRecepcion>) crudServiceBean.findByNamedQuery(
				AlmacenRecepcion.LISTAR_POR_ID_ALMACEN, parametros);
		return almaceneRecepcion;
	}

	public void guardar(List<Almacen> listaAlmacenes, List<EntidadAuditable> almacenesBorradas) throws ServiceException {

			for (Almacen almacen : listaAlmacenes) {
				List<AlmacenRecepcion> listaDesechos = almacen.getAlmacenRecepciones();
				Almacen almPersist = crudServiceBean.saveOrUpdate(almacen);

				for (AlmacenRecepcion almRecp : listaDesechos) {
					if(almRecp.getEntityRecepcionDesecho()!=null)
					almRecp.setRecepcionDesechoPeligroso(new RecepcionDesechoPeligroso(almRecp.getEntityRecepcionDesecho()
							.getIdRecepcion()));
					almRecp.setAlmacen(almPersist);
					crudServiceBean.saveOrUpdate(almRecp);
				}
				/*for (AlmacenRecepcion c : listaDesechos) {
					c.setAlmacen(almacen);
					crudServiceBean.saveOrUpdate(c);
				}*/

			}
			for (EntidadAuditable obj : almacenesBorradas) {
				obj.setEstado(false);
				crudServiceBean.saveOrUpdate(obj);
			}

	}

	public void guardarAlmacenes(List<Almacen> listaAlmacen, List<Almacen> listaAlmacenEliminar,
			List<AlmacenRecepcion> listaAlmacenRecepcionEliminar) throws ServiceException {
		for (Almacen alm : listaAlmacen) {
			List<AlmacenRecepcion> listaAlmacenRecepcion = alm.getAlmacenRecepciones();
			Almacen almPersist = crudServiceBean.saveOrUpdate(alm);
			for (AlmacenRecepcion almRecp : listaAlmacenRecepcion) {
				almRecp.setRecepcionDesechoPeligroso(new RecepcionDesechoPeligroso(almRecp.getEntityRecepcionDesecho()
						.getIdRecepcion()));
				almRecp.setAlmacen(almPersist);
				crudServiceBean.saveOrUpdate(almRecp);
			}
		}
		crudServiceBean.delete(listaAlmacenEliminar);
		crudServiceBean.delete(listaAlmacenRecepcionEliminar);
	}

	/**
	 * 
	 * <b> Metodo para llenar los locales. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 09/06/2015]
	 * </p>
	 * 
	 * @return List<TipoLocal>: lista de los locales
	 * @throws ec.gob.ambiente.suia.exceptions.ServiceException
	 */
	public List<TipoLocal> getLocales() throws ServiceException {
		try {
			return (List<TipoLocal>) crudServiceBean.findAll(TipoLocal.class);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 
	 * <b> Metodo para llenar los materiales. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 09/06/2015]
	 * </p>
	 * 
	 * @return List<TipoMaterialConstruccion>: lista de los materiales
	 * @throws ec.gob.ambiente.suia.exceptions.ServiceException
	 */
	public List<TipoMaterialConstruccion> getMateriales() throws ServiceException {
		try {
			return (List<TipoMaterialConstruccion>) crudServiceBean.findAll(TipoMaterialConstruccion.class);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 
	 * <b> Metodo para llenar los ventilacion. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 09/06/2015]
	 * </p>
	 * 
	 * @return List<CatalogoGeneral>: lista de los ventilacion
	 * @throws ec.gob.ambiente.suia.exceptions.ServiceException
	 */
	public List<TipoVentilacion> getVentilacion() throws ServiceException {
		try {
			return (List<TipoVentilacion>) crudServiceBean.findAll(TipoVentilacion.class);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 
	 * <b> Metodo para llenar los iluminacion. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 09/06/2015]
	 * </p>
	 * 
	 * @return List<CatalogoGeneral>: lista de los iluminacion
	 * @throws ec.gob.ambiente.suia.exceptions.ServiceException
	 */
	public List<TipoIluminacion> getIluminacion() throws ServiceException {
		try {
			return (List<TipoIluminacion>) crudServiceBean.findAll(TipoIluminacion.class);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 
	 * <b> Metodo para llenar los iluminacion. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 09/06/2015]
	 * </p>
	 * 
	 * @return List<CatalogoGeneral>: lista de los iluminacion
	 * @throws ec.gob.ambiente.suia.exceptions.ServiceException
	 */
	public List<TipoEnvase> getTipoEnvase() throws ServiceException {
		try {
			return (List<TipoEnvase>) crudServiceBean.findAll(TipoEnvase.class);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	public boolean validarAlmacenRecepcionDesechos(final Integer idAprobacionRequisitosTecnicos)
			throws ServiceException {
		boolean existe = false;
		try {
			int totalDesechos = almacenService.getTotalDesechosAlmacenNativeQuery(idAprobacionRequisitosTecnicos);
			int totalAlmacenRecepcion = almacenService
					.getTotalAlmacenRecepcionNativeQuery(idAprobacionRequisitosTecnicos);
			if (totalDesechos == totalAlmacenRecepcion) {
				existe = true;
			}
		} catch (Exception e) {
			throw new ServiceException(e);
		}
		return existe;

	}

	public void eliminarAlmacenRecepcionPorIdRecepcion(final Integer idRecepcionDesechoPeligroso)
			throws ServiceException {
		Map<String, Object> parametros = new HashMap<String, Object>();
		parametros.put("idRecepcionDesechoPeligroso", idRecepcionDesechoPeligroso);
		List<AlmacenRecepcion> almaceneRecepcion = (List<AlmacenRecepcion>) crudServiceBean.findByNamedQuery(
				AlmacenRecepcion.LISTAR_POR_ID_RECEPCION, parametros);
		if (almaceneRecepcion != null && !almaceneRecepcion.isEmpty()) {
			for (AlmacenRecepcion almacenRecepcion : almaceneRecepcion) {
				if (obtenerNumeroRecepcion(almacenRecepcion.getAlmacen()) == 1) {
					crudServiceBean.delete(almacenRecepcion.getAlmacen());
				}
			}
			crudServiceBean.delete(almaceneRecepcion);

		}

	}

	private Integer obtenerNumeroRecepcion(Almacen almacen) {
		Map<String, Object> parametros = new HashMap<String, Object>();
		parametros.put("idAlmacen", almacen.getId());
		List<AlmacenRecepcion> almaceneRecepcion = (List<AlmacenRecepcion>) crudServiceBean.findByNamedQuery(
				AlmacenRecepcion.LISTAR_POR_ID_ALMACEN, parametros);
		return almaceneRecepcion.size();
	}

	public List<AlmacenRecepcion> listaAlmacenRecepcion(final Integer idRecepcionDesechoPeligroso)
			throws ServiceException {
		Map<String, Object> parametros = new HashMap<String, Object>();
		parametros.put("idRecepcionDesechoPeligroso", idRecepcionDesechoPeligroso);
		List<AlmacenRecepcion> almaceneRecepcion = (List<AlmacenRecepcion>) crudServiceBean.findByNamedQuery(
				AlmacenRecepcion.LISTAR_POR_ID_RECEPCION, parametros);
		return almaceneRecepcion;
	}

}
