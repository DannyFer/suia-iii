/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados
 */
package ec.gob.ambiente.suia.proyectodesechopeligroso.service;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.domain.ProyectoDesechoPeligroso;

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
public class ProyectoDesechoPeligrosoServiceBean {

	@EJB
	private CrudServiceBean crudServiceBean;

	@SuppressWarnings("unchecked")
	public List<DesechoPeligroso> listarDesechoPeligrosos() {
		return (List<DesechoPeligroso>) crudServiceBean
				.findAll(DesechoPeligroso.class);
	}

	public DesechoPeligroso buscarDesechoPeligrososPorId(Integer id) {
		return crudServiceBean.find(DesechoPeligroso.class, id);
	}

	@SuppressWarnings("unchecked")
	public List<DesechoPeligroso> buscarDesechoPeligrososPorPadre(
			DesechoPeligroso padre) {
		List<DesechoPeligroso> desechos = (List<DesechoPeligroso>) crudServiceBean
				.getEntityManager()
				.createQuery(
						"From DesechoPeligroso c where c.desechoPeligroso =:padre order by c.id")
				.setParameter("padre", padre).getResultList();
		initIsDesechoPeligrosoFinal(desechos);
		return desechos;
	}

	@SuppressWarnings("unchecked")
	public List<DesechoPeligroso> buscarDesechoPeligrososPadres(String filtro) {
		List<DesechoPeligroso> desechos = new ArrayList<DesechoPeligroso>();
		if (filtro != null && !filtro.isEmpty()) {
			filtro = filtro.toLowerCase();
			Query query = crudServiceBean
					.getEntityManager()
					.createQuery(
							"FROM DesechoPeligroso c WHERE (lower(c.descripcion) like :filtro OR lower(c.nombre) like :filtro OR lower(c.clave) like :filtro OR lower(c.codigoBasilea) like :filtro) ORDER BY c.id");
			query.setParameter("filtro", "%" + filtro + "%");
			desechos = query.getResultList();
		} else {
			Query query = crudServiceBean
					.getEntityManager()
					.createQuery(
							"FROM DesechoPeligroso c WHERE c.desechoPeligroso = null ORDER BY c.id");
			desechos = query.getResultList();
		}
		initIsDesechoPeligrosoFinal(desechos);
		return desechos;
	}

	@SuppressWarnings("unchecked")
	public List<DesechoPeligroso> buscarDesechoPeligrososPorCodigoTipoDeDesecho(
			Integer idTipoDesecho) {
		List<DesechoPeligroso> desechos = (List<DesechoPeligroso>) crudServiceBean
				.getEntityManager()
				.createQuery(
						"From DesechoPeligroso d where d.fuenteDesechoPeligroso.id =:paramIdTipoDesecho order by d.clave")
				.setParameter("paramIdTipoDesecho", idTipoDesecho)
				.getResultList();
		return desechos;
	}

	@SuppressWarnings("unchecked")
	public List<ProyectoDesechoPeligroso> buscarProyectosDesechoPeligrosoPorProyecto(
			Integer idProyecto) {
		List<ProyectoDesechoPeligroso> desechos = (List<ProyectoDesechoPeligroso>) crudServiceBean
				.getEntityManager()
				.createQuery(
						"From ProyectoDesechoPeligroso pdp where pdp.proyectoLicenciamientoAmbiental.id =:paramIdProyecto")
				.setParameter("paramIdProyecto", idProyecto).getResultList();
		return desechos;
	}

	public ProyectoDesechoPeligroso buscarProyectoDesechoPeligrosoPorProyecto(
			Integer idProyecto, Integer idDesechoPeligroso) {
		try {
			ProyectoDesechoPeligroso desecho = (ProyectoDesechoPeligroso) crudServiceBean
					.getEntityManager()
					.createQuery(
							"From ProyectoDesechoPeligroso pdp where pdp.proyectoLicenciamientoAmbiental.id =:paramIdProyecto and pdp.desechoPeligroso.id =:paramIdDesechoPeligroso")
					.setParameter("paramIdDesechoPeligroso", idDesechoPeligroso)
					.setParameter("paramIdProyecto", idProyecto)
					.getSingleResult();
			return desecho;
		} catch (NoResultException nre) {
			return null;
		}
	}

	public void guardarProyectoDesechoPeligroso(
			ProyectoDesechoPeligroso proyectoDesechoPeligroso) {
		crudServiceBean.saveOrUpdate(proyectoDesechoPeligroso);
	}

	public void eliminarProyectoDesechoPeligroso(
			ProyectoDesechoPeligroso proyectoDesechoPeligroso) {
		crudServiceBean.delete(proyectoDesechoPeligroso);
	}

	private void initIsDesechoPeligrosoFinal(List<DesechoPeligroso> desechos) {

	}
}
