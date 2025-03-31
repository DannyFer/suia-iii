package ec.gob.ambiente.rcoa.emisionLicenciaAmbiental;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;

import ec.gob.ambiente.rcoa.inventarioForestal.controller.InventarioForestalInformeTecnicoPronunciamientoController;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.bandeja.BandejaFacade;
import ec.gob.ambiente.suia.carga.facade.CargaLaboralFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;

@ManagedBean
@ViewScoped
public class EmisionLicenciaAmbientalCoordDireRevisarAsignarController extends DocumentoReporteResolucionMemoController implements Serializable {
	
	private static final long serialVersionUID = 5382084905489409831L;

	private static final Logger LOG = Logger
			.getLogger(InventarioForestalInformeTecnicoPronunciamientoController.class);
	
	public EmisionLicenciaAmbientalCoordDireRevisarAsignarController() {	
		super();
	}

	@ManagedProperty(value = "#{loginBean}")
	@Setter
	@Getter
	private LoginBean loginBean;

	/* EJBs */
	@EJB
	private CrudServiceBean crudServiceBean;
	@EJB
	private UsuarioFacade usuarioFacade;
	@EJB
	private CargaLaboralFacade cargaLaboralFacade;
	@EJB 
	private AreaFacade areaFacade;
	@EJB
	private BandejaFacade bandejaFacade;
	
	@Getter
	@Setter
	private List<Usuario> listaUsuario;
	
	@Getter
	@Setter
	private Boolean correccionInformeOficio;
	@Getter
	@Setter
	private Usuario tecnicoJuridicoSeleccionado = new Usuario();
	@Getter
	@Setter
	private Boolean requiereInspeccion;
	@Getter
	@Setter
	private PlantillaReporte plantillaReporteInforme;
	@Setter
    @Getter
    private String tramite, tecnicoJuridico;
	
	@PostConstruct
	public void init() {
		try {
			variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(),bandejaTareasBean.getProcessId());
    		tramite = (String) variables.get(Constantes.VARIABLE_PROCESO_TRAMITE);
    		tecnicoJuridico = (String) variables.get("tecnicoJuridico");
    		idProyecto = Integer.valueOf((String)variables.get(Constantes.ID_PROYECTO));

			proyectoLicenciaCoa = proyectoLicenciaCoaFacade.buscarProyectoPorId(idProyecto);
			
			inicializarVisualizarResolucionMemorando();
			visualizarResolucion(true);
			visualizarMemorandoFirmado();
			listaUsuario = listaUsuariosTecnicoJuridico();
		} catch (Exception e) {
			JsfUtil.addMessageError("Error visualizar resolucion / memorando");
			e.printStackTrace();
		}
	}
	
	private List<Usuario> listaUsuariosTecnicoJuridico() {
		List<Usuario> listaTecnicosResponsables = new ArrayList<Usuario>();			
		Area areaTramite = proyectoLicenciaCoa.getAreaResponsable();
		if(areaTramite.getTipoArea().getSiglas().equalsIgnoreCase(Constantes.SIGLAS_TIPO_AREA_PC)) {
			areaTramite = areaFacade.getAreaSiglas("CGAJ") ;
		} else if (areaTramite.getTipoArea().getSiglas().equalsIgnoreCase(Constantes.SIGLAS_TIPO_AREA_OT))
			areaTramite = areaTramite.getArea();
		
		String rolPrefijo =  "role.resolucion.tecnico.juridico";
		String rolTecnico = Constantes.getRoleAreaName(rolPrefijo);
		
		listaTecnicosResponsables = cargaLaboralFacade.cargaLaboralPorUsuarioArea(rolTecnico, areaTramite.getAreaName());
		if (listaTecnicosResponsables==null || listaTecnicosResponsables.isEmpty()){
			LOG.error("No se encontro usuario " + rolTecnico + " en " + areaTramite.getAreaName());
		}
		return listaTecnicosResponsables;
	}
	
	public void updateSelectedUser(Usuario usuario) {
		if (usuario.getSelectable()) {
			tecnicoJuridicoSeleccionado = new Usuario();
			tecnicoJuridicoSeleccionado = usuario;
		}
	}
	
	public Boolean validarDatos() {
		Boolean validar = true;
		List<String> msg= new ArrayList<String>();
		if (tecnicoJuridicoSeleccionado.getId() == null) {
			msg.add("Seleccione Técnico Jurídico");
		}
		if (msg.size() > 0) {
			JsfUtil.addMessageError(msg);
			validar = false;
		}
		return validar;
	}

	public void enviar() {
		if (validarDatos()) {
			try {
				Map<String, Object> params=new HashMap<>();
				// Tecnico Juridico
				params.put("tecnicoJuridico",tecnicoJuridicoSeleccionado.getNombre());
				procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), params);
				
				procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(),bandejaTareasBean.getProcessId(), null);
				JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
				JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
			} catch (Exception e) {							
				JsfUtil.addMessageError("Ocurrió un error al recuperar los datos.");
			}
		}
	}

}
