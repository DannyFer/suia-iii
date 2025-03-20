package ec.gob.ambiente.rcoa.util;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.jbpm.process.audit.ProcessInstanceLog;

import ec.gob.ambiente.rcoa.facade.DocumentosProcesoRcoaFacade;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.suia.administracion.facade.FlujosCategoriaFacade;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.Flujo;
import ec.gob.ambiente.suia.domain.RolUsuario;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class BuscarDocumentosBean {
	
	@EJB
    ProcesoFacade procesoFacade;
	
	@EJB
	private FlujosCategoriaFacade flujosCategoriaFacade;
    
    @EJB
	private DocumentosProcesoRcoaFacade documentosProcesoRcoaFacade;

	public List<Documento> buscarDocumentos(Long processInstanceId, Boolean buscarContenido, ProyectoLicenciaCoa proyecto) {
		try {
			List<Documento> documentos = new ArrayList<>();
			
			Boolean mostrarTodo = isOperador(JsfUtil.getLoggedUser()) ? false : true;
			
			ProcessInstanceLog proceso = procesoFacade.getProcessInstanceLog(JsfUtil.getLoggedUser(), processInstanceId);
			
			Flujo flujo = flujosCategoriaFacade.getFlujoPorIdProceso(proceso.getProcessId());
			
			if (String.valueOf(proceso.getProcessId()).equals("Suia.AprobracionRequisitosTecnicosGesTrans2")){
					flujo.setIdProceso("rcoa.AprobracionRequisitosTecnicosGesTrans2");
			}
			
			if (String.valueOf(proceso.getProcessId()).equals("mae-procesos.RegistroAmbiental")){				
					flujo = flujosCategoriaFacade.getFlujoPorIdProceso("rcoa.RegistroAmbientalSD");
			}
			
			if(flujo.getTablaDocumento() != null && flujo.getPrefijoTablaDocumento() != null) {
				documentos = getdocumentosFlujo(flujo, processInstanceId, mostrarTodo, buscarContenido);
			} else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
			
			if (String.valueOf(proceso.getProcessId()).equals("rcoa.ResolucionLicenciaAmbiental")) {
				Flujo flujoRgd = flujosCategoriaFacade.getFlujoPorIdProceso("rcoa.RegistrodeGeneradordeDesechosPeligrososyEspeciales");
				Flujo flujoRsq = flujosCategoriaFacade.getFlujoPorIdProceso("rcoa.RegistroSustanciasQuimicas");
				
				List<Documento> documentosRgd = getdocumentosFlujo(flujoRgd, processInstanceId, mostrarTodo, buscarContenido);
				documentos.addAll(0, documentosRgd);
				if(documentosRgd != null && documentosRgd.size() > 0 && !documentosRgd.isEmpty() && proyecto != null) {
					List<Documento> listaDocumentos = documentosProcesoRcoaFacade.descargarDocumentosProcesoRgdZip(proyecto);
					documentos.addAll(0, listaDocumentos);
				}
				
				documentos.addAll(0, getdocumentosFlujo(flujoRsq, processInstanceId, mostrarTodo, buscarContenido));
			}
			
			if (String.valueOf(proceso.getProcessId()).equals("rcoa.CertificadoAmbiental")) {
				for(Documento doc : documentos){
					if(doc.getIdTable() == null){
						
						byte[] byteGuiasAlmacenamiento  = null;
						try {
							String clave = doc.getNombre();
							String claveNombre = clave.replace("-", "."); 
							
							if(!claveNombre.endsWith(".pdf")){
								String nombreDocumento = claveNombre + ".pdf";	
								doc.setNombre(nombreDocumento);
							}							
							
							byteGuiasAlmacenamiento = documentosProcesoRcoaFacade.descargarPorNombre(doc.getNombre());
							doc.setContenidoDocumento(byteGuiasAlmacenamiento);
							doc.setIdAlfresco(null);												
						} catch (Exception ex) {
						}						
					}
				}				
			}
			
			return documentos;

		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
		
		return null;
	}
	
	public List<Documento> getdocumentosFlujo(Flujo flujo, Long processInstanceId, Boolean mostrarTodo, Boolean buscarContenido) throws Exception {
		List<Documento> documentos = new ArrayList<>();
		
		if(flujo.getTablaDocumento() != null && flujo.getPrefijoTablaDocumento() != null) {
			documentos = documentosProcesoRcoaFacade.getDocumentosPorFlujoNombresUnicos(flujo, processInstanceId, mostrarTodo);
			
			if(buscarContenido) {
				for(Documento documento : documentos){
					byte[] archivoObtenido = new byte[1];
					documento.setContenidoDocumento(archivoObtenido);
				}
			}
		}
		
		return documentos;
	}
	
	public boolean isOperador(Usuario usuario){
		for (RolUsuario rolUsuario : usuario.getRolUsuarios()) {
			if(rolUsuario.getRol().getNombre().contains("sujeto") && rolUsuario.getEstado()){
					return true;
			}
		}
		return false;
	}
}
