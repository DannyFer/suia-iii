/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.prevencion.licenciamiento.requistosprevios.bean;

import java.io.Serializable;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.suia.domain.RequisitosPreviosLicenciamiento;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author Carlos Pupo
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Carlos Pupo, Fecha: 06/03/2015]
 *          </p>
 */
@ManagedBean
@ViewScoped
public class RevisarInformeOficioAprobacionBean implements Serializable {

	private static final long serialVersionUID = -8879934058316085151L;

	@EJB
	private ProcesoFacade procesoFacade;

	@Getter
	private RequisitosPreviosLicenciamiento requisitos;

	@Getter
	@Setter
	private boolean existenObservaciones;

	@Getter
	private boolean existenObservacionesPrevias;

	@Getter
	@Setter
	private String observaciones;

	@Getter
	private String observacionesPrevias;

	@PostConstruct
	public void init() {
		requisitos = new RequisitosPreviosLicenciamiento();
		try {
			Map<String, Object> params = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), JsfUtil.getCurrentProcessInstanceId());
			existenObservacionesPrevias = new Boolean(params.get("existenObservaciones").toString());
			observacionesPrevias = (String) params.get("observaciones");
		} catch (JbpmException e) {
		}
	}
}
