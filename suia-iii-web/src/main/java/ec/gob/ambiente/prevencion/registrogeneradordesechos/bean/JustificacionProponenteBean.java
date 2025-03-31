package ec.gob.ambiente.prevencion.registrogeneradordesechos.bean;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import ec.gob.ambiente.suia.comun.bean.AdicionarDesechosPeligrososBean;
import lombok.Getter;
import lombok.Setter;

import org.primefaces.event.FileUploadEvent;

import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.GeneradorDesechosDesechoPeligroso;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class JustificacionProponenteBean extends RegistroGeneradorBaseBean {

	private static final long serialVersionUID = -679661320306514525L;

	@Getter
	@Setter
	private boolean seEliminoPuntoGeneracionODesecho = false;

	public void uploadListener(FileUploadEvent event) {
		Documento documento = super.uploadListener(event, GeneradorDesechosDesechoPeligroso.class);
		JsfUtil.getBean(RegistroGeneradorDesechoBean.class).getGeneradorDesechosPeligrosos()
				.setDocumentoJustificacionProponente(documento);
	}

	public void validateArchivo(FacesContext context, UIComponent validate, Object value) {
		if (JsfUtil.getBean(RegistroGeneradorDesechoBean.class).getGeneradorDesechosPeligrosos()
				.getDocumentoJustificacionProponente() == null || JsfUtil.getBean(AdicionarDesechosPeligrososBean.class).getModificacionesEliminaciones())
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"El campo 'Jusfiticaciones (.pdf)' es requerido.", null));
	}

}
