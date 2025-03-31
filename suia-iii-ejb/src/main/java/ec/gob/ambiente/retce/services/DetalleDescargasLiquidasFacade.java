package ec.gob.ambiente.retce.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import org.apache.commons.lang.SerializationUtils;

import ec.gob.ambiente.retce.model.DescargasLiquidas;
import ec.gob.ambiente.retce.model.DetalleDescargasLiquidas;
import ec.gob.ambiente.retce.model.DetalleEmisionesAtmosfericas;
import ec.gob.ambiente.retce.model.EmisionesAtmosfericas;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Usuario;

@Stateless
public class DetalleDescargasLiquidasFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	public DetalleDescargasLiquidas findById(Integer id){
		try {
			DetalleDescargasLiquidas detalleDescargasLiquidas = (DetalleDescargasLiquidas) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM DetalleDescargasLiquidas o where o.id = :id")
					.setParameter("id", id).getSingleResult();
			return detalleDescargasLiquidas;
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void save(DetalleDescargasLiquidas obj, Usuario usuario){
		/*if(!obj.getCatalogoTipoCuerpoReceptor().getDescripcion().contains("Otro"))
			obj.setOtroTipoCuerpoReceptor(null);*/
		if(!obj.getCatalogoTratamientoAguas().getDescripcion().contains("Otro"))
			obj.setOtroTratamientoAgua(null);		
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
	
	public void guardar(DetalleDescargasLiquidas obj, Usuario usuario,Integer numeroObservacion){
		/*if(!obj.getCatalogoTipoCuerpoReceptor().getDescripcion().contains("Otro"))
			obj.setOtroTipoCuerpoReceptor(null); */
		if(obj.getCatalogoTratamientoAguas() != null && !obj.getCatalogoTratamientoAguas().getDescripcion().contains("Otro"))
			obj.setOtroTratamientoAgua(null);		
		if(obj.getId()==null){
			obj.setUsuarioCreacion(usuario.getNombre());
			obj.setFechaCreacion(new Date());
			obj.setHistorial(false);
		}
		else{
			obj.setUsuarioModificacion(usuario.getNombre());
			obj.setFechaModificacion(new Date());			
		}
		
		if(obj.getId()!=null && numeroObservacion !=null && numeroObservacion >0 && numeroObservacion.compareTo(obj.getNumeroObservacion()==null?-1:obj.getNumeroObservacion())!=0){
			DetalleDescargasLiquidas objetoOriginal=findById(obj.getId());
			boolean guardarHistorico=false;
			if(obj.getNumeroPuntoMonitoreo().compareTo(objetoOriginal.getNumeroPuntoMonitoreo())!=0
			 ||obj.getCodigoPuntoMonitoreo().compareTo(objetoOriginal.getCodigoPuntoMonitoreo())!=0
			 ||obj.getLugarPuntoMonitoreo().compareTo(objetoOriginal.getLugarPuntoMonitoreo())!=0
			 ||obj.getFechaInicioMonitoreo().compareTo(objetoOriginal.getFechaInicioMonitoreo())!=0
			 ||obj.getFechaFinMonitoreo().compareTo(objetoOriginal.getFechaFinMonitoreo())!=0
			 ||obj.getCoordenadaX().compareTo(objetoOriginal.getCoordenadaX())!=0
			 ||obj.getCoordenadaY().compareTo(objetoOriginal.getCoordenadaY())!=0
			 ||obj.getCaudalMedido().compareTo(objetoOriginal.getCaudalMedido())!=0
			 ||obj.getVolumenDescarga().compareTo(objetoOriginal.getVolumenDescarga())!=0
			 ||obj.getHorasDescargaDia().compareTo(objetoOriginal.getHorasDescargaDia())!=0
			 
			 //||obj.getTipoCuerpoReceptor().compareTo(objetoOriginal.getTipoCuerpoReceptor())!=0
			 ||obj.getCatalogoTipoDescarga().getId().compareTo(objetoOriginal.getCatalogoTipoDescarga().getId())!=0
			 ||obj.getCatalogoFrecuenciaMonitoreo().getId().compareTo(objetoOriginal.getCatalogoFrecuenciaMonitoreo().getId())!=0
			 ||obj.getCatalogoTratamientoAguas().getId().compareTo(objetoOriginal.getCatalogoTratamientoAguas().getId())!=0
			 ||obj.getTipoMuestra().getId().compareTo(objetoOriginal.getTipoMuestra().getId())!=0
			 ||obj.getCaracteristicasPuntoMonitoreo().getId().compareTo(objetoOriginal.getCaracteristicasPuntoMonitoreo().getId())!=0			 
			 
			 ||obj.getEstado()==false){
				guardarHistorico=true;
			}
			
			DetalleDescargasLiquidas objetoHistorico=findHistoricoByNumeroRevision(obj.getId(),numeroObservacion);			
			if(guardarHistorico && objetoHistorico==null){
				objetoHistorico=(DetalleDescargasLiquidas)SerializationUtils.clone(objetoOriginal);;				
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
	public List<DetalleDescargasLiquidas> findByDescargaLiquida(DescargasLiquidas descargasLiquidas){
		List<DetalleDescargasLiquidas> lista = new ArrayList<DetalleDescargasLiquidas>();
		try {
			/*lista = (ArrayList<DetalleDescargasLiquidas>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM DetalleDescargasLiquidas o where o.estado = true and o.historial=false and o.descargasLiquidas.id = :id order by o.codigoPuntoMonitoreo")
					.setParameter("id", descargasLiquidas.getId())
					.getResultList();*/
			lista = (ArrayList<DetalleDescargasLiquidas>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM DetalleDescargasLiquidas o where o.estado = true and o.historial=false  and "
							+ "o.descargasLiquidas.id = :id and o.idRegistroOriginal = null order by 1 desc")
					.setParameter("id", descargasLiquidas.getId()).getResultList();
		}catch (NoResultException e) {
			return lista;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		for (DetalleDescargasLiquidas obj : lista) {				
			obj.setHistorialLista(findHistoricoByOriginal(obj.getId()));
		}
		return lista;		
	}
	
	private DetalleDescargasLiquidas findHistoricoByNumeroRevision(Integer idRegistroOriginal,Integer numeroObservacion){
		try {
			DetalleDescargasLiquidas obj = (DetalleDescargasLiquidas) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM DetalleDescargasLiquidas o where o.idRegistroOriginal = :idRegistroOriginal and o.numeroObservacion = :numeroObservacion order by 1 desc")
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
	private List<DetalleDescargasLiquidas> findHistoricoByOriginal(Integer idRegistroOriginal){
		List<DetalleDescargasLiquidas> list=new ArrayList<DetalleDescargasLiquidas>();
		try {
			list = (List<DetalleDescargasLiquidas>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM DetalleDescargasLiquidas o where o.idRegistroOriginal = :idRegistroOriginal order by 1 desc")
					.setParameter("idRegistroOriginal", idRegistroOriginal)
					.getResultList();			
			return list;
		} catch (NoResultException e) {
			return list;
		}catch (Exception e) {
			e.printStackTrace();
			return list;
		}
	}
}
