/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.control.aprobacionpuntosmonitoreo.bean;

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
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author mayriliscs
 * @version Revision: 1.0
 *          <p>
 *          [Autor: mayriliscs, Fecha: 07/01/2015]
 *          </p>
 */
@ViewScoped
@ManagedBean
public class AdjuntarComprobantePagoBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3536892733298650903L;

	@Getter
	@Setter
	private int dineroAPagar;

	@Getter
	@Setter
	private int puntosAModificarOAprobar;

	@Getter
	@Setter
	private UploadedFile fichero;

	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;

	@EJB
	private ProcesoFacade procesoFacade;

	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;

	private static final Logger LOG = Logger.getLogger(AdjuntarComprobantePagoBean.class);

	public void uploadListener(FileUploadEvent event) {
		LOG.info("Aqui va el codigo para salvar el fichero cuando tengamos alfresco");
	}

	@PostConstruct
	public void iniciar() {

		long idInstanciaProceso = bandejaTareasBean.getTarea().getProcessInstanceId();
		int cantidadpuntosSeleccionados = 0;
		try {
			if (idInstanciaProceso != 0) {
				Map<String, Object> variablesProceso = procesoFacade.recuperarVariablesProceso(loginBean.getUsuario(), idInstanciaProceso);
				String puntosSeleccionadosPagar = (String) variablesProceso.get("puntosSeleccionadosPagar");

				if (!("".equals(puntosSeleccionadosPagar) || puntosSeleccionadosPagar == null))
					cantidadpuntosSeleccionados = puntosSeleccionadosPagar.split(Constantes.SEPARADOR).length;

				puntosAModificarOAprobar = cantidadpuntosSeleccionados;
				dineroAPagar = cantidadpuntosSeleccionados * 50;

			}
		} catch (JbpmException e) {
			LOG.error("Error al inicializar el bean", e);
		}

	}
}
