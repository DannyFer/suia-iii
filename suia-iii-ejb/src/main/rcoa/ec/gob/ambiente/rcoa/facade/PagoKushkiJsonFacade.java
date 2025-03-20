package ec.gob.ambiente.rcoa.facade;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.enums.CatalogoTipoCoaEnum;
import ec.gob.ambiente.rcoa.model.CatalogoGeneralCoa;
import ec.gob.ambiente.rcoa.model.PagoKushkiJson;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.InstitucionFinanciera;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.recaudaciones.model.DocumentoNUT;
import ec.gob.ambiente.suia.recaudaciones.model.NumeroUnicoTransaccional;
import ec.gob.ambiente.suia.recaudaciones.model.SolicitudUsuario;

@Stateless
public class PagoKushkiJsonFacade {

	@EJB
    private CrudServiceBean crudServiceBean;
	
	public void save(PagoKushkiJson obj, Usuario usuario){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
		String DateToStoreInDataBase= sdf.format(new Date()); // java.util.Date
	    Timestamp ts = Timestamp.valueOf(DateToStoreInDataBase);			
		if(obj.getId_PagoJson()==null){
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
