/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.core.converters;

import ec.gob.ambiente.suia.domain.Rol;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.primefaces.component.picklist.PickList;
import org.primefaces.model.DualListModel;

/**
 *
 * @author ishmael
 */
@FacesConverter(forClass = Rol.class, value = "RolConverter")
public class RolConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Object ret = null;

        if (component instanceof PickList) {
            Object dualList = ((PickList) component).getValue();
            DualListModel<?> dl = (DualListModel<?>) dualList;
            for (Object o : dl.getSource()) {
                String id = "" + ((Rol) o).getId();
                if (value.equals(id)) {
                    ret = o;
                    break;
                }
            }
            if (ret == null) {
                for (Object o : dl.getTarget()) {
                    String id = "" + ((Rol) o).getId();
                    if (value.equals(id)) {
                        ret = o;
                        break;
                    }
                }
            }
        }
        return ret;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        String str = "";
        if (value instanceof Rol) {
            str = "" + ((Rol) value).getId();
        }
        return str;
    }

}
