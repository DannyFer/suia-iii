package ec.gob.ambiente.rcoa.sustancias.quimicas.facade;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.sustancias.quimicas.model.PermisoDeclaracionRSQ;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.RegistroSustanciaQuimica;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.SustanciaQuimicaPeligrosa;

@Stateless
public class PermisoDeclaracionRSQFacade {

	@EJB
	private CrudServiceBean crudServiceBean;

	public void guardar(PermisoDeclaracionRSQ obj){		
		crudServiceBean.saveOrUpdate(obj);
	}
	
	@SuppressWarnings("unchecked")
	public List<PermisoDeclaracionRSQ> obtenerPermisosPorRegistro(RegistroSustanciaQuimica registro){
		List<PermisoDeclaracionRSQ> resultList=new ArrayList<>();
		try{

			Query query = crudServiceBean.getEntityManager().createQuery(
				"SELECT p FROM PermisoDeclaracionRSQ P where p.estado = true and p.registroSustanciaQuimica.id = :id and p.idPadre is null");
			query.setParameter("id", registro.getId());

			resultList = (List<PermisoDeclaracionRSQ>)query.getResultList();

		}catch(Exception ex){
			ex.printStackTrace();
		}
		return resultList;
	}
	
	@SuppressWarnings("unchecked")
	public List<Integer> obtenerIdSustanciasPorRegistro(RegistroSustanciaQuimica registro){
		List<Integer> resultList=new ArrayList<>();
		try{

			Query query = crudServiceBean.getEntityManager()
					.createQuery("SELECT p.sustanciaQuimica.id FROM PermisoDeclaracionRSQ P where p.estado = true "
							+ "and p.registroSustanciaQuimica.id = :id and p.declaracionSustancias = true and p.idPadre is null");
			query.setParameter("id", registro.getId());

			resultList = (List<Integer>) query.getResultList();

		}catch(Exception ex){
			ex.printStackTrace();
		}
		return resultList;
	}

	@SuppressWarnings("unchecked")
	public List<PermisoDeclaracionRSQ> obtenerSustanciasPorRegistro(RegistroSustanciaQuimica registro){
		List<PermisoDeclaracionRSQ> resultList=new ArrayList<>();
		try{

			Query query = crudServiceBean.getEntityManager()
					.createQuery("SELECT p FROM PermisoDeclaracionRSQ p where p.estado = true "
							+ "and p.registroSustanciaQuimica.id = :id and p.declaracionSustancias = true and p.idPadre is null");
			query.setParameter("id", registro.getId());

			resultList = (List<PermisoDeclaracionRSQ>) query.getResultList();

		}catch(Exception ex){
			ex.printStackTrace();
		}
		return resultList;
	}
	
	@SuppressWarnings("unchecked")
	public List<SustanciaQuimicaPeligrosa> obtenerSustanciasPorRSQ(RegistroSustanciaQuimica registro){
		List<SustanciaQuimicaPeligrosa> resultList=new ArrayList<>();
		try{

			Query query = crudServiceBean.getEntityManager()
					.createQuery("SELECT p.sustanciaQuimica FROM PermisoDeclaracionRSQ p where p.estado = true "
							+ "and p.registroSustanciaQuimica.id = :id and p.declaracionSustancias = true and p.idPadre is null");
			query.setParameter("id", registro.getId());

			resultList = (List<SustanciaQuimicaPeligrosa>) query.getResultList();

		}catch(Exception ex){
			ex.printStackTrace();
		}
		return resultList;
	}

	@SuppressWarnings("unchecked")
	public List<PermisoDeclaracionRSQ> obtenerSustanciasPorRSQSustancia(RegistroSustanciaQuimica registro, SustanciaQuimicaPeligrosa sustancia){
		List<PermisoDeclaracionRSQ> resultList=new ArrayList<>();
		try{

			Query query = crudServiceBean.getEntityManager()
					.createQuery("SELECT p FROM PermisoDeclaracionRSQ p where p.estado = true "
							+ "and p.registroSustanciaQuimica.id = :id and p.declaracionSustancias = true " 
							+ "and p.sustanciaQuimica = :sustancia and p.idPadre is null");
			query.setParameter("id", registro.getId());
			query.setParameter("sustancia", sustancia);

			resultList = (List<PermisoDeclaracionRSQ>) query.getResultList();

		}catch(Exception ex){
			ex.printStackTrace();
		}
		return resultList;
	}

	@SuppressWarnings("unchecked")
	public List<PermisoDeclaracionRSQ> obtenerPermisoImportacion(RegistroSustanciaQuimica registro){
		List<PermisoDeclaracionRSQ> resultList=new ArrayList<>();
		try{

			Query query = crudServiceBean.getEntityManager()
					.createQuery("SELECT p FROM PermisoDeclaracionRSQ p where p.estado = true "
							+ "and p.registroSustanciaQuimica.id = :id and p.licenciaImportacion = true and p.idPadre is null");
			query.setParameter("id", registro.getId());

			resultList = (List<PermisoDeclaracionRSQ>) query.getResultList();

		}catch(Exception ex){
			ex.printStackTrace();
		}
		return resultList;
	}

		@SuppressWarnings("unchecked")
	public PermisoDeclaracionRSQ obtenerPermisoPorId(Integer id){		
		try{
			List<PermisoDeclaracionRSQ> resultList=new ArrayList<>();
			Query query = crudServiceBean.getEntityManager().createQuery(
				"SELECT p FROM PermisoDeclaracionRSQ P where p.estado = true and p.id = :id and p.idPadre is null");
			query.setParameter("id", id);

			resultList = (List<PermisoDeclaracionRSQ>)query.getResultList();
			if(resultList != null && !resultList.isEmpty()){
				return resultList.get(0);
			}			

		}catch(Exception ex){
			ex.printStackTrace();
		}
		return null;
	}

}
