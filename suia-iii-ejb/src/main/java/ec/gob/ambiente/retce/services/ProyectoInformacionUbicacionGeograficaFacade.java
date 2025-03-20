package ec.gob.ambiente.retce.services;


import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ec.gob.ambiente.suia.domain.PrestadorServiciosDesechos;
import ec.gob.ambiente.suia.domain.SedePrestadorServiciosDesechos;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.retce.model.ProyectoInformacionUbicacionGeografica;

@Stateless
public class ProyectoInformacionUbicacionGeograficaFacade{	
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	public List<ProyectoInformacionUbicacionGeografica> findByProyecto(Integer idproyecto){
		
		List<ProyectoInformacionUbicacionGeografica> empresas = new ArrayList<ProyectoInformacionUbicacionGeografica>();
		try {
			empresas = (ArrayList<ProyectoInformacionUbicacionGeografica>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM ProyectoInformacionUbicacionGeografica o where o.estado = true and o.idProyecto = :idproyecto order by o.orden")
					.setParameter("idproyecto", idproyecto)
					.getResultList();
			return empresas;
			
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return null;
	}	

	public void save(ProyectoInformacionUbicacionGeografica obj,Usuario usuario) {
		if(obj.getIdProyecto() == null)
			return;
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
