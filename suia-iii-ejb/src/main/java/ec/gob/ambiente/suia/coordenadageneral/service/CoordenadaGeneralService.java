package ec.gob.ambiente.suia.coordenadageneral.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.ActividadLicenciamiento;
import ec.gob.ambiente.suia.domain.CoordenadaGeneral;

@Stateless
public class CoordenadaGeneralService {
	@EJB
	private CrudServiceBean crudServiceBean;

	public void guardar(CoordenadaGeneral coordenadaGeneral) {
		crudServiceBean.saveOrUpdate(coordenadaGeneral);
	}

	public void saveOrUpdate(List<CoordenadaGeneral> listaCoordenadaGeneral) {
		crudServiceBean.saveOrUpdate(listaCoordenadaGeneral);
	}
	
	public List<CoordenadaGeneral> coordenadasGeneralXTablaId(Integer idTabla,String nombreTabla)
	{
		 Map<String, Object> params = new HashMap<String, Object>();
	        params.put("idTabla", idTabla);
	        params.put("nombreTabla", nombreTabla);
	        @SuppressWarnings("unchecked")
			List<CoordenadaGeneral> listaCoordenadas = (List<CoordenadaGeneral>) crudServiceBean.findByNamedQuery(CoordenadaGeneral.LISTAR_POR_ID_NOMBRE_TABLA, params);
	       return listaCoordenadas;
	}
	
	public void modificar(CoordenadaGeneral coordenadaGeneral) {
		crudServiceBean.saveOrUpdate(coordenadaGeneral);
	}
	
	public void eliminiar(CoordenadaGeneral coordenadaGeneral){
		crudServiceBean.delete(coordenadaGeneral);
	}
	
	@SuppressWarnings("unchecked")
	public List<CoordenadaGeneral> coordenadaGeneralPorHistorico(Integer idHistorico, Integer notificacion){
		
		try{
			
			Query query = crudServiceBean.getEntityManager().createQuery("Select c FROM CoordenadaGeneral c WHERE  c.idHistorico= :idHistorico AND c.numeroNotificacion = :notificacion order by 1 desc");
			query.setParameter("idHistorico", idHistorico);
			query.setParameter("notificacion", notificacion);
			
			List<CoordenadaGeneral> coordenadas = query.getResultList();
			if(coordenadas == null || coordenadas.isEmpty()){
				return null;
			}else
				return coordenadas;
			
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}		
	}
	
	@SuppressWarnings("unchecked")
	public List<CoordenadaGeneral> coordenadaGeneralConHistorico(Integer idTabla,String nombreTabla){
		
		try{
			
			Query query = crudServiceBean.getEntityManager().createQuery("SELECT c FROM CoordenadaGeneral c WHERE "
					+ "c.nombreTabla = :nombreTabla AND c.idTable = :idTabla AND c.estado = true order by c.orden asc");
			query.setParameter("nombreTabla", nombreTabla);
			query.setParameter("idTabla", idTabla);
			
			List<CoordenadaGeneral> coordenadas = query.getResultList();
			if(coordenadas == null || coordenadas.isEmpty()){
				return null;
			}else
				return coordenadas;
			
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		
	}
}
