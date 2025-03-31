package ec.gob.ambiente.rcoa.digitalizacion;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.model.LazyDataModel;

import ec.gob.ambiente.rcoa.agrupacionAutorizaciones.facade.AutorizacionesAdministrativasFacade;
import ec.gob.ambiente.rcoa.agrupacionAutorizaciones.model.AutorizacionAdministrativa;
import ec.gob.ambiente.rcoa.digitalizacion.facade.AutorizacionAdministrativaAmbientalFacade;
import ec.gob.ambiente.rcoa.digitalizacion.facade.ProyectoAsociadoDigitalizacionFacade;
import ec.gob.ambiente.rcoa.digitalizacion.model.ProyectoAsociadoDigitalizacion;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.bean.DatosOperadorRgdBean;
import ec.gob.ambiente.suia.domain.AreaUsuario;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class DesvinculacionProyectoDigitalizacionController {
	
	Logger LOG = Logger.getLogger(DesvinculacionProyectoDigitalizacionController.class);
	
	@EJB
	private AutorizacionAdministrativaAmbientalFacade autorizacionAdministrativaAmbientalFacade;
	@EJB
	private AutorizacionesAdministrativasFacade autorizacionesAdministrativasFacade;
	@EJB
	private ProyectoAsociadoDigitalizacionFacade proyectoAsociadoFacade;
	
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
	private LazyDataModel<AutorizacionAdministrativa> listaAutorizacionPrincipal;
	
	@Getter
	@Setter
	private ProyectoAsociadoDigitalizacion proyectoVinculado;
	
	@Getter
	@Setter
	private boolean esTecnico;
	
	private String areasTecnico;
	
	@PostConstruct
	public void init(){
		try {
			areasTecnico="";
			esTecnico = true;
			for (AreaUsuario areaUser : JsfUtil.getLoggedUser().getListaAreaUsuario()) {
				if (areaUser.getArea().getAreaName().contains("PROPONENTE")) {
					esTecnico = false;
					datosOperadorRgdBean.buscarDatosOperador(JsfUtil.getLoggedUser());
					break;
				}
			}
			if(esTecnico){
				for (AreaUsuario areaUser : JsfUtil.getLoggedUser().getListaAreaUsuario()) {
					areasTecnico += (areasTecnico.isEmpty()?"":",")+areaUser.getArea().getId().toString();
					if(areaUser.getArea().getArea() != null && !areasTecnico.contains(areaUser.getArea().getArea().getId().toString()))
						areasTecnico += "," + areaUser.getArea().getArea().getId().toString();
				}
			}
			listaAutorizacionPrincipal = new LazyProyectosDigitalizacionDesvinculacionDataModel(esTecnico, areasTecnico);
		} catch (Exception e) {
			LOG.error("Error al recuperar la información", e);
		}
	}
	
	public void seleccionarProyecto(ProyectoAsociadoDigitalizacion autorizacionSeleccionada){
		proyectoVinculado = autorizacionSeleccionada;
	}
	
	public void inicioDesvinculacion(){
		listaAutorizacionPrincipal = new LazyProyectosDigitalizacionDesvinculacionDataModel(esTecnico, areasTecnico);
	}
	
	public void desvincularProyecto(){
		try {
			if(proyectoVinculado != null && proyectoVinculado.getId() != null){
				proyectoVinculado.setEstado(false);
				proyectoAsociadoFacade.guardar(proyectoVinculado, loginBean.getUsuario());
				//listaAutorizacionPrincipal = autorizacionAdministrativaAmbientalFacade.obtenerProyectosConProyectosVinculados();
			}
		} catch (Exception e) {
			LOG.error("Ocurrio un error al eliminar el proyecto", e);
		}
	}
	
	public void redireccionarBandeja(){
			JsfUtil.redirectToBandeja();
	}
}
