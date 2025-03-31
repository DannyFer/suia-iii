/*
 * Copyright 2015 MAGMASOFT
 * Todos los derechos reservados
 */
package ec.gob.ambiente.control.aprobacionrequisitostecnicos.controllers;

import java.io.ByteArrayInputStream;
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
import ec.gob.ambiente.control.aprobacionrequisitostecnicos.bean.ModalidadTratamientoBean;
import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.ModalidadTratamientoFacade;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.DesechoModalidadTratamiento;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.ModalidadTratamiento;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.reportes.UtilDocumento;
import ec.gob.ambiente.suia.utils.JsfUtil;

/**
 * <b> Controlador de la pagina modalidad tratamiento. </b>
 * 
 * @author vero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: vero $, $Date: 10/06/2015 $]
 *          </p>
 */


@ManagedBean
@ViewScoped
public class ModalidadTratamientoController {

	private static final Logger LOG = Logger.getLogger(ModalidadTratamientoController.class);

	@Setter
	@Getter
	@ManagedProperty(value = "#{modalidadTratamientoBean}")
	private ModalidadTratamientoBean modalidadTratamientoBean;

	@Setter
	@Getter
	@ManagedProperty(value = "#{aprobacionRequisitosTecnicosBean}")
	private AprobacionRequisitosTecnicosBean aprobacionRequisitosTecnicosBean; 

	@EJB
	private ModalidadTratamientoFacade modalidadTratamientoFacade;

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
			obtenerModalidadTratamiento();
			aprobacionRequisitosTecnicosBean.verART(ModalidadTratamiento.class.getName());
			/*plantillaProceso = modalidadTratamientoFacade.getDocumentoInformativo(ModalidadTratamientoFacade.PLANTILLA_TRATAMIENTO_PROCESOS);*/

		} catch (ServiceException e) {
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos modalidad tratamiento.");

		} /*catch (CmisAlfrescoException e) {
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar la plantilla del alfresco.");
		}*/

	}
	
	private void descargarPlantillaProceso() {
		try {
		plantillaProceso = modalidadTratamientoFacade
				.getDocumentoInformativo(ModalidadTratamientoFacade.PLANTILLA_TRATAMIENTO_PROCESOS);

	} catch (ServiceException e) {
		LOG.error(e, e);
		JsfUtil.addMessageError("Ocurrió un error al recuperar los datos modalidad tratamiento.");

	} catch (CmisAlfrescoException e) {
		LOG.error(e, e);
		JsfUtil.addMessageError("Ocurrió un error al recuperar la plantilla del alfresco.");
		}
	}

	private void obtenerModalidadTratamiento() throws ServiceException {

		/*modalidadTratamientoBean.setModalidadTratamiento(modalidadTratamientoFacade.guardar(
					modalidadTratamientoBean.getModalidadTratamiento(), bandejaTareasBean.getProcessId(),
					bandejaTareasBean.getTarea().getTaskId()));*/
		
		
		modalidadTratamientoBean.setModalidadTratamiento(modalidadTratamientoFacade
				.getModalidadTratamiento(aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos()));
		if (modalidadTratamientoBean.getModalidadTratamiento() == null) {
			modalidadTratamientoBean.setModalidadTratamiento(new ModalidadTratamiento());
			modalidadTratamientoBean.getModalidadTratamiento().setAprobacionRequisitosTecnicos(
					aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos());
			obtenerDesechosAsociados();
			iniciarDocumentos();
		} else {
			modalidadTratamientoBean.setHabilitarTrataDesechosBiologicos(modalidadTratamientoBean
					.getModalidadTratamiento().getTrataDesechosBiologicosInfecciosos());
			modalidadTratamientoBean.setTrataDesechosBiologicos(modalidadTratamientoBean.getModalidadTratamiento()
					.getTrataDesechosBiologicosInfecciosos().toString());
			obtenerSiExistenNuevosDesechosAsociados();
		}

	}

	private void obtenerDesechosAsociados() throws ServiceException {
		modalidadTratamientoBean.getModalidadTratamiento().setListaDesechos(
				modalidadTratamientoFacade.getDesechosTratamiento(aprobacionRequisitosTecnicosBean
						.getAprobacionRequisitosTecnicos()));
		if (modalidadTratamientoBean.getModalidadTratamiento().getListaDesechos().isEmpty()) {
			RequestContext context = RequestContext.getCurrentInstance();
			context.execute("PF('importanteWdgt').show();");
		}
	}

	private void obtenerSiExistenNuevosDesechosAsociados() throws ServiceException {

		List<DesechoModalidadTratamiento> listaDesechosElimandosConLaModalidadAlmacenadosEstan = new ArrayList<>();
		listaDesechosElimandosConLaModalidadAlmacenadosEstan.addAll(modalidadTratamientoBean
				.getModalidadTratamiento().getListaDesechos());
		List<DesechoModalidadTratamiento> listaDesechosEliminadosConLaModalidadDeTipoEliminacionDeben = modalidadTratamientoFacade
				.getDesechosTratamiento(aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos());

		int pos;
		boolean found;
		int size;
		for (DesechoModalidadTratamiento current: listaDesechosElimandosConLaModalidadAlmacenadosEstan){
			size = listaDesechosEliminadosConLaModalidadDeTipoEliminacionDeben.size();
			found=false;
			pos = 0;
			while(pos < size && !found){

				if (current.getDesecho().equals(listaDesechosEliminadosConLaModalidadDeTipoEliminacionDeben.get(pos).getDesecho())){
					found = true;
				}
				else{
					pos++;
				}
			}
			if (!found){
				modalidadTratamientoBean.getModalidadTratamiento().getListaDesechos().remove(current);
			}
		}

	}

	private void iniciarDocumentos() {
		modalidadTratamientoBean.getModalidadTratamiento().setDocumentoDesechosBiologicos(new Documento());
		modalidadTratamientoBean.getModalidadTratamiento().setDocumentoPruebas(new Documento());
		modalidadTratamientoBean.getModalidadTratamiento().setDocumentoPlano(new Documento());
		modalidadTratamientoBean.getModalidadTratamiento().setDocumentoRequisitos(new Documento());
		modalidadTratamientoBean.getModalidadTratamiento().setDocumentoRequisitosProductoProceso(new Documento());

	}

	public void guardarPagina() {
		try {
			modalidadTratamientoBean.setModalidadTratamiento(modalidadTratamientoFacade.guardar(
					modalidadTratamientoBean.getModalidadTratamiento(), bandejaTareasBean.getProcessId(),
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

	public void listenerTratamientoDesechsoBiologicos() {
		modalidadTratamientoBean.setHabilitarTrataDesechosBiologicos(new Boolean(modalidadTratamientoBean
				.getTrataDesechosBiologicos()));

		modalidadTratamientoBean.getModalidadTratamiento().setTrataDesechosBiologicosInfecciosos(
				new Boolean(modalidadTratamientoBean.getTrataDesechosBiologicos()));
		if (modalidadTratamientoBean.getModalidadTratamiento().getTrataDesechosBiologicosInfecciosos()) {
			modalidadTratamientoBean.getModalidadTratamiento().setDocumentoDesechosBiologicos(new Documento());
		}
		if(this.modalidadTratamientoBean.isHabilitarTrataDesechosBiologicos()){
			RequestContext.getCurrentInstance().execute(
					"PF('dlgNota').show();");
		}



	}

	public void guardarDlgDesechosBiologicos() {
		modalidadTratamientoBean.setHabilitarTrataDesechosBiologicos(new Boolean(modalidadTratamientoBean
				.getTrataDesechosBiologicos()));

		modalidadTratamientoBean.getModalidadTratamiento().setTrataDesechosBiologicosInfecciosos(
				new Boolean(modalidadTratamientoBean.getTrataDesechosBiologicos()));
	}

	public void handleFileUpload(FileUploadEvent event) {
		Map<String, Object> att = event.getComponent().getAttributes();
		int indice = new Integer(att.get("indice").toString());
		file = event.getFile();
		switch (indice) {
		case 0:
			modalidadTratamientoBean.getModalidadTratamiento().setDocumentoPlano(
					UtilDocumento.generateDocumentZipRarFromUpload(file.getContents(), file.getFileName()));
			break;
		case 1:
			modalidadTratamientoBean.getModalidadTratamiento().setDocumentoRequisitos(
					UtilDocumento.generateDocumentZipRarFromUpload(file.getContents(), file.getFileName()));
			break;
		case 2:
			modalidadTratamientoBean.getModalidadTratamiento().setDocumentoRequisitosProductoProceso(
					UtilDocumento.generateDocumentZipRarFromUpload(file.getContents(), file.getFileName()));
			break;
		case 3:
			modalidadTratamientoBean.getModalidadTratamiento().setDocumentoDesechosBiologicos(
					UtilDocumento.generateDocumentZipRarFromUpload(file.getContents(), file.getFileName()));
			break;
		case 4:
			modalidadTratamientoBean.getModalidadTratamiento().setDocumentoPruebas(
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
				UtilDocumento.descargarZipRar(modalidadTratamientoFacade.descargarFile(modalidadTratamientoBean
						.getModalidadTratamiento().getDocumentoPlano()), modalidadTratamientoBean
						.getModalidadTratamiento().getDocumentoPlano().getNombre());
				break;
			case 1:
				UtilDocumento.descargarZipRar(modalidadTratamientoFacade.descargarFile(modalidadTratamientoBean
						.getModalidadTratamiento().getDocumentoRequisitos()), modalidadTratamientoBean
						.getModalidadTratamiento().getDocumentoRequisitos().getNombre());
				break;
			case 2:
				UtilDocumento.descargarZipRar(modalidadTratamientoFacade.descargarFile(modalidadTratamientoBean
						.getModalidadTratamiento().getDocumentoRequisitosProductoProceso()), modalidadTratamientoBean
						.getModalidadTratamiento().getDocumentoRequisitosProductoProceso().getNombre());
				break;
			case 3:
				UtilDocumento.descargarZipRar(modalidadTratamientoFacade.descargarFile(modalidadTratamientoBean
						.getModalidadTratamiento().getDocumentoDesechosBiologicos()), modalidadTratamientoBean
						.getModalidadTratamiento().getDocumentoDesechosBiologicos().getNombre());
				break;
			case 4:
				UtilDocumento.descargarZipRar(modalidadTratamientoFacade.descargarFile(modalidadTratamientoBean
						.getModalidadTratamiento().getDocumentoPruebas()), modalidadTratamientoBean
						.getModalidadTratamiento().getDocumentoPruebas().getNombre());
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

	public void seleccionarDesecho(DesechoModalidadTratamiento desechoModalidadTratamiento) {
		modalidadTratamientoBean.setDesechoModalidadTratamiento(desechoModalidadTratamiento);
	}

	public StreamedContent getPlantillaProceso() throws Exception {
		DefaultStreamedContent content = null;
		descargarPlantillaProceso();
		try {
			if (plantillaProceso != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(plantillaProceso));
				content.setName(ModalidadTratamientoFacade.PLANTILLA_TRATAMIENTO_PROCESOS);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return content;

	}

}
