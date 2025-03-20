/*
 * Copyright 2015 MAGMASOFT
 * Todos los derechos reservados
 */
package ec.gob.ambiente.control.aprobacionrequisitostecnicos.validators;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.primefaces.context.RequestContext;

/**
 * <b> Incluir aqui la descripcion de la clase. </b>
 * 
 * @author vero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: vero $, $Date: 01/09/2015 $]
 *          </p>
 */
@FacesValidator("tipoTransporteValidator")
public class TipoTransporteValidator implements Validator {

	@Override
	public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {

		StringBuilder functionJs = new StringBuilder();
		boolean param1 = ((Boolean) (component.getAttributes().get("campo1")));
		boolean param2 = ((Boolean) (component.getAttributes().get("campo2")));
		if (!(param1 || param2)) {
			String errorMessage = "Debe indicar que tipo de transporte utiliza.";
			functionJs.append("highlightComponent('form:tipoTransporte');");
			RequestContext.getCurrentInstance().execute(functionJs.toString());
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, null));
		} else {
			functionJs.append("removeHighLightComponent('form:tipoTransporte');");
			RequestContext.getCurrentInstance().execute(functionJs.toString());
		}
	}

}
