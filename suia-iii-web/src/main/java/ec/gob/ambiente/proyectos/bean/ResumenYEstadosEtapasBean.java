/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.proyectos.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
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
import org.kie.api.task.model.TaskSummary;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.rcoa.digitalizacion.facade.AutorizacionAdministrativaAmbientalFacade;
import ec.gob.ambiente.rcoa.digitalizacion.model.AutorizacionAdministrativaAmbiental;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.catalogocategoriasflujo.facade.CatalogoCategoriasFlujoFacade;
import ec.gob.ambiente.suia.comparator.OrdenarTareaPorEstadoComparator;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.CategoriaFlujo;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.Flujo;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.PerforacionExplorativa;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.dto.ResumeTarea;
import ec.gob.ambiente.suia.dto.Tarea;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.prevencion.categoria2.ficha.mineria.facade.FichaAmbientalMineria020Facade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.survey.SurveyResponseFacade;
import ec.gob.ambiente.suia.utils.BeanLocator;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.ConvertidorObjetosDominioUtil;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.webservicesclientes.facade.JbpmSuiaCustomServicesFacade;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 *
 * @author mayriliscs
 * @version Revision: 1.0
 *          <p>
 *          [Autor: mayriliscs, Fecha: 26/02/2015]
 *          </p>
 */
@ManagedBean
@ViewScoped
public class ResumenYEstadosEtapasBean implements Serializable {

    public static final String COMPLETADO = "Completado";
    private static final long serialVersionUID = -227940254655409170L;
    private static final Logger LOG = Logger.getLogger(ResumenYEstadosEtapasBean.class);
    @Getter
    @Setter
    private List<CategoriaFlujo> flujos;
    @Getter
    @Setter
    private List<Tarea> tareas;
    @Getter
    @Setter
    private List<Documento> documentos;
    @EJB
    private CatalogoCategoriasFlujoFacade catalogoCategoriasFlujoFacade;
    @EJB
    private ProcesoFacade procesoFacade;
    @Getter
    @Setter
    @ManagedProperty(value = "#{proyectosBean}")
    private ProyectosBean proyectosBean;
    
    @Getter
	@Setter
	@ManagedProperty(value = "#{proyectosAdminBean}")
	private ProyectosAdminBean proyectosAdminBean;
    
    @EJB
	private FichaAmbientalMineria020Facade fichaAmbientalmineria020facade;
    @EJB
	private AutorizacionAdministrativaAmbientalFacade autorizacionAdministrativaAmbientalFacade;
    
    @Getter
    @Setter
    public boolean proyectoFinalizado=false;
    
    @EJB
    ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;
    
    @Getter    
    List<Documento> documentosArchivacion;
    
    @EJB
	private DocumentosFacade documentosFacade;
    
    @Getter
    @Setter
    public boolean mostrarMsjAcuerdosCamaroneras=false;
    
    @Getter
    @Setter
	public Integer tipoDocResolucion = TipoDocumentoSistema.TIPO_DOCUMENTO_RESOLUCION_REGISTRO_AMBIENTAL.getIdTipoDocumento();
    
    @Getter
    @Setter
    public Integer tipoDocLicencia = TipoDocumentoSistema.TIPO_INFORME_TECNICO_GENERAL_LA.getIdTipoDocumento();
    
    @Getter
    @Setter
    private Documento documentoPermiso;
    
    @EJB 
	private SurveyResponseFacade surveyResponseFacade;
    
    @Getter
	@Setter
	private boolean showSurveyG, mostrarEncuesta;
    
    public static String surveyLink = Constantes.getPropertyAsString("suia.survey.link");
    
    @EJB
	private OrganizacionFacade organizacionFacade;
    
    @PostConstruct
    public void init() {
        try {
        	if ((proyectosBean.getProyecto().getNombre())!=null){
				setFlujos(catalogoCategoriasFlujoFacade.obtenerFlujosDeProyectoPorCategoria(proyectosBean.getProyecto(),
						Constantes.ID_PROYECTO, JsfUtil.getLoggedUser()));
				
				String codigoActualizarScdr=fichaAmbientalmineria020facade.cargarPerforacionExplorativa(proyectosBean.getProyecto()).getCodeUpdate();
				if(codigoActualizarScdr == null)
					codigoActualizarScdr = "";
				
				if (proyectosBean.getProyecto().getCatalogoCategoria().getCodigo().equals("21.02.03.05") 
						|| proyectosBean.getProyecto().getCatalogoCategoria().getCodigo().equals("21.02.04.03")
						|| proyectosBean.getProyecto().getCatalogoCategoria().getCodigo().equals("21.02.05.03")
						|| proyectosBean.getProyecto().getCatalogoCategoria().getCodigo().equals("21.02.02.03")
						|| codigoActualizarScdr.equals("21.02.02.01") || codigoActualizarScdr.equals("21.02.03.06")) {
					PerforacionExplorativa perforacionExplorativa= new PerforacionExplorativa();
					perforacionExplorativa= fichaAmbientalmineria020facade.cargarPerforacionExplorativa(proyectosBean.getProyecto());
					
					if (perforacionExplorativa.getApproveTechnical()==null){
						perforacionExplorativa.setApproveTechnical(false);
					}
					if (perforacionExplorativa.getFinalized()==null){
						perforacionExplorativa.setFinalized(false);
					}
					for (int i = 0; i < getFlujos().size(); i++) {
						if (getFlujos().get(i).getFlujo().getNombreFlujo().equals("Registro ambiental v2")){
							if (!(perforacionExplorativa.getFinalized() && perforacionExplorativa.getApproveTechnical())){
								setProyectoFinalizado(true);
							}else{
								setProyectoFinalizado(false);
							}
						}
					}
				}
				
			}else{
				setFlujos(catalogoCategoriasFlujoFacade.obtenerFlujosDeProyectoPorCategoria(proyectosAdminBean.getProyecto(),
						Constantes.ID_PROYECTO, JsfUtil.getLoggedUser()));
			}
            for (CategoriaFlujo cf : getFlujos()) {
                if (cf.getFlujo().getEstadoProceso().equals("Activo")) {
                    cf.getFlujo().setEstadoProceso("En curso");
                }
                /*if (cf.getFlujo().getProcessInstanceId() != null) {
					List<Tarea> tareas1 = verTareas(cf.getFlujo().getProcessInstanceId(), cf.getFlujo()
							.getEstadoProceso());
					cf.getFlujo().setEstadoProceso(tareas1.get(0).getEstado());
				}*/

            }/**/
            
            verEncuesta(proyectosBean.getProyecto());
        } catch (Exception exception) {
            LOG.error("No se puede Obtener el resumen de Tareas", exception);
        }
    }

    /**
     * <b> Metodo para obtener las tareas desde el bpm. </b>
     * <p>
     * [Author: Javier Lucero, Date: 19/05/2015]
     * </p>
     *
     * @param processInstanceId : id del proceso
     * @param estadoProceso     : estado del proceso
     * @return List<Tarea>: lista de tareas
     * @throws ServiceException : Excepcion
     */
    public List<Tarea> verTareas(Long processInstanceId, String estadoProceso) throws ServiceException {
        try {
            List<Tarea> tareasAux = new ArrayList<Tarea>();
            if (estadoProceso.equals(COMPLETADO)) {
                List<ResumeTarea> resumeTareas = BeanLocator.getInstance(JbpmSuiaCustomServicesFacade.class)
                        .getResumenTareas(processInstanceId);
                for (ResumeTarea resumeTarea : resumeTareas) {
                    Tarea tarea = new Tarea();
                    ConvertidorObjetosDominioUtil.convertirBamTaskSummaryATarea(resumeTarea, tarea);
                    tareasAux.add(tarea);
                }
                Collections.sort(tareasAux, new OrdenarTareaPorEstadoComparator());
            } else {
                List<TaskSummary> taskSummaries = procesoFacade.getTaskBySelectFlow(JsfUtil.getLoggedUser(),
                        processInstanceId);
                int longitud = taskSummaries.size();
                for (int i = 0; i < longitud; i++) {
                    Tarea tarea = new Tarea();
                    ConvertidorObjetosDominioUtil.convertirTaskSummaryATarea(taskSummaries.get(i), tarea);
                    tareasAux.add(tarea);
                }
                Collections.sort(tareasAux, new OrdenarTareaPorEstadoComparator());
            }
            return tareasAux;
        } catch (Exception exception) {
            LOG.error("Ocurrio un error en var tareas", exception);
            return null;
        }
    }

    /**
     * <b> Metodo que valida si l bpm obtiene las tareas. </b>
     * <p>
     * [Author: Javier Lucero, Date: 19/05/2015]
     * </p>
     */
    public void validarBpm() {
        if (getFlujos() == null || getFlujos().isEmpty()) {
            JsfUtil.addMessageInfo("No se puede obtener el resumen de Tareas, comuniquese con mesa de ayuda.");
        }

    }
    
    public String certificadoAbortado(Flujo flujo){
        if(flujo.getNombreFlujo().equals("Requisitos previos a Certificado Ambiental")&&flujo.getEstadoProceso().equals("Abortado")){
            return "Completado - No Favorable";
        }
        if(flujo.getNombreFlujo().equals("Registro de generador de desechos especiales y peligrosos")){
            if(proyectoLicenciamientoAmbientalFacade.getRGDArchivado(proyectosBean.getProyecto().getId()))
                return "Archivado";
        }       
        if(flujo.getNombreFlujo().equals("Licencia Ambiental")&&flujo.getEstadoProceso().equals("Completado")){
            Map<String, Object> variables= new ConcurrentHashMap<String, Object>();
            Integer cantidadNotificaciones=0;
            try {
                variables=procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), flujo.getProcessInstanceId());
                cantidadNotificaciones = Integer.valueOf(variables.get("cantidadNotificaciones").toString());
            } catch (JbpmException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if(cantidadNotificaciones==3){
                flujo.setEstadoProceso("Completado - Observado (3)");
                return "Completado - Observado (3)";
            }
        }
        
        if(flujo.getEstadoProceso().equals("Completado")){
        	String proyecto = null;
        	if(proyectosBean.getProyecto() != null && proyectosBean.getProyecto().getId() != null) {
        		proyecto = proyectosBean.getProyecto().getCodigo();
        	} else if(proyectosAdminBean.getProyecto() != null && proyectosAdminBean.getProyecto().getId() != null) {
        		proyecto = proyectosAdminBean.getProyecto().getCodigo();
        	}
        	
        	Boolean esDigitalizado = false;
			//si el proceso está finalizado se busca en la base de digitalizacion
        	AutorizacionAdministrativaAmbiental autorizacionAdministrativa = autorizacionAdministrativaAmbientalFacade.obtenerAAAPorCodigoProyecto(proyecto);
			if(autorizacionAdministrativa != null && autorizacionAdministrativa.getAutorizacionAdministrativaAmbiental() != null){
				String tipoAaa = autorizacionAdministrativa.getAutorizacionAdministrativaAmbiental().split("-")[0];
				if(flujo.getNombreFlujo().toLowerCase().contains(tipoAaa.toLowerCase())) { //para mostrar el digitalizado solo en los procesos principales
					esDigitalizado = true;
				}
			}
        	
			if(esDigitalizado){
				return "Completado (Digitalizado)";
			}
        }
        
        return flujo.getEstadoProceso();
    }
    
    public boolean rgdArchivado(Flujo flujo){
        boolean btndisable=false;
        if(flujo.getNombreFlujo().equals("Registro de generador de desechos especiales y peligrosos")){
            if(proyectoLicenciamientoAmbientalFacade.getRGDArchivado(proyectosBean.getProyecto().getId()))
            {
                btndisable=true;
            }
        }
        return btndisable;
    }
    

    public void documentosProyectoArchivacion(Integer proyectoId){
        documentosArchivacion = new ArrayList<Documento>();
        
        try {
            for (Documento documento : documentosFacade.documentoXTablaIdXIdDoc(proyectoId, ProyectoLicenciamientoAmbiental.class.getSimpleName(), TipoDocumentoSistema.ARCHIVACION_PROYECTO_ADJUNTO)) {
                documento.setContenidoDocumento(documentosFacade.descargar(documento.getIdAlfresco(),documento.getFechaCreacion()));
                documentosArchivacion.add(documento);
                
            }
            for (Documento documento : documentosFacade.documentoXTablaIdXIdDoc(proyectoId, ProyectoLicenciamientoAmbiental.class.getSimpleName(), TipoDocumentoSistema.REACTIVACION_PROYECTO_ADJUNTO)) {
                documento.setContenidoDocumento(documentosFacade.descargar(documento.getIdAlfresco(),documento.getFechaCreacion()));
                documentosArchivacion.add(documento);
                
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        
    }    
    
    public void obtenerDocumentoDescarga(Documento documento) {
    	documentoPermiso = documento;
    }
    
    public void cerrarMensajeCamaroneras() {
    	mostrarMsjAcuerdosCamaroneras = false;
    }
    
    public boolean verEncuesta(ProyectoLicenciamientoAmbiental proyecto){
		if(Usuario.isUserInRole(JsfUtil.getLoggedUser(),"sujeto de control")){
			if(!surveyResponseFacade.findByProjectApp(proyecto.getCodigo(), "suia")){
				mostrarEncuesta = true;
			}else{
				mostrarEncuesta = false;
			}
		}else
			mostrarEncuesta = false;			
		
		showSurveyG = false;
		return mostrarEncuesta;
	}
    
    public String urlLinkSurvey() throws ServiceException {
    	try {
    		Organizacion organizacion = new Organizacion();
    		if(proyectosBean.getProyecto().getUsuario() != null && proyectosBean.getProyecto().getUsuario().getPersona().getOrganizaciones().size() > 0){
    			organizacion = organizacionFacade.buscarPorRuc( proyectosBean.getProyecto().getUsuario().getNombre());
    		}else{
    			organizacion = null;
    		}       	  	
        	
    		String url = surveyLink;
    		String usuarioUrl = proyectosBean.getProyecto().getUsuario() == null ? "" : proyectosBean.getProyecto().getUsuario().getNombre();
    		String proyectoUrl = proyectosBean.getProyecto().getCodigo();
    		String appUlr = "suia";
    		String tipoPerUrl = (organizacion!=null)?"juridico":"natural";
    		String tipoUsr = "externo";
    		url = url + "/faces/index.xhtml?" 
    				+ "usrid=" + usuarioUrl + "&app=" + appUlr + "&project=" + proyectoUrl 
    				+ "&tipoper=" + tipoPerUrl + "&tipouser=" + tipoUsr;
    		System.out.println("enlace>>>" + url);
    		return url;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}    	
	}
}
