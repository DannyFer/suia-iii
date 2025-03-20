package ec.gob.ambiente.rcoa.registroAmbiental.controller;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import ant.DatosMatricula;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.senescyt.www.WSConsultaTitulos.ServicioConsultaTitulo.GraduadoReporteDTO;
import ec.gob.ambiente.client.SuiaServices_PortType;
import ec.gob.ambiente.client.SuiaServices_ServiceLocator;
import ec.gob.ambiente.rcoa.facade.FasesRegistroAmbientalCoaFacade;
import ec.gob.ambiente.rcoa.facade.PlanManejoAmbientalCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCuaCiuuFacade;
import ec.gob.ambiente.rcoa.facade.RegistroAmbientalCoaFacade;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCuaCiuu;
import ec.gob.ambiente.rcoa.model.RegistroAmbientalRcoa;
import ec.gob.ambiente.rcoa.viabilidadTecnica.facade.ViabilidadTecnicaProyectoFacade;
import ec.gob.ambiente.rcoa.viabilidadTecnica.model.ViabilidadTecnicaProyecto;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.CatalogoGeneral;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.FichaAmbientalPmaFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;


@ManagedBean
@ViewScoped
public class MarcoLegalReferencialController {

    @Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;

    @Getter
    @Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;

	@EJB
	private FichaAmbientalPmaFacade fichaAmbientalPmaFacade;
	
	@EJB
	private UsuarioFacade usuarioFacade;
	
	@EJB
	private FasesRegistroAmbientalCoaFacade fasesRegistroAmbientalCoaFacade;

	@EJB
	private PlanManejoAmbientalCoaFacade planManejoAmbientalCoaFacade;
	
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	@EJB
	private ProcesoFacade procesoFacade;

	@EJB
	private RegistroAmbientalCoaFacade registroAmbientalCoaFacade;
	
	@EJB
	private ProyectoLicenciaCuaCiuuFacade proyectoLicenciaCuaCiuuFacade;
	
	@EJB
	private ViabilidadTecnicaProyectoFacade viabilidadTecnicaProyectoFacade;

	@Getter
	@Setter
	private List<CatalogoGeneral> catalogoGenerals;
	
	@Getter
	@Setter
	private RegistroAmbientalRcoa registroAmbientalRcoa;
	
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyectoLicenciaCoa;

	@Getter
	@Setter
	private boolean validarMarcoLegal, validarDatos, habilitarIngreso;

	@Getter
	@Setter
	private Long idProceso;
	
    private Map<String, Object> variables;
	
	private String tramite;

	@Getter
	private List<SelectItem> listaNormativa;
	
	@Getter
	@Setter
	private Boolean esActividadRelleno = false;
	
	@Getter
	@Setter
	private ViabilidadTecnicaProyecto viabilidadTecnica;
	
	@PostConstruct
	public void init() {
		try{
			variables=procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
			tramite=(String)variables.get(Constantes.VARIABLE_PROCESO_TRAMITE);
			idProceso = bandejaTareasBean.getProcessId();
			//tramite ="MAE-RA-2020-415415";
			proyectoLicenciaCoa = proyectoLicenciaCoaFacade.buscarProyecto(tramite);
			if(loginBean.getUsuario().getId() == null){
				JsfUtil.redirectTo("/start.js");
				return;
			}
			validarMarcoLegal = false;
			validarDatos = false;
			catalogoGenerals = new ArrayList<CatalogoGeneral>();
			//catalogoGenerals = fichaAmbientalPmaFacade.getArticulosCatalogoCoa();
			caragrNormativa();
			proyectoLicenciaCoa = proyectoLicenciaCoaFacade.buscarProyectoPorId(proyectoLicenciaCoa.getId());
			registroAmbientalRcoa = registroAmbientalCoaFacade.obtenerRegistroAmbientalPorProyecto(proyectoLicenciaCoa);
			if (registroAmbientalRcoa == null){
				registroAmbientalRcoa = new RegistroAmbientalRcoa();
				habilitarIngreso = false;
			}else{
				validarMarcoLegal = registroAmbientalRcoa.isAceptacion();
				habilitarIngreso = registroAmbientalRcoa.isFinalizadoIngreso();
			}
			validarActividadRellenoSanitario();
		}catch(Exception e){
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos.");
		}
	}
	public void mostrarMensaje() {
			JsfUtil.addCallbackParam("mostrarNoMensaje");
	}

	private void caragrNormativa() {
		listaNormativa = new ArrayList<SelectItem>();
		listaNormativa.add(new SelectItem("1", "Constitución de la República del Ecuador"));
		listaNormativa.add(new SelectItem("2", "Código Orgánico del Ambiente"));
		listaNormativa.add(new SelectItem("3", "Reglamento al Código Orgánico del Ambiente"));
	}
	
	private void validarActividadRellenoSanitario(){
		try {
			List<ProyectoLicenciaCuaCiuu> listaActividadesCiuu = new ArrayList<ProyectoLicenciaCuaCiuu>();
			listaActividadesCiuu = proyectoLicenciaCuaCiuuFacade.actividadesPorProyecto(proyectoLicenciaCoa);
			
			for(ProyectoLicenciaCuaCiuu actividad : listaActividadesCiuu){
				if(actividad.getCatalogoCIUU().getCodigo().equals("E3821.01") || actividad.getCatalogoCIUU().getCodigo().equals("E3821.01.01")){
					List<ViabilidadTecnicaProyecto> lista = viabilidadTecnicaProyectoFacade.buscarPorProyecto(proyectoLicenciaCoa.getId());
					
					if(lista!= null && !lista.isEmpty()){
						viabilidadTecnica = lista.get(0);
						esActividadRelleno = true;
					}					
					break;
				}
			}			
			
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	/*
	 * metodo para consultar los titulos en el senecyt en base al numero de cedula
	 */
	public GraduadoReporteDTO buscarTituloPorDocumento(String documento) throws ServiceException {
		try {
			GraduadoReporteDTO titulos = new GraduadoReporteDTO();
			SuiaServices_ServiceLocator wsBuscarTitulos = new SuiaServices_ServiceLocator();
			SuiaServices_PortType servicioWebBuscarVehiculo = wsBuscarTitulos.getSuiaServicesPort(getUrlTitulos());
			servicioWebBuscarVehiculo.getTitulo(documento).getNiveltitulos();
			return titulos;
		} catch (Exception e) {

			throw new ServiceException(e.getCause());
		}
	}

	private URL getUrlTitulos() throws MalformedURLException {
		URL url = null;
		URL baseUrl;
		baseUrl = ec.gob.ambiente.client.SuiaServices_Service.class.getResource(".");
		url = new URL(baseUrl, Constantes.getUrlVehicleSearch());
		return url;
	}
	
	
	public DatosMatricula buscarVehiculo(String placaVehiculo) throws ServiceException {
		try {
			return invocarWSBuscarVehiculo(placaVehiculo);
		} catch (Exception e) {
			throw new ServiceException(e.getCause());
		}
	}

	private DatosMatricula invocarWSBuscarVehiculo(String placaVehiculo) throws Exception {
		SuiaServices_ServiceLocator wsBuscarVehiculo = new SuiaServices_ServiceLocator();
		SuiaServices_PortType servicioWebBuscarVehiculo = wsBuscarVehiculo.getSuiaServicesPort(getUrlVehicleSearch());
		/*DatosMatricula respuestaServicioWebBuscarVehiculo = servicioWebBuscarVehiculo.getMatricula(placaVehiculo,
				"string", "string");*/
		DatosMatricula respuestaServicioWebBuscarVehiculo = servicioWebBuscarVehiculo.getMatricula(placaVehiculo,
				"1", Constantes.USUARIO_WS_MAE_SRI_RC);
		return respuestaServicioWebBuscarVehiculo;
	}

	private URL getUrlVehicleSearch() throws MalformedURLException {
		URL url = null;
		URL baseUrl;
		baseUrl = ec.gob.ambiente.client.SuiaServices_Service.class.getResource(".");
		url = new URL(baseUrl, Constantes.getUrlVehicleSearch());
		return url;
	}
	
	/**
	 * metodo para guardar la informacion ingresada del profesional responsable del registro
	 */
	public boolean guardar(){
		try{
			validarDatos = false;
			if(this.validarMarcoLegal){
				//guardo la ficha
				registroAmbientalRcoa.setProyectoCoa(proyectoLicenciaCoa);
				registroAmbientalRcoa.setAceptacion(validarMarcoLegal);
				registroAmbientalCoaFacade.guardar(registroAmbientalRcoa);
				
				if(esActividadRelleno){
					viabilidadTecnica.setRegistroAmbientalRcoa(registroAmbientalRcoa);
					viabilidadTecnicaProyectoFacade.guardar(viabilidadTecnica, JsfUtil.getLoggedUser());
				}			
				
				JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
				validarDatos = true;
			}else{
				JsfUtil.addMessageError("Debe aceptar las Normativas Legales que aplican a su proyecto, obra o actividad.");
			}
			return true;
		}catch(Exception e){
			JsfUtil.addMessageError(JsfUtil.ERROR_ACTUALIZAR_REGISTRO);
			return false;
		}
	}
}
