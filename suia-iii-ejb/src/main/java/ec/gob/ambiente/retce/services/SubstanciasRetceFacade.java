package ec.gob.ambiente.retce.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import org.apache.commons.lang.SerializationUtils;

import ec.gob.ambiente.retce.model.DatosLaboratorio;
import ec.gob.ambiente.retce.model.DetalleDescargasLiquidas;
import ec.gob.ambiente.retce.model.DetalleEmisionesAtmosfericas;
import ec.gob.ambiente.retce.model.SubstanciasRetce;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Usuario;

@Stateless
public class SubstanciasRetceFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	public SubstanciasRetce findById(Integer id){
		try {
			SubstanciasRetce sustancia = (SubstanciasRetce) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM SubstanciasRetce o where o.id = :id")
					.setParameter("id", id).getSingleResult();
			return sustancia;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void save(SubstanciasRetce obj, Usuario usuario){
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
	
	public void guardar(SubstanciasRetce obj, Usuario usuario,Integer numeroObservacion){
		if(obj.getId()==null){
			obj.setUsuarioCreacion(usuario.getNombre());
			obj.setFechaCreacion(new Date());
			obj.setHistorial(false);
			obj.setNumeroObservacion(numeroObservacion>0?numeroObservacion:null);
		}
		else{
			obj.setUsuarioModificacion(usuario.getNombre());
			obj.setFechaModificacion(new Date());			
		}
		
		if(obj.getId()!=null && numeroObservacion !=null && numeroObservacion >0 && numeroObservacion.compareTo(obj.getNumeroObservacion()==null?-1:obj.getNumeroObservacion())!=0){
			SubstanciasRetce objetoOriginal=findById(obj.getId());
			boolean guardarHistorico=false;
			if(obj.getCatologSustanciasRetce().getId().compareTo(objetoOriginal.getCatologSustanciasRetce().getId())!=0
			 ||obj.getCatalogoMetodoEstimacion().getId().compareTo(objetoOriginal.getCatalogoMetodoEstimacion().getId())!=0
			 ||obj.getReporteToneladaAnio().compareTo(objetoOriginal.getReporteToneladaAnio())!=0			 		 
			 ||obj.getEstado()==false){
				guardarHistorico=true;
			}
			
			SubstanciasRetce objetoHistorico=findHistoricoByNumeroRevision(obj.getId(),numeroObservacion);			
			if(guardarHistorico && objetoHistorico==null){
				objetoHistorico=(SubstanciasRetce)SerializationUtils.clone(objetoOriginal);;				
				objetoHistorico.setId(null);
				objetoHistorico.setIdRegistroOriginal(objetoOriginal.getId());
				objetoHistorico.setHistorial(true);
				objetoHistorico.setEstado(true);				
				objetoHistorico.setNumeroObservacion(numeroObservacion);
				crudServiceBean.saveOrUpdate(objetoHistorico);
			}
		}		
		crudServiceBean.saveOrUpdate(obj);
	}
	
	private SubstanciasRetce findHistoricoByNumeroRevision(Integer idRegistroOriginal,Integer numeroObservacion){
		try {
			SubstanciasRetce substanciasRetce = (SubstanciasRetce) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM SubstanciasRetce o where o.idRegistroOriginal = :idRegistroOriginal and o.numeroObservacion = :numeroObservacion order by 1 desc")
					.setParameter("idRegistroOriginal", idRegistroOriginal)
					.setParameter("numeroObservacion", numeroObservacion)
					.setMaxResults(1)
					.getSingleResult();
			return substanciasRetce;
		} catch (NoResultException e) {
			return null;
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}		
	}
	
	@SuppressWarnings("unchecked")
	public List<SubstanciasRetce> findByLaboratory(DatosLaboratorio laboratorio){
		List<SubstanciasRetce> sustancias = new ArrayList<SubstanciasRetce>();
		try {
			sustancias = (ArrayList<SubstanciasRetce>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM SubstanciasRetce o where o.estado = true and o.datosLaboratorio.id = :id")
					.setParameter("id", laboratorio.getId()).getResultList();
			return sustancias;
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return sustancias;
	}
	
	@SuppressWarnings("unchecked")
	public List<SubstanciasRetce> findByDetalleEmision(DetalleEmisionesAtmosfericas detalleEmision){
		List<SubstanciasRetce> sustancias = new ArrayList<SubstanciasRetce>();
		try {
			sustancias = (ArrayList<SubstanciasRetce>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM SubstanciasRetce o where o.estado = true and o.detalleEmisionesAtmosfericas.id = :id and o.idRegistroOriginal = null")
					.setParameter("id", detalleEmision.getId()).getResultList();
			
			for (SubstanciasRetce sustancia : sustancias) {				
				sustancia.setHistorialLista(findHistoricoByOriginal(sustancia.getId()));
			}
			
			return sustancias;
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return sustancias;
	}
	
	@SuppressWarnings("unchecked")
	public List<SubstanciasRetce> findByDetalleDescarga(DetalleDescargasLiquidas detalleDescarga){
		List<SubstanciasRetce> sustancias = new ArrayList<SubstanciasRetce>();
		try {
			sustancias = (ArrayList<SubstanciasRetce>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM SubstanciasRetce o where o.estado = true and o.historial=false and o.detalleDescargasLiquidas.id = :id")
					.setParameter("id", detalleDescarga.getId()).getResultList();			
		}catch (NoResultException e) {
			return sustancias;
		}catch (Exception e) {
			e.printStackTrace();
			return sustancias;
		}
		
		for (SubstanciasRetce sustancia : sustancias) {				
			sustancia.setHistorialLista(findHistoricoByOriginal(sustancia.getId()));
		}		
		return sustancias;
	}
	
	@SuppressWarnings("unchecked")
	public List<SubstanciasRetce> findByDetalleDescargaHistoricoEliminados(DetalleDescargasLiquidas detalleDescarga){
		List<SubstanciasRetce> sustancias = new ArrayList<SubstanciasRetce>();
		try {
			sustancias = (ArrayList<SubstanciasRetce>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM SubstanciasRetce o where o.estado = false and o.historial=false and o.detalleDescargasLiquidas.id = :id")
					.setParameter("id", detalleDescarga.getId()).getResultList();			
		}catch (NoResultException e) {
			return sustancias;
		}catch (Exception e) {
			e.printStackTrace();
			return sustancias;
		}		
		return sustancias;
	}
	
	@SuppressWarnings("unchecked")
	private List<SubstanciasRetce> findHistoricoByOriginal(Integer idRegistroOriginal){
		List<SubstanciasRetce> list=new ArrayList<SubstanciasRetce>();
		try {
			list = (List<SubstanciasRetce>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM SubstanciasRetce o where o.idRegistroOriginal = :idRegistroOriginal order by 1 desc")
					.setParameter("idRegistroOriginal", idRegistroOriginal)
					.getResultList();			
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public void guardarSustancias(List<SubstanciasRetce> sustancias) {
		crudServiceBean.saveOrUpdate(sustancias);
	}
	
	@SuppressWarnings("unchecked")
	public SubstanciasRetce getSustanciaHistorialPorID(Integer idSustancia, Integer nroObservacion) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idSustancia", idSustancia);
		parameters.put("nroObservacion", nroObservacion);
		try {
			List<SubstanciasRetce> lista = (List<SubstanciasRetce>) crudServiceBean.findByNamedQuery(SubstanciasRetce.GET_HISTORIAL_POR_ID_NRO_OBSERVACION,parameters);
			
			if (lista == null || lista.isEmpty()) {
				return null;
			} else {
				return lista.get(0);
			}
		} catch (Exception e) {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<SubstanciasRetce> getHistorialSustanciaPorID(Integer idSustancia) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idSustancia", idSustancia);
		try {
			List<SubstanciasRetce> lista = (List<SubstanciasRetce>) crudServiceBean.findByNamedQuery(SubstanciasRetce.GET_HISTORIAL_POR_ID, parameters);
			
			if (lista == null || lista.isEmpty()) {
				return null;
			} else {
				return lista;
			}
		} catch (Exception e) {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<SubstanciasRetce> getSustanciasEliminadasPorIdAutogestion(Integer idAutogestion) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idAutogestion", idAutogestion);
		try {
			List<SubstanciasRetce> lista = (List<SubstanciasRetce>) crudServiceBean.findByNamedQuery(SubstanciasRetce.GET_POR_ID_AUTOGESTION, parameters);
			
			if (lista == null || lista.isEmpty()) {
				return null;
			} else {
				return lista;
			}
		} catch (Exception e) {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<SubstanciasRetce> findByDetalleEmisionHistorial(DetalleEmisionesAtmosfericas detalleEmision){
		List<SubstanciasRetce> sustancias = new ArrayList<SubstanciasRetce>();
		try {
			sustancias = (ArrayList<SubstanciasRetce>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM SubstanciasRetce o where o.estado = true and o.detalleEmisionesAtmosfericas.id = :id and o.idRegistroOriginal != null")
					.setParameter("id", detalleEmision.getId()).getResultList();
			
			for (SubstanciasRetce sustancia : sustancias) {				
				sustancia.setHistorialLista(findHistoricoByOriginal(sustancia.getId()));
			}
			
			return sustancias;
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return sustancias;
	}

}
