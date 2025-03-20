/*
 * Copyright 2014 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.control.cierreabandono.bean;

import java.io.Serializable;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.cierreabandono.PlanCierreAbandonoVariables;
import ec.gob.ambiente.suia.control.cierreabandono.facade.CierreAbandonoFacade;
import ec.gob.ambiente.suia.domain.enums.PlanCierreFields;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
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
@ViewScoped
public class VerPlanCierreBean implements Serializable {

	private static final long serialVersionUID = -3815471219601945880L;

	private static final Logger LOGGER = Logger.getLogger(VerPlanCierreBean.class);

	@ManagedProperty(value = "#{loginBean}")
	@Setter
	private LoginBean loginBean;

	@ManagedProperty(value = "#{planCierreBean}")
	@Getter
	@Setter
	private PlanCierreBean planCierreBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;

	@EJB
	private ProcesoFacade procesoFacade;
	
	@EJB
	private CierreAbandonoFacade cierreAbandonoFacade;
	
	@Getter
	private boolean showPanel;
	
	@Getter
	private Map<String, String> comentariosPlanCierre;

	@PostConstruct
	public void init() {
		comentariosPlanCierre = JsfUtil.generateEmptyMapString(PlanCierreFields.class);
		final long processId = bandejaTareasBean.getProcessId();
		try {
			final int idPlanCierre = Integer.valueOf(procesoFacade.recuperarVariablesProceso(loginBean.getUsuario(), processId).get(PlanCierreAbandonoVariables.idPlanCierre.name()).toString());
			planCierreBean.initPlanCierre(cierreAbandonoFacade.cargarRegistro(idPlanCierre));
		} catch (JbpmException e) {
			LOGGER.error(e.getMessage());
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_INICIAR_TAREA);
		}
	}
}
