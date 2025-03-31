package ec.gob.ambiente.suia.control.documentos.service;

import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.InformeTecnico;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;

@Stateless
public class InformeTecnicoService {

	@EJB
	private CrudServiceBean crudServiceBean;

	@EJB
	private ProcesoFacade procesoFacade;

	@EJB(lookup = Constantes.JBPM_EJB_TASK_BEAN)
	private TaskBeanFacade taskBeanFacade;

	public String esEditable(Long idTarea, String nombreUsuario, String password)
			throws JbpmException {
		Map<String, Object> variables = taskBeanFacade.getTaskVariables(
				idTarea, Constantes.getDeploymentId(), nombreUsuario, password,
				Constantes.getUrlBusinessCentral(), Constantes.getRemoteApiTimeout());
		return (String) variables.get("_esEditable");
	}

	public void iniciarProceso(Map<String, Object> parametros,
			Usuario usuario) throws JbpmException {
		procesoFacade.iniciarProceso(usuario, Constantes.NOMBRE_PROCESO_INFORME_TECNICO, "Informe Técnico",
				parametros);
	}

	public InformeTecnico getInformeTecnico(Integer idInformeTecnico) {
		InformeTecnico informeTecnico = crudServiceBean.find(
				InformeTecnico.class, idInformeTecnico);
		return informeTecnico;
	}

	public void guardarInformeTecnico(InformeTecnico informeTecnico) {
		if (informeTecnico.getId() == 0) {
			System.out.println("SERVICE*******Antecedentes: "
					+ informeTecnico.getAntecedente() + " Observaciones: "
					+ informeTecnico.getObservacion());
			crudServiceBean.saveOrUpdate(informeTecnico);
		} else
			crudServiceBean.saveOrUpdate(informeTecnico);
	}
}
