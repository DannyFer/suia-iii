package ec.gob.ambiente.prevencion.registrogeneradordesechos.bean;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ec.gob.ambiente.suia.comun.bean.AdicionarDesechosPeligrososBean;

@ManagedBean
@ViewScoped
public class AdicionarDesechosPeligrososBean1 extends AdicionarDesechosPeligrososBean {

	private static final long serialVersionUID = -7425865481377271123L;

	@PostConstruct
	public void init() {
		super.init();
		dialogoDesecho = "seleccionarDesecho1";
		filtroDesecho = "filtroDesecho1";
		arbolCatalogoDesecho = "arbolCatalogoDesecho1";
		desechoContainer = "desechoContainer1";
		tableDesechos = "tableDesechos1";
		desechosContainerGeneral = "desechosContainerGeneral1";
	}
}
