package ec.gob.ambiente.suia.dto;

import javax.ejb.Stateless;

import lombok.Getter;
import lombok.Setter;

@Stateless
public class EntityCertificadoAmbientalAmb {
	
	@Getter
	@Setter
	private String numeroResolucion, provincia, fechaActual, nombreProyecto, ubicacionProyecto, operador, actividad, direccion;
	
	@Getter
	@Setter
	private String nombreRepresentanteLegal, emailpromotor, telefonopromotor, codigoproy, direccionpromotor, 
	nombreAutoridad, nombreDireccionProvincial, cedulausu, codigoQrFirma;
	
	
	

}
