package ec.gob.ambiente.prevencion.categoria2.controllers;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import lombok.Setter;
import ec.gob.ambiente.prevencion.categoria2.bean.FichaAmbientalPmaBean;
import ec.gob.ambiente.prevencion.categoria2.bean.PlanPmaBean;
import ec.gob.ambiente.prevencion.categoria2.bean.ProgramaPmaBean;
import ec.gob.ambiente.suia.catalogos.facade.CatalogoGeneralFacade;
import ec.gob.ambiente.suia.domain.CatalogoGeneral;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.PlanPmaFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;

/**
 * 
 * @author karla.carvajal
 *
 */
@ManagedBean
public class PlanPmaController {
		
	@Setter
	@ManagedProperty(value = "#{fichaAmbientalPmaBean}")
	private FichaAmbientalPmaBean fichaAmbientalPmaBean;
	
	@Setter
	@ManagedProperty(value = "#{planPmaBean}")
	private PlanPmaBean planPmaBean;
	
	@Setter
	@ManagedProperty(value = "#{programaPmaBean}")
	private ProgramaPmaBean programaPmaBean;
	
	@EJB
	private PlanPmaFacade planPmaFacade;
	
	@EJB
	private CatalogoGeneralFacade catalogoGeneralFacade;
	
	public String guardarPlan()
	{
		
		String messages = "";		
		
		if (!messages.isEmpty()) {
			JsfUtil.addMessageError(JsfUtil.getStringAsHtmlUL(messages, false));
			return null;
		}
		
		CatalogoGeneral plan = new CatalogoGeneral();
		
		try {
			plan = catalogoGeneralFacade.obtenerCatalogoXId(Integer.parseInt(planPmaBean.getPlanSeleccionado()));
		} catch (Exception e) {
			e.printStackTrace();
			return null;			
		}
		
		planPmaBean.getPlan().setCatalogoGeneral(plan);
		planPmaBean.getPlan().setFichaAmbientalPma(fichaAmbientalPmaBean.getFicha());
		planPmaFacade.guardarPlan(planPmaBean.getPlan(), planPmaBean.getDetallesPlan(), programaPmaBean.getProgramas());		
		JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
		
		return "Guardo si guardo jejejeje.";
		//return JsfUtil.actionNavigateTo("/prevencion/registroProyecto/verProyecto.jsf");		
	}
	
	public void agregarOtroPlan()
	{
		planPmaBean.cleanPlanes();
		programaPmaBean.cleanProgramas();
	}

}