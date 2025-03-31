package ec.gob.ambiente.suia.eia.finalizarEIA.bean;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.eia.facade.EstudioImpactoAmbientalFacade;
import ec.gob.ambiente.suia.eia.ficha.bean.EstudioImpactoAmbientalBean;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.validacionseccion.facade.ValidacionSeccionesFacade;

@ManagedBean
@ViewScoped
public class FinalizarEIABean implements Serializable {

	@EJB
	private ProcesoFacade procesoFacade;

	private static final long serialVersionUID = -2596257983697887700L;

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(FinalizarEIABean.class);

	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoFacade;
	@EJB
	private EstudioImpactoAmbientalFacade estudioImpactoAmbientalFacade;

	@EJB
	ValidacionSeccionesFacade validacionSeccionesFacade;

	@ManagedProperty(value = "#{loginBean}")
	@Getter
	@Setter
	private LoginBean loginBean;

	@ManagedProperty(value = "#{eia}")
	@Getter
	@Setter
	EstudioImpactoAmbientalBean estudioImpactoAmbientalBean;

	@ManagedProperty(value = "#{bandejaTareasBean}")
	@Getter
	@Setter
	private BandejaTareasBean bandejaTareasBean;

	@EJB(lookup = Constantes.JBPM_EJB_TASK_BEAN)
	private TaskBeanFacade taskBeanFacade;

	@Getter
	@Setter
	private EstudioImpactoAmbiental estudio;

	@Getter
	@Setter
	private ProyectoLicenciamientoAmbiental proyecto;

	@PostConstruct
	public void cargarDatos() {
		setEstudio(new EstudioImpactoAmbiental());
		cargarDatosBandeja();
	}

	private void cargarDatosBandeja() {

		try {
			this.estudio = estudioImpactoAmbientalBean.getEstudio();
			if (getEstudio() == null) {
				JsfUtil.addMessageError(JsfUtil.ERROR_INICIALIZAR_DATOS);
			}
		} catch (Exception e) {
			LOG.error("Error al cargar los datos del estudio.", e);
		}

	}

	public String enviarEstudio() {
		try {
			//MarielaGuano Actualización CI validar si tiene una solicitud de actualizacion de CI aprobada
			if(estudio.getProyectoLicenciamientoAmbiental().getEstadoActualizacionCertInterseccion().equals(2)) {
				RequestContext context = RequestContext.getCurrentInstance();
				context.execute("PF('dlgInformativo').show();");
				return null;
			}
			
			Map<String, Object> data = new ConcurrentHashMap<String, Object>();
			procesoFacade.aprobarTarea(loginBean.getUsuario(), bandejaTareasBean.getTarea().getTaskId(),
					bandejaTareasBean.getTarea().getProcessInstanceId(), data);
			estudioImpactoAmbientalFacade.guardar(estudio);
			procesoFacade.envioSeguimientoLicenciaAmbiental(loginBean.getUsuario(), bandejaTareasBean.getTarea().getProcessInstanceId());
			JsfUtil.addMessageInfo("Se realizó correctamente la operación.");
			return JsfUtil.actionNavigateTo("/bandeja/bandejaTareas.jsf");
		} catch (JbpmException e) {
			LOG.error(e);
			JsfUtil.addMessageError("Ocurrio un error al enviar la información.");
		} catch (ServiceException e) {
			LOG.error(e);
			JsfUtil.addMessageError("Ocurrio un error al actualizar el Estudio Ambiental.");
		}

		return "";
	}

	public void validarTareaBpm() {
		JsfUtil.validarPaginasUrlTareasBpm(bandejaTareasBean.getTarea(),
				"/prevencion/licenciamiento-ambiental/eia/resumenEjecutivo/resumenEjecutivo.jsf");
	}

}
