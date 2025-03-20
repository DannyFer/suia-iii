package ec.gob.ambiente.rcoa.registroAmbiental.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.BaseColor;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.rcoa.dto.EntityOficioPronunciamientoRegistroAPPC;
import ec.gob.ambiente.rcoa.facade.DocumentosRegistroAmbientalFacade;
import ec.gob.ambiente.rcoa.facade.RegistroAmbientalCoaFacade;
import ec.gob.ambiente.rcoa.registroAmbientalPPC.facade.OficioPronunciamientoRegistroAPPCFacade;
import ec.gob.ambiente.rcoa.model.DocumentoRegistroAmbiental;
import ec.gob.ambiente.rcoa.model.OficioPronunciamientoPPC;
import ec.gob.ambiente.rcoa.model.RegistroAmbientalRcoa;
import ec.gob.ambiente.rcoa.registroAmbiental.bean.DatosFichaRegistroAmbientalBean;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.bean.DatosOperadorRgdBean;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.PlantillaReporteFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.DigitalSign;
import ec.gob.ambiente.suia.utils.Email;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilGenerarPdf;
import lombok.Getter;
import lombok.Setter;

@ManagedBean
@ViewScoped
public class FirmarOficioAprobacionPPCController {
	
	@EJB
	private ProcesoFacade procesoFacade;
	@EJB
	private DocumentosRegistroAmbientalFacade documentosFacade;
	@EJB
	private RegistroAmbientalCoaFacade registroAmbientalCoaFacade;
	@EJB
	private PlantillaReporteFacade plantillaReporteFacade;
	@EJB
	private SecuenciasFacade secuenciasFacade;
	@EJB
	private UsuarioFacade usuarioFacade;
	@EJB
	private OficioPronunciamientoRegistroAPPCFacade OficioPronunciamientoRegistroAPPCFacade;
	@EJB
	private MensajeNotificacionFacade mensajeNotificacionFacade;
	
    @Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;

    @Getter
    @Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;

    @Getter
    @Setter
	@ManagedProperty(value = "#{datosFichaRegistroAmbientalBean}")
    private DatosFichaRegistroAmbientalBean datosFichaRegistroAmbientalBean;

    @Getter
    @Setter
	@ManagedProperty(value = "#{datosOperadorRgdBean}")
    private DatosOperadorRgdBean datosOperadorRgdBean;
	
	@Getter
	@Setter
	private RegistroAmbientalRcoa registroAmbientalRcoa;
	
	@Getter
	@Setter
	private OficioPronunciamientoPPC oficioPronunciamiento;
	
	@Getter
	@Setter
	private DocumentoRegistroAmbiental documentoManual, documentoOficioAprobacionPPC;

	@Getter
	@Setter
	private Boolean documentoDescargado = false;
	
	@Getter
	@Setter
	private boolean token, documentoSubido, firmaSoloToken;
	
	@Getter
	@Setter
	private String urlReporte, codigoProyecto;

	private PlantillaReporte plantillaReporte;
	private Usuario usuarioAutoridad;
	private String nombreArchivo = "oficio_pronunciamiento_"+codigoProyecto+".pdf";
	private byte[] archivo=null;
	private Area areaResponsable;
	private Integer tipoOficio=1; 
	
	@PostConstruct
	private void init(){
		try {
			firmaSoloToken = Constantes.getPropertyAsBoolean("ambiente.produccion");
	        documentoOficioAprobacionPPC = new DocumentoRegistroAmbiental();
			registroAmbientalRcoa = datosFichaRegistroAmbientalBean.getRegistroAmbientalRcoa();
			areaResponsable = registroAmbientalRcoa.getProyectoCoa().getAreaResponsable();
			datosOperadorRgdBean.buscarDatosOperador(registroAmbientalRcoa.getProyectoCoa().getUsuario());
			codigoProyecto = registroAmbientalRcoa.getProyectoCoa().getCodigoUnicoAmbiental();
			oficioPronunciamiento = OficioPronunciamientoRegistroAPPCFacade.obtenerPorCodigoProyectoTarea(registroAmbientalRcoa.getId(), tipoOficio, bandejaTareasBean.getTarea().getTaskId());
			// cargo la autoridad  ambiental que firma el documento
			usuarioAutoridad = loginBean.getUsuario();
			generarInforme(true);
			verificaToken();
		} catch (Exception e) {
			e.printStackTrace();
		}    
	}
	
	public void generarInforme(Boolean marcaAgua) {
		try {
			nombreArchivo = "oficio_pronunciamiento_"+codigoProyecto+".pdf";
			plantillaReporte = plantillaReporteFacade.getPlantillaReporte(TipoDocumentoSistema.OFICIO_PRONUNCIAMIENTO_REGISTRO_AMBIENTAL_PPC);
			
			EntityOficioPronunciamientoRegistroAPPC entity = (EntityOficioPronunciamientoRegistroAPPC) cargarDatosOficio();

			File informePdfAux = UtilGenerarPdf.generarFichero(plantillaReporte.getHtmlPlantilla(),	nombreArchivo, true, entity);
			File informePdf = JsfUtil.fileMarcaAgua(informePdfAux, marcaAgua ? " - - BORRADOR - - " : " ", BaseColor.GRAY);
			Path path = Paths.get(informePdf.getAbsolutePath());
			archivo= Files.readAllBytes(path);
			String reporteHtmlfinal = nombreArchivo.replace("/", "-");
			File archivoFinal = new File(JsfUtil.devolverPathReportesHtml(reporteHtmlfinal));
			FileOutputStream file = new FileOutputStream(archivoFinal);
			file.write(archivo);
			file.close();
			urlReporte = JsfUtil.devolverContexto("/reportesHtml/"+nombreArchivo);
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}

	public StreamedContent getStream() throws Exception {
		DefaultStreamedContent content = new DefaultStreamedContent();
		if (archivo != null) {
			content = new DefaultStreamedContent(new ByteArrayInputStream(archivo), "application/octet-stream");
			content.setName(nombreArchivo);
			documentoDescargado = true;
		}
		return content;
	}
	
	private EntityOficioPronunciamientoRegistroAPPC cargarDatosOficio(){
		DateFormat formatoFechaEmision = DateFormat.getDateInstance(DateFormat.LONG, new Locale("es"));
		EntityOficioPronunciamientoRegistroAPPC entidad = new EntityOficioPronunciamientoRegistroAPPC();
		entidad.setNumeroOficio(oficioPronunciamiento.getCodigoDocumento());
		entidad.setUbicacion(usuarioAutoridad.getPersona().getUbicacionesGeografica().getUbicacionesGeografica().getNombre());
		entidad.setFechaEmision(formatoFechaEmision.format(new Date()));
		entidad.setNombreOperador(datosOperadorRgdBean.getDatosOperador().getRepresentanteLegal());
		entidad.setAsunto(oficioPronunciamiento.getAsunto());
		entidad.setAntecedentes(oficioPronunciamiento.getAntecedentes());
		entidad.setPronunciamiento(oficioPronunciamiento.getPronunciamiento());
		entidad.setNombreFirma(usuarioAutoridad.getPersona().getNombre());
		return entidad;
	}

	public void uploadListenerDocumentos(FileUploadEvent event) {
		documentoSubido = false;
		if (documentoDescargado == true) {
			documentoManual = new DocumentoRegistroAmbiental();
			byte[] contenidoDocumento = event.getFile().getContents();
			documentoManual.setContenidoDocumento(contenidoDocumento);
			documentoManual.setNombre(event.getFile().getFileName());
			documentoManual.setExtesion(".pdf");
			documentoManual.setMime("application/pdf");
			documentoManual.setNombreTabla(OficioPronunciamientoPPC.class.getSimpleName());
			documentoManual.setIdTable(oficioPronunciamiento.getId());
			
			documentoSubido = true;
			JsfUtil.addMessageInfo("Documento subido exitosamente");
		} else{
			JsfUtil.addMessageError("No ha descargado el documento para la firma");
		}
	}
	
	public void redireccionarBandeja(){
		JsfUtil.redirectToBandeja();
	}

	public String firmarDocumento(){
		try {
			//guardo el docuemnto generado
			documentoOficioAprobacionPPC = new DocumentoRegistroAmbiental();
			documentoOficioAprobacionPPC.setContenidoDocumento(archivo);
			documentoOficioAprobacionPPC.setNombre(nombreArchivo);
			documentoOficioAprobacionPPC.setExtesion(".pdf");
			documentoOficioAprobacionPPC.setMime("application/pdf");
			documentoOficioAprobacionPPC.setNombreTabla(OficioPronunciamientoPPC.class.getSimpleName());
			documentoOficioAprobacionPPC.setIdTable(oficioPronunciamiento.getId());
			// guardo el docuemnto en el alfresco
			documentoOficioAprobacionPPC = documentosFacade.ingresarDocumento(documentoOficioAprobacionPPC, oficioPronunciamiento.getId(), registroAmbientalRcoa.getProyectoCoa().getCodigoUnicoAmbiental(), TipoDocumentoSistema.OFICIO_PRONUNCIAMIENTO_REGISTRO_AMBIENTAL_PPC, documentoOficioAprobacionPPC.getNombre(), OficioPronunciamientoPPC.class.getSimpleName(), bandejaTareasBean.getProcessId());			
			String documentOffice = documentosFacade.direccionDescarga(documentoOficioAprobacionPPC);
			return DigitalSign.sign(documentOffice, loginBean.getUsuario().getNombre());
		} catch (Throwable e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Error al realizar la operación.");
			return "";
		}
	}
	
	public void completarTarea(){
		try {
			if (token) {
				String idAlfrescoInforme = documentoOficioAprobacionPPC.getAlfrescoId();
				if (!documentosFacade.verificarFirmaVersion(idAlfrescoInforme)) {
					JsfUtil.addMessageError("El oficio no está firmado electrónicamente.");
					return;
				}
			} else {
				if(documentoSubido) {
					documentoOficioAprobacionPPC = documentosFacade.ingresarDocumento(documentoManual, oficioPronunciamiento.getId(), registroAmbientalRcoa.getProyectoCoa().getCodigoUnicoAmbiental(), TipoDocumentoSistema.OFICIO_PRONUNCIAMIENTO_REGISTRO_AMBIENTAL_PPC, documentoManual.getNombre(), OficioPronunciamientoPPC.class.getSimpleName(), bandejaTareasBean.getProcessId());
				} else {
					JsfUtil.addMessageError("Debe adjuntar el oficio firmado.");
					return;
				}				
			}
			Map<String, Object> parametros = new HashMap<>();
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(),bandejaTareasBean.getTarea().getTaskId(),bandejaTareasBean.getTarea().getProcessInstanceId(), parametros);
			notificacionOperador();
			redireccionarBandeja();
		} catch (JbpmException e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}
	
	private void notificacionOperador(){
		try {
			byte[] oficioContent = documentosFacade.descargar(documentoOficioAprobacionPPC.getAlfrescoId());
			File fileArchivo = new File(System.getProperty("java.io.tmpdir") + "/" + nombreArchivo);
			FileOutputStream file = new FileOutputStream(fileArchivo);
			file.write(oficioContent);
			file.close();
			List<String> listPathArchivos= new ArrayList<String>();
			listPathArchivos.add(nombreArchivo);
			Object[] parametrosNotificacion = new Object[] {datosOperadorRgdBean.getDatosOperador().getRepresentanteLegal()};
			String mensaje = mensajeNotificacionFacade.recuperarValorMensajeNotificacion("bodyNotificacion_R_A_PPC_pronunciamientoOperador", parametrosNotificacion);
			Email.sendEmailAdjuntos(registroAmbientalRcoa.getProyectoCoa().getUsuario(), "Recibir oficio de pronunciamiento de PPC para la Consulta Ambiental", mensaje, listPathArchivos, codigoProyecto, loginBean.getUsuario());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean verificaToken() {
		if(firmaSoloToken) {
			token = true;
			return token;
		}
		
		token = false;
		if (JsfUtil.getLoggedUser().getToken() != null
				&& JsfUtil.getLoggedUser().getToken())
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
}