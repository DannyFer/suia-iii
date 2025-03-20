/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.eia.descripcion.proyecto.bean;

import ec.gob.ambiente.suia.domain.CatalogoGeneral;
import ec.gob.ambiente.suia.domain.ProyectoGeneralCatalogo;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author christian
 */
public class MarcoLegalBean {

    @Getter
    @Setter
    private List<CatalogoGeneral> listaMarcoLegal;
    @Getter
    @Setter
    private List<ProyectoGeneralCatalogo> listaMarcoLegalTdr;

    public void iniciarDatos() {
        setListaMarcoLegal(null);
        setListaMarcoLegalTdr(null);
    }
}
