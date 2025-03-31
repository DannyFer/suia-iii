package ec.gob.ambiente.suia.prevencion.viabilidadtecnica.service;

import java.io.Serializable;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.ProyectoAmbientalNoRegulado;
import ec.gob.ambiente.suia.domain.ProyectoNoRegularizadoUbicacionGeografica;

@Stateless
public class ProyectoNoRegularizadoUbicacionGeograficaService implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger LOG = Logger.getLogger(ProyectoNoRegularizadoUbicacionGeograficaService.class);

	@EJB
	private CrudServiceBean crudServiceBean;

	public void ingresarProyectoAmbientalNoRegulado(List<ProyectoNoRegularizadoUbicacionGeografica> lista) {

		crudServiceBean.saveOrUpdate(lista);
	}

	public List<ProyectoNoRegularizadoUbicacionGeografica> obtenerListaProyectoNoRegularizadoUbicacionGeografica(
			ProyectoAmbientalNoRegulado proyecto) {
		// TODO Auto-generated method stub
		return crudServiceBean.getEntityManager().createNativeQuery("select * from unregulated_projects_locations where unep_id = ?", ProyectoNoRegularizadoUbicacionGeografica.class).setParameter(1, proyecto.getId()).getResultList();
	}
	
		

}
