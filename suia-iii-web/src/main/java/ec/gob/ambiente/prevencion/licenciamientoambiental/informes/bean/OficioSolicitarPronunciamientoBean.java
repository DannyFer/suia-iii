package ec.gob.ambiente.prevencion.licenciamientoambiental.informes.bean;

import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.certificadointerseccion.service.CertificadoInterseccionFacade;
import ec.gob.ambiente.suia.domain.*;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.dto.EntityOficioSolicitarPronunciamiento;
import ec.gob.ambiente.suia.eia.facade.EstudioImpactoAmbientalFacade;
import ec.gob.ambiente.suia.eia.facade.LicenciaAmbientalFacade;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.persona.facade.PersonaFacade;
import ec.gob.ambiente.suia.prevencion.licenciamientoAmbiental.facade.InformeOficioFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilGenerarInforme;
import lombok.Getter;
import lombok.Setter;
import observaciones.ObservacionesController;
import org.apache.log4j.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ManagedBean
@ViewScoped
public class OficioSolicitarPronunciamientoBean implements Serializable {

    // ///////////////////////
    // CONSTANTES
    // ///////////////////////
    public static final String TEMP_CONST_NOMBRE_SECUENCIA_NUMERO = "seq_number_job_order_statement";
    public static final String TEMP_CONST_NOMBRE_ESQUEMA_SUIA = "suia_iii";
    public static final int TEMP_CONST_TAMANIO_NUMERO_OFICIO = 6;
    /**
     *
     */
    private static final long serialVersionUID = 165683211928358047L;

    private final Logger LOG = Logger.getLogger(OficioSolicitarPronunciamientoBean.class);

    @Getter
    @Setter
    private TipoDocumento tipoDocumento;
    @Getter
    @Setter
    private PlantillaReporte plantillaReporte;

    @Getter
    @Setter
    private File oficioPdf;
    @Getter
    @Setter
    private byte[] archivoInforme;
    @Getter
    @Setter
    private String informePath;
    @Getter
    @Setter
    private OficioSolicitarPronunciamiento oficioSolicitarPronunciamiento;
    @Getter
    @Setter
    private EstudioImpactoAmbiental estudioImpactoAmbiental;

    @Getter
    @Setter
    private File archivoFinal;


    @Getter
    @Setter
    private String nombreReporte;

    @Getter
    @Setter
    private String nombreFichero;

    @EJB
    private InformeOficioFacade informeOficioFacade;

    @EJB
    private CertificadoInterseccionFacade certificadoInterseccionFacade;
    @EJB
    private EstudioImpactoAmbientalFacade estudioImpactoAmbientalFacade;

    @EJB
    private AreaFacade areaFacade;

    @EJB
    private LicenciaAmbientalFacade licenciaAmbientalFacade;

    @EJB
    private ProcesoFacade procesoFacade;

    @EJB
    private UsuarioFacade usuarioFacade;

    @EJB
    private PersonaFacade personaFacade;

    @ManagedProperty(value = "#{proyectosBean}")
    @Getter
    @Setter
    private ProyectosBean proyectosBean;

    @ManagedProperty(value = "#{loginBean}")
    @Getter
    @Setter
    private LoginBean loginBean;


    @Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;

    @Getter
    @Setter
    @ManagedProperty(value = "#{observacionesController}")
    private ObservacionesController observacionesController;

    @Getter
    @Setter
    private String tipo;

    @Getter
    @Setter
    private Boolean revisar = false;

    @Getter
    @Setter
    private boolean requiereModificaciones;

    @PostConstruct
    public void init() {
        try {
            tipoDocumento = new TipoDocumento();
            tipoDocumento.setId(TipoDocumentoSistema.TIPO_OFICIO_SOLICITAR_PRONUNCIAMIENTO.getIdTipoDocumento());
            plantillaReporte = informeOficioFacade.obtenerPlantillaReporte(this.getTipoDocumento()
                    .getId());

            Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext()
                    .getRequestParameterMap();

            if (params.containsKey("tipo")) {
                tipo = params.get("tipo");
                if (params.get("tipo").equals("revisar")) {
                    revisar = true;

                }
            }

            //Hasta que se termine el estudio de impacto ambiental se pone un valor "quemado"
            //*******************************************************************
            //estudioImpactoAmbiental =
            //estudioImpactoAmbientalFacade.obtenerPorProyectoTipo(proyectosBean.getProyecto(), "FINAL");

            estudioImpactoAmbiental =
                    estudioImpactoAmbientalFacade.obtenerPorProyecto(proyectosBean.getProyecto());

            oficioSolicitarPronunciamiento = informeOficioFacade.obtenerOficioSolicitarPronunciamientoPorEstudio(
                    tipoDocumento.getId(), estudioImpactoAmbiental.getId());

            if (oficioSolicitarPronunciamiento == null) {
                oficioSolicitarPronunciamiento = new OficioSolicitarPronunciamiento();


            }

            visualizarOficio();

        } catch (Exception e) {
            LOG.error("Error al inicializar: InformeTecnicoGirsBean: ", e);
            JsfUtil.addMessageError(JsfUtil.ERROR_INICIALIZAR_DATOS);
        }
    }

    public String visualizarOficio() {
        String pathPdf = null;
        try {


            String nombreProvincia = "";
            int i = 0;
            for (UbicacionesGeografica b : proyectosBean.getProyecto().getUbicacionesGeograficas()) {
                if (i == 0) {
                    nombreProvincia += b.getUbicacionesGeografica().getUbicacionesGeografica().getNombre();
                } else {
                    nombreProvincia += ", " + b.getUbicacionesGeografica().getUbicacionesGeografica().getNombre();
                }
            }

            Area area = areaFacade.getAreaFull(proyectosBean.getProyecto().getAreaResponsable().getId());

            String interseccion = certificadoInterseccionFacade.getDetalleInterseccion(proyectosBean.getProyecto()
                    .getCodigo());

            BigInteger numeroSecuencia = informeOficioFacade.obtenerNumeroInforme(TEMP_CONST_NOMBRE_SECUENCIA_NUMERO,
                    TEMP_CONST_NOMBRE_ESQUEMA_SUIA);

            if (oficioSolicitarPronunciamiento.getNumeroOficio() == null) {
                String numeroOficio = plantillaReporte.getCodigoProceso()
                        + "-"
                        + JsfUtil.getCurrentYear()
                        + "-"
                        + (numeroSecuencia != null ? JsfUtil.rellenarCeros(numeroSecuencia.toString(),
                        TEMP_CONST_TAMANIO_NUMERO_OFICIO) : "N/A");

                oficioSolicitarPronunciamiento.setNumeroOficio(numeroOficio);
            }

            CertificadoInterseccion certificadoInterseccion = certificadoInterseccionFacade
                    .recuperarCertificadoInterseccion(proyectosBean.getProyecto());

            String fechaOficioInterseccion = JsfUtil.getDayFromDate(certificadoInterseccion.getFechaCreacion())
                    + " de "
                    + JsfUtil.devuelveMes(JsfUtil.getMonthFromDate(certificadoInterseccion.getFechaCreacion()))
                    + " del " + JsfUtil.getYearFromDate(certificadoInterseccion.getFechaCreacion());

            Map<String, Object> variables = procesoFacade.recuperarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getTarea()
                    .getProcessInstanceId());

            Usuario tecnico = usuarioFacade.buscarUsuarioCompleta(variables.get("u_TecnicoResponsable").toString());

            Usuario director = usuarioFacade.buscarUsuarioCompleta(variables.get("u_DirectorDPNCA").toString());

            // Llenar entity oficio
            EntityOficioSolicitarPronunciamiento entityOficio = new EntityOficioSolicitarPronunciamiento();

            entityOficio.setNumeroOficio(oficioSolicitarPronunciamiento.getNumeroOficio());
            Usuario responsable = usuarioFacade.buscarUsuarioCompleta(loginBean.getUsuario().getNombre());

            entityOficio.setCiudadInforme(responsable.getPersona().getUbicacionesGeografica().getUbicacionesGeografica().getNombre());
            //  entityOficio.setCiudadInforme(area.getUbicacionesGeografica().getNombre());
            entityOficio.setFechaInforme(JsfUtil.devuelveFechaEnLetrasSinHora(new Date()));
            entityOficio.setAsunto(oficioSolicitarPronunciamiento.getAsunto());
            entityOficio.setTratamientoDirector(director.getPersona().getTipoTratos().getNombre());
            entityOficio.setNombreDirector(director.getPersona().getNombre());
            entityOficio.setCargoDirector(director.getPersona().getTitulo());
            // entityOficio.setEmpresa(empresa);
            // entityOficio.setLugarEntrega(lugarEntrega);
            entityOficio.setNombreProyecto(proyectosBean.getProyecto().getNombre());
            entityOficio.setProvinciaProyecto(nombreProvincia);
            entityOficio.setNombreProponente(proyectosBean.getProyecto().getUsuario().getNombre());
            entityOficio.setNumeroProyecto(proyectosBean.getProyecto().getCodigo());
            entityOficio.setNombreLugarInterseca(interseccion);
            entityOficio.setNumeroOficioInterseccion(certificadoInterseccion != null ? certificadoInterseccion
                    .getCodigo() : "N/A");
            entityOficio.setFechaOficioInterseccion(fechaOficioInterseccion);
            entityOficio.setNombreTecnico(tecnico.getPersona().getNombre());
            entityOficio.setCargoTecnico(tecnico.getPersona().getTitulo());

            nombreReporte = "Oficio.pdf";
            nombreFichero = "OficioPronunciamiento" + estudioImpactoAmbiental.getId() + ".pdf";
            File informePdf = UtilGenerarInforme.generarFichero(plantillaReporte.getHtmlPlantilla(), nombreReporte,
                    true, entityOficio);
            setOficioPdf(informePdf);

            Path path = Paths.get(getOficioPdf().getAbsolutePath());
            archivoInforme = Files.readAllBytes(path);
            archivoFinal = new File(JsfUtil.devolverPathReportesHtml(nombreFichero));
            FileOutputStream file = new FileOutputStream(archivoFinal);
            file.write(archivoInforme);
            file.close();
            setInformePath(JsfUtil.devolverContexto("/reportesHtml/" + nombreFichero));

            pathPdf = informePdf.getParent();

            oficioSolicitarPronunciamiento.setFechaOficio(entityOficio.getFechaInforme());
            oficioSolicitarPronunciamiento.setNombreDirector(director.getPersona().getNombre());
            oficioSolicitarPronunciamiento.setNombreTecnico(tecnico.getPersona().getNombre());
            oficioSolicitarPronunciamiento.setNumeroOficio(entityOficio.getNumeroOficio());

        } catch (Exception e) {
            LOG.error("Error al visualizar el oficio", e);
            JsfUtil.addMessageError("Error al visualizar el oficio");
        }

        return pathPdf;
    }

    public void guardar() {
        try {
            oficioSolicitarPronunciamiento.setEstado(true);
            oficioSolicitarPronunciamiento.setEstudioImpactoAmbiental(estudioImpactoAmbiental);
            oficioSolicitarPronunciamiento.setTipoDocumento(tipoDocumento);

            informeOficioFacade.guardarOficioSolicitarPronuncimiento(oficioSolicitarPronunciamiento);

            JsfUtil.addMessageInfo("Se realizó correctamente la operación.");
            String url = "/prevencion/licenciamiento-ambiental/documentos/oficioSolicitarPronunciamiento.jsf";
            JsfUtil.redirectTo((revisar ? url + "?tipo=" + tipo : url));
        } catch (Exception e) {
            LOG.error("Error al guardar oficio", e);
        }
    }

    public String completarTarea() {
        try {
            informeOficioFacade.guardarOficioSolicitarPronuncimiento(oficioSolicitarPronunciamiento);
            Map<String, Object> params = new ConcurrentHashMap<String, Object>();
            if (revisar) {
                if (validarObservaciones(!requiereModificaciones)) {
                    params.put("requiereModificacionesOficio", requiereModificaciones);
                } else {
                    return "";
                }

                if (!requiereModificaciones) {
                    subirDocuemntoAlfresco();
                }
            }
            procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getTarea().getProcessInstanceId(), params);

            Map<String, Object> data = new ConcurrentHashMap<String, Object>();
            procesoFacade.aprobarTarea(loginBean.getUsuario(), bandejaTareasBean.getTarea().getTaskId(),  bandejaTareasBean.getProcessId(), data);

            JsfUtil.addMessageInfo("Se realizó correctamente la operación.");
            return JsfUtil.actionNavigateTo("/bandeja/bandejaTareas.jsf");
        } catch (Exception e) {
            LOG.error(e);
            JsfUtil.addMessageError("Error al realizar la operación.");
        }
        return "";
    }

    public void validarTareaBpm() {
        String url = "/prevencion/licenciamiento-ambiental/documentos/oficioSolicitarPronunciamiento.jsf";
        JsfUtil.validarPaginasUrlTareasBpm(bandejaTareasBean.getTarea(), (revisar ? url + "?tipo=revisar" : url));
    }

    public Boolean validarObservaciones(Boolean estado) {
        List<ObservacionesFormularios> observaciones = observacionesController.getObservacionesBB().getMapaSecciones()
                .get("oficioPronunciamiento");

        if (estado) {
            for (ObservacionesFormularios observacion : observaciones) {
                if (observacion.getUsuario().equals(loginBean
                        .getUsuario()) && !observacion.isObservacionCorregida()) {

                    JsfUtil.addMessageError("Existen observaciones sin corregir. Por favor rectifique los datos.");
                    return false;
                }
            }
        } else {
            int posicion = 0;
            int cantidad = observaciones.size();
            Boolean encontrado = false;
            while (!encontrado && posicion < cantidad) {
                if (observaciones.get(posicion).getUsuario().equals(loginBean.getUsuario())
                        && !observaciones.get(posicion++).isObservacionCorregida()) {
                    encontrado = true;
                }
            }
            if (!encontrado) {
                JsfUtil.addMessageError("No existen observaciones sin corregir. Por favor rectifique los datos.");
                return false;
            }
        }
        return true;
    }

    public void subirDocuemntoAlfresco() throws Exception {
        licenciaAmbientalFacade.ingresarDocumentos(archivoFinal,
                estudioImpactoAmbiental.getProyectoLicenciamientoAmbiental().getId(),
                estudioImpactoAmbiental.getProyectoLicenciamientoAmbiental().getCodigo(),
                bandejaTareasBean.getProcessId(), bandejaTareasBean.getTarea().getTaskId(), TipoDocumentoSistema.TIPO_OFICIO_SOLICITAR_PRONUNCIAMIENTO, oficioSolicitarPronunciamiento.getClass().getSimpleName());

    }

}
