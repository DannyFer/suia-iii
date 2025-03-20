package ec.gob.ambiente.control.inspeccionescontrolambiental.controller;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class SubsecretariaRevisarInformeOficioICAController extends DirectorRevisarInformeOficioICAController {

	private static final long serialVersionUID = 4650344447254363594L;

	@Override
	public String getDiscriminador() {
		return "subsecretaria";
	}
	
	@Override
	public void validarTareaBpm() {
		JsfUtil.validarPaginasUrlTareasBpm(JsfUtil.getCurrentTask(),
				"/control/inspeccionescontrolambiental/subsecretariaRevisarInformeOficioPronunciamiento.jsf");
	}
}
