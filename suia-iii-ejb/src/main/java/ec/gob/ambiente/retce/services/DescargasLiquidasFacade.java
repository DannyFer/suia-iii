package ec.gob.ambiente.retce.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import org.apache.commons.lang.SerializationUtils;

import ec.gob.ambiente.retce.model.DescargasLiquidas;
import ec.gob.ambiente.retce.model.EmisionesAtmosfericas;
import ec.gob.ambiente.retce.model.InformacionProyecto;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.utils.Constantes;

@Stateless
public class DescargasLiquidasFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	@EJB
	private SecuenciasFacade secuenciasFacade;
	
	private String generarCodigo() {
		try {
			return Constantes.SIGLAS_INSTITUCION + "-RETCE-DL-"
					+ secuenciasFacade.getCurrentYear()					
					+ "-"
					+ secuenciasFacade.getNextValueDedicateSequence("MAAE-RETCE-DL",4);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public DescargasLiquidas findById(Integer id){
		try {
			DescargasLiquidas descargaLiquida = (DescargasLiquidas) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM DescargasLiquidas o where o.id = :id")
					.setParameter("id", id).getSingleResult();
			if(descargaLiquida!=null)
				descargaLiquida.setHistorialLista(findHistoricoByOriginal(descargaLiquida.getId()));
			return descargaLiquida;
		}catch (NoResultException e) {
			return null;
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}		
	}
	
	public DescargasLiquidas findByCodigo(String codigo){
		try {
			DescargasLiquidas descargaLiquida = (DescargasLiquidas) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM DescargasLiquidas o where o.historial=false and o.codigo = :codigo")
					.setParameter("codigo", codigo).getSingleResult();			
			if(descargaLiquida!=null)
				descargaLiquida.setHistorialLista(findHistoricoByOriginal(descargaLiquida.getId()));
			return descargaLiquida;
			
		}catch (NoResultException e) {
			return null;
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}		
	}
	
	public void guardar(DescargasLiquidas obj, Usuario usuario,Integer numeroObservacion){
		obj.setFechaReporte(new Date());
		if(obj.getId()==null){
			obj.setUsuarioCreacion(usuario.getNombre());
			obj.setFechaCreacion(new Date());
			obj.setCodigo(generarCodigo());
			obj.setHistorial(false);
			obj.setEnviado(false);			
		}
		else{
			obj.setUsuarioModificacion(usuario.getNombre());
			obj.setFechaModificacion(new Date());			
		}
		
		if(obj.getId()!=null && numeroObservacion !=null && numeroObservacion >0 && numeroObservacion.compareTo(obj.getNumeroObservacion()==null?-1:obj.getNumeroObservacion())!=0){
			DescargasLiquidas objetoOriginal=findById(obj.getId());
			boolean guardarHistorico=false;
			if(/*obj.getFechaInicioMonitoreo().compareTo(objetoOriginal.getFechaInicioMonitoreo())!=0
			 ||obj.getFechaFinMonitoreo().compareTo(objetoOriginal.getFechaFinMonitoreo())!=0*/			 
			 //||obj.getCatalogoFrecuenciaMonitoreo().getId().compareTo(objetoOriginal.getCatalogoFrecuenciaMonitoreo().getId())!=0			 
			 obj.getEstado()==false){
				guardarHistorico=true;
			}
			
			DescargasLiquidas objetoHistorico=findHistoricoByNumeroRevision(obj.getId(),numeroObservacion);			
			if(guardarHistorico && objetoHistorico==null){
				objetoHistorico=(DescargasLiquidas)SerializationUtils.clone(objetoOriginal);;				
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
	public List<DescargasLiquidas> findByInformacionProyecto(InformacionProyecto informacionProyecto){
		List<DescargasLiquidas> lista = new ArrayList<DescargasLiquidas>();
		try {
			lista = (ArrayList<DescargasLiquidas>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM DescargasLiquidas o where o.estado = true and o.historial=false and o.informacionProyecto.id = :id order by 1 desc")
					.setParameter("id", informacionProyecto.getId())
					.getResultList();			
		}catch (NoResultException e) {
			return lista;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		for (DescargasLiquidas obj : lista) {				
			obj.setHistorialLista(findHistoricoByOriginal(obj.getId()));
		}
		return lista;
	}
	
	private DescargasLiquidas findHistoricoByNumeroRevision(Integer idRegistroOriginal,Integer numeroObservacion){
		try {
			DescargasLiquidas obj = (DescargasLiquidas) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM DescargasLiquidas o where o.idRegistroOriginal = :idRegistroOriginal and o.numeroObservacion = :numeroObservacion order by 1 desc")
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
	private List<DescargasLiquidas> findHistoricoByOriginal(Integer idRegistroOriginal){
		List<DescargasLiquidas> list=new ArrayList<DescargasLiquidas>();
		try {
			list = (List<DescargasLiquidas>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM DescargasLiquidas o where o.idRegistroOriginal = :idRegistroOriginal order by 1 desc")
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
