package ec.gob.ambiente.prevencion.licenciamientoambiental.informes.bean;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.certificadointerseccion.service.CertificadoInterseccionFacade;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.*;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.observaciones.facade.ObservacionesFacade;
import ec.gob.ambiente.suia.prevencion.licenciamientoAmbiental.facade.InformeOficioFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.pronunciamiento.facade.PronunciamientoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilGenerarInforme;
import lombok.Getter;
import lombok.Setter;
import observaciones.ObservacionesController;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
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
public class InformeTecnicoTDRBean implements Serializable {

    // ///////////////////////
    // CONSTANTES
    // ///////////////////////
    public static final String TEMP_CONST_NOMBRE_SECUENCIA_NUMERO = "seq_number_technical_report_eia";
    public static final String TEMP_CONST_NOMBRE_ESQUEMA_SUIA = "suia_iii";
    public static final int TEMP_CONST_TAMANIO_NUMERO_OFICIO = 6;
    private static final long serialVersionUID = 3507988825742317084L;

    private final Logger LOG = Logger
            .getLogger(InformeTecnicoTDRBean.class);

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
    private InformeTecnicoTDRLA informeTecnicoTDRLA;

    @Getter
    @Setter
    private PlantillaReporte plantillaReporte;

    @Getter
    @Setter
    private String tipo = "";

    @Getter
    @Setter
    private boolean soloLectura;

    @Getter
    @Setter
    private boolean revisar;

    @Getter
    @Setter
    private Boolean intersecta;
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
    private Boolean pronunciamiento;


    @Getter
    @Setter
    private boolean requiereModificaciones;

    @Getter
    @Setter
    private List<Pronunciamiento> listaPronunciamiento;
    @Getter
    @Setter
    private Pronunciamiento pronunciamientoActivo;

    @Getter
    @Setter
    private String documentOffice = "";

    @Getter
    @Setter
    private String provincia = "";

    @Getter
    @Setter
    private String promotor = "";
    @Getter
    @Setter
    private Map<String, Object> variables;
    @Getter
    @Setter
    private Usuario responsable;
    @EJB
    private InformeOficioFacade informeOficioFacade;

    @EJB
    private ObservacionesFacade observacionesFacade;

    @EJB
    private UsuarioFacade usuarioFacade;

    @EJB
    private AreaFacade areaFacade;

    @EJB
    private OrganizacionFacade organizacionFacade;


    @EJB
    private PronunciamientoFacade pronunciamientoFacade;

    @EJB
    private ProcesoFacade procesoFacade;

    @EJB
    private DocumentosFacade documentosFacade;


    @EJB
    private CertificadoInterseccionFacade certificadoInterseccionFacade;

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
        try {
            variables = procesoFacade
                    .recuperarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getTarea()
                            .getProcessInstanceId());

            cargarPronunciamientos();
            Map<String, String> params = FacesContext.getCurrentInstance()
                    .getExternalContext().getRequestParameterMap();
            if (params.containsKey("tipo")) {
                tipo = params.get("tipo");
                if (params.get("tipo").equals("revisar")) {
                    revisar = true;

                }

                if (variables.containsKey("esPronunciamientoAprobacion")) {
                    pronunciamiento = Boolean.parseBoolean(variables.get("esPronunciamientoAprobacion").toString());
                }
            }

            tipoDocumento = new TipoDocumento();
            tipoDocumento.setId(TipoDocumentoSistema.TIPO_INFORME_TECNICO_TDR_LA.getIdTipoDocumento());


            plantillaReporte = informeOficioFacade
                    .obtenerPlantillaReporte(this.getTipoDocumento().getId());
            informeTecnicoTDRLA = informeOficioFacade
                    .obtenerInformeTecnicoTDRLAPorProyecto(TipoDocumentoSistema.TIPO_INFORME_TECNICO_TDR_LA,
                            proyectosBean.getProyecto().getId());


            if (informeTecnicoTDRLA == null) {
                informeTecnicoTDRLA = new InformeTecnicoTDRLA();


                Area area = areaFacade.getAreaFull(proyectosBean.getProyecto().getAreaResponsable().getId());
                String base = "DNPCA";
                if (!area.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_PC)) {
                    base = area.getAreaAbbreviation();
                }
                BigInteger numeroSecuencia = informeOficioFacade
                        .obtenerNumeroInforme(TEMP_CONST_NOMBRE_SECUENCIA_NUMERO,
                                TEMP_CONST_NOMBRE_ESQUEMA_SUIA);
                String numeroInforme =
                        (numeroSecuencia != null ? JsfUtil.rellenarCeros(numeroSecuencia.toString(), TEMP_CONST_TAMANIO_NUMERO_OFICIO) : "N/A") +
                                "-" +
                                JsfUtil.getCurrentYear() +
                                "-SUIA-ULA-DNPCA-SCA-MA";

                informeTecnicoTDRLA.setNumeroOficio(numeroInforme);
                informeTecnicoTDRLA.setProyectoLicenciamientoAmbiental(proyectosBean.getProyecto());
                informeTecnicoTDRLA.setTipoDocumento(tipoDocumento);
                informeOficioFacade.guardarInforme(informeTecnicoTDRLA);

            } else {
                if (informeTecnicoTDRLA.getPronunciamiento() != null && !informeTecnicoTDRLA.getPronunciamiento().isEmpty()) {
                    pronunciamiento = informeTecnicoTDRLA.getPronunciamiento().equals("Aprobación");
                }
            }

            String nombrePromotor = variables.get("u_TecnicoResponsable").toString();
            responsable = usuarioFacade.buscarUsuarioCompleta(nombrePromotor);
            visualizarOficio();
        } catch (Exception e) {
            LOG.error("Error al inicializar:  ", e);
            JsfUtil.addMessageError(JsfUtil.ERROR_INICIALIZAR_DATOS);
        }
    }

    public void cargarPronunciamientos() {
        listaPronunciamiento =
                pronunciamientoFacade.getPronunciamientosPorClase("Pronunciamiento", Long.parseLong(proyectosBean.getProyecto().getId().toString()));


        try {
            documentOffice = documentosFacade.direccionDescarga(Pronunciamiento.class.getSimpleName(),
                    proyectosBean.getProyecto().getId(), TipoDocumentoSistema.PRONUNCIAMIENTO_MINISTERIO_JUSTICIA);
        } catch (CmisAlfrescoException e) {
            LOG.error(e);
        } catch (ServiceException e) {
            LOG.error(e);
        }


    }

    public String visualizarOficio() {
        String pathPdf = null;
        try {
            informeTecnicoTDRLA.setElaboradoPor(responsable.getPersona().getNombre());

//            informeTecnicoTDRLA.setFichaTecnica(JsfUtil
//                    .devuelveFechaEnLetrasSinHora(new Date()));
            informeTecnicoTDRLA.setFichaTecnica(generarFichaTecnica());
            informeTecnicoTDRLA.setFechaProyecto(JsfUtil.devuelveFechaEnLetrasSinHora(proyectosBean.getProyecto().getFechaRegistro()));
            informeTecnicoTDRLA.setNombrePromotor(getProponente());
            informeTecnicoTDRLA.setNombreProyecto(proyectosBean.getProyecto().getNombre());
            informeTecnicoTDRLA.setCodigoProyecto(proyectosBean.getProyecto().getCodigo());
            informeTecnicoTDRLA.setProvinciaProyecto(provincia);
            CertificadoInterseccion certificadoInterseccion = certificadoInterseccionFacade.recuperarCertificadoInterseccion(proyectosBean.getProyecto());
            informeTecnicoTDRLA.setFechaCoordenadaProyecto(JsfUtil.devuelveFechaEnLetrasSinHora(certificadoInterseccion.getFechaCreacion()));


            intersecta = certificadoInterseccionService.getValueInterseccionProyectoIntersecaCapas(proyectosBean.getProyecto());

            String intersectaS = intersecta ? "<b>INTERSECTA</b>" : "<b>NO INTERSECTA</b>";

            informeTecnicoTDRLA.setIntersecaZIZA("");
            if (intersecta) {
                String zonasInterseca = certificadoInterseccionFacade.getDetalleInterseccion(proyectosBean.getProyecto().getCodigo(), "", ",", false);
                intersectaS += "  con el " + zonasInterseca;
                if (Boolean.parseBoolean(variables.get("intersecaZA").toString())) {
                    informeTecnicoTDRLA.setIntersecaZIZA(generarTextoZonasIntangibles());
                }
            }
            informeTecnicoTDRLA.setIntersecaProyecto(intersectaS);
            informeTecnicoTDRLA.setCoordenadasProyecto(generarTablaCoordenadas(proyectosBean.getProyecto()));

            informeTecnicoTDRLA.setNumeroOficioInterseccion(certificadoInterseccion.getCodigo());
            informeTecnicoTDRLA.setFechaOficioInterseccion(JsfUtil.devuelveFechaEnLetrasSinHora(certificadoInterseccion.getFechaCreacion()));


            nombreFichero = "InformeTecnico.pdf";
            nombreReporte = "InformeTecnico.pdf";


            File informePdf = UtilGenerarInforme.generarFichero(
                    plantillaReporte.getHtmlPlantilla(), nombreReporte, true,
                    informeTecnicoTDRLA);
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

    public String generarFichaTecnica() throws Exception {

        List<ProyectoBloque> proyectoBloques = proyectosBean.getProyecto().getProyectoBloques();
        String bloques = "";
        for (ProyectoBloque pb : proyectoBloques) {
            if (!bloques.isEmpty()) {
                bloques += ", ";
            }
            bloques += pb.getBloque().getNombre();
        }
        StringBuilder fichaTecnica = new StringBuilder();

        fichaTecnica.append("<div  style='font-size:14px; text-align:center;'> <div> <strong  style='font-size:14px;'>FICHA TÉCNICA" +
                "</strong></div><div>  <div> " +
                "<table style=\"width:730px;border:1px;\"> <tbody> ");

        //---
        fichaTecnica.append("<tr> <td style=\"text-align: left; vertical-align: top;font-size:14px;width:180px;border:1px;\">" +
                "<strong  style='font-size:14px'>Número de Bloque:</strong></td> ");

        fichaTecnica.append("<td  style='font-size:14px;vertical-align: top;border:1px;text-align: left;'>  ");
        fichaTecnica.append(bloques);
        fichaTecnica.append(" </td> ");
        //----
        fichaTecnica.append("</tr> <tr> <td style=\"text-align: left; vertical-align: top;font-size:14px;width:180px;border:1px;\">" +
                "<strong  style='font-size:14px'>Nombre proyecto</strong></td> ");

        fichaTecnica.append("<td  style='font-size:14px;vertical-align: top;border:1px;text-align: left;'>  ");
        fichaTecnica.append(proyectosBean.getProyecto().getNombre());//nombre del proyecto
        fichaTecnica.append(" </td> ");
        //----

        fichaTecnica.append("</tr> <tr> <td style=\"text-align: left; vertical-align: top;font-size:14px;width:180px;border:1px;\">" +
                "<strong  style='font-size:14px'>Ubicación Político Administrativa</strong></td> ");

        fichaTecnica.append("<td  style='font-size:14px;vertical-align: top;border:1px;text-align: left;vertical-align:top;'>  ");
        fichaTecnica.append(getUbicacionGeografica());
        fichaTecnica.append(" </td> ");

        //----

        fichaTecnica.append("</tr> <tr> <td style=\"text-align: left; vertical-align: top;font-size:14px;width:180px;border:1px;\">" +
                "<strong  style='font-size:14px'>Ubicación Cartográfica</strong></td> ");

        fichaTecnica.append("<td  style='font-size:14px;vertical-align: top;border:1px;text-align: left;vertical-align:top;'>  ");
        fichaTecnica.append("CORRESPONDE A LAS COORDENADAS QUE SE PRESENTAN  EN EL NUMERAL 2 ANTECEDENTES – CERTIFICADO DE INTERSECCIÓN");
        fichaTecnica.append(" </td> ");

        //----

        fichaTecnica.append("</tr> <tr> <td style=\"text-align: left; vertical-align: top;font-size:14px;width:180px;border:1px;\">" +
                "<strong  style='font-size:14px'>Fase de operaciones</strong></td> ");

        fichaTecnica.append("<td  style='font-size:14px;vertical-align: top;border:1px;text-align: left;vertical-align:top;'>  ");
        fichaTecnica.append(proyectosBean.getProyecto().getCatalogoCategoria().getFase().getNombre());
        fichaTecnica.append(" </td> ");

        fichaTecnica.append("</tr> <tr> <td style=\"text-align: left; vertical-align: top;font-size:14px;width:180px;border:1px;\">" +
                "<strong  style='font-size:14px'>Área o superficie</strong></td> ");

        fichaTecnica.append("<td  style='font-size:14px;vertical-align: top;border:1px;text-align: left;vertical-align:top;'>  ");
        fichaTecnica.append(proyectosBean.getProyecto().getArea());
        fichaTecnica.append(" </td> </tr> ");

        fichaTecnica.append("<tr> <td style=\"text-align: left; vertical-align: top;font-size:14px;width:180px;border:1px;\">" +
                "<strong  style='font-size:14px'>Composición del Equipo Técnico</strong></td> ");

        String equipoTecnico = "";
        /*
         u_TecnicoSocial
        u_TecnicoCartografo
        u_TecnicoElectrico
        u_TecnicoMineria
        u_TecnicoOtrosSectores
        u_TecnicoGeneral
        * */
        String[] equipoTec = new String[]{"u_TecnicoSocial",
                "u_TecnicoCartografo",
                "u_TecnicoElectrico",
                "u_TecnicoMineria",
                "u_TecnicoOtrosSectores",
                "u_TecnicoGeneral"};

        for(String tec: equipoTec){
            if(variables.containsKey(tec)){
               Usuario u= usuarioFacade.buscarUsuarioCompleta((String) variables.get(tec));
                if(!equipoTecnico.isEmpty()){
                    equipoTecnico +=", ";
                }

                equipoTecnico += u.getPersona().getNombre();
            }
        }
        fichaTecnica.append("<td  style='font-size:14px;vertical-align: top;border:1px;text-align: left;vertical-align:top;'>  ");
        fichaTecnica.append(equipoTecnico);
        fichaTecnica.append(" </td> </tr> ");

        fichaTecnica.append("</tbody> </table> <p></p> </div> </div> </div> ");//<div> <div> <div> <div> <div> <div>
/*
        fichaTecnica.append("<p> <strong style='font-size:14px'>Direcci&oacute;n del proyecto, obra o actividad:</strong></p>" +
                "</div> </div> </div> </div> <div>" +
                "<div style='font-size:14px'>");
        fichaTecnica.append(proyectosBean.getProyecto().getDireccionProyecto());
        fichaTecnica.append("</div> <div> &nbsp;</div> </div></div> </div> <div> <div>");
        fichaTecnica.append("<strong style='font-size:14px;vertical-align: top;'>Detalle del proyecto</strong></div>" +
                "<div> <div> <div> <table> <tbody> <tr> <td>" +
                "<strong style='font-size:14px;vertical-align: top;'>Sector</strong></td> <td style='font-size:14px;vertical-align: top;'>");
        fichaTecnica.append(proyectosBean.getProyecto().getTipoSector());
        fichaTecnica.append("</td>" +
                "</tr> <tr> <td> <strong style='font-size:14px;vertical-align: top;'>Superficie</strong></td >" +
                "<td style='font-size:14px;vertical-align: top;'> ");

        fichaTecnica.append(proyectosBean.getProyecto().getArea());
        fichaTecnica.append(proyectosBean.getProyecto().getUnidad());
        fichaTecnica.append("</td> </tr> <tr> <td> <strong style='font-size:14px;vertical-align: top;'>Altitud</strong></td><td style='font-size:14px;vertical-align: top;'>");

        fichaTecnica.append(proyectosBean.getProyecto().getAltitud());

        fichaTecnica.append("</td> </tr> </tbody> </table> </div> </div> </div> </div> ");

*/
        return fichaTecnica.toString();
    }

    public String getProponente() throws ServiceException {
        if (promotor.isEmpty()) {
            String label = proyectosBean.getProyecto().getUsuario().getPersona().getNombre();
            promotor = label;
            Organizacion organizacion = organizacionFacade.buscarPorPersona(proyectosBean.getProyecto().getUsuario().getPersona(),
                    proyectosBean.getProyecto().getUsuario().getNombre());
            if (organizacion != null) {
                promotor = organizacion.getNombre();
                return organizacion.getNombre();
            }
            return label;
        } else {
            return promotor;
        }
    }


    public String getUbicacionGeografica() throws Exception {

        StringBuilder ubicacionG = new StringBuilder();


        String provincia = "";
        String canton = "";
        String parroquia = "";
        for (UbicacionesGeografica ubicacion : proyectosBean.getProyecto().getUbicacionesGeograficas()) {
            provincia = ubicacion.getUbicacionesGeografica()
                    .getUbicacionesGeografica().getNombre();
            canton = ubicacion.getUbicacionesGeografica().getNombre();
            parroquia = ubicacion.getNombre();
            if (this.provincia.isEmpty()) {
                this.provincia = provincia;
            }

            break;
        }
        ubicacionG.append("<table>  " +
                "<tr> <td style='font-size:14px;border:1px;'>" +
                "Provincia </td> <td style='font-size:14px;border:1px;'>" + provincia + "</td></tr>" +
                "<tr> <td style='font-size:14px;border:1px;'>" +
                "Cant&oacute;n  </td> <td style='font-size:14px;border:1px;'> " + canton + "</td></tr>" +
                "<tr> <td style='font-size:14px;border:1px;'>" +
                "Parroquia </td> <td style='font-size:14px;border:1px;'> " + parroquia + "</td></tr>");
        ubicacionG.append(" </table>");


        return ubicacionG.toString();
    }


    public void guardar() {
        try {
            informeTecnicoTDRLA.setPronunciamiento(pronunciamiento ? "Aprobación" : "Observación");

            informeOficioFacade.guardarInforme(informeTecnicoTDRLA);
            String url = "/prevencion/licenciamiento-ambiental/documentos/informeTecnicoTDR.jsf";
            if (!tipo.isEmpty()) {
                url += "?tipo=" + tipo;
            }
            JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
            JsfUtil.redirectTo(url);
        } catch (Exception e) {
            LOG.error("Error al guardar oficio", e);
        }
    }


    public void guardarContinuar() {
        try {
            StringBuilder functionJs = new StringBuilder();
            Boolean validado = true;
            if (informeTecnicoTDRLA.getCaracteristicasImportantes() == null || informeTecnicoTDRLA.getCaracteristicasImportantes().isEmpty()) {
                validado = false;
                JsfUtil.addMessageError("El campo '3. CARACTERÍSTICAS IMPORTANTES DEL PROYECTO' es requerido.");

                functionJs.append("highlightComponent('form:caracteristicasIP');");
            }
            if (informeTecnicoTDRLA.getComentarioObservaciones() == null || informeTecnicoTDRLA.getComentarioObservaciones().isEmpty()) {
                validado = false;
                JsfUtil.addMessageError("El campo '4. COMENTARIOS U OBSERVACIONES' es requerido.");

                functionJs.append("highlightComponent('form:comentariosObservaciones');");
            }
            if (informeTecnicoTDRLA.getConclusionesRecomendaciones() == null || informeTecnicoTDRLA.getConclusionesRecomendaciones().isEmpty()) {
                validado = false;
                JsfUtil.addMessageError("El campo '5. CONCLUSIONES Y RECOMENDACIONES' es requerido.");
                functionJs.append("highlightComponent('form:conclisionesRecomen');");
            }
            if (!validado) {
                RequestContext.getCurrentInstance().execute("removeHighLightComponent('form:caracteristicasIP');" +
                        "removeHighLightComponent('form:comentariosObservaciones');" +
                        "removeHighLightComponent('form:conclisionesRecomen');");
                RequestContext.getCurrentInstance().execute(functionJs.toString());
                return;
            }
            informeTecnicoTDRLA.setPronunciamiento(pronunciamiento ? "Aprobación" : "Observación");
            informeOficioFacade.guardarInforme(informeTecnicoTDRLA);

            String tipoOficio = pronunciamiento ? "oficioAprobacionTDR.jsf" : "oficioObservacionTDR.jsf";


            String url = "/prevencion/licenciamiento-ambiental/documentos/" + tipoOficio;
            if (!tipo.isEmpty()) {
                url += "?tipo=" + tipo;
            }

            if (revisar) {

                if (validarObservaciones(!requiereModificaciones)) {
                    Map<String, Object> params = new ConcurrentHashMap<String, Object>();
                    params.put("requiereMoficacion",
                            requiereModificaciones);

                    procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean
                            .getTarea().getProcessInstanceId(), params);


                    JsfUtil.redirectTo(url);
                }

            } else {
                JsfUtil.redirectTo(url);
            }
        } catch (Exception e) {
            LOG.error("Error al guardar oficio", e);
            JsfUtil.addMessageInfo("Error al realizar la operación.");
        }
    }

    public void guardarContinuar(String antes) {
        try {
            informeTecnicoTDRLA.setPronunciamiento(pronunciamiento ? "Aprobación" : "Observación");
            informeOficioFacade.guardarInforme(informeTecnicoTDRLA);
            String tipoOficio = pronunciamiento ? "oficioAprobacionTDR.jsf" : "oficioObservacionTDR.jsf";
            String url = "/prevencion/licenciamiento-ambiental/documentos/" + tipoOficio;
            if (tipo != null && !tipo.isEmpty()) {
                url += "?tipo=" + tipo;
            }
            if (revisar) {
                if (validarObservaciones(!requiereModificaciones)) {
                    Map<String, Object> params = new ConcurrentHashMap<String, Object>();
                    params.put("requiereMoficacion",
                            requiereModificaciones);

                    procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean
                            .getTarea().getProcessInstanceId(), params);


                    JsfUtil.redirectTo(url);
                } else {
                    JsfUtil.redirectTo(url);
                }
            }


        } catch (Exception e) {
            LOG.error("Error al guardar oficio", e);
            JsfUtil.addMessageInfo("Error al realizar la operación.");
        }

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

    public void validarTareaBpm() {

        String url = "/prevencion/licenciamiento-ambiental/documentos/informeTecnicoTDR.jsf";
        if (!tipo.isEmpty()) {
            url += "?tipo=" + tipo;
        }
        //JsfUtil.validarPaginasUrlTareasBpm(bandejaTareasBean.getTarea(), url);
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
        sb.append(" , el Ministerio del Ambiente y Agua con base al certificado de intersección emitido mediante oficio No. ");
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

    public Date obtenerFechaAcual() {
        return new Date();
    }

    public void actualizarFechas() {

        if (informeTecnicoTDRLA.getFechaOficioSolicitud().compareTo(informeTecnicoTDRLA.getFechaOficioMJ()) > 0) {
            informeTecnicoTDRLA.setFechaOficioMJ(new Date());
            RequestContext.getCurrentInstance().reset(":form:fechaOficioSolicitud");
        }
    }

}
