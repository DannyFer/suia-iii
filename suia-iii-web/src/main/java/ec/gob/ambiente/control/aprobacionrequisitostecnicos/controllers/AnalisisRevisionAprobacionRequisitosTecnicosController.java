package ec.gob.ambiente.control.aprobacionrequisitostecnicos.controllers;

import java.io.Serializable;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.control.aprobacionrequisitostecnicos.bean.AprobacionRequisitosTecnicosBean;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.AprobacionRequisitosTecnicosFacade;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.AprobacionRequisitosTecnicos;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.validacionseccion.facade.ValidacionSeccionesFacade;

/***
 * 
 * <b> Incluir aqui la descripcion de la clase. </b>
 * 
 * @author vero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: vero $, $Date: 23/07/2015 $]
 *          </p>
 */
@ManagedBean
@ViewScoped
public class AnalisisRevisionAprobacionRequisitosTecnicosController implements Serializable {

	/**
	* 
	*/
	private static final long serialVersionUID = -4595034866077116442L;

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(AnalisisRevisionAprobacionRequisitosTecnicosController.class);

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

	public String enviarAprobacionRequisitosTecnicos() {
		try {
			aprobacionRequisitosTecnicosFacade.enviarAprobacionRequisitosTecnicosRevision(bandejaTareasBean.getTarea()
					.getProcessInstanceId(), loginBean.getUsuario(), bandejaTareasBean.getTarea().getTaskId(),
					aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos().getProyecto());
			JsfUtil.addMessageInfo("Se realizó correctamente la operación.");
			return JsfUtil.actionNavigateTo("/bandeja/bandejaTareas.jsf");
		} catch (JbpmException e) {
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al completar la tarea en el bpm.");
		} catch (ServiceException e) {
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar el técnico responsable.");
		}
		return "";
	}
}
