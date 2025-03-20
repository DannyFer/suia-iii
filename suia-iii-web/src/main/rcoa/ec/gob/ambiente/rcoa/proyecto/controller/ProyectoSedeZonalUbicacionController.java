package ec.gob.ambiente.rcoa.proyecto.controller;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.rcoa.digitalizacion.facade.UbicacionDigitalizacionFacade;
import ec.gob.ambiente.rcoa.digitalizacion.model.AutorizacionAdministrativaAmbiental;
import ec.gob.ambiente.rcoa.digitalizacion.model.UbicacionDigitalizacion;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaUbicacionFacade;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoaUbicacion;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.AreaUsuario;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.prevencion.categoria2.service.ProyectoLicenciaAmbientalServiceBean;
import ec.gob.ambiente.suia.utils.Constantes;

@ManagedBean
@ViewScoped
public class ProyectoSedeZonalUbicacionController implements Serializable{
	
	private static final long serialVersionUID = -6963992638158983944L;
	
	@EJB
    private ProyectoLicenciaCoaUbicacionFacade proyectoLicenciaCoaUbicacionFacade;
	@EJB
	private ProyectoLicenciaAmbientalServiceBean proyectoLicenciaAmbientalServiceBean;
	@EJB
	private UbicacionDigitalizacionFacade ubicacionDigitalizacionFacade;
	@EJB
	private AreaFacade areaFacade;
	
	@Getter
	@Setter
	private UbicacionesGeografica ubicacionesSeleccionada;
	
	@PostConstruct
	public void inicio() {

	}
	
	public String obtenerSedeUbicacionProyecto(Usuario usuarioAutoridad, String tipoProyecto, ProyectoLicenciaCoa proyectoLicenciaCoa, ProyectoLicenciamientoAmbiental proyectoSuia, AutorizacionAdministrativaAmbiental proyectoDigitalizado) {
		String ciudad=usuarioAutoridad.getPersona().getUbicacionesGeografica().getUbicacionesGeografica().getNombre();
		boolean esZonal = false, esAutoridad=false;
		String roldirectorgalapagos = Constantes.getRoleAreaName("role.va.cz.director.snap.galapagos");
		String rolAutoridadAmbiental = Constantes.getRoleAreaName("role.ppc.cz.autoridad");
		// verifico si tiene el rol de autoridad
		esAutoridad= Usuario.isUserInRole(usuarioAutoridad, roldirectorgalapagos) || Usuario.isUserInRole(usuarioAutoridad, rolAutoridadAmbiental); 
		//verifico si el area del usuario es una zonal	
		for (AreaUsuario areasUsuario : usuarioAutoridad.getListaAreaUsuario()) {
			if(areasUsuario.getArea().getTipoArea().getSiglas().toUpperCase().equals(Constantes.SIGLAS_TIPO_AREA_ZONALES) || areasUsuario.getArea().getId().equals(Constantes.ID_PARQUE_NACIONAL_GALAPAGOS))
				esZonal=true;
		}
		if(esZonal && esAutoridad && (proyectoLicenciaCoa != null || proyectoSuia != null || proyectoDigitalizado != null)){
			switch (tipoProyecto) {
			case "PROYECTORCOA":
				//busco la ubicacion principal del proyecto
				ProyectoLicenciaCoaUbicacion objProyectoUbucacion = proyectoLicenciaCoaUbicacionFacade.ubicacionPrincipal(proyectoLicenciaCoa);
				//obtengo el canton del proyecto
				if (objProyectoUbucacion != null && objProyectoUbucacion.getId() != null){
					ubicacionesSeleccionada = objProyectoUbucacion.getUbicacionesGeografica().getUbicacionesGeografica();
				}
				break;
			case "PROYECTOSUIAIII":
				ubicacionesSeleccionada = proyectoLicenciaAmbientalServiceBean.getUbicacionProyectoPorIdProyecto(proyectoSuia.getId());
				ubicacionesSeleccionada = ubicacionesSeleccionada.getUbicacionesGeografica();
				break;
			case "PROYECTODIGITALIZADO":
				// listo las ubicaciones del proyecto original
				List<UbicacionDigitalizacion> ListaUbicacionTipo = ubicacionDigitalizacionFacade.obtenerUbicacinesPorProyectoPorSistema(proyectoDigitalizado.getId(), 1, "WGS84", "17S");
				if(ListaUbicacionTipo == null || ListaUbicacionTipo.size() == 0){
					ListaUbicacionTipo = ubicacionDigitalizacionFacade.obtenerUbicacinesPorProyectoPorSistema(proyectoDigitalizado.getId(), 3, "WGS84", "17S");
				}
				// si no existen // listo las ubicaciones del proyecto ingresadas en digitalizacion
				if(ListaUbicacionTipo == null || ListaUbicacionTipo.size() == 0){
					ListaUbicacionTipo = ubicacionDigitalizacionFacade.obtenerUbicacinesPorProyecto(proyectoDigitalizado.getId(), 2);
				}
				// si  existen busco el area 
				if(ListaUbicacionTipo != null && ListaUbicacionTipo.size() > 0){
					ubicacionesSeleccionada = ListaUbicacionTipo.get(0).getUbicacionesGeografica();
				}
				break;
			default:
				break;
			}
			//verifico si es de galapagos
			if(ubicacionesSeleccionada != null && ubicacionesSeleccionada.getUbicacionesGeografica().getCodificacionInec().startsWith(Constantes.CODIGO_INEC_GALAPAGOS)){
				// obtengo la sede del parque nacional galapagos
				Area areaParque = areaFacade.getArea(Constantes.ID_PARQUE_NACIONAL_GALAPAGOS);
				if(areaParque != null && areaParque.getId() != null)
					ciudad = areaParque.getAreaSedeZonal();
			}else{
				// obtengo la sede en base a la zonal del canton
				if(ubicacionesSeleccionada.getAreaCoordinacionZonal() != null)
					ciudad = ubicacionesSeleccionada.getAreaCoordinacionZonal().getArea().getAreaSedeZonal();
			}
		}
		return ciudad;
	}
}
