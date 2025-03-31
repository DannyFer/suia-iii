package ec.gob.ambiente.suia.catalogos.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.MedioSocial;
import ec.gob.ambiente.suia.domain.TipoCatalogo;

@Stateless
public class MedioSocialService {

	Integer idCategoria = TipoCatalogo.MEDIO_SOCIAL;

	@EJB
	private CrudServiceBean crudServiceBean;
	

	public MedioSocial MedioSocial(int id) {
		return crudServiceBean.find(MedioSocial.class, id);
	}

	@SuppressWarnings("unchecked")
	public List<MedioSocial> mediosSocialesSeleccionados(Integer idTdr) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idTdr", idTdr);
		return (List<MedioSocial>) crudServiceBean.findByNamedQuery(
				MedioSocial.FIND_BY_TDR_EIA, parameters);
	}

	@SuppressWarnings("unchecked")
	public List<MedioSocial> listarMediosSocialesPorProyecto(
			Integer idProyecto) {
		Map<String, Object> parameters = new ConcurrentHashMap<String, Object>();
		parameters.put("idProyecto", idProyecto);
		parameters.put("idCaty", idCategoria);

		return (List<MedioSocial>) crudServiceBean
				.findByNamedQuery(MedioSocial.FIND_BY_PROJECT_CATE, parameters);
	}

	public void adicionarMedioSocial(MedioSocial MedioSocial) throws Exception {
		crudServiceBean.saveOrUpdate(MedioSocial);
	}

	public void eliminarMedioSocial(MedioSocial MedioSocial) throws Exception {
		crudServiceBean.delete(MedioSocial);
	}
	
}
