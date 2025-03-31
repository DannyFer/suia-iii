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

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.certificadointerseccion.service.CertificadoInterseccionFacade;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.domain.InformeTecnicoEia;
import ec.gob.ambiente.suia.domain.ObservacionesFormularios;
import ec.gob.ambiente.suia.domain.OficioObservacionEia;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.Pronunciamiento;
import ec.gob.ambiente.suia.domain.TipoDocumento;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.dto.EntityOficioObservacionEia;
import ec.gob.ambiente.suia.eia.facade.EstudioImpactoAmbientalFacade;
import ec.gob.ambiente.suia.eia.facade.LicenciaAmbientalFacade;
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

@ManagedBean
@ViewScoped
public class OficioObservacionEiaBean implements Serializable {

    // ///////////////////////
    // CONSTANTES
    // ///////////////////////
    public static final String TEMP_CONST_NOMBRE_SECUENCIA_NUMERO = "seq_number_job_observation_eia";
    public static final String TEMP_CONST_NOMBRE_ESQUEMA_SUIA = "suia_iii";
    public static final int TEMP_CONST_TAMANIO_NUMERO_OFICIO = 6;
    /**
     *
     */
    private static final long serialVersionUID = 165683211928358047L;

    private final Logger LOG = Logger.getLogger(OficioObservacionEiaBean.class);

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
    private OficioObservacionEia oficioObservacionEia;
    @Getter
    @Setter
    private EstudioImpactoAmbiental estudioImpactoAmbiental;

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
    private boolean mostrarInforme = false;

    @Getter
    @Setter
    private boolean requiereModificaciones;

    @Getter
    @Setter
    private PlantillaReporte plantillaReporte;

    @Getter
    @Setter
    private InformeTecnicoEia informeTecnicoEia;

    @Getter
    @Setter
    private Usuario promotor;

    @Getter
    @Setter
    private Usuario remitente;
    @Getter
    @Setter
    private Map<String, Object> variables;

    @EJB
    private AreaFacade areaFacade;
    @EJB
    private LicenciaAmbientalFacade licenciaAmbientalFacade;
    @EJB
    private ProcesoFacade procesoFacade;
    @EJB
    private InformeOficioFacade informeOficioFacade;
    @EJB
    private EstudioImpactoAmbientalFacade estudioImpactoAmbientalFacade;
    @EJB
    private CertificadoInterseccionFacade certificadoInterseccionFacade;
    @EJB
    private ObservacionesFacade observacionesFacade;

    @EJB
    private PronunciamientoFacade pronunciamientoFacade;

    @EJB
    private UsuarioFacade usuarioFacade;
    
    @EJB
    private DocumentosFacade documentosFacade;

    @ManagedProperty(value = "#{proyectosBean}")
    @Getter
    @Setter
    private ProyectosBean proyectosBean;

    @Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;


    @Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;

    @Getter
    @Setter
    @ManagedProperty(value = "#{observacionesController}")
    private ObservacionesController observacionesController;
    
    @Getter
    @Setter
    @ManagedProperty(value = "#{informeTecnicoEiaBean}")
    private InformeTecnicoEiaBean informeTecnicoEiaBean;
    
	@EJB
	private CrudServiceBean crudServiceBean;
	
	private boolean verOficio=false;
	

	private Integer cantidadNotificaciones;
	
    @PostConstruct
    public void init() {
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext()
                .getRequestParameterMap();
        if (params.containsKey("tipo")) {
            tipo = params.get("tipo");
            if (params.get("tipo").equals("revisar")) {
            }
        }
    }

    public void configurarDatosComunes() throws ServiceException, JbpmException {
        tipoDocumento = new TipoDocumento();
        tipoDocumento.setId(TipoDocumentoSistema.TIPO_OFICIO_OBSERVACION_EIA.getIdTipoDocumento());
        plantillaReporte = informeOficioFacade.obtenerPlantillaReporte(this.getTipoDocumento().getId());

        estudioImpactoAmbiental = estudioImpactoAmbientalFacade.obtenerPorProyecto(proyectosBean.getProyecto());

        oficioObservacionEia = informeOficioFacade.obtenerOficioObservacionEiaPorEstudio(
                TipoDocumentoSistema.TIPO_OFICIO_OBSERVACION_EIA, estudioImpactoAmbiental.getId());

        variables = procesoFacade.recuperarVariablesProceso(loginBean.getUsuario(),
                bandejaTareasBean.getTarea().getProcessInstanceId());

        String nombrePromotor = variables.get("u_Promotor").toString();
        String nombreFirma = variables.get("u_DirectorDPNCA").toString();  
        cantidadNotificaciones = Integer.parseInt(variables.get("cantidadNotificaciones").toString());
        
        
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
        if(nombreFirma.compareTo(usrDirectorDPNCA)!=0)
        	actualizar=true;
        
        nombreFirma=usrDirectorDPNCA;
        remitente = usuarioFacade.buscarUsuarioCompleta(nombreFirma);
        if(oficioObservacionEia!=null)
        {
        	oficioObservacionEia.setNombreRemitente(remitente.getPersona().getTipoTratos().getNombre()+"<br/>"+remitente.getPersona().getNombre());
        }
       
        if(actualizar)
        {
        	 HashMap<String, Object> params =new HashMap<String, Object>();
             params.put("u_MaximaAutoridad", usrMaximaAutoridad);
             params.put("u_DirectorDPNCA", usrDirectorDPNCA);
             params.put("u_Subsecretaria", usrSubsecretaria);
             procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getTarea().getProcessInstanceId(), params);
        }
       
        Usuario responsable = usuarioFacade.buscarUsuarioCompleta(loginBean.getUsuario().getNombre());

        if (oficioObservacionEia == null) {
            oficioObservacionEia = new OficioObservacionEia();
            String numeroOficio = obtenerNuevoNumeroOficio();
            
            
            oficioObservacionEia.setCiudadInforme(responsable.getPersona().getUbicacionesGeografica().getUbicacionesGeografica().getNombre());

            oficioObservacionEia.setNumeroOficio(numeroOficio);            
            oficioObservacionEia.setNombreRemitente(remitente.getPersona().getTipoTratos().getNombre()+"<br/>"+remitente.getPersona().getNombre());
            oficioObservacionEia.setNombreReceptor(promotor.getPersona().getNombre());
            oficioObservacionEia.setEstudioImpactoAmbiental(estudioImpactoAmbiental);
            oficioObservacionEia.setTipoDocumento(tipoDocumento);

            configurarEmisorPronunciamiento(variables);

            informeOficioFacade.guardarOficioObservacionEia(oficioObservacionEia);

        }else
        {        	
        	if(oficioObservacionEia.getCiudadInforme().compareTo(responsable.getPersona().getUbicacionesGeografica().getUbicacionesGeografica().getNombre())!=0)
        	{
        		 oficioObservacionEia.setCiudadInforme(responsable.getPersona().getUbicacionesGeografica().getUbicacionesGeografica().getNombre());
        		 informeOficioFacade.guardarOficioObservacionEia(oficioObservacionEia);
        	}        	
        	
        	if(actualizar) {
            	//para actualizar nombre de autoridad cuando hay cambio
        		oficioObservacionEia.setNombreRemitente(remitente.getPersona().getTipoTratos().getNombre()+"<br/>"+remitente.getPersona().getNombre());
        		informeOficioFacade.guardarOficioObservacionEia(oficioObservacionEia);
            }
        	
        	//para actualizar el codigo del oficio con la nueva área del proyecto
        	Area area = proyectosBean.getProyecto().getAreaResponsable();
            if (area.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_OT)) {
                if(!oficioObservacionEia.getNumeroOficio().contains(area.getArea().getAreaAbbreviation())) {
                	oficioObservacionEia.setNumeroOficioAnterior(oficioObservacionEia.getNumeroOficio());
                	oficioObservacionEia.setNumeroOficio(obtenerNuevoNumeroOficio());
                	informeOficioFacade.guardarOficioObservacionEia(oficioObservacionEia);
                }
            }
            
            Calendar cal = Calendar.getInstance();
			String anioActual = Integer.toString(cal.get(Calendar.YEAR));
			
            if((tipo == null || (tipo != null && !tipo.equals("revisar")))
            		&& !oficioObservacionEia.getNumeroOficio().contains("-" + anioActual)) {
            	oficioObservacionEia.setNumeroOficio(obtenerNuevoNumeroOficio());
            	informeOficioFacade.guardarOficioObservacionEia(oficioObservacionEia);
            }
        }
        
        
        //cantidadNotificaciones = Integer.parseInt(variables.get("cantidadNotificaciones").toString());
        if (cantidadNotificaciones > 0 && !oficioObservacionEia.isTieneObservaciones()) {
            oficioObservacionEia.setNumeroOficioObservaciones(oficioObservacionEia.getNumeroOficio());
            oficioObservacionEia.setFechaOficioObservaciones(JsfUtil.getDateFormat(oficioObservacionEia
                    .getFechaModificacion()));
            //oficioObservacionEia.setNumeroOficio(obtenerNuevoNumeroOficio());
            oficioObservacionEia.setTieneObservaciones(true);

            configurarEmisorPronunciamiento(variables);
            informeOficioFacade.guardarOficioObservacionEia(oficioObservacionEia);
        }
        informeTecnicoEia = informeOficioFacade.obtenerInformeTecnicoEiaPorEstudio(
                TipoDocumentoSistema.TIPO_INFORME_TECNICO_EIA, estudioImpactoAmbiental.getId(),cantidadNotificaciones);

        oficioObservacionEia.setListaObservaciones(informeTecnicoEia.getObservaciones());
    }

    private void configurarEmisorPronunciamiento(Map<String, Object> variables) throws ServiceException {
        Pronunciamiento pronunciamientoForestal = pronunciamientoFacade.getPronunciamientosPorClaseTipo(
                "Pronunciamiento", Long.parseLong(proyectosBean.getProyecto().getId().toString()), "Forestal");
        String emisor = "DNF";
        if (pronunciamientoForestal == null) {
            pronunciamientoForestal = pronunciamientoFacade.getPronunciamientosPorClaseTipo("Pronunciamiento",
                    Long.parseLong(proyectosBean.getProyecto().getId().toString()), "Biodiversidad");
            emisor = "DNB";
        }
        oficioObservacionEia.setEmisorPronunciamiento(emisor);
        oficioObservacionEia.setNumeroPronunciamiento(pronunciamientoForestal.getId().toString());
        oficioObservacionEia.setFechaPronunciamiento(JsfUtil.getDateFormat(pronunciamientoForestal.getFecha()));

        EstudioImpactoAmbiental eia = estudioImpactoAmbientalFacade.obtenerPorProyecto(proyectosBean.getProyecto());
        oficioObservacionEia.setNumeroInformeTecnico(eia.getId().toString());
        oficioObservacionEia.setFechaInformeTecnico(JsfUtil.getDateFormat(eia.getFechaModificacion()!=null?eia.getFechaModificacion():eia.getFechaCreacion()));

    }

    public String visualizarOficio()  {
        try {
        	if(!verOficio)
        	return visualizarOficio(false);
        } catch (Exception e) {
            LOG.error("Error al inicializar: OficioAprobacionEiaBean: ", e);
            JsfUtil.addMessageError(JsfUtil.ERROR_INICIALIZAR_DATOS);
        }
        return "";
    }

    public String visualizarOficio(boolean token) throws JbpmException, ServiceException {
    	configurarDatosComunes();
    	
        String pathPdf = null;
        String cargoReceptor="";
        String nombreOrganizacion="";
        try {

            String nombreProvincia = "";
            String nombreCanton = "";
            String nombreParroquia = "";
            int i = 0;
            for (UbicacionesGeografica b : proyectosBean.getProyecto().getUbicacionesGeograficas()) {
                if (i == 0) {
                    nombreProvincia += b.getUbicacionesGeografica().getUbicacionesGeografica().getNombre();
                    nombreCanton += b.getUbicacionesGeografica().getNombre();
                    nombreParroquia += b.getNombre();
                    i++;
				} else {
					if (nombreParroquia.contains(b.getNombre())) {
					} else {
						nombreParroquia += ", " + b.getNombre();
					}
					if (nombreCanton.contains(b.getUbicacionesGeografica().getNombre())) {
					} else {
						nombreCanton += ", "+ b.getUbicacionesGeografica().getNombre();
					}
					if (nombreProvincia.contains(b.getUbicacionesGeografica().getUbicacionesGeografica().getNombre())) {

					} else {
						nombreProvincia += ", "+ b.getUbicacionesGeografica().getUbicacionesGeografica().getNombre();
					}
				}
            }

            //String fechaProyecto= JsfUtil.devuelveFechaEnLetrasSinHora(proyectosBean.getProyecto().getFechaRegistro());
            oficioObservacionEia.setFechaInforme(JsfUtil.devuelveFechaEnLetrasSinHora(new Date()));

//            List<ObservacionesFormularios> listaObservaciones = observacionesFacade.listarPorIdClaseNombreClase(
//                    proyectosBean.getProyecto().getId(), "InformeTecnicoEiaObservacion");
            List<Organizacion> listaOrga = new ArrayList<Organizacion>();
			if (promotor.getPersona().getOrganizaciones().size() > 0) {
				Map<String, Object> parametros = new HashMap<String, Object>();
				parametros.put("persona", promotor.getPersona());
				parametros.put("nombre", promotor.getNombre());
				listaOrga = crudServiceBean.findByNamedQueryGeneric(Organizacion.FIND_BY_PERSON_NAME, parametros);
			}

            EntityOficioObservacionEia entityOficio = new EntityOficioObservacionEia();
            entityOficio.setAreaResponsable(proyectosBean.getProyecto().getAreaResponsable().getAreaAbbreviation());
            entityOficio.setNumeroOficio(oficioObservacionEia.getNumeroOficio());
            entityOficio.setCiudadInforme(oficioObservacionEia.getCiudadInforme());
            entityOficio.setFechaInforme(oficioObservacionEia.getFechaInforme());
            
            String asunto = oficioObservacionEia.getAsunto() != null ? StringEscapeUtils
                    .escapeHtml(oficioObservacionEia.getAsunto()) : null;
            entityOficio.setAsunto(asunto);
            entityOficio.setTratamientoReceptor(promotor.getPersona().getTipoTratos().getNombre());
            entityOficio.setNombreReceptor(oficioObservacionEia.getNombreReceptor());
            if(listaOrga.size()>0)
            cargoReceptor=listaOrga.get(0).getPersona().getPosicion();	
            entityOficio.setCargoReceptor("");
            if(cargoReceptor!=null)
            entityOficio.setCargoReceptor(cargoReceptor);
            
            if(listaOrga.size()>0){
            nombreOrganizacion=listaOrga.get(0).getNombre();
            entityOficio.setEmpresa(nombreOrganizacion);
            //entityOficio.setNombreEmpresa("la/el "+nombreOrganizacion);
            }else {
            //entityOficio.setNombreEmpresa("el sujeto de control "+oficioObservacionEia.getNombreReceptor());
            entityOficio.setEmpresa(promotor.getPersona().getTitulo());
			}
            /*boolean interseca=false;
            interseca=certificadoInterseccionFacade.isProyectoIntersecaCapas(proyectosBean.getProyecto().getCodigo());
            if(interseca){
            	String intersecta="";
            	intersecta= certificadoInterseccionFacade.getDetalleInterseccion(proyectosBean.getProyecto().getCodigo());
            	entityOficio.setIntersectaProyecto("<b>SI INTERSECA</b>");
                entityOficio.setLugaresIntersecta("con: "+intersecta);
            }else {
            	 entityOficio.setIntersectaProyecto("<b>NO INTERSECA.</b>");
                 entityOficio.setLugaresIntersecta("");
			}
            CertificadoInterseccion certificadoInterseccion= new CertificadoInterseccion();
            certificadoInterseccion= certificadoInterseccionFacade.recuperarCertificadoInterseccion(proyectosBean.getProyecto());
            entityOficio.setNumeroOficioInterseccion(certificadoInterseccion.getCodigo());
            entityOficio.setFechaOficioInterseccion(JsfUtil.devuelveFechaEnLetrasSinHora(certificadoInterseccion.getFechaCreacion()));
            String descripcion="del Pronunciamiento No. "+oficioObservacionEia.getNumeroPronunciamiento()+" emitido por Patrimonio Natural, y";
            entityOficio.setDescripcionPlantaCentral(descripcion);
            if(proyectosBean.getProyecto().isAreaResponsableEnteAcreditado()){
            	entityOficio.setDescripcionPlantaCentral("");
            }*/
            entityOficio.setObservacionesInformeTecnico(oficioObservacionEia.getDisposicionesLegales());
            /*entityOficio.setNumeroProyecto(proyectosBean.getProyecto().getCodigo());
            entityOficio.setFechaProyecto(fechaProyecto);
            entityOficio.setNombreProyecto(proyectosBean.getProyecto().getNombre());
            entityOficio.setProvinciaProyecto(nombreProvincia);
            entityOficio.setCantonProyecto(nombreCanton);
            entityOficio.setParroquiaProyecto(nombreParroquia);
            String fechaEia="";
            fechaEia=JsfUtil.devuelveFechaEnLetrasSinHora(estudioImpactoAmbiental.getFechaCreacion());
            entityOficio.setFechaProyectoEia(fechaEia);*/

            entityOficio
                    .setNumeroOficioObservaciones(StringEscapeUtils.escapeHtml(oficioObservacionEia
                            .getNumeroOficioObservaciones() != null ? oficioObservacionEia
                            .getNumeroOficioObservaciones() : ""));
            entityOficio
                    .setFechaOficioObservaciones(oficioObservacionEia.getFechaOficioObservaciones() != null ? oficioObservacionEia
                            .getFechaOficioObservaciones() : "");
            entityOficio.setFechaRespuestas(oficioObservacionEia.getFechaRespuestas());

            if (oficioObservacionEia.isTieneObservaciones()) {
                entityOficio.setTieneObservaciones("inline");
            } else {
                entityOficio.setTieneObservaciones("none");
            }

            entityOficio.setFechaPronunciamiento(oficioObservacionEia.getEmisorPronunciamiento());
            entityOficio.setEmisorPronunciamiento(oficioObservacionEia.getEmisorPronunciamiento());
            /*entityOficio.setNumeroInformeTecnico(informeTecnicoEia.getNumeroInforme());
            entityOficio.setFechaInformeTecnico(informeTecnicoEia.getFechaInforme());
            String disposiciones = oficioObservacionEia.getDisposicionesLegales() != null ? StringEscapeUtils
                    .escapeHtml(oficioObservacionEia.getDisposicionesLegales()) : null;
            entityOficio.setDisposicionesLegales(disposiciones);*/

            entityOficio.setListaObservaciones(oficioObservacionEia.getListaObservaciones());
            if (!token) {
            	Area area = areaFacade.getAreaFull(proyectosBean.getProyecto().getAreaResponsable().getId());
                //entityOficio.setNombreRemitente(remitente.getPersona().getTipoTratos().getNombre()+"<br/>"+oficioObservacionEia.getNombreRemitente());
                entityOficio.setNombreRemitente(oficioObservacionEia.getNombreRemitente());
                //String remitenteStr = (remitente.getPersona().getPosicion() != null ? remitente.getPersona().getPosicion() : "") + "<br />MINISTERIO DEL AMBIENTE";
                String remitenteStr = (area.getAreaName());
                if(area.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_OT))
                	remitenteStr = area.getArea().getAreaName();
                	
                entityOficio.setCargoRemitente(remitenteStr);
            }else{
                 entityOficio.setNombreRemitente("");
                entityOficio.setCargoRemitente("");
            }
            entityOficio.setSegundaObservacion(obtenerValoresExtras());

            nombreFichero = "OficioObservacion-" + estudioImpactoAmbiental.getId() + ".pdf";
            nombreReporte = "OficioObservacion.pdf";
            File informePdf = UtilGenerarInforme.generarFichero(plantillaReporte.getHtmlPlantilla(), nombreReporte,
                    true, entityOficio);
            setOficioPdf(informePdf);

            Path path = Paths.get(getOficioPdf().getAbsolutePath());
            archivoInforme = Files.readAllBytes(path);
            archivoFinal = new File(JsfUtil.devolverPathReportesHtml(getOficioPdf().getName()));
            FileOutputStream file = new FileOutputStream(archivoFinal);
            file.write(archivoInforme);
            file.close();
            setInformePath(JsfUtil.devolverContexto("/reportesHtml/" + getOficioPdf().getName()));

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
        	oficioObservacionEia.setTieneObservaciones(requiereModificaciones);
            oficioObservacionEia.setFechaInforme(JsfUtil.devuelveFechaEnLetrasSinHora(new Date()));
            informeOficioFacade.guardarOficioObservacionEia(oficioObservacionEia);                     

            JsfUtil.addMessageInfo("Se realizó correctamente la operación.");
            String url = "/prevencion/licenciamiento-ambiental/documentos/oficioObservacionEia.jsf";
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
            
            oficioObservacionEia.setTieneObservaciones(requiereModificaciones);
            informeOficioFacade.guardarOficioObservacionEia(oficioObservacionEia);
            JsfUtil.redirectTo(url);
        } catch (Exception e) {
            LOG.error("Error al guardar oficio", e);
            JsfUtil.addMessageError("Error al realizar la operación.");
        }
    }

    public String completarTarea() throws Exception {
    	oficioObservacionEia.setTieneObservaciones(requiereModificaciones);
        oficioObservacionEia.setFechaInforme(JsfUtil.devuelveFechaEnLetrasSinHora(informeTecnicoEiaBean.getInformeTecnicoEia().getFechaModificacion()));
        informeOficioFacade.guardarOficioObservacionEia(oficioObservacionEia);
        if (tipo != null && tipo.equals("revisar")) {
            Map<String, Object> params = new ConcurrentHashMap<String, Object>();
            /* MarielaG
        	 * Valida las observaciones teniendo en cuenta si el informe y oficio necesitan modificaciones
        	 * Si el oficio no requiere modificaciones valida si el informe requiere modificaciones
        	 */
            boolean subirArchivo = requiereModificaciones;
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
				List<ObservacionesFormularios> observacionesForm = observacionesController
						.getObservacionesBB().getMapaSecciones().get("informe");
				observacionesFacade.guardar(observacionesForm);
            	
                if (!subirArchivo) {
//                    visualizarOficio();
                    subirDocuemntoAlfresco();                    
                }

                params.put("requiereMoficacion", requiereModificaciones);
                params.put("esPronunciamientoAprobacion", false);
                procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getTarea().getProcessInstanceId(), params);
                Map<String, Object> data = new ConcurrentHashMap<String, Object>();
                procesoFacade.aprobarTarea(loginBean.getUsuario(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), data);
                JsfUtil.addMessageInfo("Se realizó correctamente la operación.");
                procesoFacade.envioSeguimientoLicenciaAmbiental(loginBean.getUsuario(), bandejaTareasBean.getProcessId());
                try {
                    archivoFinal.delete();
                } catch (Exception ex) {
                    LOG.error(ex);
                }
                return JsfUtil.actionNavigateTo("/bandeja/bandejaTareas.jsf");
            }
        } else {
            Map<String, Object> params = new ConcurrentHashMap<String, Object>();
            params.put("esPronunciamientoAprobacion", false);
            procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getTarea().getProcessInstanceId(), params);
            
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
//            	informeTecnicoEiaBean.subirDocuemntoInformeTecnicoAlfresco();
            }
            
            Map<String, Object> data = new ConcurrentHashMap<String, Object>();
            procesoFacade.aprobarTarea(loginBean.getUsuario(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), data);
            JsfUtil.addMessageInfo("Se realizó correctamente la operación.");
            procesoFacade.envioSeguimientoLicenciaAmbiental(loginBean.getUsuario(), bandejaTareasBean.getProcessId());
            return JsfUtil.actionNavigateTo("/bandeja/bandejaTareas.jsf");
        }
        return "";
    }

    public StreamedContent getStream() throws Exception {
        DefaultStreamedContent content = new DefaultStreamedContent();
        if (archivoInforme != null) {
            content = new DefaultStreamedContent(new ByteArrayInputStream(archivoInforme), "application/octet-stream");
            content.setName(nombreReporte);
        }
        return content;

    }

    public void validarTareaBpm() {

        String url = "/prevencion/licenciamiento-ambiental/documentos/informeTecnicoEia.jsf";
        if (tipo != null && !tipo.isEmpty()) {
            url += "?tipo=" + tipo;
        }
        JsfUtil.validarPaginasUrlTareasBpm(bandejaTareasBean.getTarea(), url);
    }

    public Boolean validarObservaciones(Boolean estado) {
        List<ObservacionesFormularios> observaciones = observacionesController.getObservacionesBB().getMapaSecciones()
                .get("informe");

        if (estado) {
            for (ObservacionesFormularios observacion : observaciones) {
                if (observacion.getUsuario().equals(loginBean.getUsuario()) && !observacion.isObservacionCorregida()) {

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
		licenciaAmbientalFacade.ingresarDocumentos(archivoFinal, proyectosBean.getProyecto().getId(),proyectosBean.getProyecto().getCodigo(), bandejaTareasBean
						.getProcessId(), bandejaTareasBean.getTarea().getTaskId(),TipoDocumentoSistema.TIPO_OFICIO_OBSERVACION_EIA,
				LicenciaAmbientalFacade.class.getSimpleName());

    }

    public String obtenerValoresExtras() throws ServiceException {

        String result = "";
        if (oficioObservacionEia.isTieneObservaciones()) {
            StringBuilder sb = new StringBuilder();
            sb.append("<p>&nbsp;</p><p > Mediante Oficio ");
            sb.append(oficioObservacionEia.getNumeroOficioObservaciones());
            sb.append(" de ");
            sb.append(oficioObservacionEia.getFechaOficioObservaciones());
            sb.append(", la Dirección Nacional de Prevención de la Contaminación  ");
            sb.append("Ambiental/Dirección Provincial del Ambiente correspondiente  solicitó información aclaratoria y complementaria del ");
            sb.append(" Ambiental/Dirección Provincial del Ambiente correspondiente  solicitó información aclaratoria y complementaria del ");
            sb.append(proyectosBean.getProyecto().getNombre());
            sb.append(" , mismas que son ingresadas al sistema SUIA el ");

            EstudioImpactoAmbiental eia = estudioImpactoAmbientalFacade.obtenerPorProyecto(proyectosBean.getProyecto());
            sb.append(eia.getFechaModificacion() == null ? JsfUtil.getDateFormat(eia.getFechaCreacion()) : JsfUtil.getDateFormat(eia.getFechaModificacion()));
            sb.append(", mismas que son ingresadas al sistema SUIA el ");
            sb.append(eia.getFechaModificacion() == null ? JsfUtil.getDateFormat(eia.getFechaCreacion()) : JsfUtil.getDateFormat(eia.getFechaModificacion()));
            sb.append(".</p><p>&nbsp;</p>");
            result = sb.toString();
        }
        return result;
    }

    public String obtenerNuevoNumeroOficio() {
        Area area = areaFacade.getAreaFull(proyectosBean.getProyecto().getAreaResponsable().getId());
        String base = Constantes.SIGLAS_INSTITUCION + "-SUIA-SCA-"+Constantes.SIGLAS_DIRECCION_REGULARIZACION_AMBIENTAL;
        String numeroInforme="";
        if (!area.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_PC)) {
            base = Constantes.SIGLAS_INSTITUCION + "-" + area.getAreaAbbreviation();
            
            if (area.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_OT))
            	base = Constantes.SIGLAS_INSTITUCION + "-" + area.getArea().getAreaAbbreviation();
        }
        BigInteger numeroSecuencia = informeOficioFacade.obtenerNumeroInforme(TEMP_CONST_NOMBRE_SECUENCIA_NUMERO,
                TEMP_CONST_NOMBRE_ESQUEMA_SUIA);
        
        if(proyectosBean.getProyecto().isAreaResponsableEnteAcreditado()){
        	numeroInforme = area.getAreaAbbreviation()+"-SUIA-RA-"
                    + JsfUtil.getCurrentYear()+"-"
                    + (numeroSecuencia != null ? JsfUtil.rellenarCeros(numeroSecuencia.toString(),
                    TEMP_CONST_TAMANIO_NUMERO_OFICIO) : "N/A");
        }else{
        numeroInforme = base
                + "-"
                + (numeroSecuencia != null ? JsfUtil.rellenarCeros(numeroSecuencia.toString(),
                TEMP_CONST_TAMANIO_NUMERO_OFICIO) : "N/A")+"-"+JsfUtil.getCurrentYear();
        }
        return numeroInforme;
    }
   
}
