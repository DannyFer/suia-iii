package ec.gob.ambiente.rcoa.participacionCiudadanaBypass.controllers;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.InformacionProyectoEIACoaFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.InformacionProyectoEia;
import ec.gob.ambiente.rcoa.participacionCiudadana.facade.DocumentoPPCFacade;
import ec.gob.ambiente.rcoa.participacionCiudadana.facade.FacilitadorPPCFacade;
import ec.gob.ambiente.rcoa.participacionCiudadana.facade.ProyectoFacilitadorPPCFacade;
import ec.gob.ambiente.rcoa.participacionCiudadana.model.DocumentosPPC;
import ec.gob.ambiente.rcoa.participacionCiudadana.model.FacilitadorPPC;
import ec.gob.ambiente.rcoa.participacionCiudadana.model.ProyectoFacilitadorPPC;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class IngresarExpedientePPCController {
		
	@ManagedProperty(value = "#{proyectosBean}")
	@Getter
	@Setter
	private ProyectosBean proyectosBean;	
	@Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;	
	@Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;
	
	@EJB
	private ProcesoFacade procesoFacade;	
	@EJB
	private DocumentoPPCFacade documentosFacade;	
	@EJB
	private FacilitadorPPCFacade facilitadorFacade;
	@EJB
	private ProyectoFacilitadorPPCFacade proyectoFacilitadorPPCFacade;
	@EJB
	private InformacionProyectoEIACoaFacade informacionProyectoEIACoaFacade;
	
	@Getter
	@Setter
	private ProyectoFacilitadorPPC proyectoFacilitadorPPC;
    @Getter
	@Setter
    private FacilitadorPPC facilitador = new FacilitadorPPC();
    
    @Getter
    @Setter
	private InformacionProyectoEia informacionProyectoEia;
    
    @Getter
	@Setter
	private List<DocumentosPPC> listaDocumentosPPC, listaDocumentosEsIA, listaDocumentosEliminar;
	
	@Getter
	@Setter
	private Map<String, Object> variables;	
	@Getter
    @Setter
    private String tramite = "";    
    @Getter
    @Setter
    private byte[] documento;
    
    private Integer idProyecto;
    
    @Getter
    @Setter
    private Boolean esPronunciamientoAprobado;
	
	@PostConstruct
	public void inicio()
	{
		try {
			variables = procesoFacade.recuperarVariablesProceso(loginBean.getUsuario(),bandejaTareasBean.getTarea().getProcessInstanceId());
			tramite = (String) variables.get("tramite");
					
			facilitador=facilitadorFacade.facilitador(proyectosBean.getProyectoRcoa(), loginBean.getUsuario());
			
			idProyecto = Integer.valueOf((String) variables.get("idProyecto"));
			proyectoFacilitadorPPC = proyectoFacilitadorPPCFacade.getByIdProyecto(idProyecto);
			informacionProyectoEia = informacionProyectoEIACoaFacade.obtenerInformacionProyectoEIAPorProyecto(proyectosBean.getProyectoRcoa());
			
			listaDocumentosEliminar = new ArrayList<>();
			
			listaDocumentosPPC = documentosFacade.documentoXTablaIdXIdDoc(proyectoFacilitadorPPC.getId(), 
					TipoDocumentoSistema.RCOA_PPC_BYPASS_EXPEDIENTE, "Expediente PPC");
			
			listaDocumentosEsIA = documentosFacade.documentoXTablaIdXIdDoc(informacionProyectoEia.getId(), 
					TipoDocumentoSistema.EIA_ESTUDIO_ACTUALIZADO, InformacionProyectoEia.class.getSimpleName());
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos.");
		}
	}
	
	public void uploadDocPPC(FileUploadEvent event) {
		DocumentosPPC documento = new DocumentosPPC();
		byte[] contenidoDocumento = event.getFile().getContents();
		documento.setContenidoDocumento(contenidoDocumento);
		documento.setNombreDocumento(event.getFile().getFileName());
		documento.setExtencionDocumento(".pdf");		
		documento.setTipo("application/pdf");
		documento.setNombreTabla("Expediente PPC");
		documento.setIdTabla(proyectoFacilitadorPPC.getId());
		documento.setIdProceso( bandejaTareasBean.getTarea().getProcessInstanceId());
		
		listaDocumentosPPC.add(documento);
	
		JsfUtil.addMessageInfo("Documento subido exitosamente");
	}
	
	public void uploadDocEstudio(FileUploadEvent event) {
		DocumentosPPC documento = new DocumentosPPC();
		byte[] contenidoDocumento = event.getFile().getContents();
		documento.setContenidoDocumento(contenidoDocumento);
		documento.setNombreDocumento(event.getFile().getFileName());
		documento.setExtencionDocumento(".pdf");		
		documento.setTipo("application/pdf");
		documento.setNombreTabla(InformacionProyectoEia.class.getSimpleName());
		documento.setIdTabla(informacionProyectoEia.getId());
		documento.setIdProceso( bandejaTareasBean.getTarea().getProcessInstanceId());
		
		listaDocumentosEsIA.add(documento);
	
		JsfUtil.addMessageInfo("Documento subido exitosamente");
	}
	
	public StreamedContent descargarDocumento(DocumentosPPC documento) throws Exception {
		DefaultStreamedContent content = new DefaultStreamedContent();
		if (documento != null) {
			if (documento.getContenidoDocumento() == null) {
				documento.setContenidoDocumento(documentosFacade
						.descargar(documento.getIdAlfresco()));
			}
			content = new DefaultStreamedContent(new ByteArrayInputStream(
					documento.getContenidoDocumento()), documento.getExtencionDocumento());
			content.setName(documento.getNombreDocumento());
		}
		return content;

	}
	
	public void eliminarDocumento(Integer lista, DocumentosPPC documento) {
		try {
			if (documento.getId() != null) {
				documento.setEstado(false);
				listaDocumentosEliminar.add(documento);
			}
			
			if(lista.equals(1))
				listaDocumentosPPC.remove(documento);
			else if(lista.equals(2))
				listaDocumentosEsIA.remove(documento);
		} catch (Exception e) {

		}
	}
	
	public void completarTarea() {
		try {
			
			if(listaDocumentosPPC == null || listaDocumentosPPC.size() == 0) {
				JsfUtil.addMessageError("Debe adjuntar por lo menos un archivo en la sección Adjuntar los archivos del Proceso de Participación Ciudadana");
				return;
			}
			
			for (DocumentosPPC documento : listaDocumentosPPC) {
				if(documento.getId() == null)
					documentosFacade.guardarDocumentoAlfresco(proyectosBean.getProyectoRcoa().getCodigoUnicoAmbiental(),
							"PARTICIPACION CIUDADANA", bandejaTareasBean.getTarea().getProcessInstanceId(),
							documento, TipoDocumentoSistema.RCOA_PPC_BYPASS_EXPEDIENTE);
			}
			
			if(esPronunciamientoAprobado) {				
				if(listaDocumentosEsIA != null && listaDocumentosEsIA.size() > 0) {
					for (DocumentosPPC documento : listaDocumentosEsIA) {
						if(documento.getId() == null)
							documentosFacade.guardarDocumentoAlfresco(proyectosBean.getProyectoRcoa().getCodigoUnicoAmbiental(),
									"ESTUDIO_AMBIENTAL", bandejaTareasBean.getTarea().getProcessInstanceId(),
									documento, TipoDocumentoSistema.EIA_ESTUDIO_ACTUALIZADO);
					}
				}
			} else {				
				if(listaDocumentosEsIA != null && listaDocumentosEsIA.size() > 0) {
					for (DocumentosPPC documento : listaDocumentosEsIA) {
						if (documento.getId() != null)
							listaDocumentosEliminar.add(documento);
					}
				}
				
				listaDocumentosEsIA = new ArrayList<DocumentosPPC>();
			}
			
			if(listaDocumentosEliminar != null && listaDocumentosEliminar.size() > 0) {
				for (DocumentosPPC documento: listaDocumentosEliminar) {
					documento.setEstado(false);;
					
					documentosFacade.guardar(documento);
				}
				
				listaDocumentosEliminar = new ArrayList<DocumentosPPC>();
			}
			
			Map<String, Object> parametros = new HashMap<>();
			parametros.put("esAprobacion", esPronunciamientoAprobado);
			
			procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(),bandejaTareasBean.getProcessId(), parametros);
			
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(),bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getTarea().getProcessInstanceId(), null);
			
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
			JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}

}
