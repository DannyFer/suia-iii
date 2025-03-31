package ec.gob.ambiente.control.inspeccionescontrolambiental.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import org.apache.log4j.Logger;

import ec.gob.ambiente.core.interfaces.CompleteOperation;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.suia.comun.bean.IdentificarProyectoComunBean;
import ec.gob.ambiente.suia.comun.bean.ReasignarTareaComunBean;
import ec.gob.ambiente.suia.control.inspeccionescontrolambiental.facade.InspeccionControlAmbientalFacade;
import ec.gob.ambiente.suia.domain.AreaUsuario;
import ec.gob.ambiente.suia.domain.SolicitudInspeccionControlAmbiental;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.dto.ProyectoCustom;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.tramiteresolver.SolicitudInspeccionControlAmbientalTramiteResolver;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;

@ManagedBean
@SessionScoped
public class InspeccionControlAmbientalIniciarController implements Serializable {

	private static final long serialVersionUID = 908900291326245412L;

	private static final Logger LOG = Logger.getLogger(InspeccionControlAmbientalIniciarController.class);

	@EJB
	private InspeccionControlAmbientalFacade inspeccionControlAmbientalFacade;

	@EJB
	private ProcesoFacade procesoFacade;

	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;

	@Getter
	private ProyectoCustom proyectoCustom;

	@Getter
	private Usuario tecnico;

	@Getter
	private String tipoInspeccion;

	@Getter
	@Setter
	private String motivoSolicitud;

	@Getter
	private boolean permitirContinuar;

	@Getter
	private boolean showModalOtrosProcesos;

	@Getter
	private boolean showedModalOtrosProcesos;

	@Getter
	private int procesosActivos;

	@Getter
	private SolicitudInspeccionControlAmbiental solicitud;

	public String continuar(String tipoInspeccion) {
		iniciar();
		this.tipoInspeccion = tipoInspeccion;
		String url = "/control/inspeccionescontrolambiental/";
		return JsfUtil.actionNavigateTo(url + (tipoInspeccion.equalsIgnoreCase("solicitud")
				? "iniciarProcesoSolicitud.jsf" : "cronograma/cronograma.jsf"));
	}

	public String iniciarProceso() {
		try {
			solicitud = inspeccionControlAmbientalFacade.iniciarProceso(JsfUtil.getLoggedUser(), tecnico,
					proyectoCustom, SolicitudInspeccionControlAmbientalTramiteResolver.class, motivoSolicitud);
		} catch (JbpmException | ServiceException e) {
			LOG.error("Error iniciando proceso de inspeccion de control ambiental", e);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_INICIAR_PROCESO);
			permitirContinuar = false;
			solicitud = null;
			return null;
		}
		JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
		return JsfUtil.actionNavigateTo("/control/inspeccionescontrolambiental/iniciarProcesoSolicitudCompletada");
	}

	private void iniciar() {
		tipoInspeccion = null;
		motivoSolicitud = null;
		proyectoCustom = null;
		tecnico = null;
		permitirContinuar = true;
		showModalOtrosProcesos = false;
		showedModalOtrosProcesos = false;
		procesosActivos = 0;
		solicitud = null;
		try {
			procesosActivos = procesoFacade.getActiveProcessInstancesLogsVariableValue(JsfUtil.getLoggedUser(),
					Constantes.NOMBRE_PROCESO_INSPECCIONES_CONTROL_AMBIENTAL, "coordinador",
					JsfUtil.getLoggedUser().getNombre()).size();
			if (procesosActivos > 0)
				showModalOtrosProcesos = true;
		} catch (JbpmException e) {
			LOG.error("Error verificando instancias del proceso Inspeccion de control ambiental", e);
		}
	}

	public void seleccionarProyecto() {
		JsfUtil.getBean(IdentificarProyectoComunBean.class)
				.initFunction("/control/inspeccionescontrolambiental/iniciarProcesoSolicitud", new CompleteOperation() {

					@Override
					public Object endOperation(Object object) {
						proyectoCustom = (ProyectoCustom) object;
						return null;
					}
				}, new IdentificarProyectoComunBean.ProyectosQueryResolver() {

					@Override
					public String[] getTiposSector() {
						return null;
					}

					@Override
					public List<ProyectoCustom> getProyectos() {
						return proyectoLicenciamientoAmbientalFacade
								.listarProyectosLicenciamientoAmbientalPorAreaResponsable(
										JsfUtil.getLoggedUser().getListaAreaUsuario());
					}

					@Override
					public String[] getCategorias() {
						return null;
					}
				});
	}

	public void eliminarProyecto() {
		proyectoCustom = null;
	}

	public void seleccionarTecnico() {
		boolean plantaCentral = JsfUtil.getLoggedUser().getListaAreaUsuario().get(0).getArea().getTipoArea().getSiglas()
				.equals(Constantes.getRoleAreaName("area.plantacentral"));
		String roleType = Constantes.getRoleAreaName(plantaCentral ? "role.pc.tecnico" : "role.area.tecnico");
		
		String subarea = "";
		
		for(AreaUsuario au : JsfUtil.getLoggedUser().getListaAreaUsuario()){
			subarea += (subarea.equals("")) ? au.getArea().getAreaName() : "," + au.getArea().getAreaName();
		}
				
//		subarea = JsfUtil.getLoggedUser().getArea().getAreaName();
		JsfUtil.getBean(ReasignarTareaComunBean.class).initFunctionOnNotStatartedProcess(
				"Asignar técnico para la inspección", roleType, subarea,
				"/control/inspeccionescontrolambiental/iniciarProcesoSolicitud", new CompleteOperation() {

					@Override
					public Object endOperation(Object object) {
						tecnico = (Usuario) object;
						return null;
					}
				});
	}

	public void eliminarTecnico() {
		tecnico = null;
	}

	public void validateProyectoTecnico(FacesContext context, UIComponent validate, Object value) {
		List<FacesMessage> messages = new ArrayList<>();
		if (proyectoCustom == null)
			messages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Debe seleccionar el proyecto objeto de la inspección.", null));
		if (tecnico == null)
			messages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Debe seleccionar el técnico responsable de la inspección.", null));
		if (!messages.isEmpty())
			throw new ValidatorException(messages);
	}

	public void updateShowModalOtrosProcesos() {
		this.showedModalOtrosProcesos = true;
	}
}
