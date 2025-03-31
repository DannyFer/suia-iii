/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.eia.descripcion.proyecto.bean;

import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.domain.IdentificacionSitiosContaminadosEia;
import ec.gob.ambiente.suia.dto.EntityAdjunto;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author christian
 */
public class IdentificacionSitiosContaminadosEiaBean {

    @Getter
    @Setter
    private List<IdentificacionSitiosContaminadosEia> listaIdentificacionSitiosContaminadosEia;
    @Getter
    @Setter
    private List<IdentificacionSitiosContaminadosEia> listaIdentificacionSitiosContaminadosEiaEliminar;
    @Getter
    @Setter
    private IdentificacionSitiosContaminadosEia identificacionSitiosContaminadosEia;
    @Getter
    @Setter
    private EstudioImpactoAmbiental estudioImpactoAmbiental;
    @Getter
    @Setter
    private EntityAdjunto entityAdjunto;

    public void iniciarDatos() {
        setListaIdentificacionSitiosContaminadosEia(null);
        setListaIdentificacionSitiosContaminadosEiaEliminar(new ArrayList<IdentificacionSitiosContaminadosEia>());
        setIdentificacionSitiosContaminadosEia(null);
        setEstudioImpactoAmbiental(null);
        setEntityAdjunto(new EntityAdjunto());
    }

    public void validarFormulario() throws ServiceException {
        if (getEntityAdjunto().getArchivo() == null) {
            throw new ServiceException("Debe adjuntar un archivo");
        }
    }
}
