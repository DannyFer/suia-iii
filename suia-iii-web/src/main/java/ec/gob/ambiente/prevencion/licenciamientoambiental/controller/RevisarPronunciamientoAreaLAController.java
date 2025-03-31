package ec.gob.ambiente.prevencion.licenciamientoambiental.controller;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import lombok.Getter;
import lombok.Setter;
import observaciones.ObservacionesController;

import org.apache.log4j.Logger;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.prevencion.licenciamientoambiental.bean.RevisarPronunciamientoAreaLABean;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.ObservacionesFormularios;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@RequestScoped
@ManagedBean
public class RevisarPronunciamientoAreaLAController implements Serializable {

    private static final long serialVersionUID = -35263714834217786L;
    private static final Logger LOGGER = Logger
            .getLogger(RevisarPronunciamientoAreaLAController.class);
    @EJB
    private ProcesoFacade procesoFacade;

    @Getter
    @Setter
    @ManagedProperty(value = "#{observacionesController}")
    private ObservacionesController observacionesController;
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
    @ManagedProperty(value = "#{revisarPronunciamientoAreaLABean}")
    private RevisarPronunciamientoAreaLABean revisarPronunciamientoAreaLABean;


    public String iniciarTarea() {

        try {
            Boolean correcto = revisarPronunciamientoAreaLABean.getCorrecto();
            if (validarObservaciones(correcto)) {
                Map<String, Object> params = new ConcurrentHashMap<String, Object>();
                params.put("requiereModificacionesPronuciamientoA",
                        !revisarPronunciamientoAreaLABean.getCorrecto());
                params.put("requiereModificacionesPronuciamientoA" + revisarPronunciamientoAreaLABean.getArea(),
                        !revisarPronunciamientoAreaLABean.getCorrecto());
                
                if(revisarPronunciamientoAreaLABean.getArea().equals("Forestal") || revisarPronunciamientoAreaLABean.getArea().equals("Biodiversidad")){
                	 Map<String, Object> variables = procesoFacade.recuperarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getTarea().getProcessInstanceId());
                     
                     String autoridadForestalBiodiversidad="";
                     if(revisarPronunciamientoAreaLABean.getArea().equals("Forestal")){
                     	autoridadForestalBiodiversidad=variables.get("u_DirectorForestal").toString();
                     	params.put("firmaForeBio", autoridadForestalBiodiversidad);
                     }else {
                     	autoridadForestalBiodiversidad=variables.get("u_DirectorBiodiversidad").toString();
                     	params.put("firmaForeBio", autoridadForestalBiodiversidad);
         			}  
                }
                
                procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean
                        .getTarea().getProcessInstanceId(), params);

                Map<String, Object> data = new ConcurrentHashMap<String, Object>();
                taskBeanFacade.approveTask(loginBean.getNombreUsuario(),
                        bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), data,
                        loginBean.getPassword(),
                        Constantes.getUrlBusinessCentral(), Constantes.getRemoteApiTimeout(), Constantes.getNotificationService());
                JsfUtil.addMessageInfo("Se realizó correctamente la operación.");
                procesoFacade.envioSeguimientoLicenciaAmbiental(loginBean.getUsuario(), bandejaTareasBean.getProcessId());
                return JsfUtil.actionNavigateTo("/bandeja/bandejaTareas.jsf");
            } else {
                return "";
            }
        } catch (JbpmException e) {
            LOGGER.error(e);
            JsfUtil.addMessageError("Ocurrio un error al enviar la información.");
        }

        return "";
    }


    public void validarTareaBpm() {
        String url = "/prevencion/licenciamiento-ambiental/revisarPronunciamientoArea.jsf?area="
                + revisarPronunciamientoAreaLABean.getArea();
        if (revisarPronunciamientoAreaLABean.getTipo() != null && !revisarPronunciamientoAreaLABean.getTipo().isEmpty()) {
            url += "&tipo=" + revisarPronunciamientoAreaLABean.getTipo();
        }
        JsfUtil.validarPaginasUrlTareasBpm(bandejaTareasBean.getTarea(), url);
    }

    public Boolean validarObservaciones(Boolean estado) {
        List<ObservacionesFormularios> observaciones = observacionesController.getObservacionesBB().getMapaSecciones().get(revisarPronunciamientoAreaLABean.getArea());

        if (estado) {
            for (ObservacionesFormularios observacion : observaciones) {
                if (!observacion.isObservacionCorregida()) {

                    JsfUtil.addMessageError("Existen observaciones sin corregir. Por favor rectifique los datos.");
                    return false;
                }
            }
        } else {
            int posicion = 0;
            int cantidad = observaciones.size();
            Boolean encontrado = false;
            while (!encontrado && posicion < cantidad) {
                if (!observaciones.get(posicion++).isObservacionCorregida()) {
                    encontrado = true;
                }
            }
            if (!encontrado) {
                JsfUtil.addMessageError("No existen observaciones sin corregir. Por favor rectifique los datos.");
                return false;
            }
        }
        return true;
    }
}
