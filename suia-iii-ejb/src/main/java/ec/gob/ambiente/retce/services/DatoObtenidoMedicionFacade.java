package ec.gob.ambiente.retce.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.retce.model.DatoObtenidoMedicion;
import ec.gob.ambiente.retce.model.DetalleEmisionesAtmosfericas;
import ec.gob.ambiente.retce.model.EmisionesAtmosfericas;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Usuario;

@Stateless
public class DatoObtenidoMedicionFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	public DatoObtenidoMedicion findById(Integer id){
		try {
			DatoObtenidoMedicion datoObtenido = (DatoObtenidoMedicion) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM DatoObtenidoMedicion o where o.id = :id")
					.setParameter("id", id).getSingleResult();
			
			return datoObtenido;
			
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void save(DatoObtenidoMedicion obj, Usuario usuario){
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
	public List<DatoObtenidoMedicion> findAll(){
		List<DatoObtenidoMedicion> datoObtenidoList = new ArrayList<DatoObtenidoMedicion>();
		try {
			datoObtenidoList = (ArrayList<DatoObtenidoMedicion>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM DatoObtenidoMedicion o where o.estado = true")
					.getResultList();			
			return datoObtenidoList;
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return datoObtenidoList;
	}
	
	@SuppressWarnings("unchecked")
	public List<DatoObtenidoMedicion> findByEmisionAtmosferica(DetalleEmisionesAtmosfericas detalleEmision){
		List<DatoObtenidoMedicion> datoObtenidoList = new ArrayList<DatoObtenidoMedicion>();
		try {
			datoObtenidoList = (ArrayList<DatoObtenidoMedicion>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM DatoObtenidoMedicion o where o.estado = true and "
							+ "o.detalleEmisionesAtmosfericas.id = :id and o.idRegistroOriginal = null")
					.setParameter("id", detalleEmision.getId()).getResultList();			
			return datoObtenidoList;
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return datoObtenidoList;
	}
	
	@SuppressWarnings("unchecked")
	public List<DatoObtenidoMedicion> findByIdRegistroOriginal(Integer id){
		List<DatoObtenidoMedicion> datoObtenidoList = new ArrayList<DatoObtenidoMedicion>();
		try {
			datoObtenidoList = (ArrayList<DatoObtenidoMedicion>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM DatoObtenidoMedicion o where o.estado = true and "
							+ "o.idRegistroOriginal = :id")
					.setParameter("id", id).getResultList();			
			return datoObtenidoList;
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return datoObtenidoList;
	}
	
	@SuppressWarnings("unchecked")
	public List<DatoObtenidoMedicion> findByDetalleEmisionAtmosfericaTotal(DetalleEmisionesAtmosfericas detalleEmision){
		List<DatoObtenidoMedicion> datoObtenidoList = new ArrayList<DatoObtenidoMedicion>();
		try {
			datoObtenidoList = (ArrayList<DatoObtenidoMedicion>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM DatoObtenidoMedicion o where o.estado = true and "
							+ "o.detalleEmisionesAtmosfericas.id = :id and o.idRegistroOriginal != null ")
					.setParameter("id", detalleEmision.getId()).getResultList();			
			return datoObtenidoList;
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return datoObtenidoList;
	}

}
