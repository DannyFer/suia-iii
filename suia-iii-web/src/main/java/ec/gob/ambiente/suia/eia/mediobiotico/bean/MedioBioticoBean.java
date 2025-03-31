/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.eia.mediobiotico.bean;

import ec.gob.ambiente.suia.domain.MedioBiotico;
import ec.gob.ambiente.suia.dto.EntityAdjunto;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.model.SelectItem;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author ishmael
 */
public class MedioBioticoBean implements Serializable {

    private static final long serialVersionUID = -5355043837970269007L;

    @Getter
    @Setter
    private MedioBiotico medioBiotico;

    @Getter
    @Setter
    private String adjunto;

    @Getter
    @Setter
    private List<SelectItem> listaEcosistemas;

    @Getter
    @Setter
    private String[] listaSeleccionaEcosistemas;

    @Getter
    @Setter
    private Map<String, EntityAdjunto> adjuntosMap;

    public static final String MAPA_INTERVENCION = "Mapa de intervención del proyecto";
    public static final String MAPA_ECOSISTEMAS = "Mapa de ecosistemas";
    public static final String DESCRIPCION_ECOSISTEMA = "Descripción de ecosistema";
    public static final String ANEXO = "Anexo";
    public static final String DETALLE_COBERTURA = "Detalle de la cobertura Vegetal y Uso de suelo";

    public void instanciarAdjuntos() {
        adjuntosMap = new HashMap<String, EntityAdjunto>();
        adjuntosMap.put("A", new EntityAdjunto(MAPA_INTERVENCION));
        adjuntosMap.put("B", new EntityAdjunto(MAPA_ECOSISTEMAS));
        adjuntosMap.put("C", new EntityAdjunto(DESCRIPCION_ECOSISTEMA));
        adjuntosMap.put("D", new EntityAdjunto(ANEXO));
        adjuntosMap.put("E", new EntityAdjunto(DETALLE_COBERTURA));
    }
}
