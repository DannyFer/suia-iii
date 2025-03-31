package ec.gob.ambiente.rcoa.dto;

import javax.ejb.Stateless;

import lombok.Getter;
import lombok.Setter;

@Stateless
public class EntityRegistroGeneradorDesechoOficio {
	
	@Getter
	@Setter
	private String codigo, fecha, operador, cargo, nombreEmpresa, ruc;
	
	@Getter
	@Setter
	private String provicional, nombreProyecto, codigoRgd, fechaLetras, operadorEmpresa, actividadProyecto, codigoCIIU,
					codigoPermisoRgd, provincia, licenciaEjecucion, adicionalmente, nombreAutoridad, cedula, cargoAutoridad, canton;
	
	
	@Getter
	@Setter
	private String gestionPolitica, acuerdoMinisterial;
	
	@Getter
	@Setter
	private String mostrarProvisional, mostrarDefinitivo, rgdProvisional, fechaRgdProvisional, institucion, mostrarInfoEmpresa;

}
