package ec.gob.ambiente.prevencion.registro.proyecto.bean;

/*import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.validator.ValidatorException;*/
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import lombok.Setter;

import org.primefaces.event.FileUploadEvent;

import ec.gob.ambiente.suia.domain.ConcesionMinera;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.MineroArtesanal;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class AreaConcesionadaBean extends RegistroProyectoBaseBean {

	private static final long serialVersionUID = -2674653199646237869L;

	@Setter
	private ConcesionMinera concesionMinera;

	@Setter
	private MineroArtesanal mineroArtesanal;

	public void aceptar() {
		JsfUtil.addCallbackParam("concesionMineroArtesanal");
	}

	public void validateArchivo(FacesContext context, UIComponent validate, Object value) {		
		List<FacesMessage> messages = new ArrayList<FacesMessage>();
		if (mineroArtesanal.getRegistroMineroArtesanal() == null)
			messages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"El campo 'Registro de minero artesanal MRNNR' es requerido.", null));
		if (!messages.isEmpty())
			throw new javax.faces.validator.ValidatorException(messages);
	}

	public void validateArchivoSubir(FacesContext context, UIComponent validate, Object value) {
		List<FacesMessage> messages = new ArrayList<FacesMessage>();
		/*if (mineroArtesanal.getContratoOperacion() == null)
			messages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"El campo 'Contrato de operación' es requerido.", null));*/
		
//		if (isRequeridoMRNNR()) {		
			if (mineroArtesanal.getRegistroMineroArtesanal() == null)
				messages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"El campo 'Autorización para libre aprovechamiento Ministerio de Minería(.pdf)' es requerido.", null));
			if (!messages.isEmpty())
				throw new javax.faces.validator.ValidatorException(messages);
//		}
	}

	public ConcesionMinera getConcesionMinera() {
		return concesionMinera == null ? concesionMinera = new ConcesionMinera() : concesionMinera;
	}

	public MineroArtesanal getMineroArtesanal() {
		return mineroArtesanal == null ? mineroArtesanal = new MineroArtesanal() : mineroArtesanal;
	}

	public boolean isDatosValidos() {
		if (concesionMinera != null && mineroArtesanal != null && concesionMinera.getCodigo() != null
				&& concesionMinera.getNombre() != null && mineroArtesanal.getCodigo() != null
				/*&& mineroArtesanal.getContratoOperacion() != null
				&& mineroArtesanal.getRegistroMineroArtesanal() != null*/)
			return true;
		return false;
	}

	public void uploadListenerContratoOperacion(FileUploadEvent event) {
		Documento documento = super.uploadListener(event);
		mineroArtesanal.setContratoOperacion(documento);
	}

	public void uploadListenerRegistroMinero(FileUploadEvent event) {
		Documento documento = super.uploadListener(event);
		mineroArtesanal.setRegistroMineroArtesanal(documento);
	}
}