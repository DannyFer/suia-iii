/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.prevencion.categoria2.bean;

import ec.gob.ambiente.suia.dto.EntityAdjunto;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author jonathan
 */
public class AdjuntosCat2Bean {

    @Getter
    @Setter
    private String nombrePanel;
    @Getter
    @Setter
    private EntityAdjunto entityAdjunto;

    public void iniciarDatos() {
        setNombrePanel(null);
        setEntityAdjunto(new EntityAdjunto());
    }

    public void validarFormulario() throws ServiceException {
        if (getEntityAdjunto().getArchivo() == null) {
            throw new ServiceException("Debe seleccionar un archivo");
        }
    }
}
