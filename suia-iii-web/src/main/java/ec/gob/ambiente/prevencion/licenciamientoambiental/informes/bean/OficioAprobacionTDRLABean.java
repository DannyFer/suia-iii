package ec.gob.ambiente.prevencion.licenciamientoambiental.informes.bean;

import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.certificadointerseccion.service.CertificadoInterseccionFacade;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.domain.*;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.eia.facade.EstudioImpactoAmbientalFacade;
import ec.gob.ambiente.suia.eia.facade.LicenciaAmbientalFacade;
import ec.gob.ambiente.suia.eia.inventarioforestal.facade.InventarioForestalFacade;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.CategoriaIIFacade;
import ec.gob.ambiente.suia.prevencion.licenciamientoAmbiental.facade.InformeOficioFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.pronunciamiento.facade.PronunciamientoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ManagedBean
@ViewScoped
public class OficioAprobacionTDRLABean implements Serializable {

    public static final int TEMP_CONST_TAMANIO_NUMERO_OFICIO = 6;
    private static final long serialVersionUID = -2520951529941685870L;

    private final Logger LOG = Logger.getLogger(OficioAprobacionTDRLABean.class);

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
    private OficioAprobacionTDRLA oficioAprobacionTDRLA;


    @Getter
    @Setter
    private String provincia = "";

    @Getter
    @Setter
    private InformeTecnicoTDRLA informeTecnicoTDRLA;


    @Getter
    @Setter
    private boolean mostrarInforme = false;

    @Getter
    @Setter
    private PlantillaReporte plantillaReporte;

    @Getter
    @Setter
    private String tipo;

    @Getter
    @Setter
    private boolean revisar = false;


    @Getter
    @Setter
    private String nombreReporte;

    @Getter
    @Setter
    private String nombreFichero;

    @Getter
    @Setter
    private File archivoFinal;

    @Getter
    @Setter
    private Usuario promotor;


    @Getter
    @Setter
    private String nombrePromotor;


    @Getter
    @Setter
    private Usuario remitente;


    @Getter
    @Setter
    private Date fechaOficioObservaciones;

    @Getter
    @Setter
    private Date fechaRespuestas;

    @Getter
    @Setter
    private Date fechaPronunciamiento;


    @Getter
    @Setter
    private Date fechaInformeTecnico;

    @Getter
    @Setter
    private Map<String, Object> variables;

    @Getter
    @Setter
    private boolean requiereModificaciones;


    @Getter
    @Setter
    private Boolean intersecta;


    @Getter
    @Setter
    private Boolean oficioAprobacion = true;

    @Getter
    @Setter
    private TipoDocumentoSistema tipoDocumentoSistema;

    @EJB
    private UsuarioFacade usuarioFacade;

    @EJB
    private PronunciamientoFacade pronunciamientoFacade;

    @EJB
    private AreaFacade areaFacade;

    @EJB
    private InventarioForestalFacade inventarioForestalFacade;

    @EJB
    private InformeOficioFacade informeOficioFacade;
    @EJB
    private EstudioImpactoAmbientalFacade estudioImpactoAmbientalFacade;
    @EJB
    private CertificadoInterseccionFacade certificadoInterseccionFacade;
    @EJB
    private LicenciaAmbientalFacade licenciaAmbientalFacade;

    @EJB
    private ProcesoFacade procesoFacade;

    @EJB
    private SecuenciasFacade secuenciasFacade;

    @EJB
    private OrganizacionFacade organizacionFacade;

    @EJB
    private CategoriaIIFacade categoriaIIFacade;

    @EJB
    private CertificadoInterseccionFacade certificadoInterseccionService;
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
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;

    @ManagedProperty(value = "#{proyectosBean}")
    @Getter
    @Setter
    private ProyectosBean proyectosBean;

    @PostConstruct
    public void init() {
        Map<String, String> params = FacesContext.getCurrentInstance()
                .getExternalContext().getRequestParameterMap();
        if (params.containsKey("tipo")) {
            tipo = params.get("tipo");
            if (params.get("tipo").equals("revisar")) {
                revisar = true;
            }
        }
        tipoDocumentoSistema = TipoDocumentoSistema.TIPO_OFICIO_APROBACION_TDR_LA;

    }

    public void cargarDatosGeneral() {
        try {


            variables = procesoFacade
                    .recuperarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean
                            .getTarea().getProcessInstanceId());

            String nombrePromotor = variables.get("u_Promotor").toString();
            String nombreFirma = variables.get("u_Subsecretaria").toString();
            if (!oficioAprobacion) {
                nombreFirma = variables.get("u_DirectorDPNCA").toString();
            }

            promotor = usuarioFacade.buscarUsuarioCompleta(nombrePromotor);

            remitente = usuarioFacade.buscarUsuarioCompleta(nombreFirma);

            plantillaReporte = informeOficioFacade
                    .obtenerPlantillaReporte(tipoDocumentoSistema.getIdTipoDocumento());


            oficioAprobacionTDRLA = informeOficioFacade
                    .obtenerOficioAprobacionTDRLAPorProyecto(tipoDocumentoSistema,
                            proyectosBean.getProyecto().getId());
            informeTecnicoTDRLA = informeOficioFacade
                    .obtenerInformeTecnicoTDRLAPorProyecto(TipoDocumentoSistema.TIPO_INFORME_TECNICO_TDR_LA,
                            proyectosBean.getProyecto().getId());
            Usuario responsable = usuarioFacade.buscarUsuarioCompleta(loginBean.getUsuario().getNombre());

            if (oficioAprobacionTDRLA == null) {
                oficioAprobacionTDRLA = new OficioAprobacionTDRLA();

                oficioAprobacionTDRLA
                        .setProyectoLicenciamientoAmbiental(proyectosBean.getProyecto());


                tipoDocumento = new TipoDocumento();
                tipoDocumento.setId(tipoDocumentoSistema.getIdTipoDocumento());

                oficioAprobacionTDRLA.setNumeroOficio(obtenerNuevoNumeroOficio());
                oficioAprobacionTDRLA.setTipoDocumento(tipoDocumento);

                informeOficioFacade.guardarInforme(oficioAprobacionTDRLA);
            } else {

               /* Integer cantidadNotificaciones = Integer.parseInt(variables.get("cantidadNotificaciones").toString());
                if (cantidadNotificaciones > 0 && !oficioAprobacionTDRLA.isTieneObservaciones()) {
                    oficioAprobacionTDRLA.setNumeroOficioObservaciones(oficioAprobacionTDRLA.getNumeroOficio());
                    oficioAprobacionTDRLA.setFechaOficioObservaciones(JsfUtil.devuelveFechaEnLetrasSinHora(oficioAprobacionTDRLA.getFechaModificacion() != null ? oficioAprobacionTDRLA.getFechaModificacion() : oficioAprobacionTDRLA.getFechaCreacion()));
                    oficioAprobacionTDRLA.setNumeroOficio(obtenerNuevoNumeroOficio());
                    oficioAprobacionTDRLA.setTieneObservaciones(true);

                    configurarEmisorPronunciamiento(variables);
                    informeOficioFacade
                            .guardarOficioAprobacionEia(oficioAprobacionTDRLA);
                }*/
            }

            oficioAprobacionTDRLA.setCiudadInforme(responsable.getPersona().getUbicacionesGeografica().getUbicacionesGeografica().getNombre());
            oficioAprobacionTDRLA.setInstitucionEmpresa(getInstitucionEmpresa());
            oficioAprobacionTDRLA.setNombreReceptor(promotor.getPersona().getNombre());
            oficioAprobacionTDRLA.setNombreRemitente(remitente.getPersona().getNombre());
            oficioAprobacionTDRLA.setNombrePromotor(nombrePromotor);
            oficioAprobacionTDRLA.setCoordenadasProyecto(generarTablaCoordenadas(proyectosBean.getProyecto()));

        } catch (Exception e) {
            LOG.error("Error al inicializar: OficioAprobacionEiaBean: ", e);
            JsfUtil.addMessageError(JsfUtil.ERROR_INICIALIZAR_DATOS);
        }
    }

    public String visualizarOficio() {
        return visualizarOficio(null);
    }

    public String visualizarOficioObservacion() {
        tipoDocumentoSistema = TipoDocumentoSistema.TIPO_OFICIO_OBSERVACION_TDR_LA;
        oficioAprobacion = false;
        return visualizarOficio(null);
    }

    public String visualizarOficio(String vacio) {
        cargarDatosGeneral();
        return visualizarOficio(vacio, false);
    }

    public String visualizarOficio(String nombreNull, boolean token) {
        String pathPdf = null;
        try {


            String nombreProvincia = "";
            String nombreCanton = "";
            String nombreParroquia = "";
            int i = 0;
            try {
                for (UbicacionesGeografica b : proyectosBean.getProyecto()
                        .getUbicacionesGeograficas()) {
                    if (i == 0) {
                        nombreProvincia += b.getUbicacionesGeografica()
                                .getUbicacionesGeografica().getNombre();
                        nombreCanton += b.getUbicacionesGeografica().getNombre();
                        nombreParroquia += b.getNombre();
                        provincia = nombreProvincia;
                    } else {
                        nombreProvincia += ", "
                                + b.getUbicacionesGeografica()
                                .getUbicacionesGeografica().getNombre();
                        nombreCanton += ", "
                                + b.getUbicacionesGeografica().getNombre();
                        nombreParroquia += ", " + b.getNombre();
                    }
                }
            } catch (Exception ex) {

            }


            CertificadoInterseccion certificadoInterseccion = certificadoInterseccionFacade
                    .recuperarCertificadoInterseccion(proyectosBean
                            .getProyecto());
            oficioAprobacionTDRLA.setFechaCoordenadaProyecto(JsfUtil.devuelveFechaEnLetrasSinHora(certificadoInterseccion.getFechaCreacion()));
            intersecta = certificadoInterseccionService.getValueInterseccionProyectoIntersecaCapas(proyectosBean.getProyecto());
            String fechaOficioInterseccion = "";
            String numeroOficioCertificado = "";
            String interseccion = "";
            oficioAprobacionTDRLA.setIntersecaZIZA("");
            if (intersecta) {
                fechaOficioInterseccion = JsfUtil
                        .getDayFromDate(certificadoInterseccion
                                .getFechaCreacion())
                        + " de "
                        + JsfUtil.devuelveMes(JsfUtil
                        .getMonthFromDate(certificadoInterseccion
                                .getFechaCreacion()))
                        + " del "
                        + JsfUtil.getYearFromDate(certificadoInterseccion
                        .getFechaCreacion());

                numeroOficioCertificado = certificadoInterseccion
                        .getCodigo();

                String interseca = certificadoInterseccionFacade
                        .getDetalleInterseccion(proyectosBean.getProyecto()
                                .getCodigo());

                interseccion = " interseca con: " + interseca;

                if (Boolean.parseBoolean(variables.get("intersecaZA").toString())) {
                    oficioAprobacionTDRLA.setIntersecaZIZA(generarTextoZonasIntangibles());
                }
            } else {
                interseccion = " no interseca ";

            }


            if (informeTecnicoTDRLA.getFechaOficioMJ() != null) {
                String baseOficio = "Oficio No. " + informeTecnicoTDRLA.getNumeroOficioMJ() +
                        " de fecha " + JsfUtil.devuelveFechaEnLetrasSinHora(informeTecnicoTDRLA.getFechaOficioMJ()) + " emitido por el Ministerio de Justicia, Derechos Humanos y Cultos";
                oficioAprobacionTDRLA.setBaseOficio(baseOficio);
            } else {
                oficioAprobacionTDRLA.setBaseOficio("");
            }
            oficioAprobacionTDRLA.setListaObservaciones(informeTecnicoTDRLA.getConclusionesRecomendaciones());


            oficioAprobacionTDRLA.setIntersecaProyecto(interseccion);
            String fechaProyecto = JsfUtil.getDayFromDate(proyectosBean
                    .getProyecto().getFechaRegistro())
                    + " de "
                    + JsfUtil.devuelveMes(JsfUtil
                    .getMonthFromDate(proyectosBean.getProyecto()
                            .getFechaCreacion()))
                    + " del "
                    + JsfUtil.getYearFromDate(proyectosBean.getProyecto()
                    .getFechaCreacion());

            oficioAprobacionTDRLA.setFechaInforme(JsfUtil
                    .devuelveFechaEnLetrasSinHora(new Date()));
            // oficioAprobacionTDRLA.setInterseccion(interseccion);

            // Llenar entity informe


            //Número de oficio


            oficioAprobacionTDRLA.setTratamientoReceptor(promotor.getPersona().getTipoTratos().getNombre());
            oficioAprobacionTDRLA.setNombreReceptor(oficioAprobacionTDRLA
                    .getNombreReceptor());
            oficioAprobacionTDRLA.setCargoReceptor(promotor.getPersona().getTitulo());
            // oficioAprobacionTDRLA.setEmpresa(empresa);
            // oficioAprobacionTDRLA.setLugarEntrega(lugarEntrega);
            oficioAprobacionTDRLA.setNumeroProyecto(proyectosBean.getProyecto()
                    .getCodigo());
            oficioAprobacionTDRLA.setFechaProyecto(JsfUtil.devuelveFechaEnLetrasSinHora(proyectosBean.getProyecto().getFechaModificacion()));
            oficioAprobacionTDRLA.setNombreProyecto(proyectosBean.getProyecto()
                    .getNombre());
            oficioAprobacionTDRLA.setProvinciaProyecto(nombreProvincia);
            oficioAprobacionTDRLA.setCantonProyecto(nombreCanton);
            //  oficioAprobacionTDRLA.setParroquiaProyecto(nombreParroquia);
            oficioAprobacionTDRLA.setNumeroOficioInterseccion(numeroOficioCertificado);
            oficioAprobacionTDRLA.setFechaOficioInterseccion(fechaOficioInterseccion);

            if (proyectosBean.getProyecto().getAreaResponsable().getTipoArea().getId().equals(3)) {
                oficioAprobacionTDRLA.setLugarFirma(proyectosBean.getProyecto().getAreaResponsable().getAreaName());
            } else {
                oficioAprobacionTDRLA.setLugarFirma("MINISTERIO DEL AMBIENTE Y AGUA");
            }

            if (!token) {
                oficioAprobacionTDRLA.setNombreRemitente(oficioAprobacionTDRLA
                        .getNombreRemitente());
                String remitenteStr = (remitente.getPersona().getPosicion() != null ? remitente.getPersona().getPosicion() : "");

                oficioAprobacionTDRLA.setCargoRemitente(remitenteStr);


            } else {
                oficioAprobacionTDRLA.setNombreRemitente("");
                oficioAprobacionTDRLA.setCargoRemitente("");
                oficioAprobacionTDRLA.setLugarFirma("");
            }


            nombreFichero = "OficioAprobacion" + proyectosBean.getProyecto().getId() + ".pdf";
            nombreReporte = "OficioAprobacion.pdf";

            byte[] firma = null;

            if (oficioAprobacion) {
                firma = categoriaIIFacade
                        .getFirmaSubsecretarioCalidadAmbiental();
            } else {
                firma = categoriaIIFacade
                        .getFirmaAutoridadPrevencion();
            }
            File informePdf;
            if (nombreNull == null) {
                //   document.add(DocumentoPDFPlantillaHtml
                //.agregarFirmaImagen(imagenFirma));
                informePdf = UtilGenerarInforme.generarFichero(
                        plantillaReporte.getHtmlPlantilla(), nombreReporte, true,
                        oficioAprobacionTDRLA, "<span style='color:red'>INGRESAR</span>", firma, false);
            } else {
                informePdf = UtilGenerarInforme.generarFichero(
                        plantillaReporte.getHtmlPlantilla(), nombreReporte, true,
                        oficioAprobacionTDRLA, nombreNull, firma, false);
            }
            setOficioPdf(informePdf);

            Path path = Paths.get(getOficioPdf().getAbsolutePath());
            archivoInforme = Files.readAllBytes(path);
            archivoFinal = new File(
                    JsfUtil.devolverPathReportesHtml(nombreFichero));

            FileOutputStream file = new FileOutputStream(archivoFinal);
            file.write(archivoInforme);
            file.close();
            setInformePath(JsfUtil.devolverContexto("/reportesHtml/"
                    + nombreFichero));

            pathPdf = informePdf.getParent();
        } catch (Exception e) {
            LOG.error("Error al visualizar el oficio", e);
            JsfUtil.addMessageError("Error al visualizar el oficio");
        }

        return pathPdf;
    }

    public void guardar() {
        try {
            informeOficioFacade.guardarInforme(oficioAprobacionTDRLA);
            String url = "/prevencion/licenciamiento-ambiental/documentos/oficioAprobacionTDR.jsf";
            if (tipo != null && !tipo.isEmpty()) {
                url += "?tipo=" + tipo;
            }
            JsfUtil.redirectTo(url);
        } catch (Exception e) {
            LOG.error("Error al guardar oficio", e);
            JsfUtil.addMessageError("Error al realizar la operación.");
        }
    }

    public void guardarRegresar() {
        try {
            String url = "/prevencion/licenciamiento-ambiental/documentos/informeTecnicoTDR.jsf";
            if (tipo != null && !tipo.isEmpty()) {
                url += "?tipo=" + tipo;
            }
            informeOficioFacade.guardarInforme(oficioAprobacionTDRLA);
            JsfUtil.redirectTo(url);
        } catch (Exception e) {
            LOG.error("Error al guardar oficio", e);
            JsfUtil.addMessageError("Error al realizar la operación.");
        }
    }

    public void validarTareaBpm() {

        String url = "/prevencion/licenciamiento-ambiental/documentos/informeTecnicoEia.jsf";
        if (tipo != null && !tipo.isEmpty()) {
            url += "?tipo=" + tipo;
        }
        //JsfUtil.validarPaginasUrlTareasBpm(bandejaTareasBean.getTarea(), url);
    }

    public String completarTarea() {
        try {

//            guardarFechas();
            if (tipo != null && tipo.equals("revisar")) {
                boolean validar = false;
                if (variables.get("requiereMoficacion") != null && Boolean.parseBoolean(variables.get("requiereMoficacion").toString())) {
                    requiereModificaciones = true;
                    validar = true;
                }
                if (validar || validarObservaciones(!requiereModificaciones)) {
                    visualizarOficio("");
                    informeOficioFacade.guardarInforme(oficioAprobacionTDRLA);
                    subirDocuemntoAlfresco();

                    Map<String, Object> params = new ConcurrentHashMap<String, Object>();

                    params.put("requiereMoficacion",
                            requiereModificaciones);
                    params.put("esPronunciamientoAprobacion",
                            oficioAprobacion);

                    procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean
                            .getTarea().getProcessInstanceId(), params);
                    Map<String, Object> data = new ConcurrentHashMap<String, Object>();
                    procesoFacade.aprobarTarea(loginBean.getUsuario(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), data);

                    JsfUtil.addMessageInfo("Se realizó correctamente la operación.");
                    return JsfUtil.actionNavigateTo("/bandeja/bandejaTareas.jsf");
                }

                return "";

            } else {
                informeOficioFacade.guardarInforme(oficioAprobacionTDRLA);
                Map<String, Object> data = new ConcurrentHashMap<String, Object>();
                procesoFacade.aprobarTarea(loginBean.getUsuario(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), data);

                JsfUtil.addMessageInfo("Se realizó correctamente la operación.");
            }
            try {
                archivoFinal.delete();
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
        List<ObservacionesFormularios> observaciones = observacionesController.getObservacionesBB().getMapaSecciones().get("informe");

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

    public StreamedContent getStream() throws Exception {
        DefaultStreamedContent content = new DefaultStreamedContent();
        if (archivoInforme != null) {
            content = new DefaultStreamedContent(new ByteArrayInputStream(
                    archivoInforme), "application/octet-stream");
            content.setName(nombreReporte);

        }
        return content;

    }

    public void subirDocuemntoAlfresco() throws Exception {
        licenciaAmbientalFacade.ingresarPronunciamiento(archivoFinal,
                proyectosBean.getProyecto().getId(),
                proyectosBean.getProyecto().getCodigo(),
                bandejaTareasBean.getProcessId(), bandejaTareasBean.getTarea().getTaskId(), tipoDocumentoSistema);

    }

    public String obtenerNuevoNumeroOficio() throws Exception {
        Area area = areaFacade.getAreaFull(proyectosBean.getProyecto().getAreaResponsable().getId());
        String base = "MAE";
      /*  if (!area.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_PC)) {
            base = "MAE-" + area.getAreaAbbreviation();
        }*/

        Long numeroSecuencia = secuenciasFacade
                .getNextValueDedicateSequence("oficio_aprobacion_tdr_la");
        String numeroInforme = base

                + "-"
                + JsfUtil.getCurrentYear()
                + "-SCA-"
                + (numeroSecuencia != null ? JsfUtil.rellenarCeros(
                numeroSecuencia.toString(),
                TEMP_CONST_TAMANIO_NUMERO_OFICIO) : "N/A")
               ;

        return numeroInforme;
    }

    public String getInstitucionEmpresa() throws ServiceException {
        Organizacion organizacion = organizacionFacade.buscarPorPersona(promotor.getPersona(),
                promotor.getNombre());
        nombrePromotor = promotor.getPersona().getNombre();
        if (organizacion != null) {
            nombrePromotor = organizacion.getNombre();
            return organizacion.getNombre();
        }
        return "";

    }


    public String generarTablaCoordenadas(
            ProyectoLicenciamientoAmbiental proyecto) {
        String strTable = "<table border=\"1\" cellpadding=\"10\" cellspacing=\"0\" class=\"w600Table\">"
                + "<tbody><tr BGCOLOR=\"#B2E6FF\">"
                + "<td><strong>COORDENADA X</strong></td><td><strong>COORDENADA Y</strong></td><td><strong>FORMA</strong></td></tr>";

        for (FormaProyecto frmProyecto : proyecto.getFormasProyectos()) {
            for (Coordenada coor : frmProyecto.getCoordenadas()) {
                strTable += "<tr>";
                strTable += "<td>" + coor.getX() + "</td>";
                strTable += "<td>" + coor.getY() + "</td>";
                strTable += "<td>" + frmProyecto.getTipoForma().getNombre()
                        + "</td>";
                strTable += "</tr>";
            }
        }
        strTable += "</tbody></table> <p style=\"text-align: -webkit-center;\"><b>Coordenadas WGS 84</b></p>";

        return strTable;
    }

    public String generarTextoZonasIntangibles() {
        StringBuilder sb = new StringBuilder();
        String noValue = "<span style='color:red;'>INGRESAR</span>";
        sb.append("Mediante Oficio No. ");
        sb.append(informeTecnicoTDRLA.getNumeroOficioSolicitud() != null ? informeTecnicoTDRLA.getNumeroOficioSolicitud() : noValue);
        sb.append(" de ");
        sb.append(informeTecnicoTDRLA.getFechaOficioSolicitud() != null ? informeTecnicoTDRLA.getFechaOficioSolicitud() : noValue);
        sb.append(" , el Ministerio del Ambiente y Agua"
        		+ " con base al certificado de intersección emitido mediante oficio No. ");
        sb.append(informeTecnicoTDRLA.getNumeroOficioSolicitud() != null ? informeTecnicoTDRLA.getNumeroOficioSolicitud() : noValue);
        sb.append(" de ");
        sb.append(informeTecnicoTDRLA.getFechaOficioSolicitud() != null ? JsfUtil.devuelveFechaEnLetrasSinHora(informeTecnicoTDRLA.getFechaOficioSolicitud()) : noValue);
        sb.append(", solicito al Ministerio de Justicia Derechos Humanos y Cultos pronunciamiento respecto del  proyecto: ");
        sb.append(informeTecnicoTDRLA.getNombreProyecto() != null ? informeTecnicoTDRLA.getNombreProyecto() : noValue);
        sb.append(" ubicado en la provincia de ");
        sb.append(provincia);
        sb.append(".");


        sb.append("Mediante Oficio No.");
        sb.append(informeTecnicoTDRLA.getNumeroOficioMJ() != null ? informeTecnicoTDRLA.getNumeroOficioMJ() : noValue);
        sb.append(" de ");
        sb.append(informeTecnicoTDRLA.getFechaOficioMJ() != null ? JsfUtil.devuelveFechaEnLetrasSinHora(informeTecnicoTDRLA.getFechaOficioMJ()) : noValue);

        sb.append(" el Ministerio de Justicia Derechos Humanos y Cultos emite pronunciamiento respecto del  proyecto: ");
        sb.append(informeTecnicoTDRLA.getNombreProyecto());
        sb.append(" ubicado en la provincia de ");
        sb.append(provincia);
        sb.append(".");


        return sb.toString();
    }

}
