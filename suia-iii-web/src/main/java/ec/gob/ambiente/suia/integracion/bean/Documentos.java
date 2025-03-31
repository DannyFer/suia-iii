/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.integracion.bean;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.FileUploadEvent;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.dto.DocumentoReemplazable;
import ec.gob.ambiente.suia.utils.JsfUtil;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author Carlos Pupo
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Carlos Pupo, Fecha: 16/07/2015]
 *          </p>
 */
@ManagedBean
@ViewScoped
public class Documentos {

	@EJB
	private DocumentosFacade documentosFacade;

	@Getter
	@Setter
	private String carpeta;

	@Getter
	private DocumentoReemplazable documentoReemplazable;

	private List<DocumentoReemplazable> documentos;

	public void filtrar() throws CmisAlfrescoException {
		documentos = documentosFacade.getDocumentsPorCarpeta(carpeta);
		if (documentos == null || documentos.isEmpty())
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_NO_RESULTADOS);
	}

	public void reset() {
		carpeta = null;
		documentos = null;
		documentoReemplazable = null;
	}

	public void select(DocumentoReemplazable documento) {
		documentoReemplazable = documento;
	}

	public void handleFileUpload(final FileUploadEvent event) {
		try {
			documentosFacade.replaceDocumentContent(documentoReemplazable, event.getFile().getContents());
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}

	public List<DocumentoReemplazable> getDocumentos() {
		return documentos == null ? documentos = new ArrayList<DocumentoReemplazable>() : documentos;
	}
}
