package ec.gob.ambiente.rcoa.revisionDiagnosticoAmbiental.controllers;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
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

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.rcoa.facade.DocumentosCoaFacade;
import ec.gob.ambiente.rcoa.facade.OficioPronunciamientoDiagnosticoFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.model.DocumentosCOA;
import ec.gob.ambiente.rcoa.model.OficioPronunciamientoDiagnostico;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class RecibirPronunciamientoDiagnosticoController implements Serializable{

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
	
	@EJB
	private OficioPronunciamientoDiagnosticoFacade oficioPronunciamientoFacade;

	@Getter
	@Setter
	private DocumentosCOA documento;
	
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
	
	@PostConstruct
	public void iniciar() throws JbpmException, NumberFormatException, CmisAlfrescoException
	{
		variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(),bandejaTareasBean.getProcessId());
		tramite = (String) variables.get("u_tramite");
		
		tipoDocumento = TipoDocumentoSistema.RCOA_OFICIO_DIAGNOSTICO_AMBIENTAL;
		docuTableClass = "OficioDiagnosticoAmbiental";
		
		proyecto = proyectoLicenciaCoaFacade.buscarProyecto(tramite);
		OficioPronunciamientoDiagnostico oficioPronunciamiento = oficioPronunciamientoFacade.getPorProyecto(proyecto.getId());
		
		if(oficioPronunciamiento.getTipoPronunciamiento().equals(3))
			tipoDocumento = TipoDocumentoSistema.RCOA_ARCHIVO_OBSERVACIONES_DIAGNOSTICO_AMBIENTAL;
		else if(oficioPronunciamiento.getTipoPronunciamiento().equals(4))
			tipoDocumento = TipoDocumentoSistema.RCOA_ARCHIVO_NO_INICIA_REGULARIZACION;
		
		List<DocumentosCOA> listaDocumentosInt = documentosFacade.documentoXTablaIdXIdDoc(oficioPronunciamiento.getId(), tipoDocumento, docuTableClass);
		if (listaDocumentosInt.size() > 0) 
			documento = listaDocumentosInt.get(0);
	}

	public StreamedContent descargar() throws Exception {
		try{
			DefaultStreamedContent content = new DefaultStreamedContent();
			if (documento != null) {
				if (documento.getContenidoDocumento() == null) {
					documento.setContenidoDocumento(documentosFacade.descargar(documento.getIdAlfresco()));
				}
				content = new DefaultStreamedContent(new ByteArrayInputStream(
						documento.getContenidoDocumento()), documento.getExtencionDocumento());
				content.setName(documento.getNombreDocumento());
				
				documentoDescargado = true;
			} else {
				content = null;
				JsfUtil.addMessageError("No se pudo descargar documento.");
			}
			
			return content;
		} catch (Exception e) {
			JsfUtil.addMessageError("No se pudo descargar documento.");			
			e.printStackTrace();
		}
		return null;
	}
	
	public void finalizar() {
		try {
			if (!documentoDescargado) {
				JsfUtil.addMessageError("Debe descargar el oficio de pronunciamiento antes de finalizar.");
				return;
			} 
			
			try {
				Map<String, Object> params = new HashMap<String, Object>();

                params.put("iniciaProceso", true);
                
                procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getProcessInstanceId(), params);
                
				procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(),bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getTarea().getProcessInstanceId(), null);
				
				documento.setIdProceso(bandejaTareasBean.getTarea().getProcessInstanceId());
				documentosFacade.guardar(getDocumento());

				JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
				JsfUtil.redirectTo(JsfUtil.NAVIGATION_TO_BANDEJA);
			} catch (JbpmException e) {
				e.printStackTrace();
				JsfUtil.addMessageError("Error al realizar la operación.");
			}


		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Error al realizar la operación.");
		}
	}
}