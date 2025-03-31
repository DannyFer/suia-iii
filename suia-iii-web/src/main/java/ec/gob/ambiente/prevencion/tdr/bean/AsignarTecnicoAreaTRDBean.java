package ec.gob.ambiente.prevencion.tdr.bean;

import java.io.Serializable;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import lombok.Getter;
import lombok.Setter;

@ManagedBean
@ViewScoped
public class AsignarTecnicoAreaTRDBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1060872140044002415L;

	@Getter
	@Setter
	private String area;

	@Getter
	@Setter
	private String usuario;

	@PostConstruct
	public void init() {
		usuario = "";
		Map<String, String> params = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap();
		String area = params.get("area");
		if (area != null && !area.isEmpty()) {
			usuario = "u_Tecnico" + area;

		}
	}

}
