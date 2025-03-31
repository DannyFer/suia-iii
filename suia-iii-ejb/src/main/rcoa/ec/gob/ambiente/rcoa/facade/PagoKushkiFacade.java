package ec.gob.ambiente.rcoa.facade;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.rcoa.model.PagoKushki;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Usuario;


@Stateless
public class PagoKushkiFacade {

	@EJB
    private CrudServiceBean crudServiceBean;
	
	public void save(PagoKushki obj, Usuario usuario){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
		String DateToStoreInDataBase= sdf.format(new Date()); // java.util.Date
	    Timestamp ts = Timestamp.valueOf(DateToStoreInDataBase);		
		if(obj.getPago_Id()==null){
			obj.setUsuarioCreacion(usuario.getNombre());
			obj.setFechaCreacion(ts);			
		}
		else{
			obj.setUsuarioModificacion(usuario.getNombre());
			obj.setFechaModificacion(ts);			
		}
		crudServiceBean.saveOrUpdate(obj);
	}
}
