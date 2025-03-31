package ec.gob.ambiente.control.aprobacionrequisitostecnicos.bean;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.AprobacionRequisitosTecnicosFacade;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.AprobacionRequisitosTecnicos;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.validacionseccion.facade.ValidacionSeccionesFacade;

/**
 * <b> Clase bean para la finalización del proceso aprobación requisitos
 * técnicos. </b>
 * 
 * @author Jonathan Guerrero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: Jonathan Guerrero $, $Date: 11/07/2015 $]
 *          </p>
 */
@ManagedBean
@ViewScoped
public class FinalizarAprobacionRequisitosTecnicosBean implements Serializable {

	/**
	* 
	*/
	private static final long serialVersionUID = -4595034866077116442L;

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(FinalizarAprobacionRequisitosTecnicosBean.class);

	@EJB
	private ProcesoFacade procesoFacade;

	@EJB
	private AprobacionRequisitosTecnicosFacade aprobacionRequisitosTecnicosFacade;

	@EJB
	ValidacionSeccionesFacade validacionSeccionesFacade;

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
	private AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos;

	@PostConstruct
	public void cargarDatos() {
		cargarDatosBandeja();
	}

	private void cargarDatosBandeja() {
		try {
			this.aprobacionRequisitosTecnicos = aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos();
			if (aprobacionRequisitosTecnicos == null) {
				JsfUtil.addMessageError(JsfUtil.ERROR_INICIALIZAR_DATOS);
			}
		} catch (Exception e) {
			LOG.error("Error al cargar los datos de la aprobación de requisitos técnicos.", e);
		}

	}

	public String enviarAprobacionRequisitosTecnicos() {
		try {
			procesoFacade.aprobarTarea(loginBean.getUsuario(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getTarea().getProcessInstanceId(), null);
			JsfUtil.addMessageInfo("Se realizó correctamente la operación.");
			return JsfUtil.actionNavigateTo("/bandeja/bandejaTareas.jsf");
		} catch (JbpmException e) {
			LOG.error(e);
			JsfUtil.addMessageError("Ocurrió un error al enviar la información.");
		}
		return "";
	}

	// public void validarTareaBpm() {
	// JsfUtil.validarPaginasUrlTareasBpm(bandejaTareasBean.getTarea(),
	// "/prevencion/licenciamiento-ambiental/eia/resumenEjecutivo/resumenEjecutivo.jsf");
	// }
}
