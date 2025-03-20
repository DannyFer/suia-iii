package ec.gob.ambiente.prevencion.registrogeneradordesechos.bean;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class TipoEliminacionDesechoBean1 extends TipoEliminacionDesechoBean {

	private static final long serialVersionUID = -6524983761454498462L;

	@PostConstruct
	public void inicializar() {
		super.inicializar();
		dialogSeleccionarTipoEliminacionDesecho = "seleccionarTipoEliminacionDesecho1";
		filtroTipoEliminacionDesecho = "filtroTipoEliminacionDesecho1";
		filterButtonTipoEliminacion = "filterButtonTipoEliminacion1";
		arbolCatalogoTipoEliminacionDesecho = "arbolCatalogoTipoEliminacionDesecho1";
		tipoEliminacionDesechoContainer = "tipoEliminacionDesechoContainer1";
		textoAdicionalOtroSeleccionadoContainer = "textoAdicionalOtroSeleccionadoContainer1";
		tipoEliminacionDesechoBtn = "tipoEliminacionDesechoBtn1";
		tipoEliminacionDesechoBtnLabel = "tipoEliminacionDesechoBtnLabel1";
		textoAdicionalOtroId = "textoAdicionalOtroId1";
	}

}
