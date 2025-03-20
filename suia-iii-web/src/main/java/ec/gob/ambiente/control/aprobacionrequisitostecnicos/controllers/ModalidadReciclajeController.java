/*
 * Copyright 2015 MAGMASOFT
 * Todos los derechos reservados
 */
package ec.gob.ambiente.control.aprobacionrequisitostecnicos.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import ec.gob.ambiente.control.aprobacionrequisitostecnicos.bean.AprobacionRequisitosTecnicosBean;
import ec.gob.ambiente.control.aprobacionrequisitostecnicos.bean.ModalidadReciclajeBean;
import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.ModalidadReciclajeFacade;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.TipoManejoDesechos;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.DesechoModalidadReciclaje;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.ModalidadReciclaje;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.TipoManejoDesechosModalidadReciclaje;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.reportes.UtilDocumento;
import ec.gob.ambiente.suia.utils.JsfUtil;

/**
 * <b> Controlador de la pagina modalidad reciclaje. </b>
 * 
 * @author vero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: vero $, $Date: 10/06/2015 $]
 *          </p>
 */
@ManagedBean
@ViewScoped
public class ModalidadReciclajeController {

	private static final Logger LOG = Logger.getLogger(ModalidadReciclajeController.class);

	@Setter
	@Getter
	@ManagedProperty(value = "#{modalidadReciclajeBean}")
	private ModalidadReciclajeBean modalidadReciclajeBean;

	@EJB
	private ModalidadReciclajeFacade modalidadReciclajeFacade;

	@Setter
	@Getter
	private UploadedFile file;

	@Setter
	@Getter
	@ManagedProperty(value = "#{aprobacionRequisitosTecnicosBean}")
	private AprobacionRequisitosTecnicosBean aprobacionRequisitosTecnicosBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;

	@PostConstruct
	public void init() {
		try {
			obtenerModalidad();
			cargarTipoManejoDesechos();

		} catch (ServiceException e) {
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos modalidad reciclaje.");
		}
		modalidadReciclajeBean.setListaTipoManejoDesechosReciclaje(modalidadReciclajeFacade
				.getTiposTratamientoDesechoReciclaje());
		aprobacionRequisitosTecnicosBean.verART(ModalidadReciclaje.class.getName());

	}

	private void obtenerDesechosAsociados() throws ServiceException {
		modalidadReciclajeBean.getModalidadReciclaje().setListaDesechos(
				modalidadReciclajeFacade.getDesechosReciclaje(aprobacionRequisitosTecnicosBean
						.getAprobacionRequisitosTecnicos()));
		if (modalidadReciclajeBean.getModalidadReciclaje().getListaDesechos().isEmpty()) {
			RequestContext context = RequestContext.getCurrentInstance();
			context.execute("PF('importanteWdgt').show();");
		}
	}

	private void obtenerSiExistenNuevosDesechosAsociados() throws ServiceException {
		List<DesechoModalidadReciclaje> listaDesechosElimandosConLaModalidadAlmacenadosEstan = modalidadReciclajeBean
				.getModalidadReciclaje().getListaDesechos();
		List<DesechoModalidadReciclaje> listaDesechosEliminadosConLaModalidadDeTipoEliminacionDeben = modalidadReciclajeFacade
				.getDesechosReciclaje(aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos());

		List<DesechoModalidadReciclaje> agregar = new ArrayList<DesechoModalidadReciclaje>();

		for (DesechoModalidadReciclaje desechoModalidadReciclajeEliminar : listaDesechosEliminadosConLaModalidadDeTipoEliminacionDeben) {
			if (!listaDesechosElimandosConLaModalidadAlmacenadosEstan.contains(desechoModalidadReciclajeEliminar)) {
				agregar.add(desechoModalidadReciclajeEliminar);
			}
		}

		List<DesechoModalidadReciclaje> quitar = new ArrayList<DesechoModalidadReciclaje>();

		for (DesechoModalidadReciclaje desechoModalidadReciclajeEliminar : listaDesechosElimandosConLaModalidadAlmacenadosEstan) {
			if (!listaDesechosEliminadosConLaModalidadDeTipoEliminacionDeben
					.contains(desechoModalidadReciclajeEliminar)) {
				quitar.add(desechoModalidadReciclajeEliminar);
			}
		}

		modalidadReciclajeBean.getModalidadReciclaje().getListaDesechos().removeAll(quitar);
		modalidadReciclajeBean.getModalidadReciclaje().getListaDesechos().addAll(agregar);
	}

	private void cargarTipoManejoDesechos() {
		modalidadReciclajeBean.setTiposManejoDesechosSeleccionadas(new ArrayList<TipoManejoDesechos>());

		for (TipoManejoDesechosModalidadReciclaje tipoManejoReciclaje : modalidadReciclajeBean.getModalidadReciclaje()
				.getTiposManejoDesechosModalidadReciclaje()) {

			modalidadReciclajeBean.getTiposManejoDesechosSeleccionadas()
					.add(tipoManejoReciclaje.getTipoManejoDesecho());
		}

	}

	private void obtenerModalidad() throws ServiceException {
		modalidadReciclajeBean.setModalidadReciclaje(modalidadReciclajeFacade
				.getModalidadReciclaje(aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos()));
		if (modalidadReciclajeBean.getModalidadReciclaje() == null) {
			modalidadReciclajeBean.setModalidadReciclaje(new ModalidadReciclaje());
			modalidadReciclajeBean.getModalidadReciclaje().setAprobacionRequisitosTecnicos(
					aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos());
			modalidadReciclajeBean.getModalidadReciclaje().setTiposManejoDesechosModalidadReciclaje(
					new ArrayList<TipoManejoDesechosModalidadReciclaje>());
			iniciarDocumentos();
			obtenerDesechosAsociados();
		} else {
			obtenerSiExistenNuevosDesechosAsociados();
		}

	}

	private void iniciarDocumentos() {
		modalidadReciclajeBean.getModalidadReciclaje().setDocumentoPlano(new Documento());
		modalidadReciclajeBean.getModalidadReciclaje().setDocumentoRequisitos(new Documento());

	}

	public void handleFileUpload(FileUploadEvent event) {
		Map<String, Object> att = event.getComponent().getAttributes();
		int indice = new Integer(att.get("indice").toString());
		file = event.getFile();
		switch (indice) {
		case 0:
			modalidadReciclajeBean.getModalidadReciclaje().setDocumentoPlano(
					UtilDocumento.generateDocumentZipRarFromUpload(file.getContents(), file.getFileName()));
			break;
		case 1:
			modalidadReciclajeBean.getModalidadReciclaje().setDocumentoRequisitos(
					UtilDocumento.generateDocumentZipRarFromUpload(file.getContents(), file.getFileName()));
			break;
		default:
			LOG.error("Indice de archivo adjunto no esperado");
			JsfUtil.addMessageError("Ocurrió un error al tratar de subir el archivo, por favor comunicarse con mesa de ayuda.");
			break;
		}
	}

	public void descargar(int indice) {
		try {
			switch (indice) {
			case 0:
				UtilDocumento.descargarZipRar(modalidadReciclajeFacade.descargarFile(modalidadReciclajeBean
						.getModalidadReciclaje().getDocumentoPlano()), modalidadReciclajeBean.getModalidadReciclaje()
						.getDocumentoPlano().getNombre());
				break;
			case 1:
				UtilDocumento.descargarZipRar(modalidadReciclajeFacade.descargarFile(modalidadReciclajeBean
						.getModalidadReciclaje().getDocumentoRequisitos()), modalidadReciclajeBean
						.getModalidadReciclaje().getDocumentoRequisitos().getNombre());
				break;
			default:
				LOG.error("Indice de archivo adjunto no esperado para descargar el adjunto");
				JsfUtil.addMessageError("Ocurrió un error al tratar de descargar el archivo, por favor comunicarse con mesa de ayuda.");
				break;

			}
		} catch (Exception e) {
			LOG.error("error al descargar ", e);
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
	}

	public void guardarPagina() {
		try {
			modalidadReciclajeBean.setModalidadReciclaje(modalidadReciclajeFacade.guardar(
					modalidadReciclajeBean.getModalidadReciclaje(),
					modalidadReciclajeBean.getTiposManejoDesechosSeleccionadas(), bandejaTareasBean.getProcessId(),
					bandejaTareasBean.getTarea().getTaskId()));

			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
		} catch (ServiceException e) {
			JsfUtil.addMessageError("Ocucció un error al guardar los datos.");
			LOG.error(e, e);
		} catch (CmisAlfrescoException e) {
			JsfUtil.addMessageError("Ocucció un error con el alfresco al guardar los documentos.");
			LOG.error(e, e);
		}
	}

	public void seleccionarDesecho(DesechoModalidadReciclaje desechoModalidadReciclaje) {
		modalidadReciclajeBean.setDesechoModalidadReciclaje(desechoModalidadReciclaje);
	}

}
