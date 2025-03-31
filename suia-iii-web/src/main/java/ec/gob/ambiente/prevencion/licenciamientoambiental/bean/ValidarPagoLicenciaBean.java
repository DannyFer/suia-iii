package ec.gob.ambiente.prevencion.licenciamientoambiental.bean;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.Serializable;
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

import lombok.Getter;
import lombok.Setter;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.LicenciaAmbiental;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.eia.facade.LicenciaAmbientalFacade;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.ProyectoLicenciaAmbientalFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class ValidarPagoLicenciaBean implements Serializable {

    private static final long serialVersionUID = 2975637042323496247L;

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
            .getLogger(ValidarPagoLicenciaBean.class);

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
	private OrganizacionFacade organizacionFacade;

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
            esEmpresaPublica = false;
            
            if (licenciaAmbiental == null) {


                Map<String, Object> variables = procesoFacade
                        .recuperarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getTarea()
                                .getProcessInstanceId());

                String tecnicoName = (String) variables.get("u_TecnicoResponsable");


                licenciaAmbiental = new LicenciaAmbiental();
                licenciaAmbiental.setProponente(proyectosBean.getProyecto().getUsuario());
                licenciaAmbiental.setCategoriaLicencia(proyectosBean.getProyecto().getCatalogoCategoria().getCategoria());
                licenciaAmbiental.setProyecto(proyectosBean.getProyecto());
                licenciaAmbiental.setTecnicoResponsable(usuarioFacade.buscarUsuario(tecnicoName));
            } else {
            	if(!recuperarDocumentos()){
            		List<Documento> documentos = documentosFacade.documentoXTablaIdXIdDoc(proyectosBean.getProyecto().getId(), LicenciaAmbiental.class.getSimpleName()
                            , TipoDocumentoSistema.TIPO_LICENCIA_AMBIENTAL);
                    if (!documentos.isEmpty()) {
                        documentoAlfresco = documentos.get(0);
                        nombreFichero = documentoAlfresco.getNombre();
                        archivo = documentosFacade.descargar(documentoAlfresco.getIdAlfresco());
                        subido = true;
                    }
            	}else{
                	subido = false;
                }
            	
        		Organizacion organizacion = organizacionFacade.buscarPorPersona(proyectosBean.getProyecto().getUsuario()
						.getPersona(), proyectosBean.getProyecto().getUsuario().getNombre());
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
            comprobante = JsfUtil.upload(event);
            subido = true;
            nombreFichero = comprobante.getName();
            JsfUtil.addMessageInfo("El archivo " + event.getFile().getFileName() + " fue adjuntado correctamente.");
        }
    }

    public void validarTareaBpm() {
        JsfUtil.validarPaginasUrlTareasBpm(bandejaTareasBean.getTarea(), "/prevencion/licenciamiento-ambiental/validarPagoLicencia.jsf");
    }

    public String realizarTarea() {
        try {

            licenciaAmbientalFacade.archivarProcesoLicenciaAmbiental(licenciaAmbiental);
            if (comprobante != null) {
                subirDocuemntoAlfresco();
            }
            //Set process variables
            Map<String, Object> params = new ConcurrentHashMap<>();
            taskBeanFacade.approveTask(loginBean.getNombreUsuario(),
                    bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getTarea().getProcessInstanceId(), params, loginBean.getPassword(),
                    Constantes.getUrlBusinessCentral(), Constantes.getRemoteApiTimeout(), Constantes.getNotificationService());
            JsfUtil.addMessageInfo("Se realizó correctamente la operación.");
            return JsfUtil.actionNavigateTo("/bandeja/bandejaTareas.jsf");
        } catch (Exception e) {
            LOG.error("Error al enviar los datos de validar pago de licencia.", e);
            JsfUtil.addMessageError("Ocurrio un error al enviar la información.");
        }


        return "";
    }


    public void subirDocuemntoAlfresco() throws Exception {
        licenciaAmbientalFacade.ingresarDocumentos(comprobante,
        		proyectosBean.getProyecto().getId(),
        		proyectosBean.getProyecto().getCodigo(),
                bandejaTareasBean.getProcessId(), bandejaTareasBean.getTarea().getTaskId(), TipoDocumentoSistema.TIPO_LICENCIA_AMBIENTAL, LicenciaAmbiental.class.getSimpleName());

    }


    public StreamedContent getStreamContent() throws Exception {
        DefaultStreamedContent content = null;
        try {
            if (comprobante != null) {
                Path path = Paths.get(comprobante.getAbsolutePath());
                byte[] data = Files.readAllBytes(path);
                content = new DefaultStreamedContent(new ByteArrayInputStream(data));
                content.setName(comprobante.getName());
            } else if (archivo != null) {
                content = new DefaultStreamedContent(new ByteArrayInputStream(archivo));
                content.setName(documentoAlfresco.getNombre());
            } else {
                JsfUtil.addMessageError("Error al obtener el archivo.");
            }
        } catch (Exception exception) {
            LOG.error("Error al obtener el archivo.", exception);
            JsfUtil.addMessageError("Error al obtener el archivo.");
        }
        return content;
    }
    
    public boolean recuperarDocumentos() throws Exception { 
    	
    	List<Integer> listaTipos = new ArrayList<Integer>(); 
    	listaTipos.add(TipoDocumentoSistema.TIPO_RLA_FACTURA_PERMISO_AMBIENTAL.getIdTipoDocumento());
    	listaTipos.add(TipoDocumentoSistema.TIPO_RLA_PROTOCOLIZACION_PAGO.getIdTipoDocumento());
    	listaTipos.add(TipoDocumentoSistema.TIPO_RLA_POLIZA_PMA.getIdTipoDocumento());
    	listaTipos.add(TipoDocumentoSistema.TIPO_RLA_JUSTIFICACION_PMA.getIdTipoDocumento());
    	listaTipos.add(TipoDocumentoSistema.TIPO_RLA_CRONOGRAMA_PMA.getIdTipoDocumento());
    	
    	List<Documento> respaldosPago = documentosFacade.recuperarDocumentosPorTipo(proyectosBean.getProyecto().getId(), LicenciaAmbiental.class.getSimpleName(), listaTipos);
    	
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
    	return false;
    }
    
    public StreamedContent getStreamContent(Integer tipoDocumento)
			throws Exception {

		Documento documento = null;

		switch (tipoDocumento) {
			case tipoFacturaPermisoAmbiental:
				facturaPermisoAmbiental = descargarAlfresco(facturaPermisoAmbiental);
				documento = facturaPermisoAmbiental;
				break;
			case tipoProtocolizacionPago:
				protocolizacionPago = descargarAlfresco(protocolizacionPago);
				documento = protocolizacionPago;
				break;
			case tipoPolizaPma:
				polizaPma = descargarAlfresco(polizaPma);
				documento = polizaPma;
				break;
			case tipoJustificacionPma:
				justificacionPma = descargarAlfresco(justificacionPma);
				documento = justificacionPma;
				break;
			case tipoCronogramaPma:
				cronogramaPma = descargarAlfresco(cronogramaPma);
				documento = cronogramaPma;
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
   
}

