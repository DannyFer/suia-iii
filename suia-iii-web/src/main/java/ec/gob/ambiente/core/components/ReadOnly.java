/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.core.components;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;

import ec.gob.ambiente.core.components.base.ComponentUtil;
import ec.gob.ambiente.core.components.base.ExecuteComponentFunction;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author Carlos Pupo
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Carlos Pupo, Fecha: 08/01/2015]
 *          </p>
 */
@FacesComponent("ec.gob.ambiente.core.components.ReadOnly")
public class ReadOnly extends UIComponentBase {

	public static String ATTRIBUTE_VALUE = "value";
	public static String ATTRIBUTE_RENDERED = "rendered";
	public static String STYLE_CLASS_DISABLE = "ui-state-disabled-read-only-NO";
	
	private List<String> componentClassesForReadOnly;
	private List<String> componentClassesForDisabled;
	private List<String> componentClassesForDisabledNotStyled;

	public ReadOnly() {
		componentClassesForReadOnly = new ArrayList<String>();
		componentClassesForDisabled = new ArrayList<String>();
		componentClassesForDisabledNotStyled = new ArrayList<String>();

		String packageJSF = "javax.faces.component.html.";
		String packagePRIMEFACES = "org.primefaces.component.";

		componentClassesForReadOnly.add(packageJSF + "HtmlInputText");
		componentClassesForReadOnly.add(packageJSF + "HtmlInputTextarea");
		componentClassesForReadOnly.add(packageJSF + "HtmlInputSecret");
		componentClassesForReadOnly.add(packageJSF + "HtmlSelectManyMenu");
		componentClassesForReadOnly.add(packageJSF + "HtmlSelectBooleanCheckbox");
		componentClassesForReadOnly.add(packageJSF + "HtmlSelectManyCheckbox");
		componentClassesForReadOnly.add(packageJSF + "HtmlSelectManyListbox");

		componentClassesForDisabled.add(packageJSF + "HtmlSelectOneMenu");
		componentClassesForDisabled.add(packagePRIMEFACES + "inputtext.InputText");
		componentClassesForDisabled.add(packagePRIMEFACES + "inputtextarea.InputTextarea");
		componentClassesForDisabled.add(packagePRIMEFACES + "selectonemenu.SelectOneMenu");
		componentClassesForDisabled.add(packagePRIMEFACES + "selectbooleancheckbox.SelectBooleanCheckbox");
		componentClassesForDisabled.add(packagePRIMEFACES + "selectmanymenu.SelectManyMenu");
		componentClassesForDisabled.add(packagePRIMEFACES + "selectmanycheckbox.SelectManyCheckbox");
		componentClassesForDisabled.add(packagePRIMEFACES + "selectoneradio.SelectOneRadio");
		componentClassesForDisabled.add(packagePRIMEFACES + "selectonebutton.SelectOneButton");
		componentClassesForDisabled.add(packagePRIMEFACES + "selectmanybutton.SelectManyButton");

		componentClassesForDisabledNotStyled.add(packageJSF + "HtmlSelectOneMenu");
	}

	@Override
	public String getFamily() {
		return ComponentUtil.FAMILY;
	}

	public String getValue() {
		return (String) getStateHelper().eval(ATTRIBUTE_VALUE);
	}

	public void setValue(String value) {
		getStateHelper().put(ATTRIBUTE_VALUE, value);
	}
	
	public String getRendered() {
		return (String) getStateHelper().eval(ATTRIBUTE_RENDERED);
	}

	public void setRendered(String rendered) {
		getStateHelper().put(ATTRIBUTE_RENDERED, rendered);
	}

	@Override
	public void encodeBegin(FacesContext context) throws IOException {
		String rendered = getValue();
		if (rendered != null && !new Boolean(rendered.toString())) {
			setRendered(false);
			return;
		}
		
		String value = getValue();
		if (value != null && !new Boolean(value.toString())) {
			return;
		}
		markAsReadOnlyChildren(this);
	}

	public List<UIComponent> markAsReadOnlyChildren(UIComponent c) {
		if (c == null)
			return null;
		else
			setComponentAsReadOnly(c);
		if (c.getChildCount() == 0) {
			return null;
		} else {
			List<UIComponent> children = c.getChildren();
			for (UIComponent uiComponent : children) {
				markAsReadOnlyChildren(uiComponent);
			}
			return null;
		}
	}

	private void setComponentAsReadOnly(UIComponent component) {
		markAsReadOnly(component);
		markAsDisabled(component);
	}

	public void markAsReadOnly(final UIComponent c) {
		ComponentUtil.isComponentAnyClass(c, componentClassesForReadOnly, new ExecuteComponentFunction() {

			@Override
			public void execute() {
				ComponentUtil.setReadonly(c, true);
			}
		});

	}

	public void markAsDisabled(final UIComponent c) {
		ComponentUtil.isComponentAnyClass(c, componentClassesForDisabled, new ExecuteComponentFunction() {

			@Override
			public void execute() {
				ComponentUtil.setDisabled(c, true);
				ComponentUtil.addStyleClass(c, STYLE_CLASS_DISABLE);
				ComponentUtil.isNotComponentAnyClass(c, componentClassesForDisabledNotStyled,
						new ExecuteComponentFunction() {

							@Override
							public void execute() {
								ComponentUtil.removeStyleClass(c, STYLE_CLASS_DISABLE);
							}
						});
			}
		});
	}
}
