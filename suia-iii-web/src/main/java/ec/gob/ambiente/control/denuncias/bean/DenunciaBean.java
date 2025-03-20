package ec.gob.ambiente.control.denuncias.bean;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.primefaces.model.UploadedFile;

import ec.gob.ambiente.suia.domain.Denuncia;
import ec.gob.ambiente.suia.domain.Denunciante;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import java.io.Serializable;

@ViewScoped
@ManagedBean
public class DenunciaBean implements Serializable{
    
    private static final long serialVersionUID = -3521371287113629686L;

	public DenunciaBean() {
		denuncia = new Denuncia();
		denunciante = new Denunciante();
		desabilitarCampos = false;
	}

	@Setter
	@Getter
	private Denuncia denuncia;

	@Setter
	@Getter
	private Denunciante denunciante;

	@Setter
	@Getter
	private Boolean desabilitarCampos;

	@Setter
	@Getter
	private UploadedFile file;

	@Setter
	@Getter
	private List<UbicacionesGeografica> provincias;

	@Setter
	@Getter
	private List<UbicacionesGeografica> cantones;

	@Setter
	@Getter
	private List<UbicacionesGeografica> parroquias;

	@Setter
	@Getter
	private UbicacionesGeografica provincia;

	@Setter
	@Getter
	private UbicacionesGeografica canton;

	@Setter
	@Getter
	private UbicacionesGeografica parroquia;
}
