package ec.gob.ambiente.suia.prevencion.viabilidadtecnica.service;

import java.io.Serializable;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.CoordenadsProyectosNoRegulados;
import ec.gob.ambiente.suia.domain.ProyectoAmbientalNoRegulado;

@Stateless
public class CoordenadsProyectosNoReguladosService implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger LOG = Logger.getLogger(CoordenadsProyectosNoReguladosService.class);

	@EJB
	private CrudServiceBean crudServiceBean;

	public void ingresarCoordenadaProyectoAmbientalNoRegulado(List<CoordenadsProyectosNoRegulados> lista) {

		crudServiceBean.saveOrUpdate(lista);
	}

	public List<CoordenadsProyectosNoRegulados> obtenerListaCoordenadasUTM(ProyectoAmbientalNoRegulado proyecto) {
		// TODO Auto-generated method stub
		return crudServiceBean.getEntityManager().createNativeQuery("select * from unregulated_projects_coordinates where unpl_id = ?", CoordenadsProyectosNoRegulados.class).setParameter(1, proyecto.getId()).getResultList();
	}
	
}
