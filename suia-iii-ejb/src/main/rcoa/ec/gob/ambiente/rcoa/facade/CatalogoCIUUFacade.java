package ec.gob.ambiente.rcoa.facade;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.model.CatalogoCIUU;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class CatalogoCIUUFacade {

	@EJB
    private CrudServiceBean crudServiceBean;
	
	public void guardar(CatalogoCIUU ciuu) {        
        	crudServiceBean.saveOrUpdate(ciuu);        
    }
	
	@SuppressWarnings("unchecked")
	public List<CatalogoCIUU> listaCatalogoCiuu()
	{
		List<CatalogoCIUU> lista = new ArrayList<CatalogoCIUU>();
		Query sql = crudServiceBean.getEntityManager().createQuery("select c from CatalogoCIUU c where c.actividadVisible=true order by c.codigo");
		if(sql.getResultList().size()>0)
			lista=sql.getResultList();
		
		return lista;
	}
	
	public CatalogoCIUU catalogoCiuu(String codigo)
	{
		CatalogoCIUU obj = new CatalogoCIUU();
		Query sql =crudServiceBean.getEntityManager().createQuery("select c from CatalogoCIUU c where c.codigo=:codigo and c.estado=true");
		sql.setParameter("codigo", codigo);
		if(sql.getResultList().size()>0)
			obj=(CatalogoCIUU) sql.getResultList().get(0);
		
		return obj;
	}
	
	@SuppressWarnings("unchecked")
	public List<CatalogoCIUU> listaActiviadesCiuuDisponibles()
	{
		List<CatalogoCIUU> lista = new ArrayList<CatalogoCIUU>();
		Query sql = crudServiceBean.getEntityManager().createQuery("select c from CatalogoCIUU c where c.actividadVisible=true and c.actividadBloqueada = false and c.estado=true order by c.codigo");
		if(sql.getResultList().size()>0)
			lista=sql.getResultList();
		
		return lista;
	}
}
