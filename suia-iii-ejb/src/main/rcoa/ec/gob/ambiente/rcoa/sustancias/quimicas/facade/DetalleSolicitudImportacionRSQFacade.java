package ec.gob.ambiente.rcoa.sustancias.quimicas.facade;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.sustancias.quimicas.model.DetalleSolicitudImportacionRSQ;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.SolicitudImportacionRSQ;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Usuario;

@Stateless
public class DetalleSolicitudImportacionRSQFacade {
	
	@EJB
    private CrudServiceBean crudServiceBean;	
	
	public void save(DetalleSolicitudImportacionRSQ obj, Usuario usuario){		
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
	
	public DetalleSolicitudImportacionRSQ buscarPorSolicitud(SolicitudImportacionRSQ solicitud){
		try {
			
			Query query = crudServiceBean.getEntityManager().createQuery("SELECT s from DetalleSolicitudImportacionRSQ s where s.solicitudImportacionRSQ.id = :idSolicitud and s.estado = true");
			query.setParameter("idSolicitud", solicitud.getId());
			List<DetalleSolicitudImportacionRSQ> lista = (List<DetalleSolicitudImportacionRSQ>)query.getResultList();
			
			if(lista != null && !lista.isEmpty()){
				return lista.get(0);
			}else
				return null;			
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<DetalleSolicitudImportacionRSQ> buscarPorIdSolicitud(SolicitudImportacionRSQ solicitud){
		
		List<DetalleSolicitudImportacionRSQ> lista = null;
		
		try {
			
			Query query = crudServiceBean.getEntityManager().createQuery("SELECT s from DetalleSolicitudImportacionRSQ s where s.solicitudImportacionRSQ.id = :idSolicitud and s.estado = true");
			query.setParameter("idSolicitud", solicitud.getId());
			lista = (List<DetalleSolicitudImportacionRSQ>)query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		return lista;
	}
}
