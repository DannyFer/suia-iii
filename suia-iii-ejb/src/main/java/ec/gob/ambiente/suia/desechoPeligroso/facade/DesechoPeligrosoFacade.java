/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.desechoPeligroso.facade;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.desechoPeligroso.service.DesechoPeligrosoServiceBean;
import ec.gob.ambiente.suia.domain.CategoriaFuenteDesechoPeligroso;
import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.domain.FuenteDesechoPeligroso;
import ec.gob.ambiente.suia.domain.PoliticaDesechosActividad;

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
public class DesechoPeligrosoFacade {
	@EJB
	private DesechoPeligrosoServiceBean serviceBean;

	public List<CategoriaFuenteDesechoPeligroso> buscarCategoriasFuentesDesechosPeligroso() {
		return serviceBean.buscarCategoriasFuentesDesechosPeligroso();
	}
	
	public List<FuenteDesechoPeligroso> buscarFuentesDesechosPeligrosoPorCategeria(CategoriaFuenteDesechoPeligroso categoria) {
		return serviceBean.buscarFuentesDesechosPeligrosoPorCategeria(categoria);
	}
	
	public List<DesechoPeligroso> buscarDesechosPeligrosoPorFuente(FuenteDesechoPeligroso fuente) {
		return serviceBean.buscarDesechosPeligrosoPorFuente(fuente);
	}
	
	public List<DesechoPeligroso> buscarDesechosPeligrosoPorDescripcion(String filtro) {
		return serviceBean.buscarDesechosPeligrosoPorDescripcion(filtro);
	}
	
	public DesechoPeligroso get(Integer id) {
		return serviceBean.get(id);
	}
	
	public List<PoliticaDesechosActividad> politicaDesechosActividad(Integer waac) {
        return serviceBean.politicaDesechosActividad(waac);
    }
	
	public List<DesechoPeligroso> todosDesechosPeligrosos() {
		return serviceBean.getAllDesechosPeligrosos();
	}
	
	public List<DesechoPeligroso> buscarDesechosPeligrosoPorFuentePorDescripcion(String filtro) {
		return serviceBean.buscarDesechosPeligrosoPorFuentePorDescripcion(filtro);
	}

}
