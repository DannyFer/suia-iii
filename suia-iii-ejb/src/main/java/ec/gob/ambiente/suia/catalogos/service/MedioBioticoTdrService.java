package ec.gob.ambiente.suia.catalogos.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.MedioBioticoTdr;
import ec.gob.ambiente.suia.domain.TipoCatalogo;

@Stateless
public class MedioBioticoTdrService {
	
	Integer idCategoria = TipoCatalogo.MEDIO_BIOTICO;

	@EJB
	private CrudServiceBean crudServiceBean;
	

	public MedioBioticoTdr medioBiotico(int id) {
		return crudServiceBean.find(MedioBioticoTdr.class, id);
	}

	@SuppressWarnings("unchecked")
	public List<MedioBioticoTdr> mediosBioticosSeleccionados(Integer idTdr) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idTdr", idTdr);
		return (List<MedioBioticoTdr>) crudServiceBean.findByNamedQuery(
				MedioBioticoTdr.FIND_BY_TDR_EIA, parameters);
	}

	@SuppressWarnings("unchecked")
	public List<MedioBioticoTdr> listarMediosBioticosPorProyecto(
			Integer idProyecto) {
		Map<String, Object> parameters = new ConcurrentHashMap<String, Object>();
		parameters.put("idProyecto", idProyecto);
		parameters.put("idCaty", idCategoria);

		return (List<MedioBioticoTdr>) crudServiceBean
				.findByNamedQuery(MedioBioticoTdr.FIND_BY_PROJECT_CATE, parameters);
	}

	public void adicionarMedioBioticoTdr(MedioBioticoTdr MedioBioticoTdr) throws Exception {
		crudServiceBean.saveOrUpdate(MedioBioticoTdr);
	}

	public void eliminarMedioBioticoTdr(MedioBioticoTdr MedioBioticoTdr) throws Exception {
		crudServiceBean.delete(MedioBioticoTdr);
	}
	
}
