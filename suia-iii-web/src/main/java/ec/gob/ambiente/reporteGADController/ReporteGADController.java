package ec.gob.ambiente.reporteGADController;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

@ManagedBean
@ViewScoped
public class ReporteGADController implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2795089684514485841L;

	@Getter
	@Setter
	private String nombreReporte;
	
	@Getter
	@Setter
	private boolean verInforme;
	
	@PostConstruct
	private void init(){
		nombreReporte="akjshdakshdkjadhsj";
		verInforme=false;
	}
	
	public void verpdf(){
		System.out.println(">>>>>>>>>>>>>>");
		verInforme=true;
	}
	
}
