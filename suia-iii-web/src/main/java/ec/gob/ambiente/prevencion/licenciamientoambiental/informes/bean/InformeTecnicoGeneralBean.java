package ec.gob.ambiente.prevencion.licenciamientoambiental.informes.bean;

import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.*;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.eia.facade.LicenciaAmbientalFacade;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.ProyectoLicenciaAmbientalFacade;
import ec.gob.ambiente.suia.prevencion.licenciamientoAmbiental.facade.InformeOficioFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilGenerarInforme;
import lombok.Getter;
import lombok.Setter;
import observaciones.ObservacionesController;
import org.apache.log4j.Logger;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.ByteArrayInputStream;
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
public class InformeTecnicoGeneralBean implements Serializable {

    // ///////////////////////
    // CONSTANTES
    // ///////////////////////
    public static final String TEMP_CONST_NOMBRE_SECUENCIA_NUMERO = "seq_number_technical_report_eia";
    public static final String TEMP_CONST_NOMBRE_ESQUEMA_SUIA = "suia_iii";
    public static final int TEMP_CONST_TAMANIO_NUMERO_OFICIO = 6;
    /**
     *
     */
    private static final long serialVersionUID = 165683211928358047L;

    private final Logger LOG = Logger
            .getLogger(InformeTecnicoGeneralBean.class);

    @Getter
    @Setter
    private TipoDocumento tipoDocumento;
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
    private InformeTecnicoGeneralLA informeTecnicoGeneralLA;
    @Getter
    @Setter
    private String tipo = "";

    @Getter
    @Setter
    private String nombreReporte = "";
    @Getter
    @Setter
    private Usuario promotor;

    @Getter
    @Setter
    private PlantillaReporte plantillaReporte;

    @Getter
    @Setter
    private ProyectoLicenciamientoAmbiental proyecto;

    @Getter
    @Setter
    private boolean revisar;


    @Getter
    @Setter
    private boolean requiereModificaciones;


    @Getter
    @Setter
    private boolean mostrarInforme;

    @Getter
    @Setter
    private Usuario remitente;

    @EJB
    private InformeOficioFacade informeOficioFacade;


    @EJB
    private AreaFacade areaFacade;

    @EJB
    private OrganizacionFacade organizacionFacade;
    @EJB
    private UsuarioFacade usuarioFacade;

    @EJB
    private ProcesoFacade procesoFacade;
    @EJB
    private LicenciaAmbientalFacade licenciaAmbientalFacade;

    @EJB
    private ProyectoLicenciaAmbientalFacade proyectoLicenciaAmbientalFacade;

    @Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;

    @Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;

    @ManagedProperty(value = "#{proyectosBean}")
    @Getter
    @Setter
    private ProyectosBean proyectosBean;

    @Getter
    @Setter
    @ManagedProperty(value = "#{observacionesController}")
    private ObservacionesController observacionesController;


    @PostConstruct
    public void init() {
        try {
            tipoDocumento = new TipoDocumento();
            tipoDocumento.setId(TipoDocumentoSistema.TIPO_INFORME_TECNICO_GENERAL_LA.getIdTipoDocumento());

            plantillaReporte = informeOficioFacade
                    .obtenerPlantillaReporte(this.getTipoDocumento().getId());

            Map<String, Object> variables = procesoFacade
                    .recuperarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getTarea()
                            .getProcessInstanceId());
            Integer idProyecto = Integer.parseInt((String) variables
                    .get(Constantes.ID_PROYECTO));

            String nombrePromotor = variables.get("u_Promotor").toString();

            promotor = usuarioFacade.buscarUsuarioCompleta(nombrePromotor);

            remitente = usuarioFacade.buscarUsuarioCompleta(loginBean.getNombreUsuario());
            proyecto = proyectosBean.getProyecto();
            Map<String, String> params = FacesContext.getCurrentInstance()
                    .getExternalContext().getRequestParameterMap();
            if (params.containsKey("tipo")) {
                tipo = params.get("tipo");
                if (params.get("tipo").equals("revisar")) {
                    revisar = true;

                }

            }

            informeTecnicoGeneralLA = informeOficioFacade
                    .obtenerInformeTecnicoLAGeneralPorProyectoId(tipoDocumento.getId(),
                            idProyecto);

            if (informeTecnicoGeneralLA == null) {
                informeTecnicoGeneralLA = new InformeTecnicoGeneralLA();
                informeTecnicoGeneralLA.setTipoDocumento(tipoDocumento);

                LicenciaAmbiental licenciaAmbiental = licenciaAmbientalFacade.obtenerLicenciaAmbientallPorProyectoId(idProyecto);
//                if (licenciaAmbiental == null) {
//                    licenciaAmbiental = new LicenciaAmbiental();
//                    licenciaAmbiental.setProyecto(proyecto);
//                    licenciaAmbientalFacade.archivarProcesoLicenciaAmbiental(licenciaAmbiental);
//                }
                informeTecnicoGeneralLA.setLicenciaAmbiental(licenciaAmbiental);

                BigInteger numeroSecuencia = informeOficioFacade
                        .obtenerNumeroInforme(TEMP_CONST_NOMBRE_SECUENCIA_NUMERO,
                                TEMP_CONST_NOMBRE_ESQUEMA_SUIA);
                String numeroInforme = plantillaReporte.getCodigoProceso()
                        + "-"
                        + JsfUtil.getCurrentYear()
                        + "-"
                        + (numeroSecuencia != null ? JsfUtil.rellenarCeros(
                        numeroSecuencia.toString(),
                        TEMP_CONST_TAMANIO_NUMERO_OFICIO) : "N/A");

                informeTecnicoGeneralLA.setNumeroOficio(numeroInforme);
                informeTecnicoGeneralLA.setNumeroLicencia(numeroSecuencia.toString());
                informeOficioFacade.guardarInformeTecnicoGeneralLA(informeTecnicoGeneralLA);


            }
            cargarDatos();
            visualizarOficio();
        } catch (Exception e) {
            LOG.error("Error al inicializar: Informe técnigo general: ", e);
            JsfUtil.addMessageError(JsfUtil.ERROR_INICIALIZAR_DATOS);
        }
    }

    public void cargarDatos() {

        Area area = areaFacade.getAreaFull(proyectosBean.getProyecto().getAreaResponsable().getId());
        informeTecnicoGeneralLA.setCiudadInforme(area.getUbicacionesGeografica().getNombre());
        informeTecnicoGeneralLA.setFechaInforme(JsfUtil
                .devuelveFechaEnLetrasSinHora(new Date()));
        try {
            Usuario ministra = areaFacade.getMinistra();
            informeTecnicoGeneralLA.setNombreMinistra(ministra.getPersona().getNombre());
            informeTecnicoGeneralLA.setCargoMinistra("Ministra del Ambiental");

            informeTecnicoGeneralLA.setNombreProyecto(proyecto.getNombre());
            configurarDirecciones();

            informeTecnicoGeneralLA.setFechaRegistroProyecto(JsfUtil.getDateFormat(proyecto.getFechaRegistro()));


            informeTecnicoGeneralLA.setNombreEmpresaProponente(getInfoToShow(proyecto.getUsuario()));
            ;
        } catch (ServiceException e) {
            LOG.error(e);
        } catch (Exception e) {
            LOG.error(e);
        }

    }

    public String visualizarOficio() {
        String pathPdf = null;
        try {


            informeTecnicoGeneralLA.setFechaInforme(JsfUtil
                    .devuelveFechaEnLetrasSinHora(new Date()));

//            if (informeTecnicoGeneralLA.getObservaciones() == null) {
////				List<ObservacionesFormularios> listaObservaciones = observacionesFacade
////						.listarPorIdClaseNombreClase(
////								estudioImpactoAmbiental.getId(),
////								EstudioImpactoAmbiental.class.getSimpleName());
//
//                String observaciones = "";
////				for (ObservacionesFormularios observacionesFormularios : listaObservaciones) {
////					observaciones += observacionesFormularios.getDescripcion()
////							+ "\n";
////				}
//                informeTecnicoGeneralLA.setObservaciones(observaciones);
//            }

            nombreReporte = "LicenciaAmbiental-" + proyecto.getCodigo() + ".pdf";
            File informePdf = UtilGenerarInforme.generarFichero(
                    plantillaReporte.getHtmlPlantilla(), nombreReporte, true,
                    informeTecnicoGeneralLA);


            Path path = Paths.get(informePdf.getAbsolutePath());
            archivoInforme = Files.readAllBytes(path);
            oficioPdf = new File(
                    JsfUtil.devolverPathReportesHtml(nombreReporte));
            FileOutputStream file = new FileOutputStream(oficioPdf);
            file.write(archivoInforme);
            file.close();
            informePath = JsfUtil.devolverContexto("/reportesHtml/"
                    + nombreReporte);

            pathPdf = informePdf.getParent();

        } catch (Exception e) {
            LOG.error("Error al visualizar el oficio", e);
            JsfUtil.addMessageError("Error al visualizar el oficio");
        }

        return pathPdf;
    }

    public void guardar() {
        try {
            informeOficioFacade.guardarInformeTecnicoGeneralLA(informeTecnicoGeneralLA);
            JsfUtil.redirectTo("/prevencion/licenciamiento-ambiental/documentos/revisarDocumentacionJuridico.jsf");
        } catch (Exception e) {
            LOG.error("Error al guardar oficio", e);
        }
    }


    public void validarTareaBpm() {

        String url = "/prevencion/licenciamiento-ambiental/documentos/revisarDocumentacionJuridico.jsf";
        if (tipo != null && !tipo.isEmpty()) {
            url += "?tipo=" + tipo;
        }
        JsfUtil.validarPaginasUrlTareasBpm(bandejaTareasBean.getTarea(), url);
    }


    public void configurarDirecciones() throws Exception {
        List<ProyectoUbicacionGeografica> ubicaciones = proyecto.getProyectoUbicacionesGeograficas();
        UbicacionesGeografica parroquia = null;
        String ubicacion = "";
        String ubicacionCompleta = "";
        /*if (proyecto.isConcesionesMinerasMultiples()) {
            ConcesionMinera primeraConcesion = proyecto.getConcesionMinera();
            parroquia = primeraConcesion.getConcesionesUbicacionesGeograficas().get(0).getUbicacionesGeografica();
        } else {*/
            parroquia = ubicaciones.get(0).getUbicacionesGeografica();
        //}
        ubicacionCompleta = parroquia.getUbicacionesGeografica().getUbicacionesGeografica().getNombre() +
                ", " +
                parroquia.getUbicacionesGeografica().getUbicacionesGeografica().getNombre()
                + ", "
                + parroquia.getNombre();
        ubicacion = parroquia.getUbicacionesGeografica().getUbicacionesGeografica().getNombre();

        informeTecnicoGeneralLA.setProvinciaProyecto(ubicacion);
        informeTecnicoGeneralLA.setDireccionCompleta(ubicacionCompleta);
    }

    public String getInfoToShow(Usuario usuario) {
        String infoToShow = usuario.getNombre();

        if (usuario.getPersona() != null) {
            try {
                if (organizacionFacade.tieneOrganizacionPorPersona(usuario.getPersona())) {
                    Organizacion organizacion = organizacionFacade.buscarPorPersona(usuario.getPersona(), usuario.getNombre());
                    if (organizacion != null && organizacion.getNombre() != null && organizacion.getRuc().trim().equals(usuario.getNombre())) {
                        return organizacion.getNombre();
                    }
                }
            } catch (ServiceException e) {
                LOG.error("Error al obtener la organización de la persona.", e);
            }
            if (usuario.getPersona().getNombre() != null
                    && !usuario.getPersona().getNombre().trim().isEmpty()) {
                infoToShow = usuario.getPersona().getNombre().trim();
            }
        }
        return infoToShow;
    }


    public StreamedContent getStream() throws Exception {
        DefaultStreamedContent content = new DefaultStreamedContent();
        if (archivoInforme != null) {
            content = new DefaultStreamedContent(new ByteArrayInputStream(
                    archivoInforme), "application/octet-stream");
            content.setName(nombreReporte);

        }
        return content;

    }


    public String completarTarea() {
        try {
            informeOficioFacade.guardarInformeTecnicoGeneralLA(informeTecnicoGeneralLA);

            Map<String, Object> params = new ConcurrentHashMap<String, Object>();
            if (revisar) {
                if (validarObservaciones(!requiereModificaciones)) {
                    params.put("requiereMoficacion",
                            requiereModificaciones);
                    procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean
                            .getTarea().getProcessInstanceId(), params);

                    if (!requiereModificaciones) {
                        subirDocuemntoAlfresco();
                    }
                } else {
                    return "";
                }
            }

            Map<String, Object> data = new ConcurrentHashMap<String, Object>();
            procesoFacade.aprobarTarea(loginBean.getUsuario(), bandejaTareasBean.getTarea().getTaskId(),  bandejaTareasBean.getProcessId(), data);

            JsfUtil.addMessageInfo("Se realizó correctamente la operación.");
            try {
                oficioPdf.delete();
            } catch (Exception ex) {
                LOG.error(ex);
            }
            return JsfUtil.actionNavigateTo("/bandeja/bandejaTareas.jsf");
        } catch (Exception e) {
            LOG.error(e);
            JsfUtil.addMessageError("Error al realizar la operación.");
        }
        return "";
    }


    public Boolean validarObservaciones(Boolean estado) {
        List<ObservacionesFormularios> observaciones = observacionesController.getObservacionesBB().getMapaSecciones().get("informeGeneral");

        if (estado) {
            for (ObservacionesFormularios observacion : observaciones) {
                if (observacion.getUsuario().equals(loginBean
                                .getUsuario()
                ) && !observacion.isObservacionCorregida()) {

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
                        && !observaciones.get(posicion).isObservacionCorregida()) {
                    encontrado = true;
                }
                posicion++;
            }
            if (!encontrado) {
                JsfUtil.addMessageError("No existen observaciones sin corregir. Por favor rectifique los datos.");
                return false;
            }
        }
        return true;
    }


    public void subirDocuemntoAlfresco() throws Exception {
        licenciaAmbientalFacade.ingresarDocumentos(oficioPdf,
                proyecto.getId(),
                proyecto.getCodigo(),
                bandejaTareasBean.getProcessId(), bandejaTareasBean.getTarea().getTaskId(), TipoDocumentoSistema.TIPO_INFORME_TECNICO_GENERAL_LA, InformeTecnicoGeneralLA.class.getSimpleName());

    }
}
