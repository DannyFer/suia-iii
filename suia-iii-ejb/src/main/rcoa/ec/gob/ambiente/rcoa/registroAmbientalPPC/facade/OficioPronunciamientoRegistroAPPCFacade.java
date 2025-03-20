package ec.gob.ambiente.rcoa.registroAmbientalPPC.facade;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.model.OficioPronunciamientoPPC;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Usuario;


@Stateless
public class OficioPronunciamientoRegistroAPPCFacade {

	@EJB
	private CrudServiceBean crudServiceBean;

	public OficioPronunciamientoPPC guardar(OficioPronunciamientoPPC obj, Usuario usuario){
		if(obj.getId()==null){
			obj.setUsuarioCreacion(usuario.getNombre());
			obj.setFechaCreacion(new Date());			
		}
		else{
			obj.setUsuarioModificacion(usuario.getNombre());
			obj.setFechaModificacion(new Date());			
		}		
		return crudServiceBean.saveOrUpdate(obj);
	}
	
	public OficioPronunciamientoPPC obtenerPorCodigoProyecto(String codigoProyecto) {
		try {
			Query query = crudServiceBean.getEntityManager().createQuery("SELECT o FROM CertificadoInterseccionOficioCoa o WHERE o.estado=true and o.proyectoLicenciaCoa.codigoUnicoAmbiental=:codigoProyecto ORDER BY 1 desc");
			query.setParameter("codigoProyecto", codigoProyecto);
			query.setMaxResults(1);
			return (OficioPronunciamientoPPC)query.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}		
	}
	
	public OficioPronunciamientoPPC obtenerPorCodigoProyectoTarea(Integer idRegistroAmbiental, Integer tipoOficio, Long idTarea) {
		try {
			Query query = crudServiceBean
					.getEntityManager()
					.createQuery(
							"SELECT o FROM OficioPronunciamientoPPC o WHERE o.estado=true and o.registroAmbiental.id=:codigoProyecto "
							+ " and o.tipo = :tipoOficio "
							+ " ORDER BY 1 desc");
			query.setParameter("codigoProyecto", idRegistroAmbiental);
			query.setParameter("tipoOficio", tipoOficio);
			query.setMaxResults(1);
			return (OficioPronunciamientoPPC) query.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}