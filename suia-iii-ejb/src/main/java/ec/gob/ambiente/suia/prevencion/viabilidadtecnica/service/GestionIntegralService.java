package ec.gob.ambiente.suia.prevencion.viabilidadtecnica.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.CoordenadsProyectosNoRegulados;
import ec.gob.ambiente.suia.domain.FaseViabilidadTecnica;
import ec.gob.ambiente.suia.domain.ProyectoAmbientalNoRegulado;
import ec.gob.ambiente.suia.domain.ServiciosBasicosProyectosNoRegulados;
import ec.gob.ambiente.suia.domain.ServiciosPublicosProyectosNoRegulados;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;


@Stateless
public class GestionIntegralService {

	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	@SuppressWarnings("unchecked")
	public List<FaseViabilidadTecnica> obtenerFasesPorGestionIntegral() {
		return (List<FaseViabilidadTecnica>) crudServiceBean.getEntityManager().createQuery("From FaseViabilidadTecnica p where p.grupo =:grupo").setParameter("grupo", "Gestión Integral").getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<UbicacionesGeografica> obtenerProvinciasPorRegion(Integer IdRegion){
		return crudServiceBean.getEntityManager().createNativeQuery("select * from geographical_locations where pglo_id = ? ", UbicacionesGeografica.class).setParameter(1, IdRegion).getResultList();
    }
	
	public void ingresarCoordenadasProyectosNoRegulados(List<CoordenadsProyectosNoRegulados> lisCoor, ProyectoAmbientalNoRegulado proyectoAmbientalNoRegulado){
		
		for (CoordenadsProyectosNoRegulados coordenadsProyectosNoRegulados : lisCoor) {
			coordenadsProyectosNoRegulados.setProyectoAmbientalNoRegulado(proyectoAmbientalNoRegulado);
			crudServiceBean.saveOrUpdate(coordenadsProyectosNoRegulados);
		}
		 
	}
	
	public ServiciosPublicosProyectosNoRegulados ingresarServiciosPublicosProyectosNoRegulados(ServiciosPublicosProyectosNoRegulados sppnr){
		return (ServiciosPublicosProyectosNoRegulados) crudServiceBean.saveOrUpdate(sppnr);
	}
	
	
	@SuppressWarnings("unchecked")
	public List<ServiciosPublicosProyectosNoRegulados> listarServiciosPublicosProyectosNoRegulados(Integer idProyectoAmbienteNoRegulado){
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idProyNoRegul", idProyectoAmbienteNoRegulado);
		return (List<ServiciosPublicosProyectosNoRegulados>) crudServiceBean.findByNamedQuery(ServiciosPublicosProyectosNoRegulados.FIND_PROYECTO_NO_REGULADOS, parameters);
	}
	
	public void eliminarServiciosProyectoNoRegulado(ServiciosPublicosProyectosNoRegulados sppng){
		sppng.setEstado(false);
		crudServiceBean.getEntityManager().merge(sppng);
	}
	
	public ServiciosBasicosProyectosNoRegulados serviciosBasicosProyectosNoRegulados(ServiciosBasicosProyectosNoRegulados serviciosBasicosProyectosNoRegulados)
	{
		return crudServiceBean.getEntityManager().merge(serviciosBasicosProyectosNoRegulados);
	}
}