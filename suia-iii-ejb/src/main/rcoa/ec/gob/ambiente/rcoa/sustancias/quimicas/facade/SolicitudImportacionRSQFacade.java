package ec.gob.ambiente.rcoa.sustancias.quimicas.facade;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import ec.gob.ambiente.rcoa.dto.SolicitudImportacionRSQVueDTO;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.SolicitudImportacionRSQ;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.SolicitudImportacionRSQExtVue;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.ConstantesEnum;

@Stateless
public class SolicitudImportacionRSQFacade {
	
	@EJB
    private CrudServiceBean crudServiceBean;
	
	@EJB
	private SecuenciasFacade secuenciasFacade;
	
	public void save(SolicitudImportacionRSQ obj, Usuario usuario){	
		try {
			if(obj.getId()==null){
				obj.setUsuarioCreacion(usuario.getNombre());
				obj.setFechaCreacion(new Date());	
				obj.setTramite(generarCodigo());
			}
			else{
				obj.setUsuarioModificacion(usuario.getNombre());
				obj.setFechaModificacion(new Date());			
			}
			crudServiceBean.saveOrUpdate(obj);	
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	private String generarCodigo() {
		try {
			return Constantes.SIGLAS_INSTITUCION + "-RSQ-IMP"
					+ "-"
					+ secuenciasFacade.getCurrentYear()					
					+ "-"
					+ secuenciasFacade.getNextValueDedicateSequence("MAAE-RSQ-IMP",4);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public SolicitudImportacionRSQ buscarPorTramite(String tramite){
		try {
			
			Query query = crudServiceBean.getEntityManager().createQuery("SELECT s from SolicitudImportacionRSQ s where s.tramite = :tramite and s.estado = true");
			query.setParameter("tramite", tramite);
			List<SolicitudImportacionRSQ> lista= (List<SolicitudImportacionRSQ>)query.getResultList();
			
			if(lista != null && !lista.isEmpty()){
				return lista.get(0);
			}else 
				return null;
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}	
	
	public SolicitudImportacionRSQ buscarPorTramiteVue(String tramite){
		try {
			
			Query query = crudServiceBean.getEntityManager().createQuery("SELECT s from SolicitudImportacionRSQ s where s.tramite = :tramite and s.estado = true");
			query.setParameter("tramite", tramite);
			List<SolicitudImportacionRSQ> lista= new ArrayList<>(); 
			lista = query.getResultList();
			
			if(lista != null && !lista.isEmpty()){
				return lista.get(0);
			}else 
				return null;
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}	
	
	@SuppressWarnings("unchecked")
	public List<SolicitudImportacionRSQ> listaPorRSQ(Integer id){
		try {
			
			Query query = crudServiceBean.getEntityManager().createQuery("SELECT s from SolicitudImportacionRSQ s where s.registroSustanciaQuimica.id = :id and s.estado = true and s.fechaFinAutorizacion != null and s.autorizacion = true order by 1 desc");
			query.setParameter("id", id);
			List<SolicitudImportacionRSQ> lista= (List<SolicitudImportacionRSQ>)query.getResultList();
			
			if(lista != null && !lista.isEmpty()){
				return lista;
			}else 
				return null;
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public SolicitudImportacionRSQ buscarPorId(Integer id){
		try {
			
			Query query = crudServiceBean.getEntityManager().createQuery("SELECT s from SolicitudImportacionRSQ s where s.id = :id and s.estado = true");
			query.setParameter("id", id);
			List<SolicitudImportacionRSQ> lista= (List<SolicitudImportacionRSQ>)query.getResultList();
			
			if(lista != null && !lista.isEmpty()){
				return lista.get(0);
			}else 
				return null;
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<SolicitudImportacionRSQ> listaSolicitudesAnuladasPorSolicitud(Integer id){
		try {
			
			Query query = crudServiceBean.getEntityManager().createQuery("SELECT s from SolicitudImportacionRSQ s where s.solicitudAnulada.id= :id and s.estado = true and s.anulacion = true order by 1 desc");
			query.setParameter("id", id);
			List<SolicitudImportacionRSQ> lista= (List<SolicitudImportacionRSQ>)query.getResultList();
			
			if(lista != null && !lista.isEmpty()){
				return lista;
			}else 
				return null;
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	@SuppressWarnings("unchecked")
	public List<SolicitudImportacionRSQ> listaSolicitudesAnuladasPorRSQ(Integer id, Integer idSustancia){
		try {
			
			Query query = crudServiceBean.getEntityManager().createQuery("SELECT s from SolicitudImportacionRSQ s where s.registroSustanciaQuimica.id= :id and s.estado = true and s.anulacion = true and s.sustanciaQuimicaPeligrosa.id = :idSustancia order by 1 desc");
			query.setParameter("id", id);
			query.setParameter("idSustancia", idSustancia);
			List<SolicitudImportacionRSQ> lista= (List<SolicitudImportacionRSQ>)query.getResultList();
			
			if(lista != null && !lista.isEmpty()){
				return lista;
			}else 
				return null;
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<SolicitudImportacionRSQ> listaSolicitudesAutorizadasPorRSQ(Integer id, Integer idSustancia){
		try {
			
			Query query = crudServiceBean.getEntityManager().createQuery("SELECT s from SolicitudImportacionRSQ s where s.registroSustanciaQuimica.id= :id and s.estado = true and s.autorizacion = true and s.sustanciaQuimicaPeligrosa.id = :idSustancia order by 1 desc");
			query.setParameter("id", id);
			query.setParameter("idSustancia", idSustancia);
			List<SolicitudImportacionRSQ> lista= (List<SolicitudImportacionRSQ>)query.getResultList();
			
			if(lista != null && !lista.isEmpty()){
				return lista;
			}else 
				return null;
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<SolicitudImportacionRSQ> listaSolicitudesTotalPorRSQ(Integer id, Integer idSustancia){
		try {
			
			Query query = crudServiceBean.getEntityManager().createQuery("SELECT s from SolicitudImportacionRSQ s where s.registroSustanciaQuimica.id= :id and s.estado = true and s.sustanciaQuimicaPeligrosa.id = :idSustancia order by 1 desc");
			query.setParameter("id", id);
			query.setParameter("idSustancia", idSustancia);
			List<SolicitudImportacionRSQ> lista= (List<SolicitudImportacionRSQ>)query.getResultList();
			
			if(lista != null && !lista.isEmpty()){
				return lista;
			}else 
				return null;
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Object[]> listaSolicitudesPorEstFecha(SolicitudImportacionRSQVueDTO pSolicitudImportacionRSQVueDTO){
		try {
			String unSQL = "SELECT s, sExt from SolicitudImportacionRSQ s, SolicitudImportacionRSQExtVue sExt"
				+ " where s.id = sExt.id and s.estado = true and s.idInstanciaProceso is not null";
			
			if (ConstantesEnum.SOLICITUD_AUTORIZACIONES_EMITIDAS.getCodigo().toString().equals(pSolicitudImportacionRSQVueDTO.getTipoSolicitud())) {
				unSQL = unSQL + " and sExt.estadoFormulario in ('320', '510')";
			}else if (ConstantesEnum.SOLICITUD_AUTORIZACIONES_ANULADAS.getCodigo().toString().equals(pSolicitudImportacionRSQVueDTO.getTipoSolicitud())) {
				unSQL = unSQL + " and sExt.estadoFormulario in ('650', '350')";
			}else if (ConstantesEnum.SOLICITUD_AUTORIZACIONES_PENDIENTES.getCodigo().toString().equals(pSolicitudImportacionRSQVueDTO.getTipoSolicitud())) {
				unSQL = unSQL + " and sExt.estadoFormulario in ('210', '150', '640')";
			}else if (ConstantesEnum.SOLICITUD_AUTORIZACIONES_CORREGIDAS.getCodigo().toString().equals(pSolicitudImportacionRSQVueDTO.getTipoSolicitud())) {
				unSQL = unSQL + " and sExt.estadoFormulario in ('340')";
			} 
			
			unSQL = unSQL + " and s.fechaCreacion >= :fInicio and s.fechaCreacion <= :fFin" 
				+ " order by 1 desc"; 
			
			Query query = crudServiceBean.getEntityManager().createQuery(unSQL);
			query.setParameter("fInicio", pSolicitudImportacionRSQVueDTO.getFechaInicio(), TemporalType.TIMESTAMP);
			query.setParameter("fFin", pSolicitudImportacionRSQVueDTO.getFechaFin(), TemporalType.TIMESTAMP);
			
			List<Object[]> lista = (List<Object[]>)query.getResultList();
			
			return lista;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<SolicitudImportacionRSQ> listaImportaciones(){
		try {
			
			Query query = crudServiceBean.getEntityManager().createQuery("SELECT s from SolicitudImportacionRSQ s "
					+ "where s.estado = true and s.fechaFinAutorizacion != null "
					+ "and s.autorizacion = true and s.anulacion = false order by 1 desc ");
			
			List<SolicitudImportacionRSQ> lista= (List<SolicitudImportacionRSQ>)query.getResultList();
			
			if(lista != null && !lista.isEmpty()){
				return lista;
			}else 
				return null;
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<SolicitudImportacionRSQ> obtenerImportaciones(String usuario, Integer idSustancia){
		List<SolicitudImportacionRSQ> lista = new ArrayList<SolicitudImportacionRSQ>();		
		try {			
			String queryString = "select inre_id, inre_authorization, inre_begin_authorization_date, inre_end_authorization_date, "
					+ "inre_status, inre_creation_date, inre_creator_user, inre_processing_code, inre_document_autorizes "
					+ "from coa_chemical_sustances.import_request ir "
					+ "where inre_id not in "
					+ "(select inre_parent_id from coa_chemical_sustances.import_request where inre_annulment = true and inre_parent_id is not null) "
					+ "and inre_authorization = true and inre_status = true and inre_annulment = false and inre_end_authorization_date is not null "
					+ "and inre_id not in "
					+ "(select inre_id from coa_chemical_sustances.chemical_substances_movements csm where inre_id is not null and csm.chsm_status = true) "
					+ "and dach_id = " + idSustancia + " and inre_creator_user = '" + usuario + "' "
					+ " order by 1;";
			
			List<Object> result = crudServiceBean.findByNativeQuery(queryString, null);
			if (result.size() > 0) {
				lista = new ArrayList<SolicitudImportacionRSQ>();
				for (Object object : result) {
					lista.add(new SolicitudImportacionRSQ((Object[]) object));					
				}
			}
			return lista;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;
	}


}
