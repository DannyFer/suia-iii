package ec.gob.ambiente.rcoa.digitalizacion.facade;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import ec.gob.ambiente.rcoa.digitalizacion.model.ProyectoAsociadoDigitalizacion;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Usuario;

@Stateless
public class ProyectoAsociadoDigitalizacionFacade {
	
	private static final Logger LOG = Logger.getLogger(ProyectoAsociadoDigitalizacionFacade.class);

	@EJB
	private CrudServiceBean crudServiceBean;
	
	
	public void guardar(ProyectoAsociadoDigitalizacion obj, Usuario usuario){
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
	public List<ProyectoAsociadoDigitalizacion> buscarProyectosAsociados(int idProyectoPrincipal){
		List<ProyectoAsociadoDigitalizacion> lista = new ArrayList<ProyectoAsociadoDigitalizacion>();
		try {
			Query query = crudServiceBean.getEntityManager().createQuery("SELECT a from ProyectoAsociadoDigitalizacion a where a.estado = true and a.autorizacionAdministrativaAmbiental.id = :id order by a.sistemaOriginal, a.tipoProyecto");
			query.setParameter("id", idProyectoPrincipal);
			lista = (List<ProyectoAsociadoDigitalizacion>)query.getResultList();
			return lista;
		} catch (Exception e) {
			LOG.error("Error al recuperar proyectos asociados", e);
		}
		return lista;
	}
	
	@SuppressWarnings("unchecked")
	public List<ProyectoAsociadoDigitalizacion> buscarProyectoAsociadoPorProyecto(int idProyectoPrincipal, int idproyectoAsociado){	
		List<ProyectoAsociadoDigitalizacion> lista = new ArrayList<ProyectoAsociadoDigitalizacion>();
		try {
			Query query = crudServiceBean.getEntityManager().createQuery("SELECT a from ProyectoAsociadoDigitalizacion a where a.estado = true and a.autorizacionAdministrativaAmbiental.id = :id and a.proyectoAsociadoId = :idAsociado ");
			query.setParameter("id", idProyectoPrincipal);
			query.setParameter("idAsociado", idproyectoAsociado);
			lista = (List<ProyectoAsociadoDigitalizacion>)query.getResultList();
			return lista;
		} catch (Exception e) {
			LOG.error("Error al recuperar proyectos asociados", e);
		}
		return lista;
	}
	
	@SuppressWarnings("unchecked")
	public List<ProyectoAsociadoDigitalizacion> buscarProyectoAsociado(int idproyectoAsociado){	
		List<ProyectoAsociadoDigitalizacion> lista = new ArrayList<ProyectoAsociadoDigitalizacion>();
		try {
			Query query = crudServiceBean.getEntityManager().createQuery("SELECT a from ProyectoAsociadoDigitalizacion a where a.estado = true and a.proyectoAsociadoId = :idAsociado ");
			query.setParameter("idAsociado", idproyectoAsociado);
			lista = (List<ProyectoAsociadoDigitalizacion>)query.getResultList();
			return lista;
		} catch (Exception e) {
			LOG.error("Error al recuperar proyectos asociados", e);
		}
		return lista;
	}
	
	@SuppressWarnings("unchecked")
	public List<ProyectoAsociadoDigitalizacion> buscarProyectoAsociadoPorCodigo(String codigo4Cat){	
		List<ProyectoAsociadoDigitalizacion> lista = new ArrayList<ProyectoAsociadoDigitalizacion>();
		try {
			Query query = crudServiceBean.getEntityManager().createQuery("SELECT a from ProyectoAsociadoDigitalizacion a where a.estado = true and a.codigo = :idAsociado ");
			query.setParameter("idAsociado", codigo4Cat);
			lista = (List<ProyectoAsociadoDigitalizacion>)query.getResultList();
			return lista;
		} catch (Exception e) {
			LOG.error("Error al recuperar proyectos asociados", e);
		}
		return lista;
	}

}
