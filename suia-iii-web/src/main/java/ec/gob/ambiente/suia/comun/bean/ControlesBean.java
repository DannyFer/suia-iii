package ec.gob.ambiente.suia.comun.bean;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import lombok.Getter;

import org.primefaces.event.ToggleEvent;
import org.primefaces.model.Visibility;

import ec.gob.ambiente.suia.utils.Constantes;

@ManagedBean
@SessionScoped
public class ControlesBean implements Serializable {

	private static final long serialVersionUID = -256234576384095509L;

	@Getter
	private boolean contextoActividadToggleVisible = true;

	public void contextoActividadToggleListener(ToggleEvent event) {
		contextoActividadToggleVisible = event.getVisibility().equals(Visibility.VISIBLE);
	}
	
	public String getUrlMaeTransparente() {
		return Constantes.getLinkMaeTransparente();
	}
}
