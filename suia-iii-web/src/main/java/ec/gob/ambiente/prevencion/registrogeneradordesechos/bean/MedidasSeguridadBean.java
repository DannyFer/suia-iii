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
import ec.gob.ambiente.suia.domain.TipoMedidaSeguridad;
import ec.gob.ambiente.suia.prevencion.registrogeneradordesechos.facade.RegistroGeneradorDesechosFacade;

@ManagedBean
@ViewScoped
public class MedidasSeguridadBean implements Serializable {

	private static final long serialVersionUID = -2074710458524093593L;

	@EJB
	private RegistroGeneradorDesechosFacade registroGeneradorDesechosFacade;

	@Getter
	private List<TipoMedidaSeguridad> tiposMedidasSeguridad;

	@Setter
	private String filter;

	@Getter
	@Setter
	private String otro;

	@Setter
	private boolean otroSeleccionado;

	public String getFilter() {
		return filter == null ? "" : filter;
	}

	public boolean isOtroSeleccionado() {
		return otroSeleccionado;
	}

	@PostConstruct
	public void init() {
		tiposMedidasSeguridad = registroGeneradorDesechosFacade.getTiposMedidasSeguridad(getFilter());
	}

	public void reset() {
		filter = null;
		otro = null;
		otroSeleccionado = false;
		init();
	}

	public List<TipoMedidaSeguridad> getTiposMedidasSeguridadSeleccionadas() {
		List<TipoMedidaSeguridad> result = new ArrayList<TipoMedidaSeguridad>();
		if (tiposMedidasSeguridad != null) {
			for (TipoMedidaSeguridad medida : tiposMedidasSeguridad) {
				if (medida.isSeleccionado())
					result.add(medida);
			}
		}
		return result;
	}

	public void setTiposMedidasSeguridadSeleccionadas(List<TipoMedidaSeguridad> medidasSeleccionadas) {
		for (TipoMedidaSeguridad medida : tiposMedidasSeguridad) {
			medida.setSeleccionado(false);
		}
		for (TipoMedidaSeguridad tipoMedidaSeguridad : medidasSeleccionadas) {
			for (TipoMedidaSeguridad medida : tiposMedidasSeguridad) {
				if (medida.equals(tipoMedidaSeguridad))
					medida.setSeleccionado(true);
			}
		}
	}

	public void setMedidaSeleccionada(TipoMedidaSeguridad medidaSeleccionada) {
		if (medidaSeleccionada.getId().equals(TipoMedidaSeguridad.TIPO_MEDIDA_SEGURIDAD_OTRO)
				&& medidaSeleccionada.isSeleccionado())
			setOtroSeleccionado(true);
		else if (medidaSeleccionada.getId().equals(TipoMedidaSeguridad.TIPO_MEDIDA_SEGURIDAD_OTRO)
				&& !medidaSeleccionada.isSeleccionado()) {
			setOtroSeleccionado(false);
			setOtro(null);
		}
	}
}
