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
import ec.gob.ambiente.suia.composite.bean.CorregirPronunciamientoCC;
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
public class CorregirPronunciamientoController implements Serializable{
    
    private static final long serialVersionUID = -3526371287113481586L;

	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{corregirPronunciamientoCC}")
	private CorregirPronunciamientoCC corregirPronunciamientoCC;

	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;

	@EJB
	private PronunciamientoFacade pronunciamientoFacade;

	@EJB
	private ProcesoFacade procesoFacade;

	private static final Logger LOG = Logger.getLogger(CorregirPronunciamientoController.class);

	public void completarTareaCorregirPronunciamientoTecnico() {

		long idTarea = bandejaTareasBean.getTarea().getTaskId();
		long idInstanciaProceso = bandejaTareasBean.getProcessId();
		List<PronunciamientoDocumentos> pronuncDocum = new ArrayList<PronunciamientoDocumentos>();

		List<Integer> pronunciamientos = pronunciamientoFacade.obtenerIdsIntegerPronunciamientosSolicitados(
				idInstanciaProceso, loginBean.getUsuario());

		Pronunciamiento pronunciamiento = pronunciamientoFacade.getPronunciamientosPorUsuarioYProceso(pronunciamientos,
				loginBean.getUsuario().getId());

		if (pronunciamiento != null) {

			for (int i = 0; i < corregirPronunciamientoCC.getFicherosSubidos().size(); i++) {
				Documento documento = new Documento();
				documento.setNombre(corregirPronunciamientoCC.getFicherosSubidos().get(i).getFileName());
				PronunciamientoDocumentos pronunciamientoDocumentos = new PronunciamientoDocumentos();
				pronunciamientoDocumentos.setDocumento(documento);
				pronunciamientoDocumentos.setPronunciamiento(pronunciamiento);
				pronuncDocum.add(pronunciamientoDocumentos);
			}
			pronunciamiento.setPronunciamientoDocumentos(pronuncDocum);
			pronunciamiento.setContenido(corregirPronunciamientoCC.getPronunciamiento().getContenido());

			if (loginBean.getUsuario() != null)
				pronunciamiento.setUsuario(loginBean.getUsuario());

			pronunciamiento.setEstadoPronunciamiento(EstadoPronunciamiento.RECIBIDO);

			pronunciamientoFacade.saveOrUpdate(pronunciamiento);

			try {
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

		corregirPronunciamientoCC.getFicherosSubidos().add(event.getFile());
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
		corregirPronunciamientoCC.getFicherosSubidos().remove(file);
	}
}
