package ec.gob.ambiente.rcoa.viabilidadAmbientalSnap.controllers;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.rcoa.model.DetalleInterseccionProyectoAmbiental;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;

public class UbicacionAreaProtegida {

	@Getter
	@Setter
	private DetalleInterseccionProyectoAmbiental interseccion;
	
	@Getter
	@Setter
	private UbicacionesGeografica parroquia;
	
}
