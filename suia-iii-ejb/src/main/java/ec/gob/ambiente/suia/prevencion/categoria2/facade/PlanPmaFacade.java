package ec.gob.ambiente.suia.prevencion.categoria2.facade;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.domain.CabeceraPma;
import ec.gob.ambiente.suia.domain.DetalleCabeceraPma;
import ec.gob.ambiente.suia.domain.Programa;
import ec.gob.ambiente.suia.prevencion.categoria2.service.PlanPmaServiceBean;
import ec.gob.ambiente.suia.prevencion.categoria2.service.ProgramaPmaServiceBean;

/**
 * 
 * @author karla.carvajal
 *
 */
@Stateless
public class PlanPmaFacade {
	
	@EJB
	private ProgramaPmaServiceBean programaPmaServiceBean;
	
	@EJB
	private PlanPmaServiceBean planPmaServiceBean;
	
	public void guardarPlan(CabeceraPma cabecera, List<DetalleCabeceraPma> detallesPlanes, List<Programa> programas)
	{
		planPmaServiceBean.guardarCabeceraPma(cabecera);		
		
		if(detallesPlanes.size() > 0)
		{		
			List<DetalleCabeceraPma> detallesCabecera = planPmaServiceBean.getDetallesPlanesPorPanId(cabecera.getId());
			
			for (DetalleCabeceraPma detalleCabeceraPma : detallesCabecera) {
				planPmaServiceBean.eliminarDetallePlan(detalleCabeceraPma);
			}
			
			for (DetalleCabeceraPma _detallePlan : detallesPlanes) {
				DetalleCabeceraPma detallePlan = new DetalleCabeceraPma();
				detallePlan = _detallePlan;
				detallePlan.setCabeceraPma(cabecera);
				planPmaServiceBean.guardarDetallePlan(detallePlan);
			}
		}
		
		if(programas.size() > 0)
		{		
			List<Programa> _programas = programaPmaServiceBean.getProgramasPorPlanId(cabecera.getId());
			
			for (Programa programa : _programas) {
				programaPmaServiceBean.eliminarPrograma(programa);
			}
			
			for (Programa _programa : programas) {
				Programa programa = new Programa();
				programa = _programa;
				programa.setCabeceraPma(cabecera);
				programaPmaServiceBean.guardarPrograma(programa);
			}
		}
	}
	
	public List<Programa> getProgramasPorPlanId(Integer idPlan)
	{
		return programaPmaServiceBean.getProgramasPorPlanId(idPlan);		
	}
	
	public void guardarPrograma(Programa programa)
	{
		programaPmaServiceBean.guardarPrograma(programa);
	}
	
	public void saveOrUpdateProgramas(List<Programa> programas)
	{
		programaPmaServiceBean.saveOrUpdateProgramas(programas);
	}
	
	public void eliminarPrograma(Programa programa)
	{
		programaPmaServiceBean.eliminarPrograma(programa);
	}
	
	public void removeDetallePlan(DetalleCabeceraPma detalleCabeceraPma)
	{
		planPmaServiceBean.eliminarDetallePlan(detalleCabeceraPma);
	}	
	
	public List<DetalleCabeceraPma> getDetallesPlanesPorPlanId(Integer idPlan)
	{	
		return planPmaServiceBean.getDetallesPlanesPorPanId(idPlan);
	}	
	
	public CabeceraPma getCabeceraPma(Integer idFicha)
	{
		return planPmaServiceBean.getCabeceraPma(idFicha);
	}	

	public List<CabeceraPma> getCabecerasPma(Integer idFicha)
	{
		return planPmaServiceBean.getCabecerasPma(idFicha);
	}

}