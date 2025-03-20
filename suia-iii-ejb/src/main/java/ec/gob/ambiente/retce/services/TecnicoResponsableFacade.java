package ec.gob.ambiente.retce.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import org.apache.commons.lang.SerializationUtils;

import ec.gob.ambiente.retce.model.DatoObtenidoMedicionDescargas;
import ec.gob.ambiente.retce.model.SubstanciasRetce;
import ec.gob.ambiente.retce.model.TecnicoResponsable;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Usuario;

@Stateless
public class TecnicoResponsableFacade{	
	
	@EJB
	private CrudServiceBean crudServiceBean;	
	
	public TecnicoResponsable findById(Integer id){		
		try{
			TecnicoResponsable tecnicoResponsable = (TecnicoResponsable) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM TecnicoResponsable o where o.id = :id")
					.setParameter("id", id)					
					.getSingleResult();
			if(tecnicoResponsable!=null)
				tecnicoResponsable.setHistorialLista(findHistoricoByOriginal(tecnicoResponsable.getId()));
			
			return tecnicoResponsable;
			
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return null;
	}	

	public void save(TecnicoResponsable obj,Usuario usuario) {
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
	
	public TecnicoResponsable saveTecnico(TecnicoResponsable obj,Usuario usuario) {
		if(obj.getId()==null){
			obj.setUsuarioCreacion(usuario.getNombre());
			obj.setFechaCreacion(new Date());			
		}
		else{
			obj.setUsuarioModificacion(usuario.getNombre());
			obj.setFechaModificacion(new Date());			
		}
		crudServiceBean.saveOrUpdate(obj);
		return obj;
	}
	
	public void guardar(TecnicoResponsable obj, Usuario usuario,Integer numeroObservacion){
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
			TecnicoResponsable objetoOriginal=findById(obj.getId());
			boolean guardarHistorico=false;
			if(obj.getIdentificador().compareTo(objetoOriginal.getIdentificador())!=0
			 ||obj.getNombre().compareTo(objetoOriginal.getNombre())!=0
			 ||obj.getCorreo().compareTo(objetoOriginal.getCorreo())!=0
			 ||obj.getTelefono().compareTo(objetoOriginal.getTelefono())!=0
			 ||obj.getCelular().compareTo(objetoOriginal.getCelular())!=0
			 ||obj.getEstado()==false){
				guardarHistorico=true;
			}
			
			TecnicoResponsable objetoHistorico=findHistoricoByNumeroRevision(obj.getId(),numeroObservacion);			
			if(guardarHistorico && objetoHistorico==null){
				objetoHistorico=(TecnicoResponsable)SerializationUtils.clone(objetoOriginal);;				
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
	
	private TecnicoResponsable findHistoricoByNumeroRevision(Integer idRegistroOriginal,Integer numeroObservacion){
		try {
			TecnicoResponsable obj = (TecnicoResponsable) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM TecnicoResponsable o where o.idRegistroOriginal = :idRegistroOriginal and o.numeroObservacion = :numeroObservacion order by 1 desc")
					.setParameter("idRegistroOriginal", idRegistroOriginal)
					.setParameter("numeroObservacion", numeroObservacion)
					.setMaxResults(1)
					.getSingleResult();
			return obj;
		} catch (NoResultException e) {
			return null;
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}		
	}
	
	@SuppressWarnings("unchecked")
	private List<TecnicoResponsable> findHistoricoByOriginal(Integer idRegistroOriginal){
		List<TecnicoResponsable> list=new ArrayList<TecnicoResponsable>();
		try {
			list = (List<TecnicoResponsable>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM TecnicoResponsable o where o.idRegistroOriginal = :idRegistroOriginal order by 1 desc")
					.setParameter("idRegistroOriginal", idRegistroOriginal)
					.getResultList();			
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public TecnicoResponsable findByIdRegistroOriginal(Integer id, Integer numObservacion){		
		try{
			List<TecnicoResponsable> tecnicoResponsableList = (ArrayList<TecnicoResponsable>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM TecnicoResponsable o where o.idRegistroOriginal = :id "
							+ "and o.numeroObservacion = :numObservacion order by 1 DESC")
					.setParameter("id", id).setParameter("numObservacion", numObservacion)				
					.getResultList();
			
			if(tecnicoResponsableList != null && !tecnicoResponsableList.isEmpty()){
				return tecnicoResponsableList.get(0);
			}else{
				return null;
			}
			
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
}