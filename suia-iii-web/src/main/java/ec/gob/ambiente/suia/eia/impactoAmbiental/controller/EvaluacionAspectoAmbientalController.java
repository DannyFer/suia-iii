/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.eia.impactoAmbiental.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.service.SustanciaQuimicaPeligrosaService;
import ec.gob.ambiente.suia.domain.DetalleEvaluacionAspectoAmbiental;
import ec.gob.ambiente.suia.domain.EtapasProyecto;
import ec.gob.ambiente.suia.domain.EvaluacionAspectoAmbiental;
import ec.gob.ambiente.suia.eia.descripcionProyecto.controler.DescripcionProyectoController;
import ec.gob.ambiente.suia.eia.descripcionProyecto.facade.ActividadLicenciamientoFacade;
import ec.gob.ambiente.suia.eia.impactoAmbiental.bean.DetalleEvaluacionAspectoAmbientalBean;
import ec.gob.ambiente.suia.eia.impactoAmbiental.bean.EvaluacionAspectoAmbientalBean;
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
public class EvaluacionAspectoAmbientalController implements Serializable {

	private static final long serialVersionUID = -1840128911871974486L;

	private static final Logger LOG = Logger.getLogger(EvaluacionAspectoAmbientalController.class);

	@Setter
	@Getter
	@ManagedProperty(value = "#{evaluacionAspectoAmbientalBean}")
	private EvaluacionAspectoAmbientalBean evaluacionAspectoAmbientalBean;

	@EJB
	private ActividadLicenciamientoFacade actividadLicenciamientoFacade;

	@EJB
	private SustanciaQuimicaPeligrosaService sustanciaQuimicaPeligrosaService;

	@Setter
	@Getter
	private EtapasProyecto etapaSeleccionada;

	@PostConstruct
	public void init(){
		if(JsfUtil.getBean(DescripcionProyectoController.class).getProyectoHidrocarburos()){
			evaluacionAspectoAmbientalBean.setFase(JsfUtil.getBean(DescripcionProyectoController.class).getFaseDelProyecto());
			evaluacionAspectoAmbientalBean.setEtapasDeFase(JsfUtil.getBean(DescripcionProyectoController.class).cargarEtapasParaEvaluacion());
		}else{
			evaluacionAspectoAmbientalBean.setEtapasProyectos(evaluacionAspectoAmbientalBean.getEvaluacionAspectoAmbiental().getEtapasProyecto());
			etapaSeleccionada = evaluacionAspectoAmbientalBean.getEtapasProyectos();
		}
	}
	public void aceptar() {
		if (!evaluacionAspectoAmbientalBean.getEvaluacionAspectoAmbientalLista().contains(
				evaluacionAspectoAmbientalBean.getEvaluacionAspectoAmbiental())) {
			evaluacionAspectoAmbientalBean.getEvaluacionAspectoAmbientalLista().add(
					evaluacionAspectoAmbientalBean.getEvaluacionAspectoAmbiental());
		}

		if (JsfUtil.getBean(DetalleEvaluacionAspectoAmbientalBean.class) != null
				&& JsfUtil.getBean(DetalleEvaluacionAspectoAmbientalBean.class)
						.getDetalleEvaluacionAspectoAmbientalLista() != null) {
			evaluacionAspectoAmbientalBean.getEvaluacionAspectoAmbiental().setDetalleEvaluacionLista(
					JsfUtil.getBean(DetalleEvaluacionAspectoAmbientalBean.class)
							.getDetalleEvaluacionAspectoAmbientalLista());
		}
			JsfUtil.addCallbackParam("addEvaluacion");
			JsfUtil.addCallbackParam("addEvaluacionOS");

	}

	public void clear() {
		evaluacionAspectoAmbientalBean.setEvaluacionAspectoAmbiental(null);
		evaluacionAspectoAmbientalBean.setCatalogoCategoriaFase(null);
		JsfUtil.getBean(DetalleEvaluacionAspectoAmbientalController.class).clearSelections();
	}

	public void delete(EvaluacionAspectoAmbiental evaluacionAspectoAmbiental) {
		if (evaluacionAspectoAmbientalBean.getEvaluacionAspectoAmbientalLista().contains(evaluacionAspectoAmbiental))
			evaluacionAspectoAmbientalBean.getEvaluacionAspectoAmbientalLista().remove(evaluacionAspectoAmbiental);
	}

	public void edit(EvaluacionAspectoAmbiental evaluacionAspectoAmbiental) {
		evaluacionAspectoAmbientalBean.setCatalogoCategoriaFase(evaluacionAspectoAmbiental.getActividadLicenciamiento().getCatalogoCategoriaFase());
		cargarActividades();
		evaluacionAspectoAmbientalBean.setEvaluacionAspectoAmbiental(evaluacionAspectoAmbiental);
		JsfUtil.getBean(DetalleEvaluacionAspectoAmbientalBean.class).setDetalleEvaluacionAspectoAmbientalLista(null);
						
		JsfUtil.getBean(DetalleEvaluacionAspectoAmbientalBean.class).getDetalleEvaluacionAspectoAmbientalLista()
				.addAll(evaluacionAspectoAmbiental.getDetalleEvaluacionLista());
	}

	public void validateData(FacesContext context, UIComponent validate, Object value) {
		List<FacesMessage> messages = new ArrayList<FacesMessage>();
		if (JsfUtil.getBean(DetalleEvaluacionAspectoAmbientalBean.class).getDetalleEvaluacionAspectoAmbientalLista()
				.isEmpty())
			messages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Debe ingresar, como mínimo, un detalle de la evaluación.", null));
		if (!messages.isEmpty())
			throw new ValidatorException(messages);
	}

	public void cargarActividadesHidro(){
			evaluacionAspectoAmbientalBean.setActividadesPorEtapas(sustanciaQuimicaPeligrosaService.getListaActividadPorEtapaID(evaluacionAspectoAmbientalBean.getEvaluacionAspectoAmbiental().getEtapasProyecto().getId()));

	}

	public void cargarActividades() {
		try {
			evaluacionAspectoAmbientalBean.setActividades(actividadLicenciamientoFacade.getActividadesLicenciamiento(evaluacionAspectoAmbientalBean.getCatalogoCategoriaFase(), evaluacionAspectoAmbientalBean.getEstudio()));
			if(evaluacionAspectoAmbientalBean.getActividades().size() == 0){
				System.out.println("Caminaaaa no hay na");
			}
		} catch (Exception e) {
			LOG.error("No se pudo recuperar la lista de actividades asociadas a la categoria", e);
		}
	}
}
