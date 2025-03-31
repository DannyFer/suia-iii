package ec.gob.ambiente.prevencion.licenciamientoambiental.informes.bean;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.persistence.Query;

import lombok.Getter;
import lombok.Setter;
import observaciones.ObservacionesController;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.domain.InformeTecnicoEia;
import ec.gob.ambiente.suia.domain.ObservacionesFormularios;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.Pronunciamiento;
import ec.gob.ambiente.suia.domain.TipoDocumento;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.dto.EntityInformeTecnicoEia;
import ec.gob.ambiente.suia.eia.facade.EstudioImpactoAmbientalFacade;
import ec.gob.ambiente.suia.eia.facade.LicenciaAmbientalFacade;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.observaciones.facade.ObservacionesFacade;
import ec.gob.ambiente.suia.prevencion.licenciamientoAmbiental.facade.InformeOficioFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.pronunciamiento.facade.PronunciamientoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoActualizacionCertificadoInterseccionFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.ConexionBpms;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.DigitalSign;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilGenerarInforme;

@ManagedBean
@ViewScoped
public class InformeTecnicoEiaBean implements Serializable {

    // ///////////////////////
    // CONSTANTES
    // ///////////////////////
    public static final String TEMP_CONST_NOMBRE_SECUENCIA_NUMERO = "seq_number_technical_report_eia";
    public static final String TEMP_CONST_NOMBRE_ESQUEMA_SUIA = "suia_iii";
    public static final int TEMP_CONST_TAMANIO_NUMERO_OFICIO = 6;
    public static final String LICENCIA_AMBIENTAL_FACADE = "LicenciaAmbientalFacade";
	public static final String NOMBRE_PDF="InformeTecnico.pdf";
    /**
     *
     */
    private static final long serialVersionUID = 165683211928358047L;

    private final Logger LOG = Logger
            .getLogger(InformeTecnicoEiaBean.class);

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
    private InformeTecnicoEia informeTecnicoEia;
    @Getter
    @Setter
    private EstudioImpactoAmbiental estudioImpactoAmbiental;

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

    @EJB
    private InformeOficioFacade informeOficioFacade;
    @EJB
    private EstudioImpactoAmbientalFacade estudioImpactoAmbientalFacade;
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
    private ConexionBpms conexionBpms;
    
    @EJB
	private ProyectoActualizacionCertificadoInterseccionFacade proyectoActualizacionCIFacade;
    
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
    
    private boolean verInforme=false;
    
    @EJB
    private LicenciaAmbientalFacade licenciaAmbientalFacade;   
    
    @EJB
	protected ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;
    
    @Getter
    @Setter
	private Documento documentoForestal = new Documento();
    
    @Getter
    @Setter
	private Documento documentoBiodiversidad = new Documento();
    
    private byte[] documentoDescargaForestal;
    
    private byte[] documentoDescargaBiodiversidad;
    
    /**
	 * FIRMA ELECTRONICA
	 */
	@Getter
	@Setter
	private boolean token, ambienteProduccion, firmaSoloToken, documentoDescargado, informacionSubida;
	@Getter
	@Setter
	private String nombreDocumentoFirmado, urlAlfresco;
	@Getter
	@Setter
	public static String tipoAmbiente = Constantes.getPropertyAsString("ambiente.produccion");
	@Getter
	@Setter
	private Documento documentoSubido, documentoInformacionManual;

	@Getter
	@Setter
	private Area area;

	@Getter
	@Setter
	private Pronunciamiento pronunciamientoVigente;

	@EJB
	private CrudServiceBean crudServiceBean;
        
    @PostConstruct
    public void init() {
        try {
        	area = new Area();
            cargarPronunciamientos();
            Map<String, String> params = FacesContext.getCurrentInstance()
                    .getExternalContext().getRequestParameterMap();
            Map<String, Object> variables = procesoFacade.recuperarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getTarea().getProcessInstanceId());
            if (params.containsKey("tipo")) {
                tipo = params.get("tipo");
                if (params.get("tipo").equals("revisar")) {
                    revisar = true;
                }                
                
                if (variables.containsKey("esPronunciamientoAprobacion")) {
                    pronunciamiento = Boolean.parseBoolean(variables.get("esPronunciamientoAprobacion").toString());
                }
                
                if(!tipo.equals("corregir")){
                	 if (variables.containsKey("requiereMoficacion")) {
                     	requiereModificaciones = Boolean.parseBoolean(variables.get("requiereMoficacion").toString());
                     }
                }               
            }

            tipoDocumento = new TipoDocumento();
            tipoDocumento.setId(TipoDocumentoSistema.TIPO_INFORME_TECNICO_EIA.getIdTipoDocumento());

            estudioImpactoAmbiental =
                    estudioImpactoAmbientalFacade.obtenerPorProyecto(proyectosBean.getProyecto());

            plantillaReporte = informeOficioFacade
                    .obtenerPlantillaReporte(this.getTipoDocumento().getId());
            
            Integer cantidadNotificaciones = Integer.parseInt(variables.get("cantidadNotificaciones").toString());
            
            informeTecnicoEia = informeOficioFacade
                    .obtenerInformeTecnicoEiaPorEstudio(TipoDocumentoSistema.TIPO_INFORME_TECNICO_EIA,
                            estudioImpactoAmbiental.getId(),cantidadNotificaciones);
           

            Usuario responsable = usuarioFacade.buscarUsuarioCompleta(loginBean.getUsuario().getNombre());
            
            if (informeTecnicoEia == null) {
                informeTecnicoEia = new InformeTecnicoEia();
                informeTecnicoEia.setOtrasObligaciones("");
                
                area = areaFacade.getAreaFull(proyectosBean.getProyecto().getAreaResponsable().getId());
                String base = Constantes.SIGLAS_DIRECCION_REGULARIZACION_AMBIENTAL;
                if (!area.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_PC)) {
                    base = area.getAreaAbbreviation();
                }
                BigInteger numeroSecuencia = informeOficioFacade
                        .obtenerNumeroInforme(TEMP_CONST_NOMBRE_SECUENCIA_NUMERO,
                                TEMP_CONST_NOMBRE_ESQUEMA_SUIA);
                String user = loginBean.getUsuario().getPersona().getNombre();
                String iniciales = "";
                StringTokenizer st = new StringTokenizer(user);
                while (st.hasMoreTokens()) {
                iniciales = iniciales + st.nextToken().charAt(0);
                }
                
                String numeroInforme = (numeroSecuencia != null ? JsfUtil.rellenarCeros(
                        numeroSecuencia.toString(),
                        TEMP_CONST_TAMANIO_NUMERO_OFICIO) : "N/A")
                        + "-"
                        + JsfUtil.getCurrentYear()
//                        + "-"
//                        + iniciales
                        + "-"
                        + base
                        + ((area.getId() == 257 || 
                        area.getAreaAbbreviation().toUpperCase().equals(Constantes.SIGLAS_DIRECCION_REGULARIZACION_AMBIENTAL)) ? "-SCA-" + Constantes.SIGLAS_INSTITUCION : "-" + Constantes.SIGLAS_INSTITUCION);

                informeTecnicoEia.setNumeroInforme(numeroInforme);
                informeTecnicoEia.setEstudioImpactoAmbiental(estudioImpactoAmbiental);
                informeTecnicoEia.setTipoDocumento(tipoDocumento);
                informeTecnicoEia.setCiudadInforme(responsable.getPersona().getUbicacionesGeografica().getUbicacionesGeografica().getNombre());
                informeTecnicoEia.setNumero(cantidadNotificaciones);
                informeOficioFacade.guardarInformeTecnicoEia(informeTecnicoEia);
            } else {
                if (informeTecnicoEia.getPronunciamiento() != null && !informeTecnicoEia.getPronunciamiento().isEmpty()) {
                    pronunciamiento = informeTecnicoEia.getPronunciamiento().equals("Aprobación");
                }
                
                if(informeTecnicoEia.getCiudadInforme().compareTo(responsable.getPersona().getUbicacionesGeografica().getUbicacionesGeografica().getNombre())!=0)
            	{
            		informeTecnicoEia.setCiudadInforme(responsable.getPersona().getUbicacionesGeografica().getUbicacionesGeografica().getNombre());            		
            	}
                
              //para actualizar el codigo del informe con la nueva área del proyecto
                area = proyectosBean.getProyecto().getAreaResponsable();
                if (area.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_OT)) {
                    String base = area.getAreaAbbreviation();
                    if(!informeTecnicoEia.getNumeroInforme().contains(base)) {
                    	BigInteger numeroSecuencia = informeOficioFacade
                                .obtenerNumeroInforme(TEMP_CONST_NOMBRE_SECUENCIA_NUMERO,
                                        TEMP_CONST_NOMBRE_ESQUEMA_SUIA);
                    	
                    	String numeroInforme = (numeroSecuencia != null ? JsfUtil.rellenarCeros(
                                numeroSecuencia.toString(),
                                TEMP_CONST_TAMANIO_NUMERO_OFICIO) : "N/A")
                                + "-"
                                + JsfUtil.getCurrentYear()
//                                + "-"
//                                + iniciales
                                + "-"
                                + base
                                + ((area.getId() == 257) ? "-SCA-"+Constantes.SIGLAS_INSTITUCION : "-"+Constantes.SIGLAS_INSTITUCION);
                    	
                    	informeTecnicoEia.setNumeroInformeAnterior(informeTecnicoEia.getNumeroInforme());
                    	informeTecnicoEia.setNumeroInforme(numeroInforme);
                    	informeOficioFacade.guardarInformeTecnicoEia(informeTecnicoEia);
                    }
                }
            }            
            
			if (!verInforme){
				
				if(bandejaTareasBean.getTarea().getTaskNameHuman().equals("Elaborar Informe Tecnico y oficio de pronunciamiento") || 
						bandejaTareasBean.getTarea().getTaskNameHuman().equals("Realizar correcciones") ){
					visualizarOficio();
				}else{
					
					TipoDocumentoSistema tipoDocumento = TipoDocumentoSistema.TIPO_INFORME_TECNICO_EIA;	
					tipoDocumento = TipoDocumentoSistema.TIPO_INFORME_TECNICO_EIA;
										
					Documento documento = documentosFacade.documentoXTablaIdXIdDocUnico(proyectosBean.getProyecto().getId(),
							LICENCIA_AMBIENTAL_FACADE, tipoDocumento);
					
					File fileDoc = documentosFacade.descargarFile(documento.getIdAlfresco());
					
					Path path = Paths.get(fileDoc.getAbsolutePath());
					
					byte[] contenido = Files.readAllBytes(path);
					String reporteHtmlfinal = documento.getNombre().replace("/", "-");
					File archivoFinal = new File(JsfUtil.devolverPathReportesHtml(reporteHtmlfinal));
					FileOutputStream file = new FileOutputStream(archivoFinal);
					file.write(contenido);
					file.close();
					
					informePath = JsfUtil.devolverContexto("/reportesHtml/" + documento.getNombre());
					nombreReporte = documento.getNombre();				
				}				
			}    			
			
			ambienteProduccion = Boolean.parseBoolean(tipoAmbiente);
			
			token = true;
			ambienteProduccion = true;
			if (!ambienteProduccion) {
				verificaToken();
				documentoDescargado = true;
			}else{
				token = true;				
			}
			urlAlfresco = "";
    			
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
        } catch (Exception e) {
        }
    }

    public String visualizarOficio() {
        String pathPdf = null;
        try {

            informeTecnicoEia.setFechaInforme(JsfUtil
                    .devuelveFechaEnLetrasSinHora(informeTecnicoEia.getFechaModificacion()!=null?informeTecnicoEia.getFechaModificacion():informeTecnicoEia.getFechaCreacion()));

            if (informeTecnicoEia.getObservaciones() == null) {
                List<ObservacionesFormularios> listaObservaciones = observacionesFacade
                        .listarPorIdClaseNombreClase(
                                estudioImpactoAmbiental.getId(),
                                EstudioImpactoAmbiental.class.getSimpleName());

                String observaciones = "";
                for (ObservacionesFormularios observacionesFormularios : listaObservaciones) {
                    observaciones += observacionesFormularios.getDescripcion()
                            + "";
                }
                informeTecnicoEia.setObservaciones(observaciones);
            }
            // Llenar entity informe
            EntityInformeTecnicoEia entityInforme = new EntityInformeTecnicoEia();
            entityInforme
                    .setAntecendentes(informeTecnicoEia.getAntecendentes());
            entityInforme.setCaracteristicasProyecto(informeTecnicoEia
                    .getCaracteristicasProyecto());
            entityInforme
                    .setCiudadInforme(informeTecnicoEia.getCiudadInforme());
            entityInforme.setConclusionesRecomendaciones(informeTecnicoEia
                    .getConclusionesRecomendaciones());
            entityInforme.setEvaluacionTecnica(informeTecnicoEia
                    .getEvaluacionTecnica());
            entityInforme.setFechaInforme(informeTecnicoEia.getFechaInforme());
            entityInforme
                    .setNumeroInforme(informeTecnicoEia.getNumeroInforme());
            entityInforme.setObjetivos(informeTecnicoEia.getObjetivos());
            entityInforme.setObservacion(informeTecnicoEia.getObservaciones());
			entityInforme.setNombreTecnico(JsfUtil.getLoggedUser().getPersona().getNombre());
            //entityInforme.setFichaTecnica(generarFichaTecnica());
            if (informeTecnicoEia.getOtrasObligaciones() != null) {
                entityInforme.setOtrasObligaciones("<p style='font-weight: bold'>Otras obligaciones</p><br />"
                        + informeTecnicoEia.getOtrasObligaciones());
            }
            nombreFichero = "InformeTecnico" + estudioImpactoAmbiental.getId() + ".pdf";
            nombreReporte = "InformeTecnico.pdf";

            /**
             * Nombre:SUIA
             * Descripción: Toma el área responsabel de ceurdo a la abreviación
             * ParametrosIngreso:
             * PArametrosSalida:
             * Fecha:16/08/2015
             */

            entityInforme.setAreaResponsable(proyectosBean.getProyecto().getAreaResponsable().getAreaAbbreviation());
            /**
             * FIN Toma el área responsabel de ceurdo a la abreviación
             */

            File informePdf = UtilGenerarInforme.generarFichero(
                    plantillaReporte.getHtmlPlantilla(), nombreReporte, true,
                    entityInforme);
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
        verInforme=true;
        return pathPdf;
    }

    public String generarFichaTecnica() throws Exception {
        StringBuilder fichaTecnica = new StringBuilder();

        fichaTecnica.append("<div  style='font-size:12px'> <div> <strong  style='font-size:12px'>Datos del proyecto" +
                "</strong></div><div>  <div> " +
                "<table style=\"width:642px\"> <tbody> <tr> <td style=\"text-align: left; vertical-align: top;font-size:12px;width:120px;\">");
        fichaTecnica.append("<strong  style='font-size:12px'>Nombre proyecto</strong></td> ");

        fichaTecnica.append("<td  style='font-size:12px'>  ");
        fichaTecnica.append(proyectosBean.getProyecto().getNombre());//nombre del proyecto
        fichaTecnica.append(" </td> ");

        fichaTecnica.append("</tr> <tr> <td style=\"text-align: left; vertical-align: top;font-size:12px;\"> <strong  style='font-size:12px'>C&oacute;digo del proyecto</strong> </td> <td style='font-size:12px'>");

        fichaTecnica.append(proyectosBean.getProyecto().getCodigo());//Código proyecto

        fichaTecnica.append("</td> </tr> <tr> <td style=\"text-align: left; vertical-align: top;font-size:12px;\"> <strong  style='font-size:12px'>Proponente</strong></td> <td style='font-size:12px'>");
        fichaTecnica.append(getProponente());//Proponente/Empresa

        fichaTecnica.append("</td> </tr> <tr  > <td style=\"text-align: left; vertical-align: top;font-size:12px;\"> <strong  style='font-size:12px'>Ente responsable</strong></td> <td style='font-size:12px'>");
        fichaTecnica.append(proyectosBean.getProyecto().getAreaResponsable().getAreaName());
        fichaTecnica.append("</td> </tr> </tbody> </table> <p>");

        fichaTecnica.append(getUbicacionGeografica());
        fichaTecnica.append("<p> <strong style='font-size:12px'>Direcci&oacute;n del proyecto, obra o actividad:</strong></p>" +
                "</div> </div> </div> </div> <div>" +
                "<div style='font-size:12px'>");
        fichaTecnica.append(proyectosBean.getProyecto().getDireccionProyecto());
        fichaTecnica.append("</div> <div> &nbsp;</div> </div></div> </div> <div> <div>");
        fichaTecnica.append("<strong style='font-size:12px'>Detalle del proyecto</strong></div>" +
                "<div> <div> <div> <table> <tbody> <tr> <td>" +
                "<strong style='font-size:12px'>Sector</strong></td> <td style='font-size:12px'>");
        fichaTecnica.append(proyectosBean.getProyecto().getTipoSector());
        fichaTecnica.append("</td>" +
                "</tr> <tr> <td> <strong style='font-size:12px'>Superficie</strong></td >" +
                "<td style='font-size:12px'> ");

        fichaTecnica.append(proyectosBean.getProyecto().getArea());
        fichaTecnica.append(proyectosBean.getProyecto().getUnidad());
        fichaTecnica.append("</td> </tr> <tr> <td> <strong style='font-size:12px'>Altitud</strong></td><td style='font-size:12px'>");

        fichaTecnica.append(proyectosBean.getProyecto().getAltitud());

        fichaTecnica.append("</td> </tr> </tbody> </table> </div> </div> </div> </div> ");


        return fichaTecnica.toString();
    }

    public String getProponente() throws ServiceException {
        String label = proyectosBean.getProyecto().getUsuario().getPersona().getNombre();

        Organizacion organizacion = organizacionFacade.buscarPorPersona(proyectosBean.getProyecto().getUsuario().getPersona(),
                proyectosBean.getProyecto().getUsuario().getNombre());
        if (organizacion != null) {
            return organizacion.getNombre();
        }
        return label;
    }


    public String getUbicacionGeografica() throws Exception {

        StringBuilder ubicacionG = new StringBuilder();


        ubicacionG.append("<strong>Ubicaci&oacute;n geogr&aacute;fica</strong></p>" +
                "</div> </div> </div> <div> <div> <div> <div> <div> <div>");
        ubicacionG.append("<table> <thead> <tr> <th style='font-size:12px'>" +
                "Provincia</th> <th style='font-size:12px'> Cant&oacute;n</th>" +
                "<th style='font-size:12px'> Parroquia</th>" + "</tr> </thead> <tbody>");


        for (UbicacionesGeografica ubicacion : proyectosBean.getProyecto().getUbicacionesGeograficas()) {
            String provincia = ubicacion.getUbicacionesGeografica()
                    .getUbicacionesGeografica().getNombre();
            String canton = ubicacion.getUbicacionesGeografica().getNombre();
            String parroquia = ubicacion.getNombre();
            ubicacionG.append("<tr> <td style='font-size:12px'>" +
                    provincia +
                    "</td> <td style='font-size:12px'>" +
                    canton +
                    "</td> <td style='font-size:12px'>" +
                    parroquia +
                    "</td> </tr>");
        }

        ubicacionG.append("</tbody> </table>");


        return ubicacionG.toString();
    }

    public void guardar() {
        try {
        	Map<String, Object> params = new HashMap<String , Object>();
        	params.put("requiereMoficacion", requiereModificaciones);
        	procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getTarea().getProcessInstanceId(), params);
        	
            informeTecnicoEia.setPronunciamiento(pronunciamiento ? "Aprobación" : "Observación");
//            org.apache.commons.lang.StringEscapeUtils.escapeHtml(informeTecnicoEia.getAntecendentes());
            informeOficioFacade.guardarInformeTecnicoEia(informeTecnicoEia);

			// MarielaGuano para actualizacion de certificado de interseccion
			proyectoLicenciamientoAmbientalFacade.actualizarSimpleProyecto(proyectosBean.getProyecto());

            String url = "/prevencion/licenciamiento-ambiental/documentos/informeTecnicoEia.jsf";
            if (!tipo.isEmpty()) {
                url += "?tipo=" + tipo;
            }
            
            visualizarOficio();
			
			JsfUtil.addMessageInfo(JsfUtil.REGISTRO_GUARDADO);			

        } catch (Exception e) {
            LOG.error("Error al guardar oficio", e);
        }
    }


    public void guardarContinuar() {
        try {
            informeTecnicoEia.setPronunciamiento(pronunciamiento ? "Aprobación" : "Observación");
            ///prevencion/licenciamiento-ambiental/documentos/oficioAprobacionEia.jsf
            ///prevencion/licenciamiento-ambiental/documentos/oficioObservacionEia.jsf
            String tipoOficio = pronunciamiento ? "oficioAprobacionEia.jsf" : "oficioObservacionEia.jsf";
//            org.apache.commons.lang.StringEscapeUtils.escapeHtml(informeTecnicoEia.getAntecendentes());
            if (revisar && requiereModificaciones) {
            	informeTecnicoEia.setFinalizado(false);
            }            	
            informeOficioFacade.guardarInformeTecnicoEia(informeTecnicoEia);

			// MarielaGuano para actualizacion de certificado de interseccion
            if(!pronunciamiento) {
            	proyectoActualizacionCIFacade.guardarEstadoActualizacionCertificado(proyectosBean.getProyecto(), null);
            } else {
            	proyectosBean.getProyecto().setEstadoActualizacionCertInterseccion(0);
            	proyectoLicenciamientoAmbientalFacade.actualizarSimpleProyecto(proyectosBean.getProyecto());
            }

            //subirDocuemntoInformeTecnicoAlfresco();
            String url = "/prevencion/licenciamiento-ambiental/documentos/" + tipoOficio;
            if (!tipo.isEmpty()) {
                url += "?tipo=" + tipo;
            }

            if (revisar) {            	            	
            	/* MarielaG
            	 * Valida las observaciones cuando el informe necesita modificaciones 
            	 * Si el informe no necesita modificaciones avanza a revision del oficio
            	 */
                if (requiereModificaciones) {
                	if (validarObservaciones(!requiereModificaciones)) {
	                    Map<String, Object> params = new ConcurrentHashMap<String, Object>();
	                    params.put("requiereMoficacion",requiereModificaciones);
	                    procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getTarea().getProcessInstanceId(), params);
	                    JsfUtil.redirectTo(url);
                	}
                }else{
                	Map<String, Object> params = new ConcurrentHashMap<String, Object>();
                    params.put("requiereMoficacion",requiereModificaciones);
                    procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getTarea().getProcessInstanceId(), params);
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

    public String completarTarea() {
        try {
            informeTecnicoEia.setPronunciamiento(pronunciamiento ? "Aprobación" : "Observación");

            informeOficioFacade.guardarInformeTecnicoEia(informeTecnicoEia);

            Map<String, Object> params = new ConcurrentHashMap<String, Object>();
            if (revisar) {
                if (validarObservaciones(!requiereModificaciones)) {
                    params.put("requiereMoficacion",
                            requiereModificaciones);
                } else {
                    return "";
                }

            }
            params.put("esPronunciamientoAprobacion",
                    pronunciamiento);
            procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getTarea().getProcessInstanceId(), params);
            
            Map<String, Object> data = new ConcurrentHashMap<String, Object>();
            procesoFacade.aprobarTarea(loginBean.getUsuario(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), data);
            JsfUtil.addMessageInfo("Se realizó correctamente la operación.");
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

    public void validarTareaBpm() {

        String url = "/prevencion/licenciamiento-ambiental/documentos/informeTecnicoEia.jsf";
        if (!tipo.isEmpty()) {
            url += "?tipo=" + tipo;
        }
        JsfUtil.validarPaginasUrlTareasBpm(bandejaTareasBean.getTarea(), url);
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
    
    public void subirDocuemntoInformeTecnicoAlfresco() throws Exception {
        licenciaAmbientalFacade.ingresarDocumentos(oficioPdf, proyectosBean.getProyecto().getId(), 
        		proyectosBean.getProyecto().getCodigo(), bandejaTareasBean.getProcessId(), bandejaTareasBean
                        .getTarea().getTaskId(), TipoDocumentoSistema.TIPO_INFORME_TECNICO_EIA,
                LicenciaAmbientalFacade.class.getSimpleName());

    }  
    
	
    public boolean validaMemorando(Pronunciamiento pronunciamiento) throws CmisAlfrescoException, ServiceException{
	   
    	String nombreTabla="";
		if(pronunciamiento.getTipo().equals("Forestal") && pronunciamiento.getNumeroMemorando()!=null){
		   nombreTabla="PRONUNCIAMIENTO FORESTAL";
		   List<Documento>ListDocumentoForestal= new ArrayList<Documento>();
		   ListDocumentoForestal=documentosFacade.documentoXTablaIdXIdDoc(pronunciamiento.getId(), nombreTabla, TipoDocumentoSistema.TIPO_PRONUNCIAMIENTO_FORESTAL);
		   if(ListDocumentoForestal.size()>0){
			   documentoForestal =ListDocumentoForestal.get(0);
			   descargarDocumento(pronunciamiento.getTipo());
			   return true;
		   }
		  }else if(pronunciamiento.getTipo().equals("Biodiversidad") && pronunciamiento.getNumeroMemorando()!=null) {
		   nombreTabla="PRONUNCIAMIENTO BIODIVERSIDAD";
		   List<Documento>ListDocumentoBiodiversidad= new ArrayList<Documento>();
		   ListDocumentoBiodiversidad=documentosFacade.documentoXTablaIdXIdDoc(pronunciamiento.getId(), nombreTabla, TipoDocumentoSistema.TIPO_PRONUNCIAMIENTO_BIODIVERSIDAD);
		   if(ListDocumentoBiodiversidad.size()>0){
			   documentoBiodiversidad = ListDocumentoBiodiversidad.get(0);
			   descargarDocumento(pronunciamiento.getTipo());
			   return true;
		   }
		}
	   return false;
   }
    
    public void descargarDocumento(String tipo) throws CmisAlfrescoException{
    	if(tipo.equals("Forestal")){
    		if(documentoDescargaForestal==null)
    		documentoDescargaForestal=documentosFacade.descargar(documentoForestal.getIdAlfresco());
    	}else {
    		if(documentoDescargaBiodiversidad==null)
    		documentoDescargaBiodiversidad=documentosFacade.descargar(documentoBiodiversidad.getIdAlfresco());
    	}
    }
    
    public StreamedContent getStreamDocumento(String tipo) throws Exception {
        
    	DefaultStreamedContent content = new DefaultStreamedContent();
        if(tipo.equals("Forestal")){
        	if (documentoDescargaForestal != null) {
            content = new DefaultStreamedContent(new ByteArrayInputStream(
               		documentoDescargaForestal), "application/pdf");
            content.setName("PronunciamientoForestal.pdf");
            }
        }else {
        	if (documentoDescargaBiodiversidad != null) {
        	content = new DefaultStreamedContent(new ByteArrayInputStream(
               		documentoDescargaBiodiversidad), "application/pdf");
            content.setName("PronunciamientoBiodiversidad.pdf");
        	}
        }
        
        return content;
    } 
    
    /**
	 * FIRMA ELECTRONICA
	 */
	public boolean verificaToken() {
		if (firmaSoloToken) {
			token = true;
			return token;
		}

		token = false;
		if (JsfUtil.getLoggedUser().getToken() != null && JsfUtil.getLoggedUser().getToken())
			token = true;
		return token;
	}

	public void guardarToken() {
		Usuario usuario = JsfUtil.getLoggedUser();
		usuario.setToken(token);
		try {
			usuarioFacade.guardar(usuario);
			verificaToken();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Descarga la informacion del panel
	 * 
	 * @return
	 * @throws IOException
	 */
	public StreamedContent descargarInformacion() throws IOException {
		DefaultStreamedContent content = null;
		try {
			
//			subirDocuemntoInformeTecnicoAlfresco();
			
			byte[] documentoContent;
			Documento auxdoc = new Documento();
			Boolean generar = false;
			TipoDocumento auxTipoDocumento = new TipoDocumento();
			TipoDocumentoSistema tipoDocumento = TipoDocumentoSistema.TIPO_INFORME_TECNICO_EIA;

			tipoDocumento = TipoDocumentoSistema.TIPO_INFORME_TECNICO_EIA;
			auxTipoDocumento = obtenerTipoDocumento(TipoDocumentoSistema.TIPO_INFORME_TECNICO_EIA.getIdTipoDocumento());
			auxdoc = documentosFacade.documentoXTablaIdXIdDocUnicos(proyectosBean.getProyecto().getId(),
					LicenciaAmbientalFacade.class.getSimpleName(), tipoDocumento);

			documentoContent = documentosFacade.descargar(auxdoc.getIdAlfresco());
			if (documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(auxdoc.getNombre());
				documentoDescargado = true;
			} else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);

		}

		catch (Exception e) {
			JsfUtil.addMessageError(
					"Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}

	/**
	 * METODO DE TIPO DOCUMENTO SE USA PARA LA FIRMA ELECTRONICA
	 * 
	 * @param codTipo
	 * @return
	 */
	public TipoDocumento obtenerTipoDocumento(Integer codTipo) {
		try {
			Query query = crudServiceBean.getEntityManager()
					.createQuery("SELECT o FROM TipoDocumento o WHERE o.estado=true and o.id=:id");
			query.setParameter("id", codTipo);
			return (TipoDocumento) query.getResultList().get(0);
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * Sube la informacion firmada En el panel
	 * 
	 * @param event
	 */
	public void uploadListenerInformacionFirmada(FileUploadEvent event) {
		if (documentoDescargado) {
			TipoDocumento auxTipoDocumento = new TipoDocumento();
			TipoDocumentoSistema tipoDocumento = TipoDocumentoSistema.TIPO_INFORME_TECNICO_EIA;

			 
			tipoDocumento = TipoDocumentoSistema.TIPO_INFORME_TECNICO_EIA;
			auxTipoDocumento = obtenerTipoDocumento(
					TipoDocumentoSistema.TIPO_INFORME_TECNICO_EIA.getIdTipoDocumento());

			 
			byte[] contenidoDocumento = event.getFile().getContents();
			documentoInformacionManual = new Documento();
			documentoInformacionManual.setId(null);
			documentoInformacionManual.setContenidoDocumento(contenidoDocumento);
			documentoInformacionManual.setNombre(event.getFile().getFileName());
			documentoInformacionManual.setExtesion(".pdf");
			documentoInformacionManual.setMime("application/pdf");
			documentoInformacionManual.setNombre(NOMBRE_PDF);

			documentoInformacionManual.setIdTable(proyectosBean.getProyecto().getId());
			documentoInformacionManual.setNombreTabla(LICENCIA_AMBIENTAL_FACADE);
			documentoInformacionManual.setTipoDocumento(auxTipoDocumento);
			
			try {
				documentosFacade.guardarDocumentoAlfrescoSinProyecto(String.valueOf(proyectosBean.getProyecto().getId()),
						String.valueOf(bandejaTareasBean.getTarea().getProcessInstanceId()),
						Long.valueOf(bandejaTareasBean.getTarea().getTaskId()), documentoInformacionManual,
						tipoDocumento);
			} catch (ServiceException e) {
				e.printStackTrace();
			} catch (CmisAlfrescoException e) {
				e.printStackTrace();
			}
			
			informacionSubida = true;

			nombreDocumentoFirmado = event.getFile().getFileName();
		} else {
			JsfUtil.addMessageError(
					"No ha descargado el documento para la firmas");
		}
	}
	
	/**
	 * PARA FIRMA CON TOKEN
	 * 
	 * @param event
	 */
//	@SuppressWarnings("static-access")
//	public String firmarOficio() {
//		try {
//			if(!revisar){
//				subirDocuemntoInformeTecnicoAlfresco();
//			}			
//			
//			TipoDocumento auxTipoDocumento = new TipoDocumento();
//			TipoDocumentoSistema tipoDocumento = TipoDocumentoSistema.TIPO_INFORME_TECNICO_EIA;			
//			
//			tipoDocumento = TipoDocumentoSistema.TIPO_INFORME_TECNICO_EIA;
//			auxTipoDocumento = obtenerTipoDocumento(TipoDocumentoSistema.TIPO_INFORME_TECNICO_EIA.getIdTipoDocumento());
//			documentoSubido = documentosFacade.documentoXTablaIdXIdDocUnico(proyectosBean.getProyecto().getId(),
//					LICENCIA_AMBIENTAL_FACADE, tipoDocumento);
//
//			if (documentoSubido != null && documentoSubido.getIdAlfresco() != null) {
//				String documento = documentosFacade.direccionDescarga(documentoSubido);
//				DigitalSign firmaE = new DigitalSign();
//				documentoDescargado = true;
//				return firmaE.sign(documento, loginBean.getUsuario().getNombre());
//
//			} else
//				return "";
//		} catch (Throwable e) {
//			JsfUtil.addMessageError("Error al realizar la operación.");
//			return "";
//		}
//	}
	
	public void abrirFirma(){
		try {			
			if (revisar	&& (bandejaTareasBean.getTarea().getTaskNameHuman().equals("Revisar Documentacion de Observacion") 
					|| bandejaTareasBean.getTarea().getTaskNameHuman().equals("Revisar Documentacion de Aprobacion"))) {
				
				guardarContinuar();
				
			} else {
				
				TipoDocumentoSistema tipoDocumento = TipoDocumentoSistema.TIPO_INFORME_TECNICO_EIA;	
				if(!revisar){
										
					subirDocuemntoInformeTecnicoAlfresco();
				}			
				
				documentoSubido = documentosFacade.documentoXTablaIdXIdDocUnico(proyectosBean.getProyecto().getId(),
						LICENCIA_AMBIENTAL_FACADE, tipoDocumento);

				if (documentoSubido != null && documentoSubido.getIdAlfresco() != null) {
					String documento = documentosFacade.direccionDescarga(documentoSubido);
					documentoDescargado = true;
					urlAlfresco = DigitalSign.sign(documento, loginBean.getUsuario().getNombre());
				}
				System.out.println("urlAlfresco: " + urlAlfresco);
				RequestContext.getCurrentInstance().update("formDialogs");				
				
				RequestContext context = RequestContext.getCurrentInstance();
				context.execute("PF('signDialog').show();");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
    
}
