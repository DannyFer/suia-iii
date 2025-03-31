package ec.gob.ambiente.rcoa.preliminar.contoller;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.jbpm.process.audit.ProcessInstanceLog;

import ec.gob.ambiente.rcoa.certificado.interseccion.facade.CertificadoInterseccionCoaFacade;
import ec.gob.ambiente.rcoa.certificado.interseccion.model.CertificadoInterseccionOficioCoa;
import ec.gob.ambiente.rcoa.facade.DocumentosCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.model.DocumentosCOA;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.BeanLocator;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class GenerarDocumentosController {
	
	@EJB
	private DocumentosCoaFacade documentosFacade;
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	@EJB
    private UsuarioFacade usuarioFacade;
	@EJB
	private ProcesoFacade procesoFacade;
	@EJB
	private CertificadoInterseccionCoaFacade certificadoInterseccionCoaFacade;
	
	@Getter
	@Setter
	@ManagedProperty(value = "#{reporteInformacionPreliminarController}")
	private ReporteInformacionPreliminarController reporteInformacionPreliminarController;
	
	@Getter
	@Setter
	private DocumentosCOA documentoInformacion, documentoInformacionManual, documentoMapa, documentoCertificado,documento;
	
	@Getter
	@Setter
	private String tramite;
	
	@Getter
	@Setter
	private String fechaOficioCI;
	
	@Getter
	@Setter
	private Long processInstanceId;
	
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyecto;
	
	@Getter
	@Setter
	private CertificadoInterseccionOficioCoa oficioCI;
	
	@PostConstruct
	public void init(){
		try {
						
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void buscarProcessId() throws Exception {
		List<ProcessInstanceLog> procesosTramite = procesoFacade
				.getProcessInstancesLogsVariableValue(JsfUtil.getLoggedUser(),
						"tramite", tramite);
		
		if (procesosTramite.size() > 0) {
			for (ProcessInstanceLog processLog : procesosTramite) {
				if (processLog.getProcessId().equals(
								Constantes.RCOA_REGISTRO_PRELIMINAR)) {
					processInstanceId = processLog.getProcessInstanceId();
					break;
				}
			}
		}
	}
	
	public void generarCertificadoInterseccion() {
		try {
			if(tramite == null || tramite.isEmpty()) {
				JsfUtil.addMessageError("El tramite es requerido");
				return;
			}
			
			if(fechaOficioCI == null || fechaOficioCI.isEmpty()) {
				JsfUtil.addMessageError("La fecha del oficio de intersección es requerida");
				return;
			}
			
			buscarProcessId();
			if(processInstanceId == null) {
				JsfUtil.addMessageError("Error al buscar el processInstanceId del proyecto.");
				return;
			}
			
			proyecto = proyectoLicenciaCoaFacade.buscarProyecto(tramite);
			if(proyecto == null || proyecto.getId() == null ) {
				JsfUtil.addMessageError("Error al recuperar la información el proyecto.");
				return;
			}
			
			oficioCI=certificadoInterseccionCoaFacade.obtenerPorCodigoProyecto(tramite);
			if(oficioCI == null || oficioCI.getId() == null ) {
				JsfUtil.addMessageError("Error al recuperar el oficio de intersección del proyecto.");
				return;
			}
			
			GeneracionDocumentosController oficioController = (GeneracionDocumentosController) BeanLocator.getInstance(GeneracionDocumentosController.class);
			documento = oficioController.generarCertificadoInterseccion(proyecto,usuarioFacade.buscarUsuario(oficioCI.getUsuarioFirma()));

			List<DocumentosCOA> listaDocumentosInt = documentosFacade.documentoXTablaIdXIdDoc(oficioCI.getId(), TipoDocumentoSistema.RCOA_CERTIFICADO_INTERSECCION_OFICIO,"CertificadoInterseccionOficioCoa");
			if (listaDocumentosInt.size() > 0) {
				documentoCertificado = listaDocumentosInt.get(0);
				
	        	certificadoInterseccionCoaFacade.actualizarFechaCertificado(fechaOficioCI, oficioCI.getId());
			}
			
			List<DocumentosCOA> listaDocumentosMap = documentosFacade.documentoXTablaIdXIdDoc(proyecto.getId(), TipoDocumentoSistema.RCOA_CERTIFICADO_INTERSECCION_MAPA,"ProyectoLicenciaCoa");		
			if (listaDocumentosMap.size() > 0) {
				documentoMapa = listaDocumentosMap.get(0);
			}
			
			documentoCertificado.setIdProceso(processInstanceId);
			documentosFacade.guardar(documentoCertificado);
			
			if(documentoMapa.getIdProceso() == null) {
				documentoMapa.setIdProceso(processInstanceId);
				documentosFacade.guardar(documentoMapa);
			}
			
			
		} catch (Exception e) {
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
			e.printStackTrace();
		}
	}
	
	public void generarPreliminar() {
		try {
			if(tramite == null || tramite.isEmpty()) {
				JsfUtil.addMessageError("El tramite es requerido");
				return;
			}
			
			buscarProcessId();
			if(processInstanceId == null) {
				JsfUtil.addMessageError("Error al buscar el processInstanceId del proyecto.");
				return;
			}
			
			proyecto = proyectoLicenciaCoaFacade.buscarProyecto(tramite);
			if(proyecto == null || proyecto.getId() == null ) {
				JsfUtil.addMessageError("Error al recuperar la información el proyecto.");
				return;
			}
			
			Boolean mostrarOperador = (proyecto.getCategorizacion() == 1) ? false : true;
			
			reporteInformacionPreliminarController.generarReporte(proyecto, true, mostrarOperador);
			List<DocumentosCOA> listaDocumentos = documentosFacade.documentoXTablaIdXIdDoc(proyecto.getId(),
					TipoDocumentoSistema.REPORTE_INFORMACION_PRELIMINAR, "ProyectoLicenciaCoa");

			if (listaDocumentos.size() > 0) {
				documentoInformacion = listaDocumentos.get(0);
			}
			
			documentoInformacion.setIdProceso(processInstanceId);
			documentosFacade.guardar(documentoInformacion);
			
		} catch (Exception e) {
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
			e.printStackTrace();
		}
	}

}
