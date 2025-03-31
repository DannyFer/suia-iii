package ec.gob.ambiente.rcoa.sustancias.quimicas.controllers;

import java.io.ByteArrayInputStream;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.DocumentosRSQFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.DocumentosSustanciasQuimicasRcoa;

@ManagedBean
@ViewScoped
public class DocumentosRSQController {
	
	private static final Logger LOG = Logger.getLogger(DocumentosRSQController.class);
	
	@EJB
    private DocumentosRSQFacade documentosRSQFacade;
	
	public StreamedContent streamedContentRsq(DocumentosSustanciasQuimicasRcoa documento) {
		try {
			if(documento.getContenidoDocumento()==null && documento.getId()!=null)
				documento.setContenidoDocumento(documentosRSQFacade.descargar(documento.getIdAlfresco()));
						
			if (documento != null && documento.getContenidoDocumento()!=null) {
				StreamedContent streamedContent = new DefaultStreamedContent(new ByteArrayInputStream(documento.getContenidoDocumento()),documento.getMime(),documento.getNombre());
				return streamedContent;
			}
		} catch (CmisAlfrescoException e) {
			LOG.error(e);
			e.printStackTrace();
			
		}	
		return null;
	}
	
	public StreamedContent streamedContentRsqPorNombre(String nombreDocumento) {
		try {
			String mime=nombreDocumento.toLowerCase().endsWith(".pdf")?"application/pdf":"application/object";
	        byte[] contenido= documentosRSQFacade.descargarPorNombre(nombreDocumento);
	        StreamedContent streamedContent = new DefaultStreamedContent(new ByteArrayInputStream(contenido),mime,nombreDocumento);
			return streamedContent;
		} catch (CmisAlfrescoException e) {
			LOG.error(e);
			e.printStackTrace();
			
		}	
		return null;
	}
	
}