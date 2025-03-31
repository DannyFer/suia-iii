/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.eia.impactoAmbiental.controller;

import java.util.ArrayList;
import java.util.List;

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
import org.primefaces.event.FileUploadEvent;

import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.eia.impactoAmbiental.bean.EvaluacionAspectoAmbientalBean;
import ec.gob.ambiente.suia.eia.impactoAmbiental.bean.IdentificacionEvaluacionImpactosBean;
import ec.gob.ambiente.suia.eia.impactoAmbiental.bean.ImpactoAmbientalBaseBean;
import ec.gob.ambiente.suia.eia.impactoAmbiental.facade.ImpactoAmbientalFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.validacionseccion.facade.ValidacionSeccionesFacade;

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
public class IdentificacionEvaluacionImpactosController extends ImpactoAmbientalBaseBean {

	private static final long serialVersionUID = 4552553314765067141L;

	private static final Logger LOG = Logger.getLogger(IdentificacionEvaluacionImpactosController.class);

	@Setter
	@Getter
	@ManagedProperty(value = "#{identificacionEvaluacionImpactosBean}")
	private IdentificacionEvaluacionImpactosBean identificacionEvaluacionImpactosBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{proyectosBean}")
	private ProyectosBean proyectosBean;

	@EJB
	private ImpactoAmbientalFacade impactoAmbientalFacade;

	@EJB
	private ValidacionSeccionesFacade validacionSeccionesFacade;
	
	private boolean existeNuevoDocumento = false;
	private boolean existeNuevoTratamiento = false;

	public void uploadListenerDocumento(FileUploadEvent event) {
		Documento documento = super.uploadListener(event);
		identificacionEvaluacionImpactosBean.getIdentificacionEvaluacionImpactoAmbiental().setDocumento(documento);
		existeNuevoDocumento = true;
	}

	public void uploadListenerTratamiento(FileUploadEvent event) {
		Documento documento = super.uploadListener(event);
		identificacionEvaluacionImpactosBean.getIdentificacionEvaluacionImpactoAmbiental().setTratamiento(documento);
		existeNuevoTratamiento = true;
	}

	public void guardar() {
		try {
			if(identificacionEvaluacionImpactosBean.isExisteObservaciones()){
				
				impactoAmbientalFacade.guardarHistorico(identificacionEvaluacionImpactosBean.getEstudio(),
						JsfUtil.getBean(BandejaTareasBean.class).getTarea(),
						identificacionEvaluacionImpactosBean.getIdentificacionEvaluacionImpactoAmbiental(),
						identificacionEvaluacionImpactosBean.getEvaluacionesSalvadas(),
						JsfUtil.getBean(EvaluacionAspectoAmbientalBean.class).getEvaluacionAspectoAmbientalLista(), 
						identificacionEvaluacionImpactosBean.getNumeroNotificaciones(), existeNuevoDocumento, 
						existeNuevoTratamiento);
			}else{
				impactoAmbientalFacade.guardar(
						identificacionEvaluacionImpactosBean.getEstudio(),
						JsfUtil.getBean(BandejaTareasBean.class).getTarea(),
						identificacionEvaluacionImpactosBean
								.getIdentificacionEvaluacionImpactoAmbiental(),
						identificacionEvaluacionImpactosBean
								.getEvaluacionesSalvadas(),
						JsfUtil.getBean(EvaluacionAspectoAmbientalBean.class)
								.getEvaluacionAspectoAmbientalLista());
			}

			validacionSeccionesFacade.guardarValidacionSeccion("EIA",
					"identificacionEvaluacionImpactos",
					identificacionEvaluacionImpactosBean.getEstudio().getId()
							.toString());

			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
			
			existeNuevoTratamiento = false;
			existeNuevoDocumento = false;
			
			JsfUtil.redirectTo("/prevencion/licenciamiento-ambiental/eia/impactoAmbiental/identificacionEvaluacionImpactos.jsf");
		} catch (Exception e) {
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
			LOG.error(
					"Error guardando el EIA, en la sección Identificación y evaluación de impactos",
					e);
		}
	}

	public void cancelar() {
		JsfUtil.redirectTo("/prevencion/licenciamiento-ambiental/eia/impactoAmbiental/identificacionEvaluacionImpactos.jsf");
	}

	public void validateData(FacesContext context, UIComponent validate,
			Object value) {
		List<FacesMessage> messages = new ArrayList<FacesMessage>();
		if (JsfUtil.getBean(EvaluacionAspectoAmbientalBean.class)
				.getEvaluacionAspectoAmbientalLista().isEmpty() && !identificacionEvaluacionImpactosBean.getEsMineriaNoMetalicos())
			messages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Debe ingresar, como mínimo, una evaluación.", null));
		if (identificacionEvaluacionImpactosBean
				.getIdentificacionEvaluacionImpactoAmbiental().getDocumento() == null)
			messages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"El campo 'Documento' es requerido.", null));
		/*if (identificacionEvaluacionImpactosBean
				.getIdentificacionEvaluacionImpactoAmbiental().getTratamiento() == null)
			messages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"El campo 'Tratamiento' es requerido.", null));*/
		if (!messages.isEmpty())
			throw new ValidatorException(messages);
	}

}
