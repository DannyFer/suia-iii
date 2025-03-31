package ec.gob.ambiente.suia.bandeja.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.process.audit.ProcessInstanceLog;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.rcoa.digitalizacion.facade.AutorizacionAdministrativaAmbientalFacade;
import ec.gob.ambiente.rcoa.digitalizacion.model.AutorizacionAdministrativaAmbiental;
import ec.gob.ambiente.suia.dto.ResumenInstanciaProceso;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.BeanLocator;
import ec.gob.ambiente.suia.utils.JsfUtil;

public class LazyDataListaProcesos extends LazyDataModel <ResumenInstanciaProceso>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4240513462263322007L;
	
	
	ProcesoFacade procesoFacade = (ProcesoFacade) BeanLocator.getInstance(ProcesoFacade.class);
	AutorizacionAdministrativaAmbientalFacade autorizacionAdministrativaAmbientalFacade = (AutorizacionAdministrativaAmbientalFacade) BeanLocator.getInstance(AutorizacionAdministrativaAmbientalFacade.class);

	private String processId;
	private List<Integer> states = new ArrayList<Integer>();
	public LazyDataListaProcesos(String processId,List<Integer> states)
	{
		this.processId=processId;
		this.states=states;
	}
	
public List<ResumenInstanciaProceso> load(int inicio, int limit, String sortField, SortOrder sortOrder, Map<String,Object> filters) {
			
		List<ProcessInstanceLog> processInstances= new ArrayList<ProcessInstanceLog>();
		List<ResumenInstanciaProceso> resumen = new ArrayList<ResumenInstanciaProceso>(); 
		String filtro="";		
		try {
			if (filters.size()>0) {
	            if (filters.containsKey("tramite") && filters.get("tramite").toString().length()>3) {
	            	filtro = filters.get("tramite").toString();
	            	this.setRowCount(procesoFacade.getCountProcessInstanceFilter(JsfUtil.getLoggedUser(), processId, filtro,states));
					Integer page=inicio/limit;
					processInstances=procesoFacade.getProcessInstancesFilterPages(JsfUtil.getLoggedUser(), processId, filtro, page, limit,states);
	            }
	        }
			else
			{	
				this.setRowCount(procesoFacade.getCountProcessInstance(JsfUtil.getLoggedUser(), processId,states));
				Integer page=inicio/limit;
				processInstances=procesoFacade.getProcessInstancesPages(JsfUtil.getLoggedUser(), processId, page, limit,states);
			}
			
			Map<String, Object> variablesProceso = new HashMap<String, Object>();			
			for (ProcessInstanceLog log : processInstances) {				
				variablesProceso=procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), log.getProcessInstanceId());
				String tramite = "(Desconocido)";
				tramite=(String)variablesProceso.get("tramite")==null?tramite:(String)variablesProceso.get("tramite");
				
				Boolean esDigitalizado = false;
				if(log.getStatus().equals(2)){//si el proceso está finalizado se busca en la base de digitalizacion
					AutorizacionAdministrativaAmbiental autorizacionAdministrativa = autorizacionAdministrativaAmbientalFacade.obtenerAAAPorCodigoProyecto(tramite);
					if(autorizacionAdministrativa != null && autorizacionAdministrativa.getAutorizacionAdministrativaAmbiental() != null){
						String tipoAaa = autorizacionAdministrativa.getAutorizacionAdministrativaAmbiental().split("-")[0];
						if(log.getProcessName().toLowerCase().contains(tipoAaa.toLowerCase())) { //para mostrar el digitalizado solo en los procesos principales
							esDigitalizado = true;
						}
					}
				}
				
				ResumenInstanciaProceso proceso = new ResumenInstanciaProceso(log, tramite);
				proceso.setDigitalizado(esDigitalizado);
				resumen.add(proceso);
			}
		} catch (JbpmException e) {
			
		}
		
		return resumen;
        
	}
}
