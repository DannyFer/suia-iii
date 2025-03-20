package ec.gob.ambiente.control.denuncias.bean;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.Denuncia;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import java.io.Serializable;

@SessionScoped
@ManagedBean
public class DenunciaIncludeBean implements Serializable{
    
    private static final long serialVersionUID = -3522371287113629686L;

	@Setter
	@Getter
	private Denuncia denuncia;

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
