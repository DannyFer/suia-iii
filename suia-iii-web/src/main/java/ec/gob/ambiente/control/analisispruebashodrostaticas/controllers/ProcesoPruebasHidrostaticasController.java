/*
 * Copyright 2014 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.control.analisispruebashodrostaticas.controllers;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author Jonathan Guerrero
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Jonathan Guerrero, Fecha: 29/12/2014]
 *          </p>
 */
@RequestScoped
@ManagedBean
public class ProcesoPruebasHidrostaticasController {
	private static final String DEPLOYMENT_ID = "org.jbpm:proyectojg:1.0";

	private static final String ID_PROCESO = "pruebasHidrostaticasId";

	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;

	@EJB
	private ProcesoFacade procesoFacade;

	private void iniciarProceso() {
		Map<String, Object> mapita = new HashMap<String, Object>();
		mapita.put("sujetoDeControl", loginBean.getNombreUsuario());
		try {
			procesoFacade.iniciarProceso(loginBean.getUsuario(), ID_PROCESO, "PH - 4587", mapita);
		} catch (JbpmException e) {
			// log.error("Error al iniciar el proceso", e);
			JsfUtil.addMessageError("Ocurrio un error al intentar iniciar el proceso");
		}
	}

	public void iniciarPruebaHidrostatica() {
		iniciarProceso();
	}

}
