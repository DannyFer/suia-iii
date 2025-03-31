package ec.gob.ambiente.prevencion.tdr.bean;

import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

@ManagedBean
@ViewScoped
public class AnalizarTDRBean implements Serializable {

	private static final long serialVersionUID = 2975846042323455827L;
	@Getter
	@Setter
	private boolean equipoMultidiciplinario;

	@PostConstruct
	public void init() {

	}

}
