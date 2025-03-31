package ec.gob.ambiente.prevencion.licenciamientoambiental.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.jbpm.process.audit.ProcessInstanceLog;
import org.kie.api.task.model.TaskSummary;

import ec.gob.ambiente.integracion.facade.IntegracionFacade;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.administracion.service.OrganizacionService;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.builders.TaskSummaryCustomBuilder;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.AreaUsuario;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.dto.ProyectoCustom;
import ec.gob.ambiente.suia.eia.facade.EstudioImpactoAmbientalFacade;
import ec.gob.ambiente.suia.integracion.bean.ContenidoExterno;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class ListarPendientesAprobacionTDRLAController implements Serializable {

	private static final long serialVersionUID = 1572525482381028668L;
	private static final Logger LOG = Logger
			.getLogger(ListarPendientesAprobacionTDRLAController.class);

	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{proyectosBean}")
	private ProyectosBean proyectosBean;

	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;

	@EJB
	private DocumentosFacade documentosFacade;

	@EJB
	private ProcesoFacade procesoFacade;

	@EJB
	private EstudioImpactoAmbientalFacade estudioImpactoAmbientalFacade;
	
	@EJB
	private UbicacionGeograficaFacade ubicacionGeograficaFacade;
	
	@EJB
	protected AreaFacade areaFacade;
	
	@EJB
	private OrganizacionService organizacionService;
	
	private Map<String, Object> processVariables;
	@EJB
	private OrganizacionFacade organizacionFacade;

	@PostConstruct
	public void init() throws Exception {

		List<String> listadoCategoriasMineria = new ArrayList<String>();
		listadoCategoriasMineria.add("21.02.03.01");
		listadoCategoriasMineria.add("21.02.03.02");
		listadoCategoriasMineria.add("21.02.03.03");
		listadoCategoriasMineria.add("21.02.03.04");
		listadoCategoriasMineria.add("21.02.04.01");
		listadoCategoriasMineria.add("21.02.04.02");
		listadoCategoriasMineria.add("21.02.05.01");
		listadoCategoriasMineria.add("21.02.05.02");
		listadoCategoriasMineria.add("21.02.08.01");
		listadoCategoriasMineria.add("21.02.07.01");
		listadoCategoriasMineria.add("21.02.07.02");
		listadoCategoriasMineria.add("21.02.06.01");
		listadoCategoriasMineria.add("21.02.06.03");
		
		proyectosBean.setProyectos(new ArrayList<ProyectoCustom>());
		
		List<Area> areasUsuario = new ArrayList<Area>();
		
		for(AreaUsuario areaUsuario : JsfUtil.getLoggedUser().getListaAreaUsuario()){
			if(areaUsuario.getArea().getAreaAbbreviation().equals(Constantes.SIGLAS_DIRECCION_REGULARIZACION_AMBIENTAL)) {
				areasUsuario.add(areaUsuario.getArea());
			} else {
				Area areaPadre = areaFacade.getareaPafre(areaUsuario.getArea().getId());
				if(areaPadre.getId() != null)
					areasUsuario.add(areaPadre);
				else
					areasUsuario.add(areaUsuario.getArea());			
			}
		}
		
		String listaTipos = "("+TipoDocumentoSistema.TIPO_RESPALDOS_APROBACION_TDRS.getIdTipoDocumento()+")";
		
		if(!areasUsuario.isEmpty()){
			for(Area areaConsulta : areasUsuario){
				List<ProyectoCustom> proyectosMineria = proyectoLicenciamientoAmbientalFacade
						.listarProyectosMineriaPendienteTdr(areaConsulta, listadoCategoriasMineria, listaTipos);
				
				proyectosBean.getProyectos().addAll(proyectosMineria);
			}
		}
	}

	public String seleccionar(ProyectoCustom proyectoCustom) {
		switch (proyectoCustom.getSourceType()) {
		case ProyectoCustom.SOURCE_TYPE_INTERNAL:
			try {
				ProyectoLicenciamientoAmbiental proyecto = proyectoLicenciamientoAmbientalFacade
						.buscarProyectosLicenciamientoAmbientalPorId(Integer
								.parseInt(proyectoCustom.getId()));
				
				proyectosBean.setProyecto(proyecto);
				
				String proponente = null;
				UbicacionesGeografica ubicacionesGeografica = null;
				if (proyecto.getUsuario().getPersona().getOrganizaciones().size() == 0) {
					proponente = proyecto.getUsuario().getPersona().getNombre();
					ubicacionesGeografica = ubicacionGeograficaFacade.ubicacionCompleta(proyecto.getUsuario().getPersona().getIdUbicacionGeografica());
				} else {
					String org =proyecto.getUsuario().getNombre();
					Organizacion orgd= organizacionFacade.buscarPorRuc(org);
					proponente = orgd.getNombre();					
//					proponente = proyecto.getUsuario().getPersona().getOrganizaciones().get(0).getNombre();
					ubicacionesGeografica = ubicacionGeograficaFacade.ubicacionCompleta(proyecto.getUsuario().getPersona().getOrganizaciones().get(0).getIdUbicacionGeografica());
				}
				proyectosBean.setProponente(proponente);
				proyectosBean.getUbicacionProponente().clear();
				proyectosBean.getUbicacionProponente().add(ubicacionesGeografica);
				
				List<ProcessInstanceLog> procesosTramite = procesoFacade.getProcessInstancesLogsVariableValue(JsfUtil.getLoggedUser(),
						Constantes.ID_PROYECTO, proyecto.getId().toString());
				Integer valorProces= 0;
				String nombreProceso=null;
				TaskSummary tareaActualaux=null;
				for (ProcessInstanceLog processLog : procesosTramite) {
					if(!processLog.getStatus().equals(4) 
							&& (processLog.getProcessId().equals(Constantes.NOMBRE_PROCESO_LICENCIA_AMBIENTAL) ||
							processLog.getProcessId().equals(Constantes.NOMBRE_PROCESO_PARTICIPACION_SOCIAL))){
						Long processInstanceId = processLog.getProcessInstanceId();
						List<TaskSummary> listaTareas = procesoFacade.getTaskReservedInProgress(JsfUtil.getLoggedUser(), processInstanceId);
						if(listaTareas != null && listaTareas.size() > 0) {
							for (TaskSummary tareaActual : listaTareas) {
								processVariables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), processInstanceId);
								if (processInstanceId.intValue() > valorProces) {
									valorProces = processInstanceId.intValue();
									tareaActualaux = tareaActual;
								}
								nombreProceso = processLog.getProcessName();
								// break;
							}
						}
						
					}
				}
				bandejaTareasBean.setTarea(new TaskSummaryCustomBuilder().fromSuiaIII(processVariables,
						nombreProceso, tareaActualaux).build()); 
				bandejaTareasBean.setProcessId(tareaActualaux.getProcessInstanceId());
				

				return JsfUtil
						.actionNavigateTo("/prevencion/licenciamiento-ambiental/documentosAprobacionTDR.jsf");
			} catch (Exception e) {
				LOG.error(e.getMessage());
				e.printStackTrace();
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
				return null;
			}

		case ProyectoCustom.SOURCE_TYPE_EXTERNAL_HIDROCARBUROS:
		case ProyectoCustom.SOURCE_TYPE_EXTERNAL_SUIA:

			JsfUtil.getBean(ContenidoExterno.class).executeAction(
					proyectoCustom,
					IntegracionFacade.IntegrationActions.mostrar_dashboard);

			break;
		}

		return null;
	}
}
