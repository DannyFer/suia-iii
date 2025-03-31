package ec.gob.ambiente.suia.bandeja.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import ec.gob.ambiente.suia.bandeja.BandejaFacade;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.dto.TaskSummaryCustom;
import ec.gob.ambiente.suia.utils.BeanLocator;
import ec.gob.ambiente.suia.utils.Constantes;

public class LazyDataModelTareas extends LazyDataModel <TaskSummaryCustom>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	BandejaFacade bandejaFacade = (BandejaFacade) BeanLocator.getInstance(BandejaFacade.class);
	
	private String actorId;
	private Usuario usuario;
	
	public LazyDataModelTareas(String actorId, Usuario usuario)
	{
		this.actorId=actorId;
		this.usuario=usuario;
	}
	
	public List<TaskSummaryCustom> load(int inicio, int limit, String sortField, SortOrder sortOrder, Map<String,Object> filters) {		
		
		Long totalContarIVCategorias =bandejaFacade.countTareasIVCategorias(filters,usuario,actorId);
		Integer totalContarSuiaIII = bandejaFacade.countTareasSuiaIII(filters,usuario,actorId);
		Integer totalContarHidrocarburos1 = bandejaFacade.countTareasHidrocarburosPorUsuario(filters,usuario,actorId);
		Integer totalContarHidrocarburos = 0;
		Integer totalContarHidrocarburosRol = 0;
//		boolean tareasHidroPorUsuario=true;
//		if(totalContarHidrocarburos==0){
//			totalContarHidrocarburos = bandejaFacade.countTareasHidrocarburosPorRoles(filters,usuario,actorId);
//			tareasHidroPorUsuario=false;
//		}
		
		
		totalContarHidrocarburosRol = bandejaFacade.countTareasHidrocarburosPorRoles(filters,usuario,actorId);
		totalContarHidrocarburos=totalContarHidrocarburos1+totalContarHidrocarburosRol;
		
		
			
		this.setRowCount(Integer.valueOf(totalContarIVCategorias.toString())+totalContarSuiaIII+totalContarHidrocarburos);
		
		List<TaskSummaryCustom> tasksByUserTotal = new ArrayList<TaskSummaryCustom>();
		List<TaskSummaryCustom> tasksByUserSuiaIII = new ArrayList<TaskSummaryCustom>();
		List<TaskSummaryCustom> tasksByUserIVCategorias = new ArrayList<TaskSummaryCustom>();
		List<TaskSummaryCustom> tasksByUserHidrocarburos = new ArrayList<TaskSummaryCustom>();
		try {
			
			 if(totalContarIVCategorias > 0 && inicio <= totalContarIVCategorias){
				 tasksByUserIVCategorias= bandejaFacade.getTasksIVCategorias(inicio,limit,filters,actorId,usuario);
				 tasksByUserTotal.addAll(tasksByUserIVCategorias);
			 }

			if(totalContarSuiaIII>0 &&  tasksByUserTotal.size()<limit ){
				Integer inicioSuiaIII=(inicio - Integer.valueOf(totalContarIVCategorias.toString()));
				if(inicioSuiaIII<0){
					inicioSuiaIII=0;
				}
				
				tasksByUserSuiaIII=bandejaFacade.getTasksSuiaIII(inicioSuiaIII, limit-tasksByUserTotal.size(), filters,actorId, usuario);
				
				String tareaDescargaGuias = Constantes.TASK_NAME_DESCARGA_GUIAS_ESIA;
				for (TaskSummaryCustom tarea : tasksByUserSuiaIII) {
					if(tarea.getProcessId().compareTo(Constantes.RCOA_ESTUDIO_IMPACTO_AMBIENTAL)==0 
							&& tarea.getTaskName().toUpperCase().equals(tareaDescargaGuias.toUpperCase())) {
						tarea.setTaskName(Constantes.NEW_TASK_NAME_DESCARGA_GUIAS_ESIA);
						tarea.setTaskNameHuman(Constantes.NEW_TASK_NAME_DESCARGA_GUIAS_ESIA);
					}
					if(tarea.getProcessId().compareTo(Constantes.RCOA_ESTUDIO_IMPACTO_AMBIENTAL_v2)==0 
							&& tarea.getTaskName().toUpperCase().equals(tareaDescargaGuias.toUpperCase())) {
						tarea.setTaskName(Constantes.NEW_TASK_NAME_DESCARGA_GUIAS_ESIA);
						tarea.setTaskNameHuman(Constantes.NEW_TASK_NAME_DESCARGA_GUIAS_ESIA);
					}
				}
				
				tasksByUserTotal.addAll(tasksByUserSuiaIII);
			}
			
			if(totalContarHidrocarburos>0 &&  tasksByUserTotal.size()<limit ){
				Integer inicioHidroCarburos=(inicio - totalContarSuiaIII);
				if(inicioHidroCarburos<0){
					inicioHidroCarburos=0;
				}
//				if(tareasHidroPorUsuario){
//					tasksByUserHidrocarburos=bandejaFacade.getTasksHidrocarburosPorUsuario(inicioHidroCarburos, limit-tasksByUserTotal.size(), filters, actorId, usuario);
//				}else {
//					tasksByUserHidrocarburos=bandejaFacade.getTasksHidrocarburosPorRoles(inicioHidroCarburos, limit-tasksByUserTotal.size(), filters, actorId, usuario);
//				}
				
				if(totalContarHidrocarburos1>0)
					tasksByUserHidrocarburos=bandejaFacade.getTasksHidrocarburosPorUsuario(inicioHidroCarburos, limit-tasksByUserTotal.size(), filters, actorId, usuario);
				if(totalContarHidrocarburosRol>0)
					tasksByUserHidrocarburos=bandejaFacade.getTasksHidrocarburosPorRoles(inicioHidroCarburos, limit-tasksByUserTotal.size(), filters, actorId, usuario);
				
				tasksByUserTotal.addAll(tasksByUserHidrocarburos);
			}			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tasksByUserTotal;
        
	}

}
