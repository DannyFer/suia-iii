package ec.gob.ambiente.control.denuncias.bean;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Denuncia;
import ec.gob.ambiente.suia.domain.RemitirDenuncia;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import java.io.Serializable;

@ManagedBean
@ViewScoped
public class RemitirDenunciaBean implements Serializable{
    
    private static final long serialVersionUID = -3546371287113629686L;

	public RemitirDenunciaBean() {
		remitirDenuncia = new RemitirDenuncia();
	}

	@Getter
	@Setter
	private String opcion;

	@Getter
	@Setter
	private List<Area> listaEntesAcreditados;

	@Getter
	@Setter
	private List<Area> listaDireccionesProvinciales;

	@Getter
	@Setter
	private RemitirDenuncia remitirDenuncia;
	@Getter
	@Setter
	private Boolean enviarDP;
	@Getter
	@Setter
	private Boolean enviarGads;
	@Getter
	@Setter
	private Boolean enviarPC;
	@Getter
	@Setter
	private Integer idTarea;

	@Getter
	@Setter
	private Denuncia denuncia;

	@Getter
	@Setter
	private UbicacionesGeografica provincia;

	@Getter
	@Setter
	private UbicacionesGeografica canton;

	@Getter
	@Setter
	private UbicacionesGeografica parroquia;
}
