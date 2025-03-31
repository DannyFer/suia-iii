package ec.gob.ambiente.retce.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;


import ec.gob.ambiente.retce.model.DetalleDescargasLiquidasTratamientoAguas;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Usuario;

@Stateless
public class DetalleDescargasTratamientoFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	public DetalleDescargasLiquidasTratamientoAguas findById(Integer id){
		try {
			DetalleDescargasLiquidasTratamientoAguas DetalleDescargasLiquidasTratamientoAguas = (DetalleDescargasLiquidasTratamientoAguas) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM DetalleDescargasLiquidasTratamientoAguas o where o.id = :id")
					.setParameter("id", id).getSingleResult();
			return DetalleDescargasLiquidasTratamientoAguas;
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void save(DetalleDescargasLiquidasTratamientoAguas obj, Usuario usuario){
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
	public List<DetalleDescargasLiquidasTratamientoAguas> findByIdDetalle(Integer idDetalle){
		List<DetalleDescargasLiquidasTratamientoAguas> lista = new ArrayList<DetalleDescargasLiquidasTratamientoAguas>();
		try {
			lista = (ArrayList<DetalleDescargasLiquidasTratamientoAguas>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM DetalleDescargasLiquidasTratamientoAguas o where o.estado = true and "
							+ "o.detalleDescargasLiquidas.id = :id order by 1 desc")
					.setParameter("id", idDetalle).getResultList();
		}catch (NoResultException e) {
			return lista;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return lista;		
	}
}
