package ec.gob.ambiente.suia.prevencion.viabilidadtecnica.facade;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.domain.CoordenadsProyectosNoRegulados;
import ec.gob.ambiente.suia.domain.ProyectoAmbientalNoRegulado;
import ec.gob.ambiente.suia.domain.ProyectoNoRegularizadoUbicacionGeografica;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.prevencion.viabilidadtecnica.service.CoordenadsProyectosNoReguladosService;
import ec.gob.ambiente.suia.prevencion.viabilidadtecnica.service.DiagnosticoFiabilidadTecnicaService;
import ec.gob.ambiente.suia.prevencion.viabilidadtecnica.service.ProyectoNoRegularizadoUbicacionGeograficaService;

@Stateless
public class DiagnosticoFiabilidadTecnicaFacade implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@EJB
	private DiagnosticoFiabilidadTecnicaService diagnostico;

	@EJB
	private ProyectoNoRegularizadoUbicacionGeograficaService proyectoService;
	
	@EJB
	private CoordenadsProyectosNoReguladosService coordenadasServicios;

	
	
	public List<CoordenadsProyectosNoRegulados> obtenerListaCoordenadasUTM(ProyectoAmbientalNoRegulado proyecto)
	{
		return coordenadasServicios.obtenerListaCoordenadasUTM(proyecto);  
	}
	
	public List<ProyectoNoRegularizadoUbicacionGeografica> obtenerListaProyectoNoRegularizadoUbicacionGeografica(ProyectoAmbientalNoRegulado proyecto)
	{
		return proyectoService.obtenerListaProyectoNoRegularizadoUbicacionGeografica(proyecto);  
	}
	
	public ProyectoAmbientalNoRegulado obtenerProyectoNoRegulado(Integer id)
	{
		return diagnostico.buscarProyectoNoRegulado(id);
	}
	
	public void ingresarProyectoAmbientalNoRegulado(ProyectoAmbientalNoRegulado proyectoAmbientalNoRegulado,
			List<UbicacionesGeografica> listaUbicaciones, List<CoordenadsProyectosNoRegulados> coordenadas) {
		diagnostico.ingresarProyectoAmbientalNoRegulado(proyectoAmbientalNoRegulado);

		List<ProyectoNoRegularizadoUbicacionGeografica> listaProyecto = new ArrayList<ProyectoNoRegularizadoUbicacionGeografica>();

		for (UbicacionesGeografica ubic : listaUbicaciones) {
			ProyectoNoRegularizadoUbicacionGeografica proyectoUbi = new ProyectoNoRegularizadoUbicacionGeografica();
			proyectoUbi.setProyectoAmbientalNoRegulado(proyectoAmbientalNoRegulado);
			proyectoUbi.setEstado(true);
			proyectoUbi.setUbicacionesGeografica(ubic);
			// persistir ubicacion proyecto
			listaProyecto.add(proyectoUbi);
		}
		proyectoService.ingresarProyectoAmbientalNoRegulado(listaProyecto);
		
		for (CoordenadsProyectosNoRegulados coor : coordenadas)
		{
			coor.setProyectoAmbientalNoRegulado(proyectoAmbientalNoRegulado);
		}
		
		coordenadasServicios.ingresarCoordenadaProyectoAmbientalNoRegulado(coordenadas);
		
	}

	
}
