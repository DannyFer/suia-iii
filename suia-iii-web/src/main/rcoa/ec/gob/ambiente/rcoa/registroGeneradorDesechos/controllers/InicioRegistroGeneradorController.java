package ec.gob.ambiente.rcoa.registroGeneradorDesechos.controllers;


import java.util.Map;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class InicioRegistroGeneradorController {
	
	
	@EJB
	private ProcesoFacade procesoFacade;
	
	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;

	private Map<String, Object> variables;

	@ManagedProperty(value = "#{bandejaTareasBean}")
	@Getter
	@Setter
	private BandejaTareasBean bandejaTareasBean;
	
	
	public void init(){
		try {
			variables=procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
			String tipoPermisoRGD =(String)variables.get(Constantes.VARIABLE_TIPO_RGD);
			if(tipoPermisoRGD != null && tipoPermisoRGD.equals(Constantes.TIPO_RGD_AAA)){
		        JsfUtil.redirectTo("/pages/rcoa/generadorDesechos/informacionRegistroGeneradorAAA.jsf");
			}else if(tipoPermisoRGD != null && tipoPermisoRGD.equals(Constantes.TIPO_RGD_REP)){
		        JsfUtil.redirectTo("/pages/rcoa/generadorDesechos/informacionRegistroGeneradorREP.jsf");
			}else{
		        JsfUtil.redirectTo("/pages/rcoa/generadorDesechos/informacionRegistroGeneradorRCOA.jsf");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
