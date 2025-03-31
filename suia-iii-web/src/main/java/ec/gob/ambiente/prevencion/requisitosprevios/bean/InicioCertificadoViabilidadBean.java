package ec.gob.ambiente.prevencion.requisitosprevios.bean;

import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.certificadointerseccion.service.CertificadoInterseccionFacade;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.ProyectoLicenciaAmbientalFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.Map;

@ManagedBean
@ViewScoped
public class InicioCertificadoViabilidadBean implements Serializable {

    private static final long serialVersionUID = 2975076547624896247L;

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
            .getLogger(InicioCertificadoViabilidadBean.class);

    @EJB
    private ProcesoFacade procesoFacade;

    @EJB
    private ProyectoLicenciaAmbientalFacade proyectoLicenciaAmbientalFacade;

    @EJB
    private CertificadoInterseccionFacade certificadoInterseccionService;

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
    private ProyectoLicenciamientoAmbiental proyecto;

    @Getter
    @Setter
    private String tramitePospuesto;

    @Getter
    @Setter
    private String zonasInterseccion;

    @PostConstruct
    public void init() {
        try {
            Map<String, Object> variables = procesoFacade
                    .recuperarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getTarea()
                            .getProcessInstanceId());

            Integer idProyecto = Integer.parseInt((String) variables.get(Constantes.ID_PROYECTO));
            proyecto = proyectoLicenciaAmbientalFacade.getProyectoPorId(idProyecto);

            Boolean esLicencia = Boolean.parseBoolean((String) variables.get("esLicenciaAmbiental"));

            if(esLicencia)
                tramitePospuesto = "Licenciamiento Ambiental";
            else
                tramitePospuesto = "Registro Ambiental";

            zonasInterseccion = certificadoInterseccionService.getDetalleCapasInterseccion(proyecto.getCodigo());

        } catch (Exception e) {
            JsfUtil.addMessageError("Error al realizar la operación. Intente más tarde.");
            LOG.error("Error al realizar la operación.", e);
        }
    }

    public void validarTareaBpm() {
        JsfUtil.validarPaginasUrlTareasBpm(bandejaTareasBean.getTarea(), "/prevencion/requisitosprevios/inicioCertificadoViabilidad.jsf");
    }
}