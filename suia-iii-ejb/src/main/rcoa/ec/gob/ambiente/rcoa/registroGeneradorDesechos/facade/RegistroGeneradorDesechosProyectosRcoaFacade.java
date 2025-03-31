package ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import ec.gob.ambiente.rcoa.dto.GeneradorCustom;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.RegistroGeneradorDesechosProyectosRcoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.RegistroGeneradorDesechosRcoa;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Usuario;

@Stateless
public class RegistroGeneradorDesechosProyectosRcoaFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	public RegistroGeneradorDesechosProyectosRcoa findById(Integer id){
		try {
			return (RegistroGeneradorDesechosProyectosRcoa) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM RegistroGeneradorDesechosProyectosRcoa o where o.id = :id")
					.setParameter("id", id)					
					.getSingleResult();			
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<RegistroGeneradorDesechosProyectosRcoa> asociados(Integer proyectoLicenciaCoa){
		try {
			return (List<RegistroGeneradorDesechosProyectosRcoa>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM RegistroGeneradorDesechosProyectosRcoa o where o.proyectoLicenciaCoa.id = :proyectoLicenciaCoa")
					.setParameter("proyectoLicenciaCoa", proyectoLicenciaCoa)					
					.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return null;
	}
	
	
	public void save(RegistroGeneradorDesechosProyectosRcoa obj, Usuario usuario){
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
	
	public void saveList(List<RegistroGeneradorDesechosProyectosRcoa> lista){
		
		crudServiceBean.saveOrUpdate(lista);
	}
	
	@SuppressWarnings("unchecked")
	public List<RegistroGeneradorDesechosProyectosRcoa> buscarPorProyectoRcoa(Integer id){
		
		List<RegistroGeneradorDesechosProyectosRcoa> lista = new ArrayList<RegistroGeneradorDesechosProyectosRcoa>();
		
		try {
			
			lista = (List<RegistroGeneradorDesechosProyectosRcoa>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM RegistroGeneradorDesechosProyectosRcoa o where o.proyectoLicenciaCoa.id = :id")
					.setParameter("id", id).getResultList();
			
			return lista;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return lista;
	}
	
	@SuppressWarnings("unchecked")
	public List<RegistroGeneradorDesechosProyectosRcoa> buscarPorTipoProyecto(Integer id, String tipoProyecto){
		List<RegistroGeneradorDesechosProyectosRcoa> lista = new ArrayList<RegistroGeneradorDesechosProyectosRcoa>();
		try {
			switch (tipoProyecto) {
			case "digitalizacion":
				lista = (List<RegistroGeneradorDesechosProyectosRcoa>) crudServiceBean
						.getEntityManager()
						.createQuery("SELECT o FROM RegistroGeneradorDesechosProyectosRcoa o where o.proyectoDigitalizado.id = :id")
						.setParameter("id", id).getResultList();
				break;

			default:
				break;
			}
			
			return lista;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;
	}
	
	@SuppressWarnings("unchecked")
	public List<RegistroGeneradorDesechosProyectosRcoa> buscarPorProyectoSuia(Integer id){
		
		List<RegistroGeneradorDesechosProyectosRcoa> lista = new ArrayList<RegistroGeneradorDesechosProyectosRcoa>();
		
		try {
			
			lista = (List<RegistroGeneradorDesechosProyectosRcoa>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM RegistroGeneradorDesechosProyectosRcoa o where o.proyectoId = :id")
					.setParameter("id", id).getResultList();
			
			return lista;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return lista;
	}
	
	@SuppressWarnings("unchecked")
	public List<RegistroGeneradorDesechosProyectosRcoa> buscarPorCodigoProyectoRcoa(String codigo){
		
		List<RegistroGeneradorDesechosProyectosRcoa> lista = new ArrayList<RegistroGeneradorDesechosProyectosRcoa>();
		
		try {
			
			lista = (List<RegistroGeneradorDesechosProyectosRcoa>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM RegistroGeneradorDesechosProyectosRcoa o where o.proyectoLicenciaCoa.codigoUnicoAmbiental = :codigo")
					.setParameter("codigo", codigo).getResultList();
			
			return lista;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return lista;
	}
	
	@SuppressWarnings("unchecked")
	public List<RegistroGeneradorDesechosProyectosRcoa> buscarPorRegistroGenerador(Integer id){
		
		List<RegistroGeneradorDesechosProyectosRcoa> lista = new ArrayList<RegistroGeneradorDesechosProyectosRcoa>();
		
		try {
			
			lista = (List<RegistroGeneradorDesechosProyectosRcoa>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM RegistroGeneradorDesechosProyectosRcoa o where o.registroGeneradorDesechosRcoa.id = :id order by 1 asc")
					.setParameter("id", id).getResultList();
			
			return lista;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return lista;
	}

	@SuppressWarnings("unchecked")
	public List<RegistroGeneradorDesechosProyectosRcoa> buscarPorProyectoDigitalizado(Integer id){
		List<RegistroGeneradorDesechosProyectosRcoa> lista = new ArrayList<RegistroGeneradorDesechosProyectosRcoa>();
		try {
			lista = (List<RegistroGeneradorDesechosProyectosRcoa>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM RegistroGeneradorDesechosProyectosRcoa o where o.proyectoDigitalizado.id = :id")
					.setParameter("id", id).getResultList();
			return lista;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;
	}

	public List<GeneradorCustom> listarGeneradoresActivosNoVinculados (String usuario) {
		String queryString = "SELECT DISTINCT "
				+ "t1.ware_id AS id, "
				+ "t2.ware_code AS codigoGenerador, "
				+ "t1.wapr_id AS idGeneradorProyecto, "
				+ "(select max(wgrd_document_number) from coa_waste_generator_record.waste_generator_record_document t where t.ware_id = t2.ware_id) as numeroGenerador "
				+ "FROM coa_waste_generator_record.waste_generator_record_project_licencing_coa t1 "
				+ "INNER JOIN coa_waste_generator_record.waste_generator_record_coa t2 ON t1.ware_id = t2.ware_id "
				+ "INNER JOIN coa_waste_generator_record.waste_generator_record_document t3 ON t2.ware_id = t3.ware_id "
				+ "WHERE "
				+ "t1.wapr_status = TRUE "
				+ "AND t2.ware_status = TRUE "
				+ "AND t3.wgrd_status = TRUE  "
				+ "AND t1.wapr_creator_user = '" + usuario + "' "
				+ "AND t1.prco_id is NULL "
				+ "AND t1.wapr_4cat_sect is NULL "
				+ "AND t1.id_proyect is NULL "
				+ "AND t1.enaa_id is NULL ";

		List<Object> result = crudServiceBean.findByNativeQuery(queryString, null);

		List<GeneradorCustom> projects = new ArrayList<GeneradorCustom>();
		for (Object object : result) {
			Object[] data = (Object[]) object;
			Integer id = (Integer) data[0];
			String codigo = (String) data[1];
			String documento = (String) data[3];
			Integer idGeneradorProyecto = (Integer) data[2];
			projects.add(new GeneradorCustom(id, codigo, documento, idGeneradorProyecto));
		}

		return projects;
	}
	
}
