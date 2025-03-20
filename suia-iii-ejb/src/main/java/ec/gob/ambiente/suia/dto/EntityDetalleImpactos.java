/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.dto;

/**
 *
 * @author ishmael
 */
public class EntityDetalleImpactos {
    
    private String actividad;
    private String factor;
    private String impacto;

    public EntityDetalleImpactos() {
    }
    
    

    public EntityDetalleImpactos(String factor, String impacto) {
        this.factor = factor;
        this.impacto = impacto;
    }

    /**
     * @return the actividad
     */
    public String getActividad() {
        return actividad;
    }

    /**
     * @param actividad the actividad to set
     */
    public void setActividad(String actividad) {
        this.actividad = actividad;
    }

    /**
     * @return the factor
     */
    public String getFactor() {
        return factor;
    }

    /**
     * @param factor the factor to set
     */
    public void setFactor(String factor) {
        this.factor = factor;
    }

    /**
     * @return the impacto
     */
    public String getImpacto() {
        return impacto;
    }

    /**
     * @param impacto the impacto to set
     */
    public void setImpacto(String impacto) {
        this.impacto = impacto;
    }
    
}
