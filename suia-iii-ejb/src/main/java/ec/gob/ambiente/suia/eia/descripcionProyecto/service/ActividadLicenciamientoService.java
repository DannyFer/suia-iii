package ec.gob.ambiente.suia.eia.descripcionProyecto.service;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.ActividadLicenciamiento;
import ec.gob.ambiente.suia.domain.CatalogoCategoriaFase;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;

@Stateless
public class ActividadLicenciamientoService implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = -2618641391920131523L;

	@EJB
	private CrudServiceBean crudServiceBean;

	public ActividadLicenciamiento guardarActividad(ActividadLicenciamiento actividadLicenciamiento){
		return crudServiceBean.saveOrUpdate(actividadLicenciamiento);
	}

	@SuppressWarnings("unchecked")
	public List<ActividadLicenciamiento> actividadesLicenciamiento(EstudioImpactoAmbiental estudio) throws Exception{
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idEstudio", estudio.getId());
		return (List<ActividadLicenciamiento>) crudServiceBean.findByNamedQuery(ActividadLicenciamiento.findByEstudio, parameters);
	}

	@SuppressWarnings("unchecked")
	public List<ActividadLicenciamiento> getActividadesLicenciamiento(CatalogoCategoriaFase catalogoCategoriaFase, EstudioImpactoAmbiental estudioImpactoAmbiental) throws Exception{
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("paramCatalogo", catalogoCategoriaFase);
		parameters.put("paramEstudio", estudioImpactoAmbiental);
		return (List<ActividadLicenciamiento>) crudServiceBean.findByNamedQuery(ActividadLicenciamiento.FIND_BY_CATALOGO_CATEGORIA_FASE_AND_ESTUDIO, parameters);
	}
	
	@SuppressWarnings("unchecked")
	public List<ActividadLicenciamiento> actividadesLicenciamientoHistorico(Integer idHistorico, Integer notificacion){
		try{
		Query query = crudServiceBean.getEntityManager().createQuery("Select a FROM ActividadLicenciamiento a WHERE a.idHistorico= :idHistorico and a.numeroNotificacion = :notificacion order by 1 desc");
		query.setParameter("idHistorico", idHistorico);
		query.setParameter("notificacion", notificacion);

		List<ActividadLicenciamiento> actividades = query.getResultList();

		if (actividades == null || actividades.isEmpty()) {
			return null;
		} else 
			return actividades;
		
		}catch (Exception e) {			
			e.printStackTrace();
			return null;
		}
	}

}
