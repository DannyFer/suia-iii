package ec.gob.ambiente.suia.recaudaciones.facade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.recaudaciones.model.SolicitudUsuario;

@Stateless
public class SolicitudTramiteFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	public void guardarSolicitudUsuario(SolicitudUsuario solicitudUsuario) throws ServiceException {
		crudServiceBean.saveOrUpdate(solicitudUsuario);
	}
	
	@SuppressWarnings("unchecked")
	public SolicitudUsuario buscarSolicitudPorTramite(String tramite) throws ServiceException {
		 
		Map<String, Object> params = new HashMap<String, Object>();
	    params.put("tramite", tramite);
	    List<SolicitudUsuario> result = (List<SolicitudUsuario>) crudServiceBean.findByNamedQuery(SolicitudUsuario.LISTAR_SOLICITUD_POR_TRAMITE,params);
	    if (result != null && !result.isEmpty()) {
			return result.get(0);
		}
	    return  null;
	}	
	
}