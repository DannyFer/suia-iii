package ec.gob.ambiente.pronunciamiento.bean;

import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

@ManagedBean
@ViewScoped
public class EquipoMultidisciplinarioContextoBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(EquipoMultidisciplinarioContextoBean.class);
    private static final long serialVersionUID = 7434530875988549285L;


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

        try {
            BandejaTareasBean bandejaTareasBean = JsfUtil.getBean(BandejaTareasBean.class);
            if (bandejaTareasBean.getTarea().getVariable("equipoMultidisciplinarioDatosAdicionales") != null) {
                String equipoMultidisciplinarioDatosAdicionales = bandejaTareasBean.getTarea().getVariable("equipoMultidisciplinarioDatosAdicionales").toString();
                String[] partes = equipoMultidisciplinarioDatosAdicionales.split("---");
                urlContexto = partes.length >= 0 ? partes[0] : urlContexto;
                labelContexto = partes.length >= 1 ? partes[1] : labelContexto;
                mostrarIndicaciones = true;
            }
            if (bandejaTareasBean.getTarea().getVariable("equipoMultidisciplinarioDatosAdicionalesIndicaciones") != null) {
                indicaciones = bandejaTareasBean.getTarea().getVariable("equipoMultidisciplinarioDatosAdicionalesIndicaciones").toString();
            }
        } catch (Exception exception) {
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_INICIAR_TAREA);
            LOGGER.error(JsfUtil.MESSAGE_ERROR_INICIAR_TAREA, exception);
        }
    }

}
