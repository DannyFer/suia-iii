package ec.gob.ambiente.prevencion.registro.proyecto.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.Bloque;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;

@ManagedBean
@ViewScoped
public class BloquesBean implements Serializable {

	private static final long serialVersionUID = -2074710458524093593L;

	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;

	@Getter
	private List<Bloque> bloques;

	@Setter
	private String filter;

	public String getFilter() {
		return filter == null ? "" : filter;
	}

	@PostConstruct
	public void init() {
		bloques = proyectoLicenciamientoAmbientalFacade.getBloques(getFilter());
	}

	public void reset() {
		filter = null;
		init();
	}

	public List<Bloque> getBloquesSeleccionados() {
		List<Bloque> result = new ArrayList<Bloque>();
		if (bloques != null) {
			for (Bloque bloque : bloques) {
				if (bloque.isSeleccionado())
					result.add(bloque);
			}
		}
		return result;
	}

	public void setBloqueSeleccionado(Bloque bloqueSeleccionado) {
		for (Bloque bloque : bloques) {
			if (bloqueSeleccionado.equals(bloque))
				bloque.setSeleccionado(true);
		}
	}
}
