package ec.gob.ambiente.rcoa.registroAmbiental.controller;


import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;


@ManagedBean
@ViewScoped
public class MenuRegistroAmbientalCoaController {

	@Getter
    @Setter
	private boolean pageMarcoLegal = false, pageDescripcion = false,
			pagePlanConstruccion = false, pagePlanOperacion = false, pagePlanCierre = false,
			pagePPC = false, pageDescripcionViabilidad = false, 
			pagePlanConstruccionViab = false, pagePlanOperacionViab = false;

}
