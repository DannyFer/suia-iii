package ec.gob.ambiente.rcoa.registroAmbiental.controller;


import java.io.ByteArrayInputStream;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.rcoa.facade.DocumentosRegistroAmbientalFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCuaCiuuFacade;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.DocumentoRegistroAmbiental;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCuaCiuu;
import ec.gob.ambiente.rcoa.model.RegistroAmbientalRcoa;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.TipoSector;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class ParticipacionCiudadanaRcoaController {
	
	private final Logger LOG = Logger.getLogger(ParticipacionCiudadanaRcoaController.class);

	@EJB
	private DocumentosRegistroAmbientalFacade documentosFacade;
	@EJB
	private DocumentosFacade documentosPlantillasFacade;
	@EJB
	private ProyectoLicenciaCuaCiuuFacade proyectoLicenciaCuaCiuuFacade;
    
	@Getter
    @Setter
    @ManagedProperty(value = "#{marcoLegalReferencialController}")
    private MarcoLegalReferencialController marcoLegalReferencialBean;
	
	@Getter
    @Setter
    @ManagedProperty(value = "#{menuRegistroAmbientalCoaController}")
    private MenuRegistroAmbientalCoaController menuCoaBean;

	@Getter
	@Setter
	private String mensajeObligatorio, mensajeopcional;
	
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyecto;
	
	@Getter
	@Setter
	private RegistroAmbientalRcoa registroAmbiental;

	@Getter
    @Setter
	private boolean validarDatos, estrategico;
	
	@Getter
	@Setter
	private DocumentoRegistroAmbiental documentoCarteles, documentoActaApertura, documentoRegistroAsistencia, documentoInformeSistematizado;
	
	@Getter
	@Setter
	private DocumentoRegistroAmbiental documentoRegistroAsamblea, documentoAudioAsamblea, documentoInvitacion, documentoActaAsamblea;

	@Getter
	@Setter
	private String acta_apertura_cierre = "", convocatoria="", invitacion_personal="", sistematizacion_ppc="", aplicacion_ppc="", acta_asamblea_presentacion="", asistencia_asamblea="",  asistencia_observaciones="";
	
	@Getter
	@Setter
	private byte[] acta_apertura_cierre_byte,convocatoria_byte,invitacion_personal_byte,sistematizacion_ppc_byte,aplicacion_ppc_byte, acta_asamblea_presentacion_byte,asistencia_asamblea_byte, asistencia_observaciones_byte;

	private Long idProceso;
	
	@PostConstruct
	public void init(){
		try {
			proyecto = marcoLegalReferencialBean.getProyectoLicenciaCoa();
			idProceso = marcoLegalReferencialBean.getIdProceso();
			registroAmbiental = marcoLegalReferencialBean.getRegistroAmbientalRcoa();
			mensajeObligatorio = "Subir los medios de verificación de la aplicación de los mecanismos PPC obligatorios establecidos en la Normativa Ambiental Vigente.";
			mensajeopcional ="En el caso que se hayan aplicado mecanismos de PPC opcionales se deberá incluir los medios de verificación del cumplimiento de los mismos";
			validarDatos = false;
			documentoCarteles = new DocumentoRegistroAmbiental();
			documentoActaApertura = new DocumentoRegistroAmbiental();
			documentoRegistroAsistencia = new DocumentoRegistroAmbiental();
			documentoInformeSistematizado = new DocumentoRegistroAmbiental();
			estrategico = false;
			List<ProyectoLicenciaCuaCiuu>  listaactividades = proyectoLicenciaCuaCiuuFacade.actividadesPorProyecto(proyecto);
			for (ProyectoLicenciaCuaCiuu objActividad : listaactividades) {
				if(objActividad.getPrimario()){
						// verifico si el sector es mineria
						if(objActividad.getCatalogoCIUU().getTipoSector().getId().equals(TipoSector.TIPO_SECTOR_MINERIA)
								|| objActividad.getCatalogoCIUU().getTipoSector().getId().equals(TipoSector.TIPO_SECTOR_HIDROCARBUROS)
								|| objActividad.getCatalogoCIUU().getTipoSector().getId().equals(TipoSector.TIPO_SECTOR_ELECTRICO)){
							estrategico = true;
						}
				}
			}
			if(registroAmbiental != null && registroAmbiental.getId() != null){
				cargarDocumentos();
		    }else{
		    	menuCoaBean.setPageMarcoLegal(true);
	            RequestContext context = RequestContext.getCurrentInstance();
	            context.execute("PF('dlgPasoFaltante').show();");
		    }
			cargarPlantillas();
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	private void cargarPlantillas(){
		try{
			if(!estrategico){
				acta_apertura_cierre= "Formato acta de apertura y cierre del centro de información pública.docx";
				acta_apertura_cierre_byte =  documentosPlantillasFacade.descargarDocumentoPorNombre(acta_apertura_cierre);
				convocatoria = "Formato Convocatoria - Sectores No Estratégicos.docx";
				convocatoria_byte =  documentosPlantillasFacade.descargarDocumentoPorNombre(convocatoria);
				invitacion_personal = "Formato de Invitación Personal - Sectores No Estratégicos.docx";
				invitacion_personal_byte =  documentosPlantillasFacade.descargarDocumentoPorNombre(invitacion_personal);
				sistematizacion_ppc = "Formato Informe de Sistematización del PPC - Sectores No Estratégicos.docx";
				sistematizacion_ppc_byte =  documentosPlantillasFacade.descargarDocumentoPorNombre(sistematizacion_ppc);
				try{
					asistencia_observaciones = "Formato para el Registro de Asistencia y Observaciones en el Centro de Información Pública.docx";
					asistencia_observaciones_byte =  documentosPlantillasFacade.descargarDocumentoPorNombre(asistencia_observaciones);
				}catch(Exception e){
                    LOG.error("Error al recuperar archivo "+ aplicacion_ppc, e);
				}
				try{
					aplicacion_ppc = "GUIA DE APLICACION DEL PPC  PARA REGISTROS AMBIENTALES - NO ESTRATEGICOS.pdf";
					aplicacion_ppc_byte =  documentosPlantillasFacade.descargarDocumentoPorNombre(aplicacion_ppc);
				}catch(Exception e){
                      LOG.error("Error al recuperar archivo "+ aplicacion_ppc, e);
				}
			}else{
				acta_asamblea_presentacion= "Formato Acta para la Asamblea de Presenración Pública.docx";
				acta_asamblea_presentacion_byte =  documentosPlantillasFacade.descargarDocumentoPorNombre(acta_asamblea_presentacion);
				convocatoria = "Formato Convocatoria.docx";
				convocatoria_byte =  documentosPlantillasFacade.descargarDocumentoPorNombre(convocatoria);
				invitacion_personal = "Formato de Invitación Personal.docx";
				invitacion_personal_byte =  documentosPlantillasFacade.descargarDocumentoPorNombre(invitacion_personal);
				sistematizacion_ppc = "Formato Informe de Sistematización del PPC - Sectores Estratégicos.docx";
				sistematizacion_ppc_byte =  documentosPlantillasFacade.descargarDocumentoPorNombre(sistematizacion_ppc);
				asistencia_asamblea = "Formato para Regsitro de Asistencia a la Asamblea de Presentación Pública.docx";
				asistencia_asamblea_byte =  documentosPlantillasFacade.descargarDocumentoPorNombre(asistencia_asamblea);
				aplicacion_ppc = "GUÍA DE APLICACIÓN PARA PARTICIPCACIÓN CIUDADANA EN REGISTROS AMBIENTALES - SECTOR ESTRATÉGICO.docx";
				aplicacion_ppc_byte =  documentosPlantillasFacade.descargarDocumentoPorNombre(aplicacion_ppc);
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	public void cargarDocumentos() throws CmisAlfrescoException{
		documentoCarteles = documentosFacade.documentoXTablaIdXIdDoc(proyecto.getId(), ProyectoLicenciaCoa.class.getSimpleName(), TipoDocumentoSistema.RA_PC_CONVOCATORIA);
		if(documentoCarteles == null){
			documentoCarteles = new DocumentoRegistroAmbiental();
		}
		documentoActaApertura = documentosFacade.documentoXTablaIdXIdDoc(proyecto.getId(), ProyectoLicenciaCoa.class.getSimpleName(), TipoDocumentoSistema.RA_PC_ACTA_APERTURA_CIERRE);
		if(documentoActaApertura == null){
			documentoActaApertura = new DocumentoRegistroAmbiental();
		}
		documentoRegistroAsistencia = documentosFacade.documentoXTablaIdXIdDoc(proyecto.getId(), ProyectoLicenciaCoa.class.getSimpleName(), TipoDocumentoSistema.RA_PC_OBSERVACIONES);
		if(documentoRegistroAsistencia == null){
			documentoRegistroAsistencia = new DocumentoRegistroAmbiental();
		}
		documentoInformeSistematizado = documentosFacade.documentoXTablaIdXIdDoc(proyecto.getId(), ProyectoLicenciaCoa.class.getSimpleName(), TipoDocumentoSistema.RA_PC_SISTEMATIZACION_PPC);
		if(documentoInformeSistematizado == null){
			documentoInformeSistematizado = new DocumentoRegistroAmbiental();
		}
		
		documentoActaAsamblea = documentosFacade.documentoXTablaIdXIdDoc(proyecto.getId(), ProyectoLicenciaCoa.class.getSimpleName(), TipoDocumentoSistema.RA_PC_ACTA_ASAMBLEA_PRESENTACION);
		if(documentoActaAsamblea == null){
			documentoActaAsamblea = new DocumentoRegistroAmbiental();
		}
		documentoInvitacion = documentosFacade.documentoXTablaIdXIdDoc(proyecto.getId(), ProyectoLicenciaCoa.class.getSimpleName(), TipoDocumentoSistema.RA_PC_INVITACION_PERSONAL);
		if(documentoInvitacion == null){
			documentoInvitacion = new DocumentoRegistroAmbiental();
		}
		documentoRegistroAsamblea = documentosFacade.documentoXTablaIdXIdDoc(proyecto.getId(), ProyectoLicenciaCoa.class.getSimpleName(), TipoDocumentoSistema.RA_PC_ASISTENCIA_ASAMBLEA);
		if(documentoRegistroAsamblea == null){
			documentoRegistroAsamblea = new DocumentoRegistroAmbiental();
		}
		documentoAudioAsamblea = documentosFacade.documentoXTablaIdXIdDoc(proyecto.getId(), ProyectoLicenciaCoa.class.getSimpleName(), TipoDocumentoSistema.RA_PC_AUDIO_PRESENTACION);
		if(documentoAudioAsamblea == null){
			documentoAudioAsamblea = new DocumentoRegistroAmbiental();
		}
	}
	
	public void uploadFileDocumento(FileUploadEvent event ) {
		byte[] contenidoDocumento = event.getFile().getContents();
		DocumentoRegistroAmbiental objDocumento = new DocumentoRegistroAmbiental();
		objDocumento = (DocumentoRegistroAmbiental) event.getComponent().getAttributes().get("tipoDocumento"); // bar
		objDocumento.setId(null);
		objDocumento.setContenidoDocumento(contenidoDocumento);
		objDocumento.setNombre(event.getFile().getFileName());
		objDocumento.setMime("application/pdf");
		objDocumento.setExtesion(".pdf");
				
		JsfUtil.addMessageInfo("Documento subido exitosamente");
	}

	public boolean validarDatos(){
		boolean valido = true;
		if(documentoCarteles.getContenidoDocumento() == null ){
			JsfUtil.addMessageError("El documento carteles informativos es obligatorio.");
			valido = false;
		}
		if(documentoActaApertura.getContenidoDocumento() == null ){
			JsfUtil.addMessageError("El documento Acta de apertura y cierre de centro de información es obligatorio.");
			valido = false;
		}
		if(documentoRegistroAsistencia.getContenidoDocumento() == null ){
			JsfUtil.addMessageError("El documento registro de asistencia y observaciones en el centro de informaciòn pùblica es obligatorio.");
			valido = false;
		}
		if(documentoInformeSistematizado.getContenidoDocumento() == null ){
			JsfUtil.addMessageError("El documento informe de sistematizaciòn de PPC es obligatorio.");
			valido = false;
		}
		return valido;
	}

	public void guardar() throws Exception{
		if(validarDatos()){
			if (documentoCarteles.getId() == null && documentoCarteles.getContenidoDocumento() != null ){
				documentosFacade.ingresarDocumento(documentoCarteles, proyecto.getId(), proyecto.getCodigoUnicoAmbiental(), TipoDocumentoSistema.RA_PC_CONVOCATORIA, "convocatoria carteles.pdf", ProyectoLicenciaCoa.class.getSimpleName(), idProceso);
				documentoCarteles = documentosFacade.documentoXTablaIdXIdDoc(proyecto.getId(), ProyectoLicenciaCoa.class.getSimpleName(), TipoDocumentoSistema.RA_PC_CONVOCATORIA);
			}
			if (documentoActaApertura.getId() == null && documentoActaApertura.getContenidoDocumento() != null){
				documentosFacade.ingresarDocumento(documentoActaApertura, proyecto.getId(), proyecto.getCodigoUnicoAmbiental(), TipoDocumentoSistema.RA_PC_ACTA_APERTURA_CIERRE, "acta apertura y cierre.pdf", ProyectoLicenciaCoa.class.getSimpleName(), idProceso);
				documentoActaApertura = documentosFacade.documentoXTablaIdXIdDoc(proyecto.getId(), ProyectoLicenciaCoa.class.getSimpleName(), TipoDocumentoSistema.RA_PC_ACTA_APERTURA_CIERRE);
			}
			if (documentoRegistroAsistencia.getId() == null && documentoRegistroAsistencia.getContenidoDocumento() != null){
				documentosFacade.ingresarDocumento(documentoRegistroAsistencia, proyecto.getId(), proyecto.getCodigoUnicoAmbiental(), TipoDocumentoSistema.RA_PC_OBSERVACIONES, "registro de asistencia y observaciones.pdf", ProyectoLicenciaCoa.class.getSimpleName(), idProceso);
				documentoRegistroAsistencia = documentosFacade.documentoXTablaIdXIdDoc(proyecto.getId(), ProyectoLicenciaCoa.class.getSimpleName(), TipoDocumentoSistema.RA_PC_OBSERVACIONES);
			}
			if (documentoInformeSistematizado.getId() == null && documentoInformeSistematizado.getContenidoDocumento() != null){
				documentosFacade.ingresarDocumento(documentoInformeSistematizado, proyecto.getId(), proyecto.getCodigoUnicoAmbiental(), TipoDocumentoSistema.RA_PC_SISTEMATIZACION_PPC, "informe de sistematizacion de ppc.pdf", ProyectoLicenciaCoa.class.getSimpleName(), idProceso);
				documentoInformeSistematizado = documentosFacade.documentoXTablaIdXIdDoc(proyecto.getId(), ProyectoLicenciaCoa.class.getSimpleName(), TipoDocumentoSistema.RA_PC_SISTEMATIZACION_PPC);
			}
			if (documentoInvitacion.getId() == null && documentoInvitacion.getContenidoDocumento() != null){
				documentosFacade.ingresarDocumento(documentoInvitacion, proyecto.getId(), proyecto.getCodigoUnicoAmbiental(), TipoDocumentoSistema.RA_PC_INVITACION_PERSONAL, "invitación personal.pdf", ProyectoLicenciaCoa.class.getSimpleName(), idProceso);
				documentoInvitacion = documentosFacade.documentoXTablaIdXIdDoc(proyecto.getId(), ProyectoLicenciaCoa.class.getSimpleName(), TipoDocumentoSistema.RA_PC_INVITACION_PERSONAL);
			}
			if (documentoActaAsamblea.getId() == null && documentoActaAsamblea.getContenidoDocumento() != null){
				documentosFacade.ingresarDocumento(documentoActaAsamblea, proyecto.getId(), proyecto.getCodigoUnicoAmbiental(), TipoDocumentoSistema.RA_PC_ACTA_ASAMBLEA_PRESENTACION, "acta para la asamblea de presentacion.pdf", ProyectoLicenciaCoa.class.getSimpleName(), idProceso);
				documentoActaAsamblea = documentosFacade.documentoXTablaIdXIdDoc(proyecto.getId(), ProyectoLicenciaCoa.class.getSimpleName(), TipoDocumentoSistema.RA_PC_ACTA_ASAMBLEA_PRESENTACION);
			}
			if (documentoRegistroAsamblea.getId() == null && documentoRegistroAsamblea.getContenidoDocumento() != null){
				documentosFacade.ingresarDocumento(documentoRegistroAsamblea, proyecto.getId(), proyecto.getCodigoUnicoAmbiental(), TipoDocumentoSistema.RA_PC_ASISTENCIA_ASAMBLEA, "registro de asistencia a asamblea.pdf", ProyectoLicenciaCoa.class.getSimpleName(), idProceso);
				documentoRegistroAsamblea = documentosFacade.documentoXTablaIdXIdDoc(proyecto.getId(), ProyectoLicenciaCoa.class.getSimpleName(), TipoDocumentoSistema.RA_PC_ASISTENCIA_ASAMBLEA);
			}
			if (documentoAudioAsamblea.getId() == null && documentoAudioAsamblea.getContenidoDocumento() != null){
				documentosFacade.ingresarDocumento(documentoAudioAsamblea, proyecto.getId(), proyecto.getCodigoUnicoAmbiental(), TipoDocumentoSistema.RA_PC_AUDIO_PRESENTACION, "audio de la asamblea de presentacion.mp3", ProyectoLicenciaCoa.class.getSimpleName(), idProceso);
				documentoAudioAsamblea = documentosFacade.documentoXTablaIdXIdDoc(proyecto.getId(), ProyectoLicenciaCoa.class.getSimpleName(), TipoDocumentoSistema.RA_PC_AUDIO_PRESENTACION);
			}
			validarDatos = true;
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
		}
	}
	public StreamedContent getPlantillaComponente(String plantillaComponente) throws Exception {
		DefaultStreamedContent content = null;
		try {
			switch (plantillaComponente) {
			case "sistematizacion_ppc_byte":
				if (sistematizacion_ppc_byte != null) {
					content = new DefaultStreamedContent(new ByteArrayInputStream(sistematizacion_ppc_byte));
					content.setName(sistematizacion_ppc);
				}
				break;
			case "convocatoria_byte":
				if (convocatoria_byte != null) {
					content = new DefaultStreamedContent(new ByteArrayInputStream(convocatoria_byte));
					content.setName(convocatoria);
				}
				break;
			case "acta_apertura_cierre_byte":
				if (acta_apertura_cierre_byte != null) {
					content = new DefaultStreamedContent(new ByteArrayInputStream(acta_apertura_cierre_byte));
					content.setName(acta_apertura_cierre);
				}
				break;
			case "invitacion_personal_byte":
				if (invitacion_personal_byte != null) {
					content = new DefaultStreamedContent(new ByteArrayInputStream(invitacion_personal_byte));
					content.setName(invitacion_personal);
				}
				break;
			case "aplicacion_ppc_byte":
				if (aplicacion_ppc_byte != null) {
					content = new DefaultStreamedContent(new ByteArrayInputStream(aplicacion_ppc_byte));
					content.setName(aplicacion_ppc);
				}
				break;
			case "asistencia_observaciones_byte":
				if (asistencia_observaciones_byte != null) {
					content = new DefaultStreamedContent(new ByteArrayInputStream(asistencia_observaciones_byte));
					content.setName(asistencia_observaciones);
				}
				break;
			case "acta_asamblea_presentacion_byte":
				if (acta_asamblea_presentacion_byte != null) {
					content = new DefaultStreamedContent(new ByteArrayInputStream(acta_asamblea_presentacion_byte));
					content.setName(acta_asamblea_presentacion);
				}
				break;
			case "asistencia_asamblea_byte":
				if (asistencia_asamblea_byte != null) {
					content = new DefaultStreamedContent(new ByteArrayInputStream(asistencia_asamblea_byte));
					content.setName(asistencia_asamblea);
				}
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return content;
	}
}