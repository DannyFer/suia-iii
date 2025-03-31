package ec.gob.ambiente.suia.templates.controller;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.templates.bean.TemplatesBean;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
public class TemplatesController implements Serializable {

	private static final long serialVersionUID = -6095477361086704930L;

	@Getter
	@Setter
	@ManagedProperty(value = "#{templatesBean}")
	private TemplatesBean templatesBean;
	
	public void markTemplate(String key) {
		templatesBean.setKey(key);
	}
	
	public void aceptar() {
		JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
	}
}
