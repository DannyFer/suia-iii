package ec.gob.ambiente.prevencion.registrogeneradordesechos.controller;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class SubsecretariaFirmarRegistroOficioAprobacionController extends FirmarDocumentosRGController {

	private static final long serialVersionUID = 650460389054629412L;

	public void validarTareaBpm() {
		JsfUtil.validarPaginasUrlTareasBpm(JsfUtil.getCurrentTask(),
				"/prevencion/registrogeneradordesechos/subsecretariaFirmarRegistroOficioAprobacion.jsf");
	}
}
