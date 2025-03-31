package ec.gob.ambiente.suia.prevencion.categoria2.service;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.CabeceraPma;
import ec.gob.ambiente.suia.domain.DetalleCabeceraPma;

/**
 * 
 * @author karla.carvajal
 * 
 */
@Stateless
public class PlanPmaServiceBean {
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	public void guardarCabeceraPma(CabeceraPma cabeceraPma)
	{
		crudServiceBean.saveOrUpdate(cabeceraPma);
	}
	
	public void eliminarCabeceraPma(CabeceraPma cabeceraPma)
	{
		crudServiceBean.delete(cabeceraPma);
	}
	
	public void guardarDetallePlan(DetalleCabeceraPma detallePlan)
	{
		crudServiceBean.saveOrUpdate(detallePlan);
	}
	
	public void guardarDetallesPlanes(List<DetalleCabeceraPma> detallesPlanes)
	{
		crudServiceBean.saveOrUpdate(detallesPlanes);
	}
	
	public void eliminarDetallePlan(DetalleCabeceraPma detalleCabeceraPma)
	{
		crudServiceBean.delete(detalleCabeceraPma);
	}
	
	@SuppressWarnings("unchecked")
	public List<DetalleCabeceraPma> getDetallesPlanesPorPanId(Integer idPlan)
	{
		List<DetalleCabeceraPma> detallesCabecera = new ArrayList<DetalleCabeceraPma>();
		detallesCabecera = (List<DetalleCabeceraPma>) crudServiceBean.getEntityManager()
				.createQuery("From DetalleCabeceraPma d where d.cabeceraPma.id =:idPlan and d.estado = true")
				.setParameter("idPlan", idPlan).getResultList();
		
		return detallesCabecera;
	}	
	
	public CabeceraPma getCabeceraPma(Integer idFicha)
	{
		CabeceraPma cabeceraPma = new CabeceraPma();		
		List<CabeceraPma> cabeceras = getCabecerasPma(idFicha);
		
		if(cabeceras.size() > 0)
		{
			cabeceraPma = cabeceras.get(0);
		}	
				
		return cabeceraPma;
	}
	
	@SuppressWarnings("unchecked")
	public List<CabeceraPma> getCabecerasPma(Integer idFicha)
	{
		List<CabeceraPma> cabeceras = (List<CabeceraPma>) crudServiceBean.getEntityManager()
				.createQuery("From CabeceraPma c where c.fichaAmbientalPma.id =:idFicha and c.estado = true")
				.setParameter("idFicha", idFicha).getResultList();
		
		return cabeceras;
	}
	
}