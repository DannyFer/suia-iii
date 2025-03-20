package ec.gob.ambiente.prevencion.participacionsocial.bean;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.participacionsocial.facade.ParticipacionSocialFacade;
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
public class VerificarNumeroFacilitadoresAdicionalesPSBean implements
        Serializable {

    private static final Logger LOG = Logger.getLogger(VerificarNumeroFacilitadoresAdicionalesPSBean.class);
    private static final long serialVersionUID = 1399398421614259436L;

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
    @ManagedProperty(value = "#{proyectosBean}")
    private ProyectosBean proyectosBean;
    @Getter
    @Setter
    private Boolean requiereFacilitador;



    @Getter
    @Setter
    private String facilitadores;

    @Getter
    @Setter
    private Boolean soloLectura;

    @Getter
    @Setter
    private Integer numeroFacilitadores = 0;

    @Getter
    @Setter
    private Integer numeroFacilitadoresAdicionales;
    @EJB
    private ProcesoFacade procesoFacade;

    @EJB
    private ParticipacionSocialFacade participacionSocialFacade;
    @PostConstruct
    public void init() {
        try {
            Map<String, Object> var = procesoFacade.recuperarVariablesProceso(loginBean.getUsuario(),
                    bandejaTareasBean.getTarea().getProcessInstanceId());
            if (var.get("numeroFacilitadores") != null) {
                numeroFacilitadores = Integer.parseInt((String) var.get("numeroFacilitadores"));
            }
            facilitadores = participacionSocialFacade.listadoFacilitadores(
                    proyectosBean.getProyecto());

        } catch (JbpmException e) {

            LOG.error("Error al recuperar las variables del proceso.", e);
            JsfUtil.addMessageError("Ocurrió un error al realizar la operación.");
        } catch (ServiceException e) {

            LOG.error("Error al recuperar las variables del proceso.", e);
            JsfUtil.addMessageError("Ocurrió un error al realizar la operación.");
        }
    }

}
