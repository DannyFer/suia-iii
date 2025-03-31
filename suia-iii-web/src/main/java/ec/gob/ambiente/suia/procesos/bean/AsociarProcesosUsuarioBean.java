/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.procesos.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.jbpm.process.audit.ProcessInstanceLog;

import ec.gob.ambiente.suia.dto.DefinicionProceso;
import ec.gob.ambiente.suia.dto.ResumenInstanciaProceso;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.webservicesclientes.facade.JbpmSuiaCustomServicesFacade;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author carlos.pupo
 * @version Revision: 1.0
 *          <p>
 *          [Autor: carlos.pupo, Fecha: 31/08/2015]
 *          </p>
 */
@ManagedBean
@ViewScoped
public class AsociarProcesosUsuarioBean implements Serializable {

	private static final long serialVersionUID = -5336977660064571036L;

	@EJB
	private ProcesoFacade procesoFacade;

	@EJB
	private JbpmSuiaCustomServicesFacade jbpmSuiaCustomServicesFacade;

	@Getter
	private List<DefinicionProceso> procesos;

	@Getter
	private List<ResumenInstanciaProceso> instanciasProcesos;

	@Getter
	@Setter
	private DefinicionProceso definicionProceso;

	@Getter
	@Setter
	private ResumenInstanciaProceso resumenInstanciaProceso;

	@Getter
	@Setter
	private String usuario;

	private static final Logger LOG = Logger.getLogger(AsociarProcesosUsuarioBean.class);

	@PostConstruct
	public void init() {
		try {
			procesos = jbpmSuiaCustomServicesFacade.listaDefinicionesProcesos();
		} catch (Exception exception) {
			JsfUtil.addMessageError("Error al cargar el resumen de procesos.");
			LOG.error("No se puede obtener el resumen de procesos", exception);
		}
	}

	public void seleccionarProceso(DefinicionProceso definicionProceso) {
		this.definicionProceso = definicionProceso;
		updateProcesos();
	}

	public void seleccionarInstanciaProceso(ResumenInstanciaProceso resumenInstanciaProceso) {
		this.resumenInstanciaProceso = resumenInstanciaProceso;
		this.usuario = null;
	}

	public void asociar() {
		try {
			procesoFacade.asociarProcesoUsuarioNombre(JsfUtil.getLoggedUser(), usuario,
					resumenInstanciaProceso.getProcessInstanceId());
		} catch (Exception e) {
			LOG.error("Error modificando variables del proceso", e);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
		updateProcesos();
	}

	private void updateProcesos() {
		try {
			instanciasProcesos = new ArrayList<ResumenInstanciaProceso>();

			List<ProcessInstanceLog> instancias = procesoFacade.getActiveProcessInstances(JsfUtil.getLoggedUser(),
					definicionProceso.getId());

			Map<Long, String> variablesUsuario = procesoFacade.getProcessInstancesIdsVariableValue(
					JsfUtil.getLoggedUser(), Constantes.USUARIO_VISTA_MIS_PROCESOS, false);

			Map<Long, String> variablesTramite = procesoFacade.getProcessInstancesIdsVariableValue(
					JsfUtil.getLoggedUser(), Constantes.VARIABLE_PROCESO_TRAMITE, false);

			for (ProcessInstanceLog log : instancias) {
				String usuarioAsociado = null;
				if (variablesUsuario.containsKey(log.getProcessInstanceId()))
					usuarioAsociado = variablesUsuario.get(log.getProcessInstanceId());

				String tramite = "(Desconocido)";
				if (variablesTramite.containsKey(log.getProcessInstanceId()))
					tramite = variablesTramite.get(log.getProcessInstanceId());

				ResumenInstanciaProceso proceso = new ResumenInstanciaProceso(log, tramite);
				proceso.setUsuarioAsociado(usuarioAsociado);
				instanciasProcesos.add(proceso);
			}
		} catch (Exception e) {
			LOG.error("Error cargando las instancias del proceso.", e);
			JsfUtil.addMessageError("Error cargando instancias del proceso.");
		}
	}
}
