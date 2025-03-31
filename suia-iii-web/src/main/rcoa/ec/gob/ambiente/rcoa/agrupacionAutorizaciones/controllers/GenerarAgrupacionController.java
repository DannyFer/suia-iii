package ec.gob.ambiente.rcoa.agrupacionAutorizaciones.controllers;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import ec.gob.ambiente.rcoa.agrupacionAutorizaciones.facade.AgrupacionAutorizacionesFacade;
import ec.gob.ambiente.rcoa.agrupacionAutorizaciones.facade.AutorizacionesAdministrativasFacade;
import ec.gob.ambiente.rcoa.agrupacionAutorizaciones.model.AgrupacionPrincipal;
import ec.gob.ambiente.rcoa.agrupacionAutorizaciones.model.AutorizacionAdministrativa;
import ec.gob.ambiente.rcoa.agrupacionAutorizaciones.model.DetalleAgrupacion;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.TipoSector;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;


@ManagedBean
@ViewScoped
public class GenerarAgrupacionController implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private final Logger LOG = Logger.getLogger(GenerarAgrupacionController.class);
	
	@Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;
	
	@ManagedProperty(value = "#{bandejaTareasBean}")
    @Getter
    @Setter
    private BandejaTareasBean bandejaTareasBean;
	
	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;
	
	@EJB
	private AutorizacionesAdministrativasFacade autorizacionesAdministrativasFacade;
	
	@EJB
	private AgrupacionAutorizacionesFacade agrupacionAutorizacionesFacade;
	
	@Getter
	@Setter
	private List<AutorizacionAdministrativa> proyectos, autorizacionesSecundarias;
	
	@Getter
	@Setter
	private AutorizacionAdministrativa proyectoSeleccionado;
	
	@Getter
	@Setter
	private List<TipoSector> listaTipoSector;
	
	@Getter
	@Setter
	private List<AutorizacionAdministrativa> listaSecundariasSeleccionadas;
	
	@Getter
	@Setter
	private AgrupacionPrincipal agrupacionPrincipal;
	
	@Getter
	@Setter
	private List<DetalleAgrupacion> detalleAgrupacion;
	
	@Getter
	@Setter
	private String filtroProyecto, filtroOperador, filtroConcesion, filtroBloque, filtroArea;
	
	@Getter
	@Setter
	private TipoSector tipoSector;
	
	@Getter
	@Setter
	private Integer panelMostrar;

	@PostConstruct
	private void iniciar() {
		try {
			
			panelMostrar = 1;
			
			cargarTipoSectores();
			
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos RegistroSnap.");
		}
	}
	
	public void validarTareaBpm() {
		JsfUtil.validarPaginasUrlTareasBpm(JsfUtil.getCurrentTask(),
				"");
	}
	
	private void cargarTipoSectores()
	{		
		listaTipoSector=new ArrayList<TipoSector>();		
		for (TipoSector tipoSector : proyectoLicenciamientoAmbientalFacade.getTiposSectores()) {
			if(tipoSector.getId()!=5){
				listaTipoSector.add(tipoSector);
			}
		}		
	}
	
	public void buscarAutorizaciones() {
		proyectos = new ArrayList<>();
		
		if (!filtroProyecto.isEmpty() || !filtroOperador.isEmpty() || tipoSector != null) {
			try {
				
				buscarLicenciasRegistros();
				
			} catch (Exception e) {
				e.printStackTrace();
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
			}
		} else {
			JsfUtil.addMessageInfo("Debe ingresar al menos un criterio de búsqueda");
			proyectos = null;
		}
	}
	
	public void buscarLicenciasRegistros() {
		//cuando se selecciona el filtro de sector no se realiza la busqueda en 4categorias y sectorSubsector xq no se dispone de esta información
		if(tipoSector == null) {
			List<AutorizacionAdministrativa> pryCuatroCategorias = autorizacionesAdministrativasFacade.getProyectos4Categorias(filtroProyecto, filtroOperador, false);
			if(pryCuatroCategorias != null)
				proyectos.addAll(pryCuatroCategorias);
			
			List<AutorizacionAdministrativa> prySectorSubsector = autorizacionesAdministrativasFacade.getProyectosSectorSubsector(filtroProyecto, filtroOperador, null);
			if(prySectorSubsector != null)
				proyectos.addAll(prySectorSubsector);
		} else {
			if (tipoSector.getId().equals(TipoSector.TIPO_SECTOR_HIDROCARBUROS)) {
				List<AutorizacionAdministrativa> pryCuatroCategorias = autorizacionesAdministrativasFacade.getProyectos4Categorias(filtroProyecto, filtroOperador, true);
				if(pryCuatroCategorias != null)
					proyectos.addAll(pryCuatroCategorias);
			}
		}
		
		List<AutorizacionAdministrativa> pryRegularizacion = autorizacionesAdministrativasFacade.getProyectosRegularizacion(tipoSector != null ? tipoSector.getId() : null, filtroArea, filtroBloque, filtroConcesion, filtroProyecto, filtroOperador);
		if(pryRegularizacion != null)
			proyectos.addAll(pryRegularizacion);
	}
	
	public void buscarOtrasAutorizaciones() {
		List<AutorizacionAdministrativa> pryArt = autorizacionesAdministrativasFacade.getProyectosAprobacionRequisitosTecnicos(filtroProyecto, proyectoSeleccionado.getNombreProponente());
		if(pryArt != null)
			autorizacionesSecundarias.addAll(pryArt);
		
		List<AutorizacionAdministrativa> pryRgd = autorizacionesAdministrativasFacade.getProyectosGeneradorDesechosPeligrosos(proyectoSeleccionado.getNombreProponente());
		if(pryRgd != null)
			autorizacionesSecundarias.addAll(pryRgd);
	}
	
	public void siguiente() {
		if(proyectoSeleccionado == null) {
			JsfUtil.addMessageError("Debe seleccionar la Autorización Administrativa Ambiental Principal");
			return;
		}
		
		panelMostrar = 2;
		
		proyectos.remove(proyectoSeleccionado);
		
		autorizacionesSecundarias = new ArrayList<AutorizacionAdministrativa>();
		autorizacionesSecundarias.addAll(proyectos);
		
		buscarOtrasAutorizaciones(); //TODOMG se deberia buscar las autorizaciones de acuerdo al operador del proceso principal
		//TODOMG se debe buscar las autorizaciones q no esten agrupadas
		
		agrupacionPrincipal = agrupacionAutorizacionesFacade.getAgrupacionPorProyectoEstado(proyectoSeleccionado.getCodigo(), 1); //proyecto en estado registrado		
		if(agrupacionPrincipal != null) {
			detalleAgrupacion = agrupacionAutorizacionesFacade.getDetalleAgrupacionPorIdPrincipal(agrupacionPrincipal.getId());
			
			if(detalleAgrupacion != null){
				for (DetalleAgrupacion detalle : detalleAgrupacion) {
					for (AutorizacionAdministrativa auto : autorizacionesSecundarias) {
						if(auto.getCodigo().equals(detalle.getCodigoProyecto())) {
							auto.setSeleccionadoSecundario(true);
							break;
						}
					}
				}
			}
		}		 
		
		proyectoSeleccionado.setSeleccionadoSecundario(true);
		autorizacionesSecundarias.add(0, proyectoSeleccionado);
		
	}
	
	public void enviar() {
		try {

			listaSecundariasSeleccionadas = new ArrayList<>();
			autorizacionesSecundarias.remove(0);
			for (AutorizacionAdministrativa auto : autorizacionesSecundarias) {
				if (auto.getSeleccionadoSecundario() != null && auto.getSeleccionadoSecundario()) 
					listaSecundariasSeleccionadas.add(auto);
			}
			
			if(listaSecundariasSeleccionadas.size() == 0) {
				autorizacionesSecundarias.add(0, proyectoSeleccionado);
				JsfUtil.addMessageError("Debe seleccionar las Autorizaciones Administrativas Ambientales Secundarias");
				return;
			}
			
			if(agrupacionPrincipal == null) {
				agrupacionPrincipal = new AgrupacionPrincipal();
				agrupacionPrincipal.setCodigoProyecto(proyectoSeleccionado.getCodigo());
				agrupacionPrincipal.setIdProyecto(proyectoSeleccionado.getId());
				agrupacionPrincipal.setCedulaOperador(proyectoSeleccionado.getCedulaProponente());
				agrupacionPrincipal.setNombreProyecto(proyectoSeleccionado.getNombre());
				agrupacionPrincipal.setNombreOperador(proyectoSeleccionado.getNombreProponente());
				agrupacionPrincipal.setEstadoProyecto(proyectoSeleccionado.getEstado());
				agrupacionPrincipal.setSector(proyectoSeleccionado.getSector());
				agrupacionPrincipal.setEstadoAgrupacion(1);
				agrupacionPrincipal.setSistemaFuente(proyectoSeleccionado.getFuente());
				
				agrupacionAutorizacionesFacade.guardarAgrupacion(agrupacionPrincipal);
			}
			
			for (AutorizacionAdministrativa auto : listaSecundariasSeleccionadas) {
				DetalleAgrupacion detalle = new DetalleAgrupacion();
				detalle.setIdPrincipal(agrupacionPrincipal.getId());
				detalle.setCodigoProyecto(auto.getCodigo());
				detalle.setIdProyecto(auto.getId());
				detalle.setNombreProyecto(auto.getNombre());
				detalle.setEstadoProyecto(auto.getEstado());
				detalle.setSector(auto.getSector());
				detalle.setSistemaFuente(auto.getFuente());
				
				
				agrupacionAutorizacionesFacade.guardarDetalleAgrupacion(detalle);
			}
			
			//TODOMG iniciar el proceso de agrupacion
			
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
			JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al enviar la tarea.");
		}
	}
}
