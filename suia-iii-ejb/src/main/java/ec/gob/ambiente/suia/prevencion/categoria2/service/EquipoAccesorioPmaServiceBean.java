package ec.gob.ambiente.suia.prevencion.categoria2.service;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.EquipoAccesorioPma;

/**
 * 
 * @author karla.carvajal
 * 
 */
@Stateless
public class EquipoAccesorioPmaServiceBean {
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	@SuppressWarnings("unchecked")
	public List<EquipoAccesorioPma> getEquiposAccesoriosPorFichaId(Integer idFicha)
	{
		List<EquipoAccesorioPma> equiposAccesorios = new ArrayList<EquipoAccesorioPma>();
		equiposAccesorios = (List<EquipoAccesorioPma>) crudServiceBean.getEntityManager()
				.createQuery("From EquipoAccesorioPma e where e.fichaAmbientalPma.id =:idFicha and e.estado = true")
				.setParameter("idFicha", idFicha).getResultList();
		
		return equiposAccesorios;
	}
	
	public void guardarEquipoAccesorio(EquipoAccesorioPma equipoAccesorioPma)
	{
		crudServiceBean.saveOrUpdate(equipoAccesorioPma);
	}
	
	public void saveOrUpdateEquiposAccesorios(List<EquipoAccesorioPma> equiposAccesoriosPma)
	{
		crudServiceBean.saveOrUpdate(equiposAccesoriosPma);
	}
	
	public void eliminarEquipoAccesorio(EquipoAccesorioPma equipoAccesorioPma)
	{
		crudServiceBean.delete(equipoAccesorioPma);
	}
}