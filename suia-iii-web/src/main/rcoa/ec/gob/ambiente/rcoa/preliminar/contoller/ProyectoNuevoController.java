package ec.gob.ambiente.rcoa.preliminar.contoller;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class ProyectoNuevoController {

	@Getter
	@Setter
	@ManagedProperty(value = "#{proyectosBean}")
	private ProyectosBean proyectosBean;

	@Getter
	@Setter
	private Integer tipoProyecto=0;
	
	public void enviar()
	{
		proyectosBean.setModificarProyectoRcoa(false);
		proyectosBean.setProyectoRcoa(null);
		
		proyectosBean.setTipoProyectoRcoa(tipoProyecto);

		if(tipoProyecto==1)			
			JsfUtil.redirectTo("/pages/rcoa/preliminar/informacionPreliminar.jsf");
		else if(tipoProyecto==2)			
			JsfUtil.redirectTo("/pages/rcoa/preliminar/informacionPreliminar.jsf");
	}
}
