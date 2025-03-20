/*
 * Copyright 2015 MAGMASOFT
 * Todos los derechos reservados
 */
package ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.service;

import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.TipoManejoDesechos;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.AprobacionRequisitosTecnicos;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.ModalidadReuso;
import ec.gob.ambiente.suia.exceptions.ServiceException;

/**
 * <b> Clase que contiene los servicios para modalidad reuso. </b>
 * 
 * @author vero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: vero $, $Date: 10/06/2015 $]
 *          </p>
 */
@Stateless
public class ModalidadReusoService {
	@EJB
	private CrudServiceBean crudServiceBean;

	@SuppressWarnings("unchecked")
	public ModalidadReuso getModalidadReuso(AprobacionRequisitosTecnicos aprobacion) throws ServiceException {
		List<ModalidadReuso> lista = null;
		try {
			lista = crudServiceBean.getEntityManager()
					.createQuery("From ModalidadReuso m where m.aprobacionRequisitosTecnicos =:aprobacion")
					.setParameter("aprobacion", aprobacion).getResultList();
			if (lista == null || lista.isEmpty()) {
				return null;
			} else {
				return lista.get(0);
			}
		} catch (Exception e) {
			throw new ServiceException("Ocurrió un problema al recuperar los datos");
		}

	}

	@SuppressWarnings("unchecked")
	public List<TipoManejoDesechos> getManejoDesechos(String tipo) {
		return crudServiceBean.getEntityManager().createQuery("From TipoManejoDesechos m where m.tipo =:tipo")
				.setParameter("tipo", tipo).getResultList();

	}

}
