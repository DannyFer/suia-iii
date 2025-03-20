package ec.gob.ambiente.retce.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.retce.model.DetalleEmisionesAtmosfericas;
import ec.gob.ambiente.retce.model.FacilidadesMonitoreoEmisiones;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Usuario;

@Stateless
public class FacilidadesMonitoreoEmisionesFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	public FacilidadesMonitoreoEmisiones findById(Integer id) {
		try {
			FacilidadesMonitoreoEmisiones facilidades = (FacilidadesMonitoreoEmisiones) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM FacilidadesMonitoreoEmisiones where o.id = :id")
					.setParameter("id", id).getSingleResult();
			return facilidades;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void save(FacilidadesMonitoreoEmisiones obj, Usuario usuario){
		if(obj.getId()== null){
			obj.setUsuarioCreacion(usuario.getNombre());
			obj.setFechaCreacion(new Date());
		}else{
			obj.setUsuarioModificacion(usuario.getNombre());
			obj.setFechaModificacion(new Date());
		}
		crudServiceBean.saveOrUpdate(obj);
	}
	
	@SuppressWarnings("unchecked")
	public List<FacilidadesMonitoreoEmisiones> findByDetail(DetalleEmisionesAtmosfericas detalle){
		List<FacilidadesMonitoreoEmisiones> lista = new ArrayList<FacilidadesMonitoreoEmisiones>();
		try {
			lista = (ArrayList<FacilidadesMonitoreoEmisiones>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM FacilidadesMonitoreoEmisiones o WHERE o.detalleEmisionesAtmosfericas =:detalle and "
							+ "o.estado = true and o.idRegistroOriginal = null order by 1 asc")
					.setParameter("detalle", detalle).getResultList();
			return lista;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;
	}
	
	@SuppressWarnings("unchecked")
	public List<FacilidadesMonitoreoEmisiones> findDetalleH(DetalleEmisionesAtmosfericas detalle){
		List<FacilidadesMonitoreoEmisiones> lista = new ArrayList<FacilidadesMonitoreoEmisiones>();
		try {
			lista = (ArrayList<FacilidadesMonitoreoEmisiones>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM FacilidadesMonitoreoEmisiones o WHERE o.detalleEmisionesAtmosfericas =:detalle and "
							+ "o.estado = true  order by 1 asc")
					.setParameter("detalle", detalle).getResultList();
			return lista;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;
	}
	
}
