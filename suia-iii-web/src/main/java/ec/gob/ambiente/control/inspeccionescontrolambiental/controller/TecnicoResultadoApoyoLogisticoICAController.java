package ec.gob.ambiente.control.inspeccionescontrolambiental.controller;

import java.util.HashMap;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;

@ManagedBean
@ViewScoped
public class TecnicoResultadoApoyoLogisticoICAController extends TecnicoTramiteICAController {

	private static final long serialVersionUID = -2838283872673864339L;

	@Getter
	@Setter
	private boolean esApoyoLogisticoResuelto = true;

	@Getter
	@Setter
	private String apoyoLogisticoDescripcion;

	@Override
	public Map<String, Object> generateParameters() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("esApoyoLogisticoResuelto", esApoyoLogisticoResuelto);
		params.put("apoyoLogisticoDescripcion", apoyoLogisticoDescripcion);
		return params;
	}

	@Override
	public String getPage() {
		return "tecnicoResponsableResultadoApoyoLogistico.jsf";
	}

	public String getLabel() {
		return esApoyoLogisticoResuelto ? "Comentarios adicionales" : "Motivos o impidimentos *";
	}

	@Override
	public boolean validate() {
		if(!esApoyoLogisticoResuelto && (apoyoLogisticoDescripcion == null || apoyoLogisticoDescripcion.isEmpty())) {
			JsfUtil.addMessageError("El campo 'Motivos o impidimentos' es requerido.");
			return false;
		}
		return true;
	}
}
