package ec.gob.ambiente.suia.prevencion.viabilidadtecnica.service;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.ProyectoAmbientalNoRegulado;
import ec.gob.ambiente.suia.domain.ProyectoNoRegularizadoUbicacionGeografica;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;



@Stateless
public class ProyectoAmbientalNoReguladoService implements Serializable{

	private static final long serialVersionUID = -5741592923354324694L;
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	public ProyectoAmbientalNoRegulado ingresarProyectoAmbientalNoRegulado(ProyectoAmbientalNoRegulado proyectoAmbientalNoRegulado){
		//return crudServiceBean.saveOrUpdate(proyectoAmbientalNoRegulado);
		return crudServiceBean.getEntityManager().merge(proyectoAmbientalNoRegulado);
	}
	
	public ProyectoNoRegularizadoUbicacionGeografica ingresarProyectoNoRegularizadoUbicacionGeografica(ProyectoNoRegularizadoUbicacionGeografica proyectoNoRegularizadoUbicacionGeografica){
		return crudServiceBean.saveOrUpdate(proyectoNoRegularizadoUbicacionGeografica);
	}
	
	@SuppressWarnings("unchecked")
	public List<ProyectoNoRegularizadoUbicacionGeografica> listarUbicacionesGeograficasPorProyectoAmbientalNoRegulado(Integer idProyectoAmbienteNoRegulado)
	{
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idProyNoRegul", idProyectoAmbienteNoRegulado);
		return (List<ProyectoNoRegularizadoUbicacionGeografica>) crudServiceBean.findByNamedQuery(UbicacionesGeografica.FIND_PROYECTO_NO_REGULADO, parameters);
	}
	
	public void eliminarProyectoAmbientalNoRegulado(ProyectoNoRegularizadoUbicacionGeografica proyectoAmbientalNoRegulado){
		proyectoAmbientalNoRegulado.setEstado(false);
		crudServiceBean.getEntityManager().merge(proyectoAmbientalNoRegulado);
	}
}
