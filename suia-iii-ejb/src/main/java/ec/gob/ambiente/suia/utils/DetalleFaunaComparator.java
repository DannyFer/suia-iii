/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.utils;

import ec.gob.ambiente.suia.domain.DetalleFauna;
import java.util.Comparator;

/**
 *
 * @author christian
 */
public class DetalleFaunaComparator implements Comparator<DetalleFauna> {

    /**
     * método que compara el campo orden de un EntityMenu sirve para ordenar
     * listas de datos
     *
     * @return int
     */
    @Override
    public int compare(DetalleFauna o1, DetalleFauna o2) {
        return o1.getEspecie().compareTo(o2.getEspecie());
    }

}
