package ec.gob.ambiente.suia.prevencion.viabilidadtecnica.facade;

import java.io.Serializable;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.domain.ProyectoAmbientalNoRegulado;
import ec.gob.ambiente.suia.domain.ProyectoNoRegularizadoUbicacionGeografica;
import ec.gob.ambiente.suia.prevencion.viabilidadtecnica.service.ProyectoAmbientalNoReguladoService;



@Stateless
public class ProyectoAmbientalNoReguladoFacade implements Serializable{
   
	private static final long serialVersionUID = 5298568613903776697L;
	
	@EJB
	private ProyectoAmbientalNoReguladoService proyectoAmbientalNoReguladoService;
	
	public ProyectoAmbientalNoRegulado ingresarProyectoAmbientalNoRegulado(ProyectoAmbientalNoRegulado proyectoAmbientalNoRegulado){
		return proyectoAmbientalNoReguladoService.ingresarProyectoAmbientalNoRegulado(proyectoAmbientalNoRegulado);
	}
	
	public List<ProyectoNoRegularizadoUbicacionGeografica> listarUbicacionesGeograficasPorProyectoAmbientalNoRegulado(Integer idProyNoRegulado)
	{
		return proyectoAmbientalNoReguladoService.listarUbicacionesGeograficasPorProyectoAmbientalNoRegulado(idProyNoRegulado);
	}
	
	public ProyectoNoRegularizadoUbicacionGeografica ingresarProyectoNoRegularizadoUbicacionGeografica(ProyectoNoRegularizadoUbicacionGeografica proyectoNoRegularizadoUbicacionGeografica)
	{
		return proyectoAmbientalNoReguladoService.ingresarProyectoNoRegularizadoUbicacionGeografica(proyectoNoRegularizadoUbicacionGeografica);
	}
	
	public void eliminarProyectoAmbientalNoRegulado(ProyectoNoRegularizadoUbicacionGeografica proyectoNoRegularizadoUbicacionGeografica)
	{
		proyectoAmbientalNoReguladoService.eliminarProyectoAmbientalNoRegulado(proyectoNoRegularizadoUbicacionGeografica);
	}
	
}
