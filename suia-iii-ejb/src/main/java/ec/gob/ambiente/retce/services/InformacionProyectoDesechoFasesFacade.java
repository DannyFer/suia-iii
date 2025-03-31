package ec.gob.ambiente.retce.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.retce.model.InformacionProyectoDesechoFases;
import ec.gob.ambiente.retce.model.InformacionProyecto;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Usuario;

@Stateless
public class InformacionProyectoDesechoFasesFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	public InformacionProyectoDesechoFases findById(Integer id){
		try {
			InformacionProyectoDesechoFases informacionProyectoDesechoFases = (InformacionProyectoDesechoFases) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM InformacionProyectoDesechoFases o where o.id = :id")
					.setParameter("id", id).getSingleResult();			
			return informacionProyectoDesechoFases;			
		} catch (Exception e) {
			e.printStackTrace();
		}	
		return null;
	}
	
	public void save(InformacionProyectoDesechoFases obj, Usuario usuario){
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
	public List<InformacionProyectoDesechoFases> findByProyecto(InformacionProyecto informacion){
		List<InformacionProyectoDesechoFases> lista = new ArrayList<InformacionProyectoDesechoFases>();
		try {
			lista = (ArrayList<InformacionProyectoDesechoFases>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM InformacionProyectoDesechoFases o where o.estado = true and o.informacionProyecto.id = :id")
					.setParameter("id", informacion.getId()).getResultList();
			return lista;
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return lista;
	}

	public void guardarDesechosFases(InformacionProyecto informacion, List<String> listaIdFases, Usuario usuario) {
		try {
			String listaId = "0";
			StringBuilder sql = new StringBuilder();
			for (String objString : listaIdFases) {
				listaId += ", "+objString;
			}
			
			// para deshabilita los no seleccionados
			sql.append("update retce.project_information_waste_fases ");
			sql.append("set piwf_status = false, piwf_user_update = '"+usuario.getNombre()+"', piwf_date_update = now() "); 
			sql.append("where proj_id = "+informacion.getId()+" ");
			sql.append("and gcde_id not in ("+listaId+") ");
			sql.append("and piwf_status = true ");
			crudServiceBean.insertUpdateByNativeQuery(sql.toString(), null);
			// para insertar los seleccionados
			sql = new StringBuilder();
			sql.append("INSERT INTO retce.project_information_waste_fases( ");
			sql.append("proj_id, gcde_id, piwf_status, piwf_user_create, piwf_date_create) "); 
			sql.append("select "+informacion.getId()+" , gcde_id, true, '"+usuario.getNombre()+"', now() ");
			sql.append("from retce.general_catalog_details ");
			sql.append("where gect_id = 5 and gcde_id in ("+listaId+") ");
			sql.append("and gcde_id not in ( ");
			sql.append("select gcde_id ");
			sql.append("from retce.project_information_waste_fases ");
			sql.append("where proj_id = "+informacion.getId()+"  ");
			sql.append("and gcde_id in ("+listaId+") ");
			sql.append("and piwf_status = true ");
			sql.append(") ");
			crudServiceBean.insertUpdateByNativeQuery(sql.toString(), null);
		} catch (Exception e) {

		}		
	}

}
