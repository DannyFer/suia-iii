package ec.gob.ambiente.rcoa.registroGeneradorDesechos.bean;

import java.io.ByteArrayInputStream;
import java.io.Serializable;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.Visibility;

import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.DocumentosRgdRcoa;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;

public class RegistroGeneradorBaseRcoaBean implements Serializable {
	
	private static final long serialVersionUID = 4109029776923657036L;

	private static final Logger LOGGER = Logger.getLogger(RegistroGeneradorBaseRcoaBean.class);

	@Getter
	private boolean panelAdicionarVisible;

	@Getter
	@Setter
	private boolean editar;
	
	@PostConstruct
	public void init() {
		panelAdicionarVisible = false;
		editar = false;
	}
	
	public void toggleHandle(ToggleEvent event) {
		
		if (event.getVisibility().equals(Visibility.HIDDEN)) {
			panelAdicionarVisible = false;
			editar = false;
		} else {
			panelAdicionarVisible = true;
		}
	}

	public DocumentosRgdRcoa uploadListener(FileUploadEvent event, Class<?> clazz) {
		byte[] contenidoDocumento = event.getFile().getContents();	
		DocumentosRgdRcoa documento = new DocumentosRgdRcoa();
		documento.setContenidoDocumento(contenidoDocumento);
		documento.setNombreTabla(clazz.getSimpleName());
		documento.setIdTable(0);		
		documento.setMime(event.getFile().getContentType());		
		String fileName=event.getFile().getFileName();
		String[] split=fileName.split("\\.");
		documento.setExtesion("."+split[split.length-1]);		
		documento.setNombre(event.getFile().getFileName());
		return documento;
	}

	public StreamedContent getStreamContent(DocumentosRgdRcoa documento) throws Exception {
		DefaultStreamedContent content = null;
		try {
			if (documento != null && documento.getNombre() != null && documento.getContenidoDocumento() != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documento.getContenidoDocumento()));
				content.setName(documento.getNombre());
			} else {
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
			}
		} catch (Exception exception) {
			LOGGER.error(JsfUtil.MESSAGE_ERROR_ALFRESCO, exception);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
		}
		return content;
	}

}
