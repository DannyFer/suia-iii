package ec.gob.ambiente.prevencion.categoriaii.mineria.bean;

import java.io.Serializable;
import java.util.List;
import ec.gob.ambiente.suia.domain.FichaAmbientalMineria;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.dto.EntityAdjunto;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author lili
 *
 */
public class InformacionGeneralBean implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 8979602654913910880L;

    @Getter
    @Setter
    private FichaAmbientalMineria fichaAmbientalMineria;
    @Getter
    @Setter
    private String direccion;
    @Getter
    @Setter
    private String telefono;
    @Getter
    @Setter
    private String fax;
    @Getter
    @Setter
    private String email;
    @Getter
    @Setter
    private List<UbicacionesGeografica> listaUbicacionProyecto;
    @Getter
    @Setter
    private UbicacionesGeografica ubicacionContacto;
    @Getter
    @Setter
    private ProyectoLicenciamientoAmbiental proyecto;
    @Getter
    @Setter
    private EntityAdjunto entityAdjunto;

    public void iniciar() {
        setFichaAmbientalMineria(null);
        setDireccion("");
        setEmail("");
        setFax("");
        setTelefono("");
        setListaUbicacionProyecto(null);
        setUbicacionContacto(null);
        setProyecto(null);
    }
}
