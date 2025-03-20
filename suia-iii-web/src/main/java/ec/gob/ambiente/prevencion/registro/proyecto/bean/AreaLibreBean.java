package ec.gob.ambiente.prevencion.registro.proyecto.bean;

import javax.faces.application.FacesMessage;
import javax.faces.validator.ValidatorException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;


import lombok.Setter;

import org.primefaces.event.FileUploadEvent;

import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.MineroArtesanal;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class AreaLibreBean extends RegistroProyectoBaseBean {

	private static final long serialVersionUID = 5864242456273958146L;

	@Setter
	private MineroArtesanal mineroArtesanal;

	public void aceptar() {
		JsfUtil.addCallbackParam("areaLibreMineroArtesanal");
	}

	public void validateArchivo(FacesContext context, UIComponent validate, Object value) {
		/*if (mineroArtesanal.getRegistroMineroArtesanal() == null)
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "El campo 'Registro de minero artesanal MRNNR' es requerido.", null));*/
	}
		
	public void validateArchivosubir(FacesContext context, UIComponent validate, Object value) {
		if (mineroArtesanal.getRegistroMineroArtesanal() == null)
		throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "El campo 'Autorización para libre aprovechamiento Ministerio de Minería(.pdf)' es requerido.", null));
	}
	
	public void validateArchivosubirlibre(FacesContext context, UIComponent validate, Object value) {
		if (mineroArtesanal.getRegistroMineroArtesanal() == null)
		throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "El campo 'Registro de minero artesanal MRNNR(.pdf))' es requerido.", null));
	}
	

	public boolean isValid() {
		return getMineroArtesanal().getCodigo() != null /*&& getMineroArtesanal().getRegistroMineroArtesanal() != null*/;
	}

	public MineroArtesanal getMineroArtesanal() {
		return mineroArtesanal == null ? mineroArtesanal = new MineroArtesanal() : mineroArtesanal;
	}

	public void uploadListenerRegistroMinero(FileUploadEvent event) {
		Documento documento = super.uploadListener(event);
		mineroArtesanal.setRegistroMineroArtesanal(documento);
	}
}