/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.utils;

import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.EstadoTarea;
import ec.gob.ambiente.suia.dto.ResumeTarea;
import ec.gob.ambiente.suia.dto.Tarea;
import ec.gob.ambiente.suia.usuario.service.UsuarioServiceBean;
import ec.gob.ambiente.suia.webservicesclientes.facade.JbpmSuiaCustomServicesFacade;
import org.kie.api.task.model.TaskSummary;

import java.text.SimpleDateFormat;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 *
 * @author mayriliscs
 * @version Revision: 1.0
 *          <p>
 *          [Autor: mayriliscs, Fecha: 26/02/2015]
 *          </p>
 */
public final class ConvertidorObjetosDominioUtil {

    public static Tarea convertirTaskSummaryATarea(TaskSummary taskSummary, Tarea tarea) throws Exception {
        tarea.setId(taskSummary.getId());
        tarea.setNombre(taskSummary.getName());
        tarea.setEstado(EstadoTarea.getNombreEstado(taskSummary.getStatus().name()));
        String idUsuario = taskSummary.getActualOwner() != null ? taskSummary.getActualOwner().getId() : taskSummary.getCreatedBy().getId();
        Usuario usuario = ((UsuarioServiceBean) BeanLocator.getInstance(UsuarioServiceBean.class))
                .buscarUsuarioWithOutFilters(idUsuario);
        if (usuario != null || usuario.getPersona() != null) {
            OrganizacionFacade organizacionFacade = (OrganizacionFacade) BeanLocator.getInstance(OrganizacionFacade.class);
            Organizacion organizacion = organizacionFacade.buscarPorPersona(usuario.getPersona(), usuario.getNombre());
            if(organizacion != null){
                 tarea.setResponsable(organizacion.getNombre());
            }else {
                tarea.setResponsable(usuario.getPersona().getNombre());
                tarea.setUsuarioId(usuario.getNombre());
            }
        } else {
            tarea.setResponsable("");
        }
        taskSummary.getActivationTime();
        tarea.setFechaInicio(taskSummary.getActivationTime());
        tarea.setFechaFin(((JbpmSuiaCustomServicesFacade) BeanLocator.getInstance(JbpmSuiaCustomServicesFacade.class))
                .getFechaFinTarea(taskSummary.getId()));
        return tarea;
    }


    /**
     * <b>
     * Metodo que convierte un resumen de tarea a una tarea.
     * </b>
     * <p>[Author: Javier Lucero, Date: 04/05/2015]</p>
     *
     * @param resumeTarea: resumen de tarea
     * @param tarea        : tarea
     * @return Tarea : tarea
     * @throws Exception: excepcion
     */
    public static Tarea convertirBamTaskSummaryATarea(ResumeTarea resumeTarea, Tarea tarea) throws Exception {
        tarea.setId(Long.valueOf(resumeTarea.getPk()));
        tarea.setNombre(resumeTarea.getTaskName());
        tarea.setEstado(EstadoTarea.getNombreEstado(resumeTarea.getStatus()));
        Usuario usuario = ((UsuarioServiceBean) BeanLocator.getInstance(UsuarioServiceBean.class))
                .buscarUsuarioWithOutFilters(resumeTarea.getUserId());
        tarea.setResponsable(usuario.getPersona().getNombre());
        tarea.setUsuarioId(resumeTarea.getUserId());
        SimpleDateFormat fechaInicio = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        tarea.setFechaInicio(fechaInicio.parse(resumeTarea.getCreatedDate()));
        SimpleDateFormat fechaFin = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        if(!resumeTarea.getEndDate().isEmpty())
        	tarea.setFechaFin(fechaFin.parse(resumeTarea.getEndDate()));
        return tarea;
    }
}
