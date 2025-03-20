package ec.gob.ambiente.retce.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.retce.model.GestorDesechosPeligrosos;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.EjecutarSentenciasNativas;

@Stateless
public class GestorDesechosPeligrososFacade {

	@EJB
	private CrudServiceBean crudServiceBean;
	
	@EJB
	private EjecutarSentenciasNativas ejecutarSentenciasNativas;
	
	@EJB
	private SecuenciasFacade secuenciasFacade;

	public GestorDesechosPeligrosos getById(Integer id) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("id", id);
		try {
			GestorDesechosPeligrosos row = (GestorDesechosPeligrosos) crudServiceBean.findByNamedQuery(GestorDesechosPeligrosos.FIND_BY_ID,parameters).get(0);
			return row;
		} catch (Exception e) {
			return null;
		}
	}
	
	public GestorDesechosPeligrosos getByCode(String codigo) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("codigo", codigo);
		try {
			GestorDesechosPeligrosos row = (GestorDesechosPeligrosos) crudServiceBean.findByNamedQuery(GestorDesechosPeligrosos.FIND_BY_CODE,parameters).get(0);
			return row;
		} catch (Exception e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<GestorDesechosPeligrosos> getByInformacionBasica(Integer idInformacionBasica) throws ServiceException {
		List<GestorDesechosPeligrosos> lista = new ArrayList<GestorDesechosPeligrosos>(); 
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idInformacionBasica", idInformacionBasica);
		try {
			lista = (List<GestorDesechosPeligrosos>) crudServiceBean.findByNamedQuery(GestorDesechosPeligrosos.GET_BY_INFORMACION_BASICA,parameters);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;
	}
	
	private String generarCodigo() {
		try {
			return Constantes.SIGLAS_INSTITUCION + "-RETCE-GES-"
					+ secuenciasFacade.getCurrentYear()					
					+ "-"
					+ secuenciasFacade.getNextValueDedicateSequence("MAAE-RETCE-GES-",4);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public GestorDesechosPeligrosos guardar(GestorDesechosPeligrosos gestorDesechoPeligroso, Usuario usuario){
		if(gestorDesechoPeligroso.getId()==null){
			gestorDesechoPeligroso.setUsuarioCreacion(usuario.getNombre());
			gestorDesechoPeligroso.setFechaCreacion(new Date());
			gestorDesechoPeligroso.setCodigo(generarCodigo());
		} else {
			gestorDesechoPeligroso.setUsuarioModificacion(usuario.getNombre());
			gestorDesechoPeligroso.setFechaModificacion(new Date());			
		}
		crudServiceBean.saveOrUpdate(gestorDesechoPeligroso);
		return gestorDesechoPeligroso;
	}
	
	public GestorDesechosPeligrosos findByCodigo(String codigo){
		try {
			GestorDesechosPeligrosos obj = (GestorDesechosPeligrosos) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM GestorDesechosPeligrosos o where o.codigo = :codigo")
					.setParameter("codigo", codigo).getSingleResult();
			return obj;
			
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return null;
	}

}
