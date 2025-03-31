/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.prevencion.categoria2.bean;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.domain.FichaAmbientalPma;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.FichaAmbientalPmaFacade;
import ec.gob.ambiente.suia.utils.Constantes;

/**
 * 
 * @author frank torres
 */
@ManagedBean
@ViewScoped
public class FichaAmbientalGeneralMenuBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6528541578042118645L;

	@ManagedProperty(value = "#{proyectosBean}")
	@Getter
	@Setter
	private ProyectosBean proyectosBean;

	@EJB
	private FichaAmbientalPmaFacade fichaAmbientalPmaFacade;
	@Getter
	@Setter
	private FichaAmbientalPma fichaAmbiental; 

	@PostConstruct
	private void cargarDatos() {
		this.fichaAmbiental = fichaAmbientalPmaFacade
				.getFichaAmbientalPorIdProyecto(proyectosBean.getProyecto()
						.getId());
		if (fichaAmbiental == null) {
			fichaAmbiental = new FichaAmbientalPma();
		}
	}

	public void visitarDescripcionProyecto() {
		this.fichaAmbiental.setValidarDescripcionProyectoObraActividad(true);
		fichaAmbientalPmaFacade.guardarSoloFicha(fichaAmbiental);
	}
	@Getter
	@Setter
	public String opcional="";
	public boolean ppc() throws ParseException {
		Date fechaproyecto=null;
		Date fechabloqueo=null;
		Date fechabloqueoObligatorioInicio=null;
		Date fechabloqueoObligatorioFin=null;
		Date fechabloqueoOpcionalInicio=null;
		Date fechabloqueoOpcionalFin=null;
		
		Date fechabloqueoSIN=null;
		boolean bloquear=false;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		fechabloqueo = sdf.parse(Constantes.getFechaProyectosSinPccAntes());
		fechabloqueoObligatorioInicio = sdf.parse(Constantes.getFechaProyectosObligatorioPccInicio());
		fechabloqueoObligatorioFin = sdf.parse(Constantes.getFechaProyectosObligatorioPccFin());	
		fechabloqueoOpcionalInicio = sdf.parse(Constantes.getFechaProyectosOpcionalPccInicio());
		fechabloqueoOpcionalFin = sdf.parse(Constantes.getFechaProyectosOpcionalPccFin());	
		
		fechabloqueoSIN = sdf.parse(Constantes.getFechaProyectosSinPpcAdelante());
		
		fechaproyecto=sdf.parse(proyectosBean.getProyecto().getFechaRegistro().toString());
		if (fechaproyecto.before(fechabloqueo)){
			return false;
		}			
		if (fechaproyecto.after(fechabloqueoObligatorioInicio) && fechaproyecto.before(fechabloqueoObligatorioFin)){
			return true;
		}
		
		if (fechaproyecto.after(fechabloqueoObligatorioInicio) && fechaproyecto.before(fechabloqueoObligatorioFin)){
			return true;
		}
		
		if (fechaproyecto.after(fechabloqueoOpcionalInicio) && fechaproyecto.before(fechabloqueoOpcionalFin)){
			setOpcional("opcional");
			return true;
		}
		if (fechaproyecto.after(fechabloqueoSIN)){
			return false;
		}		
		
		return bloquear;
		
	}
}
