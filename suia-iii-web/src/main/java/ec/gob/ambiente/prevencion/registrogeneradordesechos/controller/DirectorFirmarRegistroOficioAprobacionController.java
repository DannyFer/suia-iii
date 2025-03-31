package ec.gob.ambiente.prevencion.registrogeneradordesechos.controller;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class DirectorFirmarRegistroOficioAprobacionController extends FirmarDocumentosRGController {

	private static final long serialVersionUID = -9090374371057483531L;

	public void validarTareaBpm() {
		JsfUtil.validarPaginasUrlTareasBpm(JsfUtil.getCurrentTask(),
				"/prevencion/registrogeneradordesechos/directorFirmarRegistroOficioAprobacion.jsf");
	}
}
