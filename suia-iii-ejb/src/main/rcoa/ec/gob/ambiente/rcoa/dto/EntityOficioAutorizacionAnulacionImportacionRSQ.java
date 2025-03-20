package ec.gob.ambiente.rcoa.dto;

import javax.ejb.Stateless;

import lombok.Getter;
import lombok.Setter;

@Stateless
public class EntityOficioAutorizacionAnulacionImportacionRSQ {
	
	@Getter
	@Setter
	private String codigoOficio, 
	fecha, 
	nombreSustancia,
	subPartida,
	nombreOperador,
	representanteLegal,
	nombreEmpresa,
	codigoRegistroSustancias,
	numeroTramite,
	fechaActual,
	pesoNeto,
	tablaSustancias,
	subsecretario,
	numeroAnulacion,
	numeroOficioAprobacion,
	fechaAprobacion;

}
