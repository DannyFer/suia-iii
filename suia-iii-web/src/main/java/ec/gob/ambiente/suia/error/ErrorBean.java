package ec.gob.ambiente.suia.error;

import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import java.io.Serializable;

@ManagedBean
@ViewScoped
public class ErrorBean implements Serializable {

	private static final long serialVersionUID = 3986462198683568551L;

	@Getter
	@Setter
	private String urlMaeTransparente;
	
	@Getter
	private String urlErrorAlfrescoPage;

	@PostConstruct
	public void inicializar() {
		urlMaeTransparente = Constantes.getLinkMaeTransparente();
		urlErrorAlfrescoPage = JsfUtil.getStartPage() + "/errors/documentManagement.xhtml";
	}	
}
