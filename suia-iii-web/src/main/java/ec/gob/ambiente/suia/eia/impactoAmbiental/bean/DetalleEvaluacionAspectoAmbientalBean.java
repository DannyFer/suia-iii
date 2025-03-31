/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.eia.impactoAmbiental.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import ec.gob.ambiente.suia.domain.AspectoAmbiental;
import ec.gob.ambiente.suia.domain.Componente;
import ec.gob.ambiente.suia.domain.DetalleEvaluacionAspectoAmbiental;
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
@ViewScoped
public class DetalleEvaluacionAspectoAmbientalBean implements Serializable {

	private static final long serialVersionUID = -2643301800926830008L;

	@EJB
	private ImpactoAmbientalFacade impactoAmbientalFacade;

	private static final Logger LOGGER = Logger.getLogger(DetalleEvaluacionAspectoAmbientalBean.class);

	@Setter
	private DetalleEvaluacionAspectoAmbiental detalleEvaluacionAspectoAmbiental;

	@Setter
	private List<DetalleEvaluacionAspectoAmbiental> detalleEvaluacionAspectoAmbientalLista;

	@Getter
	@Setter
	private List<Componente> componentes;

	@Getter
	@Setter
	private List<AspectoAmbiental> aspectoAmbientalLista;

	@PostConstruct
	public void init() {
		cargarComponentes();
	}

	public void cargarComponentes() {
		try {
			componentes = impactoAmbientalFacade.getComponentes();
		} catch (Exception exception) {
			LOGGER.error(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION, exception);
		}
	}

	public DetalleEvaluacionAspectoAmbiental getDetalleEvaluacionAspectoAmbiental() {
		return detalleEvaluacionAspectoAmbiental == null ? detalleEvaluacionAspectoAmbiental = new DetalleEvaluacionAspectoAmbiental()
				: detalleEvaluacionAspectoAmbiental;
	}

	public List<DetalleEvaluacionAspectoAmbiental> getDetalleEvaluacionAspectoAmbientalLista() {
		return detalleEvaluacionAspectoAmbientalLista == null ? detalleEvaluacionAspectoAmbientalLista = new ArrayList<>()
				: detalleEvaluacionAspectoAmbientalLista;
	}
}
