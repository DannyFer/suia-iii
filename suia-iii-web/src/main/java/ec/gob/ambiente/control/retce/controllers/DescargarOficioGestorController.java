package ec.gob.ambiente.control.retce.controllers;

import java.io.Serializable;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.retce.model.DescargasLiquidas;
import ec.gob.ambiente.retce.model.GestorDesechosPeligrosos;
import ec.gob.ambiente.retce.model.InformeTecnicoRetce;
import ec.gob.ambiente.retce.model.OficioPronunciamientoRetce;
import ec.gob.ambiente.retce.services.DescargasLiquidasFacade;
import ec.gob.ambiente.retce.services.GestorDesechosPeligrososFacade;
import ec.gob.ambiente.retce.services.InformeTecnicoRetceFacade;
import ec.gob.ambiente.retce.services.OficioRetceFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.reportes.UtilDocumento;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class DescargarOficioGestorController implements Serializable {

	private static final long serialVersionUID = 1L;
	private final Logger LOG = Logger.getLogger(DescargarOficioGestorController.class);
	
	/*BEANs*/
	@ManagedProperty(value = "#{bandejaTareasBean}")
    @Getter
    @Setter
    private BandejaTareasBean bandejaTareasBean;
	
	/*EJBs*/
		
	@EJB
	private GestorDesechosPeligrososFacade gestorDesechosPeligrososFacade;
		
	@EJB
	private DocumentosFacade documentosFacade;

	@EJB
	private InformeTecnicoRetceFacade informeTecnicoRetceFacade;
	
	@EJB
	private OficioRetceFacade oficioRetceFacade;
		
	@EJB
	private ProcesoFacade procesoFacade;	
	
	/*Object*/
	private GestorDesechosPeligrosos gestorDesechosPeligrosos;
	
	private Documento documentoOficio;
	
	@Getter
	private InformeTecnicoRetce informe;
	
	private Map<String, Object> variables;
	
	@Getter
	private OficioPronunciamientoRetce oficio;	
		
	/*Boolean*/	
	
	/*Integer*/
	@Setter
	@Getter
	private Integer numeroObservaciones,numeroRevision;
	
	/*String*/
    private String tramite;      
	
    @PostConstruct
	public void init() {
		try {
			variables=procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
			tramite=(String)variables.get(Constantes.VARIABLE_PROCESO_TRAMITE);
			numeroObservaciones=variables.containsKey("numero_observaciones")?Integer.valueOf((String)variables.get("numero_observaciones")):0;		
			
			numeroRevision=1+numeroObservaciones;
			
			gestorDesechosPeligrosos=gestorDesechosPeligrososFacade.findByCodigo(tramite);
			
			informe = informeTecnicoRetceFacade.getInforme(gestorDesechosPeligrosos.getCodigo(),TipoDocumentoSistema.INFORME_TECNICO_GESTOR_DESECHOS,numeroRevision);				
			oficio = oficioRetceFacade.getOficio(gestorDesechosPeligrosos.getCodigo(),TipoDocumentoSistema.OFICIO_PRONUNCIAMIENTO_GESTOR_DESECHOS,numeroRevision);
			documentoOficio=documentosFacade.documentoXTablaIdXIdDocUnico(oficio.getId(),OficioPronunciamientoRetce.class.getSimpleName(),TipoDocumentoSistema.OFICIO_PRONUNCIAMIENTO_GESTOR_DESECHOS);
			
		} catch (Exception e) {
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos DescargarOficioGestorController.");
		}
	}
    
    private StreamedContent descargarDocumento(Documento documento){
		try {
			if(documento!=null && documento.getContenidoDocumento()==null)
				documento.setContenidoDocumento(documentosFacade.descargar(documento.getIdAlfresco()));
			return UtilDocumento.getStreamedContent(documento);
		} catch (Exception e) {
			return null;
		}
		
	}
    
    public StreamedContent getDocumentoOficio() {
		return descargarDocumento(documentoOficio);
	}
        
    public void enviar(){
		try {
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
	        JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
		} catch (JbpmException e) {
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
			e.printStackTrace();
		}
	}
}
