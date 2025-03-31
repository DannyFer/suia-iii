package ec.gob.ambiente.suia.prevencion.viabilidadtecnica.service;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.ProyectoAmbientalNoRegulado;

@Stateless
public class DiagnosticoFiabilidadTecnicaService implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

//	private static final Logger LOG = Logger.getLogger(DiagnosticoFiabilidadTecnicaService.class);

	@EJB
	private CrudServiceBean crudServiceBean;

	public void ingresarProyectoAmbientalNoRegulado(ProyectoAmbientalNoRegulado proyectoAmbientalNoRegulado) {

		crudServiceBean.saveOrUpdate(proyectoAmbientalNoRegulado);
	}
	
	
	public ProyectoAmbientalNoRegulado buscarProyectoNoRegulado (Integer id)
	{
		return (ProyectoAmbientalNoRegulado) crudServiceBean.getEntityManager().createNativeQuery("select * from unregulated_projects_locations where unpl_id = ? ", ProyectoAmbientalNoRegulado.class).setParameter(1, id).getResultList();
	}
	
}
