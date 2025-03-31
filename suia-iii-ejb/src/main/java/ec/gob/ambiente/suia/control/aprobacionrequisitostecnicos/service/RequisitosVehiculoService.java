/*
 * Copyright 2015 MAGMASOFT
 * Todos los derechos reservados
 */
package ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.AprobacionRequisitosTecnicos;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.RequisitosVehiculo;
import ec.gob.ambiente.suia.exceptions.ServiceException;

/**
 * 
 * <b> Clase que contiene los servicios de la pagina requisitos vehiculo. </b>
 * 
 * @author vero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: vero $, $Date: 12/06/2015 $]
 *          </p>
 */
@Stateless
public class RequisitosVehiculoService {

	@EJB
	private CrudServiceBean crudServiceBean;

	/**
	 * 
	 * <b> Retorna una lista de los requisitos registrados en el proceso de
	 * aprobación de requerimientos técnicos. </b>
	 * <p>
	 * [Author: vero, Date: 12/06/2015]
	 * </p>
	 * 
	 * @param aprobacionRequisitosTecnicos
	 * @return
	 * @throws ServiceException
	 */
	public List<RequisitosVehiculo> getListaRequisitosVehiculo(AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos)
			throws ServiceException {
		List<RequisitosVehiculo> requisitosVehiculos = new ArrayList<RequisitosVehiculo>();
		try {
			requisitosVehiculos = (List<RequisitosVehiculo>) crudServiceBean.getEntityManager()
					.createQuery("From RequisitosVehiculo r where r.aprobacionRequisitosTecnicos=:aprobacion")
					.setParameter("aprobacion", aprobacionRequisitosTecnicos).getResultList();
			if (requisitosVehiculos == null || requisitosVehiculos.isEmpty()) {
				return null;
			} else {
				return requisitosVehiculos;
			}
		} catch (Exception e) {
			throw new ServiceException("Ocurrió un problema al recuperar los datos");
		}

	}
	
	public RequisitosVehiculo obtenerVehiculoPlaca(String numeroPlaca)throws ServiceException{
		List<RequisitosVehiculo> requisitosVehiculos = new ArrayList<RequisitosVehiculo>();
		try {
			requisitosVehiculos = (List<RequisitosVehiculo>) crudServiceBean.getEntityManager()
					.createQuery("From RequisitosVehiculo r where r.=:numeroPlaca")
					.setParameter("numeroPlaca",numeroPlaca).getResultList();
			if (requisitosVehiculos == null || requisitosVehiculos.isEmpty()) {
				return null;
			} else {
				return requisitosVehiculos.get(0);
			}
		} catch (Exception e) {
			throw new ServiceException("Ocurrió un problema al recuperar los datos");
		}

	}
	
	public Integer getNumeroRequisitosVehiculo(Integer idAprobacionRequisitosTecnicos) {
		StringBuilder sb = new StringBuilder();
		sb.append("Select count(*) "
				+ " from suia_iii.requirements_vehicle "
				+ " where apte_id = " + idAprobacionRequisitosTecnicos + " and reve_status = true ");

		List<Object> result = crudServiceBean.findByNativeQuery(sb.toString(), null);

		for (Object object : result) {
			return (((BigInteger) object).intValue());
		}

		return 0;
	}
	
}
