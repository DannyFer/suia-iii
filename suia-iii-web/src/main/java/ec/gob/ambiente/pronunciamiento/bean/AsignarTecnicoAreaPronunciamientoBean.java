package ec.gob.ambiente.pronunciamiento.bean;

import lombok.Getter;
import lombok.Setter;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import ec.gob.ambiente.suia.utils.JsfUtil;

import java.io.Serializable;
import java.util.Map;

@ManagedBean
@ViewScoped
public class AsignarTecnicoAreaPronunciamientoBean implements Serializable {

	private static final long serialVersionUID = 2758030304229638966L;

	private static final Logger LOGGER = Logger.getLogger(AsignarEquipoMultidisciplinarioPronunciamientoBean.class);

	@Getter
	@Setter
	private String area;

	@Getter
	@Setter
	private String usuario;

	@PostConstruct
	public void init() {
		try {
			usuario = "";
			Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext()
					.getRequestParameterMap();
			String area = params.get("area");
			if (area != null && !area.isEmpty()) {
				usuario = area.replace("coordinador", "tecnico");

			}
		} catch (Exception exception) {
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_INICIAR_TAREA);
			LOGGER.error(JsfUtil.MESSAGE_ERROR_INICIAR_TAREA, exception);
		}

	}

}
