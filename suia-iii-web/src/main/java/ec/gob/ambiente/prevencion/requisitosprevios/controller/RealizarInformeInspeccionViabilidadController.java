package ec.gob.ambiente.prevencion.requisitosprevios.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.persistence.Query;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.prevencion.requisitosprevios.bean.RealizarInformeInspeccionViabilidadBean;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.TipoDocumento;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.requisitosPrevios.RequisitosPreviosLicenciaFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.DigitalSign;
import ec.gob.ambiente.suia.utils.JsfUtil;

@RequestScoped
@ManagedBean
public class RealizarInformeInspeccionViabilidadController implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -3710725874083232386L;

    @Getter
    @Setter
    @ManagedProperty(value = "#{realizarInformeInspeccionViabilidadBean}")
    private RealizarInformeInspeccionViabilidadBean realizarInformeInspeccionViabilidadBean;

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

    @EJB
    private ProcesoFacade procesoFacade;

    @EJB
    private RequisitosPreviosLicenciaFacade requisitosPreviosFacade;
    
    @EJB
	private UsuarioFacade usuarioFacade;

    private static final Logger LOGGER = Logger.getLogger(RealizarInformeInspeccionViabilidadController.class);
    
    @Getter
	@Setter
	private boolean token, documentoDescargado, informacionSubida, mapaDescargado, ciDescargado, acepta,
			esRegistroLicencia, ambienteProduccion, esEnte;
	@Getter
	@Setter
	private boolean habilitar, firmaSoloToken;
	@Getter
	@Setter
	private boolean mostrarBotonFirmar = false;


	@Getter
	@Setter
	public static String tipoAmbiente = Constantes.getPropertyAsString("ambiente.produccion");

	@Getter
	@Setter
	private Usuario usuario = JsfUtil.getLoggedUser();

	@Getter
	@Setter
	private String nombreDocumentoFirmado, urlAlfresco;

	@Getter
	@Setter
	private Documento documentoInformacion, documentoInformacionManual, documentoMapa, documentoCertificado,
			documentoSubido;

	@EJB
	private DocumentosFacade documentosFacade;

	@EJB
	private CrudServiceBean crudServiceBean;
	
	/**
	 * METODO INICIAL PARA LOS VALORES DE LA FIRMA ELECTRONICA 
	 */
	@PostConstruct
	public void init() {

		ambienteProduccion = Boolean.parseBoolean(tipoAmbiente);
		token = true;

		if (!ambienteProduccion) {
			verificaToken();
		} else {
			token = true;
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
			e.printStackTrace();
		}
	}

	/**
	 * PARA FIRMA CON TOKEN
	 * 
	 * @param event
	 */

	public void abrirFirma() {
		obtenerDocumento();
		RequestContext.getCurrentInstance().execute("PF('signDialog').show();");
	}

	public void obtenerDocumento() {
		try {

			TipoDocumento auxTipoDocumento = new TipoDocumento();
			TipoDocumentoSistema tipoDocumento = TipoDocumentoSistema.INFORME_INSPECCION_VIABILIDAD_FORESTAL;

			if (realizarInformeInspeccionViabilidadBean.getTipoPronunciamiento().equals("Biodiversidad")) {
				tipoDocumento = TipoDocumentoSistema.INFORME_INSPECCION_VIABILIDAD_BIODIVERSIDAD;
				auxTipoDocumento = obtenerTipoDocumento(
						TipoDocumentoSistema.INFORME_INSPECCION_VIABILIDAD_BIODIVERSIDAD.getIdTipoDocumento());
				documentoSubido = documentosFacade.documentoXTablaIdXIdDocUnico(
						realizarInformeInspeccionViabilidadBean.getProyecto().getId(),
						ProyectoLicenciamientoAmbiental.class.getSimpleName(), tipoDocumento);

			} else if (realizarInformeInspeccionViabilidadBean.getTipoPronunciamiento().equals("Forestal")) {
				tipoDocumento = TipoDocumentoSistema.INFORME_INSPECCION_VIABILIDAD_FORESTAL;
				auxTipoDocumento = obtenerTipoDocumento(
						TipoDocumentoSistema.INFORME_INSPECCION_VIABILIDAD_FORESTAL.getIdTipoDocumento());
				documentoSubido = documentosFacade.documentoXTablaIdXIdDocUnico(
						realizarInformeInspeccionViabilidadBean.getProyecto().getId(),
						ProyectoLicenciamientoAmbiental.class.getSimpleName(), tipoDocumento);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("static-access")
	public String firmarOficio() {
		try {
			obtenerDocumento();

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
	 * Sube la informacion firmada En el panel
	 * 
	 * @param event
	 */
	public void uploadListenerInformacionFirmada(FileUploadEvent event) {
		if (documentoDescargado) {
			TipoDocumento auxTipoDocumento = new TipoDocumento();
			TipoDocumentoSistema tipoDocumento = TipoDocumentoSistema.INFORME_INSPECCION_VIABILIDAD_FORESTAL;

			if (realizarInformeInspeccionViabilidadBean.getTipoPronunciamiento().equals("Biodiversidad")) {
				tipoDocumento = TipoDocumentoSistema.INFORME_INSPECCION_VIABILIDAD_BIODIVERSIDAD;
				auxTipoDocumento = obtenerTipoDocumento(
						TipoDocumentoSistema.INFORME_INSPECCION_VIABILIDAD_BIODIVERSIDAD.getIdTipoDocumento());

			} else if (realizarInformeInspeccionViabilidadBean.getTipoPronunciamiento().equals("Forestal")) {
				tipoDocumento = TipoDocumentoSistema.INFORME_INSPECCION_VIABILIDAD_FORESTAL;
				auxTipoDocumento = obtenerTipoDocumento(
						TipoDocumentoSistema.INFORME_INSPECCION_VIABILIDAD_FORESTAL.getIdTipoDocumento());
			}
			byte[] contenidoDocumento = event.getFile().getContents();
			documentoInformacionManual = new Documento();
			documentoInformacionManual.setId(null);
			documentoInformacionManual.setContenidoDocumento(contenidoDocumento);
			documentoInformacionManual.setNombre(event.getFile().getFileName());
			documentoInformacionManual.setExtesion(".pdf");
			documentoInformacionManual.setMime("application/pdf");

			documentoInformacionManual.setIdTable(realizarInformeInspeccionViabilidadBean.getProyecto().getId());
			documentoInformacionManual.setNombreTabla(ProyectoLicenciamientoAmbiental.class.getSimpleName());
			documentoInformacionManual.setTipoDocumento(auxTipoDocumento);

			informacionSubida = true;
			nombreDocumentoFirmado = event.getFile().getFileName();
		} else {
			JsfUtil.addMessageError("No ha descargado el documento para la firma");
		}
	}

	/**
	 * CARGA EL ARCHIVO EN LA PAGINA
	 * 
	 * @param event
	 */
	public void cargarInformacion(FileUploadEvent event) {

		realizarInformeInspeccionViabilidadBean.setAdjuntoInformeInspeccion(JsfUtil.upload(event));
		if (realizarInformeInspeccionViabilidadBean.getAdjuntoInformeInspeccion() == null) {

			JsfUtil.addMessageError(
					"Para continuar debe adjuntar el documento con el informe de inspección de viabilidad.");

		} else {
			try {
				realizarInformeInspeccionViabilidadBean.setHabilitar(false);
				realizarInformeInspeccionViabilidadBean.setMostrarBotonFirmar(true);
				FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("form");
				TipoDocumento auxTipoDocumento = new TipoDocumento();
				TipoDocumentoSistema tipoDocumento = TipoDocumentoSistema.INFORME_INSPECCION_VIABILIDAD_FORESTAL;

				if (realizarInformeInspeccionViabilidadBean.getTipoPronunciamiento().equals("Biodiversidad")) {
					tipoDocumento = TipoDocumentoSistema.INFORME_INSPECCION_VIABILIDAD_BIODIVERSIDAD;
					auxTipoDocumento = obtenerTipoDocumento(
							TipoDocumentoSistema.INFORME_INSPECCION_VIABILIDAD_BIODIVERSIDAD.getIdTipoDocumento());

				} else if (realizarInformeInspeccionViabilidadBean.getTipoPronunciamiento().equals("Forestal")) {
					tipoDocumento = TipoDocumentoSistema.INFORME_INSPECCION_VIABILIDAD_FORESTAL;
					auxTipoDocumento = obtenerTipoDocumento(
							TipoDocumentoSistema.INFORME_INSPECCION_VIABILIDAD_FORESTAL.getIdTipoDocumento());
				}

				// Adjuntar informe inspección
				requisitosPreviosFacade.adjuntarDocumentoRequisitosPrevios(
						realizarInformeInspeccionViabilidadBean.getAdjuntoInformeInspeccion(),
						realizarInformeInspeccionViabilidadBean.getProyecto().getId(),
						realizarInformeInspeccionViabilidadBean.getProyecto().getCodigo(),
						getBandejaTareasBean().getProcessId(), getBandejaTareasBean().getTarea().getTaskId(),
						tipoDocumento);

				if (documentoSubido != null && documentoSubido.getId() != null) {
					documentoSubido.setEstado(false);
					documentosFacade.actualizarDocumento(documentoSubido);
				}

				documentoDescargado = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
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

			auxdoc = documentosFacade.documentoXTablaIdXIdDocUnicos(
					realizarInformeInspeccionViabilidadBean.getProyecto().getId(),
					ProyectoLicenciamientoAmbiental.class.getSimpleName(),
					TipoDocumentoSistema.INFORME_INSPECCION_VIABILIDAD_FORESTAL);

			if (realizarInformeInspeccionViabilidadBean.getTipoPronunciamiento().equals("Biodiversidad")) {

				auxdoc = documentosFacade.documentoXTablaIdXIdDocUnicos(
						realizarInformeInspeccionViabilidadBean.getProyecto().getId(),
						ProyectoLicenciamientoAmbiental.class.getSimpleName(),
						TipoDocumentoSistema.INFORME_INSPECCION_VIABILIDAD_BIODIVERSIDAD);

			} else if (realizarInformeInspeccionViabilidadBean.getTipoPronunciamiento().equals("Forestal")) {

				auxdoc = documentosFacade.documentoXTablaIdXIdDocUnicos(
						realizarInformeInspeccionViabilidadBean.getProyecto().getId(),
						ProyectoLicenciamientoAmbiental.class.getSimpleName(),
						TipoDocumentoSistema.INFORME_INSPECCION_VIABILIDAD_FORESTAL);
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
			JsfUtil.addMessageError(
					"Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}

	public String completarTarea() {
        if (realizarInformeInspeccionViabilidadBean.getAdjuntoInformeInspeccion() == null) {
            JsfUtil.addMessageError("Para continuar debe adjuntar el documento con el informe de inspección de viabilidad.");
            return "";
        } else {
            try {
            	if(!token){
            		if(documentoInformacionManual != null && documentoInformacionManual.getContenidoDocumento() != null){
                		TipoDocumentoSistema tipoDocumento = TipoDocumentoSistema.INFORME_INSPECCION_VIABILIDAD_FORESTAL;
                        if(realizarInformeInspeccionViabilidadBean.getTipoPronunciamiento().equals("Biodiversidad")){
                            tipoDocumento = TipoDocumentoSistema.INFORME_INSPECCION_VIABILIDAD_BIODIVERSIDAD;
                        } else if (realizarInformeInspeccionViabilidadBean.getTipoPronunciamiento().equals("Forestal")) {
                            tipoDocumento = TipoDocumentoSistema.INFORME_INSPECCION_VIABILIDAD_FORESTAL;
                        }

                        //Adjuntar informe inspección
                        requisitosPreviosFacade.adjuntarDocumentoRequisitosPrevios(documentoInformacionManual.getContenidoDocumentoFile(),
                                realizarInformeInspeccionViabilidadBean.getProyecto().getId(), realizarInformeInspeccionViabilidadBean.getProyecto().getCodigo(),
                                getBandejaTareasBean().getProcessId(), getBandejaTareasBean().getTarea().getTaskId(), tipoDocumento);
                	}else{
                		JsfUtil.addMessageError("Debe adjuntar el documento de Informe de Inspección");
                		return "";
                	}
            	}
            	
                //Aprobar tarea
				taskBeanFacade.approveTask(loginBean.getNombreUsuario(), bandejaTareasBean.getTarea().getTaskId(),
						bandejaTareasBean.getTarea().getProcessInstanceId(), new ConcurrentHashMap<String, Object>(),
						loginBean.getPassword(), Constantes.getUrlBusinessCentral(), Constantes.getRemoteApiTimeout(),
						Constantes.getNotificationService());
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
}
