package ec.gob.ambiente.prevencion.licenciamiento.requistosprevios.bean.OLD;

import ec.gob.ambiente.suia.domain.ClaveOperacion;
import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import lombok.Getter;
import lombok.Setter;

public class ModalidadManejoDesechosBean {

	@Getter
	@Setter
	private DesechoPeligroso desechoPeligroso;
	@Getter
	@Setter
	private ClaveOperacion claveManejoDeschos;
	@Getter
	@Setter
	private String observaciones;
	@Getter
	@Setter
	private ClaveOperacion claveCategoria;
	@Getter
	@Setter
	private String nombreEmpresaGeneradora;
	@Getter
	@Setter
	private String registroEmpresaGeneradora;
	@Getter
	@Setter
	private String nombreEmpresaPrestadoraServicio;
	@Getter
	@Setter
	private String licenciaEmpresaPrestadoraServicio;

}
