/*
 * Copyright 2015 MAGMASOFT
 * Todos los derechos reservados
 */
package ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.service;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

/**
 * <b> Clase que tiene los servicios para la eliminacion de desechos. </b>
 * 
 * @author Javier Lucero
 * @version $Revision: 1.0 $
 *          <p>
 *          almacen [$Author: Javier Lucero $, $Date: 11/06/2015 $]
 *          </p>
 */
@Stateless
public class EliminacionDesechoService {

	private static final Logger LOG = Logger.getLogger(EliminacionDesechoService.class);

	@EJB
	private CrudServiceBean crudServiceBean;

	public int getTotalDesechoSinGeneradorNativeQuery(final Integer idAprobacionRequisitosTecnicos) {
		int total = 0;
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT count(DISTINCT(wadi_id))");
			sql.append(" FROM suia_iii.waste_disposal_receipt wr, suia_iii.waste_disposal  wd");
			sql.append(" WHERE wr.wadr_id=wd.wadr_id AND wadr_status='TRUE' AND wd.wada_id IS NOT NULL AND wd.wadi_code_generator IS NULL AND wd.wadi_status='TRUE' AND wr.apte_id = :idAprobacionRequisitosTecnicos");

			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("idAprobacionRequisitosTecnicos", idAprobacionRequisitosTecnicos);
			List<Object> result = crudServiceBean.findByNativeQuery(sql.toString(), parameters);

			for (Object object : result) {
				BigInteger array = (BigInteger) object;
				total = (Integer) array.intValue();
			}
		} catch (Exception e) {
			LOG.error("Error al obtener las recepciones con los desechos.", e);
		}
		return total;
	}

	public int getTotalDesechoSinGeneradorPorProyectoNativeQuery(final String codigoProyecto, final String codigoSolicitud) {
		int total = 0;
		try {
			StringBuilder sql = new StringBuilder();

			sql.append("SELECT wadi_id");
			sql.append(" FROM suia_iii.approval_technical_requirements rt, suia_iii.waste_disposal_receipt wr, suia_iii.waste_disposal wd,");
			sql.append(" suia_iii.hazardous_wastes_generators gd, suia_iii.hazardous_wastes_waste_dangerous dp, suia_iii.projects_environmental_licensing pr");
			sql.append(" WHERE rt.apte_id = wr.apte_id");
			sql.append(" AND wr.wadr_id = wd.wadr_id");
			sql.append(" AND gd.hwge_id = dp.hwge_id");
			sql.append(" AND wd.wada_id = dp.wada_id");
			sql.append(" AND rt.apte_status = true");
			sql.append(" AND wr.wadr_status = true");
			sql.append(" AND wd.wadi_status = true");
			sql.append(" AND gd.hwge_status = true");
			sql.append(" AND dp.hwwd_status = true");
			sql.append(" AND wd.wada_id IS NOT NULL");
			sql.append(" AND gd.pren_id = pr.pren_id");
			sql.append(" AND rt.apte_proyect = pr.pren_code");
			sql.append(" AND pr.pren_status = true");
			sql.append(" AND pren_code = :codigoProyecto");

			sql.append(" AND rt.apte_request = :codigoSolicitud");

			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("codigoProyecto", codigoProyecto);
			parameters.put("codigoSolicitud", codigoSolicitud);
			List<Object> result = crudServiceBean.findByNativeQuery(sql.toString(), parameters);

			total = result.size();
		} catch (Exception e) {
			LOG.error("Error al obtener las recepciones con los desechos.", e);
		}
		return total;
	}

	public int getTotalGeneradoresEnCursoPorProyectoNativeQuery(final Integer idProyecto) {
		int total = 0;
		try {
			StringBuilder sql = new StringBuilder();

			sql.append("SELECT g.pren_id");
			sql.append(" FROM suia_iii.hazardous_wastes_generators g, suia_iii.projects_environmental_licensing p");
			sql.append(" WHERE g.pren_id = p.pren_id");
			sql.append(" AND p.pren_id = :idProyecto");
			sql.append(" AND hwge_finalized = false");
			sql.append(" AND hwge_status = true");

			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("idProyecto", idProyecto);
			List<Object> result = crudServiceBean.findByNativeQuery(sql.toString(), parameters);

			total = result.size();
		} catch (Exception e) {
			LOG.error("Error al obtener los generadores de desechos asociados al proyecto con id = "+idProyecto+".", e);
		}
		return total;
	}

	public int getTotalGeneradoresEnCursoPorCodSolicitudNativeQuery(final String codigoSolicitud) {
		int total = 0;
		try {
			StringBuilder sql = new StringBuilder();

			sql.append("SELECT hwge_id");
			sql.append(" FROM suia_iii.hazardous_wastes_generators");
			sql.append(" WHERE hwge_code = :codigoSolicitud");
			sql.append(" AND hwge_finalized = false AND hwge_status=true");

			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("codigoSolicitud", codigoSolicitud);
			List<Object> result = crudServiceBean.findByNativeQuery(sql.toString(), parameters);

			total = result.size();
		} catch (Exception e) {
			LOG.error("Error al obtener los generadores de desechos con código de solicitud: '"+codigoSolicitud+"'.", e);
		}
		return total;
	}

/*SELECT * FROM suia_iii.hazardous_wastes_generators WHERE hwge_code = '13-14-DPP-100'*/
	public int getTotalDesechosEliminacionNativeQuery(final Integer idAprobacionRequisitosTecnicos) {
		int totalDesechosAlmacen = 0;
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT count (DISTINCT(wp.wada_key))");
			sql.append(" FROM suia_iii.waste_disposal wd,suia_iii.waste_disposal_types wt, suia_iii.waste_disposal_receipt wr, suia_iii.receipt_hazardous_waste rh, suia_iii.waste_dangerous wp");
			sql.append(" WHERE wd.wdty_id = wt.wdty_id and wd.wadr_id=wr.wadr_id and wr.rehw_id = rh.rehw_id and rh.wada_id = wp.wada_id and wr.wadr_status=true and wr.apte_id = :idAprobacionRequisitosTecnicos");

			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("idAprobacionRequisitosTecnicos", idAprobacionRequisitosTecnicos);
			List<Object> result = crudServiceBean.findByNativeQuery(sql.toString(), parameters);

			for (Object object : result) {
				BigInteger array = (BigInteger) object;
				totalDesechosAlmacen = (Integer) array.intValue();
			}
		} catch (Exception e) {
			LOG.error("Error al obtener los almacenes.", e);
		}
		return totalDesechosAlmacen;
	}

	public int getTotalEliminacionRecepcionNativeQuery(final Integer idAprobacionRequisitosTecnicos) {
		int totalAlmacenRecepcion = 0;
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT count (DISTINCT(wd.wada_key))");
			sql.append(" FROM suia_iii.receipt_hazardous_waste rh, suia_iii.waste_dangerous wd");
			sql.append(" WHERE rh.wada_id = wd.wada_id AND rh.rehw_status AND rh.apte_id = :idAprobacionRequisitosTecnicos");

			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("idAprobacionRequisitosTecnicos", idAprobacionRequisitosTecnicos);
			List<Object> result = crudServiceBean.findByNativeQuery(sql.toString(), parameters);

			for (Object object : result) {
				BigInteger array = (BigInteger) object;
				totalAlmacenRecepcion = (Integer) array.intValue();
			}
		} catch (Exception e) {
			LOG.error("Error al obtener las recepciones con los desechos.", e);
		}
		return totalAlmacenRecepcion;
	}

}
