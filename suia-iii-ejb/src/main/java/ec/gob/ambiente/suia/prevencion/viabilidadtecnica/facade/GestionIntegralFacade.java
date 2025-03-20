package ec.gob.ambiente.suia.prevencion.viabilidadtecnica.facade;


import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.domain.CoordenadsProyectosNoRegulados;
import ec.gob.ambiente.suia.domain.ProyectoAmbientalNoRegulado;
import ec.gob.ambiente.suia.domain.ServiciosBasicosProyectosNoRegulados;
import ec.gob.ambiente.suia.domain.ServiciosPublicosProyectosNoRegulados;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.prevencion.viabilidadtecnica.service.GestionIntegralService;



@Stateless
public class GestionIntegralFacade {

	
	@EJB
	private GestionIntegralService gestionIntegralService;
	
	public List<UbicacionesGeografica> obtenerProvinciasPorRegion(Integer IdRegion){
        return 	(List<UbicacionesGeografica>) gestionIntegralService.obtenerProvinciasPorRegion(IdRegion);
	}
	
    public void ingresarCoordenadasProyectosNoRegulados(List<CoordenadsProyectosNoRegulados> lisCoor, ProyectoAmbientalNoRegulado proyectoAmbientalNoRegulado){
		
    	gestionIntegralService.ingresarCoordenadasProyectosNoRegulados(lisCoor, proyectoAmbientalNoRegulado);	
	}
    
    public ServiciosPublicosProyectosNoRegulados ingresarServiciosPublicosProyectosNoRegulados(ServiciosPublicosProyectosNoRegulados sppnr){
		return (ServiciosPublicosProyectosNoRegulados) gestionIntegralService.ingresarServiciosPublicosProyectosNoRegulados(sppnr);
	}
    
    public List<ServiciosPublicosProyectosNoRegulados> listarServiciosPublicosProyectosNoRegulados(Integer idProyectoAmbienteNoRegulado){
		return (List<ServiciosPublicosProyectosNoRegulados>) gestionIntegralService.listarServiciosPublicosProyectosNoRegulados(idProyectoAmbienteNoRegulado);
	}
    
    public void eliminarServiciosProyectoNoRegulado(ServiciosPublicosProyectosNoRegulados sppng){
		gestionIntegralService.eliminarServiciosProyectoNoRegulado(sppng);
	}
    
    public ServiciosBasicosProyectosNoRegulados ingresarServiciosBasicosProyectosNoRegulados(ServiciosBasicosProyectosNoRegulados serviciosBasicosProyectosNoRegulados)
    {
    	return gestionIntegralService.serviciosBasicosProyectosNoRegulados(serviciosBasicosProyectosNoRegulados);
    }
}