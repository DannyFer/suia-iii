package ec.gob.ambiente.suia.bandeja.beans;

import java.util.Map;

import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.suia.dto.TaskSummaryCustom;
import lombok.Getter;
import lombok.Setter;

public class ContenedorTaskSummaryCustom {
	
	@Getter
	@Setter
	private Map<Long, TaskSummaryCustom> mapaTaskSummaryCustom;
	
	@Getter
	@Setter
	private Map<String, ProyectoLicenciaCoa> mapaProyectoLicenciaCoa;
	
	public ContenedorTaskSummaryCustom() {
		
	}
	
	public ContenedorTaskSummaryCustom(Map<Long, TaskSummaryCustom> mapaTaskSummaryCustom, Map<String, ProyectoLicenciaCoa> mapaProyectoLicenciaCoa) {
		this.mapaTaskSummaryCustom = mapaTaskSummaryCustom;
		this.mapaProyectoLicenciaCoa = mapaProyectoLicenciaCoa;
	}

}
