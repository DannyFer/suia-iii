package ec.gob.ambiente.rcoa.viabilidadTecnica.facade;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.viabilidadTecnica.model.MedioFrecuenciaMedida;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Usuario;

@Stateless
public class MedioFrecuenciaMedidaFacade {
	
	@EJB
    private CrudServiceBean crudServiceBean;
	
	public void guardar(MedioFrecuenciaMedida obj, Usuario usuario){
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
	
	public MedioFrecuenciaMedida buscarPorId(Integer id){
		try {
			return (MedioFrecuenciaMedida)crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM AspectoViabilidadTecnica o where o.id = :id")
					.setParameter("id", id).getSingleResult();	
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<MedioFrecuenciaMedida> obtenerPMaViabilidad(boolean manejaDesechos, int tipoPlan){
		
		List<MedioFrecuenciaMedida> lista = new ArrayList<MedioFrecuenciaMedida>();
		try {
			
			Query sql = crudServiceBean.getEntityManager().createQuery("SELECT m FROM MedioFrecuenciaMedida m where "
					+ "m.aspectoViabilidad.planManejoAmbientalPma.faseTipoPlan.id = :tipoPlan and m.estado = true "
					+ "and m.aspectoViabilidad.manejaDesechos = :manejaDesechos");
			
			sql.setParameter("tipoPlan", tipoPlan);
			sql.setParameter("manejaDesechos", manejaDesechos);
			
			lista = (List<MedioFrecuenciaMedida>)sql.getResultList();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;
	}
	

}
