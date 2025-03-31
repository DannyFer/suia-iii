package ec.gob.ambiente.rcoa.dto;

import java.util.List;

import ec.gob.ambiente.rcoa.model.DocumentoRegistroAmbiental;
import ec.gob.ambiente.rcoa.viabilidadTecnica.model.AspectoViabilidadTecnica;
import ec.gob.ambiente.rcoa.viabilidadTecnica.model.MedioFrecuenciaMedida;
import ec.gob.ambiente.rcoa.viabilidadTecnica.model.PmaViabilidadTecnica;
import lombok.Getter;
import lombok.Setter;

public class EntidadPmaViabilidad {
	
	@Getter
    @Setter
    private String planNombre;

    @Getter
    @Setter
    private Integer planId;
    
    @Getter
    @Setter
    private List<MedioFrecuenciaMedida> medidas;
    
    @Getter
    @Setter
    private List<PmaViabilidadTecnica> medidasProyecto;
    
    @Getter
    @Setter
    private List<DocumentoRegistroAmbiental> listaDocumentoAdjunto;
    
    @Getter
    @Setter
    private List<AspectoViabilidadTecnica> listaAspectoAmbiental;
    

}
