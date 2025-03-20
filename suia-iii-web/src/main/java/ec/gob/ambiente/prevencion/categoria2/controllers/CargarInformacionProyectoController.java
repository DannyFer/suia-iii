/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */

package ec.gob.ambiente.prevencion.categoria2.controllers;

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

import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.FichaAmbientalPma;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.FichaAmbientalPmaFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.ProyectoLicenciaAmbientalFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;

/**
 * <b> Clase para obtener el proyecto y crear la ficha si esta no existiera.
 * </b>
 * 
 * @author Javier Lucero
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Javier Lucero, Fecha: 18/03/2015]
 *          </p>
 */
@ManagedBean
@ViewScoped
public class CargarInformacionProyectoController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = Logger
			.getLogger(CargarInformacionProyectoController.class);

	@EJB
	private ProcesoFacade procesoFacade;

	@EJB
	private ProyectoLicenciaAmbientalFacade proyectoLicenciaAmbientalFacade;

	@EJB
	private FichaAmbientalPmaFacade fichaAmbientalPmaFacade;

	@Getter
	private String paginaConMenu;

	@Getter
	@Setter
	@ManagedProperty(value = "#{proyectosBean}")
	private ProyectosBean proyectosBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;

	@PostConstruct
	public void ini() {
		try {
			Map<String, Object> variables = procesoFacade
					.recuperarVariablesProceso(loginBean.getUsuario(),
							bandejaTareasBean.getProcessId());
			ProyectoLicenciamientoAmbiental proyectoLicenciamientoAmbiental = proyectoLicenciaAmbientalFacade
					.getProyectoPorId(Integer.parseInt(variables.get(
							"idProyecto").toString()));
			FichaAmbientalPma fichaAmbientalPma = fichaAmbientalPmaFacade
					.getFichaAmbientalPorCodigoProyecto(proyectoLicenciamientoAmbiental
							.getCodigo());
			proyectoLicenciamientoAmbiental
					.setSector(proyectoLicenciamientoAmbiental.getCatalogoCategoria()
							.getCategoriaSistema().getId());
			proyectosBean.setProyectoToShow(proyectoLicenciamientoAmbiental);
			if (fichaAmbientalPma.getId() == null) {
				fichaAmbientalPma = new FichaAmbientalPma();
				fichaAmbientalPma
						.setProyectoLicenciamientoAmbiental(proyectoLicenciamientoAmbiental);
				fichaAmbientalPma
						.setProcessId(bandejaTareasBean.getProcessId());
				fichaAmbientalPma = fichaAmbientalPmaFacade
						.guardarSoloFicha(fichaAmbientalPma);

			}
			paginaConMenu = "contenido/menu.xhtml";
		} catch (Exception e) {
			LOG.error(e , e);
		}
	}
}
