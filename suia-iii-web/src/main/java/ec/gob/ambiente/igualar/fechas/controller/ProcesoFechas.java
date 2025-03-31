package ec.gob.ambiente.igualar.fechas.controller;

import java.sql.Timestamp;

import lombok.Getter;
import lombok.Setter;

public class ProcesoFechas {
	
	@Getter
	@Setter
	private Integer taskIdAnterior;
	
	@Getter
	@Setter
	private Timestamp createdDateAnterior;
	
	@Getter
	@Setter
	private Timestamp startDateAnterior;
	
	@Getter
	@Setter
	private Timestamp endDateAnterior;
	
	@Getter
	@Setter
	private String statusAnterior;
	
	@Getter
	@Setter
	private String taskNameAnterior;
	
	@Getter
	@Setter
	private String userIdAnterior;
	
	@Getter
	@Setter
	private Integer taskIdActual;
	
	@Getter
	@Setter
	private String taskNameActual;
	
	@Getter
	@Setter
	private String userIdActual;
	
	@Getter
	@Setter
	private Integer idProcesoAnterior;
	
	@Getter
	@Setter
	private Integer idProcesoActual;
}
