package ec.gob.ambiente.retce.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.retce.model.CaracteristicasPuntoMonitoreo;
import ec.gob.ambiente.retce.model.CaracteristicasPuntoMonitoreoTabla;
import ec.gob.ambiente.retce.model.CatalogoTipoCuerpoReceptor;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.TipoSector;
import ec.gob.ambiente.suia.domain.Usuario;

@Stateless
public class CaracteristicasPuntoMonitoreoFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	public CaracteristicasPuntoMonitoreo findById(Integer id){
		try {
			CaracteristicasPuntoMonitoreo caracteristicasPuntoMonitoreo = (CaracteristicasPuntoMonitoreo) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT c FROM CaracteristicasPuntoMonitoreo c where c.id = :id")
					.setParameter("id", id).getSingleResult();
			
			return caracteristicasPuntoMonitoreo;
			
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void save(CaracteristicasPuntoMonitoreo obj, Usuario usuario){
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
	public List<CaracteristicasPuntoMonitoreo> findAll(){
		List<CaracteristicasPuntoMonitoreo> caracteristicasPuntoMonitoreoList = new ArrayList<CaracteristicasPuntoMonitoreo>();
		try {
			caracteristicasPuntoMonitoreoList = (ArrayList<CaracteristicasPuntoMonitoreo>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT c FROM CaracteristicasPuntoMonitoreo c where c.estado = true order by c.orden")
					.getResultList();
			return caracteristicasPuntoMonitoreoList;
			
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return caracteristicasPuntoMonitoreoList;
	}
	
	@SuppressWarnings("unchecked")
	public List<CaracteristicasPuntoMonitoreo> findBySector(TipoSector tipoSector){				
		List<CaracteristicasPuntoMonitoreo> caracteristicasPuntoMonitoreoList = new ArrayList<CaracteristicasPuntoMonitoreo>();
		try {
			caracteristicasPuntoMonitoreoList = (ArrayList<CaracteristicasPuntoMonitoreo>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM CaracteristicasPuntoMonitoreo o where o.estado = true and o.tipoSector.id = :idSector order by o.orden")
					.setParameter("idSector", tipoSector.getId()>3?3:tipoSector.getId())
					.getResultList();
			return caracteristicasPuntoMonitoreoList;
			
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return caracteristicasPuntoMonitoreoList;
	}
	
	@SuppressWarnings("unchecked")
	public List<CaracteristicasPuntoMonitoreo> findBySectorOtros(TipoSector tipoSector,TipoSector sectorHidrocarburos){				
		List<CaracteristicasPuntoMonitoreo> caracteristicasPuntoMonitoreoList = new ArrayList<CaracteristicasPuntoMonitoreo>();
		try {
			caracteristicasPuntoMonitoreoList = (ArrayList<CaracteristicasPuntoMonitoreo>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM CaracteristicasPuntoMonitoreo o where o.estado = true and (o.tipoSector.id = :idSector or o.tipoSector.id = :idSectorH) order by o.orden")
					.setParameter("idSector", tipoSector.getId()>3?3:tipoSector.getId())
					.setParameter("idSectorH", sectorHidrocarburos.getId())
					.getResultList();
			return caracteristicasPuntoMonitoreoList;
			
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return caracteristicasPuntoMonitoreoList;
	}	
	
	@SuppressWarnings("unchecked")
	public List<CaracteristicasPuntoMonitoreoTabla> findTablaByCaracteristica(CaracteristicasPuntoMonitoreo caracteristica){
		List<CaracteristicasPuntoMonitoreoTabla> caracteristicasPuntoMonitoreoTablaList = new ArrayList<CaracteristicasPuntoMonitoreoTabla>();
		try {
			caracteristicasPuntoMonitoreoTablaList = (ArrayList<CaracteristicasPuntoMonitoreoTabla>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM CaracteristicasPuntoMonitoreoTabla o where o.estado = true and o.caracteristicasPuntoMonitoreo.id= :idCaracteristica order by 1")
					.setParameter("idCaracteristica", caracteristica.getId())
					.getResultList();
			return caracteristicasPuntoMonitoreoTablaList;
			
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return caracteristicasPuntoMonitoreoTablaList;
	}
	
	@SuppressWarnings("unchecked")
	public List<CaracteristicasPuntoMonitoreoTabla> findTablaByCaracteristicaPorTipoCuerpo(CaracteristicasPuntoMonitoreo caracteristica, CatalogoTipoCuerpoReceptor tipoCuerpoReceptor){
		List<CaracteristicasPuntoMonitoreoTabla> caracteristicasPuntoMonitoreoTablaList = new ArrayList<CaracteristicasPuntoMonitoreoTabla>();
		try {
			caracteristicasPuntoMonitoreoTablaList = (ArrayList<CaracteristicasPuntoMonitoreoTabla>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM CaracteristicasPuntoMonitoreoTabla o where o.estado = true and o.caracteristicasPuntoMonitoreo.id= :idCaracteristica and o.catalogoTipoCuerpoReceptor.id = :idTipoCuerpo order by 1")
					.setParameter("idCaracteristica", caracteristica.getId())
					.setParameter("idTipoCuerpo", tipoCuerpoReceptor.getId())
					.getResultList();
			return caracteristicasPuntoMonitoreoTablaList;
			
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return caracteristicasPuntoMonitoreoTablaList;
	}

}
