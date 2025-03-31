package ec.gob.ambiente.rcoa.dto;

import java.util.List;

import javax.ejb.Stateless;

import lombok.Getter;
import lombok.Setter;

@Stateless
public class EntityReporteImportacionVueRSQDTO {
	
	@Getter
	@Setter
	private String tipoSolicitud, fechaInicio,
	fechaFin, tipoDocumento;
		
	@Getter
	@Setter
	private String tablaReporteSolicitudes;
	
	@Getter
	@Setter
	private List<EntityTablaReporteImportacionesVueRSQDTO> lTablaReporteSolicitudes;
	
	@Getter
	@Setter
	private String nombreReporte;
}