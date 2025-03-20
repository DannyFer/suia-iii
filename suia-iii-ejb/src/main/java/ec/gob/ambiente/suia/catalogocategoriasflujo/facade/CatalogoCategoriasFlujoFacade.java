package ec.gob.ambiente.suia.catalogocategoriasflujo.facade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.jbpm.process.audit.ProcessInstanceLog;

import ec.gob.ambiente.suia.catalogocategorias.service.CatalogoCategoriasServiceBean;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.CategoriaFlujo;
import ec.gob.ambiente.suia.domain.PerforacionExplorativa;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.prevencion.categoria2.ficha.mineria.facade.FichaAmbientalMineria020Facade;
import ec.gob.ambiente.suia.prevencion.requisitosPrevios.RequisitosPreviosLicenciaFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.EstadoProceso;

@Stateless
public class CatalogoCategoriasFlujoFacade {

	@EJB
	private CrudServiceBean crudServiceBean;

	@EJB
	private ProcesoFacade procesoFacade;

	@EJB
	private CatalogoCategoriasServiceBean catalogoCategoriasServiceBean;

    @EJB
    private RequisitosPreviosLicenciaFacade requisitosPreviosLicenciaFacade;
    
    @EJB
	private FichaAmbientalMineria020Facade fichaAmbientalmineria020facade;

	public List<CategoriaFlujo> obtenerFlujosDeProyectoPorCategoria(ProyectoLicenciamientoAmbiental proyecto,
			String nombreVariableProceso, Usuario usuario) throws Exception {
		int idCategoria = proyecto.getCatalogoCategoria().getCategoria().getId();
		String valorVariableProceso = proyecto.getId().toString();

		List<ProcessInstanceLog> listProcessProject = procesoFacade.getProcessInstancesLogsVariableValue(usuario, nombreVariableProceso,
				valorVariableProceso);

		int iteradorListProcessProject;
		
		Collections.sort(listProcessProject, new Comparator<ProcessInstanceLog>() {
			@Override
			public int compare(ProcessInstanceLog o1, ProcessInstanceLog o2) {
				return new Long(o1.getProcessInstanceId()).compareTo(new Long(o2.getProcessInstanceId()));
			}
		});
		List<CategoriaFlujo> categoriasflujos2 = new ArrayList<CategoriaFlujo>();

		List<CategoriaFlujo> categoriasflujos = obtenerCategoriasFlujosPorIdCategoria(idCategoria);
		if (categoriasflujos != null) {
			validarCondicionesMostrarFlujo(categoriasflujos, proyecto);

			for (int i = 0; i < categoriasflujos.size(); i++) {
				iteradorListProcessProject = 0;
				if (listProcessProject != null) {
					for (int j = 0; j < listProcessProject.size(); j++) {
						iteradorListProcessProject = j;
						if (listProcessProject.get(j).getStatus() != 4) {
							if (categoriasflujos.get(i).getFlujo().getIdProceso().equals(listProcessProject.get(j).getProcessId())) {
								categoriasflujos.get(i).getFlujo().setEstadoProceso(EstadoProceso.getNombreEstado(listProcessProject.get(j).getStatus()));
								categoriasflujos.get(i).getFlujo().setProcessInstanceId(listProcessProject.get(j).getProcessInstanceId());
							}
						}
					}
				}

				if (listProcessProject == null || listProcessProject.isEmpty()
						|| iteradorListProcessProject == listProcessProject.size() - 1) {
					if (categoriasflujos.get(i).getFlujo().getEstadoProceso() == null
							|| categoriasflujos.get(i).getFlujo().getEstadoProceso().equals("")) {
						categoriasflujos.get(i).getFlujo().setEstadoProceso(Constantes.ESTADO_PROCESO_NO_INICIADO);
						categoriasflujos.get(i).getFlujo().setIniciaFlujo(true);
					} /*else if (categoriasflujos.get(i).getFlujo().getEstadoProceso()
							.equals(Constantes.ESTADO_PROCESO_ABORTADO))
						categoriasflujos.get(i).getFlujo().setIniciaFlujo(true);*/
					else {
						categoriasflujos.get(i).getFlujo().setIniciaFlujo(false);
					}
				}
			}
			
			for(CategoriaFlujo flujo : categoriasflujos)
			{
				if(!flujo.getFlujo().getEstadoProceso().equals(Constantes.ESTADO_PROCESO_NO_INICIADO))
					categoriasflujos2.add(flujo);
			}
		}
		return categoriasflujos2;
	}

	public void validarCondicionesMostrarFlujo(List<CategoriaFlujo> categoriasflujos,
			ProyectoLicenciamientoAmbiental proyecto) {
		for (int i = 0; i < categoriasflujos.size(); i++) {
			if (categoriasflujos.get(i).getFlujo().getIdProceso()
					.equals(Constantes.NOMBRE_PROCESO_REQUISITOS_PREVIOS_LICENCIAMIENTO)
					&& !proyecto.getGestionaDesechosPeligrosos()
					&& !proyecto.getTransporteSustanciasQuimicasPeligrosos())
				categoriasflujos.remove(categoriasflujos.get(i));
		}
	}

	@SuppressWarnings("unchecked")
	public List<CategoriaFlujo> obtenerCategoriasFlujosPorIdCategoria(int categoriaId) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("categoriaId", categoriaId);
		List<CategoriaFlujo> result = (List<CategoriaFlujo>) crudServiceBean.findByNamedQuery(
				CategoriaFlujo.FIND_BY_CATEGORIA, parameters);
		return result;
	}
	
	public List<CategoriaFlujo> obtenerFlujosDeProyectoRcoaPorCategoria(Integer idProyecto, Integer idCategoria,
			String nombreVariableProceso, Usuario usuario, String codigoProyecto) throws Exception {
		String valorVariableProceso = idProyecto.toString();

		List<ProcessInstanceLog> listProcessProject = procesoFacade.getProcessInstancesLogsVariableValue(usuario, nombreVariableProceso,
				valorVariableProceso);
		
		List<ProcessInstanceLog> listProcessProjectAuxiliar = procesoFacade.getProcessInstancesLogsVariableValue(usuario, "u_" + nombreVariableProceso,
				valorVariableProceso);
		
		listProcessProject.addAll(listProcessProjectAuxiliar);

		int iteradorListProcessProject;
		
		Collections.sort(listProcessProject, new Comparator<ProcessInstanceLog>() {
			@Override
			public int compare(ProcessInstanceLog o1, ProcessInstanceLog o2) {
				return new Long(o1.getProcessInstanceId()).compareTo(new Long(o2.getProcessInstanceId()));
			}
		});
		List<CategoriaFlujo> categoriasflujos2 = new ArrayList<CategoriaFlujo>();
		
		List<CategoriaFlujo> categoriasflujos = obtenerCategoriasFlujosRcoaPorIdCategoria(idCategoria);
		if (categoriasflujos != null) {

			for (int i = 0; i < categoriasflujos.size(); i++) {
				iteradorListProcessProject = 0;
				if (listProcessProject != null) {
					for (int j = 0; j < listProcessProject.size(); j++) {
						iteradorListProcessProject = j;
						if (listProcessProject.get(j).getStatus() != 4) {
							if (categoriasflujos.get(i).getFlujo().getIdProceso()
									.equals(listProcessProject.get(j).getProcessId())) {
								boolean flujoValido = true;
								if(listProcessProject.get(j).getProcessId().equals("rcoa.RegistrodeGeneradordeDesechosPeligrososyEspeciales")) {
									Map<String, Object> variables = procesoFacade.recuperarVariablesProceso(usuario, listProcessProject.get(j).getProcessInstanceId());
									String tramite = (String) variables.get("tramite");
									if(tramite != null)
										flujoValido = (tramite.equals(codigoProyecto)) ? true : false;
								}
								categoriasflujos
										.get(i)
										.getFlujo()
										.setFlujoValido(flujoValido);

								categoriasflujos
										.get(i)
										.getFlujo()
										.setEstadoProceso(
												EstadoProceso.getNombreEstado(listProcessProject.get(j).getStatus()));
								categoriasflujos.get(i).getFlujo()
										.setProcessInstanceId(listProcessProject.get(j).getProcessInstanceId());
								categoriasflujos.get(i).getFlujo().setVerDocumentos(verificarSDFinalizado(categoriasflujos.get(i).getFlujo().getNombreFlujo(), idProyecto));
							}
						}
					}
				}

				if (listProcessProject == null || listProcessProject.isEmpty()
						|| iteradorListProcessProject == listProcessProject.size() - 1) {
					if (categoriasflujos.get(i).getFlujo().getEstadoProceso() == null
							|| categoriasflujos.get(i).getFlujo().getEstadoProceso().equals("")) {
						categoriasflujos.get(i).getFlujo().setEstadoProceso(Constantes.ESTADO_PROCESO_NO_INICIADO);
						categoriasflujos.get(i).getFlujo().setIniciaFlujo(true);
					} /*else if (categoriasflujos.get(i).getFlujo().getEstadoProceso()
							.equals(Constantes.ESTADO_PROCESO_ABORTADO))
						categoriasflujos.get(i).getFlujo().setIniciaFlujo(true);*/
					else {
						categoriasflujos.get(i).getFlujo().setIniciaFlujo(false);
					}
				}
			}
			
			
			for(CategoriaFlujo flujo : categoriasflujos)
			{
				if(!flujo.getFlujo().getEstadoProceso().equals(Constantes.ESTADO_PROCESO_NO_INICIADO)) {
					if(flujo.getFlujo().getFlujoValido() == null || flujo.getFlujo().getFlujoValido() )
						categoriasflujos2.add(flujo);
				}
			}
		}		
		return categoriasflujos2;
	}
	
	@SuppressWarnings("unchecked")
	public List<CategoriaFlujo> obtenerCategoriasFlujosRcoaPorIdCategoria(int categoriaId) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("categoriaId", categoriaId);
		List<CategoriaFlujo> result = (List<CategoriaFlujo>) crudServiceBean.findByNamedQuery(
				CategoriaFlujo.FIND_RCOA_BY_CATEGORIA, parameters);
		return result;
	}
	
	
	public boolean verificarSDFinalizado(String flujo, Integer idProyecto) throws ServiceException
	{
		boolean estado=true;
		if (flujo.equals("Registro ambiental SD")){
			PerforacionExplorativa perforacionExplorativa= new PerforacionExplorativa();
			perforacionExplorativa= fichaAmbientalmineria020facade.cargarPerforacionExplorativaRcoa(idProyecto);

			if (perforacionExplorativa.getApproveTechnical()==null){
				perforacionExplorativa.setApproveTechnical(false);
			}
			if (perforacionExplorativa.getFinalized()==null){
				perforacionExplorativa.setFinalized(false);
			}
			if ((perforacionExplorativa.getFinalized() && perforacionExplorativa.getApproveTechnical())){
				estado=true;
			}else{
				estado=false;
			}
		}
		return estado;
	}
	
}