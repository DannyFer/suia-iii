package ec.gob.ambiente.prevencion.registrogeneradordesechos.bean;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ec.gob.ambiente.suia.comun.bean.AdicionarUbicacionesBean;

@ManagedBean
@ViewScoped
public class AdicionarUbicacionesEmpresaPrestadoraServiciosBean extends AdicionarUbicacionesBean {

	private static final long serialVersionUID = -8563108735809094201L;

	@PostConstruct
	public void init() {
		super.init();
		tablaUbicaciones = "tbl_ubicaciones2";
		dialogWidgetVar = "adicionarUbicaciones2";
		panelUbicacion = "panelUbicacion2";

		mostrarParroquias = false;
	}

}
