/*
 * Copyright 2015 MAGMASOFT
 * Todos los derechos reservados
 */
package ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.service;

import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.ActividadProtocoloPrueba;
import ec.gob.ambiente.suia.exceptions.ServiceException;

/**
 * <b> Clase que contiene los servicios la tabla actividad protocolo pruebas.
 * </b>
 * 
 * @author vero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: vero $, $Date: 10/06/2015 $]
 *          </p>
 */
@Stateless
public class ActividadProtocoloPruebasService {
	@EJB
	private CrudServiceBean crudServiceBean;

	public List<ActividadProtocoloPrueba> getActividadPrueba(String tipo) throws ServiceException {
		List<ActividadProtocoloPrueba> lista = null;
		try {
			lista = crudServiceBean.getEntityManager()
					.createQuery("From ActividadProtocoloPrueba a where a.tipo =:tipo").setParameter("tipo", tipo)
					.getResultList();
			if (lista == null || lista.isEmpty()) {
				return null;
			} else {
				return lista;
			}
		} catch (Exception e) {
			throw new ServiceException("Ocurrió un problema al recuperar los datos");
		}
	}

}
