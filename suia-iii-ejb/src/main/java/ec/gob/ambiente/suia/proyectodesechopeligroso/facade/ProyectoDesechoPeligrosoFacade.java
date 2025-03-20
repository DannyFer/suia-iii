/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.proyectodesechopeligroso.facade;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.domain.ProyectoDesechoPeligroso;
import ec.gob.ambiente.suia.proyectodesechopeligroso.service.ProyectoDesechoPeligrosoServiceBean;

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
public class ProyectoDesechoPeligrosoFacade {
	@EJB
	private ProyectoDesechoPeligrosoServiceBean serviceBean;

	public List<DesechoPeligroso> listarDesechoPeligrosos() {
		return serviceBean.listarDesechoPeligrosos();
	}

	public DesechoPeligroso buscarDesechoPeligrososPorId(
			Integer idDesechoPeligroso) {
		return serviceBean.buscarDesechoPeligrososPorId(idDesechoPeligroso);
	}

	public List<DesechoPeligroso> buscarDesechoPeligrososPadres(String filtro) {
		return serviceBean.buscarDesechoPeligrososPadres(filtro);
	}

	public List<DesechoPeligroso> buscarDesechoPeligrososPorPadre(
			DesechoPeligroso padre) {
		return serviceBean.buscarDesechoPeligrososPorPadre(padre);
	}

	public List<DesechoPeligroso> buscarDesechoPeligrososPorCodigoTipoDeDesecho(
			Integer idTipoDesecho) {
		return serviceBean
				.buscarDesechoPeligrososPorCodigoTipoDeDesecho(idTipoDesecho);
	}

	public List<ProyectoDesechoPeligroso> buscarProyectoDesechoPeligrosoPorProyecto(
			Integer idProyecto) {
		return serviceBean
				.buscarProyectosDesechoPeligrosoPorProyecto(idProyecto);
	}

	public ProyectoDesechoPeligroso buscarProyectoDesechoPeligrosoPorProyecto(
			Integer idProyecto, Integer idDesechoPeligroso) {

		return serviceBean.buscarProyectoDesechoPeligrosoPorProyecto(
				idProyecto, idDesechoPeligroso);
	}

	public void guardarProyectoDesechoPeligroso(
			ProyectoDesechoPeligroso proyectoDesechoPeligroso) {
		serviceBean.guardarProyectoDesechoPeligroso(proyectoDesechoPeligroso);
	}

	public void eliminarProyectoDesechoPeligroso(
			ProyectoDesechoPeligroso proyectoDesechoPeligroso) {
		serviceBean.eliminarProyectoDesechoPeligroso(proyectoDesechoPeligroso);
	}
}
