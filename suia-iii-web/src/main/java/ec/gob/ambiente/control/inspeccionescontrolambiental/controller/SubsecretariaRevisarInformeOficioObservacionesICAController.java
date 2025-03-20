package ec.gob.ambiente.control.inspeccionescontrolambiental.controller;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class SubsecretariaRevisarInformeOficioObservacionesICAController
		extends DirectorRevisarInformeOficioObservacionesICAController {

	private static final long serialVersionUID = 3628640248134441545L;

	@Override
	public String getDiscriminador() {
		return "subsecretaria";
	}
	
	@Override
	public void validarTareaBpm() {
		JsfUtil.validarPaginasUrlTareasBpm(JsfUtil.getCurrentTask(),
				"/control/inspeccionescontrolambiental/subsecretariaRevisarInformeOficioObservaciones.jsf");
	}
}
