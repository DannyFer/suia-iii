package ec.gob.ambiente.suia.campanias.bajalealplastico.facade;


import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.campanias.bajalealplastico.domain.Informacion;
import ec.gob.ambiente.suia.campanias.bajalealplastico.domain.Institucion;
import ec.gob.ambiente.suia.campanias.bajalealplastico.domain.TipoEntidad;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class DesembotellateFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	@SuppressWarnings("unchecked")
	public List<Institucion> getInstitucionByParroquia(Integer idParroquia){
		List<Institucion> lista = new ArrayList<Institucion>();
		try {
			lista = (ArrayList<Institucion>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT i FROM Institucion i where i.parroquia.id = :idParroquia order by nombre")
					.setParameter("idParroquia", idParroquia)
					.getResultList();
			return lista;
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return lista;
	}
	
	@SuppressWarnings("unchecked")
	public Institucion getInstitucionByCodigo(String codigo){
		List<Institucion> lista = new ArrayList<Institucion>();
		try {
			lista = (ArrayList<Institucion>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT i FROM Institucion i where i.codigo = :codigo ")
					.setParameter("codigo", codigo)
					.getResultList();
			if (lista == null || lista.isEmpty()) {
				return null;
			} else {
				return lista.get(0);
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<TipoEntidad> getListaEntidades(){
		List<TipoEntidad> lista = new ArrayList<TipoEntidad>();
		try {
			lista = (ArrayList<TipoEntidad>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT i FROM TipoEntidad i where i.estado = true ")
					.getResultList();
			if (lista == null || lista.isEmpty()) {
				return null;
			} else {
				return lista;
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public TipoEntidad getTipoEntidadById(Integer id){
		List<TipoEntidad> lista = new ArrayList<TipoEntidad>();
		try {
			lista = (ArrayList<TipoEntidad>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT i FROM TipoEntidad i where i.id = :id ")
					.setParameter("id", id)
					.getResultList();
			if (lista == null || lista.isEmpty()) {
				return null;
			} else {
				return lista.get(0);
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void guardarRegistro(Informacion informacion) {
		crudServiceBean.saveOrUpdate(informacion);
	}
	
	@SuppressWarnings("unchecked")
	public List<Informacion> getListaRegistros(){
		List<Informacion> lista = new ArrayList<Informacion>();
		try {
			lista = (ArrayList<Informacion>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT i FROM Informacion i where i.estado = true ")
					.getResultList();
			if (lista == null || lista.isEmpty()) {
				return null;
			} else {
				
				for (Informacion informacion : lista) {
					Institucion infoInstitucion = getInstitucionByCodigo(informacion.getIdInstitucion());
					informacion.setInstitucion(infoInstitucion);
				}
				return lista;
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return null;
	}
}

