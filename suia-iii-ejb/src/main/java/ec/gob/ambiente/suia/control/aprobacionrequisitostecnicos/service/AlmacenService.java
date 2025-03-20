/*
 * Copyright 2015 MAGMASOFT
 * Todos los derechos reservados
 */
package ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.Almacen;

/**
 * <b> Clase que tiene los servicios para el almacen. </b>
 * 
 * @author Javier Lucero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: Javier Lucero $, $Date: 09/06/2015 $]
 *          </p>
 */
@Stateless
public class AlmacenService {
	private static final Logger LOG = Logger.getLogger(AlmacenService.class);

	@EJB
	private CrudServiceBean crudServiceBean;

	/**
	 * 
	 * <b> Metodo que obtiene la lista de todos los almacenes. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 05/06/2015]
	 * </p>
	 * 
	 * @return List<Almacen> : Lista de los conductores
	 */
	public List<Almacen> getListaAlmacen() {
		List<Almacen> almacenes = new ArrayList<Almacen>();
		try {
			almacenes = (List<Almacen>) crudServiceBean.getEntityManager()
					.createQuery("From Almacen m where m.estado = true").getResultList();
			if (almacenes == null || almacenes.isEmpty()) {
				return null;
			} else {
				return almacenes;
			}
		} catch (Exception e) {
			LOG.error("Error al obtener los almacenes.", e);
		}
		return almacenes;

	}

	public int getTotalDesechosAlmacenNativeQuery(final Integer idAprobacionRequisitosTecnicos) {
		int totalDesechosAlmacen = 0;
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT count (DISTINCT(wd.wada_key))");
			sql.append(" FROM suia_iii.warehouse_receipt_waste wr, suia_iii.warehouse w, suia_iii.receipt_hazardous_waste rh, suia_iii.waste_dangerous wd");
			sql.append(" WHERE wr.waho_id = w.waho_id AND wr.rehw_id = rh.rehw_id AND rh.wada_id = wd.wada_id AND w.waho_status=true AND wawa_status=true AND w.apte_id = :idAprobacionRequisitosTecnicos");

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

	public int getTotalAlmacenRecepcionNativeQuery(final Integer idAprobacionRequisitosTecnicos) {
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
