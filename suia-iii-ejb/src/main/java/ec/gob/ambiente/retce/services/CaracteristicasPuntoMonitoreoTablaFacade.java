package ec.gob.ambiente.retce.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.retce.model.CaracteristicasPuntoMonitoreo;
import ec.gob.ambiente.retce.model.CaracteristicasPuntoMonitoreoTabla;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.TipoSector;
import ec.gob.ambiente.suia.domain.Usuario;

@Stateless
public class CaracteristicasPuntoMonitoreoTablaFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	public CaracteristicasPuntoMonitoreoTabla findById(Integer id){
		try {
			CaracteristicasPuntoMonitoreoTabla caracteristicasPuntoMonitoreoTabla = (CaracteristicasPuntoMonitoreoTabla) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM CaracteristicasPuntoMonitoreoTabla o where o.id = :id")
					.setParameter("id", id).getSingleResult();
			
			return caracteristicasPuntoMonitoreoTabla;
			
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void save(CaracteristicasPuntoMonitoreoTabla obj, Usuario usuario){
		if(obj.getId()==null){
			obj.setUsuarioCreacion(usuario.getNombre());
			obj.setFechaCreacion(new Date());			
		}
		else{
			obj.setUsuarioModificacion(usuario.getNombre());
			obj.setFechaModificacion(new Date());			
		}
		crudServiceBean.saveOrUpdate(obj);
	}
	
	@SuppressWarnings("unchecked")
	public List<CaracteristicasPuntoMonitoreoTabla> findTablaByCaracteristica(CaracteristicasPuntoMonitoreo caracteristica){
		List<CaracteristicasPuntoMonitoreoTabla> caracteristicasPuntoMonitoreoTablaList = new ArrayList<CaracteristicasPuntoMonitoreoTabla>();
		try {
			caracteristicasPuntoMonitoreoTablaList = (ArrayList<CaracteristicasPuntoMonitoreoTabla>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM CaracteristicasPuntoMonitoreoTabla o where o.estado = true and o.caracteristicasPuntoMonitoreo.id= :idCaracteristica order")
					.setParameter("idCaracteristica", caracteristica.getId())
					.getResultList();
			return caracteristicasPuntoMonitoreoTablaList;
			
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return caracteristicasPuntoMonitoreoTablaList;
	}

}
