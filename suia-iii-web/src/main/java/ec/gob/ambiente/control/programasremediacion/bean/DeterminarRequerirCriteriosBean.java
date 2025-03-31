package ec.gob.ambiente.control.programasremediacion.bean;

import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

@ManagedBean
@ViewScoped
public class DeterminarRequerirCriteriosBean implements Serializable {
    	private static final long serialVersionUID = -3526371287113624686L;

	@Getter
	@Setter
	private String[] listaApoyo;

	@Getter
	@Setter
	private Boolean requiereApoyo;

	@Getter
	@Setter
	private String areasDisponibles;

	@PostConstruct
	public void init() {
		this.requiereApoyo = false;
		this.areasDisponibles = "Calidad Amiental, Patrimonio Provincial,PRAS";
	}

}
