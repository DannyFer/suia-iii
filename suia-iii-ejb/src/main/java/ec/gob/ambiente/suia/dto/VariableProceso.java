package ec.gob.ambiente.suia.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class VariableProceso {

	private Long processInstanceId;

	private String variableId;

	public VariableProceso() {

	}

	public VariableProceso(Long processInstanceId, String variableId) {
		this.processInstanceId = processInstanceId;
		this.variableId = variableId;
	}
}
