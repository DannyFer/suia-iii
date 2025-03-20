package ec.gob.ambiente.control.retce.controllers;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.prevencion.tdr.controller.RevisarDocumentacionGeneralController;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.retce.model.EmisionesAtmosfericas;
import ec.gob.ambiente.retce.model.InformeTecnicoRetce;
import ec.gob.ambiente.retce.model.OficioPronunciamientoRetce;
import ec.gob.ambiente.retce.services.EmisionesAtmosfericasFacade;
import ec.gob.ambiente.retce.services.InformeTecnicoRetceFacade;
import ec.gob.ambiente.retce.services.OficioRetceFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ViewScoped
@ManagedBean
public class DescargarOficioEmisionesController {
	
	private static final Logger LOGGER = Logger.getLogger(RevisarDocumentacionGeneralController.class);
	
	@EJB
    private ProcesoFacade procesoFacade;
    @EJB
    private DocumentosFacade documentosFacade;
    @EJB
    private OficioRetceFacade oficioRetceFacade;
    @EJB
    private EmisionesAtmosfericasFacade emisionAtmosfericaFacade;
    @EJB
    private InformeTecnicoRetceFacade informeTecnicoRetceFacade;
    
    
    @EJB(lookup = Constantes.JBPM_EJB_TASK_BEAN)
    private TaskBeanFacade taskBeanFacade;

    @Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;

    @ManagedProperty(value = "#{proyectosBean}")
    @Getter
    @Setter
    private ProyectosBean proyectosBean;
    
    @Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;

    @Getter
    @Setter
    private Documento documento;
    
    @Getter
    @Setter
    private boolean descargado;
    
    @Getter
    @Setter
    private OficioPronunciamientoRetce oficio;
    
    private String tramite;
    
    private Map<String, Object> variables;
    
    @Getter
    @Setter
    private EmisionesAtmosfericas emisionAtmosferica;
    
    @Getter
    @Setter
    private InformeTecnicoRetce informeTecnicoRetce;
    
    @Setter
	@Getter
	private Integer numeroObservaciones, numeroRevision;
    
    private boolean aprobado = false;
    
    @PostConstruct
    public void init() {
        try {
        	variables=procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
			tramite=(String)variables.get(Constantes.VARIABLE_PROCESO_TRAMITE);	
			
			String numObservaciones = (String)variables.get("numero_observaciones");
			if(numObservaciones != null)
				numeroObservaciones= Integer.valueOf(numObservaciones);
			else
				numeroObservaciones = 0;
			
			numeroRevision=1+numeroObservaciones;
						
			emisionAtmosferica = emisionAtmosfericaFacade.findByCodigo(tramite);
			
			informeTecnicoRetce = informeTecnicoRetceFacade.getInforme(emisionAtmosferica.getCodigo(), TipoDocumentoSistema.INFORME_TECNICO_EMISIONES_ATMOSFERICAS, numeroRevision);
			
			if(informeTecnicoRetce != null && informeTecnicoRetce.getEsReporteAprobacion())
				aprobado = true;
        	
        	oficio = oficioRetceFacade.getOficio(tramite, TipoDocumentoSistema.OFICIO_PRONUNCIAMIENTO_EMISION_ATMOSFERICA);

            List<Documento> documentos = documentosFacade.documentoXTablaIdXIdDoc(oficio.getId(),
            		"OficioEmisionesAtmosfericas", TipoDocumentoSistema.OFICIO_PRONUNCIAMIENTO_EMISION_ATMOSFERICA);
            if (documentos.size() > 0) {
                documento = documentos.get(0);
            } else {
                JsfUtil.addMessageError("Error al realizar la operación.");

            }

        } catch (Exception e) {
        	e.printStackTrace();
            LOGGER.error(e);
            JsfUtil.addMessageError("Error al cargar los datos. Por favor intente más tarde.");
        }
    }
    
    public String completarTarea() {
        try {
        	
            if (descargado) {
            	
            	if(aprobado){
            		emisionAtmosferica.setFinalizado(true);
            		emisionAtmosfericaFacade.save(emisionAtmosferica, loginBean.getUsuario());
            	}
            	
                Map<String, Object> data = new ConcurrentHashMap<String, Object>();
                procesoFacade.aprobarTarea(loginBean.getUsuario(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), data);

                JsfUtil.addMessageInfo("Se realizó correctamente la operación.");
                return JsfUtil.actionNavigateTo("/bandeja/bandejaTareas.jsf");
            } else {
                JsfUtil.addMessageError("Debe descargar el documento.");
                return "";
            }
        } catch (JbpmException e) {
            LOGGER.error(e);
            JsfUtil.addMessageError("Error al realizar la operación.");
        } catch (Exception e) {
            LOGGER.error(e);
            JsfUtil.addMessageError("Error al realizar la operación.");
        }
        return "";
    }
    
    public StreamedContent getStream() throws Exception {
        DefaultStreamedContent content = new DefaultStreamedContent();
        if (documento != null) {
            if (documento.getContenidoDocumento() == null) {
                documento.setContenidoDocumento(documentosFacade.descargar(documento.getIdAlfresco()));
            }
            content = new DefaultStreamedContent(new ByteArrayInputStream(
                    documento.getContenidoDocumento()), "application/pdf");
            content.setName(documento.getNombre());
            descargado = true;
        }
        return content;

    }
    
    public void validarTareaBpm() {

        String url = "/control/retce/emisionesAtmosfericas/descargarOficio.jsf";

        JsfUtil.validarPaginasUrlTareasBpm(bandejaTareasBean.getTarea(), url);
    }


}
