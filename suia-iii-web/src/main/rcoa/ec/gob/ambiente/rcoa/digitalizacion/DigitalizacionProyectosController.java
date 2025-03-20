package ec.gob.ambiente.rcoa.digitalizacion;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.bean.DatosOperadorRgdBean;
import ec.gob.ambiente.suia.domain.AreaUsuario;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class DigitalizacionProyectosController {
	
	Logger LOG = Logger.getLogger(DigitalizacionProyectosController.class);
	
	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{datosOperadorRgdBean}")
	private DatosOperadorRgdBean datosOperadorRgdBean;
	
	@Getter
	@Setter
	private boolean esTecnico, esTecnicoCalidad;
	
	@PostConstruct
	public void init(){
		try {
			esTecnico = true; 
			esTecnicoCalidad = false;
			for (AreaUsuario areaUser : JsfUtil.getLoggedUser().getListaAreaUsuario()) {
				if (areaUser.getArea().getAreaName().contains("PROPONENTE")) {
					datosOperadorRgdBean.buscarDatosOperador(JsfUtil.getLoggedUser());
					esTecnico = false;
					break;
				}
			}
			if(esTecnico){
				String rol="role.resolucion.tecnico.responsable";
				String rolTecnicoCalidad = Constantes.getRoleAreaName(rol);
				esTecnicoCalidad= JsfUtil.getLoggedUser().isUserInRole(JsfUtil.getLoggedUser(), rolTecnicoCalidad);
			}
		} catch (Exception e) {
			LOG.error("Error al recuperar la información", e);
		}
	}
	
	public void redireccionarBandeja(){
			JsfUtil.redirectToBandeja();
	}
}
