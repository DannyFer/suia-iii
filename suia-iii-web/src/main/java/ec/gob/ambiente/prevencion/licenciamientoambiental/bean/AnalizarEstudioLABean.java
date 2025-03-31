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
public class AnalizarEstudioLABean implements Serializable {

    private static final long serialVersionUID = 2975898357643455827L;
    @Getter
    @Setter
    private Boolean equipoMultidiciplinario;

    @Getter
    @Setter
    private String tipo;





    @PostConstruct
    public void init() {
        Map<String, String> params = FacesContext.getCurrentInstance()
                .getExternalContext().getRequestParameterMap();
        tipo = "";
        if (params.containsKey("tipo")) {
            tipo = params.get("tipo");
        }
    }

}
