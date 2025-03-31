package ec.gob.ambiente.control.denuncias.bean;

import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

@ManagedBean
@ViewScoped
public class EnviarDenunciaGadBean implements Serializable{
    
    private static final long serialVersionUID = -3536371287113629686L;

	@Setter
	@Getter
	private Object enteAcreditado;

}
