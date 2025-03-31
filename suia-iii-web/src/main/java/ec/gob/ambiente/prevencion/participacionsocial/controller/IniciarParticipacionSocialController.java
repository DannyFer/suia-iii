package ec.gob.ambiente.prevencion.participacionsocial.controller;

import java.io.Serializable;
import java.util.Map;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.participacionsocial.facade.ParticipacionSocialFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@RequestScoped
@ManagedBean
public class IniciarParticipacionSocialController implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -294757822834916625L;

    private static final Logger LOGGER = Logger
            .getLogger(IniciarParticipacionSocialController.class);
    @EJB
    private ProcesoFacade procesoFacade;


    @EJB
    private ParticipacionSocialFacade participacionSocialFacade;

    @Getter
    @Setter
    @ManagedProperty(value = "#{proyectosBean}")
    private ProyectosBean proyectosBean;

    @Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;

    /**
     *
     */
    public void iniciarProceso() {
        try {
            Map<String, Object> data = participacionSocialFacade.inicializarParticipacionSocial(proyectosBean.getProyecto());

            iniciaParticipacionSocial(data);
        } catch (ServiceException e) {
            LOGGER.error("Error al iniciar participación social", e);
            JsfUtil.addMessageError("Ocurrio un error al enviar la información.");
        }
    }

    /**
     * @param data
     * @return
     */
    public long iniciaParticipacionSocial(Map<String, Object> data) {
        long idProceso = 0;
        try {
            idProceso = procesoFacade.iniciarProceso(loginBean.getUsuario(),
                    Constantes.NOMBRE_PROCESO_PARTICIPACION_SOCIAL, proyectosBean.getProyecto().getCodigo(), data);
            JsfUtil.addMessageInfo("Se inició correctamente el proceso de Participación Social.");
            procesoFacade.envioSeguimientoLicenciaAmbiental(loginBean.getUsuario(), idProceso);
            JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
        } catch (JbpmException e) {
            LOGGER.error("Error al iniciar Participacion Social", e);
            JsfUtil.addMessageError("Error al iniciar la tarea Participacion Social.");
        }
        return idProceso;
    }


}
