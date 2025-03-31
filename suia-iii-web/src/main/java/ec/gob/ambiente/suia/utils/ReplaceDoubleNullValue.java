package ec.gob.ambiente.suia.utils;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 * Created by Denis Linares on 24/11/15.
 */
@FacesConverter("replaceDoubleNullValue")
public class ReplaceDoubleNullValue implements Converter {

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        return  (value == null) || (value.toString().equals("0")) ? "" : value.toString();
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        return  (value == null) || (value.toString().equals("0")) ? "" : value.toString();
    }
}
