package ec.gob.ambiente.suia.eia.descripcionProyecto.facade;

import java.io.Serializable;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.ActividadLicenciamiento;
import ec.gob.ambiente.suia.domain.CatalogoCategoriaFase;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.eia.descripcionProyecto.service.ActividadLicenciamientoService;



/**
 *
 * @author liliana.chacha
 *
 */
@Stateless
public class ActividadLicenciamientoFacade implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	
	@EJB
	private CrudServiceBean crudServiceBean;

	@EJB
	private ActividadLicenciamientoService actividadLicenciamientoService;

	public ActividadLicenciamiento guardarActividad(ActividadLicenciamiento actividadLicenciamiento){
		return actividadLicenciamientoService.guardarActividad(actividadLicenciamiento);
	}

	public List<ActividadLicenciamiento> actividadesLicenciamiento(EstudioImpactoAmbiental estudio) throws Exception{
		return actividadLicenciamientoService.actividadesLicenciamiento(estudio);
	}


	public List<ActividadLicenciamiento> getActividadesLicenciamiento(CatalogoCategoriaFase catalogoCategoriaFase, EstudioImpactoAmbiental estudioImpactoAmbiental) throws Exception{
		return actividadLicenciamientoService.getActividadesLicenciamiento(catalogoCategoriaFase, estudioImpactoAmbiental);
	}

	public List<ActividadLicenciamiento> buscarActividadHistorico(Integer idHistorico){
		try{
			Query query = crudServiceBean.getEntityManager().createQuery(
					"SELECT a FROM ActividadLicenciamiento a WHERE a.idHistorico = :idHistorico "
					+ "and a.estado = true");
			query.setParameter("idHistorico", idHistorico);
 
			return (List<ActividadLicenciamiento>) query.getResultList();
			
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public List<ActividadLicenciamiento> buscarActividadesModificadas(Integer estudio){
		try{
			Query query = crudServiceBean.getEntityManager().createQuery(
					"SELECT a FROM ActividadLicenciamiento a WHERE a.estudioImpactoAmbiental.id = :estudio ");
			query.setParameter("estudio", estudio);
 
			return (List<ActividadLicenciamiento>) query.getResultList();
			
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
}
