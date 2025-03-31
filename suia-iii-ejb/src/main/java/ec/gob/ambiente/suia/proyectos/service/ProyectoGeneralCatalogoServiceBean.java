package ec.gob.ambiente.suia.proyectos.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.ProyectoGeneralCatalogo;

@Stateless
public class ProyectoGeneralCatalogoServiceBean {

	@EJB
	private CrudServiceBean crudServiceBean;

	public void guardarProyectoGeneralCatalogo(
			ProyectoGeneralCatalogo proyectoGeneralCatalogo) {
		crudServiceBean.saveOrUpdate(proyectoGeneralCatalogo);
	}

        public void saveOrUpdateProyectoGeneralCatalogo(
			List<ProyectoGeneralCatalogo> listaProyectoGeneralCatalogo) {

		crudServiceBean.saveOrUpdate(listaProyectoGeneralCatalogo);

	}

	@SuppressWarnings("unchecked")
	public List<ProyectoGeneralCatalogo> listarProyectoGeneralCatalogoPorProyecto(
			Integer idProyecto) {
		Map<String, Object> parameters = new ConcurrentHashMap<String, Object>();
		parameters.put("idProyecto", idProyecto);

		return (List<ProyectoGeneralCatalogo>) crudServiceBean
				.findByNamedQuery(ProyectoGeneralCatalogo.FIND_BY_PROJECT,
						parameters);

	}

	@SuppressWarnings("unchecked")
	public List<ProyectoGeneralCatalogo> listarProyectoGeneralCatalogoPorProyectoCategoria(
			Integer idProyecto, Integer idCategoria) {
		Map<String, Object> parameters = new ConcurrentHashMap<String, Object>();
		parameters.put("idProyecto", idProyecto);
		parameters.put("idCaty", idCategoria);

		return (List<ProyectoGeneralCatalogo>) crudServiceBean
				.findByNamedQuery(ProyectoGeneralCatalogo.FIND_BY_PROJECT_CATE,
						parameters);

	}

	public void guardarProyectoGeneralCatalogo(
			List<ProyectoGeneralCatalogo> seleccionados, Integer idProyecto,
			Integer tipo) {
		List<ProyectoGeneralCatalogo> catalogosActuales = this
				.listarProyectoGeneralCatalogoPorProyectoCategoria(idProyecto,
						tipo);
		for (ProyectoGeneralCatalogo proyectoGeneralCatalogo : catalogosActuales) {
			crudServiceBean.delete(proyectoGeneralCatalogo);
		}
		for (ProyectoGeneralCatalogo proyectoGeneralCatalogo : seleccionados) {
			crudServiceBean.saveOrUpdate(proyectoGeneralCatalogo);
		}

	}
	
	public void eliminarProyectoGeneralCatalogo(ProyectoGeneralCatalogo proyectoGeneralCatalogo){
		crudServiceBean.delete(proyectoGeneralCatalogo);
	}
}
