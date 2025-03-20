package ec.gob.ambiente.suia.tramiteresolver.classes;

import lombok.Getter;

public class LabelModel {

	@Getter
	private String labelText;

	@Getter
	private Object fieldValue;

	public LabelModel(String labelText, Object fieldValue) {
		this.labelText = labelText;
		this.fieldValue = fieldValue;
	}
}
