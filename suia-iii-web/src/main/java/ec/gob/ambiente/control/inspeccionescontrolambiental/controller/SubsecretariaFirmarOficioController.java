package ec.gob.ambiente.control.inspeccionescontrolambiental.controller;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class SubsecretariaFirmarOficioController extends DirectorFirmarOficioController {

	private static final long serialVersionUID = 7935428172934754592L;

	public void validarTareaBpm() {
		JsfUtil.validarPaginasUrlTareasBpm(JsfUtil.getCurrentTask(),
				"/control/inspeccionescontrolambiental/subsecretariaFirmarOficio.jsf");
	}
}
