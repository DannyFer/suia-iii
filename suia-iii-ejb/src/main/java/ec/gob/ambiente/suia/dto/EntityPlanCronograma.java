/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.dto;

import ec.gob.ambiente.suia.domain.CronogramaValoradoPma;
import ec.gob.ambiente.suia.domain.TipoPlanManejoAmbiental;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author ishmael
 */
public class EntityPlanCronograma {
   
    @Getter
    @Setter
    private String idTabla;
    @Getter
    @Setter
    private TipoPlanManejoAmbiental plan;
    @Getter
    @Setter
    private List<CronogramaValoradoPma> listaDetalleCronograma;
    
    
    
}
