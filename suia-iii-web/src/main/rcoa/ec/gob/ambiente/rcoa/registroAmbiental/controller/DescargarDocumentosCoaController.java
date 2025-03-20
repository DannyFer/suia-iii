package ec.gob.ambiente.rcoa.registroAmbiental.controller;

import java.io.ByteArrayInputStream;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.DocumentoEstudioImpacto;
import ec.gob.ambiente.rcoa.facade.DocumentosRegistroAmbientalFacade;
import ec.gob.ambiente.rcoa.model.DocumentoRegistroAmbiental;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.DocumentosRgdRcoa;


@ManagedBean
@ViewScoped
public class DescargarDocumentosCoaController {

	@EJB
	private DocumentosRegistroAmbientalFacade documentosFacade;

	public StreamedContent descargarDocumento(DocumentoRegistroAmbiental documento){
		try {
			if(documento!=null){
				if (documento.getExtesion().contains(".pdf")){
					if(documento.getContenidoDocumento()==null)
						documento.setContenidoDocumento(documentosFacade.descargar(documento.getAlfrescoId()));	
					return getStreamedContent(documento);
				}else{
					DefaultStreamedContent content = null;
					if(documento.getContenidoDocumento() == null){
						documento.setContenidoDocumento(documentosFacade.descargar(documento.getAlfrescoId()));	
					}					
					if (documento.getNombre() != null && documento.getContenidoDocumento() != null) {
						content = new DefaultStreamedContent(new ByteArrayInputStream(documento.getContenidoDocumento()));
						content.setName(documento.getNombre());
						return content;
					}
				}
			}
			return null;
		} catch (Exception e) {
			return null;
		}
	}
	
	public static StreamedContent getStreamedContent(DocumentoRegistroAmbiental documento){		
		try {
			if (documento != null && documento.getContenidoDocumento()!=null) {
				StreamedContent streamedContent = new DefaultStreamedContent(new ByteArrayInputStream(documento.getContenidoDocumento()),documento.getMime(),documento.getNombre());
				return streamedContent;
			}
		} catch (Exception e) {			
			e.printStackTrace();
		}
		return null;	
	}

	public StreamedContent descargarDocumentoRGD(DocumentosRgdRcoa documento){
		try {
			if(documento!=null){
				if (documento.getExtesion().contains(".pdf")){
					if(documento.getContenidoDocumento()==null)
						documento.setContenidoDocumento(documentosFacade.descargar(documento.getIdAlfresco()));	
					return getStreamedContentRGD(documento);
				}else{
					DefaultStreamedContent content = null;
					if(documento.getContenidoDocumento() == null){
						documento.setContenidoDocumento(documentosFacade.descargar(documento.getIdAlfresco()));	
					}					
					if (documento.getNombre() != null && documento.getContenidoDocumento() != null) {
						content = new DefaultStreamedContent(new ByteArrayInputStream(documento.getContenidoDocumento()));
						content.setName(documento.getNombre());
						return content;
					}
				}
			}
			return null;
		} catch (Exception e) {
			return null;
		}
	}

	public static StreamedContent getStreamedContentRGD(DocumentosRgdRcoa documento){		
		try {
			if (documento != null && documento.getContenidoDocumento()!=null) {
				StreamedContent streamedContent = new DefaultStreamedContent(new ByteArrayInputStream(documento.getContenidoDocumento()),documento.getMime(),documento.getNombre());
				return streamedContent;
			}
		} catch (Exception e) {			
			e.printStackTrace();
		}
		return null;	
	}

	
	public StreamedContent descargarDocumentoEIA(DocumentoEstudioImpacto documento){
		try {
			if(documento!=null){
					if(documento.getContenidoDocumento()==null)
						documento.setContenidoDocumento(documentosFacade.descargar(documento.getAlfrescoId()));	
					return getStreamedContentEIA(documento);
			}
			return null;
		} catch (Exception e) {
			return null;
		}
	}
	
	public static StreamedContent getStreamedContentEIA(DocumentoEstudioImpacto documento){		
		try {
			if (documento != null && documento.getContenidoDocumento()!=null) {
				StreamedContent streamedContent = new DefaultStreamedContent(new ByteArrayInputStream(documento.getContenidoDocumento()),documento.getMime(),documento.getNombre());
				return streamedContent;
			}
		} catch (Exception e) {			
			e.printStackTrace();
		}
		return null;		
	}
}