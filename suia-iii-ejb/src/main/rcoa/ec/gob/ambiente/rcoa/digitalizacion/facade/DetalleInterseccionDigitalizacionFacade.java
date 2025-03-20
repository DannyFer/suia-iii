package ec.gob.ambiente.rcoa.digitalizacion.facade;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.rcoa.digitalizacion.model.DetalleInterseccionDigitalizacion;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Usuario;

@Stateless
public class DetalleInterseccionDigitalizacionFacade {
	
	@EJB
    private CrudServiceBean crudServiceBean;
	
	public void guardar(DetalleInterseccionDigitalizacion obj, Usuario usuario) {        
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
