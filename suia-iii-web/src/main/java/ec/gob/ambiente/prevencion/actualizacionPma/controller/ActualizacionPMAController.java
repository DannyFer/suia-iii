package ec.gob.ambiente.prevencion.actualizacionPma.controller;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import ec.gob.ambiente.integracion.facade.IntegracionFacade;
import ec.gob.ambiente.prevencion.actualizacionPma.bean.ActualizacionPMABean;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.domain.Flujo;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.dto.ProyectoCustom;
import ec.gob.ambiente.suia.eia.facade.EstudioImpactoAmbientalFacade;
import ec.gob.ambiente.suia.integracion.bean.ContenidoExterno;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class ActualizacionPMAController implements Serializable {

	private static final long serialVersionUID = -4523371587113629686L;

	private static final Logger LOG = Logger.getLogger(ActualizacionPMAController.class);
	private static final String COMPLETADA = "Completada";
	

	@Getter
	@Setter
	@ManagedProperty(value = "#{actualizacionPMABean}")
	private ActualizacionPMABean actualizacionPMABean;

	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;

	@Getter
	@Setter
	@ManagedProperty(value = "#{proyectosBean}")
	private ProyectosBean proyectosBean;

	@EJB
	private EstudioImpactoAmbientalFacade estudioImpactoAmbientalFacade;
	@EJB
	private ProcesoFacade procesoFacade;

	@EJB
	private DocumentosFacade documentosFacade;

	@EJB
	private UsuarioFacade usuarioFacade;

	@Getter
	@Setter
	private boolean habilitarEstadosEtapas;

	@Getter
	@Setter
	private boolean deletionActive;

	@Getter
	@Setter
	private boolean updateSuiaActive;

	@PostConstruct
	private void init() {
		try {
			actualizacionPMABean.setProyectos(proyectoLicenciamientoAmbientalFacade.getAllProjects34ByUser(JsfUtil
					.getLoggedUser()));
			actualizacionPMABean.setProyectosNoFinalizados(proyectoLicenciamientoAmbientalFacade
					.getAllProjectsUnfinalizedByUser(JsfUtil.getLoggedUser()));
			habilitarEstadosEtapas = false;
			deletionActive = Usuario.isUserInRole(JsfUtil.getLoggedUser(), "admin");
			updateSuiaActive = Usuario.isUserInRole(JsfUtil.getLoggedUser(),
					"TÉCNICO REASIGNACIÓN COORDINADOR PROVINCIAL")
					|| Usuario.isUserInRole(JsfUtil.getLoggedUser(), "admin");
		} catch (Exception e) {
			LOG.error("Error cargando proyectos", e);
		}
	}


	public String seleccionarProyecto() {

		EstudioImpactoAmbiental estudioImpactoAmbiental = estudioImpactoAmbientalFacade.obtenerEIAPorId(((EstudioImpactoAmbiental) JsfUtil
				.devolverObjetoSession(Constantes.SESSION_EIA_OBJECT)).getId());
		try {
			ProyectoLicenciamientoAmbiental proyecto = estudioImpactoAmbiental.getProyectoLicenciamientoAmbiental();
			proyectosBean.setProyectoToShow(proyecto);
			return JsfUtil.actionNavigateTo("/proyectos/resumenProyecto.jsf");
		} catch (Exception e) {
			LOG.error(e.getMessage());
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
			return null;
		}

	}

	public String seleccionar(ProyectoCustom proyectoCustom) {
		switch (proyectoCustom.getSourceType()) {
		case ProyectoCustom.SOURCE_TYPE_INTERNAL:

			try {
				ProyectoLicenciamientoAmbiental proyecto = proyectoLicenciamientoAmbientalFacade
						.buscarProyectosLicenciamientoAmbientalPorId(Integer.parseInt(proyectoCustom.getId()));

				JsfUtil.cargarObjetoSession(Constantes.SESSION_EIA_OBJECT, estudioImpactoAmbientalFacade.obtenerPorProyecto(proyecto));
				//setProyectoToShow(proyecto);
				return JsfUtil.actionNavigateTo("/prevencion/actualizacionPma/pma/planManejoAmbiental.jsf");
			} catch (Exception e) {
				LOG.error(e.getMessage());
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
				return null;
			}

		case ProyectoCustom.SOURCE_TYPE_EXTERNAL_HIDROCARBUROS:
		case ProyectoCustom.SOURCE_TYPE_EXTERNAL_SUIA:

			JsfUtil.getBean(ContenidoExterno.class).executeAction(proyectoCustom,
					IntegracionFacade.IntegrationActions.mostrar_dashboard);

			break;
		}

		return null;
	}

	public void iniciarProceso()
	{
		try {
		EstudioImpactoAmbiental estudioImpactoAmbiental = estudioImpactoAmbientalFacade.obtenerEIAPorId(((EstudioImpactoAmbiental) JsfUtil
				.devolverObjetoSession(Constantes.SESSION_EIA_OBJECT)).getId());

        ProyectoLicenciamientoAmbiental proyecto = estudioImpactoAmbiental.getProyectoLicenciamientoAmbiental();

		Usuario director = usuarioFacade.getDirectorProvincialArea(proyecto.getAreaResponsable());
		if(director == null) {
			JsfUtil.addMessageError("No se ha podido identificar la autoridad responsable para este proyecto.");
			return;
		}

		Map<String, Object> parametros = new HashMap<String, Object>();
		parametros.put("u_SujetoControl", JsfUtil.getLoggedUser().getNombre());
		parametros.put("id_EIA", estudioImpactoAmbiental.getId());

		procesoFacade.iniciarProceso(JsfUtil.getLoggedUser(), Constantes.NOMBRE_PROCESO_ACTUALIZACION_PMA, actualizacionPMABean.getProyectoCustom()
				.getCodigo(), parametros);
		} catch (Exception ex) {
			LOG.error(ex);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_INICIAR_PROCESO);
		}
	}

	public void iniciarProceso(Flujo flujo) {
		try {
			JsfUtil.redirectTo(flujo.getUrlInicioFlujo() + "?flujoId=" + flujo.getId());
		} catch (Exception e) {
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}

}