package ec.gob.ambiente.control.denuncias.controllers;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import ec.gob.ambiente.control.denuncias.bean.RemitirDenunciaBean;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.denuncia.facade.DenunciaFacade;
import ec.gob.ambiente.suia.domain.Denuncia;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.utils.JsfUtil;
import java.io.Serializable;

@ManagedBean
@RequestScoped
public class RemitirDenunciaController implements Serializable{
    
    private static final long serialVersionUID = -3526381287113629686L;
	@Getter
	@Setter
	@ManagedProperty(value = "#{remitirDenunciaBean}")
	private RemitirDenunciaBean remitirDenunciaBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;

	@EJB
	private DenunciaFacade denunciaFacade;

	private static final Logger LOG = Logger
			.getLogger(RemitirDenunciaController.class);

	@PostConstruct
	public void init() {
		remitirDenunciaBean.setListaDireccionesProvinciales(denunciaFacade
				.getDireccionesProvinciales());

		remitirDenunciaBean.setListaEntesAcreditados(denunciaFacade
				.getEntesAcreditados());

		try {

			Denuncia de = denunciaFacade.getDenuncia(bandejaTareasBean
					.getTarea().getTaskId(), loginBean.getUsuario());
			remitirDenunciaBean.setDenuncia(de);

		} catch (Exception e) {
			LOG.error("Error al recuperar la denuncia a revisar");
			JsfUtil.addMessageError("Ocurrio un error al recuperar la denuncia");
		}
		definirDPA();
	}

	public String enviar() {

		try {
			Map<String, Object> parametros = new HashMap<String, Object>();

			parametros.put("remitirGad_", remitirDenunciaBean.getEnviarGads());
			if (remitirDenunciaBean.getRemitirDenuncia().getResponsable() != null)
				parametros.put("codigoGad_", remitirDenunciaBean
						.getRemitirDenuncia().getResponsable().getId());

			denunciaFacade.completarTareaRemitirDenuncia(
					remitirDenunciaBean.getRemitirDenuncia(),
					remitirDenunciaBean.getDenuncia(), parametros,
					bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean
					.getProcessId(),
					loginBean.getUsuario());
			JsfUtil.addMessageInfo("Tarea completada");
			return JsfUtil.actionNavigateTo("../../bandeja/bandejaTareas.jsf");
		} catch (JbpmException e) {
			LOG.error("Error al completar la tarea");
			JsfUtil.addMessageError("Ocurrio un error al completar la tarea");
			return "#";
		}

	}

	public void actualizarSeleccion() {
		remitirDenunciaBean.setEnviarDP(remitirDenunciaBean.getOpcion().equals(
				"dp") ? true : false);
		remitirDenunciaBean.setEnviarPC(remitirDenunciaBean.getOpcion().equals(
				"pc") ? true : false);
		remitirDenunciaBean.setEnviarGads(remitirDenunciaBean.getOpcion()
				.equals("gad") ? true : false);
		if (remitirDenunciaBean.getEnviarPC())
			remitirDenunciaBean.getRemitirDenuncia().setPlantaCentral(true);
	}

	private void definirDPA() {
		/*
		 * UbicacionesGeografica ubicacion = remitirDenunciaBean.getDenuncia()
		 * .getUbicacion(); if (ubicacion != null) { if
		 * (denunciaFacade.isProvincia(ubicacion.getCodificacionInec()))
		 * remitirDenunciaBean.getDenuncia().setProvincia(ubicacion); else if
		 * (denunciaFacade.isCanton(ubicacion.getCodificacionInec())) {
		 * remitirDenunciaBean.getDenuncia().setCanton(ubicacion);
		 * remitirDenunciaBean .getDenuncia() .setProvincia( denunciaFacade
		 * .buscarUbicacionGeograficaSuperior(remitirDenunciaBean
		 * .getDenuncia().getUbicacion() .getCodificacionInec())); } else { //
		 * remitirDenunciaBean.getDenuncia().setParroquia(ubicacion);
		 * 
		 * ubicacion = denunciaFacade
		 * .buscarUbicacionGeograficaSuperior(remitirDenunciaBean
		 * .getDenuncia().getUbicacion() .getCodificacionInec()); //
		 * remitirDenunciaBean.getDenuncia().setCanton(ubicacion); ubicacion =
		 * denunciaFacade .buscarUbicacionGeograficaSuperior(remitirDenunciaBean
		 * .getDenuncia().getUbicacion() .getCodificacionInec()); //
		 * remitirDenunciaBean.getDenuncia().setProvincia(ubicacion);
		 * 
		 * } }
		 */

	}
}
