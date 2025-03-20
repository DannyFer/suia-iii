package ec.gob.ambiente.rcoa.dto;

import java.util.List;

import javax.ejb.Stateless;

import lombok.Getter;
import lombok.Setter;

@Stateless
public class EntityOficioAutorizacionCorrecionImportacionVueRSQ {
	
	@Getter
	@Setter
	private String codigoOficio, 
	fecha, 
	nombreOperador,
	representanteLegal,
	nombreEmpresa,
	codigoRegistroSustancias,
	numeroTramite,
	fechaActual,
	tablaSustancias,
	subsecretario,
	numeroCorreccion,
	numeroOficioAprobacion,
	fechaAprobacion,
	rolFirmante;

	@Getter
	@Setter
	private List<EntityTablaOficioAutorizacionCorrecionImportacionVueRSQ> tablaProductos;
}
