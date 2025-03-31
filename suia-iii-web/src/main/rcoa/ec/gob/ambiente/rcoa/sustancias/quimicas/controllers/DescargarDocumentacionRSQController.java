package ec.gob.ambiente.rcoa.sustancias.quimicas.controllers;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.rcoa.enums.TipoInformeOficioEnum;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.DocumentosRSQFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.InformesOficiosRSQFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.RegistroSustanciaQuimicaFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.DocumentosSustanciasQuimicasRcoa;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.InformeOficioRSQ;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.RegistroSustanciaQuimica;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;

@ManagedBean
@ViewScoped
public class DescargarDocumentacionRSQController implements Serializable {

	private static final long serialVersionUID = -1L;

	private static final Logger LOG = Logger.getLogger(DescargarDocumentacionRSQController.class);
	
	//EJBs
	@EJB
	private DocumentosRSQFacade documentosRSQFacade;

	@EJB
	private InformesOficiosRSQFacade informesOficiosRSQFacade;	
		
	@EJB
	private ProcesoFacade procesoFacade;
	
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	    
	@EJB
	private RegistroSustanciaQuimicaFacade registroSustanciaQuimicaFacade;  
	    
	//BEANs
    @ManagedProperty(value = "#{bandejaTareasBean}")
    @Getter
    @Setter
    private BandejaTareasBean bandejaTareasBean;
     
	//OBJs	
    @Getter
    @Setter
    private DocumentosSustanciasQuimicasRcoa documentoOficio,documentoRsq;
    
    private ProyectoLicenciaCoa proyectoLicenciaCoa;
	
	private RegistroSustanciaQuimica registroSustanciaQuimica;
    
    @Getter
    @Setter
    private InformeOficioRSQ oficio;
    
    //MAPs	
    private Map<String, Object> variables;
		
    //STRINGs
	private String varTramite;
	
	//INTEGERs
	private Integer numeroRevision;

	@PostConstruct
	public void init() {
		try {
			cargarDatosTarea();
			cargarDatosProyecto();
			
		} catch (Exception e) {
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos OficioPronunciamientoRSQController.");
		}
	}
	
	private void cargarDatosTarea() throws ServiceException{

		try {
			variables=procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
			varTramite=(String)variables.get("tramite");
			numeroRevision=variables.containsKey("numero_revision")?(Integer.valueOf((String)variables.get("numero_revision"))):1;
			
		} catch (JbpmException e) {
			LOG.error("Error al cargarDatosTarea "+e.getCause()+" "+e.getMessage());
		}
	}
	
	private void cargarDatosProyecto() throws ServiceException, CmisAlfrescoException{		
		proyectoLicenciaCoa=proyectoLicenciaCoaFacade.buscarProyecto(varTramite);
		registroSustanciaQuimica=registroSustanciaQuimicaFacade.obtenerRegistroPorProyecto(proyectoLicenciaCoa);
				
		if(registroSustanciaQuimica!=null) {
			oficio=informesOficiosRSQFacade.obtenerPorRSQ(registroSustanciaQuimica,TipoInformeOficioEnum.OFICIO_PRONUNCIAMIENTO,numeroRevision);
			documentoOficio=documentosRSQFacade.obtenerDocumentoPorTipo(registroSustanciaQuimica.pronunciamientoAprobado()?TipoDocumentoSistema.RCOA_RSQ_OFICIO_APROBADO:TipoDocumentoSistema.RCOA_RSQ_OFICIO_NEGADO,"InformeOficioRSQ",oficio.getId());
			documentoRsq=documentosRSQFacade.obtenerDocumentoPorTipo(TipoDocumentoSistema.RCOA_RSQ_REGISTRO_SUSANCIAS_QUIMICAS,RegistroSustanciaQuimica.class.getSimpleName(),registroSustanciaQuimica.getId());		
		}		
	}	
	
	public void enviar(){
		try {		
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
	        JsfUtil.redirectToBandeja();
		} catch (/*Jbpm*/Exception e) {
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
			e.printStackTrace();
		}
	}
}