package ec.gob.ambiente.core.validators;

import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator("dateRangeValidator")
public class DateRangeValidator implements Validator {

	@Override
	public void validate(FacesContext context, UIComponent component,
			Object value) throws ValidatorException {
		if (value == null) {
			return;
		}

		// Leave the null handling of startDate to required="true"
		Object startDateValue = component.getAttributes().get("startDate");
		if (startDateValue == null) {
			return;
		}
		// Leave the null handling of startDate to required="true"
		String errorMessage = (String) component.getAttributes().get(
				"errorMessage");
		if (errorMessage == null || errorMessage.isEmpty()) {
			errorMessage = "La fecha de fin no debe ser menor que la de inicio";
		}
		Date startDate = (Date) startDateValue;
		Date endDate = (Date) value;
		if (endDate.before(startDate)) {
			throw new ValidatorException(new FacesMessage(
					FacesMessage.SEVERITY_ERROR, errorMessage, null));
		}
	}
}