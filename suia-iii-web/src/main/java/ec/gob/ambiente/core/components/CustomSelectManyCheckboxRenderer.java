/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.core.components;

import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.model.SelectItem;

import org.primefaces.component.selectmanycheckbox.SelectManyCheckbox;
import org.primefaces.component.selectmanycheckbox.SelectManyCheckboxRenderer;

public class CustomSelectManyCheckboxRenderer extends
		SelectManyCheckboxRenderer {

	@Override
	protected void encodeOptionLabel(FacesContext context,
			SelectManyCheckbox checkbox, String containerClientId,
			SelectItem option, boolean disabled) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		/*if (option.getDescription() != null) {
			writer.startElement("p:tooltip", null);
			//writer.writeAttribute("escape", "false", null);
			//writer.writeAttribute("showDelay", "1200", null);
			//writer.writeAttribute("trackMouse", "true", null);

			writer.writeAttribute("for", containerClientId, null);
 
			writer.writeAttribute("value", option.getDescription(), null);
 
 
			writer.endElement("p:tooltip");

		}

		// --*/
		writer.startElement("label", null);
		writer.writeAttribute("for", containerClientId, null);

		if (option.getDescription() != null) {
			writer.writeAttribute("title", option.getDescription(), null);
		}

		if (disabled) {
			writer.writeAttribute("class", "ui-state-disabled", null);
		}

		if (option.isEscape()) {
			writer.writeText(option.getLabel(), null);
		} else {
			writer.write(option.getLabel());
		}

		writer.endElement("label");
		
	}

}
