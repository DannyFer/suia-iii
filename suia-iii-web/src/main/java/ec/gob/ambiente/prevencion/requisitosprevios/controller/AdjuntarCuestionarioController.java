package ec.gob.ambiente.prevencion.requisitosprevios.controller;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.prevencion.requisitosprevios.bean.AdjuntarCuestionarioBean;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.certificadointerseccion.service.CertificadoInterseccionFacade;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.TipoSector;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.requisitosPrevios.RequisitosPreviosLicenciaFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@RequestScoped
@ManagedBean
public class AdjuntarCuestionarioController implements Serializable {

    private static final long serialVersionUID = -65871475834217786L;
    private static final Logger LOGGER = Logger
            .getLogger(AdjuntarCuestionarioController.class);

    @EJB
    private RequisitosPreviosLicenciaFacade requisitosPreviosFacade;

    @EJB
    private ProcesoFacade procesoFacade;

    @EJB
    private AreaFacade areaFacade;

    @Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;

    @EJB(lookup = Constantes.JBPM_EJB_TASK_BEAN)
    private TaskBeanFacade taskBeanFacade;

    @Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;

    @Getter
    @Setter
    @ManagedProperty(value = "#{adjuntarCuestionarioBean}")
    private AdjuntarCuestionarioBean adjuntarCuestionarioBean;
    
    
    @EJB
    private CertificadoInterseccionFacade certificadoInterseccionFacade;
    @EJB
    private UsuarioFacade usuarioFacade;


    public String iniciarTarea() {
        Boolean fail = false;

        if(adjuntarCuestionarioBean.isRequiereBP() && !adjuntarCuestionarioBean.isDescargadoBP()){
            JsfUtil.addMessageError("Para continuar debe descargar el documento referente a Bosques Protectores.");
            fail = true;
        }
        if(adjuntarCuestionarioBean.isRequiereSNAP() && !adjuntarCuestionarioBean.isDescargadoSNAP()){
            JsfUtil.addMessageError("Para continuar debe descargar el documento referente a Áreas Protegidas.");
            fail = true;
        }
        if(adjuntarCuestionarioBean.isRequiereBP() && adjuntarCuestionarioBean.getCuestionarioBP() == null){
            JsfUtil.addMessageError("Para continuar debe adjuntar el documento para Bosques Protectores.");
            fail = true;
        }
        if(adjuntarCuestionarioBean.isRequiereSNAP() && adjuntarCuestionarioBean.getCuestionarioSNAP() == null){
            JsfUtil.addMessageError("Para continuar debe adjuntar el documento para Áreas Protegidas.");
            fail = true;
        }

        if(fail){
            return "";
        }

        //Adjuntar documento con respuestas BP
        if(adjuntarCuestionarioBean.isRequiereBP()){
            try {
                requisitosPreviosFacade.adjuntarDocumentoRequisitosPrevios(adjuntarCuestionarioBean.getCuestionarioBP(),
                        adjuntarCuestionarioBean.getProyecto().getId(), adjuntarCuestionarioBean.getProyecto().getCodigo(),
                        getBandejaTareasBean().getProcessId(), getBandejaTareasBean().getTarea().getTaskId(), TipoDocumentoSistema.PREGUNTAS_RESPUESTAS_INTERSECCION);
            } catch (Exception e) {
                LOGGER.error("Error al adjuntar documento con respuestas BP.", e);
                JsfUtil.addMessageError("Ocurrio un error al enviar la información.");
            }
        }

        //Adjuntar documento con respuestas SNAP
        if(adjuntarCuestionarioBean.isRequiereSNAP()){
            try {
                requisitosPreviosFacade.adjuntarDocumentoRequisitosPrevios(adjuntarCuestionarioBean.getCuestionarioSNAP(),
                        adjuntarCuestionarioBean.getProyecto().getId(), adjuntarCuestionarioBean.getProyecto().getCodigo(),
                        getBandejaTareasBean().getProcessId(), getBandejaTareasBean().getTarea().getTaskId(), TipoDocumentoSistema.PREGUNTAS_RESPUESTAS_INTERSECCION_SNAP);
            } catch (Exception e) {
                LOGGER.error("Error al adjuntar documento con respuestas SNAP.", e);
                JsfUtil.addMessageError("Ocurrio un error al enviar la información.");
            }
        }
        
        //--- asignar técnico con menos carga--------------------------------------------------------------------------
        Map<String, Object> params = new ConcurrentHashMap<String, Object>();
        //Intersecciones
        Map<String, Boolean> capasInterseca = certificadoInterseccionFacade.getCapasInterseccionBoolean(adjuntarCuestionarioBean.getProyecto().getCodigo());
        Boolean intersecaSNAP = capasInterseca.get(Constantes.CAPA_SNAP);
        Boolean intersecaBP = capasInterseca.get(Constantes.CAPA_BP);
        Boolean intersecaRAMSARAREA = capasInterseca.get(Constantes.CAPA_RAMSAR_AREA);
        Boolean intersecaRAMSARPUNTO = capasInterseca.get(Constantes.CAPA_RAMSAR_PUNTO);
        Boolean intersecaPFE = capasInterseca.get(Constantes.CAPA_PFE);
        if (intersecaSNAP || intersecaBP || intersecaRAMSARAREA || intersecaRAMSARPUNTO || intersecaPFE) {
        	if (intersecaSNAP || intersecaRAMSARAREA || intersecaRAMSARPUNTO) {
        		Area areaBiodiversidad;
        		if (adjuntarCuestionarioBean.getProyecto().getAreaResponsable().getTipoArea().getSiglas().equals("PC")) {
        			areaBiodiversidad = areaFacade.getAreaSiglas("DNB");
        		} else {
        			areaBiodiversidad = adjuntarCuestionarioBean.getProyecto().getAreaResponsable(); //Ver como buscar el área de biodiversidad para las provincias
        		}
        		String usrResponsablePronunciamientoBiodiversidad="";
        		try {
        			usrResponsablePronunciamientoBiodiversidad = areaFacade.getUsuarioPorRolArea("role.responsable.pronunciamiento.biodiversidad", areaBiodiversidad).getNombre();
        		} catch (ServiceException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
        		params.put("u_ResponsablePronunciamientoBiodiversidad", usrResponsablePronunciamientoBiodiversidad);
        		String userResponsable=usrResponsablePronunciamientoBiodiversidad;
        		if(usuarioFacade.buscarJefeInmediatoSuperior(usrResponsablePronunciamientoBiodiversidad) !=null){
        			userResponsable =  usuarioFacade.buscarJefeInmediatoSuperior(usrResponsablePronunciamientoBiodiversidad).getNombre();
        		}
        		params.put("u_SupervisorBiodiversidad", userResponsable);
        	}
        	if (intersecaBP || intersecaPFE) {
        		Area areaForestal;
        		if (adjuntarCuestionarioBean.getProyecto().getAreaResponsable().getTipoArea().getSiglas().equals("PC")) {
        			areaForestal = areaFacade.getAreaSiglas("DNF");
        		} else {
        			areaForestal = adjuntarCuestionarioBean.getProyecto().getAreaResponsable(); //Ver como buscar el área de forestal para las provincias
        		}
        		String usrResponsablePronunciamientoForestal="";
        		try {
        			usrResponsablePronunciamientoForestal = areaFacade.getUsuarioPorRolArea("role.responsable.pronunciamiento.Forestal", areaForestal).getNombre();
        		} catch (ServiceException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
        		params.put("u_ResponsablePronunciamientoForestal", usrResponsablePronunciamientoForestal);
        		String userResponsable=usrResponsablePronunciamientoForestal;
        		if(usuarioFacade.buscarJefeInmediatoSuperior(usrResponsablePronunciamientoForestal)!=null){
        			userResponsable=usuarioFacade.buscarJefeInmediatoSuperior(usrResponsablePronunciamientoForestal).getNombre();
        		}
        		params.put("u_SupervisorForestal", userResponsable);
        	}
        }
        try {
			procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getTarea().getProcessInstanceId(), params);
		} catch (JbpmException e1) {
			e1.printStackTrace();
		}
        //--- fin de asignar técnico con menos carga------------------

        try {
            //Aprobar tarea
            taskBeanFacade.approveTask(loginBean.getNombreUsuario(),
                    bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getTarea().getProcessInstanceId()
                    , new ConcurrentHashMap<String, Object>(), loginBean.getPassword(),
                    Constantes.getUrlBusinessCentral(),Constantes.getRemoteApiTimeout(),Constantes.getNotificationService());
            JsfUtil.addMessageInfo("Se realizó correctamente la operación.");
            procesoFacade.envioSeguimientoLicenciaAmbiental(loginBean.getUsuario(), bandejaTareasBean.getTarea().getProcessInstanceId());
            return JsfUtil.actionNavigateTo("/bandeja/bandejaTareas.jsf");
        }
        catch (Exception e){
            LOGGER.error("Error al completar la tarea para Adjuntar documentos de BP o SNAP.", e);
            JsfUtil.addMessageError("Ocurrio un error al enviar la información.");
            return "";
        }
    }
}
