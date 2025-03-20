/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.desechoPeligroso.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
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
public class DesechoPeligrosoServiceBean {

	@EJB
	private CrudServiceBean crudServiceBean;

	public DesechoPeligroso get(Integer id) {
		return crudServiceBean.find(DesechoPeligroso.class, id);
	}

	@SuppressWarnings("unchecked")
	public List<CategoriaFuenteDesechoPeligroso> buscarCategoriasFuentesDesechosPeligroso() {
		return (List<CategoriaFuenteDesechoPeligroso>) crudServiceBean.getEntityManager().createQuery(
				"From CategoriaFuenteDesechoPeligroso cd where cd.estado = true order by cd.id").getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<FuenteDesechoPeligroso> buscarFuentesDesechosPeligrosoPorCategeria(
			CategoriaFuenteDesechoPeligroso categoria) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idCategoria", categoria.getId());
		return (List<FuenteDesechoPeligroso>) crudServiceBean.findByNamedQuery(
				FuenteDesechoPeligroso.FIND_BY_CATEGORIA, parameters);
	}
	
	@SuppressWarnings("unchecked")
	public DesechoPeligroso buscarDesechosPeligrosoById(Integer idDesechoPeligroso) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idDesecho", idDesechoPeligroso);
		return (DesechoPeligroso) crudServiceBean.findByNamedQuery(DesechoPeligroso.FIND_BY_ID, parameters).get(0);
	}

	@SuppressWarnings("unchecked")
	public List<DesechoPeligroso> buscarDesechosPeligrosoPorFuente(FuenteDesechoPeligroso fuente) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idFuente", fuente.getId());
		return (List<DesechoPeligroso>) crudServiceBean.findByNamedQuery(DesechoPeligroso.FIND_BY_FUENTE, parameters);
	}
	
	@SuppressWarnings("unchecked")
	public List<DesechoPeligroso> buscarDesechosPeligrosoPorDescripcion(String filtro) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("descripcion", "%" + filtro.toLowerCase() + "%");
		return (List<DesechoPeligroso>) crudServiceBean.findByNamedQuery(DesechoPeligroso.FIND_LIKE_DESCRIPCION,
				parameters);
	}
	
	@SuppressWarnings("unchecked")
    public List<PoliticaDesechosActividad> politicaDesechosActividad(Integer waac){
        try {
            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("waacid", waac);
            List<PoliticaDesechosActividad> lista = (List<PoliticaDesechosActividad>)crudServiceBean.findByNamedQuery(
                    PoliticaDesechosActividad.FIND_BY_WAAC, parameters);
            return lista;
        }catch (Exception e) {
            return null;
        }
    }

	@SuppressWarnings("unchecked")
	public List<DesechoPeligroso> getAllDesechosPeligrosos() {
		return (List<DesechoPeligroso>) crudServiceBean.findByNamedQuery(DesechoPeligroso.FIND_ALL, null);
	}
	
	@SuppressWarnings("unchecked")
	public List<DesechoPeligroso> buscarDesechosPeligrosoPorFuentePorDescripcion(String filtro) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("descripcion", "%" + filtro.toLowerCase() + "%");
		return (List<DesechoPeligroso>) crudServiceBean.findByNamedQuery(DesechoPeligroso.FIND_BY_FUENTE_LIKE_DESCRIPCION,
				parameters);
	}

}
