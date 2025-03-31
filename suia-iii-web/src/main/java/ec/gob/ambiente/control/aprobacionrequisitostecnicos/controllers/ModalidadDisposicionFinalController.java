/*
 * Copyright 2015 MAGMASOFT
 * Todos los derechos reservados
 */
package ec.gob.ambiente.control.aprobacionrequisitostecnicos.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
import ec.gob.ambiente.control.aprobacionrequisitostecnicos.bean.ModalidadDisposicionFinalBean;
import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.ModalidadDisposicionFinalFacade;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.DetalleCapacidadConfinamientoDesechoPeligroso;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.DetalleCapacidadTotalAlmacenamientoMateriaPrimaDesechoPeligroso;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.ModalidadDisposicionFinal;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.reportes.UtilDocumento;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.validacionseccion.facade.ValidacionSeccionesFacade;

/* @version $Revision: 1.0 $
 *          <p>
 *          [$Author: Jonathan Guerrero $, $Date: 22/06/2015 $]
 *          </p>
 */
@ManagedBean
@ViewScoped
public class ModalidadDisposicionFinalController {

	private static final Logger LOG = Logger.getLogger(ModalidadDisposicionFinalController.class);

	@Setter
	@Getter
	@ManagedProperty(value = "#{modalidadDisposicionFinalBean}")
	private ModalidadDisposicionFinalBean modalidadDisposicionFinalBean;

	@Setter
	@Getter
	@ManagedProperty(value = "#{aprobacionRequisitosTecnicosBean}")
	private AprobacionRequisitosTecnicosBean aprobacionRequisitosTecnicosBean;

	@EJB
	private ModalidadDisposicionFinalFacade modalidadDisposicionFinalFacade;

	@EJB
	private ValidacionSeccionesFacade validacionSeccionesFacade;

	@Setter
	@Getter
	private UploadedFile file;

	@Setter
	@Getter
	private boolean habilitarTablaConfinamiento;

	@Setter
	@Getter
	private boolean habilitarTablaAlmacenamiento;

	@Setter
	@Getter
	private boolean habilitarBtnGuardar;

	@Setter
	@Getter
	private boolean habilitarBtnActualizar;

	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;

	@PostConstruct
	public void init() {
		aprobacionRequisitosTecnicosBean.verART(ModalidadDisposicionFinal.class.getName());

		try {
			modalidadDisposicionFinalBean.setModalidadDisposicionFinal(modalidadDisposicionFinalFacade
					.getModalidadDisposicionFinal(aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos()));
			obtenerDesechosAsociados();
		} catch (ServiceException e) {
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos modalidad disposición final.");
		}
		modalidadDisposicionFinalBean
				.setDetalleCapacidadConfinamientoDesechoPeligroso(new DetalleCapacidadConfinamientoDesechoPeligroso());
		modalidadDisposicionFinalBean
				.setDetalleCapacidadTotalAlmacenamientoMateriaPrimaDesechoPeligroso(new DetalleCapacidadTotalAlmacenamientoMateriaPrimaDesechoPeligroso());
		if (modalidadDisposicionFinalBean.getModalidadDisposicionFinal() == null) {
			modalidadDisposicionFinalBean.setModalidadDisposicionFinal(new ModalidadDisposicionFinal());
			modalidadDisposicionFinalBean
					.setDetalleCapacidadConfinamientoDesechoPeligrosos(new ArrayList<DetalleCapacidadConfinamientoDesechoPeligroso>());
			modalidadDisposicionFinalBean
					.setDetalleCapacidadTotalAlmacenamientoMateriaPrimaDesechoPeligrosos(new ArrayList<DetalleCapacidadTotalAlmacenamientoMateriaPrimaDesechoPeligroso>());
		} else {
			cargarNombreDocumentos();
			try {
				modalidadDisposicionFinalBean
						.setDetalleCapacidadConfinamientoDesechoPeligrosos(modalidadDisposicionFinalFacade
								.obtenerDetalleCapacidadConfinamientoDPXModalidadDispFin(modalidadDisposicionFinalBean
										.getModalidadDisposicionFinal().getId()));
				if (modalidadDisposicionFinalBean.getDetalleCapacidadConfinamientoDesechoPeligrosos() == null) {
					modalidadDisposicionFinalBean
							.setDetalleCapacidadConfinamientoDesechoPeligrosos(new ArrayList<DetalleCapacidadConfinamientoDesechoPeligroso>());
				} else {
					habilitarTablaConfinamiento = true;
				}
			} catch (ServiceException e) {
				LOG.error(e, e);
				JsfUtil.addMessageError("Ocurrió un error al recuperar los datos de detalle capacidad confinamiento de desechos peligrosos.");
			}
			try {
				modalidadDisposicionFinalBean
						.setDetalleCapacidadTotalAlmacenamientoMateriaPrimaDesechoPeligrosos(modalidadDisposicionFinalFacade
								.obtenerDetalleCapacidadTotalAlmacenamientoMateriaPrimaDesechoPeligrosoXModalidadDispFin(modalidadDisposicionFinalBean
										.getModalidadDisposicionFinal().getId()));
				if (modalidadDisposicionFinalBean.getDetalleCapacidadTotalAlmacenamientoMateriaPrimaDesechoPeligrosos() == null) {
					modalidadDisposicionFinalBean
							.setDetalleCapacidadTotalAlmacenamientoMateriaPrimaDesechoPeligrosos(new ArrayList<DetalleCapacidadTotalAlmacenamientoMateriaPrimaDesechoPeligroso>());
				} else {
					habilitarTablaAlmacenamiento = true;
				}
			} catch (ServiceException e) {
				LOG.error(e, e);
				JsfUtil.addMessageError("Ocurrió un error al recuperar los datos de detalle capacidad total almacenamiento materia prima desechos peligrosos.");
			}
		}
	}

	private void obtenerDesechosAsociados() throws ServiceException {
		modalidadDisposicionFinalBean.setListaDesechos(modalidadDisposicionFinalFacade
				.getDesechosDisposicionFinal(aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos()));
		if (modalidadDisposicionFinalBean.getListaDesechos().isEmpty()) {
			RequestContext context = RequestContext.getCurrentInstance();
			context.execute("PF('importanteWdgt').show();");
		}
	}

	private void cargarNombreDocumentos() {
		modalidadDisposicionFinalBean.setNombreFilePlano(modalidadDisposicionFinalBean.getModalidadDisposicionFinal()
				.getPlanoPlanta().getNombre());
		modalidadDisposicionFinalBean.setNombreFileRequisitos(modalidadDisposicionFinalBean
				.getModalidadDisposicionFinal().getRequisitos().getNombre());
		modalidadDisposicionFinalBean.setNombreFileAnexos(modalidadDisposicionFinalBean.getModalidadDisposicionFinal()
				.getAnexos().getNombre());
	}

	public void fileUploadListenerPlano(FileUploadEvent event) {
		file = event.getFile();
		modalidadDisposicionFinalBean.getModalidadDisposicionFinal().setPlanoPlanta(
				UtilDocumento.generateDocumentZipRarFromUpload(file.getContents(), file.getFileName()));
		modalidadDisposicionFinalBean.setNombreFilePlano(modalidadDisposicionFinalBean.getModalidadDisposicionFinal()
				.getPlanoPlanta().getNombre());
	}

	public void fileUploadListenerRequisitos(FileUploadEvent event) {
		file = event.getFile();
		modalidadDisposicionFinalBean.getModalidadDisposicionFinal().setRequisitos(
				UtilDocumento.generateDocumentZipRarFromUpload(file.getContents(), file.getFileName()));
		modalidadDisposicionFinalBean.setNombreFileRequisitos(modalidadDisposicionFinalBean
				.getModalidadDisposicionFinal().getRequisitos().getNombre());
	}

	public void fileUploadListenerAnexos(FileUploadEvent event) {
		file = event.getFile();
		modalidadDisposicionFinalBean.getModalidadDisposicionFinal().setAnexos(
				UtilDocumento.generateDocumentZipRarFromUpload(file.getContents(), file.getFileName()));
		modalidadDisposicionFinalBean.setNombreFileAnexos(modalidadDisposicionFinalBean.getModalidadDisposicionFinal()
				.getAnexos().getNombre());
	}

	public void descargarPlano() {
		if (modalidadDisposicionFinalBean.getModalidadDisposicionFinal().getPlanoPlanta().getId() != null) {
			try {
				UtilDocumento.descargarZipRar(modalidadDisposicionFinalFacade
						.descargarFile(modalidadDisposicionFinalBean.getModalidadDisposicionFinal().getPlanoPlanta()),
						modalidadDisposicionFinalBean.getModalidadDisposicionFinal().getPlanoPlanta().getNombre());

			} catch (Exception e) {
				LOG.error("error al descargar ", e);
				JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
			}
		}
	}

	public void descargarRequisitos() {
		if (modalidadDisposicionFinalBean.getModalidadDisposicionFinal().getRequisitos().getId() != null) {
			try {
				UtilDocumento.descargarZipRar(modalidadDisposicionFinalFacade
						.descargarFile(modalidadDisposicionFinalBean.getModalidadDisposicionFinal().getRequisitos()),
						modalidadDisposicionFinalBean.getModalidadDisposicionFinal().getRequisitos().getNombre());

			} catch (Exception e) {
				LOG.error("error al descargar ", e);
				JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
			}
		}
	}

	public void descargarAnexos() {
		if (modalidadDisposicionFinalBean.getModalidadDisposicionFinal().getAnexos().getId() != null) {
			try {
				UtilDocumento.descargarZipRar(modalidadDisposicionFinalFacade
						.descargarFile(modalidadDisposicionFinalBean.getModalidadDisposicionFinal().getAnexos()),
						modalidadDisposicionFinalBean.getModalidadDisposicionFinal().getAnexos().getNombre());

			} catch (Exception e) {
				LOG.error("error al descargar ", e);
				JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
			}
		}
	}

	public void validarModalidadDisposicionFinal() {
		List<String> mensajesError = new ArrayList<String>();
		StringBuilder functionJs = new StringBuilder();
		if (modalidadDisposicionFinalBean.getNombreFilePlano() == null
				|| modalidadDisposicionFinalBean.getNombreFilePlano().isEmpty()
				|| modalidadDisposicionFinalBean.getNombreFilePlano().equals("")) {
			mensajesError
					.add("El campo 'Plano de la Planta o del área donde se realiza la Disposición final' es requerido.");
			functionJs.append("highlightComponent('form:lblDesechos');");
		} else {
			functionJs.append("removeHighLightComponent('form:lblDesechos');");
		}
		if (!modalidadDisposicionFinalBean.getModalidadDisposicionFinal().isTransporteContratado()
				&& !modalidadDisposicionFinalBean.getModalidadDisposicionFinal().isTransportePropio()) {
			mensajesError.add("El campo 'Indicar el Tipo(s) de transporte(s) utilizado(s)' es requerido.");
			functionJs.append("highlightComponent('form:tipoTransporte');");
		} else {
			functionJs.append("removeHighLightComponent('form:tipoTransporte');");
		}
		if (modalidadDisposicionFinalBean.getModalidadDisposicionFinal().getNombreEmpresaAutorizada() == null
				|| modalidadDisposicionFinalBean.getModalidadDisposicionFinal().getNombreEmpresaAutorizada().isEmpty()
				|| modalidadDisposicionFinalBean.getModalidadDisposicionFinal().getNombreEmpresaAutorizada().equals("")) {
			if (modalidadDisposicionFinalBean.getModalidadDisposicionFinal().isTransporteContratado())
				mensajesError.add("El campo 'Nombre de empresa autorizada' es requerido.");
			functionJs.append("highlightComponent('form:nombreEmpresa');");
		} else {
			functionJs.append("removeHighLightComponent('form:nombreEmpresa');");
		}
		if (!habilitarTablaConfinamiento) {
			mensajesError.add("El campo 'Detallar Capacidad estimada del confinamiento' es requerido.");
			functionJs.append("highlightComponent('form:pngridCapConf');");
		} else {
			functionJs.append("removeHighLightComponent('form:pngridCapConf');");
		}
		if (!habilitarTablaAlmacenamiento) {
			mensajesError.add("El campo 'Detallar Capacidad Total de Almacenamiento de Materias Primas' es requerido.");
			functionJs.append("highlightComponent('form:pngridCTDP');");
		} else {
			functionJs.append("removeHighLightComponent('form:pngridCTDP');");
		}

		if (modalidadDisposicionFinalBean.getNombreFileRequisitos() == null
				|| modalidadDisposicionFinalBean.getNombreFileRequisitos().isEmpty()
				|| modalidadDisposicionFinalBean.getNombreFileRequisitos().equals("")) {
			mensajesError.add("El campo 'Presentar la descripción de los siguientes requisitos' es requerido.");
			functionJs.append("highlightComponent('form:lblRequisitos');");
		} else {
			functionJs.append("removeHighLightComponent('form:lblRequisitos');");
		}
		if (modalidadDisposicionFinalBean.getNombreFileAnexos() == null
				|| modalidadDisposicionFinalBean.getNombreFileAnexos().isEmpty()
				|| modalidadDisposicionFinalBean.getNombreFileAnexos().equals("")) {
			mensajesError.add("El campo 'Estudios de Vulnerabilidad del Sitio' es requerido.");
			functionJs.append("highlightComponent('form:lblAnexos');");
		} else {
			functionJs.append("removeHighLightComponent('form:lblAnexos');");
		}
		RequestContext.getCurrentInstance().execute(functionJs.toString());
		if (mensajesError.isEmpty()) {
			RequestContext.getCurrentInstance().execute("PF('dlgNotaFinal').show()");
		} else {
			JsfUtil.addMessageError(mensajesError);
		}
	}

	public void guardarPagina() {
		try {
			modalidadDisposicionFinalBean.getModalidadDisposicionFinal().setAprobacionRequisitosTecnicos(
					aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos());
			ModalidadDisposicionFinal mdfr = modalidadDisposicionFinalFacade
					.getModalidadDisposicionFinal(aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos());
			if (mdfr != null) {
				modalidadDisposicionFinalFacade.guardar(modalidadDisposicionFinalBean.getModalidadDisposicionFinal(),
						bandejaTareasBean.getProcessId(), bandejaTareasBean.getTarea().getTaskId());
			} else {
				modalidadDisposicionFinalFacade.guardar(modalidadDisposicionFinalBean.getModalidadDisposicionFinal(),
						bandejaTareasBean.getProcessId(), bandejaTareasBean.getTarea().getTaskId());
			}

			for (DetalleCapacidadConfinamientoDesechoPeligroso dcc : modalidadDisposicionFinalBean
					.getDetalleCapacidadConfinamientoDesechoPeligrosos()) {
				dcc.setModalidadDisposicionFinal(modalidadDisposicionFinalBean.getModalidadDisposicionFinal());
				modalidadDisposicionFinalFacade.guardarDetalleCapacidadConfinamientoDesechoPeligroso(dcc);
			}
			for (DetalleCapacidadTotalAlmacenamientoMateriaPrimaDesechoPeligroso dct : modalidadDisposicionFinalBean
					.getDetalleCapacidadTotalAlmacenamientoMateriaPrimaDesechoPeligrosos()) {
				dct.setModalidadDisposicionFinal(modalidadDisposicionFinalBean.getModalidadDisposicionFinal());
				modalidadDisposicionFinalFacade
						.guardarDetalleCapacidadTotalAlmacenamientoMateriaPrimaDesechoPeligroso(dct);
			}

			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
		} catch (ServiceException e) {
			JsfUtil.addMessageError("Ocurrió un error al guardar los datos.");
			LOG.error(e, e);
		} catch (CmisAlfrescoException e) {
			JsfUtil.addMessageError("Ocurrió un error con el alfresco al guardar los documentos.");
			LOG.error(e, e);
		}
	}

	public void editarDetalleCapacidadConfinamientoDesechoPeligroso() {
		habilitarBtnGuardar = false;
		habilitarBtnActualizar = true;
		RequestContext.getCurrentInstance().execute("PF('dlgCapConf').show()");
	}

	public void actualizarDetalleCapacidadConfinamientoDesechoPeligroso() {

		for (int i = 0; i < modalidadDisposicionFinalBean.getDetalleCapacidadConfinamientoDesechoPeligrosos().size(); i++) {
			if (modalidadDisposicionFinalBean
					.getDetalleCapacidadConfinamientoDesechoPeligroso()
					.getId()
					.equals(modalidadDisposicionFinalBean.getDetalleCapacidadConfinamientoDesechoPeligrosos().get(i)
							.getId())) {
				modalidadDisposicionFinalBean.getDetalleCapacidadConfinamientoDesechoPeligrosos().set(i,
						modalidadDisposicionFinalBean.getDetalleCapacidadConfinamientoDesechoPeligroso());
				break;
			}
		}
		JsfUtil.addMessageInfo("Actualizó Detalle Capacidad Confinamiento Desecho Peligroso correctamente.");
		RequestContext.getCurrentInstance().execute("PF('dlgCapConf').hide()");

	}

	public void eliminarDetalleCapacidadConfinamientoDesechoPeligroso() {
		boolean removioDetalleCDP = modalidadDisposicionFinalBean.getDetalleCapacidadConfinamientoDesechoPeligrosos()
				.remove(modalidadDisposicionFinalBean.getDetalleCapacidadConfinamientoDesechoPeligroso());
		if (removioDetalleCDP) {
			JsfUtil.addMessageInfo("Se eliminó Detalle Capacidad Confinamiento Desecho Peligroso correctamente");
			RequestContext.getCurrentInstance().execute("PF('dlgEliminarDCC').hide()");
			modalidadDisposicionFinalBean
					.setDetalleCapacidadConfinamientoDesechoPeligroso(new DetalleCapacidadConfinamientoDesechoPeligroso());
		} else {
			JsfUtil.addMessageInfo("No se puede eliminar Detalle Capacidad Confinamiento Desecho Peligroso seleccionado.");
		}
		if (modalidadDisposicionFinalBean.getDetalleCapacidadConfinamientoDesechoPeligrosos().isEmpty()) {
			habilitarTablaConfinamiento = false;
		}
	}

	public void editarDetalleCapacidadTotalAlmacenamientoMateriasPrimas() {
		habilitarBtnGuardar = false;
		habilitarBtnActualizar = true;
		RequestContext.getCurrentInstance().execute("PF('dlgCapMatPr').show()");
	}

	public void actualizarDetalleCapacidadTotalAlmacenamientoMateriasPrimas() {

		modalidadDisposicionFinalBean.getDetalleCapacidadTotalAlmacenamientoMateriaPrimaDesechoPeligroso();
		for (int i = 0; i < modalidadDisposicionFinalBean
				.getDetalleCapacidadTotalAlmacenamientoMateriaPrimaDesechoPeligrosos().size(); i++) {
			if (modalidadDisposicionFinalBean
					.getDetalleCapacidadTotalAlmacenamientoMateriaPrimaDesechoPeligroso()
					.getId()
					.equals(modalidadDisposicionFinalBean
							.getDetalleCapacidadTotalAlmacenamientoMateriaPrimaDesechoPeligrosos().get(i).getId())) {
				modalidadDisposicionFinalBean.getDetalleCapacidadTotalAlmacenamientoMateriaPrimaDesechoPeligrosos()
						.set(i,
								modalidadDisposicionFinalBean
										.getDetalleCapacidadTotalAlmacenamientoMateriaPrimaDesechoPeligroso());
				break;
			}
		}

		JsfUtil.addMessageInfo("Actualizó Detalle Capacidad Total Almacenamiento Materias Primas correctamente");
		RequestContext.getCurrentInstance().execute("PF('dlgCapMatPr').hide()");

	}

	public void eliminarDetalleCapacidadTotalAlmacenamientoMateriasPrimas() {
		boolean removioDetalleCTMP = modalidadDisposicionFinalBean
				.getDetalleCapacidadTotalAlmacenamientoMateriaPrimaDesechoPeligrosos().remove(
						modalidadDisposicionFinalBean
								.getDetalleCapacidadTotalAlmacenamientoMateriaPrimaDesechoPeligroso());
		if (removioDetalleCTMP) {
			JsfUtil.addMessageInfo("Se eliminó Detalle Capacidad Total Almacenamiento Materias Primas correctamente");
			RequestContext.getCurrentInstance().execute("PF('dlgEliminarDCTMP').hide()");
			modalidadDisposicionFinalBean
					.setDetalleCapacidadTotalAlmacenamientoMateriaPrimaDesechoPeligroso(new DetalleCapacidadTotalAlmacenamientoMateriaPrimaDesechoPeligroso());
		} else {
			JsfUtil.addMessageInfo("No se puede eliminar Detalle Capacidad Total Almacenamiento Materias Primas seleccionado.");
		}
		if (modalidadDisposicionFinalBean.getDetalleCapacidadTotalAlmacenamientoMateriaPrimaDesechoPeligrosos()
				.isEmpty()) {
			habilitarTablaAlmacenamiento = false;
		}
	}

	public void listenerTipoTransporte() {
		modalidadDisposicionFinalBean.setHabilitarNombreEmpresaAutorizada(modalidadDisposicionFinalBean
				.getModalidadDisposicionFinal().isTransportePropio() ? modalidadDisposicionFinalBean
				.getModalidadDisposicionFinal().isTransportePropio() : modalidadDisposicionFinalBean
				.getModalidadDisposicionFinal().isTransporteContratado());
	}

	public void abrirDialogoDetalleCapacidadConfinamiento() {
		habilitarBtnGuardar = true;
		habilitarBtnActualizar = false;
		modalidadDisposicionFinalBean
				.setDetalleCapacidadConfinamientoDesechoPeligroso(new DetalleCapacidadConfinamientoDesechoPeligroso());
	}

	public void abrirDialogoDetalleCapacidadAlmacenamientoMateriasPrimas() {
		habilitarBtnGuardar = true;
		habilitarBtnActualizar = false;
		modalidadDisposicionFinalBean
				.setDetalleCapacidadTotalAlmacenamientoMateriaPrimaDesechoPeligroso(new DetalleCapacidadTotalAlmacenamientoMateriaPrimaDesechoPeligroso());
	}

	public void agregarConfinamiento() {

		modalidadDisposicionFinalBean.getDetalleCapacidadConfinamientoDesechoPeligrosos().add(
				modalidadDisposicionFinalBean.getDetalleCapacidadConfinamientoDesechoPeligroso());

		if (!modalidadDisposicionFinalBean.getDetalleCapacidadConfinamientoDesechoPeligrosos().isEmpty()) {
			habilitarTablaConfinamiento = true;
			RequestContext.getCurrentInstance().execute("PF('dlgCapConf').hide()");
		} else {
			habilitarTablaConfinamiento = false;
		}

	}

	public void agregarAlmacenamiento() {

		modalidadDisposicionFinalBean.getDetalleCapacidadTotalAlmacenamientoMateriaPrimaDesechoPeligrosos().add(
				modalidadDisposicionFinalBean.getDetalleCapacidadTotalAlmacenamientoMateriaPrimaDesechoPeligroso());

		if (!modalidadDisposicionFinalBean.getDetalleCapacidadTotalAlmacenamientoMateriaPrimaDesechoPeligrosos()
				.isEmpty()) {
			habilitarTablaAlmacenamiento = true;
			RequestContext.getCurrentInstance().execute("PF('dlgCapMatPr').hide()");
		} else {
			habilitarTablaAlmacenamiento = false;
		}
	}

	public void descargar(int indice) throws IOException {
		try {
			switch (indice) {
			case 0:
				UtilDocumento.descargarZipRar(modalidadDisposicionFinalFacade
						.descargarFile(modalidadDisposicionFinalBean.getModalidadDisposicionFinal().getPlanoPlanta()),
						modalidadDisposicionFinalBean.getModalidadDisposicionFinal().getPlanoPlanta().getNombre());
				break;
			case 1:
				UtilDocumento.descargarZipRar(modalidadDisposicionFinalFacade
						.descargarFile(modalidadDisposicionFinalBean.getModalidadDisposicionFinal().getRequisitos()),
						modalidadDisposicionFinalBean.getModalidadDisposicionFinal().getRequisitos().getNombre());
				break;
			case 2:
				UtilDocumento.descargarZipRar(modalidadDisposicionFinalFacade
						.descargarFile(modalidadDisposicionFinalBean.getModalidadDisposicionFinal().getAnexos()),
						modalidadDisposicionFinalBean.getModalidadDisposicionFinal().getAnexos().getNombre());
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
}
