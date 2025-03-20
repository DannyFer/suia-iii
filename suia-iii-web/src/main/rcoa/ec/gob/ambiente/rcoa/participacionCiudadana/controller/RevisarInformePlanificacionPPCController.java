package ec.gob.ambiente.rcoa.participacionCiudadana.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;

import ec.gob.ambiente.alfresco.service.AlfrescoServiceBean;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.rcoa.participacionCiudadana.facade.DocumentoPPCFacade;
import ec.gob.ambiente.rcoa.participacionCiudadana.model.DocumentosPPC;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.reportes.UtilDocumento;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;

@ManagedBean
@ViewScoped
public class RevisarInformePlanificacionPPCController {
	
	private static final Logger LOGGER = Logger.getLogger(RevisarInformePlanificacionPPCController.class);

	@EJB
	private ProcesoFacade procesoFacade;
	
	@EJB
	private DocumentoPPCFacade documentosFacade;
	
	@EJB(lookup = Constantes.ALFRESCO_SERVICE_BEAN)
    private AlfrescoServiceBean alfrescoServiceBean;
	
	@Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;
	
	@Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;
	
	@Getter
	@Setter
	@ManagedProperty(value = "#{proyectosBean}")
	private ProyectosBean proyectosBean;
	@Getter
	@Setter
	private Map<String, Object> variables;
	
	@Getter
    @Setter
    private String tramite = "";
	
	@Getter
    @Setter
    private Boolean tieneObservaciones;
	@Getter
    @Setter
    private Boolean requiereFacilitador;
	@Getter
    @Setter
    private Integer numeroFacilitador;
	
	@PostConstruct
	public void inicio()
	{
		try {			
			variables = procesoFacade.recuperarVariablesProceso(loginBean.getUsuario(),bandejaTareasBean.getTarea().getProcessInstanceId());
			tramite = (String) variables.get("tramite");
		} catch (JbpmException e) {

		}
	}
	
	public void descargarInforme()
	{
		try {
			List<DocumentosPPC> listaDocumentos;
			listaDocumentos = documentosFacade.documentoXTablaIdXIdDoc(proyectosBean.getProyectoRcoa().getId(), TipoDocumentoSistema.RCOA_PPC_INFORME_PLANIFICACION, "INFORME PLANIFICACION");
			if (listaDocumentos.size() > 0) {
				UtilDocumento.descargarPDF(alfrescoServiceBean.downloadDocumentById(listaDocumentos.get(0).getIdAlfresco()),"Informe planificación-"+new Date().getTime());
			}
		} catch (Exception e) {
			LOGGER.error("error al buscar  en la tabla  DocumentosPPC ::", e);
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
	}
	
	public void completarTarea(){
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("existenObservaciones", tieneObservaciones);			
			if(!tieneObservaciones)
			{
				params.put("necesitaFacilitadores", requiereFacilitador);
				if(requiereFacilitador)
				{
					params.put("numeroFacilitadores", numeroFacilitador);
					params.put("facilitadorAdicional", true);
				}
			}
			
			procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getTarea().getProcessInstanceId(), params);
            
            procesoFacade.aprobarTarea(loginBean.getUsuario(), bandejaTareasBean.getTarea().getTaskId(),  bandejaTareasBean.getProcessId(), null);
            
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
			JsfUtil.redirectTo(JsfUtil.NAVIGATION_TO_BANDEJA);

            
		} catch (Exception e) {					
			e.printStackTrace();
		}
	}
}
