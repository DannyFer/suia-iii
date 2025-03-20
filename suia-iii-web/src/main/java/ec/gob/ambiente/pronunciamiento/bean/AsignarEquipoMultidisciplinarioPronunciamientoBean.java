package ec.gob.ambiente.pronunciamiento.bean;

import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.utils.Constantes;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ManagedBean
@ViewScoped
public class AsignarEquipoMultidisciplinarioPronunciamientoBean implements Serializable {

    private static final long serialVersionUID = 5560843337670252733L;

    private static final Logger LOGGER = Logger.getLogger(AsignarEquipoMultidisciplinarioPronunciamientoBean.class);

    @Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;
    @Getter
    @Setter
    private String[] areasSeleccionadas;

    @Getter
    @Setter
    private List<String> areas;

    @EJB
    private AreaFacade areaFacade;

    @Getter
    @Setter
    private String indicaciones = "";
    @Getter
    @Setter
    private boolean mostrarIndicaciones;


    @Getter
    @Setter
    private String labelContexto = "Ver detalles";
    @Getter
    @Setter
    private String urlContexto = "";

    @PostConstruct
    public void init() {
        this.areas = new ArrayList<String>();
        List<String> areas = new ArrayList<String>();

        try {
            BandejaTareasBean bandejaTareasBean = JsfUtil.getBean(BandejaTareasBean.class);

            String equipoMultidisciplinarioSolicitaApoyo = bandejaTareasBean.getTarea()
                    .getVariable("equipoMultidisciplinarioSolicitaApoyo").toString();
            areas = Arrays.asList(equipoMultidisciplinarioSolicitaApoyo.split(Constantes.SEPARADOR));


            for (String area : areas) {
                //if (areas.size() > 1 && !obtenerNombreArea(area)) { Así estaba antes, fue cambiado resolviendo la incidencia 0001692. Erislan
                if (areas.size() > 0 && obtenerNombreArea(area)) {
                    this.areas.add(area);
                }
            }


        } catch (Exception exception) {
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_INICIAR_TAREA);
            LOGGER.error(JsfUtil.MESSAGE_ERROR_INICIAR_TAREA, exception);
        }
    }

    public void validarTareaBpm() {
        JsfUtil.validarPaginasUrlTareasBpm(JsfUtil.getBean(BandejaTareasBean.class).getTarea(),
                "/pronunciamiento/asignarEquipoTecnico.jsf");
    }

    public Boolean obtenerNombreArea(String idAreaString) {

        try {
            idAreaString = idAreaString.replace("coordinador", "tecnico");
            String especialidad = Constantes.getRoleAreaName(idAreaString.substring(0, idAreaString.indexOf("--")));
            return Usuario.isUserInRole(loginBean.getUsuario(), especialidad);

        } catch (Exception e) {
            LOGGER.error("Ocurrió un error al recuperar las especialidades y áreas del equipo multidischiplinario" + e);
        }

        return false;
    }
}
