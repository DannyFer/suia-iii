package ec.gob.ambiente.prevencion.registrogeneradordesechos.bean;

import java.io.ByteArrayInputStream;
import java.io.Serializable;

import javax.annotation.PostConstruct;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.Visibility;

import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.utils.JsfUtil;

public class RegistroGeneradorBaseBean implements Serializable {

	private static final long serialVersionUID = 4109029776923657036L;

	private static final Logger LOGGER = Logger.getLogger(RegistroGeneradorBaseBean.class);

	@Getter
	private boolean panelAdicionarVisible;

	@Getter
	@Setter
	private boolean editar;

	public void toggleHandle(ToggleEvent event) {

		RecoleccionTransporteDesechosBean registro = JsfUtil.getBean(RecoleccionTransporteDesechosBean.class);
		if(registro.getGeneradorDesechosRecolector().getDesechoPeligroso()==null) {
			RecoleccionTransporteDesechosBean desechos = new RecoleccionTransporteDesechosBean();
			desechos.resetearValores();
		}

		if (event.getVisibility().equals(Visibility.HIDDEN)) {
			panelAdicionarVisible = false;
			editar = false;
		} else
			panelAdicionarVisible = true;
	}

	@PostConstruct
	public void init() {
		panelAdicionarVisible = false;
		editar = false;
	}

	public Documento uploadListener(FileUploadEvent event, Class<?> clazz) {
		byte[] contenidoDocumento = event.getFile().getContents();	
		Documento documento = new Documento();
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

	public StreamedContent getStreamContent(Documento documento) throws Exception {
		DefaultStreamedContent content = null;
		try {
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
