package ec.gob.ambiente.suia.reportes.facade;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.UrlBiReportes;
import ec.gob.ambiente.suia.domain.Usuario;

@Stateless
public class UrlBiReportesFacade{
	
	@EJB
	private CrudServiceBean crudServiceBean;	
	
	public UrlBiReportes findById(Integer id){
		try{
			UrlBiReportes catalogoServicios = (UrlBiReportes) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM UrlBiReportes o where o.tipoUrl = 1 and o.estado = true and o.id = :id")
					.setParameter("id", id)					
					.getSingleResult();
			return catalogoServicios;
			
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public UrlBiReportes findByIdByTipoReporte(Integer idTipoUrl, Integer idTipoReporte, Integer idArea){
		try{
			List<UrlBiReportes> catalogoServicios = (ArrayList<UrlBiReportes>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM UrlBiReportes o where o.tipoUrl = :idTipoUrl and o.estado = true and o.tipoReporte = :tiporeporteId and o.valorId = :areaId ")
					.setParameter("idTipoUrl", idTipoUrl)
					.setParameter("tiporeporteId", idTipoReporte)
					.setParameter("areaId", idArea)
					.getResultList();
			if(catalogoServicios != null && catalogoServicios.size() > 0){
				return catalogoServicios.get(0);
			}
			return null;
			
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void save(UrlBiReportes obj,Usuario usuario) {
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
}