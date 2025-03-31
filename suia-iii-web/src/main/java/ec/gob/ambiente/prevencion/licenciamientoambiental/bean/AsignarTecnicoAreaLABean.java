package ec.gob.ambiente.prevencion.licenciamientoambiental.bean;

import lombok.Getter;
import lombok.Setter;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.Map;

@ManagedBean
@ViewScoped
public class AsignarTecnicoAreaLABean implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -106087236548921415L;

    @Getter
    @Setter
    private String area;

    @Getter
    @Setter
    private String usuario;

    @Getter
    @Setter
    private String tipo;

    @PostConstruct
    public void init() {
        usuario = "";
        Map<String, String> params = FacesContext.getCurrentInstance()
                .getExternalContext().getRequestParameterMap();
        String area = params.get("area");
        if (area != null && !area.isEmpty()) {
            usuario = "u_Tecnico" + area;

        }
        if (params.containsKey("tipo")) {
            tipo = params.get("tipo");

        }

    }

}
