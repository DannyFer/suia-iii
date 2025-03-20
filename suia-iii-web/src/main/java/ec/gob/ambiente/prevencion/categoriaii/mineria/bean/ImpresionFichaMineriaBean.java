package ec.gob.ambiente.prevencion.categoriaii.mineria.bean;

import ec.gob.ambiente.suia.domain.FichaAmbientalMineria;
import ec.gob.ambiente.suia.dto.EntityFichaMineria020Reporte;
import ec.gob.ambiente.suia.dto.EntityFichaMineriaReporte;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author christian
 */
public class ImpresionFichaMineriaBean {

    @Getter
    @Setter
    private EntityFichaMineriaReporte entityFichaMineriaReporte;
    @Getter
    @Setter
    private EntityFichaMineria020Reporte entityFichaMineria020Reporte;
    @Getter
    @Setter
    private FichaAmbientalMineria fichaAmbientalMineria;
    @Getter
    @Setter
    private String plantillaHtml;
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

    public void iniciarDatos() {
        setEntityFichaMineriaReporte(new EntityFichaMineriaReporte());
        setEntityFichaMineria020Reporte(new EntityFichaMineria020Reporte());
        setFichaAmbientalMineria((FichaAmbientalMineria) JsfUtil.devolverObjetoSession(Constantes.SESSION_FICHA_AMBIENTAL_MINERA_OBJECT));
        setPlantillaHtml(null);
    }

}
