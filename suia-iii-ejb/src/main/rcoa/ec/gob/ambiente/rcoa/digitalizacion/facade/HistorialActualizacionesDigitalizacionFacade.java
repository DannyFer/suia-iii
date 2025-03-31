package ec.gob.ambiente.rcoa.digitalizacion.facade;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import ec.gob.ambiente.rcoa.digitalizacion.model.AutorizacionAdministrativaAmbiental;
import ec.gob.ambiente.rcoa.digitalizacion.model.HistorialactualizacionesDigitalizacion;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Usuario;

@Stateless
public class HistorialActualizacionesDigitalizacionFacade {
	
	private static final Logger LOG = Logger.getLogger(HistorialActualizacionesDigitalizacionFacade.class);

	@EJB
	private CrudServiceBean crudServiceBean;
	
	public void save(HistorialactualizacionesDigitalizacion obj, Usuario usuario){
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
	public List<HistorialactualizacionesDigitalizacion> obtenerHistorialAAA(AutorizacionAdministrativaAmbiental objetoAAA){
		try {
			List<HistorialactualizacionesDigitalizacion> lista = new ArrayList<>();
			Integer idDigitalizacion=0;
			if(objetoAAA.getId() != null)
				idDigitalizacion = objetoAAA.getId();
			Query query = crudServiceBean.getEntityManager().createQuery("Select a from HistorialactualizacionesDigitalizacion a where a.autorizacionAdministrativaAmbiental.id = :proyectpoId and  a.estado = true order by a.id ");
			query.setParameter("proyectpoId", objetoAAA.getId());
			lista = (List<HistorialactualizacionesDigitalizacion>) query.getResultList();			
			return lista;
		} catch (Exception e) {
			LOG.error("Error al realizar la consulta", e);
		}
		return null;
	}

}
