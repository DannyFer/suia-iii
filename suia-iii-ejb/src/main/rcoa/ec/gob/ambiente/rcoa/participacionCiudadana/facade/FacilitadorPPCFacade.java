package ec.gob.ambiente.rcoa.participacionCiudadana.facade;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.participacionCiudadana.model.FacilitadorPPC;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Usuario;

@Stateless
public class FacilitadorPPCFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	
	public FacilitadorPPC guardar(FacilitadorPPC facilitador){
		return crudServiceBean.saveOrUpdate(facilitador);
	}
	
	public FacilitadorPPC facilitador(ProyectoLicenciaCoa proyecto, Usuario usuario)
	{
		Query sql = crudServiceBean.getEntityManager().createQuery("select f from FacilitadorPPC f where f.proyectoLicenciaCoa=:proyecto and f.usuario=:usuario and f.estado=true");
		sql.setParameter("proyecto", proyecto);
		sql.setParameter("usuario", usuario);
		if(sql.getResultList().size()>0)
			return (FacilitadorPPC) sql.getResultList().get(0);
		else
			return null;
			
	}
	
	public boolean existeFacilitadorPrincipal(ProyectoLicenciaCoa proyecto)
	{
		Query sql = crudServiceBean.getEntityManager().createQuery("select f from FacilitadorPPC f where f.proyectoLicenciaCoa=:proyecto and f.esPrincipal=true and f.estado=true");
		sql.setParameter("proyecto", proyecto);
		if(sql.getResultList().size()>0)
			return true;
		else
			return false;
	}
	
	public FacilitadorPPC facilitadorPrincipal(ProyectoLicenciaCoa proyecto)
	{
		Query sql = crudServiceBean.getEntityManager().createQuery("select f from FacilitadorPPC f where f.proyectoLicenciaCoa=:proyecto and f.esPrincipal=true and f.estado=true");
		sql.setParameter("proyecto", proyecto);
		if(sql.getResultList().size()>0)
			return (FacilitadorPPC) sql.getResultList().get(0);
		else
			return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<FacilitadorPPC> facilitadores(ProyectoLicenciaCoa proyecto)
	{
		List<FacilitadorPPC> lista = new ArrayList<FacilitadorPPC>();
		Query sql = crudServiceBean.getEntityManager().createQuery("select f from FacilitadorPPC f where f.proyectoLicenciaCoa=:proyecto and f.aceptaProyecto=true and f.esPrincipal=false and f.estado=true");
		sql.setParameter("proyecto", proyecto);
		if(sql.getResultList().size()>0)
			lista=sql.getResultList();
		
		return lista;
	}
	
	@SuppressWarnings("unchecked")
	public List<FacilitadorPPC> allFacilitadores(ProyectoLicenciaCoa proyecto)
	{
		List<FacilitadorPPC> lista = new ArrayList<FacilitadorPPC>();
		Query sql = crudServiceBean.getEntityManager().createQuery("select f from FacilitadorPPC f where f.proyectoLicenciaCoa=:proyecto and f.aceptaProyecto=true and f.estado=true");
		sql.setParameter("proyecto", proyecto);
		if(sql.getResultList().size()>0)
			lista=sql.getResultList();
		
		return lista;
	}
	
	@SuppressWarnings("unchecked")
	public List<Usuario> facilitadoresConfirmado(ProyectoLicenciaCoa proyecto)
	{
		List<Usuario> lista = new ArrayList<Usuario>();
		Query sql = crudServiceBean.getEntityManager().createQuery("select f.usuario from FacilitadorPPC f where f.proyectoLicenciaCoa=:proyecto and f.aceptaProyecto=true and f.estado=true");
		sql.setParameter("proyecto", proyecto);
		if(sql.getResultList().size()>0)
			lista=sql.getResultList();
		
		return lista;
	}
	
	@SuppressWarnings("unchecked")
	public List<Usuario> facilitadoresRezhazado(ProyectoLicenciaCoa proyecto)
	{
		List<Usuario> lista = new ArrayList<Usuario>();
		Query sql = crudServiceBean.getEntityManager().createQuery("select f.usuario from FacilitadorPPC f where f.proyectoLicenciaCoa=:proyecto and f.aceptaProyecto=false and f.estado=true");
		sql.setParameter("proyecto", proyecto);
		if(sql.getResultList().size()>0)
			lista=sql.getResultList();
		
		return lista;
	}
	
	@SuppressWarnings("unchecked")
	public List<FacilitadorPPC> facilitadoresAdicionales(ProyectoLicenciaCoa proyecto)
	{
		List<FacilitadorPPC> lista = new ArrayList<FacilitadorPPC>();
		Query sql = crudServiceBean.getEntityManager().createQuery("select f from FacilitadorPPC f where f.proyectoLicenciaCoa=:proyecto and f.aceptaProyecto=true and esAdicional=true and f.estado=true");
		sql.setParameter("proyecto", proyecto);
		if(sql.getResultList().size()>0)
			lista=sql.getResultList();
		
		return lista;
	}
	
	@SuppressWarnings("unchecked")
	public List<Usuario> usuariosFacilitadoresAsignadosPendientesAceptacion(ProyectoLicenciaCoa proyecto)
	{
		List<Usuario> lista = new ArrayList<Usuario>();
		Query sql = crudServiceBean.getEntityManager().createQuery("select f.usuario from FacilitadorPPC f where f.proyectoLicenciaCoa=:proyecto and f.aceptaProyecto=null and f.estado=true");
		sql.setParameter("proyecto", proyecto);
		if(sql.getResultList().size()>0)
			lista=sql.getResultList();
		
		return lista;
	}
	
	@SuppressWarnings("unchecked")
	public List<FacilitadorPPC> facilitadoresAsignadosPendientesAceptacion(ProyectoLicenciaCoa proyecto)
	{
		List<FacilitadorPPC> lista = new ArrayList<FacilitadorPPC>();
		Query sql = crudServiceBean.getEntityManager().createQuery("select f from FacilitadorPPC f where f.proyectoLicenciaCoa=:proyecto and f.aceptaProyecto=null and f.estado=true");
		sql.setParameter("proyecto", proyecto);
		if(sql.getResultList().size()>0)
			lista=sql.getResultList();
		
		return lista;
	}
	
	public FacilitadorPPC facilitadorPendienteAceptacion(ProyectoLicenciaCoa proyecto, Usuario usuario)
	{
		Query sql = crudServiceBean.getEntityManager().createQuery("select f from FacilitadorPPC f where f.proyectoLicenciaCoa=:proyecto and f.usuario=:usuario and f.aceptaProyecto=null and f.estado=true");
		sql.setParameter("proyecto", proyecto);
		sql.setParameter("usuario", usuario);
		if(sql.getResultList().size()>0)
			return (FacilitadorPPC) sql.getResultList().get(0);
		else
			return null;
	}

}
