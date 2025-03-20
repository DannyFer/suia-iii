package ec.gob.ambiente.prevencion.categoriaii.mineria.controller;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.prevencion.categoria2.controllers.FichaAmbientalPmaController;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.FichaAmbientalMineria;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.CategoriaIIFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.ficha.mineria.facade.FichaAmbientalMineriaFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.reportes.DocumentoPDFPlantillaHtml;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilFichaMineria;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author frank torres
 */
@ManagedBean
@ViewScoped
public class FichaMineriaEnviarController implements Serializable {

    private static final long serialVersionUID = 1657407381900049845L;

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
            .getLogger(FichaMineriaEnviarController.class);

    @Getter
    @Setter
    private FichaAmbientalMineria fichaAmbientalMineria;

    @ManagedProperty(value = "#{bandejaTareasBean}")
    @Getter
    @Setter
    private BandejaTareasBean bandejaTareasBean;

    @ManagedProperty(value = "#{proyectosBean}")
    @Getter
    @Setter
    private ProyectosBean proyectosBean;

    @EJB
    private CategoriaIIFacade categoriaIIFacade;

    @EJB
    private FichaAmbientalMineriaFacade fichaAmbientalMineriaFacade;

    @EJB(lookup = Constantes.JBPM_EJB_TASK_BEAN)
    private TaskBeanFacade taskBeanFacade;
    @EJB
    private ProcesoFacade procesoFacade;

    @Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;

    @Getter
    @Setter
    @ManagedProperty(value = "#{impresionFichaMineriaController}")
    private ImpresionFichaMineriaController impresionFichaMineriaController;

    @Getter
    @Setter
    @ManagedProperty(value = "#{fichaAmbientalPmaController}")
    private FichaAmbientalPmaController fichaAmbientalPmaController;

    @Getter
    @Setter
    private String mensaje;

    @Getter
    @Setter
    private byte[] archivo;

    @Getter
    @Setter
    private String pdf;
    @Getter
    @Setter
    private File archivoFinal;
    @Getter
    @Setter
    private Boolean descargarFicha = false;
    @Getter
    @Setter
    private Boolean descargarRegistro = false;

    @Getter
    @Setter
    private Boolean condiciones = false;

    @Getter
    @Setter
    private StreamedContent ficha;

    @Getter
    @Setter
    private ProyectoLicenciamientoAmbiental proyecto;

    private static final int TIPO_DOCUMENTO_FICHA = 6;
    private static final int TIPO_ESTUDO = 2;
    private Boolean completado;

    @PostConstruct
    public void cargarDatos() {
        cargarDatosBandeja();
    }

    private void cargarDatosBandeja() {
        proyecto = proyectosBean.getProyecto();
        try {
            fichaAmbientalMineria = fichaAmbientalMineriaFacade
                    .obtenerPorProyecto(proyecto);
            mensaje = getNotaResponsabilidadInformacionRegistroProyecto(loginBean
                    .getUsuario());
            ficha = getStream();
            completado = validarFichaMineraCompletaInicial();
            if (validarFichaMineraCompleta()) {
                File archivoTmp = impresionFichaMineriaController
                        .cargarDatosPdfFile();
                Path path = Paths.get(archivoTmp.getAbsolutePath());
                FileOutputStream file;

                archivo = Files.readAllBytes(path);
                archivoFinal = new File(
                        JsfUtil.devolverPathReportesHtml(archivoTmp.getName()));
                file = new FileOutputStream(archivoFinal);
                file.write(archivo);
                file.close();
                setPdf(JsfUtil.devolverContexto("/reportesHtml/"
                        + archivoTmp.getName()));
            }


        } catch (ServiceException e) {
            LOG.error(e, e);
        } catch (IOException e) {
            LOG.error(e, e);
        }
    }

    public Boolean validarFichaMineraCompleta() {
        return completado;
    }


    public Boolean validarFichaMineraCompletaInicial() {
        try {
            return fichaAmbientalMineria.getValidarInformacionGeneral()
                    && fichaAmbientalMineria.getValidarCaracteristicasGenerales()
                    && fichaAmbientalMineria.getValidarDescripcionActividad()
                    && fichaAmbientalMineria.getValidarCaracteristicasAreaInfluencia()
                    && fichaAmbientalMineria.getValidarMuestreoInicialLineaBase()
                    && fichaAmbientalMineria.getValidarMatrizIdentificacionImpactosAmbientales()
                    && fichaAmbientalMineria.getValidarPlanManejoAmbiental();
        } catch (RuntimeException e) {
            //LOG.error(e, e);
            return false;
        }
    }

    public String enviarFicha() {
        try {

            if (validarFichaMineraCompleta()) {

                long idTask = bandejaTareasBean.getTarea().getTaskId();
                long idProcessInstance = bandejaTareasBean.getTarea()
                        .getProcessInstanceId();
                inicializarNotificacion();
                File fichaAmbiental = impresionFichaMineriaController
                        .cargarDatosPdfFile();
                categoriaIIFacade.ingresarDocumentoCategoriaII(fichaAmbiental,
                        proyecto.getId(), proyecto.getCodigo(),
                        idProcessInstance, idTask, TipoDocumentoSistema.TIPO_DOCUMENTO_FICHA_MINERIA, "Ficha Mineria");

                File file = fichaAmbientalPmaController.subirLicenciaAmbiental(true,1);

                if (file != null) {
                    taskBeanFacade.approveTask(loginBean.getNombreUsuario(),bandejaTareasBean.getTarea().getTaskId(),
                            bandejaTareasBean.getTarea().getProcessInstanceId(), null,
                            loginBean.getPassword(),
                            Constantes.getUrlBusinessCentral(), Constantes.getRemoteApiTimeout(), Constantes.getNotificationService());

                    archivoFinal.delete();
                    JsfUtil.addMessageInfo("Se realizó correctamente la operación.");
                } else {
                    JsfUtil.addMessageError("Error al realizar la operación.");
                    return "";
                }
            } else {
                JsfUtil.addMessageError("Debe completar los datos del Registro Ambiental Minería Artesanal.");
            }
        } catch (Exception e) {
            LOG.error(e, e);
        }
        return "";
    }

    private String getNotaResponsabilidadInformacionRegistroProyecto(
            Usuario persona) {
        String[] parametros = {persona.getPersona().getNombre(), persona.getPin()};
        return DocumentoPDFPlantillaHtml.getPlantillaConParametros(
                "nota_responsabilidad_certificado_interseccion", parametros);
    }

    public StreamedContent getStream() {
        DefaultStreamedContent content = new DefaultStreamedContent();

        if (archivo != null) {
            content = new DefaultStreamedContent(new ByteArrayInputStream(
                    archivo), "application/pdf");
            content.setName("Registro_Ambiental_Minería_Artesanal.pdf");
        }
        return content;

    }

    public void descargarFichaTecnico() {
        descargarFicha = true;
        String[] tokens = archivoFinal.getName().split("\\.(?=[^\\.]+$)");
        UtilFichaMineria.descargar(archivoFinal.getAbsolutePath(), tokens[0]);
    }

    public void inicializarNotificacion() throws JbpmException {
        mensaje = "Estimado usuario, en cumplimiento de la normativa ambiental actual, usted debe realizar el Registro de Generador de Desechos Peligrosos y/o Especiales en un plazo de sesenta (60) días.";
        if (generaDesechosEspeciales()) {
            mensaje = "Estimado usuario, en cumplimiento de la normativa ambiental actual, usted debe realizar el Registro de Generador de Desechos Peligrosos y/o Especiales en un plazo de quince (15) días. ";
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
        return proyecto.getTipoEstudio().getId() == TIPO_ESTUDO
                && proyecto.getGeneraDesechos() != null
                && proyecto.getGeneraDesechos();
    }

    public void validarTareaBpm() {
        //JsfUtil.validarPaginasUrlTareasBpm(bandejaTareasBean.getTarea(), "/prevencion/categoria2/v2/fichaMineria/default.jsf");
    }
}
