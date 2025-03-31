package ec.gob.ambiente.prevencion.tdr.controller;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.prevencion.tdr.bean.AnalizarTDRBean;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.certificadointerseccion.service.CertificadoInterseccionFacade;
import ec.gob.ambiente.suia.domain.TdrEiaLicencia;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.tdr.facade.TdrFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@RequestScoped
@ManagedBean
public class AnalizarTdrController implements Serializable {

	private static final long serialVersionUID = -35263712834217786L;
	private static final Logger LOGGER = Logger
			.getLogger(IniciarTdrController.class);
	@EJB
	private ProcesoFacade procesoFacade;

	@EJB
	private TdrFacade tdrFacade;

	@EJB
	private AreaFacade areaFacade;

	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;

	@EJB(lookup = Constantes.JBPM_EJB_TASK_BEAN)
	private TaskBeanFacade taskBeanFacade;

	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{analizarTDRBean}")
	private AnalizarTDRBean analizarTDRBean;

	@EJB
	private CertificadoInterseccionFacade certificadoInterseccionService;

	public boolean validarInicioTdr(Integer idProyecto) {
		return true;

	}

	public String iniciarTarea() {

		try {

			Map<String, Object> variables = procesoFacade
					.recuperarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getTarea()
							.getProcessInstanceId());
			Integer idProyecto = Integer.parseInt((String) variables
					.get(Constantes.ID_PROYECTO));
			TdrEiaLicencia tdrEia = tdrFacade
					.getTdrEiaLicenciaPorIdProyecto(idProyecto);

			Map<String, Object> params = new ConcurrentHashMap<String, Object>();

			Usuario diretor = new Usuario(); // areaFacade.getDirectorPlantaCentral();
			Usuario diretor_B = new Usuario(); // areaFacade
					//.getDirectorPlantaCentralPorArea("role.pc.director.biodiversidad");
			Usuario diretor_F = new Usuario(); // areaFacade
					//.getDirectorPlantaCentralPorArea("role.pc.director.forestal");

			params.put("u_Director", diretor.getNombre());
			params.put("u_DirectorBiodiversidad", diretor_B.getNombre());
			params.put("u_DirectorForestal", diretor_F.getNombre());

			params.put("requiereEquipo",
					analizarTDRBean.isEquipoMultidiciplinario());

			try {
				Boolean interseca = certificadoInterseccionService
						.getValueInterseccionProyectoIntersecaCapas(tdrEia
								.getProyecto());
				params.put("intersecaC", interseca);
				if (interseca) {
					Map<String, Boolean> capasInterseca = new HashMap<String, Boolean>();
					capasInterseca = certificadoInterseccionService
							.getCapasInterseccionBoolean(tdrEia.getProyecto()
									.getCodigo());
					params.put("intersecaSNAP",
							capasInterseca.get(Constantes.CAPA_SNAP));
					params.put("intersecaBP",
							capasInterseca.get(Constantes.CAPA_BP));
					params.put("intersecaPFE",
							capasInterseca.get(Constantes.CAPA_PFE));

					params.put("requierePronunciamiento", capasInterseca
							.get(Constantes.CAPAS_AMORTIGUAMIENTO));
				} else {
					params.put("intersecaSNAP", false);
					params.put("intersecaBP", false);
					params.put("intersecaPFE", false);
				}
			} catch (Exception e) {

				JsfUtil.addMessageError("Error al obtener la intersección del proyecto. Intente más tarde.");
				LOGGER.error("Error al obtener la intersección del proyecto", e);
				return "";
			}
			// params.put("requierePronunciamiento", false);

			params.put("anlizarForestal",
					tdrEia.getTdelRequiredForestInventory());

			// params.put("usuariosAreasA", "Forestal, Biodiversidad");

			procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean
					.getTarea().getProcessInstanceId(), params);

			Map<String, Object> data = new ConcurrentHashMap<String, Object>();
			taskBeanFacade.approveTask(loginBean.getNombreUsuario(),
					bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), data,
					loginBean.getPassword(),
					Constantes.getUrlBusinessCentral(), Constantes.getRemoteApiTimeout(), Constantes.getNotificationService());
			JsfUtil.addMessageInfo("Se realizó correctamente la operación.");
			return JsfUtil.actionNavigateTo("/bandeja/bandejaTareas.jsf");
		} catch (JbpmException e) {
			LOGGER.error(e);
			JsfUtil.addMessageError("Ocurrio un error al enviar la información.");
		}

		return "";
	}
}
