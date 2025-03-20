package ec.gob.ambiente.retce.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import org.apache.commons.lang.SerializationUtils;

import ec.gob.ambiente.retce.model.DatoObtenidoMedicionDescargas;
import ec.gob.ambiente.retce.model.DetalleDescargasLiquidas;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Usuario;

@Stateless
public class DatoObtenidoMedicionDescargasFacade {

	@EJB
	private CrudServiceBean crudServiceBean;
	
	public DatoObtenidoMedicionDescargas findById(Integer id){
		try {
			DatoObtenidoMedicionDescargas datoObtenidoDescarga = (DatoObtenidoMedicionDescargas) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM DatoObtenidoMedicionDescargas o where o.id = :id")
					.setParameter("id", id).getSingleResult();
			if(datoObtenidoDescarga!=null){
				datoObtenidoDescarga.setHistorialLista(findHistoricoByOriginal(datoObtenidoDescarga.getId()));
			}				
			return datoObtenidoDescarga;
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return null;
	}
		
	@SuppressWarnings("unchecked")
	private List<DatoObtenidoMedicionDescargas> findHistoricoByOriginal(Integer idRegistroOriginal){
		List<DatoObtenidoMedicionDescargas> list=new ArrayList<DatoObtenidoMedicionDescargas>();
		try {
			list = (List<DatoObtenidoMedicionDescargas>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM DatoObtenidoMedicionDescargas o where o.idRegistroOriginal = :idRegistroOriginal order by 1 desc")
					.setParameter("idRegistroOriginal", idRegistroOriginal)
					.getResultList();			
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	private DatoObtenidoMedicionDescargas findHistoricoByNumeroRevision(Integer idRegistroOriginal,Integer numeroObservacion){
		try {
			DatoObtenidoMedicionDescargas datoObtenidoDescarga = (DatoObtenidoMedicionDescargas) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM DatoObtenidoMedicionDescargas o where o.idRegistroOriginal = :idRegistroOriginal and o.numeroObservacion = :numeroObservacion order by 1 desc")
					.setParameter("idRegistroOriginal", idRegistroOriginal)
					.setParameter("numeroObservacion", numeroObservacion)
					.setMaxResults(1)
					.getSingleResult();
			return datoObtenidoDescarga;
		}catch (NoResultException e) {
			return null;
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}		
	}
	
	@Deprecated
	public void save(DatoObtenidoMedicionDescargas obj, Usuario usuario){
		if(obj.getId()==null){
			obj.setUsuarioCreacion(usuario.getNombre());
			obj.setFechaCreacion(new Date());
			obj.setHistorial(false);
		}
		else{
			obj.setUsuarioModificacion(usuario.getNombre());
			obj.setFechaModificacion(new Date());			
		}
		
		if(obj.getEstado()==null || obj.getEstado()==true){
			if(obj.getParametrosTablas().getLimiteMinimoPermisible()==null && obj.getParametrosTablas().getLimiteMaximoPermisible()==null){
				obj.setValorIngresadoCorrecto(null);
			}else{	
				boolean minimoCorrecto=obj.getParametrosTablas().getLimiteMinimoPermisible()!=null && obj.getParametrosTablas().getLimiteMinimoPermisible()>obj.getValorIngresado()?false:true;
				boolean maximoCorrecto=obj.getParametrosTablas().getLimiteMaximoPermisible()!=null && obj.getParametrosTablas().getLimiteMaximoPermisible()<obj.getValorIngresado()?false:true;
				obj.setValorIngresadoCorrecto(minimoCorrecto && maximoCorrecto);
			}
		}
		
		if(obj.getId()!=null){
			DatoObtenidoMedicionDescargas objetoOriginal=findById(obj.getId());
			boolean guardarHistorico=false;
			if(obj.getValorIngresado().compareTo(objetoOriginal.getValorIngresado())!=0
			 ||obj.getParametrosTablas().getId().compareTo(objetoOriginal.getParametrosTablas().getId())!=0
			 ||obj.getEstado()==false){
				guardarHistorico=true;
			}
			
			if(guardarHistorico){
				DatoObtenidoMedicionDescargas objetoHistorico=(DatoObtenidoMedicionDescargas)SerializationUtils.clone(objetoOriginal);;				
				objetoHistorico.setId(null);
				objetoHistorico.setIdRegistroOriginal(objetoOriginal.getId());
				objetoHistorico.setHistorial(true);
				objetoHistorico.setEstado(true);				
				objetoHistorico.setNumeroObservacion(1);
				crudServiceBean.saveOrUpdate(objetoHistorico);
			}
		}
			
		crudServiceBean.saveOrUpdate(obj);
	}
	
	public void guardar(DatoObtenidoMedicionDescargas obj, Usuario usuario,Integer numeroObservacion){
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
		
		if(obj.getEstado()==null || obj.getEstado()==true){
			if(obj.getParametrosTablas().getLimiteMinimoPermisible()==null && obj.getParametrosTablas().getLimiteMaximoPermisible()==null){
				obj.setValorIngresadoCorrecto(null);
			}else{	
				boolean minimoCorrecto=obj.getParametrosTablas().getLimiteMinimoPermisible()!=null && obj.getParametrosTablas().getLimiteMinimoPermisible()>obj.getValorIngresado()?false:true;
				boolean maximoCorrecto=obj.getParametrosTablas().getLimiteMaximoPermisible()!=null && obj.getParametrosTablas().getLimiteMaximoPermisible()<obj.getValorIngresado()?false:true;
				obj.setValorIngresadoCorrecto(minimoCorrecto && maximoCorrecto);
			}
		}
		
		if(obj.getId()!=null && numeroObservacion !=null && numeroObservacion >0 && numeroObservacion.compareTo(obj.getNumeroObservacion()==null?-1:obj.getNumeroObservacion())!=0){
			DatoObtenidoMedicionDescargas objetoOriginal=findById(obj.getId());
			boolean guardarHistorico=false;
			if(obj.getValorIngresado().compareTo(objetoOriginal.getValorIngresado())!=0
			 ||obj.getParametrosTablas().getId().compareTo(objetoOriginal.getParametrosTablas().getId())!=0
			 ||obj.getEstado()==false){
				guardarHistorico=true;
			}
			
			DatoObtenidoMedicionDescargas objetoHistorico=findHistoricoByNumeroRevision(obj.getId(),numeroObservacion);			
			if(guardarHistorico && objetoHistorico==null){
				objetoHistorico=(DatoObtenidoMedicionDescargas)SerializationUtils.clone(objetoOriginal);;				
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
	
	@SuppressWarnings("unchecked")
	public List<DatoObtenidoMedicionDescargas> findAll(){
		List<DatoObtenidoMedicionDescargas> datoMedicionDescargaList = new ArrayList<DatoObtenidoMedicionDescargas>();
		try {
			datoMedicionDescargaList = (ArrayList<DatoObtenidoMedicionDescargas>) crudServiceBean.getEntityManager().createQuery("SELECT o FROM DatoObtenidoMedicionDescargas o where o.estado = true").getResultList();
			return datoMedicionDescargaList;
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return datoMedicionDescargaList;
	}
	
	@SuppressWarnings("unchecked")
	public List<DatoObtenidoMedicionDescargas> findByDescarga(DetalleDescargasLiquidas detalleDescarga){
		List<DatoObtenidoMedicionDescargas> datosMedicionDescargaList = new ArrayList<DatoObtenidoMedicionDescargas>();
		try {
			datosMedicionDescargaList = (ArrayList<DatoObtenidoMedicionDescargas>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM DatoObtenidoMedicionDescargas o where o.estado = true and o.historial=false and o.detalleDescargasLiquidas.id = :id")
					.setParameter("id", detalleDescarga.getId())
					.getResultList();
		}catch (NoResultException e) {
			return datosMedicionDescargaList;
		}catch (Exception e) {
			e.printStackTrace();
			return datosMedicionDescargaList;
		}
		for (DatoObtenidoMedicionDescargas dato : datosMedicionDescargaList) {				
			dato.setHistorialLista(findHistoricoByOriginal(dato.getId()));
		}
		return datosMedicionDescargaList;
	}
	
	@SuppressWarnings("unchecked")
	public List<DatoObtenidoMedicionDescargas> findByDescargaHistoricoEliminados(DetalleDescargasLiquidas detalleDescarga){
		List<DatoObtenidoMedicionDescargas> datosMedicionDescargaList = new ArrayList<DatoObtenidoMedicionDescargas>();
		try {
			datosMedicionDescargaList = (ArrayList<DatoObtenidoMedicionDescargas>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM DatoObtenidoMedicionDescargas o where o.estado = false and o.historial=false and o.detalleDescargasLiquidas.id = :id")
					.setParameter("id", detalleDescarga.getId())
					.getResultList();
		}catch (NoResultException e) {
			return datosMedicionDescargaList;
		}catch (Exception e) {
			e.printStackTrace();
			return datosMedicionDescargaList;
		}		
		return datosMedicionDescargaList;
	}
}
