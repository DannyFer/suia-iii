package ec.gob.ambiente.rcoa.digitalizacion.facade;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.digitalizacion.model.AreasProtegidasBosques;
import ec.gob.ambiente.rcoa.digitalizacion.model.ShapeDigitalizacion;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class AreasProtegidasBosquesFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	public void guardar (AreasProtegidasBosques obj, Usuario usuario){
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
	
	public List<AreasProtegidasBosques> obtenerIntersecciones(Integer proyectoId, Integer tipo){
		List<AreasProtegidasBosques> listaIntersecciones = new ArrayList<AreasProtegidasBosques>();
		try {
			Query sql = crudServiceBean.getEntityManager().createQuery("Select p from AreasProtegidasBosques p where p.autorizacionAdministrativaAmbiental.id = :proyectoId and p.estado = true and tipo = :tipo ");
			sql.setParameter("proyectoId", proyectoId);
			sql.setParameter("tipo", tipo);
			if(sql.getResultList().size() > 0)
				listaIntersecciones = sql.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listaIntersecciones;
	}
	
	public void eliminarIntersecciones(Integer proyectoId, Integer tipo, String nombre, Usuario usuario){
		try {
			String sql ="UPDATE coa_digitalization_linkage.protected_areas set prar_status = false, prar_date_update=now(), prar_creator_user = '"+usuario.getNombre()+"' "
					+ " where enaa_id = "+proyectoId+" and prar_status = true and prar_type = "+tipo+" and prar_name = '"+nombre+"' ;";
			crudServiceBean.insertUpdateByNativeQuery(sql.toString(), null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public List<AreasProtegidasBosques> obtenerInterseccionesPorIngreso(Integer proyectoId, Integer tipo, Integer ingresoId){
		List<AreasProtegidasBosques> listaIntersecciones = new ArrayList<AreasProtegidasBosques>();
		try {
			Query sql = crudServiceBean.getEntityManager().createQuery("Select p from AreasProtegidasBosques p where p.autorizacionAdministrativaAmbiental.id = :proyectoId and p.estado = true and tipo = :tipo and p.tipoIngreso =:ingreso ");
			sql.setParameter("proyectoId", proyectoId);
			sql.setParameter("tipo", tipo);
			sql.setParameter("ingreso", ingresoId);
			if(sql.getResultList().size() > 0)
				listaIntersecciones = sql.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listaIntersecciones;
	}
}
