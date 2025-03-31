package ec.gob.ambiente.control.aprobacionrequisitostecnicos.bean;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.RequisitosConductor;

@ManagedBean
@ViewScoped
public class RequisitosConductorBean {
	
	@Setter
	@Getter
	private List<RequisitosConductor> requisitoConductores;
	
	@Setter
	@Getter
	private List<RequisitosConductor> requisitoConductoresEli;
	
	@Setter
	@Getter
	private RequisitosConductor requisitosConductor;
	
	@Setter
	@Getter
	private boolean mostrarPanel;
	
	
	@Setter
	@Getter
	private boolean conductorEncontrado;
	

}
