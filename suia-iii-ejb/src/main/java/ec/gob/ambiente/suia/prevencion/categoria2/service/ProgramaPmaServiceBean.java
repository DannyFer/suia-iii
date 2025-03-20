package ec.gob.ambiente.suia.prevencion.categoria2.service;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Programa;

/**
 * 
 * @author karla.carvajal
 * 
 */
@Stateless
public class ProgramaPmaServiceBean {
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	@SuppressWarnings("unchecked")
	public List<Programa> getProgramasPorPlanId(Integer idPlan)
	{
		List<Programa> programas = new ArrayList<Programa>();
		programas = (List<Programa>) crudServiceBean.getEntityManager()
				.createQuery("From Programa p where p.cabeceraPma.id =:idPlan and p.estado = true")
				.setParameter("idPlan", idPlan).getResultList();
		
		return programas;
	}
	
	public void guardarPrograma(Programa programa)
	{
		crudServiceBean.saveOrUpdate(programa);
	}
	
	public void saveOrUpdateProgramas(List<Programa> programas)
	{
		crudServiceBean.saveOrUpdate(programas);
	}
	
	public void eliminarPrograma(Programa programa)
	{
		crudServiceBean.delete(programa);
	}
}