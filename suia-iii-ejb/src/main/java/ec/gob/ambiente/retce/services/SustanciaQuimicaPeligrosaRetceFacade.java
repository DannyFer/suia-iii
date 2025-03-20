package ec.gob.ambiente.retce.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.retce.model.ReporteSustanciasQuimicasPeligrosas;
import ec.gob.ambiente.retce.model.SustanciaQuimicaPeligrosaDetalle;
import ec.gob.ambiente.retce.model.SustanciaQuimicaPeligrosaRetce;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Usuario;

@Stateless
public class SustanciaQuimicaPeligrosaRetceFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	public SustanciaQuimicaPeligrosaRetce findById(Integer id){
		try {
			SustanciaQuimicaPeligrosaRetce sustancia = (SustanciaQuimicaPeligrosaRetce) crudServiceBean
					.getEntityManager()
					.createQuery(
							"SELECT o FROM SustanciaQuimicaPeligrosaRetce o where o.id = :id")
					.setParameter("id", id).getSingleResult();
			return sustancia;			
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void save(SustanciaQuimicaPeligrosaRetce obj, Usuario usuario){
		
		List<SustanciaQuimicaPeligrosaDetalle> lista = obj.getDetalleList();
		
		if(obj.getId()==null){
			obj.setUsuarioCreacion(usuario.getNombre());
			obj.setFechaCreacion(new Date());			
		}
		else{
			obj.setUsuarioModificacion(usuario.getNombre());
			obj.setFechaModificacion(new Date());			
		}
		crudServiceBean.saveOrUpdate(obj);
				
		for(SustanciaQuimicaPeligrosaDetalle detalle : lista){
			detalle.setSustanciaQuimicaPeligrosa(obj);
			saveDetalle(detalle, usuario);
		}		
	}
	
	public void saveDetalle(SustanciaQuimicaPeligrosaDetalle obj, Usuario usuario){
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
	public List<SustanciaQuimicaPeligrosaRetce> findByReporte(ReporteSustanciasQuimicasPeligrosas reporte){
		List<SustanciaQuimicaPeligrosaRetce> lista = new ArrayList<SustanciaQuimicaPeligrosaRetce>();
		try {
			lista = (ArrayList<SustanciaQuimicaPeligrosaRetce>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM SustanciaQuimicaPeligrosaRetce o where o.estado = true and o.reporteSustanciaQuimica.id = :id order by 1 desc")
					.setParameter("id", reporte.getId()).getResultList();
			return lista;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;
	}
	
	public boolean encontrarSustancia(ReporteSustanciasQuimicasPeligrosas reporte){
		try {
			
			String sql = "select c.dcsr_id from retce.dangerous_chemistry_substances_retce c inner join "
					+ "retce.hazardous_chemicals h on c.dcsr_id = h.dcsr_id inner join "
					+ "retce.hazardous_chemicals_detail d on h.hach_id = d.hach_id "
					+ "inner join suia_iii.dangerous_chemistry_substances s "
					+ "on d.dach_id = s.dach_id where (s.dach_description like '%Cianuro de sodio%' or "
					+ "s.dach_description like '%Cianuro de potasio%' or s.dach_description like '%Mercurio%') "
					+ "and h.hach_status = true and c.dcsr_id = :idReporte";

			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("idReporte", reporte.getId());
			List<Object> result = crudServiceBean.findByNativeQuery(sql, parameters);
			
			if(result != null && !result.isEmpty())
				return true;
			else
				return false;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	

}
