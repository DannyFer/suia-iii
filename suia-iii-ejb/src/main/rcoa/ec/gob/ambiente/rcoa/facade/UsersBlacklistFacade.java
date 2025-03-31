package ec.gob.ambiente.rcoa.facade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;

import ec.gob.ambiente.rcoa.model.UsersBlacklist;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class UsersBlacklistFacade {

	@EJB
	private CrudServiceBean crudServiceBean;
	
	private static final Logger LOG = Logger.getLogger(UsersBlacklistFacade.class);
	
	@SuppressWarnings("unchecked")
	public Boolean listaUsuarios(String identificacion) {
		Boolean Validacion = false;
		try {		
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("ci", identificacion);
		    List<UsersBlacklist> listacedula = (List<UsersBlacklist>) crudServiceBean.findByNamedQuery(UsersBlacklist.LISTAR_POR_CED,params);
			if (listacedula != null && !listacedula.isEmpty()) {
				Validacion=true;
			} else {
				Validacion=false;
			}			
		}
		catch(Exception e)
		{
			LOG.error(e, e);
		}	
       return Validacion;
	}
	
}
