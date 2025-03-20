/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.administracion.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import lombok.Setter;
import ec.gob.ambiente.suia.administracion.bean.FlujosCategoriasBean;
import ec.gob.ambiente.suia.administracion.facade.FlujosCategoriaFacade;
import ec.gob.ambiente.suia.domain.Categoria;
import ec.gob.ambiente.suia.domain.CategoriaFlujo;
import ec.gob.ambiente.suia.domain.Flujo;
import ec.gob.ambiente.suia.dto.DefinicionProceso;
import ec.gob.ambiente.suia.utils.JsfUtil;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author Carlos Pupo
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Carlos Pupo, Fecha: 03/03/2015]
 *          </p>
 */
@ManagedBean
public class FlujosCategoriasController {

	@Setter
	@ManagedProperty(value = "#{flujosCategoriasBean}")
	private FlujosCategoriasBean flujosCategoriasBean;

	@EJB
	private FlujosCategoriaFacade flujosCategoriaFacade;

	public void cambiarOrden(CategoriaFlujo categoriaFlujo, boolean incrementar) {
		categoriaFlujo.getCategoria().getCategoriaFlujos().remove(categoriaFlujo.getOrden() - 1);
		categoriaFlujo.getCategoria().getCategoriaFlujos()
				.add(categoriaFlujo.getOrden() - (!incrementar ? 2 : 0), categoriaFlujo);
		for (int i = 0; i < categoriaFlujo.getCategoria().getCategoriaFlujos().size(); i++) {
			categoriaFlujo.getCategoria().getCategoriaFlujos().get(i).setOrden(i + 1);
		}
		flujosCategoriaFacade.updateCatalogoCategorias(categoriaFlujo.getCategoria());
	}

	public void eliminarCategoriaFlujo(CategoriaFlujo categoriaFlujo) {
		categoriaFlujo.setEstado(false);
		int orden = 1;
		for (CategoriaFlujo cf : categoriaFlujo.getCategoria().getCategoriaFlujos()) {
			if (cf.getEstado())
				cf.setOrden(orden++);
		}
		flujosCategoriaFacade.updateCatalogoCategorias(categoriaFlujo.getCategoria());
		flujosCategoriasBean.init();
	}

	public void seleccionarCategoria(Categoria categoria) {
		flujosCategoriasBean.setCategoriaSeleccionada(categoria);
		flujosCategoriasBean.clearSelectionDefiniciones();
	}

	public void adicionarFlujos() {
		List<Flujo> flujosSeleccionados = new ArrayList<Flujo>();
		for (DefinicionProceso definicionProceso : flujosCategoriasBean.getDefinicionesProcesos()) {
			if (definicionProceso.isSeleccionado()) {
				flujosSeleccionados.add(flujosCategoriasBean.getFlujoPorDefinicion(definicionProceso));
			}
		}
		if (!flujosSeleccionados.isEmpty()) {
			flujosCategoriaFacade.guardarFlujos(flujosCategoriasBean.getCategoriaSeleccionada(), flujosSeleccionados);
			flujosCategoriasBean.clearSelectionDefiniciones();
			flujosCategoriasBean.init();
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
		}
	}
}
