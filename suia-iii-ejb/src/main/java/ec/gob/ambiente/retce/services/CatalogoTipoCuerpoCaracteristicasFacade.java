package ec.gob.ambiente.retce.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.retce.model.ActividadCiiu;
import ec.gob.ambiente.retce.model.CaracteristicasPuntoMonitoreo;
import ec.gob.ambiente.retce.model.CatalogoTipoCuerpoReceptorCaracteristicasPunto;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Usuario;

@Stateless
public class CatalogoTipoCuerpoCaracteristicasFacade{	
	
	@EJB
	private CrudServiceBean crudServiceBean;	
	
	
	@SuppressWarnings("unchecked")
	public List<CatalogoTipoCuerpoReceptorCaracteristicasPunto> findAll(CaracteristicasPuntoMonitoreo caracteristicasPuntoMonitoreo){	
		List<CatalogoTipoCuerpoReceptorCaracteristicasPunto> activitiesCiiu=new ArrayList<CatalogoTipoCuerpoReceptorCaracteristicasPunto>();
		try{
			 activitiesCiiu = (ArrayList<CatalogoTipoCuerpoReceptorCaracteristicasPunto>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM CatalogoTipoCuerpoReceptorCaracteristicasPunto o where o.estado=true and o.caracteristicasPuntoMonitoreo.id=:id ")
					.setParameter("id", caracteristicasPuntoMonitoreo.getId())					
					.getResultList();
			return activitiesCiiu;
			
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return activitiesCiiu;
	}
}