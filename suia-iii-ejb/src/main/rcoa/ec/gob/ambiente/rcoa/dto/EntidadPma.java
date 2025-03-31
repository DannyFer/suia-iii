package ec.gob.ambiente.rcoa.dto;

import java.util.List;

import ec.gob.ambiente.rcoa.model.AspectoAmbientalPma;
import ec.gob.ambiente.rcoa.model.DocumentoRegistroAmbiental;
import ec.gob.ambiente.rcoa.model.MedidaVerificacionPma;
import ec.gob.ambiente.rcoa.model.PmaAceptadoRegistroAmbiental;
import lombok.Getter;
import lombok.Setter;

public class EntidadPma {


    @Getter
    @Setter
    private String planNombre;

    @Getter
    @Setter
    private Integer planId;
    
    @Getter
    @Setter
    private List<MedidaVerificacionPma> medidas;
    
    @Getter
    @Setter
    private List<PmaAceptadoRegistroAmbiental> medidasProyecto;
    
    @Getter
    @Setter
    private List<DocumentoRegistroAmbiental> listaDocumentoAdjunto;
    
    @Getter
    @Setter
    private List<AspectoAmbientalPma> listaAspectoAmbiental;

}
