package ec.gob.ambiente.rcoa.registroGeneradorDesechos.controllers;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.retce.model.InformacionProyecto;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class MotivoEmisionController {
	
	private Integer tipoEmision;

	private String url;
	
	public Integer getTipoEmision() {
		return tipoEmision;
	}

	public void setTipoEmision(Integer tipoEmision) {
		this.tipoEmision = tipoEmision;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	@Getter
    @Setter
    @ManagedProperty(value = "#{emisionGeneradorConAAA}")
    private EmisionGeneradorConAAAController emisionGeneradorConAAA;

	@PostConstruct
	public void init(){
		
		
	}
	
	public void redirigirMotivo(){
		JsfUtil.redirectTo("/pages/rcoa/generadorDesechos/motivoEmision.jsf");
	}
	
	public void redirigir(){
		System.out.println(tipoEmision);
		JsfUtil.cargarObjetoSession("motivoEmision", tipoEmision);
		switch (tipoEmision) {
		case 1:			
			JsfUtil.redirectTo("/pages/rcoa/generadorDesechos/generadorConAAA.jsf");			
			break;
		case 2:
			JsfUtil.redirectTo("/pages/rcoa/generadorDesechos/generadorConAAA.jsf");			
			break;
		case 3:
			JsfUtil.redirectTo("/pages/rcoa/generadorDesechos/generadorVariosProyectos.jsf");
			break;
		case 4:
			JsfUtil.redirectTo("");
			break;

		default:
			break;
		}
	}
}
