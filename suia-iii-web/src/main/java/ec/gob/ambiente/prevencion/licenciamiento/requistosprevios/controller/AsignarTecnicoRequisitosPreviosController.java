package ec.gob.ambiente.prevencion.licenciamiento.requistosprevios.controller;

import java.util.Map;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import lombok.Setter;

import org.apache.log4j.Logger;

import ec.gob.ambiente.core.interfaces.CompleteOperation;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.comun.bean.ReasignarTareaComunBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
public class AsignarTecnicoRequisitosPreviosController {

	private static final Logger LOGGER = Logger.getLogger(AsignarTecnicoRequisitosPreviosController.class);

	@EJB
	private ProcesoFacade procesoFacade;

	@EJB
	private AreaFacade areaFacade;

	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;

	@Setter
	@ManagedProperty(value = "#{reasignarTareaComunBean}")
	private ReasignarTareaComunBean reasignarTareaComunBean;

	public void delegarTecnico() {

		try {
			Map<String, Object> variables = procesoFacade
					.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getProcessInstanceId());
			String tipoAreaProyecto = (String) variables.get("tipoAreaProyecto");
			String roleType = "role.pc.tecnico";
			String subarea = null;
			if (!tipoAreaProyecto.equalsIgnoreCase(Constantes.SIGLAS_TIPO_AREA_PC)) {
				roleType = "role.area.tecnico";
				Integer areaProyectoId = Integer.parseInt((String) variables.get("areaProyectoId"));
				Area areaProyecto = areaFacade.getArea(areaProyectoId);
//				subarea = new Area[1];
//				subarea[0] = areaProyecto;
				subarea = areaProyecto.getAreaName();
			}

			reasignarTareaComunBean.initFunctionOnNotStatartedTask(bandejaTareasBean.getTarea().getProcessInstanceId(),
					"Delegar a técnico para analizar requisitos previos al licenciamiento", "tecnico",
					Constantes.getRoleAreaName(roleType), subarea, null, new CompleteOperation() {
						public Object endOperation(Object object) {
							try {
								procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean
										.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
							} catch (JbpmException ex) {
								LOGGER.error(ex);
							}
							return null;
						}
					});
		} catch (JbpmException e) {
			LOGGER.error("Error al recuperar los datos del proyecto.", e);
		}
	}
}
