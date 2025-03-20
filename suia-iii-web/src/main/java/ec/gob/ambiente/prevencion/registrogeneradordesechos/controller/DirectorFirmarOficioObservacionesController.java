package ec.gob.ambiente.prevencion.registrogeneradordesechos.controller;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.prevencion.registrogeneradordesechos.bean.DocumentoRGBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.GeneradorDesechosPeligrosos;
import ec.gob.ambiente.suia.domain.OficioObservacionRegistroGeneradorDesechos;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.registrogeneradordesechos.facade.InformeOficioRGFacade;
import ec.gob.ambiente.suia.prevencion.registrogeneradordesechos.facade.RegistroGeneradorDesechosFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.DigitalSign;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class DirectorFirmarOficioObservacionesController implements Serializable {

	private static final long serialVersionUID = 7738159810770443729L;

	private final Logger LOG = Logger.getLogger(DirectorFirmarOficioObservacionesController.class);

	@EJB
	protected ProcesoFacade procesoFacade;

	@EJB
	private DocumentosFacade documentosFacade;

	@Getter
	private GeneradorDesechosPeligrosos generador;

	@Getter
	private OficioObservacionRegistroGeneradorDesechos oficio;

	@EJB
	private InformeOficioRGFacade informeOficioRGFacade;

	@EJB
	private RegistroGeneradorDesechosFacade registroGeneradorDesechosFacade;

	private int contadorBandejaTecnico;

	@Getter
	@Setter
	private boolean token;
	@EJB
    private UsuarioFacade usuarioFacade;

	@Getter
	private Documento archivoObservaciones;
	
//	private int docObsVersion;

	private boolean documentoObservacionesAdjuntado;

	@PostConstruct
	public void init() {

		try {
			if (JsfUtil.getLoggedUser().getToken() != null && JsfUtil.getLoggedUser().getToken())
				token = true;

			Integer idGenerador = Integer.parseInt(JsfUtil.getCurrentTask()
					.getVariable(GeneradorDesechosPeligrosos.VARIABLE_ID_GENERADOR).toString());

			Object contadorBandejaTecnicoValue = JsfUtil.getCurrentTask().getVariable(
					GeneradorDesechosPeligrosos.VARIABLE_CANTIDAD_OBSERVACIONES);

			if (contadorBandejaTecnicoValue == null || contadorBandejaTecnicoValue.toString().isEmpty()) {
				contadorBandejaTecnico = 0;
			} else {
				try {
					contadorBandejaTecnico = Integer.parseInt(contadorBandejaTecnicoValue.toString());
				} catch (Exception e) {
					LOG.error("Error recuperado cantidad de observaciones en registro de generador", e);
				}
			}

			generador = registroGeneradorDesechosFacade.cargarGeneradorParaDocumentoPorId(idGenerador);
			oficio = informeOficioRGFacade.getOficioObservacion(generador, JsfUtil.getCurrentTask()
					.getProcessInstanceId(), contadorBandejaTecnico);
			
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		    String fechaModificacion = (oficio.getFechaModificacion() != null) ? sdf.format(oficio.getFechaModificacion()) : null;
		    
		    String fechaActual = sdf.format(new Date());
			
		    boolean nuevoDocumento = false;
		    if(fechaModificacion == null)
		    	nuevoDocumento = true;
		    else if(!fechaActual.equals(fechaModificacion)){
				nuevoDocumento = true;
			}
			
			if(nuevoDocumento){
				JsfUtil.getBean(DocumentoRGBean.class).inicializarInformeTecnicoAsociado();
				JsfUtil.getBean(DocumentoRGBean.class).visualizarOficioObservaciones(true, true);
				Documento documento = JsfUtil.getBean(DocumentoRGBean.class).guardarOficioObservacionesDocumento();
				oficio.setDocumento(documento);
				
				informeOficioRGFacade.guardarOficioObservacion(oficio);
			}		
			

			archivoObservaciones = oficio.getDocumento();
//			String id = archivoObservaciones.getIdAlfresco();
			//this.docObsVersion = Integer.parseInt(id.substring(id.length()-1));
			//if (!token) {
				archivoObservaciones.setContenidoDocumento(documentosFacade.descargar(archivoObservaciones
						.getIdAlfresco()));
			//}
			
			documentoObservacionesAdjuntado = false;
		} catch (Exception exception) {
			LOG.error(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION, exception);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}

	}

	public void validarTareaBpm() {
		JsfUtil.validarPaginasUrlTareasBpm(JsfUtil.getCurrentTask(),
				"/prevencion/registrogeneradordesechos/directorFirmarOficioObservaciones.jsf");
	}

	public String aceptar() throws ServiceException, CmisAlfrescoException {
		try {
			String idAlfresco = archivoObservaciones.getIdAlfresco();
			//idAlfresco = archivoObservaciones.getIdAlfresco().substring(0,idAlfresco.length()-4);
			archivoObservaciones.setIdAlfresco(idAlfresco);

			if (token && !documentosFacade.verificarFirmaVersion(idAlfresco)) {
				JsfUtil.addMessageError("Oficio de observaciones" + JsfUtil.MESSAGE_ERROR_DOCUMENTO_NO_FIRMADO);
				return null;
			}
			else{
			if (!token && !documentoObservacionesAdjuntado) {
				JsfUtil.addMessageError("Debe adjuntar el oficio de observaciones firmado.");
				return null;
			}
			}

			this.documentosFacade.actualizarDocumento(archivoObservaciones);

			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), JsfUtil.getCurrentTask().getTaskId(),
					JsfUtil.getCurrentProcessInstanceId(), null);
			procesoFacade.envioSeguimientoRGD(JsfUtil.getLoggedUser(), JsfUtil.getCurrentProcessInstanceId());
		} catch (JbpmException exception) {
			LOG.error(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION, exception);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
		JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
		return JsfUtil.actionNavigateToBandeja();
	}

	public String firmarDocumento() {
		try {
			String documentOffice = documentosFacade.direccionDescarga(oficio.getDocumento());
			System.out.println("****************************************************** " + documentOffice);
			return DigitalSign.sign(documentOffice, JsfUtil.getBean(LoginBean.class).getUsuario().getNombre());
		} catch (Exception exception) {
			LOG.error("Ocurrió un error durante la firma del oficio por el director", exception);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
			return "";
		}
	}

	
	public boolean verificaToken (){
		token = false;
		if (JsfUtil.getLoggedUser().getToken() != null && JsfUtil.getLoggedUser().getToken())
			token = true;
		return token;
	}

	
	public void guardarToken(){
		Usuario usuario= JsfUtil.getLoggedUser();
		usuario.setToken(token);
		try {
			usuarioFacade.guardar(usuario);
			verificaToken ();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void uploadListenerObservaciones(FileUploadEvent event) {
		byte[] contenidoDocumento = event.getFile().getContents();
		Documento documento = new Documento();
		documento.setNombre("OficioObservacion-" + oficio.getNumero() + ".pdf");
		documento.setContenidoDocumento(contenidoDocumento);
		documento.setNombreTabla(oficio.getClass().getSimpleName());
		documento.setIdTable(oficio.getId());
		documento.setMime("application/pdf");
		documento.setExtesion(".pdf");
		try {
			if (generador.getProyecto() != null) {
				documento = documentosFacade.guardarDocumentoAlfresco(generador.getProyecto().getCodigo(),
						Constantes.NOMBRE_PROCESO_GENERADOR_DESECHOS, JsfUtil.getCurrentProcessInstanceId(), documento,
						oficio.getTipoDocumento().getTipoDocumentoSistema(), null);
			} else {
				documento = documentosFacade.guardarDocumentoAlfrescoSinProyecto(generador.getSolicitud(),
						Constantes.NOMBRE_PROCESO_GENERADOR_DESECHOS, JsfUtil.getCurrentProcessInstanceId(), documento,
						oficio.getTipoDocumento().getTipoDocumentoSistema(), null);
			}
			oficio.setDocumento(documento);
			informeOficioRGFacade.guardarOficioObservacion(oficio, generador, JsfUtil.getLoggedUser());
			this.archivoObservaciones = documento;
			this.archivoObservaciones.setContenidoDocumento(contenidoDocumento);
			documentoObservacionesAdjuntado = true;
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
}
