package ec.gob.ambiente.prevencion.licenciamiento.requistosprevios.bean.OLD;

import ec.gob.ambiente.suia.domain.CatalogoGeneral;
import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import lombok.Getter;
import lombok.Setter;

public class AlmacenamientoTemporalDesechosBean {

	@Getter
	@Setter
	private String identificacionAlmacen;
	@Getter
	@Setter
	private DesechoPeligroso desechoPeligroso;
	@Getter
	@Setter
	private CatalogoGeneral estadoLocal;
	@Getter
	@Setter
	private CatalogoGeneral tipoVentilacion;
	@Getter
	@Setter
	private CatalogoGeneral materialConstruccion;
	@Getter
	@Setter
	private CatalogoGeneral tipoIluminacion;
	@Getter
	@Setter
	private String cantidad;
	@Getter
	@Setter
	private CatalogoGeneral unidadMedida;
	@Getter
	@Setter
	private String tieneFosas;
	@Getter
	@Setter
	private String capacidadFosas;
	@Getter
	@Setter
	private String tieneTrincherasCanaletas;
	@Getter
	@Setter
	private String tieneSistemaExtincion;
	@Getter
	@Setter
	private String tieneSenalamiento;
	@Getter
	@Setter
	private String tieneSistemaAlarma;




}
