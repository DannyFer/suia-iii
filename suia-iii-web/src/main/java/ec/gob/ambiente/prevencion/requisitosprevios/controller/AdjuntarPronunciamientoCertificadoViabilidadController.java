package ec.gob.ambiente.prevencion.requisitosprevios.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.persistence.Query;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.prevencion.requisitosprevios.bean.AdjuntarPronunciamientoCertificadoViabilidadBean;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.TipoDocumento;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.eia.facade.LicenciaAmbientalFacade;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.ProyectoLicenciaAmbientalFacade;
import ec.gob.ambiente.suia.prevencion.requisitosPrevios.RequisitosPreviosLicenciaFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.DigitalSign;
import ec.gob.ambiente.suia.utils.JsfUtil;

@RequestScoped
@ManagedBean
public class AdjuntarPronunciamientoCertificadoViabilidadController implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -3710785362083232386L;
    private static final Logger LOGGER = Logger.getLogger(AdjuntarPronunciamientoCertificadoViabilidadController.class);
    @Getter
    @Setter
    @ManagedProperty(value = "#{adjuntarPronunciamientoCertificadoViabilidadBean}")
    private AdjuntarPronunciamientoCertificadoViabilidadBean adjuntarPronunciamientoCertificadoViabilidadBean;
    @EJB
    private RequisitosPreviosLicenciaFacade requisitosPreviosFacade;
    @EJB(lookup = Constantes.JBPM_EJB_TASK_BEAN)
    private TaskBeanFacade taskBeanFacade;
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
    @ManagedProperty(value = "#{proyectosBean}")
    private ProyectosBean proyectosBean;
    @EJB
    private ProcesoFacade procesoFacade;
    @EJB
    private LicenciaAmbientalFacade licenciaAmbientalFacade;
    @EJB
    private ProyectoLicenciaAmbientalFacade proyectoLicenciaAmbientalFacade;
    
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
	@EJB
	private CrudServiceBean crudServiceBean;
	@EJB
	private UsuarioFacade usuarioFacade;
	@EJB
	private DocumentosFacade documentosFacade;
	
	/**
	 * METODO INICIAL PARA LOS VALORES DE LA FIRMA ELECTRONICA
	 */
	@PostConstruct
	public void init() {
		ambienteProduccion = Boolean.parseBoolean(tipoAmbiente);
		
		token = true;

		if (!ambienteProduccion) {
			verificaToken();
			documentoDescargado = true;
		}
		urlAlfresco = "";
	}

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
	 * PARA FIRMA CON TOKEN
	 * 
	 * @param event
	 */
	@SuppressWarnings("static-access")
	public String firmarOficio() {
		try {
			TipoDocumento auxTipoDocumento = new TipoDocumento();
			TipoDocumentoSistema tipoDocumento = TipoDocumentoSistema.TIPO_CERTIFICADO_VIABILIDAD;

			if (adjuntarPronunciamientoCertificadoViabilidadBean.getTipoPronunciamiento().equals("Biodiversidad")) {
				tipoDocumento = TipoDocumentoSistema.TIPO_CERTIFICADO_VIABILIDAD;
				auxTipoDocumento = obtenerTipoDocumento(
						TipoDocumentoSistema.TIPO_CERTIFICADO_VIABILIDAD.getIdTipoDocumento());
				documentoSubido = documentosFacade.documentoXTablaIdXIdDocUnico(
						adjuntarPronunciamientoCertificadoViabilidadBean.getProyecto().getId(),
						ProyectoLicenciamientoAmbiental.class.getSimpleName(), tipoDocumento);

			} else if (adjuntarPronunciamientoCertificadoViabilidadBean.getTipoPronunciamiento().equals("Forestal")) {
				tipoDocumento = TipoDocumentoSistema.TIPO_CERTIFICADO_VIABILIDAD;
				auxTipoDocumento = obtenerTipoDocumento(
						TipoDocumentoSistema.TIPO_CERTIFICADO_VIABILIDAD.getIdTipoDocumento());
				documentoSubido = documentosFacade.documentoXTablaIdXIdDocUnico(
						adjuntarPronunciamientoCertificadoViabilidadBean.getProyecto().getId(),
						ProyectoLicenciamientoAmbiental.class.getSimpleName(), tipoDocumento);
			}
			
			if (documentoSubido != null && documentoSubido.getIdAlfresco() != null) {
				String documento = requisitosPreviosFacade.direccionDescarga(documentoSubido);
				DigitalSign firmaE = new DigitalSign();
				documentoDescargado = true;
				return firmaE.sign(documento, loginBean.getUsuario().getNombre());

			} else
				return "";
		} catch (Throwable e) {
			JsfUtil.addMessageError("Error al realizar la operación.");
			return "";
		}
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
	 * Metodo de carga de archivo
	 */

	public void cargarInformacion(FileUploadEvent event) {
		try {
			adjuntarPronunciamientoCertificadoViabilidadBean.setAdjuntoPronunciamiento(JsfUtil.upload(event));
			requisitosPreviosFacade.adjuntarCertificadoViabilidad(adjuntarPronunciamientoCertificadoViabilidadBean.getAdjuntoPronunciamiento(),	proyectosBean.getProyecto(), bandejaTareasBean.getTarea().getProcessInstanceId(),bandejaTareasBean.getTarea().getTaskId());
			informacionSubida = true;
			
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CmisAlfrescoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			TipoDocumentoSistema tipoDocumento = TipoDocumentoSistema.TIPO_CERTIFICADO_VIABILIDAD;

			tipoDocumento = TipoDocumentoSistema.TIPO_CERTIFICADO_VIABILIDAD;
			auxTipoDocumento = obtenerTipoDocumento(
					TipoDocumentoSistema.TIPO_CERTIFICADO_VIABILIDAD.getIdTipoDocumento());

			tipoDocumento = TipoDocumentoSistema.TIPO_CERTIFICADO_VIABILIDAD;
			auxTipoDocumento = obtenerTipoDocumento(
					TipoDocumentoSistema.TIPO_CERTIFICADO_VIABILIDAD.getIdTipoDocumento());
			
			byte[] contenidoDocumento = event.getFile().getContents();
			documentoInformacionManual = new Documento();
			documentoInformacionManual.setId(null);
			documentoInformacionManual.setContenidoDocumento(contenidoDocumento);
			documentoInformacionManual.setNombre(event.getFile().getFileName());
			documentoInformacionManual.setExtesion(".pdf");
			documentoInformacionManual.setMime("application/pdf");
			documentoInformacionManual.setNombre("Certificado_Viabilidad.pdf");

			documentoInformacionManual.setIdTable(adjuntarPronunciamientoCertificadoViabilidadBean.getProyecto().getId());
			documentoInformacionManual.setNombreTabla(ProyectoLicenciamientoAmbiental.class.getSimpleName());
			documentoInformacionManual.setTipoDocumento(auxTipoDocumento);

			informacionSubida = true;
			nombreDocumentoFirmado = event.getFile().getFileName();
		} else {
			JsfUtil.addMessageError("No ha descargado el documento para la firmas");
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
			byte[] documentoContent;
			Documento auxdoc = new Documento();
			Boolean generar = false;

			if (adjuntarPronunciamientoCertificadoViabilidadBean.getTipoPronunciamiento().equals("Biodiversidad")) {

				auxdoc = documentosFacade.documentoXTablaIdXIdDocUnicos(
						adjuntarPronunciamientoCertificadoViabilidadBean.getProyecto().getId(),
						ProyectoLicenciamientoAmbiental.class.getSimpleName(),
						TipoDocumentoSistema.TIPO_CERTIFICADO_VIABILIDAD);

			} else if (adjuntarPronunciamientoCertificadoViabilidadBean.getTipoPronunciamiento().equals("Forestal")) {

				auxdoc = documentosFacade.documentoXTablaIdXIdDocUnicos(
						adjuntarPronunciamientoCertificadoViabilidadBean.getProyecto().getId(),
						ProyectoLicenciamientoAmbiental.class.getSimpleName(),
						TipoDocumentoSistema.TIPO_CERTIFICADO_VIABILIDAD);
			}

			documentoContent = documentosFacade.descargar(auxdoc.getIdAlfresco());
			if (documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(auxdoc.getNombre());
				documentoDescargado = true;
			} else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);

		}

		catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}

//    public String completarTarea() {
//        try {
//            if (adjuntarPronunciamientoCertificadoViabilidadBean.getAdjuntoPronunciamiento() == null) {
//                JsfUtil.addMessageError("Debe adjuntar el pronunciamiento para el Certificado de Viabilidad.");
//                return "";
//            }
//
//            //Se adjunta el certificado de viabilidad
//            requisitosPreviosFacade.adjuntarCertificadoViabilidad(adjuntarPronunciamientoCertificadoViabilidadBean.getAdjuntoPronunciamiento(),
//                    proyectosBean.getProyecto(), bandejaTareasBean.getTarea().getProcessInstanceId(), bandejaTareasBean.getTarea().getTaskId());
//
//            //Se incluye el pronunciamiento
//            Map<String, Object> params = new ConcurrentHashMap<String, Object>();
//
//            if (adjuntarPronunciamientoCertificadoViabilidadBean.getTipoPronunciamiento().equals("Biodiversidad")) {
//                params.put("pronunciamientoBiodiversidadFavorable", adjuntarPronunciamientoCertificadoViabilidadBean.getEsPronunciamientoFavorable());
//            } else if (adjuntarPronunciamientoCertificadoViabilidadBean.getTipoPronunciamiento().equals("Forestal")) {
//                params.put("pronunciamientoForestalFavorable", adjuntarPronunciamientoCertificadoViabilidadBean.getEsPronunciamientoFavorable());
//            }
//
//            procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getTarea()
//                    .getProcessInstanceId(), params);
//
//            taskBeanFacade.approveTask(loginBean.getNombreUsuario(),
//                    bandejaTareasBean.getTarea().getTaskId(),
//                    bandejaTareasBean.getTarea().getProcessInstanceId(), new ConcurrentHashMap<String, Object>(), loginBean.getPassword(),
//                    Constantes.getUrlBusinessCentral(), Constantes.getRemoteApiTimeout(), Constantes.getNotificationService());
//            
//            procesoFacade.envioSeguimientoLicenciaAmbiental(loginBean.getUsuario(), bandejaTareasBean.getTarea().getProcessInstanceId());
//            return JsfUtil.actionNavigateTo("/bandeja/bandejaTareas.jsf");
//        } catch (Exception e) {
//            LOGGER.error(e);
//            JsfUtil.addMessageError("Error al realizar la operación. Intente más tarde.");
//            return "";
//        }
//    }
	
	public String completarTarea() {
		try {
			
			// Verifica si se ha adjuntado el pronunciamiento
			if (adjuntarPronunciamientoCertificadoViabilidadBean.getAdjuntoPronunciamiento() == null) {
				JsfUtil.addMessageError("Debe adjuntar el pronunciamiento para el Certificado de Viabilidad.");
				return "";
			}

			if (!token) {
				// Adjunta el certificado de viabilidad
				requisitosPreviosFacade.adjuntarCertificadoViabilidad(
						adjuntarPronunciamientoCertificadoViabilidadBean.getAdjuntoPronunciamiento(),
						proyectosBean.getProyecto(), bandejaTareasBean.getTarea().getProcessInstanceId(),
						bandejaTareasBean.getTarea().getTaskId());

				// Incluye el pronunciamiento
				Map<String, Object> params = new ConcurrentHashMap<String, Object>();

				if (adjuntarPronunciamientoCertificadoViabilidadBean.getTipoPronunciamiento().equals("Biodiversidad")) {
					params.put("pronunciamientoBiodiversidadFavorable",
							adjuntarPronunciamientoCertificadoViabilidadBean.getEsPronunciamientoFavorable());
				} else if (adjuntarPronunciamientoCertificadoViabilidadBean.getTipoPronunciamiento()
						.equals("Forestal")) {
					params.put("pronunciamientoForestalFavorable",
							adjuntarPronunciamientoCertificadoViabilidadBean.getEsPronunciamientoFavorable());
				}

				// Modifica las variables del proceso
				procesoFacade.modificarVariablesProceso(loginBean.getUsuario(),
						bandejaTareasBean.getTarea().getProcessInstanceId(), params);
			}

			// Aprueba la tarea
			taskBeanFacade.approveTask(loginBean.getNombreUsuario(), bandejaTareasBean.getTarea().getTaskId(),
					bandejaTareasBean.getTarea().getProcessInstanceId(), new ConcurrentHashMap<String, Object>(),
					loginBean.getPassword(), Constantes.getUrlBusinessCentral(), Constantes.getRemoteApiTimeout(),
					Constantes.getNotificationService());

			// Envía el seguimiento de la licencia ambiental
			procesoFacade.envioSeguimientoLicenciaAmbiental(loginBean.getUsuario(),
					bandejaTareasBean.getTarea().getProcessInstanceId());

			return JsfUtil.actionNavigateTo("/bandeja/bandejaTareas.jsf");

		} catch (Exception e) {
			LOGGER.error(e);
			JsfUtil.addMessageError("Error al realizar la operación. Intente más tarde.");
			return "";
		}
	}
}
