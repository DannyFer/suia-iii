package ec.gob.ambiente.prevencion.licenciamientoambiental.informes.bean;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import lombok.Getter;
import lombok.Setter;
import observaciones.ObservacionesController;

import org.apache.log4j.Logger;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.log.SysoCounter;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.certificadointerseccion.service.CertificadoInterseccionFacade;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.CertificadoInterseccion;
import ec.gob.ambiente.suia.domain.ConcesionMinera;
import ec.gob.ambiente.suia.domain.Coordenada;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.domain.FormaProyecto;
import ec.gob.ambiente.suia.domain.InformeTecnicoEia;
import ec.gob.ambiente.suia.domain.InformeTecnicoGeneralLA;
import ec.gob.ambiente.suia.domain.LicenciaAmbiental;
import ec.gob.ambiente.suia.domain.ObservacionesFormularios;
import ec.gob.ambiente.suia.domain.OficioAprobacionEia;
import ec.gob.ambiente.suia.domain.OficioObservacionEia;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.ProyectoUbicacionGeografica;
import ec.gob.ambiente.suia.domain.RolUsuario;
import ec.gob.ambiente.suia.domain.TipoDocumento;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.eia.facade.EstudioImpactoAmbientalFacade;
import ec.gob.ambiente.suia.eia.facade.LicenciaAmbientalFacade;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.observaciones.facade.ObservacionesFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.ProyectoLicenciaAmbientalFacade;
import ec.gob.ambiente.suia.prevencion.licenciamientoAmbiental.facade.InformeOficioFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.reportes.LicenciaAmbientalGeneralMunicipal;
import ec.gob.ambiente.suia.reportes.LicenciaAmbientalGeneralProvincial;
import ec.gob.ambiente.suia.reportes.LicenciaAmbientalMineriaMunicipal;
import ec.gob.ambiente.suia.reportes.LicenciaAmbientalProvincialMineria;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.BeanLocator;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilGenerarInforme;
import ec.gob.ambiente.suia.webservice.facade.AsignarTareaFacade;

@ManagedBean
@ViewScoped
public class InformeTecnicoGeneralLABean implements Serializable {

    // ///////////////////////
    // CONSTANTES
    // ///////////////////////
    public static final String TEMP_CONST_NOMBRE_SECUENCIA_NUMERO = "seq_number_technical_report_eia";
    public static final String TEMP_CONST_NOMBRE_ESQUEMA_SUIA = "suia_iii";
    public static final int TEMP_CONST_TAMANIO_NUMERO_OFICIO = 6;    /**
     *
     */
    private static final long serialVersionUID = 165683211928358047L;

    private final Logger LOG = Logger
            .getLogger(InformeTecnicoGeneralLABean.class);

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
    private  Map<String, Object> variables ;

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
    private EstudioImpactoAmbientalFacade estudioImpactoAmbientalFacade;
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

    @EJB
    private CertificadoInterseccionFacade certificadoInterseccionFacade;
    
    @EJB
    private AsignarTareaFacade asignarTareaFacade;

    @EJB
    private ObservacionesFacade observacionesFacade;

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
    /**
     * Nombre:SUIA
     * Descripción: Parámetro para visualizar las plantilla
     * ParametrosIngreso:
     * PArametrosSalida:
     * Fecha:16/08/2015
     */

    @Getter
    @Setter
    private String plantillaHtml;
    
    @Getter
    @Setter
    private boolean cargarLicencia;
    
    @Getter
    @Setter
    private boolean verOficio;

    /**
     * FIN Parámetro para visualizar las plantilla
     */

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

    }

    public void configuracionGeneral() {
        try {
            tipoDocumento = new TipoDocumento();
            tipoDocumento.setId(TipoDocumentoSistema.TIPO_INFORME_TECNICO_GENERAL_LA.getIdTipoDocumento());

             variables = procesoFacade
                    .recuperarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getTarea()
                            .getProcessInstanceId());
            Integer idProyecto = Integer.parseInt((String) variables
                    .get(Constantes.ID_PROYECTO));

            /**
             * Nombre:SUIA
             * Descripción: Llenar la plantilla de la resolución de acuerdo al ente acreditado
             * ParametrosIngreso:
             * PArametrosSalida:
             * Fecha:16/08/2015
             */

            proyecto = proyectosBean.getProyecto();
            
            informeTecnicoGeneralLA = informeOficioFacade
                    .obtenerInformeTecnicoLAGeneralPorProyectoId(tipoDocumento.getId(),
                            idProyecto);
            
            BigInteger numeroSecuencia=null;
            String numeroInforme;
            
            if(informeTecnicoGeneralLA!=null && informeTecnicoGeneralLA.getNumeroResolucion()!=null)
            {
            	numeroInforme=informeTecnicoGeneralLA.getNumeroResolucion();
            }else{
            	String base ="";
            	/*if (!proyecto.getAreaResponsable().getArea().getTipoArea().getId().equals(3)){
            		base = " MAAE-DNPCA";
            	}else{
            		base = proyecto.getAreaResponsable().getArea().getAreaAbbreviation();
            	}*/
            	if (proyecto.getAreaResponsable().getArea() == null) {
            		base = proyecto.getAreaResponsable().getAreaAbbreviation();
				} else {
					if (!proyecto.getAreaResponsable().getArea().getTipoArea().getId().equals(3)){
	            		base = Constantes.SIGLAS_INSTITUCION + "-DNPCA";
	            	}else{
	            		base = proyecto.getAreaResponsable().getArea().getAreaAbbreviation();
	            	}
				}
              
                numeroSecuencia = informeOficioFacade
                        .obtenerNumeroInforme(TEMP_CONST_NOMBRE_SECUENCIA_NUMERO,
                                TEMP_CONST_NOMBRE_ESQUEMA_SUIA);
                numeroInforme = base
                        + "-"
                        + JsfUtil.getCurrentYear()
                        + "-"
                        + (numeroSecuencia != null ? JsfUtil.rellenarCeros(
                        numeroSecuencia.toString(),
                        TEMP_CONST_TAMANIO_NUMERO_OFICIO) : "N/A");
            }
            
            
            
            if (proyecto.getAreaResponsable().getTipoEnteAcreditado() != null 
            		&& !proyecto.getAreaResponsable().getTipoEnteAcreditado().equals("ZONAL")) {
    			if (proyecto.getAreaResponsable().getTipoEnteAcreditado().equals("GOBIERNO")) {
    				if (variables.get("codigoSector").toString().equals("2")) {
    					LicenciaAmbientalProvincialMineria lapm = BeanLocator.getInstance(LicenciaAmbientalProvincialMineria.class);
    					plantillaHtml = lapm.visualizarOficio(proyecto.getCodigo(), numeroInforme);
    				} else {
    					LicenciaAmbientalGeneralProvincial lagp = BeanLocator.getInstance(LicenciaAmbientalGeneralProvincial.class);
    					plantillaHtml = lagp.visualizarOficio(proyecto.getCodigo(), numeroInforme);
    				}
    			} else if (proyecto.getAreaResponsable()
    					.getTipoEnteAcreditado().equals("MUNICIPIO")) {
    				if (variables.get("codigoSector").toString().equals("2")) {
    					LicenciaAmbientalMineriaMunicipal lamm = BeanLocator.getInstance(LicenciaAmbientalMineriaMunicipal.class);
    					plantillaHtml = lamm.visualizarOficio(proyecto.getCodigo(), numeroInforme);
    				} else {
    					LicenciaAmbientalGeneralMunicipal lagm = BeanLocator.getInstance(LicenciaAmbientalGeneralMunicipal.class);
    					plantillaHtml = lagm.visualizarOficio(proyecto.getCodigo(), numeroInforme);
    				}
    			}
    		} else {
    			plantillaReporte = informeOficioFacade.obtenerPlantillaReporte(this.getTipoDocumento().getId());
    			plantillaHtml = plantillaReporte.getHtmlPlantilla();
    		}



            /**
             * FIN Llenar la plantilla de la resolución de acuerdo al ente acreditado
             */

            String nombrePromotor = variables.get("u_Promotor").toString();

            promotor = usuarioFacade.buscarUsuarioCompleta(nombrePromotor);

            remitente = usuarioFacade.buscarUsuarioCompleta(loginBean.getNombreUsuario());
            proyecto = proyectosBean.getProyecto();

           

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
                Area area = areaFacade.getAreaFull(proyectosBean.getProyecto().getAreaResponsable().getId());

                informeTecnicoGeneralLA.setNumeroOficio(numeroInforme);
                informeTecnicoGeneralLA.setNumeroLicencia(numeroSecuencia.toString());
                informeTecnicoGeneralLA.setNumeroResolucion(numeroInforme);
                informeTecnicoGeneralLA.setCodigoProyecto(proyecto.getCodigo());

                CertificadoInterseccion certificado = certificadoInterseccionFacade
                        .recuperarCertificadoInterseccion(proyecto);
                Boolean intersecta = certificadoInterseccionFacade.getValueInterseccionProyectoIntersecaCapas(proyecto);
                informeTecnicoGeneralLA.setNumeroOficioCI(certificado.getCodigo());

                informeTecnicoGeneralLA.setFechaOficioCI(JsfUtil.getDateFormat(certificado.getFechaCreacion()));
                String intersectaS = intersecta ? "intersecta" : "no intersecta";

                if (intersecta) {
                    String zonasInterseca = certificadoInterseccionFacade.getDetalleInterseccion(proyecto.getCodigo());
                    intersectaS = " con el " + zonasInterseca;
                }
                informeTecnicoGeneralLA.setInterseca(intersectaS);

                EstudioImpactoAmbiental eia = estudioImpactoAmbientalFacade
                        .obtenerPorProyecto(proyecto);

                informeTecnicoGeneralLA.setFechaTDR(JsfUtil.devuelveFechaEnLetrasSinHora(eia.getFechaModificacion()));

                informeTecnicoGeneralLA.setFechaRevisionEIA(JsfUtil.devuelveFechaEnLetrasSinHora(eia.getFechaModificacion()));

                OficioAprobacionEia oficioAprobacionEia = informeOficioFacade
                        .obtenerOficioAprobacionEiaPorEstudio(
                                TipoDocumentoSistema.TIPO_OFICIO_APROBACION_EIA,
                                eia.getId());
                if (oficioAprobacionEia != null) {
                    informeTecnicoGeneralLA.setNumeroOficioSCA(oficioAprobacionEia.getNumeroOficio());
                    informeTecnicoGeneralLA.setFechaOficioSCA(oficioAprobacionEia.getFechaInforme());
                    informeTecnicoGeneralLA.setActividadesRelacionadas(oficioAprobacionEia.getActividades() != null ? oficioAprobacionEia.getActividades() : "");
                    generarObservacionesEIA(eia);
                }//obligaciones
                else {
                	Integer cantidadNotificaciones = Integer.parseInt(variables.get("cantidadNotificaciones").toString());
                    InformeTecnicoEia informeTecnicoEia = informeOficioFacade
                            .obtenerInformeTecnicoEiaPorEstudio(TipoDocumentoSistema.TIPO_INFORME_TECNICO_EIA,
                                    eia.getId(),cantidadNotificaciones);
                    informeTecnicoGeneralLA.setObligaciones(informeTecnicoEia.getOtrasObligaciones());
                }
                if (informeTecnicoGeneralLA.getObligaciones() == null) {
                    informeTecnicoGeneralLA.setObligaciones("");
                }

                informeOficioFacade.guardarInformeTecnicoGeneralLA(informeTecnicoGeneralLA);

            }
            cargarDatos();
            cargarLicencia=true;
        } catch (Exception e) {
            LOG.error("Error al inicializar: Informe técnigo general: ", e);
            JsfUtil.addMessageError(JsfUtil.ERROR_INICIALIZAR_DATOS);
        }
    }

    public void cargarDatos() {

//        Area area = areaFacade.getAreaFull(proyectosBean.getProyecto().getAreaResponsable().getId());

        Usuario responsable = usuarioFacade.buscarUsuarioCompleta(loginBean.getUsuario().getNombre());

        informeTecnicoGeneralLA.setCiudadInforme(responsable.getPersona().getUbicacionesGeografica().getUbicacionesGeografica().getNombre());
        // informeTecnicoGeneralLA.setCiudadInforme(area.getUbicacionesGeografica().getNombre());

        informeTecnicoGeneralLA.setFechaInforme(JsfUtil
                .devuelveFechaEnLetrasSinHora(new Date()));
        informeTecnicoGeneralLA.setFechaFirma(JsfUtil
                .devuelveFechaEnLetrasSinHora(new Date()));
        try {
            Usuario ministra = areaFacade.getMinistra();
            //informeTecnicoGeneralLA.setNumeroResolucion(numeroInforme);
            String nombreFirma = variables.get("u_MaximaAutoridad").toString();
            Usuario maximaAutoridad =actualizarAutoridad(nombreFirma);
            //informeTecnicoGeneralLA.setNombreMinistra(ministra.getPersona().getNombre());
            informeTecnicoGeneralLA.setNombreMinistra(maximaAutoridad.getPersona().getNombre());
            informeTecnicoGeneralLA.setCargoMinistra(proyecto.getAreaResponsable().getArea().getAreaName());
            //informeTecnicoGeneralLA.setCargoMinistra("Ministro del Ambiente");

            informeTecnicoGeneralLA.setNombreProyecto(proyecto.getNombre());
            configurarDirecciones();

            informeTecnicoGeneralLA.setFechaRegistroProyecto(JsfUtil.getDateFormat(proyecto.getFechaRegistro()));


            informeTecnicoGeneralLA.setNombreEmpresaProponente(getInfoToShow(proyecto.getUsuario()));
            informeTecnicoGeneralLA.setNombreCompletoRepresentanteLegal(informeTecnicoGeneralLA.getNombreEmpresaProponente());
            informeTecnicoGeneralLA.setCoordenadasProyecto(generarTablaCoordenadas(proyecto));
            informeTecnicoGeneralLA.setCodigoProyecto(proyecto.getCodigo());

        } catch (ServiceException e) {
            LOG.error(e);
        } catch (Exception e) {
            LOG.error(e);
        }

    }

    public String visualizarOficio() {
        if(!cargarLicencia)
    	configuracionGeneral();
        return visualizarOficio(false);
        
    }

    public String visualizarOficio(Boolean token) {
    	String pathPdf=null;
    	if(!verOficio){
        try {
            if (!token) {
                String nombreFirma = variables.get("u_MaximaAutoridad").toString();
                Usuario maximaAutoridad=actualizarAutoridad(nombreFirma);                	
                String firma = "<br/><br/><br/><br/>"+maximaAutoridad.getPersona().getNombre();
                String remitenteStr = (maximaAutoridad.getPersona().getPosicion() != null ? maximaAutoridad.getPersona().getPosicion() : "") ;
                firma +="<br/>"+remitenteStr;
                // firma +="<br/>"+ proyecto.getAreaResponsable().getArea().getAreaName();
                firma += (proyecto.getAreaResponsable().getArea() == null) ? "<br/>"+ proyecto.getAreaResponsable().getAreaName() : "<br/>"+ proyecto.getAreaResponsable().getArea().getAreaName();
                informeTecnicoGeneralLA.setFirmaDigital(firma);
            }else{
               informeTecnicoGeneralLA.setFirmaDigital("");
            }
           /* if (!token) {
                entityOficio.setNombreRemitente(oficioAprobacionEia
                        .getNombreRemitente());
                String remitenteStr = (remitente.getPersona().getPosicion() != null ? remitente.getPersona().getPosicion() : "") ;

                entityOficio.setCargoRemitente(remitenteStr);

            } else {
                entityOficio.setNombreRemitente("");
                entityOficio.setCargoRemitente("");
                entityOficio.setEntidad("");
            }*/
            informeTecnicoGeneralLA.setFechaInforme(JsfUtil
                    .devuelveFechaEnLetrasSinHora(new Date()));
            /**
             * Nombre:SUIA
             * Descripción: Llena el objeto informe Técnico General LA
             * ParametrosIngreso:
             * PArametrosSalida:
             * Fecha:15/08/2015
             */
            //informeTecnicoGeneralLA.setMemorandoFinanciero("MEMORANDO FINANCIERO");
            // informeTecnicoGeneralLA.setAreaResponsable(proyecto.getAreaResponsable().getArea().getAreaAbbreviation());
            informeTecnicoGeneralLA.setAreaResponsable(proyecto.getAreaResponsable().getArea() == null ? proyecto.getAreaResponsable().getAreaAbbreviation() : proyecto.getAreaResponsable().getArea().getAreaAbbreviation());
            /**
             * FIN Llena el objeto informe Técnico General LA
             */
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

            nombreReporte = "Licencia_Ambiental_" + proyecto.getCodigo() + ".pdf";
            UtilGenerarInforme utilGenerarInforme= new UtilGenerarInforme();
            File informePdf = utilGenerarInforme.generarFichero(
                    plantillaHtml, nombreReporte, true,
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
            verOficio=true;
			} catch (Exception e) {
				LOG.error("Error al visualizar el oficio", e);
				JsfUtil.addMessageError("Error al visualizar el oficio");
			}
		}
        return pathPdf;
    }

    public void guardar() {
        try {
        	cargarLicencia=false;
        	verOficio=true;
            informeOficioFacade.guardarInformeTecnicoGeneralLA(informeTecnicoGeneralLA);
            JsfUtil.addMessageInfo("Se realizó correctamente la operación.");
            String url = "/prevencion/licenciamiento-ambiental/documentos/revisarDocumentacionJuridico.jsf";
            if (tipo != null && !tipo.isEmpty()) {
                url += "?tipo=" + tipo;
            }
            JsfUtil.redirectTo(url);
        } catch (Exception e) {
            LOG.error("Error al guardar oficio", e);
            JsfUtil.addMessageError("Error al guardar oficio");
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
       /* if (proyecto.isConcesionesMinerasMultiples()) {
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
					/* MarielaG 
					 * Guardar las observaciones cuando son válidas */
					List<ObservacionesFormularios> observacionesForm = observacionesController
							.getObservacionesBB().getMapaSecciones()
							.get("informeGeneral");
					observacionesFacade.guardar(observacionesForm);
                } else {
                    return "";
                }
            }

            Map<String, Object> data = new ConcurrentHashMap<String, Object>();
            procesoFacade.aprobarTarea(loginBean.getUsuario(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), data);
            JsfUtil.addMessageInfo("Se realizó correctamente la operación.");
            procesoFacade.envioSeguimientoLicenciaAmbiental(loginBean.getUsuario(), bandejaTareasBean.getProcessId());
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

    public boolean validarObservaciones() {
        List<ObservacionesFormularios> observaciones = observacionesController.getObservacionesBB().getMapaSecciones().get("informeGeneral");
        return (observaciones != null && observaciones.size() > 0) || revisar;
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

    public void generarObservacionesEIA(EstudioImpactoAmbiental estudioImpactoAmbiental) {
        StringBuilder sb = new StringBuilder();
        OficioObservacionEia oficioObservacionEia = informeOficioFacade
                .obtenerOficioObservacionEiaPorEstudio(
                        TipoDocumentoSistema.TIPO_OFICIO_OBSERVACION_EIA,
                        estudioImpactoAmbiental.getId());
        Integer cantidadNotificaciones = Integer.parseInt(variables.get("cantidadNotificaciones").toString());
        InformeTecnicoEia informeTecnicoEia = informeOficioFacade
                .obtenerInformeTecnicoEiaPorEstudio(TipoDocumentoSistema.TIPO_INFORME_TECNICO_EIA,
                        estudioImpactoAmbiental.getId(),cantidadNotificaciones);

        if (oficioObservacionEia != null) {
            sb.append("<table border=\"0\" cellpadding=\"1\" cellspacing=\"1\" style=\"width:700px\"> <tr><td style=\"vertical-align: top; text-align: justify;\">Que,</td>");
            sb.append("<td style=\"vertical-align: top; text-align: justify;\">");
            sb.append("mediante Oficio No. ");
            sb.append(oficioObservacionEia.getNumeroOficio());
            sb.append(" del ");
            sb.append(oficioObservacionEia.getFechaInforme());
            sb.append(" , sobre la base del Informe Técnico No. ");


            sb.append(informeTecnicoEia.getNumeroInforme());
            sb.append(" del ");
            sb.append(informeTecnicoEia.getFechaInforme());

            sb.append(", la Dirección Nacional de Prevención de la Contaminación Ambiental del Ministerio del Ambiente y Agua," +
                    " realiza observaciones al Estudio de Impacto Ambiental del proyecto ");

            sb.append(proyecto.getCodigo());
            sb.append(", ubicado en ");
            sb.append(proyecto.getNombre());
            sb.append(".");
            sb.append("</td> </tr></table>");

            informeTecnicoGeneralLA.setObservacionesEIANormativa(sb.toString());

            sb = new StringBuilder();
            sb.append("<table border=\"0\" cellpadding=\"1\" cellspacing=\"1\" style=\"width:700px\" > <tr><td style=\"vertical-align: top; text-align: justify;\">Que,</td>");
            sb.append("<td style=\"vertical-align: top; text-align: justify;\">");
            sb.append("el ");
            sb.append(JsfUtil.getDateFormat(estudioImpactoAmbiental.getFechaModificacion()) + ", ");
            sb.append(informeTecnicoGeneralLA.getNombreEmpresaProponente());
            sb.append(", ingresa al Sistema Único de Información Ambiental para análisis, revisión y " +
                    "pronunciamiento las respuestas a las observaciones realizadas al Estudio de Impacto Expost ");
            sb.append(informeTecnicoGeneralLA.getNombreProyecto() + ", ubicado en");
            sb.append(informeTecnicoGeneralLA.getProvinciaProyecto() + ".");
            sb.append("</td> </tr></table>");
            informeTecnicoGeneralLA.setRespuestaEIANormativa(sb.toString());


        }
        
        informeTecnicoGeneralLA.setNumeroInformeSCA(informeTecnicoEia.getNumeroInforme());

        informeTecnicoGeneralLA.setFechaInformeSCA(informeTecnicoEia.getFechaInforme());
        informeTecnicoGeneralLA.setObligaciones(informeTecnicoEia.getOtrasObligaciones());

    }
    
    public Usuario actualizarAutoridad(String cedula) throws JbpmException
    {
    	String rol="";
    	Map<String, Object> params = new ConcurrentHashMap<String, Object>();
    	Usuario maximaAutoridad =usuarioFacade.buscarUsuarioCompleta(cedula);    	
    	/*if(proyecto.getAreaResponsable().getArea().getId().equals(257))
			rol="AUTORIDAD AMBIENTAL MAE";
		else
			rol="AUTORIDAD AMBIENTAL";*/
    	if (proyecto.getAreaResponsable().getArea() == null) {
    		rol="AUTORIDAD AMBIENTAL";
		} else {
			if(proyecto.getAreaResponsable().getArea().getId().equals(257))
				rol="AUTORIDAD AMBIENTAL MAE";
			else
				rol="AUTORIDAD AMBIENTAL";
		}
    	
    	String area_nombre = "";
		if (proyecto.getAreaResponsable().getArea() == null) {
    		area_nombre = proyecto.getAreaResponsable().getAreaName();
		} else {
			area_nombre = proyecto.getAreaResponsable().getArea().getAreaName();
		}
    	
    	if(maximaAutoridad==null)
    	{    		
    		// maximaAutoridad=asignarTareaFacade.autoridadXRolArea(rol, proyecto.getAreaResponsable().getArea().getAreaName());
    		maximaAutoridad=asignarTareaFacade.autoridadXRolArea(rol, area_nombre);
    		if(maximaAutoridad!=null)
    		{
    			params.put("u_MaximaAutoridad",maximaAutoridad.getNombre());
    			procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getTarea().getProcessInstanceId(), params);
    		}
    		else
    			LOG.error("====>>> No existe usuario con el ROL:"+rol+" del AREA:"+proyecto.getAreaResponsable().getArea().getAreaName());
    	}
    	else
    	{
    		List<String> roles = new ArrayList<String>();
    		for(RolUsuario res: maximaAutoridad.getRolUsuarios())
    		{
    			roles.add(res.getRol().getNombre());
    		}
    		if(!roles.contains(rol))
    		{
    			// maximaAutoridad=asignarTareaFacade.autoridadXRolArea(rol, proyecto.getAreaResponsable().getArea().getAreaName());
    			maximaAutoridad=asignarTareaFacade.autoridadXRolArea(rol, area_nombre);
        		if(maximaAutoridad!=null)
        		{
        			params.put("u_MaximaAutoridad",maximaAutoridad.getNombre());
        			procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getTarea().getProcessInstanceId(), params);
        		}
        		else
        			LOG.error("====>>> No existe usuario con el ROL:"+rol+" del AREA:"+proyecto.getAreaResponsable().getArea().getAreaName());
    		}    		
    	}    	
    	return maximaAutoridad;
    }
}
