package ec.gob.ambiente.suia.recaudaciones.model;

import java.util.Date;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;


public class EntityRegistroBancario {
	
	@Getter
	@Setter
	private Integer id;

	@Getter
	@Setter
	private String referenciaDocumento;

	@Getter
	@Setter
	private Double valorPago;
	
	@Getter
	@Setter
	@Temporal(TemporalType.TIMESTAMP)
	private Date fechaPago;

	@Getter
	@Setter
	private String codigoNut;
}