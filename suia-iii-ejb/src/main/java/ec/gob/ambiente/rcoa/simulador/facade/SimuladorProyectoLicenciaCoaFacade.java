package ec.gob.ambiente.rcoa.simulador.facade;


import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.simulador.model.SimuladorProyectoLicenciaCoa;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.SustanciaQuimicaPeligrosa;

@Stateless
public class SimuladorProyectoLicenciaCoaFacade {
	
	@EJB
    private CrudServiceBean crudServiceBean;
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	
	public SimuladorProyectoLicenciaCoa guardar(SimuladorProyectoLicenciaCoa proyecto) {        
        	return crudServiceBean.saveOrUpdate(proyecto);        
    }
	
	public SimuladorProyectoLicenciaCoa buscarProyecto(String codigo)
	{
		SimuladorProyectoLicenciaCoa obj = new SimuladorProyectoLicenciaCoa();
		Query sql=crudServiceBean.getEntityManager().createQuery("Select p from SimuladorProyectoLicenciaCoa p where p.codigoUnicoAmbiental=:codigo and p.estado=true");
		sql.setParameter("codigo", codigo);
		if(sql.getResultList().size()>0)
			obj=(SimuladorProyectoLicenciaCoa) sql.getSingleResult();
		
		return obj;
		
	}
	
	public SimuladorProyectoLicenciaCoa buscarProyectoPorId(Integer idProyecto)
	{
		SimuladorProyectoLicenciaCoa obj = new SimuladorProyectoLicenciaCoa();
		Query sql=crudServiceBean.getEntityManager().createQuery("Select p from SimuladorProyectoLicenciaCoa p where p.id=:idProyecto and p.estado=true");
		sql.setParameter("idProyecto", idProyecto);
		if(sql.getResultList().size()>0)
			obj=(SimuladorProyectoLicenciaCoa) sql.getSingleResult();
		
		return obj;
		
	}
	@SuppressWarnings("unchecked")
	public List<SustanciaQuimicaPeligrosa> listaSustanciasQuimicas()
	{
		List<SustanciaQuimicaPeligrosa> lista = new ArrayList<SustanciaQuimicaPeligrosa>();
		Query sql =crudServiceBean.getEntityManager().createQuery("Select s From SustanciaQuimicaPeligrosa s where s.sustanciaQuimicaPeligrosa.id in(3953,3952,3951) and s.estado=true order by s.descripcion");
		if(sql.getResultList().size()>0)
			lista=sql.getResultList();
		
		return lista;
	}

	public SustanciaQuimicaPeligrosa buscaSustanciasQuimicas(String sustanciaOtros) {
		SustanciaQuimicaPeligrosa sustancia = proyectoLicenciaCoaFacade.buscaSustanciasQuimicas(sustanciaOtros);
		return sustancia;
	}	
	
}
