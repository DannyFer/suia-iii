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

import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCuaCiuuFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.controller.InventarioForestalInformeTecnicoPronunciamientoController;
import ec.gob.ambiente.rcoa.model.CatalogoCIUU;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCuaCiuu;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
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
public class EmisionLicenciaAmbientalCoordDireAsignarTecnicoController extends DocumentoReporteResolucionMemoController implements Serializable {
	
	private static final long serialVersionUID = 5382084905489409831L;

	private static final Logger LOG = Logger
			.getLogger(InventarioForestalInformeTecnicoPronunciamientoController.class);
	
	public EmisionLicenciaAmbientalCoordDireAsignarTecnicoController() {	
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
	private ProyectoLicenciaCuaCiuuFacade proyectoLicenciaCuaCiuuFacade;
	
	@Getter
	@Setter
	private List<Usuario> listaUsuario;
	
	@Getter
	@Setter
	private Boolean correccionInformeOficio;
	@Getter
	@Setter
	private Usuario tecnicoResponsableSeleccionado = new Usuario();
	@Getter
	@Setter
	private Boolean requiereInspeccion;
	@Getter
	@Setter
	private PlantillaReporte plantillaReporteInforme;
	@Setter
    @Getter
    private String tramite, tecnicoResponsable;
	
	@PostConstruct
	public void init() {
		try {
			variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(),bandejaTareasBean.getProcessId());
    		tramite = (String) variables.get(Constantes.VARIABLE_PROCESO_TRAMITE);
    		tecnicoResponsable = (String) variables.get("tecnicoResponsable");
    		idProyecto = Integer.valueOf((String)variables.get(Constantes.ID_PROYECTO));
    		
			proyectoLicenciaCoa = proyectoLicenciaCoaFacade.buscarProyectoPorId(idProyecto);
			inicializarMemorandoPronunciamiento(false);
			visualizarResolucion(true);
			visualizarMemorandoJuridicoFirmado();
			listaUsuario = listaUsuariosTecnico();
		} catch (Exception e) {
			JsfUtil.addMessageError("Error visualizar resolucion / memorando");
			e.printStackTrace();
		}
	}
	
	private List<Usuario> listaUsuariosTecnico() {
		Area areaResponsable = proyectoLicenciaCoa.getAreaResponsable();
		String tipoRol = "role.esia.cz.tecnico.responsable";

		if (areaResponsable.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_PC)) {
			ProyectoLicenciaCuaCiuu proyectoCiuuPrincipal = proyectoLicenciaCuaCiuuFacade.actividadPrincipal(proyectoLicenciaCoa);
			CatalogoCIUU actividadPrincipal = proyectoCiuuPrincipal.getCatalogoCIUU();

			Integer idSector = actividadPrincipal.getTipoSector().getId();

			tipoRol = "role.esia.pc.tecnico.responsable.tipoSector." + idSector;
		} else if (!areaResponsable.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_OT)) {
			tipoRol = "role.esia.gad.tecnico.responsable";
		}

		String rolTecnico = Constantes.getRoleAreaName(tipoRol);

		// buscar usuarios por rol y area
		List<Usuario> listaUsuario = cargaLaboralFacade.cargaLaboralPorUsuarioArea(rolTecnico, areaResponsable.getAreaName());
		if (listaUsuario == null || listaUsuario.size() == 0) {
			LOG.error("No se encontró técnico responsable en " + areaResponsable.getAreaName());
		}
		
		Usuario usuarioTecnico = usuarioFacade.buscarUsuario(tecnicoResponsable);
		
		for (Usuario usuario : listaUsuario) {
			if(usuarioTecnico != null && usuario.getNombre().equals(usuarioTecnico.getNombre())){
				usuario.setAvailable(true);
			} else {
				usuario.setAvailable(false);
			}
		}
		
		return listaUsuario;
	}
	
	public Boolean validarDatos() {
		Boolean validar = true;
		List<String> msg= new ArrayList<String>();
		if (tecnicoResponsableSeleccionado == null || tecnicoResponsableSeleccionado.getId() == null) {
			msg.add("Seleccione Técnico Responsable");
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
				// Tecnico responsable
				if(!tecnicoResponsable.equals(tecnicoResponsableSeleccionado.getNombre())) {
					params.put("tecnicoResponsable",tecnicoResponsableSeleccionado.getNombre());
					procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), params);
				}
				
				procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(),bandejaTareasBean.getProcessId(), null);
				JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
				JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
			} catch (Exception e) {							
				JsfUtil.addMessageError("Ocurrió un error al recuperar los datos.");
			}
		}
	}

}
