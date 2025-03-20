/*
 * Copyright 2015 MAGMASOFT
 * Todos los derechos reservados
 */
package ec.gob.ambiente.control.aprobacionrequisitostecnicos.controllers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.*;
import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import ec.gob.ambiente.control.aprobacionrequisitostecnicos.bean.AprobacionRequisitosTecnicosBean;
import ec.gob.ambiente.control.aprobacionrequisitostecnicos.bean.ModalidadCoprocesamientoBean;
import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.ModalidadCoprocesamientoFacade;
import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.reportes.UtilDocumento;
import ec.gob.ambiente.suia.utils.JsfUtil;

/* @version $Revision: 1.0 $
 *          <p>
 *          [$Author: Jonathan Guerrero $, $Date: 22/06/2015 $]
 *          </p>
 */
@ManagedBean
@ViewScoped
public class ModalidadCoprocesamientoController {

	private static final Logger LOG = Logger.getLogger(ModalidadCoprocesamientoController.class);

	@Setter
	@Getter
	@ManagedProperty(value = "#{modalidadCoprocesamientoBean}")
	private ModalidadCoprocesamientoBean modalidadCoprocesamientoBean;

	@Setter
	@Getter
	@ManagedProperty(value = "#{aprobacionRequisitosTecnicosBean}")
	private AprobacionRequisitosTecnicosBean aprobacionRequisitosTecnicosBean;

	@EJB
	private ModalidadCoprocesamientoFacade modalidadCoprocesamientoFacade;

	@Setter
	@Getter
	private UploadedFile file;

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

	private byte[] plantillaProceso;

	@PostConstruct
	public void init() {
		try {
			modalidadCoprocesamientoBean
					.setListaModalidadCoprocesamientoFormulacionEliminados(new ArrayList<ModalidadCoprocesamientoFormulacion>());

			AprobacionRequisitosTecnicos aprobacion = aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos();
			ModalidadCoprocesamiento modalidad = null;
			if (aprobacion!=null)
				modalidad = modalidadCoprocesamientoFacade.getModalidadCoprocesamiento(aprobacion);

			modalidadCoprocesamientoBean.setModalidadCoprocesamiento(modalidad!=null ? modalidad : new ModalidadCoprocesamiento());

			modalidadCoprocesamientoBean.setModalidadCoprocesamientoFormulacion(new ModalidadCoprocesamientoFormulacion());

			if (modalidadCoprocesamientoBean.getModalidadCoprocesamiento().getId() == null) {
				obtenerCoprocesamientoDesechos();
				modalidadCoprocesamientoBean.setModalidadCoprocesamiento(new ModalidadCoprocesamiento());
				modalidadCoprocesamientoBean.getModalidadCoprocesamiento().setAprobacionRequisitosTecnicos(
						aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos());
				modalidadCoprocesamientoBean.getModalidadCoprocesamiento().setModalidadCoprocesamientoDesechos(
						modalidadCoprocesamientoBean.getListaCoprocesamientoDesecho());
				modalidadCoprocesamientoBean.getModalidadCoprocesamiento().setModalidadDesechoProcesados(
						modalidadCoprocesamientoBean.getListaDesechoProcesar());
				modalidadCoprocesamientoBean.getModalidadCoprocesamiento().setModalidadCoprocesamientoFormulaciones(
						new ArrayList<ModalidadCoprocesamientoFormulacion>());
			} else {
				obtenerSiExistenNuevosDesechosAsociados();
				obtenerListaformulario();
				obtenerListaDesechos();
				obtenerListaProceso();
				cargarNombreDocumentos();
			}
			plantillaProceso = modalidadCoprocesamientoFacade
					.getDocumentoInformativo(ModalidadCoprocesamientoFacade.PLANTILLA_COOPROCESAMIENTO_PROCESOS);
			aprobacionRequisitosTecnicosBean.verART(ModalidadCoprocesamiento.class.getName());

		} catch (ServiceException e) {
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos modalidad coprocesamiento.");
		} catch (CmisAlfrescoException e) {
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar la plantilla del alfresco.");
		}

	}

	public void obtenerListaformulario() {
		int i = 0;
		for (ModalidadCoprocesamientoFormulacion modalidad : modalidadCoprocesamientoBean.getModalidadCoprocesamiento()
				.getModalidadCoprocesamientoFormulaciones()) {
			modalidad.setIndice(i);
			i++;
		}

	}

	public void obtenerListaDesechos() {
		int i = 0;
		for (ModalidadCoprocesamientoDesecho modalidad : modalidadCoprocesamientoBean.getModalidadCoprocesamiento()
				.getModalidadCoprocesamientoDesechos()) {
			modalidad.setIndice(i);
			i++;
		}

	}

	public void obtenerListaProceso() {
		int i = 0;
		for (ModalidadCoprocesamientoDesechoProcesar modalidad : modalidadCoprocesamientoBean
				.getModalidadCoprocesamiento().getModalidadDesechoProcesados()) {
			modalidad.setIndice(i);
			i++;
		}

	}

	public void agregarFormulario() {
		modalidadCoprocesamientoBean.setModalidadCoprocesamientoFormulacion(new ModalidadCoprocesamientoFormulacion());
	}

	public void guardarFormulario() {

		if (!modalidadCoprocesamientoBean.getModalidadCoprocesamientoFormulacion().isEditar()) {
			modalidadCoprocesamientoBean.getModalidadCoprocesamientoFormulacion().setIndice(
					modalidadCoprocesamientoBean.getModalidadCoprocesamiento()
							.getModalidadCoprocesamientoFormulaciones().size());
			modalidadCoprocesamientoBean.getModalidadCoprocesamiento().getModalidadCoprocesamientoFormulaciones()
					.add(modalidadCoprocesamientoBean.getModalidadCoprocesamientoFormulacion());
		}
	}

	public void seleccionarFormulario(ModalidadCoprocesamientoFormulacion modalidadCoprocesamientoFormulacion) {
		modalidadCoprocesamientoFormulacion.setEditar(true);
		modalidadCoprocesamientoBean.setModalidadCoprocesamientoFormulacion(modalidadCoprocesamientoFormulacion);
	}

	public void remover() {
		modalidadCoprocesamientoBean.getModalidadCoprocesamiento().getModalidadCoprocesamientoFormulaciones()
				.remove(modalidadCoprocesamientoBean.getModalidadCoprocesamientoFormulacion());
		modalidadCoprocesamientoBean.getListaModalidadCoprocesamientoFormulacionEliminados().add(
				modalidadCoprocesamientoBean.getModalidadCoprocesamientoFormulacion());
	}

	private void obtenerCoprocesamientoDesechos() {
		try {
			modalidadCoprocesamientoBean
					.setListaCoprocesamientoDesecho(new ArrayList<ModalidadCoprocesamientoDesecho>());
			modalidadCoprocesamientoBean
					.setListaDesechoProcesar(new ArrayList<ModalidadCoprocesamientoDesechoProcesar>());
			List<DesechoPeligroso> listaDesechos = modalidadCoprocesamientoFacade
					.getDesechosCoprocesamiento(aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos());
			if (listaDesechos != null && !listaDesechos.isEmpty()) {
				int i = 0;
				for (DesechoPeligroso desecho : listaDesechos) {
					ModalidadCoprocesamientoDesecho modalidad = new ModalidadCoprocesamientoDesecho();
					ModalidadCoprocesamientoDesechoProcesar desechoProcesar = new ModalidadCoprocesamientoDesechoProcesar();
					modalidad.setDesecho(desecho);
					modalidad.setIndice(i);
					desechoProcesar.setDesecho(desecho);
					desechoProcesar.setIndice(i);
					modalidadCoprocesamientoBean.getListaCoprocesamientoDesecho().add(modalidad);
					modalidadCoprocesamientoBean.getListaDesechoProcesar().add(desechoProcesar);
				}
			}

			if (modalidadCoprocesamientoBean.getListaCoprocesamientoDesecho().isEmpty()) {
				RequestContext context = RequestContext.getCurrentInstance();
				context.execute("PF('importantWdgt').show();");
			}
		} catch (ServiceException e) {
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos modalidad coprocesamiento.");
		}
	}

	private void obtenerSiExistenNuevosDesechosAsociados() throws ServiceException {
		List<ModalidadCoprocesamientoDesecho> listaDesechosElimandosConLaModalidadAlmacenados = modalidadCoprocesamientoBean
				.getModalidadCoprocesamiento().getModalidadCoprocesamientoDesechos();

		List<DesechoPeligroso> listaDesechosEliminadosConLaModalidadDeTipoEliminacion = modalidadCoprocesamientoFacade
				.getDesechosCoprocesamiento(aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos());

		List<DesechoPeligroso> listoDesechos = new ArrayList<DesechoPeligroso>();

		for (ModalidadCoprocesamientoDesecho mid : listaDesechosElimandosConLaModalidadAlmacenados) {

			listoDesechos.add(mid.getDesecho());

		}
		List<DesechoPeligroso> agregar = new ArrayList<DesechoPeligroso>();

		for (DesechoPeligroso desechoModalidadAgregar : listaDesechosEliminadosConLaModalidadDeTipoEliminacion) {
			if (!listaDesechosElimandosConLaModalidadAlmacenados.contains(desechoModalidadAgregar)) {
				agregar.add(desechoModalidadAgregar);
			}
		}
		List<DesechoPeligroso> quitar = new ArrayList<DesechoPeligroso>();

		for (DesechoPeligroso desechoModalidadEliminar : listoDesechos) {
			if (!listaDesechosEliminadosConLaModalidadDeTipoEliminacion.contains(desechoModalidadEliminar)) {
				quitar.add(desechoModalidadEliminar);
			}
		}
		modalidadCoprocesamientoBean.getModalidadCoprocesamiento().getDesechosAsociadosModalidad().removeAll(quitar);
		modalidadCoprocesamientoBean.getModalidadCoprocesamiento().getDesechosAsociadosModalidad().addAll(agregar);

		/*for (DesechoPeligroso desechoPeligroso : agregar) {
			ModalidadCoprocesamientoDesecho a = new ModalidadCoprocesamientoDesecho();
			a.setDesecho(desechoPeligroso);
			listaDesechosElimandosConLaModalidadAlmacenados.add(a);
		}
		for (DesechoPeligroso desechoPeligroso : agregar) {
			ModalidadCoprocesamientoDesechoProcesar a = new ModalidadCoprocesamientoDesechoProcesar();
			a.setDesecho(desechoPeligroso);
			listaDesechosProcesarElimandosConLaModalidadAlmacenados.add(a);
		}

		for (ModalidadCoprocesamientoDesecho modalidadIncineracionDesecho : listaDesechosElimandosConLaModalidadAlmacenados) {
			if (quitar.contains(modalidadIncineracionDesecho.getDesecho())) {
				listaDesechosElimandosConLaModalidadAlmacenados.remove(modalidadIncineracionDesecho);
			}
		}

		for (ModalidadCoprocesamientoDesechoProcesar modalidadIncineracionDesecho : listaDesechosProcesarElimandosConLaModalidadAlmacenados) {
			if (quitar.contains(modalidadIncineracionDesecho.getDesecho())) {
				listaDesechosProcesarElimandosConLaModalidadAlmacenados.remove(modalidadIncineracionDesecho);
			}
		}*/

	}




	public void seleccionarCoprocesamientoDesecho(ModalidadCoprocesamientoDesecho coprocesamientoDesecho) {
		modalidadCoprocesamientoBean.setModalidadCoprocesamientoDesecho(new ModalidadCoprocesamientoDesecho());
		modalidadCoprocesamientoBean.setModalidadCoprocesamientoDesecho(coprocesamientoDesecho);
	}

	public void seleccionarDesechoProcesar(ModalidadCoprocesamientoDesechoProcesar desechoProcesar) {
		modalidadCoprocesamientoBean.setModalidadCoprocesamientoDesechoProcesar(desechoProcesar);
	}

	public void cerrarModal() {
		modalidadCoprocesamientoBean.setModalidadCoprocesamientoDesecho(null);
		modalidadCoprocesamientoBean.setModalidadCoprocesamientoDesechoProcesar(null);
		modalidadCoprocesamientoBean.setModalidadCoprocesamientoFormulacion(null);
	}

	private void cargarNombreDocumentos() {

		modalidadCoprocesamientoBean
				.setNombreFileSistemaAlimentacionDesechosYOperacionesActividad(modalidadCoprocesamientoBean
						.getModalidadCoprocesamiento().getSistemaAlimentacionDesechosYOperacionesActividad()
						.getNombre());
		modalidadCoprocesamientoBean
				.setNombreFileRequisitosCoprocesamientoDesechoPeligroso(modalidadCoprocesamientoBean
						.getModalidadCoprocesamiento().getRequisitosCoprocesamientoDesechoPeligroso().getNombre());

		modalidadCoprocesamientoBean.setNombreFilePlano(modalidadCoprocesamientoBean.getModalidadCoprocesamiento()
				.getPlanoPlanta().getNombre());
		modalidadCoprocesamientoBean.setNombreFileResumenEjecutivoProtocoloPruebas(modalidadCoprocesamientoBean
				.getModalidadCoprocesamiento().getResumenEjecutivoProtocoloPruebas().getNombre());
		if (modalidadCoprocesamientoBean.getModalidadCoprocesamiento().getProcedimientoProtocoloPrueba() != null) {
			modalidadCoprocesamientoBean.setNombreFileProcedimientoProtocoloPrueba(modalidadCoprocesamientoBean
					.getModalidadCoprocesamiento().getProcedimientoProtocoloPrueba().getNombre());
		} else {
			modalidadCoprocesamientoBean.setNombreFileProcedimientoProtocoloPrueba(null);
		}
		modalidadCoprocesamientoBean.setNombreFileRequisitosProtocoloPrueba(modalidadCoprocesamientoBean
				.getModalidadCoprocesamiento().getRequisitosProtocoloPrueba().getNombre());
	}

	public void fileUploadListenerPlano(FileUploadEvent event) {
		file = event.getFile();
		modalidadCoprocesamientoBean.getModalidadCoprocesamiento().setPlanoPlanta(
				UtilDocumento.generateDocumentZipRarFromUpload(file.getContents(), file.getFileName()));
		modalidadCoprocesamientoBean.setNombreFilePlano(modalidadCoprocesamientoBean.getModalidadCoprocesamiento()
				.getPlanoPlanta().getNombre());
	}

	public void fileUploadListenerProcedimientoProtocoloPrueba(FileUploadEvent event) {
		file = event.getFile();
		modalidadCoprocesamientoBean.getModalidadCoprocesamiento().setProcedimientoProtocoloPrueba(
				UtilDocumento.generateDocumentZipRarFromUpload(file.getContents(), file.getFileName()));
		modalidadCoprocesamientoBean.setNombreFileProcedimientoProtocoloPrueba(modalidadCoprocesamientoBean
				.getModalidadCoprocesamiento().getProcedimientoProtocoloPrueba().getNombre());
	}

	public void fileUploadListenerRequisitosCoprocesamientoDesechoPeligroso(FileUploadEvent event) {
		file = event.getFile();
		modalidadCoprocesamientoBean.getModalidadCoprocesamiento().setRequisitosCoprocesamientoDesechoPeligroso(
				UtilDocumento.generateDocumentZipRarFromUpload(file.getContents(), file.getFileName()));
		modalidadCoprocesamientoBean
				.setNombreFileRequisitosCoprocesamientoDesechoPeligroso(modalidadCoprocesamientoBean
						.getModalidadCoprocesamiento().getRequisitosCoprocesamientoDesechoPeligroso().getNombre());
	}

	public void fileUploadListenerRequisitosProtocoloPrueba(FileUploadEvent event) {
		file = event.getFile();
		modalidadCoprocesamientoBean.getModalidadCoprocesamiento().setRequisitosProtocoloPrueba(
				UtilDocumento.generateDocumentZipRarFromUpload(file.getContents(), file.getFileName()));
		modalidadCoprocesamientoBean.setNombreFileRequisitosProtocoloPrueba(modalidadCoprocesamientoBean
				.getModalidadCoprocesamiento().getRequisitosProtocoloPrueba().getNombre());
	}

	public void fileUploadListenerSistemaAlimentacionDesechosYOperacionesActividad(FileUploadEvent event) {
		file = event.getFile();
		modalidadCoprocesamientoBean.getModalidadCoprocesamiento().setSistemaAlimentacionDesechosYOperacionesActividad(
				UtilDocumento.generateDocumentZipRarFromUpload(file.getContents(), file.getFileName()));
		modalidadCoprocesamientoBean
				.setNombreFileSistemaAlimentacionDesechosYOperacionesActividad(modalidadCoprocesamientoBean
						.getModalidadCoprocesamiento().getSistemaAlimentacionDesechosYOperacionesActividad()
						.getNombre());
	}

	public void fileUploadListenerResumenEjecutivoProtocoloPruebas(FileUploadEvent event) {
		file = event.getFile();
		modalidadCoprocesamientoBean.getModalidadCoprocesamiento().setResumenEjecutivoProtocoloPruebas(
				UtilDocumento.generateDocumentZipRarFromUpload(file.getContents(), file.getFileName()));
		modalidadCoprocesamientoBean.setNombreFileResumenEjecutivoProtocoloPruebas(modalidadCoprocesamientoBean
				.getModalidadCoprocesamiento().getResumenEjecutivoProtocoloPruebas().getNombre());
	}

	public void descargar(int indice) throws IOException {
		try {
			switch (indice) {
			case 0:
				UtilDocumento.descargarZipRar(modalidadCoprocesamientoFacade.descargarFile(modalidadCoprocesamientoBean
						.getModalidadCoprocesamiento().getPlanoPlanta()), modalidadCoprocesamientoBean
						.getModalidadCoprocesamiento().getPlanoPlanta().getNombre());
				break;
			case 5:
				UtilDocumento.descargarZipRar(modalidadCoprocesamientoFacade.descargarFile(modalidadCoprocesamientoBean
						.getModalidadCoprocesamiento().getProcedimientoProtocoloPrueba()), modalidadCoprocesamientoBean
						.getModalidadCoprocesamiento().getProcedimientoProtocoloPrueba().getNombre());
				break;
			case 2:
				UtilDocumento.descargarZipRar(modalidadCoprocesamientoFacade.descargarFile(modalidadCoprocesamientoBean
						.getModalidadCoprocesamiento().getRequisitosCoprocesamientoDesechoPeligroso()),
						modalidadCoprocesamientoBean.getModalidadCoprocesamiento()
								.getRequisitosCoprocesamientoDesechoPeligroso().getNombre());
				break;
			case 4:
				UtilDocumento.descargarZipRar(modalidadCoprocesamientoFacade.descargarFile(modalidadCoprocesamientoBean
						.getModalidadCoprocesamiento().getRequisitosProtocoloPrueba()), modalidadCoprocesamientoBean
						.getModalidadCoprocesamiento().getRequisitosProtocoloPrueba().getNombre());
				break;
			case 3:
				UtilDocumento.descargarZipRar(modalidadCoprocesamientoFacade.descargarFile(modalidadCoprocesamientoBean
						.getModalidadCoprocesamiento().getResumenEjecutivoProtocoloPruebas()),
						modalidadCoprocesamientoBean.getModalidadCoprocesamiento()
								.getResumenEjecutivoProtocoloPruebas().getNombre());
				break;
			case 1:
				UtilDocumento.descargarZipRar(modalidadCoprocesamientoFacade.descargarFile(modalidadCoprocesamientoBean
						.getModalidadCoprocesamiento().getSistemaAlimentacionDesechosYOperacionesActividad()),
						modalidadCoprocesamientoBean.getModalidadCoprocesamiento()
								.getSistemaAlimentacionDesechosYOperacionesActividad().getNombre());
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

	public void validarModalidadCoprocesamiento() {

		guardarPagina();

	}

	public void guardarPagina() {
		try {
			modalidadCoprocesamientoFacade.guardar(modalidadCoprocesamientoBean.getModalidadCoprocesamiento(),
					bandejaTareasBean.getProcessId(), bandejaTareasBean.getTarea().getTaskId(),
					modalidadCoprocesamientoBean.getListaModalidadCoprocesamientoFormulacionEliminados());

			init();

			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
		} catch (ServiceException e) {
			JsfUtil.addMessageError("Ocurrió un error al guardar los datos.");
			LOG.error(e, e);
		} catch (CmisAlfrescoException e) {
			JsfUtil.addMessageError("Ocurrió un error con el alfresco al guardar los documentos.");
			LOG.error(e, e);
		}
	}

	public void listenerIncineracionHornosCementeros() {
		if (modalidadCoprocesamientoBean.getModalidadCoprocesamiento().isIncineraHornosCementeros()) {
			RequestContext.getCurrentInstance().execute("PF('dlgNota').show()");
		}
	}

	public StreamedContent getPlantillaProceso() throws Exception {
		DefaultStreamedContent content = null;
		try {
			if (plantillaProceso != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(plantillaProceso));
				content.setName(ModalidadCoprocesamientoFacade.PLANTILLA_COOPROCESAMIENTO_PROCESOS);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return content;

	}


}
