/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.eia.mediofisico.bean;

import ec.gob.ambiente.suia.domain.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * @author martin
 */
public class CuerpoHidricoBean implements Serializable {

    private static final long serialVersionUID = -6121426502807786614L;

    @Getter
    @Setter
    private CuerpoHidrico cuerpoHidrico;

    @Getter
    @Setter
    private CoordenadaGeneral coordenadaGeneral;

    @Getter
    @Setter
    private List<CuerpoHidrico> listaCuerposHidricos;

    @Getter
    @Setter
    private EstudioImpactoAmbiental estudioImpactoAmbiental;

    @Getter
    @Setter
    private List ListaEntidadesRemover;

    @Getter
    @Setter
    private CatalogoUso[] listaUsos;

}
