
package ec.gob.ambiente.prevencion.categoria2.v2.controller;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang.SerializationUtils;
import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.DocumentosTareasProceso;
import ec.gob.ambiente.suia.domain.FichaAmbientalMineria;
import ec.gob.ambiente.suia.domain.FichaAmbientalPma;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.TipoSector;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.FichaAmbientalPmaFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.ficha.mineria.facade.FichaAmbientalMineriaFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class ParticipacionCiudadanaSubirController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4066905757278530970L;

	private static final Logger LOG = Logger.getLogger(ParticipacionCiudadanaSubirController.class);

	@EJB
	private FichaAmbientalPmaFacade fichaAmbientalPmaFacade;
	
	@EJB
	private FichaAmbientalMineriaFacade fichaAmbientalMineriaFacade;

	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;
	
	@EJB
	private DocumentosFacade documentosFacade;

	@Getter
	@Setter
	@ManagedProperty(value = "#{proyectosBean}")
	private ProyectosBean proyectosBean;
	
    @ManagedProperty(value = "#{bandejaTareasBean}")
    @Getter
    @Setter
    private BandejaTareasBean bandejaTareasBean;
    
	
	@Getter
    @Setter
    private Documento documentoActaAperturaCierre,documentoActaAsambleaPresentacion,documentoConvocatoria,documentoSistematizacionPPC,documentoInvitacionPersonal,documentoAsistenciaAsamblea,documentoObservaciones,documentoAudioPresentacion;

	@Getter
    @Setter
    private boolean otrosSectoresOSaneamiento=false;
	
	//MarielaG para historial
	@Getter
    @Setter
    private Documento docOriginalActaAperturaCierre, docOriginalActaAsambleaPresentacion, docOriginalConvocatoria, docOriginalSistematizacionPPC, 
    docOriginalInvitacionPersonal, docOriginalAsistenciaAsamblea, docOriginalObservaciones, docOriginalAudioPresentacion;
	
	@Getter
    @Setter
    private List<Documento> historialesActaAperturaCierre, historialesActaAsambleaPresentacion, historialesConvocatoria, 
    historialesSistematizacionPPC, historialesInvitacionPersonal, historialesAsistenciaAsamblea, historialesObservaciones, 
    historialesAudioPresentacion, listaHistorialDocumentos;
	
	@PostConstruct
	public void init() {
		
		if(proyectosBean.getProyecto().getTipoSector().getId()==TipoSector.TIPO_SECTOR_OTROS || proyectosBean.getProyecto().getTipoSector().getId()==TipoSector.TIPO_SECTOR_SANEAMIENTO)
			otrosSectoresOSaneamiento=true;
		
		buscarDocumentos(null);			
	}
	
	
	
	private void buscarDocumentos(TipoDocumentoSistema tipoDocumento){
		try {
			//MarielaG para historial
			historialesActaAperturaCierre = new ArrayList<Documento>();
			historialesActaAsambleaPresentacion = new ArrayList<Documento>();
			historialesConvocatoria = new ArrayList<Documento>(); 
		    historialesSistematizacionPPC = new ArrayList<Documento>();
		    historialesInvitacionPersonal = new ArrayList<Documento>();
		    historialesAsistenciaAsamblea = new ArrayList<Documento>();
		    historialesObservaciones = new ArrayList<Documento>(); 
		    historialesAudioPresentacion = new ArrayList<Documento>();
		    listaHistorialDocumentos = new ArrayList<Documento>();
		    
		    List<Documento> documentosPpc = null;
		    if(tipoDocumento == null){
			    List<Integer> listaTipos = new ArrayList<Integer>(); 
		    	listaTipos.add(TipoDocumentoSistema.RA_PC_ACTA_APERTURA_CIERRE.getIdTipoDocumento());
		    	listaTipos.add(TipoDocumentoSistema.RA_PC_ACTA_ASAMBLEA_PRESENTACION.getIdTipoDocumento());
		    	listaTipos.add(TipoDocumentoSistema.RA_PC_CONVOCATORIA.getIdTipoDocumento());
		    	listaTipos.add(TipoDocumentoSistema.RA_PC_SISTEMATIZACION_PPC.getIdTipoDocumento());
		    	listaTipos.add(TipoDocumentoSistema.RA_PC_INVITACION_PERSONAL.getIdTipoDocumento());
		    	listaTipos.add(TipoDocumentoSistema.RA_PC_ASISTENCIA_ASAMBLEA.getIdTipoDocumento());
		    	listaTipos.add(TipoDocumentoSistema.RA_PC_OBSERVACIONES.getIdTipoDocumento());
		    	listaTipos.add(TipoDocumentoSistema.RA_PC_AUDIO_PRESENTACION.getIdTipoDocumento());
		    	
		    	documentosPpc = documentosFacade.recuperarDocumentosPorTipo(proyectosBean.getProyecto().getId(),
						ProyectoLicenciamientoAmbiental.class.getSimpleName(), listaTipos);
		    }else{
				documentosPpc = documentosFacade.documentoXTablaIdXIdDoc(proyectosBean.getProyecto().getId(),
						ProyectoLicenciamientoAmbiental.class.getSimpleName(), tipoDocumento);
		    }
			if (documentosPpc != null && documentosPpc.size() > 0) {
				buscarDocumentosPorTipo(documentosPpc);
			}
			documentoActaAperturaCierre = (documentoActaAperturaCierre == null) ? new Documento() : documentoActaAperturaCierre;
			documentoActaAsambleaPresentacion = (documentoActaAsambleaPresentacion == null) ? new Documento() : documentoActaAsambleaPresentacion;
			documentoConvocatoria = (documentoConvocatoria == null) ? new Documento() : documentoConvocatoria;
			documentoSistematizacionPPC = (documentoSistematizacionPPC == null) ? new Documento() : documentoSistematizacionPPC;
			documentoInvitacionPersonal = (documentoInvitacionPersonal == null) ? new Documento() : documentoInvitacionPersonal;
			documentoAsistenciaAsamblea = (documentoAsistenciaAsamblea == null) ? new Documento() : documentoAsistenciaAsamblea;
			documentoObservaciones = (documentoObservaciones == null) ? new Documento() : documentoObservaciones;
			documentoAudioPresentacion = (documentoAudioPresentacion == null) ? new Documento() : documentoAudioPresentacion;
			
		} catch (Exception e) {
			JsfUtil.addMessageError("Error en ParticipacionCiudadanaSubirController buscarDocumentos");
		}
		
		
	}	
	
	public void uploadFileActaAperturaCierre(FileUploadEvent event) {
		byte[] contenidoDocumento = event.getFile().getContents();
		documentoActaAperturaCierre.setId(null);
		documentoActaAperturaCierre.setContenidoDocumento(contenidoDocumento);
		documentoActaAperturaCierre.setNombre(event.getFile().getFileName());
		documentoActaAperturaCierre.setMime("application/pdf");
		documentoActaAperturaCierre.setExtesion(".pdf");
				
		documentoActaAperturaCierre=guardarDocumento(proyectosBean.getProyecto().getCodigo(), documentoActaAperturaCierre, TipoDocumentoSistema.RA_PC_ACTA_APERTURA_CIERRE, docOriginalActaAperturaCierre);
		if(docOriginalActaAperturaCierre == null)
			docOriginalActaAperturaCierre = (Documento) SerializationUtils.clone(documentoActaAperturaCierre);
		
		JsfUtil.addMessageInfo("Documento subido exitosamente");
	}
	
	public void uploadFileActaAsambleaPresentacion(FileUploadEvent event) {
		byte[] contenidoDocumento = event.getFile().getContents();
		documentoActaAsambleaPresentacion.setId(null);
		documentoActaAsambleaPresentacion.setContenidoDocumento(contenidoDocumento);
		documentoActaAsambleaPresentacion.setNombre(event.getFile().getFileName());
		documentoActaAsambleaPresentacion.setMime("application/pdf");
		documentoActaAsambleaPresentacion.setExtesion(".pdf");
		
		documentoActaAsambleaPresentacion=guardarDocumento(proyectosBean.getProyecto().getCodigo(), documentoActaAsambleaPresentacion, TipoDocumentoSistema.RA_PC_ACTA_ASAMBLEA_PRESENTACION, docOriginalActaAsambleaPresentacion);
		if(docOriginalActaAsambleaPresentacion == null)
			docOriginalActaAsambleaPresentacion = (Documento) SerializationUtils.clone(documentoActaAsambleaPresentacion);
		
		JsfUtil.addMessageInfo("Documento subido exitosamente");
	}
	
	public void uploadFileConvocatoria(FileUploadEvent event) {
		byte[] contenidoDocumento = event.getFile().getContents();
		documentoConvocatoria.setId(null);
		documentoConvocatoria.setContenidoDocumento(contenidoDocumento);
		documentoConvocatoria.setNombre(event.getFile().getFileName());
		documentoConvocatoria.setMime("application/pdf");
		documentoConvocatoria.setExtesion(".pdf");
		
		documentoConvocatoria=guardarDocumento(proyectosBean.getProyecto().getCodigo(), documentoConvocatoria, TipoDocumentoSistema.RA_PC_CONVOCATORIA, docOriginalConvocatoria);
		if(docOriginalConvocatoria == null)
			docOriginalConvocatoria = (Documento) SerializationUtils.clone(documentoConvocatoria);
		
		JsfUtil.addMessageInfo("Documento subido exitosamente");
	}
	
	public void uploadFileSistematizacionPPC(FileUploadEvent event) {
		byte[] contenidoDocumento = event.getFile().getContents();
		documentoSistematizacionPPC.setId(null);
		documentoSistematizacionPPC.setContenidoDocumento(contenidoDocumento);
		documentoSistematizacionPPC.setNombre(event.getFile().getFileName());
		documentoSistematizacionPPC.setMime("application/pdf");
		documentoSistematizacionPPC.setExtesion(".pdf");
		
		documentoSistematizacionPPC=guardarDocumento(proyectosBean.getProyecto().getCodigo(), documentoSistematizacionPPC, TipoDocumentoSistema.RA_PC_SISTEMATIZACION_PPC, docOriginalSistematizacionPPC);
		if(docOriginalSistematizacionPPC == null)
			docOriginalSistematizacionPPC = (Documento) SerializationUtils.clone(documentoSistematizacionPPC);
		
		JsfUtil.addMessageInfo("Documento subido exitosamente");
	}
	
	public void uploadFileInvitacionPersonal(FileUploadEvent event) {
		byte[] contenidoDocumento = event.getFile().getContents();
		documentoInvitacionPersonal.setId(null);
		documentoInvitacionPersonal.setContenidoDocumento(contenidoDocumento);
		documentoInvitacionPersonal.setNombre(event.getFile().getFileName());
		documentoInvitacionPersonal.setMime("application/pdf");
		documentoInvitacionPersonal.setExtesion(".pdf");
		
		documentoInvitacionPersonal=guardarDocumento(proyectosBean.getProyecto().getCodigo(), documentoInvitacionPersonal, TipoDocumentoSistema.RA_PC_INVITACION_PERSONAL, docOriginalInvitacionPersonal);
		if(docOriginalInvitacionPersonal == null)
			docOriginalInvitacionPersonal = (Documento) SerializationUtils.clone(documentoInvitacionPersonal);
		
		JsfUtil.addMessageInfo("Documento subido exitosamente");
	}
	
	public void uploadFileAsistenciaAsamblea(FileUploadEvent event) {
		byte[] contenidoDocumento = event.getFile().getContents();
		documentoAsistenciaAsamblea.setId(null);
		documentoAsistenciaAsamblea.setContenidoDocumento(contenidoDocumento);
		documentoAsistenciaAsamblea.setNombre(event.getFile().getFileName());
		documentoAsistenciaAsamblea.setMime("application/pdf");
		documentoAsistenciaAsamblea.setExtesion(".pdf");
		
		documentoAsistenciaAsamblea=guardarDocumento(proyectosBean.getProyecto().getCodigo(), documentoAsistenciaAsamblea, TipoDocumentoSistema.RA_PC_ASISTENCIA_ASAMBLEA, docOriginalAsistenciaAsamblea);
		if(docOriginalAsistenciaAsamblea == null)
			docOriginalAsistenciaAsamblea = (Documento) SerializationUtils.clone(documentoAsistenciaAsamblea);
		
		JsfUtil.addMessageInfo("Documento subido exitosamente");
	}
	
	public void uploadFileObservaciones(FileUploadEvent event) {
		byte[] contenidoDocumento = event.getFile().getContents();
		documentoObservaciones.setId(null);
		documentoObservaciones.setContenidoDocumento(contenidoDocumento);
		documentoObservaciones.setNombre(event.getFile().getFileName());
		documentoObservaciones.setMime("application/pdf");
		documentoObservaciones.setExtesion(".pdf");
				
		documentoObservaciones=guardarDocumento(proyectosBean.getProyecto().getCodigo(), documentoObservaciones, TipoDocumentoSistema.RA_PC_OBSERVACIONES, docOriginalObservaciones);
		if(docOriginalObservaciones == null)
			docOriginalObservaciones = (Documento) SerializationUtils.clone(documentoObservaciones);
		
		JsfUtil.addMessageInfo("Documento subido exitosamente");
	}
	
	public void uploadFileAudioPresentacion(FileUploadEvent event) {
		byte[] contenidoDocumento = event.getFile().getContents();
		documentoAudioPresentacion.setId(null);
		documentoAudioPresentacion.setContenidoDocumento(contenidoDocumento);
		documentoAudioPresentacion.setNombre(event.getFile().getFileName());
		documentoAudioPresentacion.setMime("audio/mpeg3");
		documentoAudioPresentacion.setExtesion(".mp3");
				
		documentoAudioPresentacion=guardarDocumento(proyectosBean.getProyecto().getCodigo(), documentoAudioPresentacion, TipoDocumentoSistema.RA_PC_AUDIO_PRESENTACION, docOriginalAudioPresentacion);
		if(docOriginalAudioPresentacion == null)
			docOriginalAudioPresentacion = (Documento) SerializationUtils.clone(documentoAudioPresentacion);
		
		JsfUtil.addMessageInfo("Documento subido exitosamente");
	}
    
    private Documento guardarDocumento(String codigoProyecto,Documento documento,TipoDocumentoSistema tipoDocumento, Documento documentoOriginal){
    	try {    		
    		DocumentosTareasProceso documentoTarea = new DocumentosTareasProceso();
        	documentoTarea.setIdTarea(bandejaTareasBean.getTarea().getTaskId());    	
        	documentoTarea.setProcessInstanceId(bandejaTareasBean.getProcessId());
        	
        	documento.setIdTable(proyectosBean.getProyecto().getId());
        	documento.setNombreTabla(ProyectoLicenciamientoAmbiental.class.getSimpleName());
        	documento = documentosFacade.guardarDocumentoAlfresco(codigoProyecto,Constantes.CARPETA_PARTICIPACION_CIUDADANA,documentoTarea.getProcessInstanceId(),documento,tipoDocumento,documentoTarea);
    		
    		//MarielaG para guardar historial
    		if (documentoOriginal != null) {
    			documentoOriginal.setIdHistorico(documento.getId());
    			documentosFacade.actualizarDocumento(documentoOriginal);
    			buscarDocumentos(tipoDocumento);
    		}
    		
    		return documento;
    	} catch (Exception e) {
    		LOG.error(e.getMessage());
    		return null;
    	}    	
    }
	
	public String guardar(boolean mineria) {
		try {
            if (otrosSectoresOSaneamiento && (documentoConvocatoria.getId() == null || documentoActaAperturaCierre.getId() == null || documentoObservaciones.getId() == null || documentoSistematizacionPPC.getId() == null)){
            	 JsfUtil.addMessageError("Debe subir todos los documentos obligatorios.");
                 return "";            	
            }
            if (!otrosSectoresOSaneamiento && (documentoInvitacionPersonal.getId() == null || documentoActaAsambleaPresentacion.getId() == null || documentoAsistenciaAsamblea.getId() == null || documentoAudioPresentacion.getId() == null || documentoSistematizacionPPC.getId() == null)){
           	 	JsfUtil.addMessageError("Debe subir todos los documentos obligatorios.");
                return "";            	
            }
            
            ProyectoLicenciamientoAmbiental proyectoActivo = proyectoLicenciamientoAmbientalFacade.buscarProyectosLicenciamientoAmbientalPorId(proyectosBean.getProyecto().getId());
            
            if(mineria)
            {
            	FichaAmbientalMineria fichaAmbientalMineria=fichaAmbientalMineriaFacade.obtenerPorProyecto(proyectoActivo);
            	fichaAmbientalMineria.setValidarParticipacionCiudadana(true);
                fichaAmbientalMineriaFacade.guardarFicha(fichaAmbientalMineria);
                
            }else{
            	FichaAmbientalPma fichaAmbientalPma = fichaAmbientalPmaFacade.getFichaAmbientalPorCodigoProyecto(proyectoActivo.getCodigo());          
                fichaAmbientalPma.setValidarParticipacionCiudadana(true);
                fichaAmbientalPmaFacade.guardarSoloFicha(fichaAmbientalPma);                	
            }            
            
            JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
            return "/prevencion/categoria2/v2/ficha"+(mineria?"Mineria/enviar":"Ambiental/envio")+"Ficha.jsf?faces-redirect=true";	
            

		} catch (Exception e) {
			LOG.error("error al guardar ", e);
			JsfUtil.addMessageError("Ocurrió un error al guardar la tarea");
			return "";
		}
	}
	
	public boolean ppc() throws ParseException {
		Date fechaproyecto=null;
		Date fechabloqueo=null;
		Date fechabloqueoObligatorioInicio=null;
		Date fechabloqueoObligatorioFin=null;
		Date fechabloqueoOpcionalInicio=null;
		Date fechabloqueoOpcionalFin=null;
		
		Date fechabloqueoSIN=null;
		boolean bloquear=false;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		fechabloqueo = sdf.parse(Constantes.getFechaProyectosSinPccAntes());
		fechabloqueoObligatorioInicio = sdf.parse(Constantes.getFechaProyectosObligatorioPccInicio());
		fechabloqueoObligatorioFin = sdf.parse(Constantes.getFechaProyectosObligatorioPccFin());	
		fechabloqueoOpcionalInicio = sdf.parse(Constantes.getFechaProyectosOpcionalPccInicio());
		fechabloqueoOpcionalFin = sdf.parse(Constantes.getFechaProyectosOpcionalPccFin());	
		
		fechabloqueoSIN = sdf.parse(Constantes.getFechaProyectosSinPpcAdelante());
		
		fechaproyecto=sdf.parse(proyectosBean.getProyecto().getFechaRegistro().toString());
				
		if (fechaproyecto.after(fechabloqueoOpcionalInicio) && fechaproyecto.before(fechabloqueoOpcionalFin)){
			return true;
		}
				
		return bloquear;
		
	}

//MarielaG para recuperar historial
	public void fillHistorialDocumentos(Integer opcion) {
		listaHistorialDocumentos = new ArrayList<>();
		switch (opcion) {
		case 1:
			listaHistorialDocumentos = historialesConvocatoria;
			break;
		case 2:
			listaHistorialDocumentos = historialesActaAperturaCierre;
			break;
		case 3:
			listaHistorialDocumentos = historialesObservaciones;
			break;
		case 4:
			listaHistorialDocumentos = historialesSistematizacionPPC;
			break;
		case 5:
			listaHistorialDocumentos = historialesInvitacionPersonal;
			break;
		case 6:
			listaHistorialDocumentos = historialesActaAsambleaPresentacion;
			break;
		case 7:
			listaHistorialDocumentos = historialesAsistenciaAsamblea;
			break;
		case 8:
			listaHistorialDocumentos = historialesAudioPresentacion;
			break;
		default:
			break;
		}
	}
	
	//MarielaG para historial
	public StreamedContent descargarDocumentoHistorico(Documento documento) {
		try {
			DefaultStreamedContent content = null;
			byte[] documentoContenido = null;
			
			if (documento != null && documento.getIdAlfresco() != null)
				documentoContenido = documentosFacade.descargar(documento.getIdAlfresco());
			
			if (documentoContenido != null)
				documento.setContenidoDocumento(documentoContenido);
			
			if (documento != null && documento.getNombre() != null
					&& documento.getContenidoDocumento() != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(
						documento.getContenidoDocumento()));
				content.setName(documento.getNombre());
			} else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
			
			return content;
		} catch (Exception e) {
			LOG.error("error al descargar ", e);
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
          return null;
		}
	}
	
	//MarielaG para historial
	private void buscarDocumentosPorTipo(List<Documento> documentosPpc){
		for (Documento documento : documentosPpc) {
			switch (documento.getTipoDocumento().getId()) {
			case 3401:
				if(documento.getIdHistorico() == null){
					documentoActaAperturaCierre = documento;
					
					docOriginalActaAperturaCierre = (Documento) SerializationUtils.clone(documentoActaAperturaCierre);
				} else{
					historialesActaAperturaCierre.add(0, documento);
				}
				break;
			case 3402:
				if (documento.getIdHistorico() == null) {
					documentoActaAsambleaPresentacion=documento;
					
					docOriginalActaAsambleaPresentacion = (Documento) SerializationUtils.clone(documentoActaAsambleaPresentacion);
				} else {
					historialesActaAsambleaPresentacion.add(0, documento);
				}
				break;
			case 3403:
				if (documento.getIdHistorico() == null) {
					documentoConvocatoria = documento;
					
					docOriginalConvocatoria = (Documento) SerializationUtils.clone(documento);
				} else {
					historialesConvocatoria.add(0, documento);
				}
				break;
			case 3404:
				if (documento.getIdHistorico() == null) {
					documentoSistematizacionPPC = documento;
					
					docOriginalSistematizacionPPC = (Documento) SerializationUtils.clone(documentoSistematizacionPPC);
				} else {
					historialesSistematizacionPPC.add(0, documento);
				}
				break;
			case 3405:
				if (documento.getIdHistorico() == null) {
					//if (documentoInvitacionPersonal == null || documentoInvitacionPersonal.getId() == null)
						documentoInvitacionPersonal = documento;
					
					docOriginalInvitacionPersonal = (Documento) SerializationUtils.clone(documentoInvitacionPersonal);
				} else {
					historialesInvitacionPersonal.add(0, documento);
				}
				break;
			case 3406:
				if (documento.getIdHistorico() == null) {
					documentoAsistenciaAsamblea = documento;
					
					docOriginalAsistenciaAsamblea = (Documento) SerializationUtils.clone(documentoAsistenciaAsamblea);
				} else {
					historialesAsistenciaAsamblea.add(0, documento);
				}
				break;
			case 3407:
				if (documento.getIdHistorico() == null) {
					documentoObservaciones = documento;
					
					docOriginalObservaciones = (Documento) SerializationUtils.clone(documentoObservaciones);
				} else {
					historialesObservaciones.add(0, documento);
				}
				break;
			case 3408:
				if (documento.getIdHistorico() == null) {
					documentoAudioPresentacion = documento;
					
					docOriginalAudioPresentacion = (Documento) SerializationUtils.clone(documentoAudioPresentacion);
				} else {
					historialesAudioPresentacion.add(0, documento);
				}
				break;
			default:
				break;
			}
		}
	}
}