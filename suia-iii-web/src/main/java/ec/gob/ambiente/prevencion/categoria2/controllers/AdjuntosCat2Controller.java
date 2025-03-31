/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.prevencion.categoria2.controllers;

import java.io.Serializable;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import lombok.Getter;
import lombok.Setter;

import org.jbpm.process.audit.ProcessInstanceLog;
import org.primefaces.event.FileUploadEvent;

import ec.gob.ambiente.prevencion.categoria2.bean.AdjuntosCat2Bean;
import ec.gob.ambiente.prevencion.categoria2.bean.FichaAmbientalPmaBean;
import ec.gob.ambiente.suia.administracion.facade.AdjuntosRegistroFichaAmbientalFacade;
import ec.gob.ambiente.suia.domain.EiaOpciones;
import ec.gob.ambiente.suia.domain.FichaAmbientalPma;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;

/**
 * 
 * @author jonathan
 */
@ManagedBean
@ViewScoped
public class AdjuntosCat2Controller implements Serializable {

	private static final long serialVersionUID = 2635668009553173905L;

	@EJB
	private AdjuntosRegistroFichaAmbientalFacade adjuntosRegistroFichaAmbientalFacade;

	@EJB
	private ProcesoFacade procesoFacade;

	@Getter
	@Setter
	@ManagedProperty(value = "#{adjuntosCat2Bean}")
	private AdjuntosCat2Bean adjuntosCat2Bean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{fichaAmbientalPmaBean}")
	private FichaAmbientalPmaBean fichaAmbientalPmaBean;

	@Getter
	@Setter
	private Integer codigoParametro;

	private String codigoOpcion;

	@Getter
	@Setter
	private boolean subioArchivo;

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(AdjuntosCat2Controller.class);

	private static final int RESUMEN_EJECUTIVO = 6;

	@SuppressWarnings("rawtypes")
	@PostConstruct
	private void cargarDatos() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		Map params = externalContext.getRequestParameterMap();
		String idAdjunto = (String) params.get("id");
		if (idAdjunto != null) {
			setCodigoParametro(new Integer(idAdjunto));
			setAdjuntosCat2Bean(new AdjuntosCat2Bean());
			getAdjuntosCat2Bean().iniciarDatos();
			cargarNombrePanel();
		}
	}

	private void cargarDatosAux() {
		setAdjuntosCat2Bean(new AdjuntosCat2Bean());
		getAdjuntosCat2Bean().iniciarDatos();
		cargarNombrePanel();
	}

	private void cargarNombrePanel() {
		if (getCodigoParametro().equals(RESUMEN_EJECUTIVO)) {
			getAdjuntosCat2Bean().setNombrePanel("Resumen ejecutivo");
			this.codigoOpcion = EiaOpciones.RESUMEN_EJECUTIVO_HIDRO;
		}
	}

	public void handleFileUpload(FileUploadEvent event) {
		getAdjuntosCat2Bean().getEntityAdjunto().setArchivo(
				event.getFile().getContents());
		getAdjuntosCat2Bean().getEntityAdjunto().setExtension(
				JsfUtil.devuelveExtension(event.getFile().getFileName()));
		getAdjuntosCat2Bean().getEntityAdjunto().setMimeType(
				event.getFile().getContentType());
		adjuntarResumenEjecutivo();
		LOG.info(event.getFile().getFileName() + " is uploaded.");
	}

	public void adjuntarResumenEjecutivo() {
		try {
			// TODO SE NECESITA VALORES DINÁMICOS PARA LA FICHA AMBIENTAL
			fichaAmbientalPmaBean.getFicha().setProcessId(new Long(677));
			fichaAmbientalPmaBean.getFicha().setId(1);
			/**************************************************************/
			getAdjuntosCat2Bean().validarFormulario();
			ProcessInstanceLog processInstanceLog = procesoFacade
					.getProcessInstanceLog(fichaAmbientalPmaBean.getProyectosBean()
							.getProyecto().getUsuario(), fichaAmbientalPmaBean.getFicha()
							.getProcessId());
			FichaAmbientalPma fichaAmbientalPma = fichaAmbientalPmaBean
					.getFicha();
			StringBuilder nombreArchvivo = new StringBuilder();
			nombreArchvivo.append("RFA");
			nombreArchvivo.append(getAdjuntosCat2Bean().getNombrePanel());
			nombreArchvivo.append(fichaAmbientalPma.getId());
			nombreArchvivo.append(".").append(
					getAdjuntosCat2Bean().getEntityAdjunto().getExtension());
			getAdjuntosCat2Bean().getEntityAdjunto().setNombre(
					nombreArchvivo.toString());
			adjuntosRegistroFichaAmbientalFacade.guardarAdjunto(
					getAdjuntosCat2Bean().getEntityAdjunto(),
					FichaAmbientalPma.class.getSimpleName(),
					fichaAmbientalPma.getId(),
					fichaAmbientalPmaBean.getFicha(), fichaAmbientalPmaBean
							.getProyectosBean().getProyecto().getCodigo(),
					processInstanceLog.getProcessName(),
					fichaAmbientalPma.getProcessId());
			setAdjuntosCat2Bean(null);
			cargarDatosAux();
			JsfUtil.addMessageInfo(JsfUtil.REGISTRO_ACTUALIZADO);
		} catch (Exception e) {
			JsfUtil.addMessageError(JsfUtil.ERROR_ACTUALIZAR_REGISTRO + " "
					+ e.getMessage());
			LOG.error(e , e);
			e.printStackTrace();
		}
	}
}
