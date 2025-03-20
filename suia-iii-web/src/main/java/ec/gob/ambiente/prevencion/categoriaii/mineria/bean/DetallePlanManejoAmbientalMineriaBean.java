package ec.gob.ambiente.prevencion.categoriaii.mineria.bean;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.FichaAmbientalMineria;
import ec.gob.ambiente.suia.domain.PlanSector;

public class DetallePlanManejoAmbientalMineriaBean {

    @Getter
    @Setter
    private Integer id;
    @Getter
    @Setter
    private PlanSector planSector;
    @Getter
    @Setter
    private Date fechaInicio;
    @Getter
    @Setter
    private Date fechaFin;
    @Getter
    @Setter
    private FichaAmbientalMineria fichaMineria;
}
