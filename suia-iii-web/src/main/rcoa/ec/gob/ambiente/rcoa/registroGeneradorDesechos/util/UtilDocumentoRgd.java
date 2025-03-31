package ec.gob.ambiente.rcoa.registroGeneradorDesechos.util;

import java.io.ByteArrayInputStream;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.DocumentosRgdRcoa;

public class UtilDocumentoRgd {
	
	public static StreamedContent getStreamedContent(DocumentosRgdRcoa documento){		
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
