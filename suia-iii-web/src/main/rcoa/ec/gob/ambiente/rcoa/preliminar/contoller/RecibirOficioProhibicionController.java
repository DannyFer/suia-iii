package ec.gob.ambiente.rcoa.preliminar.contoller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.rcoa.certificado.interseccion.facade.CertificadoInterseccionCoaFacade;
import ec.gob.ambiente.rcoa.certificado.interseccion.model.CertificadoInterseccionOficioCoa;
import ec.gob.ambiente.rcoa.facade.DocumentosCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.model.DocumentosCOA;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;

@ManagedBean
@ViewScoped
public class RecibirOficioProhibicionController implements Serializable{

	 private static final long serialVersionUID = -875087443147320594L;
	  
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
	private DocumentosCoaFacade documentosFacade;
	
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;

	@Getter
	@Setter
	private DocumentosCOA documento,documentoCertificado,documentoMapa,documentoInformacion;
	
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyecto;
	
	@Getter
	@Setter
	private TipoDocumentoSistema tipoDocumento;

	@Getter
	@Setter
	private Boolean documentoDescargado = false;
	
	private Map<String, Object> variables;
	
	@Getter
	@Setter
	private String tramite ="", docuTableClass;
	
	private CertificadoInterseccionOficioCoa oficioCI;
	
	@EJB
	private CertificadoInterseccionCoaFacade certificadoInterseccionCoaFacade;
	
	@Getter
	@Setter
	@ManagedProperty(value = "#{reporteInformacionPreliminarController}")
	private ReporteInformacionPreliminarController reporteInformacionPreliminarController;
	
	@Getter
	@Setter
	private boolean mapaDescargado, ciDescargado,preliminarDescargado;
	
	
	@PostConstruct
	public void iniciar() throws JbpmException, NumberFormatException, CmisAlfrescoException
	{
		variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(),bandejaTareasBean.getProcessId());
		tramite = (String) variables.get("u_tramite");
		
		tipoDocumento = TipoDocumentoSistema.RCOA_PRONUNCIAMIENTO_PROHIBICION_ACTIVIDAD;
		docuTableClass = "PronunciamientoProhibicionActividad";		
		
		proyecto = proyectoLicenciaCoaFacade.buscarProyecto(tramite);
		
		List<DocumentosCOA> listaDocumentosInt = documentosFacade.documentoXTablaIdXIdDoc(proyecto.getId(), tipoDocumento, docuTableClass);
		if (listaDocumentosInt.size() > 0) 
			documento = listaDocumentosInt.get(0);
		
		oficioCI=certificadoInterseccionCoaFacade.obtenerPorCodigoProyecto(tramite);
		List<DocumentosCOA> listaDocumentosCert = documentosFacade.documentoXTablaIdXIdDoc(oficioCI.getId(), TipoDocumentoSistema.RCOA_CERTIFICADO_INTERSECCION_OFICIO,"CertificadoInterseccionOficioCoa");
		if (listaDocumentosCert.size() > 0) {
			documentoCertificado = listaDocumentosCert.get(0);
		}
		List<DocumentosCOA> listaDocumentosMap = documentosFacade.documentoXTablaIdXIdDoc(proyecto.getId(), TipoDocumentoSistema.RCOA_CERTIFICADO_INTERSECCION_MAPA,"ProyectoLicenciaCoa");		
		if (listaDocumentosMap.size() > 0) {
			documentoMapa = listaDocumentosMap.get(0);
		}
			
//		List<DocumentosCOA> listaDocumentos = documentosFacade.documentoXTablaIdXIdDoc(proyecto.getId(),TipoDocumentoSistema.REPORTE_INFORMACION_PRELIMINAR,"ProyectoLicenciaCoa");
//		if (listaDocumentos.size() > 0) {
//			documentoInformacion = listaDocumentos.get(0);
//		}		
//		boolean generar = false;
//		if (documentoInformacion == null || documentoInformacion.getId() == null)
//			generar = true;
//			
//		if(generar) {
//			reporteInformacionPreliminarController.generarReporte(proyecto, true, false);
//			List<DocumentosCOA> listaDocumentos1 = documentosFacade.documentoXTablaIdXIdDoc(proyecto.getId(),TipoDocumentoSistema.REPORTE_INFORMACION_PRELIMINAR,"ProyectoLicenciaCoa");
//
//			if (listaDocumentos1.size() > 0) {
//				documentoInformacion = listaDocumentos1.get(0);
//			}
//		}
	}
	
	public StreamedContent descargar() throws Exception {
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
		documentoDescargado = true;
		return content;

	}
	
	public void finalizar() {
		try {
//			if (!mapaDescargado || !ciDescargado || !preliminarDescargado || !documentoDescargado) {
			if (!mapaDescargado || !ciDescargado || !documentoDescargado) {
				if (!mapaDescargado)
					JsfUtil.addMessageError("Debe descargar el Mapa de certificado de intersección.");

				if (!ciDescargado)
					JsfUtil.addMessageError("Debe descargar el Certificado de intersección.");
				
//				if (!preliminarDescargado)
//					JsfUtil.addMessageError("Debe descargar el resumen de la información preliminar.");
				
				if (!documentoDescargado)
					JsfUtil.addMessageError("Debe descargar el oficio antes de finalizar.");
				
				return;
			}
			
			try {
				procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(),bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getTarea().getProcessInstanceId(), null);
				
				documento.setIdProceso(bandejaTareasBean.getTarea().getProcessInstanceId());
				documentosFacade.guardar(getDocumento());
				
//				documentoInformacion.setIdProceso(bandejaTareasBean.getTarea().getProcessInstanceId());
//				documentosFacade.guardar(documentoInformacion);

				JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
				JsfUtil.redirectTo(JsfUtil.NAVIGATION_TO_BANDEJA);
			} catch (JbpmException e) {
				JsfUtil.addMessageError("Error al realizar la operación.");
			}


		} catch (Exception e) {
			JsfUtil.addMessageError("Error al realizar la operación.");
		}
	}
	
	public StreamedContent descargarMapa() throws IOException {
		DefaultStreamedContent content = null;
		try {
			byte[] documentoContent = null;
				
			if (documentoMapa != null && documentoMapa.getIdAlfresco() != null) {
				documentoContent = documentosFacade.descargar(documentoMapa.getIdAlfresco());
			}
			
			if (documentoMapa != null && documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(documentoMapa.getNombreDocumento());
			} 
			else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
			
			mapaDescargado = true;
		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
	
	public StreamedContent descargarCertificadoInterseccion() throws IOException {
		DefaultStreamedContent content = null;
		try {
			byte[] documentoContent = null;
				
			if (documentoCertificado != null && documentoCertificado.getIdAlfresco() != null) {
				documentoContent = documentosFacade.descargar(documentoCertificado.getIdAlfresco());
			}
			
			if (documentoCertificado != null && documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(documentoCertificado.getNombreDocumento());
			} 
			else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
			
			ciDescargado = true;
		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
	public StreamedContent descargarInformacion() throws IOException {
		DefaultStreamedContent content = null;
		try {
			byte[] documentoContent = null;
				
			if (documentoInformacion != null && documentoInformacion.getIdAlfresco() != null) {
				documentoContent = documentosFacade.descargar(documentoInformacion.getIdAlfresco());
			}
			
			if (documentoInformacion != null && documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(documentoInformacion.getNombreDocumento());
			} 
			else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
			
			preliminarDescargado = true;
		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
}