/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.prevencion.licenciamiento.requistosprevios.controller;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.prevencion.licenciamiento.requistosprevios.bean.RegistroRequisitosPreviosBean;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.entidadResponsable.facade.EntidadResponsableFacade;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.licenciamiento.facade.RequisitosPreviosFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Setter;
import org.apache.log4j.Logger;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import java.util.HashMap;
import java.util.Map;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 *
 * @author Carlos Pupo
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Carlos Pupo, Fecha: 06/03/2015]
 *          </p>
 */
@ManagedBean
public class RegistroRequisitosPreviosController {

    private static final Logger LOGGER = Logger.getLogger(RegistroRequisitosPreviosController.class);

    @Setter
    @ManagedProperty(value = "#{registroRequisitosPreviosBean}")
    private RegistroRequisitosPreviosBean registroRequisitosPreviosBean;

    @Setter
    @ManagedProperty(value = "#{proyectosBean}")
    private ProyectosBean proyectosBean;

    @EJB
    private ProcesoFacade procesoFacade;

    @EJB
    private AreaFacade areaFacade;

    @EJB
    private EntidadResponsableFacade entidadResponsableFacade;

    @EJB
    private RequisitosPreviosFacade requisitosPreviosFacade;

    public String guardar() {
        try {
            Area areaProyecto = null;
            Usuario coordinador = null;
            Usuario director = null;

            try {
                areaProyecto = entidadResponsableFacade.obtenerEntidadResponsable(proyectosBean.getProyecto());
                areaProyecto = areaFacade.getAreaFull(areaProyecto.getId());
            } catch (Exception e) {
            }

            boolean plantaCentral = false;
            if (areaProyecto == null
                    || areaProyecto.getTipoArea().getSiglas().equalsIgnoreCase(Constantes.SIGLAS_TIPO_AREA_PC)) {
                coordinador = areaFacade.getCoordinadorControlAmbientalPlantaCentral();
                director = areaFacade.getDirectorPlantaCentral();
                plantaCentral = true;
            } else {
                coordinador = areaFacade.getCoordinadorPorArea(areaProyecto);
                director = areaFacade.getDirectorProvincial(areaProyecto);
                plantaCentral = false;
            }

            Map<String, Object> parametros = new HashMap<String, Object>();
            if (!plantaCentral) {
                parametros.put("tipoAreaProyecto", areaProyecto.getTipoArea().getSiglas());
                parametros.put("areaProyectoId", areaProyecto.getId());
            } else
                parametros.put("tipoAreaProyecto", Constantes.SIGLAS_TIPO_AREA_PC);
            parametros.put("sujetoControl", JsfUtil.getLoggedUser().getNombre());
            parametros.put("viceministra", areaFacade.getViceministra().getNombre());
            parametros.put("secretaria", areaFacade.getSubsecretariaCalidadAmbiental().getNombre());
            parametros.put("coordinador", coordinador.getNombre());
            parametros.put("director", director.getNombre());
            parametros.put("licenciaAmbiental", false);
            parametros.put("direccionProvincial", false);
            parametros.put("categoria", proyectosBean.getProyecto().getCatalogoCategoria().getCategoria().getId());
            parametros.put(Constantes.ID_PROYECTO, proyectosBean.getProyecto().getId());

            requisitosPreviosFacade.guardar(registroRequisitosPreviosBean.getRequisitos(),
                    proyectosBean.getProyecto());

            parametros.put("idRequisitosPrevios", registroRequisitosPreviosBean.getRequisitos().getId());

            procesoFacade.iniciarProceso(JsfUtil.getLoggedUser(), Constantes.NOMBRE_PROCESO_REQUISITOS_PREVIOS_LICENCIAMIENTO, proyectosBean
                    .getProyecto().getCodigo(), parametros);

            JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
            return JsfUtil.actionNavigateTo(JsfUtil.NAVIGATION_TO_BANDEJA);
        } catch (JbpmException e) {
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_INICIAR_PROCESO);
            LOGGER.error(e);
        } catch (ServiceException e) {
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_INICIAR_PROCESO);
            LOGGER.error(e);
        }
        return null;
    }
}
