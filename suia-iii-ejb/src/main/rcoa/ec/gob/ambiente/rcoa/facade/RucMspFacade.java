package ec.gob.ambiente.rcoa.facade;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.model.NombreProyectos;
import ec.gob.ambiente.rcoa.model.RucMsp;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class RucMspFacade {
	
	@EJB
    private CrudServiceBean crudServiceBean;
	
	@SuppressWarnings("unchecked")
	public RucMsp buscarPorRuc(String ruc){
		
		try {
			
			Query sql = crudServiceBean.getEntityManager().createQuery("select r from RucMsp r where r.estado = true and r.ruc = :ruc order by 1 desc");
			sql.setParameter("ruc", ruc);
			
			List<RucMsp> objeto = (List<RucMsp>)sql.getResultList();
			
			if(objeto != null && !objeto.isEmpty()){
				return objeto.get(0);
			}else{
				return null;
			}						
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	public RucMsp buscarProyectoMspPorId(Integer id){
		try {
			
			Query sql = crudServiceBean.getEntityManager().createQuery("select r from RucMsp r where r.id = :id");
			sql.setParameter("id", id);
			
			RucMsp proyecto = (RucMsp)sql.getSingleResult();
			
			return proyecto;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	@SuppressWarnings("unchecked")
	public List<NombreProyectos> buscarProyectosPorRuc(String ruc){
		List<NombreProyectos> lista = new ArrayList<NombreProyectos>();		
		try {
			
			Query sql = crudServiceBean.getEntityManager().createQuery("select r from RucMsp r where r.estado = true and r.ruc = :ruc");
			sql.setParameter("ruc", ruc);
			
			if(sql.getResultList().size() > 0){
				
				RucMsp rucMsp = (RucMsp) sql.getResultList().get(0);
				
				Query query = crudServiceBean.getEntityManager().createQuery("select p from NombreProyectos p where p.ubicacionesGeografica = :ubicaciones and proyectoUsado = false and p.estado = true order by p.nombreProyectoMsp asc");
				query.setParameter("ubicaciones", rucMsp.getUbicacionesGeograficas());
				
				if(query.getResultList().size() > 0){
					lista = query.getResultList();
				}				
			}
			
			return lista;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;
	}
	
	
	@SuppressWarnings("unchecked")
	public List<NombreProyectos> buscarProyectosPorTipoRuc(String ruc, Integer idCat){
		List<NombreProyectos> lista = new ArrayList<NombreProyectos>();		
		try {		
						
			Query query = crudServiceBean.getEntityManager().createQuery("select p from NombreProyectos p where p.estado = true and p.catalogoGeneralCoa.id = :idCat order by p.nombreProyectoMsp asc");
			
			query.setParameter("idCat", idCat);
			
			if(query.getResultList().size() > 0){
				lista = query.getResultList();
			}			
			return lista;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;
	}
	
	
	

}
