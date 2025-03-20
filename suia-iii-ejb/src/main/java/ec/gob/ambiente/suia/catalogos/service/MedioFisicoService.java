package ec.gob.ambiente.suia.catalogos.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.MedioFisico;
import ec.gob.ambiente.suia.domain.TipoCatalogo;

@Stateless
public class MedioFisicoService {

	Integer idCategoria = TipoCatalogo.MEDIO_FISICO;

	@EJB
	private CrudServiceBean crudServiceBean;
	

	public MedioFisico medioFisico(int id) {
		return crudServiceBean.find(MedioFisico.class, id);
	}

	@SuppressWarnings("unchecked")
	public List<MedioFisico> mediosFisicosSeleccionados(Integer idTdr) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idTdr", idTdr);
		return (List<MedioFisico>) crudServiceBean.findByNamedQuery(
				MedioFisico.FIND_BY_TDR_EIA, parameters);
	}

	@SuppressWarnings("unchecked")
	public List<MedioFisico> listarMediosFisicosPorProyecto(
			Integer idProyecto) {
		Map<String, Object> parameters = new ConcurrentHashMap<String, Object>();
		parameters.put("idProyecto", idProyecto);
		parameters.put("idCaty", idCategoria);

		return (List<MedioFisico>) crudServiceBean
				.findByNamedQuery(MedioFisico.FIND_BY_PROJECT_CATE, parameters);
	}

	public void adicionarMedioFisico(MedioFisico medioFisico) throws Exception {
		crudServiceBean.saveOrUpdate(medioFisico);
	}

	public void eliminarMedioFisico(MedioFisico medioFisico) throws Exception {
		crudServiceBean.delete(medioFisico);
	}
	
}
