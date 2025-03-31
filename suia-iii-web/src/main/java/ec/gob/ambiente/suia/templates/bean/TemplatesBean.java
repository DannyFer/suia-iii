package ec.gob.ambiente.suia.templates.bean;

import java.io.Serializable;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.webservicesclientes.facade.TemplatesServicesFacade;

@ManagedBean
@ViewScoped
public class TemplatesBean implements Serializable {

	private static final long serialVersionUID = -3768853360799437071L;

	@EJB
	private TemplatesServicesFacade templatesServicesFacade;

	@Getter
	private Map<String, String> templates;

	@Getter
	private String key;

	@Getter
	@Setter
	private String body;

	@PostConstruct
	private void init() {
		try {
			templates = templatesServicesFacade.getTemplates();
		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrión un error al cargar las plantillas.");
			e.printStackTrace();
		}
	}

	public void setKey(String key) {
		this.key = key;
		this.body = null;
		if (templates.containsKey(key))
			body = templates.get(key);
	}
}
