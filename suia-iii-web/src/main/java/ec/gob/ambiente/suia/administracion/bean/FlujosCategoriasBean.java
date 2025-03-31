/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.administracion.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.administracion.facade.FlujosCategoriaFacade;
import ec.gob.ambiente.suia.domain.Categoria;
import ec.gob.ambiente.suia.domain.Flujo;
import ec.gob.ambiente.suia.dto.DefinicionProceso;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.webservicesclientes.facade.JbpmSuiaCustomServicesFacade;

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
@ViewScoped
public class FlujosCategoriasBean implements Serializable {

	private static final long serialVersionUID = 4063733262569153237L;

	@EJB
	private FlujosCategoriaFacade flujosCategoriaFacade;

	@EJB
	private JbpmSuiaCustomServicesFacade jbpmSuiaCustomServicesFacade;

	@Getter
	private List<Categoria> categorias;

	@Getter
	private List<Flujo> flujos;

	private List<DefinicionProceso> definicionesProcesos;

	private Map<String, Boolean> definicionesIds;

	@Getter
	private boolean validarProcesos;

	@Getter
	@Setter
	private Categoria categoriaSeleccionada;

	@PostConstruct
	public void init() {
		flujos = flujosCategoriaFacade.getFlujos();
		categorias = flujosCategoriaFacade.getCategorias();

		try {
			definicionesProcesos = jbpmSuiaCustomServicesFacade.listaDefinicionesProcesos();
			if (definicionesProcesos != null) {
				for (DefinicionProceso proceso : definicionesProcesos)
					getDefinicionesIds().put(proceso.getId(), true);
			}
			validarProcesos = true;
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("No se pudieron cargar las definiciones de procesos.");
			validarProcesos = false;
		}
	}

	public boolean definicionValida(String id) {
		return getDefinicionesIds().containsKey(id);
	}

	public List<DefinicionProceso> getDefinicionesProcesos() {
		return definicionesProcesos == null ? definicionesProcesos = new ArrayList<DefinicionProceso>()
				: definicionesProcesos;
	}

	public void clearSelectionDefiniciones() {
		for (DefinicionProceso definicionProceso : getDefinicionesProcesos()) {
			definicionProceso.setSeleccionado(false);
		}
	}

	public Map<String, Boolean> getDefinicionesIds() {
		return definicionesIds == null ? definicionesIds = new HashMap<String, Boolean>() : definicionesIds;
	}

	public Flujo getFlujoPorDefinicion(DefinicionProceso definicionProceso) {
		for (Flujo flujo : flujos) {
			if (flujo.getIdProceso().equals(definicionProceso.getId())) {
				flujo.setNombreFlujo(definicionProceso.getName());
				return flujo;
			}
		}
		Flujo flujo = new Flujo();
		flujo.setIdProceso(definicionProceso.getId());
		flujo.setNombreFlujo(definicionProceso.getName());
		return flujo;
	}
}
