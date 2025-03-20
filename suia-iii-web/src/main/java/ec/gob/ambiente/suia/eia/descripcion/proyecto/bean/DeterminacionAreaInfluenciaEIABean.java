/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.eia.descripcion.proyecto.bean;

import ec.gob.ambiente.suia.domain.DeterminacionAreaInfluenciaEIA;
import ec.gob.ambiente.suia.dto.EntityAdjunto;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author christian
 */
public class DeterminacionAreaInfluenciaEIABean {

    @Getter
    @Setter
    private List<DeterminacionAreaInfluenciaEIA> listaDeterminacionAreaInfluenciaEIA;
    @Getter
    @Setter
    private EntityAdjunto entityAdjunto;

    public void iniciarDatos() {
        setListaDeterminacionAreaInfluenciaEIA(null);
        setEntityAdjunto(new EntityAdjunto());
    }

    public void validarFormulario() throws ServiceException {
        if (getEntityAdjunto().getArchivo() == null) {
            throw new ServiceException("Debe seleccionar un archivo");
        }
    }
}
