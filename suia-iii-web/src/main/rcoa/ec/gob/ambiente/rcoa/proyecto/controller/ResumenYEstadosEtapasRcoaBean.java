/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.rcoa.proyecto.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;

import org.apache.log4j.Logger;
import org.jfree.util.Log;
import org.kie.api.task.model.TaskSummary;

import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.model.CatalogoCIUU;
import ec.gob.ambiente.rcoa.model.CoordenadasProyecto;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.ValorMagnitud;
import ec.gob.ambiente.rcoa.util.CoordendasPoligonos;
import ec.gob.ambiente.suia.catalogocategoriasflujo.facade.CatalogoCategoriasFlujoFacade;
import ec.gob.ambiente.suia.comparator.OrdenarTareaPorEstadoComparator;
import ec.gob.ambiente.suia.domain.CategoriaFlujo;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.SustanciaQuimicaPeligrosa;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.dto.ResumeTarea;
import ec.gob.ambiente.suia.dto.Tarea;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.BeanLocator;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.ConvertidorObjetosDominioUtil;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.webservicesclientes.facade.JbpmSuiaCustomServicesFacade;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Daniel Porras
 */
public class ResumenYEstadosEtapasRcoaBean implements Serializable {
    
    private static final long serialVersionUID = -227940254655409170L;
    static final Logger LOGGER = Logger.getLogger(ResumenYEstadosEtapasRcoaBean.class);
    static final String COMPLETADO = "Completado";
    static final String COMPLETADA = "Completada";
    static final String ENCURSO = "En curso";
    static final String ACTIVO = "Activo";
    static final String NOFAVORABLE = "No Favorable";
    
    @EJB
    CatalogoCategoriasFlujoFacade catalogoCategoriasFlujoFacade;
    @EJB
    ProcesoFacade procesoFacade;
    @EJB
	ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	@EJB
	UsuarioFacade usuarioFacade;

	@Getter
	@Setter
	CatalogoCIUU ciiuPrincipal;
	@Getter
	@Setter
	CatalogoCIUU ciiuComplementaria1;
	@Getter
	@Setter
	CatalogoCIUU ciiuComplementaria2;
	@Getter
	@Setter
	ValorMagnitud valorMagnitud1;
	@Getter
	@Setter
	ValorMagnitud valorMagnitud2;
	@Getter
	@Setter
	ValorMagnitud valorMagnitud3;
	@Getter
	@Setter
	List<UbicacionesGeografica> ubicacionesSeleccionadas;
	@Getter
	@Setter
	List<CoordenadasProyecto> coordenadasGeograficas;
	@Getter
    @Setter
    List<CoordendasPoligonos> coordinatesWrappers;
	@Getter
	ProyectoLicenciaCoa proyectoLicenciaCoa;
	
	@Getter
    @Setter
    List<CategoriaFlujo> flujos;
    @Getter
    @Setter
    List<Tarea> tareas;
    @Getter
    @Setter
    List<Documento> documentos;
    @Getter
    @Setter
    boolean proyectoFinalizado=false;
    @Getter
	@Setter
	List<SustanciaQuimicaPeligrosa> sustanciaQuimicaSeleccionada;


    /**
     * <b> Metodo que valida si l bpm obtiene las tareas. </b>
     * <p>
     * [Author: Javier Lucero, Date: 19/05/2015]
     * </p>
     */
    public void validarBpm() {
        if (getFlujos() == null || getFlujos().isEmpty()) {
            JsfUtil.addMessageInfo("No se puede obtener el resumen de Tareas, comuniquese con mesa de ayuda.");
        }

    }

	public void getFlujosProyecto() {
		try {
			flujos = catalogoCategoriasFlujoFacade
					.obtenerFlujosDeProyectoRcoaPorCategoria(
							proyectoLicenciaCoa.getId(), proyectoLicenciaCoa.getCategorizacion(),
							Constantes.ID_PROYECTO, JsfUtil.getLoggedUser(), proyectoLicenciaCoa.getCodigoUnicoAmbiental());
			
			Integer flujosActivos = 0;
			for (CategoriaFlujo cf : flujos) {
                if (cf.getFlujo().getEstadoProceso().equals(ACTIVO)) { 
					flujosActivos++;}
            }
			
			if(flujosActivos > 1) {
				for (CategoriaFlujo cf : flujos) {
					if (cf.getFlujo().getEstadoProceso().equals(ACTIVO)) {
						cf.getFlujo().setEstadoProceso(ENCURSO);
						if(cf.getFlujo().getIdProceso().equals(Constantes.RCOA_REGISTRO_PRELIMINAR))
							cf.getFlujo().setEstadoProceso(COMPLETADO);
	                }
	            }
			} else {
				boolean existePPCCompletado = false;
				for (CategoriaFlujo cf : flujos) {
					if(cf.getFlujo().getIdProceso().equals(Constantes.RCOA_REGISTRO_AMBIENTAL_PPC) && cf.getFlujo().getEstadoProceso().equals(COMPLETADO)){
						existePPCCompletado = true;
						break;
					}
				}				
				
				for (CategoriaFlujo cf : flujos) {
	                if (cf.getFlujo().getEstadoProceso().equals(ACTIVO)) {
	                	if(cf.getFlujo().getIdProceso().equals(Constantes.RCOA_REGISTRO_PRELIMINAR) && existePPCCompletado){
	                		cf.getFlujo().setEstadoProceso(COMPLETADO);
	                	}else{
	                		cf.getFlujo().setEstadoProceso(ENCURSO);
	                	}
	                }

	                if((cf.getFlujo().getIdProceso().equals("rcoa.ViabilidadAmbiental") || cf.getFlujo().getIdProceso().equals("rcoa.ViabilidadAmbientalBypass")) 
	                		&& cf.getFlujo().getEstadoProceso().equals(COMPLETADO)) {
	                	Map<String, Object> variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), cf.getFlujo().getProcessInstanceId());
	                	Boolean resultado = variables.containsKey("viabilidadAmbientalFavorable") ? (Boolean.valueOf((String) variables.get("viabilidadAmbientalFavorable"))) : null;
	        			if(resultado != null && !resultado)
	        				cf.getFlujo().setEstadoProceso(NOFAVORABLE);
	                }
	            }
			}
		} catch (Exception e) {
			Log.debug(e.toString());
		}
	}
	
	public void verTareas(Long processInstanceId, CategoriaFlujo categoriaFlujo) {
		try {
			String estadoProceso = categoriaFlujo.getFlujo().getEstadoProceso();
			List<Tarea> tareas = new ArrayList<Tarea>();
			if (COMPLETADA.equals(estadoProceso)||ACTIVO.equals(estadoProceso)||COMPLETADO.equals(estadoProceso)||("Completado - Observado (3)".equals(estadoProceso))) {
				List<ResumeTarea> resumeTareas = BeanLocator.getInstance(JbpmSuiaCustomServicesFacade.class)
						.getResumenTareas(processInstanceId);
				for (ResumeTarea resumeTarea : resumeTareas) {
					Tarea tarea = new Tarea();
					ConvertidorObjetosDominioUtil.convertirBamTaskSummaryATarea(resumeTarea, tarea);
					tareas.add(tarea);
				}
				Collections.sort(tareas, new OrdenarTareaPorEstadoComparator());

				this.tareas = tareas;

			} else {
				List<TaskSummary> taskSummaries = procesoFacade.getTaskBySelectFlow(JsfUtil.getLoggedUser(), processInstanceId);
				int longitud = taskSummaries.size();
				for (int i = 0; i < longitud; i++) {
					Tarea tarea = new Tarea();
					ConvertidorObjetosDominioUtil.convertirTaskSummaryATarea(taskSummaries.get(i), tarea);
					tareas.add(tarea);
				}

				Collections.sort(tareas, new OrdenarTareaPorEstadoComparator());

				this.tareas = tareas;

			}
			
			if(this.tareas.size() > 0 && (categoriaFlujo.getFlujo().getIdProceso().equals(Constantes.RCOA_ESTUDIO_IMPACTO_AMBIENTAL) || 
					categoriaFlujo.getFlujo().getIdProceso().equals(Constantes.RCOA_ESTUDIO_IMPACTO_AMBIENTAL_v2))) {
				for (Tarea tarea : this.tareas) {
					if(tarea.getNombre().toUpperCase().equals(Constantes.TASK_NAME_DESCARGA_GUIAS_ESIA.toUpperCase())) {
						tarea.setNombre(Constantes.NEW_TASK_NAME_DESCARGA_GUIAS_ESIA);
						break;
					}
				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}
}
