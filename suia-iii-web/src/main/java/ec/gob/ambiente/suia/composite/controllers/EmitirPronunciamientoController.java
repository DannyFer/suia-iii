/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.composite.controllers;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.composite.bean.EmitirPronunciamientoCC;
import ec.gob.ambiente.suia.comun.bean.EmitirPronunciamientoComunBean;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.Pronunciamiento;
import ec.gob.ambiente.suia.domain.PronunciamientoDocumentos;
import ec.gob.ambiente.suia.domain.enums.EstadoPronunciamiento;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.pronunciamiento.facade.PronunciamientoFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author mayriliscs
 * @version Revision: 1.0
 *          <p>
 *          [Autor: mayriliscs, Fecha: 15/01/2015]
 *          </p>
 */
@ManagedBean
@RequestScoped
public class EmitirPronunciamientoController implements Serializable{
    
    private static final long serialVersionUID = -3526371845123629686L;

	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{emitirPronunciamientoCC}")
	private EmitirPronunciamientoCC emitirPronunciamientoCC;

	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;

	@EJB
	private PronunciamientoFacade pronunciamientoFacade;

	@EJB
	private ProcesoFacade procesoFacade;

	private static final Logger LOG = Logger.getLogger(EmitirPronunciamientoComunBean.class);

	public void completarTareaEmitirCriterioTecnico() {

		long idTarea = bandejaTareasBean.getTarea().getTaskId();
		long idInstanciaProceso = bandejaTareasBean.getProcessId();
		// List<Documento> documentos = new ArrayList<Documento>();
		List<PronunciamientoDocumentos> pronuncDocum = new ArrayList<PronunciamientoDocumentos>();

		List<Integer> pronunciamientos = pronunciamientoFacade.obtenerIdsIntegerPronunciamientosSolicitados(
				idInstanciaProceso, loginBean.getUsuario());

		Pronunciamiento pronunciamiento = pronunciamientoFacade.getPronunciamientosPorUsuarioYProceso(pronunciamientos,
				loginBean.getUsuario().getId());

		if (pronunciamiento != null) {

			try {

				for (int i = 0; i < emitirPronunciamientoCC.getFicherosSubidos().size(); i++) {
					Documento documento = new Documento();
					documento.setNombre(emitirPronunciamientoCC.getFicherosSubidos().get(i).getFileName());
					pronunciamientoFacade.saveOrUpdate(documento);
					PronunciamientoDocumentos pronunciamientoDocumentos = new PronunciamientoDocumentos();
					pronunciamientoDocumentos.setDocumento(documento);
					pronunciamientoDocumentos.setPronunciamiento(pronunciamiento);
					pronunciamientoFacade.saveOrUpdate(pronunciamientoDocumentos);
					pronuncDocum.add(pronunciamientoDocumentos);

					// documentos.add(documento);
				}
				// pronunciamiento.setDocumentos(documentos);
				pronunciamiento.setPronunciamientoDocumentos(pronuncDocum);
				pronunciamiento.setFecha(emitirPronunciamientoCC.getFecha());
				pronunciamiento.setContenido(emitirPronunciamientoCC.getPronunciamiento().getContenido());

				if (loginBean.getUsuario() != null)
					pronunciamiento.setUsuario(loginBean.getUsuario());

				pronunciamiento.setEstadoPronunciamiento(EstadoPronunciamiento.RECIBIDO);

				if (pronunciamiento.isPersisted()) {
					pronunciamientoFacade.saveOrUpdate(pronunciamiento);
				} else {
					pronunciamientoFacade.saveOrUpdate(pronunciamiento);
					/*
					 * pronunciamientoFacade.adicionarPronunciamientoVariableProceso
					 * (pronunciamiento, idInstanciaProceso,
					 * loginBean.getNombreUsuario(), loginBean.getPassword());
					 */
				}

				procesoFacade.aprobarTarea(loginBean.getUsuario(), idTarea, idInstanciaProceso, null);
			} catch (JbpmException e) {
				LOG.error(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA, e);
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
			}
		}
		JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
	}

	public void uploadListener(FileUploadEvent event) {
		LOG.info("Aqui va el codigo para salvar el fichero");

		emitirPronunciamientoCC.getFicherosSubidos().add(event.getFile());
	}

	public StreamedContent getFileToDownload(final UploadedFile file) {
		DefaultStreamedContent content = null;
		if (file != null) {
			content = new DefaultStreamedContent(new ByteArrayInputStream(file.getContents()));
			content.setName(file.getFileName());
		}
		return content;
	}

	public void eliminarArchivo(final UploadedFile file) {
		emitirPronunciamientoCC.getFicherosSubidos().remove(file);
	}
}
