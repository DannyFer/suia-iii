package ec.gob.ambiente.control.inspeccionescontrolambiental.controller;

import java.io.ByteArrayInputStream;
import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.control.inspeccionescontrolambiental.bean.DocumentoICABean;
import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.control.inspeccionescontrolambiental.facade.InformeOficioICAFacade;
import ec.gob.ambiente.suia.control.inspeccionescontrolambiental.facade.InspeccionControlAmbientalFacade;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.OficioObservacionInspeccionesControlAmbiental;
import ec.gob.ambiente.suia.domain.OficioPronunciamientoInspeccionesControlAmbiental;
import ec.gob.ambiente.suia.domain.SolicitudInspeccionControlAmbiental;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.DigitalSign;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;

@ManagedBean
@ViewScoped
public class DirectorFirmarOficioController implements Serializable {

	private static final long serialVersionUID = -468835706418108856L;

	private final Logger LOG = Logger.getLogger(DirectorFirmarOficioController.class);

	@EJB
	protected ProcesoFacade procesoFacade;

	@EJB
	private DocumentosFacade documentosFacade;

	@Getter
	private SolicitudInspeccionControlAmbiental solicitud;

	@Getter
	private OficioPronunciamientoInspeccionesControlAmbiental oficioPronunciamiento;

	@Getter
	private OficioObservacionInspeccionesControlAmbiental oficioObservaciones;

	@EJB
	private InformeOficioICAFacade informeOficioICAFacade;

	@EJB
	private InspeccionControlAmbientalFacade inspeccionControlAmbientalFacade;

	private int contadorBandejaTecnico;

	@Getter
	private boolean token;

	private Documento archivoOficio;

	private boolean documentoOficioAdjuntado;

	@Getter
	private boolean tipoOficioPronunciamiento;

	@PostConstruct
	public void init() {

		try {
			if (JsfUtil.getLoggedUser().getToken() != null && JsfUtil.getLoggedUser().getToken())
				token = true;

			Integer idSolicitud = Integer.parseInt(JsfUtil.getCurrentTask()
					.getVariable(SolicitudInspeccionControlAmbiental.VARIABLE_ID_SOLICITUD).toString());

			Object contadorBandejaTecnicoValue = JsfUtil.getCurrentTask()
					.getVariable(SolicitudInspeccionControlAmbiental.VARIABLE_CANTIDAD_OBSERVACIONES);

			if (contadorBandejaTecnicoValue == null || contadorBandejaTecnicoValue.toString().isEmpty()) {
				contadorBandejaTecnico = 0;
			} else {
				try {
					contadorBandejaTecnico = Integer.parseInt(contadorBandejaTecnicoValue.toString());
				} catch (Exception e) {
					LOG.error("Error recuperado cantidad de observaciones en inspecciones de control ambiental", e);
				}
			}

			tipoOficioPronunciamiento = JsfUtil.getCurrentTask()
					.getVariable(SolicitudInspeccionControlAmbiental.VARIABLE_TIPO_OFICIO).toString()
					.equals("Pronunciamiento");

			solicitud = inspeccionControlAmbientalFacade.cargarSolicitudParaDocumentoPorId(idSolicitud);

			if (tipoOficioPronunciamiento) {
				oficioPronunciamiento = informeOficioICAFacade.getOficioPronunciamiento(solicitud,
						JsfUtil.getCurrentTask().getProcessInstanceId(), contadorBandejaTecnico);

			} else {
				oficioObservaciones = informeOficioICAFacade.getOficioObservacion(solicitud,
						JsfUtil.getCurrentTask().getProcessInstanceId(), contadorBandejaTecnico);
			}

			JsfUtil.getBean(DocumentoICABean.class).inicializarInformeTecnicoAsociado();

			if (tipoOficioPronunciamiento) {
				JsfUtil.getBean(DocumentoICABean.class).visualizarOficio(true, !token);
				JsfUtil.getBean(DocumentoICABean.class).guardarOficio();
				JsfUtil.getBean(DocumentoICABean.class).getOficio()
						.setDocumento(JsfUtil.getBean(DocumentoICABean.class).guardarOficioDocumento());
				JsfUtil.getBean(DocumentoICABean.class).guardarOficio();
			} else {
				JsfUtil.getBean(DocumentoICABean.class).visualizarOficioObservaciones(true, !token);
				JsfUtil.getBean(DocumentoICABean.class).guardarOficioObservaciones();
				JsfUtil.getBean(DocumentoICABean.class).getOficioObservaciones()
						.setDocumento(JsfUtil.getBean(DocumentoICABean.class).guardarOficioObservacionesDocumento());
				JsfUtil.getBean(DocumentoICABean.class).guardarOficioObservaciones();
			}

			if (!token) {
				getArchivoOficio()
						.setContenidoDocumento(documentosFacade.descargar(getArchivoOficio().getIdAlfresco()));
			}

			documentoOficioAdjuntado = false;
		} catch (Exception exception) {
			LOG.error(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION, exception);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}

	}

	public void validarTareaBpm() {
		JsfUtil.validarPaginasUrlTareasBpm(JsfUtil.getCurrentTask(),
				"/control/inspeccionescontrolambiental/directorFirmarOficio.jsf");
	}

	public String aceptar() throws ServiceException, CmisAlfrescoException {
		try {
			/*if (token && !documentosFacade.verificarFirma(archivoOficio)) {
				JsfUtil.addMessageError("Oficio de " + getTipoOficio() + JsfUtil.MESSAGE_ERROR_DOCUMENTO_NO_FIRMADO);
				return null;
			}*/
			if (!token && !documentoOficioAdjuntado) {
				JsfUtil.addMessageError("Debe adjuntar el oficio de " + getTipoOficio() + " firmado.");
				return null;
			}

			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), JsfUtil.getCurrentTask().getTaskId(),
					JsfUtil.getCurrentProcessInstanceId(), null);
		} catch (JbpmException exception) {
			LOG.error(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION, exception);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
		JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
		return JsfUtil.actionNavigateToBandeja();
	}

	public String firmarDocumento() {
		try {
			String documentOffice = documentosFacade.direccionDescarga(getArchivoOficio());
			System.out.println("****************************************************** " + documentOffice);
			return DigitalSign.sign(documentOffice, JsfUtil.getBean(LoginBean.class).getUsuario().getNombre());
		} catch (Exception exception) {
			LOG.error("Ocurrió un error durante la firma del oficio por el director", exception);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
			return "";
		}
	}

	public void uploadListener(FileUploadEvent event) {
		byte[] contenidoDocumento = event.getFile().getContents();
		Documento documento = new Documento();
		documento.setNombre(getTipoOficioNombreDocumento());
		documento.setContenidoDocumento(contenidoDocumento);
		documento.setNombreTabla(getTipoOficioClassName());
		documento.setIdTable(getTipoOficioClassId());
		documento.setMime("application/pdf");
		documento.setExtesion(".pdf");
		try {
			documento = documentosFacade.guardarDocumentoAlfresco(solicitud.getProyecto().getCodigo(),
					Constantes.NOMBRE_PROCESO_GENERADOR_DESECHOS, JsfUtil.getCurrentProcessInstanceId(), documento,
					getTipoOficioTipoDocumentoSistema(), null);

			if (tipoOficioPronunciamiento) {
				oficioPronunciamiento.setDocumento(documento);
				informeOficioICAFacade.guardarOficioPronunciamiento(oficioPronunciamiento, solicitud,
						JsfUtil.getLoggedUser());
			} else {
				oficioObservaciones.setDocumento(documento);
				informeOficioICAFacade.guardarOficioObservacion(oficioObservaciones, solicitud,
						JsfUtil.getLoggedUser());
			}

			this.archivoOficio = documento;
			this.archivoOficio.setContenidoDocumento(contenidoDocumento);
			documentoOficioAdjuntado = true;
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}

	public StreamedContent getStream(Documento documento) throws Exception {
		DefaultStreamedContent content = new DefaultStreamedContent();
		if (documento.getContenidoDocumento() != null) {
			content = new DefaultStreamedContent(new ByteArrayInputStream(documento.getContenidoDocumento()),
					"application/octet-stream");
			content.setName(documento.getNombre());
		}
		return content;
	}

	public Documento getArchivoOficio() {
		if (archivoOficio == null)
			archivoOficio = tipoOficioPronunciamiento ? oficioPronunciamiento.getDocumento()
					: oficioObservaciones.getDocumento();
		return archivoOficio;
	}

	public String getTipoOficio() {
		return tipoOficioPronunciamiento ? "pronunciamiento" : "observaciones";
	}

	private String getTipoOficioNombreDocumento() {
		return tipoOficioPronunciamiento ? "OficioPronunciamiento-" + oficioPronunciamiento.getNumero() + ".pdf"
				: "OficioObservacion-" + oficioObservaciones.getNumero() + ".pdf";
	}

	private String getTipoOficioClassName() {
		return tipoOficioPronunciamiento ? oficioPronunciamiento.getClass().getSimpleName()
				: oficioObservaciones.getClass().getSimpleName();
	}

	private Integer getTipoOficioClassId() {
		return tipoOficioPronunciamiento ? oficioPronunciamiento.getId() : oficioObservaciones.getId();
	}

	private TipoDocumentoSistema getTipoOficioTipoDocumentoSistema() {
		return tipoOficioPronunciamiento ? oficioPronunciamiento.getTipoDocumento().getTipoDocumentoSistema()
				: oficioObservaciones.getTipoDocumento().getTipoDocumentoSistema();
	}
}
