package ec.gob.ambiente.rcoa.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.ObservacionesEsIA;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.PlanManejoAmbientalEsIA;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.ProgramaPlanManejoEsIA;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.SubplanesManejoAmbientalEsIA;

public class EntityPmaEsIA {


    @Getter
    @Setter
    private SubplanesManejoAmbientalEsIA subplan;

    @Getter
    @Setter
    private PlanManejoAmbientalEsIA planManejo;
    
    @Getter
    @Setter
    private PlanManejoAmbientalEsIA planManejoCronograma;
    
    @Getter
    @Setter
    private List<ProgramaPlanManejoEsIA> listaProgramas;

    @Getter
    @Setter
    private List<ObservacionesEsIA> listaObservacionesSubPlan;
    
}
