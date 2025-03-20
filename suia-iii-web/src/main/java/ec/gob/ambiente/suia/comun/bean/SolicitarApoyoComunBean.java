/*
} * Copyright 2014 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.comun.bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.core.interfaces.CompleteOperation;
import ec.gob.ambiente.suia.comun.base.ComunBean;
import ec.gob.ambiente.suia.utils.JsfUtil;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author Carlos Pupo
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Carlos Pupo, Fecha: 23/12/2014]
 *          </p>
 */
@ManagedBean
@SessionScoped
public class SolicitarApoyoComunBean extends ComunBean {

	private static final long serialVersionUID = 3508186252205332321L;

	// private List<Object> listaApoyo;
	@Getter
	@Setter
	private String[] listaApoyo;

	@Getter
	@Setter
	private Boolean requiereApoyo;

	@Getter
	@Setter
	String taskName;

	/**
	 * Inicializar el componente
	 * 
	 * @param taskName
	 *            Nombre a mostrar en la pantalla como título
	 * @param requiereApoyo
	 *            Si por defecto tendrá marcado el requiere o no, si null por
	 *            defecto false
	 * @param nextURL
	 *            Url a direccionar al terminar
	 * @param completeOperation
	 *            Operación al realizar al terminar
	 */
	public void initFunction(String taskName, Boolean requiereApoyo,
			String nextURL, CompleteOperation completeOperation) {
		super.initFunction(nextURL, completeOperation);
		this.taskName = taskName;
		if (requiereApoyo != null) {
			this.requiereApoyo = requiereApoyo;
		} else {
			this.requiereApoyo = false;
		}
	}

	@Override
	public String getFunctionURL() {
		return "/comun/solicitarApoyo.jsf";
	}

	@Override
	public void cleanData() {
		listaApoyo = null;
		taskName = null;
	}

	@Override
	public boolean executeBusinessLogic(Object object) {
		if (this.requiereApoyo && this.listaApoyo == null
				|| this.listaApoyo.length == 0) {
			JsfUtil.addMessageError("Debe seleccionar al menos un elemento.");
			return false;

		} else {

			List<String> lista = new ArrayList<String>();
			if (listaApoyo != null) {
				lista = Arrays.asList(listaApoyo);
			}
			executeOperation(lista);
			return true;
		}
	}

}
