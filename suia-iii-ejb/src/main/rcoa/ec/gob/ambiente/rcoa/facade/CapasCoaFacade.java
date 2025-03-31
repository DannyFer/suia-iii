package ec.gob.ambiente.rcoa.facade;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.model.CapasCoa;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class CapasCoaFacade {

	@EJB
    private CrudServiceBean crudServiceBean;
	
	@SuppressWarnings("unchecked")
	public List<CapasCoa> listaCapas()
	{
		List<CapasCoa> lista = new ArrayList<CapasCoa>();
		Query sql = crudServiceBean.getEntityManager().createQuery("select c from CapasCoa c where c.estado=true");
		if(sql.getResultList().size()>0)
			lista=sql.getResultList();
		
		return lista;
	}
	
	public CapasCoa getCapasById(Integer idCapa) {
		CapasCoa result = new CapasCoa();
		Query sql = crudServiceBean.getEntityManager().createQuery("select c from CapasCoa c where c.id=:idCapa and c.estado=true");
		sql.setParameter("idCapa", idCapa);
		if (sql.getResultList().size() > 0)
			result = (CapasCoa) sql.getResultList().get(0);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public List<CapasCoa> listaCapasCertificadoInterseccion()
	{
		List<CapasCoa> lista = new ArrayList<CapasCoa>();
		Query sql = crudServiceBean.getEntityManager().createQuery("select c from CapasCoa c where c.estado=true and c.certificadoInterseccion=true");
		if(sql.getResultList().size()>0)
			lista=sql.getResultList();
		
		return lista;
	}
	
	public CapasCoa getCapaByNombre(String nombreCapa) {
		CapasCoa result = new CapasCoa();
		Query sql = crudServiceBean.getEntityManager().createQuery("select c from CapasCoa c where UPPER(c.nombre)=:nombreCapa and c.estado=true");
		sql.setParameter("nombreCapa", nombreCapa.toUpperCase());
		if (sql.getResultList().size() > 0)
			result = (CapasCoa) sql.getResultList().get(0);
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<CapasCoa> listaCapasSoapInterseccion()
	{
		List<CapasCoa> lista = new ArrayList<CapasCoa>();
		Query sql = crudServiceBean.getEntityManager().createQuery("select c from CapasCoa c where c.estado=true and soapInterseccion = true order by c.pesoTiempoCarga desc");
		if(sql.getResultList().size()>0)
			lista=sql.getResultList();
		
		return lista;
	}

	@SuppressWarnings("unchecked")
	public Integer totalPesoCapasSoapInterseccion()
	{
		List<CapasCoa> lista = new ArrayList<CapasCoa>();
		Integer totalPeso = 0;
		Query sql = crudServiceBean.getEntityManager().createQuery("select c from CapasCoa c where c.estado=true and soapInterseccion = true order by c.pesoTiempoCarga desc");
		if(sql.getResultList().size()>0) {
			lista=sql.getResultList();

			for(CapasCoa capa : lista) {
				totalPeso = totalPeso + capa.getPesoTiempoCarga();
			}
		}
		
		return totalPeso;
	}
}
