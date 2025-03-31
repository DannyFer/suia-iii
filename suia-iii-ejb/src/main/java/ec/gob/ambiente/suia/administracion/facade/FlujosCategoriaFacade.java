/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.administracion.facade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Categoria;
import ec.gob.ambiente.suia.domain.CategoriaFlujo;
import ec.gob.ambiente.suia.domain.Flujo;

/**
 * 
 * <b> Administrar flujos por categorias. </b>
 * 
 * @author Carlos Pupo
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Carlos Pupo, Fecha: 04/03/2015]
 *          </p>
 */
@Stateless
public class FlujosCategoriaFacade {

	@EJB
	private CrudServiceBean crudServiceBean;

	public void updateCatalogoCategorias(Categoria categoria) {
		crudServiceBean.saveOrUpdate(categoria.getCategoriaFlujos());
	}

	@SuppressWarnings("unchecked")
	public List<Categoria> getCategorias() {
		List<Categoria> list = (List<Categoria>) crudServiceBean.findAll(Categoria.class);
		for (Categoria categoria : list) {
			 List<CategoriaFlujo> flujosPorCategoria = obtenerCategoriasFlujosPorIdCategoria(categoria.getId());
			 categoria.setCategoriaFlujos(flujosPorCategoria);
			categoria.getCategoriaFlujos().size();
			Collections.sort(categoria.getCategoriaFlujos(), new Comparator<CategoriaFlujo>() {

				@Override
				public int compare(CategoriaFlujo o1, CategoriaFlujo o2) {
					if (o1.getOrden() > o2.getOrden())
						return 1;
					return -1;
				}
			});
			int order = 1;
			for (CategoriaFlujo cf : categoria.getCategoriaFlujos()) {
				cf.setOrden(order++);
			}
		}
		Collections.sort(list, new Comparator<Categoria>() {

			@Override
			public int compare(Categoria o1, Categoria o2) {
				if (o1.getId() > o2.getId())
					return 1;
				return -1;
			}
		});
		return list;
	}

	@SuppressWarnings("unchecked")
	public List<Flujo> getFlujos() {
		return (List<Flujo>) crudServiceBean.findAll(Flujo.class);
	}

	public void guardarFlujos(Categoria categoria, List<Flujo> flujos) {
		crudServiceBean.saveOrUpdate(flujos);
		List<CategoriaFlujo> categoriaFlujos = categoria.getCategoriaFlujos();
		categoriaFlujos = categoriaFlujos == null ? categoriaFlujos = new ArrayList<CategoriaFlujo>() : categoriaFlujos;
		for (Flujo flujo : flujos) {
			boolean add = true;
			for (CategoriaFlujo categoriaFlujo : categoriaFlujos) {
				if (categoriaFlujo.getFlujo().equals(flujo)) {
					add = false;
					break;
				}
			}
			if (add) {
				CategoriaFlujo categoriaFlujo = new CategoriaFlujo();
				categoriaFlujo.setCategoria(categoria);
				categoriaFlujo.setFlujo(flujo);
				categoriaFlujos.add(categoriaFlujo);
			}
		}
		crudServiceBean.saveOrUpdate(categoriaFlujos);
	}
	
	@SuppressWarnings("unchecked")
	public List<CategoriaFlujo> obtenerCategoriasFlujosPorIdCategoria(int categoriaId) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("categoriaId", categoriaId);
		List<CategoriaFlujo> result = (List<CategoriaFlujo>) crudServiceBean.findByNamedQuery(
				CategoriaFlujo.FIND_BY_CATEGORIA, parameters);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public Flujo getFlujoPorIdProceso(String idProceso) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idProceso", idProceso);
		List<Flujo> result = (List<Flujo>) crudServiceBean.findByNamedQuery(
				Flujo.FIND_BY_ID_PROCESO, parameters);
		if (result == null || result.isEmpty()) {
			return null;
		} else {
			return result.get(0);
		}
	}
}
