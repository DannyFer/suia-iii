/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.prevencion.categoriaii.mineria.bean;

import ec.gob.ambiente.suia.domain.FichaAmbientalMineria;

/**
 *
 * @author christian
 */
public class FichaMineriaBean {

    private FichaAmbientalMineria fichaAmbientalMineria;
    private String mensaje;

    public void iniciarDatos() {
        setFichaAmbientalMineria(null);
    }

    /**
     * @return the fichaAmbientalMineria
     */
    public FichaAmbientalMineria getFichaAmbientalMineria() {
        return fichaAmbientalMineria;
    }

    /**
     * @param fichaAmbientalMineria the fichaAmbientalMineria to set
     */
    public void setFichaAmbientalMineria(FichaAmbientalMineria fichaAmbientalMineria) {
        this.fichaAmbientalMineria = fichaAmbientalMineria;
    }

    /**
     * @return the mensaje
     */
    public String getMensaje() {
        return mensaje;
    }

    /**
     * @param mensaje the mensaje to set
     */
    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
