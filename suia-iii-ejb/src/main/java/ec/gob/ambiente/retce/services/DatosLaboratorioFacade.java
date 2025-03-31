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
import ec.gob.ambiente.retce.model.DescargasLiquidas;
import ec.gob.ambiente.retce.model.DetalleDescargasLiquidas;
import ec.gob.ambiente.retce.model.DetalleEmisionesAtmosfericas;
import ec.gob.ambiente.retce.model.EmisionesAtmosfericas;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Usuario;

@Stateless
public class DatosLaboratorioFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	public DatosLaboratorio findById(Integer id){
		try {
			DatosLaboratorio datosLaboratorio = (DatosLaboratorio) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM DatosLaboratorio o where o.id = :id")
					.setParameter("id", id).getSingleResult();
			return datosLaboratorio;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void save(DatosLaboratorio obj, Usuario usuario){
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
	
	public void guardar(DatosLaboratorio obj, Usuario usuario,Integer numeroObservacion){
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
			DatosLaboratorio objetoOriginal=findById(obj.getId());
			boolean guardarHistorico=false;
			if(obj.getRuc().compareTo(objetoOriginal.getRuc())!=0
			 ||obj.getNombre().compareTo(objetoOriginal.getNombre())!=0
			 ||obj.getNumeroRegistroSAE().compareTo(objetoOriginal.getNumeroRegistroSAE())!=0
			 ||obj.getFechaVigenciaRegistro().compareTo(objetoOriginal.getFechaVigenciaRegistro())!=0			 
			 ||obj.getEstado()==false){
				guardarHistorico=true;
			}
			
			DatosLaboratorio objetoHistorico=findHistoricoByNumeroRevision(obj.getId(),numeroObservacion);			
			if(guardarHistorico && objetoHistorico==null){
				objetoHistorico=(DatosLaboratorio)SerializationUtils.clone(objetoOriginal);;				
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
	
	private DatosLaboratorio findHistoricoByNumeroRevision(Integer idRegistroOriginal,Integer numeroObservacion){
		try {
			DatosLaboratorio datoObtenidoDescarga = (DatosLaboratorio) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM DatosLaboratorio o where o.idRegistroOriginal = :idRegistroOriginal and o.numeroObservacion = :numeroObservacion order by 1 desc")
					.setParameter("idRegistroOriginal", idRegistroOriginal)
					.setParameter("numeroObservacion", numeroObservacion)
					.setMaxResults(1)
					.getSingleResult();
			return datoObtenidoDescarga;
		} catch (NoResultException e) {
			return null;
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}		
	}
	
	@SuppressWarnings("unchecked")
	private List<DatosLaboratorio> findHistoricoByOriginal(Integer idRegistroOriginal){
		List<DatosLaboratorio> list=new ArrayList<DatosLaboratorio>();
		try {
			list = (List<DatosLaboratorio>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM DatosLaboratorio o where o.idRegistroOriginal = :idRegistroOriginal order by 1 desc")
					.setParameter("idRegistroOriginal", idRegistroOriginal)
					.getResultList();			
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public List<DatosLaboratorio> findAll(){
		List<DatosLaboratorio> datoLaboratorioList = new ArrayList<DatosLaboratorio>();
		try {
			datoLaboratorioList = (ArrayList<DatosLaboratorio>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM DatosLaboratorio o where o.estado = true")
					.getResultList();
			return datoLaboratorioList;
			
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return datoLaboratorioList;
	}
	
	@SuppressWarnings("unchecked")
	public List<DatosLaboratorio> findByDetalleEmisiones(EmisionesAtmosfericas detalleEmision){
		List<DatosLaboratorio> datoLaboratorioList = new ArrayList<DatosLaboratorio>();
		try {
			datoLaboratorioList = (ArrayList<DatosLaboratorio>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM DatosLaboratorio o where o.estado = true and o.emisionesAtmosfericas.id = :id and o.idRegistroOriginal= null")
					.setParameter("id", detalleEmision.getId()).getResultList();
			
			for (DatosLaboratorio dato : datoLaboratorioList) {				
				dato.setHistorialLista(findHistoricoByOriginal(dato.getId()));
			}
			
			return datoLaboratorioList;
			
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return datoLaboratorioList;
	}
	
	@SuppressWarnings("unchecked")
	public List<DatosLaboratorio> findByDescarga(DescargasLiquidas descargasLiquidas){
		List<DatosLaboratorio> datoLaboratorioList = new ArrayList<DatosLaboratorio>();
		try {
			datoLaboratorioList = (ArrayList<DatosLaboratorio>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM DatosLaboratorio o where o.estado = true and o.historial=false and o.descargasLiquidas.id = :id")
					.setParameter("id", descargasLiquidas.getId())
					.getResultList();
		}catch (NoResultException e) {
			return datoLaboratorioList;
		}catch (Exception e) {
			e.printStackTrace();
			return datoLaboratorioList;
		}
		for (DatosLaboratorio dato : datoLaboratorioList) {				
			dato.setHistorialLista(findHistoricoByOriginal(dato.getId()));
		}
		return datoLaboratorioList;
	}
	
	@SuppressWarnings("unchecked")
	public List<DatosLaboratorio> findByDescargaHistoricoEliminados(DescargasLiquidas descargasLiquidas){
		List<DatosLaboratorio> datoLaboratorioList = new ArrayList<DatosLaboratorio>();
		try {
			datoLaboratorioList = (ArrayList<DatosLaboratorio>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM DatosLaboratorio o where o.estado = false and o.historial=false and o.descargasLiquidas.id = :id")
					.setParameter("id", descargasLiquidas.getId())
					.getResultList();
		}catch (NoResultException e) {
			return datoLaboratorioList;
		}catch (Exception e) {
			e.printStackTrace();
			return datoLaboratorioList;
		}
		for (DatosLaboratorio dato : datoLaboratorioList) {				
			dato.setHistorialLista(findHistoricoByOriginal(dato.getId()));
		}
		return datoLaboratorioList;
	}
	
	public DatosLaboratorio saveLaboratorioEmisiones(DatosLaboratorio obj, Usuario usuario){
		if(obj.getId()==null){
			obj.setUsuarioCreacion(usuario.getNombre());
			obj.setFechaCreacion(new Date());			
		}
		else{
			obj.setUsuarioModificacion(usuario.getNombre());
			obj.setFechaModificacion(new Date());			
		}
		DatosLaboratorio dato = crudServiceBean.saveOrUpdate(obj);
		return dato;
	}
	
	public void guardarLaboratorios(List<DatosLaboratorio> laboratorios) {
		crudServiceBean.saveOrUpdate(laboratorios);
	}
	
	@SuppressWarnings("unchecked")
	public DatosLaboratorio getLaboratorioHistorialPorID(Integer idLaboratorio, Integer nroObservacion) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idLaboratorio", idLaboratorio);
		parameters.put("nroObservacion", nroObservacion);
		try {
			List<DatosLaboratorio> lista = (List<DatosLaboratorio>) crudServiceBean.findByNamedQuery(DatosLaboratorio.GET_HISTORIAL_POR_ID_NRO_OBSERVACION,parameters);
			
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
	public List<DatosLaboratorio> getHistorialLaboratorioPorID(Integer idLaboratorio) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idLaboratorio", idLaboratorio);
		try {
			List<DatosLaboratorio> lista = (List<DatosLaboratorio>) crudServiceBean.findByNamedQuery(DatosLaboratorio.GET_HISTORIAL_POR_ID,parameters);
			
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
	public List<DatosLaboratorio> findByEmisionHistorial(EmisionesAtmosfericas emisionAtmosferica){
		List<DatosLaboratorio> datoLaboratorioList = new ArrayList<DatosLaboratorio>();
		try {
			datoLaboratorioList = (ArrayList<DatosLaboratorio>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM DatosLaboratorio o where o.estado = true and o.emisionesAtmosfericas.id = :id and o.idRegistroOriginal != null")
					.setParameter("id", emisionAtmosferica.getId()).getResultList();			
			return datoLaboratorioList;
		
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return datoLaboratorioList;
	}
	
}
