package ec.gob.ambiente.control.aprobacionrequisitostecnicos.bean;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.InformacionPatioManiobra;

@ManagedBean
@ViewScoped
public class InformacionPatioManiobrasBean {

	@Setter
	@Getter
	private InformacionPatioManiobra informacionPatioManiobra;

	@Setter
	@Getter
	private UbicacionesGeografica provincia;

	@Setter
	@Getter
	private UbicacionesGeografica canton;

	@Setter
	@Getter
	private UbicacionesGeografica parroquia;
	
	@Setter
	@Getter
	private List<UbicacionesGeografica> provincias;
	
	@Setter
	@Getter
	private List<UbicacionesGeografica> cantones;
	
	@Setter
	@Getter
	private List<UbicacionesGeografica> parroquias;
}
