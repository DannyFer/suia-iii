/*
 * Copyright 2014 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.comun.base;

import java.io.Serializable;

import lombok.Getter;
import ec.gob.ambiente.core.interfaces.CompleteOperation;
import ec.gob.ambiente.suia.utils.JsfUtil;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author Carlos Pupo
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Carlos Pupo, Fecha: 31/12/2014]
 *          </p>
 */
public abstract class ComunBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private CompleteOperation completeOperation;
	private String functionURL;
	private String nextURL;

	@Getter
	protected String previousURL;

	private boolean needNavigateToFunction = true;

	public ComunBean() {
		this.functionURL = getFunctionURL();
	}

	public void initFunction(String nextURL, CompleteOperation completeOperation) {
		this.nextURL = nextURL;
		this.completeOperation = completeOperation;
		cleanData();
		if (needNavigateToFunction)
			navigateToFunction();
	}

	public void initFunction(String nextURL, boolean needNavigateToFunction, CompleteOperation completeOperation) {
		this.nextURL = nextURL;
		this.completeOperation = completeOperation;
		cleanData();
		if (needNavigateToFunction)
			navigateToFunction();
		this.needNavigateToFunction = needNavigateToFunction;
	}

	public Object executeOperation(Object object) {
		needNavigateToFunction = true;
		if (completeOperation != null)
			return completeOperation.endOperation(object);
		return null;
	}

	public String executeOperationAction(Object object) {
		if (!executeBusinessLogic(object))
			return null;
		if (nextURL == null)
			nextURL = "/bandeja/bandejaTareas.jsf";
		return JsfUtil.actionNavigateTo(nextURL);
	}

	public String executeOperationAction() {
		return executeOperationAction(null);
	}

	public String cancelar() {
		if (previousURL != null)
			return JsfUtil.actionNavigateTo(previousURL);
		return JsfUtil.actionNavigateToBandeja();
	}

	public abstract String getFunctionURL();

	public abstract void cleanData();

	public abstract boolean executeBusinessLogic(Object object);

	private void navigateToFunction() {
		JsfUtil.redirectTo(functionURL);
	}
}
