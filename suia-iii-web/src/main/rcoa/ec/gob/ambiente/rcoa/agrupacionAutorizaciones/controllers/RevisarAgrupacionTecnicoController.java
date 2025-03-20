package ec.gob.ambiente.rcoa.agrupacionAutorizaciones.controllers;


import java.io.ByteArrayInputStream;
import java.io.IOException;
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
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.rcoa.agrupacionAutorizaciones.facade.AgrupacionAutorizacionesFacade;
import ec.gob.ambiente.rcoa.agrupacionAutorizaciones.facade.AutorizacionesAdministrativasFacade;
import ec.gob.ambiente.rcoa.agrupacionAutorizaciones.facade.DocumentosAgrupacionFacade;
import ec.gob.ambiente.rcoa.agrupacionAutorizaciones.model.AgrupacionPrincipal;
import ec.gob.ambiente.rcoa.agrupacionAutorizaciones.model.AutorizacionAdministrativa;
import ec.gob.ambiente.rcoa.agrupacionAutorizaciones.model.DetalleAgrupacion;
import ec.gob.ambiente.rcoa.agrupacionAutorizaciones.model.DocumentoAgrupacion;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.TipoSector;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;


@ManagedBean
@ViewScoped
public class RevisarAgrupacionTecnicoController implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private final Logger LOG = Logger.getLogger(RevisarAgrupacionTecnicoController.class);
	
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
	
	@EJB
	private DocumentosAgrupacionFacade documentosFacade;
	
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
	private DocumentoAgrupacion documentoRespaldo;
	
	@Getter
	@Setter
	private Integer panelMostrar, idAgrupacion;
	
	@Getter
	@Setter
	private Boolean realizarCorrecciones;
	
	@Getter
	@Setter
	private String comentarioNoCorrecciones;

	@PostConstruct
	private void iniciar() {
		try {
			
			panelMostrar = 1;
			
			idAgrupacion = 1; //TODOMG recuperar id de bpm
			
			//TODOMG falta enlaces para visualizacion de informacion
			
			agrupacionPrincipal = agrupacionAutorizacionesFacade.getAgrupacionPorId(idAgrupacion);
			if (agrupacionPrincipal != null) {
				detalleAgrupacion = agrupacionAutorizacionesFacade
						.getDetalleAgrupacionPorIdPrincipal(agrupacionPrincipal.getId());
			}
			
			proyectos = new ArrayList<AutorizacionAdministrativa>();
			
			for (DetalleAgrupacion detalle : detalleAgrupacion) {
				AutorizacionAdministrativa auto = new AutorizacionAdministrativa();
				auto.setCodigo(detalle.getCodigoProyecto());
				auto.setId(detalle.getIdProyecto());
				auto.setNombre(detalle.getNombreProyecto());
				auto.setEstado(detalle.getEstadoProyecto());
				auto.setSector(detalle.getSector());
				auto.setFuente(detalle.getSistemaFuente());
				auto.setSeleccionadoSecundario(true);
				
				proyectos.add(auto);
			}
			
			AutorizacionAdministrativa autoPrincipal = new AutorizacionAdministrativa();
			autoPrincipal.setCodigo(agrupacionPrincipal.getCodigoProyecto());
			autoPrincipal.setId(agrupacionPrincipal.getIdProyecto());
			autoPrincipal.setNombre(agrupacionPrincipal.getNombreProyecto());
			autoPrincipal.setNombreProponente(agrupacionPrincipal.getNombreOperador());
			autoPrincipal.setEstado(agrupacionPrincipal.getEstadoProyecto());
			autoPrincipal.setSector(agrupacionPrincipal.getSector());
			autoPrincipal.setFuente(agrupacionPrincipal.getSistemaFuente());
			autoPrincipal.setSeleccionadoSecundario(true);
			
			proyectoSeleccionado = autoPrincipal;
			
			proyectos.add(0, autoPrincipal);
			
			List<DocumentoAgrupacion> documentos = documentosFacade
					.getDocumentoPorIdTablaTipo(agrupacionPrincipal.getId(),
							TipoDocumentoSistema.RCOA_AGRUPACION_RESPALDO_OPERADOR.getIdTipoDocumento());
			if (documentos.size() > 0) {
				documentoRespaldo = documentos.get(0);
			}
			
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
	
	public StreamedContent descargar() throws IOException {
		DefaultStreamedContent content = null;
		try {
			byte[] documentoContent = null;
			
			if (documentoRespaldo != null && documentoRespaldo.getIdAlfresco() != null) {
				documentoContent = documentosFacade.descargar(documentoRespaldo.getIdAlfresco(), documentoRespaldo.getFechaCreacion());
			} else if (documentoRespaldo.getContenidoDocumento() != null) {
				documentoContent = documentoRespaldo.getContenidoDocumento();
			}
			
			if (documentoRespaldo != null && documentoRespaldo.getNombre() != null
					&& documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(documentoRespaldo.getNombre());
			} else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
	
	public void siguienteObservaciones() {
		panelMostrar = 2;
		
		filtroOperador = proyectoSeleccionado.getNombreProponente();
		buscarAutorizaciones();
		
		if (proyectos != null) {
			for (AutorizacionAdministrativa pry : proyectos) {
				if(pry.getId() != null && pry.getId().equals(proyectoSeleccionado.getId()) && 
						pry.getCodigo().equals(proyectoSeleccionado.getCodigo())) {
					proyectoSeleccionado = pry;
					break;
				} else if(pry.getId() == null && pry.getCodigo().equals(proyectoSeleccionado.getCodigo())) {
					proyectoSeleccionado = pry;
					break;
				}
			}
		}
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
		
		if ((filtroProyecto != null && !filtroProyecto.isEmpty()) || 
				(filtroOperador != null && !filtroOperador.isEmpty()) || 
				(tipoSector != null)) {
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
		proyectos = new ArrayList<AutorizacionAdministrativa>();
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
		
		panelMostrar = 3;
		
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
	
	public void finalizarAgrupacion() {
		
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
			
			//TODOMG aprobar tarea revision
			
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
			JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al enviar la tarea.");
		}
	}
}
