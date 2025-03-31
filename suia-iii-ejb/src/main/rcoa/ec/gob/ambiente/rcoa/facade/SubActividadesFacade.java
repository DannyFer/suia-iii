package ec.gob.ambiente.rcoa.facade;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.dto.SubactividadDto;
import ec.gob.ambiente.rcoa.model.CatalogoCIUU;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.SubActividades;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.AprobacionRequisitosTecnicos;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.EliminacionDesecho;
import ec.gob.ambiente.suia.exceptions.ServiceException;

@Stateless
public class SubActividadesFacade {
	
	@EJB
    private CrudServiceBean crudServiceBean;
	
	@SuppressWarnings("unchecked")
	public List<SubActividades> listaXactividad(CatalogoCIUU catalogo)
	{
		List<SubActividades> lista = new ArrayList<SubActividades>();
		Query sql = crudServiceBean.getEntityManager().createQuery("select s from SubActividades s where s.catalogoCIUU=:catalogo and s.subActividades is null order by s.orden");
		sql.setParameter("catalogo", catalogo);
		if(sql.getResultList().size()>0)
			lista=sql.getResultList();
		
		return lista;
	}
	
	@SuppressWarnings("unchecked")
	public List<SubActividades> listaTodoXactividad(CatalogoCIUU catalogo)
	{
		List<SubActividades> lista = new ArrayList<SubActividades>();
		Query sql = crudServiceBean.getEntityManager().createQuery("select s from SubActividades s where s.catalogoCIUU=:catalogo order by s.orden");
		sql.setParameter("catalogo", catalogo);
		if(sql.getResultList().size()>0)
			lista=sql.getResultList();
		
		return lista;
	}
	
	@SuppressWarnings("unchecked")
	public List<SubActividades> actividadParent(CatalogoCIUU catalogo)
	{		
		Query sql = crudServiceBean.getEntityManager().createQuery("select s from SubActividades s where s.catalogoCIUU=:catalogo and s.subActividades.id = null");
		sql.setParameter("catalogo", catalogo);
		if(sql.getResultList().size()>0)
			return sql.getResultList();
		
		return null;
	}
	
	public SubActividades actividadParent(Integer parent)
	{		
		Query sql = crudServiceBean.getEntityManager().createQuery("select s from SubActividades s where s.id=:parent");
		sql.setParameter("parent", parent);
		if(sql.getResultList().size()>0)
			return (SubActividades) sql.getResultList().get(0);
		
		return null;
	}
	
	public List<SubActividades> actividadHijos(Integer parent,CatalogoCIUU catalogo)
	{		
		List<SubActividades> lista = new ArrayList<>();
		Query sql = crudServiceBean.getEntityManager().createQuery("select s from SubActividades s where s.subActividades.id=:parent and s.catalogoCIUU=:catalogo order by s.orden");
		sql.setParameter("parent", parent);
		sql.setParameter("catalogo", catalogo);
		if(sql.getResultList().size()>0)
			lista= sql.getResultList();
		
		return lista;
	}
	
	
	@SuppressWarnings("unchecked")
	public List<SubActividades> actividadHijosPorRespuestaPadre(SubActividades parent)
	{		
		List<SubActividades> lista = new ArrayList<>();
		Query sql = crudServiceBean.getEntityManager().createQuery("select s from SubActividades s where s.subActividades.id=:parent and s.padreVerdadero=:respuesta order by s.orden");
		sql.setParameter("parent", parent.getId());
		sql.setParameter("respuesta", parent.getValorOpcion());
		if(sql.getResultList().size()>0)
			lista= sql.getResultList();
		
		return lista;
	}
	
	@SuppressWarnings("unchecked")
	public List<SubActividades> actividadHijosPorPadre(SubActividades parent)
	{		
		List<SubActividades> lista = new ArrayList<>();
		Query sql = crudServiceBean.getEntityManager().createQuery("select s from SubActividades s where s.subActividades.id=:parent order by orden");
		sql.setParameter("parent", parent.getId());
		if(sql.getResultList().size()>0)
			lista= sql.getResultList();
		
		return lista;
	}
	
	@SuppressWarnings("unchecked")
	public List<SubActividades> actividadPrincipalConHijos(CatalogoCIUU catalogo)
	{		
		Query sql = crudServiceBean.getEntityManager().createQuery("select s from SubActividades s where s.catalogoCIUU=:catalogo and s.subActividades.id = null order by orden");
		sql.setParameter("catalogo", catalogo);
		if(sql.getResultList().size()>0)
			return sql.getResultList();
		
		return null;
	}
	@SuppressWarnings("unchecked")
	public List<SubActividades> actividadPorBloque(CatalogoCIUU catalogo,int bloque)
	{		
		List<SubActividades> lista = new ArrayList<SubActividades>();
		Query sql = crudServiceBean.getEntityManager().createQuery("select s from SubActividades s where s.catalogoCIUU=:catalogo and s.subActividades.id = null and s.bloque=:bloque order by orden");
		sql.setParameter("catalogo", catalogo);
		sql.setParameter("bloque", bloque);
		if(sql.getResultList().size()>0)
			lista=sql.getResultList();
		
		return lista;
	}
	
	public SubActividades getSubActividadesPorProyecto(ProyectoLicenciaCoa proyecto,String name) throws ServiceException {
		StringBuilder query = new StringBuilder();
		query.append("select cs.* from coa_mae.catalog_subcategories cs ");
		query.append("join coa_mae.project_licencing_coa_ciuu plc on plc.cosu_id =cs.cosu_id and prli_primary=true ");
		query.append("join coa_mae.project_licencing_coa lc on lc.prco_id =plc.prco_id ");
		query.append("where cosu_name='"+name+"' and lc.prco_id="+proyecto.getId() );
		SubActividades sub= new SubActividades();
		try
		{
			 sub = (SubActividades) crudServiceBean.findNativeQuery(query.toString(),SubActividades.class).get(0);
		}
		catch(Exception e)
		{
			sub = new SubActividades();
		}
		return sub;
	}
	
	public Integer getNumeroBloques(CatalogoCIUU catalogo)
	{
		Integer nroBloque = 0;
		Query sql = crudServiceBean.getEntityManager().createQuery("select max(s.bloque) from SubActividades s where s.catalogoCIUU=:catalogo and s.subActividades.id = null order by 1");
		sql.setParameter("catalogo", catalogo);
		if (sql.getResultList().size() > 0) {
			nroBloque = Integer.parseInt(sql.getResultList().get(0).toString());
		}
		
		return nroBloque;
	}

	@SuppressWarnings("unchecked")
	public SubactividadDto actividadesPadrePorBloque(CatalogoCIUU catalogo, Integer bloque)
	{
		SubactividadDto nuevo = null;
		Query sql = crudServiceBean.getEntityManager().createQuery("select s from SubActividades s where s.catalogoCIUU=:catalogo and s.subActividades.id = null and s.bloque =:nroBloque order by orden");
		sql.setParameter("catalogo", catalogo);
		sql.setParameter("nroBloque", bloque);
		if(sql.getResultList().size()>0) {
			List<SubActividades> lista = (List<SubActividades>) sql.getResultList();

			nuevo = new SubactividadDto();
			nuevo.setBloque(bloque);
			nuevo.setSubActividades(lista);
		}
		
		return nuevo;
	}
	
	
	public SubActividades getSubActividadesPorProyectoMineria(ProyectoLicenciaCoa proyecto,String name) throws ServiceException {
		StringBuilder query = new StringBuilder();
		query.append("select cs.* from coa_mae.catalog_subcategories cs ");
		query.append("join coa_mae.project_licencing_coa_ciuu plc on plc.cosu_id =cs.cosu_id and prli_primary=true ");
		query.append("join coa_mae.project_licencing_coa lc on lc.prco_id =plc.prco_id ");
		query.append("join coa_mae.project_licencing_coa_ciuu_mining_concessions cm on cm.prli_id = plc.prli_id and cm.prmi_contract = true ");
		query.append("where cosu_name='"+name+"' and lc.prco_id="+proyecto.getId() + " and cm.prmi_status = true" );
		SubActividades sub= new SubActividades();
		try
		{
			 sub = (SubActividades) crudServiceBean.findNativeQuery(query.toString(),SubActividades.class).get(0);
		}
		catch(Exception e)
		{
			sub = new SubActividades();
		}
		return sub;
	}
}
