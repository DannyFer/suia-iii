package ec.gob.ambiente.control.aprobacionrequisitostecnicos.controllers;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import ec.gob.ambiente.control.aprobacionrequisitostecnicos.bean.AprobacionRequisitosTecnicosBean;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.AprobacionRequisitosTecnicosFacade;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.EliminacionDesechoFacade;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.AprobacionRequisitosTecnicos;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.registrogeneradordesechos.facade.RegistroGeneradorDesechosFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;

@ManagedBean
@ViewScoped
public class FinalizarAprobacionRequisitosTecnicosController implements Serializable {

	private static final long serialVersionUID = -4595034866077116442L;

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(FinalizarAprobacionRequisitosTecnicosController.class);

	@EJB
	private ProcesoFacade procesoFacade;

	@EJB
	private AprobacionRequisitosTecnicosFacade aprobacionRequisitosTecnicosFacade;

	@EJB
	private RegistroGeneradorDesechosFacade registroGeneradorDesechosFacade;

	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;

	@EJB
	private EliminacionDesechoFacade eliminacionDesechoFacade;
	
	@EJB
	private AreaFacade areaFacade;

	@ManagedProperty("#{loginBean}")
	@Getter
	@Setter
	private LoginBean loginBean;

	@ManagedProperty("#{aprobacionRequisitosTecnicosBean}")
	@Getter
	@Setter
	AprobacionRequisitosTecnicosBean aprobacionRequisitosTecnicosBean;

	@ManagedProperty(value = "#{bandejaTareasBean}")
	@Getter
	@Setter
	private BandejaTareasBean bandejaTareasBean;

	@Getter
	@Setter
	private String isInformacionConfidencial;

	@Getter
	@Setter
	private boolean aceptarTerminos;

	@Getter
	@Setter
	private boolean habilitarMensajeNumVehiculoConductores;

	@Getter
	@Setter
	private AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos;

	@Getter
	@Setter
	private boolean existeGenerador;

	@PostConstruct
	public void cargarDatos() {
		cargarDatosBandeja();
	}

	private void cargarDatosBandeja() {
		try {
			if (aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos() == null) {
				JsfUtil.addMessageError(JsfUtil.ERROR_INICIALIZAR_DATOS);
			} else {
				aprobacionRequisitosTecnicos = aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos();

			}
		} catch (Exception e) {
			LOG.error("Error al cargar los datos de la aprobación de requisitos técnicos.", e);
		}

	}

	public String enviarAprobacionRequisitosTecnicos() throws Exception {
		existeGenerador = true;				
		return guardarVariableBpm()?direccionar():"";
	}

	public String direccionar() {
		try {
			if (aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos().isGestion()
					|| aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos().isTransporte()) {

				if (aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos().isGestion()) {
					aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos().setInformacionConfidencial(
							Boolean.valueOf(isInformacionConfidencial));
					aprobacionRequisitosTecnicosFacade.guardar(aprobacionRequisitosTecnicosBean
							.getAprobacionRequisitosTecnicos());
				}
				aprobacionRequisitosTecnicosFacade.enviarAprobacionRequisitosTecnicos(bandejaTareasBean.getTarea()
						.getProcessInstanceId(), loginBean.getUsuario(), bandejaTareasBean.getTarea().getTaskId());
				JsfUtil.addMessageInfo("Se realizó correctamente la operación.");
				return JsfUtil.actionNavigateTo("/bandeja/bandejaTareas.jsf");
			} else {
				JsfUtil.addMessageError("Debe indicar si hace transporte o gestión.");
			}
		} catch (JbpmException e) {
			LOG.error(e);
			JsfUtil.addMessageError("Ocurrió un error al enviar la información.");
		}
		return "";

	}

	public boolean guardarVariableBpm() {		
		try {
			Map<String, Object> params = new ConcurrentHashMap<>();
			params.put("existeRegistroGenerador", existeGenerador);
					
			Usuario usuarioTecnico= areaFacade.getUsuarioPorRolArea(AprobacionRequisitosTecnicosFacade.ROLE_TECNICO_CALIDAD_AMBIENTAL, aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos().getAreaResponsable());
			if(usuarioTecnico!=null) {
				params.put("tecnico", usuarioTecnico.getNombre());
				params.put("tecnicoPredeterminado", usuarioTecnico.getNombre());
				procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getTarea().getProcessInstanceId(), params);
				return true;
			}			
		} catch (Exception e) {
			LOG.error("Error no se puede enviar", e);
			JsfUtil.addMessageError("Error no se puede enviar");
		}		 
		return false;
	}	

}
