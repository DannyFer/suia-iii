package ec.gob.ambiente.control.inspeccionescontrolambiental.controller;

import java.util.HashMap;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

@ManagedBean
@ViewScoped
public class TecnicoRecibirSolicitudICAController extends TecnicoTramiteICAController {

	private static final long serialVersionUID = -2838283872673864339L;

	@Getter
	@Setter
	private boolean esApoyoRequerido = false;

	@Override
	public Map<String, Object> generateParameters() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("esApoyoRequerido", esApoyoRequerido);
		return params;
	}

	@Override
	public String getPage() {
		return "tecnicoResponsableRecibirSolicitud.jsf";
	}

}
