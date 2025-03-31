package ec.gob.ambiente.rcoa.facade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.RegistroAmbientalRcoa;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class RegistroAmbientalCoaFacade {
	
	@EJB
    private CrudServiceBean crudServiceBean;
	
	public RegistroAmbientalRcoa guardar(RegistroAmbientalRcoa registroAmbiental) {        
    	return crudServiceBean.saveOrUpdate(registroAmbiental);        
	}
	
	@SuppressWarnings("unchecked")
	public RegistroAmbientalRcoa obtenerRegistroAmbientalPorProyecto(ProyectoLicenciaCoa proyecto )
	{
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("proyectoId", proyecto.getId());
		
		List<RegistroAmbientalRcoa> lista = (List<RegistroAmbientalRcoa>) crudServiceBean
					.findByNamedQuery(
							RegistroAmbientalRcoa.GET_REGISTROAMBIENTAL_POR_PROYECTO,
							parameters);
		if(lista.size() > 0 ){
			return lista.get(0);
		}
		return  null;
	}
	
	@SuppressWarnings("unchecked")
	public Integer contarRegistroAmbientalPorResolucion(Integer idRegistro, String numeroresolucion)
	{
		List<RegistroAmbientalRcoa> lista = (List<RegistroAmbientalRcoa>) crudServiceBean
					.getEntityManager().createQuery(" select r from RegistroAmbientalRcoa r"
							+ " where r.estado is true and r.id != :idRegistro"
							+ " and (lower(r.numeroResolucion) = :numeroResolucion or lower(r.resolucionAmbietalFisica) = :numeroResolucion) ")
							.setParameter("numeroResolucion", numeroresolucion.toLowerCase())
							.setParameter("idRegistro", idRegistro).getResultList();
		if(lista.size() > 0 ){
			return lista.size();
		}
		return  0;
	}
	
	public void activarIngresoRegistroAmbiental(Integer idProyecto){
		Query sqlUpdateTarea = crudServiceBean.getEntityManager().createNativeQuery(" update coa_environmental_record.environmental_record "
				+ " set enre_registration_end = false "
				+ " where prco_id = "+idProyecto+" and enre_status is true; ");
		sqlUpdateTarea.executeUpdate();
	}
}