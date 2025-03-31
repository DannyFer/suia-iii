/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.prevencion.registro.proyecto.bean;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import ec.gob.ambiente.suia.procesoadministrativo.facade.ProcesoAdministrativoFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author mayriliscs
 * @version Revision: 1.0
 *          <p>
 *          [Autor: mayriliscs, Fecha: 19/03/2015]
 *          </p>
 */
@ManagedBean
@ViewScoped
public class ProcesoAdministrativoBean implements Serializable {

	private static final long serialVersionUID = -7102030234380382746L;

	private static final Logger LOGGER = Logger.getLogger(ProcesoAdministrativoBean.class);

	@Setter
	private boolean mostrarTieneProcesoAdministrativo;

	@Getter
	@Setter
	private int cantidadProcesosAdmUsuarioAutenticado;

	@EJB
	private ProcesoAdministrativoFacade procesoAdministrativoFacade;

	public boolean isMostrarTieneProcesoAdministrativo() {
		if (mostrarTieneProcesoAdministrativo)
			JsfUtil.addCallbackParam("mostrarTieneProcesoAdministrativo");
		return mostrarTieneProcesoAdministrativo;
	}

	@PostConstruct
	public void init() {
		cantidadProcesosAdmUsuarioAutenticado = 0;
		//Persona usuarioAutenticado = JsfUtil.getBean(LoginBean.class).getUsuario().getPersona();
		try {
			/*cantidadProcesosAdmUsuarioAutenticado = procesoAdministrativoFacade
					.cantidadProcesoAdministrativosPorPersona(usuarioAutenticado);*/
			cantidadProcesosAdmUsuarioAutenticado = procesoAdministrativoFacade
					.cantidadProcesoAdministrativosPorPersona(JsfUtil.getLoggedUser().getNombre());
		} catch (Exception exception) {
			LOGGER.error(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION, exception);
		}
		mostrarTieneProcesoAdministrativo = cantidadProcesosAdmUsuarioAutenticado == 0 ? false : true;

	}
}
