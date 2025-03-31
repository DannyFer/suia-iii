package ec.gob.ambiente.prevencion.participacionsocial.bean;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.Map;

@ManagedBean
@ViewScoped
public class VerificarInformacionParticipacionSocialBean implements
        Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1151170626240689434L;

    private static final Logger LOG = Logger.getLogger(VerificarInformacionParticipacionSocialBean.class);

    @Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;

    @Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;

    @Getter
    @Setter
    private Boolean requiereFacilitador;


    @Getter
    @Setter
    private Boolean soloLectura;

    @Getter
    @Setter
    private Integer numeroFacilitadores;

    @EJB
    private ProcesoFacade procesoFacade;

    @PostConstruct
    public void init() {

        try {
            Map<String, Object> var = procesoFacade.recuperarVariablesProceso(loginBean.getUsuario(),
                    bandejaTareasBean.getTarea().getProcessInstanceId());
            if (var.get("numeroFacilitadores") != null) {
                numeroFacilitadores = Integer.parseInt((String) var.get("numeroFacilitadores"));
            }
            if (var.get("requiereFacilitador") != null&& Boolean.parseBoolean((String) var.get("requiereFacilitador"))) {
                requiereFacilitador = Boolean.parseBoolean((String) var.get("requiereFacilitador"));

                soloLectura = true;
            }
            if (numeroFacilitadores != null && numeroFacilitadores > 0) {
                requiereFacilitador = true;
                soloLectura = true;
            }


        } catch (JbpmException e) {

            LOG.error("Error al recuperar las variables del proceso.", e);
            JsfUtil.addMessageError("Ocurrió un error al realizar la operación.");
        }
    }

}
