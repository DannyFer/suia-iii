package ec.gob.ambiente.rcoa.sustancias.quimicas.facade;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.model.GestionarProductosQuimicosProyectoAmbiental;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.RegistroSustanciaQuimica;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.UbicacionSustancia;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Usuario;


@Stateless
public class UbicacionSustanciaQuimicaFacade {

	@EJB
	private CrudServiceBean crudServiceBean;	
	
	public void guardar(UbicacionSustancia obj, Usuario usuario){			
		if(obj.getId()==null){
			obj.setUsuarioCreacion(usuario.getNombre());
			obj.setFechaCreacion(new Date());			
		}
		else{
			obj.setUsuarioModificacion(usuario.getNombre());
			obj.setFechaModificacion(new Date());			
		}	
		crudServiceBean.saveOrUpdate(obj);
	} 
	
	@SuppressWarnings("unchecked")
	public List<UbicacionSustancia> obtenerUbicacionesPorRSQ(RegistroSustanciaQuimica registroSustanciaQuimica) {
		
		List<UbicacionSustancia> resultList=new ArrayList<UbicacionSustancia>();
		try {
			Query query = crudServiceBean.getEntityManager().createQuery("SELECT o FROM UbicacionSustancia o WHERE o.estado=true and o.registroSustanciaQuimica.id=:idRSQ ORDER BY 1");
			query.setParameter("idRSQ", registroSustanciaQuimica.getId());			
			resultList=(List<UbicacionSustancia>)query.getResultList();
			for (UbicacionSustancia ubicacionSustancia : resultList) {
				if(ubicacionSustancia.getLugar()!=null && !ubicacionSustancia.getLugar().isEmpty()) {
					String[] lugares=ubicacionSustancia.getLugar().split(";");
					ubicacionSustancia.setLugaresArray(lugares);
				}				
			}
		} catch (NoResultException nre) {
			//TODO
		} catch (Exception e) {
			e.printStackTrace();
			
		}		
		return resultList;
	}
	
	@SuppressWarnings("unchecked")
	public List<UbicacionSustancia> obtenerUbicacionesPorIdSustancia(GestionarProductosQuimicosProyectoAmbiental sustancia) {
		
		List<UbicacionSustancia> resultList=new ArrayList<UbicacionSustancia>();
		try {
			Query query = crudServiceBean.getEntityManager().createQuery("SELECT o FROM UbicacionSustancia o WHERE o.estado=true and o.gestionarProductosQuimicosProyectoAmbiental.id=:idRSQ ORDER BY 1");
			query.setParameter("idRSQ", sustancia.getId());			
			resultList=(List<UbicacionSustancia>)query.getResultList();
			for (UbicacionSustancia ubicacionSustancia : resultList) {
				if(ubicacionSustancia.getLugar()!=null && !ubicacionSustancia.getLugar().isEmpty()) {
					String[] lugares=ubicacionSustancia.getLugar().split(";");
					ubicacionSustancia.setLugaresArray(lugares);
				}				
			}
		} catch (NoResultException nre) {
			//TODO
		} catch (Exception e) {
			e.printStackTrace();
			
		}		
		return resultList;
	}
}
