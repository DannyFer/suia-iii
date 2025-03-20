/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.desechoPeligroso.service;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.IncompatibilidadDesechoPeligroso;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author mayriliscs
 * @version Revision: 1.0
 *          <p>
 *          [Autor: mayriliscs, Fecha: 16/03/2015]
 *          </p>
 */
@Stateless
public class IncompatibilidadDesechoServiceBean {

	@EJB
	private CrudServiceBean crudServiceBean;

	@SuppressWarnings("unchecked")
	public List<IncompatibilidadDesechoPeligroso> listarIncompatibilidadDesechoPeligrosos() {
		return (List<IncompatibilidadDesechoPeligroso>) crudServiceBean
				.findAll(IncompatibilidadDesechoPeligroso.class);
	}

	@SuppressWarnings("unchecked")
	public List<IncompatibilidadDesechoPeligroso> buscarIncompatibilidadDesechoPeligrososPorPadre(
			IncompatibilidadDesechoPeligroso padre) {
		List<IncompatibilidadDesechoPeligroso> incompatibilidades = (List<IncompatibilidadDesechoPeligroso>) crudServiceBean
				.getEntityManager()
				.createQuery(
						"From IncompatibilidadDesechoPeligroso i where i.incompatibilidadDesechoPeligroso =:padre order by i.id")
				.setParameter("padre", padre).getResultList();
		initIsIncompatibilidadDesechoPeligrosoFinal(incompatibilidades);
		return incompatibilidades;
	}

	@SuppressWarnings("unchecked")
	public List<IncompatibilidadDesechoPeligroso> buscarIncompatibilidadDesechoPeligrososPadres(String filtro) {
		List<IncompatibilidadDesechoPeligroso> incompatibilidades = new ArrayList<IncompatibilidadDesechoPeligroso>();
		if (filtro != null && !filtro.isEmpty()) {
			filtro = filtro.toLowerCase();
			Query query = crudServiceBean
					.getEntityManager()
					.createQuery(
							"FROM IncompatibilidadDesechoPeligroso i WHERE lower(i.nombre) like :filtro ORDER BY i.id");
			query.setParameter("filtro", "%" + filtro + "%");
			incompatibilidades = query.getResultList();
		} else {
			Query query = crudServiceBean
					.getEntityManager()
					.createQuery(
							"FROM IncompatibilidadDesechoPeligroso i WHERE i.incompatibilidadDesechoPeligroso = null ORDER BY i.id");
			incompatibilidades = query.getResultList();
		}
		initIsIncompatibilidadDesechoPeligrosoFinal(incompatibilidades);
		return incompatibilidades;
	}

	private void initIsIncompatibilidadDesechoPeligrosoFinal(List<IncompatibilidadDesechoPeligroso> desechos) {
		if (desechos != null)
			for (IncompatibilidadDesechoPeligroso incompatibilidadDesechoPeligroso : desechos)
				incompatibilidadDesechoPeligroso.isIncompatibilidadDesechoFinal();
	}
}
