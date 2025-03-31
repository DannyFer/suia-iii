/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.utils;

import ec.gob.ambiente.suia.dto.EntityMenu;
import java.util.Comparator;

/**
 *
 * @author christian
 */
public class EntityMenuComparator implements Comparator<EntityMenu> {

    /**
     * método que compara el campo orden de un EntityMenu sirve para ordenar
     * listas de datos
     *
     * @return int
     */
    @Override
    public int compare(EntityMenu o1, EntityMenu o2) {
        return o1.getOrden().compareTo(o2.getOrden());
    }

}
