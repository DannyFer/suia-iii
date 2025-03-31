package ec.gob.ambiente.retce.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.retce.model.EmisionesAtmosfericas;
import ec.gob.ambiente.retce.model.InformacionProyecto;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.utils.Constantes;

@Stateless
public class EmisionesAtmosfericasFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;
	@EJB
	private SecuenciasFacade secuenciasFacade;
	
	public EmisionesAtmosfericas findById(Integer id){
		try {
			EmisionesAtmosfericas emisionesAtmosfericas = (EmisionesAtmosfericas) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM EmisionesAtmosfericas o where o.id = :id")
					.setParameter("id", id).getSingleResult();
			return emisionesAtmosfericas;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void save(EmisionesAtmosfericas obj, Usuario usuario){
		if(obj.getId()==null){
			obj.setUsuarioCreacion(usuario.getNombre());
			obj.setFechaCreacion(new Date());
			if(obj.getCodigo() == null || obj.getCodigo().isEmpty()){
				obj.setCodigo(generarCodigo());
			}
		}
		else{
			obj.setUsuarioModificacion(usuario.getNombre());
			obj.setFechaModificacion(new Date());			
		}
		crudServiceBean.saveOrUpdate(obj);
	}
	
	@SuppressWarnings("unchecked")
	public List<EmisionesAtmosfericas> findByInformacionProyecto(InformacionProyecto informacion){
		List<EmisionesAtmosfericas> lista = new ArrayList<EmisionesAtmosfericas>();
		try {
			lista = (ArrayList<EmisionesAtmosfericas>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM EmisionesAtmosfericas o where o.estado = true and o.informacionProyecto.id = :id "
							+ "and o.idRegistroOriginal = null order by 1 desc")
					.setParameter("id", informacion.getId()).getResultList();
			return lista;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;
	}	
	
	private String generarCodigo() {
		try {
			return Constantes.SIGLAS_INSTITUCION + "-RETCE-EA"
					+ "-"
					+ secuenciasFacade.getCurrentYear()					
					+ "-"
					+ secuenciasFacade.getNextValueDedicateSequence("MAAE-RETCE-EA",4);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
		
	public EmisionesAtmosfericas findByCodigo(String codigoEmision){
		try {
			EmisionesAtmosfericas emisionesAtmosfericas = (EmisionesAtmosfericas) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM EmisionesAtmosfericas o where o.estado = true and o.historial is null and o.codigo = :codigoEmision")
					.setParameter("codigoEmision", codigoEmision).getSingleResult();
			return emisionesAtmosfericas;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}	
	
	@SuppressWarnings("unchecked")
	public List<EmisionesAtmosfericas> findByEmision(String codigoEmision){
		List<EmisionesAtmosfericas> lista = new ArrayList<EmisionesAtmosfericas>();
		try {
			lista = (ArrayList<EmisionesAtmosfericas>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM EmisionesAtmosfericas o where o.estado = true and o.codigo = :codigoEmision and o.idRegistroOriginal = null")
					.setParameter("codigoEmision", codigoEmision).getResultList();
			return lista;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;
	}	
	
	@SuppressWarnings("unchecked")
	public EmisionesAtmosfericas findByIdOriginalObservacion(Integer id, Integer numeroOb){
		try {
			
			List<EmisionesAtmosfericas> emisionesAtmosfericas = (List<EmisionesAtmosfericas>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM EmisionesAtmosfericas o where o.estado = true and o.idRegistroOriginal = :id and o.numeroObservacion = :numeroOb")
					.setParameter("id", id).setParameter("numeroOb", numeroOb).getResultList();
			
			if(emisionesAtmosfericas != null && !emisionesAtmosfericas.isEmpty()){
				return emisionesAtmosfericas.get(0);
			}	
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<EmisionesAtmosfericas> findHistoricos(Integer id){
		List<EmisionesAtmosfericas> lista = new ArrayList<EmisionesAtmosfericas>();
		try {
			lista = (ArrayList<EmisionesAtmosfericas>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM EmisionesAtmosfericas o where o.estado = true and o.idRegistroOriginal = :id")
					.setParameter("id", id).getResultList();
			return lista;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;
	}	

}
