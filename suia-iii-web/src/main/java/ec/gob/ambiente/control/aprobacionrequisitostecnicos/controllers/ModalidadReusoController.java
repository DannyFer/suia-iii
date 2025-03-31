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
import ec.gob.ambiente.control.aprobacionrequisitostecnicos.bean.ModalidadReusoBean;
import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.ModalidadReusoFacade;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.TipoManejoDesechos;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.DesechoModalidadReuso;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.ModalidadReuso;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.TipoManejoDesechosModalidadReuso;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.reportes.UtilDocumento;
import ec.gob.ambiente.suia.utils.JsfUtil;

/**
 * <b> Controlador de la pagina modalidad reuso. </b>
 * 
 * @author vero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: vero $, $Date: 10/06/2015 $]
 *          </p>
 */
@ManagedBean
@ViewScoped
public class ModalidadReusoController {

	private static final Logger LOG = Logger.getLogger(ModalidadReusoController.class);

	@Setter
	@Getter
	@ManagedProperty(value = "#{modalidadReusoBean}")
	private ModalidadReusoBean modalidadReusoBean;

	@EJB
	private ModalidadReusoFacade modalidadReusoFacade;

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
			modalidadReusoBean.setModalidadReuso(modalidadReusoFacade
					.getModalidadReuso(aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos()));

			if (modalidadReusoBean.getModalidadReuso() == null) {
				modalidadReusoBean.setModalidadReuso(new ModalidadReuso());
				modalidadReusoBean.getModalidadReuso().setAprobacionRequisitosTecnicos(
						aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos());
				modalidadReusoBean.getModalidadReuso().setTiposManejoDesechosModalidadReuso(
						new ArrayList<TipoManejoDesechosModalidadReuso>());
				obtenerDesechosAsociados();
				iniciarDocumentos();
			} else {
				obtenerSiExistenNuevosDesechosAsociados();
			}
			cargarTipoManejoDesechos();
			modalidadReusoBean.setListaTipoManejoDesechosReuso(modalidadReusoFacade.getTiposTratamientoDesechoReuso());
			aprobacionRequisitosTecnicosBean.verART(ModalidadReuso.class.getName());
		} catch (ServiceException e) {
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos modalidad reuso.");
		}
	}

	private void obtenerDesechosAsociados() throws ServiceException {
		modalidadReusoBean.getModalidadReuso().setListaDesechos(
				modalidadReusoFacade.getDesechosReuso(aprobacionRequisitosTecnicosBean
						.getAprobacionRequisitosTecnicos()));
		if (modalidadReusoBean.getModalidadReuso().getListaDesechos().isEmpty()) {
			RequestContext context = RequestContext.getCurrentInstance();
			context.execute("PF('importanteWdgt').show();");
		}
	}

	private void obtenerSiExistenNuevosDesechosAsociados() throws ServiceException {
		List<DesechoModalidadReuso> listaDesechosElimandosConLaModalidadAlmacenadosEstan = modalidadReusoBean
				.getModalidadReuso().getListaDesechos();
		List<DesechoModalidadReuso> listaDesechosEliminadosConLaModalidadDeTipoEliminacionDeben = modalidadReusoFacade
				.getDesechosReuso(aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos());

		List<DesechoModalidadReuso> agregar = new ArrayList<DesechoModalidadReuso>();

		for (DesechoModalidadReuso desechoModalidadAgregar : listaDesechosEliminadosConLaModalidadDeTipoEliminacionDeben) {
			if (!listaDesechosElimandosConLaModalidadAlmacenadosEstan.contains(desechoModalidadAgregar)) {
				agregar.add(desechoModalidadAgregar);
			}
		}

		List<DesechoModalidadReuso> quitar = new ArrayList<DesechoModalidadReuso>();

		for (DesechoModalidadReuso desechoModalidadEliminar : listaDesechosElimandosConLaModalidadAlmacenadosEstan) {
			if (!listaDesechosEliminadosConLaModalidadDeTipoEliminacionDeben.contains(desechoModalidadEliminar)) {
				quitar.add(desechoModalidadEliminar);
			}
		}

		modalidadReusoBean.getModalidadReuso().getListaDesechos().removeAll(quitar);
		modalidadReusoBean.getModalidadReuso().getListaDesechos().addAll(agregar);

	}

	private void iniciarDocumentos() {
		modalidadReusoBean.getModalidadReuso().setDocumentoPlano(new Documento());
		modalidadReusoBean.getModalidadReuso().setDocumentoRequisitos(new Documento());

	}

	private void cargarTipoManejoDesechos() {
		modalidadReusoBean.setTiposManejoDesechosSeleccionadas(new ArrayList<TipoManejoDesechos>());

		for (TipoManejoDesechosModalidadReuso tipoManejoReuso : modalidadReusoBean.getModalidadReuso()
				.getTiposManejoDesechosModalidadReuso()) {

			modalidadReusoBean.getTiposManejoDesechosSeleccionadas().add(tipoManejoReuso.getTipoManejoDesecho());
		}

	}

	public void guardarPagina() {
		try {
			modalidadReusoBean.setModalidadReuso(modalidadReusoFacade.guardar(modalidadReusoBean.getModalidadReuso(),
					modalidadReusoBean.getListaTipoManejoDesechosReuso(), bandejaTareasBean.getProcessId(),
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

	public void handleFileUpload(FileUploadEvent event) {
		Map<String, Object> att = event.getComponent().getAttributes();
		int indice = new Integer(att.get("indice").toString());
		file = event.getFile();
		switch (indice) {
		case 0:
			modalidadReusoBean.getModalidadReuso().setDocumentoPlano(
					UtilDocumento.generateDocumentZipRarFromUpload(file.getContents(), file.getFileName()));
			break;
		case 1:
			modalidadReusoBean.getModalidadReuso().setDocumentoRequisitos(
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
				UtilDocumento.descargarZipRar(
						modalidadReusoFacade.descargarFile(modalidadReusoBean.getModalidadReuso().getDocumentoPlano()),
						modalidadReusoBean.getModalidadReuso().getDocumentoPlano().getNombre());
				break;
			case 1:
				UtilDocumento.descargarZipRar(modalidadReusoFacade.descargarFile(modalidadReusoBean.getModalidadReuso()
						.getDocumentoRequisitos()), modalidadReusoBean.getModalidadReuso().getDocumentoRequisitos()
						.getNombre());
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

	public void seleccionarDesecho(DesechoModalidadReuso desechoModalidadReuso) {
		modalidadReusoBean.setDesechoModalidadReuso(desechoModalidadReuso);
	}

}
