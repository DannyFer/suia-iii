/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.eia.impactoAmbiental.bean;

import java.io.ByteArrayInputStream;
import java.io.Serializable;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.IdentificacionEvaluacionImpactoAmbiental;
import ec.gob.ambiente.suia.utils.JsfUtil;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author mayriliscs
 * @version Revision: 1.0
 *          <p>
 *          [Autor: mayriliscs, Fecha: 27/06/2015]
 *          </p>
 */
@ManagedBean
@ViewScoped
public class ImpactoAmbientalBaseBean implements Serializable {

	private static final long serialVersionUID = 3075309898032859214L;

	private static final Logger LOGGER = Logger.getLogger(ImpactoAmbientalBaseBean.class);

	@EJB
	private DocumentosFacade documentosFacade;

	public Documento uploadListener(FileUploadEvent event) {
		byte[] contenidoDocumento = event.getFile().getContents();
		Documento documento = crearDocumentoPdf(contenidoDocumento);
		documento.setNombre(event.getFile().getFileName());
		return documento;
	}

	public Documento crearDocumentoPdf(byte[] contenidoDocumento) {
		Documento documento = new Documento();
		documento.setContenidoDocumento(contenidoDocumento);
		documento.setNombreTabla(IdentificacionEvaluacionImpactoAmbiental.class.getSimpleName());
		documento.setIdTable(0);
		documento.setMime("application/pdf");
		documento.setExtesion(".pdf");
		return documento;
	}

	public Documento descargarDocumentoAlfresco(Documento documento) throws CmisAlfrescoException {
		byte[] documentoContenido = null;
		if (documento != null && documento.getIdAlfresco() != null)
			documentoContenido = documentosFacade.descargar(documento.getIdAlfresco());
		if (documentoContenido != null)
			documento.setContenidoDocumento(documentoContenido);
		return documento;
	}

	public StreamedContent getStreamContent(Documento documentoDesc) throws Exception {
		DefaultStreamedContent content = null;
		try {
			Documento documento = this.descargarDocumentoAlfresco(documentoDesc);
			if (documento != null && documento.getNombre() != null && documento.getContenidoDocumento() != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documento.getContenidoDocumento()));
				content.setName(documento.getNombre());
			} else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);

		} catch (Exception exception) {
			LOGGER.error(JsfUtil.MESSAGE_ERROR_ALFRESCO, exception);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
		}
		return content;
	}
}
