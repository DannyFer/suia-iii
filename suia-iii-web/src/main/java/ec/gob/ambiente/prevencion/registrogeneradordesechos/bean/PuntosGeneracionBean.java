package ec.gob.ambiente.prevencion.registrogeneradordesechos.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.PuntoGeneracion;
import ec.gob.ambiente.suia.prevencion.registrogeneradordesechos.facade.RegistroGeneradorDesechosFacade;

@ManagedBean
@ViewScoped
public class PuntosGeneracionBean implements Serializable {

	private static final long serialVersionUID = -2074710458524093593L;

	@EJB
	private RegistroGeneradorDesechosFacade registroGeneradorDesechosFacade;

	@Getter
	private List<PuntoGeneracion> puntosGeneracion;

	@Setter
	private String filter;

	public String getFilter() {
		return filter == null ? "" : filter;
	}

	@PostConstruct
	public void init() {
		puntosGeneracion = registroGeneradorDesechosFacade.getPuntosGeneracion(getFilter());
	}

	public void reset() {
		filter = null;
		init();
	}

	public List<PuntoGeneracion> getPuntosGeneracionSeleccionados() {
		List<PuntoGeneracion> result = new ArrayList<PuntoGeneracion>();
		if (puntosGeneracion != null) {
			for (PuntoGeneracion punto : puntosGeneracion) {
				if (punto.isSeleccionado())
					result.add(punto);
			}
		}
		return result;
	}

	public void setPuntosGeneracionSeleccionados(List<PuntoGeneracion> puntosGeneracionSeleccionados) {
		for (PuntoGeneracion puntoGeneracion : puntosGeneracionSeleccionados) {
			for (PuntoGeneracion punto : puntosGeneracion) {
				if (punto.equals(puntoGeneracion))
					punto.setSeleccionado(true);
			}
		}
	}

	public void setPuntoGeneracionSeleccionado(PuntoGeneracion puntoGeneracionSeleccionado) {
		for (PuntoGeneracion punto : puntosGeneracion) {
			if (puntoGeneracionSeleccionado.equals(punto))
				punto.setSeleccionado(true);
		}
	}
}
