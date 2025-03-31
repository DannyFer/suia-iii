package ec.gob.ambiente.control.denuncias.bean;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.Denuncia;
import java.io.Serializable;

@ManagedBean
@ViewScoped
public class SolicitarAcompaniamientoBean implements Serializable{
    
    private static final long serialVersionUID = -3553371287113629686L;

	@Getter
	@Setter
	private String[] listaApoyo;

	@Getter
	@Setter
	private Boolean requiereApoyo;

	@Getter
	@Setter
	private String areasDisponibles;

	@Getter
	@Setter
	private String opcion;

	@Getter
	@Setter
	private Denuncia denuncia;

	public SolicitarAcompaniamientoBean() {
		this.requiereApoyo = false;
		this.areasDisponibles = "Unidad 1, Unidad 3,unidad 2";
	}

}
