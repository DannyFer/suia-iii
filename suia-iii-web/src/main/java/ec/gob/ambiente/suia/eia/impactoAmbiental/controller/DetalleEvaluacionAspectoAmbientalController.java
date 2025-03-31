/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.eia.impactoAmbiental.controller;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

import ec.gob.ambiente.suia.domain.DetalleEvaluacionAspectoAmbiental;
import ec.gob.ambiente.suia.eia.impactoAmbiental.bean.DetalleEvaluacionAspectoAmbientalBean;
import ec.gob.ambiente.suia.eia.impactoAmbiental.facade.ImpactoAmbientalFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author mayriliscs
 * @version Revision: 1.0
 *          <p>
 *          [Autor: mayriliscs, Fecha: 26/06/2015]
 *          </p>
 */
@ManagedBean
public class DetalleEvaluacionAspectoAmbientalController implements Serializable {

	private static final long serialVersionUID = -536007663616317108L;

	@EJB
	private ImpactoAmbientalFacade impactoAmbientalFacade;

	@Setter
	@ManagedProperty(value = "#{detalleEvaluacionAspectoAmbientalBean}")
	private DetalleEvaluacionAspectoAmbientalBean detalleEvaluacionAspectoAmbientalBean;

	private static final Logger LOGGER = Logger.getLogger(DetalleEvaluacionAspectoAmbientalController.class);

	public void cargarAspectosAmbientales() {
		try {
			if (detalleEvaluacionAspectoAmbientalBean.getDetalleEvaluacionAspectoAmbiental().getComponente() != null)
				detalleEvaluacionAspectoAmbientalBean.setAspectoAmbientalLista(impactoAmbientalFacade
						.getAspectosAmbientalesPorComponente(detalleEvaluacionAspectoAmbientalBean
								.getDetalleEvaluacionAspectoAmbiental().getComponente()));
		} catch (Exception exception) {
			LOGGER.error(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION, exception);
		}
	}

	public void aceptar() {
		if (!detalleEvaluacionAspectoAmbientalBean.getDetalleEvaluacionAspectoAmbientalLista().contains(
				detalleEvaluacionAspectoAmbientalBean.getDetalleEvaluacionAspectoAmbiental()))
			detalleEvaluacionAspectoAmbientalBean.getDetalleEvaluacionAspectoAmbientalLista().add(
					detalleEvaluacionAspectoAmbientalBean.getDetalleEvaluacionAspectoAmbiental());
		JsfUtil.addCallbackParam("addDetalleEvaluacion");
		JsfUtil.addCallbackParam("addDetalleEvaluacionOS");
	}

	public void clear() {
		detalleEvaluacionAspectoAmbientalBean.setDetalleEvaluacionAspectoAmbiental(null);
		detalleEvaluacionAspectoAmbientalBean.setAspectoAmbientalLista(null);
	}

	public void clearSelections() {
		detalleEvaluacionAspectoAmbientalBean.setDetalleEvaluacionAspectoAmbiental(null);
		detalleEvaluacionAspectoAmbientalBean.setDetalleEvaluacionAspectoAmbientalLista(null);
		detalleEvaluacionAspectoAmbientalBean.setAspectoAmbientalLista(null);
	}

	public void delete(DetalleEvaluacionAspectoAmbiental detalle) {
		if (detalleEvaluacionAspectoAmbientalBean.getDetalleEvaluacionAspectoAmbientalLista().contains(detalle))
			detalleEvaluacionAspectoAmbientalBean.getDetalleEvaluacionAspectoAmbientalLista().remove(detalle);
	}

	public void edit(DetalleEvaluacionAspectoAmbiental detalle) {
		detalleEvaluacionAspectoAmbientalBean.setDetalleEvaluacionAspectoAmbiental(detalle);
		cargarAspectosAmbientales();
	}
}
