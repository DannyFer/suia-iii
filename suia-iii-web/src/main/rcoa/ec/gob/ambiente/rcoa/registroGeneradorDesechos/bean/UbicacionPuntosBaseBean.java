package ec.gob.ambiente.rcoa.registroGeneradorDesechos.bean;

import javax.annotation.PostConstruct;

import lombok.Getter;
import lombok.Setter;

import org.primefaces.event.ToggleEvent;
import org.primefaces.model.Visibility;

public class UbicacionPuntosBaseBean {
	
	@Getter
	private boolean panelAdicionarVisible;

	@Getter
	@Setter
	private boolean editar;

	public void toggleHandle(ToggleEvent event) {
		
		if (event.getVisibility().equals(Visibility.HIDDEN)) {
			panelAdicionarVisible = false;
			editar = false;
		} else {
			panelAdicionarVisible = true;
		}
	}

	@PostConstruct
	public void init() {
		panelAdicionarVisible = false;
		editar = false;
	}

}
