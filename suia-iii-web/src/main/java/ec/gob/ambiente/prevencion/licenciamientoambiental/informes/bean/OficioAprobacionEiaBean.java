package ec.gob.ambiente.prevencion.licenciamientoambiental.informes.bean;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.certificadointerseccion.service.CertificadoInterseccionFacade;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.CertificadoInterseccion;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.domain.InventarioForestal;
import ec.gob.ambiente.suia.domain.ObservacionesFormularios;
import ec.gob.ambiente.suia.domain.OficioAprobacionEia;
import ec.gob.ambiente.suia.domain.OficioObservacionEia;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.Pronunciamiento;
import ec.gob.ambiente.suia.domain.TipoDocumento;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.dto.EntityOficioAprobacionEia;
import ec.gob.ambiente.suia.eia.facade.EstudioImpactoAmbientalFacade;
import ec.gob.ambiente.suia.eia.facade.LicenciaAmbientalFacade;
import ec.gob.ambiente.suia.eia.inventarioforestal.facade.InventarioForestalFacade;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.observaciones.facade.ObservacionesFacade;
import ec.gob.ambiente.suia.prevencion.licenciamientoAmbiental.facade.InformeOficioFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.pronunciamiento.facade.PronunciamientoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.ConexionBpms;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilGenerarInforme;

@ManagedBean
@ViewScoped
public class OficioAprobacionEiaBean implements Serializable {

    // ///////////////////////
    // CONSTANTES
    // ///////////////////////
    public static final String TEMP_CONST_NOMBRE_SECUENCIA_NUMERO = "seq_number_job_approval_eia";
    public static final String TEMP_CONST_NOMBRE_ESQUEMA_SUIA = "suia_iii";
    public static final int TEMP_CONST_TAMANIO_NUMERO_OFICIO = 6;
    /**
     *
     */
    private static final long serialVersionUID = 165683211928358047L;

    private final Logger LOG = Logger.getLogger(OficioAprobacionEiaBean.class);

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
    private OficioAprobacionEia oficioAprobacionEia;
    @Getter
    @Setter
    private EstudioImpactoAmbiental estudioImpactoAmbiental;
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
    private ObservacionesFacade observacionesFacade;
    @EJB
    private LicenciaAmbientalFacade licenciaAmbientalFacade;

    @EJB
    private ProcesoFacade procesoFacade;

    @EJB
    private ConexionBpms conexionBpms;
    
    @EJB
    private DocumentosFacade documentosFacade;
    
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
    @ManagedProperty(value = "#{informeTecnicoEiaBean}")
    private InformeTecnicoEiaBean informeTecnicoEiaBean;

    @Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;

    @ManagedProperty(value = "#{proyectosBean}")
    @Getter
    @Setter
    private ProyectosBean proyectosBean;
    
	@EJB
	private CrudServiceBean crudServiceBean;
	
	private boolean verOficio=false;

    @PostConstruct
    public void init() {
        Map<String, String> params = FacesContext.getCurrentInstance()
                .getExternalContext().getRequestParameterMap();
        if (params.containsKey("tipo")) {
            tipo = params.get("tipo");
            if (params.get("tipo").equals("revisar")) {
            }
        }
    }

    public void cargarDatosGeneral() {
        try {


            variables = procesoFacade
                    .recuperarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean
                            .getTarea().getProcessInstanceId());

            String nombrePromotor = variables.get("u_Promotor").toString();
            String nombreFirma = variables.get("u_Subsecretaria").toString();
            promotor = usuarioFacade.buscarUsuarioCompleta(nombrePromotor);

            String usrDirectorDPNCA = ""; //Director de la DNPCA      
            String usrMaximaAutoridad;
            String usrSubsecretaria;
            Area areaProyecto = areaFacade.getArea(proyectosBean.getProyecto().getAreaResponsable().getId());
            
            //Para Area Planta Central
            if (areaProyecto.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_PC)) {            
                usrMaximaAutoridad = areaFacade.getMinistra().getNombre(); //Ministra
                usrDirectorDPNCA = areaFacade.getDirectorPrevencionPlantaCentral().getNombre();
                usrSubsecretaria = areaFacade.getDirectorPlantaCentral("role.area.subsecretario.calidad.ambiental").getNombre(); //Subsecretaria
                }
            //Para otras Areas
            else {            
            	if(areaProyecto.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_OT))
                	areaProyecto = areaProyecto.getArea();
            	
            	usrMaximaAutoridad = areaFacade.getDirectorProvincial(areaProyecto).getNombre(); //Director Provincial (por ministra)
                usrDirectorDPNCA = usrMaximaAutoridad;
                usrSubsecretaria = usrMaximaAutoridad; //Director Provincial (por Subsecretaria)
            }
            boolean actualizar=false;
            if(nombreFirma.compareTo(usrSubsecretaria)!=0)
            	actualizar=true;
            nombreFirma=usrSubsecretaria;
            remitente = usuarioFacade.buscarUsuarioCompleta(nombreFirma);
            if(oficioAprobacionEia!=null)
            {
            	oficioAprobacionEia.setNombreRemitente(remitente.getPersona().getTipoTratos().getNombre()+"<br/>"+remitente.getPersona().getNombre());
            }
           
            if(actualizar)
            {
            	 HashMap<String, Object> params =new HashMap<String, Object>();
                 params.put("u_MaximaAutoridad", usrMaximaAutoridad);
                 params.put("u_DirectorDPNCA", usrDirectorDPNCA);
                 params.put("u_Subsecretaria", usrSubsecretaria);
                 procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getTarea().getProcessInstanceId(), params);
            }
            
          

            plantillaReporte = informeOficioFacade
                    .obtenerPlantillaReporte(TipoDocumentoSistema.TIPO_OFICIO_APROBACION_EIA.getIdTipoDocumento());


            estudioImpactoAmbiental =
                    estudioImpactoAmbientalFacade.obtenerPorProyecto(proyectosBean.getProyecto());

            oficioAprobacionEia = informeOficioFacade
                    .obtenerOficioAprobacionEiaPorEstudio(
                            TipoDocumentoSistema.TIPO_OFICIO_APROBACION_EIA,
                            estudioImpactoAmbiental.getId());

            Usuario responsable = usuarioFacade.buscarUsuarioCompleta(loginBean.getUsuario().getNombre());
            
            if (oficioAprobacionEia == null) {
                oficioAprobacionEia = new OficioAprobacionEia();
                oficioAprobacionEia.setCiudadInforme(responsable.getPersona().getUbicacionesGeografica().getUbicacionesGeografica().getNombre());
                oficioAprobacionEia.setEstudioImpactoAmbiental(estudioImpactoAmbiental);
                oficioAprobacionEia.setNombreReceptor(promotor.getPersona().getNombre());
                oficioAprobacionEia.setNombreRemitente(remitente.getPersona().getNombre());
               
                tipoDocumento = new TipoDocumento();
                tipoDocumento.setId(TipoDocumentoSistema.TIPO_OFICIO_APROBACION_EIA.getIdTipoDocumento());

                oficioAprobacionEia.setNumeroOficio(obtenerNuevoNumeroOficio());
                oficioAprobacionEia.setTipoDocumento(tipoDocumento);
                configurarEmisorPronunciamiento(variables);

                informeOficioFacade.guardarOficioAprobacionEia(oficioAprobacionEia);
            } else { 
            	Integer cantidadNotificaciones = Integer.parseInt(variables.get("cantidadNotificaciones").toString());
                if (cantidadNotificaciones > 0 && !oficioAprobacionEia.isTieneObservaciones()) {
                    oficioAprobacionEia.setNumeroOficioObservaciones(oficioAprobacionEia.getNumeroOficio());
                    oficioAprobacionEia.setFechaOficioObservaciones(JsfUtil.devuelveFechaEnLetrasSinHora(oficioAprobacionEia.getFechaModificacion() != null ? oficioAprobacionEia.getFechaModificacion() : oficioAprobacionEia.getFechaCreacion()));
                    oficioAprobacionEia.setNumeroOficio(obtenerNuevoNumeroOficio());
                    oficioAprobacionEia.setTieneObservaciones(true);

                    configurarEmisorPronunciamiento(variables);
                    informeOficioFacade
                            .guardarOficioAprobacionEia(oficioAprobacionEia);
                }
                
                
                if(oficioAprobacionEia.getCiudadInforme().compareTo(responsable.getPersona().getUbicacionesGeografica().getUbicacionesGeografica().getNombre())!=0)
            	{
            		oficioAprobacionEia.setCiudadInforme(responsable.getPersona().getUbicacionesGeografica().getUbicacionesGeografica().getNombre());
            		informeOficioFacade.guardarOficioAprobacionEia(oficioAprobacionEia);
            	}
                
                if(actualizar) {
                	//para actualizar nombre de autoridad cuando hay cambio
                	oficioAprobacionEia.setNombreRemitente(remitente.getPersona().getNombre());
                	informeOficioFacade.guardarOficioAprobacionEia(oficioAprobacionEia);
                } else if(!oficioAprobacionEia.getNombreRemitente().equals(remitente.getPersona().getNombre())) {
                	//para actualizar nombre de autoridad cuando el oficio ya está guardado
                	oficioAprobacionEia.setNombreRemitente(remitente.getPersona().getNombre());
                	informeOficioFacade.guardarOficioAprobacionEia(oficioAprobacionEia);
                }
                
              //para actualizar el codigo del oficio con la nueva área del proyecto
                Area area = proyectosBean.getProyecto().getAreaResponsable();
                if (area.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_OT)) {
                    if(!oficioAprobacionEia.getNumeroOficio().contains(area.getArea().getAreaAbbreviation())) {
                    	oficioAprobacionEia.setNumeroOficioAnterior(oficioAprobacionEia.getNumeroOficio());
                    	oficioAprobacionEia.setNumeroOficio(obtenerNuevoNumeroOficio());
                    	informeOficioFacade.guardarOficioAprobacionEia(oficioAprobacionEia);
                    }
                }
                
                Calendar cal = Calendar.getInstance();
				String anioActual = Integer.toString(cal.get(Calendar.YEAR));
				
				if((tipo == null || (tipo != null && !tipo.equals("revisar")))
                		&& !oficioAprobacionEia.getNumeroOficio().contains("-" + anioActual)) {
                	oficioAprobacionEia.setNumeroOficio(obtenerNuevoNumeroOficio());
                	informeOficioFacade.guardarOficioAprobacionEia(oficioAprobacionEia);
                }
            }
//            cargarFechas();
            oficioAprobacionEia
                    .setPagoInspeccionDiaria(Constantes.PAGO_INSPECCION_DIARIA);


        } catch (Exception e) {
            LOG.error("Error al inicializar: OficioAprobacionEiaBean: ", e);
            JsfUtil.addMessageError(JsfUtil.ERROR_INICIALIZAR_DATOS);
        }
    }

    public String visualizarOficio() {
    	try {
        	if(!verOficio)
        	return visualizarOficio(null);
        } catch (Exception e) {
            LOG.error("Error al inicializar: OficioAprobacionEiaBean: ", e);
            JsfUtil.addMessageError(JsfUtil.ERROR_INICIALIZAR_DATOS);
        }
        return "";
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
            String nombreOrganizacion="";
            int i = 0;
            try {
                for (UbicacionesGeografica b : proyectosBean.getProyecto()
                        .getUbicacionesGeograficas()) {
                    if (i == 0) {
                        nombreProvincia += b.getUbicacionesGeografica()
                                .getUbicacionesGeografica().getNombre();
                        nombreCanton += b.getUbicacionesGeografica().getNombre();
                        nombreParroquia += b.getNombre();
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

            String fechaOficioInterseccion = "";
            String numeroOficioCertificado = "";
            String interseccion = "";
            String cargoReceptor="";
            if (certificadoInterseccion != null) {
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

                numeroOficioCertificado = certificadoInterseccion != null ? certificadoInterseccion
                        .getCodigo() : "N/A";

                String interseca = certificadoInterseccionFacade
                        .getDetalleInterseccion(proyectosBean.getProyecto()
                                .getCodigo());

                interseccion = " interseca con: " + interseca;
            } else {
                interseccion = " no interseca ";
            }

            String fechaProyecto = JsfUtil.getDayFromDate(proyectosBean
                    .getProyecto().getFechaRegistro())
                    + " de "
                    + JsfUtil.devuelveMes(JsfUtil
                    .getMonthFromDate(proyectosBean.getProyecto()
                            .getFechaCreacion()))
                    + " del "
                    + JsfUtil.getYearFromDate(proyectosBean.getProyecto()
                    .getFechaCreacion());

            oficioAprobacionEia.setFechaInforme(JsfUtil
                    .devuelveFechaEnLetrasSinHora(new Date()));
            oficioAprobacionEia.setInterseccion(interseccion);

            // Llenar entity informe
            EntityOficioAprobacionEia entityOficio = new EntityOficioAprobacionEia();

            if (oficioAprobacionEia.getActualizacionCertificadoInterseccion() != null) {
                /*entityOficio.setActualizacionCertificadoInterseccion(oficioAprobacionEia
                                .getActualizacionCertificadoInterseccion()); cambio de formato solo tiene asunto y pronunciamiento*/ 
            } else {
                entityOficio.setActualizacionCertificadoInterseccion("");
            }
            
            
            List<Organizacion> listaOrga = new ArrayList<Organizacion>();
			if (promotor.getPersona().getOrganizaciones().size() > 0) {
				Map<String, Object> parametros = new HashMap<String, Object>();
				parametros.put("persona", promotor.getPersona());
				parametros.put("nombre", promotor.getNombre());
				listaOrga = crudServiceBean.findByNamedQueryGeneric(Organizacion.FIND_BY_PERSON_NAME, parametros);
			}

            //Número de oficio
            entityOficio.setNumeroOficio(oficioAprobacionEia.getNumeroOficio());
            entityOficio.setCiudadInforme(oficioAprobacionEia
                    .getCiudadInforme());
            entityOficio.setFechaInforme(oficioAprobacionEia.getFechaInforme());
            String asunto = oficioAprobacionEia.getAsunto() != null ? StringEscapeUtils.escapeHtml(oficioAprobacionEia.getAsunto()) : null;
            entityOficio.setAsunto(asunto+"<br/>");
            entityOficio.setTratamientoReceptor(promotor.getPersona().getTipoTratos().getNombre());
            entityOficio.setNombreReceptor(oficioAprobacionEia
                    .getNombreReceptor());
            if(listaOrga.size()>0)
            cargoReceptor=listaOrga.get(0).getPersona().getPosicion();
            entityOficio.setCargoReceptor("");
            if(cargoReceptor!=null)
            entityOficio.setCargoReceptor(cargoReceptor);
            
            if(listaOrga.size()>0)
            nombreOrganizacion=listaOrga.get(0).getNombre();
            entityOficio.setEmpresa(nombreOrganizacion);
            
            /* entityOficio.setLugarEntrega(lugarEntrega);
            entityOficio.setNumeroProyecto(proyectosBean.getProyecto().getCodigo());
            entityOficio.setFechaProyecto(JsfUtil.devuelveFechaEnLetrasSinHora(estudioImpactoAmbiental.getFechaCreacion()));
            entityOficio.setNombreProyecto(proyectosBean.getProyecto().getNombre());
            entityOficio.setProvinciaProyecto(nombreProvincia);
            entityOficio.setCantonProyecto(nombreCanton);
            entityOficio.setParroquiaProyecto(nombreParroquia); cambio de formato solo tiene asunto y pronunciamiento*/
            entityOficio.setNumeroOficioInterseccion(numeroOficioCertificado);
            entityOficio.setFechaOficioInterseccion(fechaOficioInterseccion);
            entityOficio.setInterseccion(oficioAprobacionEia.getInterseccion());

            entityOficio.setNumeroOficioObservaciones(oficioAprobacionEia
                    .getNumeroOficioObservaciones());
            entityOficio.setFechaOficioObservaciones(oficioAprobacionEia
                    .getFechaOficioObservaciones());
            entityOficio.setFechaRespuestas(oficioAprobacionEia
                    .getFechaRespuestas());

            /**
             * Nombre:SUIA
             * Descripción: obtener la endidad para el reporte
             * ParametrosIngreso:
             * PArametrosSalida:
             * Fecha:17/08/2015
             */
            if (proyectosBean.getProyecto().getAreaResponsable().getTipoArea().getId().equals(3)) {
                entityOficio.setPagomae("");
                entityOficio.setEntidad(proyectosBean.getProyecto().getAreaResponsable().getAreaName());
                entityOficio.setCargoRemitente(remitente.getPersona().getTipoTratos().getNombre());
            } else {
                entityOficio.setCargoRemitente(remitente.getPersona().getPosicion());
                entityOficio.setPagomae("en la cuenta corriente n&uacute;mero No. 3001480620, sublinea: 190499 de BanEcuador,");
                entityOficio.setEntidad(Constantes.NOMBRE_INSTITUCION);
            }

            /**
             * FIN obtener la endidad para el reporte
             */


            ///
            Pronunciamiento pronunciamientoForestal = pronunciamientoFacade.getPronunciamientosPorClaseTipo("Pronunciamiento", Long.parseLong(proyectosBean.getProyecto().getId().toString()), "Forestal");

            if (pronunciamientoForestal == null) {
                pronunciamientoForestal = pronunciamientoFacade.getPronunciamientosPorClaseTipo("Pronunciamiento", Long.parseLong(proyectosBean.getProyecto().getId().toString()), "Biodiversidad");

            }
//            if (oficioAprobacionEia.isTieneObservaciones()) {
//                entityOficio.setTieneObservaciones("inline");
//            } else {
//                entityOficio.setTieneObservaciones("none");
//            }

            if (oficioAprobacionEia.getPagoInspeccionDiaria() != null
                    && oficioAprobacionEia.getNumeroTecnicos() != null
                    && oficioAprobacionEia.getNumeroDias() != null) {
                oficioAprobacionEia.setValorTotal(oficioAprobacionEia
                        .getPagoInspeccionDiaria()
                        * oficioAprobacionEia.getNumeroTecnicos()
                        * oficioAprobacionEia.getNumeroDias());
                entityOficio.setValorTotal(oficioAprobacionEia.getValorTotal()
                        .toString());
            }

            InventarioForestal inventarioForestal = inventarioForestalFacade
                    .obtenetInventarioForestal(estudioImpactoAmbiental);
            if (inventarioForestal != null && inventarioForestal.getTieneInventarioForestal() != null &&
                    inventarioForestal.getTieneInventarioForestal() && inventarioForestal.getRemocionVegetal() != null &&
                    inventarioForestal.getRemocionVegetal()) {
                entityOficio.setNumeroDNF(pronunciamientoForestal.getId().toString());
                entityOficio.setFechaDNF(JsfUtil.getDateFormat(pronunciamientoForestal.getFecha()));

                StringBuilder sb = new StringBuilder();
                sb.append("<p style=\"text-align:justify\">3. Pago en la cuenta corriente n&uacute;mero No 3001480620 sublinea 190499 de BanEcuador, a nombre del "+ Constantes.NOMBRE_INSTITUCION + " por un valor de "
                );
                double valorCoberturaVegetal=0.00;
                valorCoberturaVegetal=inventarioForestalFacade.obtenerMontoTotalValoracion(estudioImpactoAmbiental);
                DecimalFormat decimales = new DecimalFormat("0.00");
                String valorTotal=decimales.format(valorCoberturaVegetal);
                sb.append(valorTotal);
                sb.append(
                        " USD, por concepto de Valoraciones Econ&oacute;micas de Bienes y Servicios Ecosist&eacute;micos y de Inventarios de Recursos Forestales, de conformidad con el pronunciamiento n&uacute;mero ");
                sb.append(pronunciamientoForestal.getId().toString());
                sb.append(" de la fecha ");
                sb.append(JsfUtil.getDateFormat(pronunciamientoForestal.getFecha()));
                sb.append(" y con los acuerdos ministeriales 076 publicado en registro oficial Nro 766 del 14 de Agosto del 2012, acuerdo ministerial Nro 134 publicado en registro oficial n&uacute;mero 812 del 18 de Octubre del 2012 y acuerdo ministerial Nro 083-B publicado en registro oficial 387  de 04 de noviembre del 2015.</p>-<p>&nbsp;</p>");
    /*
 <p>3. Pago en la cuenta corriente n&uacute;mero No 0010000777 del Banco Nacional de Fomento, a nombre del Ministerio del Ambiente por un valor de $F{valorForestal} USD, por concepto de las tasas de servicios forestales de conformidad con el pronunciamiento n&uacute;mero $F{numeroDNF} de la fecha $F{fechaDNF} y con los acuerdos ministeriales 076 publicado en registro oficial Nro 766 del 14 de Agosto del 2012 y acuerdo ministerial Nro 134 publicado en registro oficial n&uacute;mero 812 del 18 de Octubre del 2012.</p>

<p>&nbsp;</p>

    * */
                entityOficio.setValorForestal(sb.toString());


            } else {
                entityOficio.setValorForestal("");
            }
            /*entityOficio.setNumeroPronunciamiento(oficioAprobacionEia
                    .getNumeroPronunciamiento());
            entityOficio.setFechaPronunciamiento(oficioAprobacionEia
                    .getFechaPronunciamiento());
            entityOficio.setEmisorPronunciamiento(oficioAprobacionEia
                    .getEmisorPronunciamiento());
            entityOficio.setNumeroInformeTecnico(oficioAprobacionEia
                    .getNumeroInformeTecnico());
            entityOficio.setFechaInformeTecnico(oficioAprobacionEia
                    .getFechaInformeTecnico());
            entityOficio.setDisposicionesLegales(StringEscapeUtils.escapeHtml(oficioAprobacionEia
                    .getDisposicionesLegales() != null ? oficioAprobacionEia
                    .getDisposicionesLegales() : ""));cambio de formato solo tiene asunto y pronunciamiento*/
            entityOficio.setNombreFavorable(oficioAprobacionEia
                    .getNombreFavorable());
            entityOficio.setActividades(oficioAprobacionEia.getActividades());
            entityOficio.setRequisitos1(oficioAprobacionEia.getRequisitos1());
            entityOficio.setFormula(oficioAprobacionEia.getFormula());

            entityOficio.setPagoInspeccionDiaria(oficioAprobacionEia
                    .getPagoInspeccionDiaria() != null ? oficioAprobacionEia
                    .getPagoInspeccionDiaria().toString() : "0.00");

            entityOficio.setNumeroTecnicos(oficioAprobacionEia
                    .getNumeroTecnicos() != null ? oficioAprobacionEia
                    .getNumeroTecnicos().toString() : "0");
            entityOficio
                    .setNumeroDias(oficioAprobacionEia.getNumeroDias() != null ? oficioAprobacionEia
                            .getNumeroDias().toString() : "0");
            entityOficio.setRequisitos2(oficioAprobacionEia.getRequisitos2());


            if (!token) {
                entityOficio.setNombreRemitente(oficioAprobacionEia.getNombreRemitente());
                String remitenteStr = (remitente.getPersona().getTipoTratos().getNombre() != null ? remitente.getPersona().getTipoTratos().getNombre() : "") ;

                entityOficio.setCargoRemitente(remitenteStr);

            } else {
                entityOficio.setNombreRemitente("");
                entityOficio.setCargoRemitente("");
                entityOficio.setEntidad("");
            }
            //entityOficio.setDatosRevision(obtenerValoresExtras());


            nombreFichero = "OficioAprobacion" + estudioImpactoAmbiental.getId() + ".pdf";
            nombreReporte = "OficioAprobacion.pdf";

            /**
             * Nombre:SUIA
             * Descripción: Selecciona la abreviatura para el ente responsable
             * ParametrosIngreso:
             * PArametrosSalida:
             * Fecha:15/08/2015
             */
            entityOficio.setAreaResponsable(proyectosBean.getProyecto()
                    .getAreaResponsable().getAreaAbbreviation());
            /**
             * FIN Selecciona la abreviatura para el ente responsable
             */


            File informePdf;
            if (nombreNull == null) {
                informePdf = UtilGenerarInforme.generarFichero(
                        plantillaReporte.getHtmlPlantilla(), nombreReporte, true,
                        entityOficio);
            } else {
                informePdf = UtilGenerarInforme.generarFichero(
                        plantillaReporte.getHtmlPlantilla(), nombreReporte, true,
                        entityOficio, nombreNull);
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
            verOficio=true;
        } catch (Exception e) {
            LOG.error("Error al visualizar el oficio", e);
            JsfUtil.addMessageError("Error al visualizar el oficio");
        }

        return pathPdf;
    }

    public void guardar() {
        try {
        	verOficio=false;
        	oficioAprobacionEia.setTieneObservaciones(requiereModificaciones);
            informeOficioFacade.guardarOficioAprobacionEia(oficioAprobacionEia);
            String url = "/prevencion/licenciamiento-ambiental/documentos/oficioAprobacionEia.jsf";
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
            String url = "/prevencion/licenciamiento-ambiental/documentos/informeTecnicoEia.jsf";
            if (tipo != null && !tipo.isEmpty()) {
                url += "?tipo=" + tipo;
            }
            oficioAprobacionEia.setTieneObservaciones(requiereModificaciones);
            informeOficioFacade.guardarOficioAprobacionEia(oficioAprobacionEia);
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
            	/* MarielaG
            	 * Valida las observaciones teniendo en cuenta si el informe y oficio necesitan modificaciones
            	 * Si el oficio no requiere modificaciones valida si el informe requiere modificaciones
            	 */
            	boolean opcionRequiereModificaciones = requiereModificaciones;
                if(!requiereModificaciones){ 
                	if (variables.get("requiereMoficacion") != null && Boolean.parseBoolean(variables.get("requiereMoficacion").toString())) {            	
                        requiereModificaciones = true; 
                    }
                }

                if (validarObservaciones(!requiereModificaciones)) {            	
                	/*
                	 * MarielaG
                	 * Guardar las observaciones cuando son válidas
                	 */
					List<ObservacionesFormularios> observacionesForm = observacionesController.getObservacionesBB().getMapaSecciones().get("informe");
					observacionesFacade.guardar(observacionesForm);

                    //visualizarOficio("");
                	oficioAprobacionEia.setTieneObservaciones(opcionRequiereModificaciones);
                    informeOficioFacade.guardarOficioAprobacionEia(oficioAprobacionEia);
                    subirDocuemntoAlfresco();
                   
                    Map<String, Object> params = new ConcurrentHashMap<String, Object>();

                    params.put("requiereMoficacion",requiereModificaciones);
                    params.put("esPronunciamientoAprobacion",true);

                    procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean
                            .getTarea().getProcessInstanceId(), params);
                    Map<String, Object> data = new ConcurrentHashMap<String, Object>();
                    procesoFacade.aprobarTarea(loginBean.getUsuario(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), data);
                    JsfUtil.addMessageInfo("Se realizó correctamente la operación.");
                    procesoFacade.envioSeguimientoLicenciaAmbiental(loginBean.getUsuario(), bandejaTareasBean.getProcessId());
                    return JsfUtil.actionNavigateTo("/bandeja/bandejaTareas.jsf");
                }
                return "";
            } else {
                Map<String, Object> params = new ConcurrentHashMap<String, Object>();
                params.put("esPronunciamientoAprobacion", true);
                procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getTarea().getProcessInstanceId(), params);
                informeOficioFacade.guardarOficioAprobacionEia(oficioAprobacionEia);
                if(informeTecnicoEiaBean.getInformeTecnicoEia().getFinalizado()==null || !informeTecnicoEiaBean.getInformeTecnicoEia().getFinalizado())
                {
                	if(bandejaTareasBean.getTarea().getTaskSummary().getName().contains("Realizar correcciones"))
                    {                	
                		List<Documento> documentosList= documentosFacade.documentoXTablaIdXIdDoc(proyectosBean.getProyecto().getId(), "LicenciaAmbientalFacade", TipoDocumentoSistema.TIPO_INFORME_TECNICO_EIA);
                		if(documentosList.size()>0){
                    		Documento docu=documentosList.get(0);
                    		docu.setEstado(false);
                    		documentosFacade.actualizarDocumento(docu);
                    	}           		
                    }
                	
                	informeTecnicoEiaBean.getInformeTecnicoEia().setFinalizado(true);
                	informeOficioFacade.guardarInforme(informeTecnicoEiaBean.getInformeTecnicoEia());
//                	informeTecnicoEiaBean.subirDocuemntoInformeTecnicoAlfresco();
                }
                Map<String, Object> data = new ConcurrentHashMap<String, Object>();
                procesoFacade.aprobarTarea(loginBean.getUsuario(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), data);
                JsfUtil.addMessageInfo("Se realizó correctamente la operación.");
                procesoFacade.envioSeguimientoLicenciaAmbiental(loginBean.getUsuario(), bandejaTareasBean.getProcessId());
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
                bandejaTareasBean.getProcessId(), bandejaTareasBean.getTarea().getTaskId(), TipoDocumentoSistema.TIPO_OFICIO_APROBACION_EIA);

    }

    public String obtenerValoresExtras() throws ServiceException {
      /*
      Mediante Oficio (número del oficio de certificado de intersección xxxx) de (xxxx fecha que se emitió el oficio), de la Dirección Nacional de Prevención de la Contaminación Ambiental del Ministerio del Ambiente emite la actualización del certificado de intersección para el proyecto (nombre del proyecto), concluyendo que dicho proyecto (Interseca con el listado con lo que interseque o no interseca con el Patrimonio de \u00E1reas Naturales del Estado, Bosques y Vegetación Protectora y Patrimonio Forestal del Estado).

      Mediante Oficio (número de oficio observaciones) de (fecha de oficio), la Dirección Nacional de Prevención de la Contaminación Ambiental/Dirección Provincial del Ambiente correspondiente  solicitó información aclaratoria y complementaria del (nombre se jala del sistema con el que se registró el proyecto), mismas que son ingresadas al sistema SUIA el (fecha de ingreso de las respuestas).



      * */
        OficioObservacionEia oficioObservacionEia = informeOficioFacade
                .obtenerOficioObservacionEiaPorEstudio(
                        TipoDocumentoSistema.TIPO_OFICIO_OBSERVACION_EIA,
                        estudioImpactoAmbiental.getId());

        String result = "";
        if (oficioObservacionEia != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("<p>&nbsp;</p><p style=\"text-align:justify\"> Mediante Oficio ");
            sb.append(oficioObservacionEia.getNumeroOficio());
            sb.append(" de ");
            sb.append(JsfUtil.getDateFormat(oficioObservacionEia.getFechaModificacion() != null ? oficioObservacionEia.getFechaModificacion() :
                    oficioObservacionEia.getFechaCreacion()));
            sb.append(", la Dirección Nacional de Prevención de la Contaminación  ");
            sb.append("Ambiental/Dirección Provincial del Ambiente correspondiente  solicitó información aclaratoria y complementaria del ");
            sb.append(" Ambiental/Dirección Provincial del Ambiente correspondiente  solicitó información aclaratoria y complementaria del ");
            sb.append(proyectosBean.getProyecto().getNombre());
            sb.append(" , mismas que son ingresadas al sistema SUIA el ");

            sb.append(JsfUtil.getDateFormat(estudioImpactoAmbiental.getFechaModificacion()));
            sb.append(".</p><p>&nbsp;</p>");
            result = sb.toString();
        }
        return result;
    }

    private void configurarEmisorPronunciamiento(Map<String, Object> variables) throws ServiceException {
        Pronunciamiento pronunciamientoForestal = pronunciamientoFacade.getPronunciamientosPorClaseTipo("Pronunciamiento", proyectosBean.getProyecto().getId(), "Forestal");
        String emisor = "DNF";
        if (pronunciamientoForestal == null) {
            emisor = "DNB";
            pronunciamientoForestal = pronunciamientoFacade.getPronunciamientosPorClaseTipo("Pronunciamiento", Long.parseLong(proyectosBean.getProyecto().getId().toString()), "Biodiversidad");

            if (pronunciamientoForestal == null) {

                JsfUtil.addMessageError(JsfUtil.ERROR_INICIALIZAR_DATOS);
                return;
            }
        }


        oficioAprobacionEia.setEmisorPronunciamiento(emisor);
        oficioAprobacionEia.setNumeroPronunciamiento(pronunciamientoForestal.getId().toString());
        oficioAprobacionEia.setFechaPronunciamiento(JsfUtil.getDateFormat(pronunciamientoForestal.getFecha()));

        /*EstudioImpactoAmbiental eia = estudioImpactoAmbientalFacade
                .obtenerPorProyecto(proyectosBean.getProyecto());
        oficioAprobacionEia.setNumeroInformeTecnico(eia.getId().toString());
        oficioAprobacionEia.setFechaInformeTecnico(JsfUtil.getDateFormat(eia.getFechaModificacion()!=null?eia.getFechaModificacion():eia.getFechaCreacion()));*/
    }

    public String obtenerNuevoNumeroOficio() {
        Area area = areaFacade.getAreaFull(proyectosBean.getProyecto().getAreaResponsable().getId());
        String base = Constantes.SIGLAS_INSTITUCION + "-SCA";
        String numeroInforme="";
        boolean enteAcreditado=false;
        if (!area.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_PC)) {
            base = Constantes.SIGLAS_INSTITUCION + "-" + area.getAreaAbbreviation();
            
            if (area.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_OT))
            	base = Constantes.SIGLAS_INSTITUCION + "-" + area.getArea().getAreaAbbreviation();
        }

        BigInteger numeroSecuencia = informeOficioFacade
                .obtenerNumeroInforme(TEMP_CONST_NOMBRE_SECUENCIA_NUMERO,
                        TEMP_CONST_NOMBRE_ESQUEMA_SUIA);
        
        enteAcreditado= proyectosBean.getProyecto().isAreaResponsableEnteAcreditado();
        
        if(enteAcreditado){
        	base=area.getAreaAbbreviation();
        	 numeroInforme = base+"-SUIA-RA"

                     + "-"+ JsfUtil.getCurrentYear()+"-"
                     + (numeroSecuencia != null ? JsfUtil.rellenarCeros(
                     numeroSecuencia.toString(),
                     TEMP_CONST_TAMANIO_NUMERO_OFICIO) : "N/A");
        }else {
            numeroInforme = base

                    + "-"
                    + (numeroSecuencia != null ? JsfUtil.rellenarCeros(
                    numeroSecuencia.toString(),
                    TEMP_CONST_TAMANIO_NUMERO_OFICIO) : "N/A") + "-"
                    + JsfUtil.getCurrentYear();
	
		}
        
        return numeroInforme;
    }

}
