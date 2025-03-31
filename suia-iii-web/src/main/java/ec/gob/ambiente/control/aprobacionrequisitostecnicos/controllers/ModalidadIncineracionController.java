/*
 * Copyright 2015 MAGMASOFT
 * Todos los derechos reservados
 */
package ec.gob.ambiente.control.aprobacionrequisitostecnicos.controllers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
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
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import ec.gob.ambiente.control.aprobacionrequisitostecnicos.bean.AprobacionRequisitosTecnicosBean;
import ec.gob.ambiente.control.aprobacionrequisitostecnicos.bean.ModalidadIncineracionBean;
import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.ModalidadIncineracionFacade;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.ModalidadIncineracion;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.ModalidadIncineracionDesecho;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.ModalidadIncineracionDesechoProcesar;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.ModalidadIncineracionFormulacion;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.reportes.UtilDocumento;
import ec.gob.ambiente.suia.utils.JsfUtil;

/**
 * <b> Controlador de la pagina modalidad incineracion. </b>
 * 
 * @author vero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: vero $, $Date: 10/06/2015 $]
 *          </p>
 */
@ManagedBean
@ViewScoped
public class ModalidadIncineracionController {

	private static final Logger LOG = Logger.getLogger(ModalidadIncineracionController.class);

	@Setter
	@Getter
	@ManagedProperty(value = "#{modalidadIncineracionBean}")
	private ModalidadIncineracionBean modalidadIncineracionBean;

	@Setter
	@Getter
	@ManagedProperty(value = "#{aprobacionRequisitosTecnicosBean}")
	private AprobacionRequisitosTecnicosBean aprobacionRequisitosTecnicosBean;

	@EJB
	private ModalidadIncineracionFacade modalidadIncineracionFacade;

	@EJB
	DocumentosFacade documentosFacade;

	@Setter
	@Getter
	private UploadedFile file;

	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;

	private byte[] plantillaProceso;

	@PostConstruct
	public void init() {
		try {
			modalidadIncineracionBean
					.setListaModalidadIncineracionFormulacionEliminados(new ArrayList<ModalidadIncineracionFormulacion>());
			obtenerModalidadTratamiento();
			aprobacionRequisitosTecnicosBean.verART(ModalidadIncineracion.class.getName());
			plantillaProceso = modalidadIncineracionFacade
					.getDocumentoInformativo(ModalidadIncineracionFacade.PLANTILLA_INCINERACION_PROCESOS);

		} catch (ServiceException e) {
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos modalidad tratamiento.");
		} catch (CmisAlfrescoException e) {
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar la plantilla del alfresco.");
		}

	}

	private void obtenerModalidadTratamiento() throws ServiceException {
		modalidadIncineracionBean.setModalidadIncineracion(modalidadIncineracionFacade
				.getModalidadIncineracion(aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos()));
		if (modalidadIncineracionBean.getModalidadIncineracion() == null) {
			obtenerIncineracionDesechos();
			modalidadIncineracionBean.setModalidadIncineracion(new ModalidadIncineracion());
			modalidadIncineracionBean.getModalidadIncineracion().setAprobacionRequisitosTecnicos(
					aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos());
			modalidadIncineracionBean.getModalidadIncineracion().setModalidadIncineracionDesechos(
					modalidadIncineracionBean.getListaModalidadIncineracionDesechos());
			modalidadIncineracionBean.getModalidadIncineracion().setModalidadIncineracionDesechoProcesados(
					modalidadIncineracionBean.getListaModalidadIncineracionDesechoProcesados());
			modalidadIncineracionBean.getModalidadIncineracion().setModalidadIncineracionFormulaciones(
					new ArrayList<ModalidadIncineracionFormulacion>());

			iniciarDocumentos();
		} else {
			obtenerSiExistenNuevosDesechosAsociados();
			obtenerListaDesechos();
			obtenerListaProceso();
			obtenerListaformulario();
			modalidadIncineracionBean.setHabilitarTrataDesechosBiologicos(modalidadIncineracionBean
					.getModalidadIncineracion().getTrataDesechosBiologicosInfecciosos());
			modalidadIncineracionBean.setTrataDesechosBiologicos(modalidadIncineracionBean.getModalidadIncineracion()
					.getTrataDesechosBiologicosInfecciosos().toString());
		}
		agregarMensajeDesechosRequeridos();
		if(modalidadIncineracionBean.getModalidadIncineracion().getDocumentoDesechosBiologicosProtocoloIncineracion() == null) {
			modalidadIncineracionBean.getModalidadIncineracion().setDocumentoDesechosBiologicosProtocoloIncineracion(new Documento());
			modalidadIncineracionBean.getModalidadIncineracion().setDocumentoDesechosBiologicosIncineracion(new Documento());
		}
	}

	public void obtenerListaDesechos() {
		int i = 0;
		for (ModalidadIncineracionDesecho modalidad : modalidadIncineracionBean.getModalidadIncineracion()
				.getModalidadIncineracionDesechos()) {
			modalidad.setIndice(i);
			i++;
		}
	}

	private void agregarMensajeDesechosRequeridos() {
		if (modalidadIncineracionBean.getModalidadIncineracion().getModalidadIncineracionDesechos().isEmpty()) {
			RequestContext context = RequestContext.getCurrentInstance();
			context.execute("PF('importanteWdgt').show();");
		}
	}

	public void obtenerListaProceso() {
		int i = 0;
		for (ModalidadIncineracionDesechoProcesar modalidad : modalidadIncineracionBean.getModalidadIncineracion()
				.getModalidadIncineracionDesechoProcesados()) {
			modalidad.setIndice(i);
			i++;
		}

	}

	public void obtenerListaformulario() {
		int i = 0;
		for (ModalidadIncineracionFormulacion modalidad : modalidadIncineracionBean.getModalidadIncineracion()
				.getModalidadIncineracionFormulaciones()) {
			modalidad.setIndice(i);
			i++;
		}

	}

	private void obtenerIncineracionDesechos() {
		try {
			modalidadIncineracionBean
					.setListaModalidadIncineracionDesechos(new ArrayList<ModalidadIncineracionDesecho>());
			modalidadIncineracionBean
					.setListaModalidadIncineracionDesechoProcesados(new ArrayList<ModalidadIncineracionDesechoProcesar>());
			List<DesechoPeligroso> listaDesechos = modalidadIncineracionFacade
					.getDesechosIncineracion(aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos());
			if (listaDesechos != null && !listaDesechos.isEmpty()) {
				int i = 0;
				for (DesechoPeligroso desecho : listaDesechos) {
					ModalidadIncineracionDesecho modalidad = new ModalidadIncineracionDesecho();
					ModalidadIncineracionDesechoProcesar desechoProcesar = new ModalidadIncineracionDesechoProcesar();
					modalidad.setDesecho(desecho);
					modalidad.setIndice(i);
					desechoProcesar.setDesecho(desecho);
					desechoProcesar.setIndice(i);
					modalidadIncineracionBean.getListaModalidadIncineracionDesechos().add(modalidad);
					modalidadIncineracionBean.getListaModalidadIncineracionDesechoProcesados().add(desechoProcesar);
				}
			}
		} catch (ServiceException e) {
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos modalidad coprocesamiento.");
		}
	}

	private void obtenerSiExistenNuevosDesechosAsociados() throws ServiceException {
		List<ModalidadIncineracionDesecho> listaDesechosElimandosConLaModalidadAlmacenadosEstan = modalidadIncineracionBean
				.getModalidadIncineracion().getModalidadIncineracionDesechos();
		//List<ModalidadIncineracionDesechoProcesar> listaDesechosProcesarElimandosConLaModalidadAlmacenados = modalidadIncineracionBea
		// .getModalidadIncineracion().getModalidadIncineracionDesechoProcesados();

		List<DesechoPeligroso> listaDesechosEliminadosConLaModalidadDeTipoEliminacionDeben = modalidadIncineracionFacade
				.getDesechosIncineracion(aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos());

		//listaDesechosEliminadosConLaModalidadDeTipoEliminacionDeben.removeAll(listaDesechosElimandosConLaModalidadAlmacenadosEstan);

		List<DesechoPeligroso> listoDesechosEstan = new ArrayList<DesechoPeligroso>();

		for (ModalidadIncineracionDesecho mid : listaDesechosElimandosConLaModalidadAlmacenadosEstan) {
			listoDesechosEstan.add(mid.getDesecho());
		}

		List<DesechoPeligroso> agregar = new ArrayList<DesechoPeligroso>();

		for (DesechoPeligroso desechoModalidadAgregar : listaDesechosEliminadosConLaModalidadDeTipoEliminacionDeben) {
			if (!listaDesechosElimandosConLaModalidadAlmacenadosEstan.contains(desechoModalidadAgregar)) {
				agregar.add(desechoModalidadAgregar);
			}
		}

		List<DesechoPeligroso> quitar = new ArrayList<DesechoPeligroso>();

		for (DesechoPeligroso desechoModalidadEliminar : listoDesechosEstan) {
			if (!listaDesechosEliminadosConLaModalidadDeTipoEliminacionDeben.contains(desechoModalidadEliminar)) {
				quitar.add(desechoModalidadEliminar);
			}
		}

		modalidadIncineracionBean.getModalidadIncineracion().getDesechosAsociadosModalidad().removeAll(quitar);
		modalidadIncineracionBean.getModalidadIncineracion().getDesechosAsociadosModalidad().addAll(agregar);




		/*for (DesechoPeligroso desechoPeligroso : agregar) {
			ModalidadIncineracionDesecho a = new ModalidadIncineracionDesecho();
			a.setDesecho(desechoPeligroso);
			listaDesechosElimandosConLaModalidadAlmacenadosEstan.add(a);
		}
		for (DesechoPeligroso desechoPeligroso : agregar) {
			ModalidadIncineracionDesechoProcesar a = new ModalidadIncineracionDesechoProcesar();
			a.setDesecho(desechoPeligroso);
			listaDesechosProcesarElimandosConLaModalidadAlmacenados.add(a);
		}

		for (ModalidadIncineracionDesecho modalidadIncineracionDesecho : listaDesechosElimandosConLaModalidadAlmacenadosEstan) {
			if (quitar.contains(modalidadIncineracionDesecho.getDesecho())) {
				listaDesechosElimandosConLaModalidadAlmacenadosEstan.remove(modalidadIncineracionDesecho);
			}
		}

		for (ModalidadIncineracionDesechoProcesar modalidadIncineracionDesecho : listaDesechosProcesarElimandosConLaModalidadAlmacenados) {
			if (quitar.contains(modalidadIncineracionDesecho.getDesecho())) {
				listaDesechosProcesarElimandosConLaModalidadAlmacenados.remove(modalidadIncineracionDesecho);
			}
		}*/

	}

	public void agregarFormulario() {
		modalidadIncineracionBean.setModalidadIncineracionFormulacion(new ModalidadIncineracionFormulacion());
	}

	public void seleccionarIncineracionDesecho(ModalidadIncineracionDesecho incineracionDesecho) {
		modalidadIncineracionBean.setModalidadIncineracionDesecho(new ModalidadIncineracionDesecho());
		modalidadIncineracionBean.setModalidadIncineracionDesecho(incineracionDesecho);
	}

	public void cerrarModal() {
		modalidadIncineracionBean.setModalidadIncineracionDesecho(null);
		modalidadIncineracionBean.setModalidadIncineracionDesechoProcesar(null);
		modalidadIncineracionBean.setModalidadIncineracionFormulacion(null);
	}

	public void guardarFormulario() {

		if (!modalidadIncineracionBean.getModalidadIncineracionFormulacion().isEditar()) {
			modalidadIncineracionBean.getModalidadIncineracionFormulacion()
					.setIndice(
							modalidadIncineracionBean.getModalidadIncineracion()
									.getModalidadIncineracionFormulaciones().size());
			modalidadIncineracionBean.getModalidadIncineracion().getModalidadIncineracionFormulaciones()
					.add(modalidadIncineracionBean.getModalidadIncineracionFormulacion());
		}
	}

	public void seleccionarFormulario(ModalidadIncineracionFormulacion modalidadIncineracionFormulacion) {
		modalidadIncineracionFormulacion.setEditar(true);
		modalidadIncineracionBean.setModalidadIncineracionFormulacion(modalidadIncineracionFormulacion);
	}

	public void remover() {
		modalidadIncineracionBean.getModalidadIncineracion().getModalidadIncineracionFormulaciones()
				.remove(modalidadIncineracionBean.getModalidadIncineracionFormulacion());
		modalidadIncineracionBean.getListaModalidadIncineracionFormulacionEliminados().add(
				modalidadIncineracionBean.getModalidadIncineracionFormulacion());
	}

	public void seleccionarDesechoProcesar(ModalidadIncineracionDesechoProcesar desechoProcesar) {
		modalidadIncineracionBean.setModalidadIncineracionDesechoProcesar(desechoProcesar);
	}

	private void iniciarDocumentos() {
		modalidadIncineracionBean.getModalidadIncineracion().setDocumentoPlano(new Documento());
		modalidadIncineracionBean.getModalidadIncineracion().setDocumentoRequisitos(new Documento());
		modalidadIncineracionBean.getModalidadIncineracion().setDocumentoCombustible(new Documento());
		modalidadIncineracionBean.getModalidadIncineracion().setDocumentoProtocoloPruebas(new Documento());
		modalidadIncineracionBean.getModalidadIncineracion().setDocumentoProcedimiento(new Documento());
		modalidadIncineracionBean.getModalidadIncineracion().setDocumentoPruebas(new Documento());
		modalidadIncineracionBean.getModalidadIncineracion()
				.setDocumentoDesechosBiologicosIncineracion(new Documento());
		modalidadIncineracionBean.getModalidadIncineracion()
				.setDocumentoDesechosBiologicosIncineracion(new Documento());
		modalidadIncineracionBean.getModalidadIncineracion().setDocumentoDesechosBiologicosProtocoloIncineracion(new Documento());
		modalidadIncineracionBean.getModalidadIncineracion().setDocumentoDesechosBiologicosIncineracion(new Documento());
	}

	public void guardarPagina() {
		try {
			modalidadIncineracionFacade.guardar(modalidadIncineracionBean.getModalidadIncineracion(),
					bandejaTareasBean.getProcessId(), bandejaTareasBean.getTarea().getTaskId(),
					modalidadIncineracionBean.getListaModalidadIncineracionFormulacionEliminados());

			obtenerModalidadTratamiento();
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
		} catch (ServiceException e) {
			JsfUtil.addMessageError("Ocurrió un error al guardar los datos.");
			LOG.error(e, e);
		} catch (CmisAlfrescoException e) {
			JsfUtil.addMessageError("Ocurrió un error con el alfresco al guardar los documentos.");
			LOG.error(e, e);
		}
	}

	public void listenerTratamientoDesechsoBiologicos() {
		modalidadIncineracionBean.setHabilitarTrataDesechosBiologicos(new Boolean(modalidadIncineracionBean
				.getTrataDesechosBiologicos()));

		modalidadIncineracionBean.getModalidadIncineracion().setTrataDesechosBiologicosInfecciosos(
				new Boolean(modalidadIncineracionBean.getTrataDesechosBiologicos()));

		if (modalidadIncineracionBean.getModalidadIncineracion().getTrataDesechosBiologicosInfecciosos()) {
			modalidadIncineracionBean.getModalidadIncineracion().setDocumentoDesechosBiologicosProtocoloIncineracion(new Documento());
			modalidadIncineracionBean.getModalidadIncineracion().setDocumentoDesechosBiologicosIncineracion(new Documento());
		}

	}

	public void guardarDlgDesechosBiologicos() {
		modalidadIncineracionBean.setHabilitarTrataDesechosBiologicos(new Boolean(modalidadIncineracionBean
				.getTrataDesechosBiologicos()));

		modalidadIncineracionBean.getModalidadIncineracion().setTrataDesechosBiologicosInfecciosos(
				new Boolean(modalidadIncineracionBean.getTrataDesechosBiologicos()));
	}

	public void handleFileUpload(FileUploadEvent event) {
		Map<String, Object> att = event.getComponent().getAttributes();
		int indice = new Integer(att.get("indice").toString());
		file = event.getFile();
		switch (indice) {
		case 0:
			modalidadIncineracionBean.getModalidadIncineracion().setDocumentoPlano(
					UtilDocumento.generateDocumentZipRarFromUpload(file.getContents(), file.getFileName()));
			break;
		case 1:
			modalidadIncineracionBean.getModalidadIncineracion().setDocumentoCombustible(
					UtilDocumento.generateDocumentZipRarFromUpload(file.getContents(), file.getFileName()));
			break;
		case 2:
			modalidadIncineracionBean.getModalidadIncineracion().setDocumentoProtocoloPruebas(
					UtilDocumento.generateDocumentZipRarFromUpload(file.getContents(), file.getFileName()));
			break;
		case 3:
			modalidadIncineracionBean.getModalidadIncineracion().setDocumentoProcedimiento(
					UtilDocumento.generateDocumentZipRarFromUpload(file.getContents(), file.getFileName()));
			break;
		case 4:
			modalidadIncineracionBean.getModalidadIncineracion().setDocumentoRequisitos(
					UtilDocumento.generateDocumentZipRarFromUpload(file.getContents(), file.getFileName()));
			break;
		case 5:
			modalidadIncineracionBean.getModalidadIncineracion().setDocumentoDesechosBiologicosIncineracion(
					UtilDocumento.generateDocumentZipRarFromUpload(file.getContents(), file.getFileName()));
			break;
		case 6:
			modalidadIncineracionBean.getModalidadIncineracion().setDocumentoDesechosBiologicosProtocoloIncineracion(
					UtilDocumento.generateDocumentZipRarFromUpload(file.getContents(), file.getFileName()));
			break;
		case 7:
			modalidadIncineracionBean.getModalidadIncineracion().setDocumentoPruebas(
					UtilDocumento.generateDocumentZipRarFromUpload(file.getContents(), file.getFileName()));
			break;

		default:
			LOG.error("Indice de archivo adjunto no esperado");
			JsfUtil.addMessageError("Ocurrió un error al tratar de subir el archivo, por favor comunicarse con mesa de ayuda.");
			break;
		}

	}

	public void descargar(int indice) throws IOException {
		try {
			switch (indice) {
			case 0:
				UtilDocumento.descargarZipRar(modalidadIncineracionFacade.descargarFile(modalidadIncineracionBean
						.getModalidadIncineracion().getDocumentoPlano()), modalidadIncineracionBean
						.getModalidadIncineracion().getDocumentoPlano().getNombre());
				break;
			case 1:
				UtilDocumento.descargarZipRar(modalidadIncineracionFacade.descargarFile(modalidadIncineracionBean
						.getModalidadIncineracion().getDocumentoCombustible()), modalidadIncineracionBean
						.getModalidadIncineracion().getDocumentoCombustible().getNombre());
				break;
			case 2:
				UtilDocumento.descargarZipRar(modalidadIncineracionFacade.descargarFile(modalidadIncineracionBean
						.getModalidadIncineracion().getDocumentoProtocoloPruebas()), modalidadIncineracionBean
						.getModalidadIncineracion().getDocumentoProtocoloPruebas().getNombre());
				break;
			case 3:
				UtilDocumento.descargarZipRar(modalidadIncineracionFacade.descargarFile(modalidadIncineracionBean
						.getModalidadIncineracion().getDocumentoProcedimiento()), modalidadIncineracionBean
						.getModalidadIncineracion().getDocumentoProcedimiento().getNombre());
				break;
			case 4:
				UtilDocumento.descargarZipRar(modalidadIncineracionFacade.descargarFile(modalidadIncineracionBean
						.getModalidadIncineracion().getDocumentoRequisitos()), modalidadIncineracionBean
						.getModalidadIncineracion().getDocumentoRequisitos().getNombre());
				break;
			case 5:
				UtilDocumento.descargarZipRar(modalidadIncineracionFacade.descargarFile(modalidadIncineracionBean
						.getModalidadIncineracion().getDocumentoDesechosBiologicosIncineracion()),
						modalidadIncineracionBean.getModalidadIncineracion()
								.getDocumentoDesechosBiologicosIncineracion().getNombre());
				break;
			case 6:
				UtilDocumento.descargarZipRar(modalidadIncineracionFacade.descargarFile(modalidadIncineracionBean
						.getModalidadIncineracion().getDocumentoDesechosBiologicosProtocoloIncineracion()),
						modalidadIncineracionBean.getModalidadIncineracion()
								.getDocumentoDesechosBiologicosProtocoloIncineracion().getNombre());
				break;
			case 7:
				UtilDocumento.descargarZipRar(modalidadIncineracionFacade.descargarFile(modalidadIncineracionBean
						.getModalidadIncineracion().getDocumentoPruebas()), modalidadIncineracionBean
						.getModalidadIncineracion().getDocumentoPruebas().getNombre());
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

	public StreamedContent getPlantillaProceso() throws Exception {
		DefaultStreamedContent content = null;
		try {
			if (plantillaProceso != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(plantillaProceso));
				content.setName(ModalidadIncineracionFacade.PLANTILLA_INCINERACION_PROCESOS);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return content;

	}

}
