package ec.gob.ambiente.prevencion.licenciamientoambiental.bean;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.Serializable;
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

import lombok.Getter;
import lombok.Setter;

import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.DocumentosTareasProceso;
import ec.gob.ambiente.suia.domain.LicenciaAmbiental;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.eia.facade.EstudioImpactoAmbientalFacade;
import ec.gob.ambiente.suia.eia.facade.LicenciaAmbientalFacade;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.ProyectoLicenciaAmbientalFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class ValidarPagoLicencia2Bean implements Serializable {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
            .getLogger(ValidarPagoLicencia2Bean.class);
    private static final long serialVersionUID = -1752252953222734370L;

    @EJB
    private DocumentosFacade documentosFacade;

    @EJB
    private ProyectoLicenciaAmbientalFacade proyectoLicenciaAmbientalFacade;

    @EJB
    private ProcesoFacade procesoFacade;

    @EJB
    private LicenciaAmbientalFacade licenciaAmbientalFacade;

    @EJB
    private UsuarioFacade usuarioFacade;

    @EJB
	private EstudioImpactoAmbientalFacade estudioImpactoAmbientalFacade;

    @EJB
	private OrganizacionFacade organizacionFacade;
    @EJB
    private AreaFacade areaFacade;

    @Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;

    @EJB(lookup = Constantes.JBPM_EJB_TASK_BEAN)
    private TaskBeanFacade taskBeanFacade;

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
    private ProyectoLicenciamientoAmbiental proyecto;

    @Getter
    private File comprobante;

    @Getter
    @Setter
    private boolean subido = false;

    @Getter
    @Setter
    private String nombreFichero;



    @Getter
    @Setter
    private byte[] archivo;

    @Getter
    @Setter
    private Documento documentoAlfresco;


    @Getter
    @Setter
    private LicenciaAmbiental licenciaAmbiental;

    
    @Getter
    private Documento facturaPermisoAmbiental, protocolizacionPago, polizaPma, justificacionPma, cronogramaPma;
    
    @Getter
	@Setter
	private Integer costoImplementacionPma;
    
    @Getter
    private Boolean cargaFacturaPermisoAmbiental, cargaProtocolizacionPago, cargaPolizaPma, cargaJustificacionPma, cargaCronogramaPma;
    
    @Getter
    private Boolean esEmpresaPublica;
    
    public static final int tipoFacturaPermisoAmbiental = 1020;
    public static final int tipoProtocolizacionPago = 1021;
    public static final int tipoPolizaPma = 1022;
    public static final int tipoJustificacionPma = 1023;
    public static final int tipoCronogramaPma = 1024;
    
    public Date getCurrentDate() {
        return new Date();
    }

    @PostConstruct
    public void init() {
        try {
            licenciaAmbiental = licenciaAmbientalFacade.obtenerLicenciaAmbientallPorProyectoId(proyectosBean.getProyecto().getId());
            proyecto = proyectosBean.getProyecto();
            
            JsfUtil.cargarObjetoSession(Constantes.SESSION_EIA_OBJECT, estudioImpactoAmbientalFacade.obtenerPorProyecto(proyecto));
            
            cargaFacturaPermisoAmbiental = false;
            cargaProtocolizacionPago = false;
            cargaPolizaPma = false;
            cargaJustificacionPma = false;
            cargaCronogramaPma = false;
            esEmpresaPublica = false;
            
            if (licenciaAmbiental == null) {


                Map<String, Object> variables = procesoFacade
                        .recuperarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean
                                .getTarea().getProcessInstanceId());

                String tecnicoName = (String) variables.get("u_TecnicoResponsable");


                licenciaAmbiental = new LicenciaAmbiental();
                licenciaAmbiental.setProponente(proyecto.getUsuario());
                licenciaAmbiental.setCategoriaLicencia(proyecto.getCatalogoCategoria().getCategoria());
                licenciaAmbiental.setProyecto(proyecto);
                licenciaAmbiental.setTecnicoResponsable(usuarioFacade.buscarUsuario(tecnicoName));
            } else {            	
            	if(!recuperarDocumentos()){
            		List<Documento> documentos = documentosFacade.documentoXTablaIdXIdDoc(proyectosBean.getProyecto().getId(), LicenciaAmbiental.class.getSimpleName()
                            , TipoDocumentoSistema.TIPO_LICENCIA_AMBIENTAL);
                    if (!documentos.isEmpty()) {
                        documentoAlfresco = documentos.get(0);
                        nombreFichero = documentoAlfresco.getNombre();
//                        archivo = documentosFacade.descargar(documentoAlfresco.getIdAlfresco());
                        subido = true;
                    }
            	}
            	
        		Organizacion organizacion = organizacionFacade.buscarPorPersona(loginBean.getUsuario()
						.getPersona(), loginBean.getUsuario().getNombre());
				if (organizacion != null) {
					if(organizacion.getTipoOrganizacion().getDescripcion().equals("EP")){
						esEmpresaPublica = true;
					}
				}
            }
        } catch (Exception e) {
            JsfUtil.addMessageError("Error al realizar la operación. Intente más tarde.");
            LOG.error("Error al realizar la operación.", e);
        }
    }
    
    public void adjuntarComprobante(FileUploadEvent event) {
        if (event != null) {
        	StringBuilder functionJs = new StringBuilder();
        	String tipoDocumentoString = (String) event.getComponent().getAttributes().get("tipoDocumento");
        	
        	int tipoDocumento = Integer.parseInt(tipoDocumentoString);
        	
			switch (tipoDocumento) {
			case tipoFacturaPermisoAmbiental:
				facturaPermisoAmbiental = this.uploadListener(event, LicenciaAmbiental.class, "pdf");
				functionJs.append("removeHighLightComponent('frmDatos:fileUploadFactura');");
				cargaFacturaPermisoAmbiental = true;
				break;
			case tipoProtocolizacionPago:
				protocolizacionPago = this.uploadListener(event, LicenciaAmbiental.class, "pdf");
				functionJs.append("removeHighLightComponent('frmDatos:fileUploadProtocolizacionPago');");
				 cargaProtocolizacionPago = true;
				break;
			case tipoPolizaPma:
				polizaPma = this.uploadListener(event, LicenciaAmbiental.class, "pdf");
				functionJs.append("removeHighLightComponent('frmDatos:fileUploadPoliza');");
				 cargaPolizaPma = true;
				break;
			case tipoJustificacionPma:
				justificacionPma = this.uploadListener(event, LicenciaAmbiental.class, "pdf");
				functionJs.append("removeHighLightComponent('frmDatos:fileUploadJustificacion');");
				cargaJustificacionPma = true;
				break;
			case tipoCronogramaPma:
				cronogramaPma = this.uploadListener(event, LicenciaAmbiental.class, "pdf");
				if(esEmpresaPublica)
					functionJs.append("removeHighLightComponent('frmDatos:fileUploadCronograma');");
				else
					functionJs.append("removeHighLightComponent('frmDatos:fileUploadCronograma_2');");
				cargaCronogramaPma = true;
				break;
			default:
				break;
			}
        	
			RequestContext.getCurrentInstance().execute(functionJs.toString());
            JsfUtil.addMessageInfo("El archivo " + event.getFile().getFileName() + " fue adjuntado correctamente.");
        }
    }

    public void validarTareaBpm() {
        JsfUtil.validarPaginasUrlTareasBpm(bandejaTareasBean.getTarea(), "/prevencion/licenciamiento-ambiental/adjuntarRespaldo.jsf");
    }

    public String realizarTarea() {
        try {
        	if (!validarFinalizar())
				return "";
        	
            licenciaAmbientalFacade.archivarProcesoLicenciaAmbiental(licenciaAmbiental);
            
            guardarDocumentos();
            
            //Set process variables
            Map<String, Object> params = new ConcurrentHashMap<>();
            
            //para actualizar el usuario del director financiero
            String usrDirectorFinanciero = areaFacade.getDirectorPlantaCentralPorArea("role.pc.director.financiero").getNombre(); //Dirección Financiera
            Area areaProyecto = areaFacade.getArea(proyecto.getAreaResponsable().getId());
            if (!areaProyecto.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_PC)) {
            	Area areaUsuarios = areaProyecto;
                if(areaProyecto.getTipoArea().getSiglas().toUpperCase().equals(Constantes.SIGLAS_TIPO_AREA_OT)) 
                	areaUsuarios = areaProyecto.getArea(); //zonal
            	usrDirectorFinanciero = areaFacade.getUsuarioPorRolArea("role.pc.director.financiero", areaUsuarios).getNombre(); //Dirección Financiera
            }
            
            params.put("u_DirectorFinanciero", usrDirectorFinanciero);
            procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getTarea().getProcessInstanceId(), params);
            
            taskBeanFacade.approveTask(loginBean.getNombreUsuario(),
                    bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getTarea().getProcessInstanceId(), params, loginBean.getPassword(),
                    Constantes.getUrlBusinessCentral(),Constantes.getRemoteApiTimeout(),Constantes.getNotificationService());
            JsfUtil.addMessageInfo("Se realizó correctamente la operación.");
            procesoFacade.envioSeguimientoLicenciaAmbiental(loginBean.getUsuario(), bandejaTareasBean.getProcessId());
            return JsfUtil.actionNavigateTo("/bandeja/bandejaTareas.jsf");
        } catch (Exception e) {
            LOG.error("Error al enviar los datos de validar pago de licencia.", e);
            JsfUtil.addMessageError("Ocurrio un error al enviar la información.");
        }
        return "";
    }


    public void subirDocuemntoAlfresco() throws Exception {
        licenciaAmbientalFacade.ingresarDocumentos(comprobante,
                proyecto.getId(),
                proyecto.getCodigo(),
                bandejaTareasBean.getProcessId(), bandejaTareasBean.getTarea().getTaskId(), TipoDocumentoSistema.TIPO_LICENCIA_AMBIENTAL, LicenciaAmbiental.class.getSimpleName());

    }

    public StreamedContent getStreamContent(Integer tipoDocumento)
			throws Exception {

		Documento documento = null;

		switch (tipoDocumento) {
			case tipoFacturaPermisoAmbiental:
				documento = descargarAlfresco(facturaPermisoAmbiental);
				break;
			case tipoProtocolizacionPago:
				documento = descargarAlfresco(protocolizacionPago);
				break;
			case tipoPolizaPma:
				documento = descargarAlfresco(polizaPma);
				break;
			case tipoJustificacionPma:
				documento = descargarAlfresco(justificacionPma);
				break;
			case tipoCronogramaPma:
				documento = descargarAlfresco(cronogramaPma);
				break;
		}

		DefaultStreamedContent content = null;

		try {
			if (documento != null && documento.getNombre() != null
					&& documento.getContenidoDocumento() != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(
						documento.getContenidoDocumento()));
				content.setName(documento.getNombre());

			} else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);

		} catch (Exception exception) {
			LOG.error(JsfUtil.MESSAGE_ERROR_ALFRESCO, exception);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
		}
		return content;
	}
    
	public boolean recuperarDocumentos() throws Exception {
		try {
			List<Integer> listaTipos = new ArrayList<Integer>();
			listaTipos.add(TipoDocumentoSistema.TIPO_RLA_FACTURA_PERMISO_AMBIENTAL
					.getIdTipoDocumento());
			listaTipos.add(TipoDocumentoSistema.TIPO_RLA_PROTOCOLIZACION_PAGO
					.getIdTipoDocumento());
			listaTipos.add(TipoDocumentoSistema.TIPO_RLA_POLIZA_PMA
					.getIdTipoDocumento());
			listaTipos.add(TipoDocumentoSistema.TIPO_RLA_JUSTIFICACION_PMA
					.getIdTipoDocumento());
			listaTipos.add(TipoDocumentoSistema.TIPO_RLA_CRONOGRAMA_PMA
					.getIdTipoDocumento());
	
			List<Documento> respaldosPago = documentosFacade
					.recuperarDocumentosPorTipo(
							proyectosBean.getProyecto().getId(),
							LicenciaAmbiental.class.getSimpleName(), listaTipos);

			if (!respaldosPago.isEmpty()) {
				for (Documento documento : respaldosPago) {
					switch (documento.getTipoDocumento().getId()) {
					case tipoFacturaPermisoAmbiental:
						if (facturaPermisoAmbiental == null)
							facturaPermisoAmbiental = documento;
						break;
					case tipoProtocolizacionPago:
						if (protocolizacionPago == null)
							protocolizacionPago = documento;
						break;
					case tipoPolizaPma:
						if (polizaPma == null)
							polizaPma = documento;
						break;
					case tipoJustificacionPma:
						if (justificacionPma == null)
							justificacionPma = documento;
						break;
					case tipoCronogramaPma:
						if (cronogramaPma == null)
							cronogramaPma = documento;
						break;
					default:
						break;
					}
				}
				return true;
			}
		} catch (Exception e) {
			throw e;
		}
		return false;
	}
    
    /**
	 * Descarga documento desde el Alfresco
	 *
	 * @param documento
	 * @return
	 * @throws CmisAlfrescoException
	 */
	public Documento descargarAlfresco(Documento documento)
			throws CmisAlfrescoException {
		byte[] documentoContenido = null;
		if (documento != null && documento.getIdAlfresco() != null)
			documentoContenido = documentosFacade
					.descargar(documento.getIdAlfresco());
		if (documentoContenido != null)
			documento.setContenidoDocumento(documentoContenido);
		return documento;
	}

	private Documento uploadListener(FileUploadEvent event, Class<?> clazz,
			String extension) {
		byte[] contenidoDocumento = event.getFile().getContents();
		Documento documento = crearDocumento(contenidoDocumento, clazz,
				extension);
		documento.setNombre(event.getFile().getFileName());
		return documento;
	}
	
	/**
	 * Crea el documento
	 *
	 * @param contenidoDocumento
	 *            arreglo de bytes
	 * @param clazz
	 *            Clase a la cual se va a ligar al documento
	 * @param extension
	 *            extension del archivo
	 * @return Objeto de tipo Documento
	 */
	public Documento crearDocumento(byte[] contenidoDocumento, Class<?> clazz,
									String extension) {
		Documento documento = new Documento();
		documento.setContenidoDocumento(contenidoDocumento);
		documento.setNombreTabla(clazz.getSimpleName());
		documento.setIdTable(0);
		documento.setExtesion("." + extension);

		documento.setMime(extension == "pdf" ? "application/pdf"
				: "application/vnd.ms-excel");
		return documento;
	}
		
	public boolean validarFinalizar(){
		List<String> mensajesError = new ArrayList<String>();
		StringBuilder functionJs = new StringBuilder();
		
		if (facturaPermisoAmbiental == null
				|| facturaPermisoAmbiental.getNombre() == null
				|| facturaPermisoAmbiental.getNombre().equals("")) {// NO VALIDO
			mensajesError.add("El documento 'Factura por permiso ambiental' es requerido.");
			functionJs.append("highlightComponent('frmDatos:fileUploadFactura');");
		} else {
			functionJs.append("removeHighLightComponent('frmDatos:fileUploadFactura');");
		}
		
		if (protocolizacionPago == null
				|| protocolizacionPago.getNombre() == null
				|| protocolizacionPago.getNombre().equals("")) {// NO VALIDO
			mensajesError.add("El documento 'Protocolización del pago por emisión de permiso ambiental' es requerido.");
			functionJs.append("highlightComponent('frmDatos:fileUploadProtocolizacionPago');");
		} else {
			functionJs.append("removeHighLightComponent('frmDatos:fileUploadProtocolizacionPago');");
		}
		
		if (!esEmpresaPublica && (polizaPma == null
				|| polizaPma.getNombre() == null
				|| polizaPma.getNombre().equals(""))) {// NO VALIDO
			mensajesError.add("El documento 'Póliza o garantía bancaria por el 100% del costo de implementación del PMA' es requerido.");
			functionJs.append("highlightComponent('frmDatos:fileUploadPoliza');");
		} else {
			functionJs.append("removeHighLightComponent('frmDatos:fileUploadPoliza');");
		}
		
		if (justificacionPma == null
				|| justificacionPma.getNombre() == null
				|| justificacionPma.getNombre().equals("")) {// NO VALIDO
			mensajesError.add("El documento 'Justificación del costo de las medidas incluidas dentro del PMA' es requerido.");
			functionJs.append("highlightComponent('frmDatos:fileUploadJustificacion');");
		} else {
			functionJs.append("removeHighLightComponent('frmDatos:fileUploadJustificacion');");
		}
		
		if (cronogramaPma == null
				|| cronogramaPma.getNombre() == null
				|| cronogramaPma.getNombre().equals("")) {// NO VALIDO
			mensajesError.add("El documento 'Cronograma valorado del PMA' es requerido.");
			if(esEmpresaPublica)
				functionJs.append("highlightComponent('frmDatos:fileUploadCronograma');");
			else
				functionJs.append("highlightComponent('frmDatos:fileUploadCronograma_2');");
		} else {
			if(esEmpresaPublica)
				functionJs.append("removeHighLightComponent('frmDatos:fileUploadCronograma');");
			else
				functionJs.append("removeHighLightComponent('frmDatos:fileUploadCronograma_2');");
		}
		
		RequestContext.getCurrentInstance().execute(functionJs.toString());
		if (mensajesError.isEmpty()) {
			return true;
		} else {
			JsfUtil.addMessageError(mensajesError);
			return false;
		}
	}
	
	public void guardarDocumentos() throws Exception {
		try {
			if (cargaFacturaPermisoAmbiental && facturaPermisoAmbiental != null
					&& facturaPermisoAmbiental.getContenidoDocumento() != null) {
				
				DocumentosTareasProceso documentoTarea = new DocumentosTareasProceso();
		        documentoTarea.setIdTarea(bandejaTareasBean.getTarea().getTaskId());
		        documentoTarea.setProcessInstanceId(bandejaTareasBean.getProcessId());
		        
				facturaPermisoAmbiental.setIdTable(proyecto.getId());
				documentosFacade.guardarDocumentoAlfresco(
						proyecto.getCodigo(), "LicenciaAmbiental",
		                bandejaTareasBean.getProcessId(),
		                facturaPermisoAmbiental,
						TipoDocumentoSistema.TIPO_RLA_FACTURA_PERMISO_AMBIENTAL, documentoTarea);
			}
			if (cargaProtocolizacionPago && protocolizacionPago != null
					&& protocolizacionPago.getContenidoDocumento() != null) {
				
				DocumentosTareasProceso documentoTarea = new DocumentosTareasProceso();
		        documentoTarea.setIdTarea(bandejaTareasBean.getTarea().getTaskId());
		        documentoTarea.setProcessInstanceId(bandejaTareasBean.getProcessId());
		        
		        protocolizacionPago.setIdTable(proyecto.getId());
				documentosFacade.guardarDocumentoAlfresco(
						proyecto.getCodigo(), "LicenciaAmbiental",
		                bandejaTareasBean.getProcessId(),
		                protocolizacionPago,
						TipoDocumentoSistema.TIPO_RLA_PROTOCOLIZACION_PAGO, documentoTarea);
			}
			if (cargaPolizaPma && polizaPma != null
					&& polizaPma.getContenidoDocumento() != null) {
				
				DocumentosTareasProceso documentoTarea = new DocumentosTareasProceso();
		        documentoTarea.setIdTarea(bandejaTareasBean.getTarea().getTaskId());
		        documentoTarea.setProcessInstanceId(bandejaTareasBean.getProcessId());
		        
		        polizaPma.setIdTable(proyecto.getId());
				documentosFacade.guardarDocumentoAlfresco(
						proyecto.getCodigo(), "LicenciaAmbiental",
		                bandejaTareasBean.getProcessId(),
		                polizaPma,
						TipoDocumentoSistema.TIPO_RLA_POLIZA_PMA, documentoTarea);
			}
			if (cargaJustificacionPma && justificacionPma != null
					&& justificacionPma.getContenidoDocumento() != null) {
				
				DocumentosTareasProceso documentoTarea = new DocumentosTareasProceso();
		        documentoTarea.setIdTarea(bandejaTareasBean.getTarea().getTaskId());
		        documentoTarea.setProcessInstanceId(bandejaTareasBean.getProcessId());
		        
		        justificacionPma.setIdTable(proyecto.getId());
				documentosFacade.guardarDocumentoAlfresco(
						proyecto.getCodigo(), "LicenciaAmbiental",
		                bandejaTareasBean.getProcessId(),
		                justificacionPma,
						TipoDocumentoSistema.TIPO_RLA_JUSTIFICACION_PMA, documentoTarea);
			}
			if (cargaCronogramaPma && cronogramaPma != null
					&& cronogramaPma.getContenidoDocumento() != null) {
				
				DocumentosTareasProceso documentoTarea = new DocumentosTareasProceso();
		        documentoTarea.setIdTarea(bandejaTareasBean.getTarea().getTaskId());
		        documentoTarea.setProcessInstanceId(bandejaTareasBean.getProcessId());
		        
		        cronogramaPma.setIdTable(proyecto.getId());
				documentosFacade.guardarDocumentoAlfresco(
						proyecto.getCodigo(), "LicenciaAmbiental",
		                bandejaTareasBean.getProcessId(),
		                cronogramaPma,
						TipoDocumentoSistema.TIPO_RLA_CRONOGRAMA_PMA, documentoTarea);
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	public void validarFechaIni(SelectEvent event)
	{
		this.licenciaAmbiental.setFechaVigencia(null);
	}
}

