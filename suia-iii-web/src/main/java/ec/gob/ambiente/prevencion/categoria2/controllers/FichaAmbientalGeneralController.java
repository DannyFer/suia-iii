/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.prevencion.categoria2.controllers;

import java.io.File;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.prevencion.categoria2.bean.FichaAmbientalGeneralImprimirBean;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.CatalogoCategoriaSistema;
import ec.gob.ambiente.suia.domain.FichaAmbientalPma;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.CategoriaIIFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.FichaAmbientalPmaFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

/**
 * @author frank torres
 */
@ManagedBean
@ViewScoped
public class FichaAmbientalGeneralController implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -3139973485508002420L;

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
            .getLogger(FichaAmbientalGeneralController.class);

    @ManagedProperty(value = "#{bandejaTareasBean}")
    @Getter
    @Setter
    private BandejaTareasBean bandejaTareasBean;
    @ManagedProperty(value = "#{proyectosBean}")
    @Getter
    @Setter
    private ProyectosBean proyectosBean;

    @EJB
    private FichaAmbientalPmaFacade fichaAmbientalFacade;
    @EJB
    private ProcesoFacade procesoFacade;
    @EJB
    private CategoriaIIFacade categoriaIIFacade;

    @EJB
    private FichaAmbientalPmaFacade fichaAmbientalPmaFacade;

    @EJB(lookup = Constantes.JBPM_EJB_TASK_BEAN)
    private TaskBeanFacade taskBeanFacade;

    @Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;

    @Getter
    @Setter
    private FichaAmbientalPma fichaAmbiental;

    @Getter
    @Setter
    private String mensaje;

    @Getter
    @Setter
    private byte[] archivo;

    @Getter
    @Setter
    private Boolean descargarFicha = false;
    @Getter
    @Setter
    private Boolean descargarRegistro = false;
    @Getter
    @Setter
    private Boolean completado = false;

    @PostConstruct
    public void inicializarDatos() {
        completado = validarFichaCompletada();
    }

    public Boolean validarFichaCompletada() {
        CatalogoCategoriaSistema catalogo = proyectosBean.getProyecto()
                .getCatalogoCategoria();
        fichaAmbiental = fichaAmbientalPmaFacade
                .getFichaAmbientalPorIdProyecto(proyectosBean.getProyecto()
                        .getId());
        if (catalogo.getCodigo().equals(Constantes.SECTOR_HIDROCARBURO_CODIGO)) {
            fichaAmbiental.setValidarDescripcionAreaImplantacion(true);
        }
        try {
            return fichaAmbiental.getValidarDatosGenerales() != null
                    && fichaAmbiental.getValidarMarcoLegalReferencial() != null
                    && fichaAmbiental
                    .getValidarDescripcionProyectoObraActividad() != null
                    && fichaAmbiental.getValidarDescripcionProceso() != null
                    && fichaAmbiental.getValidarDescripcionAreaImplantacion() != null
                    && fichaAmbiental
                    .getValidarPrincipalesImpactosAmbientales() != null
//					&& fichaAmbiental.getValidarPlanManejoAmbiental() != null
//					&& fichaAmbiental
//							.getValidarCronogramaConstruccionOperacionProyecto() != null
                    && fichaAmbiental
                    .getValidarCronogramaValoradoPlanManejoAmbiental() != null
                    && fichaAmbiental.getValidarDatosGenerales()
                    && fichaAmbiental.getValidarMarcoLegalReferencial()
                    && fichaAmbiental
                    .getValidarDescripcionProyectoObraActividad()
                    && fichaAmbiental.getValidarDescripcionProceso()
                    && fichaAmbiental.getValidarDescripcionAreaImplantacion()
                    && fichaAmbiental
                    .getValidarPrincipalesImpactosAmbientales()
//					&& fichaAmbiental.getValidarPlanManejoAmbiental()
//					&& fichaAmbiental
//							.getValidarCronogramaConstruccionOperacionProyecto()
                    && fichaAmbiental
                    .getValidarCronogramaValoradoPlanManejoAmbiental();
        } catch (Exception e) {
            return false;
        }

        // return false;
    }

    public String enviarFicha() {
        try {

            if (validarFichaCompletada()) {

                long idTask = bandejaTareasBean.getTarea().getTaskId();
                long idProcessInstance = bandejaTareasBean.getTarea()
                        .getProcessInstanceId();

                try {
                    inicializarNotificacion();
                } catch (JbpmException e) {
                    JsfUtil.addMessageError("Ocurrió un error al realizar la operación. Por favor intente más tarde.");
                    LOG.error("Ocurrió un error al realizar la operación 'inicializar Notificacion'", e);
                    return "";
                }
                FichaAmbientalGeneralImprimirBean fichaAmbientalGeneralImprimirBean = JsfUtil
                        .getBean(FichaAmbientalGeneralImprimirBean.class);
                categoriaIIFacade.ingresarDocumentoCategoriaII(fichaAmbientalGeneralImprimirBean.getArchivoFinal(),
                        proyectosBean.getProyecto().getId(), proyectosBean.getProyecto().getCodigo(),
                        idProcessInstance, idTask, TipoDocumentoSistema.TIPO_DOCUMENTO_REGISTRO_AMBIENTAL, "Registro Ambiental");
                FichaAmbientalPmaController fichaAmbientalPmaController = JsfUtil
                        .getBean(FichaAmbientalPmaController.class);
                File licenciaTmp = fichaAmbientalPmaController
                        .subirLicenciaAmbiental(true,1);
                if (licenciaTmp != null) {
                    taskBeanFacade.approveTask(loginBean.getNombreUsuario(),
                            bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getTarea().getProcessInstanceId(), null, loginBean.getPassword(),
                            Constantes.getUrlBusinessCentral(), Constantes.getRemoteApiTimeout(), Constantes.getNotificationService());

                    fichaAmbientalGeneralImprimirBean.getArchivoFinal()
                            .delete();
                    JsfUtil.addMessageInfo("Se realizó correctamente la operación.");
                    return JsfUtil
                            .actionNavigateTo("/prevencion/categoria2/fichaAmbiental/enviarFicha.jsf");
                } else {
                    return "";
                }
            } else {
                JsfUtil.addMessageError("Debe completar los datos del Registro Ambiental Minería Artesanal.");
            }
        } catch (Exception e) {
            LOG.error(e, e);
            JsfUtil.addMessageError("Ocurrió un error al realizar la operación. Por favor intente más tarde.");
        }

        return "";
    }

    public void inicializarNotificacion() throws JbpmException {
        String mensaje = "Estimado usuario, en cumplimiento de la normativa ambiental actual, usted debe realizar el Registro de Generador de Desechos Peligrosos y/o Especiales en un plazo de sesenta (60) días.";

        String mensajeAutoridades = "El sistema de Regularización y Control Ambiental notifica que se ha emitido el Registro Ambiental al proyecto "
                + proyectosBean.getProyecto().getCodigo()
                + ". En cumplimiento de las normativas legales vigentes, el proponente debe proceder a realizar el Registro de Generador de Desechos Peligrosos y/o Especiales en un plazo de sesenta (60) días.";
        if (generaDesechosEspeciales()) {
            mensaje = "Estimado usuario, en cumplimiento de la normativa ambiental actual, usted debe realizar el Registro de Generador de Desechos Peligrosos y/o Especiales en un plazo de quince (15) días. ";

            mensajeAutoridades = "El sistema de Regularización y Control Ambiental notifica que se ha emitido el Registro Ambiental al proyecto "
                    + proyectosBean.getProyecto().getCodigo()
                    + ". En cumplimiento de las normativas legales vigentes, el proponente debe proceder a realizar el Registro de Generador de Desechos Peligrosos y/o Especiales en un plazo de quince (15) días. ";

        }

        Map<String, Object> params = new ConcurrentHashMap<String, Object>();

        params.put("mensajePago", mensaje);
        params.put("mensajeAutoridad", mensaje);
        params.put("u_Autoridad",
                Constantes.getUsuariosNotificarRegistroGeneradoresDesechos());

        procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getTarea()
                        .getProcessInstanceId(), params);

    }

    public boolean generaDesechosEspeciales() {
        return proyectosBean.getProyecto().getTipoEstudio().getId() == 2
                && proyectosBean.getProyecto().getGeneraDesechos() != null
                && proyectosBean.getProyecto().getGeneraDesechos() == true;
    }

}
