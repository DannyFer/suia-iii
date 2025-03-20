package ec.gob.ambiente.suia.recaudaciones.facade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.recaudaciones.model.ProyectosConPagoSinNut;

@Stateless
public class ProyectosConPagoSinNutFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	public void guardarNUT(ProyectosConPagoSinNut proyecto) throws ServiceException {
		crudServiceBean.saveOrUpdate(proyecto);
	}
	
	@SuppressWarnings("unchecked")
	public ProyectosConPagoSinNut buscarNUTPorProyectoPorUsuario(Long procesoId, Usuario usuario) throws ServiceException {
		Map<String, Object> params = new HashMap<String, Object>();
	    params.put("processinstanceid", procesoId);
	    params.put("usuarioId", usuario.getId());
	    List<ProyectosConPagoSinNut> result = (List<ProyectosConPagoSinNut>) crudServiceBean.findByNamedQuery(ProyectosConPagoSinNut.LISTAR_SOLICITUD_POR_TRAMITE_POR_USUARIO,params);
	    if (result != null && !result.isEmpty()) {
			return result.get(0);
		}
	    return  null;
	}
	
	@SuppressWarnings("unchecked")
	public ProyectosConPagoSinNut buscarPorProyectoPorUsuarioPagoLiberado(String codigoTramite, Usuario usuario, Long procesoId) throws ServiceException {
		Map<String, Object> params = new HashMap<String, Object>();
	    params.put("codigoUnico", codigoTramite);
	    params.put("processinstanceid", procesoId);
	    params.put("usuarioId", usuario.getId());
	    List<ProyectosConPagoSinNut> result = (List<ProyectosConPagoSinNut>) crudServiceBean.findByNamedQuery(ProyectosConPagoSinNut.LISTAR_SOLICITUD_POR_TRAMITE_POR_USUARIO_PAGO_LIBERADO,params);
	    if (result != null && !result.isEmpty()) {
			return result.get(0);
		}
	    return  null;
	}
	
	@SuppressWarnings("unchecked")
	public ProyectosConPagoSinNut buscarPorProyectoPorUsuarioPorNumero(String codigoTramite, Usuario usuario, Long procesoId, String numeroTransaccion) throws ServiceException {
		Map<String, Object> params = new HashMap<String, Object>();
	    params.put("codigoUnico", codigoTramite);
	    params.put("processinstanceid", procesoId);
	    params.put("numeroTransaccion", numeroTransaccion);
	    params.put("usuarioId", usuario.getId());
	    List<ProyectosConPagoSinNut> result = (List<ProyectosConPagoSinNut>) crudServiceBean.findByNamedQuery(ProyectosConPagoSinNut.LISTAR_SOLICITUD_POR_TRAMITE_POR_USUARIO_POR_NUMERO,params);
	    if (result != null && !result.isEmpty()) {
			return result.get(0);
		}
	    return  null;
	}
	
	@SuppressWarnings("unchecked")
	public List<ProyectosConPagoSinNut> buscarPorProyectoPorUsuarioPorCodigoProyecto(Usuario usuario, String codigoUnico) throws ServiceException {
		Map<String, Object> params = new HashMap<String, Object>();
	    params.put("codigoProyecto", codigoUnico);
	    params.put("usuarioId", usuario.getId());
	    List<ProyectosConPagoSinNut> result = (List<ProyectosConPagoSinNut>) crudServiceBean.findByNamedQuery(ProyectosConPagoSinNut.LISTAR_SOLICITUD_POR_CODIGOPROYECTO,params);
	    if (result != null && !result.isEmpty()) {
			return result;
		}
	    return  null;
	}
	
	@SuppressWarnings("unchecked")
	public List<ProyectosConPagoSinNut> buscarPorProyectoPorUsuarioPorNumero(String numeroTransaccion) throws ServiceException {
		Map<String, Object> params = new HashMap<String, Object>();
	    params.put("numeroTransaccion", numeroTransaccion);
	    List<ProyectosConPagoSinNut> result = (List<ProyectosConPagoSinNut>) crudServiceBean.findByNamedQuery(ProyectosConPagoSinNut.LISTAR_SOLICITUD_POR_NUMEROTRANSACCION,params);
	    if (result != null && !result.isEmpty()) {
			return result;
		}
	    return  null;
	}
}
