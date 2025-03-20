/*
 * Copyright 2015 MAGMASOFT
 * Todos los derechos reservados
 */
package ec.gob.ambiente.control.aprobacionrequisitostecnicos.controllers;

import static ec.gob.ambiente.control.aprobacionrequisitostecnicos.oficios.bean.OficioAprobacionRtBean.OBSERVACION;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

import ec.gob.ambiente.control.aprobacionrequisitostecnicos.oficios.bean.OficioAprobacionRtBean;
import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.AprobacionRequisitosTecnicosFacade;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.InformeOficioArtFacade;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.InformeTecnicoAproReqTec;
import ec.gob.ambiente.suia.domain.OficioAproReqTec;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.AprobacionRequisitosTecnicos;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.registrogeneradordesechos.facade.InformeOficioRGFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.reportes.UtilDocumento;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.DigitalSign;
import ec.gob.ambiente.suia.utils.JsfUtil;

/**
 * <b> Clase controlador para la firma del oficio. </b>
 * 
 * @author Javier Lucero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: Javier Lucero $, $Date: 04/07/2015 $]
 *          </p>
 */
@ManagedBean
@ViewScoped
public class FirmarOficioAprobacionRequisitosController {

	public static final String OFICIO_APROBACION = "Oficio_Aprobacion_Requisitos";
	public static final String TIPO_PDF = ".pdf";
	public static final String URL_FIRMA_PRONUNCIAMIENTO = "/control/aprobacionRequisitosTecnicos/documentos/firmarPronunciamiento.jsf";

	private static final Logger LOGGER = Logger.getLogger(FirmarOficioAprobacionRequisitosController.class);

	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;

	@EJB
	private AprobacionRequisitosTecnicosFacade aprobacionRequisitosTecnicosFacade;
	
	@EJB
	private ProcesoFacade procesoFacade;

	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;

	private String documentOffice = "";

	@EJB
	private DocumentosFacade documentosFacade;

	@Getter
	@Setter
	private OficioAproReqTec oficioArt;

	@EJB
	private InformeOficioArtFacade informeOficioFacade;
	
	@EJB
	private UsuarioFacade usuarioFacade;

	@Getter
	@Setter
	private AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos;

	@Getter
	@Setter
	private String informePath;

	@Getter
	@Setter
	private boolean token;

	@Getter
	@Setter
	private Documento documento;

	private int docVersion;

	@Getter
	@Setter
	private boolean subido, firmaSoloToken;
	
	@Getter
	@Setter
	private InformeTecnicoAproReqTec informeTecnicoArt;
	
	@EJB
	private InformeOficioRGFacade informeOficioRGFacade;

	@PostConstruct
	public void init() {
		try {

			firmaSoloToken = Constantes.getPropertyAsBoolean("ambiente.produccion");
			
			aprobacionRequisitosTecnicos = aprobacionRequisitosTecnicosFacade
					.recuperarCrearAprobacionRequisitosTecnicos(bandejaTareasBean.getProcessId(),
							loginBean.getUsuario());
			if (JsfUtil.getLoggedUser().getToken() != null && JsfUtil.getLoggedUser().getToken())
				token = true;

			

			oficioArt = this.informeOficioFacade.obtenerOficioAprobacionPorArt(
					TipoDocumentoSistema.TIPO_OFICIO_APROBACION_ART, this.aprobacionRequisitosTecnicos.getId(),
					aprobacionRequisitosTecnicosFacade.getCantidadVecesObservadoInformacion(loginBean.getUsuario(),
							bandejaTareasBean.getProcessId()));
			documento = oficioArt.getDocumentoOficio();
			if(documento==null){
				JsfUtil.getBean(OficioAprobacionRtBean.class).visualizarOficio(!token);
				documento = JsfUtil.getBean(OficioAprobacionRtBean.class).subirDocumentoAlfresco();
				oficioArt.setDocumentoOficio(documento);
				
				informeOficioFacade.guardarOficioAprobacionArt(oficioArt);
			}
//			String id = documento.getIdAlfresco();
			//this.docVersion = Integer.parseInt(id.substring(id.length()-1));

			setInformePath(obtenerPath(oficioArt));
			
			verificaToken();
			
			Map<String, Object> variables = procesoFacade.recuperarVariablesProceso(loginBean.getUsuario(),
					bandejaTareasBean.getTarea().getProcessInstanceId());
			if (!variables.containsKey("pronunciamento")) {
				informeTecnicoArt = informeOficioFacade.obtenerInformeTecnicoArtPorNumeroInforme(oficioArt.getNumeroInfTecnico());
				
				Map<String, Object> params = new ConcurrentHashMap<>();
				params.put("pronunciamento", informeTecnicoArt.getPronunciamiento().equals("Favorable"));
				procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getTarea().getProcessInstanceId(), params);
			}
			
		} catch (Exception e) {
			LOGGER.error("Error al inicializar: OficioAprobacionRtBean: ", e);
			JsfUtil.addMessageError("Ocurrio un error al inicializar los datos. Por favor intentelo mas tarde.");
		}
	}

	
	/**
	 * 
	 * <b> Metodo para firmar el documento. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 13/08/2015]
	 * </p>
	 * 
	 * @return String: la firma
	 */
	public String firmarDocumento() {
		try {
			documentOffice = documentosFacade.direccionDescarga(oficioArt.getDocumentoOficio());
			return DigitalSign.sign(documentOffice, loginBean.getUsuario().getNombre()); // loginBean.getUsuario()
		} catch (Throwable e) {
			LOGGER.error(e, e);
			JsfUtil.addMessageError("Error al realizar la operación.");
			return "";
		}
	}

	public boolean verificaToken (){
		if(firmaSoloToken) {
			token = true;
			return token;
		}
		
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
	
	
	/**
	 * 
	 * <b> Metodo para completar la tarea. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 13/08/2015]
	 * </p>
	 * 
	 * @return
	 */
	public String completarTarea() {
		try {
			if (documento != null) {
				if (subido) {

					informeTecnicoArt = informeOficioFacade.obtenerInformeTecnicoArtPorNumeroInforme(oficioArt.getNumeroInfTecnico());
					
					String nombreOficio=oficioArt.getNombreOficio();
					if(informeTecnicoArt.getPronunciamiento().equals("Observacion")){
						nombreOficio=oficioArt.getNombreOficio().replace("AprobacionRequisitos", "ObservacionRequisitos");	
					}
					
					oficioArt.setDocumentoOficio(informeOficioFacade.subirFileAlfresco(UtilDocumento.generateDocumentPDFFromUpload(
									documento.getContenidoDocumento(),nombreOficio), oficioArt
									.getAprobacionRequisitosTecnicos().getSolicitud(), bandejaTareasBean.getProcessId(),
							bandejaTareasBean.getTarea().getTaskId(), OficioAproReqTec.class.getSimpleName(),
							TipoDocumentoSistema.TIPO_OFICIO_APROBACION_ART));

					informeOficioFacade.guardarOficioAprobacionArt(oficioArt);
				}
			}
			String idAlfresco = documento.getIdAlfresco();

			idAlfresco = idAlfresco.substring(0, idAlfresco.length() - 1);
			idAlfresco= idAlfresco+""+(++this.docVersion);

			if (!token || documentosFacade.verificarFirmaVersion(idAlfresco)) {

				aprobacionRequisitosTecnicosFacade.enviarAprobacionRequisitosTecnicos(bandejaTareasBean.getTarea()
						.getProcessInstanceId(), loginBean.getUsuario(), bandejaTareasBean.getTarea().getTaskId());

				JsfUtil.addMessageInfo("Se realizó correctamente la operación.");
				return JsfUtil.actionNavigateTo("/bandeja/bandejaTareas.jsf");
			} else {
				JsfUtil.addMessageError("El documento no está firmado.");
				return "";
			}

		} catch (JbpmException e) {
			LOGGER.error(e);
			JsfUtil.addMessageError("Error al realizar la operación.");
		} catch (CmisAlfrescoException e) {
			LOGGER.error(e);
			JsfUtil.addMessageError("Error al subir el documento al alfresco.");
		} catch (ServiceException e) {
			LOGGER.error(e);
			JsfUtil.addMessageError("Error al realizar la operación.");
		}
		return "";
	}

	/**
	 * 
	 * <b> Metodo para validar la tarea con la pagina. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 13/08/2015]
	 * </p>
	 * 
	 */
	public void validarTareaBpm() {
		String url = URL_FIRMA_PRONUNCIAMIENTO;
		JsfUtil.validarPaginasUrlTareasBpm(bandejaTareasBean.getTarea(), url);
	}

	/**
	 * 
	 * <b> Metodo que construye el Pdf de la firma. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 13/08/2015]
	 * </p>
	 * 
	 * @param oficioAproReqTec
	 *            : oficio
	 * @return String: path del oficio
	 * @throws Exception
	 *             : Excepcion
	 */
	public String obtenerPath(OficioAproReqTec oficioAproReqTec) throws Exception {
		byte[] oficioDoc = documentosFacade.descargar(oficioAproReqTec.getDocumentoOficio().getIdAlfresco());
		File informePdf = File.createTempFile(OFICIO_APROBACION, TIPO_PDF);
		try (FileOutputStream fileOutputStream = new FileOutputStream(informePdf)) {
			fileOutputStream.write(oficioDoc);
		}
		Path path = Paths.get(informePdf.getAbsolutePath(), new String[0]);
		byte[] archivoInforme = Files.readAllBytes(path);
		File archivoFinal = new File(JsfUtil.devolverPathReportesHtml(informePdf.getName()));
		try (FileOutputStream file = new FileOutputStream(archivoFinal)) {
			file.write(archivoInforme);
		}
		return JsfUtil.devolverContexto("/reportesHtml/" + informePdf.getName());

	}

	public StreamedContent getStream() throws Exception {

		DefaultStreamedContent content = new DefaultStreamedContent();
		if (documento != null) {
			if (documento.getContenidoDocumento() == null) {
				documento.setContenidoDocumento(documentosFacade.descargar(oficioArt.getDocumentoOficio()
						.getIdAlfresco()));
			}
			content = new DefaultStreamedContent(new ByteArrayInputStream(documento.getContenidoDocumento()),
					"application/pdf");
			content.setName(documento.getNombre());

		}
		return content;

	}

	public void uploadListenerDocumentos(FileUploadEvent event) {
		byte[] contenidoDocumento = event.getFile().getContents();

		documento.setContenidoDocumento(contenidoDocumento);
		documento.setNombre(event.getFile().getFileName());
		subido = true;
	}

}
